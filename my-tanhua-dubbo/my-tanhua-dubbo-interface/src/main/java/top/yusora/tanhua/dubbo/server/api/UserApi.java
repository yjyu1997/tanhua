package top.yusora.tanhua.dubbo.server.api;

import top.yusora.tanhua.dubbo.server.pojo.po.mysql.User;

public interface UserApi {

    /**
     * 根据手机号查询用户
     *
     * @param mobile
     * @return
     */
    User queryByMobile(String mobile);

    /**
     * 根据手id查询用户
     *
     * @param id
     * @return
     */
    User queryById(Long id);


    /**
     * 注册新用户,返回用户id
     *
     * @param mobile
     * @return
     */
    Long save(String mobile);


    /**
     * 保存新手机号
     *
     * @param userId   用户id
     * @param newPhone 用户新手机号
     * @return 是否保存成功
     */
    Boolean updatePhone(Long userId, String newPhone);

}

