package com.yy.cs.base.hostgroup;

import com.yy.cs.base.hostinfo.HostInfoHelper;
import com.yy.cs.base.hostinfo.OriginHostInfo;

/**
 * 通过cmdb获取机器服务城市
 * @author Zhangtao3
 * @email zhangtao3@yy.com
 */
public class HostCityCmdbLocator extends AbstractHostGroupCmdbLocator {
    
    public static HostCityCmdbLocator INSTANCE = new HostCityCmdbLocator();

    public HostCityCmdbLocator() {
    }

    public HostCityCmdbLocator(String hostGroupLocatorUrl) {
        this();
        this.hostGroupLocatorUrl = hostGroupLocatorUrl;
    }
    
    @Override
    protected String getGroupFromLocal() {
        OriginHostInfo origInfo = HostInfoHelper.getOrigInfo();
        if (origInfo == null) {
            return null;
        }
        return origInfo.getCity_id();
    }

    @Override
    protected String getGroupFromRemote(ServerInfo serverInfo) {
        if(null != serverInfo && null != serverInfo.getCity_id()){
            return Long.toString(serverInfo.getCity_id());
        }
        return null;
    }
}
