package Singleton;

/**
 * 懒加载:是
 * 线程安全:是
 *
 * 性能与安全平衡的方案
 */
public class StaticNestedClass {

    private static class SingletonHolder {
        private static final StaticNestedClass INSTANCE = new StaticNestedClass();
    }
    private StaticNestedClass (){}
    public static final StaticNestedClass getInstance() {
        return SingletonHolder.INSTANCE;
    }
}