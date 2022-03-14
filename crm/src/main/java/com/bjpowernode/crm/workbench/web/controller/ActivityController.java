package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.*;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Service;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到市场活动控制器");

        String path = request.getServletPath();

        if("/workbench/activity/getUserList.do".equals(path)){

            getUserList(request,response);

        }else if("/workbench/activity/save.do".equals(path)){

            save(request,response);

        }else if("/workbench/activity/pageList.do".equals(path)){

            pageList(request,response);

        }else if("/workbench/activity/delete.do".equals(path)){

            delete(request,response);

        }else if("/workbench/activity/getUserListAndActivity.do".equals(path)){

            getUserListAndActivity(request,response);

        }else if("/workbench/activity/update.do".equals(path)){

            update(request,response);

        }else if("/workbench/activity/detail.do".equals(path)){

            detail(request,response);

        }else if("/workbench/activity/getRemarkListByAid.do".equals(path)){

            getRemarkListByAid(request,response);

        }else if("/workbench/activity/deleteRemarkById.do".equals(path)){

            deleteRemarkById(request,response);

        }else if("/workbench/activity/saveRemark.do".equals(path)){

            saveRemark(request,response);

        }else if("/workbench/activity/updateRemark.do".equals(path)){

            updateRemark(request,response);

        }


    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行修改备注信息的操作");

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "1";

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditFlag(editFlag);
        ar.setEditTime(editTime);
        ar.setEditBy(editBy);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.updateRemark(ar);

        Map<String ,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("ar",ar);
        PrintJson.printJsonObj(response,map);



    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行添加备注操作");

        String activityId = request.getParameter("id");
        String id = UUIDUtil.getUUID();
        String noteContent = request.getParameter("noteContent");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "0";

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setActivityId(activityId);
        ar.setCreateBy(createBy);
        ar.setCreateTime(createTime);
        ar.setEditFlag(editFlag);
        ar.setNoteContent(noteContent);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.saveRemark(ar);

        Map<String ,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("ar",ar);
        PrintJson.printJsonObj(response,map);

    }

    private void deleteRemarkById(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据市场活动的备注信息的id来执行删除备注操作");

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.deleteRemarkById(id);

        PrintJson.printJsonFlag(response,flag);

    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据市场活动id查询备注信息列表操作");

        String aid = request.getParameter("aid");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<ActivityRemark> arList = as.getRemarkListByAid(aid);

        PrintJson.printJsonObj(response,arList);

    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到跳转到详细信息页操作");

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        Activity a = as.detail(id);

        request.setAttribute("a",a);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);

    }

    private void update(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("对市场活动信息进行修改操作");

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        Activity activity = new Activity();
        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setEditTime(editTime);
        activity.setEditBy(editBy);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.update(activity);

        PrintJson.printJsonFlag(response,flag);


    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入查询用户列表和当前市场活动信息的操作");

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //前端需要的是用户列表和Activity，所以可以使用vo和map来存储
        //这里使用map来存储，因为使用的次数不多
        Map<String,Object> map = as.getUserListAndActivity(id);

        PrintJson.printJsonObj(response,map);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行市场活动信息的删除操作");

        //获取要删除的信息
        String[] ids = request.getParameterValues("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.delete(ids);
        PrintJson.printJsonFlag(response,flag);


    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行市场活动分页查询操作");

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        int pageNo = Integer.valueOf(pageNoStr);
        //每页展示的记录条数
        int pageSize = Integer.valueOf(pageSizeStr);
        //分页查询略过的条数
        int skipCount = (pageNo-1)*pageSize;

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("pageSize",pageSize);
        map.put("skipCount",skipCount);
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //返回结果是一个vo类，paginationVo
        PaginationVo<Activity> vo =  as.pageList(map);
        PrintJson.printJsonObj(response,vo);



    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行市场活动添加操作");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getCreateBy();

        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);
        //动态创建代理类对象
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.save(a);
        //以json对象返回处理的结果
        PrintJson.printJsonFlag(response,flag);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("获取用户信息列表");
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = userService.getUserList();
        PrintJson.printJsonObj(response,userList);

    }


}
