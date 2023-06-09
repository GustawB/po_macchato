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

    private IfStatement(Builder cb){
        ifInstructions = new ArrayList<>();
        elseInstructions = new ArrayList<>();
        ifInstructions.addAll(cb.ifInstructions);
        elseInstructions.addAll(cb.elseInstructions);
        left = cb.expression;
        right = cb.compareWith;
        operation = cb.operator;
    }

    //used for cloning
    private IfStatement(IfStatement toClone){
        ifInstructions = new ArrayList<>();
        elseInstructions = new ArrayList<>();
        left = toClone.left;
        right = toClone.right;
        operation = toClone.operation;
        for(Instructions ins : toClone.ifInstructions){
            this.ifInstructions.add(ins.clone());
        }
        for(Instructions ins : toClone.elseInstructions){
            this.elseInstructions.add(ins.clone());
        }
    }

    @Override
    public IfStatement clone(){
        return new IfStatement(this);
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

    public static class Builder {
        private List<Instructions> ifInstructions = new ArrayList<>();
        private List<Instructions> elseInstructions = new ArrayList<>();
        private Operations operator;
        private Expressions expression;
        private Expressions compareWith;
        private boolean addToIfInstructions = true;

        public Builder(Expressions expression, Expressions compareWith, Operations compareBy){
            this.expression = expression;
            this.compareWith = compareWith;
            operator = compareBy;
        }

        public Builder switchToElseBranch(){
            addToIfInstructions = false;
            return  this;
        }

        public Builder switchToIfBranch(){
            addToIfInstructions = true;
            return this;
        }

        public Builder assign(char assignTo, Expressions valueToAssign){
            if(addToIfInstructions) {
                ifInstructions.add(new AssignValue(assignTo, valueToAssign));
            }
            else{
                elseInstructions.add(new AssignValue(assignTo, valueToAssign));
            }

            return this;
        }

        public Builder print(Expressions valueToPrint){
            if(addToIfInstructions) {
                ifInstructions.add(new PrintExpr(valueToPrint));
            }
            else {
                elseInstructions.add(new PrintExpr(valueToPrint));
            }

            return this;
        }

        public Builder condition(IfStatement condition){
            if(addToIfInstructions){
                ifInstructions.add(condition);
            }
            else{
                elseInstructions.add(condition);
            }

            return this;
        }

        public Builder newLoop(ForLoop loop){
            if(addToIfInstructions){
                ifInstructions.add(loop);
            }
            else{
                elseInstructions.add(loop);
            }

            return this;
        }

        public Builder invoke(Procedure procedure){
            if(addToIfInstructions) {
                ifInstructions.add(procedure);
            }
            else{
                elseInstructions.add(procedure);
            }
            return this;
        }

        public IfStatement build(){return new IfStatement(this);}
    }
}
