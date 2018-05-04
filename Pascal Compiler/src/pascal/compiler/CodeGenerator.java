package pascal.compiler;

import java.util.ArrayList;

/**
 *
 * @author ahmed
 */
public class CodeGenerator {

    private static String RegisterA;
    private static ArrayList<String> listCount = new ArrayList<>();
    private static String destination;
    private static int locationCounter = 0;

    public static void loadRegisterA(String firstOperand) {
        if (RegisterA.equals(firstOperand)) {
            return;
        }
        RegisterA = firstOperand;
        System.out.println("\tLDA\t" + firstOperand);
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

    private static void clearListCount() {
        listCount.clear();
    }

    public static void addToListCount(String id) {
        listCount.add(id);
    }

    public static void setDestination(String var) {
        destination = var;
    }

    public void addToLocationCounter(int value) {
        locationCounter += value;
    }

}
