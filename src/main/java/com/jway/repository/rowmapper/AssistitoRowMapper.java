package com.jway.repository.rowmapper;

import com.jway.domain.Assistito;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Assistito}, with proper type conversions.
 */
@Service
public class AssistitoRowMapper implements BiFunction<Row, String, Assistito> {

    private final ColumnConverter converter;

    public AssistitoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Assistito} stored in the database.
     */
    @Override
    public Assistito apply(Row row, String prefix) {
        Assistito entity = new Assistito();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdAssistito(converter.fromRow(row, prefix + "_id_assistito", Long.class));
        return entity;
    }
}
