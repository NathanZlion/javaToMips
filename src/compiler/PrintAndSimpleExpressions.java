package compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PrintAndSimpleExpressions {
    public static String translateSimplePrint(String code, ArrayList<String> keywordsList) {
        // ArrayList<Variable> variables = new ArrayList<Variable>();
        Variable var = new Variable();
        if (code.charAt(code.length() - 1) != ';') {
            return "Syntax Error. Make sure your code ends with a semi-colon\n";
        }

        // String hello="hello";
        // System.out.println(hello);
        String dataVarName = "";
        // count how many times print is called per line and
        // if it is more than one return syntax error if it is only one, count it
        String[] lines = code.split(";");
        if (lines.length <= 2) {
            for (String line : lines) {

                if (Assembler.countHelper(line, keywordsList.get(0)) > 1) {
                    return "Syntax Error. Make sure you have used semi-colons correctly.\n";
                }
                if (Assembler.countHelper(line, keywordsList.get(0)) == 0) {
                    if (!line.contains("==") && Assembler.countHelper(line, "=") == 1) {
                        // System.out.println(line);
                        String type, name, value;

                        String[] separate = line.split("=");
                        // System.out.println(Arrays.toString(separate));
                        String[] words = separate[0].split(" ");
                        // System.out.println(Arrays.toString(words));
                        type = words[0].trim();
                        name = words[1].trim();
                        value = separate[1].trim();

                        // System.out.println(value);
                        Variable var1 = new Variable(type, name, value);
                        // System.out.println(var1.isValid());
                        if (var1.isValid()) {
                            var = var1;
                            Assembler.variableStore.add(var1);
                            dataVarName = var.name;
                            Assembler.variablenames.add(var.name);

                        } else {
                            return "Syntax Error. Invalid variable declaration detected.";
                        }

                    } else {
                        return "Syntax Error. More than one equal sign in declaration";
                    }
                }
                if (line.contains("System.out.print")) {
                    if (line.trim().indexOf("System.out.print") != 0) {
                        return "Syntax Error. Check your semi colons";
                    }

                    int start = line.indexOf("(");
                    int end = line.indexOf(")");
                    if (start != -1 && end != -1 && start < end) {
                        String varName = line.substring(start + 1, end);
                        String textVarName = varName.trim();
                        // System.out.println(textVarName);
                        if (!dataVarName.equals("") && !textVarName.equals(dataVarName)) {
                            return "Error. Variable mismatch detected";
                        }
                        // System.out.println(varName);
                        Variable var0 = Assembler.findVariable(varName);
                        //

                        if (var0 != null && var0.isValid()) {
                            var = var0;
                            // System.out.println(var.name+", "+var.type+", "+var.value);
                        }

                        else if (varName.length() != 0) {

                            if (varName.charAt(0) == '\"' && varName.charAt(varName.length() - 1) == '\"') {
                                Variable varStr = new Variable("String",
                                        "var" + Assembler.variablenames.size(),
                                        varName);
                                // System.out.println("var"+variablenames.size());
                                Assembler.variablenames.add("var" + Assembler.variablenames.size());
                                if (!varStr.isValid()) {
                                    return "Error. Variable not defined correctly";
                                }
                                // System.out.println(varStr.value);
                                // System.out.println(varStr.isValid());
                                Assembler.variableStore.add(varStr);
                                // System.out.println(variableStore.get(0).name);
                                var = varStr;
                                // System.out.println(var.name);
                            } else {
                                try {
                                    int temp = Integer.parseInt(varName);
                                    // System.out.println(temp);
                                    Variable varInt = new Variable("int", "var" + Assembler.variablenames.size(), varName);

                                    if (varInt.isValid()) {
                                        Assembler.variableStore.add(varInt);
                                        Assembler.variablenames.add("var" + Assembler.variablenames.size());
                                        var = varInt;
                                    } else {
                                        return "Error. Invalid integer";
                                    }

                                } catch (Exception e) {
                                    return "Error. Invalid variable";
                                }
                            }
                        } else if (varName.length() == 0) {
                            return "\n";
                        } else {
                            return "Syntax Error. Unknown or undeclared Variable used.";
                        }

                    }
                }

            }
        }
        // if(lines.length==1){
        //
        // }
        String data = "", text = "";
        // System.out.println(var.name);
        try {

            data = ".data\n\t" +
                    var.name + ": " +
                    var.mipsType + " " + var.value + "\n";

            text = ".text\n\t" +
                    "li $v0, " + var.liValue + "\n\t" +
                    var.loadR + " " + var.argRegister + ", " +
                    var.name + "\n\t" +
                    "syscall";

        } catch (Exception e) {
            return "Error. Variable not found";
        }
        if (code.contains("System.out.println")) {
            data += "\nnewline: .asciiz \"\\n\"\n";
            text += "\nli $v0, 4\n la $a0, newline\n  syscall \n";
        }

        return data + text;
        // return var.mipsType +" "+var.liValue;
    }

    public static String simpleExpression(String code, String operator) {
        if (code.trim().charAt(code.length() - 1) != ';') {
            return "Syntax Error. Make sure your code ends with a semi-colon\n";
        }

        int operatorIndex = code.indexOf(operator);
        // System.out.println(operatorIndex);
        String var1 = code.substring(0, operatorIndex).trim();
        String var2 = code.substring(operatorIndex + 1, code.length() - 1).trim();

        Variable varOne = Assembler.findVariable(var1);
        Variable varTwo = Assembler.findVariable(var2);

        if (varOne == null || varTwo == null) {
            return "Syntax Error. Incorrect variable";
        }

        if (varOne.isValid() && varTwo.isValid()) {
            Operator oprtr = Operator.arithOpMap.get(operator);
            // System.out.println(oprtr.operation);

            String data = ".data\n\t" +
                    varOne.name + ": " +
                    varOne.mipsType + " " + varOne.value + "\n\t" +
                    varTwo.name + ": " +
                    varTwo.mipsType + " " + varTwo.value + "\n";

            String text = ".text\n\t" +
            // ".main:\n\t"+
                    varOne.loadR + " " + Objects.requireNonNull(Assembler.getFirstEmptyRegister(Assembler.tempRegisters)).name +
                    ", " + varOne.name + "($0)" + "\n\t" +
                    varTwo.loadR + " " + Objects.requireNonNull(Assembler.getFirstEmptyRegister(Assembler.tempRegisters)).name +
                    ", " + varTwo.name + "($0)" + "\n\t" +
                    oprtr.operation + " " + Objects.requireNonNull(Assembler.getFirstEmptyRegister(Assembler.tempRegisters)).name
                    + ", " +
                    Assembler.usedRegisters.get(Assembler.usedRegisters.size() - 3).name + ", " +
                    Assembler.usedRegisters.get(Assembler.usedRegisters.size() - 2).name +
                    "\n\t";// +"syscall";

            return data + text;

        } else {
            return "Syntax error. Invalid variable detected.";
        }

    }

    public static String declarations(String code) {
        String data = ".data\n\t";
        Variable var = new Variable();
        if (code.charAt(code.trim().length() - 1) != ';') {
            return "Syntax Error. Make sure your code ends with a semi-colon\n";
        }

        // String dataVarName="";
        String[] lines = code.split(";");
        for (String line : lines) {
            line = line.trim();
            if (!line.contains("==") && Assembler.countHelper(line, "=") == 1) {

                // System.out.println(Assembler.countHelper(line, "="));
                // System.out.println(line.contains("=="));
                String type, name, value;

                String[] separate = line.split("=");
                // System.out.println(Arrays.toString(separate));
                String[] words = separate[0].split(" ");
                // System.out.println(Arrays.toString(words));
                type = words[0].trim();
                // System.out.println(type);
                name = words[1].trim();
                // System.out.println(name);
                value = separate[1].trim();
                // System.out.println(value);
                // System.out.println(type+ name+ value);
                // System.out.println(value);
                Variable var1 = new Variable(type, name, value);
                // System.out.println(var1.type+" "+ var1.name+" "+var1.value);
                // System.out.println(var1.isValid());
                boolean bool = var1.isValid();
                if (bool) {
                    var = var1;
                    Assembler.variableStore.add(var1);
                    // dataVarName = var.name;
                    Assembler.variablenames.add(var.name);
                    data = data + " " +
                            var.name + ": " +
                            var.mipsType + " " + var.value + "\n\t";

                } else {
                    return "Syntax Error. Invalid variable declaration detected.";
                }

            } else {
                return "Syntax Error. More than one equal sign";
            }
        }

        return data;
    }

}
