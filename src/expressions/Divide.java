package expressions;

import instructions.Instructions;

import java.util.List;

public class Divide extends Expressions {
    Expressions left;
    Expressions right;
    private Divide(Expressions left, Expressions right){
        this.left = left;
        this.right = right;
    }

    @Override
    public int value(List<Instructions> scopeStack) {
        return left.value(scopeStack) / right.value(scopeStack);
    }

    @Override
    public String toString(){
        return (left.toString() + " / " + right.toString());
    }

    public static Divide of(Expressions left, Expressions right){
        return new Divide(left, right);
    }
}
