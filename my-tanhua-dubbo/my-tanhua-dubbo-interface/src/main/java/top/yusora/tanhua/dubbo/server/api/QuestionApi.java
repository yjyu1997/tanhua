package top.yusora.tanhua.dubbo.server.api;

import top.yusora.tanhua.dubbo.server.pojo.po.mysql.Question;

/**
 * @author heyu
 */
public interface QuestionApi {

    /**
     * 根据用户id检索相应问题
     * @param userId 用户id
     * @return 问题
     */
    Question queryQuestion(Long userId);

    /**
     * 保存该用户的陌生人问题
     * @param userId 用户id
     * @param content 问题内容
     */
    void save(Long userId, String content);
}
