package com.tanhua.server.controller;


import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 根据手机号查询用户
     */
    @RequestMapping(value = "/findUser",method = RequestMethod.GET)
    public ResponseEntity findUser(String mobile){
        return userService.findByMobile(mobile);
    }

    /**
     * 新增用户
     */
    @RequestMapping(value = "/saveUser",method = RequestMethod.POST)
    public ResponseEntity saveUser(@RequestBody Map<String,Object> param){
        String mobile = (String)param.get("mobile");
        String password = (String)param.get("password");
        return userService.saveUser(mobile,password);
    }

    /**
     * 用户登陆发送验证码
     * @param param
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity sendValidateCode(@RequestBody Map<String,String> param){
        //获得手机号
        String phone = param.get("phone");
        //调用service层
        userService.sendValidateCode(phone);
        //返回结果
        return ResponseEntity.ok(null);
    }
    /**
     * 登陆验证码校验
     * @param param
     * @return
     */
    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map<String,String> param){
        String phone = param.get("phone");
        String verificationCode = param.get("verificationCode");
        Map<String, Object> map = userService.loginVerification(phone, verificationCode);
        return ResponseEntity.ok(map);
    }
}
