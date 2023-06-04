package expressions;

import expressions.Expressions;
import instructions.Instructions;

import java.util.List;

public class Literal extends Expressions {
    protected int value;
    public Literal(int value){
        this.value = value;
    }
    @Override
    public int value(List<Instructions> scopeStack) {
        return value;
    }

    @Override
    public String toString() {
        return value + "";
    }
}
