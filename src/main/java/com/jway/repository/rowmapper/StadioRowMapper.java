package com.jway.repository.rowmapper;

import com.jway.domain.Stadio;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Stadio}, with proper type conversions.
 */
@Service
public class StadioRowMapper implements BiFunction<Row, String, Stadio> {

    private final ColumnConverter converter;

    public StadioRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Stadio} stored in the database.
     */
    @Override
    public Stadio apply(Row row, String prefix) {
        Stadio entity = new Stadio();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdStadio(converter.fromRow(row, prefix + "_id_stadio", Long.class));
        entity.setDsStadio(converter.fromRow(row, prefix + "_ds_stadio", String.class));
        entity.setProcessoId(converter.fromRow(row, prefix + "_processo_id", Long.class));
        return entity;
    }
}
