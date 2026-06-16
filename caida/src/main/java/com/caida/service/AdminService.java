package com.caida.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caida.entity.Admin;
import com.caida.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 管理员登录
     */
    public Admin login(String username, String password) {
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        Admin admin = adminMapper.selectOne(wrapper);

        if (admin == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (admin.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        if (!admin.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }

        admin.setLastLoginTime(LocalDateTime.now());
        adminMapper.updateById(admin);

        return admin;
    }
}
