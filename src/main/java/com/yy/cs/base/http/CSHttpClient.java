package com.yy.cs.base.http;

import com.yy.cs.base.status.LogLevel;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基于Apache的httpclient 4.3.X以上的版本，使用PoolingHttpClientConnectionManager封装了HttpClient常用API。
 * <br>
 * 池化的参数设置：{@link CSHttpClientFactory}
 */
public class CSHttpClient {

    private static final Logger log = LoggerFactory.getLogger(CSHttpClient.class);

    private final RequestConfig defaultRequestConfig;

    private final CloseableHttpClient httpClient;

    private LogLevel logLevel = LogLevel.ERROR;

    private HttpRequestStatisticsInterceptor httpRequestStatisticsInterceptor;

    /**
     * 带参数的构造函数，通过工厂类{@link CSHttpClientFactory}生成指定的CSHttpClient,设置post请求方式是否自动跳转302地址
     *
     * @param
     *            factory 生成CSHttpClient的工厂类
     */
    public CSHttpClient(CSHttpClientFactory factory, Boolean isPostRedirect) {
        this(factory,isPostRedirect,null);
    }

    public CSHttpClient(CSHttpClientFactory factory, Boolean isPostRedirect,HttpRoutePlanner httpRoutePlanner) {
        PoolingHttpClientConnectionManager cm = null;
        if (factory.isTrustAllCertificates()) {
            RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory());

            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustStrategy() {

                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }

                }).build();
                LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,
                        SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                registryBuilder.register("https", sslSF);
            } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            cm = new PoolingHttpClientConnectionManager(registryBuilder.build(), null, null, null,
                    factory.getConnectionTimeToLive(), TimeUnit.MILLISECONDS);
        } else {
            cm = new PoolingHttpClientConnectionManager(factory.getConnectionTimeToLive(), TimeUnit.MILLISECONDS);
        }


        this.defaultRequestConfig = RequestConfig.custom().setConnectTimeout(factory.getConnectionTimeout())
                .setConnectionRequestTimeout(factory.getConnectionRequestTimeout())
                .setSocketTimeout(factory.getSocketTimeOut()).build();
        cm.setMaxTotal(factory.getMaxTotal());
        cm.setDefaultMaxPerRoute(factory.getDefaultMaxPerRoute());
        HttpClientBuilder builder = HttpClients.custom();
        if (isPostRedirect != null && isPostRedirect.booleanValue()) {
            builder.setRedirectStrategy(new LaxRedirectStrategy());
        }
        if (null != factory.getLogLevel()) {
            this.logLevel = factory.getLogLevel();
        }
        builder.setConnectionManager(cm);
        builder.setRoutePlanner(httpRoutePlanner);
        this.httpClient = builder.build();
    }


    public void setHttpRequestStatisticsInterceptor(HttpRequestStatisticsInterceptor httpRequestStatisticsInterceptor) {
        this.httpRequestStatisticsInterceptor = httpRequestStatisticsInterceptor;
    }

    private void logErrorInfo(String msg, Object... args) {
        switch (this.logLevel) {
        case TRACE:
            log.trace(msg, args);
            break;
        case DEBUG:
            log.debug(msg, args);
            break;
        case INFO:
            log.info(msg, args);
            break;
        case WARN:
            log.warn(msg, args);
            break;
        case ERROR:
            log.error(msg, args);
            break;
        default:
            log.error(msg, args);
            break;
        }
    }

    /**
     * 带参数的构造函数，通过工厂类{@link CSHttpClientFactory}生成指定的CSHttpClient,
     *
     * @param
     *            factory 生成CSHttpClient的工厂类
     */
    public CSHttpClient(CSHttpClientFactory factory) {
        this(factory, false);
    }

    /**
     * 无参构造函数
     */
    public CSHttpClient() {
        this(new CSHttpClientFactory());
    }

    public CSHttpClient(HttpRoutePlanner httpRoutePlanner) {
        this(new CSHttpClientFactory(),false,httpRoutePlanner);
    }

    /**
     * 获取池化的原生httpClient
     *
     * @return
     *         httpClient
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * 执行一个http方法
     *
     * @param httpRequestBase 执行方法的类型
     * @return response正确返回后的字符串
     * @throws HttpClientException
     */
    public String executeMethod(HttpRequestBase httpRequestBase) throws HttpClientException {
        CloseableHttpResponse response = null;
        StatusLine status = null;
        String result = "";
        HttpRequestStatistics statistics = new HttpRequestStatistics();
        long _s = System.currentTimeMillis();
        try {
            setDefaultRequestConfig(httpRequestBase);
            statistics.setUri(httpRequestBase.getURI());
            log.debug("executing request " + decodeUrl(httpRequestBase.getURI().toString()));
            response = httpClient.execute(httpRequestBase);
            status = response.getStatusLine();
            statistics.setStatusLine(status);
            log.debug("response status " + status);
            HttpEntity entity = response.getEntity();
            statistics.setSpendTime(System.currentTimeMillis() - _s);
            if (status.getStatusCode() == HttpStatus.SC_OK) {
                result = inputStream2String(entity.getContent());
            } else {
                throw new HttpClientException("get data from url:" + httpRequestBase.getURI() + " fail, status: "
                        + status + ",resp:" + inputStream2String(entity.getContent()));
            }
        } catch (ClientProtocolException e) {
            statistics.setException(e);
            statistics.setSpendTime(System.currentTimeMillis() - _s);
            logErrorInfo("get data from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
            throw new HttpClientException("get data from url:" + httpRequestBase.getURI() + " fail, status: " + status,
                    e);
        } catch (IOException e) {
            statistics.setException(e);
            statistics.setSpendTime(System.currentTimeMillis() - _s);
            logErrorInfo("get data from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
            throw new HttpClientException("get data from url:" + httpRequestBase.getURI() + " fail, status: " + status,
                    e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logErrorInfo("response close IOException:" + httpRequestBase.getURI(), e);
                }
            }
            if (httpRequestBase != null) {
                httpRequestBase.releaseConnection();
            }
            if (httpRequestStatisticsInterceptor != null) {
                // http 请求的一些监控，同步调用，过于依赖实现方。异步调用，需要提供对线程池的支持
                try {
                    httpRequestStatisticsInterceptor.handle(statistics);
                } catch (Exception e) {
                    logErrorInfo("httpRequestStatisticsInterceptor Exception:" + httpRequestBase.getURI(), e);
                }
            }
        }
        return result;
    }

    /**
     * 执行一个http方法
     *
     * @param httpRequestBase 执行方法的类型
     * @return Header[] http返回的response头信息
     * @throws HttpClientException
     */
    public Header[] executeMethodReturnHeaders(HttpRequestBase httpRequestBase) throws HttpClientException {
        CloseableHttpResponse response = null;
        StatusLine status = null;
        Header[] headers;
        try {
            setDefaultRequestConfig(httpRequestBase);
            log.debug("executing request " + decodeUrl(httpRequestBase.getURI().toString()));
            response = httpClient.execute(httpRequestBase);
            status = response.getStatusLine();
            headers = response.getAllHeaders();
        } catch (IOException e) {
            logErrorInfo("get data from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
            throw new HttpClientException("get data from url:" + httpRequestBase.getURI() + " fail, status: " + status,
                    e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logErrorInfo("response close IOException:" + httpRequestBase.getURI(), e);
                }
            }
            if (httpRequestBase != null) {
                httpRequestBase.releaseConnection();
            }
        }
        return headers;
    }

    public int getFileSize(String url) {
        HttpRequestBase httpRequestBase = new HttpHead(url);
        Header[] headers;
        try {
            headers = executeMethodReturnHeaders(httpRequestBase);
            if (headers == null || headers.length == 0) {
                log.error("get file size fail, header is null or empty!");
                return 0;
            }
            for (Header header : headers) {
                if (header.getName().equalsIgnoreCase("content-length")) {
                    return Integer.valueOf(header.getValue());
                }
            }
            log.error("get file size fail! no content-length element in headers");
        } catch (Exception e) {
            log.error("get file size fail!", e);
        }
        return 0;
    }

    /**
     * 执行一个http方法
     *
     * @param httpRequestBase 执行方法的类型
     * @return response正确返回后的byte[]
     * @throws HttpClientException
     */
    public byte[] executeMethodAndReturnByteArray(HttpRequestBase httpRequestBase) throws HttpClientException {
        CloseableHttpResponse response = null;
        StatusLine status = null;
        HttpRequestStatistics statistics = new HttpRequestStatistics();
        long _s = System.currentTimeMillis();
        try {
            setDefaultRequestConfig(httpRequestBase);
            statistics.setUri(httpRequestBase.getURI());
            log.debug("executing request " + decodeUrl(httpRequestBase.getURI().toString()));
            response = httpClient.execute(httpRequestBase);
            status = response.getStatusLine();
            statistics.setStatusLine(status);
            log.debug("response status " + status);
            HttpEntity entity = response.getEntity();
            statistics.setSpendTime(System.currentTimeMillis() - _s);
            if (status.getStatusCode() == HttpStatus.SC_OK) {
                return inputStream2ByteArray(entity.getContent());
            } else {
                throw new HttpClientException(
                        "get byte array from url:" + httpRequestBase.getURI() + " fail, status: " + status);
            }
        } catch (ClientProtocolException e) {
            statistics.setException(e);
            statistics.setSpendTime(System.currentTimeMillis() - _s);
            logErrorInfo("get byte array from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
            throw new HttpClientException(
                    "get byte array from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
        } catch (IOException e) {
            statistics.setException(e);
            statistics.setSpendTime(System.currentTimeMillis() - _s);
            logErrorInfo("get byte array from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
            throw new HttpClientException(
                    "get byte array from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logErrorInfo("response close IOException:" + httpRequestBase.getURI(), e);
                }
            }
            if (httpRequestBase != null) {
                httpRequestBase.releaseConnection();
            }

            if (httpRequestStatisticsInterceptor != null) {
                // http 请求的一些监控，同步调用，过于依赖实现方。异步调用，需要提供对线程池的支持
                try {
                    httpRequestStatisticsInterceptor.handle(statistics);
                } catch (Exception e) {
                    logErrorInfo("httpRequestStatisticsInterceptor Exception:" + httpRequestBase.getURI(), e);
                }
            }
        }
    }


    public CSHttpResponse executeMethodAndReturnCSHttpResponse(HttpRequestBase httpRequestBase) throws HttpClientException {
        CSHttpResponse csHttpResponse = new CSHttpResponse();
        CloseableHttpResponse response = null;
        StatusLine status = null;
        HttpRequestStatistics statistics = new HttpRequestStatistics();
        long _s = System.currentTimeMillis();
        try {
            setDefaultRequestConfig(httpRequestBase);
            statistics.setUri(httpRequestBase.getURI());
            log.debug("executing request " + decodeUrl(httpRequestBase.getURI().toString()));
            response = httpClient.execute(httpRequestBase);
            status = response.getStatusLine();
            statistics.setStatusLine(status);
            log.debug("response status " + status);
            Header[] headers = response.getAllHeaders();
            Map<String,String> headersMap = new HashMap<>();
            if (headers != null) {
                for(Header header : headers) {
                    headersMap.put(header.getName(),header.getValue());
                }
            }
            csHttpResponse.setHeaders(headersMap);
            HttpEntity entity = response.getEntity();
            statistics.setSpendTime(System.currentTimeMillis() - _s);
            csHttpResponse.setStatusLine(status);
            csHttpResponse.setBytes(inputStream2ByteArray(entity.getContent()));
            return csHttpResponse;
        } catch (ClientProtocolException e) {
            statistics.setException(e);
            statistics.setSpendTime(System.currentTimeMillis() - _s);
            logErrorInfo("get byte array from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
            throw new HttpClientException(
                    "get byte array from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
        } catch (IOException e) {
            statistics.setException(e);
            statistics.setSpendTime(System.currentTimeMillis() - _s);
            logErrorInfo("get byte array from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
            throw new HttpClientException(
                    "get byte array from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logErrorInfo("response close IOException:" + httpRequestBase.getURI(), e);
                }
            }
            if (httpRequestBase != null) {
                httpRequestBase.releaseConnection();
            }

            if (httpRequestStatisticsInterceptor != null) {
                // http 请求的一些监控，同步调用，过于依赖实现方。异步调用，需要提供对线程池的支持
                try {
                    httpRequestStatisticsInterceptor.handle(statistics);
                } catch (Exception e) {
                    logErrorInfo("httpRequestStatisticsInterceptor Exception:" + httpRequestBase.getURI(), e);
                }
            }
        }

    }

    /**
     * 执行一个HttpGet方法
     *
     * @param url 请求地址
     * @return response正确返回后的字符串
     * @throws HttpClientException
     */
    public String doGet(String url) throws HttpClientException {
        HttpGet get = new HttpGet(url);
        String result = this.executeMethod(get);
        return result;
    }

    /**
     * 执行一个HttpGet方法,返回response返回的流
     *
     * @param
     *            url ,请求地址
     * @param statusArray 指点正确返回的状态码
     * @return response正确返回后的流
     * @throws HttpClientException
     */
    public InputStream getResponseStream(String url, int[] statusArray) throws HttpClientException {
        HttpGet httpRequestBase = new HttpGet(url);
        CloseableHttpResponse response = null;
        StatusLine status = null;
        try {
            setDefaultRequestConfig(httpRequestBase);
            log.debug("executing request " + decodeUrl(httpRequestBase.getURI().toString()));
            response = httpClient.execute(httpRequestBase);
            status = response.getStatusLine();
            log.debug("response status " + status);
            HttpEntity entity = response.getEntity();
            if (isInStatusArray(status.getStatusCode(), statusArray)) {
                byte[] array = EntityUtils.toByteArray(entity);
                ByteArrayInputStream bytes = new ByteArrayInputStream(array);
                return bytes;
            }
            throw new HttpClientException("get data from url:" + httpRequestBase.getURI() + " fail, status: " + status);
        } catch (ClientProtocolException e) {
            logErrorInfo("get data from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
            throw new HttpClientException("get data from url:" + httpRequestBase.getURI() + " fail, status: " + status,
                    e);
        } catch (IOException e) {
            logErrorInfo("get data from url:" + httpRequestBase.getURI() + " fail, status: " + status, e);
            throw new HttpClientException("get data from url:" + httpRequestBase.getURI() + " fail, status: " + status,
                    e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logErrorInfo("response close IOException:" + httpRequestBase.getURI(), e);
                }
            }
            if (httpRequestBase != null) {
                httpRequestBase.releaseConnection();
            }
        }
    }

    /**
     * 发送一个HttpGet请求，检查地址是否正常
     *
     * @param url
     *            地址
     * @return
     *         boolean 'response返回响应状态码200或304'
     */
    public boolean isGetOK(String url) {
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = null;
        StatusLine status = null;
        try {
            setDefaultRequestConfig(get);
            log.debug("executing request " + decodeUrl(get.getURI().toString()));
            response = httpClient.execute(get);
            status = response.getStatusLine();
            log.debug("response status " + status);
            if (status.getStatusCode() == HttpStatus.SC_OK || status.getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
                return true;
            }
            return false;
        } catch (ClientProtocolException e) {
            logErrorInfo("get data from url:" + get.getURI() + " fail, status: " + status, e);
        } catch (Exception e) {
            logErrorInfo("get data from url:" + get.getURI() + " fail, status: " + status, e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logErrorInfo("response close IOException:" + get.getURI(), e);
                }
            }
            if (get != null) {
                get.releaseConnection();
            }
        }
        return false;
    }

    /**
     * 执行一个HttPost请求
     *
     * @param url 请求地址
     * @param parameters 自动参数按utf-8编码
     * @return response正确返回后的字符串
     * @throws HttpClientException
     */
    public String doPost(String url, Map<String, String> parameters) throws HttpClientException {

        HttpPost httpRequestBase = new HttpPost(url);
        if (parameters != null && !parameters.isEmpty()) {
            try {
                httpRequestBase
                        .setEntity(new UrlEncodedFormEntity(HttpClientUtil.toNameValuePairs(parameters), "UTF-8"));
            } catch (UnsupportedEncodingException e1) {
                throw new HttpClientException(e1);
            }
        }
        return this.executeMethod(httpRequestBase);
    }

    /**
     * 执行一个HttPost请求
     *
     * @param url 请求地址
     * @param jsonStr json字符串, 按utf-8编码
     * @return response正确返回后的字符串
     * @throws HttpClientException
     */
    public String doPost(String url, String jsonStr) throws HttpClientException {
        HttpPost httpRequestBase = new HttpPost(url);
        if (jsonStr != null && !jsonStr.isEmpty()) {
            try {
                httpRequestBase.setHeader("Content-Type", "application/json");
                httpRequestBase.setEntity(new StringEntity(jsonStr, "UTF-8"));
            } catch (UnsupportedEncodingException e1) {
                throw new HttpClientException(e1);
            }
        }
        return this.executeMethod(httpRequestBase);
    }

    private String inputStream2String(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 256);
        byte[] temp = new byte[1024 * 256];
        int i = -1;
        while ((i = in.read(temp)) != -1) {
            baos.write(temp, 0, i);
        }
        return baos.toString();
    }

    private byte[] inputStream2ByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 256);
        byte[] temp = new byte[1024 * 256];
        int i = -1;
        while ((i = in.read(temp)) != -1) {
            baos.write(temp, 0, i);
        }
        return baos.toByteArray();
    }

    private void setDefaultRequestConfig(HttpRequestBase requestBase) {

        RequestConfig config = requestBase.getConfig();
        if (config == null) {
            requestBase.setConfig(defaultRequestConfig);
            return;
        }
        Builder builder = RequestConfig.custom();
        if (config.getConnectionRequestTimeout() == -1) {
            builder.setConnectionRequestTimeout(defaultRequestConfig.getConnectionRequestTimeout());
        }
        if (config.getConnectTimeout() == -1) {
            builder.setConnectTimeout(defaultRequestConfig.getConnectTimeout());
        }
        if (config.getSocketTimeout() == -1) {
            builder.setSocketTimeout(defaultRequestConfig.getSocketTimeout());
        }
        config = builder.setExpectContinueEnabled(config.isExpectContinueEnabled())
                .setStaleConnectionCheckEnabled(config.isStaleConnectionCheckEnabled())
                .setAuthenticationEnabled(config.isAuthenticationEnabled())
                .setRedirectsEnabled(config.isRedirectsEnabled())
                .setRelativeRedirectsAllowed(config.isRelativeRedirectsAllowed())
                .setCircularRedirectsAllowed(config.isCircularRedirectsAllowed())
                .setMaxRedirects(config.getMaxRedirects()).setCookieSpec(config.getCookieSpec())
                .setLocalAddress(config.getLocalAddress()).setProxy(config.getProxy())
                .setTargetPreferredAuthSchemes(config.getTargetPreferredAuthSchemes())
                .setProxyPreferredAuthSchemes(config.getProxyPreferredAuthSchemes()).build();
        requestBase.setConfig(config);
    }

    private boolean isInStatusArray(int status, int[] statusArray) {
        for (int i = 0; i < statusArray.length; i++) {
            if (status == statusArray[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * 关闭httpClient,关闭流并释放相关的系统资源
     *
     * @throws IOException
     */
    public void shutdown() throws IOException {
        httpClient.close();
    }

    /**
     * 以 utf-8 编码 url，完善日志的输出
     *
     * @param url
     * @return
     */
    private String decodeUrl(String url) {
        try {
            return URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            logErrorInfo("cannot use urf-8 to decode url : {}", url);
            return url;
        }
    }
}
