package me.abdelaziz.api;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.parser.Parser;

public interface StatementHandler {
    Statement parse(final Parser parser);
}