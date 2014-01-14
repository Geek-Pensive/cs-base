package com.yy.cs.base.thrift.client.transport;

import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.yy.cs.base.thrift.ThriftConfig;

public class TransportFactory {
	
	private TransportFactory(){}
	
	public static TTransport getTTransport(final ThriftConfig config,TransportType type){
		
		TTransport t = null;
		if(type == TransportType.TSOCKET){
			t = config.getTimeout() > 0 ? new TSocket(config.getHost(), config.getPort(),config.getTimeout())
						: new TSocket(config.getHost(), config.getPort());
		}else if(type == TransportType.TFRAMED){
			t = config.getTimeout() > 0 ? new TSocket(config.getHost(), config.getPort(), config.getTimeout()) 
						: new TSocket(config.getHost(), config.getPort());
			t = new TFramedTransport(t);
		}else{
			t = config.getTimeout() > 0 ? new TSocket(config.getHost(), config.getPort(), config.getTimeout())
						: new TSocket(config.getHost(), config.getPort());
			t = new TFramedTransport(t);
		}
		return t;
	}

	public enum TransportType{
		TSOCKET,TFRAMED;
	}
}


