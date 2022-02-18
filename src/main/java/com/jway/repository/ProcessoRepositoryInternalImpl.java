package com.jway.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.jway.domain.Processo;
import com.jway.repository.rowmapper.ProcessoRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Processo entity.
 */
@SuppressWarnings("unused")
class ProcessoRepositoryInternalImpl extends SimpleR2dbcRepository<Processo, Long> implements ProcessoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProcessoRowMapper processoMapper;

    private static final Table entityTable = Table.aliased("processo", EntityManager.ENTITY_ALIAS);

    public ProcessoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProcessoRowMapper processoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Processo.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.processoMapper = processoMapper;
    }

    @Override
    public Flux<Processo> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Processo> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Processo> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ProcessoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, Processo.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Processo> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Processo> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private Processo process(Row row, RowMetadata metadata) {
        Processo entity = processoMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Processo> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
