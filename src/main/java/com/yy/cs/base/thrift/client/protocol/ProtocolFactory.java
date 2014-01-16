package com.yy.cs.base.thrift.client.protocol;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.apache.thrift.transport.TTransport;

public class ProtocolFactory {
	
	private ProtocolFactory(){}
	
	public static TProtocol getTProtocol(final TTransport transport,final ProtocolType type){
		
		TProtocol protocol = null;
		if(type == ProtocolType.BINARY){
			 protocol = new TBinaryProtocol(transport);
		}else if (type == ProtocolType.COMPACT){
			protocol = new TCompactProtocol(transport);
		}else if (type == ProtocolType.JSON){
			protocol = new TJSONProtocol(transport);
		}else if (type == ProtocolType.SIMPLEJSON){
			protocol = new TSimpleJSONProtocol(transport);
		}else{
			 protocol = new TBinaryProtocol(transport);
		}
		return protocol;
	}

	public enum ProtocolType{
		BINARY,COMPACT,JSON,SIMPLEJSON,TUPLE;
	}
}
