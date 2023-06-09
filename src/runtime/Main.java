package runtime;

import expressions.*;
import instructions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Block program = new Block.Builder()
                .declareProcedure(new Procedure.declarationBuilder("zegz", 'x')
                        .condition(new IfStatement.Builder(Variable.of('x'), Literal.of(6), Operations.SMALLER_EQUAL)
                                .print(Variable.of('x'))
                                .assign('x', Add.of(Variable.of('x'), Literal.of(1)))
                                .invoke(new Procedure.invokeBuilder("zegz", Variable.of('x'))
                                        .build())
                                .build())
                        .build())
                .invoke(new Procedure.invokeBuilder("zegz", Literal.of(0))
                        .build())
                .build();

        Debugger debugger = new Debugger();
        debugger.run(program, 'n');
    }
}
