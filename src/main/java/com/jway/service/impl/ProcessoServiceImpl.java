package com.jway.service.impl;

import com.jway.domain.Processo;
import com.jway.repository.ProcessoRepository;
import com.jway.service.ProcessoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Processo}.
 */
@Service
@Transactional
public class ProcessoServiceImpl implements ProcessoService {

    private final Logger log = LoggerFactory.getLogger(ProcessoServiceImpl.class);

    private final ProcessoRepository processoRepository;

    public ProcessoServiceImpl(ProcessoRepository processoRepository) {
        this.processoRepository = processoRepository;
    }

    @Override
    public Mono<Processo> save(Processo processo) {
        log.debug("Request to save Processo : {}", processo);
        return processoRepository.save(processo);
    }

    @Override
    public Mono<Processo> partialUpdate(Processo processo) {
        log.debug("Request to partially update Processo : {}", processo);

        return processoRepository
            .findById(processo.getId())
            .map(existingProcesso -> {
                if (processo.getIdProcesso() != null) {
                    existingProcesso.setIdProcesso(processo.getIdProcesso());
                }
                if (processo.getDsProcesso() != null) {
                    existingProcesso.setDsProcesso(processo.getDsProcesso());
                }

                return existingProcesso;
            })
            .flatMap(processoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Processo> findAll(Pageable pageable) {
        log.debug("Request to get all Processos");
        return processoRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return processoRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Processo> findOne(Long id) {
        log.debug("Request to get Processo : {}", id);
        return processoRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Processo : {}", id);
        return processoRepository.deleteById(id);
    }
}
