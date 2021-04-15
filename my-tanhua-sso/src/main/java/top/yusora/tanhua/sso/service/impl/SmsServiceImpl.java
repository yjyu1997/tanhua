package top.yusora.tanhua.sso.service.impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.sms.MessageStatus;
import com.nexmo.client.sms.SmsSubmissionResponse;
import com.nexmo.client.sms.messages.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.yusora.tanhua.constant.SSOCode;
import top.yusora.tanhua.sso.config.AliyunSMSConfig;
import top.yusora.tanhua.sso.service.SmsService;
import top.yusora.tanhua.vo.ErrorResult;

import java.time.Duration;
import java.util.Objects;

/**
 * @author heyu
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AliyunSMSConfig aliyunSMSConfig;




    /**
     * 短信验证码服务： 1。发送 2。存入Redis
     * @param phone 手机号
     * @return 发送结果 成功：null
     */
    @Override
    public ErrorResult sendCheckCode(String phone) {
            String redisKey = "CHECK_CODE_" + phone;

            if (Objects.requireNonNull(this.redisTemplate.hasKey(redisKey))) {
                return ErrorResult.builder()
                        .errCode(SSOCode.SSO_CHECKCODE_NOT_EXPIRED.getErrCode())
                        .errMessage(SSOCode.SSO_CHECKCODE_NOT_EXPIRED.getErrMessage())
                        .build();
            }

            //String code = this.sendSmsV(phone);

        //mock
            String code = "123456";
            if (StringUtils.isEmpty(code)) {
                return ErrorResult.builder()
                        .errCode(SSOCode.SSO_SMS_API_REJECTED.getErrCode())
                        .errMessage(SSOCode.SSO_SMS_API_REJECTED.getErrMessage())
                        .build();
            }


            //短信发送成功,将验证码存储到Redis,5分钟后失效
            this.redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(5));

            return null;
    }


    /**
     * @Description 短信发送
     * @param mobile 手机号
     * @return code
     */
    private String sendSms(String mobile) {
        DefaultProfile profile = DefaultProfile.getProfile(this.aliyunSMSConfig.getRegionId(), this.aliyunSMSConfig.getAccessKeyId(), this.aliyunSMSConfig.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);
        String code = RandomUtils.nextInt(100000, 999999) + "";
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(this.aliyunSMSConfig.getDomain());
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", this.aliyunSMSConfig.getRegionId());
        //目标手机号
        request.putQueryParameter("PhoneNumbers", mobile);
        //签名名称
        request.putQueryParameter("SignName", this.aliyunSMSConfig.getSignName());
        request.putQueryParameter("TemplateCode", this.aliyunSMSConfig.getTemplateCode());
        // 短信模板code
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
        // 模板中变量替换
        try {
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            if (StringUtils.contains(data, "\"Message\":\"OK\"")) {

                return code;

            }
            log.info("发送短信验证码失败~ data = " + data);

        } catch (Exception e) {

            log.error("发送短信验证码失败~ mobile = " + mobile, e);

        }
        return null;
    }

    private String sendSmsV(String mobile){
        NexmoClient client = new NexmoClient.Builder()
                .apiKey("821623fe")
                .apiSecret("s0k6XrwEb3sAWdOx")
                .build();
        String code = RandomUtils.nextInt(100000, 999999) + "";
        String messageText = "Hello from Vonage SMS API" + code;
        TextMessage message = new TextMessage("Vonage APIs", "86"+mobile, messageText);
        try {
            SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

            if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
                log.info("Message sent successfully.");
                return code;
            } else {
               log.info("Message failed with error: " + response.getMessages().get(0).getErrorText());
            }
        }catch(Exception e){
            log.error("Message failed with internal error. mobile = " + mobile, e);
        }
        return null;
    }


}
