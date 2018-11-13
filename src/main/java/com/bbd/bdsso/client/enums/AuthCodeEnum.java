/**  
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client.enums;

import com.bbd.commons.lang.enums.EnumInterface;

/**
 * 权限编码枚举
 * 
 * @author byron
 * @version $Id: AuthCodeEnum.java, v 0.1 Oct 31, 2017 4:28:19 PM byron Exp $
 */
public enum AuthCodeEnum implements EnumInterface {

    /** 管理员 */
    ADMIN("ADMIN", "管理员"),;

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
    private AuthCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 通过code获取枚举
     * 
     * @param code          代码
     * @return              枚举
     */
    public static AuthCodeEnum getEnumByCode(String code) {
        for (AuthCodeEnum entry : AuthCodeEnum.values()) {
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

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
