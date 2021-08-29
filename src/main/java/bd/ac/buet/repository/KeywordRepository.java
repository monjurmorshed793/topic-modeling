package bd.ac.buet.repository;

import bd.ac.buet.domain.Keyword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Keyword entity.
 */
@SuppressWarnings("unused")
@Repository
public interface KeywordRepository extends R2dbcRepository<Keyword, Long>, KeywordRepositoryInternal {
    Flux<Keyword> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Keyword> findAll();

    @Override
    Mono<Keyword> findById(Long id);

    @Override
    <S extends Keyword> Mono<S> save(S entity);
}

interface KeywordRepositoryInternal {
    <S extends Keyword> Mono<S> insert(S entity);
    <S extends Keyword> Mono<S> save(S entity);
    Mono<Integer> update(Keyword entity);

    Flux<Keyword> findAll();
    Mono<Keyword> findById(Long id);
    Flux<Keyword> findAllBy(Pageable pageable);
    Flux<Keyword> findAllBy(Pageable pageable, Criteria criteria);
}
