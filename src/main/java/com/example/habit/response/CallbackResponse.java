package com.example.habit.response;

import com.example.habit.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class CallbackResponse implements Serializable {

    private String errorMessage;
    private PaymentStatus status;



}
