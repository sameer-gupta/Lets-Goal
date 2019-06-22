package com.example.habit.service;

import com.example.habit.enums.GoalCategory;
import com.example.habit.enums.GoalSubCategory;
import com.example.habit.enums.PaymentOptions;
import com.example.habit.ServiceImpl.PaytmPaymentServiceImpl;
import com.example.habit.dao.CategoryDAO;
import com.example.habit.dao.GoalDAO;
import com.example.habit.dao.UserDAO;
import com.example.habit.entity.Category;
import com.example.habit.entity.Column;
import com.example.habit.entity.Goal;
import com.example.habit.entity.User;
import com.example.habit.bo.MessageBO;
import com.example.habit.bo.StepDataPacketBO;
import com.example.habit.dao.*;
import com.example.habit.entity.*;
import com.example.habit.request.*;
import com.example.habit.response.*;
import com.example.habit.enums.FrequencyType;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.habit.enums.FrequencyType.*;

/**
 * @author Sameer Gupta
 */

@Service
public class CommonService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    CategoryDAO categoryDAO;

    @Autowired
    GoalDAO goalDAO;

    @Autowired
    PaytmPaymentServiceImpl paymentService;

    @Autowired
    WhatsappDAO whatsappDAO;

    @Autowired
    ThirdPartyAPICredentialDAO thirdPartyAPICredentialDAO;

    @Autowired
    HttpGenericRequestService httpHelper;

    @Autowired
    TransactionRequestDAO transactionRequestDAO;


    private static Logger logger = LoggerFactory.getLogger(CommonService.class);


    public InitiatePaymentResponse createGoals(CreateGoalRequest request) {
        InitiatePaymentResponse initiatePaymentResponse = new InitiatePaymentResponse();
        String orderId = generateCode(request.getUser().getId().toString(), System.currentTimeMillis()+""); // need to generate Random id here for order xyz.

        User user = request.getUser();
        try {
            // need Changes based on selected category with subcategory.

            for(Goal goal : request.getGoals()) {
                goal.setUserId(user.getId());
                goal.setIsActive(false);
                goal.setIsDeleted(false);
                goal.setCreatedAt(new Date());
                goal.setUpdatedAt(new Date());
                goal.setOrderRefId(orderId);

                if (goal.getTargetAmount().compareTo(BigDecimal.ONE) < 0) {
                    logger.info("Amount should be more than zero");
                    continue;
                }

                goalDAO.insertGoal(goal);

            }
            logger.info("Goal created Successfully");
            InitiatePaymentRequest paymentRequest = populatePaymentRequest(request, orderId);
           initiatePaymentResponse =  processTransaction(paymentRequest);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return initiatePaymentResponse;
    }


    private InitiatePaymentRequest populatePaymentRequest(CreateGoalRequest request, String orderId){
        InitiatePaymentRequest paymentRequest = new InitiatePaymentRequest();
        paymentRequest.setUser(request.getUser());
        paymentRequest.setCustomerId(request.getUser().getCustomerId());
        paymentRequest.setOrderId(orderId);
        paymentRequest.setPaymentOptions(request.getPaymentType());
        paymentRequest.setRequestType(request.getRequestType());
        paymentRequest.setTxnAmount(request.getGoals().stream().filter(x -> x.getTargetAmount().compareTo(BigDecimal.ONE) >= 0).
                map(x ->  x.getTargetAmount()).reduce(BigDecimal.ZERO ,BigDecimal::add));
        return paymentRequest;

    }

    public InitiatePaymentResponse processTransaction(InitiatePaymentRequest request) throws Exception{

        IPaymentService service = getService(request.getPaymentOptions());

        return  service.initiatePayment(request);
    }


    IPaymentService getService(PaymentOptions options) throws  Exception {
        if(PaymentOptions.PAYTM.equals(options)){
            return paymentService;
        }

        throw new Exception("NO Implementation found");
    }


    public static String generateCode(String prefix, String suffix) {
        StringBuilder code = new StringBuilder();
        if (!StringUtils.isEmpty(prefix))
            code.append(prefix);
        DateFormat sdf = new SimpleDateFormat("ddMMyyHHmmss");
        Random random = new Random();
        code.append(sdf.format(new Date()));
        code.append(random.nextInt(10));
        if (!StringUtils.isEmpty(suffix))
            code.append(suffix);
        return code.toString();
    }

    public DashboardResponse getDashboardData(DashboardRequest request){

        DashboardResponse response = new DashboardResponse();
        BigInteger userId = request.getUser().getId();
        List<Pair<Goal,Category>> result = new ArrayList<>();

        try {

            List<Goal> goals = goalDAO.getByUserId(userId);

            for(Goal goal : goals){
                Category myCategory = categoryDAO.getById(goal.getCategoryId());
                switch (GoalCategory.valueOf(myCategory.getCategory())){
                    case WHATSAPP:
                        WhatsappRequest request1 = new WhatsappRequest();
                        request1.setUser(request.getUser());
                        request1.setCategory(myCategory);
                        request1.setGoal(goal);
                        WhatsappResponse response1 = getWhatsappData(request1);
                        Pair<Goal,Category> pair = new Pair(response1.getGoal(),response1.getCategory());
                        result.add(pair);
                        break;
                    case FITBIT:
                        FitbitRequest request2 = new FitbitRequest();
                        request2.setUser(request.getUser());
                        request2.setCategory(myCategory);
                        request2.setGoal(goal);
                        FitbitControllerResponse response2 = getFitbitData(request2);
                        Pair<Goal,Category> pair2 = new Pair(response2.getGoal(),response2.getCategory());
                        result.add(pair2);
                        break;
                    default:
                        break;
                }
            }

             List<TransactionRequests>  transactionRequests = transactionRequestDAO.getByUserId(userId);
                if(CollectionUtils.isEmpty(transactionRequests)){
                    response.setPaidAmount(BigDecimal.ZERO);
                    response.setRefundedAmount(BigDecimal.ZERO);
                }else{

                    BigDecimal paid = BigDecimal.ZERO;
                    BigDecimal refunded = BigDecimal.ZERO;
                    for( TransactionRequests req : transactionRequests){
                        paid = paid.add(req.getAmount());
                        refunded = refunded.add(req.getRefundAmount());

                    }


                    response.setPaidAmount(paid);
                    response.setRefundedAmount(refunded);


                }


        }catch (Exception e){
            e.printStackTrace();
        }
        response.setResult(result);
        response.setUser(request.getUser());
        return response;

    }


    public FitbitControllerResponse getFitbitData(FitbitRequest request){

        FitbitControllerResponse response = new FitbitControllerResponse();

        BigInteger goalId = request.getGoal().getId();
        BigInteger userId = request.getUser().getId();
        try{

            Goal myGoal = goalDAO.getById(goalId);
            BigInteger categoryId = myGoal.getCategoryId();
            Category myCategory = request.getCategory();
            if(myCategory==null){
                myCategory = categoryDAO.getById(categoryId);
            }

            if(!myCategory.getCategory().equals(GoalCategory.FITBIT.toString())){
                logger.error("in getFitbitData : wrong category");
                return null;
            }


            ThirdPartyAPICredential credential = thirdPartyAPICredentialDAO.fetchByOrgCode("FITBIT");

            Date end = new Date();
            Date start;
            Calendar cal = Calendar.getInstance();
            String ptr = "";

            switch (FrequencyType.valueOf(myCategory.getFrequency())) {
                case DAILY:
                    cal.add(Calendar.DATE, +1);
                    start = cal.getTime();
                    ptr = "1d";
                    break;
                case WEEKLY:
                    cal.add(Calendar.DATE, -7);
                    start = cal.getTime();
                    ptr = "7d";
                    break;
                case MONTHLY:
                    cal.add(Calendar.MONTH, -1);
                    start = cal.getTime();
                    ptr = "1m";
                    break;
                default:
                    cal.add(Calendar.DATE, -1);
                    start = cal.getTime();
                    break;
            }

            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            String startDate = simpleDateFormat.format(start);
            String endDate = simpleDateFormat.format(end);

            String url = "https://api.fitbit.com/1/user/-/activities/steps/date/" + endDate + "/" + ptr + ".json";

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", credential.getTokenId());
            headers.put("accept", "application/x-www-form-urlencoded");


            HttpHelperResponse<FitbitResponse, FitbitResponse> fitbitResp = httpHelper.sendGet(url, null, headers, FitbitResponse.class, FitbitResponse.class);

            Integer count = 0;

            FitbitResponse temp = fitbitResp.getResponse();
            if(temp!=null && temp.getActivitiesSteps()!=null) {
                for (StepDataPacketBO i : temp.getActivitiesSteps()) {
                    count += Integer.parseInt(i.getValue());
                }
            }

            myGoal.setCompletedGoal(new BigInteger(count.toString()));
            myGoal.setUpdatedAt(new Date());

            goalDAO.update(myGoal);

            if(myGoal.getCompletedGoal().compareTo(myGoal.getTargetGoal()) >= 0){
                InitiatePaymentRequest paymentRequest = new InitiatePaymentRequest();
                paymentRequest.setOrderId(myGoal.getOrderRefId());
                paymentRequest.setUser(request.getUser());
                paymentRequest.setTxnAmount(myGoal.getTargetAmount());
                paymentRequest.setPaymentOptions(PaymentOptions.PAYTM);
                try {
                    refundProcess(paymentRequest);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }




            response.setUser(request.getUser());
            response.setGoal(myGoal);
            response.setCategory(myCategory);

        }catch (Exception e){
            e.printStackTrace();
            response.setSteps(new BigInteger("12"));
        }
        return response;

    }


    public WhatsappResponse getWhatsappData(WhatsappRequest request){

        WhatsappResponse response = new WhatsappResponse();

        BigInteger goalId = request.getGoal().getId();
        BigInteger userId = request.getUser().getId();
        String userContact = request.getUser().getContactNumber();

        BigInteger count = BigInteger.ZERO;
        try{

            Goal myGoal = goalDAO.getById(goalId);
            BigInteger categoryId = myGoal.getCategoryId();
            Category myCategory = request.getCategory();
            if(myCategory==null){
                myCategory = categoryDAO.getById(categoryId);
            }

            if(!myCategory.getCategory().equals(GoalCategory.WHATSAPP.toString())){
                logger.error("in getWhatsappData : wrong category");
                return null;
            }

            Date end = new Date();
            Date start;
            Calendar cal = Calendar.getInstance();
            switch (FrequencyType.valueOf(myCategory.getFrequency())) {
                case DAILY:
                    cal.add(Calendar.DATE, -1);
                    start = cal.getTime();
                    break;
                case WEEKLY:
                    cal.add(Calendar.DATE, -7);
                    start = cal.getTime();
                    break;
                case MONTHLY:
                    cal.add(Calendar.MONTH, -1);
                    start = cal.getTime();
                    break;
                default:
                    cal.add(Calendar.DATE, -1);
                    start = cal.getTime();
                    break;
            }

            switch (GoalSubCategory.valueOf(myCategory.getSubCategory())){
                case WHATSAPP_MAX_NUMBER_OF_MESSAGES:
                    count = whatsappDAO.countWhatsappMessages(userContact,start, end);
                    break;
                default:
                    break;
            }

            if(count.compareTo(BigInteger.ZERO)!=0){
                myGoal.setCompletedGoal(count);
            }
            myGoal.setUpdatedAt(new Date());
            Date now =new Date();

            if(myGoal.getTargetDate() != null && now.compareTo(myGoal.getTargetDate())>0 && myGoal.getCompletedGoal().compareTo(myGoal.getTargetGoal()) < 0){
                InitiatePaymentRequest paymentRequest = new InitiatePaymentRequest();
                paymentRequest.setOrderId(myGoal.getOrderRefId());
                paymentRequest.setUser(request.getUser());
                paymentRequest.setTxnAmount(myGoal.getTargetAmount());
                paymentRequest.setPaymentOptions(PaymentOptions.PAYTM);
                try {
                    refundProcess(paymentRequest);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }


            response.setUser(request.getUser());
            response.setGoal(myGoal);
            response.setCategory(myCategory);

        } catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }


    public void logWhatsappCallback(whatsappCallbackRequest request){

        MessageBO messageBO = request.getMessages().get(0);

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.setUserContact(messageBO.getAuthor().substring(2,12));
        whatsapp.setMessageNumber(messageBO.getMessageNumber());
        whatsapp.setMessageTimestamp(new Date(messageBO.getTime().getTime()*1000));

        try{
            whatsappDAO.insertObject(whatsapp);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void initGithub(){
        HttpHelperResponse<GithubResponse, GithubResponse> githubResponse =
                httpHelper.sendGet("https://api.github.com/users/github-noob?client_id=2bf5290d25f6b780dd70&client_secret=eb06dc36d1dfd9e1082498165b2b04fbabeaed30", null, null,
                        GithubResponse.class, GithubResponse.class);

    }

    public void getGithubCommits(String url){
        HttpHelperResponse<List, List> githubResponse =
                httpHelper.sendGet("https://api.github.com/repos/github-noob/test/commits", null, null,
                        List.class, List.class);
    }


    public InitiatePaymentResponse refundProcess(InitiatePaymentRequest request) throws Exception {
         request.setRefundOrderId(generateCode(request.getOrderId(), request.getUser().getId().toString()));

        return getService(request.getPaymentOptions()).refundPayment(request);


    }










}
