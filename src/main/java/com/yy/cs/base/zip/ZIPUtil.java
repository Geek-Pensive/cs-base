/*
 * Copyright (c) 2011 duowan.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with duowan.com.
 */
package com.yy.cs.base.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZIPUtil {
	
	/**
	 * unGZip解压缩方法
	 * @throws IOException 
	 */
    public static byte[] unGZip(byte[] data) throws IOException {
		byte[] b = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		GZIPInputStream gzip = new GZIPInputStream(bis);
		byte[] buf = new byte[1024];
		int num = -1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((num = gzip.read(buf, 0, buf.length)) != -1) {
			baos.write(buf, 0, num);
		}
		b = baos.toByteArray();
		baos.flush();
		baos.close();
		gzip.close();
		bis.close();
		return b;
    }

    /**
     * gZip压缩方法
     * @throws IOException 
     * 
     */
    public static byte[] gZip(byte[] data) throws IOException {
		byte[] b = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(bos);
		gzip.write(data);
		gzip.finish();
		gzip.close();
		b = bos.toByteArray();
		bos.close();
        return b;
    }
    
    
    /**
	 * 解压缩zip
	 * 
	 * @param data
	 *            待压缩的数据
	 * @return byte[] 解压缩后的数据
	 */
	public static byte[] unzip(byte[] data) throws IOException{
		
		byte[] b = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ZipInputStream gzip = new ZipInputStream(bis);
        byte[] buf = new byte[1024];
        int num = -1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((num = gzip.read(buf, 0, buf.length)) != -1) {
			baos.write(buf, 0, num);
		}
        b = baos.toByteArray();
        baos.flush();
        baos.close();
        gzip.close();
        bis.close();
        return b;
		 
	}

	public static byte[] zip(byte[] data) throws IOException {
		byte[] b = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream gzip = new ZipOutputStream(bos);
		gzip.write(data);
		gzip.finish();
		gzip.close();
		b = bos.toByteArray();
		bos.close();
		return b;
	}
	
    /**
     * Checks if the signature matches what is expected for a .gz file.
     *
     * @param signature the bytes to check
     * @param length    the number of bytes to check
     * @return          true if this is a .gz stream, false otherwise
     *
     */
    public static boolean matchesGZ(byte[] signature, int length) {

        if (length < 2) {
            return false;
        }
        if (signature[0] != 31) {
            return false;
        }

        if (signature[1] != -117) {
            return false;
        }

        return true;
    }
    
}
