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

import com.yy.cs.base.hostinfo.HostInfo;
import com.yy.cs.base.hostinfo.HostInfoHelper;
import com.yy.cs.base.hostinfo.IpInfo;
import com.yy.cs.base.hostinfo.NetType;
import com.yy.cs.base.http.CSHttpClient;
import com.yy.cs.base.http.HttpClientException;
import com.yy.cs.base.json.Json;

public class HiidoApi {

    private static final String API_URL = "https://api.hiido.com:443/api.php";
    private static final String LOGIN_URL = "https://api.hiido.com:443/apilogin.php";

    private static CSHttpClient httpClient = new CSHttpClient();

    public static <T> ResultObject access(String apiName, String cname, String secret, String localIp, T requestData)
            throws SocketException, UnknownHostException, HttpClientException {
        ResultObject resultObject = new ResultObject();
        ResultObject loginResult = login(apiName, cname, secret, localIp);
        if (null != loginResult && loginResult.getResultcode() == 1) {
            String sign = DigestUtils.md5Hex(loginResult.getMessage() + "_" + secret);
            HttpPost post = new HttpPost(API_URL);
            post.addHeader("Authorization", "id=" + loginResult.getMessage() + ",code=" + sign);
            String reqJson = "";
            if (requestData instanceof String) {
                reqJson = (String) requestData;
            } else {
                reqJson = Json.ObjToStr(requestData);
            }
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

    public static <T> ResultObject access(String apiName, String cname, String secret, T requestData)
            throws SocketException, UnknownHostException, HttpClientException {
        return access(apiName, cname, secret, null, requestData);
    }

    private static ResultObject login(String apiName, String cname, String secret, String ip)
            throws SocketException, UnknownHostException, HttpClientException {
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
        HostInfo hostInfo = HostInfoHelper.getHostInfo();
        if (null != hostInfo) {
            Map<NetType, IpInfo> ips = hostInfo.getIpList();
            if (ips.size() > 0) {
                for (NetType type : NetType.values()) {
                    if (ips.containsKey(type)) {
                        return ips.get(type).getIp();
                    }
                }
            }
        }

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
            System.out.println(HiidoApi.access("getJiaoyouData", "product", "product@chinaduo.com", "58.254.172.48", req));

            System.out.println(HiidoApi.access("getJiaoyouData", "product", "product@chinaduo.com", "183.56.146.11",
                    "{\"dataType\":\"DAU\",\"sd\":\"2015-8-12\",\"ed\":\"2015-8-25\"}"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
