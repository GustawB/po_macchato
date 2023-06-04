package expressions;

import instructions.Instructions;

import java.util.List;

public class Substract extends Expressions {
    Expressions left;
    Expressions right;
    public Substract(Expressions left, Expressions right){
        this.left = left;
        this.right = right;
    }

    @Override
    public int value(List<Instructions> scopeStack) {
        int result;
        try{
            result = left.value(scopeStack) - right.value(scopeStack);
        }
        catch(Exception e){
            throw e;
        }
        return result;
    }

    @Override
    public String toString(){
        return (left.toString() + " - " + right.toString());
    }
}
