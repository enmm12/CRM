package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;

public interface ClueDao {

    int save(Clue c);

    Clue detail(String id);


    Clue getClueById(String clueId);

    int delete(String clueId);
}
