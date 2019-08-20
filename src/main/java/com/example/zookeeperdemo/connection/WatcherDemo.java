package com.example.zookeeperdemo.connection;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @program: zookeeper-demo->WatcherDemo
 * @description: Watch事件机制测试
 * @author: lXC
 * @create: 2019-08-20 22:01
 **/
public class WatcherDemo {

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper("192.168.1.106:2181,192.168.1.107:2181,192.168.1.108:2181",
                4000, (event) -> {
            if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();

        zooKeeper.create("/zk-persis-mike", "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //exists,getdata,getChildren
        Stat stat = zooKeeper.exists("/zk-persis-mike", event -> {
            System.out.println("全局事件:"+event.getType());
            System.out.println(event.getType() + "->" + event.getPath());
            try {
                //循环监听
                zooKeeper.exists(event.getPath(),true);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        //通过修改事务来触发事件
        zooKeeper.setData("/zk-persis-mike","hello".getBytes(),stat.getVersion());

        Thread.sleep(1000);

        System.in.read();
    }
}
