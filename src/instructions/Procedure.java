package instructions;

import exceptions.InvalidNumberOfProcedureParametersException;
import exceptions.NonExistentProcedureException;
import exceptions.NonExistingProcedureException;
import exceptions.NonExistingVariableException;
import expressions.Expressions;
import expressions.Literal;

import java.util.*;

public class Procedure extends Block{
    private String procedureName;
    private List<Character> paramNames;
    private List<Expressions> paramExpressions = new ArrayList<>();
    //Constructor used for creating a new procedure object so that
    //a new procedure can be declared

    //Constructor used by the declarationBuilder.
    private Procedure(declarationBuilder db){
        super(); //for my visibility
        this.bWasThereProcedureRedeclaration = db.bWasThereProcedureRedeclaration;
        this.bWasThereVariableRedeclaration = db.bWasThereVariableRedeclaration;
        this.bWasVariableRedeclarationFirst = db.bWasVariableRedeclarationFirst;
        this.procedureName = db.name;
        variables.putAll(db.variables);
        paramNames = new ArrayList<>();
        paramNames.addAll(db.variableNames);
        instructions.addAll(db.instructions);
        procedures.putAll(db.procedures);
    }

    //Constructor used by the invokeBuilder.
    private Procedure(invokeBuilder ib){
        super();
        this.procedureName = ib.name;
        this.paramExpressions.addAll(ib.parameters);
    }

    //Constructor used by the clone() function.
    private Procedure(Procedure toClone){
        super();
        variables.putAll(toClone.variables);
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
        //Handling of the NonExisting and ProcedureRedeclaration exceptions.
        if(bWasThereVariableRedeclaration){
            if(bWasThereProcedureRedeclaration &&
                    !bWasVariableRedeclarationFirst) {
                System.out.println("Error in: Procedure named "
                        + procedureName + "; ");
                printVariablesInScope(scopeStack);
                printVariablesInScope(scopeStack);
                throw new NonExistingProcedureException();
            }
            else{
                System.out.println("Error in: Procedure named "
                        + procedureName + "; ");
                printVariablesInScope(scopeStack);
                throw new NonExistingVariableException();
            }
        }
        else if(bWasThereProcedureRedeclaration){
            System.out.println("Error in: Procedure named "
                    + procedureName + "; ");
            printVariablesInScope(scopeStack);
            throw new NonExistingProcedureException();
        }

        boolean bWasProcedureDeclared = false;
        //Loop responsible for checking whether the procedure with the given
        //name was declared, and if so, "creating" it based on the declaration,
        //and then adding it to the scope.
        for(int i = scopeStack.size() - 1; i >= 0 ; --i) {
            if (scopeStack.get(i).getProcedures().containsKey(procedureName)) {
                Procedure p = scopeStack.get(i).
                        getProcedures().get(procedureName);
                //Invalid number of params.
                if (this.paramExpressions.size() != p.getParamNames().size()) {
                    System.out.println("Error in: Procedure named "
                            + procedureName + "; ");
                    printVariablesInScope(scopeStack);
                    throw new InvalidNumberOfProcedureParametersException();
                }
                //Make Map of variables based on the names of the parameters
                //and its values.
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
                    throw e;
                }
                //Copy instructions from the declaration. Clone function
                //prevents shallow copy here.
                for(Instructions ins : p.instructions){
                    this.instructions.add(ins.clone());
                }
                bWasProcedureDeclared = true;
                break;
            }
        }
        //Procedure with the given name wasn't declared before.
        if(!bWasProcedureDeclared){
            System.out.println("Error in: Procedure named "
                    + procedureName + "; ");
            printVariablesInScope(scopeStack);
            throw new NonExistentProcedureException();
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

    //Two builders for the procedure class. declarationBuilder is used to
    //declare a new procedure, by defining its name, parameters, instructions
    //and varables inside its body. Then, the invokeBuilder is used to
    //call the declared procedure, by creating an object of the class
    //Procedure with the name, and a list of parameters. Then,
    //this newly added procedure is added to the list of instructions,
    //and when its executed, it's missing fields (e.g. variable's map)
    //are filled based on its declaration (made with declarationBuilder).
    public static class declarationBuilder {
        private final List<Instructions> instructions = new ArrayList<>();
        private final List<Character> variableNames = new ArrayList<>();
        private final Map<Character, Integer> variables = new HashMap<>();
        private final Map<String, Procedure> procedures  = new HashMap<>();
        boolean bWasThereVariableRedeclaration = false;
        boolean bWasThereProcedureRedeclaration = false;
        boolean bWasVariableRedeclarationFirst = false;

        private String name;

        public declarationBuilder (String name, char... variableNames){
            this.name = name;
            for(char c : variableNames){
                this.variableNames.add(c);
            }
        }

        public declarationBuilder declareVariable(char name, Literal value){
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
        public declarationBuilder declareProcedure(Procedure procedure){
            if(procedures.containsKey(procedure.getProcedureName())){
                bWasThereProcedureRedeclaration = true;
            }
            else {
                procedures.put(procedure.getProcedureName(), procedure);
            }
            return this;
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
            this.parameters.addAll(Arrays.asList(params));
        }

        public Procedure build(){
            return new Procedure(this);
        }
    }
}
