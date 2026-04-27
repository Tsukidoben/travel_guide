CREATE DATABASE IF NOT EXISTS travel_guide CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE travel_guide;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attraction_collection
-- ----------------------------
DROP TABLE IF EXISTS `attraction_collection`;
CREATE TABLE `attraction_collection`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `attraction_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '景点id',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '景点收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attraction_collection
-- ----------------------------
INSERT INTO `attraction_collection` VALUES ('2016059259205652482', '1753000000000000006', '2016059219544313857', '2026-01-27 16:02:39');
INSERT INTO `attraction_collection` VALUES ('2016060955520602114', '1753000000000000012', '2016034476667363330', '2026-01-27 16:09:23');
INSERT INTO `attraction_collection` VALUES ('2016060967071715329', '1753000000000000009', '2016034476667363330', '2026-01-27 16:09:26');

-- ----------------------------
-- Table structure for attraction_comment
-- ----------------------------
DROP TABLE IF EXISTS `attraction_comment`;
CREATE TABLE `attraction_comment`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `attraction_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '景点id',
  `comment_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '评论详情',
  `picture_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '评论图片',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '景点评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attraction_comment
-- ----------------------------
INSERT INTO `attraction_comment` VALUES ('2016064090903941122', '1753000000000000015', '这也太好看了吧', '[{\"id\":\"2016047312296939521\",\"fileName\":\"漳州火山岛.png\",\"fileType\":\"png\",\"keyword\":null}]', '2016034476667363330', '张三', '2026-01-27 16:21:51');

-- ----------------------------
-- Table structure for attraction_info
-- ----------------------------
DROP TABLE IF EXISTS `attraction_info`;
CREATE TABLE `attraction_info`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `attraction_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '景点名称',
  `attraction_pic` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '景点图片',
  `type_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '景点分类',
  `attraction_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '景点简介',
  `attraction_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '景点描述',
  `attraction_place` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '景点位置',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '景点信息表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for attraction_order
