package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.function.exception.BreakException;

public final class BreakStatement implements Statement {

    @Override
    public void execute(final Environment env) {
        throw new BreakException();
    }
}