package pascal.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author ahmed
 */
public class Parser {

    private final Token token;
    private CodeGenerator codeGenerator = new CodeGenerator();

    public Parser(ArrayList<TOKENTYPE> tokenName, ArrayList<String> tokens, HashSet<String> identifiers) {
        token = new Token(tokenName, tokens, identifiers);
    }

    public void init() throws IOException {
        if ("PROGRAM".equals(token.getCurrentToken()) && token.getNextTokenType() == TOKENTYPE.PROGRAM_NAME && "VAR".equals(token.getNextToken())) {
            codeGenerator.generateStartOfProgram(token.peakPreviousToken());
            checkIdList();
            codeGenerator.generateIdList();
        } else {
            return;
        }
        if ("BEGIN".equals(token.getNextToken())) {
            checkStmt();
        }
    }

    private void checkIdList() {
        if (token.getNextTokenType() == TOKENTYPE.IDENTIFIER && token.getNextTokenType() == TOKENTYPE.SEPERATOR) {
            codeGenerator.addToListCount(token.peakPreviousToken());
            checkIdList();
            return;
        }
        if (token.getPreviousTokenType() == TOKENTYPE.IDENTIFIER) {
            codeGenerator.addToListCount(token.getCurrentToken());
            return;
        }
    }

    private void checkStmt() throws IOException {

        if ("END".equals(token.getNextToken())) {
            codeGenerator.generateEndOfProgram();
            return;
        }
        if (token.getCurrentTokenType() == TOKENTYPE.END_STMT) {
            checkStmt();
            return;
        }
        if ("READ".equals(token.getCurrentToken())) {
            checkRead();
            checkStmt();
        } else if ("WRITE".equals(token.getCurrentToken())) {
            checkWrite();
            checkStmt();
        } else if (token.isDefinedIdentifier(token.getCurrentToken())) {
            checkAssign();
            checkStmt();
        } else {
            System.out.println("else");
            checkStmt();
        }
    }

    private void checkRead() throws IOException {
        if ("READ".equals(token.getCurrentToken()) && token.getNextTokenType() == TOKENTYPE.OPENING_BRACKET) {
            codeGenerator.readSubRoutine();
            checkIdList();
        }
        if (token.getNextTokenType() == TOKENTYPE.CLOSING_BRACKET) {
            codeGenerator.generateIdListForMethod();
            return;
        }
    }

    private void checkWrite() throws IOException {
        if ("WRITE".equals(token.getCurrentToken()) && token.getNextTokenType() == TOKENTYPE.OPENING_BRACKET) {
            codeGenerator.writeSubRoutine();
            checkIdList();
        }
        if (token.getNextTokenType() == TOKENTYPE.CLOSING_BRACKET) {
            codeGenerator.generateIdListForMethod();
            return;
        }
    }

    private void checkAssign() throws IOException {
        if (token.getCurrentTokenType() == TOKENTYPE.IDENTIFIER && ":=".equals(token.getNextToken())) {
            codeGenerator.loadDestination(token.peakPreviousToken());
            checkExp();
        }
    }

    private void checkExp() throws IOException {
        checkFactor();
        if (token.getCurrentTokenType() == TOKENTYPE.OPERATOR) {
            checkFactor();
            return;
        }
    }

    private void checkFactor() throws IOException {
        if (token.getNextTokenType() == TOKENTYPE.IDENTIFIER && token.getNextTokenType() == TOKENTYPE.OPERATOR) {
            codeGenerator.addToExpArray(token.peakPreviousToken());
            codeGenerator.addToExpArray(token.getCurrentToken());
            checkExp();
            return;
        }
        if (token.getPreviousTokenType() == TOKENTYPE.IDENTIFIER) {
            codeGenerator.addToExpArray(token.getCurrentToken());
            codeGenerator.changeExpArrayPriority();
            codeGenerator.generateExpressionCode();
            return;
        }
    }

}
