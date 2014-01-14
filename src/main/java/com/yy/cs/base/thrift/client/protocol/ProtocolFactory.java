package com.yy.cs.base.thrift.client.protocol;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

public class ProtocolFactory {
	
	private ProtocolFactory(){}
	
	public static TProtocol getTProtocol(final TTransport transport,final ProtocolType type){
		
		TProtocol protocol = null;
		if(type == ProtocolType.BINARY){
			 protocol = new TBinaryProtocol(transport);
		}else{
			 protocol = new TBinaryProtocol(transport);
		}
		return protocol;
	}

	public enum ProtocolType{
		BINARY;
	}
}
