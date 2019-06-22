package com.example.habit.request;

import com.example.habit.entity.Category;
import com.example.habit.entity.Goal;
import com.example.habit.entity.User;
import com.loadshare.network.bo.utils.DateRange;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author Sameer Gupta
 */
@Setter
@Getter
@ToString
public class WhatsappRequest {
    User user;
    Goal goal;
    Category category;
}
