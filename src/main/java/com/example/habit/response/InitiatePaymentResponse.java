package com.example.habit.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.TreeMap;

@Setter
@Getter
@ToString
public class InitiatePaymentResponse implements Serializable {
    private String transactionUrl;
    private TreeMap<String, String> values;
    private TreeMap<String, String> headers;
    private String errorMessage;
}
