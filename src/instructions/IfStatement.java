package instructions;

import expressions.Expressions;
import expressions.Operations;

import java.util.ArrayList;
import java.util.List;

public class IfStatement extends Instructions {
    protected Expressions left;
    protected Expressions right;
    protected int leftValue;
    protected int rightValue;
    protected Operations operation;
    protected boolean bWasEvaluated = false;
    protected boolean bIsConditionTrue;
    protected List<Instructions> ifInstructions;
    protected List<Instructions> elseInstructions;

    public IfStatement(Expressions expr1, Expressions expr2, Operations op) {
        super();
        elseInstructions = new ArrayList<>();
        ifInstructions = new ArrayList<>();
        left = expr1;
        right = expr2;
        operation = op;
    }

    @Override
    public void addInstruction(Instructions instruction,
                               boolean bIsConditionTrue){
        if(bIsConditionTrue){
            ifInstructions.add(instruction);
        }
        else{
            elseInstructions.add(instruction);
        }
        instructions.add(instruction);
    }

    @Override
    public void execute(List<Instructions> scopeStack) {
        try {
            leftValue = left.value(scopeStack);
            rightValue = right.value(scopeStack);
        }
        catch(Exception e){
            System.out.println("Error in: if(" + left + " " +
                    operation + " " + right + ");");
            printVariablesInScope(scopeStack);
            throw e;
        }

        //determining whether the given operation yields true or false
        switch(operation){
            case EQUAL: bIsConditionTrue = (leftValue == rightValue); break;
            case NOT_EQUAL: bIsConditionTrue = (leftValue != rightValue); break;
            case BIGGER: bIsConditionTrue = (leftValue > rightValue); break;
            case SMALLER: bIsConditionTrue = (leftValue < rightValue); break;
            case BIGGER_EQUAL:
                bIsConditionTrue = (leftValue >= rightValue); break;
            case SMALLER_EQUAL:
                bIsConditionTrue = (leftValue <= rightValue); break;
            default: break;
        }
        bWasEvaluated = true;
    }

    @Override
    public void end(List<Instructions> scopeStack){
        bWasEvaluated = false;
    }

    @Override
    public List<Instructions> getInstructions() {
        if(!bWasEvaluated){return ifInstructions;}
        else {
            if (bIsConditionTrue) {
                return ifInstructions;
            }//if condition is true
            else {
                return elseInstructions;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendTabs(sb);
        sb.append("if(");
        sb.append(left.toString());
        sb.append(" ");
        sb.append(operation.toString());
        sb.append(" ");
        sb.append(right.toString());
        sb.append(")\n");
        if(!ifInstructions.isEmpty()) {
            for (Instructions ins : ifInstructions) {
                for(int i = 0; i <= numberOfTabs; ++i) {
                    ins.incrementNumberOfTabs();
                }
                sb.append(ins.toString());
                for(int i = 0; i <= numberOfTabs; ++i) {
                    ins.decrementNumberOfTabs();
                }
            }
        }
        if(!elseInstructions.isEmpty()){
            appendTabs(sb);
            sb.append("else\n");
            for(Instructions ins : elseInstructions){
                for(int i = 0; i <= numberOfTabs; ++i) {
                    ins.incrementNumberOfTabs();
                }
                sb.append(ins.toString());
                for(int i = 0; i <= numberOfTabs; ++i) {
                    ins.decrementNumberOfTabs();
                }
            }
        }

        return sb.toString();
    }
}
