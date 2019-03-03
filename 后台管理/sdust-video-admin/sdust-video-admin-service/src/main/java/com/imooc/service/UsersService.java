package com.imooc.service;

import com.imooc.pojo.Users;
import org.n3r.idworker.utils.PagedResult;

/**
 * @Author: mate_J
 * @Date: 2019/2/17 15:17
 * @Version 1.0
 */
public interface UsersService {
    public PagedResult queryUsers(Users user, Integer page, Integer pageSize);
}
