package com.imooc.service;

import com.imooc.pojo.Bgm;
import com.imooc.pojo.Users;

import java.util.List;

/**
 * @Author: mate_J
 * @Date: 2018/12/12 15:51
 * @Version 1.0
 */
public interface BgmService {
    public List<Bgm> queryBgmList();

    public Bgm queryBgmById(String bgmId);
}
