package runtime;

import exceptions.NonExistingVariableException;
import instructions.Block;
import instructions.Instructions;
import instructions.PrintExpr;
import instructions.Procedure;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Debugger {
    //list responsible for keeping the information about the current scopes
    private List<Instructions> scopeStack = new ArrayList<>();
    //variable which stores the information about
    //the run mode (debug or non-debug)
    private char mode;
    //if mode is set to debug, this variable stores
    // the information about the current debug command
    private String debugMode = "";
    //number of steps for the s(tep) instruction
    private int numberOfSteps;
    //keeps the information how many steps we have already performed
    private int stepsIter;

    //Filepath for testing dump functionality.
    private String testFilePath = "";

    //Used by the overloaded run() in tests fo the dump functionality.
    private boolean bShouldDump = false;

    //Function responsible for determining whether we run
    // with or without the debug mode
    private void chooseMode(){
        String input = "";
        //basic input checking (we accept only y(es) or n(o))
        while(input.equals("") || (!input.equals("y") && !input.equals("n"))) {
            System.out.println("Do you want to run with debug mode? y/n");
            Scanner in = new Scanner(System.in);
            input = in.nextLine();
            String[] array = input.split("[ ]+");
            for (int i = 0; i < array.length; ++i) {
                array[i] = array[i].trim();
            }
            System.out.println(input);
        }
        mode = input.charAt(0);
    }

    //Function resposnible for reading debug command from user
    private String[] readDebugCommand(){
        //if we don't want to run with debug, this function sets
        //debugMode to c(ontinue), which is equivalent to run without debug
        if(mode == 'n'){return new String[]{"c"};}
        else {
            System.out.println("Input next debug command: c/s/m/d/e");
            Scanner in = new Scanner(System.in);
            String debugCommand = in.nextLine();
            String[] array = debugCommand.split("[ ]+");
            for (int i = 0; i < array.length; ++i) {
                array[i] = array[i].trim();
            }
            return array;
        }
    }

    //Used to get the value of the given variable.
    //If it doesn't exist, this function
    // throws the exceptions.NonExistingVariableException
    public static int getVariableValue(char name,
                                       List<Instructions> scopeStack){
        for(int i = scopeStack.size() - 1; i >= 0 ; --i){
            if(scopeStack.get(i).getVariables().containsKey(name)){
                return scopeStack.get(i).getVariables().get(name);}
        }
        throw new NonExistingVariableException();
    }

    //Used to set the value of the given variable.
    //If it doesn't exist, this function
    // throws the exceptions.NonExistingVariableException
    public static void setVariableValue(char name,
                                        List<Instructions> scopeStack,
                                        int newValue){
        for(int i = scopeStack.size() - 1; i >= 0 ; --i) {
            if (scopeStack.get(i).getVariables().containsKey(name)) {
                scopeStack.get(i).getVariables().replace(name, newValue);
                return;
            }
        }
        throw new NonExistingVariableException();
    }

    //Called when the debugMode is equal to d(isplay).
    //Thsi function displays values of the variables in the given number
    //of scopes
    protected void display(int numberOfScopesToDisplay){
        numberOfScopesToDisplay = scopeStack.size() - numberOfScopesToDisplay - 1;
        //numberOfScopesToDisplay is higher than
        //the actual number of scopes
        if(numberOfScopesToDisplay < 0){
            System.out.println("There aren't as many " +
                    "scopes as you wish to display");
        }
        else{//we have enough scopes to display
            for(int i = scopeStack.size() - 1;
                i >= scopeStack.size() - 1 - numberOfScopesToDisplay; --i){
                Instructions instructions = scopeStack.get(i);
                for(Map.Entry<Character, Integer> m :
                        instructions.getVariables().entrySet()){
                    System.out.print(m.getKey() + ": " + m.getValue() + "; ");
                }
                System.out.println("");
            }
            System.out.println("");
        }
    }

    //Function used to write a current valuation into the specified file.
    protected void dump(String filePath){
        Path file = null;
        try {
            file = Paths.get(filePath);
        }
        catch(Exception e){
            System.out.println("Failed to find the specified path.");
        }
        List<String> textToWrite = new ArrayList<>();
        if(scopeStack.size() > 0) {
            for (int i = scopeStack.size() - 1;
                 i >= 0; --i) {
                Instructions instructions = scopeStack.get(i);
                for (Map.Entry<Character, Integer> m :
                        instructions.getVariables().entrySet()) {
                    textToWrite.add(m.getKey() + ": " + m.getValue() + "; ");
                }
                for(Map.Entry<String, Procedure> m :
                        instructions.getProcedures().entrySet()){
                    String line = m.getKey() + "(";
                    Procedure p = m.getValue();
                    for(int y = 0; y < p.getParamNames().size(); ++y){
                        List<Character> params = p.getParamNames();
                        line += p.getParamNames().get(y);
                        if(y != p.getParamNames().size() - 1){
                            line += ", ";
                        }
                    }
                    line += ");";
                    textToWrite.add(line);
                }
            }
        }
        try{
            Files.write(file, textToWrite, StandardCharsets.UTF_8);
        }
        catch (Exception e){
            System.out.println("Failed to write to the specified file.");
        }
    }

    //Main logic of the debugger. THis function iterates through instructions,
    //and if they have nested instructions, it recursively iterates
    //through them too
    protected void iterateThroughInstructions(List<Instructions> instructions){
        for(Instructions ins : instructions) {
            do {
                //if we haven't set debugMode yet OR we stopped stepping
                if ((debugMode.equals("s") && stepsIter == numberOfSteps) ||
                        debugMode.isEmpty()) {
                    do {
                        if(bShouldDump){
                            String filePath = "";
                            boolean bIsValidCommand = true;
                            try {
                                filePath = testFilePath;
                            }
                            catch (Exception e){
                                bIsValidCommand = false;
                            }
                            if(bIsValidCommand) {
                                dump(filePath);
                            }
                            debugMode = "e";
                            break;
                        }
                        //if we stopped stepping, we display the
                        //next instruction
                        if (debugMode.equals("s")) {
                            System.out.println(ins);
                        }
                        //reading the new debug command
                        String[] command = readDebugCommand();
                        debugMode = command[0];
                        if (debugMode.equals("s")) {
                            stepsIter = 0;
                            //basic input checking
                            try {
                                numberOfSteps = Integer.parseInt(command[1]);
                            }
                            catch (Exception e){
                                //this way we won't exit the loop if the user
                                // passed wrong param and nothing will be
                                // displayed on the next iteration of the loop
                                debugMode = "d";
                            }
                        } else if (debugMode.equals("d")) {
                            int numberOfLevels = 0;
                            boolean bIsNumberValid = true;
                            //checking whether the user passed a number or not
                            try{
                                numberOfLevels = Integer.parseInt(command[1]);
                            }
                            catch(Exception e){
                                bIsNumberValid = false;
                            }

                            //if user passed a valid number we display
                            //the current valuation
                            if(bIsNumberValid){
                                display(numberOfLevels);
                            }
                        }
                        else if(debugMode.equals("m")){
                            String filePath = "";
                            boolean bIsValidCommand = true;
                            try {
                                filePath = command[1];
                            }
                            catch (Exception e){
                                bIsValidCommand = false;
                            }
                            if(bIsValidCommand) {
                                dump(filePath);
                            }
                        }
                        else{
                            //basic input checking
                            while(!debugMode.equals("c") &&
                                    !debugMode.equals("s") &&
                                    !debugMode.equals("m") &&
                                    !debugMode.equals("d") &&
                                    !debugMode.equals("e")) {
                                command = readDebugCommand();
                                debugMode = command[0];
                                System.out.println(debugMode);
                            }
                        }
                    } while ((debugMode.equals("s") && numberOfSteps == 0) ||
                            debugMode.equals("d") || debugMode.equals("m"));
                }
                //if debugger didn't step enough instructions OR
                //the user wants to continue (continuing is equivalent to
                // stepping through the entire program at once)
                if ((debugMode.equals("s") && stepsIter != numberOfSteps) ||
                        debugMode.equals("c")) {
                    ++stepsIter;
                    try {
                        ins.execute(scopeStack);
                    }
                    catch (Exception e){
                        //if executing the instruction caused an error,
                        //we want to exit the loop, so we set debugMode
                        // to e(xit)
                        debugMode = "e";
                        return;
                    }
                    //if the instruction has its own nested instructions,
                    //debugger will iterate through them too
                    iterateThroughInstructions(ins.getInstructions());
                } else if (debugMode.equals("e")) {//exiting the program
                    return;
                }
            } while (ins.doesRequireMoreSteps());
            ins.end(scopeStack);
        }
    }

    //Function called in main in order to run the program
    public void run (Block Program) {
        chooseMode();
        numberOfSteps = 0;
        stepsIter = 0;
        List<Instructions> instructions = new ArrayList<>();
        instructions.add(Program);
        iterateThroughInstructions(instructions);
        if(mode == 'y' && debugMode.equals("s") && stepsIter < numberOfSteps)
            System.out.println("Program ended before reaching " +
                    numberOfSteps + " steps");
        else if(mode == 'y' && debugMode.equals("c")){
            System.out.println("The program has ended");
        }
    }

    //run() overload used in tests to omit unnecessary prints
    public void run (Block Program, char runMode) {
        mode = runMode;
        numberOfSteps = 0;
        stepsIter = 0;
        List<Instructions> instructions = new ArrayList<>();
        instructions.add(Program);
        iterateThroughInstructions(instructions);
    }

    //run() overload used in testing dumper functionality.
    public void run(Block Program, String filePath, int stepsBeforeDump){
        mode = 'y';
        debugMode = "s";
        numberOfSteps = stepsBeforeDump;
        stepsIter = 0;
        testFilePath = filePath;
        bShouldDump = true;
        List<Instructions> instructions = new ArrayList<>();
        instructions.add(Program);
        iterateThroughInstructions(instructions);
    }
}