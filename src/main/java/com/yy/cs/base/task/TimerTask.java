package com.yy.cs.base.task;

import com.yy.cs.base.status.CsStatus;


public abstract class TimerTask implements Task{
	
	private String id;
	
	private CsStatus   csStatus;
	
	private String cron;
	
	private ClusterConfig cluster;
	
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
