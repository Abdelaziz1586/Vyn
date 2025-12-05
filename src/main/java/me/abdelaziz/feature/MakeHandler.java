package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.expression.LiteralExpression;
import me.abdelaziz.ast.expression.VariableExpression;
import me.abdelaziz.ast.expression.VarDeclaration;
import me.abdelaziz.ast.statement.PropertySetStatement;
import me.abdelaziz.token.Token;
import me.abdelaziz.token.TokenType;
import me.abdelaziz.parser.Parser;

public final class MakeHandler implements StatementHandler {

    @Override
    public Statement parse(final Parser parser) {
        final Token nameToken = parser.consume(TokenType.IDENTIFIER, "Expected variable name");

        if (parser.match(TokenType.DOT)) {
            final String propName = parser.consume(TokenType.IDENTIFIER, "Expected property name").text;
            return new PropertySetStatement(new VariableExpression(nameToken.text), propName, parser.expression());
        }

        if (parser.check(TokenType.NEWLINE) || parser.check(TokenType.EOF))
            return new VarDeclaration(nameToken.text, new LiteralExpression(null), false);

        return new VarDeclaration(nameToken.text, parser.expression(), false);
    }
}