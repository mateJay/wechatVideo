package com.imooc.controller;

import com.imooc.enums.VideoStatusEnum;
import com.imooc.pojo.Bgm;
import com.imooc.service.VideoService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.utils.IMoocJSONResult;
import org.n3r.idworker.utils.PagedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @Author: mate_J
 * @Date: 2019/1/31 19:47
 * @Version 1.0
 */
@Controller
@RequestMapping("/video")
public class VideoController {
    Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private VideoService videoService;


    @GetMapping("/showReportList")
    public String showReportList() {
        return "video/reportList";
    }

    @PostMapping("/reportList")
    @ResponseBody
    public PagedResult reportList(Integer page){
        PagedResult result = videoService.queryReportList(page,10);
        logger.info("result:{}",result);
        return result;
    }

    @PostMapping("/forbidVideo")
    @ResponseBody
    public IMoocJSONResult forbidVideo(String videoId){
        videoService.updateVideoStatus(videoId, VideoStatusEnum.FORBID.getValue());
        return IMoocJSONResult.ok();
    }

    @GetMapping("/showAddBgm")
    public String showAddBgm() {
        return "video/addBgm";
    }

    @GetMapping("/showBgmList")
    public String showBgmList() {
        return "video/bgmList";
    }

    @ResponseBody
    @PostMapping("/queryBgmList")
    public PagedResult queryBgmList(Integer page, HttpServletRequest request) {

        if (page == null) {
            page = 1;
        }

        return videoService.queryBgmList(page, 10);
    }

    @PostMapping("/addBgm")
    @ResponseBody
    public IMoocJSONResult addBgm(Bgm bgm) {
        videoService.addBgm(bgm);

        return IMoocJSONResult.ok();
    }

    @PostMapping("/delBgm")
    @ResponseBody
    public IMoocJSONResult delBgm(String bgmId) {
        videoService.delBgm(bgmId);

        return IMoocJSONResult.ok();
    }

    @PostMapping("/bgmUpload")
    @ResponseBody
    public IMoocJSONResult bgmUpload(@RequestParam("file") MultipartFile[] files) throws Exception {

        // 文件保存的命名空间
//		String fileSpace = File.separator + "imooc_videos_dev" + File.separator + "mvc-bgm";
        String fileSpace = "F:" + File.separator + "imooc_videos_dev" + File.separator + "mvc-bgm";
        // 保存到数据库中的相对路径
        String uploadPathDB = File.separator + "bgm";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (files != null && files.length > 0) {

                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    // 文件上传的最终保存路径
                    String finalPath = fileSpace + uploadPathDB + File.separator + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += (File.separator + fileName);

                    File outFile = new File(finalPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }

            } else {
                return IMoocJSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        return IMoocJSONResult.ok(uploadPathDB);
    }
}
