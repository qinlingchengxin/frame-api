package net.ys.filter;

import net.ys.cache.BaseCache;
import net.ys.component.AppContextUtil;
import net.ys.component.SysConfig;
import net.ys.constant.GenResult;
import net.ys.constant.X;
import net.ys.util.Tools;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(urlPatterns = "/api/*")
public final class ApiFilter implements Filter {

    String pk = "4c4e6c7120ad4748";

    BaseCache baseCache;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        baseCache = AppContextUtil.getBean("baseCache", BaseCache.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        boolean flag = validParams(request);
        if (!flag) {
            response.setCharacterEncoding(X.Code.U);
            response.setContentType("application/json; charset=" + X.Code.U);
            response.getWriter().write(GenResult.REQUEST_INVALID.toJson());
        } else {
            chain.doFilter(request, response);
        }
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
}