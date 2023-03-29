/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compiler;

import java.util.regex.Pattern;

/**
 *
 * @author Nathnael_Dereje
 */
public class unwrap {

    /**
     * Finds the position of the matching closing brace for the opening brace at
     * the given position. Returns -1 if no matching brace is found.
     */
    private static int findMatchingClosingBrace(String code, int start) {
        int depth = 1;
        int end = start + 1;
        while (depth > 0 && end < code.length()) {
            if (code.charAt(end) == '{') {
                depth++;
            } else if (code.charAt(end) == '}') {
                depth--;
            }
            end++;
        }
        return (depth == 0) ? (end - 1) : -1;
    }

    private static String removeOuterClass(String code) {
        int classStart = code.indexOf("class");

        if (classStart == -1) {
            return code;
        }

        int braceStart = code.indexOf("{", classStart);

        if (braceStart == -1) {
            return code;
        }

        int braceEnd = findMatchingClosingBrace(code, braceStart);

        if (braceEnd == -1) {
            return code;
        }

        String classContent = code.substring(braceStart + 1, braceEnd);
        return classContent;
    }

    private static String getContentInMainFunction(String code) {
        // Find the position of the first occurrence of the "public static void main"
        // function
        int functionStart = code.indexOf("public static void main");

        // If the function is not found, return the original code
        if (functionStart == -1) {
            return code;
        }

        // Find the position of the opening brace of the function
        int braceStart = code.indexOf("{", functionStart);

        // If the opening brace is not found, return the original code
        if (braceStart == -1) {
            return code;
        }

        // Find the position of the matching closing brace for the opening brace
        int braceEnd = findMatchingClosingBrace(code, braceStart);

        // If there's no matching '}', return the original code
        if (braceEnd == -1) {
            return code;
        }

        // Get the content inside the braces of the function
        String functionContent = code.substring(braceStart + 1, braceEnd);

        return functionContent.trim();
    }

    public static String unwrapCode(String code) {
        return removeComments(removeEmptyLines(getContentInMainFunction(removeOuterClass(code)))).replaceAll("(?m)^[ \t]*\r?\n", "");
    }

    private static String removeComments(String code) {
        // Regular expression to match comments of the form /* ... */ and //
        Pattern commentPattern = Pattern.compile("/\\*.*?\\*/|//.*?$", Pattern.MULTILINE | Pattern.DOTALL);
        return commentPattern.matcher(code).replaceAll("");
    }

    private static String removeEmptyLines(String str) {
        return str.replaceAll("(?m)^[ \t]*\r?\n", "");
    }
}
