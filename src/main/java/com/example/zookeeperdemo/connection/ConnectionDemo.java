package com.example.zookeeperdemo.connection;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @program: zookeeper-demo->ConnectionDemo
 * @description: zookeeper连接
 * @author: lXC
 * @create: 2019-08-20 21:33
 **/
public class ConnectionDemo {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            ZooKeeper zooKeeper = new ZooKeeper("192.168.1.106:2181,192.168.1.107:2181,192.168.1.108:2181",
                    4000, (event) -> {
                if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            System.out.println(zooKeeper.getState());

            //zookeeper 操作
            zooKeeper.create("/zk-persis-mike", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            Thread.sleep(1000);
            Stat stat = new Stat();

            //得到当前的值
            byte[] bytes = zooKeeper.getData("/zk-persis-mike", null, stat);
            System.out.println("修改之前的值:" + new String(bytes));

            //修改值
            zooKeeper.setData("/zk-persis-mike", "welcome".getBytes(), stat.getVersion());
            byte[] bytes2 = zooKeeper.getData("/zk-persis-mike", null, stat);
            System.out.println("修改之后的值:" + new String(bytes2));
            zooKeeper.close();
            System.in.read();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
