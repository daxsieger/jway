package com.jway.web.rest;

import com.jway.domain.TipoEvento;
import com.jway.repository.TipoEventoRepository;
import com.jway.service.TipoEventoService;
import com.jway.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.jway.domain.TipoEvento}.
 */
@RestController
@RequestMapping("/api")
public class TipoEventoResource {

    private final Logger log = LoggerFactory.getLogger(TipoEventoResource.class);

    private static final String ENTITY_NAME = "tipoEvento";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TipoEventoService tipoEventoService;

    private final TipoEventoRepository tipoEventoRepository;

    public TipoEventoResource(TipoEventoService tipoEventoService, TipoEventoRepository tipoEventoRepository) {
        this.tipoEventoService = tipoEventoService;
        this.tipoEventoRepository = tipoEventoRepository;
    }

    /**
     * {@code POST  /tipo-eventos} : Create a new tipoEvento.
     *
     * @param tipoEvento the tipoEvento to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tipoEvento, or with status {@code 400 (Bad Request)} if the tipoEvento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tipo-eventos")
    public Mono<ResponseEntity<TipoEvento>> createTipoEvento(@RequestBody TipoEvento tipoEvento) throws URISyntaxException {
        log.debug("REST request to save TipoEvento : {}", tipoEvento);
        if (tipoEvento.getId() != null) {
            throw new BadRequestAlertException("A new tipoEvento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return tipoEventoService
            .save(tipoEvento)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/tipo-eventos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tipo-eventos/:id} : Updates an existing tipoEvento.
     *
     * @param id the id of the tipoEvento to save.
     * @param tipoEvento the tipoEvento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tipoEvento,
     * or with status {@code 400 (Bad Request)} if the tipoEvento is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tipoEvento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tipo-eventos/{id}")
    public Mono<ResponseEntity<TipoEvento>> updateTipoEvento(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TipoEvento tipoEvento
    ) throws URISyntaxException {
        log.debug("REST request to update TipoEvento : {}, {}", id, tipoEvento);
        if (tipoEvento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tipoEvento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tipoEventoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tipoEventoService
                    .save(tipoEvento)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /tipo-eventos/:id} : Partial updates given fields of an existing tipoEvento, field will ignore if it is null
     *
     * @param id the id of the tipoEvento to save.
     * @param tipoEvento the tipoEvento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tipoEvento,
     * or with status {@code 400 (Bad Request)} if the tipoEvento is not valid,
     * or with status {@code 404 (Not Found)} if the tipoEvento is not found,
     * or with status {@code 500 (Internal Server Error)} if the tipoEvento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tipo-eventos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TipoEvento>> partialUpdateTipoEvento(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TipoEvento tipoEvento
    ) throws URISyntaxException {
        log.debug("REST request to partial update TipoEvento partially : {}, {}", id, tipoEvento);
        if (tipoEvento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tipoEvento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tipoEventoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TipoEvento> result = tipoEventoService.partialUpdate(tipoEvento);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /tipo-eventos} : get all the tipoEventos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tipoEventos in body.
     */
    @GetMapping("/tipo-eventos")
    public Mono<List<TipoEvento>> getAllTipoEventos() {
        log.debug("REST request to get all TipoEventos");
        return tipoEventoService.findAll().collectList();
    }

    /**
     * {@code GET  /tipo-eventos} : get all the tipoEventos as a stream.
     * @return the {@link Flux} of tipoEventos.
     */
    @GetMapping(value = "/tipo-eventos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<TipoEvento> getAllTipoEventosAsStream() {
        log.debug("REST request to get all TipoEventos as a stream");
        return tipoEventoService.findAll();
    }

    /**
     * {@code GET  /tipo-eventos/:id} : get the "id" tipoEvento.
     *
     * @param id the id of the tipoEvento to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tipoEvento, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tipo-eventos/{id}")
    public Mono<ResponseEntity<TipoEvento>> getTipoEvento(@PathVariable Long id) {
        log.debug("REST request to get TipoEvento : {}", id);
        Mono<TipoEvento> tipoEvento = tipoEventoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tipoEvento);
    }

    /**
     * {@code DELETE  /tipo-eventos/:id} : delete the "id" tipoEvento.
     *
     * @param id the id of the tipoEvento to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tipo-eventos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteTipoEvento(@PathVariable Long id) {
        log.debug("REST request to delete TipoEvento : {}", id);
        return tipoEventoService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
