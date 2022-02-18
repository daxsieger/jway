package com.jway.repository.rowmapper;

import com.jway.domain.Transizioni;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Transizioni}, with proper type conversions.
 */
@Service
public class TransizioniRowMapper implements BiFunction<Row, String, Transizioni> {

    private final ColumnConverter converter;

    public TransizioniRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Transizioni} stored in the database.
     */
    @Override
    public Transizioni apply(Row row, String prefix) {
        Transizioni entity = new Transizioni();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdTransizione(converter.fromRow(row, prefix + "_id_transizione", Long.class));
        entity.setDsTransizione(converter.fromRow(row, prefix + "_ds_transizione", String.class));
        entity.setProcessoId(converter.fromRow(row, prefix + "_processo_id", Long.class));
        entity.setStadioInizialeId(converter.fromRow(row, prefix + "_stadio_iniziale_id", Long.class));
        entity.setStadioFinaleId(converter.fromRow(row, prefix + "_stadio_finale_id", Long.class));
        return entity;
    }
}
