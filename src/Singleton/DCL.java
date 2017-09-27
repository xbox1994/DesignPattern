package Singleton;

/**
 * 懒加载:是
 * 线程安全:是
 *
 * 性能与安全平衡的方案 double-checked locking
 */
public class DCL {
    private volatile static DCL singleton;
    private DCL (){}
    public static DCL getSingleton() {
        if (singleton == null) { // 保证new了之后的得到实例的效率
            synchronized (DCL.class) {
                if (singleton == null) { //保证new的时候实例还没有被创建,否则别的线程会new很多实例
                    singleton = new DCL();
                    /*
                    1.allocate memory
                    2.init DCL
                    3.mov singleton, object
                    1-2-3/1-3-2，后者的问题在于3执行完成之后另外一个线程进到同步块的时候就会返回还没初始化的对象
                     */
                }
            }
        }
        return singleton;
    }
}