package com.yy.cs.base.hostgroup;

/**
 * 获取机器服务分组
 * @author Zhangtao3
 * @email zhanghao3@yy.com
 */
public interface HostGroupLocator {

    String DEFAULT_GROUP = "*";

    /**
     * 获取本机host归属的group
     * @return 成功返回本机所在的group，失败返回<code>'*'</code>
     */
    String getGroup();

    /**
     * 获取目标host归属的group
     * @return 成功返回目标host所在的group，失败返回<code>'*'</code>
     */
    String getGroup(String host);

    /**
     * 获取目标host归属的group
     * @return 成功返回目标host所在的group，失败返回<code>defaultGroup</code>
     */
    String getGroup(String host, String defaultGroup);

}
