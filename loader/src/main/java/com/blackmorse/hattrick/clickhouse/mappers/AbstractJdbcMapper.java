package com.blackmorse.hattrick.clickhouse.mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractJdbcMapper<T> {
    private  final Map<String, Function<T, Object>> fieldsMap;
    private final Function<T, Map<String, Object>> tMapFunction;
    private final String sql;

    protected AbstractJdbcMapper(String tableName) {
        fieldsMap = initFieldsMap();
        tMapFunction = t -> {
            Map<String, Object> map = new HashMap<>();

            for (Map.Entry<String, Function<T, Object>> stringFunctionEntry : fieldsMap.entrySet()) {
                map.put(stringFunctionEntry.getKey(), stringFunctionEntry.getValue().apply(t));
            }

            return map;
        };

        List<String> fields = new ArrayList<>(fieldsMap.keySet());

        String fieldsString = fields.stream().collect(Collectors.joining(", ", "(", ")"));
        String insertString  = fields.stream().map(s -> ":" + s).collect(Collectors.joining(", ", "(", ")"));

        this.sql = "INSERT INTO " + tableName + " " + fieldsString + " VALUES " + insertString;
    }

    protected abstract Map<String, Function<T, Object>> initFieldsMap();

    public Function<T, Map<String, Object>> getTMapFunction() {
        return tMapFunction;
    }

    public String getSql() {
        return sql;
    }
}
