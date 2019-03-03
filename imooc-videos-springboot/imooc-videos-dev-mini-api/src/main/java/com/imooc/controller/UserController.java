package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;
import com.imooc.pojo.vo.PublisherVideo;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.pojo.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @Author: mate_J
 * @Date: 2018/12/12 15:35
 * @Version 1.0
 */
@RestController
@Api(value = "用户相关业务的接口",tags = {"用户相关业务的controller"})
@RequestMapping("/user")
public class UserController extends BasicController{
    @Autowired
    private UserService userService;


    @ApiOperation(value = "用户上传头像",notes = "用户上传头像的接口")
    @PostMapping("/uploadFace")
    public IMoocJSONResult uploadFace(String userId,
                                  @RequestParam("file") MultipartFile[] files) throws Exception {
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }

        //文件保存空间
        String fileSpace = "F:/workspace/upload";
        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/face";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;

        try {
            if (files != null && files.length > 0){
                String fileName = files[0].getOriginalFilename();

                if(!StringUtils.isBlank(fileName)){
                    //上传文件的最终保存位置
                    String finalFacePath = fileSpace + uploadPathDB + "/" + fileName;
                    //数据库的保存位置
                    uploadPathDB += ("/" + fileName);

                    //
                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);

                }else {
                    return IMoocJSONResult.errorMsg("文件上传出错");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("文件上传出错");
        }finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        Users user = new Users();
        user.setId(userId);
        user.setFaceImage(uploadPathDB);

        userService.updateUserInfo(user);
        return IMoocJSONResult.ok(uploadPathDB);
    }
    @ApiOperation(value = "查询用户信息",notes = "查询用户信息")
    @PostMapping("/query")
    public IMoocJSONResult query(String userId,String fanId){
        Users user = userService.query(userId);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);

        Boolean isFollow = userService.queryIfFollow(userId, fanId);
        userVO.setFollow(isFollow);

        return IMoocJSONResult.ok(userVO);
    }

    @PostMapping("/queryPublisher")
    public IMoocJSONResult queryPublisher(String loginUserId,String videoId,String publishUserId){
        if(StringUtils.isBlank(publishUserId)){
            return IMoocJSONResult.errorMsg("");
        }

        //1、查询视频发布者的信息
        Users userInfo = userService.query(publishUserId);
        UserVO publisher = new UserVO();
        BeanUtils.copyProperties(userInfo,publisher);

        //2、查询当前登录者和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId,videoId);

        PublisherVideo publisherVideo = new PublisherVideo();
        publisherVideo.setPublisherVo(publisher);
        publisherVideo.setUserLikeVideo(userLikeVideo);

        return IMoocJSONResult.ok(publisherVideo);
    }

    @PostMapping("/beyourFans")
    public IMoocJSONResult  beyourFans(String userId,String fanId){
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }

        userService.saveUserFanRelation(userId,fanId);
        return IMoocJSONResult.ok("关注成功");
    }

    @PostMapping("/dontyourFans")
    public IMoocJSONResult  dontyourFans(String userId,String fanId){
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }

        userService.deleteUserFanRelation(userId,fanId);
        return IMoocJSONResult.ok("取消关注成功");
    }

    @PostMapping("/reportUser")
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport){

        userService.reportUser(usersReport);

        return IMoocJSONResult.ok("举报成功");
    }

}
