-- 小吃分类表
CREATE TABLE food_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    sort INT DEFAULT 0 COMMENT '排序序号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name (name)
) COMMENT '小吃分类表';

-- 小吃店铺表
CREATE TABLE food_shop (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '店铺名称',
    address VARCHAR(200) COMMENT '店铺地址',
    phone VARCHAR(20) COMMENT '联系电话',
    business_hours VARCHAR(100) COMMENT '营业时间',
    avg_price DECIMAL(10,2) COMMENT '人均消费',
    images VARCHAR(500) COMMENT '店铺图片',
    description TEXT COMMENT '店铺简介',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '小吃店铺表';

-- 小吃信息表
CREATE TABLE food_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '小吃名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    shop_id BIGINT NOT NULL COMMENT '所属店铺ID',
    images VARCHAR(500) COMMENT '小吃图片',
    score DECIMAL(3,1) DEFAULT 0 COMMENT '综合评分',
    avg_price DECIMAL(10,2) COMMENT '人均价格',
    description TEXT COMMENT '小吃介绍',
    recommend_reason VARCHAR(200) COMMENT '推荐理由',
    is_recommend TINYINT DEFAULT 0 COMMENT '是否推荐 1是 0否',
    status TINYINT DEFAULT 1 COMMENT '1上架 0下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '小吃信息表';

-- 小吃评论表
CREATE TABLE food_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    food_id BIGINT NOT NULL COMMENT '小吃ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    score INT DEFAULT 5 COMMENT '评分1-5',
    content TEXT COMMENT '评价内容',
    images VARCHAR(500) COMMENT '评价图片',
    status TINYINT DEFAULT 1 COMMENT '0待审核 1通过 2驳回',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '小吃评论表';

-- 小吃收藏表
CREATE TABLE food_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    food_id BIGINT NOT NULL COMMENT '小吃ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_food (user_id,food_id)
) COMMENT '小吃收藏表';
