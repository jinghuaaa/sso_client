/**  
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client.enums;

import com.bbd.commons.lang.enums.EnumInterface;

/**
 * 单点登陆结果枚举
 * 
 * @author byron
 * @version $Id: BdssoResultEnum.java, v 0.1 Sep 12, 2017 4:49:23 PM byron Exp $
 */
public enum BdssoResultEnum implements EnumInterface {

    /** 成功(此时不能修改为200，它需要与sso-server端返回的报文字段做匹配) */
    SUCCESS("SUCCESS", "成功"),

    /** 没有权限 */
    PERMISSION_NOT_ALLOWED("2001", "没有权限"),

    /** 权限码为空 */
    EMPTY_AUTHCODE("2002", "权限码为空"),

    /** 系统异常 */
    SYSTEM_ERROR("3003", "系统异常"),

    /** 空值对象 */
    NULL_OBJECT("3004", "对象为NULL"),

    /** 空对象 */
    EMPTY_OBJECT("3005", "空对象"),

    /** 数据库异常 */
    DATABASE_EXCEPTION("3006", "数据库异常"),

    /** ticket值为空 */
    TICKET_EMPTY("3007", "ticket值为空"),

    /** ticket值无效 **/
    TICKET_INVALID("3008", "ticket值无效"),

    /** 业务方的返回返回结果类,不是BdssoBaseResult.java的子类 **/
    BIZ_RESULT_CLASS_INVALID("3009", "业务方的返回返回结果类,不是BdssoBaseResult.java的子类"),

    /** 请求过程触发RPC异常 **/
    REQUEST_RPC_EXCEPTION("3010", "请求过程触发RPC异常"),

    /** 未知异常 */
    UNKNOWN_EXCEPTION("9999", "未知异常");

    /** 枚举值 */
    private String code;

    /** 枚举描述 */
    private String desc;

    /**
     * 构造方法
     *
     * @param code
     * @param desc
     */
    BdssoResultEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    /**
     * 获取日志信息
     *
     * @return
     */
    public String getLogMessage() {
        return String.format("[%s:%s]", code, desc);
    }
}
