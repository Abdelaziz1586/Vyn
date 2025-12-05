package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.IfStatement;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public final class CheckHandler implements StatementHandler {

    @Override
    public Statement parse(final Parser parser) {
        final Expression condition = parser.expression();
        parser.consume("do", "Expected 'do'");

        final List<Statement> thenBranch = new ArrayList<>();
        while (!parser.check("end") && !parser.check("otherwise") && !parser.check(TokenType.EOF))
            thenBranch.add(parser.statement());

        List<Statement> elseBranch = null;
        if (parser.check("otherwise")) {
            parser.consume("otherwise", "Expected 'otherwise'");

            elseBranch = new ArrayList<>();
            while (!parser.check("end") && !parser.check(TokenType.EOF))
                elseBranch.add(parser.statement());
        }

        parser.consume("end", "Expected 'end'");

        return new IfStatement(condition, thenBranch, elseBranch);
    }
}