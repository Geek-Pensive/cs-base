package com.yy.cs.base.nyy.common;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

 
public final class ConfigUtil implements Serializable {

    private static final long serialVersionUID = -1985165475234910535L;

    private final String protocol;

	private final String host;

	private final int port;

	private final String path;

    private final Map<String, String> parameters = new HashMap<String, String>();
    
    protected ConfigUtil() {
        this.protocol = null;
        this.host = null;
        this.port = 0;
        this.path = null;
    }
    
	public ConfigUtil(String protocol, String host, int port) {
	    this(protocol, host, port, null, (Map<String, String>) null);
	}
	
	public ConfigUtil(String protocol, String host, int port, Map<String, String> parameters) {
        this(protocol,  host, port, null, parameters);
    }
	
	
	public ConfigUtil(String protocol,String host, int port, String path) {
        this(protocol, host, port, path, (Map<String, String>) null);
    }
	
	public ConfigUtil(String protocol,String host, int port, String path, Map<String, String> parameters) {
		 
		this.protocol = protocol;
		this.host = host;
		this.port = (port < 0 ? 0 : port);
		this.path = path;
		if(parameters != null && parameters.size() != 0 ){
			this.parameters.putAll(parameters); 
		}
	}
 
	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getAddress() {
	    return port <= 0 ? host : host + ":" + port;
	}

	public String getPath() {
		return path;
	}
	
	public String getAbsolutePath() {
        if (path != null && !path.startsWith("/")) {
            return "/" + path;
        }
        return path;
	}
	
	
	public static ConfigUtil valueOf(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String host = null;
        int port = 0;
        String path = null;
        int i = url.indexOf("://");
        if (i >= 0) {
            if(i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        }
        else {
            i = url.indexOf(":/");
            if(i>=0) {
                if(i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }
        i = url.indexOf("/");
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }
        i = url.indexOf(":");
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if(url.length() > 0) host = url;
        return new ConfigUtil(protocol,host, port, path);
    }
	
    @SuppressWarnings("unused")
	public void setAddress(String address) {
        int i = address.lastIndexOf(':');
        String host;
        int port = this.port;
        if (i >= 0) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
        }
    }

    public void addParameter(String key, String value) {
        if (key == null || key.length() == 0
                || value == null || value.length() == 0) {
            return;
        }
        this.parameters.put(key, value);
    }
    
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    
    public String getParamete(String key, String defaultValue) {
        if (key == null || key.length() == 0) {
            return defaultValue;
        }
        String value = this.parameters.get(key);
        if(value == null || value.length() == 0){
        	value = defaultValue;
        }
        return value;
    }
    
    public String getParamete(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        return this.parameters.get(key);
    }
    
	public String toString() {
		StringBuilder buf = new StringBuilder();
		if (protocol != null) {
			buf.append(protocol);
			buf.append("://");
		}
		if(host != null && host.length() > 0) {
    		buf.append(host);
    		if (port > 0) {
    			buf.append(":");
    			buf.append(port);
    		}
		}
		buf.append("/");
		if (path != null && path.length() > 0) {
			buf.append(path);
		}
		return buf.toString(); 
    }

    public java.net.URL toJavaURL() {
        try {
            return new java.net.URL(toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

      
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + port;
		result = prime * result
				+ ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigUtil other = (ConfigUtil) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (port != other.port)
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		return true;
	}


    public static String encode(String value) {
        if (value == null || value.length() == 0) { 
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static String decode(String value) {
        if (value == null || value.length() == 0) { 
            return "";
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    
    public static void main(String[] args) {
    	
		ConfigUtil.encode("请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请");
		long st = System.currentTimeMillis();
		System.out.println(st);
		for(int i=0; i<1000000; i++){
			ConfigUtil.encode("请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请按照我深信请按照请按照请");
		}
		long et = System.currentTimeMillis();
		System.out.println(et-st);
	}
}