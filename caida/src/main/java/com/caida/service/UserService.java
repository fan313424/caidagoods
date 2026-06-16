package com.caida.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caida.entity.User;
import com.caida.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     */
    @Transactional
    public User register(User user) {
        // 检查用户名是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            QueryWrapper<User> emailWrapper = new QueryWrapper<>();
            emailWrapper.eq("email", user.getEmail());
            if (userMapper.selectCount(emailWrapper) > 0) {
                throw new RuntimeException("邮箱已被注册");
            }
        }

        // 检查手机号是否已存在
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            QueryWrapper<User> phoneWrapper = new QueryWrapper<>();
            phoneWrapper.eq("phone", user.getPhone());
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new RuntimeException("手机号已被注册");
            }
        }

        userMapper.insert(user);
        return user;
    }

    /**
     * 用户登录
     */
    public User login(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }

        return user;
    }

    /**
     * 根据ID获取用户
     */
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        User existing = userMapper.selectById(user.getId());
        if (existing == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.updateById(user);
        return user;
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.selectCount(wrapper) > 0;
    }
}
