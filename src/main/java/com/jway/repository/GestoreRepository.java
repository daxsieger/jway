package com.jway.repository;

import com.jway.domain.Gestore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Gestore entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GestoreRepository extends ReactiveCrudRepository<Gestore, Long>, GestoreRepositoryInternal {
    Flux<Gestore> findAllBy(Pageable pageable);

    @Override
    <S extends Gestore> Mono<S> save(S entity);

    @Override
    Flux<Gestore> findAll();

    @Override
    Mono<Gestore> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface GestoreRepositoryInternal {
    <S extends Gestore> Mono<S> save(S entity);

    Flux<Gestore> findAllBy(Pageable pageable);

    Flux<Gestore> findAll();

    Mono<Gestore> findById(Long id);

    Flux<Gestore> findAllBy(Pageable pageable, Criteria criteria);
}
