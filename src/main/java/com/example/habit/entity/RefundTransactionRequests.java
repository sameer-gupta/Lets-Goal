package com.example.habit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;



@Setter
@Getter
@ToString
@NoArgsConstructor
@Table(name = "refund_transaction_requests")
public class RefundTransactionRequests {


    @Column(name = "id")
    private BigInteger id;

    @Column(name = "pg_name")
    private String pgName;

    @Column(name= "order_ref_id")
    private String orderRefId;

    @Column(name= "ref_id")
    private String refId;

    @Column(name = "user_id")
    private BigInteger userId;

    @Column(name = "status")
    private String status;

    @Column(name = "data")
    private String data;

    @Column(name = "txn_id")
    private String txnId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name ="created_at")
    private Date createdAt;

    @Column(name ="updated_at")
    private Date updatedAt;


}

