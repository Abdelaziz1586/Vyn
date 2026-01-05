package me.abdelaziz.feature;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.ast.statement.CatalogStatement;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public final class CatalogHandler implements StatementHandler {

    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public Statement parse(final Parser parser) {
        final String name = parser.consume(TokenType.IDENTIFIER, "Expected catalog name").text;

        parser.consume("do", "Expected 'do' after catalog name");

        final List<String> entries = new ArrayList<>();
        while (!parser.check("end") && !parser.match(TokenType.EOF)) {
            while (parser.match(TokenType.NEWLINE));

            if (parser.check("end"))
                break;

            entries.add(parser.consume(TokenType.IDENTIFIER, "Expected catalog entry").text);
            parser.match(TokenType.COMMA);
        }

        parser.consume("end", "Expected 'end'");
        return new CatalogStatement(name, entries);
    }
}
