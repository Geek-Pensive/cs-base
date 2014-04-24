package com.yy.cs.base.task.context;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.status.CsStatus;
import com.yy.cs.base.task.context.Constants.MonitorType;

public class MonitorTask {
	private static final Logger log = LoggerFactory
			.getLogger(MonitorTask.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static final String html = "<html>";
	private static final String htmlEnd = "</html>";
	private static final String mete = "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />";
	private static final String table = "<table cellpadding='0' cellspacing='0' align='center' border='1' >";
	private static final String tableEnd = "</table>";
	private static final String tr = "<tr>";
	private static final String trEnd = "</tr>";
	private static final String headTh = "<th colspan='5' align='center' >总览</th>";
	private static final String th = "<th  align='center' >";
	private static final String thEnd = "</th>";
	private static final String td = "<td align='center' style='padding:10px 10px 10px 10px;'>";
	private static final String tdExp = "<td align='center' style='padding:10px 10px 10px 10px;word-break: break-all; word-wrap:break-word;'>";
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
	private static final String lastStartDate = "开始时间";
	private static final String lastCompletionDate = "完成时间";
	private static final String lastExceptionDate = "异常时间";
	private static final String lastException = "异常信息";
	private static final String timeout = "是否超时";
	// private static final String lastAddress = "上次集群task执行地址";

	private String monitorfile;
	private MonitorType monitorType;


	public static void main(String[] args) {
		// System.out.println(getWebPath());
	}

	public MonitorTask(String monitorfile) {
		this.monitorfile = monitorfile;
	}

	public MonitorTask() {
	}

	public MonitorTask(String monitorfile, MonitorType monitorType) {
		this.monitorfile = monitorfile;
		this.monitorType = monitorType;
	}

	/**
	 * @return
	 */
	protected String getWebPath() {
		if (monitorfile != null && !"".equals(monitorfile)) {
			return monitorfile;
		}
		String dragonProjName = System.getProperty("dragon.bizName.projName");
    	String parent = null;
    	if(dragonProjName != null && !"".equals(dragonProjName)){
    		String p = "/data/file";
    		File f = new File(p);
    		if(f.exists() && f.isDirectory()){
    			parent = p + File.separator + dragonProjName;	
    		}
    	}
    	if(parent == null || "".equals(parent)){
    		parent = System.getProperty("user.dir");
    	}
    	if(!parent.endsWith(File.separator)){
    		parent += File.separator;
    	}
		if (MonitorType.LOG == this.monitorType) {
			this.monitorfile = parent + "monitortask.log";
		}else{
			this.monitorfile = parent + "monitortask.html";
		}
		return monitorfile;
	}

	public void writeTaskFile(CsStatus csStatus) {

		String path = getWebPath();
		try {
			File f = new File(path);
			if (!f.exists()) {
				if (!f.createNewFile()) {
					return;
				}
			}
			StringBuffer strBuffer = new StringBuffer();
			if (MonitorType.LOG == this.monitorType) {
				addLogInfo(strBuffer, csStatus);
			} else {
				addHtmlInfo(strBuffer, csStatus);
			}
			FileOutputStream fileOut = new FileOutputStream(f);
			fileOut.write(strBuffer.toString().getBytes("UTF-8"));
			fileOut.flush();
			fileOut.close();
		} catch (Exception e) {
			log.error("write file path:"+ path +" error:" + e.getMessage(), e);
		}
	}

	private void addBodyValue(StringBuffer strBuffer, CsStatus csStatus) {
		List<CsStatus> cs = csStatus.getSubCsStatus();
		if (csStatus.getSubCsStatus() == null
				|| csStatus.getSubCsStatus().size() == 0) {
			return;
		}
		for (CsStatus c : cs) {
			strBuffer.append("\n");
			strBuffer.append(tr);
			addTD(strBuffer, c.getAdditionInfo(Constants.TASK_ID));
			addTD(strBuffer, c.getCode());
			addTD(strBuffer, c.getMessage());
			addTD(strBuffer,
					dateToString(c.getAdditionInfo(Constants.NEXT_EXECUTE_TIME)));
			addTD(strBuffer,
					dateToString(c.getAdditionInfo(Constants.LAST_START_TIME)));
			addTD(strBuffer,
					dateToString(c
							.getAdditionInfo(Constants.LAST_COMPLETION_TIME)));
			addTD(strBuffer,
					dateToString(c
							.getAdditionInfo(Constants.LAST_EXCEPTION_TIME)));
			addTDExp(strBuffer,
					throwableToString(c.getAdditionInfo(Constants.THROWABLE)));
			addTD(strBuffer, c.getAdditionInfo(Constants.TIMEOUT));
			strBuffer.append(trEnd);
			strBuffer.append("\n");
		}
	}

	private String dateToString(Object o) {
		if (o != null && o instanceof Date) {
			return sdf.format(o);
		}
		return "";
	}

	/**
	 * 获得堆栈信息
	 */
	private String getStackTrace(Throwable e) {
		StackTraceElement[] traces = e.getStackTrace();
		if (traces == null) {
			return "";
		}
		StringBuffer str = new StringBuffer();
		for (StackTraceElement t : traces) {
			str.append(t.toString());
		}
		try {
			return str.toString();// ;new String(out.toByteArray(), "utf-8");
		} catch (Exception e1) {
			log.warn("", e1);
			return "";
		}
	}

	private String throwableToString(Object o) {
		if (o != null && o instanceof Throwable) {
			Throwable t = (Throwable) o;
			return getStackTrace(t);
		}
		return "";
	}

	private String throwableInfo(Object o) {
		if (o != null && o instanceof Throwable) {
			Throwable t = (Throwable) o;
			return t.toString();
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
		addTH(strBuffer, timeout);
		strBuffer.append(trEnd);
	}

	private void addTD(StringBuffer strBuffer, Object value) {
		strBuffer.append(td);
		strBuffer.append(value);
		strBuffer.append(tdEnd);
	}
	private void addTDExp(StringBuffer strBuffer, Object value) {
		strBuffer.append(tdExp);
		strBuffer.append(value);
		strBuffer.append(tdEnd);
	}
	private void addHeadValue(StringBuffer strBuffer, CsStatus csStatus) {
		strBuffer.append(tr);
		addTD(strBuffer, csStatus.getName());
		addTD(strBuffer, csStatus.getCode().toString());
		addTD(strBuffer, csStatus.getMessage());
		addTD(strBuffer, csStatus.getFailNumber() + "");
		addTD(strBuffer, csStatus.getTotalNumber() + "");
		strBuffer.append(trEnd);
	}

	private void addTH(StringBuffer strBuffer, String value) {
		strBuffer.append(th);
		strBuffer.append(value);
		strBuffer.append(thEnd);
	}

	private void addHeadTable(StringBuffer strBuffer) {
		strBuffer.append(tr);
		strBuffer.append(headTh);
		strBuffer.append(trEnd);
	}

	private void addHeadTR(StringBuffer strBuffer) {
		strBuffer.append(tr);
		addTH(strBuffer, name);
		addTH(strBuffer, code);
		addTH(strBuffer, message);
		addTH(strBuffer, failNumber);
		addTH(strBuffer, totalNumber);
		strBuffer.append(trEnd);
	}

	private void addLogInfo(StringBuffer strBuffer, CsStatus csStatus) {
		List<CsStatus> cs = csStatus.getSubCsStatus();
		if (csStatus.getSubCsStatus() == null
				|| csStatus.getSubCsStatus().size() == 0) {
			return;
		}
		for (CsStatus c : cs) {
			strBuffer.append(c.getAdditionInfo(Constants.TASK_ID));
			strBuffer.append(";");
			strBuffer.append(c.getCode());
			strBuffer.append(";");
			if (c.getMessage()== null || c.getMessage() == "") {
				strBuffer.append(c.getMessage());
				strBuffer.append(";");
			}
			strBuffer.append(lastExceptionDate);
			strBuffer.append(":");
			strBuffer.append(dateToString(c
					.getAdditionInfo(Constants.LAST_EXCEPTION_TIME)));
			strBuffer.append(";");
			strBuffer.append(lastException);
			strBuffer.append(":");
			strBuffer.append(throwableInfo(c
					.getAdditionInfo(Constants.THROWABLE)));
			strBuffer.append(";");
			Object out = c.getAdditionInfo(Constants.TIMEOUT);
			if(null != out &&  Boolean.parseBoolean(out.toString())){
				strBuffer.append(timeout);
				strBuffer.append(":");
				strBuffer.append(out);
				strBuffer.append(";");
			}
			strBuffer.append("\r\n");
		}
	}

	private void addHtmlInfo(StringBuffer strBuffer, CsStatus csStatus) {
		strBuffer.append(html);
		strBuffer.append(mete);
		strBuffer.append(table);
		addHeadTable(strBuffer);
		addHeadTR(strBuffer);
		addHeadValue(strBuffer, csStatus);
		strBuffer.append(tableEnd);
		strBuffer.append(hr);
		strBuffer.append(br);
		strBuffer.append(table);
		addBodyTitle(strBuffer);
		addBodyValue(strBuffer, csStatus);
		strBuffer.append(tableEnd);
		strBuffer.append(htmlEnd);
	}

}
