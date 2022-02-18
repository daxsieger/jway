package com.jway.service.impl;

import com.jway.domain.Gestore;
import com.jway.repository.GestoreRepository;
import com.jway.service.GestoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Gestore}.
 */
@Service
@Transactional
public class GestoreServiceImpl implements GestoreService {

    private final Logger log = LoggerFactory.getLogger(GestoreServiceImpl.class);

    private final GestoreRepository gestoreRepository;

    public GestoreServiceImpl(GestoreRepository gestoreRepository) {
        this.gestoreRepository = gestoreRepository;
    }

    @Override
    public Mono<Gestore> save(Gestore gestore) {
        log.debug("Request to save Gestore : {}", gestore);
        return gestoreRepository.save(gestore);
    }

    @Override
    public Mono<Gestore> partialUpdate(Gestore gestore) {
        log.debug("Request to partially update Gestore : {}", gestore);

        return gestoreRepository
            .findById(gestore.getId())
            .map(existingGestore -> {
                if (gestore.getIdGestore() != null) {
                    existingGestore.setIdGestore(gestore.getIdGestore());
                }

                return existingGestore;
            })
            .flatMap(gestoreRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Gestore> findAll(Pageable pageable) {
        log.debug("Request to get all Gestores");
        return gestoreRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return gestoreRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Gestore> findOne(Long id) {
        log.debug("Request to get Gestore : {}", id);
        return gestoreRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Gestore : {}", id);
        return gestoreRepository.deleteById(id);
    }
}
