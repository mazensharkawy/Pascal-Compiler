package pascal.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ahmed
 */
public class LexicalAnalyzer {

    // private ArrayList<HashMap<TOKENTYPE, String>> tokens = new ArrayList<>();
    private ArrayList<TOKENTYPE> tokenName = new ArrayList<>();
    private ArrayList<String> tokens = new ArrayList<>();
    private HashSet<String> identifiers = new HashSet<>();

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
                //System.out.println(newLine);
                if (isReadMethod(newLine)) {
                    // System.out.println("READ");

                } else if (isWriteMethod(newLine)) {
                    //System.out.println("WRITE");

                } else if (isAssignment(newLine)) {
                    //System.out.println("ASSIGNMENT");

                } else {
                    throw new SyntaxError("Undefined Instruction");
                }
            }
            addToken(TOKENTYPE.KEYWORD, "END");

            printTokens();
            reader.close();
//            System.out.println("IDENTIFIERS-----------------------------------------------------------");
//            for (String item : identifiers) {
//                System.out.println(item);
//            }

            Parser parser = new Parser(tokenName, tokens, identifiers);
            parser.init();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                fileReader.close();
            } catch (IOException ex) {

            }
        }
    }

    private void printTokens() {
        for (int i = 0; i < tokens.size(); i++) {
            System.out.printf("%-20s", tokenName.get(i));

            System.out.println(tokens.get(i));
        }
    }

    public ArrayList<TOKENTYPE> getTokenNameList() {
        return tokenName;
    }

    public ArrayList<String> getTokensList() {
        return tokens;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
        addToken(TOKENTYPE.PROGRAM_NAME, programName);
    }

    public boolean isStartOfProgram(String line) {
        boolean flag;
        String pattern = "(?i)\\s*PROGRAM\\s+(\\w+)\\s*";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();
        if (flag) {
            addToken(TOKENTYPE.KEYWORD, "PROGRAM");
            setProgramName(matcher.group(1));
        }

        return flag;
    }

    public void setVariables(String line) {
        variables = line.trim().split("[,\\s+]");

        identifiers.add(variables[0]);
        addToken(TOKENTYPE.IDENTIFIER, variables[0]);
        for (int i = 1; i < variables.length; i++) {
            addToken(TOKENTYPE.SEPERATOR, ",");
            addToken(TOKENTYPE.IDENTIFIER, variables[i]);

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
                addToken(TOKENTYPE.IDENTIFIER, token);
                addToken(TOKENTYPE.OPERATOR, String.valueOf(charArray[i]));
                token = "";
            } else if (charArray[i] == '(') {
                addToken(TOKENTYPE.OPENING_BRACKET, String.valueOf(charArray[i]));
            } else if (charArray[i] == ')') {
                addToken(TOKENTYPE.IDENTIFIER, token);
                token = "";
                addToken(TOKENTYPE.CLOSING_BRACKET, String.valueOf(charArray[i]));
            } else {
                token += charArray[i];
            }
        }
        if (token != "") {
            addToken(TOKENTYPE.IDENTIFIER, token);
        }
    }

    public boolean isReadMethod(String line) {
        boolean flag;
        String pattern = "(?i)\\s*READ\\s*\\((\\s*[a-zA-Z_]\\w*\\s*(,\\s*[a-zA-Z_]\\w*\\s*)*)\\)\\s*;";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();
        if (flag) {
            addToken(TOKENTYPE.STMT, "READ");
            addToken(TOKENTYPE.OPENING_BRACKET, "(");
            readArgumentsAndAddTokens(matcher.group(1));
            addToken(TOKENTYPE.CLOSING_BRACKET, ")");
            addToken(TOKENTYPE.END_STMT, ";");
        }

        return flag;
    }

    private void readArgumentsAndAddTokens(String line) {

        String[] methodArguments = line.replaceAll(" ", "").split(",");
        addToken(TOKENTYPE.IDENTIFIER, methodArguments[0]);
        for (int i = 1; i < methodArguments.length; i++) {
            addToken(TOKENTYPE.SEPERATOR, ",");
            addToken(TOKENTYPE.IDENTIFIER, methodArguments[i]);

        }
    }

    private void addToken(TOKENTYPE key, String token) {

        tokenName.add(key);
        tokens.add(token);
    }

    public boolean isAssignment(String line) {
        String pattern1 = "\\s*([a-zA-Z]\\w*)\\s*:=\\s*(\\(?([a-zA-Z]\\w*|\\d+)\\s*([+*]\\s*\\(?([a-zA-Z]+\\w*|\\d+)\\s*\\)?)+)\\s*;";
        String pattern2 = "\\s*([a-zA-Z]\\w*)\\s*:=\\s*([a-zA-Z]\\w*|\\d+)\\s*;";

        Pattern r1 = Pattern.compile(pattern1);
        Pattern r2 = Pattern.compile(pattern2);
        Matcher matcher1 = r1.matcher(line);
        Matcher matcher2 = r2.matcher(line);
        if (matcher1.find() && checkOpenningAndClosingBrackets(line)) {
//            System.out.println(matcher1.group(1));
//            System.out.println(matcher1.group(2));
            addToken(TOKENTYPE.STMT, matcher1.group(1));
            addToken(TOKENTYPE.OPERATOR, ":=");
            tokenizeAssignment(matcher1.group(2));
            addToken(TOKENTYPE.END_STMT, ";");
            return true;
        } else if (matcher2.find() && checkOpenningAndClosingBrackets(line)) {
//            System.out.println(matcher2.group(1));
//            System.out.println(matcher2.group(2));
//            System.out.println("ANANAAA hennaaaaa");
            addToken(TOKENTYPE.STMT, matcher2.group(1));
            addToken(TOKENTYPE.OPERATOR, ":=");
            addToken(TOKENTYPE.IDENTIFIER, matcher2.group(2));
            addToken(TOKENTYPE.END_STMT, ";");
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
        String pattern = "(?i)\\s*WRITE\\s*\\((\\s*[a-zA-Z_]\\w*\\s*(,\\s*[a-zA-Z_]\\w*\\s*)*)\\)\\s*;";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();

        if (flag) {
            addToken(TOKENTYPE.STMT, "WRITE");
            addToken(TOKENTYPE.OPENING_BRACKET, "(");
            readArgumentsAndAddTokens(matcher.group(1));
            addToken(TOKENTYPE.CLOSING_BRACKET, ")");
            addToken(TOKENTYPE.END_STMT, ";");
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
            addToken(TOKENTYPE.KEYWORD, "VAR");
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
            addToken(TOKENTYPE.KEYWORD, "BEGIN");
            System.out.println("Start of the program logic.");
        }
        return flag;
    }

}
