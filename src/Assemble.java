import java.util.*;

public class Assemble {

    public static String main(String[] args) {
//        String code = "String val=\"hello\"; System.out.println(val);";
//        String code = "int val=9; System.out.println(val);";
//        String code ="System.out.println(var9);";
//        Variable var9 = new Variable("int", "var9", "9");
//        variableStore.add(var9);
//        Variable var8= new Variable("int", "var8", "3");
//        variableStore.add(var8);
//        String code="if (8 == 8){System.out.println(\"true\");} \n else{System.out.println(\"false\")}";
        String code ="int x=3; System.out.println(y);";
        return (translate(code));


    }
    private final static String[] keywords =
            {
                    "if", "else", "System.out.print",
                    "while", "continue", "break", "switch", "case", "default"
            };
    private final static String[] arithmeticOperators =
            {
                    "+","-", "*", "/"
            };
    private final static String[] booleanOperators =
            {
                    "==", "!=", ">=", "=<"
            };
    private static Register[] tempRegisters =
            {
                    new Register("$t0"), new Register("$t1"),new Register("$t2"),
                    new Register("$t3"), new Register("$t4"),new Register("$t5"),
                    new Register("$t6"), new Register("$t7"),new Register("$t8"),
                    new Register("$t9")

            };
    private static ArrayList<Register> usedRegisters= new ArrayList<>();
    static ArrayList<Variable> variableStore = new ArrayList<>();
    static ArrayList<String> variablenames = new ArrayList<>();




