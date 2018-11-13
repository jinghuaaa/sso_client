/**
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client;

import com.bbd.bdsso.client.constants.SsoConstant;
import com.bbd.bdsso.client.exception.BdssoBaseException;
import com.bbd.bdsso.client.service.impl.SsoAccessServiceImpl;
import com.bbd.bdsso.client.util.MixAllUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * BDSSO权限拦截器
 *
 * @author byron
 * @version $Id: SsoFilter.java, v 0.1 Oct 24, 2017 3:49:24 PM byron Exp $
 */
public class SsoFilter implements Filter {

    private static final Logger LOGGER     = LoggerFactory.getLogger(SsoFilter.class);

    /** 属性 */
    public static Properties    properties = new Properties();

    /** SSO服务地址 */
    private String              serverUrl;
    /** 应用名 */
    private String              appName;
    /** 应用回调地址 */
    private String              callback;
    /** SSO API地址 */
    private String              ssoApi;
    /** SSO校验开关  **/
    private Boolean             enable;

    /**
     * 获取cookie对应的map
     *
     * @param request       请求
     * @return
     */
    public static Map<String, Cookie> getCookies(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

    /**
     * 清除cookie
     *
     * @param request       请求
     */
    private static void cleanCookie(HttpServletRequest request, HttpServletResponse httpResponse) {
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), SsoConstant.TICKET)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    httpResponse.addCookie(cookie);
                    LOGGER.info("clean cookie success. name={}, value={}", cookie.getName(), cookie.getValue());
                }
            }
        }
    }

    /**
     * 构建SsoLoginUrl
     * @param serverUrl
     * @param appName
     * @return
     */
    private static String buildSsoLoginUrl(String serverUrl, String appName) {
        String formatter = "%s?appName=%s&_current=%s";
        // 如果已经带了参数，那么就追加参数
        if (serverUrl.indexOf("?") > 0) {
            formatter = "%s&appName=%s&_current=%s";
        }
        return String.format(formatter, serverUrl, appName, System.currentTimeMillis());
    }

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("ssoFilter init ...");

        // 非空校验
        String formatter = "[bdsso-client] '%s' is blank";
        serverUrl = filterConfig.getInitParameter(SsoConstant.SERVER_URL);
        if (StringUtils.isBlank(serverUrl)) {
            throw new BdssoBaseException(String.format(formatter, SsoConstant.SERVER_URL));
        }

        appName = filterConfig.getInitParameter(SsoConstant.APP_NAME);
        if (StringUtils.isBlank(appName)) {
            throw new BdssoBaseException(String.format(formatter, SsoConstant.APP_NAME));
        }
        callback = filterConfig.getInitParameter(SsoConstant.CALLBACK);
        if (StringUtils.isBlank(callback)) {
            throw new BdssoBaseException(String.format(formatter, SsoConstant.CALLBACK));
        }

        ssoApi = filterConfig.getInitParameter(SsoConstant.SSO_API);
        if (StringUtils.isBlank(ssoApi)) {
            throw new BdssoBaseException(String.format(formatter, SsoConstant.SSO_API));
        }

        // 设置SSO校验开关
        enable = MixAllUtil.asBoolean(filterConfig.getInitParameter(SsoConstant.SSO_ENABLE), true);

        // 设置属性
        properties.setProperty(SsoConstant.SERVER_URL, serverUrl);
        properties.setProperty(SsoConstant.CALLBACK, callback);
        properties.setProperty(SsoConstant.APP_NAME, appName);
        properties.setProperty(SsoConstant.SSO_API, ssoApi);
        properties.setProperty(SsoConstant.SSO_ENABLE, Boolean.toString(enable));
        LOGGER.info("ssoFilter init success. properties={}", properties);
    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!enable) {
            LOGGER.warn("sso enable={} is invalid.", enable);
            chain.doFilter(request, response);
        }

        LOGGER.info("sso enable={} is valid. it will be execute doFilter() method.", enable);
        String ticket = httpRequest.getParameter(SsoConstant.TICKET);
        if (StringUtils.equalsIgnoreCase("undefined", ticket) || StringUtils.equalsIgnoreCase("null", ticket)) {
            LOGGER.info("invalid ticket={} with url.", ticket);
            ticket = null;
        }

        // 1. 检测URL是否包含ticket值[优先级最高]
        if (StringUtils.isNotBlank(ticket)) {
            // 1.1 如果URL包含ticket并且ticket失效，则需要清除cookies并跳转登陆
            if (!SsoAccessServiceImpl.getSsoAccessService().checkValid(ticket)) {
                LOGGER.info("url include ticket, but it is invalid. ticket={}", ticket);
                cleanCookie(httpRequest, httpResponse);
                sendRedirect(properties, httpRequest, httpResponse);
                return;
            }

            // 1.2 新增cookie
            if (!getCookies(httpRequest).containsKey(SsoConstant.TICKET)) {
                // cookies过期时间，设置为session会话级别，浏览器退出则cookie过期
                addCookie(httpResponse, SsoConstant.TICKET, ticket, SsoConstant.SESSION_TIMEOUT);
            }
        }

        // 2. 检测cookies中是否包含ticket值[优先级低]
        if (!getCookies(httpRequest).containsKey(SsoConstant.TICKET)) {
            // 2.1 如果cookie中不包含ticket, 则跳转登陆
            LOGGER.info("cookies not include ticket.");
            sendRedirect(properties, httpRequest, httpResponse);
            return;
        } else {
            ticket = getCookies(httpRequest).get(SsoConstant.TICKET).getValue();
            if (!SsoAccessServiceImpl.getSsoAccessService().checkValid(ticket)) {
                // 2.2 如果cookie中包含ticket, 但是ticket值已失效, 则跳转登陆
                LOGGER.info("cookies include ticket, but it is invalid. ticket={}", ticket);
                cleanCookie(httpRequest, httpResponse);
                sendRedirect(properties, httpRequest, httpResponse);
                return;
            }
        }

        // 3. 如果URL或cookies中，包含ticket并且ticket值有效，则直接放行
        chain.doFilter(request, response);
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        LOGGER.info("ssoFilter destroy ...");
    }

    /**
     * 添加cookie
     *
     * @param response      httpResponse
     * @param name          Cookie名
     * @param value         Cookie对应的值
     * @param maxAge        最大生命周期
     */
    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        LOGGER.info("add cookie success. name={}, value={}", name, value);
        response.addCookie(cookie);
    }

    /**
     * 跳转至SSO登陆
     *
     * @param properties
     * @param httpResponse
     * @throws IOException
     */
    public void sendRedirect(Properties properties, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String callback = properties.getProperty(SsoConstant.CALLBACK);
        String serverUrl = properties.getProperty(SsoConstant.SERVER_URL);
        String appName = properties.getProperty(SsoConstant.APP_NAME);

        String userAgent = httpRequest.getHeader("User-Agent").toLowerCase();
        if (userAgent.contains("iphone") || userAgent.contains("android")) {
            // 暂时移除userAgent判断，设置PC端和移动端都跳转相同的地址 2018-07-11 Add by tianyuliang
            // String ssoLoginUrl = String.format("%s?callback=%s&appName=%s", serverUrl, callback, appName);
            // httpResponse.setHeader("loginUrl", ssoLoginUrl);
        }

        String ssoLoginUrl = buildSsoLoginUrl(serverUrl, appName);
        LOGGER.info("build {} success.  {}", SsoConstant.SSO_LOGIN_URL_HEADER_KEY, ssoLoginUrl);
        httpResponse.setHeader(SsoConstant.SSO_LOGIN_URL_HEADER_KEY, ssoLoginUrl);

        // 关闭302跳转, 设置响应码200, 以便于前端拦截请求 2018-07-11 Add by tianyuliang
        httpResponse.setStatus(HttpServletResponse.SC_OK);
    }

}
