/**  
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * URL工具类
 * 
 * @author byron
 * @version $Id: UrlUtil.java, v 0.1 Oct 31, 2017 9:16:26 AM byron Exp $
 */
public class UrlUtil {

    /**
     * url decode
     * 
     * @param source
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decode(String source) throws UnsupportedEncodingException {
        source = source.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        return URLDecoder.decode(source, "UTF-8");
    }

    public static void main(String args[]) throws UnsupportedEncodingException {
        System.out.println(decode("5PYtvv1%2FA%2BOv7d9lobJR7KmZ7B%2BAefIkkvrms4zDEetzIu2OTmK%2BR9JRf7xHG5DW"));
    }
}
