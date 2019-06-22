package com.example.habit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;

@Setter
@Getter
@ToString
@NoArgsConstructor
@Table(name = "payment_gateway_configuration")
public class PaymentGatewayConfiguration {

    @Column(name = "id")
    private BigInteger id;

    @Column(name = "pg_name")
    private String pgName;

    @Column(name = "data")
    private String data;


}
