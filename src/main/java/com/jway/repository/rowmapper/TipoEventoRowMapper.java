package com.jway.repository.rowmapper;

import com.jway.domain.TipoEvento;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TipoEvento}, with proper type conversions.
 */
@Service
public class TipoEventoRowMapper implements BiFunction<Row, String, TipoEvento> {

    private final ColumnConverter converter;

    public TipoEventoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TipoEvento} stored in the database.
     */
    @Override
    public TipoEvento apply(Row row, String prefix) {
        TipoEvento entity = new TipoEvento();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdTipoEvento(converter.fromRow(row, prefix + "_id_tipo_evento", Long.class));
        entity.setDsTipoEvento(converter.fromRow(row, prefix + "_ds_tipo_evento", String.class));
        return entity;
    }
}
