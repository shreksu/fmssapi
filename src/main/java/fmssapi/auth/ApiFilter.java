package fmssapi.auth;

import fmssapi.api.service.AppClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 公共接口的数据检验
 * @author suyuanyang
 * @create 2017-12-25 下午1:15
 */
@Order(1)
@WebFilter(filterName = "apiFilter", urlPatterns = "/api/*")
public class ApiFilter implements Filter {

    @Autowired
    AppClientService appClientService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        //System.out.println("-----接口平台数据-----");
        String data = request.getParameter("data");// 公共平台分配的客户端ID
        String ak = request.getParameter("ak");// 请求签名
        if(appClientService.checkLegal(data,ak)) {
            filterChain.doFilter(request, response);
        }else{
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("签名失败!");
        }
    }

    @Override
    public void destroy() {

    }

}
