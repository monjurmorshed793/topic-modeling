package bd.ac.buet.repository;

import bd.ac.buet.domain.TopicModeling;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the TopicModeling entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TopicModelingRepository extends R2dbcRepository<TopicModeling, Long>, TopicModelingRepositoryInternal {
    Flux<TopicModeling> findAllBy(Pageable pageable);

    @Query("SELECT * FROM topic_modeling entity WHERE entity.keyword_id = :id")
    Flux<TopicModeling> findByKeyword(Long id);

    @Query("SELECT * FROM topic_modeling entity WHERE entity.keyword_id IS NULL")
    Flux<TopicModeling> findAllWhereKeywordIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<TopicModeling> findAll();

    @Override
    Mono<TopicModeling> findById(Long id);

    @Override
    <S extends TopicModeling> Mono<S> save(S entity);
}

interface TopicModelingRepositoryInternal {
    <S extends TopicModeling> Mono<S> insert(S entity);
    <S extends TopicModeling> Mono<S> save(S entity);
    Mono<Integer> update(TopicModeling entity);

    Flux<TopicModeling> findAll();
    Mono<TopicModeling> findById(Long id);
    Flux<TopicModeling> findAllBy(Pageable pageable);
    Flux<TopicModeling> findAllBy(Pageable pageable, Criteria criteria);
}
