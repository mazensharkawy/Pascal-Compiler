package pascal.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ahmed
 */
public class LexicalAnalyzer {

   // private ArrayList<HashMap<TOKEN, String>> tokens = new ArrayList<>();
    private ArrayList<TOKEN> tokenName = new ArrayList<>();
    private ArrayList<String> tokens = new ArrayList<>();

    public String programName;
    public String[] variables;

    public String[] writeMethodArguments;

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
            addToken(TOKEN.KEYWORD, "END");

            printTokens();
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
    private void printTokens(){
        for(int i =0;i<tokens.size();i++){
            System.out.print(tokenName.get(i));
            System.out.print("\t");
            System.out.println(tokens.get(i));
        }
    }
    
    public ArrayList<TOKEN> getTokenNameList(){
        return tokenName;
    }
    public ArrayList<String> getTokensList(){
        return tokens;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
        addToken(TOKEN.PROGRAM_NAME, programName);
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
            addToken(TOKEN.IDENTIFIER, token);
        }
    }

    public String[] getVariables() {
        return variables;
    }

    public void tokenizeAssignment(String line) {
        System.out.println("TOKENIZEEEEEE");
        System.out.println(line);
        ArrayList<String> tokens = new ArrayList<>();
        String token = "";
        line = line.replaceAll(" ", "");
        char[] charArray = line.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == '+' || charArray[i] == '*') {
                addToken(TOKEN.OPERAND, token);
                addToken(TOKEN.OPERATOR, String.valueOf(charArray[i]));
                token = "";
            } else if (charArray[i] == '(') {
                addToken(TOKEN.OPENING_BRACKET, String.valueOf(charArray[i]));
            } else if (charArray[i] == ')') {
                addToken(TOKEN.OPERAND, token);
                token = "";
                addToken(TOKEN.CLOSING_BRACKET, String.valueOf(charArray[i]));
            } else {
                token += charArray[i];
            }
        }
        if (token != "") {
            addToken(TOKEN.OPERAND, token);
        }
    }

    public boolean isReadMethod(String line) {
        boolean flag;
        String pattern = "(?i)\\s*READ\\s*\\((\\s*[a-zA-Z_]\\w*\\s*(,\\s*[a-zA-Z_]\\w*\\s*)*)\\)\\s*";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();
        if (flag) {
            addToken(TOKEN.KEYWORD, "READ");
            addToken(TOKEN.OPENING_BRACKET, "(");
            readArgumentsAndAddTokens(matcher.group(1));
            addToken(TOKEN.CLOSING_BRACKET, ")");
        }

        return flag;
    }

    private void readArgumentsAndAddTokens(String line) {

        String[] methodArguments = line.replaceAll(" ", "").split(",");
        for (String item : methodArguments) {
            System.out.println("argument: " + item);
            addToken(TOKEN.ARGUMENT, item);
        }
    }

    private void addToken(TOKEN key, String token) {
        
        tokenName.add(key);
        tokens.add(token);
//        HashMap<TOKEN, String> map = new HashMap<>();
//        map.put(key, token);
//        tokens.add(map);
    }

    public boolean isAssignment(String line) {
        String pattern1 = "\\s*([a-zA-Z]\\w*)\\s*:=\\s*(\\(?([a-zA-Z]\\w*|\\d+)\\s*([+*]\\s*\\(?([a-zA-Z]+\\w*|\\d+)\\s*\\)?)+)";
        String pattern2 = "\\s*([a-zA-Z]\\w*)\\s*:=\\s*([a-zA-Z]\\w*|\\d+)\\s*";

        Pattern r1 = Pattern.compile(pattern1);
        Pattern r2 = Pattern.compile(pattern2);
        Matcher matcher1 = r1.matcher(line);
        Matcher matcher2 = r2.matcher(line);
        if (matcher1.find() && checkOpenningAndClosingBrackets(line)) {
            System.out.println(matcher1.group(1));
            System.out.println(matcher1.group(2));
            addToken(TOKEN.DESTINATION, matcher1.group(1));
            addToken(TOKEN.OPERATOR, ":=");
            tokenizeAssignment(matcher1.group(2));
            return true;
        } else if (matcher2.find() && checkOpenningAndClosingBrackets(line)) {
            System.out.println(matcher2.group(1));
            System.out.println(matcher2.group(2));
            System.out.println("ANANAAA hennaaaaa");
            addToken(TOKEN.DESTINATION, matcher2.group(1));
            addToken(TOKEN.OPERATOR, ":=");
            addToken(TOKEN.OPERAND, matcher2.group(2));
            return true;
        }
        return false;
    }

    private boolean checkOpenningAndClosingBrackets(String line) {
        char[] charArray = line.toCharArray();
        int openingBrackets = 0;
        int closingBrackets = 0;
        for (char item : charArray) {
            if (item == '(') {
                openingBrackets++;
            } else if (item == ')') {
                closingBrackets++;
            }
        }
        if (closingBrackets != openingBrackets) {
            return false;
        }
        return true;
    }

    public boolean isWriteMethod(String line) {
        boolean flag;
        String pattern = "(?i)\\s*WRITE\\s*\\((\\s*[a-zA-Z_]\\w*\\s*(,\\s*[a-zA-Z_]\\w*\\s*)*)\\)\\s*";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();

        if (flag) {
            addToken(TOKEN.KEYWORD, "WRITE");
            addToken(TOKEN.OPENING_BRACKET, "(");
            readArgumentsAndAddTokens(matcher.group(1));
            addToken(TOKEN.CLOSING_BRACKET, ")");
        }

        return flag;

    }

    public boolean isVar(String line) {
        boolean flag;
        String pattern = "(?i)\\s*VAR\\s*";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();
        if (flag) {
            addToken(TOKEN.KEYWORD, "VAR");
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
            addToken(TOKEN.KEYWORD, "BEGIN");
            System.out.println("Start of the program logic.");
        }
        return flag;
    }

}
