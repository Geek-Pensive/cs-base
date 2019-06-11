package com.yy.cs.base.hostgroup;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.hostinfo.LocalIpHelper;
import com.yy.cs.base.http.CSHttpClient;
import com.yy.cs.base.json.Json;

/**
 * 获取机器服务分组
 * @author Zhangtao3
 * @email zhangtao3@yy.com
 */
public abstract class AbstractHostGroupCmdbLocator implements HostGroupLocator {
    
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final String GROUP_URL = "http://ws.cmdb.sysop.duowan.com/webservice/server/getServerInfos.do";

    protected static final Map<String, SoftReference<ServerInfo>> serverInfoCacheMap = new ConcurrentHashMap<>();

    protected static final CSHttpClient httpClient = new CSHttpClient();
    
    protected String hostGroupLocatorUrl = GROUP_URL;
    
    protected ServerInfo getServerInfo(String host) {
        SoftReference<ServerInfo> serverInfoRef = serverInfoCacheMap.get(host);
        if (serverInfoRef == null || serverInfoRef.get() == null) {
            try {
                String response = httpClient.doGet(toCmdbServerInfoUrl(host));
                ServerInfoResponse serverInfoResponse = Json.strToObj(response, ServerInfoResponse.class);
                if (serverInfoResponse != null) {
                    List<ServerInfo> serverInfoList = serverInfoResponse.getObject();
                    if (serverInfoList != null && !serverInfoList.isEmpty()) {
                        ServerInfo serverInfo = serverInfoList.get(0);
                        if (log.isInfoEnabled()) {
                            log.info("HostGroupLocatorCmdb update serverInfoMap,host:{},serverInfo:{}", host,
                                    serverInfo);
                        }
                        if (serverInfo != null) {
                            serverInfoCacheMap.put(host, new SoftReference<>(serverInfo));
                            return serverInfo;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("HostGroupLocatorCmdb getGroup failed,host:{},cause:{}", host, e);
            }
        } else {
            return serverInfoRef.get();
        }
        return null;
    }
    
    private String toCmdbServerInfoUrl(String host) {
        return hostGroupLocatorUrl + "?ip=" + host;
    }
    
    /**
     * 不存在返回null
     * 
     * @return
     */
    protected abstract String getGroupFromLocal();
    
    /**
     * 不存在返回null
     * 
     * @param serverInfo
     * @return
     */
    protected abstract String getGroupFromRemote(ServerInfo serverInfo);

    @Override
    public String getGroup() {

        // get from local
        String groupId = getGroupFromLocal();
        if (groupId != null && groupId.length() > 0) {
            return groupId;
        }

        // get from remote
        String localhost = LocalIpHelper.getLocalHostIP();
        return getGroup(localhost);
    }

    @Override
    public String getGroup(String host) {
        return this.getGroup(host, DEFAULT_GROUP);
    }

    @Override
    public String getGroup(String host, String defaultGroup) {
        ServerInfo serverInfo = getServerInfo(host);
        String priGroupIdStr = getGroupFromRemote(serverInfo);
        priGroupIdStr = priGroupIdStr == null ? defaultGroup : priGroupIdStr;
        if (log.isInfoEnabled()) {
            log.info("HostGroupLocatorCmdb getGroup priGroupId:{}", priGroupIdStr);
        }
        return priGroupIdStr;
    }
}
