package com.example.habit.entity;

/**
 * @author Sameer Gupta
 */
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name();
}
