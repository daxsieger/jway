package com.jway.repository.rowmapper;

import com.jway.domain.Processo;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Processo}, with proper type conversions.
 */
@Service
public class ProcessoRowMapper implements BiFunction<Row, String, Processo> {

    private final ColumnConverter converter;

    public ProcessoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Processo} stored in the database.
     */
    @Override
    public Processo apply(Row row, String prefix) {
        Processo entity = new Processo();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdProcesso(converter.fromRow(row, prefix + "_id_processo", Long.class));
        entity.setDsProcesso(converter.fromRow(row, prefix + "_ds_processo", String.class));
        return entity;
    }
}
