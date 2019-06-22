package com.example.habit.ServiceImpl;

import com.example.habit.enums.PaymentOptions;
import com.example.habit.enums.PaymentStatus;
import com.example.habit.PaymentEntities.PaytmBO;
import com.example.habit.PaymentEntities.RefundBO;
import com.example.habit.Utils.JsonHelper;
import com.example.habit.Utils.StaticStrings;
import com.example.habit.dao.PGConfigurationDAO;
import com.example.habit.dao.TransactionRequestDAO;
import com.example.habit.entity.PaymentGatewayConfiguration;
import com.example.habit.entity.RefundTransactionRequests;
import com.example.habit.entity.TransactionRequests;
import com.example.habit.request.InitiatePaymentRequest;
import com.example.habit.response.HttpHelperResponse;
import com.example.habit.response.InitiatePaymentResponse;
import com.example.habit.service.HttpGenericRequestService;
import com.example.habit.service.IPaymentService;
import com.google.gson.JsonObject;
import com.paytm.pg.merchant.CheckSumServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.TreeMap;

@Service
public class PaytmPaymentServiceImpl implements IPaymentService {

    @Autowired
    PGConfigurationDAO configurationDAO;

    @Autowired
    TransactionRequestDAO transactionRequestDAO;

    @Autowired
    HttpGenericRequestService service;

    Logger logger = LoggerFactory.getLogger(PaytmPaymentServiceImpl.class);

    @Override
    public String generateCheckSum(Object object) throws Exception {
       return null;
    }

    @Override
    public InitiatePaymentResponse initiatePayment(InitiatePaymentRequest request) throws Exception {
     PaymentGatewayConfiguration configuration = configurationDAO.getByPaymentOptions(request.getPaymentOptions());
        if(configuration== null){
            throw new Exception("No Configuration found for payment gateway");
        }

        PaytmBO bo = JsonHelper.fromJson(configuration.getData(), PaytmBO.class);
        TreeMap<String,String> parameters = new TreeMap<String,String>();
        parameters.put("MID",StaticStrings.PAYTM_MERCHANT_ID);
        parameters.put("ORDER_ID", request.getOrderId());
        parameters.put("CUST_ID",request.getUser().getCustomerId());
        parameters.put("CHANNEL_ID", request.getRequestType().name());
        parameters.put("INDUSTRY_TYPE_ID",StaticStrings.PAYTM_INDUSTRY_TYPE_ID);
        parameters.put("WEBSITE",StaticStrings.PAYTM_WEBSTRING);
        parameters.put("MOBILE_NO",request.getUser().getContactNumber());
        parameters.put("CALLBACK_URL", bo.getCALLBACK_URL());
        parameters.put("TXN_AMOUNT", request.getTxnAmount().toString());



        String checkSumHash =  CheckSumServiceHelper.getCheckSumServiceHelper().
               genrateCheckSum(StaticStrings.PAYTM_MERCHANT_KEY, parameters);

       parameters.put("CHECKSUMHASH", checkSumHash);

        TransactionRequests transactionRequests = new TransactionRequests();
        transactionRequests.setData(JsonHelper.convertToString(parameters));
        transactionRequests.setPgName(request.getPaymentOptions().name());
        transactionRequests.setOrderRefId(request.getOrderId());
        transactionRequests.setStatus(PaymentStatus.INITIATED.name());
        transactionRequests.setUser_id(request.getUser().getId());
        transactionRequests.setAmount(request.getTxnAmount());
        transactionRequests.setRefundAmount(BigDecimal.ZERO);

        transactionRequestDAO.insertJDBC(transactionRequests);

        InitiatePaymentResponse response = new InitiatePaymentResponse();
        response.setTransactionUrl("https://securegw-stage.paytm.in/theia/processTransaction");
        response.setValues(parameters);
        return response;



    }

    @Override
    public InitiatePaymentResponse refundPayment(InitiatePaymentRequest request) throws Exception {

        TransactionRequests trans = transactionRequestDAO.fetchTransactionByOrderAndUserId(request.getOrderId(),request.getUser().getId());
        if(trans == null )
            throw new Exception("Transaction not found for request");

        InitiatePaymentResponse refundRequest = populateRefundRequest(trans, request.getRefundOrderId(), request.getTxnAmount());

        HttpHelperResponse<JsonObject, JsonObject> response  = service.sendPost(refundRequest.getTransactionUrl(), JsonHelper.convertToString(refundRequest.getValues()), refundRequest.getHeaders(), JsonObject.class, JsonObject.class);

         logger.info("Response :" +response.getResponse());

         JsonObject bo=  response.getResponse();

         if(bo == null){
             bo = response.getError();
         }

         String status = bo.get("STATUS").getAsString();

         if(bo!= null &&
                 Arrays.asList(PaymentStatus.PENDING.name(), PaymentStatus.SUCCESS.name()).contains(
                         bo.get("STATUS").getAsString())){

            transactionRequestDAO.updateRefundAmountInTransaction(trans.getOrderRefId(),bo.get("TOTALREFUNDAMT").getAsBigDecimal());
            transactionRequestDAO.updateRefundDetailsByrefIdAnd(bo.get("REFID").getAsString(),bo.get("TXNID").getAsString(),PaymentStatus.SUCCESS);
        }else {
             if(bo != null) {
                 transactionRequestDAO.updateRefundDetailsByrefIdAnd(bo.get("REFID").getAsString(), bo.get("TXNID").getAsString(), PaymentStatus.FAILED);
             }
        }






        return refundRequest;

    }

      private  InitiatePaymentResponse  populateRefundRequest(TransactionRequests transactionRequests, String refId, BigDecimal amount) throws Exception{

          TreeMap<String,String> parameters = new TreeMap<String,String>();
          parameters.put("MID",StaticStrings.PAYTM_MERCHANT_ID);
          parameters.put("ORDERID", transactionRequests.getOrderRefId());
          parameters.put("REFID", refId);
          parameters.put("TXNID", transactionRequests.getTxnId());
          parameters.put("REFUNDAMOUNT",amount.toString());
          parameters.put("TXNTYPE", "REFUND");
          String checkSumHash =  CheckSumServiceHelper.getCheckSumServiceHelper().
                  genrateRefundCheckSum(StaticStrings.PAYTM_MERCHANT_KEY, parameters);

          parameters.put("CHECKSUM", checkSumHash);

          TreeMap<String,String> headers = new TreeMap<String,String>();
          RefundTransactionRequests refundTransactionRequests = new RefundTransactionRequests();
          refundTransactionRequests.setData(JsonHelper.convertToString(parameters));
          refundTransactionRequests.setPgName(transactionRequests.getPgName());
          refundTransactionRequests.setOrderRefId(transactionRequests.getOrderRefId());
          refundTransactionRequests.setStatus(PaymentStatus.INITIATED.name());
          refundTransactionRequests.setUserId(transactionRequests.getUser_id());
          refundTransactionRequests.setAmount(amount);
          refundTransactionRequests.setRefId(refId);

          transactionRequestDAO.insertJDBC(refundTransactionRequests);

          InitiatePaymentResponse response = new InitiatePaymentResponse();
          response.setTransactionUrl("https://securegw-stage.paytm.in/refund/process");
          response.setValues(parameters);
          response.setHeaders(headers);
          return response;

      }


}
