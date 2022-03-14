package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {

    ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);


    public boolean save(Activity a) {

        boolean flag = true;

        int res = activityDao.save(a);

        if(res != 1){
            flag = false;
        }

        return flag;
    }

    public PaginationVo<Activity> pageList(Map<String, Object> map) {

        //获取总记录条数total
        int total = activityDao.getTotalByCondition(map);


        //获取查询的市场活动信息dataList
        List<Activity> dataList = activityDao.getDataListByCondition(map);


        //将total和dataList封装到vo中
        PaginationVo<Activity> vo = new PaginationVo<Activity>();
        vo.setTotal(total);
        vo.setDataList(dataList);

        //将vo返回
        return vo;

    }

    public boolean delete(String[] ids) {

        boolean flag = true;

        //查询市场活动备注表要删除的信息条数
        int count1 = activityRemarkDao.getCountByAid(ids);

        //真实删除的信息条数
        int count2 = activityRemarkDao.deleteByAid(ids);

        if(count1 != count2){
            flag = false;
        }

        //删除市场活动信息
        int count3 = activityDao.delete(ids);

        if(count3 != ids.length){
            flag = false;
        }

        return flag;
    }

    public Map<String, Object> getUserListAndActivity(String id) {

        //获取用户信息列表uList
        List<User> uList = userDao.getUserList();

        //获取Activity
        Activity a = activityDao.getById(id);


        //将uList和Activity存入到map集合中
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("uList",uList);
        map.put("a",a);

        //返回map集合
        return map;
    }

    public boolean update(Activity activity) {

        boolean flag = true;

        //修改市场活动数据
        int res = activityDao.update(activity);

        if(res != 1){
            flag = false;
        }

        return flag;
    }

    public Activity detail(String id) {

        Activity a = activityDao.detail(id);


        return a;
    }

    public List<ActivityRemark> getRemarkListByAid(String aid) {

        List<ActivityRemark> arList = activityRemarkDao.getRemarkListByAid(aid);


        return arList;
    }

    public boolean deleteRemarkById(String id) {

        boolean flag = true;

        int count = activityRemarkDao.deleteRemarkById(id);

        if(count != 1){
            flag = false;
        }

        return flag;
    }

    public boolean saveRemark(ActivityRemark ar) {

        boolean flag = true;

        int count = activityRemarkDao.saveRemark(ar);

        if(count != 1){

            flag = false;
        }

        return flag;
    }

    public boolean updateRemark(ActivityRemark ar) {

        boolean flag = true;

        int count = activityRemarkDao.updateRemark(ar);

        if(count != 1){

            flag = false;
        }

        return flag;
    }

    public List<Activity> getActivityListByClueId(String clueId) {

        List<Activity> aList = activityDao.getActivityListByClueId(clueId);


        return aList;
    }

    public List<Activity> getActivityListByAnameAndNotByClueId(Map<String, String> map) {

        List<Activity> aList = activityDao.getActivityListByAnameAndNotByClueId(map);

        return aList;
    }

    public List<Activity> getActivityListByName(String aname) {

        List<Activity> aList = activityDao.getActivityListByName(aname);

        return aList;
    }
}
