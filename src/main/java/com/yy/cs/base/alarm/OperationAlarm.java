package com.yy.cs.base.alarm;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OperationAlarm {
    private static final String PYTHON = "/home/dspeak/yyms/yymp/yymp_report_script/yymp_report_alarm.py";

    /**
     * 调用运维的告警脚本进行告警。
     * 
     * @param reportId 告警ID，http://ms.sysop.duowan.com/feature/index.jspx 中新建和管理（特性ID）
     * @param progressName 进程名称，http://ms.sysop.duowan.com/strategy/list.jspx 告警策略中配置，关联特性ID
     * @param message 告警消息（空格会被替换）
     */
    public static void callAlarm(String reportId, String progressName, String message)
            throws IllegalArgumentException, RuntimeException {
        if (null == reportId || null == progressName || null == message) {
            throw new IllegalArgumentException("argument[reportId,progressName,message] cannot be null");
        }
        try {
            Process p = Runtime.getRuntime().exec(new String[] { "python", PYTHON, reportId, progressName, message });
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            p.waitFor();
            reader.close();
            p.destroy();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
