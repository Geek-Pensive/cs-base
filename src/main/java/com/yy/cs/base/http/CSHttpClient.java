package com.yy.cs.base.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  基于Apache的httpclient 4.3.X以上的版本，使用PoolingHttpClientConnectionManager封装了HttpClient常用API。
 *  <br>
 *  池化的参数设置：{@link CSHttpClientFactory}
 */
public class CSHttpClient {

    private static final Logger log = LoggerFactory.getLogger(CSHttpClient.class);
    
    private final  RequestConfig defaultRequestConfig;
    
    private final  CloseableHttpClient httpClient;
    
    /**
     * 带参数的构造函数，通过工厂类{@link CSHttpClientFactory}生成指定的CSHttpClient,
     * @param 
     * 	    factory 生成CSHttpClient的工厂类
     */
    public CSHttpClient(CSHttpClientFactory factory) {
    	PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    	this.defaultRequestConfig = RequestConfig.custom()
    	        .setConnectTimeout(factory.getConnectionTimeout())				
    	        .setConnectionRequestTimeout(factory.getConnectionRequestTimeout())
    			 .setSocketTimeout(factory.getSocketTimeOut())
    	        .build();
        cm.setMaxTotal(factory.getMaxTotal());
        cm.setDefaultMaxPerRoute(factory.getDefaultMaxPerRoute());
        this.httpClient = HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * 无参构造函数
     */
    public CSHttpClient() {
    	this(new CSHttpClientFactory());
    }
    
    /**
     * 获取池化的原生httpClient
     * @return 
     * 		 httpClient
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * 执行一个http方法。为了方法的名称更加易懂和清晰，建议使用executeMethodAndReturnString(HttpRequestBase)
     * @param httpRequestBase  执行方法的类型
     * @return response正确返回后的字符串
     * @throws HttpClientException
     * @see com.yy.cs.base.http.CSHttpClient.executeMethodAndReturnString(HttpRequestBase)
     */
    @Deprecated
    public String executeMethod(HttpRequestBase httpRequestBase) throws HttpClientException {
    	InputStream in = executeMethodAndReturnInputStream(httpRequestBase);
    	try {
            return inputStream2String(in);
        } catch (IOException e) {
            throw new HttpClientException("cannot convert response inputStream into String due to IOE",e);
        }finally{
            if (in !=null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // may ignore
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 执行一个http方法
     * @param httpRequestBase 执行方法的类型
     * @return response正确返回后的byte[]
     * @throws HttpClientException
     */
    public byte[] executeMethodAndReturnByteArray(HttpRequestBase httpRequestBase) throws HttpClientException {
        InputStream in = executeMethodAndReturnInputStream(httpRequestBase);
        try {
            return inputStream2ByteArray(in);
        } catch (IOException e) {
            throw new HttpClientException("could not convert response strean to byte array",e);
        }finally{
            if (in !=null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // may ignore
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 执行一个HttpGet方法
     * @param url 请求地址
     * @return response正确返回后的字符串
     * @throws HttpClientException
     */
    public String doGet(String url) throws HttpClientException  {
    	HttpGet get = new HttpGet(url);
        return executeMethodAndReturnString(get);
    }
    
    /**
     * 执行一个HttpGet方法,返回response返回的流
     * @param 
     * 		url ,请求地址
     * @param statusArray 指定正确返回的状态码
     * @return response正确返回后的流
     * @throws HttpClientException
     */
    public InputStream getResponseStream(String url, int[] statusArray) throws HttpClientException{
        Set<Integer> statusSet = intArrayToSet(statusArray);
        HttpGet httpGet = new HttpGet(url);
        
        return executeMethodAndReturnInputStream(httpGet, statusSet);
    }

    /**
     * 发送一个HttpGet请求，检查地址是否正常 
     * @param url
     * 		地址
     * @return 
     * 		boolean 'response返回响应状态码200或304'
     */
    public boolean isGetOK(String url)  {
    	HttpGet get = new HttpGet(url);
    	CloseableHttpResponse response = null;
    	StatusLine status = null;
    	try {
			setDefaultRequestConfig(get);
			log.debug("executing request " + get.getURI());
			response = httpClient.execute(get);
			status = response.getStatusLine(); 
			log.debug("response status " + status);
			if(status.getStatusCode() ==  HttpStatus.SC_OK || status.getStatusCode() == HttpStatus.SC_NOT_MODIFIED){
			     return true;
			}
			return false;
		}catch(ClientProtocolException e){
			log.error("get data from url:"+ get.getURI() + " fail, status: " + status,e);
		} catch (Exception e) {
			log.error("get data from url:"+ get.getURI() + " fail, status: " + status,e);
		}finally {
			if(response != null){
				try {
					response.close();
				} catch (IOException e) {
					 log.error("response close IOException:"+get.getURI(), e);
				}
			}
			if(get != null){
			    get.releaseConnection();
            }
		}
        return false;
    }

    /**
     * 执行一个HttPost请求
     * @param url 请求地址
     * @param parameters 自动参数按utf-8编码
     * @return  response正确返回后的字符串
     * @throws HttpClientException
     */
    public String doPost(String url, Map<String, String> parameters) throws HttpClientException  { 
    	
    	HttpPost httpRequestBase = new HttpPost(url);
		if (parameters != null && !parameters.isEmpty()) {
			try {
				httpRequestBase.setEntity(new UrlEncodedFormEntity(
						HttpClientUtil.toNameValuePairs(parameters), "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				throw new HttpClientException(e1);
			}
		}
        return this.executeMethodAndReturnString(httpRequestBase);
    }
    
    /**
     * 执行一个HttPost请求
     * @param url 请求地址
     * @param jsonStr  json字符串, 按utf-8编码
     * @return   response正确返回后的字符串
     * @throws HttpClientException
     */
    public String doPost(String url, String jsonStr) throws HttpClientException  { 
    	HttpPost httpRequestBase = new HttpPost(url);
		if (jsonStr != null && !jsonStr.isEmpty()) {
			try {
				httpRequestBase.setHeader("Content-Type","application/json");
				httpRequestBase.setEntity(new StringEntity(jsonStr, "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				throw new HttpClientException(e1);
			}
		}
        return this.executeMethodAndReturnString(httpRequestBase);
    }
    
    /**
     * 执行一个 http 方法
     * @param httpRequestBase 执行方法的类型
     * @return response正确返回后的 inputstream
     * @throws HttpClientException
     */
    public String executeMethodAndReturnString(HttpRequestBase httpRequestBase) throws HttpClientException {
        InputStream in = executeMethodAndReturnInputStream(httpRequestBase);
        try {
            return inputStream2String(in);
        } catch (IOException e) {
            throw new HttpClientException("cannot convert response inputStream into String due to IOException",e);
        }finally{
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // may ignore
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    /**
     * 执行一个 http 方法
     * @param httpRequestBase 执行方法的类型
     * @return response 正确返回后(状态码为 200）的 inputstream
     * @throws HttpClientException
     */
    public InputStream executeMethodAndReturnInputStream(HttpRequestBase httpRequestBase) throws HttpClientException {
        Set<Integer> statusSet = new HashSet<Integer>();
        statusSet.add(200);
        return executeMethodAndReturnInputStream(httpRequestBase,statusSet);
    }
    
    /**
     * 执行一个 http 方法
     * @param httpRequestBase 执行方法的类型
     * @param acceptStatus 可以接受的返回请求状态码，为 null 时，代表可接受任何返回状态码，当返回非接受状态码时，会抛出 HttpClientException
     * @return response正确返回后的 inputstream
     * @throws HttpClientException
     */
    public InputStream executeMethodAndReturnInputStream(HttpRequestBase httpRequestBase,Set<Integer> acceptStatus) throws HttpClientException{
        CloseableHttpResponse response = null;
        StatusLine status = null;
        try {
            setDefaultRequestConfig(httpRequestBase);
            log.debug("executing " +httpRequestBase.getMethod() +" request " + httpRequestBase.getURI());
            response = httpClient.execute(httpRequestBase);
            status = response.getStatusLine(); 
            log.debug("response status " + status);
            HttpEntity entity = response.getEntity();
            if (acceptStatus == null) {
                return entity.getContent();
            }else {
                int statusCode = status.getStatusCode();
                if (isAcceptStatus(acceptStatus, statusCode)) {
                    return entity.getContent();
                }else {
                    throw new HttpClientException("faile to get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status);
                }
            }
        }catch(ClientProtocolException e){
            log.error("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
            throw new HttpClientException("fail to get data from url:"+ httpRequestBase.getURI() + " , status: " + status,e);
        } catch (IOException e) {
            log.error("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
            throw new HttpClientException("fail to get data from url:"+ httpRequestBase.getURI() + " , status: " + status,e);
        }finally {
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                     log.error("response close IOException:"+httpRequestBase.getURI(), e);
                }
            }
            if(httpRequestBase != null ){
                httpRequestBase.releaseConnection();
            }
        }
    }
    
    private String inputStream2String(InputStream in) throws IOException{
    	ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 256);
    	byte[] temp = new byte[1024 * 256];
    	int i=-1;
    	while((i=in.read(temp))!=-1){
    		baos.write(temp, 0, i);
    	}
    	return baos.toString();
    }
    
    
    private byte[] inputStream2ByteArray(InputStream in) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 256);
        byte[] temp = new byte[1024 * 256];
        int i=-1;
        while((i=in.read(temp))!=-1){
            baos.write(temp, 0, i);
        }
        return baos.toByteArray();
    }
    
    private void setDefaultRequestConfig(HttpRequestBase requestBase){

        RequestConfig config = requestBase.getConfig();
        if(config == null){
            requestBase.setConfig(defaultRequestConfig);	 
    		return;
        }
    	Builder builder = RequestConfig.custom();
    	if(config.getConnectionRequestTimeout() == -1){
    		builder.setConnectionRequestTimeout(defaultRequestConfig.getConnectionRequestTimeout());
    	}
    	if(config.getConnectTimeout() == -1){
    		builder.setConnectTimeout(defaultRequestConfig.getConnectTimeout());
    	}
    	if(config.getSocketTimeout() == -1){
    		builder.setSocketTimeout(defaultRequestConfig.getSocketTimeout());
    	}
		config = builder
				.setExpectContinueEnabled(config.isExpectContinueEnabled())
				.setStaleConnectionCheckEnabled(
						config.isStaleConnectionCheckEnabled())
				.setAuthenticationEnabled(config.isAuthenticationEnabled())
				.setRedirectsEnabled(config.isRedirectsEnabled())
				.setRelativeRedirectsAllowed(
						config.isRelativeRedirectsAllowed())
				.setCircularRedirectsAllowed(
						config.isCircularRedirectsAllowed())
				.setMaxRedirects(config.getMaxRedirects())
				.setCookieSpec(config.getCookieSpec())
				.setLocalAddress(config.getLocalAddress())
				.setProxy(config.getProxy())
				.setTargetPreferredAuthSchemes(
						config.getTargetPreferredAuthSchemes())
				.setProxyPreferredAuthSchemes(
						config.getProxyPreferredAuthSchemes()).build();
		requestBase.setConfig(config);
    }
    
//    private boolean isInStatusArray(int status, int[] statusArray) {
//        for (int i = 0; i < statusArray.length; i++) {
//            if (status == statusArray[i]) {
//                return true;
//            }
//        }
//        return false;
//    }
    
    /**
     * int 数组转换为 Set<Integer>
     * <br /> 增减此方法是为了与 旧API 兼容 
     * @param intArray
     * @return
     */
    private Set<Integer> intArrayToSet(int[] intArray){
        Set<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < intArray.length; i++) {
            set.add(intArray[i]);
        }
        
        return set;
    }
    
    /**
     * 判断当前状态码是否可以接受
     * @param acceptStatus  可接受的状态码集
     * @param status        当前状态码
     * <pre>
     *      status == null          return false
     *      acceptStatus == null    return false
     * </pre>
     * @return
     */
    private boolean isAcceptStatus(Set<Integer> acceptStatus,Integer status){
        if (status == null) {
            return false;
        }
        if (acceptStatus == null) {
           return false;
        }
        for (Integer s : acceptStatus) {
            if (s.intValue() == status.intValue()) {
                return true;
            }
        }
        
        return false;
    }
    /**
     * 关闭httpClient,关闭流并释放相关的系统资源
     * @throws IOException
     */
    public void shutdown() throws IOException{
        httpClient.close();
    }
}
