package pascal.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author ahmed
 */
public class LexicalAnalyzer {

    private ArrayList<String> tokens = new ArrayList<>();
    private HashSet<String> variables = new HashSet<>();

    private Program program = new Program();
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void read(String filePath) {
        FileReader fileReader = null;
        try {
            File file = new File(filePath);
            fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String newLine = null;
            newLine = reader.readLine();
            while ((newLine = reader.readLine()) != null) {

            }
            reader.close();
        } catch (FileNotFoundException ex) {

        } catch (IOException ex) {

        } finally {
            try {
                fileReader.close();
            } catch (IOException ex) {

            }
        }
    }

}
