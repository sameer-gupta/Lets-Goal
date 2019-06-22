package com.example.habit.dao;

import com.example.habit.entity.Goal;
import com.example.habit.entity.Whatsapp;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @author Sameer Gupta
 */
@Repository
public class WhatsappDAO {


    @Autowired
    @Qualifier("mysql")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert insertActor;


    public Whatsapp insertObject(Whatsapp object) throws Exception {

        BigInteger number = insertJDBC(object);
        object.setId(number);
        return object;
    }

    private BigInteger insertJDBC(Whatsapp object) {

        String query = DatabaseUtil.getInsertQuery(Whatsapp.class);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(query, DatabaseUtil.getParameterSource(object), keyHolder,
                new String[]{"id"});
        return new BigInteger(keyHolder.getKey().toString());
    }


    public BigInteger countWhatsappMessages(String contact, Date start, Date end) throws Exception {

        String query = "SELECT COUNT(0) FROM whatsapp where user_contact = :contact and message_timestamp <= :end and " +
                "message_timestamp >= :start ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        StringBuilder queryBuilder = new StringBuilder();
        map.addValue("contact", contact);
        map.addValue("end", end);
        map.addValue("start", start);

        return this.jdbcTemplate.queryForObject(query, map, BigInteger.class);

    }
}