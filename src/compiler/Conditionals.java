package compiler;

import java.util.ArrayList;
import java.util.Objects;

public class Conditionals {
    public static String translateConditionals(String code){
        String data=".data\n";
        String text=".text\n main:\n";
        if(code.charAt(code.length()-1)!='}'){
            return "Syntax Error. bracket not closed properly\n";
        }

        int indexOfIf = code.indexOf("if");

        if (indexOfIf > 0){
            String declarations = code.substring(0, indexOfIf).trim();
            String dataLine = PrintAndSimpleExpressions.declarations(declarations);
//            System.out.println(dataLine);
            if (dataLine.contains(".data")){
                data += dataLine.substring(6);
            }else{
                return "Error. Wrong declarations";
            }
        }

        String conditional = code.substring(indexOfIf);
        int statementStart = conditional.indexOf("{");
        String conditionPart = conditional.substring(0, statementStart);
        int conStart = conditionPart.indexOf("(");
        int conEnd = conditionPart.lastIndexOf(")");
        String condition = conditional.substring(conStart+1, conEnd);
        ArrayList<String> conditionBooleans = Assembler.findBooleanOps(condition);

        String mipsCondition = translateBooleanExpression(condition);
        int labelStart = mipsCondition.indexOf("label:");
        text+=mipsCondition.substring(0, labelStart);
//        System.out.println(text);
//        int elseStart = code.indexOf("else");
//        System.out.println(elseStart);
        String[] codeBlocks = conditional.split("else");
        String ifStatements = codeBlocks[0].substring(statementStart+1).trim();
        String elseStatements="";
        if (codeBlocks.length>1){
            elseStatements = codeBlocks[1].trim();
            elseStatements=elseStatements.substring(1);
//            System.out.println(elseStatements.charAt(elseStatements.length()-1));

        }
//        System.out.println(ifStatements);
        if(ifStatements.trim().charAt(ifStatements.length()-1)!='}'){
            return "Syntax Error. curved bracket not closed properly";
        }
        if(elseStatements.length()>1){
            if(elseStatements.charAt(elseStatements.length()-1)!='}'){
                return "Syntax Error. curved bracket not closed properly";
            }
        }

        ifStatements=ifStatements.substring(0,ifStatements.length()-1);

//        System.out.println(statement);
        String[] ifLines = ifStatements.split(";");
        for(String line: ifLines){
//            System.out.println(line+"\n");
            line = line.trim() +';';
            if (!line.contains("==") && Assembler.countHelper(line, "=")==1){
                //System.out.println(line);
                String type, name, value;

                String[] separate = line.split("=");
                //System.out.println(Arrays.toString(separate));
                String[] words = separate[0].split(" ");
                //System.out.println(Arrays.toString(words));
                type=words[0];
                name=words[1];
                value=separate[1];
                //System.out.println(value);
                Variable var1 = new Variable(type,name, value);
                //System.out.println(var1.isValid());
                if (var1.isValid()){

                    Assembler.variableStore.add(var1);
                }
                else{
                    return "Error. Invalid variable declaration detected.";
                }

            }
            if (line.contains("System.out.println")){
                String printLine = PrintAndSimpleExpressions.translateSimplePrint(line, Assembler.findKeywords(line));
//                System.out.println(printLine);
                String[] blocks = printLine.split(".text");
//                blocks[1].replace("syscall", "j exit");
                try{
                    text+="li $v0, 10\n syscall\n"
                            +"label:\n\t"+ blocks[1].trim() + "\n li $v0, 10\n syscall\n";
//                System.out.println(text);
                    data+=blocks[0].substring(6).trim()+'\n';
//                System.out.println(data);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                    return "Error caught.";
                }

            }
        }




//        return translateBooleanExpression(condition);
        if (elseStatements.length()==0){
            text = text.replace("j end", "");
        }else{
            String[] elseLines = elseStatements.split(";");
            for (String line: elseLines){
                line = line.trim() +';';
                if (!line.contains("==") && Assembler.countHelper(line, "=")==1){
                    //System.out.println(line);
                    String type, name, value;

                    String[] separate = line.split("=");
                    //System.out.println(Arrays.toString(separate));
                    String[] words = separate[0].split(" ");
                    //System.out.println(Arrays.toString(words));
                    type=words[0];
                    name=words[1];
                    value=separate[1];
                    //System.out.println(value);
                    Variable var1 = new Variable(type,name, value);
                    //System.out.println(var1.isValid());
                    if (var1.isValid()){

                        Assembler.variableStore.add(var1);
                    }
                    else{
                        return "Error. Invalid variable declaration detected.";
                    }

                }
                if (line.contains("System.out.println")){
                    String printLine = PrintAndSimpleExpressions.translateSimplePrint(line, Assembler.findKeywords(line));
//                System.out.println(printLine);
                    String[] blocks = printLine.split(".text");
//                blocks[1].replace("syscall", "j exit");
                    text+="end:\n\t"+ blocks[1].trim() + "\n li $v0, 10\n syscall\n";
//                System.out.println(text);
                    data+=blocks[0].substring(6).trim()+'\n';
//                System.out.println(data);

                }
            }
        }
        return data + text;

    }

    public static String translateBooleanExpression(String booleanExpression) {
        String[] operands = booleanExpression.split(" "); // Split the boolean expression by spaces
        String op1 = operands[0];
        String op2 = operands[2];
        String op = operands[1];
        String mipsOp = "";
        switch (op) { // Map the operator to its MIPS assembly instruction
            case "==":
                mipsOp = "beq";
                break;
            case "!=":
                mipsOp = "bne";
                break;
            case ">":
                mipsOp = "bgt";
                break;
            case ">=":
                mipsOp = "bge";
                break;
            case "<":
                mipsOp = "blt";
                break;
            case "<=":
                mipsOp = "ble";
                break;
            default:
                return "Invalid operator: " + op;
        }
        String mipsCode = "li " + Objects.requireNonNull(Assembler.getFirstEmptyRegister(Assembler.tempRegisters)).name+", " + op1 + "\n"; // Load operand 1 into $t0
        mipsCode += "li "+ Objects.requireNonNull(Assembler.getFirstEmptyRegister(Assembler.tempRegisters)).name+", " + op2 + "\n"; // Load operand 2 into $t1
        mipsCode += mipsOp +" "+ Assembler.usedRegisters.get(Assembler.usedRegisters.size()-2).name+", "
                + Assembler.usedRegisters.get(Assembler.usedRegisters.size()-1).name+", label\n"; // Construct the MIPS code

        mipsCode += "j end\n"; // Jump to the end of the code
        mipsCode += "label:\n"; // Define the label for the conditional jump
//        mipsCode += "li $v0, 1\n"; // Load 1 into $v0 to indicate true
        mipsCode += "j exit\n"; // Jump to the exit of the code
        mipsCode += "end:\n"; // Define the end label
//        mipsCode += "li $v0, 0\n"; // Load 0 into $v0 to indicate false
        mipsCode += "exit:\n"; // Define the exit label
        mipsCode += "li $v0, 10\n syscall\n";
        return mipsCode;
    }

}
