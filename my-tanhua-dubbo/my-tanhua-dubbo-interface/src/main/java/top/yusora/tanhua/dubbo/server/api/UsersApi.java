package top.yusora.tanhua.dubbo.server.api;

import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Users;

import java.util.List;

/**
 * 好友关系Api
 * @author heyu
 */
public interface UsersApi {

    /**
     * 保存好友关系
     *
     * @param userId   用户id
     * @param friendId 好友id
     * @return 好友与我关系主键Id
     */
    String saveUsers(Long userId, Long friendId);


    /**
     * 删除好友数据
     *
     * @param userId   用户id
     * @param friendId 好友id
     * @return 是否删除成功
     */
    Boolean removeUsers(Long userId, Long friendId);

    /**
     * 根据用户id查询全部Users列表
     *
     * @param userId 用户Id
     * @return 全部Users列表
     */
    List<Users> queryAllUsersList(Long userId);

    /**
     * 根据用户id查询Users列表(分页查询)
     *
     * @param userId 用户id
     * @param page 当前页数
     * @param pageSize 每页条数
     * @return Users列表(分页
     */
    PageInfo<Users> queryUsersList(Long userId, Integer page, Integer pageSize);


}

