package com.yy.cs.base.thrift.transport;

import java.lang.reflect.InvocationTargetException;

import org.apache.thrift.transport.TTransportException;

public interface Client {

	public void open() throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			ClassNotFoundException, SecurityException, NoSuchMethodException,
			TTransportException;

	public  Object thriftClinet();
	
	public  void close();
	
	public Class<?>  getInterface();
	

}