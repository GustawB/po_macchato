import expressions.Literal;
import expressions.Variable;
import expressions.Add;
import expressions.Divide;
import expressions.Literal;
import expressions.Modulo;
import expressions.Multiply;
import expressions.Substract;
import expressions.Variable;
import expressions.Operations;
import instructions.Block;
import instructions.ForLoop;
import instructions.IfStatement;
import instructions.PrintExpr;
import org.junit.jupiter.api.*;
import runtime.Debugger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildersTests {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }


    //Tests basic functionality of builders in Block
    @Test
    public void TestBlockPrints(){
        Block program = new Block.Builder()
                .declareVariable('x', Literal.of(69))
                .declareVariable('y', Literal.of(420))
                .print(Variable.of('x'))
                .print(Variable.of('y'))
                .declareVariable('z', Literal.of(2137))
                .print(Variable.of('z'))
                .build();
        Debugger debugger = new Debugger();
        debugger.run(program, 'n');

        //This type of test has problems with line separators, so I ignore them.
        assertEquals("694202137", outputStreamCaptor
                .toString().trim().replace("\n", "")
                .replace("\r", ""));
    }

    //Tests builder functionality on Loops
    @Test
    public void TestLoopPrints(){
        Block program = new Block.Builder()
                .newLoop(new ForLoop.Builder('x', Literal.of(5))
                        .print(Variable.of('x'))
                        .assign('x', Add.of(Variable.of('x'), Literal.of(2)))
                        .print(Variable.of('x'))
                        .build())
                .build();
        Debugger debugger = new Debugger();
        debugger.run(program, 'n');

        assertEquals("0213243546", outputStreamCaptor
                .toString().trim().replace("\n", "")
                .replace("\r", ""));
    }

    //Tests builder functionality on IfStatements
    @Test
    public void TestIfStatements(){
        Block program = new Block.Builder()
                .newLoop(new ForLoop.Builder('x', Literal.of(10))
                        .condition(new IfStatement
                                .Builder(Modulo.of(Variable.of('x'),
                                Literal.of(3)),
                                Literal.of(0),
                                Operations.EQUAL)
                                .print(Variable.of('x'))
                                .switchToElseBranch()
                                .print(Literal.of(69))
                                .build())
                        .build())
                .build();
        Debugger debugger = new Debugger();
        debugger.run(program, 'n');

        assertEquals("0696936969669699", outputStreamCaptor
                .toString().trim().replace("\n", "")
                .replace("\r", ""));
    }

    //One big test to test builder functionality in the remaining Expressions classes.
    @Test
    public void ExpressionsTest(){
        Block program = new Block.Builder()
                .declareVariable('z', Literal.of(2137))
                .assign('z', Add.of(Variable.of('z'), Literal.of(420)))
                .assign('z', Substract.of(Variable.of('z'), Literal.of(69)))
                .assign('z', Modulo.of(Variable.of('z'), Literal.of(45)))
                .assign('z', Multiply.of(Variable.of('z'), Literal.of(29)))
                .print(Variable.of('z'))
                .build();
        Debugger debugger = new Debugger();
        debugger.run(program, 'n');

        assertEquals("377", outputStreamCaptor
                .toString().trim().replace("\n", "")
                .replace("\r", ""));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }
}
