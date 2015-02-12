package com.yy.cs.base.hiidoapi;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import com.yy.cs.base.http.CSHttpClient;
import com.yy.cs.base.http.HttpClientException;
import com.yy.cs.base.json.Json;

public class HiidoApi {

    private static final String API_URL = "https://api.hiido.com:443/api.php";
    private static final String LOGIN_URL = "https://api.hiido.com:443/apilogin.php";

    private static CSHttpClient httpClient = new CSHttpClient();

    public static ResultObject access(String apiName, String cname, String secret, String localIp,
            Map<String, Object> requestData) throws SocketException, UnknownHostException, HttpClientException {
        ResultObject resultObject = new ResultObject();
        ResultObject loginResult = login(apiName, cname, secret, localIp);
        if (null != loginResult && loginResult.getResultcode() == 1) {
            String sign = DigestUtils.md5Hex(loginResult.getMessage() + "_" + secret);
            HttpPost post = new HttpPost(API_URL);
            post.addHeader("Authorization", "id=" + loginResult.getMessage() + ",code=" + sign);
            String reqJson = Json.ObjToStr(requestData);
            post.setEntity(EntityBuilder.create().setText(reqJson).build());
            String respJson = httpClient.executeMethod(post);
            if (null != respJson && !"".equals(respJson)) {
                resultObject = Json.strToObj(respJson, ResultObject.class);
            }
        } else if (null != loginResult) {
            resultObject.setResultcode(loginResult.getResultcode());
            resultObject.setMessage(loginResult.getMessage());
        }
        return resultObject;
    }

    public static ResultObject access(String apiName, String cname, String secret, Map<String, Object> requestData)
            throws SocketException, UnknownHostException, HttpClientException {
        return access(apiName, cname, secret, null, requestData);
    }

    private static ResultObject login(String apiName, String cname, String secret, String ip) throws SocketException,
            UnknownHostException, HttpClientException {
        ResultObject resultObject = null;
        if (ip == null) {
            ip = getIp("eth0");
        }
        String sign = DigestUtils.md5Hex(apiName + "_" + cname + "_" + ip);
        String reqString = "apiname=" + apiName + ",cname=" + cname + ",code=" + sign;
        HttpRequestBase get = new HttpGet(LOGIN_URL);
        get.addHeader("Authorization", reqString);
        String result = httpClient.executeMethod(get);
        if (null != result && !"".equals(result)) {
            resultObject = Json.strToObj(result, ResultObject.class);
        }
        return resultObject;
    }

    public static class ResultObject {
        private Integer resultcode;
        private String message;

        public Integer getResultcode() {
            return resultcode;
        }

        public String getMessage() {
            return message;
        }

        public void setResultcode(Integer resultcode) {
            this.resultcode = resultcode;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "ResultObject [resultcode=" + resultcode + ", message=" + message + "]";
        }

    }

    private static String getIp(String ethName) throws SocketException, UnknownHostException {
        Enumeration<NetworkInterface> eiter = NetworkInterface.getNetworkInterfaces();
        String ip = null;
        while (eiter.hasMoreElements()) {
            NetworkInterface ni = eiter.nextElement();
            if (ni.isLoopback()) {
                continue;
            }
            if (ni.getName().equalsIgnoreCase(ethName)) {
                Enumeration<InetAddress> adds = ni.getInetAddresses();
                while (adds.hasMoreElements()) {
                    InetAddress address = adds.nextElement();
                    if (!address.isLoopbackAddress()) {
                        if (address.getHostAddress().equals("127.0.0.1")) {
                            continue;
                        }
                        if (address instanceof Inet6Address) {
                            continue;
                        }
                        if (address instanceof Inet4Address) {
                            ip = address.getHostAddress();
                            break;
                        }
                    }
                }
            }
        }
        if (null == ip) {
            ip = InetAddress.getLocalHost().getHostAddress();
        }
        return ip;
    }

    public static void main(String[] args) {
        Map<String, Object> req = new HashMap<String, Object>();
        req.put("sd", "2014-11-26");
        req.put("ed", "2014-11-28");
        req.put("dataType", "DAU");
        try {
            System.out.println(HiidoApi.access("getJiaoyouData", "product", "product@chinaduo.com", "183.60.177.228",
                    req));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
