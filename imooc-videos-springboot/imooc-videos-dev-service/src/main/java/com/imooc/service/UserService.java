package com.imooc.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;
import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * @Author: mate_J
 * @Date: 2018/12/12 15:51
 * @Version 1.0
 */
public interface UserService {
    /**
     * 判断用户是否存在
     * @param username
     * @return
     */
    Boolean queryUsernameIsExist(String username);

    /**
     * 用户登录，校验用户信息
     * @param name
     * @param password
     * @return
     */
    Users queryUserForLogin(String name,String password);

    /**
     * 保存用户到数据库中（用户注册）
     * @param user
     */
    void save(Users user);

    /**
     * 更新用户信息
     * @param user
     */
    void updateUserInfo(Users user);

    /**
     * 查询用户
     * @param userId
     * @return
     */
    Users query(String userId);

    /**
     * 查询用户是否喜欢当前视频
     * @param userId
     * @param videoId
     * @return
     */
    Boolean isUserLikeVideo(String userId,String videoId);

    /**
     * 保存粉丝，用户之间的关系
     * @param userId
     * @param fanId
     */
    void saveUserFanRelation(String userId,String fanId);

    void deleteUserFanRelation(String userId,String fanId);

    /**
     * 查询是否关注
     * @param userId
     * @param fanId
     * @return
     */
    Boolean queryIfFollow(String userId,String fanId);

    /**
     * 举报用户
     * @param usersReport
     */
    void reportUser(UsersReport usersReport);
}
