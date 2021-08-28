package bd.ac.buet.repository;

import bd.ac.buet.domain.Labeling;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Labeling entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LabelingRepository extends R2dbcRepository<Labeling, Long>, LabelingRepositoryInternal {
    Flux<Labeling> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Labeling> findAll();

    @Override
    Mono<Labeling> findById(Long id);

    @Override
    <S extends Labeling> Mono<S> save(S entity);
}

interface LabelingRepositoryInternal {
    <S extends Labeling> Mono<S> insert(S entity);
    <S extends Labeling> Mono<S> save(S entity);
    Mono<Integer> update(Labeling entity);

    Flux<Labeling> findAll();
    Mono<Labeling> findById(Long id);
    Flux<Labeling> findAllBy(Pageable pageable);
    Flux<Labeling> findAllBy(Pageable pageable, Criteria criteria);
}
