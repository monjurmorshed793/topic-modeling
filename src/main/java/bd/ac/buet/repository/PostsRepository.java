package bd.ac.buet.repository;

import bd.ac.buet.domain.Posts;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Posts entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostsRepository extends R2dbcRepository<Posts, Long>, PostsRepositoryInternal {
    Flux<Posts> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Posts> findAll();

    @Override
    Mono<Posts> findById(Long id);

    @Override
    <S extends Posts> Mono<S> save(S entity);
}

interface PostsRepositoryInternal {
    <S extends Posts> Mono<S> insert(S entity);
    <S extends Posts> Mono<S> save(S entity);
    Mono<Integer> update(Posts entity);

    Flux<Posts> findAll();
    Mono<Posts> findById(Long id);
    Flux<Posts> findAllBy(Pageable pageable);
    Flux<Posts> findAllBy(Pageable pageable, Criteria criteria);
}
