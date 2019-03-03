package com.imooc.mapper;

import com.imooc.pojo.Users;
import com.imooc.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {
    /**
     *用户收到点赞
     * @param userId
     */
    void addReciveLikeCount(String userId);

    /**
     * 点赞取消
     * @param userId
     */
    void reduceReciveLikeCount(String userId);

    /**
     * 增加粉丝数量
     * @param userId
     */
    void addFansCount(String userId);

    /**
     * 减少粉丝数量
     * @param userId
     */
    void reduceFansCount(String userId);

    /**
     * 增加我关注的数量
     * @param userId
     */
    void addFollowerCount(String userId);

    /**
     * 减少我关注的数量
     * @param userId
     */
    void reduceFollowerCount(String userId);
}