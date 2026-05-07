-- 路线生成每日计数表
CREATE TABLE `route_generate_count` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` VARCHAR(50) NOT NULL COMMENT '用户ID',
  `generate_date` DATE NOT NULL COMMENT '生成日期',
  `count` INT NOT NULL DEFAULT 0 COMMENT '今日生成次数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `generate_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线生成每日计数表';
