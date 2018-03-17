package com.yy.cs.base.task.log.metrics;

import com.yy.cs.base.task.log.HostInfo;
import org.apache.commons.lang3.StringUtils;

/**
 * SystemUtils 封装了与潜龙相关的各种工具信息
 * 如:
 *  - 当前环境是否为潜龙环境（通过潜龙发布)
 *
 *  - 当前环境，是 潜龙什么环境 ？ 开发环境，预发布环境 或 生成环境
 *
 *  - 通过潜龙发布的进程，获取当前进程 pid
 *
 *  - 获取在潜龙上配置的项目域名信息，可作为业务进程的标识，如 sv-topic.yy.com , api-web.zhiniu8.com 等
 */
public class SystemUtils extends org.apache.commons.lang3.SystemUtils {

    private static final String ENVIRONMENT_STR = StringUtils.isBlank(System.getProperty("dragon.destEnv")) ?"-1":System.getProperty("dragon.destEnv");

    /**
     * 环境标识
     * -1 ： 未知,可以认为是本地环境
     * 1 ： 潜龙开发环境
     * 2 ： 潜龙测试环境
     * 3 ： 潜龙生产环境
     */
    public static final int ENVIRONMENT_INT = Integer.parseInt(ENVIRONMENT_STR);
    /**
     * 是否本地环境
     */
    public static final Boolean IS_LOCAL_ENVIRONMENT = ENVIRONMENT_INT == -1;
    /** 是否潜龙环境 */
    public static final Boolean IS_DRAGON_ENVIRONMENT = ENVIRONMENT_INT != -1;
    /** 是否潜龙开发环境 */
    public static final Boolean IS_DRAGON_DEVELOP_ENVIRONMENT = ENVIRONMENT_INT == 1;
    /** 是否潜孔测试环境 */
    public static final Boolean IS_DRAGON_TEST_ENVIRONMENT = ENVIRONMENT_INT == 2;
    /** 是否潜龙生产环境 */
    public static final Boolean IS_DRAGON_PRODUCT_ENVIRONMENT = ENVIRONMENT_INT == 3;


    private static final String proessIdStr = StringUtils.isBlank(System.getProperty("commons.daemon.process.id")) ?"-1":System.getProperty("commons.daemon.process.id");
    /** 进程 id */
    public static final int PROCESS_ID = Integer.parseInt(proessIdStr);



    /** 潜龙配置项目域名 */
    public static final String DRAGON_BUSINESS_DOMAIN = StringUtils.isBlank(System.getProperty("dragon.businessDomain")) ? HostInfo.getIp():System.getProperty("dragon.businessDomain");
}
