package com.jway.service;

import com.jway.domain.Transizioni;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Transizioni}.
 */
public interface TransizioniService {
    /**
     * Save a transizioni.
     *
     * @param transizioni the entity to save.
     * @return the persisted entity.
     */
    Mono<Transizioni> save(Transizioni transizioni);

    /**
     * Partially updates a transizioni.
     *
     * @param transizioni the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Transizioni> partialUpdate(Transizioni transizioni);

    /**
     * Get all the transizionis.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Transizioni> findAll(Pageable pageable);

    /**
     * Returns the number of transizionis available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" transizioni.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Transizioni> findOne(Long id);

    /**
     * Delete the "id" transizioni.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
