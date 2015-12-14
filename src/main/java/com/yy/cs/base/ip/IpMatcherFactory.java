package com.yy.cs.base.ip;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.yy.cs.base.ip.matchers.CommonIpMatcher;
import com.yy.cs.base.ip.matchers.IpMatcher;
import com.yy.cs.base.ip.matchers.RangeIpMatcher;
import com.yy.cs.base.ip.matchers.SectionIpMatcher;

public class IpMatcherFactory {

    private static List<IpMatcher> matchers=new LinkedList<IpMatcher>();;

    static {
        matchers.add(new CommonIpMatcher());    
        matchers.add(new RangeIpMatcher());
        matchers.add(new SectionIpMatcher());
    }

    public static IpMatcher build(String ipMatch) {
        IpMatcher matcher = null;
        for (IpMatcher m : matchers) {
            if (m.getMatchPatter().matcher(ipMatch).matches()) {
                matcher = m.build(ipMatch);
                break;
            }
        }
        return matcher;
    }
    
    public static List<IpMatcher> matchers(List<String> ipMatchs){
        return matchers(new HashSet<String>(ipMatchs));
    }
    
    public static List<IpMatcher> matchers(Set<String> ipMatchs){
        List<IpMatcher> matchers = new LinkedList<IpMatcher>();
        for (String ip : ipMatchs) {
            IpMatcher matcher = IpMatcherFactory.build(ip);
            if (null != matcher) {
                matchers.add(matcher);
            }
        }
        return matchers;
    }
    
    public static void main(String[] args){
        System.out.println(IpMatcherFactory.build("12.7.0.1").isMatch("12.7.0.1"));
        System.out.println(IpMatcherFactory.build("12.7.0.1~12.7.0.5").isMatch("12.7.0.6"));
        System.out.println(IpMatcherFactory.build("12.7.0.1/29").isMatch("12.7.0.2"));
        System.out.println(IpMatcherFactory.build("0.0.0.0/0").isMatch("12.2.2.2"));
        System.out.println(IpMatcherFactory.build("255.255.255.0/24").isMatch("255.255.255.255"));
    }
}