-- ----------------------------
DROP TABLE IF EXISTS `attraction_order`;
CREATE TABLE `attraction_order`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `order_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单编码',
  `attraction_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '景点id',
  `ticket_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '门票id',
  `buy_count` int NULL DEFAULT NULL COMMENT '购买数量',
  `total_price` decimal(16, 2) NULL DEFAULT NULL COMMENT '订单价格',
  `attraction_shot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '景点快照',
  `ticket_shot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '门票快照',
  `order_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单备注',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购买用户',
  `captcha` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '核销码',
  `writeoff_time` datetime NULL DEFAULT NULL COMMENT '核销时间',
  `order_state` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单状态',
  `pay_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付流水号',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '景点订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attraction_order
-- ----------------------------
INSERT INTO `attraction_order` VALUES ('2016058702638288898', 'TICKET20260127001', '1753000000000000016', '1753000000000000040', 1, 65.00, '{\"id\":\"1753000000000000016\",\"attractionName\":\"河坑土楼群\",\"attractionPic\":\"[{\\\"id\\\":\\\"2016045077781483522\\\",\\\"fileName\\\":\\\"河坑土楼群1.png\\\",\\\"fileType\\\":\\\"png\\\",\\\"keyword\\\":null},{\\\"id\\\":\\\"2016045087403216898\\\",\\\"fileName\\\":\\\"河坑土楼群2.png\\\",\\\"fileType\\\":\\\"png\\\",\\\"keyword\\\":null},{\\\"id\\\":\\\"2016045096001540097\\\",\\\"fileName\\\":\\\"河坑土楼群3.png\\\",\\\"fileType\\\":\\\"png\\\",\\\"keyword\\\":null}]\",\"typeId\":\"1753000000000000004\",\"attractionDesc\":\"北斗七星布局，星空摄影与农耕文化体验地\",\"attractionPlace\":\"漳州市南靖县书洋镇\",\"creator\":\"1797549871140298753\",\"createName\":\"超级管理员\",\"createTime\":\"2026-01-27 00:00:00\"}', '{\"id\":\"1753000000000000040\",\"ticketName\":\"河坑土楼群观光票\",\"ticketPrice\":65.00,\"attractionId\":\"1753000000000000016\",\"useScope\":\"北斗七星观景台+14座土楼外观+农耕文化展示区\",\"status\":\"1\",\"creator\":\"1797549871140298753\",\"createName\":\"超级管理员\",\"createTime\":\"2026-01-27 00:00:00\"}', NULL, '2016034476667363330', '00000001', NULL, '20', '2026012722001490330507835079', '2026-01-27 16:00:41', '2016034476667363330', '张三', '2026-01-27 16:00:26');
INSERT INTO `attraction_order` VALUES ('2016059270488330241', 'TICKET20260127002', '1753000000000000006', '1753000000000000019', 3, 105.00, '{\"id\":\"1753000000000000006\",\"attractionName\":\"六鳌翡翠湾\",\"attractionPic\":\"[{\\\"id\\\":\\\"2016048294460329986\\\",\\\"fileName\\\":\\\"六鳌翡翠湾.png\\\",\\\"fileType\\\":\\\"png\\\",\\\"keyword\\\":null}]\",\"typeId\":\"1753000000000000001\",\"attractionDesc\":\"细腻沙滩与清澈海水交融的亲子天堂\",\"attractionPlace\":\"漳州市漳浦县六鳌镇\",\"creator\":\"1797549871140298753\",\"createName\":\"超级管理员\",\"createTime\":\"2026-01-25 00:00:00\"}', '{\"id\":\"1753000000000000019\",\"ticketName\":\"六鳌翡翠湾成人票\",\"ticketPrice\":35.00,\"attractionId\":\"1753000000000000006\",\"useScope\":\"沙滩区、彩虹栈道、观景台\",\"status\":\"1\",\"creator\":\"1797549871140298753\",\"createName\":\"超级管理员\",\"createTime\":\"2026-01-27 00:00:00\"}', NULL, '2016059219544313857', '00000002', NULL, '20', '2026012722001490330507841832', '2026-01-27 16:03:03', '2016059219544313857', '王五', '2026-01-27 16:02:41');
INSERT INTO `attraction_order` VALUES ('2016064437613498370', 'TICKET20260127003', '1753000000000000006', '1753000000000000019', 1, 35.00, '{\"id\":\"1753000000000000006\",\"attractionName\":\"六鳌翡翠湾\",\"attractionPic\":\"[{\\\"id\\\":\\\"2016048294460329986\\\",\\\"fileName\\\":\\\"六鳌翡翠湾.png\\\",\\\"fileType\\\":\\\"png\\\",\\\"keyword\\\":null}]\",\"typeId\":\"1753000000000000001\",\"attractionDesc\":\"细腻沙滩与清澈海水交融的亲子天堂\",\"attractionPlace\":\"漳州市漳浦县六鳌镇\",\"creator\":\"1797549871140298753\",\"createName\":\"超级管理员\",\"createTime\":\"2026-01-25 00:00:00\"}', '{\"id\":\"1753000000000000019\",\"ticketName\":\"六鳌翡翠湾成人票\",\"ticketPrice\":35.00,\"attractionId\":\"1753000000000000006\",\"useScope\":\"沙滩区、彩虹栈道、观景台\",\"status\":\"1\",\"creator\":\"1797549871140298753\",\"createName\":\"超级管理员\",\"createTime\":\"2026-01-27 00:00:00\"}', NULL, '2016034476667363330', NULL, NULL, '-1', NULL, NULL, '2016034476667363330', '张三', '2026-01-27 16:23:13');

-- ----------------------------
-- Table structure for attraction_type
-- ----------------------------
DROP TABLE IF EXISTS `attraction_type`;
CREATE TABLE `attraction_type`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `type_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类名称',
  `type_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类简介',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分类管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attraction_type
-- ----------------------------
INSERT INTO `attraction_type` VALUES ('1753000000000000001', '自然风光', '涵盖山川湖海、地质奇观等原生态景观，适合登山徒步、摄影采风', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `attraction_type` VALUES ('1753000000000000002', '人文历史', '聚焦古城街巷、历史遗迹、宗教建筑，承载闽南文化精髓', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `attraction_type` VALUES ('1753000000000000003', '滨海度假', '集合沙滩海岛、灯塔礁石、海上奇观，主打休闲亲水体验', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `attraction_type` VALUES ('1753000000000000004', '土楼文化', '世界文化遗产核心区，展现客家民居智慧与宗族文化', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');

-- ----------------------------
-- Table structure for carousel_image
-- ----------------------------
DROP TABLE IF EXISTS `carousel_image`;
CREATE TABLE `carousel_image`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `img_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '轮播图图片',
  `img_title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '轮播图标题',
  `sort_num` int NULL DEFAULT NULL COMMENT '排序',
  `status` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮播图管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of carousel_image
-- ----------------------------
INSERT INTO `carousel_image` VALUES ('2016039092409012225', '[{\"id\":\"2016040952763191298\",\"fileName\":\"banner1.jpg\",\"fileType\":\"jpg\",\"keyword\":null}]', '奔赴山海，即刻出发', 1, '1', '1797549871140298753', '系统管理员', '2026-01-27 14:42:30');
INSERT INTO `carousel_image` VALUES ('2016039129297915905', '[{\"id\":\"2016040974720372738\",\"fileName\":\"banner2.jpg\",\"fileType\":\"jpg\",\"keyword\":null}]', '暖心休憩，邂逅漳州', 2, '1', '1797549871140298753', '系统管理员', '2026-01-27 14:42:39');
INSERT INTO `carousel_image` VALUES ('2016039174810308609', '[{\"id\":\"2016040997126344705\",\"fileName\":\"banner3.jpg\",\"fileType\":\"jpg\",\"keyword\":null}]', '记录旅途，分享美好', 3, '1', '1797549871140298753', '系统管理员', '2026-01-27 14:42:50');

-- ----------------------------
-- Table structure for chat_record
-- ----------------------------
DROP TABLE IF EXISTS `chat_record`;
CREATE TABLE `chat_record`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'id',
  `room_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '聊天室id',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户id',
  `message_type` int NULL DEFAULT NULL COMMENT '消息类型（1 文字 2 图片）',
  `message_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `is_read` int NULL DEFAULT NULL COMMENT '是否已读，1是 0否',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_record
-- ----------------------------
INSERT INTO `chat_record` VALUES ('2016061884238663681', '2016061838625607681', '2016034476667363330', 1, '你好，今天晚上还有房间么\n', '2026-01-27 16:13:04', 1);
INSERT INTO `chat_record` VALUES ('2016062408732184578', '2016061838625607681', '2016050063315767297', 1, '您好，有的，可以直接预定哦', '2026-01-27 16:15:10', 1);

-- ----------------------------
-- Table structure for chat_room
-- ----------------------------
DROP TABLE IF EXISTS `chat_room`;
CREATE TABLE `chat_room`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'id',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户id',
  `hotel_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品id',
  `room_state` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态10正常 20 未激活',
  `start_time` datetime NULL DEFAULT NULL COMMENT '第一条消息的发起时间',
  `recent_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天室表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_room
-- ----------------------------
INSERT INTO `chat_room` VALUES ('2016061838625607681', '2016034476667363330', '1754000000000000005', '10', NULL, '2026-01-27 16:15:10', '2026-01-27 16:12:54', '2016034476667363330', '张三');

-- ----------------------------
-- Table structure for file_upload_records
-- ----------------------------
DROP TABLE IF EXISTS `file_upload_records`;
CREATE TABLE `file_upload_records`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'id',
  `file_name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文件名',
  `file_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_upload_time` datetime NULL DEFAULT NULL COMMENT '文件上传时间',
  `file_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文件保存路径',
  `file_size` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件大小',
  `file_upload_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件上传人',
  `file_upload_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件上传人姓名',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件上传类型',
  `file_upload_sys` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件上传系统',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件上传记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_upload_records
-- ----------------------------
INSERT INTO `file_upload_records` VALUES ('2016034476524756994', '张三.png', 'png', '2026-01-27 14:24:10', '/files/20260127/head_879e92801ac94a2194b8b020ce52fb72.png', '1735字节/0.00MB', '112.19.146.5', '112.19.146.5-神秘用户', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016040952763191298', 'banner1.jpg', 'jpg', '2026-01-27 14:49:54', '/files/20260127/d7fcf37241624bba88f599403277b395.jpg', '425689字节/0.41MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016040974720372738', 'banner2.jpg', 'jpg', '2026-01-27 14:49:59', '/files/20260127/43233b472cbf490f95483f6e830b2b67.jpg', '484469字节/0.46MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016040997126344705', 'banner3.jpg', 'jpg', '2026-01-27 14:50:05', '/files/20260127/2276b94010354e28b2a15ece1dbdd034.jpg', '494282字节/0.47MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016045077781483522', '河坑土楼群1.png', 'png', '2026-01-27 15:06:18', '/files/20260127/12b57449d9bb4fb083d3b612f3ce7366.png', '488965字节/0.47MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016045087403216898', '河坑土楼群2.png', 'png', '2026-01-27 15:06:20', '/files/20260127/205d077bf490485383230c2b4675d1fe.png', '469797字节/0.45MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016045096001540097', '河坑土楼群3.png', 'png', '2026-01-27 15:06:22', '/files/20260127/e8d738bc69e34451b0d4137e658586fb.png', '328523字节/0.31MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016045723012239362', '田螺坑土楼群.png', 'png', '2026-01-27 15:08:51', '/files/20260127/78b0fc2547dc40eda81c35191fd46b17.png', '354987字节/0.34MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016046012431798273', '云水谣古镇1.png', 'png', '2026-01-27 15:10:00', '/files/20260127/31366e745db84cd8a7c76c7069aa9276.png', '506514字节/0.48MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016046020233203713', '云水谣古镇2.png', 'png', '2026-01-27 15:10:02', '/files/20260127/b4ac98aec9a6413abb431f1e5a6c6cbe.png', '334976字节/0.32MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016046584606167041', '镇海角1.png', 'png', '2026-01-27 15:12:17', '/files/20260127/14cd758f8c6d47419a4d79a2fea6b7ef.png', '230178字节/0.22MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016046595507163138', '镇海角2.png', 'png', '2026-01-27 15:12:19', '/files/20260127/06d5d938a4324bc693de8fa30bb207ed.png', '193544字节/0.18MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016046604155817986', '镇海角3.png', 'png', '2026-01-27 15:12:21', '/files/20260127/65624f48af99476a91a29abe4b73845c.png', '257032字节/0.25MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016047312296939521', '漳州火山岛.png', 'png', '2026-01-27 15:15:10', '/files/20260127/e8fd81ddd6cb46d291e33878aecb517b.png', '309317字节/0.29MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016047354659409922', '东山岛.png', 'png', '2026-01-27 15:15:20', '/files/20260127/6d4891a269284d2d8295bc447ecd0883.png', '351061字节/0.33MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016047471567245314', '南炮台遗址.png', 'png', '2026-01-27 15:15:48', '/files/20260127/f233a69e556d4200a7a9a44a4d7fa350.png', '462521字节/0.44MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016047627419193345', '漳州文庙1.png', 'png', '2026-01-27 15:16:25', '/files/20260127/cf16c0f3c255471a84a0aabae070ae71.png', '290285字节/0.28MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016047635782635521', '漳州文庙2.png', 'png', '2026-01-27 15:16:27', '/files/20260127/1247e493be914abb917ecb48c2d17887.png', '376867字节/0.36MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016048037441769473', '漳州古城.png', 'png', '2026-01-27 15:18:03', '/files/20260127/57c8770040484baba36f568db5b0cfc0.png', '458185字节/0.44MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016048184179494914', '香山湿地公园1.png', 'png', '2026-01-27 15:18:38', '/files/20260127/5e12b293cbf64e6198057da734d23a1c.png', '276872字节/0.26MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016048294460329986', '六鳌翡翠湾.png', 'png', '2026-01-27 15:19:04', '/files/20260127/274bfef759184f57a5da5e0a8e35caba.png', '232205字节/0.22MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016048403273158657', '云洞岩.png', 'png', '2026-01-27 15:19:30', '/files/20260127/4c83991fedfa432b861aa7a3eccfc09d.png', '270083字节/0.26MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016050063265435650', '刘云鹏.png', 'png', '2026-01-27 15:26:06', '/files/20260127/head_36ec9dd64ae24716b6a30d4139c26877.png', '2132字节/0.00MB', '1797549871140298753', '系统管理员', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016051782204461058', '云水谣溪畔民宿.png', 'png', '2026-01-27 15:32:56', '/files/20260127/4b03e8d4527f469bbf5bb9e8cc436527.png', '441703字节/0.42MB', '2016050063315767297', '刘云鹏', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016052165693870082', '东山海景度假酒店.png', 'png', '2026-01-27 15:34:27', '/files/20260127/100e6e0dcad34899b6cefd0f5508d29c.png', '414675字节/0.40MB', '2016050063315767297', '刘云鹏', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016052493050908674', '漳州古城 boutique 酒店.jpg', 'jpg', '2026-01-27 15:35:45', '/files/20260127/93d34f312c0f46e69ad934af6d4c9dad.jpg', '304302字节/0.29MB', '2016050063315767297', '刘云鹏', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016053420440883202', '六鳌翡翠湾房车营地.jpg', 'jpg', '2026-01-27 15:39:27', '/files/20260127/a11aa92d901d49ab974f265818573bbc.jpg', '233990字节/0.22MB', '2016050063315767297', '刘云鹏', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016054337340903425', '火山岛设计师酒店.jpg', 'jpg', '2026-01-27 15:43:05', '/files/20260127/5a50ed21899a41d9af65fb78f2a35a4c.jpg', '313105字节/0.30MB', '2016050063315767297', '刘云鹏', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016054367942545410', '镇海角灯塔精品客栈.jpg', 'jpg', '2026-01-27 15:43:12', '/files/20260127/a779da9fab754336a8192df58234b6e5.jpg', '294587字节/0.28MB', '2016050063315767297', '刘云鹏', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016054410044968962', '南炮台海防文化酒店.jpg', 'jpg', '2026-01-27 15:43:22', '/files/20260127/34309275f97b468e8c015a47490e5a27.jpg', '338869字节/0.32MB', '2016050063315767297', '刘云鹏', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016054440021659649', '香山湿地生态木屋.jpg', 'jpg', '2026-01-27 15:43:30', '/files/20260127/7b2993354a774ff584fe1ce3d0c1824f.jpg', '269510字节/0.26MB', '2016050063315767297', '刘云鹏', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016054558401695746', '河坑星空土楼客栈.jpg', 'jpg', '2026-01-27 15:43:58', '/files/20260127/da743a2ecd4243149276af5e3f6e2552.jpg', '252180字节/0.24MB', '2016050063315767297', '刘云鹏', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016054754032422913', '大床.png', 'png', '2026-01-27 15:44:45', '/files/20260127/b696f88c2679414b8662ce7d5399b96a.png', '238708字节/0.23MB', '2016050063315767297', '刘云鹏', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016059219523342337', '王五.png', 'png', '2026-01-27 16:02:29', '/files/20260127/head_eaf25e59083e4a85ba20a79c86e2fb93.png', '1368字节/0.00MB', '112.19.146.5', '112.19.146.5-神秘用户', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016059750484480001', '谢部楼.png', 'png', '2026-01-27 16:04:36', '/files/20260127/head_9472e967b0884908aa54238295fb0c5e.png', '2692字节/0.00MB', '112.19.146.5', '112.19.146.5-神秘用户', 'local', 'travelGuide');
INSERT INTO `file_upload_records` VALUES ('2016059856654897153', '沙箱.png', 'png', '2026-01-27 16:05:01', '/files/20260127/head_c4f671ecd7514d1e842215be35d25857.png', '2416字节/0.00MB', '112.19.146.5', '112.19.146.5-神秘用户', 'local', 'travelGuide');

-- ----------------------------
-- Table structure for hotel_info
-- ----------------------------
DROP TABLE IF EXISTS `hotel_info`;
CREATE TABLE `hotel_info`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `htoel_pic` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '酒店图片',
  `htoel_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '酒店名称',
  `hotel_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '酒店介绍',
  `hotel_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '酒店描述',
  `hotel_service` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '酒店服务',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '酒店管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel_info
