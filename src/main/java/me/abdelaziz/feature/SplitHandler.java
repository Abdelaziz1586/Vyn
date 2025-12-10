package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.SplitStatement;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public final class SplitHandler implements StatementHandler {

    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public Statement parse(final Parser parser) {
        parser.consume("do", "Expected 'do' after split");

        final List<Statement> body = new ArrayList<>();

        while (true) {
            while (parser.match(TokenType.NEWLINE));

            if (parser.check("end") || parser.check(TokenType.EOF))
                break;

            body.add(parser.statement());
        }

        parser.consume("end", "Expected 'end' after split body");

        return new SplitStatement(body);
    }
}