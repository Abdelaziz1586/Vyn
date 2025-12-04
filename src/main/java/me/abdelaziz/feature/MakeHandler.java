package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
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
            final Expression value = parser.expression();
            return new PropertySetStatement(new VariableExpression(nameToken.text), propName, value);
        }

        final Expression value = parser.expression();
        return new VarDeclaration(nameToken.text, value, false);
    }
}