package me.abdelaziz.parser;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.expression.*;
import me.abdelaziz.ast.statement.CallExpression;
import me.abdelaziz.ast.statement.ExpressionStatement;
import me.abdelaziz.token.Token;
import me.abdelaziz.token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Parser {

    private static final Map<String, StatementHandler> handlers = new HashMap<>();

    public static void register(final String keyword, final StatementHandler handler) {
        handlers.put(keyword, handler);
    }

    private int pos;
    private final List<Token> tokens;

    public Parser(final List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    public List<Statement> parse() {
        final List<Statement> statements = new ArrayList<>();
        while (!match(TokenType.EOF)) {
            final Statement stmt = statement();
            if (stmt != null)
                statements.add(stmt);
        }

        return statements;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public Statement statement() {
        while (match(TokenType.NEWLINE))
            ;

        if (check(TokenType.EOF))
            return null;

        final Token current = peek();

        if (current.type == TokenType.IDENTIFIER && handlers.containsKey(current.text)) {
            advance();
            return handlers.get(current.text).parse(this);
        }

        return new ExpressionStatement(expression());
    }

    public Expression expression() {
        return logicalOr();
    }

    private Expression logicalOr() {
        Expression expr = logicalAnd();
        while (match(TokenType.OR)) {
            final String op = previous().text;
            final Expression right = logicalAnd();
            expr = new LogicalExpression(expr, op, right);
        }
        return expr;
    }

    private Expression logicalAnd() {
        Expression expr = additive();
        while (match(TokenType.AND)) {
            final String op = previous().text;
            final Expression right = additive();
            expr = new LogicalExpression(expr, op, right);
        }
        return expr;
    }

    public Token consume(final TokenType type, final String err) {
        if (check(type))
            return advance();
        throw new RuntimeException(err);
    }

    public void consume(final String text, final String err) {
        if (pos < tokens.size() && tokens.get(pos).text.equals(text)) {
            advance();
            return;
        }
        throw new RuntimeException(err);
    }

    public boolean check(final String text) {
        if (pos >= tokens.size())
            return false;
        return tokens.get(pos).text.equals(text);
    }

    public boolean match(final TokenType... types) {
        for (final TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    public boolean check(final TokenType type) {
        if (pos >= tokens.size())
            return false;
        return tokens.get(pos).type == type;
    }

    private Expression additive() {
        Expression expr = multiplicative();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            final String op = previous().text;
            final Expression right = multiplicative();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }

    private Expression multiplicative() {
        Expression expr = unary();
        while (match(TokenType.STAR, TokenType.SLASH, TokenType.MOD, TokenType.GT, TokenType.LT, TokenType.EQ)) {
            final String op = previous().text;
            final Expression right = unary();
            expr = new BinaryExpression(expr, op, right);
        }

        return expr;
    }

    private Expression unary() {
        return match(TokenType.BANG, TokenType.MINUS)
                ? new UnaryExpression(previous().text, unary())
                : primary();
    }

    private Expression primary() {
        if (check("nothing")) {
            advance();
            return new LiteralExpression(null);
        }

        if (match(TokenType.NUMBER))
            return new LiteralExpression(Double.parseDouble(previous().text));

        if (match(TokenType.STRING))
            return new LiteralExpression(previous().text);

        if (check("true")) {
            advance();
            return new LiteralExpression(true);
        }

        if (check("false")) {
            advance();
            return new LiteralExpression(false);
        }

        if (check("new")) {
            advance();
            final String className = consume(TokenType.IDENTIFIER, "Expected class name").text;

            final List<Expression> args = new ArrayList<>();
            if (match(TokenType.LPAREN)) {
                if (!check(TokenType.RPAREN)) {
                    do {
                        args.add(expression());
                    } while (match(TokenType.COMMA));
                }
                consume(TokenType.RPAREN, "Expected ')' after constructor arguments.");
            }

            return new NewExpression(className, args);
        }

        if (match(TokenType.LBRACKET)) {
            final List<Expression> elements = new ArrayList<>();
            if (!check(TokenType.RBRACKET)) {
                do {
                    elements.add(expression());
                } while (match(TokenType.COMMA));
            }

            consume(TokenType.RBRACKET, "Expected ']' after list.");
            return new ArrayLiteralExpression(elements);
        }

        if (match(TokenType.LPAREN)) {
            final Expression expr = expression();
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return expr;
        }

        if (match(TokenType.IDENTIFIER)) {
            Expression expr = new VariableExpression(previous().text);

            while (true) {
                if (match(TokenType.DOT)) {
                    final String prop = consume(TokenType.IDENTIFIER, "Expect property name").text;
                    expr = new GetExpression(expr, prop);
                } else if (match(TokenType.LPAREN)) {
                    expr = finishCall(expr);
                } else if (match(TokenType.LBRACKET)) {
                    final Expression index = expression();
                    consume(TokenType.RBRACKET, "Expected ']' after index.");
                    expr = new ArrayAccessExpression(expr, index);
                } else {
                    break;
                }
            }
            return expr;
        }

        throw new RuntimeException("Expected expression. Found: " + peek().text);
    }

    private Expression finishCall(final Expression callee) {
        final List<Expression> arguments = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RPAREN, "Expect ')' after arguments.");
        return new CallExpression(callee, arguments);
    }

    private Token advance() {
        if (pos < tokens.size())
            pos++;
        return tokens.get(pos - 1);
    }

    private Token previous() {
        return tokens.get(pos - 1);
    }

    private Token peek() {
        return tokens.get(pos);
    }
}