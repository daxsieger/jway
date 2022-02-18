package com.jway.web.rest;

import com.jway.domain.Stato;
import com.jway.repository.StatoRepository;
import com.jway.service.StatoService;
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
 * REST controller for managing {@link com.jway.domain.Stato}.
 */
@RestController
@RequestMapping("/api")
public class StatoResource {

    private final Logger log = LoggerFactory.getLogger(StatoResource.class);

    private static final String ENTITY_NAME = "stato";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StatoService statoService;

    private final StatoRepository statoRepository;

    public StatoResource(StatoService statoService, StatoRepository statoRepository) {
        this.statoService = statoService;
        this.statoRepository = statoRepository;
    }

    /**
     * {@code POST  /statoes} : Create a new stato.
     *
     * @param stato the stato to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stato, or with status {@code 400 (Bad Request)} if the stato has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/statoes")
    public Mono<ResponseEntity<Stato>> createStato(@RequestBody Stato stato) throws URISyntaxException {
        log.debug("REST request to save Stato : {}", stato);
        if (stato.getId() != null) {
            throw new BadRequestAlertException("A new stato cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return statoService
            .save(stato)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/statoes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /statoes/:id} : Updates an existing stato.
     *
     * @param id the id of the stato to save.
     * @param stato the stato to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stato,
     * or with status {@code 400 (Bad Request)} if the stato is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stato couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/statoes/{id}")
    public Mono<ResponseEntity<Stato>> updateStato(@PathVariable(value = "id", required = false) final Long id, @RequestBody Stato stato)
        throws URISyntaxException {
        log.debug("REST request to update Stato : {}, {}", id, stato);
        if (stato.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stato.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return statoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return statoService
                    .save(stato)
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
     * {@code PATCH  /statoes/:id} : Partial updates given fields of an existing stato, field will ignore if it is null
     *
     * @param id the id of the stato to save.
     * @param stato the stato to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stato,
     * or with status {@code 400 (Bad Request)} if the stato is not valid,
     * or with status {@code 404 (Not Found)} if the stato is not found,
     * or with status {@code 500 (Internal Server Error)} if the stato couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/statoes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Stato>> partialUpdateStato(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Stato stato
    ) throws URISyntaxException {
        log.debug("REST request to partial update Stato partially : {}, {}", id, stato);
        if (stato.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stato.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return statoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Stato> result = statoService.partialUpdate(stato);

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
     * {@code GET  /statoes} : get all the statoes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statoes in body.
     */
    @GetMapping("/statoes")
    public Mono<ResponseEntity<List<Stato>>> getAllStatoes(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Statoes");
        return statoService
            .countAll()
            .zipWith(statoService.findAll(pageable).collectList())
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
     * {@code GET  /statoes/:id} : get the "id" stato.
     *
     * @param id the id of the stato to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stato, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/statoes/{id}")
    public Mono<ResponseEntity<Stato>> getStato(@PathVariable Long id) {
        log.debug("REST request to get Stato : {}", id);
        Mono<Stato> stato = statoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stato);
    }

    /**
     * {@code DELETE  /statoes/:id} : delete the "id" stato.
     *
     * @param id the id of the stato to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/statoes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteStato(@PathVariable Long id) {
        log.debug("REST request to delete Stato : {}", id);
        return statoService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
