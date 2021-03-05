package com.tanhua.server.service;
import com.alibaba.fastjson.JSON;
import com.tanhua.commons.exception.TanHuaException;
import com.aliyuncs.utils.StringUtils;
import com.tanhua.commons.templates.SmsTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.server.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理业务层
 */
@Service
@Slf4j
public class UserService {
    @Reference
    private UserApi userApi;

    @Autowired
    private SmsTemplate smsTemplate;

    @Value("${tanhua.redisValidateCodeKeyPrefix}")
    private String redisValidateCodeKeyPrefix;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 根据手机号查询用户
     * @param mobile
     * @return
     */
    public ResponseEntity findByMobile(String mobile) {
        User user = userApi.findByMobile(mobile);
        return ResponseEntity.ok(user);
    }

    /**
     *新增用户
     * @param mobile
     * @param password
     * @return
     */
    public ResponseEntity saveUser(String mobile, String password) {
        User user = new User();
        user.setMobile(mobile);
        user.setPassword(password);
        userApi.save(user);
        return ResponseEntity.ok(null);
    }
    /**
     * 发送验证码
     * @param phone
     * @return
     */
    public void sendValidateCode(String phone) {
        //1构建redis中的key
        String key =  redisValidateCodeKeyPrefix + phone;
        //2获得redis中的验证码
        String codeInRedis = redisTemplate.opsForValue().get(key);
        //3如果值存在，则提示上一次发送的验证码未生效
        if (StringUtils.isNotEmpty(codeInRedis)){
            throw new TanHuaException(ErrorResult.duplicate());
        }
        //4如果值不存在，则生成验证码
        String validateCode = RandomStringUtils.randomNumeric(6);
        //5发送验证码
        //phone = 手机号码
        //validateCode = 验证码
        log.debug("发送验证码：{}，{}",phone,validateCode);
        Map<String, String> smsRs = smsTemplate.sendValidateCode(phone, validateCode);
        if (smsRs != null){
            throw new TanHuaException(ErrorResult.fail());
        }
        //6将验证码存入redis中，有效时间为五分钟
        log.info("将验证码存入redis");
        redisTemplate.opsForValue().set(key,validateCode,5, TimeUnit.MINUTES);
    }
    /**
     * 登陆或注册
     * @param phone
     * @param verificationCode
     * @return
     */
    public Map<String,Object> loginVerification(String phone, String verificationCode) {
        // redis中存入验证码的key
        String key = redisValidateCodeKeyPrefix + phone;
        // redis中的验证码
        String codeInRedis = redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);// 防止重复提交
        log.debug("========== 校验 验证码:{},{},{}",phone,codeInRedis,verificationCode);
        if(StringUtils.isEmpty(codeInRedis)){
            throw new TanHuaException(ErrorResult.loginError());
        }
        if(!codeInRedis.equals(verificationCode)){
            throw new TanHuaException(ErrorResult.validateCodeError());
        }
        // 查看用户是否存在
        User user = userApi.findByMobile(phone);
        boolean isNew = false;
        if(null == user){
            // 不存在则添加用户
            user = new User();
            user.setMobile(phone);
            // 手机号后6位为默认密码
            user.setPassword(DigestUtils.md5Hex(phone.substring(phone.length()-6)));
            log.info("============== 添加新用户 {}", phone);
            Long userId = userApi.save(user);
            user.setId(userId);
            isNew = true;
        }
        // 签发token令牌
        String token = jwtUtils.createJWT(phone,user.getId());
        // 用户信息存入redis，方便后期获取，有效期为1天
        String userString = JSON.toJSONString(user);
        redisTemplate.opsForValue().set("TOKEN_" + token,userString,1, TimeUnit.DAYS);
        log.debug("=========== 签发token: {}", token);
        // 返回结果
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("isNew",isNew);
        resultMap.put("token",token);
        return resultMap;
    }
}