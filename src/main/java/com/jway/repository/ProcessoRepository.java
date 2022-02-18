package com.jway.repository;

import com.jway.domain.Processo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Processo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProcessoRepository extends ReactiveCrudRepository<Processo, Long>, ProcessoRepositoryInternal {
    Flux<Processo> findAllBy(Pageable pageable);

    @Override
    <S extends Processo> Mono<S> save(S entity);

    @Override
    Flux<Processo> findAll();

    @Override
    Mono<Processo> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProcessoRepositoryInternal {
    <S extends Processo> Mono<S> save(S entity);

    Flux<Processo> findAllBy(Pageable pageable);

    Flux<Processo> findAll();

    Mono<Processo> findById(Long id);

    Flux<Processo> findAllBy(Pageable pageable, Criteria criteria);
}
