package runtime;

import expressions.*;
import instructions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Block program = new Block.Builder()
                .declareVariable('x', Literal.of(101))
                .declareVariable('y', Literal.of(1))
                .declareProcedure(new Procedure.declarationBuilder("out", 'a')
                        .print(Add.of(Variable.of('a'), Variable.of('x')))
                        .build())
                .assign('x', Substract.of(Variable.of('x'), Variable.of('y')))
                .invoke(new Procedure.invokeBuilder("out", Variable.of('x'))
                        .build())
                .invoke(new Procedure.invokeBuilder("out", Literal.of(100))
                        .build())
                .newBlock(new Block.Builder()
                        .declareVariable('x', Literal.of(10))
                        .invoke(new Procedure.invokeBuilder("out", Literal.of(100))
                                .build())
                        .build())
                .build();

        Debugger debugger = new Debugger();
        debugger.run(program);
    }
}
