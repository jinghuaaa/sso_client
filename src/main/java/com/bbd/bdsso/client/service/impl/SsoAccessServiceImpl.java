/**  
 * BBD Service Inc
 * All Rights Reserved @2017
 */
package com.bbd.bdsso.client.service.impl;

import com.bbd.bdsso.client.SsoFilter;
import com.bbd.bdsso.client.constants.SsoConstant;
import com.bbd.bdsso.client.enums.BdssoResultEnum;
import com.bbd.bdsso.client.exception.BdssoBaseException;
import com.bbd.bdsso.client.service.SsoAccessService;
import com.bbd.bdsso.client.util.MixAllUtil;
import com.bbd.bdsso.client.util.RequestUtil;
import com.bbd.bdsso.common.service.facade.result.BdssoBaseAuthResult;
import com.bbd.bdsso.common.service.facade.result.BdssoBaseResult;
import com.bbd.bdsso.common.service.facade.vo.SsoExAuthVO;
import net.sf.json.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.protocol.HTTP;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * SSO访问Token验证服务实现
 * 
 * @author byron
 * @version $Id: SsoAccessServiceImpl.java, v 0.1 Sep 25, 2017 1:25:46 PM byron Exp $
 */
@SuppressWarnings("unchecked")
public final class SsoAccessServiceImpl implements SsoAccessService {

    private static final Logger                  LOGGER               = LoggerFactory.getLogger(SsoAccessServiceImpl.class);

    private static volatile SsoAccessServiceImpl ssoAccessServiceImpl = null;

    private SsoAccessServiceImpl() {

    }

    // 单例模式
    public static SsoAccessServiceImpl getSsoAccessService() {
        if (ssoAccessServiceImpl == null) {
            synchronized (SsoAccessServiceImpl.class) {
                if (ssoAccessServiceImpl == null) {
                    ssoAccessServiceImpl = new SsoAccessServiceImpl();
                }
            }
        }
        return ssoAccessServiceImpl;
    }

