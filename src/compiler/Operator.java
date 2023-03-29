package compiler;

import java.util.ArrayList;
import java.util.HashMap;

public class Operator {
    String symbol;
    String type; // arithmetic or boolean
    String operation;


    static ArrayList<Operator> arthimeticOperators = new ArrayList<Operator>();
    public Operator(String symbol, String type, String operation){
        this.symbol=symbol;
        this.type=type;
        this.operation=operation;
    }

    public static HashMap<String, Operator> arithOpMap = new HashMap<String, Operator>();


    static {
        Operator add = new Operator("+","arithmetic", "add");
        Operator subtract = new Operator("-","arithmetic", "sub");
        Operator multiply = new Operator ("*", "arithmetic", "mul");
        Operator divide = new Operator ("/", "arithmetic", "div");
        Operator exponent = new Operator("**", "arithemetic", "mul");

        arithOpMap.put("+", add);
        arithOpMap.put("-", subtract);
        arithOpMap.put("*", multiply);
        arithOpMap.put( "/", divide);
        arithOpMap.put("**", exponent);
    }



}
