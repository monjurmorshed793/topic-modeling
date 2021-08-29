package bd.ac.buet.repository.rowmapper;

import bd.ac.buet.domain.Labeling;
import bd.ac.buet.domain.enumeration.Framework;
import bd.ac.buet.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Labeling}, with proper type conversions.
 */
@Service
public class LabelingRowMapper implements BiFunction<Row, String, Labeling> {

    private final ColumnConverter converter;

    public LabelingRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Labeling} stored in the database.
     */
    @Override
    public Labeling apply(Row row, String prefix) {
        Labeling entity = new Labeling();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUserName(converter.fromRow(row, prefix + "_user_name", String.class));
        entity.setFramework(converter.fromRow(row, prefix + "_framework", Framework.class));
        entity.setDocumentNo(converter.fromRow(row, prefix + "_document_no", Integer.class));
        entity.setDominantTopic(converter.fromRow(row, prefix + "_dominant_topic", Integer.class));
        entity.setTopicPercContrib(converter.fromRow(row, prefix + "_topic_perc_contrib", Double.class));
        entity.setKeywords(converter.fromRow(row, prefix + "_keywords", String.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setTexts(converter.fromRow(row, prefix + "_texts", String.class));
        entity.setAnswer(converter.fromRow(row, prefix + "_answer", String.class));
        entity.setLabel(converter.fromRow(row, prefix + "_label", String.class));
        entity.setReason(converter.fromRow(row, prefix + "_reason", String.class));
        return entity;
    }
}
