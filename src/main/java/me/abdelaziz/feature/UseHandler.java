package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.UseStatement;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.token.TokenType;

public final class UseHandler implements StatementHandler {

    @Override
    public Statement parse(final Parser parser) {
        return new UseStatement(parser.consume(TokenType.STRING, "Expected file path string").text);
    }
}