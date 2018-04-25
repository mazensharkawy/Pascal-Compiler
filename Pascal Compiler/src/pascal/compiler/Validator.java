package pascal.compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ahmed
 */
public class Validator {

    public String programName;
    public String[] variables;
    public String[] readArguments;
    public String[] writeArguments;

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
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
        variables = line.split("[,\\s+]");
    }

    public String[] getVariables() {
        return variables;
    }

    public String[] getVariablesInFunction(String line) {
        return line.replaceAll(" ", "").split("[+*]");
    }

    public boolean isRead(String line) {
        boolean flag;
        String pattern = "(?i)\\s*READ\\s*\\((\\s*[a-zA-Z_]\\w*\\s*(,\\s*[a-zA-Z_]\\w*\\s*)*)\\)\\s*";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();

        if (flag) {
            readArguments(matcher.group(1));
        }
        return flag;
    }

    private void readArguments(String line) {
        readArguments = line.replaceAll(" ", "").split(",");
        for (String item : readArguments) {
            System.out.println(item);
        }
    }

    public boolean isAssignment(String line) {
        String pattern1 = "(?i)\\s*[a-zA-Z]\\w*\\s*:=\\s*(([a-zA-Z]\\w+|\\d+)\\s*([+*]\\s*([a-zA-Z]+\\w*|\\d+)\\s*)+)";
        String pattern2 = "(?i)\\s*[a-zA-Z]\\w*\\s*:=\\s*(([a-zA-Z]\\w+|\\d+))\\s*";

        Pattern r1 = Pattern.compile(pattern1);
        Pattern r2 = Pattern.compile(pattern2);
        Matcher matcher1 = r1.matcher(line);
        Matcher matcher2 = r2.matcher(line);
        if (matcher1.find()) {
            System.out.println(matcher1.group(1));
            getVariablesInFunction(matcher1.group(1));
            return true;
        } else if (matcher2.find()) {
            System.out.println(matcher2.group(1));
            getVariablesInFunction(matcher2.group(1));
            return true;
        }
        return false;

    }

    public boolean isWrite(String line) {
        boolean flag;
        String pattern = "(?i)\\s*WRITE\\s*\\((\\s*[a-zA-Z_]\\w*\\s*(,\\s*[a-zA-Z_]\\w*\\s*)*)\\)\\s*";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();

        if (flag) {
            writeArguments = matcher.group(1).replaceAll(" ", "").split(",");
            for (String item : writeArguments) {
                System.out.println(item);
            }
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
            System.out.println("Next line is the decalaration of variables");
        }

        return flag;

    }

}
