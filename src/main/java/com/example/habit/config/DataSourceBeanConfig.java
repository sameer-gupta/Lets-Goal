package com.example.habit.config;


import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DataSourceBeanConfig {

    private static Logger LOG = LoggerFactory.getLogger(DataSourceBeanConfig.class);

    @Autowired
    Environment env;

    public static final String DEFAULT_REGION = "us-east-1";

    @Bean("mysqlds")
    public DataSource getDataSourceMysql() {
        DataSource datasource = new DataSource();

        LOG.info("URL : " + env.getProperty("spring.datasource.mysql.url"));
        datasource.setUrl(env.getProperty("spring.datasource.mysql.url"));
        datasource.setUsername(env.getProperty("spring.datasource.mysql.username"));
        datasource.setPassword(env.getProperty("spring.datasource.mysql.password"));
        datasource.setDriverClassName(env.getProperty("spring.datasource.mysql.driver"));
        datasource.setMaxActive(
                Integer.valueOf(env.getProperty("spring.datasource.mysql.maxactive")));
        datasource.setMaxIdle(Integer.valueOf(env.getProperty("spring.datasource.mysql.maxidle")));
        datasource.setMaxWait(Integer.valueOf(env.getProperty("spring.datasource.mysql.maxwait")));
        datasource.setRemoveAbandoned(
                Boolean.valueOf(env.getProperty("spring.datasource.mysql.removeabandoned")));
        datasource.setRemoveAbandonedTimeout(
                Integer.valueOf(env.getProperty("spring.datasource.mysql.removeabandonedtimeout")));
        datasource.setDefaultAutoCommit(
                Boolean.valueOf(env.getProperty("spring.datasource.mysql.defaultautocommit")));
        datasource.setValidationQuery(env.getProperty("spring.datasource.mysql.validationquery"));
        datasource.setValidationInterval(
                Long.valueOf(env.getProperty("spring.datasource.mysql.validationinterval")));
        datasource.setTestOnBorrow(
                Boolean.valueOf(env.getProperty("spring.datasource.mysql.testonborrow")));
        datasource.setJdbcInterceptors(env.getProperty("spring.datasource.mysql.jdbcinterceptors"));
        return datasource;
    }

    @Bean("mysql")
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplateMysql(@Qualifier("mysqlds") DataSource dataSource) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        return template;
    }
}
