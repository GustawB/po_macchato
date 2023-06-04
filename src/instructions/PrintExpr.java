package instructions;

import expressions.Expressions;

import java.util.List;

public class PrintExpr extends Instructions {
    protected Expressions expr;
    public PrintExpr(Expressions expr) {
        super();
        this.expr = expr;
    }

    @Override
    public void execute(List<Instructions> scopeStack) {
        int value;
        try{
            value = expr.value(scopeStack);
        }
        catch (Exception e){
            System.out.println("Error in: instructions.PrintExpr(" + expr + ")");
            printVariablesInScope(scopeStack);
            throw e;
        }
        System.out.println(value);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        appendTabs(sb);
        sb.append("PrintfExpr(" + expr.toString() + ")\n");
        return sb.toString();
    }
}
