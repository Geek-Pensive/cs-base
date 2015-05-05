/*
 * Copyright (c) 2012 duowan.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with duowan.com.
 */
package com.yy.cs.base.censor;

import com.yy.cs.base.censor.impl.CensorWordsImpl;
import com.yy.cs.base.censor.impl.StandardWordsFilterImpl;
import com.yy.cs.base.http.CSHttpClient;
import com.yy.cs.base.http.HttpClientException;
import com.yy.cs.base.task.thread.NamedThreadFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoweiteng
 *
 */
public class KeyWordUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyWordUtil.class);

    private static CensorWords censorWords;

    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("KeyWordUtil", true));

    private static Long lastUpdate = 0L;

    private String eTag = "\"\"";

    private final CSHttpClient httpClient;

    private String censorUrl = "http://do.yy.duowan.com/dynamic-shunt-data/type2/102/censor.txt";

    private static KeyWordUtil keywordUtil = new  KeyWordUtil();

    private long interval = 5 * 1000 * 60;

    public static KeyWordUtil getInstance() {
        return keywordUtil;
    }

    private  KeyWordUtil() {
        scheduledExecutor.scheduleAtFixedRate(new Task(), 1000, interval, TimeUnit.MILLISECONDS) ;
        httpClient = new CSHttpClient();
    }

    /**
     * 执行获取过滤关键字的任务
     *
     */
    private class Task implements Runnable{
        public void run() {
            try {
                updateCensorWords();
            } catch (Exception e) {
                LOGGER.error("updateCensorWords error!" + e.getMessage());
            }

        }
    }

    public Boolean isCensor(String text) {
        return censorWords.isCensor(text);
    }

    private CensorWords updateCensorWords() {
        List<String> list = new LinkedList<String>();
        try {
            HttpUriRequest hur = new HttpGet(censorUrl);
            hur.addHeader("If-None-Match", eTag);

            HttpClient hc = httpClient.getHttpClient();
            HttpResponse hrp = hc.execute(hur);

            if (hrp.getStatusLine().getStatusCode() == 304) {
                return censorWords;
            }
            InputStream is = hrp.getEntity().getContent();
            int len = 1;
            byte[] b = new byte[2048];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            while ((len = is.read(b, 0, 2048)) > 0) {
                os.write(b, 0, len);
            }
            is.close();
            String str = new String(os.toByteArray(), "utf-8").trim();
            os.close();
            if ("".equals(str)) {
                return censorWords;
            }
            Header[] ha = hrp.getHeaders("ETag");
            if (ha != null && ha.length > 0) {
                eTag = ha[0].getValue();
            }
            byte[] lb = Base64.decodeBase64(str);
            String ls = new String(lb, "utf-8");
            String[] ws = ls.split("\n");
            for (String t: ws) {
                list.add(t.trim());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        if (list == null || list.isEmpty()) {
            LOGGER.warn("[updateCensorWords] list must be not null or empty.");
            throw new RuntimeException("list must be not null or empty.");
        }
        censorWords = CensorWordsImpl.build(list, new StandardWordsFilterImpl());
        return censorWords;
    }

}
