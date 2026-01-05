package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.ClassDeclaration;
import me.abdelaziz.token.TokenType;
import me.abdelaziz.parser.Parser;

import java.util.List;

public final class BlueprintHandler implements StatementHandler {

    @Override
    public Statement parse(final Parser parser) {
        final String name = parser.consume(TokenType.IDENTIFIER, "Expected class name").text;

        String parentName = null;
        if (parser.check("mimics")) {
            parser.consume("mimics", "Expected 'mimics'");
            parentName = parser.consume(TokenType.IDENTIFIER, "Expected parent class name").text;
        }

        parser.consume("do", "Expected 'do'");

        final List<Statement> body = parser.parseBlock("end");

        parser.consume("end", "Expected 'end'");
        return new ClassDeclaration(name, parentName, body);
    }
}