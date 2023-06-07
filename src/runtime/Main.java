package runtime;

import expressions.*;
import instructions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Block program = new Block.Builder()
                .declareVariable('x', Literal.of(69))
                .declareVariable('y', Literal.of(420))
                .print(Variable.of('x'))
                .print(Variable.of('y'))
                .declareVariable('z', Literal.of(2137))
                .print(Variable.of('z'))
                .build();
        Debugger debugger = new Debugger();
        debugger.run(program);
    }
}
