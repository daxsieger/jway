package com.jway.service.impl;

import com.jway.domain.Stadio;
import com.jway.repository.StadioRepository;
import com.jway.service.StadioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Stadio}.
 */
@Service
@Transactional
public class StadioServiceImpl implements StadioService {

    private final Logger log = LoggerFactory.getLogger(StadioServiceImpl.class);

    private final StadioRepository stadioRepository;

    public StadioServiceImpl(StadioRepository stadioRepository) {
        this.stadioRepository = stadioRepository;
    }

    @Override
    public Mono<Stadio> save(Stadio stadio) {
        log.debug("Request to save Stadio : {}", stadio);
        return stadioRepository.save(stadio);
    }

    @Override
    public Mono<Stadio> partialUpdate(Stadio stadio) {
        log.debug("Request to partially update Stadio : {}", stadio);

        return stadioRepository
            .findById(stadio.getId())
            .map(existingStadio -> {
                if (stadio.getIdStadio() != null) {
                    existingStadio.setIdStadio(stadio.getIdStadio());
                }
                if (stadio.getDsStadio() != null) {
                    existingStadio.setDsStadio(stadio.getDsStadio());
                }

                return existingStadio;
            })
            .flatMap(stadioRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Stadio> findAll(Pageable pageable) {
        log.debug("Request to get all Stadios");
        return stadioRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return stadioRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Stadio> findOne(Long id) {
        log.debug("Request to get Stadio : {}", id);
        return stadioRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Stadio : {}", id);
        return stadioRepository.deleteById(id);
    }
}
