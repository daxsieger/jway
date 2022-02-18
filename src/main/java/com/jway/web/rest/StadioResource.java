package com.jway.web.rest;

import com.jway.domain.Stadio;
import com.jway.repository.StadioRepository;
import com.jway.service.StadioService;
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
 * REST controller for managing {@link com.jway.domain.Stadio}.
 */
@RestController
@RequestMapping("/api")
public class StadioResource {

    private final Logger log = LoggerFactory.getLogger(StadioResource.class);

    private static final String ENTITY_NAME = "stadio";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StadioService stadioService;

    private final StadioRepository stadioRepository;

    public StadioResource(StadioService stadioService, StadioRepository stadioRepository) {
        this.stadioService = stadioService;
        this.stadioRepository = stadioRepository;
    }

    /**
     * {@code POST  /stadios} : Create a new stadio.
     *
     * @param stadio the stadio to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stadio, or with status {@code 400 (Bad Request)} if the stadio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stadios")
    public Mono<ResponseEntity<Stadio>> createStadio(@RequestBody Stadio stadio) throws URISyntaxException {
        log.debug("REST request to save Stadio : {}", stadio);
        if (stadio.getId() != null) {
            throw new BadRequestAlertException("A new stadio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return stadioService
            .save(stadio)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/stadios/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /stadios/:id} : Updates an existing stadio.
     *
     * @param id the id of the stadio to save.
     * @param stadio the stadio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stadio,
     * or with status {@code 400 (Bad Request)} if the stadio is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stadio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stadios/{id}")
    public Mono<ResponseEntity<Stadio>> updateStadio(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Stadio stadio
    ) throws URISyntaxException {
        log.debug("REST request to update Stadio : {}, {}", id, stadio);
        if (stadio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stadio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return stadioRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return stadioService
                    .save(stadio)
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
     * {@code PATCH  /stadios/:id} : Partial updates given fields of an existing stadio, field will ignore if it is null
     *
     * @param id the id of the stadio to save.
     * @param stadio the stadio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stadio,
     * or with status {@code 400 (Bad Request)} if the stadio is not valid,
     * or with status {@code 404 (Not Found)} if the stadio is not found,
     * or with status {@code 500 (Internal Server Error)} if the stadio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stadios/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Stadio>> partialUpdateStadio(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Stadio stadio
    ) throws URISyntaxException {
        log.debug("REST request to partial update Stadio partially : {}, {}", id, stadio);
        if (stadio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stadio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return stadioRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Stadio> result = stadioService.partialUpdate(stadio);

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
     * {@code GET  /stadios} : get all the stadios.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stadios in body.
     */
    @GetMapping("/stadios")
    public Mono<ResponseEntity<List<Stadio>>> getAllStadios(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Stadios");
        return stadioService
            .countAll()
            .zipWith(stadioService.findAll(pageable).collectList())
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
     * {@code GET  /stadios/:id} : get the "id" stadio.
     *
     * @param id the id of the stadio to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stadio, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stadios/{id}")
    public Mono<ResponseEntity<Stadio>> getStadio(@PathVariable Long id) {
        log.debug("REST request to get Stadio : {}", id);
        Mono<Stadio> stadio = stadioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stadio);
    }

    /**
     * {@code DELETE  /stadios/:id} : delete the "id" stadio.
     *
     * @param id the id of the stadio to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stadios/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteStadio(@PathVariable Long id) {
        log.debug("REST request to delete Stadio : {}", id);
        return stadioService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
