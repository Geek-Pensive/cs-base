package com.yy.cs.base.task;

import com.yy.cs.base.status.CsStatus;

/**
 * 定时执行任务
 * @author duowan-PC
 *
 */
public abstract class TimerTask implements Task{
	
	private String id;						//任务Id标识
	
	private CsStatus   csStatus; 			// 任务调度执行状态
	
	private String cron;  					//quartz表达式
	
	private ClusterConfig cluster;  		//任务集成配置中心
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
	
	
	public CsStatus getCsStatus() {
		return csStatus;
	}


	public void setCsStatus(CsStatus csStatus) {
		this.csStatus = csStatus;
	}


	public String getCron() {
		return cron;
	}


	public void setCron(String cron) {
		this.cron = cron;
	}
	
	public ClusterConfig getCluster() {
		return cluster;
	}


	public void setCluster(ClusterConfig cluster) {
		this.cluster = cluster;
	}
}
