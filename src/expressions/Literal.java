package expressions;

import expressions.Expressions;
import instructions.Instructions;

import java.util.List;

public class Literal extends Expressions {
    protected int value;
    private Literal(int value){
        this.value = value;
    }
    @Override
    public int value(List<Instructions> scopeStack) {
        return value;
    }

    public int value(){return value;}

    public static Literal of(int value){
        return new Literal(value);
    }

    @Override
    public String toString() {
        return value + "";
    }
}
