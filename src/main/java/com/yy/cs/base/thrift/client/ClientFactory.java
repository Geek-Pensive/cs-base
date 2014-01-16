package com.yy.cs.base.thrift.client;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.thrift.ThriftClientFactory;
import com.yy.cs.base.thrift.ThriftConfig;
import com.yy.cs.base.thrift.client.protocol.ProtocolFactory.ProtocolType;
import com.yy.cs.base.thrift.client.transport.TransportFactory.TransportType;
import com.yy.cs.base.thrift.exception.CsThriftException;
 

public class ClientFactory extends BasePoolableObjectFactory {
	private static Logger log = LoggerFactory.getLogger(ClientFactory.class);

	private final ThriftConfig config;

	private final Class<?> interfaces;
	
	private final TransportType ttype;
	
	private final ProtocolType ptype;
	
	public ClientFactory(ThriftConfig config,ThriftClientFactory<?> thriftClient) {
		this.config = config;
		this.interfaces = thriftClient.getInterface();
		this.ttype = thriftClient.getTransport();
		this.ptype = thriftClient.getProtocol();
	}

	/**
	 * 创建TTransport，
	 * TODO 应该根据配置支持多种TTransport
	 */
	public Client  makeObject() throws CsThriftException {
		Client client = new SyncClient(config,interfaces,ttype,ptype);
		try {
			client.open();
		} catch (Exception e) {
			log.error("error creating client to:" + config.getHost() + ":" + config.getPort(), e);
			throw new CsThriftException(e.getMessage(),e);
		}  
		log.debug("client created, [" + config.getHost() + ":" + config.getPort() + "]");
		return client;
	}

	 
	public void destroyObject(Object obj) throws Exception {
		Client client = (Client) obj;
		client.close();
		log.debug("client closed, [" + config.getHost() + ":" + config.getPort() + "] ");
	}

	public enum ClientType{
		SYNC("sync"),
		ASYNC("async");
		private String name = "";
		
		private ClientType(String name){
			this.name = name;
		}
		
		public static ClientType getType(String name){
			for (ClientType type : ClientType.values()) {
				if (type.getName().equalsIgnoreCase(name)) {
					return type;
				}
			}
			return null;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}