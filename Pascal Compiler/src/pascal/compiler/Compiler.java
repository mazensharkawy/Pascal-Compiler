package pascal.compiler;

import java.util.Scanner;

/**
 *
 * @author ahmed
 */
public class Compiler {

    /**
     * @throws pascal.compiler.SyntaxError
     */
    public static String fileName;

    public static void main(String[] args) throws SyntaxError {
        LexicalAnalyzer program = new LexicalAnalyzer();
        //Scanner input = new Scanner(System.in);
        //fileName = input.nextLine();
        fileName = "PascalCode3.txt";
        program.read();
    }

}
