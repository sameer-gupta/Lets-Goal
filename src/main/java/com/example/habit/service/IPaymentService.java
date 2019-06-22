package com.example.habit.service;

import com.example.habit.enums.PaymentOptions;
import com.example.habit.request.InitiatePaymentRequest;
import com.example.habit.response.InitiatePaymentResponse;

public interface IPaymentService {

     String generateCheckSum(Object  object) throws  Exception;

     InitiatePaymentResponse initiatePayment(InitiatePaymentRequest request) throws Exception;


     InitiatePaymentResponse refundPayment(InitiatePaymentRequest request) throws Exception;





}
