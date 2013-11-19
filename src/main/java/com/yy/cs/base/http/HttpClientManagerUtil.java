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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class HttpClientManagerUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpClientManagerUtil.class);
    
    private  final RequestConfig defaultRequestConfig;
    
    private  final CloseableHttpClient httpClient;
    
    public HttpClientManagerUtil(HttpClientConfig httpClientConfig) {
    	 PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
         cm.setMaxTotal(httpClientConfig.getMaxTotal());
         cm.setDefaultMaxPerRoute(httpClientConfig.getDefaultMaxPerRoute());
         this.defaultRequestConfig = RequestConfig.custom()
         .setConnectTimeout(httpClientConfig.getConnectionTimeout())				
         .setConnectionRequestTimeout(httpClientConfig.getConnectionRequestTimeout())
		 .setSocketTimeout(httpClientConfig.getSocketTimeOut())
         .build();
         this.httpClient = HttpClients.custom().setConnectionManager(cm).build();
    }

    public HttpClientManagerUtil() {
    	this(new HttpClientConfig());
    }
    /**
     * @return 池化的httpClient
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * 
     * @param method HttpRequestBase
     * @return the method's response content
     * @throws HttpClientException
     */
    public String executeMethod(HttpRequestBase httpRequestBase) throws HttpClientException {
    	CloseableHttpResponse response = null;
    	StatusLine status = null;
    	String  result = "";
    	try {
			setDefaultRequestConfig(httpRequestBase.getConfig());
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
		}
        return  result;
    }
    
    
    public String doGet(String url) throws HttpClientException  {
    	HttpGet get = new HttpGet(url);
        return this.executeMethod(get);
    }

    public InputStream getResponseStream(String url, int[] statusArray) throws HttpClientException{
    	HttpGet httpRequestBase = new HttpGet(url);
        CloseableHttpResponse response = null;
    	StatusLine status = null;
    	try {
			setDefaultRequestConfig(httpRequestBase.getConfig());
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
		}
    }

    public boolean isGetOK(String url)  {
    	HttpGet get = new HttpGet(url);
    	CloseableHttpResponse response = null;
    	StatusLine status = null;
    	try {
			setDefaultRequestConfig(get.getConfig());
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
		}
        return false;
    }


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
 
    
    private String inputStream2String(InputStream in) throws IOException{
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	int i=-1;
    	while((i=in.read())!=-1){
    		baos.write(i);
    	}
    	return baos.toString();
    }
    
    
    private void setDefaultRequestConfig(RequestConfig config){
    	if(config == null){
    		config = defaultRequestConfig;	 
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
    }
    private boolean isInStatusArray(int status, int[] statusArray) {
        for (int i = 0; i < statusArray.length; i++) {
            if (status == statusArray[i]) {
                return true;
            }
        }
        return false;
    }
}
