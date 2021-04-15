package top.yusora.tanhua.dubbo.server.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.UsersApi;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Users;

import java.util.List;

/**
 * @author heyu
 */
@DubboService(version = "1.0.0")
@Service
public class UsersApiImpl implements UsersApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存好友关系
     *
     * @param userId   用户id
     * @param friendId 好友id
     * @return 好友与我关系主键Id
     */
    @Override
    public String saveUsers(Long userId, Long friendId) {
        //校验入参
        if (!ObjectUtil.isAllNotEmpty(userId, friendId)) {
            return null;
        }
        // 检测是否该好友关系是否存在
        Query query = Query.query(Criteria
                .where("userId").is(userId)
                .and("friendId").is(friendId));
        long count = this.mongoTemplate.count(query, Users.class);
        if (count > 0) {
            return null;
        }

        //init
        Users users = new Users();
        users.setId(ObjectId.get());
        users.setDate(System.currentTimeMillis());
        users.setUserId(userId);
        users.setFriendId(friendId);

        //注册我与好友的关系
        this.mongoTemplate.save(users);

        //注册好友与我的关系
        users.setId(ObjectId.get());
        users.setUserId(friendId);
        users.setFriendId(userId);
        this.mongoTemplate.save(users);

        return users.getId().toHexString();

    }

    /**
     * 删除好友数据
     *
     * @param userId   用户id
     * @param friendId 好友id
     * @return 是否删除成功
     */
    @Override
    public Boolean removeUsers(Long userId, Long friendId) {
        //TODO:事务回滚
        Query query1 = Query.query(Criteria.where("userId").is(userId)
                .and("friendId").is(friendId));

        //删除我与好友的关系数据
        long count1 = this.mongoTemplate.remove(query1, Users.class).getDeletedCount();

        Query query2 = Query.query(Criteria.where("userId").is(friendId)
                .and("friendId").is(userId));
        //删除好友与我的关系数据
        long count2 = this.mongoTemplate.remove(query2, Users.class).getDeletedCount();

        return count1 > 0 && count2 > 0;
    }

    /**
     * 根据用户id查询全部Users列表
     *
     * @param userId 用户Id
     * @return 全部Users列表
     */
    @Override
    public List<Users> queryAllUsersList(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return this.mongoTemplate.find(query, Users.class);
    }

    /**
     * 根据用户id查询Users列表(分页查询)
     *
     * @param userId   用户id
     * @param page     当前页数
     * @param pageSize 每页条数
     * @return Users列表(分页
     */
    @Override
    public PageInfo<Users> queryUsersList(Long userId, Integer page, Integer pageSize) {
        //初始化pageInfo
        PageInfo<Users> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);

        //-----------分页查询联系人列表----------------
        if(page < 1){
            page = 1;
        }
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("userId").is(userId)).with(pageRequest);

        List<Users> usersList = this.mongoTemplate.find(query, Users.class);
        if(CollUtil.isEmpty(usersList)){
            return pageInfo;
        }
        pageInfo.setRecords(usersList);
        return pageInfo;
    }

}
