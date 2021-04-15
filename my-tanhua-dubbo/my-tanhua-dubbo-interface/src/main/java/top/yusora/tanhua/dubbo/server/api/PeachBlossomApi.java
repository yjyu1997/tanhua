package top.yusora.tanhua.dubbo.server.api;

import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Sound;

import java.util.List;

public interface PeachBlossomApi {

    /**
     * 保存语音
     *
     * @param sound 语音
     * @return 保存成功后，返回语音id
     */
    String saveSound(Sound sound);

    /**
     * 获取今日声音用户列表
     * @param userId 用户Id
     * @return 声音列表
     */
    List<Long> getTodayList(Long userId);


    /**
     * 根据用户id 随机拉取一条语音
     *
     * @param userId 用户id
     * @param soundUserId 要查询的用户id
     * @return 语音
     */
    Sound getSound(Long userId, Long soundUserId);
}
