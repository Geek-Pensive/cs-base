package com.yy.cs.base.task;

import com.yy.cs.base.status.CsStatus;

public interface Task {
	
	
	 void execute();
	
	 String getId();
	 
	 CsStatus getCsStatus();
}
