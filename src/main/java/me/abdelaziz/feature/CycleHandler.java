package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.CycleStatement;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public final class CycleHandler implements StatementHandler {

    @Override
    public Statement parse(final Parser parser) {
        if (parser.check("while")) {
            parser.consume("while", "Expected 'while'");

            return new CycleStatement(parser.expression(), parseBody(parser));
        }

        final String variableName = parser.consume(TokenType.IDENTIFIER, "Expected variable name").text;
        parser.consume("from", "Expected 'from'");
        final Expression start = parser.expression();
        parser.consume("to", "Expected 'to'");
        final Expression end = parser.expression();

        return new CycleStatement(variableName, start, end, parseBody(parser));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private List<Statement> parseBody(final Parser parser) {
        parser.consume("do", "Expected 'do'");
        final List<Statement> body = new ArrayList<>();

        while (true) {
            while (parser.match(TokenType.NEWLINE));

            if (parser.check("end") || parser.check(TokenType.EOF))
                break;

            body.add(parser.statement());
        }

        parser.consume("end", "Expected 'end'");
        return body;
    }
}