package pascal.compiler;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author ahmed
 */
public class Token {

    private ArrayList<TOKENTYPE> tokenType = new ArrayList<>();
    private ArrayList<String> tokens = new ArrayList<>();
    private HashSet<String> identifiers = new HashSet<>();

    private int currentIndex = 0;

    public Token(ArrayList<TOKENTYPE> tokenName, ArrayList<String> tokens, HashSet<String> identifiers) {
        this.tokenType = tokenName;
        this.tokens = tokens;
        this.identifiers = identifiers;
    }

    public ArrayList<TOKENTYPE> getTokenType() {
        return tokenType;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public HashSet<String> getIdentifiers() {
        return identifiers;
    }

    public String getNextToken() {
        return tokens.get(++currentIndex);
    }

    public String getCurrentToken() {
        return tokens.get(currentIndex);
    }

    public TOKENTYPE getCurrentTokenType() {
        return tokenType.get(currentIndex);
    }

    public TOKENTYPE getNextTokenType() {
        return tokenType.get(++currentIndex);
    }
    
    public TOKENTYPE getPreviousTokenType() {
        return tokenType.get(--currentIndex);
    }
    
    public TOKENTYPE peakPreviousTokenType() {
        return tokenType.get(currentIndex-1);
    }
    
    public String getPreviousToken() {
        return tokens.get(--currentIndex);
    }
    
     public String peakPreviousToken() {
        return tokens.get(currentIndex-1);
    }

    public boolean isDefinedIdentifier(String identifier) {
        return identifiers.contains(identifier);
    }

}
