package com.example.habit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Sameer Gupta
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Table(name = "goal")
public class Goal {

    @Column(name = "id")
    private BigInteger id;

    @Column(name = "user_id")
    private BigInteger userId;

    @Column(name = "category_id")
    private BigInteger categoryId;

    @Column(name = "target_date")
    private Date targetDate;

    @Column(name = "target_amount")
    private BigDecimal targetAmount;

    @Column(name = "target_goal")
    private BigInteger targetGoal;

    @Column(name = "completed_goal")
    private BigInteger completedGoal;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "order_ref_id")
    private String orderRefId;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

}
