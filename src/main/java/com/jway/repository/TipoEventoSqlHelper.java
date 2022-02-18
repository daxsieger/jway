package com.jway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TipoEventoSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("id_tipo_evento", table, columnPrefix + "_id_tipo_evento"));
        columns.add(Column.aliased("ds_tipo_evento", table, columnPrefix + "_ds_tipo_evento"));

        return columns;
    }
}
