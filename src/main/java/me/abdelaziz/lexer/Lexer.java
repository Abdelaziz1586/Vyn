package me.abdelaziz.lexer;

import me.abdelaziz.token.Token;
import me.abdelaziz.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public final class Lexer {

    private final String input;
    private int pos;

    public Lexer(final String input) {
        this.input = input;
        this.pos = 0;
    }

    public List<Token> tokenize() {
        final List<Token> tokens = new ArrayList<>();
        while (pos < input.length()) {
            final char current = peek(0);
            if (Character.isDigit(current)) tokens.add(tokenizeNumber());
            else if (Character.isLetter(current)) tokens.add(tokenizeWord());
            else if (current == '"') tokens.add(tokenizeString());
            else if (current == '\n') {
                tokens.add(new Token(TokenType.NEWLINE, "\n"));
                pos++;
            } else tokenizeOperatorOrWhitespace(tokens, current);
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private void tokenizeOperatorOrWhitespace(final List<Token> tokens, final char current) {
        switch (current) {
            case '+':
                tokens.add(new Token(TokenType.PLUS, "+"));
                pos++;
                break;
            case '-':
                tokens.add(new Token(TokenType.MINUS, "-"));
                pos++;
                break;
            case '*':
                tokens.add(new Token(TokenType.STAR, "*"));
                pos++;
                break;
            case '/':
                tokens.add(new Token(TokenType.SLASH, "/"));
                pos++;
                break;
            case '%':
                tokens.add(new Token(TokenType.MOD, "%"));
                pos++;
                break;
            case '=':
                if (peek(1) == '=') {
                    tokens.add(new Token(TokenType.EQ, "=="));
                    pos += 2;
                } else {
                    pos++;
                }
                break;
            case '!':
                if (peek(1) == '=') {
                    tokens.add(new Token(TokenType.EQ, "!="));
                    pos += 2;
                } else {
                    tokens.add(new Token(TokenType.BANG, "!"));
                    pos++;
                }
                break;
            case '>':
                tokens.add(new Token(TokenType.GT, ">"));
                pos++;
                break;
            case '<':
                tokens.add(new Token(TokenType.LT, "<"));
                pos++;
                break;
            case '.':
                tokens.add(new Token(TokenType.DOT, "."));
                pos++;
                break;
            case ' ':
            case '\t':
            case '\r':
                pos++;
                break;
            case '(':
                tokens.add(new Token(TokenType.LPAREN, "("));
                pos++;
                break;
            case ')':
                tokens.add(new Token(TokenType.RPAREN, ")"));
                pos++;
                break;
            case ',':
                tokens.add(new Token(TokenType.COMMA, ","));
                pos++;
                break;
            default:
                throw new RuntimeException("Unknown character at pos " + pos + ": " + current);
        }
    }

    private Token tokenizeNumber() {
        final StringBuilder sb = new StringBuilder();
        while (pos < input.length() && (Character.isDigit(peek(0)) || peek(0) == '.')) {
            sb.append(peek(0));
            pos++;
        }

        return new Token(TokenType.NUMBER, sb.toString());
    }

    private Token tokenizeString() {
        pos++;
        final StringBuilder sb = new StringBuilder();
        while (pos < input.length() && peek(0) != '"') {
            sb.append(peek(0));
            pos++;
        }
        pos++;
        return new Token(TokenType.STRING, sb.toString());
    }

    private Token tokenizeWord() {
        final StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isLetterOrDigit(peek(0))) {
            sb.append(peek(0));
            pos++;
        }

        return new Token(TokenType.IDENTIFIER, sb.toString());
    }

    private char peek(final int offset) {
        if (pos + offset >= input.length()) return '\0';

        return input.charAt(pos + offset);
    }
}