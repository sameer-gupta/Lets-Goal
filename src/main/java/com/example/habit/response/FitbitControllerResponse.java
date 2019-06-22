package com.example.habit.response;

import com.example.habit.entity.Category;
import com.example.habit.entity.Goal;
import com.example.habit.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;

/**
 * @author Sameer Gupta
 */
@Setter
@Getter
@ToString
public class FitbitControllerResponse {

    User user;
    Goal goal;
    Category category;
    BigInteger steps;
}
