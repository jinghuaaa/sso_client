package com.bbd.bdsso.client.util;

import com.alibaba.fastjson.JSONObject;
import com.bbd.bdsso.client.enums.BdssoResultEnum;
import com.bbd.bdsso.client.exception.BdssoBaseException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author songyusheng
 * @version $ v 0.1  2018/7/7 15:28 songyusheng Exp $
 */
public class RequestUtil {

    public static CloseableHttpResponse processHttpRequest(Map<String, String> headerMap, String url, String requestMethod, Map<String, String> paramsMap) {
        List<BasicNameValuePair> formParams = new ArrayList<>();
        if ("post".equalsIgnoreCase(requestMethod)) {
            HttpPost httppost = new HttpPost(url);
            headerMap.forEach((k, v) -> httppost.setHeader(k, v));
            for (Iterator<String> it = paramsMap.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                String value = paramsMap.get(key);
                formParams.add(new BasicNameValuePair(key, value));
            }
            return doRequest(httppost, null, formParams);
        } else if ("get".equalsIgnoreCase(requestMethod)) {
            HttpGet httppost = new HttpGet(url);
            for (Iterator<String> it = paramsMap.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                String value = paramsMap.get(key);
                formParams.add(new BasicNameValuePair(key, value));
            }
            return doRequest(null, httppost, formParams);
        }
        return null;
    }

    private static CloseableHttpResponse doRequest(HttpPost httpPost, HttpGet httpGet, List<BasicNameValuePair> formparams) {
        try {
            CloseableHttpResponse response = null;
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams);
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(25000).setConnectTimeout(3000).build();
            if (null != httpPost) {
                uefEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(uefEntity);
                httpPost.setConfig(requestConfig);
                response = HttpClientUtil.getHttpClient().execute(httpPost);
            } else {
                httpGet.setConfig(requestConfig);
                response = HttpClientUtil.getHttpClient().execute(httpGet);
            }
            return response;
        } catch (ParseException e) {
            throw new BdssoBaseException(BdssoResultEnum.REQUEST_RPC_EXCEPTION, e.getMessage());
        } catch (IOException e) {
            throw new BdssoBaseException(BdssoResultEnum.REQUEST_RPC_EXCEPTION, e.getMessage());
        } finally {
        }
    }

    /**
     * 处理json格式的body post请求
     *
     * @return
     * @throws Exception
     * @throws ClientProtocolException
     */
    public static String processPostJson(String postUrl, JSONObject jsonObj) throws ClientProtocolException, Exception {
        // HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(postUrl);
        post.setHeader("Content-Type", "application/json");
        post.addHeader("Authorization", "Basic YWRtaW46");
        String str = null;
        StringEntity s = new StringEntity(jsonObj.toJSONString(), "utf-8");
        s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(25000).setConnectTimeout(3000).build();

        post.setEntity(s);
        post.setConfig(requestConfig);

        CloseableHttpResponse response = HttpClientUtil.getHttpClient().execute(post);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instreams = entity.getContent();
            str = convertStreamToString(instreams);
            post.abort();
        }
        // System.out.println(str);
        return str;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return sb.toString();
    }
}
