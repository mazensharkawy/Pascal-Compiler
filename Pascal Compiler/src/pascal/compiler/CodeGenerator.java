package pascal.compiler;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahmed
 */
public class CodeGenerator {

    private String RegisterA = "";
    private ArrayList<String> listCount = new ArrayList<>();
    private String destination;
    private ArrayList<String> expArray = new ArrayList<>();
    private FileWriter filewriter;
    private BufferedWriter writer;

    public CodeGenerator() {
        String fileName = Compiler.fileName.substring(0, Compiler.fileName.length()-4);
        try {
            filewriter = new FileWriter(fileName + "Assembly.txt");
        } catch (IOException ex) {
            Logger.getLogger(CodeGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        writer = new BufferedWriter(filewriter);
    }

    public void loadRegisterA(String firstOperand) throws IOException {
        if (RegisterA.equals(firstOperand)) {
            return;
        }
        RegisterA = firstOperand;

        writer.write("\tLDA\t" + RegisterA);
        writer.newLine();
    }

    public void loadDestination(String destinationValue) {
        destination = destinationValue;
    }

    public void generateDestination() throws IOException {
        writer.write("\tSTA\t" + destination);
        writer.newLine();
    }

    public void readSubRoutine() throws IOException {
        writer.write("\t+SUB\tREADX");
        writer.newLine();
    }

    public void writeSubRoutine() throws IOException {
        writer.write("\t+SUB\tWRITEX");
        writer.newLine();
    }

    public void generateIdListForMethod() throws IOException {
        for (String item : listCount) {
            writer.write("\tWORD\t" + item);
            writer.newLine();
        }
        clearListCount();
    }

    public void addToExpArray(String token) {
        expArray.add(token);
    }

    public void printExpArray() throws IOException {
        System.out.println("EXPRESSION ARRAY");
        writer.newLine();
        for (String item : expArray) {
            System.out.print(item);
            System.out.print("  ");
        }

        System.out.println("");
    }

    public void generateMulExpression(String term1, int index) throws IOException {
        writer.write("\tMUL\t" + term1);
        writer.newLine();
        if (expArray.size() > index + 1 && ("+".equals(expArray.get(index + 1)) || "*".equals(expArray.get(index + 1)))) {

            return;
        }
        generateDestination();

    }

    public boolean generateMulExpressionWithTemp(String term1, int index) throws IOException {
        writer.write("\tMUL\t" + term1);
        writer.newLine();
        if (expArray.size() > index + 2 && "*".equals(expArray.get(index + 1))) {
            return true;
        }
        writer.write("\tADD\tTEMP1");
        writer.newLine();
        return false;

    }

    public void generateAddExpression(String term1, int index) throws IOException {
        writer.write("\tADD\t" + term1);
        writer.newLine();
        if (expArray.size() > index + 1 && ("+".equals(expArray.get(index + 1)) || "*".equals(expArray.get(index + 1)))) {
            return;
        }
        generateDestination();
    }

    public void generateStartOfProgram(String programName) throws IOException {
        writer.write(programName + "\tSTART\t0");
        writer.newLine();
        writer.write("\tEXTREF\tXWRITE, XREAD");
        writer.newLine();
        writer.write("\tSTL\tRETADR");
        writer.newLine();

    }

    public void generateEndOfProgram() throws IOException {
        writer.write("\tLDL\tRETADR");
        writer.newLine();
        writer.write("\tRSUB");
        writer.newLine();
        writer.flush();
        writer.close();
    }

    public void generateIdList() throws IOException {
        for (String item : listCount) {
            writer.write(item + "\tRESW\t1");
            writer.newLine();
        }
        clearListCount();
    }

    public void generateExpressionCode() throws IOException {
        String previousOperand = "*";
        boolean recurse = false;
        boolean oneElement = false;
        if (isOneElementAssignment()) {
            oneElement = true;
        }

        loadFirstOperand(expArray.get(0));
        for (int i = 1; i < expArray.size(); i++) {
            if (recurse) {
                previousOperand = "*";
                recurse = generateMulExpressionWithTemp(expArray.get(++i), i);
            } else if (expArray.size() > i + 2 && "*".equals(previousOperand) && "+".equals(expArray.get(i)) && "*".equals(expArray.get(i + 2))) {
                previousOperand = "*";
                storeInTempAndLoadNextA(expArray.get(++i));
                recurse = true;
            } else if ("+".equals(expArray.get(i))) {
                recurse = false;
                previousOperand = "+";
                generateAddExpression(expArray.get(++i), i);
            } else if ("*".equals(expArray.get(i)) && "+".equals(previousOperand)) {
                recurse = false;
                generateMulExpressionWithTemp(expArray.get(++i), i);
            } else if ("*".equals(expArray.get(i))) {
                recurse = false;
                previousOperand = "*";
                generateMulExpression(expArray.get(++i), i);
            } else {
                recurse = false;
                loadRegisterA(destination);
            }
        }
        if (oneElement) {
            generateDestination();
        }
        expArray.clear();
    }

    private void clearListCount() {
        listCount.clear();
    }

    private void loadFirstOperand(String token) throws IOException {
        writer.write("\tLDA\t" + token);
        writer.newLine();
    }

    private void storeInTempAndLoadNextA(String token) throws IOException {
        writer.write("\tSTA\tTEMP1");
        writer.newLine();
        writer.write("\tLDA\t" + token);
        writer.newLine();
    }

    private boolean isOneElementAssignment() {
        return expArray.size() == 1;
    }

    public void addToListCount(String id) {
        listCount.add(id);
    }

    public void changeExpArrayPriority() {
        //printExpArray();
        expArray = shiftMultiplication();
        //printExpArray();
    }

    private ArrayList<String> shiftMultiplication() {

        ArrayList<String> newList = new ArrayList<>();

        for (int i = 0; i < expArray.size(); i++) {
            if ("*".equals(expArray.get(i))) {
                if (newList.size() > 0 && !"*".equals(newList.get(newList.size() - 1)) && !"+".equals(expArray.get(i - 1)) && !"#".equals(expArray.get(i - 1))) {
                    newList.add("+");
                }
                if (!"+".equals(expArray.get(i - 1)) && !"#".equals(expArray.get(i - 1))) {
                    newList.add(expArray.get(i - 1));
                }
                newList.add(expArray.get(i));
                newList.add(expArray.get(i + 1));

                expArray.set(i - 1, "#");
                expArray.set(i, "#");
                expArray.set(i + 1, "#");
            }
        }
        if (newList.size() > 0 && !"+".equals(getFirstEncounter())) {
            newList.add("+");
        }
        for (int i = 0; i < expArray.size(); i++) {

            if (!"#".equals(expArray.get(i))) {
                if ("+".equals(expArray.get(i)) && "+".equals(newList.get(newList.size() - 1))) {
                } else {
                    newList.add(expArray.get(i));
                }
            }
        }
        if ("+".equals(newList.get(newList.size() - 1))) {
            newList.remove(newList.size() - 1);
        }
        return newList;
    }

    private String getFirstEncounter() {
        for (int i = 0; i < expArray.size(); i++) {
            if (!"#".equals(expArray.get(i))) {
                return expArray.get(i);
            }
        }
        return "#";
    }

}
