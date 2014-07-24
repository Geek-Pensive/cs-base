package com.yy.cs.base.nyy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


import com.fasterxml.jackson.databind.JsonNode;
import com.yy.cs.base.json.Json;

public class NYYHttpRequestWrapper extends HttpServletRequestWrapper {

    public NYYHttpRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    private JsonNode dataNode = null;
    
    private static final String DATA = "data";

    private String getValueFromData(String name) {
        if (dataNode == null) {
            String data = super.getParameter(DATA);
            if (data != null && !("").equals(data)) {
                dataNode = Json.strToObj(data, JsonNode.class);
            } else {
                return null;
            }
        }
        JsonNode nameNode = dataNode.get(name);
        if (nameNode != null) {
            String value = nameNode.asText();
            return value;
        }
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        // TODO 如果要严格遵循NYY，则除了appId和sign，其他字段必须从data中取
        String[] results = super.getParameterValues(name);
        if (results == null) {
            String value = getValueFromData(name);
            if (value != null && !("").equals(value)) {
                return new String[] { value };
            }
        }
        return results;
    }
    
    @Override
    public String getParameter(String name) {
        // TODO 
        String result = super.getParameter(name);
        if (result == null) {
            return getValueFromData(name);
        }
        return result;
    }
}
