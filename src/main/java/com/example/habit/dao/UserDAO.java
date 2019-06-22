package com.example.habit.dao;

import com.example.habit.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;


import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @author Sameer Gupta
 */

@Repository
public class UserDAO {

    @Autowired
    @Qualifier("mysql")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert insertActor;


    public User getById(BigInteger id) throws Exception {
        User object = new User();
        try {
            MapSqlParameterSource map = new MapSqlParameterSource();
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select * from users where id = :id order by id desc");
            map.addValue("id", id);
            List<User> users = jdbcTemplate.query(queryBuilder.toString(), map,
                    new BeanPropertyRowMapper<>(User.class));
            return CollectionUtils.isEmpty(users) ? object : users.get(0);
        } catch (Exception exception) {
            return object;
        }
    }


    public User getByUsername(String username, String password) throws Exception {
        User object = new User();
        try {
            MapSqlParameterSource map = new MapSqlParameterSource();
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select * from users where username = :username order by id desc");
            map.addValue("username", username);

            List<User> users = jdbcTemplate.query(queryBuilder.toString(), map,
                    new BeanPropertyRowMapper<>(User.class));
            return CollectionUtils.isEmpty(users) ? object : users.get(0);
        } catch (Exception exception) {
            return object;
        }
    }
}
