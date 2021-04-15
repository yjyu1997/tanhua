package top.yusora.tanhua.utils;



/**
 * @author heyu 用户ThreadLocal
 */
public class UserThreadLocal {

    private static final ThreadLocal<Long> LOCAL = new ThreadLocal<>();

    private UserThreadLocal(){

    }

    /**
     * 将用户id放入到ThreadLocal
     */
    public static void set(Long userId){
        LOCAL.set(userId);
    }

    /**
     * 返回当前线程中的用户id
     *
     * @return 用户Id
     */
    public static Long get(){
        return LOCAL.get();
    }

    /**
     * 删除当前线程中的User对象
     */
    public static void remove(){
        LOCAL.remove();
    }



}

