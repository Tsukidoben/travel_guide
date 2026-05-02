-- 景点详情页路线&沿途小吃模块优化 - 数据库迁移脚本
-- 执行时间：2026-05-02

-- 1. 为 food_shop 表添加营业状态字段
ALTER TABLE food_shop 
ADD COLUMN business_status VARCHAR(20) DEFAULT '营业中' COMMENT '营业状态（营业中/休息中）' AFTER business_hours;

-- 2. 为 food_info 表添加距离路线字段
ALTER TABLE food_info 
ADD COLUMN distance_to_route DECIMAL(10,2) DEFAULT 0.00 COMMENT '距离当前路线的距离（千米）' AFTER recommend_reason;

-- 3. 为已有数据设置默认值
UPDATE food_shop SET business_status = '营业中' WHERE business_status IS NULL;
UPDATE food_info SET distance_to_route = 0.00 WHERE distance_to_route IS NULL;

-- 完成
