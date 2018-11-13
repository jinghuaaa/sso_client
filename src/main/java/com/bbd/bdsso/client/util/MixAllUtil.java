/**
 * BBD Service Inc
 * All Rights Reserved @2018
 */
package com.bbd.bdsso.client.util;

import java.util.ArrayList;

/**
 * 大杂烩工具类
 *
 * @author tianyuliang
 * @version $Id: MixAllUtil.java, v0.1 2018-07-11 11:15 tianyuliang Exp $$
 */
public class MixAllUtil {

    /**
     * 转化为Integer数字
     */
    public static Integer asInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 转化为Boolean布尔值
     */
    public static boolean asBoolean(String value, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将数组转换为ArrayList
     * @param strings
     * @return
     */
    public static ArrayList<String> toArrayList(String[] strings) {
        if(strings == null){
            return null;
        }
        ArrayList<String> ret = new ArrayList<>();
        for (String str : strings) {
            ret.add(str);
        }
        return ret;
    }

}
