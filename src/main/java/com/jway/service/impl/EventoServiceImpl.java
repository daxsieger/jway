package com.jway.service.impl;

import com.jway.domain.Evento;
import com.jway.repository.EventoRepository;
import com.jway.service.EventoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Evento}.
 */
@Service
@Transactional
public class EventoServiceImpl implements EventoService {

    private final Logger log = LoggerFactory.getLogger(EventoServiceImpl.class);

    private final EventoRepository eventoRepository;

    public EventoServiceImpl(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Override
    public Mono<Evento> save(Evento evento) {
        log.debug("Request to save Evento : {}", evento);
        return eventoRepository.save(evento);
    }

    @Override
    public Mono<Evento> partialUpdate(Evento evento) {
        log.debug("Request to partially update Evento : {}", evento);

        return eventoRepository
            .findById(evento.getId())
            .map(existingEvento -> {
                if (evento.getIdEvento() != null) {
                    existingEvento.setIdEvento(evento.getIdEvento());
                }
                if (evento.getTsEvento() != null) {
                    existingEvento.setTsEvento(evento.getTsEvento());
                }
                if (evento.getNote() != null) {
                    existingEvento.setNote(evento.getNote());
                }

                return existingEvento;
            })
            .flatMap(eventoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Evento> findAll(Pageable pageable) {
        log.debug("Request to get all Eventos");
        return eventoRepository.findAllBy(pageable);
    }

    public Flux<Evento> findAllWithEagerRelationships(Pageable pageable) {
        return eventoRepository.findAllWithEagerRelationships(pageable);
    }

    public Mono<Long> countAll() {
        return eventoRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Evento> findOne(Long id) {
        log.debug("Request to get Evento : {}", id);
        return eventoRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Evento : {}", id);
        return eventoRepository.deleteById(id);
    }
}
