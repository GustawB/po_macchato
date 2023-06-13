import expressions.Literal;
import expressions.Variable;
import instructions.Block;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import runtime.Debugger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.*;

public class DumperTests {
    private String filePath = "ugabuga.txt";

    @Test
    public void OneScopeDumper(){
        Block program = new Block.Builder()
                .declareVariable('a', Literal.of(69))
                .declareVariable('b', Literal.of(420))
                .print(Variable.of('b'))
                .build();

        Debugger debugger = new Debugger();
        debugger.run(program, filePath, 1);

        File test = new File(filePath);
        assertTrue(test.exists() && !test.isDirectory());

        String[] validResult = {"a: 69;", "b: 420;"};

        BufferedReader readFromFile;
        try {
            readFromFile = new BufferedReader(new FileReader(filePath));
        }
        catch (Exception e){
            System.out.println("FileReader in OneScopeDumper() test failed");
            return;
        }
        String line;
        for (String s : validResult) {
            try {
                line = readFromFile.readLine();
            }
            catch(Exception e){
                System.out.println("readLine() in OneScopeDumper() loop failed");
                return;
            }
            assertEquals(line.trim().replace("\n", "")
                    .replace("\r", ""), s);
        }

        try {
            line = readFromFile.readLine();
        }
        catch(Exception e){
            System.out.println("readLine() in OneScopeDumper() after loop failed");
            return;
        }
        assertNull(line);
        try {
            readFromFile.close();
        }
        catch(Exception e){
            System.out.println("Failed to close the buffer");
            return;
        }
        assertTrue(test.delete());
    }

    @Test
    public void TwoScopes(){
        Block program = new Block.Builder()
                .declareVariable('a', Literal.of(69))
                .declareVariable('b', Literal.of(420))
                .newBlock(new Block.Builder()
                        .declareVariable('c', Literal.of(2137))
                        .declareVariable('d', Literal.of(96))
                        .print(Variable.of('b'))
                        .build())
                .build();

        Debugger debugger = new Debugger();
        debugger.run(program, filePath, 2);

        File test = new File(filePath);
        assertTrue(test.exists() && !test.isDirectory());

        String[] validResult = {"c: 2137;", "d: 96;", "a: 69;", "b: 420;"};

        BufferedReader readFromFile;
        try {
            readFromFile = new BufferedReader(new FileReader(filePath));
        }
        catch (Exception e){
            System.out.println("FileReader in TwoScopeDumper() test failed");
            return;
        }
        String line;
        for (String s : validResult) {
            try {
                line = readFromFile.readLine();
            }
            catch(Exception e){
                System.out.println("readLine() in TwoScopeDumper() loop failed");
                return;
            }
            assertEquals(line.trim().replace("\n", "")
                    .replace("\r", ""), s);
        }

        try {
            line = readFromFile.readLine();
        }
        catch(Exception e){
            System.out.println("readLine() in TwoScopeDumper() after loop failed");
            return;
        }
        assertNull(line);
        try {
            readFromFile.close();
        }
        catch(Exception e){
            System.out.println("Failed to close the buffer");
            return;
        }
        assertTrue(test.delete());
    }
}
