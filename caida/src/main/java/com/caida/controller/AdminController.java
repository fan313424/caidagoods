package com.caida.controller;

import com.caida.common.Result;
import com.caida.entity.Admin;
import com.caida.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            if (username == null || username.trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                return Result.error("密码不能为空");
            }

            Admin admin = adminService.login(username, password);

            // 存入Session
            session.setAttribute("currentAdmin", admin);

            Map<String, Object> data = new HashMap<>();
            data.put("id", admin.getId());
            data.put("username", admin.getUsername());
            data.put("realName", admin.getRealName());
            data.put("role", admin.getRole());

            return Result.success("登录成功", data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.removeAttribute("currentAdmin");
        session.invalidate();
        return Result.success("已退出", null);
    }
}
