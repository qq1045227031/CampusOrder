package com.imooc.o2o.web.local;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/local")
public class localController {
    @RequestMapping(value = "/accountbind",method = RequestMethod.GET)
    private String accountbind(){
        return "local/accountbind";
    }
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    private String login(){
        return "local/login";
    }
    @RequestMapping(value = "/changepsw",method = RequestMethod.GET)
    private String changepsw(){
        return "local/changepsw";
    }
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    private String register(){
        return "local/register";
    }
}
