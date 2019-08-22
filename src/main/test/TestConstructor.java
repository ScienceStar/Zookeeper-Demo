import org.junit.Test;

/**
 * @program: Zookeeper-Demo->TestConstructor
 * @description: 构造块测试
 * @author: lXC
 * @create: 2019-08-22 19:38
 **/
public class TestConstructor {

    {
        for(int i=0;i<10;i++){
            System.out.println(i);
        }
    }

    @Test
    public void show(){
        System.out.println("构造块演示.....");
    }
}
