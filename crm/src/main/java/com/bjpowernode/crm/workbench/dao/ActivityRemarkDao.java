package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityRemarkDao {


    int getCountByAid(String[] ids);

    int deleteByAid(String[] ids);

    List<ActivityRemark> getRemarkListByAid(String aid);

    int deleteRemarkById(String id);

    int saveRemark(ActivityRemark ar);

    int updateRemark(ActivityRemark ar);
}
