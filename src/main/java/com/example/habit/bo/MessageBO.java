package com.example.habit.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Sameer Gupta
 */
@Setter
@Getter
@ToString
public class MessageBO implements Serializable {

    private static final long serialVersionUID = 1L;

    String fromMe;
    String author;
    BigInteger messageNumber;
    Date time;

}
