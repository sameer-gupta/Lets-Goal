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
@Table(name = "transaction_requests")
public class TransactionRequests {

    @Column(name = "id")
    private BigInteger id;

    @Column(name = "pg_name")
    private String pgName;

    @Column(name= "order_ref_id")
    private String orderRefId;

    @Column(name = "user_id")
    private BigInteger user_id;

    @Column(name = "status")
    private String status;

    @Column(name = "data")
    private String data;

    @Column(name = "txn_id")
    private String txnId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Column(name ="created_at")
    private Date createdAt;

    @Column(name ="updated_at")
    private Date updatedAt;


}
