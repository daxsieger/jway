package com.jway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TransizioniSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("id_transizione", table, columnPrefix + "_id_transizione"));
        columns.add(Column.aliased("ds_transizione", table, columnPrefix + "_ds_transizione"));

        columns.add(Column.aliased("processo_id", table, columnPrefix + "_processo_id"));
        columns.add(Column.aliased("stadio_iniziale_id", table, columnPrefix + "_stadio_iniziale_id"));
        columns.add(Column.aliased("stadio_finale_id", table, columnPrefix + "_stadio_finale_id"));
        return columns;
    }
}
