package com.yy.cs.base.thrift.transport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.yy.cs.base.thrift.ThriftConfig;

public class SyncClient implements Client{

	private  final ThriftConfig config;
	
	private  final String name  = "Client";
	
	private final Class<?>  interfaces;
	
	private  TTransport transport;
	
	private  TProtocol protocol;
	
	private  Constructor<?> ctor;
	
	private  Class<?>  clinet;
	
	private  Object thriftClinet;
	
	public SyncClient(ThriftConfig config,Class<?>  interfaces){
		 this.config = config; 
		 this.interfaces = interfaces; 
	}
	
	
	public  void close(){
		if(transport != null){
			transport.close();
		}
		thriftClinet = null;
		protocol = null;
		transport = null;
		ctor = null;
		clinet = null;
	}
	
	public  Object thriftClinet(){
		return thriftClinet;
	}
	
	public void open() throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			ClassNotFoundException, SecurityException, NoSuchMethodException, TTransportException {
		
		this.clinet = createClient(interfaces);
		this.ctor = clinet.getConstructor(TProtocol.class);
		/**
		 * TODO 后续这两个地方需要独立，单独出来创建，以满足不同类型的传输和序列化组合
		 */
		transport = config.getTimeout() > 0 ? new TSocket(config.getHost(), config.getPort(),
				config.getTimeout()) : new TSocket(config.getHost(), config.getPort());
		protocol = new TBinaryProtocol(transport);
		transport.open();
		thriftClinet = ctor.newInstance(protocol);
	}
	
	
	private Class<?> createClient(Class<?> interfaces)
			throws ClassNotFoundException {
		String face = interfaces.getName();
		int f = face.lastIndexOf("$");
		if (f <= 0) {
			throw new ClassNotFoundException(face);
		}
		String clientName = face.substring(0, f + 1) + name;
		return Class.forName(clientName);
	}

	@Override
	public Class<?> getInterface() {
		return this.interfaces;
	}

}
