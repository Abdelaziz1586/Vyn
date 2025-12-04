package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.ClassDeclaration;
import me.abdelaziz.token.TokenType;
import me.abdelaziz.parser.Parser;

import java.util.ArrayList;
import java.util.List;

public final class BlueprintHandler implements StatementHandler {

    @Override
    public Statement parse(final Parser parser) {
        final String name = parser.consume(TokenType.IDENTIFIER, "Expected class name").text;
        parser.consume("do", "Expected 'do'");

        final List<Statement> body = new ArrayList<>();
        while (!parser.check("end") && !parser.check(TokenType.EOF)) {
            body.add(parser.statement());
        }
        parser.consume("end", "Expected 'end'");
        return new ClassDeclaration(name, body);
    }
}