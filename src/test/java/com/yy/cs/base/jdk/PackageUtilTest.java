package com.yy.cs.base.jdk;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class PackageUtilTest {

    @Test
    public void testListClassByPackage() {
        List<Class<?>> classes = PackageUtil.listClassByPackage("com.yy.cs.base.jdk", true,
                new PackageUtil.ClassFilter() {

                    @Override
                    public boolean filter(Class<?> clz) {
                        return true;
                    }
                });
        System.out.println(classes);
        Assert.assertTrue("listPackage not Contain PackageUtil", classes.contains(PackageUtil.class));
    }

}
