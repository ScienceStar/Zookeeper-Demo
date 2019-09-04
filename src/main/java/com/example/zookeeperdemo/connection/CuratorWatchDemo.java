package com.example.zookeeperdemo.connection;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

/**
 * @program: Zookeeper-Demo->CuratorWatchDemo
 * @description: Curator事件监听
 * @author: lXC
 * @create: 2019-09-04 21:18
 **/
public class CuratorWatchDemo {

    CuratorFramework curatorFramework = null;

    {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("192.168.1.106:2181,192.168.1.107:2181,192.168.1.108:2181")
                .sessionTimeoutMs(4000).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("curator").build();

        curatorFramework.start();
    }


    @Test
    public void listenerWatch() throws Exception {
        addListenerWithNodeCache(curatorFramework,"/mike");
    }

    @Test
    public void childrenListenerWatch() throws Exception {
        addListenerWithPathChildCache(curatorFramework,"/mike");
    }

    @Test
    public void treeListenerWatch() throws Exception {
        addListenerWithTreeCache(curatorFramework,"/mike");
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

//        addListenerWithNodeCache(curatorFramework,"/mike");
        System.in.read();
    }


    /**
     * 监听子节点
     * @param curatorFramework
     * @param path
     */
    public static void addListenerWithPathChildCache(CuratorFramework curatorFramework,String path) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework,path,true);

        PathChildrenCacheListener pathChildrenCacheListener = (client,event)->{
            System.out.println("Receive Event:"+event.getType());
        };

        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);

        pathChildrenCache.start(PathChildrenCache.StartMode.NORMAL);

        System.in.read();
    }

    /**
     * 综合事件
     * @param curatorFramework
     * @param path
     */
    public static void addListenerWithTreeCache(CuratorFramework curatorFramework,String path) throws Exception {
        TreeCache treeCache = new TreeCache(curatorFramework,path);

        TreeCacheListener treeCacheListener = (client,event)->{
            System.out.println(event.getType()+"->"+event.getData().getPath());
        };

        treeCache.getListenable().addListener(treeCacheListener);
        treeCache.start();
        System.in.read();
    }
}
