package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.TryStatement;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.token.TokenType;

import java.util.List;

public final class AttemptHandler implements StatementHandler {

    @Override
    public Statement parse(final Parser parser) {
        parser.consume("do", "Expected 'do' after attempt");

        final List<Statement> tryBlock = parser.parseBlock("recover", "end");

        List<Statement> catchBlock = null;
        String errorVar = "error";

        if (parser.check("recover")) {
            parser.consume("recover", "Expected 'recover'");

            errorVar = parser.consume(TokenType.IDENTIFIER, "Expected error variable name").text;

            parser.consume("do", "Expected 'do' after recover variable");

            catchBlock = parser.parseBlock("end");
        }

        parser.consume("end", "Expected 'end'");

        return new TryStatement(tryBlock, catchBlock, errorVar);
    }
}