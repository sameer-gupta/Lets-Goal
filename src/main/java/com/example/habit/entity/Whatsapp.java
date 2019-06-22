package com.example.habit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.util.Date;

/**
 * @author Sameer Gupta
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Table(name = "whatsapp")
public class Whatsapp {

    @Column(name = "id")
    private BigInteger id;

    @Column(name = "user_contact")
    private String userContact;

    @Column(name = "message_number")
    private BigInteger messageNumber;

    @Column(name = "message_timestamp")
    private Date messageTimestamp;
}
