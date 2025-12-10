package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.FunctionDeclaration;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.token.TokenType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DemolishHandler implements StatementHandler {

    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public Statement parse(final Parser parser) {
        parser.consume("do", "Expect 'do' after demolish");

        final List<Statement> body = new ArrayList<>();
        while (true) {
            while (parser.match(TokenType.NEWLINE));

            if (parser.check("end") || parser.check(TokenType.EOF))
                break;

            body.add(parser.statement());
        }

        parser.consume("end", "Expect 'end'");

        return new FunctionDeclaration("_destroy", Collections.emptyList(), body);
    }
}