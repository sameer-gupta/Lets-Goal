package com.example.habit.response;

import com.example.habit.entity.Category;
import com.example.habit.entity.Goal;
import com.example.habit.entity.User;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author Sameer Gupta
 */
@Setter
@Getter
@ToString
public class DashboardResponse {
    User user;
    List<Pair<Goal,Category>> result;
    BigDecimal paidAmount;
    BigDecimal refundedAmount;
}
