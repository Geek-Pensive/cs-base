package com.yy.cs.base.thrift.protocol;

import java.lang.reflect.Constructor;
import java.util.NoSuchElementException;

import org.apache.commons.pool.ObjectPool;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.pooling.IObjectPoolManager;
import com.yy.cs.base.thrift.exception.CsThriftException;
import com.yy.cs.base.thrift.proxy.Invocation;

 

public class ClientInvoker  {
	
	private static Logger log = LoggerFactory.getLogger(ClientInvoker.class);
	
	private final String name  = "Client";
	
	private final Class<?>  interfaces;
	
	private final Constructor<?> ctor;
	
	private final Class<?>  clinet;
	
	private final IObjectPoolManager<? extends ObjectPool, TTransport> objPoolMgr;
	
	public ClientInvoker(IObjectPoolManager<? extends ObjectPool, TTransport> objPoolMgr,Class<?>  interfaces) throws ClassNotFoundException, SecurityException, NoSuchMethodException{
		this.objPoolMgr = objPoolMgr;
		this.interfaces = interfaces;
		this.clinet = createClient(interfaces);
		this.ctor = clinet.getConstructor(TProtocol.class);
	}
 

	public Object invoke(Invocation invocation) throws Exception {
		TTransport transport = null;
    	try{
    		transport = objPoolMgr.borrowObject();
			TProtocol protocol = new TBinaryProtocol(transport);
			Object o = ctor.newInstance(protocol);
	    	return invocation.getMethod().invoke(o, invocation.getParameters());
    	} catch (NoSuchElementException e) {
			log.error(e.getMessage(),e);
			throw new CsThriftException(e.getMessage(),e);
		} catch (IllegalStateException e) {
			log.error(e.getMessage(),e);
			throw new CsThriftException(e.getMessage(),e);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new CsThriftException(e.getMessage(),e);
		}finally{
			if(transport != null){
				objPoolMgr.returnObject(transport);
			}
    	}
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

	public Class<?> getInterface() {
		return interfaces;
	}

}
