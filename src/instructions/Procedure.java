package instructions;

import expressions.Expressions;
import expressions.Literal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Procedure extends Block{
    private String procedureName;
    private List<Character> paramNames;
    private List<Expressions> paramExpressions = new ArrayList<>();
    //Constructor used for creating a new procedure object so that
    //a new procedure can be declared

    private Procedure(declarationBuilder db){
        super(); //for my visibility
        this.procedureName = db.name;
        paramNames = new ArrayList<>();
        paramNames.addAll(db.variableNames);
        instructions.addAll(db.instructions);
    }

    private Procedure(invokeBuilder ib){
        super();
        this.procedureName = ib.name;
        this.paramExpressions.addAll(ib.parameters);
    }

    private Procedure(Procedure toClone){
        super();
        this.paramExpressions.addAll(toClone.paramExpressions);
        this.procedureName = toClone.getProcedureName();
        for(Instructions ins : toClone.instructions){
            this.instructions.add(ins.clone());
        }
    }

    @Override
    public Procedure clone(){
        return new Procedure(this);
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
        for(int i = scopeStack.size() - 1; i >= 0 ; --i) {
            if (scopeStack.get(i).getProcedures().containsKey(procedureName)) {
                Procedure p = scopeStack.get(i).getProcedures().get(procedureName);
                if (this.paramExpressions.size() != p.getParamNames().size()) {
                    System.out.println("Error in: Procedure named "
                            + procedureName + "; ");
                    printVariablesInScope(scopeStack);
                    return;
                }
                try {
                    for (int y = 0; y < this.paramExpressions.size(); ++y) {
                        char key = p.paramNames.get(y);
                        int value = paramExpressions.get(y).value(scopeStack);
                        this.variables.put(key, value);
                    }
                }
                catch (Exception e){
                    System.out.println("Error in: Procedure named "
                            + procedureName + "; ");
                    printVariablesInScope(scopeStack);
                    return;
                }
                for(Instructions ins : p.instructions){
                    this.instructions.add(ins.clone());
                }
                break;
            }
        }
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

    public static class declarationBuilder {
        private final List<Instructions> instructions = new ArrayList<>();
        private final List<Character> variableNames = new ArrayList<>();
        private String name;

        public declarationBuilder (String name, char... variableNames){
            this.name = name;
            for(char c : variableNames){
                this.variableNames.add(c);
            }
        }

        public declarationBuilder assign(char assignTo, Expressions valueToAssign){
            instructions.add(new AssignValue(assignTo, valueToAssign));
            return this;
        }

        public declarationBuilder print(Expressions valueToPrint){
            instructions.add(new PrintExpr(valueToPrint));
            return this;
        }

        public declarationBuilder newLoop(ForLoop loop){
            instructions.add(loop);
            return this;
        }

        public declarationBuilder condition(IfStatement condition){
            instructions.add(condition);
            return this;
        }

        public Procedure build(){
            return new Procedure(this);
        }
    }

    public static class invokeBuilder {
        private final List<Expressions> parameters = new ArrayList<>();
        private String name;

        public invokeBuilder (String name, Expressions... params){
            this.name = name;
            for(Expressions e : params){
                this.parameters.add(e);
            }
        }

        public Procedure build(){
            return new Procedure(this);
        }
    }
}
