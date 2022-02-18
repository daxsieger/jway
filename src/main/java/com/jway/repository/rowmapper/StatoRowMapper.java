package com.jway.repository.rowmapper;

import com.jway.domain.Stato;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Stato}, with proper type conversions.
 */
@Service
public class StatoRowMapper implements BiFunction<Row, String, Stato> {

    private final ColumnConverter converter;

    public StatoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Stato} stored in the database.
     */
    @Override
    public Stato apply(Row row, String prefix) {
        Stato entity = new Stato();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdStadio(converter.fromRow(row, prefix + "_id_stadio", Long.class));
        entity.setDsStadio(converter.fromRow(row, prefix + "_ds_stadio", String.class));
        entity.setTsCambioStato(converter.fromRow(row, prefix + "_ts_cambio_stato", Instant.class));
        entity.setStadioId(converter.fromRow(row, prefix + "_stadio_id", Long.class));
        return entity;
    }
}
