package com.imooc.service;

import com.imooc.pojo.Bgm;
import com.imooc.pojo.Comments;
import com.imooc.pojo.Videos;
import com.imooc.utils.PagedResult;

import java.util.List;

/**
 * @Author: mate_J
 * @Date: 2018/12/12 15:51
 * @Version 1.0
 */
public interface VideoService {
    public String saveVideo(Videos video);


    public void updateVideo(String videoId,String coverPath);

    /**
     * 分页查询视频列表
     * @param page
     * @param size
     * @return
     */
    public PagedResult queryVideos(Videos video,Integer isSaveRecord,Integer page,Integer size);

    /**
     * 获取热搜词
     * @return
     */
    public List<String> getHotWords();

    /**
     * 用户点赞
     * @param userId
     * @param videoId
     * @param videoOwnerId
     */
    public void userLikeVideo(String userId,String videoId,String videoOwnerId);

    /**
     * 取消点赞
     * @param userId
     * @param videoId
     * @param videoOwnerId
     */
    public void userUnlikeVideo(String userId,String videoId,String videoOwnerId);

    /**
     * 查询我喜欢的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryMyLikeVideo(String userId ,Integer page , Integer pageSize);

    /**
     * 查询我关注的的人的视频
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult queryMyFollowVideos(String userId, Integer page, int pageSize);

    void saveComment(Comments comments);


    PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
}
