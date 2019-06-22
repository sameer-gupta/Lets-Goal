package com.example.habit.dao;


import com.example.habit.enums.PaymentStatus;
import com.example.habit.entity.Goal;
import com.example.habit.entity.RefundTransactionRequests;
import com.example.habit.entity.TransactionRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionRequestDAO {

    @Autowired
    @Qualifier("mysql")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert insertActor;


    public BigInteger insertJDBC(TransactionRequests request) {

        String query = DatabaseUtil.getInsertQuery(TransactionRequests.class);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(query, DatabaseUtil.getParameterSource(request), keyHolder,
                new String[]{"id"});
        return new BigInteger(keyHolder.getKey().toString());
    }


    public BigInteger insertJDBC(RefundTransactionRequests request) {

        String query = DatabaseUtil.getInsertQuery(RefundTransactionRequests.class);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(query, DatabaseUtil.getParameterSource(request), keyHolder,
                new String[]{"id"});
        return new BigInteger(keyHolder.getKey().toString());
    }

    public int updateStatusByOrderId(String orderId, String txnId, PaymentStatus status){

        String query = "update transaction_requests set status = :status, txn_id = :txnId where order_ref_id = :order_ref_id";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("status", status.name());
        source.addValue("order_ref_id", orderId);
        source.addValue("txnId", txnId);

        return jdbcTemplate.update(query,source);

    }


    public List<TransactionRequests> getByUserId(BigInteger userId) {

        String query = "select * from transaction_requests where user_id = :userId and status = :status";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("status", PaymentStatus.SUCCESS.name());
        source.addValue("userId", userId);

        List<TransactionRequests> transactionRequest = jdbcTemplate.query(query, source, new BeanPropertyRowMapper<>(TransactionRequests.class));
        if(CollectionUtils.isEmpty(transactionRequest)){
            return new ArrayList<TransactionRequests>();
        }

        return transactionRequest;
    }


    public int updateRefundAmountInTransaction(String orderId, BigDecimal refundAmount) {
        String query = "update transaction_requests set refund_amount = :refund_amount where order_ref_id = :order_ref_id";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("refund_amount", refundAmount);
        source.addValue("order_ref_id", orderId);
        return jdbcTemplate.update(query,source);
    }


    public int updateRefundDetailsByrefIdAnd(String refId, String txnId, PaymentStatus status){
        String query = "update refund_transaction_requests set status = :status, txn_id = :txnId where ref_id = :refId";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("status", status.name());
        source.addValue("refId", refId);
        source.addValue("txnId", txnId);

        return jdbcTemplate.update(query,source);
    }

    public TransactionRequests fetchTransactionByOrderAndUserId(String orderId, BigInteger userId) throws Exception{

        String query = "select * from transaction_requests where order_ref_id = :orderRefId and user_id = :userId";

        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("userId", userId);
        source.addValue("orderRefId", orderId);

         List<TransactionRequests> transactionRequest = jdbcTemplate.query(query, source, new BeanPropertyRowMapper<>(TransactionRequests.class));

         if(CollectionUtils.isEmpty(transactionRequest))
             return null;

         return transactionRequest.get(0);




    }





}
