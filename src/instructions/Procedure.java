package instructions;

import expressions.Expressions;

import java.util.ArrayList;
import java.util.List;

public class Procedure extends Block{
    private String procedureName;
    private List<Character> paramNames;
    private List<Expressions> paramExpressions;
    //Constructor used for creating a new procedure object so that
    //a new procedure can be declared
    public Procedure(String procedureName, List<Character> paramNames){
        super();
        this.procedureName = procedureName;
        this.paramNames = new ArrayList<>();
        paramExpressions = new ArrayList<>();
        if(paramNames != null){
            for(Character c : paramNames){
                this.paramNames.add(c);
            }
        }
    }

    //Constructor used for using the same function declaration multiple times
    public Procedure(Procedure procedure){
        this.procedureName = procedure.getProcedureName();
        paramNames = new ArrayList<>(procedure.getParamNames());
        paramExpressions = new ArrayList<>();
        instructions = new ArrayList<>(procedure.getInstructions());
    }

    public List<Character> getParamNames(){
        return paramNames;
    }

    public String getProcedureName(){
        return procedureName;
    }

    //Function responsible for adding a new expression to the procedure,
    //that will later resolve to the int value passed as a procedure argument
    public void addParamValue(Expressions expression){
        paramExpressions.add(expression);
    }

    @Override
    public void execute(List<Instructions> scopeStack) {
        for(int i = 0; i < paramNames.size(); ++i) {
            variableCopying.put(paramNames.get(i), paramExpressions.get(i).value(scopeStack));
        }
        variables = variableCopying;
        scopeStack.add(this);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        appendTabs(sb);
        sb.append("begin " + procedureName + "(");
        for(int i = 0; i < paramNames.size(); ++i){
            sb.append(paramNames.get(i));
            if(i != paramNames.size() - 1){
                sb.append(", ");
            }
        }
        sb.append(")\n");
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
        sb.append("end " + procedureName + "(");
        for(int i = 0; i < paramNames.size(); ++i){
            sb.append(paramNames.get(i));
            if(i != paramNames.size() - 1){
                sb.append(", ");
            }
        }
        sb.append(")\n");

        return sb.toString();
    }
}
