package com.jway.web.rest;

import com.jway.domain.Transizioni;
import com.jway.repository.TransizioniRepository;
import com.jway.service.TransizioniService;
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
 * REST controller for managing {@link com.jway.domain.Transizioni}.
 */
@RestController
@RequestMapping("/api")
public class TransizioniResource {

    private final Logger log = LoggerFactory.getLogger(TransizioniResource.class);

    private static final String ENTITY_NAME = "transizioni";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransizioniService transizioniService;

    private final TransizioniRepository transizioniRepository;

    public TransizioniResource(TransizioniService transizioniService, TransizioniRepository transizioniRepository) {
        this.transizioniService = transizioniService;
        this.transizioniRepository = transizioniRepository;
    }

    /**
     * {@code POST  /transizionis} : Create a new transizioni.
     *
     * @param transizioni the transizioni to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transizioni, or with status {@code 400 (Bad Request)} if the transizioni has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transizionis")
    public Mono<ResponseEntity<Transizioni>> createTransizioni(@RequestBody Transizioni transizioni) throws URISyntaxException {
        log.debug("REST request to save Transizioni : {}", transizioni);
        if (transizioni.getId() != null) {
            throw new BadRequestAlertException("A new transizioni cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return transizioniService
            .save(transizioni)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/transizionis/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /transizionis/:id} : Updates an existing transizioni.
     *
     * @param id the id of the transizioni to save.
     * @param transizioni the transizioni to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transizioni,
     * or with status {@code 400 (Bad Request)} if the transizioni is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transizioni couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transizionis/{id}")
    public Mono<ResponseEntity<Transizioni>> updateTransizioni(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Transizioni transizioni
    ) throws URISyntaxException {
        log.debug("REST request to update Transizioni : {}, {}", id, transizioni);
        if (transizioni.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transizioni.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return transizioniRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return transizioniService
                    .save(transizioni)
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
     * {@code PATCH  /transizionis/:id} : Partial updates given fields of an existing transizioni, field will ignore if it is null
     *
     * @param id the id of the transizioni to save.
     * @param transizioni the transizioni to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transizioni,
     * or with status {@code 400 (Bad Request)} if the transizioni is not valid,
     * or with status {@code 404 (Not Found)} if the transizioni is not found,
     * or with status {@code 500 (Internal Server Error)} if the transizioni couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transizionis/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Transizioni>> partialUpdateTransizioni(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Transizioni transizioni
    ) throws URISyntaxException {
        log.debug("REST request to partial update Transizioni partially : {}, {}", id, transizioni);
        if (transizioni.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transizioni.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return transizioniRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Transizioni> result = transizioniService.partialUpdate(transizioni);

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
     * {@code GET  /transizionis} : get all the transizionis.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transizionis in body.
     */
    @GetMapping("/transizionis")
    public Mono<ResponseEntity<List<Transizioni>>> getAllTransizionis(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Transizionis");
        return transizioniService
            .countAll()
            .zipWith(transizioniService.findAll(pageable).collectList())
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
     * {@code GET  /transizionis/:id} : get the "id" transizioni.
     *
     * @param id the id of the transizioni to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transizioni, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transizionis/{id}")
    public Mono<ResponseEntity<Transizioni>> getTransizioni(@PathVariable Long id) {
        log.debug("REST request to get Transizioni : {}", id);
        Mono<Transizioni> transizioni = transizioniService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transizioni);
    }

    /**
     * {@code DELETE  /transizionis/:id} : delete the "id" transizioni.
     *
     * @param id the id of the transizioni to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transizionis/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteTransizioni(@PathVariable Long id) {
        log.debug("REST request to delete Transizioni : {}", id);
        return transizioniService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
