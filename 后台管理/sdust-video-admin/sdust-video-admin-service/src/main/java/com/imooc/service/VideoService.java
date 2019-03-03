package com.imooc.service;

import com.imooc.pojo.Bgm;
import org.n3r.idworker.utils.PagedResult;

/**
 * @Author: mate_J
 * @Date: 2019/1/31 22:52
 * @Version 1.0
 */
public interface VideoService {

    void addBgm(Bgm bgm);
    PagedResult queryBgmList(Integer page,Integer pageSize);
    void delBgm(String bgmId);

    PagedResult queryReportList(Integer page, Integer i);

    void updateVideoStatus(String videoId, Integer status);
}
