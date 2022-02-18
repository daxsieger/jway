package com.jway.service.impl;

import com.jway.domain.TipoEvento;
import com.jway.repository.TipoEventoRepository;
import com.jway.service.TipoEventoService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link TipoEvento}.
 */
@Service
@Transactional
public class TipoEventoServiceImpl implements TipoEventoService {

    private final Logger log = LoggerFactory.getLogger(TipoEventoServiceImpl.class);

    private final TipoEventoRepository tipoEventoRepository;

    public TipoEventoServiceImpl(TipoEventoRepository tipoEventoRepository) {
        this.tipoEventoRepository = tipoEventoRepository;
    }

    @Override
    public Mono<TipoEvento> save(TipoEvento tipoEvento) {
        log.debug("Request to save TipoEvento : {}", tipoEvento);
        return tipoEventoRepository.save(tipoEvento);
    }

    @Override
    public Mono<TipoEvento> partialUpdate(TipoEvento tipoEvento) {
        log.debug("Request to partially update TipoEvento : {}", tipoEvento);

        return tipoEventoRepository
            .findById(tipoEvento.getId())
            .map(existingTipoEvento -> {
                if (tipoEvento.getIdTipoEvento() != null) {
                    existingTipoEvento.setIdTipoEvento(tipoEvento.getIdTipoEvento());
                }
                if (tipoEvento.getDsTipoEvento() != null) {
                    existingTipoEvento.setDsTipoEvento(tipoEvento.getDsTipoEvento());
                }

                return existingTipoEvento;
            })
            .flatMap(tipoEventoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TipoEvento> findAll() {
        log.debug("Request to get all TipoEventos");
        return tipoEventoRepository.findAll();
    }

    public Mono<Long> countAll() {
        return tipoEventoRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TipoEvento> findOne(Long id) {
        log.debug("Request to get TipoEvento : {}", id);
        return tipoEventoRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TipoEvento : {}", id);
        return tipoEventoRepository.deleteById(id);
    }
}
