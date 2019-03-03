package com.imooc.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.BGMOperatorTypeEnum;
import com.imooc.mapper.BgmMapper;
import com.imooc.mapper.UsersReportCustomerMapper;
import com.imooc.mapper.VideosMapper;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.BgmExample;
import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.Reports;
import com.imooc.service.VideoService;
import com.imooc.web.util.ZKCurator;
import org.n3r.idworker.Sid;
import org.n3r.idworker.utils.JsonUtils;
import org.n3r.idworker.utils.PagedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: mate_J
 * @Date: 2019/1/31 22:53
 * @Version 1.0
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private BgmMapper bgmMapper;

    @Autowired
    private UsersReportCustomerMapper usersReportCustomerMapper;

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private ZKCurator zkCurator;

    @Override
    public void addBgm(Bgm bgm) {
        String bgmId = sid.nextShort();
        bgm.setId(bgmId);
        bgmMapper.insert(bgm);

        Map<String,String> map = new HashMap<>();
        map.put("type",BGMOperatorTypeEnum.ADD.type);
        try {
            map.put("path", URLEncoder.encode(bgm.getPath(),"UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        zkCurator.sendBgmOperator(bgmId, JsonUtils.objectToJson(map));
    }
    @Override
    public void delBgm(String bgmId) {
        Bgm bgm = bgmMapper.selectByPrimaryKey(bgmId);

        bgmMapper.deleteByPrimaryKey(bgmId);

        Map<String,String> map = new HashMap<>();
        map.put("type",BGMOperatorTypeEnum.DELETE.type);
        map.put("path",bgm.getPath());

        zkCurator.sendBgmOperator(bgmId, JsonUtils.objectToJson(map));
    }

    @Override
    public PagedResult queryReportList(Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<Reports> reportsList = usersReportCustomerMapper.selectAllVideoReport();
        PageInfo<Reports> pageInfo = new PageInfo<>(reportsList);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setRecords(pageInfo.getTotal());
        pagedResult.setRows(reportsList);
        pagedResult.setTotal(pageInfo.getPages());

        return pagedResult;
    }

    @Override
    public void updateVideoStatus(String videoId, Integer status) {
        Videos video = new Videos();
        video.setId(videoId);
        video.setStatus(status);

        videosMapper.updateByPrimaryKeySelective(video);
    }

    @Override
    public PagedResult queryBgmList(Integer page,Integer pageSize) {

        PageHelper.startPage(page,pageSize);

        BgmExample bgmExample = new BgmExample();
        List<Bgm> bgmList = bgmMapper.selectByExample(bgmExample);
        PageInfo<Bgm> pageInfo = new PageInfo<>(bgmList);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setRecords(pageInfo.getTotal());
        pagedResult.setRows(bgmList);
        pagedResult.setTotal(pageInfo.getPages());

        return pagedResult;
    }


}