-- ----------------------------
INSERT INTO `hotel_info` VALUES ('1754000000000000001', '[{\"id\":\"2016052165693870082\",\"fileName\":\"东山海景度假酒店.png\",\"fileType\":\"png\",\"keyword\":null}]', '东山海景度假酒店', '南门湾一线海景，步行3分钟至沙滩', '<p>坐落于东山岛南门湾核心位置，270°全景落地窗直面台湾海峡。酒店由闽南古厝改造而成，融合现代设计与渔村文化。大堂设观海茶吧，顶楼无边泳池可眺望风动石。所有客房配智能客控、戴森吹风机、定制海洋香氛。提供免费接驳车往返铜陵镇中心，夜间有星空电影放映。</p>', '免费WiFi, 海景泳池, 接送服务, 行李寄存, 海鲜餐厅, 婚礼策划', '2016050063315767297', '刘云鹏', '2026-01-27 00:00:00');
INSERT INTO `hotel_info` VALUES ('1754000000000000002', '[{\"id\":\"2016051782204461058\",\"fileName\":\"云水谣溪畔民宿.png\",\"fileType\":\"png\",\"keyword\":null}]', '云水谣溪畔民宿', '千年古榕旁，推窗见溪流', '<p>位于云水谣古镇核心区，紧邻“夫妻榕”与鹅卵石古道。6间独栋土楼客房保留夯土墙、木梁结构，内部配备地暖、乳胶床垫、独立泡池。庭院提供客家擂茶体验，清晨可参与摏糍粑活动。无电梯设计保持原生态氛围，适合追求慢生活的旅人。</p>', '免费早餐, 擂茶体验, 古装租赁, 洗衣服务, 停车场', '2016050063315767297', '刘云鹏', '2026-01-27 00:00:00');
INSERT INTO `hotel_info` VALUES ('1754000000000000003', '[{\"id\":\"2016052493050908674\",\"fileName\":\"漳州古城 boutique 酒店.jpg\",\"fileType\":\"jpg\",\"keyword\":null}]', '漳州古城 boutique 酒店', '明清骑楼里的轻奢居所', '<p>藏身于漳州古城台湾路百年骑楼内，由民国商号改建。12间客房以“漳绣”“木偶”“水仙”为主题，配备智能马桶、迷你吧、BOSE音响。顶楼露台可俯瞰文庙飞檐，夜间提供闽南语歌仔戏私享演出。步行5分钟达青年路美食街，四果汤、豆花粉丝即点即送。</p>', '管家服务, 露台酒吧, 文化导览, 免费停车, 快速入住', '2016050063315767297', '刘云鹏', '2026-01-27 00:00:00');
INSERT INTO `hotel_info` VALUES ('1754000000000000004', '[{\"id\":\"2016053420440883202\",\"fileName\":\"六鳌翡翠湾房车营地.jpg\",\"fileType\":\"jpg\",\"keyword\":null}]', '六鳌翡翠湾房车营地', '沙滩上的移动别墅', '<p>福建首个滨海房车营地，30辆美式拖挂房车沿翡翠湾一字排开。每车含独立卫浴、空调、小厨房，外接露台配烧烤架。营地提供赶海工具租赁、风筝DIY、篝火晚会。距抽象画廊1公里，可预约电瓶车接送。适合亲子家庭与年轻群体，宠物友好（需报备）。</p>', '房车住宿, 烧烤设备, 赶海工具, 宠物友好, 24小时安保', '2016050063315767297', '刘云鹏', '2026-01-27 00:00:00');
INSERT INTO `hotel_info` VALUES ('1754000000000000005', '[{\"id\":\"2016045723012239362\",\"fileName\":\"田螺坑土楼群.png\",\"fileType\":\"png\",\"keyword\":null}]', '田螺坑土楼观景度假村', '“四菜一汤”最佳观景点', '<p>位于田螺坑上观景台入口处，10栋独立木屋依山而建。房间均设观景阳台，晨起可观云雾缭绕的土楼群。提供客家家宴（簸箕宴）、土楼夜宿故事会、梯田摄影向导。冬季开放地暖，夏季配防蚊纱帐。无信号区域，倡导“数字排毒”旅行理念。</p>', '观景阳台, 家宴预订, 摄影向导, 免费停车, 无烟楼层', '2016050063315767297', '刘云鹏', '2026-01-27 00:00:00');
INSERT INTO `hotel_info` VALUES ('1754000000000000006', '[{\"id\":\"2016054337340903425\",\"fileName\":\"火山岛设计师酒店.jpg\",\"fileType\":\"jpg\",\"keyword\":null}]', '火山岛设计师酒店', 'INS风网红打卡住宿', '<p>毗邻火山岛纪念碑谷，建筑外观为粉白几何体，内部充满艺术装置。30间客房分“玄武岩”“彩虹山”“海誓”主题，配浴缸投影、蓝牙音箱、定制洗护。酒店设网红咖啡厅、屋顶打卡平台、旅拍服务站。住客享火山岛门票8折，提供无人机航拍协助。</p>', '网红打卡点, 旅拍服务, 咖啡厅, 门票折扣, 行李寄存', '2016050063315767297', '刘云鹏', '2026-01-27 00:00:00');
INSERT INTO `hotel_info` VALUES ('1754000000000000007', '[{\"id\":\"2016054367942545410\",\"fileName\":\"镇海角灯塔精品客栈.jpg\",\"fileType\":\"jpg\",\"keyword\":null}]', '镇海角灯塔精品客栈', '悬崖灯塔下的浪漫栖居', '<p>距镇海角灯塔仅200米，6间海景房均带私人露台。房间以航海元素装饰，配望远镜、星空图、红酒套装。提供日出叫醒服务、悬崖晚餐预订、摄影黄金时段引导。因地处岬角，网络信号较弱，但Wi-Fi覆盖公共区。适合情侣蜜月与摄影爱好者。</p>', '日出叫醒, 悬崖晚餐, 摄影引导, 免费停车, 情侣布置', '2016050063315767297', '刘云鹏', '2026-01-27 00:00:00');
INSERT INTO `hotel_info` VALUES ('1754000000000000008', '[{\"id\":\"2016054410044968962\",\"fileName\":\"南炮台海防文化酒店.jpg\",\"fileType\":\"jpg\",\"keyword\":null}]', '南炮台海防文化酒店', '清代炮台旁的历史主题酒店', '<p>由民国海关旧址改造，融合海防文化元素。客房命名“克虏伯”“胡里山”“屿仔尾”，墙面展示老照片与海图。酒店设军事主题餐厅（供应“炮台炣饭”）、小型海防博物馆。步行5分钟达南炮台遗址，提供历史讲解器租赁。适合研学团队与历史爱好者。</p>', '文化导览, 主题餐厅, 博物馆, 团队接待, 免费停车', '2016050063315767297', '刘云鹏', '2026-01-27 00:00:00');
INSERT INTO `hotel_info` VALUES ('1754000000000000009', '[{\"id\":\"2016054440021659649\",\"fileName\":\"香山湿地生态木屋.jpg\",\"fileType\":\"jpg\",\"keyword\":null}]', '香山湿地生态木屋', '湿地公园内的静谧居所', '<p>建于香山湿地木栈道旁，12栋原木小屋悬浮水面。采用环保建材，配太阳能热水、雨水回收系统。房间设观鸟窗、生态科普手册，提供望远镜租赁。清晨可预约湿地导览员带观白鹭，夜间组织萤火虫观察（季节性）。无电视设计，倡导自然疗愈。</p>', '生态导览, 望远镜租赁, 萤火虫观察, 环保设施, 免费停车', '2016050063315767297', '刘云鹏', '2026-01-27 00:00:00');
INSERT INTO `hotel_info` VALUES ('1754000000000000010', '[{\"id\":\"2016054558401695746\",\"fileName\":\"河坑星空土楼客栈.jpg\",\"fileType\":\"jpg\",\"keyword\":null}]', '河坑星空土楼客栈', '北斗七星下的观星胜地', '<p>位于河坑土楼群核心区，由废弃土楼改造的特色客栈。8间客房保留土楼圆环结构，屋顶设透明观星穹顶。提供银河拍摄教学、客家山歌夜话、稻田瑜伽课程。夏季开放天台露营，配防蚊装备与热姜茶。光污染极低，是天文爱好者理想基地。</p>', '观星穹顶, 天文教学, 稻田瑜伽, 露营装备, 免费停车', '2016050063315767297', '刘云鹏', '2026-01-27 00:00:00');

