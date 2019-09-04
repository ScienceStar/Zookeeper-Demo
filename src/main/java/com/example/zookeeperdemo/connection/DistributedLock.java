package com.example.zookeeperdemo.connection;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @program: Zookeeper-Demo->DistributedLock
 * @description: 分布式锁实现
 * @author: lXC
 * @create: 2019-08-22 18:42
 **/
public class DistributedLock implements Lock, Watcher {

    private ZooKeeper zk;

    private String ROOT_LOCK="/locks";
    private String WAIT_LOCK;
    private String CURRENT_LOCK;
    private CountDownLatch countDownLatch;

    public DistributedLock() {

        try {
            zk = new ZooKeeper("192.168.1.106:2181,192.168.1.107:2181,192.168.1.108:2181",
                    4000, this);

            Stat stat = zk.exists(ROOT_LOCK,false);

            if(stat == null){
                zk.create(ROOT_LOCK,"0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }

        } catch (IOException | KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lock() {
        if(this.tryLock()){
            System.out.println(Thread.currentThread().getName()+"->"+CURRENT_LOCK+"->获得锁");
            return;
        }
        try {
            waitForLock(WAIT_LOCK);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean waitForLock(String prev) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(prev,true);

        if(stat!=null){
            System.out.println(Thread.currentThread().getName()+"->等待锁"+ROOT_LOCK+prev+"释放");
            countDownLatch=new CountDownLatch(1);
            countDownLatch.await();
            System.out.println(Thread.currentThread().getName()+"->获得锁成功");
        }
        return true;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        try {
            CURRENT_LOCK = zk.create(ROOT_LOCK+"/","0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);

            System.out.println(Thread.currentThread().getName()+"->"+CURRENT_LOCK+",尝试竞争锁");

            List<String> childrens = zk.getChildren(ROOT_LOCK,false);

            SortedSet<String> sortedSet = new TreeSet<String>();
            for(String children : childrens){
                sortedSet.add(ROOT_LOCK+"/"+children);
            }

            String firstNode = sortedSet.first();
            SortedSet<String> lessThenMe = ((TreeSet<String>) sortedSet).headSet(CURRENT_LOCK);

            if(CURRENT_LOCK.equals(firstNode)){
                return true;
            }

            if(!lessThenMe.isEmpty()){
                WAIT_LOCK=lessThenMe.last();
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName()+"->释放锁"+CURRENT_LOCK);

        try {
            zk.delete(CURRENT_LOCK,-1);
            CURRENT_LOCK=null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent event) {
        if(this.countDownLatch!=null){
            this.countDownLatch.countDown();
        }
    }
}
