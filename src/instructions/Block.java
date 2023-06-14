package instructions;

import exceptions.InvalidNumberOfProcedureParametersException;
import exceptions.NonExistingProcedureException;
import exceptions.NonExistingVariableException;
import expressions.Expressions;
import expressions.Literal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block extends Instructions {
    //When we make the new object of the class block, we pass all the
    //variable declarations to the constructor, so we won't be able to
    //add new variables after the end of the declaration series
    protected boolean bWasThereVariableRedeclaration = false;
    protected boolean bWasThereProcedureRedeclaration = false;
    protected boolean bWasVariableRedeclarationFirst = false;

    //Constructor used by the builder.
    private Block(Builder bb){
        this.bWasThereProcedureRedeclaration = bb.bWasThereProcedureRedeclaration;
        this.bWasThereVariableRedeclaration = bb.bWasThereVariableRedeclaration;
        this.bWasVariableRedeclarationFirst = bb.bWasVariableRedeclarationFirst;
        this.variables = bb.variables;
        this.procedures = bb.procedures;
        //procedureCopying = new HashMap<>();
        //preventing shallow copy on variables
        for (Character c : bb.variables.keySet()) {
            variables.put(c, bb.variables.get(c));
        }
        for (String s : bb.procedures.keySet()) {
            procedures.put(s, bb.procedures.get(s));
        }
        this.instructions.addAll(bb.instructions);
    }


    //Constructor used to quickly initialize variables in child classes
    protected Block(){
        variables = new HashMap<>();
        procedures = new HashMap<>();
        instructions = new ArrayList<>();
    }

    //Clone constructor
    private Block(Block toClone){
        variables = new HashMap<>();
        procedures = new HashMap<>();
        instructions = new ArrayList<>();
        for(Instructions ins : toClone.instructions){
            this.instructions.add(ins.clone());
        }
        this.variables.putAll(toClone.variables);
        this.procedures.putAll(toClone.procedures);
    }

    @Override
    public Block clone() {
        return new Block(this);
    }

    @Override
    public void end(List<Instructions> scopeStack){
        scopeStack.remove(this);
    }

    @Override
    public void execute(List<Instructions> scopeStack) {
        if(bWasThereVariableRedeclaration){
            if(bWasThereProcedureRedeclaration && !bWasVariableRedeclarationFirst) {
                System.out.println("Error in: Block;");
                printVariablesInScope(scopeStack);
                throw new NonExistingProcedureException();
            }
            else{
                System.out.println("Error in: Block;");
                printVariablesInScope(scopeStack);
                throw new NonExistingVariableException();
            }
        }
        else if(bWasThereProcedureRedeclaration){
            System.out.println("Error in: Block;");
            printVariablesInScope(scopeStack);
            throw new NonExistingProcedureException();
        }

        scopeStack.add(this);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        appendTabs(sb);
        sb.append("begin Block\n");
        for(Instructions ins : instructions){
            for(int i = 0; i <= numberOfTabs; ++i) {
                ins.incrementNumberOfTabs();
            }
            sb.append(ins.toString());
            for(int i = 0; i <= numberOfTabs; ++i) {
                ins.decrementNumberOfTabs();
            }
        }
        appendTabs(sb);
        sb.append("end Block\n");

        return sb.toString();
    }

    //Builder for the Block class. Its values are updated by calling the
    //respective functions. Then, by calling the build() function we
    //create a new object of the Block class by using the special constructor.
    public static class Builder {
        private final Map<Character, Integer> variables = new HashMap<>();
        private final Map<String, Procedure> procedures  = new HashMap<>();
        private final List<Instructions> instructions = new ArrayList<>();
        boolean bWasThereVariableRedeclaration = false;
        boolean bWasThereProcedureRedeclaration = false;
        boolean bWasVariableRedeclarationFirst = false;

        public Builder declareVariable(char name, Literal value){
            if(variables.containsKey(name)){
                if(!bWasThereProcedureRedeclaration && bWasThereVariableRedeclaration){
                    bWasVariableRedeclarationFirst = true;
                }
                bWasThereVariableRedeclaration = true;
            }
            else {
                variables.put(name, value.value());
            }
            return this;
        }
        public Builder declareProcedure(Procedure procedure){
            if(procedures.containsKey(procedure.getProcedureName())){
                bWasThereProcedureRedeclaration = true;
            }
            else {
                procedures.put(procedure.getProcedureName(), procedure);
            }
            return this;
        }

        public Builder invoke(Procedure procedure){
            instructions.add(procedure);
            return this;
        }
        public Builder newBlock(Block newBlock){
            instructions.add(newBlock);
            return this;
        }

        public Builder assign(char assignTo, Expressions valueToAssign){
            instructions.add(new AssignValue(assignTo, valueToAssign));
            return this;
        }

        public Builder print(Expressions valueToPrint){
            instructions.add(new PrintExpr(valueToPrint));
            return this;
        }

        public Builder newLoop(ForLoop loop){
            instructions.add(loop);
            return this;
        }

        public Builder condition(IfStatement condition){
            instructions.add(condition);
            return this;
        }

        public Block build(){
            return new Block(this);
        }

    }
}


