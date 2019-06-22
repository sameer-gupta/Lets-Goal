package com.example.habit.response;

import com.example.habit.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author Sameer Gupta
 */
@Getter
@Setter
@ToString
public class LoginResponse implements Serializable {

    User user;


}
