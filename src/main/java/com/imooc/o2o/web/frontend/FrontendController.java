package com.imooc.o2o.web.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/frontend")
public class FrontendController {
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    private String index(){
        return "frontend/index";
    }
    @RequestMapping(value = "/shoplist",method = RequestMethod.GET)
    private String shopList(){
        return "frontend/shoplist";
    }
    @RequestMapping(value = "/shopdetail",method = RequestMethod.GET)
    private String shopDetail(){
        return "frontend/shopdetail";
    }
    @RequestMapping(value = "/productdetail",method = RequestMethod.GET)
    private String productDetail(){
        return "frontend/productdetail";
    }
    @RequestMapping(value = "/awardlist",method = RequestMethod.GET)
    private String awardList(){
        return "frontend/awardlist";
    }
    @RequestMapping(value = "/pointrecord",method = RequestMethod.GET)
    private String pointRecord(){
        return "frontend/pointrecord";
    }
    @RequestMapping(value = "/myawarddetail",method = RequestMethod.GET)
    private String myAwardDetail(){
        return "frontend/myawarddetail";
    }
    @RequestMapping(value = "/myrecord",method = RequestMethod.GET)
    private String myRecord(){
        return "frontend/myrecord";
    }
    @RequestMapping(value = "/mypoint",method = RequestMethod.GET)
    private String myPoint(){
        return "frontend/mypoint";
    }
    @RequestMapping(value = "/exit",method = RequestMethod.GET)
    private String exit(HttpServletRequest request){
        request.getSession().setAttribute("user",null);
        return "/local/login";
    }
    @RequestMapping(value = "/mymessage",method = RequestMethod.GET)
    private String myMessage(){
        return "frontend/mymessage";
    }
}
