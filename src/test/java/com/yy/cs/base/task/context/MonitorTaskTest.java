package com.yy.cs.base.task.context;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.yy.cs.base.censor.impl.EmptyWordsFilterImpl;
import com.yy.cs.base.task.trigger.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.yy.cs.base.status.CsStatus;
import com.yy.cs.base.status.StatusCode;
import com.yy.cs.base.task.context.Constants.MonitorType;



public class MonitorTaskTest {

	private static final String monitorFile = "D:\\workspace\\monitortask.txt";
	
	private static final String monitorFileLog = "/data/monitortask.log";
	
	private static final MonitorType monitorType = MonitorType.LOG;
	
	private CsStatus csStatus;
	

/**
 * 初始化csStatus	
 */
	private void initCsStatus(){
		CsStatus subStatusFst = new CsStatus(StatusCode.FAIL,"1111111","Test11111");
		CsStatus subStatusSed = new CsStatus(StatusCode.FAIL,"2222222","Test22222");
		CsStatus subStatusThd = new CsStatus(StatusCode.SUCCCESS,"3333333","Test33333");
		CsStatus subStatusFth = new CsStatus(StatusCode.WRONG,"4444444","Test4444");
		csStatus = new CsStatus();
		csStatus.addSubCsStatus(subStatusFst);
		csStatus.addSubCsStatus(subStatusSed);
		csStatus.addSubCsStatus(subStatusThd);
		csStatus.addSubCsStatus(subStatusFth);
	}
	
   /**
    * 1.用户没有设置monitorFile，文件名由monitorType决定
    * 	a.monitorType = MonitorType.LOG： 当前路径+"monitortask.log"
    * 	b.monitorType = MonitorType.HTML：当前路径+"monitortask.html"
    *  	c.monitorType没有设定，默认为HTML格式输出：当前路径+"monitortask.html"
    * 2.配置了monitorFile，文件名与monitorFile一致。文件格式同上
    */
	@Test
    public void testGetWebPath(){
    	MonitorTask monitor = new MonitorTask();
    	MonitorTask monitorB = new MonitorTask("");
    	MonitorTask monitorF = new MonitorTask(monitorFile);
    	MonitorTask monitorFT = new MonitorTask(monitorFile, monitorType);
    	String pathStr = System.getProperty("user.dir") + File.separatorChar;
    	String path = monitor.getWebPath();
    	String pathB = monitorB.getWebPath();
    	String pathF = monitorF.getWebPath();
    	String pathFT = monitorFT.getWebPath();
    	Assert.assertNotNull(path);
    	Assert.assertTrue(pathB.contains(pathStr));
    	Assert.assertEquals(pathF, monitorFile);
    	Assert.assertEquals(pathFT, monitorFile);   	
    }

	/**
	 * 测试生成两种格式的监控文件：LOG/默认HTML格式
	 */
	@Test
	public void testWriteTaskFile(){
		initCsStatus();
    	MonitorTask monitorFT = new MonitorTask(null, monitorType);
    	MonitorTask monitorF = new MonitorTask(monitorFile);
    	monitorFT.writeTaskFile(csStatus);
    	monitorF.writeTaskFile(csStatus);
    	Assert.assertTrue(new File(monitorFile).exists());
    	Assert.assertTrue(new File(monitorFileLog).exists());
	}

	@Test
	public void testDeleteTaskFiles() throws Exception {
		initCsStatus();
		MonitorTask monitorFT = new MonitorTask(null, monitorType);
		monitorFT.writeTaskFile(csStatus);
		String logFilePath = monitorFT.getWebPath();
		File logFile = new File(logFilePath);
		Assert.assertTrue(logFile.exists());
//		monitorFT.deleteTaskLogFiles(-1);
		Assert.assertFalse(logFile.exists());
	}
}
