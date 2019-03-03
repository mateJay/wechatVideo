package com.imooc.web.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: mate_J
 * @Date: 2019/2/16 10:29
 * @Version 1.0
 */
public class ZKCurator {

    //zk客户端
    private CuratorFramework client = null;

    private final static Logger log = LoggerFactory.getLogger(ZKCurator.class);

    public ZKCurator(CuratorFramework client){
        this.client = client;
    }

    private void init() {
        client = client.usingNamespace("admin");
        try {
            //判断在admin命名空间下是否有bgm节点  /admin/bgm
            if (client.checkExists().forPath("/bgm") == null){
                /**
                 * 对于zk来说，有两种类型的节点：
                 * 永久节点：创建一个节点后，就永久存在，除非你手动删除
                 * 临时节点：创建一个节点后，会话断开，会自动删除，当然也可以手动删除。
                 */
                client.create().creatingParentsIfNeeded()
                        //节点类型,永久节点
                        .withMode(CreateMode.PERSISTENT)
                        //ACL:匿名权限
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath("/bgm");
                log.info("zookeeper 初始化成功");
            }
        } catch (Exception e) {
            log.error("zookeeper 客户端连接 初始化错误");
            e.printStackTrace();
        }
    }

    /**
     * 增加或删除bgm，向zk-server创建节点，供小程序后端监听
     */
    public void sendBgmOperator(String bgmId,String operType){
        try {
            client.create().creatingParentsIfNeeded()
                    //节点类型
                    .withMode(CreateMode.PERSISTENT)
                    //ACL:匿名权限
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    //在该节点下存储数据
                    .forPath("/bgm/" + bgmId,operType.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
