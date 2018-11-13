/**  
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client.enums;

import com.bbd.commons.lang.enums.EnumInterface;

/**
 * 权限类型枚举
 * 
 * @author byron
 * @version $Id: AuthTypeEnum.java, v 0.1 Oct 11, 2017 2:20:46 PM byron Exp $
 */
public enum AuthTypeEnum implements EnumInterface {

    /** 不需要验证 */
    NO("NO", "不需要验证"),

    /** 需要验证 */
    YES("YES", "需要验证");

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
    private AuthTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 通过code获取枚举
     * 
     * @param code          代码
     * @return              枚举
     */
    public static AuthTypeEnum getEnumByCode(String code) {
        for (AuthTypeEnum entry : AuthTypeEnum.values()) {
            if (entry.getCode().equals(code)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * 获取日志信息
     *
     * @return
     */
    public String getLogMessage() {
        return String.format("[%s:%s]", code, desc);
    }

    /** 
     * @see com.bbd.commons.lang.enums.EnumInterface#getCode()
     */
    public String getCode() {
        return code;
    }

    /** 
     * @see com.bbd.commons.lang.enums.EnumInterface#getDesc()
     */
    public String getDesc() {
        return desc;
    }
}