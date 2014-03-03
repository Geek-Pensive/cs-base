package com.yy.cs.base.nyy;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class NYYFilter implements Filter {
    
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain) throws IOException,
            ServletException {
        // TODO NYY校验，记录ip之类。也可放在拦截器中做，最好有拦截器中做，对NYY的每个安全场景实现一个拦截器，开发者按需配置
        chain.doFilter(new NYYHttpRequestWrapper((HttpServletRequest) arg0), arg1);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
        
    }

}
