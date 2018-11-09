package net.ys.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@javax.servlet.annotation.WebFilter(urlPatterns = "/web/*")
public final class WebLoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();
        Object admin = request.getSession().getAttribute("admin");
        if (admin == null) {
            if (!uri.endsWith("web/admin/login")) {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.sendRedirect(request.getContextPath() + "/web/admin/login");
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}