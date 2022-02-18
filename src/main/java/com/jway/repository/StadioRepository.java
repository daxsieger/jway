package com.jway.repository;

import com.jway.domain.Stadio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Stadio entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StadioRepository extends ReactiveCrudRepository<Stadio, Long>, StadioRepositoryInternal {
    Flux<Stadio> findAllBy(Pageable pageable);

    @Query("SELECT * FROM stadio entity WHERE entity.processo_id = :id")
    Flux<Stadio> findByProcesso(Long id);

    @Query("SELECT * FROM stadio entity WHERE entity.processo_id IS NULL")
    Flux<Stadio> findAllWhereProcessoIsNull();

    @Override
    <S extends Stadio> Mono<S> save(S entity);

    @Override
    Flux<Stadio> findAll();

    @Override
    Mono<Stadio> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface StadioRepositoryInternal {
    <S extends Stadio> Mono<S> save(S entity);

    Flux<Stadio> findAllBy(Pageable pageable);

    Flux<Stadio> findAll();

    Mono<Stadio> findById(Long id);

    Flux<Stadio> findAllBy(Pageable pageable, Criteria criteria);
}
