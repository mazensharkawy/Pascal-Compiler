package pascal.compiler;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author ahmed
 */
public class CodeGenerator {

    private static String RegisterA;
    private static ArrayList<String> listCount = new ArrayList<>();
    private static String destination;
    private static ArrayList<String> expArray = new ArrayList<>();

    public static void loadRegisterA(String firstOperand) {
        if (RegisterA.equals(firstOperand)) {
            return;
        }
        RegisterA = firstOperand;
        System.out.println("\tLDA\t" + RegisterA);
    }

    public static void loadDestination(String destinationValue) {
        destination = destinationValue;
    }

    public static void generateDestination() {
        System.out.println("\tSTA\t" + destination);
    }

    public static void readSubRoutine() {
        System.out.println("\t+SUB\tREADX");
    }

    public static void writeSubRoutine() {
        System.out.println("\t+SUB\tWRITEX");
    }

    public static void generateIdListForMethod() {
        for (String item : listCount) {
            System.out.println("\tWORD\t" + item);
        }
        clearListCount();
    }

    public static void addToExpArray(String token) {
        expArray.add(token);
    }

    public static void printExpArray() {
        System.out.println("EXPRESSION ARRAY");
        for (String item : expArray) {
            System.out.print(item);
            System.out.print("  ");
        }
        System.out.println("");
    }

    public static void generateMulExpression(String term1, int index) {
        System.out.println("\tMUL\t" + term1);
        if (expArray.size() > index + 1 && ("+".equals(expArray.get(index + 1)) || "*".equals(expArray.get(index + 1)))) {
            return;
        }
        generateDestination();

    }

    public static void generateAddExpression(String term1, int index) {
        System.out.println("\tADD\t" + term1);
        if (expArray.size() > index + 1 && ("+".equals(expArray.get(index + 1)) || "*".equals(expArray.get(index + 1)))) {
            return;
        }
        generateDestination();
    }

    public static void generateStartOfProgram(String programName) {
        System.out.println(programName + "\tSTART\t0");
        System.out.println("\tEXTREF\tXWRITE, XREAD");
        System.out.println("\tSTL\tRETADR");

    }

    public static void generateEndOfProgram() {
        System.out.println("\tLDL\tRETADR");
        System.out.println("\tRSUB");
    }

    public static void generateIdList() {
        for (String item : listCount) {
            System.out.println(item + "\tRESW\t1");
        }
        clearListCount();
    }

    public static void generateExpressionCode() {
        System.out.println("\tLDA\t" + expArray.get(0));
        for (int i = 1; i < expArray.size(); i++) {
            if ("+".equals(expArray.get(i))) {
                generateAddExpression(expArray.get(++i), i);
            } else if ("*".equals(expArray.get(i))) {
                generateMulExpression(expArray.get(++i), i);
            } else {
                loadRegisterA(destination);
            }
        }
        expArray.clear();
    }

    private static void clearListCount() {
        listCount.clear();
    }

    public static void addToListCount(String id) {
        listCount.add(id);
    }

    public static void setDestination(String var) {
        destination = var;
    }


    public static void changeExpArrayPriority() {
        for (int i = 0; i < expArray.size(); i++) {

            if ("+".equals(expArray.get(i))) {
                checkForMult(i);
            }
        }
    }

    private static void checkForMult(int index) {
        for (int i = index; i < expArray.size(); i++) {
            if ("*".equals(expArray.get(i))) {
                swap(index - 1, i - 1);
                swap(index, i);
                swap(index + 1, i + 1);
            }
        }
        //printExpArray();
    }

    private static void swap(int firstIndex, int secondIndex) {
        String temp = expArray.get(firstIndex);
        expArray.set(firstIndex, expArray.get(secondIndex));
        expArray.set(secondIndex, temp);
    }

}
