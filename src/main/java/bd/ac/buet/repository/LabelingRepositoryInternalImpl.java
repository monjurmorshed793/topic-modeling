package bd.ac.buet.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import bd.ac.buet.domain.Labeling;
import bd.ac.buet.domain.enumeration.Framework;
import bd.ac.buet.repository.rowmapper.LabelingRowMapper;
import bd.ac.buet.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Labeling entity.
 */
@SuppressWarnings("unused")
class LabelingRepositoryInternalImpl implements LabelingRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final LabelingRowMapper labelingMapper;

    private static final Table entityTable = Table.aliased("labeling", EntityManager.ENTITY_ALIAS);

    public LabelingRepositoryInternalImpl(R2dbcEntityTemplate template, EntityManager entityManager, LabelingRowMapper labelingMapper) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.labelingMapper = labelingMapper;
    }

    @Override
    public Flux<Labeling> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Labeling> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Labeling> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = LabelingSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, Labeling.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Labeling> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Labeling> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Labeling process(Row row, RowMetadata metadata) {
        Labeling entity = labelingMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Labeling> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Labeling> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Labeling with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Labeling entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class LabelingSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("user_name", table, columnPrefix + "_user_name"));
        columns.add(Column.aliased("framework", table, columnPrefix + "_framework"));
        columns.add(Column.aliased("document_no", table, columnPrefix + "_document_no"));
        columns.add(Column.aliased("dominant_topic", table, columnPrefix + "_dominant_topic"));
        columns.add(Column.aliased("topic_perc_contrib", table, columnPrefix + "_topic_perc_contrib"));
        columns.add(Column.aliased("keywords", table, columnPrefix + "_keywords"));
        columns.add(Column.aliased("title", table, columnPrefix + "_title"));
        columns.add(Column.aliased("texts", table, columnPrefix + "_texts"));
        columns.add(Column.aliased("answer", table, columnPrefix + "_answer"));
        columns.add(Column.aliased("label", table, columnPrefix + "_label"));
        columns.add(Column.aliased("reason", table, columnPrefix + "_reason"));

        return columns;
    }
}
