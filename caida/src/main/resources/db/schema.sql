-- 创建数据库
CREATE DATABASE IF NOT EXISTS caida_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE caida_db;

-- 商品表
CREATE TABLE IF NOT EXISTS product (
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
CREATE TABLE IF NOT EXISTS cart_item (
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

-- 插入示例商品数据
INSERT INTO product (name, description, price, image_url, category, stock, status) VALUES
('潮色指甲油', '持久显色，适合日常搭配与个性妆容', 299.00, 'images/goods01.png', '美妆个护', 100, 1),
('高性能轻薄本', '兼顾学习办公与娱乐体验，适合大学生使用', 6900.00, 'images/goods02.png', '电脑办公', 50, 1),
('日用个护好物', '宿舍生活常用单品，兼顾实用与健康', 99.00, 'images/goods08.png', '日用百货', 200, 1),
('高清办公本', '大屏显示更清晰，适合文档处理与网课学习', 5999.00, 'images/goods04.png', '电脑办公', 30, 1),
('出行搭配包', '漂亮轻便易携带，适合日常通勤与校园出行', 999.00, 'images/goods06.png', '服装配饰', 80, 1),
('5G智能手机', '高清大屏、流畅性能，满足拍照游戏日常需求', 4999.00, 'images/goods03.png', '手机数码', 40, 1),
('运动跑步鞋', '轻便透气、缓震耐磨，适合校园日常运动', 599.00, 'images/goods05.png', '运动户外', 60, 1);
