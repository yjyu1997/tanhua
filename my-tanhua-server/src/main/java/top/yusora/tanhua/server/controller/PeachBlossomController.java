package top.yusora.tanhua.server.controller;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.exception.CastException;
import top.yusora.tanhua.server.service.PeachBlossomService;
import top.yusora.tanhua.server.service.TanhuaService;
import top.yusora.tanhua.server.vo.NearUserVo;
import top.yusora.tanhua.server.vo.SoundVo;
import top.yusora.tanhua.server.vo.TodayBest;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author heyu
 */
@RequestMapping("/peachblossom")
@RestController
public class PeachBlossomController {

    @Autowired
    PeachBlossomService peachBlossomService;



    @PostMapping
    public Void sendSound(@RequestParam("soundFile") MultipartFile soundFile){
        try {
            Boolean result = this.peachBlossomService.sendSound(soundFile);
            if(result){
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.SEND_SOUND_FAILED);
    }

    @GetMapping
    public SoundVo getSound(){
        try{
            return Optional.ofNullable(this.peachBlossomService.getSound())
                    .orElseThrow(() -> CastException.cast(ErrorCode.GET_SOUND_FAILED));
        }catch (Exception e){
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.GET_SOUND_FAILED);
    }
}

