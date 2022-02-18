package com.jway.service;

import com.jway.domain.Assistito;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Assistito}.
 */
public interface AssistitoService {
    /**
     * Save a assistito.
     *
     * @param assistito the entity to save.
     * @return the persisted entity.
     */
    Mono<Assistito> save(Assistito assistito);

    /**
     * Partially updates a assistito.
     *
     * @param assistito the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Assistito> partialUpdate(Assistito assistito);

    /**
     * Get all the assistitos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Assistito> findAll(Pageable pageable);

    /**
     * Returns the number of assistitos available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" assistito.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Assistito> findOne(Long id);

    /**
     * Delete the "id" assistito.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
