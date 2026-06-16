package com.caida.controller;

import com.caida.common.Result;
import com.caida.entity.User;
import com.caida.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody User user) {
        try {
            // 验证必填字段
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return Result.error("密码不能为空");
            }
            
            // 注册用户
            User registeredUser = userService.register(user);
            
            // 返回用户信息（不包含密码）
            Map<String, Object> data = new HashMap<>();
            data.put("id", registeredUser.getId());
            data.put("username", registeredUser.getUsername());
            data.put("nickname", registeredUser.getNickname());
            data.put("email", registeredUser.getEmail());
            data.put("phone", registeredUser.getPhone());
            data.put("role", registeredUser.getRole() != null ? registeredUser.getRole() : 0);

            return Result.success("注册成功", data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("注册失败：" + e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            
            // 验证必填字段
            if (username == null || username.trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                return Result.error("密码不能为空");
            }
            
            // 登录
            User user = userService.login(username, password);
            
            // 将用户信息存入Session
            session.setAttribute("currentUser", user);
            
            // 返回用户信息（不包含密码）
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());
            data.put("email", user.getEmail());
            data.put("phone", user.getPhone());
            data.put("role", user.getRole() != null ? user.getRole() : 0);

            return Result.success("登录成功", data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            // 清除密码信息
            user.setPassword(null);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    public Result<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        try {
            boolean exists = userService.existsByUsername(username);
            Map<String, Boolean> data = new HashMap<>();
            data.put("exists", exists);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("检查用户名失败：" + e.getMessage());
        }
    }
    
    /**
     * 用户退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        try {
            // 清除Session中的用户信息
            session.removeAttribute("currentUser");
            session.invalidate();
            return Result.success("退出成功", null);
        } catch (Exception e) {
            return Result.error("退出失败：" + e.getMessage());
        }
    }
}
