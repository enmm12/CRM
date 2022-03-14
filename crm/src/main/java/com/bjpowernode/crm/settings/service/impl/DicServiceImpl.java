package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.settings.dao.DicTypeDao;
import com.bjpowernode.crm.settings.dao.DicValueDao;
import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {

    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);


    public Map<String, List<DicValue>> getAll() {

        Map<String,List<DicValue>> map = new HashMap<String, List<DicValue>>();

        //获取所有的DicType的值
        List<DicType> dtList = dicTypeDao.getTypeList();


        //获取每一个DicType对应的DicValue，存到list中
        for(DicType dt:dtList){

            String code = dt.getCode();
            List<DicValue> dvList = dicValueDao.getValueList(code);

            map.put(code,dvList);
        }

        //将这两个存到map集合中并返回
        return map;
    }
}
