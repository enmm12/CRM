package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到用户控制器");

        String path = request.getServletPath();

        if("/settings/user/login.do".equals(path)){

            login(request,response);

        }else if("/settings/user/xx.do".equals(path)){

            //xxx(request,response);

        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到验证登录操作");
        //获取用户名、密码、IP地址
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将密码转换为MD5码
        loginPwd = MD5Util.getMD5(loginPwd);
        String ip = request.getRemoteAddr();
        System.out.println("ip-------->" + ip);

        //创建service对象，使用动态代理对象
        UserService us = (UserService)ServiceFactory.getService(new UserServiceImpl());

        try{

            User user = us.login(loginAct,loginPwd,ip);
            request.getSession().setAttribute("user",user);
            //登录成功  向jsp传递一个{success ：true}
            PrintJson.printJsonFlag(response,true);

        }catch (Exception e){
            e.printStackTrace();
            //登录失败  向jsp传递一个{success ：true, msg : "错误类型"}
            //这两个信息使用map集合存储，再转为json
            String msg = e.getMessage();
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("success",false);
            map.put("msg",msg);
            PrintJson.printJsonObj(response,map);

        }





    }
}
