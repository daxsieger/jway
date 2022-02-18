package com.jway.service.impl;

import com.jway.domain.Stato;
import com.jway.repository.StatoRepository;
import com.jway.service.StatoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Stato}.
 */
@Service
@Transactional
public class StatoServiceImpl implements StatoService {

    private final Logger log = LoggerFactory.getLogger(StatoServiceImpl.class);

    private final StatoRepository statoRepository;

    public StatoServiceImpl(StatoRepository statoRepository) {
        this.statoRepository = statoRepository;
    }

    @Override
    public Mono<Stato> save(Stato stato) {
        log.debug("Request to save Stato : {}", stato);
        return statoRepository.save(stato);
    }

    @Override
    public Mono<Stato> partialUpdate(Stato stato) {
        log.debug("Request to partially update Stato : {}", stato);

        return statoRepository
            .findById(stato.getId())
            .map(existingStato -> {
                if (stato.getIdStadio() != null) {
                    existingStato.setIdStadio(stato.getIdStadio());
                }
                if (stato.getDsStadio() != null) {
                    existingStato.setDsStadio(stato.getDsStadio());
                }
                if (stato.getTsCambioStato() != null) {
                    existingStato.setTsCambioStato(stato.getTsCambioStato());
                }

                return existingStato;
            })
            .flatMap(statoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Stato> findAll(Pageable pageable) {
        log.debug("Request to get all Statoes");
        return statoRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return statoRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Stato> findOne(Long id) {
        log.debug("Request to get Stato : {}", id);
        return statoRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Stato : {}", id);
        return statoRepository.deleteById(id);
    }
}
