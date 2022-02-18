package com.jway.service;

import com.jway.domain.Stadio;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Stadio}.
 */
public interface StadioService {
    /**
     * Save a stadio.
     *
     * @param stadio the entity to save.
     * @return the persisted entity.
     */
    Mono<Stadio> save(Stadio stadio);

    /**
     * Partially updates a stadio.
     *
     * @param stadio the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Stadio> partialUpdate(Stadio stadio);

    /**
     * Get all the stadios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Stadio> findAll(Pageable pageable);

    /**
     * Returns the number of stadios available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" stadio.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Stadio> findOne(Long id);

    /**
     * Delete the "id" stadio.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
