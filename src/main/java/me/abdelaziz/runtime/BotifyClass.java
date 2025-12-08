package me.abdelaziz.runtime;

import me.abdelaziz.ast.Statement;
import java.util.List;

public final class BotifyClass {

    private final String name;
    private final String parentName;
    private final Environment closure;
    private final List<Statement> body;

    public BotifyClass(final String name, final String parentName, final List<Statement> body, final Environment closure) {
        this.name = name;
        this.parentName = parentName;
        this.body = body;
        this.closure = closure;
    }

    public String getParentName() {
        return parentName;
    }

    public Environment getClosure() {
        return closure;
    }

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "<blueprint " + name + ">";
    }
}