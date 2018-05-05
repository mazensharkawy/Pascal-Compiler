package pascal.compiler;

import java.util.ArrayList;

/**
 *
 * @author ahmed
 */
public class CodeGenerator {

    private static String RegisterA = "";
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

    public static boolean generateMulExpressionWithTemp(String term1, int index) {
        System.out.println("\tMUL\t" + term1);
        if (expArray.size() > index + 2 && "*".equals(expArray.get(index + 1))) {
            return true;
        }
        System.out.println("\tADD\tTEMP1");
        return false;

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

    private static void clearListCount() {
        listCount.clear();
    }

    private static void loadFirstOperand(String token) {
        System.out.println("\tLDA\t" + token);
    }

    private static void storeInTempAndLoadNextA(String token) {
        System.out.println("\tSTA\tTEMP1");
        System.out.println("\tLDA\t" + token);
    }

    private static boolean isOneElementAssignment() {
        return expArray.size() == 1;
    }

    public static void addToListCount(String id) {
        listCount.add(id);
    }

    public static void changeExpArrayPriority() {
        //printExpArray();
        expArray = shiftMultiplication();
        //printExpArray();
    }

    private static ArrayList<String> shiftMultiplication() {

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

    private static String getFirstEncounter() {
        for (int i = 0; i < expArray.size(); i++) {
            if (!"#".equals(expArray.get(i))) {
                return expArray.get(i);
            }
        }
        return "#";
    }

}