-- ----------------------------
-- Table structure for hotel_order
-- ----------------------------
DROP TABLE IF EXISTS `hotel_order`;
CREATE TABLE `hotel_order`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `order_state` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单状态',
  `hotel_shot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '酒店快照',
  `hotel_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '酒店id',
  `total_price` decimal(16, 2) NULL DEFAULT NULL COMMENT '订单价格',
  `room_shot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房间快照',
  `room_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '房间id',
  `order_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单编码',
  `order_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单备注',
  `captcha` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '核销码',
  `writeoff_time` datetime NULL DEFAULT NULL COMMENT '核销时间',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购买用户',
  `buy_count` int NULL DEFAULT NULL COMMENT '购买数量',
  `days` int NULL DEFAULT NULL COMMENT '天数',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `pay_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付流水号',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '酒店订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel_order
-- ----------------------------
INSERT INTO `hotel_order` VALUES ('2016055732651626498', '-1', '{\"id\":\"1754000000000000001\",\"htoelPic\":\"[{\\\"id\\\":\\\"2016052165693870082\\\",\\\"fileName\\\":\\\"东山海景度假酒店.png\\\",\\\"fileType\\\":\\\"png\\\",\\\"keyword\\\":null}]\",\"htoelName\":\"东山海景度假酒店\",\"hotelDesc\":\"南门湾一线海景，步行3分钟至沙滩\",\"hotelService\":\"免费WiFi, 海景泳池, 接送服务, 行李寄存, 海鲜餐厅, 婚礼策划\",\"creator\":\"2016050063315767297\",\"createName\":\"刘云鹏\",\"createTime\":\"2026-01-27 00:00:00\"}', '1754000000000000001', 1376.00, '{\"id\":\"1754000000000000011\",\"hotelId\":\"1754000000000000001\",\"roomPic\":\"[{\\\"id\\\":\\\"2016054754032422913\\\",\\\"fileName\\\":\\\"大床.png\\\",\\\"fileType\\\":\\\"png\\\",\\\"keyword\\\":null}]\",\"roomName\":\"豪华海景大床房\",\"roomDesc\":\"270°落地窗，1.8m大床，智能客控\",\"price\":688.00,\"roomCount\":8,\"peopleCount\":2,\"roomFloor\":3}', '1754000000000000011', 'HOTEL20260127001', NULL, NULL, NULL, '2016034476667363330', 1, 2, '2016034476667363330', '张三', '2026-01-27 15:48:38', NULL, NULL, '2026-01-27', '2026-01-29');
INSERT INTO `hotel_order` VALUES ('2016056333020106753', '80', '{\"id\":\"1754000000000000007\",\"htoelPic\":\"[{\\\"id\\\":\\\"2016054367942545410\\\",\\\"fileName\\\":\\\"镇海角灯塔精品客栈.jpg\\\",\\\"fileType\\\":\\\"jpg\\\",\\\"keyword\\\":null}]\",\"htoelName\":\"镇海角灯塔精品客栈\",\"hotelDesc\":\"悬崖灯塔下的浪漫栖居\",\"hotelService\":\"日出叫醒, 悬崖晚餐, 摄影引导, 免费停车, 情侣布置\",\"creator\":\"2016050063315767297\",\"createName\":\"刘云鹏\",\"createTime\":\"2026-01-27 00:00:00\"}', '1754000000000000007', 620.00, '{\"id\":\"1754000000000000027\",\"hotelId\":\"1754000000000000007\",\"roomPic\":\"[{\\\"id\\\":\\\"2016054754032422913\\\",\\\"fileName\\\":\\\"大床.png\\\",\\\"fileType\\\":\\\"png\\\",\\\"keyword\\\":null}]\",\"roomName\":\"悬崖观星双床房\",\"roomDesc\":\"双1.35m床，星空图天花板\",\"price\":620.00,\"roomCount\":2,\"peopleCount\":2,\"roomFloor\":2}', '1754000000000000027', 'HOTEL20260127002', NULL, '00000001', '2026-01-27 15:54:23', '2016034476667363330', 1, 1, '2016034476667363330', '张三', '2026-01-27 15:51:01', '2026012722001490330507840759', '2026-01-27 15:51:45', '2026-01-27', '2026-01-28');
INSERT INTO `hotel_order` VALUES ('2016056602600607745', '10', '{\"id\":\"1754000000000000007\",\"htoelPic\":\"[{\\\"id\\\":\\\"2016054367942545410\\\",\\\"fileName\\\":\\\"镇海角灯塔精品客栈.jpg\\\",\\\"fileType\\\":\\\"jpg\\\",\\\"keyword\\\":null}]\",\"htoelName\":\"镇海角灯塔精品客栈\",\"hotelDesc\":\"悬崖灯塔下的浪漫栖居\",\"hotelService\":\"日出叫醒, 悬崖晚餐, 摄影引导, 免费停车, 情侣布置\",\"creator\":\"2016050063315767297\",\"createName\":\"刘云鹏\",\"createTime\":\"2026-01-27 00:00:00\"}', '1754000000000000007', 580.00, '{\"id\":\"1754000000000000026\",\"hotelId\":\"1754000000000000007\",\"roomPic\":\"[{\\\"id\\\":\\\"2016054754032422913\\\",\\\"fileName\\\":\\\"大床.png\\\",\\\"fileType\\\":\\\"png\\\",\\\"keyword\\\":null}]\",\"roomName\":\"灯塔海景大床房\",\"roomDesc\":\"私人露台，望远镜，红酒\",\"price\":580.00,\"roomCount\":4,\"peopleCount\":2,\"roomFloor\":2}', '1754000000000000026', 'HOTEL20260127003', NULL, NULL, NULL, '2016034476667363330', 1, 1, '2016034476667363330', '张三', '2026-01-27 15:52:05', NULL, NULL, '2026-01-27', '2026-01-28');
INSERT INTO `hotel_order` VALUES ('2016056954666291201', '20', '{\"id\":\"1754000000000000007\",\"htoelPic\":\"[{\\\"id\\\":\\\"2016054367942545410\\\",\\\"fileName\\\":\\\"镇海角灯塔精品客栈.jpg\\\",\\\"fileType\\\":\\\"jpg\\\",\\\"keyword\\\":null}]\",\"htoelName\":\"镇海角灯塔精品客栈\",\"hotelDesc\":\"悬崖灯塔下的浪漫栖居\",\"hotelService\":\"日出叫醒, 悬崖晚餐, 摄影引导, 免费停车, 情侣布置\",\"creator\":\"2016050063315767297\",\"createName\":\"刘云鹏\",\"createTime\":\"2026-01-27 00:00:00\"}', '1754000000000000007', 580.00, '{\"id\":\"1754000000000000026\",\"hotelId\":\"1754000000000000007\",\"roomPic\":\"[{\\\"id\\\":\\\"2016054754032422913\\\",\\\"fileName\\\":\\\"大床.png\\\",\\\"fileType\\\":\\\"png\\\",\\\"keyword\\\":null}]\",\"roomName\":\"灯塔海景大床房\",\"roomDesc\":\"私人露台，望远镜，红酒\",\"price\":580.00,\"roomCount\":4,\"peopleCount\":2,\"roomFloor\":2}', '1754000000000000026', 'HOTEL20260127004', NULL, '00000002', NULL, '2016034476667363330', 1, 1, '2016034476667363330', '张三', '2026-01-27 15:53:29', '2026012722001490330507835077', '2026-01-27 15:53:44', '2026-01-27', '2026-01-28');
INSERT INTO `hotel_order` VALUES ('2016057245922955266', '20', '{\"id\":\"1754000000000000003\",\"htoelPic\":\"[{\\\"id\\\":\\\"2016052493050908674\\\",\\\"fileName\\\":\\\"漳州古城 boutique 酒店.jpg\\\",\\\"fileType\\\":\\\"jpg\\\",\\\"keyword\\\":null}]\",\"htoelName\":\"漳州古城 boutique 酒店\",\"hotelDesc\":\"明清骑楼里的轻奢居所\",\"hotelService\":\"管家服务, 露台酒吧, 文化导览, 免费停车, 快速入住\",\"creator\":\"2016050063315767297\",\"createName\":\"刘云鹏\",\"createTime\":\"2026-01-27 00:00:00\"}', '1754000000000000003', 498.00, '{\"id\":\"1754000000000000016\",\"hotelId\":\"1754000000000000003\",\"roomPic\":\"[{\\\"id\\\":\\\"2016054754032422913\\\",\\\"fileName\\\":\\\"大床.png\\\",\\\"fileType\\\":\\\"png\\\",\\\"keyword\\\":null}]\",\"roomName\":\"水仙主题大床房\",\"roomDesc\":\"手绘水仙壁画，智能卫浴\",\"price\":498.00,\"roomCount\":4,\"peopleCount\":2,\"roomFloor\":2}', '1754000000000000016', 'HOTEL20260127005', NULL, '00000003', NULL, '2016034476667363330', 1, 1, '2016034476667363330', '张三', '2026-01-27 15:54:39', '2026012722001490330507835078', '2026-01-27 15:54:53', '2026-01-27', '2026-01-28');

-- ----------------------------
-- Table structure for hotel_room
-- ----------------------------
DROP TABLE IF EXISTS `hotel_room`;
CREATE TABLE `hotel_room`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `hotel_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属酒店',
  `room_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '房间名称',
  `room_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '房间介绍',
  `price` decimal(16, 2) NULL DEFAULT NULL COMMENT '房间价格',
  `room_count` int NULL DEFAULT NULL COMMENT '房间数量',
  `people_count` int NULL DEFAULT NULL COMMENT '可住人数',
  `room_floor` int NULL DEFAULT NULL COMMENT '房间楼层',
  `room_pic` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房间图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '房间管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel_room
