package instructions;

import expressions.Expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//instructions.ForLoop extends instructions.Block because it has its own scope,
//and the block class represents that
public class ForLoop extends Block{
    private char iteratorName;
    //expression that holds the value of the loop iterator
    private Expressions iterExpr;
    private int iterValue;
    //used to determine whether the loop ended or not
    private int iterValueStateAtTheEnd;
    //used to determine whether the loop has started
    private boolean isStarted;
    private boolean bAreThereAnyIterationsToPerform = true;

    private ForLoop(Builder lb){
        super(); //for my visibility
        this.iteratorName = lb.iteratorName;
        this.iterExpr = lb.limit;
        this.instructions.addAll(lb.instructions);
        isStarted = false;
    }

    private ForLoop(ForLoop toClone){
        super();
        iterExpr = toClone.iterExpr;
        iteratorName = toClone.iteratorName;
        for(Instructions ins : toClone.instructions){
            this.instructions.add(ins.clone());
        }
        isStarted = false;
    }
    @Override
    public ForLoop clone() {
        return new ForLoop(this);
    }

    @Override
    public void end(List<Instructions> scopeStack){
        super.end(scopeStack);
        isStarted = false;
    }

    @Override
    public boolean doesRequireMoreSteps(){
        return (iterValue < iterValueStateAtTheEnd);
    }

    @Override
    public List<Instructions> getInstructions(){
        if(bAreThereAnyIterationsToPerform) {
            return instructions;
        }
        else{
            return new ArrayList<>();
        }
    }

    @Override
    public void execute(List<Instructions> scopeStack) {
        if(!isStarted){
            bAreThereAnyIterationsToPerform = true;
            super.execute(scopeStack);
            isStarted = true;
            iterValue = 0;
            //trying to calculate the value of the expression
            try {
                iterValueStateAtTheEnd = iterExpr.value(scopeStack);
            }
            catch(Exception e){//calculating the value of the expression failed
                System.out.println("Error in: instructions.ForLoop(" + iteratorName +
                        "; " + iterExpr + ")");
                printVariablesInScope(scopeStack);
                throw e;
            }
            if(!doesRequireMoreSteps()){
                bAreThereAnyIterationsToPerform = false;
            }
            this.variables.put(iteratorName, iterValue);
        }
        else{//updating iterator value
            this.variables.replace(iteratorName, iterValue);
        }

        ++iterValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendTabs(sb);
        sb.append("for(" + iteratorName + "; " + iterExpr.toString() + ")\n");
        for(Instructions ins : instructions){
            for(int i = 0; i <= numberOfTabs; ++i) {
                ins.incrementNumberOfTabs();
            }
            sb.append(ins.toString());
            for(int i = 0; i <= numberOfTabs; ++i) {
                ins.decrementNumberOfTabs();
            }
        }

        return sb.toString();
    }

    public static class Builder {
        private char iteratorName;
        private Expressions limit;

        private final List<Instructions> instructions = new ArrayList<>();

        public Builder(char iteratorName, Expressions limit) {
            this.iteratorName = iteratorName;
            this.limit = limit;
        }

        public Builder assign(char assignTo, Expressions valueToAssign) {
            instructions.add(new AssignValue(assignTo, valueToAssign));
            return this;
        }

        public Builder print(Expressions valueToPrint) {
            instructions.add(new PrintExpr(valueToPrint));
            return this;
        }

        public Builder newLoop(ForLoop loop) {
            instructions.add(loop);
            return this;
        }

        public Builder condition(IfStatement condition) {
            instructions.add(condition);
            return this;
        }

        public ForLoop build(){return new ForLoop(this);}
    }
}