    /** 
     * @see com.bbd.bdsso.client.service.SsoAccessService#checkValid(java.lang.String)
     */
    @Override
    public boolean checkValid(String ticket) throws BdssoBaseException {
        CloseableHttpResponse resp = null;
        try {
            // 请求地址
            String requestUrl = SsoFilter.properties.getProperty(SsoConstant.SSO_API) + SsoConstant.APP_URI + "/verifyTicket";

            //            ClientRequest req = new ClientRequest(requestUrl);
            //
            //            req.formParameter(SsoConstant.TICKET, ticket);
            //            ClientResponse<String> resp = req.post(String.class);

            Map params = new HashMap<>();
            Map headers = new HashMap<>();
            params.put(SsoConstant.TICKET, ticket);
            headers.put(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            resp = RequestUtil.processHttpRequest(headers, requestUrl, "post", params);

            if (resp.getStatusLine().getStatusCode() != 200) {
                LOGGER.error("checkValid() [bdsso-client] RPC error code：" + resp.getStatusLine().getStatusCode());
                throw new BdssoBaseException(BdssoResultEnum.REQUEST_RPC_EXCEPTION);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
            String output;
            while ((output = br.readLine()) != null) {
                BdssoBaseResult result = (BdssoBaseResult) JSONObject.toBean(JSONObject.fromObject(output), BdssoBaseResult.class);
                return result.isSuccess();
            }
        } catch (MalformedURLException e) {
            throw new BdssoBaseException(e.getMessage());
        } catch (IOException e) {
            throw new BdssoBaseException(e.getMessage());
        } catch (Exception e) {
            throw new BdssoBaseException(e.getMessage());
        } finally {
            if (null != resp) {
                try {
                    resp.getEntity().getContent().close();
                } catch (IOException e) {
                    throw new BdssoBaseException(e.getMessage());
                }
            }

        }
        return false;
    }

    /**
     * 
     * @see com.bbd.bdsso.client.service.SsoAccessService#getAuthCodeList(java.lang.String, java.lang.String)
     */
    @Override
    public BdssoBaseAuthResult getAuthCodeList(String ticket, String authCode) {
        CloseableHttpResponse resp = null;
        try {
            String requestUrl = SsoFilter.properties.getProperty(SsoConstant.SSO_API) + SsoConstant.AUTH_URI + "/authCodeByTicket";
            //            ClientRequest req = new ClientRequest(requestUrl);
            //            req.formParameter(SsoConstant.AUTH_CODE, authCode);
            //            req.formParameter(SsoConstant.APP_NAME, SsoFilter.properties.getProperty(SsoConstant.APP_NAME));
            //
            //            req.header(SsoConstant.TICKET, ticket);
            //            ClientResponse<String> resp = req.post(String.class);

            Map params = new HashMap<>();
            Map headers = new HashMap<>();
            headers.put(SsoConstant.TICKET, ticket);
            headers.put(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            params.put(SsoConstant.AUTH_CODE, authCode);
            params.put(SsoConstant.APP_NAME, SsoFilter.properties.getProperty(SsoConstant.APP_NAME));
            resp = RequestUtil.processHttpRequest(headers, requestUrl, "post", params);

            if (resp.getStatusLine().getStatusCode() != 200) {
                LOGGER.error("getAuthCodeList() [bdsso-client] RPC error code：" + resp.getStatusLine().getStatusCode());
                throw new BdssoBaseException(BdssoResultEnum.REQUEST_RPC_EXCEPTION);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
            String output;
            while ((output = br.readLine()) != null) {
                return (BdssoBaseAuthResult) JSONObject.toBean(JSONObject.fromObject(output), BdssoBaseAuthResult.class);
            }

        } catch (MalformedURLException e) {
            throw new BdssoBaseException(e.getMessage());
        } catch (IOException e) {
            throw new BdssoBaseException(e.getMessage());
        } catch (Exception e) {
            throw new BdssoBaseException(e.getMessage());
        } finally {
            if (null != resp) {
                try {
                    resp.getEntity().getContent().close();
                } catch (IOException e) {
                    throw new BdssoBaseException(e.getMessage());
                }
            }

        }
        return null;
    }

    /** 
     * @see com.bbd.bdsso.client.service.SsoAccessService#addUserVisitHistory(javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.String)
     */
    @Override
    public BdssoBaseResult addUserVisitHistory(HttpServletRequest request, String ticket, String appName) {
        CloseableHttpResponse resp = null;
        try {
            String requestUrl = SsoFilter.properties.getProperty(SsoConstant.SSO_API) + SsoConstant.VISIT_HISTORY_URI + "/add";

            //            ClientRequest req = new ClientRequest(requestUrl);
            //
            //            req.formParameter(SsoConstant.IP, request.getRemoteAddr());
            //            req.formParameter(SsoConstant.APP_NAME, appName + ":" + request.getRequestURI());
            //
            //            req.header(SsoConstant.TICKET, ticket);
            //            ClientResponse<String> resp = req.post(String.class);

            Map params = new HashMap<>();
            Map headers = new HashMap<>();
            headers.put(SsoConstant.TICKET, ticket);
            headers.put(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            params.put(SsoConstant.IP, request.getRemoteAddr());
            params.put(SsoConstant.APP_NAME, appName + ":" + request.getRequestURI());
            resp = RequestUtil.processHttpRequest(headers, requestUrl, "post", params);

            if (resp.getStatusLine().getStatusCode() != 200) {
                LOGGER.error("addUserVisitHistory() [bdsso-client] RPC error code：" + resp.getStatusLine().getStatusCode());
                throw new BdssoBaseException(BdssoResultEnum.REQUEST_RPC_EXCEPTION);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
            String output;
            while ((output = br.readLine()) != null) {
                return (BdssoBaseResult) JSONObject.toBean(JSONObject.fromObject(output), BdssoBaseResult.class);
            }
        } catch (MalformedURLException e) {
            throw new BdssoBaseException(e.getMessage());
        } catch (IOException e) {
            throw new BdssoBaseException(e.getMessage());
        } catch (Exception e) {
            throw new BdssoBaseException(e.getMessage());
        } finally {
            if (null != resp) {
                try {
                    resp.getEntity().getContent().close();
                } catch (IOException e) {
                    throw new BdssoBaseException(e.getMessage());
                }
            }

        }
        return null;
    }

    /**
     * 批量校验权限
     * @param ticket
     * @param authCodes
     * @return
     */
    @Override
    public BdssoBaseAuthResult getAuthCodeList(String ticket, String[] authCodes) {
        try {
            String requestUrl = SsoFilter.properties.getProperty(SsoConstant.SSO_API) + SsoConstant.AUTH_URI + "/authCodeListByTicket";

            ClientRequest req = new ClientRequest(requestUrl);

            //组装数据
            SsoExAuthVO authVO = new SsoExAuthVO();
            authVO.setAppName(SsoFilter.properties.getProperty(SsoConstant.APP_NAME));
            authVO.setAuthCodeList(MixAllUtil.toArrayList(authCodes));
            req.body(MediaType.APPLICATION_JSON_TYPE, authVO);

            //设置票据
            req.header(SsoConstant.TICKET, ticket);
            ClientResponse<String> resp = req.post(String.class);

            if (resp.getStatus() != 200) {
                LOGGER.error("getAuthCodeList() [bdsso-client] RPC error code：" + resp.getStatus());
                throw new BdssoBaseException(BdssoResultEnum.REQUEST_RPC_EXCEPTION);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(resp.getEntity().getBytes())));
            String output;
            String tmp = null;
            while ((output = br.readLine()) != null) {
                tmp = new String(output.getBytes(HTTP.ISO_8859_1), HTTP.UTF_8);
                return (BdssoBaseAuthResult) JSONObject.toBean(JSONObject.fromObject(tmp), BdssoBaseAuthResult.class);
            }
        } catch (MalformedURLException e) {
            throw new BdssoBaseException(e.getMessage());
        } catch (IOException e) {
            throw new BdssoBaseException(e.getMessage());
        } catch (Exception e) {
            throw new BdssoBaseException(e.getMessage());
        }
        return null;
    }

    @Override
    public BdssoBaseResult logout(String ticket) {
        try {
            String requestUrl = SsoFilter.properties.getProperty(SsoConstant.SSO_API) + SsoConstant.USER_URI + "/logoutByTicket";

            ClientRequest req = new ClientRequest(requestUrl);

            //设置票据
            req.header(SsoConstant.TICKET, ticket);
            ClientResponse<String> resp = req.post(String.class);

            if (resp.getStatus() != 200) {
                LOGGER.error("logout() [bdsso-client] RPC error code：" + resp.getStatus());
                throw new BdssoBaseException(BdssoResultEnum.REQUEST_RPC_EXCEPTION);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(resp.getEntity().getBytes())));
            String output;
            while ((output = br.readLine()) != null) {
                return (BdssoBaseAuthResult) JSONObject.toBean(JSONObject.fromObject(new String(output.getBytes("ISO-8859-1"), "utf-8")), BdssoBaseAuthResult.class);
            }
        } catch (MalformedURLException e) {
            throw new BdssoBaseException(e.getMessage());
        } catch (IOException e) {
            throw new BdssoBaseException(e.getMessage());
        } catch (Exception e) {
            throw new BdssoBaseException(e.getMessage());
        }
        return null;
    }

    @Override
    public BdssoBaseAuthResult getResourcesList(String ticket) {
        try {
            String requestUrl = SsoFilter.properties.getProperty(SsoConstant.SSO_API) + SsoConstant.RESOURCE_URI + "/queryByTicketAndAppName";

            ClientRequest req = new ClientRequest(requestUrl);

            //设置查询应用
            req.formParameter(SsoConstant.APP_NAME, SsoFilter.properties.getProperty(SsoConstant.APP_NAME));

            //设置票据
            req.header(SsoConstant.TICKET, ticket);
            ClientResponse<String> resp = req.post(String.class);

            if (resp.getStatus() != 200) {
                LOGGER.error("getResourcesList() [bdsso-client] RPC error code：" + resp.getStatus());
                throw new BdssoBaseException(BdssoResultEnum.REQUEST_RPC_EXCEPTION);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(resp.getEntity().getBytes())));
            String output;
            while ((output = br.readLine()) != null) {
                return (BdssoBaseAuthResult) JSONObject.toBean(JSONObject.fromObject(new String(output.getBytes("ISO-8859-1"), "utf-8")), BdssoBaseAuthResult.class);
            }
        } catch (MalformedURLException e) {
            throw new BdssoBaseException(e.getMessage());
        } catch (IOException e) {
            throw new BdssoBaseException(e.getMessage());
        } catch (Exception e) {
            throw new BdssoBaseException(e.getMessage());
        }
        return null;
    }
}
