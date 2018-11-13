/**  
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bbd.bdsso.client.enums.AuthTypeEnum;

/**
 * 权限校验注解
 * 
 * @author byron
 * @version $Id: AuthValidate.java, v 0.1 Sep 20, 2017 3:49:42 PM byron Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface AuthValidate {

    /** 默认需要验证 */
    AuthTypeEnum type() default AuthTypeEnum.YES;

    /** 权限吗 */
    String authCode() default "";
}
