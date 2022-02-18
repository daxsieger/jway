package com.jway.repository.rowmapper;

import com.jway.domain.Gestore;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Gestore}, with proper type conversions.
 */
@Service
public class GestoreRowMapper implements BiFunction<Row, String, Gestore> {

    private final ColumnConverter converter;

    public GestoreRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Gestore} stored in the database.
     */
    @Override
    public Gestore apply(Row row, String prefix) {
        Gestore entity = new Gestore();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdGestore(converter.fromRow(row, prefix + "_id_gestore", Long.class));
        return entity;
    }
}
