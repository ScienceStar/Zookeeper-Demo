package com.example.zookeeperdemo.connection;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

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

    @Override
    public void lock() {
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent event) {

    }
}
