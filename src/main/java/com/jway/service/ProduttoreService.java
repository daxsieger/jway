package com.jway.service;

import com.jway.domain.Produttore;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Produttore}.
 */
public interface ProduttoreService {
    /**
     * Save a produttore.
     *
     * @param produttore the entity to save.
     * @return the persisted entity.
     */
    Mono<Produttore> save(Produttore produttore);

    /**
     * Partially updates a produttore.
     *
     * @param produttore the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Produttore> partialUpdate(Produttore produttore);

    /**
     * Get all the produttores.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Produttore> findAll(Pageable pageable);

    /**
     * Returns the number of produttores available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" produttore.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Produttore> findOne(Long id);

    /**
     * Delete the "id" produttore.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
