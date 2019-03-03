package com.imooc.interceptor;

import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @Author: mate_J
 * @Date: 2018/12/19 20:29
 * @Version 1.0
 */
public class MyInceptor implements HandlerInterceptor {
    @Autowired
    protected RedisOperator redis;

    public final static String USER_REDIS_SESSION="user-redis-session";


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String userId = httpServletRequest.getHeader("userId");
        String userToken = httpServletRequest.getHeader("userToken");

        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(userToken)){
            System.out.println("请登陆。。。");
            returnErrorResponse(httpServletResponse,IMoocJSONResult.errorTokenMsg("请登陆。。。"));
            return false;
        }else {
            String uniqueToken = redis.get(USER_REDIS_SESSION + ":" + userId);
            if(StringUtils.isEmpty(uniqueToken)){
                System.out.println("身份信息过期");

                returnErrorResponse(httpServletResponse,IMoocJSONResult.errorTokenMsg("身份信息过期"));
                return false;
            }else {
                if(!uniqueToken.equals(userToken)){
                    System.out.println("账号被挤出");
                    returnErrorResponse(httpServletResponse,IMoocJSONResult.errorTokenMsg("账号被挤出"));
                    return false;
                }
            }
        }

        /**
         * false : 拦截
         * true ： 放行
         */
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response, IMoocJSONResult result)
            throws IOException, UnsupportedEncodingException {
        OutputStream out=null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally{
            if(out!=null){
                out.close();
            }
        }
    }

    /**
     * 请求controller之后，视图渲染之前
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