    public static String translate(String code) {
        if(!checkBrackets(code)){
            return "Invalid syntax. At least one open bracket is left unclosed";
        }
        ArrayList<String> keywordsList = findKeywords(code);

        if (keywordsList.size() == 1 && code.contains("System.out.print")) {
            return translateSimplePrint(code, keywordsList);
        }
        ArrayList<String> arithmeticOpsList = findArithOps(code);
        //System.out.println(arithmeticOpsList);
        if (keywordsList.size()==0 && arithmeticOpsList.size()==1){
            return simpleExpression(code, arithmeticOpsList.get(0));
        }

        ArrayList<String> booleanOpsList = findBooleanOps(code);
        if (code.indexOf("if")==0 && booleanOpsList.size()>0){
            return translateConditionals(code, booleanOpsList);
        }


        return null;
    }
    public static String translateConditionals(String code, ArrayList<String> booleanList){
        String data=".data\n";
        String text=".text\n main:\n";
        if(code.charAt(code.length()-1)!='}'){
            return "Syntax Error. bracket not closed properly\n";
        }
        int statementStart = code.indexOf("{");
        String conditionPart = code.substring(0, statementStart);
        int conStart = conditionPart.indexOf("(");
        int conEnd = conditionPart.lastIndexOf(")");
        String condition = code.substring(conStart+1, conEnd);
        ArrayList<String> conditionBooleans = findBooleanOps(condition);

        String mipsCondition = translateBooleanExpression(condition);
        int labelStart = mipsCondition.indexOf("label:");
        text+=mipsCondition.substring(0, labelStart);
//        System.out.println(text);
//        int elseStart = code.indexOf("else");
//        System.out.println(elseStart);
        String[] codeBlocks = code.split("else");
        String ifStatements = codeBlocks[0].substring(statementStart+1).trim();
        String elseStatements="";
        if (codeBlocks.length>1){
            elseStatements = codeBlocks[1].trim();
            elseStatements=elseStatements.substring(1);
            System.out.println(elseStatements.charAt(elseStatements.length()-1));

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
            if (!line.contains("==") && countHelper(line, "=")==1){
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

                    variableStore.add(var1);
                }
                else{
                    return "Invalid variable declaration detected.";
                }

            }
            if (line.contains("System.out.println")){
                String printLine = translateSimplePrint(line, findKeywords(line));
//                System.out.println(printLine);
                String[] blocks = printLine.split(".text");
//                blocks[1].replace("syscall", "j exit");
                text+="li $v0, 10\n syscall\n"
                        +"label:\n\t"+ blocks[1].trim() + "\n li $v0, 10\n syscall\n";
//                System.out.println(text);
                data+=blocks[0].substring(6).trim()+'\n';
//                System.out.println(data);

            }
        }




//        return translateBooleanExpression(condition);
        if (elseStatements.length()==0){
            text = text.replace("j end", "");
        }else{
            String[] elseLines = elseStatements.split(";");
            for (String line: elseLines){
                line = line.trim() +';';
                if (!line.contains("==") && countHelper(line, "=")==1){
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

                        variableStore.add(var1);
                    }
                    else{
                        return "Invalid variable declaration detected.";
                    }

                }
                if (line.contains("System.out.println")){
                    String printLine = translateSimplePrint(line, findKeywords(line));
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

    public static String simpleExpression(String code, String operator){
        if(code.charAt(code.length()-1)!=';'){
            return "Syntax Error. Make sure your code ends with a semi-colon\n";
        }


        int operatorIndex = code.indexOf(operator);
        //System.out.println(operatorIndex);
        String var1 = code.substring(0, operatorIndex).trim();
        String var2 = code.substring(operatorIndex+1, code.length()-1).trim();

        Variable varOne = findVariable(var1);
        Variable varTwo = findVariable(var2);

        if (varOne == null || varTwo==null){
            return "Syntax Error. Incorrect variable";
        }

        if (varOne.isValid() && varTwo.isValid()){
            Operator oprtr = Operator.arithOpMap.get(operator);
            //System.out.println(oprtr.operation);

            String data = ".data\n\t" +
                    varOne.name + ": " +
                    varOne.mipsType + " " +varOne.value+ "\n\t" +
                    varTwo.name + ": " +
                    varTwo.mipsType + " " +varTwo.value+ "\n";

            String text = ".text\n\t" +
                    //".main:\n\t"+
                    varOne.loadR + " " + Objects.requireNonNull(getFirstEmptyRegister(tempRegisters)).name +
                    ", "+varOne.name +"($0)" + "\n\t" +
                    varTwo.loadR + " " + Objects.requireNonNull(getFirstEmptyRegister(tempRegisters)).name +
                    ", "+varTwo.name +"($0)" + "\n\t" +
                    oprtr.operation + " " +Objects.requireNonNull(getFirstEmptyRegister(tempRegisters)).name+", "+
                    usedRegisters.get(usedRegisters.size()-3).name + ", "+
                    usedRegisters.get(usedRegisters.size()-2).name+
                     "\n\t";//+"syscall";

            return data + text;


        }
        else {
            return "Syntax error. Invalid variable detected.";
        }

    }





    public static String translateSimplePrint(String code, ArrayList<String> keywordsList){
        // ArrayList<Variable> variables = new ArrayList<Variable>();
        Variable var = new Variable();
        if(code.charAt(code.length()-1)!=';'){
            return "Syntax Error. Make sure your code ends with a semi-colon\n";
        }


        // count how many times print is called per line and
        // if it is more than one return syntax error if it is only one, count it
        String[] lines = code.split(";");
        if (lines.length<=2){
            for(String line: lines){

                if(countHelper(line, keywordsList.get(0))>1) {
                    return "Syntax Error. Make sure you have used semi-colons correctly.\n";
                }
                if(countHelper(line, keywordsList.get(0))==0){
                    if (!line.contains("==") && countHelper(line, "=")==1){
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
                            var = var1;
                            variableStore.add(var1);
                            variablenames.add(var.name);

                        }
                        else{
                            return "Syntax Error. Invalid variable declaration detected.";
                        }

                    }else{
                        return "Syntax Error. More than one equal to sign";
                    }
                }
                if (line.contains("System.out.print")){
                    if (line.trim().indexOf("System.out.print")!=0){
                        return "Syntax Error. Check your semi colons";
                    }
                    int start = line.indexOf("(");
                    int end = line.indexOf(")");
                    if (start!=-1 && end!=-1 && start < end){
                        String varName = line.substring(start+1,end);
//                        System.out.println(varName);
                        Variable var0 = findVariable(varName);
                        if (!variablenames.contains(varName)){
                            return "Error: This variable is not declared.";
                        }

                        if (var0!=null && var0.isValid()){
                            var = var0;
//                            System.out.println(var.name+", "+var.type+", "+var.value);
                        }
                        else if(varName.length()!=0){

                            if (varName.charAt(0)=='\"' && varName.charAt(varName.length()-1)=='\"'){
                                Variable varStr = new Variable("String",
                                        "var"+variablenames.size(),
                                        varName);
                                //System.out.println("var"+variablenames.size());
                                variablenames.add("var"+variablenames.size());
                                if (!varStr.isValid()){
                                    return "Variable not defined correctly";
                                }
                                //System.out.println(varStr.value);
                                //System.out.println(varStr.isValid());
                                variableStore.add(varStr);
                                //System.out.println(variableStore.get(0).name);
                                var=varStr;
                               //System.out.println(var.name);
                            }
                        }
                        else if (varName.length()==0){
                            return "\n";
                        }
                        else{
                            return "Syntax Error. Unknown or undeclared Variable used.";
                        }

                    }
                }

            }
        }
//        if(lines.length==1){
//
//        }
        String data="", text ="";
//        System.out.println(var.name);
        try{

            data = ".data\n\t" +
                    var.name + ": " +
                    var.mipsType + " " +var.value+ "\n";

            text = ".text\n\t" +
                    "li $v0, " + var.liValue + "\n\t" +
                    var.loadR + " " + var.argRegister +", "+
                    var.name + "\n\t" +
                    "syscall";

        }catch (Exception e){
            return "Variable not found";
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
        String mipsCode = "li " + getFirstEmptyRegister(tempRegisters).name+", " + op1 + "\n"; // Load operand 1 into $t0
        mipsCode += "li "+getFirstEmptyRegister(tempRegisters).name+", " + op2 + "\n"; // Load operand 2 into $t1
        mipsCode += mipsOp +" "+ usedRegisters.get(usedRegisters.size()-2).name+", "
                +usedRegisters.get(usedRegisters.size()-1).name+", label\n"; // Construct the MIPS code

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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//               Error checking function
    // This function checks if open brackets are closed.
    public static boolean checkBrackets(String code) {
        Stack<Character> stack = new Stack<>();

        for (char c : code.toCharArray()) {
            if (c == '{' || c == '[' || c == '(') {
                stack.push(c);
            } else if (c == '}') {
                if (stack.isEmpty() || stack.pop() != '{') {
                    return false;
                }
            } else if (c == ']') {
                if (stack.isEmpty() || stack.pop() != '[') {
                    return false;
                }
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(') {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                              Helper Functions                                                                    //

    // helper function that counts the number of occurrences of a sequence in a string
    public static int countHelper(String string, String seq){
        int index = string.indexOf(seq);
        int count = 0;
        while (index != -1) {
            count++;
            string = string.substring(index + 1);
            index = string.indexOf("is");
        }

        return count;
    }


    // This function returns an array of the keywords the code contains
    public static ArrayList<String> findKeywords(String code){
        ArrayList<String> keyList=new ArrayList<String>();
        for (String keyword: keywords){
            if (code.contains(keyword)){
                keyList.add(keyword);
            }
        }
        return keyList;
    }
    // This function returns an array of the arithmetic operators the code contains
    public static ArrayList<String> findArithOps(String code){
        ArrayList<String> arithOpsList=new ArrayList<String>();
        for (String operator: arithmeticOperators){
            if (code.contains(operator)){
                arithOpsList.add(operator);
            }
        }
        return arithOpsList;
    }
    // This function returns an array of the boolean operators the code contains
    public static ArrayList<String> findBooleanOps(String code){
        ArrayList<String> boolOpsList=new ArrayList<String>();
        for (String operator: booleanOperators){
            if (code.contains(operator)){
                boolOpsList.add(operator);
            }
        }
        return boolOpsList;
    }

    // This function searches for a variable from the variableStore by their name
    public static Variable findVariable(String name){
        //System.out.println(name);
        for(Variable varibale : variableStore){
            if((varibale.name).equals(name.trim())){
                //System.out.println(varibale.name);
                return varibale;
            }
        }
        return null;
    }

    // This functions searches for the first empty register
    public static Register getFirstEmptyRegister(Register[] registers) {
        for (Register r : registers) {
            if (!r.hasValue) {
                r.hasValue=true;
                usedRegisters.add(r);
                return r;
            }
        }
        return null;
    }

}