-- ----------------------------
INSERT INTO `hotel_room` VALUES ('1754000000000000011', '1754000000000000001', '豪华海景大床房', '270°落地窗，1.8m大床，智能客控', 688.00, 8, 2, 3, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000012', '1754000000000000001', '家庭海景套房', '两卧室+客厅，儿童帐篷，海景浴缸', 1288.00, 3, 4, 4, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000013', '1754000000000000001', '无边泳池景观房', '直通顶楼泳池，日落view绝佳', 888.00, 5, 2, 5, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000014', '1754000000000000002', '土楼溪景大床房', '夯土墙+溪流view，独立泡池', 520.00, 3, 2, 1, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000015', '1754000000000000002', '古榕庭院双床房', '私密小院，双1.5m床', 580.00, 2, 2, 1, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000016', '1754000000000000003', '水仙主题大床房', '手绘水仙壁画，智能卫浴', 498.00, 4, 2, 2, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000017', '1754000000000000003', '木偶艺术套房', '提线木偶装饰，BOSE音响，露台', 788.00, 2, 2, 3, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000018', '1754000000000000003', '骑楼观景双床房', '临街骑楼view，双1.35m床', 568.00, 3, 2, 2, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000019', '1754000000000000004', '标准房车（2人）', '1.5m床，独立卫浴，小厨房', 398.00, 20, 2, 0, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000020', '1754000000000000004', '家庭房车（4人）', '上下铺+1.8m床，户外露台', 688.00, 8, 4, 0, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000021', '1754000000000000005', '观景木屋大床房', '全景阳台，“四菜一汤”view', 620.00, 6, 2, 1, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000022', '1754000000000000005', '梯田景观双床房', '双1.35m床，晨雾view', 650.00, 4, 2, 1, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000023', '1754000000000000006', '玄武岩主题大床房', '黑岩纹理墙，浴缸投影', 720.00, 10, 2, 2, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000024', '1754000000000000006', '彩虹山双床房', '彩色玻璃窗，双1.35m床', 780.00, 12, 2, 2, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000025', '1754000000000000006', '海誓蜜月套房', '心形浴缸，露台打卡点', 1088.00, 5, 2, 3, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000026', '1754000000000000007', '灯塔海景大床房', '私人露台，望远镜，红酒', 580.00, 4, 2, 2, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000027', '1754000000000000007', '悬崖观星双床房', '双1.35m床，星空图天花板', 620.00, 2, 2, 2, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000028', '1754000000000000008', '克虏伯历史大床房', '海防主题装饰，1.8m床', 368.00, 15, 2, 2, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000029', '1754000000000000009', '湿地观鸟木屋', '悬浮水面，观鸟窗，环保卫浴', 420.00, 8, 2, 1, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000030', '1754000000000000009', '萤火虫生态套房', '私密庭院，夏季萤火虫view', 520.00, 4, 2, 1, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000031', '1754000000000000010', '北斗观星大床房', '透明穹顶，银河直view', 580.00, 5, 2, 2, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000032', '1754000000000000010', '稻田瑜伽双床房', '双1.35m床，晨练露台', 620.00, 3, 2, 2, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');
INSERT INTO `hotel_room` VALUES ('1754000000000000033', '1754000000000000010', '土楼天台露营房', '含露营装备，夏季开放', 480.00, 6, 2, 3, '[{\"id\":\"2016054754032422913\",\"fileName\":\"大床.png\",\"fileType\":\"png\",\"keyword\":null}]');

-- ----------------------------
-- Table structure for hu_ai_result
-- ----------------------------
DROP TABLE IF EXISTS `hu_ai_result`;
CREATE TABLE `hu_ai_result`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对话用户id',
  `ai_result` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'ai的对话结果',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `user_ques` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户的提问',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ai对话结果表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of hu_ai_result
