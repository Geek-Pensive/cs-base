package com.yy.cs.base.nyy.remoting;

import com.yy.cs.base.nyy.common.ConfigUtil;
import com.yy.cs.base.nyy.exception.NyyException;
import com.yy.cs.base.nyy.proxy.Invocation;

 

public interface Invoker  {
	
	Result invoke(Invocation invocation) throws NyyException;
	
	ConfigUtil  getConfigUtil();
}
