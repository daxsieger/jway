package com.jway.repository.rowmapper;

import com.jway.domain.Produttore;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Produttore}, with proper type conversions.
 */
@Service
public class ProduttoreRowMapper implements BiFunction<Row, String, Produttore> {

    private final ColumnConverter converter;

    public ProduttoreRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Produttore} stored in the database.
     */
    @Override
    public Produttore apply(Row row, String prefix) {
        Produttore entity = new Produttore();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdProduttore(converter.fromRow(row, prefix + "_id_produttore", Long.class));
        entity.setDsProduttore(converter.fromRow(row, prefix + "_ds_produttore", String.class));
        return entity;
    }
}
