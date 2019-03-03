package com.imooc.service.impl;

import com.imooc.mapper.UsersFansMapper;
import com.imooc.mapper.UsersLikeVideosMapper;
import com.imooc.mapper.UsersMapper;
import com.imooc.mapper.UsersReportMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.UsersFans;
import com.imooc.pojo.UsersLikeVideos;
import com.imooc.pojo.UsersReport;
import com.imooc.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.Date;
import java.util.List;

/**
 * @Author: mate_J
 * @Date: 2018/12/12 16:05
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;
    @Autowired
    private UsersFansMapper usersFansMapper;
    @Autowired
    private UsersReportMapper usersReportMapper;

    @Transactional(propagation = Propagation.SUPPORTS,rollbackFor = Exception.class)
    @Override
    public Boolean queryUsernameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);

        Users userResult = usersMapper.selectOne(user);

        return userResult != null;
    }

    @Transactional(propagation = Propagation.SUPPORTS,rollbackFor = Exception.class)
    @Override
    public Users queryUserForLogin(String name, String password) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username",name);
        criteria.andEqualTo("password",password);

        Users result = usersMapper.selectOneByExample(userExample);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    @Override
    public void save(Users user) {
        String userId = sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }

    @Override
    public void updateUserInfo(Users user) {
        usersMapper.updateByPrimaryKeySelective(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS,rollbackFor = Exception.class)
    @Override
    public Users query(String userId) {

        return usersMapper.selectByPrimaryKey(userId);
    }

    @Override
    public Boolean isUserLikeVideo(String userId, String videoId) {
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)){
            return false;
        }
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);

        List<UsersLikeVideos> usersLikeVideosList = usersLikeVideosMapper.select(usersLikeVideos);
        if(usersLikeVideosList != null && usersLikeVideosList.size() > 0){
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {
        //先保存用户、粉丝之间的关系
        UsersFans usersFans = new UsersFans();
        usersFans.setId(sid.nextShort());
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        usersFansMapper.insert(usersFans);

        usersMapper.addFansCount(userId);
        usersMapper.addFollowerCount(fanId);
    }

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fanId) {
        UsersFans usersFans = new UsersFans();

        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        usersFansMapper.delete(usersFans);

        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollowerCount(fanId);
    }

    @Override
    public Boolean queryIfFollow(String userId, String fanId) {

        UsersFans usersFans = new UsersFans();
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);

        List<UsersFans> usersFansList = usersFansMapper.select(usersFans);

        if(usersFansList != null && usersFansList.size() > 0){
            return true;
        }
        return false;
    }

    @Override
    public void reportUser(UsersReport usersReport) {

        usersReport.setId(sid.nextShort());
        usersReport.setCreateDate(new Date());

        usersReportMapper.insert(usersReport);
    }
}
