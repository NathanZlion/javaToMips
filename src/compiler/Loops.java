package compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Loops {

    public static String translateLoops(String code) {
        String data = ".data\n", text = "";
        int whileIndex = code.trim().indexOf("while");
        String codeBeforeWhile = code.trim().substring(0, whileIndex);
        // System.out.println(codeBeforeWhile);
        String mipsForCodeBeforeWhile = Assembler.translate(codeBeforeWhile.trim());
        assert mipsForCodeBeforeWhile != null;
        if (!mipsForCodeBeforeWhile.contains("error") && mipsForCodeBeforeWhile.length() > 0) {
            if (!mipsForCodeBeforeWhile.contains(".text")) {
                data += mipsForCodeBeforeWhile.substring(6);
            } else {
                int indexOfText = mipsForCodeBeforeWhile.indexOf(".text");
                data += mipsForCodeBeforeWhile.substring(6, indexOfText);
                text += mipsForCodeBeforeWhile.substring(indexOfText);
                text = text.replace("li $v0, 10\n syscall", "");
            }

        }
        code = code.trim().substring(whileIndex).trim();
        String condition = code.substring(code.indexOf('(') + 1, code.indexOf(')')).trim();
        String[] conditionParts = condition.split(" ");
        if (conditionParts.length != 3) {
            return "Error. Invalid condition given in loop";
        }
        String var1 = conditionParts[0].trim();
        String operator = conditionParts[1].trim();
        String var2 = conditionParts[2].trim();
        // System.out.println(var1 + ", " + var2);
        // System.out.println(var2);

        Variable variable1 = Assembler.findVariable(var1);
        Variable variable2 = Assembler.findVariable(var2);
        if (variable1 == null) {
            try {
                int num = Integer.parseInt(var1);
                variable1 = new Variable("int", "var" + Assembler.variablenames.size(), String.valueOf(num));
                if (variable1.isValid()) {
                    Assembler.variableStore.add(variable1);
                    Assembler.variablenames.add("var" + Assembler.variablenames.size());
                    data += variable1.name + ": " + variable1.mipsType + " " + variable1.value + "\n";

                } else {
                    return "Error. unsupported type in loop";
                }

            } catch (Exception e) {
                // System.out.println(e);
                return "Error. unsupported type in loop";
            }
        }
        if (variable2 == null) {
            try {
                int num = Integer.parseInt(var2);
                variable2 = new Variable("int", "var" + Assembler.variablenames.size(), String.valueOf(num));
                if (variable2.isValid()) {
                    Assembler.variableStore.add(variable2);
                    Assembler.variablenames.add("var" + Assembler.variablenames.size());
                    data += variable2.name + ": " + variable2.mipsType + " " + variable2.value + "\n";

                } else {
                    return "Error. unsupported type in loop";
                }

            } catch (Exception e) {
                return "Error. unsupported type in loop";
            }
        }
        String whileMips = variable1.loadR + " "
                + Objects.requireNonNull(Assembler.getFirstEmptyRegister(Assembler.tempRegisters)).name + ", " + variable1.name
                + "\n"
                + variable2.loadR + " " + Objects.requireNonNull(Assembler.getFirstEmptyRegister(Assembler.tempRegisters)).name
                + ", " + variable2.name + "\n"
                + "li $s0, " + variable1.value + "\n" + "li $s1, " + variable2.value + "\n";
        whileMips += "loop:\n";

        String var1Register = Assembler.usedRegisters.get(Assembler.usedRegisters.size() - 2).name;
        String var2Register = Assembler.usedRegisters.get(Assembler.usedRegisters.size() - 1).name;

        switch (operator) {
            case "<" -> whileMips += "bge $s0, $s1, exit\n";
            case ">" -> whileMips += "ble $s0, $s1, exit\n";
            case "<=" -> whileMips += "bgt $s0, $s1, exit\n";
            case ">=" -> whileMips += "blt $s0, $s1, exit\n";
            case "==" -> whileMips += "bne $s0, $s1, exit\n";
            case "!=" -> whileMips += "beq $s0, $s1, exit\n";
        }

        int start = code.indexOf('{');
        int end = code.indexOf('}');
        String whileBody = code.substring(start + 1, end);
        String[] lines = whileBody.split(";");

        // String mipsBody ="";
        String mipsBodyData = "";
        String mipsBodyText = "";
        // System.out.println(lines.length);
        ArrayList<String> linesList = new ArrayList<>();
        for (String line : lines) {
            if (line.length() > 2) {
                linesList.add(line);
            }
        }

        for (String line : linesList) {
            line = line.trim() + ";";

            HashMap<String, String> map = new HashMap<>();
            // map.put("++", "addi");
            // map.put("--", "subi");
            map.put("+=", "addi");
            map.put("-=", "subi");
            boolean flag = !line.contains(var1) && !line.contains(var2);

            if (line.contains("System.out.print") && line.indexOf("System.out.print") == 0) {
                String printVal = line.substring(16);
                if (!printVal.contains(var1) && !printVal.contains(var2)) {
                    String temp = PrintAndSimpleExpressions.translateSimplePrint(line, Assembler.findKeywords(line));
                    // System.out.println(temp);
                    // System.out.println("333");
                    if (temp.contains("Error")) {
                        return temp;
                    }
                    int indexOfText = temp.indexOf(".text");
                    mipsBodyData += temp.substring(5, indexOfText) + "\n";
                    mipsBodyText += temp.substring(indexOfText + 5) + "\n";

                } else {
                    if (line.contains(var1)) {
                        mipsBodyText += "li $v0, " + variable1.liValue + "\n"
                                + "move " + variable1.argRegister + ", $s0\n"
                                + "syscall\n";
                        if (line.contains("System.out.println")) {
                            mipsBodyData += "\nnewline: .asciiz \"\\n\"\n";
                            mipsBodyText += "\nli $v0, 4\n la $a0, newline\n  syscall \n";
                        }

                    }
                    if (line.contains(var2)) {
                        mipsBodyText += "li $v0, " + variable2.liValue + "\n"
                                + "move " + variable2.argRegister + ", $s1\n"
                                + "syscall\n";
                        if (line.contains("System.out.println")) {
                            mipsBodyData += "\nnewline: .asciiz \"\\n\"\n";
                            mipsBodyText += "\nli $v0, 4\n la $a0, newline\n  syscall \n";
                        }

                    }

                }

            } else if (line.contains("+=") && !(line.contains("-="))) {
                line = line.replace(";", "");
                // System.out.println(line);
                String[] temp = line.split("\\+=");
                // System.out.println(Arrays.toString(temp));
                try {
                    if (temp.length != 2)
                        throw new Exception();
                    int b = Integer.parseInt(temp[1]);
                    // System.out.println(b);

                } catch (Exception e) {
                    return "Error. unsupported incrementation";
                }
                if (flag) {
                    return "Error. Unsupported code";
                } else if (temp[0].equals(var1)) {
                    mipsBodyText += "\n" +
                            map.get("+=") + " $s0, $s0, " + temp[1] + "\n";
                } else if (temp[0].equals(var2)) {
                    mipsBodyText += "\n" +
                            map.get("+=") + " $s1, $s1, " + temp[1] + "\n";
                }
            } else if (!line.contains("+=") && (line.contains("-="))) {
                line = line.replace(";", "");
                String[] temp = line.split("\\+=");
                try {
                    if (temp.length != 2)
                        throw new Exception();
                    int b = Integer.parseInt(temp[1]);

                } catch (Exception e) {
                    return "Error. unsupported incrementation";
                }
                if (flag) {
                    return "Error. Unsupported code";
                } else if (temp[0].equals(var1)) {
                    mipsBodyText += "\n" +
                            map.get("-=") + " " + var1Register
                            + ", " + var1Register + ", " + temp[1] + "\n";
                    // System.out.println(temp[1]);
                } else if (temp[0].equals(var2)) {
                    mipsBodyText += "\n" +
                            map.get("-=") + " " + var2Register
                            + ", " + var2Register + ", " + temp[1] + "\n";
                    // System.out.println(temp[1]);
                }
            } else {
                System.out.println(line);
                return "Error. unsupported statement";
            }

        }
        if (mipsBodyData.length() > 1) {
            data += mipsBodyData;
            data += "\n";
        }
        if (mipsBodyText.length() > 1) {
            whileMips += mipsBodyText;
            whileMips += "\n";
        }
        whileMips += "j loop\n";
        whileMips += "li $v0, 10\n syscall\n";

        if (text.length() == 0) {
            text += ".text\n main:\n" + whileMips;
        } else {
            text += whileMips;
        }
        text += "exit:\n" + "li $v0, 10\n syscall\n";
        // System.out.println(data);
        data = ".data\n" + removeDuplicateLines(data.substring(5));
        // System.out.println(data);
        return data + text;
        // return mipsCode;
        // return "";
    }

    public static String removeDuplicateLines(String inputString) {
        // Split input string into separate lines
        String[] lines = inputString.split("\\r?\\n");

        // Use a HashSet to keep track of unique lines
        Set<String> uniqueLines = new HashSet<>();
        List<String> duplicateLines = new ArrayList<>();

        // Iterate through each line in the input string
        for (String line : lines) {
            // Remove any leading or trailing whitespace
            line = line.trim();

            // Check if the current line has already been seen
            if (uniqueLines.contains(line)) {
                // If the line is a duplicate, add it to the list of duplicates
                duplicateLines.add(line);
            } else {
                // If the line is unique, add it to the HashSet of unique lines
                uniqueLines.add(line);
            }
        }

        // Join the unique lines back together into a single string
        StringBuilder resultBuilder = new StringBuilder();
        for (String uniqueLine : uniqueLines) {
            resultBuilder.append(uniqueLine).append("\n");
        }
        String result = resultBuilder.toString();

        return result;
    }

}
