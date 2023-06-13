import expressions.Add;
import expressions.Divide;
import expressions.Literal;
import expressions.Modulo;
import expressions.Multiply;
import expressions.Operations;
import expressions.Substract;
import expressions.Variable;
import instructions.Block;
import instructions.IfStatement;
import instructions.Procedure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import runtime.Debugger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProceduresTests {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    //This test tests the basic procedure functionality by
    //implementing the example given in the instruction
    @Test
    public void BasicUsage(){
        Block program = new Block.Builder()
                .declareVariable('x', Literal.of(57))
                .declareVariable('y', Literal.of(15))
                .declareProcedure(new Procedure.declarationBuilder("out", 'a')
                        .print(Variable.of('a'))
                        .build())
                .assign('x', Substract.of(Variable.of('x'), Variable.of('y')))
                .invoke(new Procedure.invokeBuilder("out", Variable.of('x'))
                        .build())
                .invoke(new Procedure.invokeBuilder("out", Literal.of(125))
                        .build())
                .build();
        Debugger debugger = new Debugger();
        debugger.run(program, 'n');

        assertEquals("42125", outputStreamCaptor
                .toString().trim().replace("\n", "")
                .replace("\r", ""));
    }

    @Test
    public void recursion(){
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

        assertEquals("0123456", outputStreamCaptor
                .toString().trim().replace("\n", "")
                .replace("\r", ""));
    }

    //Code based on the example provided in the design specification.
    @Test
    public void designSpecCode(){
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
        debugger.run(program, 'n');

        assertEquals("200200110", outputStreamCaptor
                .toString().trim().replace("\n", "")
                .replace("\r", ""));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }
}
