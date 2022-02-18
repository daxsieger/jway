package com.jway.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.jway.domain.Evento;
import com.jway.domain.Stato;
import com.jway.repository.rowmapper.AssistitoRowMapper;
import com.jway.repository.rowmapper.EventoRowMapper;
import com.jway.repository.rowmapper.GestoreRowMapper;
import com.jway.repository.rowmapper.ProduttoreRowMapper;
import com.jway.repository.rowmapper.TipoEventoRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Evento entity.
 */
@SuppressWarnings("unused")
class EventoRepositoryInternalImpl extends SimpleR2dbcRepository<Evento, Long> implements EventoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AssistitoRowMapper assistitoMapper;
    private final TipoEventoRowMapper tipoeventoMapper;
    private final GestoreRowMapper gestoreMapper;
    private final ProduttoreRowMapper produttoreMapper;
    private final EventoRowMapper eventoMapper;

    private static final Table entityTable = Table.aliased("evento", EntityManager.ENTITY_ALIAS);
    private static final Table assistitoTable = Table.aliased("assistito", "assistito");
    private static final Table tipoTable = Table.aliased("tipo_evento", "tipo");
    private static final Table gestoreTable = Table.aliased("gestore", "gestore");
    private static final Table origineTable = Table.aliased("produttore", "origine");

    private static final EntityManager.LinkTable statiLink = new EntityManager.LinkTable("rel_evento__stati", "evento_id", "stati_id");

    public EventoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AssistitoRowMapper assistitoMapper,
        TipoEventoRowMapper tipoeventoMapper,
        GestoreRowMapper gestoreMapper,
        ProduttoreRowMapper produttoreMapper,
        EventoRowMapper eventoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Evento.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.assistitoMapper = assistitoMapper;
        this.tipoeventoMapper = tipoeventoMapper;
        this.gestoreMapper = gestoreMapper;
        this.produttoreMapper = produttoreMapper;
        this.eventoMapper = eventoMapper;
    }

    @Override
    public Flux<Evento> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Evento> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Evento> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = EventoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AssistitoSqlHelper.getColumns(assistitoTable, "assistito"));
        columns.addAll(TipoEventoSqlHelper.getColumns(tipoTable, "tipo"));
        columns.addAll(GestoreSqlHelper.getColumns(gestoreTable, "gestore"));
        columns.addAll(ProduttoreSqlHelper.getColumns(origineTable, "origine"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(assistitoTable)
            .on(Column.create("assistito_id", entityTable))
            .equals(Column.create("id", assistitoTable))
            .leftOuterJoin(tipoTable)
            .on(Column.create("tipo_id", entityTable))
            .equals(Column.create("id", tipoTable))
            .leftOuterJoin(gestoreTable)
            .on(Column.create("gestore_id", entityTable))
            .equals(Column.create("id", gestoreTable))
            .leftOuterJoin(origineTable)
            .on(Column.create("origine_id", entityTable))
            .equals(Column.create("id", origineTable));

        String select = entityManager.createSelect(selectFrom, Evento.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Evento> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Evento> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    @Override
    public Mono<Evento> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Evento> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Evento> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Evento process(Row row, RowMetadata metadata) {
        Evento entity = eventoMapper.apply(row, "e");
        entity.setAssistito(assistitoMapper.apply(row, "assistito"));
        entity.setTipo(tipoeventoMapper.apply(row, "tipo"));
        entity.setGestore(gestoreMapper.apply(row, "gestore"));
        entity.setOrigine(produttoreMapper.apply(row, "origine"));
        return entity;
    }

    @Override
    public <S extends Evento> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Evento> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager.updateLinkTable(statiLink, entity.getId(), entity.getStatis().stream().map(Stato::getId)).then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(statiLink, entityId);
    }
}
