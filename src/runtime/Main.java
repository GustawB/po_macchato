package runtime;

import expressions.*;
import instructions.*;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<Character, Integer> variables = new HashMap<>();
        variables.put('n', 30);
        Block block1 = new Block(variables);
        ForLoop forLoop1 = new ForLoop('k',
                new Substract(new Variable('n'), new Literal(1)));
        Map<Character, Integer> variables2 = new HashMap<>();
        variables2.put('p', 1);
        Block block2 = new Block(variables2);
        block2.addInstruction(new AssignValue('k',
                new Add(new Variable('k'),
                        new Literal(2))), true);
        ForLoop forLoop2 = new ForLoop('i',
                new Substract(new Variable('k'), new Literal(2)));
        forLoop2.addInstruction(new AssignValue('i', new Add(new Variable('i'),
                new Literal(2))), true);
        IfStatement ifStatement1 = new IfStatement(new Modulo(new Variable('k'),
                new Variable('i')), new Literal(0), Operations.EQUAL);
        ifStatement1.addInstruction(new AssignValue('p', new Literal(0)),
                true);
        forLoop2.addInstruction(ifStatement1, true);
        block2.addInstruction(forLoop2, true);
        IfStatement ifStatement2 = new IfStatement(new Variable('p'),
                new Literal(1), Operations.EQUAL);
        ifStatement2.addInstruction(new PrintExpr(new Variable('k')), true);
        block2.addInstruction(ifStatement2, true);
        forLoop1.addInstruction(block2, true);
        block1.addInstruction(forLoop1, true);
        Debugger debug = new Debugger();
        debug.run(block1);
    }
}
