package expressions;

import instructions.Instructions;

import java.util.List;

public class Modulo extends Expressions {
    Expressions left;
    Expressions right;
    public Modulo(Expressions left, Expressions right){
        this.left = left;
        this.right = right;
    }

    @Override
    public int value(List<Instructions> scopeStack) {
        return left.value(scopeStack) % right.value(scopeStack);
    }

    @Override
    public String toString(){
        return (left.toString() + " % " + right.toString());
    }
}
