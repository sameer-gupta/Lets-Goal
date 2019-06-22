package com.example.habit.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author Sameer Gupta
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Table(name = "third_party_api_credentials")
public class ThirdPartyAPICredential implements Serializable {

    private static final long serialVersionUID = 9175061842947316259L;
    @Column(name = "id")
    private BigInteger id;

    @Column(name = "token_id")
    private String tokenId;

    @Column(name = "organisation_code")
    private String organisationCode;

    @Column(name = "customer_id")
    private BigInteger customerId;
}