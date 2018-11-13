/**  
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client.constants;

/**
 * SSO常量
 * 
 * @author byron
 * @version $Id: SsoConstant.java, v 0.1 Sep 21, 2017 4:28:27 PM byron Exp $
 */
public class SsoConstant {

    /** cookies过期时间 */
    public static final Integer SESSION_TIMEOUT                          = -1;

    /** authCode */
    public static final String  AUTH_CODE                                = "authCode";

    /** sso enable flag */
    public static final String  SSO_ENABLE                               = "enable";

    /** serverUrl */
    public static final String  SERVER_URL                               = "serverUrl";

    /** serverUrlPhone */
    public static final String  SERVER_URL_PHONE                         = "serverUrlPhone";

    /** requestUri */
    public static final String  REQUEST_URI                              = "requestUri";

    /** callback */
    public static final String  CALLBACK                                 = "callback";

    /** callbackPhone */
    public static final String  CALLBACK_PHONE                           = "callbackPhone";

    /** visitHistory */
    public static final String  IP                                       = "ip";

    /** appName */
    public static final String  APP_NAME                                 = "appName";

    /** 用户accessToken */
    public static final String  TICKET                                   = "ticket";

    /** loginUrl  **/
    public static final String  LOGIN_URL_HEADER_KEY                     = "loginUrl";

    /** SSO-Login-Url  **/
    public static final String  SSO_LOGIN_URL_HEADER_KEY                 = "SSO-Login-Url";

    /** 无权限请求头 */
    public static final String  SSO_PERMISSION_NOT_ALLOWED               = "Permission-Not-Allowed";

    /** 未登录时用户UID */
    public static final String  NOT_LOGGED_UID                           = "notLogged";

    /** 容器IP */
    public static final String  ENV_SERVER_IP                            = "container.ip";

    /** 本地IP */
    public static final String  LOCAL_IP                                 = "127.0.0.1";

    /** sso授权URI */
    public static final String  AUTH_URI                                 = "/api/v1.0/bdsso/auth";
    /** sso资源URI */
    public static final String  RESOURCE_URI                             = "/api/v1.0/bdsso/resource";
    /** sso访问历史记录 */
    public static final String  VISIT_HISTORY_URI                        = "/api/v1.0/bdsso/visit_history";
    /** sso应用管理URI */
    public static final String  APP_URI                                  = "/api/v1.0/bdsso/app";
    /** sso用户管理URI */
    public static final String  USER_URI                                 = "/api/v1.0/bdsso/user";
    /** sso api */
    public static final String  SSO_API                                  = "ssoApi";
    /** 有效 */
    public static final int     VALID                                    = 1;
    /** 失效 */
    public static final int     NOT_VALID                                = 0;
    /** 注册 */
    public static final String  REGIST                                   = "账户注册";
    /** 重置密码 */
    public static final String  RESET_PASSWORD                           = "重置密码";
    /** 注册验证码 */
    public static final String  SEND_REGIST_VERIFY_CODE_SUBJECT          = "注册验证码";
    /** 找回密码验证码 */
    public static final String  FIND_PASSWORD_REGIST_VERIFY_CODE_SUBJECT = "找回密码验证码";
    /** 验证码类型错误 */
    public static final String  ERROR_VERIFY_CODE_TYPE                   = "验证码类型错误";
    /** 验证码错误 */
    public static final String  ERROR_VERIFY_CODE                        = "验证码错误";
    /** 验证码失效 */
    public static final String  INVALID_VERIFY_CODE                      = "验证码失效";
    /** 用户注册 */
    public static final String  USER_REGIST                              = "用户注册";
    /** 初始化 */
    public static final String  INIT                                     = "初始化";
    /** 无效的token */
    public static final String  INVALID_TOKEN                            = "无效的token";
    /** 无效的uid */
    public static final String  INVALID_UID                              = "无效的uid";
    /** 无效的uid和token */
    public static final String  INVALID_UID_OR_TOKEN                     = "无效的uid或token";
    /** 请求错误 */
    public static final String  ERROR_REQUEST                            = "请求错误";

}
