package com.imooc.o2o.util;

import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestUtil {
    //将request中取键位key并转化为整形，失败则返回负一
    public static int getInt(HttpServletRequest request, String key){
        try{
            return Integer.decode(request.getParameter(key));
        }catch (Exception e ){
            return -1;
        }
    }

    public static Long getLong(HttpServletRequest request, String key){
        try{
            return Long.valueOf(request.getParameter(key));
        }catch (Exception e ){
            return -1L;
        }
    }
    public static double getDouble(HttpServletRequest request, String key){
        try{
            return Double.valueOf(request.getParameter(key));
        }catch (Exception e ){
            return -1d;
        }
    }
    public static boolean getBoolean(HttpServletRequest request, String key){
        try{
            return Boolean.valueOf(request.getParameter(key));
        }catch (Exception e ){
            return false;
        }
    }
    public static String getString(HttpServletRequest request,String key){
        try{
            String result = request.getParameter(key);
            if (request!=null){
                result = result.trim();//去头尾空格
            }else{
                result =  null;
            }
            if ("".equals(result)){
                result = null;
            }
            return result;
        }catch (Exception e){
            return null;
        }

    }
}
