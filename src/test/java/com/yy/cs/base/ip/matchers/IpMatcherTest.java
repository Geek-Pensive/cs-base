package com.yy.cs.base.ip.matchers;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.yy.cs.base.ip.IpMatcherFactory;
import com.yy.cs.base.ip.IpUtil;

public class IpMatcherTest {

    @Test
    public void testNumberHex() {
        String ip="255.255.255.255";
        long i = IpUtil.atoi(ip);
        System.out.println(IpUtil.numberHex(i));
        System.out.println(IpUtil.itoa(i));
        System.out.println(Arrays.toString(IpUtil.atob(ip)));
        Assert.assertEquals("0xffffffff", IpUtil.numberHex(i));
        i = IpUtil.atoi("0.0.0.0");
        Assert.assertEquals("0x0", IpUtil.numberHex(i));
    }

    @Test
    public void testCommonIpMatcher() {
        String match = "127.0.0.1";
        IpMatcher matcher = IpMatcherFactory.build(match);
        System.out.println(matcher);
        Assert.assertTrue(matcher instanceof CommonIpMatcher);
        Assert.assertTrue(matcher.isMatch("127.0.0.1"));
        Assert.assertTrue(!matcher.isMatch("120.2.3.2"));
    }

    @Test
    public void testRangeIpMatcher() {
        String match = "127.0.0.1~127.0.0.10";
        IpMatcher matcher = IpMatcherFactory.build(match);
        System.out.println(matcher);
        Assert.assertTrue(matcher instanceof RangeIpMatcher);
        Assert.assertTrue(matcher.isMatch("127.0.0.1"));
        Assert.assertTrue(!matcher.isMatch("127.0.0.11"));
        Assert.assertTrue(!matcher.isMatch("1.0.0.1"));

    }

    @Test
    public void testSectionIpMatcher() {
        String match = "127.0.0.1/24";
        IpMatcher matcher = IpMatcherFactory.build(match);
        System.out.println(matcher);
        Assert.assertTrue(matcher instanceof SectionIpMatcher);
        Assert.assertTrue(matcher.isMatch("127.0.0.1"));
        Assert.assertTrue(matcher.isMatch("127.0.0.11"));
        Assert.assertTrue(matcher.isMatch("127.0.0.255"));
        Assert.assertTrue(matcher.isMatch("127.0.0.0"));

        Assert.assertTrue(!matcher.isMatch("127.0.1.1"));
        Assert.assertTrue(!matcher.isMatch("127.0.255.0"));
        Assert.assertTrue(!matcher.isMatch("127.0.1.0"));
    }

}
