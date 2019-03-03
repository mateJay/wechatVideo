package com.imooc;

import com.imooc.config.ResourceConfig;
import com.imooc.enums.BGMOperatorTypeEnum;
import com.imooc.pojo.Bgm;
import com.imooc.service.BgmService;
import com.imooc.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @Author: mate_J
 * @Date: 2019/2/16 15:35
 * @Version 1.0
 */
@Component
public class ZKCuratorClient {

//    @Autowired
//    private BgmService bgmService;

    @Autowired
    private ResourceConfig resourceConfig;

    //zk客户端
    private CuratorFramework client = null;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);

//    public static final String ZOOKEEPER_SERVER = "192.168.1.5:2181";

    public void init() {
        if (client != null) {
            return;
        }

        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
        //创建zk客户端
        client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer())
                .sessionTimeoutMs(1000).retryPolicy(retryPolicy).namespace("admin").build();
        //启动zk客户端
        client.start();

        try {
//            String testData = new String( client.getData().forPath("/bgm/aaa4") );
//            log.info("测试节点数据为={}",testData);
            addChildWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置监听
     */
    public void addChildWatch(String nodePath) throws Exception {
        final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
        cache.start();
//         cache.getListenable().addListener(new PathChildrenCacheListener() {
//             @Override
//             public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
//
//                 if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
//                     log.info("监听到事件 CHILD_ADDED");
//                 }
//             }
//         });
        cache.getListenable().addListener((CuratorFramework client, PathChildrenCacheEvent event) -> {
            if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                log.info("监听到事件 CHILD_ADDED");

                //1、从数据库查询bgm对象---->>从zk中获取类型和bgm路径
                /**
                 * 节点路径
                 */
                String path = event.getData().getPath();
                //获取在节点中存的值,（类型和bgm路径)
                String operatorObj =new String(event.getData().getData(),"UTF-8");
                Map<String,String> map = JsonUtils.jsonToPojo(operatorObj,Map.class);
                String operType = (map.get("type"));
                String songPath = (map.get("path"));
                songPath = URLDecoder.decode(songPath,"UTF-8");


//                String[] arr = path.split("/");
//                String bgmId = arr[arr.length - 1];
//                Bgm bgm = bgmService.queryBgmById(bgmId);
//
//                if(bgm == null){
//                    return;
//                }
//                //bgm 所在路径
//                String songPath = bgm.getPath();

                //2、定义保存到本地的bgm路径,这是springboot项目保存bgm的路径
                String filePath = resourceConfig.getFileSpace() + songPath;

                //3、定义下载的路径(播放url，管理人员上传的bgm路径 ssm项目)
                //切割\时，要用\\\\，注意是四个
                String[] arrPath = songPath.split("\\\\");
                String finalPath = "";
                for(String t : arrPath){
                    if(StringUtils.isNotBlank(t)){
                        finalPath += "/";
                        finalPath += URLEncoder.encode(t,"UTF-8");
                    }
                }

                String bgmUrl = resourceConfig.getBgmServer() + finalPath;
                //对空格的处理问题
                bgmUrl = bgmUrl.replaceAll("\\+","%20");

                //下载bgm 到springboot服务器
                if(operType.equals(BGMOperatorTypeEnum.ADD.type)){
                    URL url = new URL(bgmUrl);
                    File file = new File(filePath);
                    FileUtils.copyURLToFile(url,file);
                    //删除节点
                    client.delete().forPath(path);
                }else if(operType.equals(BGMOperatorTypeEnum.DELETE.type)){
                    File file = new File(filePath);
                    FileUtils.forceDelete(file);
                    //删除节点
                    client.delete().forPath(path);
                }

            }
        });
    }
}
