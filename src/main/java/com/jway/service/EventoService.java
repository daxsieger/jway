package com.jway.service;

import com.jway.domain.Evento;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Evento}.
 */
public interface EventoService {
    /**
     * Save a evento.
     *
     * @param evento the entity to save.
     * @return the persisted entity.
     */
    Mono<Evento> save(Evento evento);

    /**
     * Partially updates a evento.
     *
     * @param evento the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Evento> partialUpdate(Evento evento);

    /**
     * Get all the eventos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Evento> findAll(Pageable pageable);

    /**
     * Get all the eventos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Evento> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of eventos available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" evento.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Evento> findOne(Long id);

    /**
     * Delete the "id" evento.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
