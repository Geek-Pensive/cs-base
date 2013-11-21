package com.yy.cs.base.task;

import javax.sql.DataSource;

public class ClusterConfig {

	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