-- ----------------------------
INSERT INTO `hu_ai_result` VALUES ('2016071224962265090', '1797549871140298753', '根据您的时间，我为您推荐以下一日游方案：\n\n**上午：文化探索**\n*   **漳州古城**（约2-3小时）：漫步古街，欣赏骑楼建筑，品尝当地小吃，感受闽南风情。\n*   **漳州文庙**（约1小时）：参观宋代古建筑，了解儒家文化与历史。\n\n**下午：自然风光**\n*   **云洞岩**（约1.5-2小时）：登山观奇岩、洞穴和摩崖石刻，登顶俯瞰城区全景。\n*   **香山湿地公园**（约1-2小时）：沿木栈道散步，观鸟赏景，放松身心。\n\n**备选方案：**\n*   若偏爱海岛，可全天游览**东山岛**，体验海湾、风动石和渔村文化。\n*   若对地质感兴趣，可前往**漳州火山岛**，观赏独特的玄武岩地貌。\n\n**温馨提示：**\n以上景点均在漳州市区或近郊，交通便利。请根据体力合理安排，穿舒适的鞋子。', '2026-01-27 16:49:45', '我想玩一天，可以如何游玩');
INSERT INTO `hu_ai_result` VALUES ('2016071312228954113', '1797549871140298753', '漳州古城街区本身免费开放。若想深度体验，推荐购买文化体验联票（58元/人），可参观文庙、木偶艺术馆等收费场馆。两位成人总费用为116元。', '2026-01-27 16:50:25', '漳州古城门票多少，我们2个大人');
INSERT INTO `hu_ai_result` VALUES ('2016071524934692866', '1797549871140298753', '推荐以下两家适合观星的住宿：\n\n**河坑星空土楼客栈**（河坑土楼群核心区）\n- 客房保留土楼圆环结构，屋顶设**透明观星穹顶**，可直接观赏银河。\n- 提供银河拍摄教学、天台露营（夏季开放）等服务。\n- 光污染极低，适合天文爱好者。\n\n**镇海角灯塔精品客栈**（距灯塔200米）\n- 海景房带私人露台，配有望远镜和星空图。\n- 提供日出叫醒、摄影引导服务，适合情侣或摄影爱好者。\n- 位于岬角，视野开阔，但网络信号较弱。\n\n两家均能提供独特的观星体验，可根据行程偏好选择。', '2026-01-27 16:51:12', '晚上想看星星可以住哪');
INSERT INTO `hu_ai_result` VALUES ('2016074280454008834', '1797549871140298753', '云洞岩是漳州代表性的丹霞地貌景区，集奇岩、洞穴与摩崖石刻于一体。建议游览1.5—2小时，以登山为主。\n\n**核心看点：**\n- **天然洞穴**：如千人洞、一线天、通天洞。\n- **摩崖石刻**朱熹：包括题刻“溪山第一”等历史遗迹。\n- **山顶视野**：登顶可俯瞰漳州城区全景。\n\n**游览提示：**\n- 部分路段狭窄陡峭，建议穿防滑运动鞋。\n- 洞内湿滑，注意安全。\n- 适合登山健身、历史文化爱好者及短途游。', '2026-01-27 17:02:11', '云洞岩怎么玩合适');
INSERT INTO `hu_ai_result` VALUES ('2016074607366451202', '1797549871140298753', '东山海景度假酒店提供以下服务：免费WiFi、海景泳池、接送服务、行李寄存、海鲜餐厅以及婚礼策划。酒店还设有观海茶吧、顶楼无边泳池，并提供免费接驳车往返铜陵镇中心，夜间有星空电影放映。', '2026-01-27 17:03:31', '东山海景度假酒店有什么服务');
INSERT INTO `hu_ai_result` VALUES ('2016074841123401730', '1797549871140298753', '东山海景度假酒店位于东山岛南门湾核心位置，拥有**一线海景**，步行仅3分钟即可到达沙滩。酒店由闽南古厝改造，融合现代设计与渔村文化，设有**270°全景落地窗**直面台湾海峡，顶楼还有**无边泳池**可眺望风动石。客房配备智能客控、戴森吹风机等高品质设施，并提供免费接驳车往返铜陵镇中心，夜间有星空电影放映，服务贴心周到。', '2026-01-27 17:04:24', '东山海景度假酒店有什么优点么');
INSERT INTO `hu_ai_result` VALUES ('2016075882598445058', '1797549871140298753', '东山海景度假酒店有3种房型可选，适合9人入住。推荐组合如下：\n\n**方案一（兼顾游泳需求）**\n- **家庭海景套房**：1间，可住4人，含海景浴缸。\n- **豪华海景大床房**：2间，可住4人。\n- **无边泳池景观房**：1间，可住2人，直通顶楼泳池，满足游泳需求。\n\n**方案二（经济型）**\n- **豪华海景大床房**：4间，可住8人。\n- **无边泳池景观房**：1间，可住2人（其中1人可与其他房间调配），确保3人能使用泳池。\n\n酒店顶楼有无边泳池，所有住客均可使用。建议提前预订，因家庭套房仅3间、泳池景观房仅5间。', '2026-01-27 17:08:28', '东山海景度假酒店我们有9个人怎么住呢，其中3个想游泳');

-- ----------------------------
-- Table structure for ticket_info
-- ----------------------------
DROP TABLE IF EXISTS `ticket_info`;
CREATE TABLE `ticket_info`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `ticket_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '门票名称',
  `ticket_price` decimal(16, 2) NULL DEFAULT NULL COMMENT '门票价格',
  `attraction_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属景点',
  `use_scope` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '使用范围',
  `status` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '门票状态',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '门票管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ticket_info
-- ----------------------------
INSERT INTO `ticket_info` VALUES ('1753000000000000017', '云洞岩景区成人票', 20.00, '1753000000000000005', '全景区（含千人洞、一线天、摩崖石刻区）', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000018', '云洞岩文化导览票', 45.00, '1753000000000000005', '含专业讲解+石刻拓片体验（限1小时，10人内）', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000019', '六鳌翡翠湾成人票', 35.00, '1753000000000000006', '沙滩区、彩虹栈道、观景台', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000020', '家庭套票（2大1小）', 85.00, '1753000000000000006', '含儿童戏水区优先入场+挖沙工具套装', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000021', '星空露营体验票', 198.00, '1753000000000000006', '指定区域露营过夜（18:00-次日9:00），含帐篷+防潮垫', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000022', '香山湿地生态观光票', 25.00, '1753000000000000007', '全园木栈道、观鸟台、科普馆（含电子导览）', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000023', '湿地观鸟摄影许可票', 60.00, '1753000000000000007', '含专业观鸟点开放+三脚架使用+鸟类图鉴手册', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000024', '漳州古城文化体验联票', 58.00, '1753000000000000008', '含文庙快速通道+木偶艺术馆+古城记忆馆+骑楼导览', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000025', '古城非遗手作体验票', 88.00, '1753000000000000008', '含面煎粿制作+木偶彩绘+闽南语歌仔戏互动', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000026', '漳州文庙文化参观票', 30.00, '1753000000000000009', '大成殿、东西庑、泮池、科举文物展', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000027', '南炮台海防历史参观票', 25.00, '1753000000000000010', '炮台遗址、克虏伯大炮、陈列馆、观景平台', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000028', '东山岛风动石景区成人票', 50.00, '1753000000000000011', '风动石、关帝庙、铜山古城、海滨栈道', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000029', '东山岛学生优惠票', 30.00, '1753000000000000011', '凭有效学生证，同成人票权益', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000030', '东山环岛深度游套票', 128.00, '1753000000000000011', '含风动石+苏峰山环岛路接驳+南门湾导览+海鲜小吃券', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000031', '火山岛自然地质票', 65.00, '1753000000000000012', '玄武岩海岸、地质博物馆、基础观景台', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000032', 'INS网红打卡套票', 138.00, '1753000000000000012', '纪念碑谷+彩虹山+海誓山盟台+专业摄影点位', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000033', '火山岛夜场光影票', 98.00, '1753000000000000012', '18:00后入园，含灯光秀+星空观测+热饮一份', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000034', '镇海角观景台门票', 20.00, '1753000000000000013', '灯塔观景台、悬崖步道、风车平台', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000035', '镇海角摄影创作票', 60.00, '1753000000000000013', '含黄金时段（日出/日落）优先拍摄权+三脚架许可', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000036', '云水谣古镇标准联票', 95.00, '1753000000000000014', '古镇核心区+和贵楼+怀远楼+千年古榕栈道', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000037', '云水谣竹筏漂流附加票', 55.00, '1753000000000000014', '需持联票购买，单程15分钟溪流体验（含救生衣）', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000038', '田螺坑土楼群成人票', 75.00, '1753000000000000015', '上下观景台+步云楼内部参观+土楼文化讲解', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000039', '田螺坑学生票', 40.00, '1753000000000000015', '凭有效学生证，同成人票权益', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000040', '河坑土楼群观光票', 65.00, '1753000000000000016', '北斗七星观景台+14座土楼外观+农耕文化展示区', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');
INSERT INTO `ticket_info` VALUES ('1753000000000000041', '河坑星空摄影体验票', 120.00, '1753000000000000016', '含夜间开放权限+银河拍摄指导+热姜茶+防蚊套装', '1', '1797549871140298753', '超级管理员', '2026-01-27 00:00:00');

-- ----------------------------
-- Table structure for trip_strategy
-- ----------------------------
DROP TABLE IF EXISTS `trip_strategy`;
CREATE TABLE `trip_strategy`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `attraction_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '景点id',
  `strategy_pic` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '攻略图片',
  `strategy_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '攻略内容',
  `status` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '攻略状态',
  `review_reason` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核意见',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '攻略管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trip_strategy
