-- ----------------------------
-- 应用
-- ----------------------------
DROP TABLE IF EXISTS `t_app`;
CREATE TABLE `t_app` (
  `app_id` int(11) NOT NULL COMMENT '应用id',
  `type` varchar(16) NOT NULL COMMENT '应用类型 1 平台应用 2 子商户应用',
  `name` varchar(50) NOT NULL COMMENT '应用名',
  `description` varchar(200) NOT NULL COMMENT '应用描述',
  `status` varchar(16) NOT NULL COMMENT '应用状态 0 停用 1 启用',
  `responsible_user_no` bigint DEFAULT NULL COMMENT '应用关联的用户对象id,即负责人',
  `create_user_no` BIGINT DEFAULT NULL COMMENT '创建当前应用的人',
  `use_platform_config` tinyint(4) DEFAULT NULL COMMENT '是否使用平台商户渠道配置，默认使用平台商户配置，子商户字段',
  `parent_app_id` int(11) DEFAULT NULL COMMENT '父级应用',
  `trade_public_key` varchar(4096) DEFAULT NULL COMMENT '交易公钥',
  `notify_private_key` varchar(4096) NOT NULL COMMENT '通知私钥',
  `notify_public_key` varchar(4096) NOT NULL COMMENT '通知公钥',
  `level` int(11) NOT NULL DEFAULT 0 COMMENT 'APP树的层级',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用表';


-- ----------------------------
-- 支付渠道配置
-- ----------------------------
DROP TABLE IF EXISTS `t_channel_config`;
CREATE TABLE `t_channel_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` int(11) NOT NULL COMMENT '支付应用ID',
  `channel` varchar(45) NOT NULL COMMENT '交易渠道',
  `channel_config` varchar(4096) NOT NULL DEFAULT '{}' COMMENT '渠道参数配置 json格式',
  `start_date` datetime DEFAULT NULL COMMENT '启用时间',
  `stop_date` datetime DEFAULT NULL COMMENT '停用时间',
  `status` VARCHAR(12) NOT NULL COMMENT '渠道状态\n ‘0’：未启用 ‘1’：启用',
  `logical_del` char(1) DEFAULT '0' COMMENT '逻辑删除(针对App删除后设置) ‘0’未删除 1：已删除',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_app_id_channel` (`app_id`,`channel`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;



-- ----------------------------
-- 支付单
-- ----------------------------
DROP TABLE IF EXISTS `t_charge`;
CREATE TABLE `t_charge` (
  `charge_no` varchar(32) NOT NULL COMMENT '支付中心凭据号,系统内唯一，以“ch_”开头',
  `order_no` varchar(32) DEFAULT NULL COMMENT '商户订单号，必须在商户系统内唯一',
  `app_id` int(11) NOT NULL COMMENT '平台应用Id',
  `service_app_id` int(11) NOT NULL COMMENT '服务方应用id',
  `amount` bigint NOT NULL COMMENT '支付单金额',
  `actual_amount` bigint COMMENT '实付金额',
  `subject` varchar(256) NOT NULL COMMENT '购买商品的标题',
  `body` varchar(128) DEFAULT NULL COMMENT '购买商品的描述信息',
  `channel` varchar(32) NOT NULL COMMENT '支付渠道',
  `client_ip` varchar(20) DEFAULT NULL COMMENT '发起支付的客户端IP',
  `description` varchar(300) DEFAULT NULL COMMENT '描述信息，限制300个字符内',
  `user_hold` varchar(100) DEFAULT NULL COMMENT '用户保留信息',
  `platform_trade_no` varchar(32) DEFAULT NULL COMMENT '支付渠道订单号',
  `time_created` datetime DEFAULT NULL COMMENT '支付单创建时间',
  `time_paid` datetime DEFAULT NULL COMMENT '支付单支付完成时间',
  `time_expire` bigint(20) DEFAULT NULL COMMENT '支付单有效时间，单位分钟m',
  `live_mode` tinyint(1) DEFAULT NULL COMMENT '是否是生产模式',
  `status` tinyint(1) NOT NULL COMMENT '订单状态',
  `currency` varchar(10) NOT NULL COMMENT '三位ISO货币代码，只支持人民币cny，默认cny',
  `credential` text DEFAULT NULL COMMENT '支付凭证',
  `extra` varchar(1024) DEFAULT NULL COMMENT '渠道额外参数',
  `version` int(11) NOT NULL DEFAULT '0',
  `failure_code` varchar(128) DEFAULT NULL,
  `failure_msg` varchar(128) DEFAULT NULL,
  `update_time` datetime NOT NULL COMMENT '记录修改时间',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  PRIMARY KEY (`charge_no`),
  UNIQUE KEY `uk_order_no_app_id` (`order_no`,`app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='支付订单表';

-- ----------------------------
-- 退款单
-- ----------------------------
DROP TABLE IF EXISTS `t_refund`;
CREATE TABLE `t_refund` (
  `refund_no` varchar(32) NOT NULL COMMENT '退款单no',
  `charge_no` varchar(32) DEFAULT NULL COMMENT '退款订单对应的支付订单no',
  `order_no` varchar(32) DEFAULT NULL COMMENT '商户订单号',
  `app_id` int(11) NOT NULL COMMENT '应用id',
  `channel` varchar(50) NOT NULL COMMENT '退款渠道',
  `amount` bigint NOT NULL COMMENT '退款金额',
  `subject` varchar(256) NOT NULL COMMENT '商品标题，冗余数据',
  `description` varchar(300) DEFAULT NULL COMMENT '退款备注',
  `client_ip` varchar(50) DEFAULT NULL,
  `extra` varchar(256) DEFAULT NULL COMMENT '特定渠道需要的的额外附加参数',
  `user_hold` varchar(100) DEFAULT NULL,
  `platform_trade_no` varchar(32) DEFAULT NULL COMMENT '支付渠道退款订单号',
  `time_created` datetime DEFAULT NULL COMMENT '发起退款时间',
  `time_succeed` datetime DEFAULT NULL COMMENT '退款成功时间',
  `status` tinyint(1) NOT NULL COMMENT '退款状态',
  `failure_code` varchar(128) DEFAULT NULL COMMENT '错误码',
  `failure_msg` varchar(128) DEFAULT NULL COMMENT '错误消息的描述',
  `currency` varchar(8) NOT NULL COMMENT '三位货币ISO代码，目前仅支持cny',
  `operator_id` varchar(16) DEFAULT NULL COMMENT '操作人id',
  `version` int(11) NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`refund_no`),
  KEY `idx_refund_no` (`charge_no`) USING BTREE,
  KEY `idx_order_no` (`order_no`) USING BTREE,
  UNIQUE KEY `uk_refund_no_app_id` (`refund_no`,`app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- 事件订阅
-- ----------------------------
DROP TABLE IF EXISTS `t_event_subscription`;
CREATE TABLE `t_event_subscription` (
  `id` int AUTO_INCREMENT NOT NULL COMMENT '主键',
  `app_id` int NOT NULL COMMENT '订阅的应用',
  `event_type` varchar(64) NOT NULL COMMENT '订阅的事件类型',
  `notify_url` varchar(512) NOT NULL COMMENT '事件推送地址',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- -- ----------------------------
-- -- 事件通知记录
-- -- ----------------------------
DROP TABLE IF EXISTS `t_event_notify`;
CREATE TABLE `t_event_notify` (
  `event_no` varchar(32) NOT NULL COMMENT '事件号',
  `source_no` varchar(32) NOT NULL COMMENT '事件源号，如chargeNo',
  `app_id` int(11) NOT NULL COMMENT '事件所属应用id',
  `notify_status` varchar(32) NOT NULL COMMENT '事件通知状态',
  `type` varchar(32) NOT NULL COMMENT '事件类型',
  `notify_url` varchar(512) NOT NULL COMMENT '事件推送地址，冗余字段',
  `time_occur` datetime NOT NULL COMMENT '事件发生时间',
  `notify_time` int NOT NULL COMMENT '当前事件通知次数',
  `notify_interval` varchar(62) DEFAULT NULL COMMENT '通知的时间间隔，为rmq的延迟级别，json数组',
  `event_data` varchar(5096) NOT NULL COMMENT '事件携带的数据，json格式',
  `last_reply` varchar(1024) DEFAULT NULL COMMENT '上次响应',
  `time_last_notify` datetime DEFAULT NULL COMMENT '上次通知时间',
  `version` int(11) NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`event_no`),
  KEY `idx_source_no` (`source_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- -- ----------------------------
-- -- Http日志记录
-- -- ----------------------------
DROP TABLE IF EXISTS `t_http_log`;
CREATE TABLE `t_http_log` (
  `id` int not null auto_increment,
  `type` varchar(32) not null comment 'http日志类型',
  `app_id` varchar(16) comment '应用标识',
  `platform` varchar(16) comment '支付平台',
  `live_mode` tinyint(2) comment '是否是生产模式',
  `req_timestamp` bigint comment '请求时间戳',
  `req_client_ip` varchar(32) comment '请求源的ip地址',
  `req_uri` varchar(128) comment '请求uri地址',
  `req_body` varchar(4096) comment '请求体',
  `req_method` varchar(8) comment '请求方法',
  `req_header` varchar(2048) comment '请求头',
  `resp_milli` bigint comment '相应时间',
  `resp_http_status` varchar(6) comment '响应状态码',
  `resp_header` varchar(2048) comment '响应头',
  `resp_body` varchar(4096) comment '响应内容',
  `resp_timestamp` bigint comment '响应时间戳',
  primary key (`id`),
  index `http_log_app_type`(`app_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- -- ----------------------------
-- -- 证书数据
-- -- ----------------------------
DROP TABLE IF EXISTS `t_certificate`;
CREATE TABLE `t_certificate` (
  `id` int not null auto_increment,
  `app_id` int comment '应用标识',
  `type` varchar(64) not null comment '证书类型',
  `channel` varchar(50) NOT NULL COMMENT '渠道',
  `cert_data` blob not null comment '证书数据',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  primary key (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for t_auth_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_permission`;
CREATE TABLE `t_auth_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限id',
  `name` varchar(128) NOT NULL COMMENT '权限名',
  `description` varchar(64) NOT NULL COMMENT '权限描述信息',
  `type` varchar(64) NOT NULL COMMENT '权限类型',
  `is_high_risk` tinyint(4) DEFAULT NULL COMMENT '是否高危权限',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_permission_name` (`name`) COMMENT '权限名唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_auth_role
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_role`;
CREATE TABLE `t_auth_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `code` varchar(32) NOT NULL COMMENT '角色编码',
  `name` varchar(32) NOT NULL COMMENT '角色名',
  `description` varchar(64) NOT NULL COMMENT '角色描述信息',
  `app_id` int(11) NOT NULL COMMENT '在哪个应用下的角色',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_role_name` (`name`) COMMENT '角色名唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_auth_role_permission_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_role_permission_relation`;
CREATE TABLE `t_auth_role_permission_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_code` bigint(32) NOT NULL COMMENT '角色编码',
  `permission_id` varchar(32) NOT NULL COMMENT '权限id',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_role_relation` (`role_code`,`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_auth_user
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_user`;
CREATE TABLE `t_auth_user` (
  `user_no` bigint(32) NOT NULL COMMENT '全局唯一的用户号',
  `nick_name` varchar(32) NOT NULL COMMENT '昵称',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `salt` varchar(64) DEFAULT NULL COMMENT '密码加盐',
  `phone` varchar(11) NOT NULL COMMENT '手机号码,全局唯一',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱,全局唯一',
  `sex` varchar(16) NOT NULL COMMENT '性别,女性,男性',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像地址',
  `type` varchar(32) NOT NULL COMMENT '用户类型',
  `status` varchar(32) NOT NULL COMMENT '账号状态,1正常,2锁定',
  `original_app_id` int(11) DEFAULT NULL COMMENT '最初创建该用户的应用',
  `created_user_no` bigint(20) DEFAULT NULL COMMENT '创建当前用户的用户',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`user_no`),
  UNIQUE KEY `t_auth_user_phone` (`phone`) USING BTREE COMMENT '手机号全局唯一',
  UNIQUE KEY `t_auth_user_email` (`email`) USING BTREE COMMENT '邮箱全局唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- super admin user data
-- ----------------------------
INSERT INTO `pay`.`t_auth_user`(`user_no`, `nick_name`, `password`, `salt`, `phone`, `email`, `sex`, `avatar`, `type`, `status`, `original_app_id`, `created_user_no`, `create_time`, `update_time`) VALUES (1000000, 'super_admin', '$2a$10$QHUj.9oFcZMhzdv9mJID5Or76zsdIE0VxCRMurvbhkVJeGAW.87Re', NULL, '15811112222', '123@123.com', 'MALE', 'https://pic2.zhimg.com/80/v2-1ceecf8a8ce3b882d326e8c3d5382a90_hd.jpg', 'SUPER_ADMIN', 'NORMALITY', NULL, NULL, '2019-04-28 10:08:45', '2019-04-28 10:08:45');

-- ----------------------------
-- Table structure for t_auth_user_role_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_user_role_relation`;
CREATE TABLE `t_auth_user_role_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_no` bigint(32) NOT NULL COMMENT '全局的用户号',
  `role_code` varchar(32) NOT NULL COMMENT '角色id',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_role_relation` (`user_no`,`role_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;