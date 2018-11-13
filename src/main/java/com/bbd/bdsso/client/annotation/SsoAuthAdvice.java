/**  
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client.annotation;

import java.lang.reflect.Method;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.bbd.bdsso.client.util.MixAllUtil;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bbd.bdsso.client.SsoFilter;
import com.bbd.bdsso.client.constants.SsoConstant;
import com.bbd.bdsso.client.enums.AuthTypeEnum;
import com.bbd.bdsso.client.enums.BdssoResultEnum;
import com.bbd.bdsso.client.exception.BdssoBaseException;
import com.bbd.bdsso.client.service.impl.SsoAccessServiceImpl;
import com.bbd.bdsso.client.template.BdssoAopTemplate;
import com.bbd.bdsso.client.template.BdssoCallBack;
import com.bbd.bdsso.common.service.facade.result.BdssoBaseAuthResult;
import com.bbd.bdsso.common.service.facade.result.BdssoBaseResult;

/**
 * SSO权限AOP服务
 * 
 * @author byron
 * @version $Id: SsoAuthAdvice.java, v 0.1 Sep 20, 2017 4:01:32 PM byron Exp $
 */
public class SsoAuthAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(SsoAuthAdvice.class);

    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // 获取注解枚举值
        Method method = signature.getMethod();
        AuthTypeEnum authTypeEnum = method.getAnnotation(AuthValidate.class).type();
        String authCodeEnum = method.getAnnotation(AuthValidate.class).authCode();

        // SSO配置开关（默认是开启SSO）除非用户手动配置false
        Boolean enable = MixAllUtil.asBoolean(SsoFilter.properties.getProperty(SsoConstant.SSO_ENABLE), true);

        // 检查是否需要校验权限
        if (!enable || StringUtils.equals(authTypeEnum.getCode(), AuthTypeEnum.NO.getCode())) {
            // 不需要校验
            return joinPoint.proceed();
        }

        Class<?> c = signature.getReturnType();
        BdssoBaseResult result = (BdssoBaseResult) c.newInstance();
        // 通过统一模板来封装返回值
        BdssoAopTemplate.getBdssoAopTemplate().executeWithoutTransaction(new BdssoCallBack() {

            @Override
            public void check() {

                String ticket = getTicket();
                // 检查ticket， 如果请求通过了SsoFilter.doFilter()而运行到当前doAround()，那可能是业务方在对接SSO的请求参数配置错误.
                if (StringUtils.isBlank(ticket)) {
                    LOGGER.error("[bdsso-client] Ticket is null");
                    throw new BdssoBaseException(BdssoResultEnum.TICKET_EMPTY);
                }

                // 检查注解的结果类，是否是BdssoBaseResult的子类
                if (!BdssoBaseResult.class.isAssignableFrom(c)) {
                    LOGGER.error("[bdsso-client] The result class is not a subclass of BdssoBaseResult. {}", c.getName());
                    throw new BdssoBaseException(BdssoResultEnum.BIZ_RESULT_CLASS_INVALID, c.getName());
                }

                // 检查authCode
                if (StringUtils.isBlank(authCodeEnum)) {
                    throw new BdssoBaseException(BdssoResultEnum.EMPTY_AUTHCODE);
                }
            }

            @Override
            public void service() {
                // core service
                BdssoBaseAuthResult baseResult = SsoAccessServiceImpl.getSsoAccessService().getAuthCodeList(getTicket(), authCodeEnum);

                if (null == baseResult.getAuthResult().get(authCodeEnum)) {
                    throw new BdssoBaseException(BdssoResultEnum.PERMISSION_NOT_ALLOWED);
                }
                if (!baseResult.getAuthResult().get(authCodeEnum)) {
                    throw new BdssoBaseException(BdssoResultEnum.PERMISSION_NOT_ALLOWED);
                }

                addVisitHistory();
            }

        }, result);

        // 如果校验成功，继续后续处理；反之，返回结果
        // 此处“result.getResultCode()”的值不是返回200，而是返回字符串"SUCCESS"
        if (result.getResultCode().equals(BdssoResultEnum.SUCCESS.getCode())) {
            Object rvt = joinPoint.proceed();
            if (null == rvt) {
                return null;
            } else {
                /**  注意：此处要求对接系统自己的返回结果集，是BdssoBaseResult的子类**/
                return (BdssoBaseResult) rvt;
            }
        } else {
            return result;
        }
    }

    /**
     * 获取Ticket
     * 
     * @return
     */
    public String getTicket() {
        // 访问HttpServletRequest
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), SsoConstant.TICKET)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 新增用户访问记录
     */
    public void addVisitHistory() {
        // 访问HttpServletRequest
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // 新增用户访问记录
        SsoAccessServiceImpl.getSsoAccessService().addUserVisitHistory(request, getTicket(), SsoFilter.properties.getProperty(SsoConstant.APP_NAME));
    }

}
