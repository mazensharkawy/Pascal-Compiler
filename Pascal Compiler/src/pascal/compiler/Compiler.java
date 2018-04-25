package pascal.compiler;

/**
 *
 * @author ahmed
 */
public class Compiler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Program program = new Program();
        System.out.println(program.isAssignment("Z := A"));
    }
    
}
