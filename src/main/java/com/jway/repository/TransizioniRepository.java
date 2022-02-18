package com.jway.repository;

import com.jway.domain.Transizioni;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Transizioni entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransizioniRepository extends ReactiveCrudRepository<Transizioni, Long>, TransizioniRepositoryInternal {
    Flux<Transizioni> findAllBy(Pageable pageable);

    @Query("SELECT * FROM transizioni entity WHERE entity.processo_id = :id")
    Flux<Transizioni> findByProcesso(Long id);

    @Query("SELECT * FROM transizioni entity WHERE entity.processo_id IS NULL")
    Flux<Transizioni> findAllWhereProcessoIsNull();

    @Query("SELECT * FROM transizioni entity WHERE entity.stadio_iniziale_id = :id")
    Flux<Transizioni> findByStadioIniziale(Long id);

    @Query("SELECT * FROM transizioni entity WHERE entity.stadio_iniziale_id IS NULL")
    Flux<Transizioni> findAllWhereStadioInizialeIsNull();

    @Query("SELECT * FROM transizioni entity WHERE entity.stadio_finale_id = :id")
    Flux<Transizioni> findByStadioFinale(Long id);

    @Query("SELECT * FROM transizioni entity WHERE entity.stadio_finale_id IS NULL")
    Flux<Transizioni> findAllWhereStadioFinaleIsNull();

    @Override
    <S extends Transizioni> Mono<S> save(S entity);

    @Override
    Flux<Transizioni> findAll();

    @Override
    Mono<Transizioni> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TransizioniRepositoryInternal {
    <S extends Transizioni> Mono<S> save(S entity);

    Flux<Transizioni> findAllBy(Pageable pageable);

    Flux<Transizioni> findAll();

    Mono<Transizioni> findById(Long id);

    Flux<Transizioni> findAllBy(Pageable pageable, Criteria criteria);
}
