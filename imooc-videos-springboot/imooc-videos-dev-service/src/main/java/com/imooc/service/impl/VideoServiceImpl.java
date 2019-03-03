package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.*;
import com.imooc.pojo.Comments;
import com.imooc.pojo.SearchRecords;
import com.imooc.pojo.UsersLikeVideos;
import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.service.VideoService;
import com.imooc.utils.PagedResult;
import com.imooc.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author: mate_J
 * @Date: 2018/12/12 16:05
 * @Version 1.0
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideosMapper videosMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private VideosMapperCustomer videosMapperCustomer;
    @Autowired
    private SearchRecordsMapper searchRecordsMapper;
    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Override
    public String saveVideo(Videos video) {
        String id = sid.nextShort();
        video.setId(id);

        videosMapper.insertSelective(video);
        return id;
    }

    @Override
    public void updateVideo(String videoId, String coverPath) {
        Videos video = new Videos();
        video.setId(videoId);
        video.setCoverPath(coverPath);

        videosMapper.updateByPrimaryKeySelective(video);
    }

    @Override
    public PagedResult queryVideos(Videos video,Integer isSaveRecord,Integer page, Integer size) {
        //保存热搜词
        String desc = video.getVideoDesc();
        String userId = video.getUserId();
        //保存热搜词
        if( isSaveRecord != null && isSaveRecord == 1){
            SearchRecords record = new SearchRecords();
            record.setId(sid.nextShort());
            record.setContent(desc);

            searchRecordsMapper.insert(record);
        }


        PageHelper.startPage(page,size);
        List<VideosVO> videosVOList = videosMapperCustomer.queryAllVideos(desc,userId);
        PageInfo<VideosVO> pageList = new PageInfo<>(videosVOList);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(videosVOList);
        pagedResult.setRecords(pageList.getTotal());
        pagedResult.setPage(page);

        return pagedResult;
    }

    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotwords();
    }

    @Override
    public void userLikeVideo(String userId, String videoId, String videoOwnerId) {
        //1、保存用户和视频拥有着的关联关系
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        usersLikeVideos.setId(sid.nextShort());
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideosMapper.insert(usersLikeVideos);
        //2、视频喜欢数量的累加
        videosMapperCustomer.addVideoLikeCount(videoId);
        //3、用户受喜欢数量的累加
        usersMapper.addReciveLikeCount(videoOwnerId);

    }

    @Override
    public void userUnlikeVideo(String userId, String videoId, String videoOwnerId) {
        //1、删除用户和视频拥有着的关联关系
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();

        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoOwnerId);

        usersLikeVideosMapper.delete(usersLikeVideos);
        //2、视频喜欢数量的累减
        videosMapperCustomer.reduceVideoLikeCount(videoId);
        //3、用户受喜欢数量的累减
        usersMapper.reduceReciveLikeCount(videoOwnerId);

    }

    @Override
    public PagedResult queryMyLikeVideo(String userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        List<VideosVO> videosVOList = videosMapperCustomer.queryMyLikeVideo(userId);
        PageInfo<VideosVO> pageList = new PageInfo<>(videosVOList);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(videosVOList);
        pagedResult.setRecords(pageList.getTotal());
        pagedResult.setPage(page);

        return pagedResult;
    }

    @Override
    public PagedResult queryMyFollowVideos(String userId, Integer page, int pageSize) {

        PageHelper.startPage(page,pageSize);
        List<VideosVO> videosVOList = videosMapperCustomer.queryMyFollowVideo(userId);
        PageInfo<VideosVO> pageList = new PageInfo<>(videosVOList);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(videosVOList);
        pagedResult.setRecords(pageList.getTotal());
        pagedResult.setPage(page);

        return pagedResult;
    }


    @Override
    public void saveComment(Comments comments) {
        comments.setId(sid.nextShort());
        comments.setCreateTime(new Date());

        commentsMapper.insert(comments);
    }

    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);

        List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);

        for (CommentsVO c : list) {
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }

        PageInfo<CommentsVO> pageList = new PageInfo<>(list);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(list);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());

        return grid;

    }
}
