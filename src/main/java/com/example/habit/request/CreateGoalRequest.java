package com.example.habit.request;

import com.example.habit.enums.PaymentOptions;
import com.example.habit.enums.PaymentRequestType;
import com.example.habit.entity.Category;
import com.example.habit.entity.Goal;
import com.example.habit.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Locale;

/**
 * @author Sameer Gupta
 */
@Getter
@Setter
@ToString
public class CreateGoalRequest {

    User user;
    List<Goal> goals;
    PaymentOptions paymentType;
    PaymentRequestType requestType;

}
