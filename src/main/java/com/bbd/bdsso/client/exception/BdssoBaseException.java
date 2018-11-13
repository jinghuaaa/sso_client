/**  
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client.exception;

import com.bbd.bdsso.client.enums.BdssoResultEnum;

/**
 * 单点登陆异常基类
 * 
 * @author byron
 * @version $Id: BdssoBaseException.java, v 0.1 May 10, 2016 10:24:48 AM byron Exp $
 */
public class BdssoBaseException extends RuntimeException {

    /** 序列化id */
    private static final long     serialVersionUID = 6275484492686634015L;

    /** 错误码 */
    private final BdssoResultEnum errorCode;

    /** 错误详细描述 */
    private String                detailMessage;

    /** 
     * 含错误信息的构造函数
     *
     * @param errorMsg
     */
    public BdssoBaseException(String errorMsg) {
        super(errorMsg);
        errorCode = BdssoResultEnum.SYSTEM_ERROR;
    }

    /**
     * 含有异常栈和错误信息构造器
     *
     * @param errorMsg
     * @param t
     */
    public BdssoBaseException(String errorMsg, Throwable t) {
        super(errorMsg, t);
        errorCode = BdssoResultEnum.SYSTEM_ERROR;
    }

    /**
     * 含错误码的构造函数
     *
     * @param errorCode
     */
    public BdssoBaseException(BdssoResultEnum errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param errorCode
     * @param detailMessage
     */
    public BdssoBaseException(BdssoResultEnum errorCode, String detailMessage) {
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    /**
     * 含有异常栈的构造函数
     * 
     * @param errorCode
     * @param t
     */
    public BdssoBaseException(BdssoResultEnum errorCode, Throwable t) {
        super(errorCode.getDesc(), t);
        this.errorCode = errorCode;
    }

    /**
     * 含有异常栈的构造函数
     *
     * @param errorCode
     * @param detailMessage
     * @param t
     */
    public BdssoBaseException(BdssoResultEnum errorCode, String detailMessage, Throwable t) {
        super(errorCode.getDesc(), t);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    /**
     * Getter method for property <tt>detailMessage</tt>.
     * 
     * @return property value of detailMessage
     */
    public String getDetailMessage() {
        return detailMessage;
    }

    /**
     * Setter method for property <tt>detailMessage</tt>.
     * 
     * @param detailMessage value to be assigned to property detailMessage
     */
    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }

    /**
     * Getter method for property <tt>errorCode</tt>.
     * 
     * @return property value of errorCode
     */
    public BdssoResultEnum getErrorCode() {
        return errorCode;
    }

}
