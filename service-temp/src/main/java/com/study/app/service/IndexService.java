package com.study.app.service;

import com.study.entity.UserUser;
import com.study.user.dao.UserUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA
 * Created By zhang
 * Date: 2018/8/9
 * Time: 14:00
 */
@Service
public class IndexService {
    private Logger logger = LoggerFactory.getLogger(IndexService.class);

    @Autowired
    private UserUserDao userUserDao;

    public UserUser findById (Integer id) {
        return userUserDao.findById(id);
    }


}
