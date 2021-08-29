package bd.ac.buet.repository.rowmapper;

import bd.ac.buet.domain.TopicModeling;
import bd.ac.buet.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TopicModeling}, with proper type conversions.
 */
@Service
public class TopicModelingRowMapper implements BiFunction<Row, String, TopicModeling> {

    private final ColumnConverter converter;

    public TopicModelingRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TopicModeling} stored in the database.
     */
    @Override
    public TopicModeling apply(Row row, String prefix) {
        TopicModeling entity = new TopicModeling();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUserName(converter.fromRow(row, prefix + "_user_name", String.class));
        entity.setCategory(converter.fromRow(row, prefix + "_category", String.class));
        entity.setSubCategory(converter.fromRow(row, prefix + "_sub_category", String.class));
        entity.setTopic(converter.fromRow(row, prefix + "_topic", String.class));
        entity.setSubTopic(converter.fromRow(row, prefix + "_sub_topic", String.class));
        entity.setKeywordId(converter.fromRow(row, prefix + "_keyword_id", Long.class));
        return entity;
    }
}
