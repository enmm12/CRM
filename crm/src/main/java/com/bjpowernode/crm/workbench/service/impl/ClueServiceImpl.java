package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.apache.ibatis.session.SqlSession;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {
    //线索相关的表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    //客户相关的表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);
    //联系人相关的表
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);
    //交易相关的表
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);


    public boolean save(Clue c) {

        boolean flag = true;

        int count = clueDao.save(c);

        if(count != 1){

            flag = false;

        }

        return flag;
    }

    public Clue detail(String id) {

        Clue c = clueDao.detail(id);

        return c;
    }

    public boolean unbund(String id) {

        boolean flag = true;

        int count = clueActivityRelationDao.unbund(id);

        if(count != 1){

            flag = false;
        }

        return flag;
    }

    public boolean bund(String clueId, String[] aids) {

        boolean flag = true;

        for(String aid:aids){

            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setActivityId(aid);
            car.setClueId(clueId);

            int count = clueActivityRelationDao.bund(car);
            if(count != 1){

                flag = false;
            }

        }

        return flag;
    }

    public boolean convert(String clueId, Tran t, String createBy) {

        boolean flag = true;

        String createTime = DateTimeUtil.getSysTime();

        //(1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue c = clueDao.getClueById(clueId);


        //(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        String company = c.getCompany();
        Customer cus = customerDao.getCustomerByName(company);
        //客户不存在，新建客户
        if(cus==null){

            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setWebsite(c.getWebsite());
            cus.setPhone(c.getPhone());
            cus.setOwner(c.getOwner());
            cus.setNextContactTime(c.getNextContactTime());
            cus.setName(company);
            cus.setDescription(c.getDescription());
            cus.setCreateTime(createTime);
            cus.setCreateBy(createBy);
            cus.setContactSummary(c.getContactSummary());
            cus.setAddress(c.getAddress());
            //添加客户
            int count1 = customerDao.save(cus);
            if(count1 != 1){

                flag = false;
            }
        }

        //(3) 通过线索对象提取联系人信息，保存联系人
        Contacts con = new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setSource(c.getSource());
        con.setOwner(c.getOwner());
        con.setNextContactTime(c.getNextContactTime());
        con.setMphone(c.getMphone());
        con.setJob(c.getJob());
        con.setFullname(c.getFullname());
        con.setEmail(c.getEmail());
        con.setCreateBy(createBy);
        con.setContactSummary(c.getContactSummary());
        con.setAppellation(c.getAppellation());
        con.setAddress(c.getAddress());
        con.setDescription(c.getDescription());
        con.setCustomerId(cus.getId());
        con.setCreateTime(createTime);

        int count2 = contactsDao.save(con);
        if(count2 != 1){

            flag = false;

        }

        //(4) 线索备注转换到客户备注以及联系人备注
        //通过clueId查找线索备注
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListById(clueId);
        //遍历取出每一条线索备注
        for(ClueRemark clueRemark:clueRemarkList){

            //保存客户备注信息
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(cus.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setNoteContent(clueRemark.getNoteContent());
            int count3 = customerRemarkDao.save(customerRemark);
            if(count3 != 1){

                flag = false;
            }

            //保存联系人备注信息
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(con.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setNoteContent(clueRemark.getNoteContent());
            int count4 = contactsRemarkDao.save(contactsRemark);
            if(count4 != 1){

                flag = false;
            }
        }

        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        //先根据clueId找出线索和市场活动的关系信息
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        //将这个信息保存到联系人和市场活动的关系表中
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){

            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(clueActivityRelation.getActivityId());
            contactsActivityRelation.setContactsId(con.getId());

            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5 != 1){
                flag = false;
            }
        }

        //(6) 如果有创建交易需求，创建一条交易
        if(t != null){

            t.setSource(c.getSource());
            t.setOwner(c.getOwner());
            t.setNextContactTime(c.getNextContactTime());
            t.setDescription(c.getDescription());
            t.setCustomerId(cus.getId());
            t.setContactSummary(c.getContactSummary());
            t.setContactsId(con.getId());
            //将这天交易保存到交易表中
            int count6 = tranDao.save(t);

            if(count6 != 1){

                flag = false;
            }

            //(7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(createTime);
            tranHistory.setExpectedDate(t.getExpectedDate());
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setMoney(t.getMoney());
            tranHistory.setStage(t.getStage());
            tranHistory.setTranId(t.getId());
            //添加交易历史
            int count7 = tranHistoryDao.save(tranHistory);
            if(count7 != 1){

                flag = false;

            }
        }

        //(8) 删除线索备注
        for(ClueRemark clueRemark:clueRemarkList){

            int count8 = clueRemarkDao.delete(clueRemark);
            if(count8 != 1){

                flag = false;

            }
        }

        //(9) 删除线索和市场活动的关系
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){

            int count9 = clueActivityRelationDao.delete(clueActivityRelation);
            if(count9 != 1){

                flag = false;

            }
        }

        //(10) 删除线索
        int count10 = clueDao.delete(clueId);
        if(count10 != 1){

            flag = false;

        }



        return flag;
    }

}
