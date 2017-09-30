package com.yy.cs.base.ip;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.task.thread.NamedThreadFactory;

public class OfficeIpLoader {

    private static final Logger logger = LoggerFactory.getLogger(OfficeIpLoader.class);

    private static final String HOST = "http://webapi.sysop.duowan.com:62175/office/ip_desc.txt";
    private static final Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("OfficeIpLoader", true));

    private String lastUpdateContent;
    private Set<String> ips = new HashSet<>();
    private IpMatcherManager matchManager;

    private static class OfficeIpLoaderHolder {
        private static final OfficeIpLoader instance = new OfficeIpLoader();
    }

    private OfficeIpLoader() {
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                try {
                    build();
                } catch (Exception e) {
                    logger.error("build content ", e);
                }
            }

        }, 0, 600, TimeUnit.SECONDS);
    }

    public static OfficeIpLoader getInstance() {
        return OfficeIpLoaderHolder.instance;
    }

    public Set<String> getCompanyIps() {
        return ips;
    }

    public boolean isCompanyIp(String ip) {
        if (null == matchManager) {
            return false;
        }
        return matchManager.isMatch(ip);
    }

    private void build() throws Exception {
        List<String> lines = load();
        String updateContent = getContent(lines);
        if (!updateContent.equals(lastUpdateContent)) {
            List<String> ips = parseIp(lines);
            this.ips = new HashSet<String>(ips);
            lastUpdateContent = updateContent;

            List<String> groups = groupIp(ips);
            this.matchManager = new IpMatcherManager(IpMatcherFactory.matchers(groups));
        }
    }

    private String getContent(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private List<String> parseIp(List<String> ipContent) {
        List<String> ret = new ArrayList<>();
        for (String line : ipContent) {
            Matcher m = pattern.matcher(line);
            while (m.find()) {
                ret.add(m.group());
            }
        }
        return ret;
    }

    private List<String> load() throws Exception {
        URL url = new URL(HOST);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        List<String> lines = new ArrayList<>();
        try {
            lines = readLines(conn.getInputStream());
        } finally {
            conn.disconnect();
        }
        return lines;
    }

    private static List<String> groupIp(List<String> ips) {
        List<String> ret = new ArrayList<>();
        Collections.sort(ips, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                long t = IpUtil.atoi(o1) - IpUtil.atoi(o2);
                if (t > 0) {
                    return 1;
                } else if (t < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }

        });
        String start = "";
        String stepIp = "";
        long startNum = 0;
        long stepNum = 0;
        int step = 0;
        for (String ip : ips) {
            if ("".equals(start)) {
                start = ip;
                startNum = IpUtil.atoi(start);
                step++;
                stepIp = start;
                stepNum = startNum;
                continue;
            }
            long c = IpUtil.atoi(ip);
            if ((c - stepNum) == 1) {
                step++;
                stepIp = ip;
                stepNum = c;
                continue;
            } else {
                if (step > 1) {
                    ret.add(start + "~" + stepIp);
                } else {
                    ret.add(stepIp);
                }
                start = ip;
                startNum = c;
                stepNum = c;
                step = 1;
                stepIp = ip;
            }
        }
        return ret;
    }

    private List<String> readLines(InputStream is) throws Exception {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line = null;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
        return lines;
    }

    public static void main(String[] args) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        System.out.println(OfficeIpLoader.getInstance().isCompanyIp("58.248.229.148"));
    }
}
