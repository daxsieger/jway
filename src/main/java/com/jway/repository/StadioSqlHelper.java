package com.jway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class StadioSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("id_stadio", table, columnPrefix + "_id_stadio"));
        columns.add(Column.aliased("ds_stadio", table, columnPrefix + "_ds_stadio"));

        columns.add(Column.aliased("processo_id", table, columnPrefix + "_processo_id"));
        return columns;
    }
}
