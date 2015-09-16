package com.yy.cs.base.task;

import com.yy.cs.base.status.CsStatus;

/**
 * 
 * 定时任务的的抽象类，提交到 {@link TimerTaskManager} 的任务都需要继承该类，并且实现  execute() 方法
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
	
	@Override
	public String toString() {
		return "TimerTask [id=" + id + ", csStatus=" + csStatus + ", cron="
				+ cron + ", cluster=" + cluster + "]";
	}

}
