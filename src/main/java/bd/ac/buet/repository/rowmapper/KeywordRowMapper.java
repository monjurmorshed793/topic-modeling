package bd.ac.buet.repository.rowmapper;

import bd.ac.buet.domain.Keyword;
import bd.ac.buet.domain.enumeration.Framework;
import bd.ac.buet.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Keyword}, with proper type conversions.
 */
@Service
public class KeywordRowMapper implements BiFunction<Row, String, Keyword> {

    private final ColumnConverter converter;

    public KeywordRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Keyword} stored in the database.
     */
    @Override
    public Keyword apply(Row row, String prefix) {
        Keyword entity = new Keyword();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFramework(converter.fromRow(row, prefix + "_framework", Framework.class));
        entity.setTopicNumber(converter.fromRow(row, prefix + "_topic_number", Integer.class));
        entity.setKeywords(converter.fromRow(row, prefix + "_keywords", String.class));
        entity.setNumberOfPosts(converter.fromRow(row, prefix + "_number_of_posts", Integer.class));
        return entity;
    }
}
