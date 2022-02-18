package com.jway.repository;

import com.jway.domain.Evento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Evento entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventoRepository extends ReactiveCrudRepository<Evento, Long>, EventoRepositoryInternal {
    Flux<Evento> findAllBy(Pageable pageable);

    @Override
    Mono<Evento> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Evento> findAllWithEagerRelationships();

    @Override
    Flux<Evento> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM evento entity WHERE entity.assistito_id = :id")
    Flux<Evento> findByAssistito(Long id);

    @Query("SELECT * FROM evento entity WHERE entity.assistito_id IS NULL")
    Flux<Evento> findAllWhereAssistitoIsNull();

    @Query("SELECT * FROM evento entity WHERE entity.tipo_id = :id")
    Flux<Evento> findByTipo(Long id);

    @Query("SELECT * FROM evento entity WHERE entity.tipo_id IS NULL")
    Flux<Evento> findAllWhereTipoIsNull();

    @Query("SELECT * FROM evento entity WHERE entity.gestore_id = :id")
    Flux<Evento> findByGestore(Long id);

    @Query("SELECT * FROM evento entity WHERE entity.gestore_id IS NULL")
    Flux<Evento> findAllWhereGestoreIsNull();

    @Query("SELECT * FROM evento entity WHERE entity.origine_id = :id")
    Flux<Evento> findByOrigine(Long id);

    @Query("SELECT * FROM evento entity WHERE entity.origine_id IS NULL")
    Flux<Evento> findAllWhereOrigineIsNull();

    @Query(
        "SELECT entity.* FROM evento entity JOIN rel_evento__stati joinTable ON entity.id = joinTable.evento_id WHERE joinTable.stati_id = :id"
    )
    Flux<Evento> findByStati(Long id);

    @Override
    <S extends Evento> Mono<S> save(S entity);

    @Override
    Flux<Evento> findAll();

    @Override
    Mono<Evento> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EventoRepositoryInternal {
    <S extends Evento> Mono<S> save(S entity);

    Flux<Evento> findAllBy(Pageable pageable);

    Flux<Evento> findAll();

    Mono<Evento> findById(Long id);

    Flux<Evento> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Evento> findOneWithEagerRelationships(Long id);

    Flux<Evento> findAllWithEagerRelationships();

    Flux<Evento> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
