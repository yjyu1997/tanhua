package top.yusora.tanhua.server.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.exception.CastException;
import top.yusora.tanhua.server.service.TodayBestService;
import top.yusora.tanhua.server.vo.PageResult;
import top.yusora.tanhua.server.vo.RecommendUserQueryParam;
import top.yusora.tanhua.server.vo.TodayBest;
import top.yusora.tanhua.utils.Cache;

import java.util.Optional;


/**
 * @author heyu
 */
@RestController
@RequestMapping("/tanhua")
@Slf4j
public class TodayBestController {
    @Autowired
    private TodayBestService todayBestService;

    @Cache
    @GetMapping("/todayBest")
    public TodayBest queryTodayBest(@RequestHeader("Authorization") String token) {

        Optional<TodayBest> todayBest = Optional.ofNullable(this.todayBestService.queryTodayBest(token));
        return todayBest.orElseThrow(() -> CastException.cast(ErrorCode.QUERY_TODAY_BEST_FAILED));
    }

    @Cache
    @GetMapping("/recommendation")
    public PageResult queryRecommendation(@RequestHeader("Authorization") String token, RecommendUserQueryParam queryParam){

        Optional<PageResult> pageResult = Optional.ofNullable(this.todayBestService
                .queryRecommendation(token, queryParam));

        return pageResult.orElseThrow(() -> CastException.cast(ErrorCode.QUERY_RECOMMENDED_USER_FAILED));
    }
}
