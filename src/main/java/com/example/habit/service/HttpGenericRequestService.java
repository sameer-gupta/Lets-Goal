package com.example.habit.service;

import com.example.habit.response.HttpHelperResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.loadshare.network.b2b.bo.GenericErrorBO;
import com.loadshare.network.bo.IObjectMapperJson;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sameer Gupta
 */
@Service
public class HttpGenericRequestService {

    private static Logger logger = LoggerFactory.getLogger(HttpGenericRequestService.class);

    private static final int TIME_OUT = 30;



    public <T, J> HttpHelperResponse<T, J> sendPost(String url, String data, Map<String, String> headerValues,
                                                    Class<T> t, Class<J> j) {
        logger.info("-------------------- About to send a http request to " + url + " with data= " + data
                + " -------------------HeaderValues"+ headerValues );
        HttpPost post = new HttpPost(url);
        populateHeader(headerValues, post);
        StringEntity requestEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);
        return fetchResponse(post, t, j, null, null);
    }

    public <T, J> HttpHelperResponse<T, J> sendGet(String url, Map<String, String> data,
                                                   Map<String, String> headerValues, Class<T> t, Class<J> j) {
        logger.info("-------------------- About to send a http request to " + url + " with data= " + data
                + " -------------------");
        String param = populateGetParam(data);
        if (param != null) {
            url = url + "?" + param;
        }
        HttpGet get = new HttpGet(url);
        populateHeader(headerValues, get);
        return fetchResponse(get, t, j, null, null);
    }

    private <T, J> HttpHelperResponse<T, J> fetchResponse(HttpUriRequest request, Class<T> t, Class<J> j, Type type,
                                                          Integer timeOut) {
        HttpHelperResponse<T, J> helperResponse = new HttpHelperResponse<>();
        try {
            Map<String, String> responseHeaderValues = new HashMap<>();

            RequestConfig requestConfig = null;
            if (timeOut != null) {
                requestConfig = RequestConfig.custom().setConnectTimeout(timeOut * 1000).build();
            } else {
                requestConfig = RequestConfig.custom().setConnectTimeout(TIME_OUT * 1000).build();
            }

            HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            long start = System.currentTimeMillis();
            HttpResponse response = client.execute(request);
            logger.info("Time taken for request {} is {}", request, (System.currentTimeMillis() - start));
            populateBackHeaderMap(response.getAllHeaders(), responseHeaderValues);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            Gson gObj = new Gson();
            try {
                logger.info("Response Result: {}", result);
                if (t == null && j == null)
                    return helperResponse;
                if (IObjectMapperJson.class.isAssignableFrom(t)) {

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

                    T resultJson = objectMapper.readValue(result.toString(), t);
                    helperResponse.setResponse(resultJson);

                    helperResponse.setHeaders(responseHeaderValues);
                } else {
                    T resultJson;
                    if (type == null) {
                        resultJson = gObj.fromJson(result.toString(), t);
                    } else {
                        resultJson = gObj.fromJson(result.toString(), type);
                    }

                    helperResponse.setResponse(resultJson);
                    helperResponse.setHeaders(responseHeaderValues);
                    logger.info("------------------- received http response {} -------------------------", resultJson);
                }

            } catch (Exception ex) {
                Gson gson = new GsonBuilder().setLenient().create();
                try {
                    J resultJson = gson.fromJson(result.toString(), j);
                    helperResponse.setError(resultJson);
                    helperResponse.setHeaders(responseHeaderValues);
                    logger.info("------------------- received http response {} -------------------------", resultJson);
                } catch (JsonIOException | JsonSyntaxException je) {
                    J resultJson = (J) result;
                    helperResponse.setError(resultJson);
                    helperResponse.setHeaders(responseHeaderValues);
                    logger.info("------------------- received http response {} -------------------------", resultJson);
                }
            }
        } catch (ClientProtocolException ce) {
            logger.error("ClientProtocolException: request: {}, Error: {}", request, ce);
            GenericErrorBO ge = new GenericErrorBO();
            ge.setErrorMessage(ce.getMessage());

            J resultJson = new Gson().fromJson(JsonHelper.toJson(ge), j);
            helperResponse.setError(resultJson);
        } catch (IOException ie) {
            logger.error("IOException : request: {} Error: {}", request, ie);
            GenericErrorBO ge = new GenericErrorBO();
            ge.setErrorMessage(ie.getMessage());

            J resultJson = new Gson().fromJson(JsonHelper.toJson(ge.toString()), j);
            helperResponse.setError(resultJson);
        } catch (Exception e) {
            logger.error("Exception : request: {}, {}", request, e);
            GenericErrorBO ge = new GenericErrorBO();
            ge.setErrorMessage(e.getMessage());

            J resultJson = new Gson().fromJson(JsonHelper.toJson(ge), j);
            helperResponse.setError(resultJson);
        }

        return helperResponse;
    }


    private void populateBackHeaderMap(Header[] headers, Map<String, String> headerValues) {
        if (headers == null || headers.length == 0)
            return;
        for (Header header : headers) {
            headerValues.put(header.getName(), header.getValue());
        }
        logger.info("headerValues: {}", headerValues);
    }


    private void populateHeader(Map<String, String> headerValues, HttpRequestBase get) {
        if (!CollectionUtils.isEmpty(headerValues)) {
            for (Map.Entry<String, String> entry : headerValues.entrySet()) {
                get.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private String populateGetParam(Map<String, String> param) {
        String url_param = null;
        int k = 0;
        if (param != null) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                if (k >= 1) {
                    url_param += "&" + entry.getKey() + "=" + entry.getValue();
                } else {
                    url_param = entry.getKey() + "=" + entry.getValue();
                }
                k = k + 1;
            }
        }
        return url_param;
    }

}
