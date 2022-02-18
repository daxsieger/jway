package com.jway.repository;

import com.jway.domain.Assistito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Assistito entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssistitoRepository extends ReactiveCrudRepository<Assistito, Long>, AssistitoRepositoryInternal {
    Flux<Assistito> findAllBy(Pageable pageable);

    @Override
    <S extends Assistito> Mono<S> save(S entity);

    @Override
    Flux<Assistito> findAll();

    @Override
    Mono<Assistito> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AssistitoRepositoryInternal {
    <S extends Assistito> Mono<S> save(S entity);

    Flux<Assistito> findAllBy(Pageable pageable);

    Flux<Assistito> findAll();

    Mono<Assistito> findById(Long id);

    Flux<Assistito> findAllBy(Pageable pageable, Criteria criteria);
}
