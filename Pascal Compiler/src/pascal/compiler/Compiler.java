package pascal.compiler;

/**
 *
 * @author ahmed
 */
public class Compiler {

    /**
     * @param args the command line arguments
     * @throws pascal.compiler.SyntaxError
     */
    public static void main(String[] args) throws SyntaxError {
        LexicalAnalyzer program = new LexicalAnalyzer();
        program.read("PascalCode2.txt");
    }
    
}
