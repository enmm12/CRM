package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {

//        System.out.println("系统初始化的application启动了");

        System.out.println("服务器缓存处理数据字典开始");


        ServletContext application = sce.getServletContext();

        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());

        //获取所有的DicValue,存到List中
        Map<String, List<DicValue>> map = ds.getAll();

        Set<String> set = map.keySet();

        for(String key:set){

            application.setAttribute(key,map.get(key));

        }

        System.out.println("服务器缓存处理数据字典结束");

        //------------------------------------------------------

        //数据字典处理完毕后，处理Stage2Possibility.properties文件
        //解析properties文件
        ResourceBundle bundle = ResourceBundle.getBundle("Stage2Possibility");

        Map<String,String> pMap = new HashMap<String, String>();

        Enumeration<String> e = bundle.getKeys();

        while (e.hasMoreElements()){

            //阶段
            String key = e.nextElement();
            //可能性
            String value = bundle.getString(key);

            pMap.put(key,value);

        }

        //将PMap保存到服务器缓存中
        application.setAttribute("pMap",pMap);

    }
}
