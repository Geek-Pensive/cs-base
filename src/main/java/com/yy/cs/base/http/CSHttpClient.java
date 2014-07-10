package com.yy.cs.base.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

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
 * 
 */
public class CSHttpClient {

    private static final Logger log = LoggerFactory.getLogger(CSHttpClient.class);
    
    private final  RequestConfig defaultRequestConfig;
    
    private final  CloseableHttpClient httpClient;
    
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

    
    public CSHttpClient() {
    	this(new CSHttpClientFactory());
    }
    /**
     * @return 池化的原生httpClient
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * 执行一个http方法
     * @param 执行方法的类型
     * @return response正确返回后的字符串
     * @throws HttpClientException
     */
    public String executeMethod(HttpRequestBase httpRequestBase) throws HttpClientException {
    	CloseableHttpResponse response = null;
    	StatusLine status = null;
    	String  result = "";
    	
    	try {
			setDefaultRequestConfig(httpRequestBase);
			log.debug("executing request " + httpRequestBase.getURI());
			response = httpClient.execute(httpRequestBase);
			status = response.getStatusLine(); 
			log.debug("response status " + status);
			HttpEntity entity = response.getEntity();
			if(status.getStatusCode() ==  HttpStatus.SC_OK){
				result = inputStream2String(entity.getContent());
			}else{
				throw new HttpClientException("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status);
			}
		}catch(ClientProtocolException e){
			log.error("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
			throw new HttpClientException("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
		} catch (IOException e) {
			log.error("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
			throw new HttpClientException("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
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
        return  result;
    }
    
    
    /**
     * 执行一个http方法
     * @param 执行方法的类型
     * @return response正确返回后的byte[]
     * @throws HttpClientException
     */
    public byte[] executeMethodAndReturnByteArray(HttpRequestBase httpRequestBase) throws HttpClientException {
        CloseableHttpResponse response = null;
        StatusLine status = null;
        try {
            setDefaultRequestConfig(httpRequestBase);
            log.debug("executing request " + httpRequestBase.getURI());
            response = httpClient.execute(httpRequestBase);
            status = response.getStatusLine(); 
            log.debug("response status " + status);
            HttpEntity entity = response.getEntity();
            if(status.getStatusCode() ==  HttpStatus.SC_OK){
                return inputStream2ByteArray(entity.getContent());
            }else{
                throw new HttpClientException("get byte array from url:"+ httpRequestBase.getURI() + " fail, status: " + status);
            }
        }catch(ClientProtocolException e){
            log.error("get byte array from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
            throw new HttpClientException("get byte array from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
        } catch (IOException e) {
            log.error("get byte array from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
            throw new HttpClientException("get byte array from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
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
    
    /**
     * 执行一个HttpGet方法
     * @param 请求地址
     * @return response正确返回后的字符串
     * @throws HttpClientException
     */
    public String doGet(String url) throws HttpClientException  {
    	HttpGet get = new HttpGet(url);
        String result = this.executeMethod(get);
        return result;
    }
    
    /**
     * 执行一个HttpGet方法,返回response返回的流
     * @param 请求地址
     * @param 指点正确返回的状态码
     * @return response正确返回后的流
     * @throws HttpClientException
     */
    public InputStream getResponseStream(String url, int[] statusArray) throws HttpClientException{
    	HttpGet httpRequestBase = new HttpGet(url);
        CloseableHttpResponse response = null;
    	StatusLine status = null;
    	try {
			setDefaultRequestConfig(httpRequestBase);
			log.debug("executing request " + httpRequestBase.getURI());
			response = httpClient.execute(httpRequestBase);
			status = response.getStatusLine(); 
			log.debug("response status " + status);
			HttpEntity entity = response.getEntity();
			if(isInStatusArray(status.getStatusCode(),statusArray)){
				return entity.getContent();
			}
			throw new HttpClientException("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status);
		}catch(ClientProtocolException e){
			log.error("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
			throw new HttpClientException("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
		} catch (IOException e) {
			log.error("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
			throw new HttpClientException("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + status,e);
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
    /**
     * 发送一个HttpGet请求，检查地址是否正常 
     * @param url地址
     * @return true 'response返回响应状态码200或304'
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
        return this.executeMethod(httpRequestBase);
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
        return this.executeMethod(httpRequestBase);
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
    	Builder b = RequestConfig.custom();
    	if(config.getConnectionRequestTimeout() == -1){
    		b.setConnectionRequestTimeout(defaultRequestConfig.getConnectionRequestTimeout());
    	}
    	if(config.getConnectTimeout() == -1){
    		b.setConnectTimeout(defaultRequestConfig.getConnectTimeout());
    	}
    	if(config.getSocketTimeout() == -1){
    		b.setSocketTimeout(defaultRequestConfig.getSocketTimeout());
    	}
    	
		config = b
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
    
    private boolean isInStatusArray(int status, int[] statusArray) {
        for (int i = 0; i < statusArray.length; i++) {
            if (status == statusArray[i]) {
                return true;
            }
        }
        return false;
    }
    
    public void shutdown() throws IOException{
        httpClient.close();
    }
}
