package com.example.habit.PaymentEntities;

import com.mysql.jdbc.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class PaytmBO implements Serializable {

    private String MID ;
    private String ORDER_ID;
    private  String CUST_ID;
    private String MOBILE_NO;
    private String CHANNEL_ID;
    private String TXN_AMOUNT;
    private String WEBSITE ;
    private String INDUSTRY_TYPE_ID;
    private String CALLBACK_URL;
    private String CHECKSUMHASH;
}
