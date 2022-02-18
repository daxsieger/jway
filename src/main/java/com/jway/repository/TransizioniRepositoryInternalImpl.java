package com.jway.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.jway.domain.Transizioni;
import com.jway.repository.rowmapper.ProcessoRowMapper;
import com.jway.repository.rowmapper.StadioRowMapper;
import com.jway.repository.rowmapper.TransizioniRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Transizioni entity.
 */
@SuppressWarnings("unused")
class TransizioniRepositoryInternalImpl extends SimpleR2dbcRepository<Transizioni, Long> implements TransizioniRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProcessoRowMapper processoMapper;
    private final StadioRowMapper stadioMapper;
    private final TransizioniRowMapper transizioniMapper;

    private static final Table entityTable = Table.aliased("transizioni", EntityManager.ENTITY_ALIAS);
    private static final Table processoTable = Table.aliased("processo", "processo");
    private static final Table stadioInizialeTable = Table.aliased("stadio", "stadioIniziale");
    private static final Table stadioFinaleTable = Table.aliased("stadio", "stadioFinale");

    public TransizioniRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProcessoRowMapper processoMapper,
        StadioRowMapper stadioMapper,
        TransizioniRowMapper transizioniMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Transizioni.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.processoMapper = processoMapper;
        this.stadioMapper = stadioMapper;
        this.transizioniMapper = transizioniMapper;
    }

    @Override
    public Flux<Transizioni> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Transizioni> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Transizioni> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = TransizioniSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProcessoSqlHelper.getColumns(processoTable, "processo"));
        columns.addAll(StadioSqlHelper.getColumns(stadioInizialeTable, "stadioIniziale"));
        columns.addAll(StadioSqlHelper.getColumns(stadioFinaleTable, "stadioFinale"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(processoTable)
            .on(Column.create("processo_id", entityTable))
            .equals(Column.create("id", processoTable))
            .leftOuterJoin(stadioInizialeTable)
            .on(Column.create("stadio_iniziale_id", entityTable))
            .equals(Column.create("id", stadioInizialeTable))
            .leftOuterJoin(stadioFinaleTable)
            .on(Column.create("stadio_finale_id", entityTable))
            .equals(Column.create("id", stadioFinaleTable));

        String select = entityManager.createSelect(selectFrom, Transizioni.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Transizioni> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Transizioni> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private Transizioni process(Row row, RowMetadata metadata) {
        Transizioni entity = transizioniMapper.apply(row, "e");
        entity.setProcesso(processoMapper.apply(row, "processo"));
        entity.setStadioIniziale(stadioMapper.apply(row, "stadioIniziale"));
        entity.setStadioFinale(stadioMapper.apply(row, "stadioFinale"));
        return entity;
    }

    @Override
    public <S extends Transizioni> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
