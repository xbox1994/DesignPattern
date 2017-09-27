package Singleton;

/**
 * 懒加载:是
 * 线程安全:否
 *
 * 不考虑线程安全的方案,加同步块保证线程安全之后效率很低
 */
public class LazyMan {
    private static LazyMan instance;
    private LazyMan(){}

    public static LazyMan getInstance() {
        if (instance == null) {
            instance = new LazyMan();
        }
        return instance;
    }
}