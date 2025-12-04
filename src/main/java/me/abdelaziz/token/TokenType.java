package me.abdelaziz.token;

public enum TokenType {

    // Structural
    EOF,

    // Data
    IDENTIFIER, NUMBER, STRING,

    // Operators
    PLUS, MINUS, STAR, SLASH, MOD, EQ, GT, LT, DOT,

    LPAREN, RPAREN, COMMA
}