package com.yy.cs.base.thrift.client;

import java.lang.reflect.InvocationTargetException;

import org.apache.thrift.transport.TTransportException;

import com.yy.cs.base.thrift.ThriftConfig;

public interface Client {

	public void open() throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			ClassNotFoundException, SecurityException, NoSuchMethodException,
			TTransportException;

	public  Object thriftClinet();
	
	public  void close();
	
	public Class<?>  getInterface();
	
	public ThriftConfig  getThriftConfig();

}