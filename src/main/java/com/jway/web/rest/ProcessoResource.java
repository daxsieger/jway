package com.jway.web.rest;

import com.jway.domain.Processo;
import com.jway.repository.ProcessoRepository;
import com.jway.service.ProcessoService;
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
 * REST controller for managing {@link com.jway.domain.Processo}.
 */
@RestController
@RequestMapping("/api")
public class ProcessoResource {

    private final Logger log = LoggerFactory.getLogger(ProcessoResource.class);

    private static final String ENTITY_NAME = "processo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProcessoService processoService;

    private final ProcessoRepository processoRepository;

    public ProcessoResource(ProcessoService processoService, ProcessoRepository processoRepository) {
        this.processoService = processoService;
        this.processoRepository = processoRepository;
    }

    /**
     * {@code POST  /processos} : Create a new processo.
     *
     * @param processo the processo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new processo, or with status {@code 400 (Bad Request)} if the processo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/processos")
    public Mono<ResponseEntity<Processo>> createProcesso(@RequestBody Processo processo) throws URISyntaxException {
        log.debug("REST request to save Processo : {}", processo);
        if (processo.getId() != null) {
            throw new BadRequestAlertException("A new processo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return processoService
            .save(processo)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/processos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /processos/:id} : Updates an existing processo.
     *
     * @param id the id of the processo to save.
     * @param processo the processo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated processo,
     * or with status {@code 400 (Bad Request)} if the processo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the processo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/processos/{id}")
    public Mono<ResponseEntity<Processo>> updateProcesso(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Processo processo
    ) throws URISyntaxException {
        log.debug("REST request to update Processo : {}, {}", id, processo);
        if (processo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, processo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return processoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return processoService
                    .save(processo)
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
     * {@code PATCH  /processos/:id} : Partial updates given fields of an existing processo, field will ignore if it is null
     *
     * @param id the id of the processo to save.
     * @param processo the processo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated processo,
     * or with status {@code 400 (Bad Request)} if the processo is not valid,
     * or with status {@code 404 (Not Found)} if the processo is not found,
     * or with status {@code 500 (Internal Server Error)} if the processo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/processos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Processo>> partialUpdateProcesso(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Processo processo
    ) throws URISyntaxException {
        log.debug("REST request to partial update Processo partially : {}, {}", id, processo);
        if (processo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, processo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return processoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Processo> result = processoService.partialUpdate(processo);

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
     * {@code GET  /processos} : get all the processos.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of processos in body.
     */
    @GetMapping("/processos")
    public Mono<ResponseEntity<List<Processo>>> getAllProcessos(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Processos");
        return processoService
            .countAll()
            .zipWith(processoService.findAll(pageable).collectList())
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
     * {@code GET  /processos/:id} : get the "id" processo.
     *
     * @param id the id of the processo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the processo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/processos/{id}")
    public Mono<ResponseEntity<Processo>> getProcesso(@PathVariable Long id) {
        log.debug("REST request to get Processo : {}", id);
        Mono<Processo> processo = processoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(processo);
    }

    /**
     * {@code DELETE  /processos/:id} : delete the "id" processo.
     *
     * @param id the id of the processo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/processos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteProcesso(@PathVariable Long id) {
        log.debug("REST request to delete Processo : {}", id);
        return processoService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