-- ----------------------------
INSERT INTO `trip_strategy` VALUES ('2016055654213947393', '1753000000000000016', '[{\"id\":\"2016045723012239362\",\"fileName\":\"田螺坑土楼群.png\",\"fileType\":\"png\",\"keyword\":null}]', '田螺坑土楼群位于福建省漳州市南靖县，是世界文化遗产“福建土楼”的核心景区之一，由一座方楼与四座圆楼依山势错落分布，形成独特的聚落形态，因整体布局形似“四菜一汤”而闻名。土楼以夯土结构为主，兼具防御、居住与宗族聚居功能，集中体现了客家建筑文化与传统生活方式。\n\n景区核心看点包括观景台全景视角、土楼内部参观以及客家民俗文化展示。登上观景台可完整拍摄五座土楼的整体格局，是摄影创作的最佳位置；进入土楼内部，可了解土楼建筑结构、居住布局及家族历史，部分土楼仍有原住民生活其中。\n\n游览路线建议从入口步行至观景台，再依次进入各土楼参观，整体游览时间约2—3小时。该景区适合摄影爱好者、亲子家庭、文化研学及对客家文化感兴趣的游客。游览过程中应注意尊重当地居民生活，雨后道路湿滑，建议穿防滑鞋。', '80', '审核通过，自动发布', '2026-01-27 15:56:44', '2016034476667363330', '张三', '2026-01-27 15:48:19');
INSERT INTO `trip_strategy` VALUES ('2016055654213947394', '1753000000000000005', '[{\"id\":\"2016048403273158657\",\"fileName\":\"云洞岩.png\",\"fileType\":\"png\",\"keyword\":null}]', '云洞岩位于漳州市区西北部，是漳州代表性的丹霞地貌景区，集奇岩、洞穴与摩崖石刻于一体，被誉为“闽南第一山”。景区内保存有大量宋、明、清时期石刻，兼具自然景观与人文价值。\n\n主要看点包括千人洞、一线天、通天洞等天然洞穴景观，以及朱熹题刻“溪山第一”等历史遗迹。登顶后可俯瞰漳州城区全景，视野开阔。\n\n景区以登山游览为主，路线成熟但部分路段狭窄陡峭，整体游览时间约1.5—2小时。适合登山健身、历史文化爱好者及城市周边短途游游客。建议穿防滑运动鞋，注意洞内湿滑区域安全。', '80', '审核通过，自动发布', '2026-01-27 15:56:44', '2016059219544313857', '王五', '2026-01-27 15:48:19');
INSERT INTO `trip_strategy` VALUES ('2016055654213947395', '1753000000000000006', '[{\"id\":\"2016048294460329986\",\"fileName\":\"六鳌翡翠湾.png\",\"fileType\":\"png\",\"keyword\":null}]', '六鳌翡翠湾位于漳州市漳浦县六鳌半岛，是闽南地区知名的滨海旅游景区，以细腻沙滩、清澈海水及滨海风车群为主要特色。海岸线开阔，景观通透，兼具观光与休闲属性。\n\n景区适合进行踏浪、拍照、沙滩休闲及亲子活动，退潮时海滩面积更大，适合游玩。周边配套有步道、观景栈道及简易露营区域。\n\n建议安排半天时间游览，适合亲子家庭、情侣出行及轻度度假游客。夏季注意防晒补水，留意潮汐变化，避免靠近深水区域。', '80', '审核通过，自动发布', '2026-01-27 15:56:44', '2016034476667363330', '张三', '2026-01-27 15:48:19');
INSERT INTO `trip_strategy` VALUES ('2016055654213947396', '1753000000000000007', '[{\"id\":\"2016048184179494914\",\"fileName\":\"香山湿地公园1.png\",\"fileType\":\"png\",\"keyword\":null}]', '香山湿地公园是集生态保护、科普教育与休闲游憩于一体的城市湿地公园，园区内水系发达、植被丰富，是多种鸟类的重要栖息地。\n\n主要看点包括木栈道湿地景观、观鸟平台及自然科普展馆，可近距离观察水鸟、湿地植物及生态系统运行情况。\n\n游览方式以步行为主，节奏舒缓，适合安排1—2小时休闲游览。该景区适合亲子家庭、自然爱好者及休闲散步人群，建议文明观鸟，不打扰野生动物。', '80', '审核通过，自动发布', '2026-01-27 15:56:44', '2016059219544313857', '王五', '2026-01-27 15:48:19');
INSERT INTO `trip_strategy` VALUES ('2016055654213947397', '1753000000000000008', '[{\"id\":\"2016048037441769473\",\"fileName\":\"漳州古城.png\",\"fileType\":\"png\",\"keyword\":null}]', '漳州古城是国家历史文化名城的重要组成部分，保留了较为完整的古街格局与大量历史建筑，集中展现了闽南传统城市风貌与人文底蕴。\n\n主要游览区域包括台湾路、青年路、文庙周边及古城墙遗址，可体验骑楼建筑、传统小吃、非遗表演及夜景灯光。\n\n建议安排半天至一整天游览，适合文化体验、美食探索及城市漫步游客。夜间灯光开放后更具观赏性，注意节假日人流较大。', '80', '审核通过，自动发布', '2026-01-27 15:56:44', '2016034476667363330', '张三', '2026-01-27 15:48:19');
INSERT INTO `trip_strategy` VALUES ('2016055654213947398', '1753000000000000009', '[{\"id\":\"2016047627419193345\",\"fileName\":\"漳州文庙1.png\",\"fileType\":\"png\",\"keyword\":null},{\"id\":\"2016047635782635521\",\"fileName\":\"漳州文庙2.png\",\"fileType\":\"png\",\"keyword\":null}]', '漳州文庙始建于宋代，是福建保存较为完整的文庙建筑群之一，具有重要的历史、文化与教育意义。\n\n核心建筑包括大成殿、泮池及历代碑刻，整体布局庄重肃穆，是了解儒家文化与古代教育制度的重要场所。\n\n游览时间约1小时，适合历史文化爱好者、研学团队及安静参观人群。参观时应保持安静，注意文明礼仪。', '80', '审核通过，自动发布', '2026-01-27 15:56:44', '2016059750501257217', '谢部楼', '2026-01-27 15:48:19');
INSERT INTO `trip_strategy` VALUES ('2016055654213947399', '1753000000000000010', '[{\"id\":\"2016047471567245314\",\"fileName\":\"南炮台遗址.png\",\"fileType\":\"png\",\"keyword\":null}]', '南炮台遗址是近代重要的海防军事遗迹，见证了近代沿海防御体系与抗战历史，具有较高的爱国主义教育价值。\n\n景区内保留有炮台遗址、历史展陈及观景平台，可远眺港口及周边海域。\n\n适合历史爱好者、研学团队及短途参观游客，游览时间约1—1.5小时。注意部分遗址区域地势较高，需留意安全。', '80', '审核通过，自动发布', '2026-01-27 15:56:44', '2016059856671674369', '沙箱', '2026-01-27 15:48:19');
INSERT INTO `trip_strategy` VALUES ('2016055654213947400', '1753000000000000011', '[{\"id\":\"2016047354659409922\",\"fileName\":\"东山岛.png\",\"fileType\":\"png\",\"keyword\":null}]', '东山岛是福建省重要的海岛旅游目的地，集海湾风光、渔村文化与滨海休闲于一体。岛内自然景观与人文景点分布广泛。\n\n主要游览点包括南门湾、风动石景区、关帝庙及环岛公路，可体验海岛骑行、海鲜美食及渔港风情。\n\n建议安排1—2天游览，适合情侣出行、家庭度假及海岛休闲游客。夏季注意防晒，留意台风及海况信息。', '80', '审核通过，自动发布', '2026-01-27 15:56:44', '2016034476667363330', '张三', '2026-01-27 15:48:19');
INSERT INTO `trip_strategy` VALUES ('2016055654213947401', '1753000000000000012', '[{\"id\":\"2016047312296939521\",\"fileName\":\"漳州火山岛.png\",\"fileType\":\"png\",\"keyword\":null}]', '漳州火山岛是国家地质公园，拥有大规模玄武岩柱状节理景观，地质遗迹保存完整，兼具科普与观光价值。\n\n景区内设有火山地貌步道、纪念碑谷风格拍照区及滨海观景平台，适合摄影及休闲游览。\n\n建议游览时间约3—4小时，适合年轻游客、情侣及地质科普爱好者。注意防晒、防风，遵守景区安全提示。', '80', '审核通过，自动发布', '2026-01-27 15:56:44', '2016059750501257217', '谢部楼', '2026-01-27 15:48:19');
INSERT INTO `trip_strategy` VALUES ('2016055654213947402', '1753000000000000013', '[{\"id\":\"2016046584606167041\",\"fileName\":\"镇海角1.png\",\"fileType\":\"png\",\"keyword\":null},{\"id\":\"2016046595507163138\",\"fileName\":\"镇海角2.png\",\"fileType\":\"png\",\"keyword\":null},{\"id\":\"2016046604155817986\",\"fileName\":\"镇海角3.png\",\"fileType\":\"png\",\"keyword\":null}]', '镇海角位于漳州沿海，是集灯塔景观、海岸风光与风车群于一体的观景胜地，被称为“福建小垦丁”。\n\n主要看点包括悬崖灯塔、海峡风光及日落景观，适合摄影及短途观光。\n\n建议傍晚前往观赏日落，游览时间约1—2小时。地势较高、风力较大，注意安全。', '80', '审核通过，自动发布', '2026-01-27 15:56:44', '2016059856671674369', '沙箱', '2026-01-27 15:48:19');
INSERT INTO `trip_strategy` VALUES ('2016055654213947403', '1753000000000000014', '[{\"id\":\"2016046012431798273\",\"fileName\":\"云水谣古镇1.png\",\"fileType\":\"png\",\"keyword\":null},{\"id\":\"2016046020233203713\",\"fileName\":\"云水谣古镇2.png\",\"fileType\":\"png\",\"keyword\":null}]', '云水谣古镇位于南靖县，是集古道、古榕、土楼与水乡景观于一体的传统村落，兼具自然风光与人文历史价值。\n\n主要景点包括千年古榕、和贵楼、怀远楼及溪畔古道，可体验客家村落生活与传统建筑特色。\n\n建议安排半天至一天游览，适合文化体验、摄影及慢节奏游客。夜间环境安静，适合住宿体验乡村风情。', '80', '审核通过，自动发布', '2026-01-27 15:56:44', '2016034476667363330', '张三', '2026-01-27 15:48:19');

