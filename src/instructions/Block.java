package instructions;

import exceptions.InvalidNumberOfProcedureParametersException;
import exceptions.NonExistingProcedureException;
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

    //Arrays below are used to prevent shallow copy on the constructor's args
    protected Map<Character, Integer> variableCopying;
    //Child classes won't be operating on procedures, because one of them is a procedure
    private Map<String, Procedure> procedureCopying;

    private Block(Builder bb){
        this.variables = bb.variables;
        this.procedures = bb.procedures;
        variableCopying = new HashMap<>();
        procedureCopying = new HashMap<>();
        //preventing shallow copy on variables
        for (Character c : bb.variables.keySet()) {
            variableCopying.put(c, bb.variables.get(c));
        }
        for (String s : bb.procedures.keySet()) {
            procedureCopying.put(s, bb.procedures.get(s));
        }
        this.instructions.addAll(bb.instructions);
    }

    public Instructions callProcedure(String name, Expressions... params){
        Procedure p;
        if(procedureCopying.get(name) == null){
            throw new NonExistingProcedureException();
        }
        else{
            p = new Procedure(procedureCopying.get(name));
            if(p.getParamNames().size() != params.length){
                throw new InvalidNumberOfProcedureParametersException();
            }
            for(Expressions e : params){
                p.addParamValue(e);
            }
        }
        return p;
    }

    protected Block(){
        variables = new HashMap<>();
        variableCopying = new HashMap<>();
        procedures = new HashMap<>();
        procedureCopying = new HashMap<>();
    }

    @Override
    public void end(List<Instructions> scopeStack){
        scopeStack.remove(this);
        for(Character c : variableCopying.keySet()){
            variables.put(c, variableCopying.get(c));
        }
        for(String s : procedureCopying.keySet()){
            procedures.put(s, procedureCopying.get(s));
        }
    }

    @Override
    public void execute(List<Instructions> scopeStack) {
        scopeStack.add(this);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        appendTabs(sb);
        sb.append("begin instructions.Block\n");
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
        sb.append("end instructions.Block\n");

        return sb.toString();
    }


    public static class Builder {
        private final Map<Character, Integer> variables = new HashMap<>();
        private final Map<String, Procedure> procedures  = new HashMap<>();
        private final List<Instructions> instructions = new ArrayList<>();

        public Builder declareVariable(char name, Literal value){
            variables.put(name, value.value());
            return this;
        }
        public Builder declareProcedure(Procedure procedure){
            procedures.put(procedure.getProcedureName(), procedure);
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


