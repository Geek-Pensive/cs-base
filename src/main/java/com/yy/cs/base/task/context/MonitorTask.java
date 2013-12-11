package com.yy.cs.base.task.context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.status.CsStatus;

public class MonitorTask {
    private static final Logger log = LoggerFactory.getLogger(MonitorTask.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private static final String html = "<html>";
    private static final String htmlEnd = "</html>";
    private static final String mete  = "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />";
    private static final String table = "<table cellpadding='0' cellspacing='0' align='center' border='1' >";
    private static final String tableEnd = "</table>";
    private static final String tr = "<tr>";
    private static final String trEnd = "</tr>";
    private static final String headTh = "<th colspan='5' align='center' >总览</th>";
    private static final String th = "<th  align='center' >";
    private static final String thEnd = "</th>";
    private static final String td = "<td align='center' style='padding:10px 10px 10px 10px;'>";
    private static final String tdEnd = "</td>"; 
    private static final String hr = "<hr />";
    private static final String br = "<br />";
    
    private static final String name = "name";
    private static final String code = "code";
    private static final String message = "message";
    private static final String failNumber = "FailNumber";
    private static final String totalNumber = "TotalNumber";
    
    
    private static final String taskId = "task id";
    private static final String nextDate = "下次执行时间";
    private static final String lastStartDate = "上次执行开始时间点";
    private static final String lastCompletionDate = "上次执行完成时间点";
    private static final String lastExceptionDate = "上次异常执行时间点";
    private static final String lastException = "上次异常信息";
//    private static final String lastAddress = "上次集群task执行地址";
    
    private  String monitorfile;
    
    public static void main(String[] args) {
//		System.out.println(getWebPath());
	}
    public MonitorTask(String monitorfile){
    	this.monitorfile = monitorfile;
    }
    
    /**
     * @return
     */
    private  String getWebPath(){
    	if(monitorfile != null && !"".equals(monitorfile)){
    		return monitorfile;
    	}
    	this.monitorfile = System.getProperty("user.dir") + File.separatorChar + "monitortask.html";
    	return monitorfile;
    } 
    
    
    public void writeTaskFile(CsStatus csStatus) {
        
        String path = getWebPath();
//        log.info("file path is {}", new Object[] { path });
        try {
            File f = new File(path);
            if (!f.exists()) {
                if (!f.createNewFile()) {
                    return;
                }
            }
            StringBuffer strBuffer = new StringBuffer();
            strBuffer.append(html);
            strBuffer.append(mete);
	        strBuffer.append(table);
	        addHeadTable(strBuffer);
	        addHeadTR(strBuffer);
	        addHeadValue(strBuffer,csStatus);
	        strBuffer.append(tableEnd);
	        strBuffer.append(hr);
	        strBuffer.append(br);
	        strBuffer.append(table);
	        addBodyTitle(strBuffer);
	        addBodyValue(strBuffer,csStatus);
	        strBuffer.append(tableEnd);
	        strBuffer.append(htmlEnd);
	        FileOutputStream fileOut =  new FileOutputStream(f);
	        fileOut.write(strBuffer.toString().getBytes("UTF-8"));
	        fileOut.flush();
	        fileOut.close();
        } catch (Exception e) {
            log.error(" write csStatus {} error : {}", csStatus, e);
        }  
    }
    
    private void addBodyValue(StringBuffer strBuffer, CsStatus csStatus) {
    	List<CsStatus> cs = csStatus.getSubCsStatus();
		if(csStatus.getSubCsStatus() == null || csStatus.getSubCsStatus().size() == 0){
			return;
		} 
    	for(CsStatus c : cs){
    		strBuffer.append("\n");
    		strBuffer.append(tr);
        	addTD(strBuffer, c.getAdditionInfo(Constants.TASK_ID));
        	addTD(strBuffer, c.getCode());
        	addTD(strBuffer, c.getMessage());
        	addTD(strBuffer, dateToString(c.getAdditionInfo(Constants.NEXT_EXECUTE_TIME)));
        	addTD(strBuffer, dateToString(c.getAdditionInfo(Constants.LAST_START_TIME)));
        	addTD(strBuffer, dateToString(c.getAdditionInfo(Constants.LAST_COMPLETION_TIME)));
        	addTD(strBuffer, dateToString(c.getAdditionInfo(Constants.LAST_EXCEPTION_TIME)));
        	addTD(strBuffer, throwableToString(c.getAdditionInfo(Constants.THROWABLE)));
            strBuffer.append(trEnd);
            strBuffer.append("\n");
		 }
	}

    private String dateToString(Object o){
    	if(o != null && o instanceof Date){
    		return sdf.format(o);
    	}
    	return "";
    }
    /**
     * 获得堆栈信息
     * 
     */
    private String getStackTrace(Throwable e) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(out);
        e.printStackTrace(pw);
        pw.close();
        try {
            return new String(out.toByteArray(), "utf-8");
        } catch (Exception e1) {
        	log.warn("", e1);
            return new String(out.toByteArray());
        }
    }
    
    private String throwableToString(Object o){
    	if(o != null && o instanceof Throwable){
    		Throwable t = (Throwable)o;
    		return getStackTrace(t);
    	}
    	return "";
    }
    
	private void addBodyTitle(StringBuffer strBuffer) {
    	strBuffer.append(tr);
    	addTH(strBuffer, taskId);
    	addTH(strBuffer, code);
    	addTH(strBuffer, message);
    	addTH(strBuffer, nextDate);
    	addTH(strBuffer, lastStartDate);
    	addTH(strBuffer, lastCompletionDate);
    	addTH(strBuffer, lastExceptionDate);
    	addTH(strBuffer, lastException);
        strBuffer.append(trEnd);
	}

	private void addTD(StringBuffer strBuffer, Object value){
		strBuffer.append(td);
        strBuffer.append(value);
        strBuffer.append(tdEnd);
	}
	
	private void addHeadValue(StringBuffer strBuffer, CsStatus csStatus) {
    	strBuffer.append(tr);
    	addTD(strBuffer,csStatus.getName());
    	addTD(strBuffer,csStatus.getCode().toString());
    	addTD(strBuffer,csStatus.getMessage());
    	addTD(strBuffer,csStatus.getFailNumber()+"");
    	addTD(strBuffer,csStatus.getTotalNumber()+"");
        strBuffer.append(trEnd);
	}


	private void addTH(StringBuffer strBuffer,String value){
    	 strBuffer.append(th);
         strBuffer.append(value);
         strBuffer.append(thEnd);
    }
    
    private void addHeadTable(StringBuffer strBuffer){
    	strBuffer.append(tr);
        strBuffer.append(headTh);
        strBuffer.append(trEnd);
   }
    
    private void addHeadTR(StringBuffer strBuffer){
    	strBuffer.append(tr);
        addTH(strBuffer,name);
        addTH(strBuffer,code);
        addTH(strBuffer,message);
        addTH(strBuffer,failNumber);
        addTH(strBuffer,totalNumber);
        strBuffer.append(trEnd);
   }
}
