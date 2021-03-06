package com.yy.cs.base.hostgroup;

import com.yy.cs.base.hostinfo.HostInfoHelper;
import com.yy.cs.base.hostinfo.OriginHostInfo;

/**
 * 通过cmdb获取机器服务分组
 * @author Zhangtao3
 * @email zhangtao3@yy.com
 */
public class HostGroupCmdbLocator extends AbstractHostGroupCmdbLocator {

    public static HostGroupCmdbLocator INSTANCE = new HostGroupCmdbLocator();
    
    public HostGroupCmdbLocator() {
    }

    public HostGroupCmdbLocator(String hostGroupLocatorUrl) {
        this();
        this.hostGroupLocatorUrl = hostGroupLocatorUrl;
    }

    @Override
    protected String getGroupFromLocal() {
        OriginHostInfo origInfo = HostInfoHelper.getOrigInfo();
        if (origInfo == null) {
            return null;
        }
        return origInfo.getPri_group_id();
    }

    @Override
    protected String getGroupFromRemote(ServerInfo serverInfo) {
        if(null != serverInfo && null != serverInfo.getPriGroupId()){
            return Long.toString(serverInfo.getPriGroupId());
        }
        return null;
    }
}
