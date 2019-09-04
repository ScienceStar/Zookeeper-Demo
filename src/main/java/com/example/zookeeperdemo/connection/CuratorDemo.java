package com.example.zookeeperdemo.connection;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

/**
 * @program: Zookeeper-Demo->CuratorDemo
 * @description: Curator操作zookeeper
 * @author: lXC
 * @create: 2019-08-20 23:14
 **/
public class CuratorDemo {

    CuratorFramework curatorFramework = null;

    {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("192.168.1.106:2181,192.168.1.107:2181,192.168.1.108:2181")
                .sessionTimeoutMs(4000).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("curator").build();

        curatorFramework.start();
    }


    @Test
    public void addNode() throws Exception {
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                .forPath("/mike/node1", "welcome".getBytes());

        addListenerWithNodeCache(curatorFramework, "/mike");
        System.in.read();
    }

    @Test
    public void updateNode(){

    }

    /**
     * @Description: 添加监听
     * @Param: []
     * @return: void
     * @Author: LXC
     * @Date: 2019/8/20 23:35
     */
    public static void addListenerWithNodeCache(CuratorFramework curatorFramework, String path) throws Exception {
        final NodeCache nodeCache = new NodeCache(curatorFramework, path, false);

        NodeCacheListener nodeCacheListener = () -> {
            System.out.println("Receive Event:" + nodeCache.getCurrentData().getPath());
        };

        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start();
    }
}
