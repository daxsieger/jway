package com.jway.service.impl;

import com.jway.domain.Produttore;
import com.jway.repository.ProduttoreRepository;
import com.jway.service.ProduttoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Produttore}.
 */
@Service
@Transactional
public class ProduttoreServiceImpl implements ProduttoreService {

    private final Logger log = LoggerFactory.getLogger(ProduttoreServiceImpl.class);

    private final ProduttoreRepository produttoreRepository;

    public ProduttoreServiceImpl(ProduttoreRepository produttoreRepository) {
        this.produttoreRepository = produttoreRepository;
    }

    @Override
    public Mono<Produttore> save(Produttore produttore) {
        log.debug("Request to save Produttore : {}", produttore);
        return produttoreRepository.save(produttore);
    }

    @Override
    public Mono<Produttore> partialUpdate(Produttore produttore) {
        log.debug("Request to partially update Produttore : {}", produttore);

        return produttoreRepository
            .findById(produttore.getId())
            .map(existingProduttore -> {
                if (produttore.getIdProduttore() != null) {
                    existingProduttore.setIdProduttore(produttore.getIdProduttore());
                }
                if (produttore.getDsProduttore() != null) {
                    existingProduttore.setDsProduttore(produttore.getDsProduttore());
                }

                return existingProduttore;
            })
            .flatMap(produttoreRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Produttore> findAll(Pageable pageable) {
        log.debug("Request to get all Produttores");
        return produttoreRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return produttoreRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Produttore> findOne(Long id) {
        log.debug("Request to get Produttore : {}", id);
        return produttoreRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Produttore : {}", id);
        return produttoreRepository.deleteById(id);
    }
}
