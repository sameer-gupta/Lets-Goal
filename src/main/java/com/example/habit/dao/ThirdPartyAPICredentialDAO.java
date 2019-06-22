package com.example.habit.dao;

import com.example.habit.entity.ThirdPartyAPICredential;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Sameer Gupta
 */

@Repository
public class ThirdPartyAPICredentialDAO {

    @Autowired
    @Qualifier("mysql")
    private NamedParameterJdbcTemplate jdbcTemplate;

    public ThirdPartyAPICredential fetchByOrgCode(String orgCode) throws Exception {
        ThirdPartyAPICredential object = new ThirdPartyAPICredential();
        try {
            MapSqlParameterSource map = new MapSqlParameterSource();
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select * from third_party_api_credentials where organisation_code = :orgCode order by id desc");
            map.addValue("orgCode", orgCode);

            List<ThirdPartyAPICredential> ThirdPartyAPICredentials = jdbcTemplate.query(queryBuilder.toString(), map, new BeanPropertyRowMapper<>(ThirdPartyAPICredential.class));

            return CollectionUtils.isEmpty(ThirdPartyAPICredentials) ? object : ThirdPartyAPICredentials.get(0);

        } catch (Exception exception) {
            return object;
        }
    }

}
