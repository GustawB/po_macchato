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
}
