package net.ys.filter;

import net.ys.utils.LogUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@javax.servlet.annotation.WebFilter(urlPatterns = "/api/*")
public final class ApiFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();
        Enumeration<String> names = request.getParameterNames();
        String name;
        String newLine = "\r\n";
        StringBuffer sb = new StringBuffer("uri-->" + uri + newLine);
        while (names.hasMoreElements()) {
            name = names.nextElement();
            sb.append(name + "->" + request.getParameter(name) + newLine);
        }
        LogUtil.debug(sb.toString());

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}