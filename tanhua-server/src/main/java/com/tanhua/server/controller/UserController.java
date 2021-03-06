package com.tanhua.server.controller;


import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.server.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired(required = false)
    private UserService userService;

    /**
     * 个人信息
     * @param userInfoVo
     * @return
     */
    @RequestMapping("/loginReginfo")
    public ResponseEntity loginReginfo
            (@RequestBody UserInfoVo userInfoVo,
             @RequestHeader("Authorization") String token){

        UserInfo userInfo = new UserInfo();

        //复制属性
        BeanUtils.copyProperties(userInfoVo,userInfo);

        //保存用户信息
        userService.saveUserInfo(userInfo,token);
        return ResponseEntity.ok(null);
    }

    /**
     * 上传用户头像
     * @param headPhoto
     * @param token
     * @return
     */
    @PostMapping("/loginReginfo/head")
    public ResponseEntity uploadAvatar(MultipartFile headPhoto,@RequestHeader("Authorization") String token){
        userService.updateUserAvatar(headPhoto,token);
        return ResponseEntity.ok(null);
    }

}
