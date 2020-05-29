package com.imooc.o2o.web.local;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.PersonExecution;
import com.imooc.o2o.dto.PersonInfoExecution;
import com.imooc.o2o.enums.PersonInfoStateEnum;
import com.imooc.o2o.service.PersonInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.o2o.dto.LocalAuthExecution;
import com.imooc.o2o.entity.LocalAuth;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.enums.LocalAuthStateEnum;
import com.imooc.o2o.service.LocalAuthService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping(value="local",method={RequestMethod.GET,RequestMethod.POST})
public class LocalAuthController {
    @Autowired
    private LocalAuthService localAuthService;
    @Autowired
    private PersonInfoService personInfoService;
    /**
     * 将用户信息与平台账号绑定
     * @param request
     * @return
     */
    @RequestMapping(value="/bindlocalauth",method=RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> bindLocalAuth(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String,Object>();
        //验证码校验
        if(!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success", false);
            modelMap.put("errmsg", "输入了错误的验证码");
            return modelMap;
        }
        //获取输入的账号
        String userName=HttpServletRequestUtil.getString(request, "userName");
        //获取输入的密码
        String password=HttpServletRequestUtil.getString(request, "password");
        //从session中获取当前用户信息，用户一旦通过微信登录之后，便能获取到用户的信息
        PersonInfo user=(PersonInfo)request.getSession().getAttribute("user");
        //非空判断，要求账号密码以及当前的用户session非空
        if (userName!=null&&password!=null&&user!=null&&user.getUserId()!=null){
            //创建LocalAuth对象并赋值
            LocalAuth localAuth=new LocalAuth();
            localAuth.setUsername(userName);
            localAuth.setPassword(password);
            localAuth.setPersonInfo(user);
            //绑定账号
            LocalAuthExecution le=localAuthService.bindLocalAuth(localAuth);
            if(le.getState()==LocalAuthStateEnum.SUCCESS.getState()){
                modelMap.put("success", true);

            }else{
                modelMap.put("success", false);
                modelMap.put("errmsg", le.getStateInfo());
            }

        }else{
            modelMap.put("success", false);
            modelMap.put("errmsg", "用户名和密码都不能为空");
        }
        return modelMap;
    }
    @RequestMapping(value = "/toregister",method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> register(HttpServletRequest request) throws JsonProcessingException {
        Map<String,Object> modelMap = new HashMap<>();
        if(!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success", false);
            modelMap.put("errmsg", "输入了错误的验证码");
            return modelMap;
        }
        String root = HttpServletRequestUtil.getString(request,"root");
        String person = HttpServletRequestUtil.getString(request,"person");
        ObjectMapper mapper = new ObjectMapper();
        LocalAuth localAuth = mapper.readValue(root,LocalAuth.class);
        PersonInfo personInfo = mapper.readValue(person,PersonInfo.class);
        personInfo.setEnableStatus(1);
        personInfo.setCreateTime(new Date());
        personInfo.setLastEditTime(new Date());
        localAuth.setCreateTime(new Date());
        localAuth.setLastEditTime(new Date());
        PersonInfoExecution personInfoExecution =  personInfoService.addPersonInfo(personInfo);
        localAuth.setPersonInfo(personInfoExecution.getPersonInfo());
        LocalAuthExecution localAuthExecution = localAuthService.addLocalAuth(localAuth);
        if (localAuthExecution.getState()==LocalAuthStateEnum.SUCCESS.getState()&&personInfoExecution.getState()== PersonInfoStateEnum.SUCCESS.getState()) {
            modelMap.put("success",true);
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","创建失败");
        }
        return modelMap;
    }
    /**
     * 修改密码
     * @param request
     * @return
     */
    @RequestMapping(value="/changelocalpwd",method=RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> changeLocalPwd(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String,Object>();
        //验证码校验
        if(!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success", false);
            modelMap.put("errmsg", "输入了错误的验证码");
            return modelMap;
        }
        //获取账号
        String userName=HttpServletRequestUtil.getString(request, "userName");
        //获取密码
        String password=HttpServletRequestUtil.getString(request, "password");
        //获取新密码
        String newPassword=HttpServletRequestUtil.getString(request, "newPassword");
        //从session中获取当前用户信息，用户一旦通过微信登录之后，便能获取到用户的信息
        PersonInfo user=(PersonInfo)request.getSession().getAttribute("user");
        //非空判断，要求账号新旧密码以及当前的用户session非空，且新旧密码不相同
        if(userName!=null&&password!=null&&newPassword!=null&&user!=null&&user.getUserId()!=null
                &&!password.equals(newPassword)){
            try {
                //查看原先账号，看看与输入的账号是否一致，不一致则认为是非法操作
                LocalAuth localAuth=localAuthService.getLocalAuthByUserId(user.getUserId());
                if(localAuth==null||!localAuth.getUsername().equals(userName)){
                    //不一致则直接退出
                    modelMap.put("success", false);
                    modelMap.put("errmsg", "输入的账号非本次登录的账号");
                    return modelMap;
                }
                //修改平台账号的用户密码
                LocalAuthExecution le=localAuthService.modifyLocalAuth(user.getUserId(),
                        userName, password, newPassword);
                if(le.getState()==LocalAuthStateEnum.SUCCESS.getState()){
                    modelMap.put("success", true);

                }else{
                    modelMap.put("success", false);
                    modelMap.put("errmsg", le.getStateInfo());
                }
            } catch (Exception e) {
                modelMap.put("success", false);
                modelMap.put("errmsg", e.toString());
                return modelMap;
            }

        }else{
            modelMap.put("success", false);
            modelMap.put("errmsg", "请输入密码");
        }
        return modelMap;
    }

    /**
     * 登陆功能
     * @param request
     * @return
     */
    @RequestMapping(value="/logincheck",method=RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> logincheck(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String,Object>();
        //获取是否需要进行验证码校验的标识符，若连续输入错误三次前台会传入true
        boolean needVerify=HttpServletRequestUtil.getBoolean(request, "needVerify");
        if(needVerify&&!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success", false);
            modelMap.put("errmsg", "输入了错误的验证码");
            return modelMap;
        }
        //获取输入的账号
        String userName=HttpServletRequestUtil.getString(request, "userName");
        //获取输入的密码
        String password=HttpServletRequestUtil.getString(request, "password");
        //非空校验
        if(userName!=null &&password!=null){
            //传入账号和密码去获取平台账号信息,若有返回帐号
            LocalAuth localAuth=localAuthService.getLocalAuthByUserNameAndPwd(userName, password);
            if(localAuth!=null){
                //若能取到账号信息则登录成功
                modelMap.put("success", true);
                //同时在session里设置用户信息
                request.getSession().setAttribute("user", localAuth.getPersonInfo());
                //返回用户类型,前台根据其类型跳转到对应界面
                modelMap.put("usertype",localAuth.getPersonInfo().getUserType());
            }else{
                modelMap.put("success", false);
                modelMap.put("errmsg", "用户名或密码错误");
            }
        }else{
            modelMap.put("success", false);
            modelMap.put("errmsg", "用户名和密码均不能为空");
        }
        return modelMap;
    }
    /**
     * 当用户点击登出按钮时注销session
     * @param request
     * @return
     */
    @RequestMapping(value="logout",method=RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> logout(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String,Object>();
        //将用户session置为空
        request.getSession().setAttribute("user", null);
        modelMap.put("success", true);
        return modelMap;
    }
}
