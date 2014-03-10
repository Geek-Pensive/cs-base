package com.yy.cs.base.thrift.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.yy.cs.base.thrift.ThriftConfig;
import com.yy.cs.base.thrift.client.protocol.ProtocolFactory;
import com.yy.cs.base.thrift.client.protocol.ProtocolFactory.ProtocolType;
import com.yy.cs.base.thrift.client.transport.TransportFactory;
import com.yy.cs.base.thrift.client.transport.TransportFactory.TransportType;

public abstract class AbstractClient  implements Client{
	
	private  final ThriftConfig config;
	
	private final Class<?>  interfaces;
	
	private  TTransport transport;
	
	private  TProtocol protocol;
	
	private  Constructor<?> ctor;
	
	private  Class<?>  clinet;
	
	private  Object thriftClinet;
	
	private final TransportType ttype;
	
	private final ProtocolType ptype;
	
	public AbstractClient(ThriftConfig config,Class<?>  interfaces,TransportType ttype,ProtocolType ptype){
		 this.config = config; 
		 this.interfaces = interfaces; 
		 this.ttype = ttype;
		 this.ptype = ptype;
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
		transport = TransportFactory.getTTransport(config, ttype);
		protocol = ProtocolFactory.getTProtocol(transport, ptype);// new TBinaryProtocol(transport);
		transport.open();
		thriftClinet = ctor.newInstance(protocol);
	}
	
	protected abstract Class<?> createClient(Class<?> interfaces)
			throws ClassNotFoundException ;

	@Override
	public Class<?> getInterface() {
		return this.interfaces;
	}
	
	@Override
	public ThriftConfig  getThriftConfig(){
		return this.config;
	}
}
