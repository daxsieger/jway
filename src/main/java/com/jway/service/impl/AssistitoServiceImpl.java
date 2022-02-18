package com.jway.service.impl;

import com.jway.domain.Assistito;
import com.jway.repository.AssistitoRepository;
import com.jway.service.AssistitoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Assistito}.
 */
@Service
@Transactional
public class AssistitoServiceImpl implements AssistitoService {

    private final Logger log = LoggerFactory.getLogger(AssistitoServiceImpl.class);

    private final AssistitoRepository assistitoRepository;

    public AssistitoServiceImpl(AssistitoRepository assistitoRepository) {
        this.assistitoRepository = assistitoRepository;
    }

    @Override
    public Mono<Assistito> save(Assistito assistito) {
        log.debug("Request to save Assistito : {}", assistito);
        return assistitoRepository.save(assistito);
    }

    @Override
    public Mono<Assistito> partialUpdate(Assistito assistito) {
        log.debug("Request to partially update Assistito : {}", assistito);

        return assistitoRepository
            .findById(assistito.getId())
            .map(existingAssistito -> {
                if (assistito.getIdAssistito() != null) {
                    existingAssistito.setIdAssistito(assistito.getIdAssistito());
                }

                return existingAssistito;
            })
            .flatMap(assistitoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Assistito> findAll(Pageable pageable) {
        log.debug("Request to get all Assistitos");
        return assistitoRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return assistitoRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Assistito> findOne(Long id) {
        log.debug("Request to get Assistito : {}", id);
        return assistitoRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Assistito : {}", id);
        return assistitoRepository.deleteById(id);
    }
}
