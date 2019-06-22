package com.example.habit.dao;

import com.example.habit.entity.Category;
import com.example.habit.entity.Goal;
import com.example.habit.entity.User;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sameer Gupta
 */
@Repository
public class CategoryDAO {


    @Autowired
    @Qualifier("mysql")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert insertActor;


    public List<Category> findByCategory(String category, String subCategory) throws Exception {
        List<Category> categories = new ArrayList<>();
        try {
            MapSqlParameterSource map = new MapSqlParameterSource();
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select * from category where category = :category and sub_category = :sub_category order by id desc");
            map.addValue("category", category);
            map.addValue("sub_category", subCategory);

            categories = jdbcTemplate.query(queryBuilder.toString(), map, new BeanPropertyRowMapper<>(Category.class));
        } catch (Exception exception) {
        }
        return categories;
    }

    public Category getById(BigInteger id){
        Category object = new Category();
        try {
            MapSqlParameterSource map = new MapSqlParameterSource();
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select * from category where id = :id order by id desc");
            map.addValue("id", id);
            List<Category> categories = jdbcTemplate.query(queryBuilder.toString(), map, new BeanPropertyRowMapper<>(Category.class));
            return CollectionUtils.isEmpty(categories) ? object : categories.get(0);
        } catch (Exception exception) {
            return object;
        }
    }

}
