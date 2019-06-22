package com.example.habit.dao;

/**
 * @author Sameer Gupta
 */

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.habit.entity.Column;
import com.example.habit.entity.Table;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;


public class DatabaseUtil {

    public static <T> String getInsertQuery(Class<T> classs) {
        StringBuilder query = new StringBuilder();
        query.append("insert into ");
        Table table = classs.getAnnotation(Table.class);
        query.append(table.name());
        query.append("(");
        StringBuilder values = new StringBuilder(" values( ");
        ReflectionUtils.doWithFields(classs, new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.getName().equalsIgnoreCase("id") || field.getName().equalsIgnoreCase("createdAt")
                        || field.getName().equalsIgnoreCase("updatedAt"))
                    return;
                if (field.getAnnotation(Column.class) != null)
                    query.append("`" + field.getAnnotation(Column.class).name() + "`");
                else
                    return;
                query.append(", ");
                values.append(":");
                values.append(field.getName());
                values.append(", ");
            }
        });
        query.delete(query.length() - 2, query.length());
        values.delete(values.length() - 2, values.length());
        query.append(") ");
        values.append(")");
        query.append(values);
        return query.toString();
    }

    public static <T> String getInsertQueryWithId(Class<T> classs) {
        StringBuilder query = new StringBuilder();
        query.append("insert into ");
        Table table = classs.getAnnotation(Table.class);
        query.append(table.name());
        query.append("(");
        StringBuilder values = new StringBuilder(" values( ");
        ReflectionUtils.doWithFields(classs, new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.getName().equalsIgnoreCase("createdAt") || field.getName().equalsIgnoreCase("updatedAt"))
                    return;
                if (field.getAnnotation(Column.class) != null)
                    query.append(field.getAnnotation(Column.class).name());
                else
                    return;
                query.append(", ");
                values.append(":");
                values.append(field.getName());
                values.append(", ");
            }
        });
        query.delete(query.length() - 2, query.length());
        values.delete(values.length() - 2, values.length());
        query.append(") ");
        values.append(")");
        query.append(values);
        return query.toString();
    }

    public static <T> String getUpdateByIdQuery(Class<T> classs) {
        StringBuilder query = new StringBuilder();
        query.append("update ");
        Table table = classs.getAnnotation(Table.class);
        query.append(table.name());
        query.append(" set ");
        ReflectionUtils.doWithFields(classs, new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.getName().equalsIgnoreCase("id") || field.getName().equalsIgnoreCase("createdAt")
                        || field.getName().equalsIgnoreCase("updatedAt"))
                    return;
                if (field.getAnnotation(Column.class) != null) {
                    query.append(field.getAnnotation(Column.class).name());
                    query.append(" = :");
                    query.append(field.getName());
                    query.append(", ");
                } else
                    return;
            }
        });
        query.delete(query.length() - 2, query.length());
        query.append(" where id = :id");
        return query.toString();
    }

    public static <T> String getUpdateByIdQuery(Class<T> classs, String whereClause) {
        StringBuilder query = new StringBuilder();
        query.append("update ");
        Table table = classs.getAnnotation(Table.class);
        query.append(table.name());
        query.append(" set ");
        ReflectionUtils.doWithFields(classs, new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.getName().equalsIgnoreCase("id") || field.getName().equalsIgnoreCase("createdAt")
                        || field.getName().equalsIgnoreCase("updatedAt"))
                    return;
                if (field.getAnnotation(Column.class) != null) {
                    query.append(field.getAnnotation(Column.class).name());
                    query.append(" = :");
                    query.append(field.getName());
                    query.append(", ");
                } else
                    return;
            }
        });
        query.delete(query.length() - 2, query.length());
        query.append(" where ");
        query.append(whereClause);
        return query.toString();
    }

    public static <T> MapSqlParameterSource getParameterSource(T object) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        ReflectionUtils.doWithFields(object.getClass(), new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                field.setAccessible(true);
                if (field.getAnnotation(Column.class) != null) {
                    if (Enum.class.isAssignableFrom(field.getType())) {
                        source.registerSqlType(field.getName(), Types.VARCHAR);
                    }
                    source.addValue(field.getName(), field.get(object));
                }
            }
        });
        return source;
    }

    public static <T> MapSqlParameterSource[] getParameterSource(List<T> objects) {
        MapSqlParameterSource[] sources = new MapSqlParameterSource[objects.size()];
        for (int index = 0; index < objects.size(); index++) {
            Object object = objects.get(index);
            MapSqlParameterSource source = new MapSqlParameterSource();
            ReflectionUtils.doWithFields(object.getClass(), new FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    field.setAccessible(true);
                    if (field.getAnnotation(Column.class) != null) {
                        if (Enum.class.isAssignableFrom(field.getType())) {
                            source.registerSqlType(field.getName(), Types.VARCHAR);
                        }
                        source.addValue(field.getName(), field.get(object));
                    }
                }
            });
            sources[index] = source;
        }
        return sources;
    }

}
