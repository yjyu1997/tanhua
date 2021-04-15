package top.yusora.tanhua.server.service;


import top.yusora.tanhua.server.vo.HuanXinUserVo;

/**
 * @author heyu
 */
public interface HuanXinService {
    /**
     * 通过dubbo服务查询环信用户
     * @return 环信用户信息
     */
    HuanXinUserVo queryHuanXinUser();
}
