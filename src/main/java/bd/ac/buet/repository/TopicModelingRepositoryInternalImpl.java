package bd.ac.buet.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import bd.ac.buet.domain.TopicModeling;
import bd.ac.buet.repository.rowmapper.KeywordRowMapper;
import bd.ac.buet.repository.rowmapper.TopicModelingRowMapper;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the TopicModeling entity.
 */
@SuppressWarnings("unused")
class TopicModelingRepositoryInternalImpl implements TopicModelingRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final KeywordRowMapper keywordMapper;
    private final TopicModelingRowMapper topicmodelingMapper;

    private static final Table entityTable = Table.aliased("topic_modeling", EntityManager.ENTITY_ALIAS);
    private static final Table keywordTable = Table.aliased("keyword", "keyword");

    public TopicModelingRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        KeywordRowMapper keywordMapper,
        TopicModelingRowMapper topicmodelingMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.keywordMapper = keywordMapper;
        this.topicmodelingMapper = topicmodelingMapper;
    }

    @Override
    public Flux<TopicModeling> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<TopicModeling> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<TopicModeling> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = TopicModelingSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(KeywordSqlHelper.getColumns(keywordTable, "keyword"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(keywordTable)
            .on(Column.create("keyword_id", entityTable))
            .equals(Column.create("id", keywordTable));

        String select = entityManager.createSelect(selectFrom, TopicModeling.class, pageable, criteria);
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
    public Flux<TopicModeling> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<TopicModeling> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private TopicModeling process(Row row, RowMetadata metadata) {
        TopicModeling entity = topicmodelingMapper.apply(row, "e");
        entity.setKeyword(keywordMapper.apply(row, "keyword"));
        return entity;
    }

    @Override
    public <S extends TopicModeling> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends TopicModeling> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update TopicModeling with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(TopicModeling entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class TopicModelingSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("user_name", table, columnPrefix + "_user_name"));
        columns.add(Column.aliased("category", table, columnPrefix + "_category"));
        columns.add(Column.aliased("sub_category", table, columnPrefix + "_sub_category"));
        columns.add(Column.aliased("topic", table, columnPrefix + "_topic"));
        columns.add(Column.aliased("sub_topic", table, columnPrefix + "_sub_topic"));

        columns.add(Column.aliased("keyword_id", table, columnPrefix + "_keyword_id"));
        return columns;
    }
}
