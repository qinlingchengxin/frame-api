package net.ys.filter;

import net.ys.cache.BaseCache;
import net.ys.component.AppContextUtil;
import net.ys.component.SysConfig;
import net.ys.constant.GenResult;
import net.ys.constant.X;
import net.ys.utils.Tools;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

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
        List<String> params = new ArrayList<String>();

        Enumeration<String> names = request.getParameterNames();
        String name;
        int count = 0;
        while (names.hasMoreElements()) {
            name = names.nextElement();
            if (!"sign".equals(name)) {
                params.add(name);
            }
            if ("t".equals(name) || "sign".equals(name)) {//必须存在
                count++;
            }
        }
        if (count != 2) {
            return false;
        }
        Collections.sort(params);

        String src = "";
        String value;
        for (int i = 0; i < params.size(); i++) {

            value = request.getParameter(params.get(i));
            if (i % 2 == 0) {
                src += value;
            } else {
                src = value + src;
            }
        }

        String md = Tools.genMD5(pk + src + pk);
        String sign = request.getParameter("sign");

        if (!md.equals(sign)) {
            return false;
        }

        return baseCache.getAccessCount(sign, SysConfig.apiAccessTimeLimit * X.Time.MINUTE_SECOND) == 1;
    }

    @Override
    public void destroy() {
    }
}