package com.yy.cs.base.hostgroup;

import java.util.List;

/**
 * @author Zhangtao3
 * @email zhangtao3@yy.com
 */
public class ServerInfoResponse {
    private Integer code;
    private String message;
    private List<ServerInfo> object;
    private boolean success;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ServerInfo> getObject() {
        return object;
    }

    public void setObject(List<ServerInfo> object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "ServerInfoResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", object=" + object +
                ", success=" + success +
                '}';
    }
}
