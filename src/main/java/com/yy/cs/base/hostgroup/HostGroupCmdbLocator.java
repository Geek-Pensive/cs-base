package com.yy.cs.base.hostgroup;

import com.yy.cs.base.hostinfo.HostInfoHelper;
import com.yy.cs.base.hostinfo.LocalIpHelper;
import com.yy.cs.base.hostinfo.OriginHostInfo;
import com.yy.cs.base.http.CSHttpClient;
import com.yy.cs.base.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过cmdb获取机器服务分组
 * @author Zhangtao3
 * @email zhanghao3@yy.com
 */
public class HostGroupCmdbLocator implements HostGroupLocator {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final String GROUP_URL = "http://ws.cmdb.sysop.duowan.com/webservice/server/getServerInfos.do";

    private static final Map<String, SoftReference<ServerInfo>> serverInfoCacheMap = new ConcurrentHashMap<>();

    protected String hostGroupLocatorUrl = GROUP_URL;

    protected final CSHttpClient httpClient;

    public HostGroupCmdbLocator() {
        httpClient = new CSHttpClient();
    }

    public HostGroupCmdbLocator(String hostGroupLocatorUrl) {
        this();
        this.hostGroupLocatorUrl = hostGroupLocatorUrl;
    }

    @Override
    public String getGroup() {

        // get from local
        String groupId = getLocalGroupFromLocal();
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
        Long priGroupId = null;
        String priGroupIdStr;
        SoftReference<ServerInfo> serverInfoRef = serverInfoCacheMap.get(host);
        if (serverInfoRef == null || serverInfoRef.get() == null) {
            try {
                String response = httpClient.doGet(toCmdbServerInfoUrl(host));
                ServerInfoResponse serverInfoResponse = Json.strToObj(response, ServerInfoResponse.class);
                if (serverInfoResponse != null) {
                    List<ServerInfo> serverInfoList = serverInfoResponse.getObject();
                    if (serverInfoList != null && !serverInfoList.isEmpty()) {
                        ServerInfo serverInfo = serverInfoList.get(0);
                        priGroupId = serverInfo.getPriGroupId();
                        if (priGroupId != null) {
                            serverInfoCacheMap.put(host, new SoftReference<>(serverInfo));
                        }
                        if (log.isInfoEnabled()) {
                            log.info("HostGroupLocatorCmdb update serverInfoMap,host:{},serverInfo:{}", host,
                                    serverInfo);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("HostGroupLocatorCmdb getGroup failed,host:{},cause:{}", host, e);
            }
        } else {
            priGroupId = serverInfoRef.get().getPriGroupId();
        }
        priGroupIdStr = priGroupId == null ? defaultGroup : Long.toString(priGroupId);
        if (log.isInfoEnabled()) {
            log.info("HostGroupLocatorCmdb getGroup priGroupId:{}", priGroupIdStr);
        }
        return priGroupIdStr;
    }

    protected String toCmdbServerInfoUrl(String host) {
        return hostGroupLocatorUrl + "?ip=" + host;
    }

    private String getLocalGroupFromLocal() {
        OriginHostInfo origInfo = HostInfoHelper.getOrigInfo();
        if (origInfo == null) {
            return null;
        }
        return origInfo.getPri_group_id();
    }

}
