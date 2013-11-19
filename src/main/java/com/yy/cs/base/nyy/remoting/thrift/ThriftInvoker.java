package com.yy.cs.base.nyy.remoting.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.yy.cs.base.nyy.common.ConfigUtil;
import com.yy.cs.base.nyy.exception.NyyException;
import com.yy.cs.base.nyy.proxy.Invocation;
import com.yy.cs.base.nyy.remoting.Invoker;
import com.yy.cs.base.nyy.remoting.RemotingResult;
import com.yy.cs.base.nyy.remoting.Result;
import com.yy.cs.base.nyy.remoting.thrift.nyyService.NyyService;

public class ThriftInvoker implements Invoker {

	
	
	private final ConfigUtil config;
	
	private final NyyService.Client client;
	
	private final TTransport transport;
	
	public ThriftInvoker(ConfigUtil config){
		 
		transport = new TSocket(config.getHost(),config.getPort());
		TProtocol protocol = new TBinaryProtocol(transport);
		NyyService.Client.Factory factory = new NyyService.Client.Factory();
		client = factory.getClient(protocol);
		this.config = config;
	}
	
	public Result invoke(Invocation invocation) throws NyyException {
		String str = null;
		try {
			transport.open();
			str = client.send(invocation.getAppId(),invocation.getSign(), invocation.getData());
		} catch (TTransportException e) {
			throw new NyyException(e);
		} catch (TException e) {
			throw new NyyException(e);
		}finally{
			transport.close();
		}
		return new RemotingResult(str);
	}

	@Override
	public ConfigUtil getConfigUtil() {
		return this.config;
	}

}
