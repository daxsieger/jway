package com.jway.service;

import com.jway.domain.TipoEvento;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link TipoEvento}.
 */
public interface TipoEventoService {
    /**
     * Save a tipoEvento.
     *
     * @param tipoEvento the entity to save.
     * @return the persisted entity.
     */
    Mono<TipoEvento> save(TipoEvento tipoEvento);

    /**
     * Partially updates a tipoEvento.
     *
     * @param tipoEvento the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TipoEvento> partialUpdate(TipoEvento tipoEvento);

    /**
     * Get all the tipoEventos.
     *
     * @return the list of entities.
     */
    Flux<TipoEvento> findAll();

    /**
     * Returns the number of tipoEventos available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" tipoEvento.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TipoEvento> findOne(Long id);

    /**
     * Delete the "id" tipoEvento.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
