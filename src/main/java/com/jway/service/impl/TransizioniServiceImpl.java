package com.jway.service.impl;

import com.jway.domain.Transizioni;
import com.jway.repository.TransizioniRepository;
import com.jway.service.TransizioniService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Transizioni}.
 */
@Service
@Transactional
public class TransizioniServiceImpl implements TransizioniService {

    private final Logger log = LoggerFactory.getLogger(TransizioniServiceImpl.class);

    private final TransizioniRepository transizioniRepository;

    public TransizioniServiceImpl(TransizioniRepository transizioniRepository) {
        this.transizioniRepository = transizioniRepository;
    }

    @Override
    public Mono<Transizioni> save(Transizioni transizioni) {
        log.debug("Request to save Transizioni : {}", transizioni);
        return transizioniRepository.save(transizioni);
    }

    @Override
    public Mono<Transizioni> partialUpdate(Transizioni transizioni) {
        log.debug("Request to partially update Transizioni : {}", transizioni);

        return transizioniRepository
            .findById(transizioni.getId())
            .map(existingTransizioni -> {
                if (transizioni.getIdTransizione() != null) {
                    existingTransizioni.setIdTransizione(transizioni.getIdTransizione());
                }
                if (transizioni.getDsTransizione() != null) {
                    existingTransizioni.setDsTransizione(transizioni.getDsTransizione());
                }

                return existingTransizioni;
            })
            .flatMap(transizioniRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Transizioni> findAll(Pageable pageable) {
        log.debug("Request to get all Transizionis");
        return transizioniRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return transizioniRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Transizioni> findOne(Long id) {
        log.debug("Request to get Transizioni : {}", id);
        return transizioniRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Transizioni : {}", id);
        return transizioniRepository.deleteById(id);
    }
}
