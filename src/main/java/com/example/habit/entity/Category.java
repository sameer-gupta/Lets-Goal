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
@Table(name = "category")
public class Category {

    @Column(name = "id")
    private BigInteger id;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "category")
    private String category;

    @Column(name = "sub_category")
    private String subCategory;

    @Column(name = "frequency")
    private String frequency;

    @Column(name = "created_at")
    private Date created_at;

}
