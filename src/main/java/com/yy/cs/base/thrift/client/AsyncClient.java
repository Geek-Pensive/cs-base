package com.yy.cs.base.thrift.client;

import com.yy.cs.base.thrift.ThriftConfig;
import com.yy.cs.base.thrift.client.protocol.ProtocolFactory.ProtocolType;
import com.yy.cs.base.thrift.client.transport.TransportFactory.TransportType;

public class AsyncClient extends AbstractClient {
	
	public AsyncClient(ThriftConfig config,Class<?>  interfaces,TransportType ttype,ProtocolType ptype) {
		super(config, interfaces,ttype,ptype);
	}

	private  final String name  = "AsyncClient";
	
	protected  Class<?> createClient(Class<?> interfaces)
			throws ClassNotFoundException{
		String face = interfaces.getName();
		int f = face.lastIndexOf("$");
		if (f <= 0) {
			throw new ClassNotFoundException(face);
		}
		String clientName = face.substring(0, f + 1) + name;
		return Class.forName(clientName);
	}
	  
	
}