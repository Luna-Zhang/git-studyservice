package com.study.app.controller;

import com.study.app.service.IndexService;
import com.study.entity.UserUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA
 * Created By zhang
 * Date: 2018/8/9
 * Time: 12:04
 */
@RestController
@RequestMapping(value = "/app")
public class IndexController {
    @Autowired
    private IndexService indexService;


    @RequestMapping(value = "/index" , method = RequestMethod.GET)
    public String index (@RequestParam String userId) {
        UserUser userUser = indexService.findById(Integer.valueOf(userId));
        if (!StringUtils.isEmpty(userUser)) {
            return userUser.getMobileNumber();
        }
        return "to java";
    }


}
