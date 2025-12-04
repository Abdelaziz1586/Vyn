package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.FunctionDeclaration;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public final class TaskHandler implements StatementHandler {

    @Override
    public Statement parse(final Parser parser) {
        final String name = parser.consume(TokenType.IDENTIFIER, "Expect task name").text;
        final List<String> params = new ArrayList<>();

        if (parser.check("takes")) {
            parser.consume("takes", "Expect 'takes'");
            do {
                params.add(parser.consume(TokenType.IDENTIFIER, "Expect parameter name").text);
            } while (parser.match(TokenType.COMMA));
        }

        parser.consume("do", "Expect 'do'");

        final List<Statement> body = new ArrayList<>();
        while (!parser.check("end") && !parser.check(TokenType.EOF))
            body.add(parser.statement());

        parser.consume("end", "Expect 'end'");

        return new FunctionDeclaration(name, params, body);
    }
}