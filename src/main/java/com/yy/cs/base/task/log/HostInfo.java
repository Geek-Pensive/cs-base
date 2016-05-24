package com.yy.cs.base.task.log;

import com.yy.cs.base.hostinfo.HostInfoHelper;
import com.yy.cs.base.hostinfo.IpInfo;
import com.yy.cs.base.hostinfo.LocalIpHelper;
import com.yy.cs.base.hostinfo.NetType;
import org.apache.commons.lang3.SystemUtils;

import java.util.Map;

public class HostInfo {

    /**
     * 当前系统名称
     */
    private static final String OS_NAME = SystemUtils.OS_NAME;

    /** 当前系统是否为 windows 系统 */
    private static final Boolean IS_OS_WINDOWS = SystemUtils.IS_OS_WINDOWS;

    /** 当前系统是否为 linux 系统 */
    private static final Boolean IS_OS_LINUX = SystemUtils.IS_OS_LINUX;

    /**
     *  当前系统 ip
     *  ip 获取方式:
     *  1） 获取 /home/dspeak/yyms/hostinfo.ini 下电信 ip，若没有 转 2）
     *  2) 使用 com.yy.cs.base.hostinfo.LocalIpHelper#getLocalHostIP() 获取本地 ip，若 无法获取，设置 127.0.0.1
     */
    private static final String ip;
    static {
        String tempIp = null;
        com.yy.cs.base.hostinfo.HostInfo commonHostInfo = HostInfoHelper.getHostInfo();
        if (commonHostInfo != null){
            Map<NetType, IpInfo> ipList = commonHostInfo.getIpList();
            if (ipList != null) {
                IpInfo ipInfo = ipList.get(NetType.CTL);
                if (ipInfo != null) {
                    tempIp = ipInfo.getIp();
                }
            }
        }
        if (tempIp != null) {
            ip =  tempIp;
        } else {
            tempIp = LocalIpHelper.getLocalHostIP();
            if (tempIp != null) {
                ip = tempIp;
            } else {
                ip = "127.0.0.1";
            }
        }
    }

    public static String getOsName() {
        return OS_NAME;
    }

    public static Boolean getIsOsWindows() {
        return IS_OS_WINDOWS;
    }

    public static Boolean getIsOsLinux() {
        return IS_OS_LINUX;
    }

    public static String getIp() {
        return ip;
    }
}
