package com.user.Config;

import com.user.Utils.MyInfoLog;
import com.user.Utils.TokenUtil;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class WebInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        StringBuffer requestURL = request.getRequestURL();
        String URL = requestURL.toString();
        int port = request.getServerPort();
        if(port == 80) {
            MyInfoLog.LogOut("请求的地址===端口错误  " + URL, false);
            // 拒绝访问
            return setStatusCode(response,403);
        }
        // 没有携带请求头的或者请求头错误的不予访问
        String WebAnalyze = request.getHeader("Wenlyze");
        if (WebAnalyze == null || !WebAnalyze.equals("db-rabbit")) {
            MyInfoLog.LogOut("请求的地址===请求头错误  " + URL, false);
            // 拒绝访问
            return setStatusCode(response,403);
        }
        if(port == 81) {
                TokenUtil tokenUtil = new TokenUtil();
                 // post、put、delete请求必须携带token才能继续放行
                String token = request.getHeader("Author");

                // 如果token不存在，不予放行
                boolean exist = tokenUtil.verifyToken(token);
                if(exist) {
                    return true;
                }
                return setStatusCode(response,401);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

    // 设置403状态码
    public boolean setStatusCode(HttpServletResponse response, int statusCode) {
        try {
            response.sendError(statusCode, "The server rejects the request");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
