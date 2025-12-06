package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.BreakStatement;
import me.abdelaziz.parser.Parser;

public final class EscapeHandler implements StatementHandler {

    @Override
    public Statement parse(final Parser parser) {
        return new BreakStatement();
    }
}