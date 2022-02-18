package com.jway.service;

import com.jway.domain.Processo;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Processo}.
 */
public interface ProcessoService {
    /**
     * Save a processo.
     *
     * @param processo the entity to save.
     * @return the persisted entity.
     */
    Mono<Processo> save(Processo processo);

    /**
     * Partially updates a processo.
     *
     * @param processo the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Processo> partialUpdate(Processo processo);

    /**
     * Get all the processos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Processo> findAll(Pageable pageable);

    /**
     * Returns the number of processos available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" processo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Processo> findOne(Long id);

    /**
     * Delete the "id" processo.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
