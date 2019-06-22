package com.example.habit.PaymentEntities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@ToString
public class RefundBO implements Serializable {


    private String MID;
    private String TXNID;
    private String ORDERID;
    private BigDecimal TXNAMOUNT;
    private BigDecimal REFUNDAMOUNT;
    private Date TXNDATE;
    private String RESPCODE;
    private String RESPMSG;
    private String STATUS;
    private String REFID;
    private String CARD_ISSUER;
    private BigDecimal TOTALREFUNDAMT;
    private String REFUNDID;
}
