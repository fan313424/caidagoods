-- 快速初始化数据库脚本
-- 使用方法: mysql -u root -p < init_database.sql

-- 创建数据库
CREATE DATABASE IF NOT EXISTS caida_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE caida_db;

-- 删除已存在的表（谨慎使用）
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS order_info;
DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS admin;

-- 用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(50) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    role TINYINT DEFAULT 0 COMMENT '角色：1-超级管理员，0-普通用户',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品表
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description VARCHAR(500) COMMENT '商品描述',
    price DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
    image_url VARCHAR(500) COMMENT '商品图片地址',
    category VARCHAR(100) COMMENT '商品分类',
    stock INT DEFAULT 0 COMMENT '库存数量',
    status TINYINT DEFAULT 1 COMMENT '状态：1-上架，0-下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 购物车表
CREATE TABLE cart_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车项ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量',
    total_price DECIMAL(10, 2) COMMENT '小计金额（单价*数量）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单表
CREATE TABLE order_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10, 2) COMMENT '订单总金额',
    status VARCHAR(20) DEFAULT 'paid' COMMENT '订单状态：paid-已付款, shipped-待收货, delivered-已完成, cancelled-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单项表
CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单项ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(200) COMMENT '商品名称',
    product_image VARCHAR(500) COMMENT '商品图片',
    product_desc VARCHAR(500) COMMENT '商品描述',
    price DECIMAL(10, 2) COMMENT '商品单价',
    quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    total_price DECIMAL(10, 2) COMMENT '小计金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- 插入示例用户数据
INSERT INTO user (username, password, nickname, email, phone, role, status) VALUES
('admin', '123456', '超级管理员', 'admin@caida.edu.cn', '13800138000', 1, 1),
('test', '123456', '测试用户', 'test@caida.edu.cn', '13800138001', 0, 1);

-- 员工表
CREATE TABLE employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '员工ID',
    employee_no VARCHAR(50) NOT NULL UNIQUE COMMENT '员工编号',
    name VARCHAR(50) NOT NULL COMMENT '员工姓名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(50) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    department VARCHAR(50) COMMENT '部门',
    position VARCHAR(50) COMMENT '职位',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_employee_no (employee_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 超级管理员表
CREATE TABLE admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(50) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    role TINYINT DEFAULT 1 COMMENT '角色：1-超级管理员，2-普通管理员',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='超级管理员表';

-- 插入示例员工数据
INSERT INTO employee (employee_no, name, password, department, position, email, phone, status) VALUES
('EMP001', '张三', '123456', '技术部', '软件工程师', 'zhangsan@caida.edu.cn', '13900139001', 1),
('EMP002', '李四', '123456', '市场部', '市场专员', 'lisi@caida.edu.cn', '13900139002', 1);

-- 插入示例超级管理员数据
INSERT INTO admin (username, password, real_name, role, email, phone, status) VALUES
('superadmin', 'admin123', '系统管理员', 1, 'superadmin@caida.edu.cn', '13900139000', 1);

-- 插入示例商品数据
INSERT INTO product (name, description, price, image_url, category, stock, status) VALUES
('潮色指甲油', '持久显色，适合日常搭配与个性妆容', 299.00, 'images/goods01.png', '美妆个护', 100, 1),
('高性能轻薄本', '兼顾学习办公与娱乐体验，适合大学生使用', 6900.00, 'images/goods02.png', '电脑办公', 50, 1),
('日用个护好物', '宿舍生活常用单品，兼顾实用与健康', 99.00, 'images/goods08.png', '日用百货', 200, 1),
('高清办公本', '大屏显示更清晰，适合文档处理与网课学习', 5999.00, 'images/goods04.png', '电脑办公', 30, 1),
('出行搭配包', '漂亮轻便易携带，适合日常通勤与校园出行', 999.00, 'images/goods06.png', '服装配饰', 80, 1);

SELECT '数据库初始化完成！' AS message;
SELECT COUNT(*) AS product_count FROM product;
