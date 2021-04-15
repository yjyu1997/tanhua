package top.yusora.tanhua.dubbo.server.api.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.SettingsApi;
import top.yusora.tanhua.dubbo.server.mapper.SettingsMapper;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.Settings;

/**
 * @author heyu
 */
@DubboService(version = "1.0.0")
@Slf4j
@Service
public class SettingsApiImpl implements SettingsApi {
    @Autowired
    private SettingsMapper settingsMapper;


    /**
     * 根据用户id查询配置
     *
     * @param userId 用户id
     * @return 该用户的配置信息
     */
    @Override
    public Settings querySettings(Long userId) {
        QueryWrapper<Settings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.settingsMapper.selectOne(queryWrapper);
    }

    /**
     * 更新通知参数
     *
     * @param userId              用户id
     * @param likeNotification    推送喜欢通知
     * @param pinglunNotification 推送评论通知
     * @param gonggaoNotification 推送公告通知
     */
    @Override
    public void updateNotification(Long userId, Boolean likeNotification, Boolean pinglunNotification, Boolean gonggaoNotification) {
        Settings settings = this.querySettings(userId);
        if(ObjectUtil.isNull(settings)){
            //如果没有数据的话，插入一条数据
            settings = new Settings();
            settings.setUserId(userId);
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setLikeNotification(likeNotification);
            settings.setPinglunNotification(pinglunNotification);
            this.settingsMapper.insert(settings);
        }else {
            new LambdaUpdateChainWrapper<>(this.settingsMapper)
                    .eq(Settings::getUserId, userId)
                    .set(Settings::getGonggaoNotification, gonggaoNotification)
                    .set(Settings::getLikeNotification, likeNotification)
                    .set(Settings::getPinglunNotification, pinglunNotification)
                    .update();
        }
    }
}
