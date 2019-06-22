package com.example.habit.controllers;


import com.example.habit.Utils.JsonHelper;
import com.example.habit.enums.PaymentStatus;
import com.example.habit.Utils.StaticStrings;
import com.example.habit.dao.GoalDAO;
import com.example.habit.dao.TransactionRequestDAO;
import com.example.habit.response.CallbackResponse;
import com.example.habit.service.CommonService;
import com.google.gson.JsonObject;
import com.paytm.pg.merchant.CheckSumServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/callback")
public class CallbackController {

    @Autowired
    TransactionRequestDAO transactionRequestDAO;

    @Autowired
    GoalDAO goalDAO;

    Logger logger = LoggerFactory.getLogger(CallbackController.class);


    @RequestMapping(value ="/response", method = RequestMethod.POST)
    public CallbackResponse verifyCallbackResponseAndReturn(HttpServletRequest request) throws Exception {
        CallbackResponse response = new CallbackResponse() ;
         String merchantKey = StaticStrings.PAYTM_MERCHANT_KEY;
         String paytmChecksum = null;
// Create a tree map from the form post param
        TreeMap<String, String> paytmParams = new TreeMap<String, String>();
// Request is HttpServletRequest
        for (Map.Entry<String, String[]> requestParamsEntry : request.getParameterMap().entrySet()) {
            if ("CHECKSUMHASH".equalsIgnoreCase(requestParamsEntry.getKey())){
                paytmChecksum = requestParamsEntry.getValue()[0];
            } else {
                paytmParams.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
            }
        }

        logger.info("PAYTM RESPONSE : "+ paytmParams);

        if(!paytmParams.containsKey("ORDERID")){
            response.setErrorMessage("Something went wrong.");
            return response;
        }

// Call the method for verification
        boolean isValidChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum(merchantKey, paytmParams, paytmChecksum);
// If isValidChecksum is false, then checksum is not valid
        String errorMsg = paytmParams.get("RESPMSG");
        PaymentStatus status;
        if(isValidChecksum){
            System.out.append("Checksum Matched");
            if(paytmParams.get("RESPCODE").equals("01")) {
                status = PaymentStatus.SUCCESS;
            }else{
                if(paytmParams.get("STATUS").equals(PaymentStatus.PENDING.name())){
                    status = PaymentStatus.PENDING;
                }else{
                    status = PaymentStatus.FAILED;
                }
            }
            goalDAO.makeItActive(paytmParams.get("ORDERID"));

        }else{
            status = PaymentStatus.FAILED;
            System.out.append("Checksum MisMatch");
        }

        transactionRequestDAO.updateStatusByOrderId(paytmParams.get("ORDERID"), paytmParams.get("TXNID"), status);
        response.setErrorMessage(errorMsg);
        response.setStatus(status);
         return response;

    }

    @RequestMapping(value ="/response1", method = RequestMethod.POST)
    public CallbackResponse verifyCallbackResponseAndReturn(HttpServletRequest request, @RequestBody String object) throws Exception {
        CallbackResponse response = new CallbackResponse() ;
        JsonObject json =  JsonHelper.toObject(object, JsonObject.class);
        Map<String, Object> mapp= JsonHelper.toMap(json);
        String merchantKey = StaticStrings.PAYTM_MERCHANT_KEY;
        String paytmChecksum = null;
// Create a tree map from the form post param
        TreeMap<String, String> paytmParams = new TreeMap<String, String>();
// Request is HttpServletRequest
        for (Map.Entry<String, Object> requestParamsEntry : mapp.entrySet()) {
            if ("CHECKSUMHASH".equalsIgnoreCase(requestParamsEntry.getKey())){
                paytmChecksum = (String)requestParamsEntry.getValue();
            } else {
                paytmParams.put(requestParamsEntry.getKey(), (String)requestParamsEntry.getValue());
            }
        }

        logger.info("PAYTM RESPONSE : "+ paytmParams);

        if(!paytmParams.containsKey("ORDERID")){
            response.setErrorMessage("Something went wrong.");
            return response;
        }

// Call the method for verification
        boolean isValidChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum(merchantKey, paytmParams, paytmChecksum);
// If isValidChecksum is false, then checksum is not valid
        String errorMsg = paytmParams.get("RESPMSG");
        PaymentStatus status;
        if(isValidChecksum){
            System.out.append("Checksum Matched");
            if(paytmParams.get("RESPCODE").equals("01")) {
                status = PaymentStatus.SUCCESS;
            }else{
                if(paytmParams.get("STATUS").equals(PaymentStatus.PENDING.name())){
                    status = PaymentStatus.PENDING;
                }else{
                    status = PaymentStatus.FAILED;
                }
            }
            goalDAO.makeItActive(paytmParams.get("ORDERID"));

        }else{
            status = PaymentStatus.FAILED;
            System.out.append("Checksum MisMatch");
        }

        transactionRequestDAO.updateStatusByOrderId(paytmParams.get("ORDERID"), paytmParams.get("TXNID"), status);
        response.setErrorMessage(errorMsg);
        response.setStatus(status);
        return response;

    }

}
