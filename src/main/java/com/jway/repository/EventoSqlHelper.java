package com.jway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class EventoSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("id_evento", table, columnPrefix + "_id_evento"));
        columns.add(Column.aliased("ts_evento", table, columnPrefix + "_ts_evento"));
        columns.add(Column.aliased("note", table, columnPrefix + "_note"));

        columns.add(Column.aliased("assistito_id", table, columnPrefix + "_assistito_id"));
        columns.add(Column.aliased("tipo_id", table, columnPrefix + "_tipo_id"));
        columns.add(Column.aliased("gestore_id", table, columnPrefix + "_gestore_id"));
        columns.add(Column.aliased("origine_id", table, columnPrefix + "_origine_id"));
        return columns;
    }
}
