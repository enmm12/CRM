package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    public boolean save(Tran t, String customerName) {

        boolean flag = true;

        //判断客户存不存在，不存在创建新客户
        Customer cus = customerDao.getCustomerByName(customerName);

        if(cus==null){

            //创建新客户
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setContactSummary(t.getContactSummary());
            cus.setCreateBy(t.getCreateBy());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setDescription(t.getDescription());
            cus.setNextContactTime(t.getNextContactTime());
            cus.setOwner(t.getOwner());
            cus.setName(customerName);

            //添加客户
            int count1 = customerDao.save(cus);
            if (count1 != 1){

                flag = false;

            }
        }

        //保存交易
        t.setCustomerId(cus.getId());
        int count2 = tranDao.save(t);
        if(count2 != 1){

            flag = false;

        }

        //保存交易历史
        TranHistory th = new TranHistory();
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setId(UUIDUtil.getUUID());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(t.getCreateTime());
        th.setCreateBy(t.getCreateBy());

        int count3 = tranHistoryDao.save(th);
        if(count3 != 1){

            flag = false;

        }

        return flag;
    }

    public Tran detail(String id) {

        Tran t = tranDao.detail(id);

        return t;
    }

    public List<TranHistory> getHistoryListByTranId(String tranId) {

        List<TranHistory> thList = tranHistoryDao.getHistoryListByTranId(tranId);

        return thList;
    }

    public boolean changeStage(Tran t) {

        boolean flag = true;

        //改变一条交易
        int count1 = tranDao.changeStage(t);
        if(count1 != 1){

            flag = false;

        }

        //创建一条交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(t.getEditTime());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setStage(t.getStage());
        th.setTranId(t.getId());

        //添加一条交易历史
        int count2 = tranHistoryDao.save(th);
        if(count2 != 1){

            flag = false;

        }

        return flag;
    }

    public Map<String, Object> getCharts() {

        //获取统计条数
        int total = tranDao.getTotal();

        //获取各个阶段的交易
        List<Map<String,Object>> dataList =  tranDao.getCharts();

        Map<String,Object> map = new HashMap<String, Object>();

        map.put("total",total);
        map.put("dataList",dataList);

        return map;
    }
}
