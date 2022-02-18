package com.jway.repository.rowmapper;

import com.jway.domain.Evento;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Evento}, with proper type conversions.
 */
@Service
public class EventoRowMapper implements BiFunction<Row, String, Evento> {

    private final ColumnConverter converter;

    public EventoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Evento} stored in the database.
     */
    @Override
    public Evento apply(Row row, String prefix) {
        Evento entity = new Evento();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdEvento(converter.fromRow(row, prefix + "_id_evento", Long.class));
        entity.setTsEvento(converter.fromRow(row, prefix + "_ts_evento", Instant.class));
        entity.setNote(converter.fromRow(row, prefix + "_note", String.class));
        entity.setAssistitoId(converter.fromRow(row, prefix + "_assistito_id", Long.class));
        entity.setTipoId(converter.fromRow(row, prefix + "_tipo_id", Long.class));
        entity.setGestoreId(converter.fromRow(row, prefix + "_gestore_id", Long.class));
        entity.setOrigineId(converter.fromRow(row, prefix + "_origine_id", Long.class));
        return entity;
    }
}
