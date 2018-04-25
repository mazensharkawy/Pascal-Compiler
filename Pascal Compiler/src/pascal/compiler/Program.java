/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pascal.compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ahmed
 */
public class Program {

    public String programName;
    public String[] variables;
    public String[] readArguments;

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public boolean isStart(String line) {
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

    public boolean isRead(String line) {
        boolean flag;
        String pattern = "(?i)\\s*READ\\s*\\(((\\w+(\\s+)?,?(\\s+)?)+)\\)\\s*";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);
        flag = matcher.find();

        if (flag) {
            readArguments = matcher.group(1).replaceAll(" ", "").split(",");
            for (String item : readArguments) {
                System.out.println(item);
            }
        }
        return flag;
    }

    public boolean isAssignment(String line) {
        boolean flag;
        String pattern1 = "(?i)\\s*\\w+\\s*:=\\s*(\\w+\\s*([+*]\\w+\\s*)+)";
        String pattern2 = "(?i)\\s*\\w+\\s*:=\\s*(\\w+)\\s*";
        Pattern r1 = Pattern.compile(pattern1);
        Pattern r2 = Pattern.compile(pattern2);
        Matcher matcher1 = r1.matcher(line);
        Matcher matcher2 = r2.matcher(line);
        if (matcher1.find()) {
            System.out.println(matcher1.group(1));
            return true;
        } else if (matcher2.find()) {
            System.out.println(matcher2.group(1));
            return true;
        }
        return false;

    }

}
