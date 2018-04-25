package pascal.compiler;

/**
 *
 * @author ahmed
 */
public class Compiler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SyntaxError {
        LexicalAnalyzer program = new LexicalAnalyzer();
        //System.out.println(program.isAssignment("Z := A+B+C+Y"));
        program.read("PascalCode.txt");
    }
    
}
