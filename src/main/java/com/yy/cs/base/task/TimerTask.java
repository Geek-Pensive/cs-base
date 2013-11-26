package com.yy.cs.base.task;


public abstract class TimerTask implements Task{
	
	private String id;
	
//	private long fixedDelay;
//	
//	private long initialDelay;
	
	private String cron;
	
	private ClusterConfig cluster;
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


//	public long getFixedDelay() {
//		return fixedDelay;
//	}
//
//
//	public void setFixedDelay(long fixedDelay) {
//		this.fixedDelay = fixedDelay;
//	}


//	public long getInitialDelay() {
//		return initialDelay;
//	}
//
//
//	public void setInitialDelay(long initialDelay) {
//		this.initialDelay = initialDelay;
//	}


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
