package com.yy.cs.base.hostgroup;

import org.junit.Test;

public class HostGroupLocatorTest {

    @Test
    public void test() throws Exception {
        System.out.println(HostGroupCmdbLocator.INSTANCE.getGroup("221.228.110.138"));
        System.out.println(HostCityCmdbLocator.INSTANCE.getGroup("221.228.110.138"));
        System.out.println(HostAreaCmdbLocator.INSTANCE.getGroup("221.228.110.138"));
        
        System.out.println(HostGroupCmdbLocator.INSTANCE.getGroup("10.20.172.115"));
        System.out.println(HostCityCmdbLocator.INSTANCE.getGroup("10.20.172.115"));
        System.out.println(HostAreaCmdbLocator.INSTANCE.getGroup("10.20.172.115"));
    }
}
