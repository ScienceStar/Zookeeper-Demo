import com.example.zookeeperdemo.connection.DistributedLock;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @program: Zookeeper-Demo->TestZookeeperLock
 * @description: 分布式锁测试
 * @author: lXC
 * @create: 2019-09-04 22:37
 **/
public class TestZookeeperLock {

    @Test
    public void testLock() throws IOException {
        CountDownLatch countDownLatch = new CountDownLatch(10);

        for(int i=0;i<10;i++){
            new Thread(()->{
                try {
                    countDownLatch.await();
                    DistributedLock distributedLock = new DistributedLock();
                    distributedLock.lock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"Thread-"+i).start();
            countDownLatch.countDown();
        }
        System.in.read();
    }
}
