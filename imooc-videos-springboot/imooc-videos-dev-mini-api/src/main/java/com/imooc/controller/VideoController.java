package com.imooc.controller;

import com.imooc.enums.VideoStatusEnum;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.Comments;
import com.imooc.pojo.Videos;
import com.imooc.service.BgmService;
import com.imooc.service.VideoService;
import com.imooc.utils.FetchVideoCover;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MergeVideoMp3;
import com.imooc.utils.PagedResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * @Author: mate_J
 * @Date: 2018/12/12 15:35
 * @Version 1.0
 */
@RestController
@RequestMapping("/video")
public class VideoController extends BasicController{
    @Autowired
    private VideoService videoService;
    @Autowired
    private BgmService bgmService;

    /**
     * 上传视频
     * @param userId
     * @param bgmId
     * @param videoSeconds
     * @param videoWidth
     * @param videoHeight
     * @param desc
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public IMoocJSONResult upload(String userId,String bgmId,double videoSeconds,
                                  int videoWidth,int videoHeight,String desc,
                                  @RequestParam("file") MultipartFile file) throws Exception {
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }

        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String uploadCoverPathDB = "/" + userId + "/video";
        //上传文件的最终保存位置
        String finalVideoPath = "";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;

        try {
            if (file != null){
                String fileName = file.getOriginalFilename();
                String videoCoverName = fileName.split("\\.")[0];

                if(!StringUtils.isBlank(fileName)){
                    finalVideoPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //数据库的保存位置
                    uploadPathDB += ("/" + fileName);
                    uploadCoverPathDB = uploadCoverPathDB + "/" +videoCoverName + ".jpg";

                    //
                    File outFile = new File(finalVideoPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
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

        if(!StringUtils.isBlank(bgmId)){
            Bgm bgm = bgmService.queryBgmById(bgmId);

            String mp3InputPath = FILE_SPACE + bgm.getPath();
            String videoInputPath = finalVideoPath;
            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/" + userId + "/video/" + videoOutputName;
            String videoOutputPath = FILE_SPACE + uploadPathDB;

            MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
            tool.convertor(videoInputPath,mp3InputPath,videoSeconds,videoOutputPath);
        }

        //对视频进行截图
        FetchVideoCover fetchVideoCover = new FetchVideoCover(FFMPEG_EXE);
        fetchVideoCover.getCover(finalVideoPath,FILE_SPACE + uploadCoverPathDB);

        //保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoDesc(desc);
        video.setStatus(VideoStatusEnum.SUCCESS.getValue());
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoPath(uploadPathDB);
        video.setCoverPath(uploadCoverPathDB);
        video.setVideoSeconds((float)videoSeconds);
        video.setCreateTime(new Date());

        String videoId = videoService.saveVideo(video);

        return IMoocJSONResult.ok(videoId);
    }

    @PostMapping("/uploadCover")
    public IMoocJSONResult uploadCover(String userId,String videoId,
                                  @RequestParam("file") MultipartFile file) throws Exception {
        if(StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }

        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        //上传文件的最终保存位置
        String finalCoverPath = "";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;

        try {
            if (file != null){
                String fileName = file.getOriginalFilename();
                if(!StringUtils.isBlank(fileName)){
                    finalCoverPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //数据库的保存位置
                    uploadPathDB += ("/" + fileName);

                    //
                    File outFile = new File(finalCoverPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
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
        videoService.updateVideo(videoId,uploadPathDB);


        return IMoocJSONResult.ok();
    }

    @PostMapping("/showAll")
    public IMoocJSONResult showAll(@RequestBody Videos video,Integer isSaveRecord, Integer page){
        if(page == null){
            page = 1;
        }
        PagedResult result = videoService.queryVideos(video,isSaveRecord,page, PAGE_SIZE);

        return IMoocJSONResult.ok(result);

    }

    @PostMapping("/hot")
    public IMoocJSONResult hot(){
       return IMoocJSONResult.ok(videoService.getHotWords());
    }

    @PostMapping("/userLike")
    public IMoocJSONResult userLike(String userId,String videoId,String videoOwnerId){
        videoService.userLikeVideo(userId,videoId,videoOwnerId);
        return IMoocJSONResult.ok();
    }

    @PostMapping("/userUnLike")
    public IMoocJSONResult userUnLike(String userId,String videoId,String videoOwnerId){
        videoService.userUnlikeVideo(userId,videoId,videoOwnerId);
        return IMoocJSONResult.ok();
    }

    /**
     * 我收藏，点赞过的视频
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @PostMapping("/showMyLike")
    public IMoocJSONResult showMyLike(String userId,Integer page,Integer pageSize){
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.ok();
        }
        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = 6;
        }

        PagedResult videosList = videoService.queryMyLikeVideo(userId, page, pageSize);
        return IMoocJSONResult.ok(videosList);
    }

    /**
     * 我关注的人发的视频
     * @param userId
     * @param page
     * @return
     * @throws Exception
     */
    @PostMapping("/showMyFollow")
    public IMoocJSONResult showMyFollow(String userId, Integer page) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }
        int pageSize = 6;
        PagedResult videosList = videoService.queryMyFollowVideos(userId, page, pageSize);
        return IMoocJSONResult.ok(videosList);
    }

    /**
     * 保存留言
     * @param comments
     * @return
     */
    @PostMapping("/saveComment")
    public IMoocJSONResult saveComment(@RequestBody Comments comments,
                                       String fatherCommentId,
                                       String toUserId){
        if(StringUtils.isNoneBlank(fatherCommentId) && StringUtils.isNoneBlank(toUserId)){
            comments.setFatherCommentId(fatherCommentId);
            comments.setToUserId(toUserId);
        }
        videoService.saveComment(comments);
        return IMoocJSONResult.ok();
    }

    /**
     * 获取留言
     * @param videoId
     * @param page
     * @param pageSize
     * @return
     */
    @PostMapping("/getVideoComments")
    public IMoocJSONResult getVideoComments(String videoId,Integer page,Integer pageSize){
        if(StringUtils.isBlank(videoId)){
            return IMoocJSONResult.ok();
        }
        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }

        PagedResult list = videoService.getAllComments(videoId, page, pageSize);

        return IMoocJSONResult.ok(list);
    }

}
