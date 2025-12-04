package me.abdelaziz.token;

public final class Token {

    public final String text;
    public final TokenType type;

    public Token(final TokenType type, final String text) {
        this.type = type;
        this.text = text;
    }

    @Override
    public String toString() {
        return type + ":" + text;
    }
}