-- ----------------------------
-- Table structure for trip_strategy_comment
-- ----------------------------
DROP TABLE IF EXISTS `trip_strategy_comment`;
CREATE TABLE `trip_strategy_comment`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论id',
  `trip_strategy_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '攻略id',
  `comment_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '评论详情',
  `main_comment_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主评论id（所属的直接回复帖子的评论id，如果自身就是，则就是帖子id）',
  `main_comment` smallint NULL DEFAULT NULL COMMENT '是否是主评论0否1是',
  `replay_comment_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回复的评论id，如果是主评论，则为null',
  `reply_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回复给的人的id',
  `reply_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回复给的人的名称',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '攻略评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trip_strategy_comment
-- ----------------------------
INSERT INTO `trip_strategy_comment` VALUES ('2016057905133326336', '2016055654213947393', '大家一起来讨论吧', '2016057905133326336', 1, NULL, NULL, NULL, '2016034476667363330', '张三', '2026-01-27 15:57:16');
INSERT INTO `trip_strategy_comment` VALUES ('2016062575791312897', '2016055654213947393', '玩的怎么样呢', '2016057905133326336', 0, '2016057905133326336', '2016034476667363330', '张三', '2016059219544313857', '王五', '2026-01-27 16:15:49');
INSERT INTO `trip_strategy_comment` VALUES ('2016063988898467840', '2016055654213947394', '玩得愉快呀！', '2016063988898467840', 1, NULL, NULL, NULL, '2016034476667363330', '张三', '2026-01-27 16:21:26');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `user_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `user_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `head_pic_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户头像',
  `user_role` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色',
  `user_sex` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别',
  `user_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `create_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1797549871140298753', '系统管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAGpUlEQVR42u1dz2scdRSfQw85ePAP8ODBgwcPHnvwkIOHHoo7m6YQJMIKFYonpT2IVnZK1KopClZcStC2rJK2QbCkGDSirQEjrXZrV4oSaiqKP5rDtqw2tml5/b75TtzZZGZ2fnx/zcwbeISws8l33mfez+9772tZBl/gDG8Bp/oI1Ktj4FT2Qr3SgLo9y6jFaJlRlxH0U2XV+6zNaJ7REXDsfYxqUB/ZCk5tyKIrLgA772OMG2YATDLGLgYzPDOtcUAZuE51G/5P4vxGEOqVUUbTkgCIAxBK3XipwXHVR706xRjR0QBCGOFamkxyHiuJNDCb4EqDvWAQCCGEKhNt1/CWgoJR2cUedMl8IDYRW3N1d2GAQfFnD3U+h0BspBY6HDkGYuQBVx9zownFoeoMA+bBvEnFNrb4lWIB0UddtC85AKI2xBZ7qHhSESotU8a6yuDseDinRjsrLWM2wbCYwnVluyUEw5euMUSFub56qcHwRfzMtddtvF8oj72ITRM6wSAAglXYAQKjrJLi2QxSU3HIsWvkTZlm6GV5X16c0SEmp3CJGe9kROBLxNwswaPAiN5LhxBjM6ZZRCYKyYiLoXERKfQVYqTALHGW1L23n0GMFLyfkmWnj1SVnPhkOI10nCfmydwOTrBH7xUkEOPkqq7dCUp1KOZQUs0SR0q89AgxTI2UjMWxHQvEKHXFeDHKO4lRaj2uiLJVr9aWmKSWmhFV6JTN1VLg7dj3kzE3PcfllkgW9YFVXenXOBvUNLOaO6YUB5C1PrXFIvPHpT2YaYBcXhRLYgBBb2u7X11NKgWkvRBMSe9JA4hMyc8WkzT8Bn0x1pcGvSFBnydZuKh7cgmI3fa3ma1KXxABEsOO1IYs3geuYEEESAwa2Wq57cAEiBmAYGEdn5BQIkDMdHvXAdmXPX+VBhCKQ8LzWsygz5UKEFPjEE7zljfIRS0g338eTEnvKZxRZ66vNzmn2DYkRyWnVuZqdgJEaGG2JT1BqBMQXVcGUAiQXAOSt1yWTO9KkrdlKfMydLyJKu2NQEC6BIiP/r3R+86rY4oB4UZdvduraj8kDZNudjUCwt3eVmHd3jRM+u9m7zsTO1UD0laXOtERUaf5e7dv9b7jVFWvfV5dcjEvgNy9y+/Hn+rX3lSXfs8DIK892bsfVZfqtfP0u4INKl1BWFImffRK7/5fL+sApKZmCzcvgJw50bv/4pcapBu3cEUXOaAhbO4HuPAFwLm58Ag/TdQflQnIyiRcd+fv/u9c/RFg+oAqQNb+n0sfuwxo0EIunQW4tdr7felCfmzIbCNcCq9fA/jsaHRckn3t7eSFcuu0fwfAhxMACx9HqxMdhjENNZ7vjz/QfvxzffPz4Mv2zSmAt5+RkXpvJCslRYOHAOBi79wJBwHdxeU2f6Pee858QD54sT9d0u0AvPEUV2Gn3gX4czn4GTFrcHiPyMad7cmKrVGfRkkCLnDmYLJ0g05AcJ1fz/TiDrzwRTv68uZ7j7zE1W/QhXw5VhdQJLehR2RgO8KnU5v16uIsf4AkEa1uQCZrAF9N90sFXpjDGsRYlPgfzvSDuH79cQXg+Oti2hFiNewcfBrgr6vcNUSda1L5zSAAEYRPDgH8/F0wM1HFvrUrPgPRhuDLGKS6V37nqi7ZSzqupqVNFyDvPAtw8k3uduNLFHYh81DNZpE2VHt+z9KvQU4f5g5QmpY2KU2fsnfpNsYj+Nau3Y4GDiXkykXupIh6TnQCzp4MBuaXS+maPgvRFo1qJ4gpqFp+OsdjDXyrZf1/BAYlxp8xHuSJDTrNJ/eDA9BgX/uNe31z73MPSZTTkUSVfXuahwlZBgdQN66JozVo+IxZw2doPJNh45logJmBA8xoxJ+BI/5oCKZhQzBpTKykMbH1Jx7KOLvXtomRmgw5jRqXCoaYUeM0jF9MZ5Tw4/W84yro7JAUBdTSjtWjA11S7ATKPk7PO/KImB0r3lB0jB4dChaL1B6fR6BE2g21x+bRwZIGSQYdvRpVyqP56FXyvnyurWnnq9Px3XTAvRHpEGMPuA9QYZ1CZ21NsRcJU/fN4klLdSbTSWv6gXF3HouwHdxKvdNnJjBu4UQejf4S7mMk3gPPByhu+9xoPorxsKuMxVhFBCK8bNWtJTbJ+HdcuzeovLPQwPBmoVHenyL5hIbQFHllDluSQ6vQSw2O216HPY+oMqQAhF5fG3v6sI2MQEhqcxz7UXe4gTtxAlWcO5ul5U0x6ganMdzP2jg7hKsg2+ESwDw+wwO5eyoIkh+uvMcYAAAAAElFTkSuQmCC', '1', '1', '16556666666', NULL);
INSERT INTO `user` VALUES ('2016034476667363330', '张三', 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '[{\"id\":\"2016034476524756994\",\"fileName\":\"张三.png\",\"fileType\":\"png\"}]', '2', '1', '18827382738', '2026-01-27 14:24:10');
INSERT INTO `user` VALUES ('2016050063315767297', '刘云鹏', 'liuyunpeng', 'e10adc3949ba59abbe56e057f20f883e', '[{\"id\":\"2016050063265435650\",\"fileName\":\"刘云鹏.png\",\"fileType\":\"png\"}]', '3', '1', '18273232322', '2026-01-27 15:26:06');
INSERT INTO `user` VALUES ('2016059219544313857', '王五', 'wangwu', 'e10adc3949ba59abbe56e057f20f883e', '[{\"id\":\"2016059219523342337\",\"fileName\":\"王五.png\",\"fileType\":\"png\"}]', '2', '0', '18727382132', '2026-01-27 16:02:29');
INSERT INTO `user` VALUES ('2016059750501257217', '谢部楼', 'xiebulou', 'e10adc3949ba59abbe56e057f20f883e', '[{\"id\":\"2016059750484480001\",\"fileName\":\"谢部楼.png\",\"fileType\":\"png\"}]', '2', '1', '18273823232', '2026-01-27 16:04:36');
INSERT INTO `user` VALUES ('2016059856671674369', '沙箱', 'shaxiang', 'e10adc3949ba59abbe56e057f20f883e', '[{\"id\":\"2016059856654897153\",\"fileName\":\"沙箱.png\",\"fileType\":\"png\"}]', '2', '1', '13837823222', '2026-01-27 16:05:01');

SET FOREIGN_KEY_CHECKS = 1;
