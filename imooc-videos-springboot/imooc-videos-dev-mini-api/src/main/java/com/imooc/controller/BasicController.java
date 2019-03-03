package com.imooc.controller;

import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: mate_J
 * @Date: 2018/12/12 14:40
 * @Version 1.0
 */
@RestController
public class BasicController {

    @Autowired
    protected RedisOperator redis;

    public final static String USER_REDIS_SESSION="user-redis-session";

    public final static String FFMPEG_EXE = "F:\\ffmpeg\\bin\\ffmpeg.exe";
    //文件保存空间
    public final static String FILE_SPACE = "F:/workspace/upload";

    public final static Integer PAGE_SIZE = 3;

}
