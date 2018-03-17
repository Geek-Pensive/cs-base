package com.yy.cs.base.task.log.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.sysop.hawk.metrics.client2.attribute.UriTag;
import com.duowan.sysop.hawk.metrics.client2.type.CodeDistributeModel;
import com.duowan.sysop.hawk.metrics.client2.type.DefMetricsValue;
import com.duowan.sysop.hawk.metrics.client2.type.DefaultModel;
import com.duowan.sysop.hawk.metrics.client2.type.GaugeModel;
import com.duowan.sysop.hawk.metrics.client2.type.LongGauge;
import com.duowan.sysop.hawk.metrics.client2.type.MetricsModelFactory;

/**
 * 鹰眼上报的基本封装
 */
public class TaskMetricsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskMetricsClient.class);
    public static final String METRICS_SEPARATOR = "/";
    private static MetricsModelFactory factory;
	private static DefaultModel metricsIn;//别人提供服务给我们
	private static DefaultModel defMetrics;//我们提供服务给别人,默认
    private static final Boolean IS_DRAGON_ENVIROMENT = SystemUtils.IS_DRAGON_ENVIRONMENT ;
    private static final String DRAGON_BUSINESS_DOMAIN = SystemUtils.DRAGON_BUSINESS_DOMAIN;
    private static final String service_Name = SystemUtils.IS_DRAGON_PRODUCT_ENVIRONMENT ? DRAGON_BUSINESS_DOMAIN : "test_" + DRAGON_BUSINESS_DOMAIN;       // 服务名

    public static void init(String metricsAppName) {
        long[] scales = {50,100,150,200,300,500,800,1000,1500,2000,3000,
                5000,8000,10000,12000,15000,20000,30000,60000,90000};

        MetricsModelFactory.Builder builder = new MetricsModelFactory.Builder(
        			metricsAppName,				//应用名
                service_Name,				//服务名
                "timerTask", 				//服务版本
                scales, 					//时延分布
                false, 					//isFailureCode, false:第6个参数是成功码,true: 第6个参数是失败码
                0, 200);					//状态码,表示成功或失败的代码,支持多个,要么int,要么字符串

        //以下参数根据需求设置
        builder.notSkipInitialPeriod();		//指定不丢弃第1个上报周期的数据,建议设置
        builder.period(MetricsModelFactory.Period._1Min);		//设置上报周期为1分钟,还支持5分钟,(默认是1分钟)

        //创建工厂实例，一个服务使用同一个即可
        factory = builder.build();
        //创建外部服务指标模型
        defMetrics = factory.defaultModel();
        //创建内部服务指标模型，别人提供服务给我们
        metricsIn = factory.defaultModel(UriTag.INTERNAL);
    }

	public static void reportCode(String uri, int code) {
		if(!IS_DRAGON_ENVIROMENT){
			return;
		}

		try {
			defMetrics.get(uri).markCode(code);
		} catch (Exception e) {
		    LOGGER.error("report code uri:{},code:{}",uri,code,e);
		}
	}

	public static void reportTime(String uri, long time) {
		if(!IS_DRAGON_ENVIROMENT){
			return;
		}

		try {
			defMetrics.get(uri).markDuration(time);
		} catch (Exception e) {
            LOGGER.error("report time uri:{},time:{}",uri,time,e);
		}
	}

	/**
	 *  对外服务指标上报
	 * 
	 * @param uri
	 *            消息标示
	 * @param code
	 *            处理结果
	 * @param time
	 *            处理时间
	 */
	public static void reportData(String uri, int code, long time) {
		if(!IS_DRAGON_ENVIROMENT){
			return;
		}
		try {
			defMetrics.get(uri).markDurationAndCode(time, code);
		} catch (Exception e) {
		    LOGGER.error("report data,uri:{},code:{},time:{}",uri,code,time,e);
		}
	}

    public static void reportData(String uri, int code, long time,boolean isSuccessCode) {
        if(!IS_DRAGON_ENVIROMENT){
            return;
        }

        try {
            DefMetricsValue value  = defMetrics.get(uri);
            if (value != null && value.isValid()){
                value.markDurationAndCode(time, code,isSuccessCode);
            }
        } catch (Exception e) {
            LOGGER.error("report data,uri:{},code:{},time:{}",uri,code,time,e);
        }
    }

	/**
	 * 上报内部监控数据
	 * 
	 * @param uri
	 *            消息标示
	 * @param code
	 *            处理结果
	 * @param time
	 *            处理时间
	 */
	public static void reportDataInner(String uri, int code, long time) {
		if(!IS_DRAGON_ENVIROMENT){
			return;
		}
		try {
			metricsIn.get(uri).markDurationAndCode(time, code);
		} catch (Exception e) {
		    LOGGER.error("uri:{},code:{},time:{}",uri,code,time);
		}
	}

	public static void reportCodeInner(String uri, int code) {
		if(!IS_DRAGON_ENVIROMENT){
			return;
		}
		try {
			metricsIn.get(uri).markCode(code);
		} catch (Exception e) {
			LOGGER.error("uri:{},code:{}",uri,code);
		}
	}

	public static void reportGauge(String topic,String key,int number) {
        if(!IS_DRAGON_ENVIROMENT){
            return;
        }
        GaugeModel gaugeModel = factory.gauge(topic);
        LongGauge longGauge = gaugeModel.get(key);
        if(null != longGauge && longGauge.isValid()) {
            longGauge.setValue(number);
        }
    }

    public static void codeDistribute(String topic,String key,int number) {
        if(!IS_DRAGON_ENVIROMENT){
            return;
        }
        CodeDistributeModel model = factory.codeDistribute(topic);
        if(model != null) {
            model.get(key).mark(number);
        }
    }

}
