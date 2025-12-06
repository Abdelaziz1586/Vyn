package me.abdelaziz.token;

public enum TokenType {

    // Structural
    EOF, NEWLINE,

    // Data
    IDENTIFIER, NUMBER, STRING,

    // Operators
    PLUS, MINUS, STAR, SLASH, MOD, EQ, GT, LT, DOT, BANG,

    LPAREN, RPAREN, COMMA,

    // Arrays
    LBRACKET, RBRACKET
}