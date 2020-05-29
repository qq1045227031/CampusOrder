package com.imooc.o2o.interceptor.shopadmin;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.imooc.o2o.entity.PersonInfo;
/**
 * 店家管理系统登录验证拦截器
 * @author acer
 *
 */
public class ShopLoginInterceptor extends HandlerInterceptorAdapter {
    /**
     * 主要做事前拦截，即用户操作发生前，改写perhandle里的逻辑进行拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        //从session中取出用户信息来
        Object userObj = request.getSession().getAttribute("user");
        if (userObj != null) {
            //若用户信息不为空，则将session里的用户信息转换成personInfo实体类对象
            PersonInfo user = (PersonInfo) userObj;
            //做空值判断，确保userid不为空并且该账号的可用状态为1，并且用户类型为店家
            if (user != null && user.getUserId() != null
                    && user.getUserId() > 0 && user.getEnableStatus() == 1)
                //若通过验证则返回true，拦截器返回true之后，用户接下来的操作得以正常进行
                return true;
        }
        //r若不满足登录验证，则直接跳转到账号登录页面
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<script>");
        out.println("window.open ('" + request.getContextPath()
                + "/local/login?usertype=2','_self')");
        out.println("</script>");
        out.println("</html>");
        return false;
    }
}
