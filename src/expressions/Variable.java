package expressions;

import instructions.Instructions;
import runtime.Debugger;

import java.util.List;

public class Variable extends Expressions {
    protected char name;
    public Variable(char name){
        this.name = name;
    }

    @Override
    public int value(List<Instructions> scopeStack) {
        try {
            return Debugger.getVariableValue(name, scopeStack);
        }
        catch(Exception e){
            throw e;
        }
    }

    @Override
    public String toString() {
        return name + "";
    }
}
