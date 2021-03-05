package com.tanhua.dubbo.api;

import com.tanhua.domain.db.User;

public interface UserApi {

    /**
     * 添加用户
     * @param user
     * @return
     */
    Long save(User user);

    /**
     * 通过手机号码查询
     * @param mobile
     * @return
     */
    User findByMobile(String mobile);
}