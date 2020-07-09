package com.hife.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hife.auth.WxEndpoint;
import com.riversoft.weixin.common.exception.WxRuntimeException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HttpclientUtil {

    private static Properties endpoints;

    private static synchronized void loadProperties() {
        if (endpoints == null) {
            try {
                Properties properties = new Properties();
                InputStream inputStream = WxEndpoint.class.getClassLoader().getResourceAsStream("httpclient.properties");
                properties.load(inputStream);
                endpoints = properties;
            } catch (IOException e) {
                throw new WxRuntimeException(999, "cannot find resource wx-app-endpoint.properties from classpath.");
            }
        }
    }

    public static String get(String key) {
        loadProperties();
        return endpoints.getProperty(key);
    }

    /**
     * Get请求数据
     */
    public static String httpGet(String url) {
        String result ="";
        try {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                HttpGet httpGet = new HttpGet( url);
                client = HttpClients.createDefault();
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
                System.out.println(result);
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Post发送json数据
     */
    @Test
    public static String httpPost(String url,JSONObject jsonParam) {
        String result ="";
        try {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                HttpPost httpPost = new HttpPost( url);
                httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
                httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(jsonParam),
                ContentType.create("text/json", "UTF-8")));
                client = HttpClients.createDefault();
                response = client.execute(httpPost);
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
                System.out.println("result="+result);
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
