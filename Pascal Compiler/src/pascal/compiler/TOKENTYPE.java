/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pascal.compiler;

/**
 *
 * @author ahmed
 */
public enum TOKENTYPE {
    PROGRAM_NAME,
    IDENTIFIER,
    OPERAND,
    DESTINATION,
    ARGUMENT,
    OPENING_BRACKET,
    CLOSING_BRACKET,
    KEYWORD,
    END_STMT,
    STMT,
    SEPERATOR,
    OPERATOR;
}
