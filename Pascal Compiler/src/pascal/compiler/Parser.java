package pascal.compiler;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author ahmed
 */
public class Parser {

    private Token token;

    public Parser(ArrayList<TOKENTYPE> tokenName, ArrayList<String> tokens, HashSet<String> identifiers) {
        token = new Token(tokenName, tokens, identifiers);
    }

    public void init() {
        if ("PROGRAM".equals(token.getCurrentToken()) && token.getNextTokenType() == TOKENTYPE.PROGRAM_NAME && "VAR".equals(token.getNextToken())) {
            checkIdList();
        } else {
            return;
        }
        if ("BEGIN".equals(token.getNextToken())) {
            checkStmt();
        }
    }

    private void checkIdList() {
        if (token.getNextTokenType() == TOKENTYPE.IDENTIFIER && token.getNextTokenType() == TOKENTYPE.SEPERATOR) {
            checkIdList();
            return;
        }
        if (token.getPreviousTokenType() == TOKENTYPE.IDENTIFIER) {
            return;
        }
    }

    private void checkStmt() {

        if ("END".equals(token.getNextToken())) {
            System.out.println("END-----------------------------------------END");
            return;
        }
        if (token.getCurrentTokenType()== TOKENTYPE.END_STMT) {
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
        }else{
            checkStmt();
        }
    }

    private void checkRead() {
        if ("READ".equals(token.getCurrentToken()) && token.getNextTokenType() == TOKENTYPE.OPENING_BRACKET) {
            checkIdList();
        }
        if (token.getNextTokenType() == TOKENTYPE.CLOSING_BRACKET) {
            return;
        }
    }

    private void checkWrite() {
        if ("Write".equals(token.getCurrentToken()) && token.getNextTokenType() == TOKENTYPE.OPENING_BRACKET) {
            checkIdList();
        }
        if (token.getNextTokenType() == TOKENTYPE.CLOSING_BRACKET) {
            return;
        }
    }

    private void checkAssign() {
        if (token.getNextTokenType() == TOKENTYPE.IDENTIFIER && token.getNextToken() == ":=") {
            checkExp();
        }
    }

    private void checkExp() {
        checkFactor();
        if (token.getCurrentTokenType() == TOKENTYPE.OPERATOR) {
            checkFactor();
            return;
        }
    }

    private void checkFactor() {
        if (token.getNextTokenType() == TOKENTYPE.IDENTIFIER && token.getNextTokenType() == TOKENTYPE.OPERATOR) {
            checkExp();
            return;
        }
        if (token.getPreviousTokenType() == TOKENTYPE.IDENTIFIER) {
            return;
        }
    }
}
