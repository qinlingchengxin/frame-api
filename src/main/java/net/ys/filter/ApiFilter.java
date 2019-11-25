package net.ys.filter;

import net.ys.cache.BaseCache;
import net.ys.component.AppContextUtil;
import net.ys.component.SysConfig;
import net.ys.constant.GenResult;
import net.ys.constant.X;
import net.ys.util.LogUtil;
import net.ys.util.Tools;
import net.ys.util.WebUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@WebFilter(urlPatterns = "/api/*")
public final class ApiFilter implements Filter {

    String pk = "4c4e6c7120ad4748";

    BaseCache baseCache;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        baseCache = AppContextUtil.getBean("baseCache", BaseCache.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        request = new XssRequestWrapper(request);

        boolean flag = validClient(request);

        if (flag) {
            flag = validParams(request);
            if (!flag) {
                response.setCharacterEncoding(X.Code.U);
                response.setContentType("application/json; charset=" + X.Code.U);
                response.getWriter().write(GenResult.REQUEST_INVALID.toJson());
            } else {
                logParam(request);
                chain.doFilter(request, response);
            }
        } else {
            response.setCharacterEncoding(X.Code.U);
            response.setContentType("application/json; charset=" + X.Code.U);
            response.getWriter().write(GenResult.REQUEST_IP_INVALID.toJson());
        }
    }

    private boolean validClient(ServletRequest request) {//ip校验
        String clientIP = WebUtil.getClientIP((HttpServletRequest) request);
        if (SysConfig.enableWhiteList == 0 || SysConfig.backupServerIp.contains(clientIP)) {
            return true;
        } else {
            LogUtil.debug("access client ip:" + clientIP);
        }
        return false;
    }

    boolean validParams(ServletRequest request) {

        if (!SysConfig.validApiParameter) {
            return true;
        }

        String k = request.getParameter("k");
        String t = request.getParameter("t");
        String sign = request.getParameter("sign");

        if (!Tools.isNotEmpty(k, t, sign)) {
            return false;
        }
        String md = Tools.genMD5(pk + k + t + pk);
        if (!md.equals(sign)) {
            return false;
        }
        return baseCache.getAccessCount(sign, SysConfig.apiAccessTimeLimit) == 1;
    }

    @Override
    public void destroy() {
    }

    private void logParam(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();
        Enumeration<String> names = request.getParameterNames();
        String name;
        String newLine = "\r\n";
        StringBuffer sb = new StringBuffer("request uri-->" + uri + newLine);
        while (names.hasMoreElements()) {
            name = names.nextElement();
            sb.append(name + "->" + request.getParameter(name) + newLine);
        }
        LogUtil.info(sb.toString());
    }
}