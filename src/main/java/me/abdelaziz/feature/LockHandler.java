package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.expression.VarDeclaration;
import me.abdelaziz.token.TokenType;
import me.abdelaziz.parser.Parser;

public final class LockHandler implements StatementHandler {

    @Override
    public Statement parse(final Parser parser) {
        final String name = parser.consume(TokenType.IDENTIFIER, "Expected constant name").text;
        return new VarDeclaration(name, parser.expression(), true);
    }
}