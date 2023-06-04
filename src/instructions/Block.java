package instructions;

import expressions.Expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block extends Instructions {
    //When we make the new object of the class block, we pass all the
    //variable declarations to the constructor, so we won't be able to
    //add new variables after the end of the declaration series

    //Arrays below are used to prevent shallow copy on the constructor's args
    Map<Character, Integer> variableCopying;
    //Child classes won't be operating on procedures, because one of them is a procedure
    private Map<String, Procedure> procedureCopying;
    public Block(Map<Character, Integer> variables, Map<String, Procedure> procedures){
        this.variables = variables;
        variableCopying = new HashMap<>();
        //preventing shallow copy on variables
        if(variables != null) {
            for (Character c : variables.keySet()) {
                variableCopying.put(c, variables.get(c));
            }
        }

        procedureCopying = new HashMap<>();
        //preventing shallow copy on procedures
        if(procedures != null) {
            for (String s : procedures.keySet()) {
                procedureCopying.put(s, procedures.get(s));
            }
        }
    }

    public void callProcedure(String name, Expressions... params){
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
        addInstruction(p, true);
    }

    protected Block(){
        variables = new HashMap<>();
        variableCopying = new HashMap<>();
    }

    @Override
    public void end(List<Instructions> scopeStack){
        scopeStack.remove(this);
        for(Character c : variableCopying.keySet()){
            variables.put(c, variableCopying.get(c));
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
}
