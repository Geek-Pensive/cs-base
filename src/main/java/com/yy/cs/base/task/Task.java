package com.yy.cs.base.task;

import com.yy.cs.base.status.CsStatus;

public interface Task {
	
	
	 void execute() throws Exception;
	
	 String getId();
	 
	 CsStatus getCsStatus();
}
