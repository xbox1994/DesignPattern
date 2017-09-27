package Singleton;

/**
 * 懒加载:否
 * 线程安全:是
 *
 * 不考虑初始化性能开销最好方案,enum防止反序列化重新创建新的对象,Effective Java作者Josh Bloch 提倡
 */
public enum ENUM {
    INSTANCE;
}