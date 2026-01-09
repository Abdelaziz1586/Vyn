package me.abdelaziz.compiler;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.expression.*;
import me.abdelaziz.ast.statement.*;
import me.abdelaziz.bytecode.Chunk;
import me.abdelaziz.bytecode.OpCode;
import me.abdelaziz.runtime.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class BytecodeCompiler {

    private final Chunk chunk;
    private final Stack<List<Integer>> breakJumps = new Stack<>();
    private final Stack<Integer> continuePoints = new Stack<>();

    public BytecodeCompiler() {
        this.chunk = new Chunk();
    }

    public Chunk compile(final List<Statement> program) {
        for (final Statement stmt : program)
            compileStatement(stmt);

        if (chunk.code.isEmpty() || (chunk.code.get(chunk.code.size() - 1) & 0xFF) != OpCode.HALT.ordinal())
            chunk.write(OpCode.HALT);

        return chunk;
    }

    private void compileStatement(final Statement stmt) {
        if (stmt instanceof VarDeclaration) {
            final VarDeclaration var = (VarDeclaration) stmt;
            compileExpression(var.getInitializer());
            emitConstant(new Value(var.getName()));
            chunk.write(OpCode.DEFINE_GLOBAL);
        } else if (stmt instanceof FunctionDeclaration) {
            compileFunction((FunctionDeclaration) stmt);
        } else if (stmt instanceof ClassDeclaration) {
            compileClass((ClassDeclaration) stmt);
        } else if (stmt instanceof IfStatement) {
            compileIf((IfStatement) stmt);
        } else if (stmt instanceof CycleStatement) {
            compileCycle((CycleStatement) stmt);
        } else if (stmt instanceof TryStatement) {
            compileTry((TryStatement) stmt);
        } else if (stmt instanceof CatalogStatement) {
            compileCatalog((CatalogStatement) stmt);
        } else if (stmt instanceof SplitStatement) {
            compileSplit((SplitStatement) stmt);
        } else if (stmt instanceof PrintStatement) {
            final PrintStatement print = (PrintStatement) stmt;
            compileExpression(print.getExpression());
            chunk.write(print.isNewLine() ? OpCode.PRINT : OpCode.PRINT_NOLINE);
        } else if (stmt instanceof ReturnStatement) {
            compileExpression(((ReturnStatement) stmt).getValueExpr());
            chunk.write(OpCode.RETURN);
        } else if (stmt instanceof BreakStatement) {
            if (breakJumps.isEmpty()) throw new RuntimeException("Cannot use escape outside loop.");
            breakJumps.peek().add(emitJump(OpCode.JUMP));
        } else if (stmt instanceof ContinueStatement) {
            if (continuePoints.isEmpty()) throw new RuntimeException("Cannot use skip outside loop.");
            emitLoop(continuePoints.peek());
        } else if (stmt instanceof HoldStatement) {
            compileExpression(((HoldStatement) stmt).getTimeExpr());
            chunk.write(OpCode.HOLD);
        } else if (stmt instanceof PropertySetStatement) {
            final PropertySetStatement set = (PropertySetStatement) stmt;
            compileExpression(set.getObject());
            compileExpression(set.getValue());
            emitConstant(new Value(set.getPropertyName()));
            chunk.write(OpCode.SET_PROPERTY);
        } else if (stmt instanceof ArraySetStatement) {
            final ArraySetStatement set = (ArraySetStatement) stmt;
            compileExpression(set.getObject());
            compileExpression(set.getIndex());
            compileExpression(set.getValue());
            chunk.write(OpCode.SET_INDEX);
        } else if (stmt instanceof ExpressionStatement) {
            compileExpression(((ExpressionStatement) stmt).getExpression());
            chunk.write(OpCode.POP);
        }
    }

    private void compileExpression(final Expression expr) {
        if (expr instanceof LiteralExpression) {
            final Value val = ((LiteralExpression) expr).getValue();
            final Object raw = val.asJavaObject();
            if (raw == null) {
                chunk.write(OpCode.NULL);
            } else if (raw instanceof Boolean) {
                chunk.write((Boolean) raw ? OpCode.TRUE : OpCode.FALSE);
            } else {
                emitConstant(val);
            }
        } else if (expr instanceof VariableExpression) {
            emitConstant(new Value(((VariableExpression) expr).getName()));
            chunk.write(OpCode.GET_GLOBAL);
        } else if (expr instanceof UnaryExpression) {
            final UnaryExpression unary = (UnaryExpression) expr;
            compileExpression(unary.getRight());
            chunk.write(unary.getOperator().equals("!") ? OpCode.NOT : OpCode.NEGATE);
        } else if (expr instanceof BinaryExpression) {
            compileBinary((BinaryExpression) expr);
        } else if (expr instanceof LogicalExpression) {
            compileLogical((LogicalExpression) expr);
        } else if (expr instanceof CallExpression) {
            final CallExpression call = (CallExpression) expr;
            compileExpression(call.getCallee());
            for (final Expression arg : call.getArguments())
                compileExpression(arg);
            chunk.write(OpCode.CALL);
            chunk.write(call.getArguments().size());
        } else if (expr instanceof NewExpression) {
            final NewExpression n = (NewExpression) expr;
            emitConstant(new Value(n.getClassName()));
            chunk.write(OpCode.GET_GLOBAL);
            for (final Expression arg : n.getArguments())
                compileExpression(arg);
            chunk.write(OpCode.CALL);
            chunk.write(n.getArguments().size());
        } else if (expr instanceof GetExpression) {
            final GetExpression get = (GetExpression) expr;
            compileExpression(get.getObject());
            emitConstant(new Value(get.getName()));
            chunk.write(OpCode.GET_PROPERTY);
        } else if (expr instanceof ArrayAccessExpression) {
            final ArrayAccessExpression acc = (ArrayAccessExpression) expr;
            compileExpression(acc.getObject());
            compileExpression(acc.getIndex());
            chunk.write(OpCode.GET_INDEX);
        } else if (expr instanceof ArrayLiteralExpression) {
            final List<Expression> elements = ((ArrayLiteralExpression) expr).getElements();
            for (final Expression e : elements) compileExpression(e);
            chunk.write(OpCode.ARRAY);
            chunk.write(elements.size());
        }
    }

    private void compileFunction(final FunctionDeclaration func) {
        final int jump = emitJump(OpCode.JUMP);
        final int functionStart = chunk.code.size();

        for (final Statement bodyStmt : func.getBody())
            compileStatement(bodyStmt);

        if (chunk.code.isEmpty() || (chunk.code.get(chunk.code.size() - 1) & 0xFF) != OpCode.RETURN.ordinal()) {
            chunk.write(OpCode.NULL);
            chunk.write(OpCode.RETURN);
        }

        patchJump(jump);
        chunk.write(OpCode.CLOSURE);
        chunk.write((functionStart >> 8) & 0xFF);
        chunk.write(functionStart & 0xFF);

        final List<String> params = func.getParams();
        chunk.write(params.size());
        for (final String param : params) {
            chunk.write(chunk.addConstant(new Value(param)));
        }

        emitConstant(new Value(func.getName()));
        chunk.write(OpCode.DEFINE_GLOBAL);
    }

    private void compileClass(final ClassDeclaration decl) {
        emitConstant(new Value(decl.getName()));
        chunk.write(OpCode.CLASS);

        if (decl.getParentName() != null) {
            emitConstant(new Value(decl.getParentName()));
            chunk.write(OpCode.GET_GLOBAL);
            chunk.write(OpCode.INHERIT);
        }

        for (final Statement stmt : decl.getBody()) {
            if (stmt instanceof FunctionDeclaration) {
                final FunctionDeclaration func = (FunctionDeclaration) stmt;
                compileFunction(func);
                emitConstant(new Value(func.getName()));
                chunk.write(OpCode.METHOD);
            } else {
                compileStatement(stmt);
            }
        }
        chunk.write(OpCode.DEFINE_GLOBAL);
    }

    private void compileIf(final IfStatement stmt) {
        compileExpression(stmt.getCondition());
        final int falseJump = emitJump(OpCode.JUMP_IF_FALSE);
        chunk.write(OpCode.POP);

        for (final Statement s : stmt.getThenBranch()) compileStatement(s);

        final int endJump = emitJump(OpCode.JUMP);
        patchJump(falseJump);
        chunk.write(OpCode.POP);

        if (stmt.getElseBranch() != null) {
            for (final Statement s : stmt.getElseBranch()) compileStatement(s);
        }

        patchJump(endJump);
    }

    private void compileCycle(final CycleStatement stmt) {
        final List<Integer> currentBreakJumps = new ArrayList<>();
        breakJumps.push(currentBreakJumps);

        if (stmt.getConditionExpr() != null) {
            final int loopStart = chunk.code.size();
            continuePoints.push(loopStart);
            compileExpression(stmt.getConditionExpr());
            final int exitJump = emitJump(OpCode.JUMP_IF_FALSE);
            chunk.write(OpCode.POP);

            for (final Statement s : stmt.getBody()) compileStatement(s);

            emitLoop(loopStart);
            patchJump(exitJump);
            chunk.write(OpCode.POP);
            continuePoints.pop();
        } else if (stmt.getVariableName() != null) {
            compileExpression(stmt.getStartExpr());
            final String varName = stmt.getVariableName();
            emitConstant(new Value(varName));
            chunk.write(OpCode.DEFINE_GLOBAL);

            final int condStart = chunk.code.size();
            emitConstant(new Value(varName));
            chunk.write(OpCode.GET_GLOBAL);
            compileExpression(stmt.getEndExpr());
            emitConstant(new Value(1.0));
            chunk.write(OpCode.ADD);
            chunk.write(OpCode.LESS);
            final int exitJump = emitJump(OpCode.JUMP_IF_FALSE);
            chunk.write(OpCode.POP);

            final int bodyStart = chunk.code.size();
            continuePoints.push(bodyStart);

            for (final Statement s : stmt.getBody()) compileStatement(s);

            emitConstant(new Value(varName));
            chunk.write(OpCode.GET_GLOBAL);
            emitConstant(new Value(1.0));
            chunk.write(OpCode.ADD);
            emitConstant(new Value(varName));
            chunk.write(OpCode.DEFINE_GLOBAL);

            emitLoop(condStart);
            patchJump(exitJump);
            chunk.write(OpCode.POP);
            continuePoints.pop();
        }

        for (final int jump : breakJumps.pop()) patchJump(jump);
    }

    private void compileBinary(final BinaryExpression bin) {
        compileExpression(bin.getLeft());
        compileExpression(bin.getRight());
        switch (bin.getOperator()) {
            case "+": chunk.write(OpCode.ADD); break;
            case "-": chunk.write(OpCode.SUBTRACT); break;
            case "*": chunk.write(OpCode.MULTIPLY); break;
            case "/": chunk.write(OpCode.DIVIDE); break;
            case "%": chunk.write(OpCode.MODULO); break;
            case "==": chunk.write(OpCode.EQUAL); break;
            case "!=": chunk.write(OpCode.EQUAL); chunk.write(OpCode.NOT); break;
            case ">": chunk.write(OpCode.GREATER); break;
            case "<": chunk.write(OpCode.LESS); break;
        }
    }

    private void compileLogical(final LogicalExpression log) {
        if (log.getOperator().equals("||")) {
            compileExpression(log.getLeft());
            final int jump = emitJump(OpCode.JUMP_IF_FALSE);
            final int end = emitJump(OpCode.JUMP);
            patchJump(jump);
            chunk.write(OpCode.POP);
            compileExpression(log.getRight());
            patchJump(end);
        } else {
            compileExpression(log.getLeft());
            final int jump = emitJump(OpCode.JUMP_IF_FALSE);
            chunk.write(OpCode.POP);
            compileExpression(log.getRight());
            patchJump(jump);
        }
    }

    private void compileTry(final TryStatement stmt) {
        final int tryBegin = emitJump(OpCode.TRY_BEGIN);
        for (final Statement s : stmt.getTryBlock()) compileStatement(s);
        chunk.write(OpCode.TRY_END);
        final int catchJump = emitJump(OpCode.JUMP);

        patchJump(tryBegin);
        if (stmt.getCatchBlock() != null) {
            emitConstant(new Value(stmt.getErrorVariableName()));
            chunk.write(OpCode.DEFINE_GLOBAL);
            for (final Statement s : stmt.getCatchBlock()) compileStatement(s);
        }
        patchJump(catchJump);
    }

    private void compileCatalog(final CatalogStatement stmt) {
        for (final String entry : stmt.getEntries()) {
            emitConstant(new Value(entry));
        }
        chunk.write(OpCode.CATALOG);
        chunk.write(stmt.getEntries().size());
        emitConstant(new Value(stmt.getName()));
        chunk.write(OpCode.DEFINE_GLOBAL);
    }

    private void compileSplit(final SplitStatement stmt) {
        final int jump = emitJump(OpCode.JUMP);
        final int start = chunk.code.size();
        for (final Statement s : stmt.getBody()) compileStatement(s);
        chunk.write(OpCode.HALT);
        patchJump(jump);
        chunk.write(OpCode.SPLIT);
        chunk.write((start >> 8) & 0xFF);
        chunk.write(start & 0xFF);
    }

    private void emitConstant(final Value value) {
        chunk.write(OpCode.CONSTANT);
        chunk.write(chunk.addConstant(value));
    }

    private int emitJump(final OpCode op) {
        chunk.write(op);
        chunk.write(0xFF);
        chunk.write(0xFF);
        return chunk.code.size() - 2;
    }

    private void patchJump(final int offset) {
        final int jump = chunk.code.size();
        chunk.code.set(offset, (byte) ((jump >> 8) & 0xFF));
        chunk.code.set(offset + 1, (byte) (jump & 0xFF));
    }

    private void emitLoop(final int start) {
        chunk.write(OpCode.LOOP);
        chunk.write((start >> 8) & 0xFF);
        chunk.write(start & 0xFF);
    }
}