package com.yy.cs.base.censor;

import org.junit.BeforeClass;
import org.junit.Test;

public class KeyWordUtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		
	}

	@Test
	public void test() throws InterruptedException {
		KeyWordUtil util = KeyWordUtil.getInstance();
		System.out.println(util.isCensored("色情"));
		synchronized (KeyWordUtilTest.this) {
			wait();
		}
	}

}
