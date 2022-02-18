package com.jway.repository;

import com.jway.domain.Produttore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Produttore entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProduttoreRepository extends ReactiveCrudRepository<Produttore, Long>, ProduttoreRepositoryInternal {
    Flux<Produttore> findAllBy(Pageable pageable);

    @Override
    <S extends Produttore> Mono<S> save(S entity);

    @Override
    Flux<Produttore> findAll();

    @Override
    Mono<Produttore> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProduttoreRepositoryInternal {
    <S extends Produttore> Mono<S> save(S entity);

    Flux<Produttore> findAllBy(Pageable pageable);

    Flux<Produttore> findAll();

    Mono<Produttore> findById(Long id);

    Flux<Produttore> findAllBy(Pageable pageable, Criteria criteria);
}
