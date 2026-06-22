package com.caida.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caida.entity.Admin;
import com.caida.entity.User;
import com.caida.mapper.AdminMapper;
import com.caida.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * 数据初始化器
 *
 * 职责：应用启动时自动执行数据库初始化任务
 * - 自动添加缺失的数据库列
 * - 初始化默认管理员账号
 * - 确保管理员角色权限正确
 *
 * @author 团队
 * @since 1.0
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DataSource dataSource;

    @Value("${admin.default.password:admin123}")
    private String adminDefaultPassword;

    @Override
    public void run(String... args) {
        ensureRoleColumn();
        initAdminAccount();
        initAdminUserRole();
    }

    /**
     * 自动添加 role 列（如果不存在）
     */
    private void ensureRoleColumn() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE user ADD COLUMN role TINYINT DEFAULT 0 COMMENT '角色：1-管理员，0-普通用户' AFTER nickname");
            System.out.println("数据库：已添加 user.role 列");
        } catch (Exception e) {
            // 列已存在则忽略
            if (e.getMessage().contains("Duplicate column")) {
                System.out.println("数据库：user.role 列已存在，跳过");
            }
        }
    }

    /**
     * 初始化管理员表账号
     */
    private void initAdminAccount() {
        Long count = adminMapper.selectCount(null);
        if (count == 0) {
            Admin admin = new Admin();
            admin.setUsername("superadmin");
            admin.setPassword(adminDefaultPassword);
            admin.setRealName("系统管理员");
            admin.setEmail("superadmin@caida.edu.cn");
            admin.setPhone("13900139000");
            admin.setRole(1);
            admin.setStatus(1);
            adminMapper.insert(admin);
            System.out.println("========================================");
            System.out.println("  默认管理员账号已创建");
            System.out.println("  用户名: superadmin");
            System.out.println("  密码:   " + adminDefaultPassword);
            System.out.println("========================================");
        }
    }

    /**
     * 确保 admin 用户是超级管理员
     */
    private void initAdminUserRole() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", "admin");
        User adminUser = userMapper.selectOne(wrapper);
        if (adminUser != null && (adminUser.getRole() == null || adminUser.getRole() != 1)) {
            adminUser.setRole(1);
            userMapper.updateById(adminUser);
            System.out.println("已设置 admin 用户为超级管理员 (role=1)");
        }
    }
}
