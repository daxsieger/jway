package com.jway.repository;

import com.jway.domain.Stato;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Stato entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StatoRepository extends ReactiveCrudRepository<Stato, Long>, StatoRepositoryInternal {
    Flux<Stato> findAllBy(Pageable pageable);

    @Query("SELECT * FROM stato entity WHERE entity.stadio_id = :id")
    Flux<Stato> findByStadio(Long id);

    @Query("SELECT * FROM stato entity WHERE entity.stadio_id IS NULL")
    Flux<Stato> findAllWhereStadioIsNull();

    @Override
    <S extends Stato> Mono<S> save(S entity);

    @Override
    Flux<Stato> findAll();

    @Override
    Mono<Stato> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface StatoRepositoryInternal {
    <S extends Stato> Mono<S> save(S entity);

    Flux<Stato> findAllBy(Pageable pageable);

    Flux<Stato> findAll();

    Mono<Stato> findById(Long id);

    Flux<Stato> findAllBy(Pageable pageable, Criteria criteria);
}
