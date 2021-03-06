package com.tanhua.dubbo.api;

import com.tanhua.domain.db.UserInfo;

public interface UserInfoApi {
    /**
     * 保存用户基本信息
     * @param userInfo
     */
    void save(UserInfo userInfo);

    /**
     * 通过id更新用户信息
     * @param userInfo
     */
    void update(UserInfo userInfo);
}
