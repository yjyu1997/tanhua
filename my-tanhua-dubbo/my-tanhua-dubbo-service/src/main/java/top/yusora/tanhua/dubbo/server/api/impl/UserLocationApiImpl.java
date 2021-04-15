package top.yusora.tanhua.dubbo.server.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.UserLocationApi;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.dto.UserLocationDto;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.UserLocation;

import java.util.List;
import java.util.Optional;

@DubboService (version = "1.0.0")
@Slf4j
@Service
public class UserLocationApiImpl implements UserLocationApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 更新用户地理位置
     *
     * @param userId 用户id
     * @param longitude 经度
     * @param latitude 纬度
     * @param address 地址名称
     * @return 是否更新成功
     */
    @Override
    public Boolean updateUserLocation(Long userId, Double longitude, Double latitude, String address) {

        //判断用户的地理位置数据是否存在，如果存在，更新数据，如果不存在就插入数据

        try {
            Query query = Query.query(Criteria.where("userId").is(userId));
            UserLocation userLocation = this.mongoTemplate.findOne(query, UserLocation.class);
            if(null == userLocation){
                this.initUserLocation(userId, longitude, latitude, address);
            }else{
                Long updatedDate = userLocation.getUpdated();
                Update update = Update.update("location", new GeoJsonPoint(longitude, latitude))
                        .set("updated", System.currentTimeMillis())
                        .set("lastUpdated", updatedDate)
                        .set("address", address);
                //更新数据
                this.mongoTemplate.updateFirst(query, update, UserLocation.class);
            }
        } catch (Exception e) {
            log.error("更新地理位置失败~ userId = " + userId + ", longitude = " + longitude + ", latitude = " + latitude + ", address = " + address, e);
            return false;
        }

        return true;
    }

    /**
     * 查询用户地理位置
     *
     * @param userId 用户id
     * @return 用户位置
     */
    @Override
    public UserLocationDto queryByUserId(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return Optional.ofNullable(this.mongoTemplate.findOne(query, UserLocation.class))
        .map(UserLocationDto::format).orElse(null);
    }

    /**
     * 根据位置搜索（分页）
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param distance  距离(米)
     * @param page      页数
     * @param pageSize  页面大小
     * @return 用户位置信息列表
     */
    @Override
    public PageInfo<UserLocationDto> queryUserFromLocation(Double longitude, Double latitude, Double distance, Integer page, Integer pageSize) {
        // 根据传入的坐标，进行确定中心点
        GeoJsonPoint geoJsonPoint = new GeoJsonPoint(longitude, latitude);

        // 画圈的半径
        Distance distanceObj = new Distance(distance / 1000, Metrics.KILOMETERS);

        // 画了一个圆圈(中心点，半径）
        Circle circle = new Circle(geoJsonPoint, distanceObj);

        //设置分页以及排序
        if(page < 1){
            page = 1;
        }
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize,Sort.by(Sort.Order.desc("location")));

        Query query = Query.query(Criteria.where("location").withinSphere(circle)).with(pageRequest);

        List<UserLocation> userLocations = this.mongoTemplate.find(query, UserLocation.class);

        PageInfo<UserLocationDto> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setRecords(UserLocationDto.formatToList(userLocations));
        return pageInfo;
    }

    /**
     * 初始化指定用户位置
     * @param userId 用户Id
     * @param longitude 经度
     * @param latitude 纬度
     * @param address 地址
     */
    private void initUserLocation(Long userId, Double longitude, Double latitude, String address) {
        UserLocation userLocation;
        userLocation = new UserLocation();
        userLocation.setId(ObjectId.get());
        userLocation.setUserId(userId);
        userLocation.setLocation(new GeoJsonPoint(longitude, latitude));
        userLocation.setAddress(address);
        userLocation.setCreated(System.currentTimeMillis());
        //数据更新时间
        userLocation.setUpdated(userLocation.getCreated());
        //上次更新时间
        userLocation.setLastUpdated(userLocation.getCreated());

        this.mongoTemplate.save(userLocation);
    }
}

