/**  
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client.template;

import com.bbd.bdsso.client.enums.BdssoResultEnum;
import com.bbd.bdsso.client.exception.BdssoBaseException;
import com.bbd.bdsso.common.service.facade.result.BdssoBaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author byron
 * @version $Id: BdssoAopTemplate.java, v 0.1 Oct 12, 2017 10:15:33 AM byron Exp $
 */
public final class BdssoAopTemplate {

    /** 日志 */
    private static final Logger              LOGGER           = LoggerFactory.getLogger(BdssoAopTemplate.class);

    private static volatile BdssoAopTemplate bdssoAopTemplate = null;

    private BdssoAopTemplate() {

    }

    // 单例模式
    public static BdssoAopTemplate getBdssoAopTemplate() {
        if (bdssoAopTemplate == null) {
            synchronized (BdssoAopTemplate.class) {
                if (bdssoAopTemplate == null) {
                    bdssoAopTemplate = new BdssoAopTemplate();
                }
            }
        }
        return bdssoAopTemplate;
    }

    /**
     * 不带事务的模板
     * 
     * @param callback
     * @param result
     * @throws Throwable 
     */
    public void executeWithoutTransaction(BdssoCallBack callback, BdssoBaseResult result) throws Throwable {
        doExecute(callback, result);
    }

    /**
     * 具体实现方法
     * 
     * @param callback
     * @param result
     * @throws Throwable 
     */
    private void doExecute(final BdssoCallBack callback, final Object result) throws Throwable {
        try {
            // 服务预检查
            callback.check();

            // 服务处理
            callback.service();

            // 构建成功结果
            buildResult(result, true, BdssoResultEnum.SUCCESS);
        } catch (BdssoBaseException e) {
            BdssoResultEnum errorCode = e.getErrorCode();
            LOGGER.error("单点登陆出现业务异常: " + errorCode.getCode(), e);
            buildResult(result, false, errorCode);
        } catch (Exception e) {
            LOGGER.error("单点登陆出现未知异常: " + e.getMessage(), e);
            buildResult(result, false, BdssoResultEnum.UNKNOWN_EXCEPTION);
        }
    }

    /**
     * 设置结果
     *
     * @param result
     * @param success
     * @param resultCode
     */
    private void buildResult(Object result, boolean success, BdssoResultEnum resultCode) {
        BdssoBaseResult baseQueryResult = (BdssoBaseResult) result;
        baseQueryResult.setSuccess(success);
        baseQueryResult.setResultCode(resultCode.getCode());
        baseQueryResult.setResultDesc(resultCode.getDesc());
    }

}
