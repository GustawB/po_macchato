package runtime;

import expressions.*;
import instructions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<Character, Integer> variables = new HashMap<>();
        Map<String, Procedure> procedures = new HashMap<>();
        Procedure a = new Procedure("zegz", Arrays.asList('a', 'b'));
        procedures.put(a.getProcedureName(), a);
        variables.put('n', 30);
        a.addInstruction(new PrintExpr(new Variable('a')), true);
        a.addInstruction(new PrintExpr(new Variable('b')), true);
        Block block1 = new Block(variables, procedures);
        block1.callProcedure("zegz", new Literal(69), new Literal(2137));
        block1.callProcedure("zegz", new Literal(96), new Literal(420));
        Debugger debugger = new Debugger();
        debugger.run(block1);
    }
}
