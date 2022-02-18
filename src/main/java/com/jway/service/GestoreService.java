package com.jway.service;

import com.jway.domain.Gestore;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Gestore}.
 */
public interface GestoreService {
    /**
     * Save a gestore.
     *
     * @param gestore the entity to save.
     * @return the persisted entity.
     */
    Mono<Gestore> save(Gestore gestore);

    /**
     * Partially updates a gestore.
     *
     * @param gestore the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Gestore> partialUpdate(Gestore gestore);

    /**
     * Get all the gestores.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Gestore> findAll(Pageable pageable);

    /**
     * Returns the number of gestores available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" gestore.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Gestore> findOne(Long id);

    /**
     * Delete the "id" gestore.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
