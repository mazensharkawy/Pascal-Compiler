package pascal.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ahmed
 */
public class LexicalAnalyzer {

    private ArrayList<HashMap<TokenName, String>> tokens = new ArrayList<>();
    //private HashSet<String> variablesSet = new HashSet<>();
    public String programName;
    public String[] variables;

    public String[] writeMethodArguments;

    public enum TokenName {
        PROGRAM_NAME,
        IDENTIFIER,
        OPERAND,
        DESTINATION,
        ARGUMENT,
        OPENING_BRACKET,
        CLOSING_BRACKET,
        KEYWORD,
        OPERATOR;
    }

    public void read(String filePath) throws SyntaxError {
        FileReader fileReader = null;
        try {
            File file = new File(filePath);
            fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String newLine = null;
            if (!isStartOfProgram(reader.readLine())) {
                throw new SyntaxError("Start of program is not correct");
            }
            if (isVar(reader.readLine())) {
                setVariables(reader.readLine());
            } else {
                System.out.println("There are no variables in the program");
            }
            if (!isBegin(reader.readLine())) {
                throw new SyntaxError("Begin statement missing");
            }

            while ((newLine = reader.readLine()) != null && !"END".equals(newLine.trim())) {
                System.out.println(newLine);
                if (isReadMethod(newLine)) {
                    System.out.println("READ");

                } else if (isWriteMethod(newLine)) {
                     System.out.println("WRITE");

                } else if (isAssignment(newLine)) {
                     System.out.println("ASSIGNMENT");

                } else {
                    throw new SyntaxError("Undefined Instruction");
                }
            }
            
            for(HashMap<TokenName, String> map: tokens){
                System.out.print(map.keySet());
                System.out.println(map.values());
            }
            System.out.println(tokens.toString());
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

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
        addToken(TokenName.PROGRAM_NAME, programName);
    }

    public boolean isStartOfProgram(String line) {
        boolean flag;
        String pattern = "(?i)\\s*PROGRAM\\s+(\\w+)\\s*";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();
        if (flag) {
            setProgramName(matcher.group(1));
        }

        return flag;
    }

    public void setVariables(String line) {
        variables = line.trim().split("[,\\s+]");
        for (String token : variables) {
            addToken(TokenName.IDENTIFIER, token);
        }
    }

    public String[] getVariables() {
        return variables;
    }

    public void tokenizeAssignment(String line) {
        ArrayList<String> tokens = new ArrayList<>();
        String token = "";
        line = line.replaceAll(" ", "");
        char[] charArray = line.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == '+' || charArray[i] == '*') {
                addToken(TokenName.OPERAND, token);
                addToken(TokenName.OPERATOR, String.valueOf(charArray[i]));
                token = "";
            } else {
                token += charArray[i];
            }
        }
    }

    public boolean isReadMethod(String line) {
        boolean flag;
        String pattern = "(?i)\\s*READ\\s*\\((\\s*[a-zA-Z_]\\w*\\s*(,\\s*[a-zA-Z_]\\w*\\s*)*)\\)\\s*";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();
        addToken(TokenName.KEYWORD, "READ");
        addToken(TokenName.OPENING_BRACKET, "(");
        if (flag) {
            readArgumentsAndAddTokens(matcher.group(1));
        }

        addToken(TokenName.CLOSING_BRACKET, ")");
        return flag;
    }

    private void readArgumentsAndAddTokens(String line) {
        
        String[] methodArguments = line.replaceAll(" ", "").split(",");
        for (String item : methodArguments) {
            System.out.println("argument: "+item);
            addToken(TokenName.ARGUMENT, item);
        }
    }

    private void addToken(TokenName key, String token) {
        HashMap<TokenName, String> map = new HashMap<>();
        map.put(key, token);
        tokens.add(map);
    }

    public boolean isAssignment(String line) {
        String pattern1 = "(?i)\\s*([a-zA-Z]\\w*)\\s*:=\\s*(([a-zA-Z]\\w*|\\d+)\\s*([+*]\\s*([a-zA-Z]+\\w*|\\d+)\\s*)+)";
        String pattern2 = "(?i)\\s*([a-zA-Z]\\w*)\\s*:=\\s*(([a-zA-Z]\\w*|\\d+))\\s*";

        Pattern r1 = Pattern.compile(pattern1);
        Pattern r2 = Pattern.compile(pattern2);
        Matcher matcher1 = r1.matcher(line);
        Matcher matcher2 = r2.matcher(line);
        if (matcher1.find()) {
            System.out.println(matcher1.group(1));
            System.out.println(matcher1.group(2));
            addToken(TokenName.DESTINATION, matcher1.group(1));
            addToken(TokenName.OPERATOR, "=");
            tokenizeAssignment(matcher1.group(2));
            return true;
        } else if (matcher2.find()) {
            System.out.println(matcher1.group(1));
            System.out.println(matcher2.group(2));
            tokenizeAssignment(matcher2.group(2));
            for (String item : variables) {
                addToken(TokenName.ARGUMENT, item);
            }
            return true;
        }
        return false;
    }

    public boolean isWriteMethod(String line) {
        boolean flag;
        String pattern = "(?i)\\s*WRITE\\s*\\((\\s*[a-zA-Z_]\\w*\\s*(,\\s*[a-zA-Z_]\\w*\\s*)*)\\)\\s*";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();
        addToken(TokenName.KEYWORD, "WRITE");
        addToken(TokenName.OPENING_BRACKET, "(");
        if (flag) {
            readArgumentsAndAddTokens(matcher.group(1));
        }

        addToken(TokenName.CLOSING_BRACKET, ")");
        return flag;

    }

    public boolean isVar(String line) {
        boolean flag;
        String pattern = "(?i)\\s*VAR\\s*";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();
        if (flag) {
            //addToken("keyword","VAR");
            System.out.println("Next line is the decalaration of variables");
        }
        return flag;
    }

    public boolean isNumber(String line) {
        String pattern = "(?i)\\d+";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        return matcher.find();
    }

    public boolean isidentifier(String line) {
        String pattern = "(?i)[_$\\w]+";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        return matcher.find();
    }

    public boolean isBegin(String line) {
        boolean flag;
        String pattern = "(?i)\\s*BEGIN\\s*";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();
        if (flag) {
            System.out.println("Start of the program logic.");
        }
        return flag;
    }

}
