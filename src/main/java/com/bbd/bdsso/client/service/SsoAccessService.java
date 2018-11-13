/**  
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client.service;

import javax.servlet.http.HttpServletRequest;

import com.bbd.bdsso.common.service.facade.result.BdssoBaseAuthResult;
import com.bbd.bdsso.common.service.facade.result.BdssoBaseResult;

/**
 * SSO服务接口
 * 
 * @author byron
 * @version $Id: SsoAccessService.java, v 0.1 Oct 31, 2017 9:24:16 AM byron Exp $
 */
public interface SsoAccessService {

    /**
     * 通过ticket来获取authCode列表
     * 
     * @param ticket        用户票据
     * @param authCode      用户权限码
     * @return
     */
    public BdssoBaseAuthResult getAuthCodeList(String ticket, String authCode);

    /**
     * 检查ticket是否有效
     * 
     * @param ticket        用户票据
     * @return 
     */
    public boolean checkValid(String ticket);

    /**
     * 添加用户访问记录
     * 
     * @param request       用户请求
     * @param ticket        用户票据
     * @param appName       用户访问的应用
     * @return
     */
    public BdssoBaseResult addUserVisitHistory(HttpServletRequest request, String ticket, String appName);

    /**
     * 批量校验权限
     * @param ticket    用户票据
     * @param authCodes 需要校验的权限码数组
     * @return
     */
    BdssoBaseAuthResult getAuthCodeList(String ticket, String[] authCodes);

    /**
     * 登出SSO
     * @param ticket 用户票据
     * @return
     */
    BdssoBaseResult logout(String ticket);

    /**
     * 获取当前用户在当前应用下的资源认证列表
     * @param ticket    用户票据
     * @return
     */
    BdssoBaseAuthResult getResourcesList(String ticket);
}
