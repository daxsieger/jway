package com.jway.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.jway.domain.Stato;
import com.jway.repository.rowmapper.StadioRowMapper;
import com.jway.repository.rowmapper.StatoRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
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
 * Spring Data SQL reactive custom repository implementation for the Stato entity.
 */
@SuppressWarnings("unused")
class StatoRepositoryInternalImpl extends SimpleR2dbcRepository<Stato, Long> implements StatoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final StadioRowMapper stadioMapper;
    private final StatoRowMapper statoMapper;

    private static final Table entityTable = Table.aliased("stato", EntityManager.ENTITY_ALIAS);
    private static final Table stadioTable = Table.aliased("stadio", "stadio");

    public StatoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        StadioRowMapper stadioMapper,
        StatoRowMapper statoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Stato.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.stadioMapper = stadioMapper;
        this.statoMapper = statoMapper;
    }

    @Override
    public Flux<Stato> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Stato> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Stato> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = StatoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(StadioSqlHelper.getColumns(stadioTable, "stadio"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(stadioTable)
            .on(Column.create("stadio_id", entityTable))
            .equals(Column.create("id", stadioTable));

        String select = entityManager.createSelect(selectFrom, Stato.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Stato> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Stato> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private Stato process(Row row, RowMetadata metadata) {
        Stato entity = statoMapper.apply(row, "e");
        entity.setStadio(stadioMapper.apply(row, "stadio"));
        return entity;
    }

    @Override
    public <S extends Stato> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
