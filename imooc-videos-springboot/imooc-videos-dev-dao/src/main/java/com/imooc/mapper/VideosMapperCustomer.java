package com.imooc.mapper;

import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustomer extends MyMapper<Videos> {
    public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc,
                                         @Param("userId") String userId);

    /**
     * 添加视频点赞的数量
     * @param videoId
     */
    public void addVideoLikeCount(String videoId);

    /**
     * 减少视频点赞数量
     * @param videoId
     */
    public void reduceVideoLikeCount(String videoId);

    /**
     * 查询我喜欢、点赞的视频
     * @param userId
     * @return
     */
    List<VideosVO> queryMyLikeVideo(String userId);

    /**
     * 查询我关注的人发过的视频
     * @param userId
     * @return
     */
    List<VideosVO> queryMyFollowVideo(String userId);
}