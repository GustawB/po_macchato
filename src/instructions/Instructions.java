package instructions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Instructions {
    //list of the nested instructions
    protected List<Instructions> instructions;
    //list if the variables for instructions that need it
    protected Map<Character, Integer> variables;
    protected Map<String, Procedure> procedures;

    protected Instructions(){
        variables = new HashMap<>();
        instructions = new ArrayList<>();
        procedures = new HashMap<>();
    }
    //variable used for printing the correct number of tabs when printing
    //the next instruction with its nested instructions
    protected int numberOfTabs = 0;
    //used to determine whether the debugger stopped stepping
    //in the middle of the execution of the instruction
    public boolean doesRequireMoreSteps(){return false;}
    //function responsible for the execution of the instruction
    public abstract void execute(List<Instructions> scopeStack);

    //function called at the end of the instruction execution
    public void end(List<Instructions> scopeStack){}

    //returns the map containing variables belonging to the instruction
    public Map<Character, Integer> getVariables() {
        return variables;
    }

    //returns the map containing procedures belonging to the instruction
    public Map<String, Procedure> getProcedures(){return procedures;}

    //function used for printing tabs when the debugger
    //prints nested instructions
    protected void appendTabs(StringBuilder sb){
        for(int i = 0; i < numberOfTabs; ++i){
            sb.append("    ");
        }
    }

    public List<Instructions> getInstructions(){
        return  instructions;
    }

    public void incrementNumberOfTabs(){
        ++numberOfTabs;
    }

    public void decrementNumberOfTabs(){
        --numberOfTabs;
    }

    //function used to add a nested instruction. Adding mode
    //isn't important for all instructions except ifStatement.
    //In ifStatement, addingMode is true if we add instructions for
    //the if part, and false for else part
    public void addInstruction(Instructions instruction, boolean addingMode){
        instructions.add(instruction);
    }

    //function that prints all the variables in the current scope
    public void printVariablesInScope(List<Instructions> scopeStack){
        for(Instructions p : scopeStack){
            for(Map.Entry<Character, Integer> entry : p.getVariables().entrySet()){
                System.out.print(entry.getKey() + ": " + entry.getValue() + "; ");
            }
        }
        System.out.println("");
    }
    public abstract String toString();
}
