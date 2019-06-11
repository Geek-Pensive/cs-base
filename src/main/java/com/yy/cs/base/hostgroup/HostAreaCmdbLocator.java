package com.yy.cs.base.hostgroup;

import com.yy.cs.base.hostinfo.HostInfoHelper;
import com.yy.cs.base.hostinfo.OriginHostInfo;

/**
 * 通过cmdb获取机器服务区域
 * @author Zhangtao3
 * @email zhangtao3@yy.com
 */
public class HostAreaCmdbLocator extends AbstractHostGroupCmdbLocator {

    public static HostAreaCmdbLocator INSTANCE = new HostAreaCmdbLocator();
    
    public HostAreaCmdbLocator() {
    }

    public HostAreaCmdbLocator(String hostGroupLocatorUrl) {
        this();
        this.hostGroupLocatorUrl = hostGroupLocatorUrl;
    }
    
    @Override
    protected String getGroupFromLocal() {
        OriginHostInfo origInfo = HostInfoHelper.getOrigInfo();
        if (origInfo == null) {
            return null;
        }
        return origInfo.getArea_id();
    }
    
    @Override
    protected String getGroupFromRemote(ServerInfo serverInfo) {
        if(null != serverInfo && null != serverInfo.getArea_id()){
            return Long.toString(serverInfo.getArea_id());
        }
        return null;
    }
}
