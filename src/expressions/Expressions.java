package expressions;

import instructions.Instructions;

import java.util.List;

public abstract class Expressions {
    //returns the value of the expression
    public abstract int value(List<Instructions> scopeStack);

    public abstract String toString();
}
