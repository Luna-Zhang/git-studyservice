package com.study.user.dao;

import com.study.entity.UserUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created with IntelliJ IDEA
 * Created By zhang
 * Date: 2018/8/13
 * Time: 10:02
 */
@Mapper
public interface UserUserDao {

    @Select("select * from user_user where id = #{id} ")
    public UserUser findById(@Param("id") Integer id);

}
