package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import com.imooc.pojo.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @Author: mate_J
 * @Date: 2018/12/12 15:35
 * @Version 1.0
 */
@RestController
@Api(value = "用户登陆注册接口",tags = {"注册和登陆的controller"})
public class RegistLoginController extends BasicController{
    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册",notes = "用户注册的接口")
    @PostMapping("/regist")
    public IMoocJSONResult register(@RequestBody Users user){
        //判断用户名和密码是否为空
        if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }

        //查看用户名是否被占用
        Boolean isExist = userService.queryUsernameIsExist(user.getUsername());
        if(isExist){
            return IMoocJSONResult.errorMsg("用户名已存在，请换一个再试");
        }else {
            user.setNickname(user.getUsername());
            try {
                user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            user.setFansCounts(0);
            user.setFollowCounts(0);
            user.setReceiveLikeCounts(0);

            userService.save(user);
        }

        UserVO userVO = setRedisSession(user);
//        UserVO userVO = new UserVO();
//        BeanUtils.copyProperties(user,userVO);

        return IMoocJSONResult.ok(userVO);
    }

    public UserVO setRedisSession(Users user){
        String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION + ":" + user.getId(),uniqueToken,1000 * 60 * 30);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        userVO.setUserToken(uniqueToken);
        return userVO;
    }

    @ApiOperation(value = "用户登陆",notes = "用户登陆的接口")
    @PostMapping("/login")
    public IMoocJSONResult login(@RequestBody Users user) throws Exception {
        //判断用户名和密码是否为空
        if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            return IMoocJSONResult.errorMsg("用户名或密码不能为空");
        }

        //判断用户是否存在,并返回
        Users result = userService.queryUserForLogin(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
        if(result == null){
            return IMoocJSONResult.errorMsg("用户名或密码不正确，请重新尝试。。。");
        }else {
            UserVO userVO = setRedisSession(result);
            return IMoocJSONResult.ok(userVO);
        }

    }


    @ApiOperation(value = "用户注销",notes = "用户注销的接口")
    @PostMapping("/logout")
    public IMoocJSONResult logout(String userId) throws Exception {

        redis.del(USER_REDIS_SESSION + ":" +userId);
        return IMoocJSONResult.ok();
    }
}
