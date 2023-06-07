package runtime;

import expressions.*;
import instructions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Block program = new Block.Builder()
                .declareVariable('x', new Literal(69))
                .declareVariable('y', new Literal(420))
                .print(new Variable('x'))
                .print(new Variable('y'))
                .declareVariable('z', new Literal(90))
                .print(new Variable('z'))
                .build();
        Debugger debugger = new Debugger();
        debugger.run(program);
    }
}
