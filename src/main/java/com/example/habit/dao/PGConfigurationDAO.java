package com.example.habit.dao;


import com.example.habit.enums.PaymentOptions;
import com.example.habit.entity.PaymentGatewayConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Repository
public class PGConfigurationDAO {

    @Autowired
    @Qualifier("mysql")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert insertActor;


    public PaymentGatewayConfiguration getByPaymentOptions(PaymentOptions options){

        String query = "select * from payment_gateway_configuration where pg_name = :pgName";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("pgName", options.name());

        List<PaymentGatewayConfiguration> result = jdbcTemplate.query(query,source, new BeanPropertyRowMapper<>(PaymentGatewayConfiguration.class));
        if(CollectionUtils.isEmpty(result)){
            return null;
        }
        return result.get(0);

    }


}
