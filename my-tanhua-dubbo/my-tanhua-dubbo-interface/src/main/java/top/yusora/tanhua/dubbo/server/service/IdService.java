package top.yusora.tanhua.dubbo.server.service;

import top.yusora.tanhua.dubbo.server.enums.IdType;

/**
 * @author heyu
 * @Description 分布式自增长ID服务
 */
public interface IdService {
    /**
     * 创建自增ID
     * @param idType Id类型： PUBLISH VIDEO
     * @return 自增ID
     */
    Long createId(IdType idType);
}
