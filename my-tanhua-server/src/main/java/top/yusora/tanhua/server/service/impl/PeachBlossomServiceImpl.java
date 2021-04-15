package top.yusora.tanhua.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.dubbo.server.api.PeachBlossomApi;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Sound;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;
import top.yusora.tanhua.server.service.PeachBlossomService;
import top.yusora.tanhua.server.service.UserInfoService;
import top.yusora.tanhua.server.vo.SoundVo;
import top.yusora.tanhua.utils.UserThreadLocal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author heyu
 */
@Slf4j
@Service
public class PeachBlossomServiceImpl implements PeachBlossomService {

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Autowired
    protected FastFileStorageClient storageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserInfoService userInfoService;

    @DubboReference(version = "1.0.0")
    private PeachBlossomApi peachBlossomApi;

    private static final String REMAINING_REDIS_KEY_PREFIX = "PEACH_BLOSSOM_REMAINING_";

    private static final String PEACH_BLOSSOM_REDIS_KEY_PREFIX ="PEACH_BLOSSOM_";

    private static final Long MAX_REMAINING_TIMES = 10L;
    /**
     * 发送声音
     * @param soundFile  声音
     * @return 是否发送成功
     * @throws IOException io异常
     */
    @Override
    public Boolean sendSound(MultipartFile soundFile) throws IOException {

        //获取用户上下文
        Long userId = UserThreadLocal.get();

        Sound sound = new Sound();

        sound.setUserId(userId);

        //上传声音
        StorePath storePath = storageClient.uploadFile(soundFile.getInputStream(),
                soundFile.getSize(),
                StrUtil.subAfter(soundFile.getOriginalFilename(), '.', true),
                null);

        //设置声音url
        sound.setSoundUrl(fdfsWebServer.getWebServerUrl() + storePath.getFullPath());

        //Rpc调用保存
        String soundId = this.peachBlossomApi.saveSound(sound);

        return StrUtil.isNotEmpty(soundId);
    }

    /**
     * 获取声音
     *
     * @return 声音vo
     */
    @Override
    public SoundVo getSound() {
        //获取用户上下文
        Long userId = UserThreadLocal.get();
        //初始化
        SoundVo soundVo = new SoundVo();
        //查询缓存-> 今日剩余次数
        String remainingRedisKey = this.getRemainingRedisKey(userId);

        Long usedTimes = Optional.ofNullable(this.redisTemplate.opsForValue().increment(remainingRedisKey,1)).orElse(1L);

        if(usedTimes > MAX_REMAINING_TIMES){
            soundVo.setRemainingTimes(0);
            return null;
        }

        soundVo.setRemainingTimes(Convert.toInt(MAX_REMAINING_TIMES - usedTimes));

        //查询推荐缓存是否命中
        String peachBlossomRedisKey = this.getPeachBlossomRedisKey(userId);
        List<Long> userIds = new ArrayList<>();
        Boolean isPresent = Optional.ofNullable(this.redisTemplate.hasKey(peachBlossomRedisKey)).orElse(false);
        if(!isPresent){
            //未命中则查询mongodb
            userIds = this.peachBlossomApi.getTodayList(userId);
            if(CollUtil.isEmpty(userIds)){
                return null;
            }
        }else{
            String jsonStr = this.redisTemplate.opsForValue().get(peachBlossomRedisKey);
            if(StrUtil.isEmpty(jsonStr)){
                return null;
            }
            userIds = JSONArray.parseArray(jsonStr, Long.TYPE);
        }

        //获得需查询语音的用户id，根据该id随机查找一条语音
        Long soundUserId = userIds.remove(0);

        Sound sound = this.peachBlossomApi.getSound(userId, soundUserId);
        if(ObjectUtil.isNull(sound)){
            return null;
        }

        //拉取被查询语音的用户基本信息
        soundUserId = sound.getUserId();
        UserInfo userInfo = this.userInfoService.queryUserInfoByUserId(soundUserId);
        if(ObjectUtil.isNull(userInfo)){
            return null;
        }

        soundVo.setId(soundUserId);
        soundVo.setAge(userInfo.getAge());
        soundVo.setAvatar(userInfo.getLogo());
        soundVo.setGender(userInfo.getSex().name().toLowerCase());
        soundVo.setNickname(userInfo.getNickName());
        soundVo.setSoundUrl(sound.getSoundUrl());

        //将pop后的列表传回redis,用户一小时不操作 刷新redis列表
        this.redisTemplate.opsForValue().set(peachBlossomRedisKey,JSONUtil.toJsonStr(userIds),1, TimeUnit.HOURS);


        return soundVo;
    }

    private String getRemainingRedisKey(Long userId){
        return REMAINING_REDIS_KEY_PREFIX + userId;
    }

    private String getPeachBlossomRedisKey(Long userId){
        return PEACH_BLOSSOM_REDIS_KEY_PREFIX + userId;
    }
}
