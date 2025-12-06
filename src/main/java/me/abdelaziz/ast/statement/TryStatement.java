package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.exception.ReturnException;

import java.util.List;

public final class TryStatement implements Statement {

    private final String errorVariableName;
    private final List<Statement> tryBlock, catchBlock;

    public TryStatement(final List<Statement> tryBlock, final List<Statement> catchBlock, final String errorVariableName) {
        this.tryBlock = tryBlock;
        this.catchBlock = catchBlock;
        this.errorVariableName = errorVariableName;
    }

    @Override
    public void execute(final Environment env) {
        try {
            for (final Statement stmt : tryBlock)
                stmt.execute(env);
        } catch (final ReturnException re) {
            throw re;
        } catch (final RuntimeException e) {
            if (catchBlock != null) {
                final Environment catchEnv = new Environment(env);
                
                String msg = e.getMessage();
                if (msg == null) msg = "Unknown Error";
                
                catchEnv.define(errorVariableName, new Value(msg), true);

                for (final Statement stmt : catchBlock)
                    stmt.execute(catchEnv);
            }
        }
    }
}