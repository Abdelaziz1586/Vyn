package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.IfStatement;
import me.abdelaziz.token.TokenType;
import me.abdelaziz.parser.Parser;

import java.util.ArrayList;
import java.util.List;

public final class CheckHandler implements StatementHandler {

    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public Statement parse(final Parser parser) {
        final Expression condition = parser.expression();
        parser.consume("do", "Expected 'do'");

        final List<Statement> thenBranch = new ArrayList<>();

        while (true) {
            while (parser.match(TokenType.NEWLINE));

            if (parser.check("end") || parser.check("otherwise") || parser.check(TokenType.EOF))
                break;

            thenBranch.add(parser.statement());
        }

        List<Statement> elseBranch = null;
        if (parser.check("otherwise")) {
            parser.consume("otherwise", "Expected 'otherwise'");
            elseBranch = new ArrayList<>();

            while (true) {
                while (parser.match(TokenType.NEWLINE));

                if (parser.check("end") || parser.check(TokenType.EOF))
                    break;

                elseBranch.add(parser.statement());
            }
        }

        parser.consume("end", "Expected 'end'");
        return new IfStatement(condition, thenBranch, elseBranch);
    }
}