package com.jway.web.rest;

import com.jway.domain.Evento;
import com.jway.repository.EventoRepository;
import com.jway.service.EventoService;
import com.jway.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.jway.domain.Evento}.
 */
@RestController
@RequestMapping("/api")
public class EventoResource {

    private final Logger log = LoggerFactory.getLogger(EventoResource.class);

    private static final String ENTITY_NAME = "evento";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventoService eventoService;

    private final EventoRepository eventoRepository;

    public EventoResource(EventoService eventoService, EventoRepository eventoRepository) {
        this.eventoService = eventoService;
        this.eventoRepository = eventoRepository;
    }

    /**
     * {@code POST  /eventos} : Create a new evento.
     *
     * @param evento the evento to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new evento, or with status {@code 400 (Bad Request)} if the evento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/eventos")
    public Mono<ResponseEntity<Evento>> createEvento(@RequestBody Evento evento) throws URISyntaxException {
        log.debug("REST request to save Evento : {}", evento);
        if (evento.getId() != null) {
            throw new BadRequestAlertException("A new evento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return eventoService
            .save(evento)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/eventos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /eventos/:id} : Updates an existing evento.
     *
     * @param id the id of the evento to save.
     * @param evento the evento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated evento,
     * or with status {@code 400 (Bad Request)} if the evento is not valid,
     * or with status {@code 500 (Internal Server Error)} if the evento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/eventos/{id}")
    public Mono<ResponseEntity<Evento>> updateEvento(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Evento evento
    ) throws URISyntaxException {
        log.debug("REST request to update Evento : {}, {}", id, evento);
        if (evento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, evento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eventoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return eventoService
                    .save(evento)
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
     * {@code PATCH  /eventos/:id} : Partial updates given fields of an existing evento, field will ignore if it is null
     *
     * @param id the id of the evento to save.
     * @param evento the evento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated evento,
     * or with status {@code 400 (Bad Request)} if the evento is not valid,
     * or with status {@code 404 (Not Found)} if the evento is not found,
     * or with status {@code 500 (Internal Server Error)} if the evento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/eventos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Evento>> partialUpdateEvento(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Evento evento
    ) throws URISyntaxException {
        log.debug("REST request to partial update Evento partially : {}, {}", id, evento);
        if (evento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, evento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eventoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Evento> result = eventoService.partialUpdate(evento);

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
     * {@code GET  /eventos} : get all the eventos.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventos in body.
     */
    @GetMapping("/eventos")
    public Mono<ResponseEntity<List<Evento>>> getAllEventos(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Eventos");
        return eventoService
            .countAll()
            .zipWith(eventoService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /eventos/:id} : get the "id" evento.
     *
     * @param id the id of the evento to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the evento, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/eventos/{id}")
    public Mono<ResponseEntity<Evento>> getEvento(@PathVariable Long id) {
        log.debug("REST request to get Evento : {}", id);
        Mono<Evento> evento = eventoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(evento);
    }

    /**
     * {@code DELETE  /eventos/:id} : delete the "id" evento.
     *
     * @param id the id of the evento to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/eventos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteEvento(@PathVariable Long id) {
        log.debug("REST request to delete Evento : {}", id);
        return eventoService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
