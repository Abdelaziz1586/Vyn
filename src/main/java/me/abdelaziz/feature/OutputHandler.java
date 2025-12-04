package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.PrintStatement;
import me.abdelaziz.parser.Parser;

public final class OutputHandler implements StatementHandler {

    private final boolean isNewLine;

    public OutputHandler(final boolean isNewLine) {
        this.isNewLine = isNewLine;
    }

    @Override
    public Statement parse(final Parser parser) {
        return new PrintStatement(parser.expression(), isNewLine);
    }
}