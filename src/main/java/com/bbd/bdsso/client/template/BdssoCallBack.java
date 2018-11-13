/**  
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client.template;

/**
 * 单点登陆模板回调类
 * 
 * @author byron
 * @version $Id: BdssoCallBack.java, v 0.1 Sep 12, 2017 4:57:29 PM byron Exp $
 */
public interface BdssoCallBack {

    /**
     * 相关检查
     */
    public void check();

    /**
     * 调用服务
     */
    public void service();

}
