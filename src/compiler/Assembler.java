package compiler;

import java.util.*;

public class Assembler {

    public static String assemble(String code) {
        /**
         * clear every thing stored in these three arrayLists usedRegisters,
         * variableStore, variablenames because something might be left from the
         * last translate execution.
         */
        usedRegisters.clear();
        variableStore.clear();
        variablenames.clear();
        for (Register r : tempRegisters) {
            r.hasValue = false;
        }
        if (code.isBlank()) {
            return "Error: empty input";
        }
        return translate(code);
    }

    private final static String[] keywords = {
            "if", "else", "System.out.print",
            "while", "continue", "break"
    };
    private final static String[] arithmeticOperators = {
            "+", "-", "*", "/"
    };
    private final static String[] booleanOperators = {
            "==", "!=", ">=", "=<", ">", "<"
    };
    static Register[] tempRegisters = {
            new Register("$t0"), new Register("$t1"), new Register("$t2"),
            new Register("$t3"), new Register("$t4"), new Register("$t5"),
            new Register("$t6"), new Register("$t7"), new Register("$t8"),
            new Register("$t9")

    };
    static ArrayList<Register> usedRegisters = new ArrayList<>();
    static ArrayList<Variable> variableStore = new ArrayList<>();
    static ArrayList<String> variablenames = new ArrayList<>();
    // static ArrayList<String> labelNames = new ArrayList<>();

    public static String translate(String code) {
        if (!checkBrackets(code)) {
            return "Invalid syntax. At least one open bracket is left unclosed";
        }
        ArrayList<String> keywordsList = findKeywords(code);

        if (keywordsList.size() == 1 && code.contains("System.out.print")) {
            return PrintAndSimpleExpressions.translateSimplePrint(code, keywordsList);
        }
        ArrayList<String> arithmeticOpsList = findArithOps(code);
        // System.out.println(arithmeticOpsList);
        if (keywordsList.size() == 0 && arithmeticOpsList.size() == 1) {
            return PrintAndSimpleExpressions.simpleExpression(code, arithmeticOpsList.get(0));
        }
        if (keywordsList.size() == 0 && arithmeticOpsList.size() == 0) {
            return PrintAndSimpleExpressions.declarations(code);
        }

        ArrayList<String> booleanOpsList = findBooleanOps(code);
        if (!code.contains("while") && code.contains("if") && booleanOpsList.size() > 0) {
            return Conditionals.translateConditionals(code);
        }
        if (code.contains("while") && booleanOpsList.size() > 0) {
            return Loops.translateLoops(code);
        }

        return "code out of scope of this project";
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Error checking function
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
    // Helper Functions //

    // // helper function that counts the number of occurrences of a sequence in a
    // string
    // "String hey String eghehe String" "String"
    // "tring hey String "
    public static int countHelper(String string, String seq) {
        int index = string.indexOf(seq);
        int count = 0;
        while (index != -1) {
            count++;
            string = string.substring(index + 1);
            index = string.indexOf(seq);
        }

        return count;
    }

    // This function returns an array of the keywords the code contains
    public static ArrayList<String> findKeywords(String code) {
        ArrayList<String> keyList = new ArrayList<String>();
        for (String keyword : keywords) {
            if (code.contains(keyword)) {
                keyList.add(keyword);
            }
        }
        return keyList;
    }

    // This function returns an array of the arithmetic operators the code contains
    public static ArrayList<String> findArithOps(String code) {
        ArrayList<String> arithOpsList = new ArrayList<String>();
        for (String operator : arithmeticOperators) {
            if (code.contains(operator)) {
                arithOpsList.add(operator);
            }
        }
        return arithOpsList;
    }

    // This function returns an array of the boolean operators the code contains
    public static ArrayList<String> findBooleanOps(String code) {
        ArrayList<String> boolOpsList = new ArrayList<String>();
        for (String operator : booleanOperators) {
            if (code.contains(operator)) {
                boolOpsList.add(operator);
            }
        }
        return boolOpsList;
    }

    // This function searches for a variable from the variableStore by their name
    public static Variable findVariable(String name) {
        // System.out.println(name);
        for (Variable varibale : variableStore) {
            if ((varibale.name).equals(name.trim())) {
                // System.out.println(varibale.name);
                return varibale;
            }
        }
        return null;
    }

    // This functions searches for the first empty register
    public static Register getFirstEmptyRegister(Register[] registers) {
        for (Register r : registers) {
            if (!r.hasValue) {
                r.hasValue = true;
                usedRegisters.add(r);
                return r;
            }
        }
        return null;
    }

}
