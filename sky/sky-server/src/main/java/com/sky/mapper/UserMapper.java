package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid获取用户
     * @param openid
     * @return
     */
    @Select("select * from  user where openid=#{openid}")
    User getByOpenId(String openid);

    /**
     * 注册新用户
     * @param user
     */
    void insertUser(User user);

    /**
     * 通过用户id获取用户信息
     * @param userId
     * @return
     */
    @Select("select * from user where id=#{userId}")
    User getById(Long userId);

    /**
     * 今日新增用户
     * @param map
     * @return
     */
    Integer countByMap(@Param("map") Map map);
}
