DROP TABLE IF EXISTS `route_recommendation_detail`;
CREATE TABLE `route_recommendation_detail` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `recommendation_id` varchar(64) DEFAULT NULL COMMENT '推荐记录ID',
  `day_num` int DEFAULT NULL COMMENT '第几天',
  `sort_order` int DEFAULT NULL COMMENT '排序序号',
  `item_type` varchar(20) DEFAULT NULL COMMENT '项目类型: attraction/food/hotel/route',
  `name` varchar(200) DEFAULT NULL COMMENT '统一名称(景点名/店铺名/路线起点-终点)',
  `duration` int DEFAULT NULL COMMENT '耗时/游玩时长(分钟)',
  `cost` decimal(10, 2) DEFAULT NULL COMMENT '费用(门票/餐费/交通费)',
  `longitude` decimal(10, 6) DEFAULT NULL COMMENT '经度(点位中心或路线起点)',
  `latitude` decimal(10, 6) DEFAULT NULL COMMENT '纬度(点位中心或路线起点)',
  `extra_info` json DEFAULT NULL COMMENT '扩展信息(JSON格式，存储各类特有字段)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_recommendation_id` (`recommendation_id`),
  KEY `idx_day_sort` (`day_num`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线推荐详情表(JSON优化版)';
