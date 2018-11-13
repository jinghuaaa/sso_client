/**
 * BBD Service Inc
 * All Rights Reserved @2018
 */
package com.bbd.bdsso.client.util;

import okhttp3.*;
import okio.BufferedSink;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OkHttp工具类
 *
 * @author tianyuliang
 * @version $Id: OkHttpUtil.java, v0.1 2018-07-09 11:50 tianyuliang Exp $$
 */
public class OkHttpUtil {

    public static final String  CHARSET_NAME              = "UTF-8";
    public static final int     RETRY_COUNT               = 3;
    public static final int     ERROR_CODE                = 900;
    public static final int     SUCCESS_CODE              = 200;
    public static final String  JSON_CONTENT_TYPE         = "application/json; charset=utf-8";
    private static final Logger logger                    = LoggerFactory.getLogger(OkHttpUtil.class);
    private static final String URL_ENCODeED_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static OkHttpUtil   instance                  = null;

    private OkHttpClient        okHttpClient              = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true).build();

    /***
     * 获取当前OkHttpUtil实例
     * @return
     */
    public static OkHttpUtil getInstance() {
        if (instance == null) {
            synchronized (OkHttpUtil.class) {
                if (instance == null) {
                    instance = new OkHttpUtil();
                }
            }
        }
        return instance;
    }

    //*****************************对外公布的方法****************************
    public static String getAsString(String url) throws IOException {
        return getInstance().getResponseBodyForGet(url);
    }

    public static String getAsString(String url, String key, String value) throws IOException {
        return getInstance().getResponseBodyForGet(url, key, value);
    }

    public static String getAsString(String url, List<BasicNameValuePair> params) throws IOException {
        return getInstance().getResponseBodyForGet(url, params);
    }

    public static String postAsString(String url, String content) throws IOException {
        return getInstance().getResponseBodyForPost(url, content);
    }

    public static String postAsString(String url, String content, String contentType) throws IOException {
        return getInstance().getResponseBodyForPost(url, content, contentType);
    }

    public static HttpResult postAsyn(String url, String postContent) {
        HttpResult result = new HttpResult();
        try {
            String responseBody = postAsString(url, postContent);
            result.setContent(responseBody);
            result.setStatusCode(SUCCESS_CODE);
        } catch (IOException e) {
            logger.error("post error. msg={}", e.getMessage(), e);
            result.setStatusCode(ERROR_CODE);
            result.setT(e);
        } catch (Exception e) {
            logger.error("post error. msg={}", e.getMessage(), e);
            result.setStatusCode(ERROR_CODE);
            result.setT(e);
        }

        return result;
    }

    // 简单封装：http://www.cnblogs.com/devli/p/5253360.html
    // 资料文档：https://www.ibm.com/developerworks/cn/java/j-lo-okhttp/
    public static void main(String[] args) throws IOException {

        String getUrl = "http://cn.bing.com/";
        String data111 = getAsString(getUrl);
        System.out.println("get.responseBody=\n\n\n" + data111);

        String postUrl = "http://cn.bing.com/";
        String data222 = postAsString(postUrl, "");
        System.out.println("\n\n\n\npost.responseBody=\n\n\n" + data222);

    }

    /**
     * 构建默认GET请求的Request
     *
     * @param url
     * @return
     * @throws IOException
     */
    private Request buildGetRequest(String url) throws IOException {
        return new Request.Builder().url(url).build();
    }

    /**
     * 构建默认GET请求的Request
     *
     * @param url
     * @return
     * @throws IOException
     */
    private Request buildGetRequest(String url, String key, String value) throws IOException {
        return new Request.Builder().url(attachHttpGetParam(url, key, value)).build();
    }

    /**
     * 构建默认GET请求的Request
     *
     * @param url
     * @return
     * @throws IOException
     */
    private Request buildGetRequest(String url, List<BasicNameValuePair> params) throws IOException {
        return new Request.Builder().url(attachHttpGetParams(url, params)).build();
    }

    /**
     * 构建默认POST请求的Request
     *
     * @param url
     * @return
     * @throws IOException
     */
    private Request buildPostRequest(String url, String content) throws IOException {
        return buildPostRequest(url, content, JSON_CONTENT_TYPE);
    }

    /**
     * 构建默认POST请求的Request
     *
     * @param url
     * @return
     * @throws IOException
     */
    private Request buildPostRequest(String url, String content, String contentType) throws IOException {
        return new Request.Builder().url(url).post(RequestBody.create(MediaType.parse(contentType), content)).build();
    }

    /**
     * 同步请求访问网络
     *
     * @param request
     * @return
     * @throws IOException
     */
    private Response execute(Request request) throws IOException {
        return okHttpClient.newCall(request).execute();
    }

    /**
     * 同步请求访问网络，添加重试机制
     *
     * @param request
     * @return
     * @throws IOException
     */
    private Response executeAndRetry(Request request, Integer retryCount) throws IOException {
        Response response = execute(request);

        // 添加重试N次机制
        AtomicInteger tryCount = new AtomicInteger(1);
        while (!response.isSuccessful() && tryCount.getAndIncrement() <= retryCount) {
            response = execute(request);
        }

        return response;
    }

    /**
     * 异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    private void enqueue(Request request, Callback responseCallback) {
        okHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     *
     * @param request
     */
    private void enqueue(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
            }

        });
    }

    /***
     * 获取普通"Get"请求的响应报文
     * @param url
     * @return
     * @throws IOException
     */
    private String getResponseBodyForGet(String url) throws IOException {
        Request request = buildGetRequest(url);
        Response response = executeAndRetry(request, RETRY_COUNT);

        // 添加重试机制
        AtomicInteger tryCount = new AtomicInteger(1);
        while (response.priorResponse() != null && !response.isSuccessful() && tryCount.getAndIncrement() <= RETRY_COUNT) {
            response = execute(request);
        }

        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            return responseBody;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 获取普通"Get"请求的响应报文
     * @param url 请求URL地址
     * @param key URL参数key
     * @param value URL参数value
     * @return
     * @throws IOException
     */
    private String getResponseBodyForGet(String url, String key, String value) throws IOException {
        Request request = buildGetRequest(url, key, value);
        Response response = executeAndRetry(request, RETRY_COUNT);

        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            return responseBody;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 获取普通"Get"请求的响应报文
     * @param url 请求URL地址
     * @param params URL参数键值队
     * @return
     * @throws IOException
     */
    private String getResponseBodyForGet(String url, List<BasicNameValuePair> params) throws IOException {
        Request request = buildGetRequest(url, params);
        Response response = executeAndRetry(request, RETRY_COUNT);

        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            return responseBody;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 获取普通"post"请求的响应报文
     * @param url 请求URL地址
     * @param content 请求报文内容(通常是json格式字符串)
     * @return
     * @throws IOException
     */
    private String getResponseBodyForPost(String url, String content) throws IOException {
        return getResponseBodyForPost(url, content, JSON_CONTENT_TYPE);
    }

    /**
     * 获取普通"post"请求的响应报文
     * @param url 请求URL地址
     * @param content 请求报文内容(通常是json格式字符串)
     * @param contentType 请求报文类型
     * @return
     * @throws IOException
     */
    private String getResponseBodyForPost(String url, String content, String contentType) throws IOException {
        Request request = buildPostRequest(url, content, contentType);
        Response response = executeAndRetry(request, RETRY_COUNT);

        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 这里使用了HttpClinet的API。只是为了方便
     * @param params
     * @return
     */
    private String formatUrlParams(List<BasicNameValuePair> params) {
        return URLEncodedUtils.format(params, CHARSET_NAME);
    }

    /**
     * 为HttpGet的url地址添加多个name value 参数。
     * @param url
     * @param params
     * @return
     */
    private String attachHttpGetParams(String url, List<BasicNameValuePair> params) {
        return url + "?" + formatUrlParams(params);
    }

    /**
     * 为HttpGet的url地址添加1个name value 参数。
     *
     * @param url
     * @param name
     * @param value
     * @return
     */
    private String attachHttpGetParam(String url, String name, String value) {
        return url + "?" + name + "=" + value;
    }

    public static class HttpResult {

        public static final int errorStatusCode = 900;

        private String          content;

        private int             statusCode      = errorStatusCode;

        private Throwable       t;

        public Throwable getT() {
            return t;
        }

        public void setT(Throwable t) {
            this.t = t;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public String toString() {
            return MessageFormat.format("httpStatusCode={0}|content={1}", statusCode, content);
        }
    }

}
