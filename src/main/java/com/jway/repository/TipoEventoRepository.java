package com.jway.repository;

import com.jway.domain.TipoEvento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the TipoEvento entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TipoEventoRepository extends ReactiveCrudRepository<TipoEvento, Long>, TipoEventoRepositoryInternal {
    @Override
    <S extends TipoEvento> Mono<S> save(S entity);

    @Override
    Flux<TipoEvento> findAll();

    @Override
    Mono<TipoEvento> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TipoEventoRepositoryInternal {
    <S extends TipoEvento> Mono<S> save(S entity);

    Flux<TipoEvento> findAllBy(Pageable pageable);

    Flux<TipoEvento> findAll();

    Mono<TipoEvento> findById(Long id);

    Flux<TipoEvento> findAllBy(Pageable pageable, Criteria criteria);
}
