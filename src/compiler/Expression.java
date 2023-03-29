package compiler;

public class Expression {
    Variable var1;
    Operator operator;
    Variable var2;
    boolean isCondition = false;
    String result = "";

    public Expression(Variable var1, Operator operator, Variable var2) {
        this.var1 = var1;
        this.operator = operator;
        this.var2 = var2;

        if ((operator.type).equals("boolean")) {
            this.isCondition = true;
        }
    }

    public boolean isValid() {
        return (var1.type).equals(var2.type);
    }

    public String getResult() {
        return "";
    }
}
