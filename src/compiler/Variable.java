/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compiler;
import java.util.Arrays;
    
/**
 *
 * @author Hp
 */
public class Variable {
    
    String name;
    String type;
    String value;
    int liValue=-1;
    String loadR="";
    String mipsType="";
    String argRegister="";
    public Variable(String type, String name, String value){
        this.type=type;
        this.name=name;
        this.value=value;
    }

    public boolean isValid(){
        final String[] validTypes ={"String", "int", "boolean", "double", "char"};

        if (!Arrays.asList(validTypes).contains(type)){
            return false;
        }

        else if(type.equals("String")) {
            if (value.contains("\"") && value.lastIndexOf("\"") != value.indexOf("\"")) {
                liValue=4;
                loadR="la";
                mipsType=".asciiz";
                argRegister="$a0";
                return true;
            }
        }
        else if(type.equals("char")){
            if (value.length()<=3 && value.contains("\'") && value.lastIndexOf("\'") != value.indexOf("\'")) {
                liValue=4;
                loadR="la";
                mipsType=".byte";
                argRegister="$a0";
                return true;
            }
        }
        else if (type.equals("int")){
            try{
                int temp = Integer.parseInt(value);
                liValue=1;
                loadR="lw";
                mipsType=".word";
                argRegister="$a0";
                return true;
            }catch(Exception e){
                return false;
            }
        }
        else if (type.equals("double")){
            try{
                double temp= Double.parseDouble(value);
                return true;
            }catch(Exception e){
                return false;
            }
        }
        else if (type.equals("boolean")){
            if(value.equals("true") || value.equals("false")){
                return true;
            }
        }
        return false;
    }
//    public int liValue(){
//        if (type.equals("String") || type.equals("char")){
//            return 4;
//        }
//        if (type.equals("int")){
//            return 1;
//        }
//        if (type.equals("double")){
//
//        }
//        return 0;
//    }

}
