package com.jway.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.jway.domain.Stadio;
import com.jway.repository.rowmapper.ProcessoRowMapper;
import com.jway.repository.rowmapper.StadioRowMapper;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Stadio entity.
 */
@SuppressWarnings("unused")
class StadioRepositoryInternalImpl extends SimpleR2dbcRepository<Stadio, Long> implements StadioRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProcessoRowMapper processoMapper;
    private final StadioRowMapper stadioMapper;

    private static final Table entityTable = Table.aliased("stadio", EntityManager.ENTITY_ALIAS);
    private static final Table processoTable = Table.aliased("processo", "processo");

    public StadioRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProcessoRowMapper processoMapper,
        StadioRowMapper stadioMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Stadio.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.processoMapper = processoMapper;
        this.stadioMapper = stadioMapper;
    }

    @Override
    public Flux<Stadio> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Stadio> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Stadio> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = StadioSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProcessoSqlHelper.getColumns(processoTable, "processo"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(processoTable)
            .on(Column.create("processo_id", entityTable))
            .equals(Column.create("id", processoTable));

        String select = entityManager.createSelect(selectFrom, Stadio.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Stadio> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Stadio> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private Stadio process(Row row, RowMetadata metadata) {
        Stadio entity = stadioMapper.apply(row, "e");
        entity.setProcesso(processoMapper.apply(row, "processo"));
        return entity;
    }

    @Override
    public <S extends Stadio> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
