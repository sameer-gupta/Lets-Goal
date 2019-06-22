package com.example.habit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;

/**
 * @author Sameer Gupta
 */

@Setter
@Getter
@ToString
@NoArgsConstructor
@Table(name = "users")
public class User {

    private static final long serialVersionUID = 1L;

    @Column(name = "id")
    private BigInteger id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "customer_id")
    private String customerId;

}
