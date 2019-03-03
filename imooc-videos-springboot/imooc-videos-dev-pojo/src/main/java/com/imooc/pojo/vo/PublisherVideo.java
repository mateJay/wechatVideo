package com.imooc.pojo.vo;

/**
 * @Author: mate_J
 * @Date: 2018/12/20 12:48
 * @Version 1.0
 */
public class PublisherVideo {
    private UserVO publisherVo;
    private boolean userLikeVideo;

    public UserVO getPublisherVo() {
        return publisherVo;
    }

    public void setPublisherVo(UserVO publisherVo) {
        this.publisherVo = publisherVo;
    }

    public boolean isUserLikeVideo() {
        return userLikeVideo;
    }

    public void setUserLikeVideo(boolean userLikeVideo) {
        this.userLikeVideo = userLikeVideo;
    }
}
