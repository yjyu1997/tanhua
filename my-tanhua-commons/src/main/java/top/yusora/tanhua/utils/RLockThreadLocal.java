package top.yusora.tanhua.utils;

import org.redisson.api.RLock;

/**
 * @author heyu 用户ThreadLocal
 */
public class RLockThreadLocal {

    private static final ThreadLocal<RLock> LOCK_THREAD_LOCAL = new ThreadLocal<>();

    private RLockThreadLocal(){

    }

    /**
     * 将对象放入到ThreadLocal
     *
     * @param lock Redisson RLock对象
     */
    public static void set(RLock lock){
        LOCK_THREAD_LOCAL.set(lock);
    }

    /**
     * 返回当前线程中的User对象
     *
     * @return User对象
     */
    public static RLock get(){
        return LOCK_THREAD_LOCAL.get();
    }

    /**
     * 删除当前线程中的User对象
     */
    public static void remove(){
        LOCK_THREAD_LOCAL.remove();
    }

}

