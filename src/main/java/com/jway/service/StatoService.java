package com.jway.service;

import com.jway.domain.Stato;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Stato}.
 */
public interface StatoService {
    /**
     * Save a stato.
     *
     * @param stato the entity to save.
     * @return the persisted entity.
     */
    Mono<Stato> save(Stato stato);

    /**
     * Partially updates a stato.
     *
     * @param stato the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Stato> partialUpdate(Stato stato);

    /**
     * Get all the statoes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Stato> findAll(Pageable pageable);

    /**
     * Returns the number of statoes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" stato.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Stato> findOne(Long id);

    /**
     * Delete the "id" stato.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
