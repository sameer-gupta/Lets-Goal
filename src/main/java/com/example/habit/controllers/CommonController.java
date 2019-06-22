package com.example.habit.controllers;

import com.example.habit.Utils.StaticStrings;
import com.example.habit.dao.UserDAO;
import com.example.habit.entity.User;
import com.example.habit.request.*;
import com.example.habit.response.FitbitControllerResponse;
import com.example.habit.response.InitiatePaymentResponse;
import com.example.habit.response.LoginResponse;
import com.example.habit.response.CreateGoalResponse;
import com.example.habit.response.*;
import com.example.habit.service.CommonService;
import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;

/**
 * @author Sameer Gupta
 */


@RestController
@RequestMapping("/v1")
public class CommonController {

    @Autowired
    UserDAO userDAO;

    @Autowired
    CommonService commonService;



    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public LoginResponse getPendingBalance(HttpServletRequest httpRequest,
                                           @RequestBody LoginRequest request) {
        LoginResponse response = new LoginResponse();
        try {
            User user = userDAO.getByUsername(request.getUsername(), request.getPassword());
            response.setUser(user);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    @RequestMapping(path = "/createGoal", method = RequestMethod.POST)
    public InitiatePaymentResponse createGoals(HttpServletRequest httpRequest, @RequestBody CreateGoalRequest request) {
        InitiatePaymentResponse response = new InitiatePaymentResponse();
        try {
            User user = userDAO.getById(BigInteger.ONE);
            if(user == null ){
                response.setErrorMessage("User does not exist in the system");
                return response;
            }
            request.setUser(user);
            response = commonService.createGoals(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @RequestMapping(path = "/dashboard", method = RequestMethod.POST)
    public DashboardResponse getdashboardData(HttpServletRequest httpRequest, @RequestBody DashboardRequest request) {
        DashboardResponse response = new DashboardResponse();
        try {
            response = commonService.getDashboardData(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    @RequestMapping(path = "/refund/process", method = RequestMethod.POST)
    public InitiatePaymentResponse getFitbitData(HttpServletRequest httpRequest, @RequestBody InitiatePaymentRequest request) {

        try {
           return  commonService.refundProcess(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @RequestMapping(path = "/whatsapp/messageCount", method = RequestMethod.POST)
    public WhatsappResponse getwhatsappMessageCount(HttpServletRequest httpRequest, @RequestBody WhatsappRequest request) {
        WhatsappResponse response = new WhatsappResponse();
        try {
            response = commonService.getWhatsappData(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    @RequestMapping(path = "/whatsapp/callback", method = RequestMethod.POST)
    public void logWhatsappCallback(HttpServletRequest httpRequest, @RequestBody whatsappCallbackRequest request) {
        try {
            commonService.logWhatsappCallback(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(path = "/fitbit/callback", method = RequestMethod.POST)
    public FitbitControllerResponse githubCallback(HttpServletRequest httpRequest, @RequestBody FitbitRequest request) {
        FitbitControllerResponse response = new FitbitControllerResponse();
        try {
            return commonService.getFitbitData(request);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }


}
