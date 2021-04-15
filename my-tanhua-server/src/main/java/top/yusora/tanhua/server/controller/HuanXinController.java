package top.yusora.tanhua.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.exception.CastException;
import top.yusora.tanhua.server.service.HuanXinService;
import top.yusora.tanhua.server.vo.HuanXinUserVo;

import java.util.Optional;

/**
 * @author heyu
 */
@RestController
@RequestMapping("/huanxin")
public class HuanXinController {

    @Autowired
    private HuanXinService huanXinService;

    @GetMapping("/user")
    public HuanXinUserVo queryHuanXinUser() {
        return Optional.ofNullable(this.huanXinService.queryHuanXinUser())
                .orElseThrow(() -> CastException.cast(ErrorCode.HUANXIN_USER_DOESNT_EXIST));
    }
}