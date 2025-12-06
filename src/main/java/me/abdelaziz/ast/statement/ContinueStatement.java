package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.function.exception.ContinueException;

public final class ContinueStatement implements Statement {

    @Override
    public void execute(final Environment env) {
        throw new ContinueException();
    }
}