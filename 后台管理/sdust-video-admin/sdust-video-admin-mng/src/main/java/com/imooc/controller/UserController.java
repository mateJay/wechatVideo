package com.imooc.controller;

import com.imooc.bean.AdminUser;
import com.imooc.pojo.Users;
import com.imooc.service.UsersService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.utils.IMoocJSONResult;
import org.n3r.idworker.utils.PagedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @Author: mate_J
 * @Date: 2019/1/31 17:50
 * @Version 1.0
 */
@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UsersService usersService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("login")
    @ResponseBody
    public IMoocJSONResult userLogin(String username, String password,
                                     HttpServletRequest request, HttpServletResponse response) {

        // TODO 模拟登陆
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return IMoocJSONResult.errorMap("用户名和密码不能为空");
        } else if (username.equals("lee") && password.equals("lee")) {

            String token = UUID.randomUUID().toString();
            AdminUser user = new AdminUser(username, password, token);
            request.getSession().setAttribute("sessionUser", user);
            return IMoocJSONResult.ok();
        }

        return IMoocJSONResult.errorMsg("登陆失败，请重试...");
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("sessionUser");
        return "login";
    }

    @GetMapping("/showList")
    public String showList() {
        return "/users/usersList";
    }

    @PostMapping("/list")
    @ResponseBody
    public PagedResult list(Users user,Integer page) {
        PagedResult result = usersService.queryUsers(user,page == null ? 1 : page,10);
        return result;
    }
}
