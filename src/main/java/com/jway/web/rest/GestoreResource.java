package com.jway.web.rest;

import com.jway.domain.Gestore;
import com.jway.repository.GestoreRepository;
import com.jway.service.GestoreService;
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
 * REST controller for managing {@link com.jway.domain.Gestore}.
 */
@RestController
@RequestMapping("/api")
public class GestoreResource {

    private final Logger log = LoggerFactory.getLogger(GestoreResource.class);

    private static final String ENTITY_NAME = "gestore";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GestoreService gestoreService;

    private final GestoreRepository gestoreRepository;

    public GestoreResource(GestoreService gestoreService, GestoreRepository gestoreRepository) {
        this.gestoreService = gestoreService;
        this.gestoreRepository = gestoreRepository;
    }

    /**
     * {@code POST  /gestores} : Create a new gestore.
     *
     * @param gestore the gestore to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gestore, or with status {@code 400 (Bad Request)} if the gestore has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/gestores")
    public Mono<ResponseEntity<Gestore>> createGestore(@RequestBody Gestore gestore) throws URISyntaxException {
        log.debug("REST request to save Gestore : {}", gestore);
        if (gestore.getId() != null) {
            throw new BadRequestAlertException("A new gestore cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return gestoreService
            .save(gestore)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/gestores/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /gestores/:id} : Updates an existing gestore.
     *
     * @param id the id of the gestore to save.
     * @param gestore the gestore to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gestore,
     * or with status {@code 400 (Bad Request)} if the gestore is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gestore couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/gestores/{id}")
    public Mono<ResponseEntity<Gestore>> updateGestore(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Gestore gestore
    ) throws URISyntaxException {
        log.debug("REST request to update Gestore : {}, {}", id, gestore);
        if (gestore.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gestore.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return gestoreRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return gestoreService
                    .save(gestore)
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
     * {@code PATCH  /gestores/:id} : Partial updates given fields of an existing gestore, field will ignore if it is null
     *
     * @param id the id of the gestore to save.
     * @param gestore the gestore to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gestore,
     * or with status {@code 400 (Bad Request)} if the gestore is not valid,
     * or with status {@code 404 (Not Found)} if the gestore is not found,
     * or with status {@code 500 (Internal Server Error)} if the gestore couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/gestores/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Gestore>> partialUpdateGestore(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Gestore gestore
    ) throws URISyntaxException {
        log.debug("REST request to partial update Gestore partially : {}, {}", id, gestore);
        if (gestore.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gestore.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return gestoreRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Gestore> result = gestoreService.partialUpdate(gestore);

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
     * {@code GET  /gestores} : get all the gestores.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of gestores in body.
     */
    @GetMapping("/gestores")
    public Mono<ResponseEntity<List<Gestore>>> getAllGestores(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Gestores");
        return gestoreService
            .countAll()
            .zipWith(gestoreService.findAll(pageable).collectList())
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
     * {@code GET  /gestores/:id} : get the "id" gestore.
     *
     * @param id the id of the gestore to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gestore, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/gestores/{id}")
    public Mono<ResponseEntity<Gestore>> getGestore(@PathVariable Long id) {
        log.debug("REST request to get Gestore : {}", id);
        Mono<Gestore> gestore = gestoreService.findOne(id);
        return ResponseUtil.wrapOrNotFound(gestore);
    }

    /**
     * {@code DELETE  /gestores/:id} : delete the "id" gestore.
     *
     * @param id the id of the gestore to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/gestores/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteGestore(@PathVariable Long id) {
        log.debug("REST request to delete Gestore : {}", id);
        return gestoreService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
