package Singleton;

/**
 * 懒加载:否,当使用A的时候会new,具体见深入理解java虚拟机
 * 线程安全:是
 *
 * 不考虑初始化性能开销最好方案
 */
public class HungryMan {
    public static int A = 3;
    private static HungryMan instance = new HungryMan();
    private HungryMan(){
        System.out.println("init");
    }

    public static HungryMan getInstance() {
        return instance;
    }
}