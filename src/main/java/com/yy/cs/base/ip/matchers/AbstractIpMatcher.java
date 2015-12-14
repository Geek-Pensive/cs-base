package com.yy.cs.base.ip.matchers;

public abstract class AbstractIpMatcher implements IpMatcher {

    protected String ipMatch;

    @Override
    public IpMatcher build(String ipMatch) {
        AbstractIpMatcher m = (AbstractIpMatcher) newInstance();
        m.setIpMatch(ipMatch);
        return m.doBuild(ipMatch);
    }

    
    @Override
    public String getIpMatch() {
        return this.ipMatch;
    }

    protected void setIpMatch(String ipMatch) {
        this.ipMatch = ipMatch;
    }
    
    protected abstract IpMatcher doBuild(String ipMatch);

    protected abstract IpMatcher newInstance();
}
