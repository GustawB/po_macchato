package instructions;

import expressions.Expressions;
import runtime.Debugger;

import java.util.List;

public class AssignValue extends Instructions {
    //variable responsible fot keeping the information about the expression
    //whose value will be assigned to the given variable
    private Expressions valueExpr;
    //value of the valueExpr
    private int newValue;
    //name of the variable that we want to change
    private char name;
    protected AssignValue(char name, Expressions expression){
        super();
        valueExpr = expression;
        this.name = name;
    }

    //Constructor used in clone() function.
    private AssignValue(AssignValue toClone){
        super();
        this.name = toClone.name;
        this.valueExpr = toClone.valueExpr;
    }

    @Override
    public AssignValue clone() {
        return new AssignValue(this);
    }

    @Override
    public void execute(List<Instructions> scopeStack) {
        try {
            newValue = valueExpr.value(scopeStack);
        } catch (Exception e) {
            System.out.println("Error in: instructions.AssignValue("+ name +
                    ", " + valueExpr + ");");
            printVariablesInScope(scopeStack);
            throw e;
        }
        Debugger.setVariableValue(name, scopeStack, newValue);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        appendTabs(sb);
        sb.append("instructions.AssignValue(" + name + ", " + valueExpr.toString() + ")\n");
        return sb.toString();
    }
}
