package com.example.habit.request;

import com.example.habit.entity.Category;
import com.example.habit.entity.Goal;
import com.example.habit.entity.User;
import com.example.habit.enums.FrequencyType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Sameer Gupta
 */
@Setter
@Getter
public class FitbitRequest {
    User user;
    Goal goal;
    Category category;
}
