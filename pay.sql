-- ----------------------------
-- 应用
-- ----------------------------
DROP TABLE IF EXISTS `t_app`;
CREATE TABLE `t_app` (
  `app_id` int(11) NOT NULL COMMENT '应用id',
  `type` tinyint(4) NOT NULL COMMENT '应用类型 1 平台应用 2 子商户应用',
  `name` varchar(50) NOT NULL COMMENT '应用名',
  `description` varchar(200) NOT NULL COMMENT '应用描述',
  `status` tinyint(4) NOT NULL COMMENT '应用状态 0 停用 1 启用',
  `user_id` varchar(32) NOT NULL COMMENT '应用关联的用户对象id，创建应用时同时会创建用户',
  `notify_url` varchar(400) DEFAULT NULL COMMENT '异步通知地址',
  `platform_app` int(11) NOT NULL COMMENT '平台应用',
  `use_platform_config` tinyint(4) DEFAULT NULL COMMENT '是否使用平台商户渠道配置，默认使用平台商户配置，子商户字段',
  `extra` varchar(300) DEFAULT NULL,
  `access_secret` varchar(200) DEFAULT NULL,
  `parent_app` int(11) DEFAULT NULL,
  `trade_public_key` varchar(4096) NOT NULL COMMENT '交易公钥',
  `notify_private_key` varchar(4096) NOT NULL COMMENT '通知私钥',
  `notify_public_key` varchar(4096) NOT NULL COMMENT '通知公钥',
  `level` int(11) NOT NULL DEFAULT 0 COMMENT 'APP树的层级',
  `version` int(11) NOT NULL DEFAULT 0 COMMENT '用于实现乐观锁',
  `logical_del` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否逻辑删除',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`app_id`),
  KEY `idx_parent_app_status` (`parent_app`,`status`) USING BTREE
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
  `logical_del` char(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除(针对App删除后设置) ‘0’未删除 1：已删除',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_app_id_channel` (`app_id`,`channel`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;



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
  `description` varchar(300) DEFAULT NULL COMMENT '退款备注',
  `client_ip` varchar(50) DEFAULT NULL,
  `extra` varchar(256) DEFAULT NULL COMMENT '特定渠道需要的的额外附加参数',
  `user_hold` varchar(100) DEFAULT NULL,
  `platform_trade_no` varchar(32) DEFAULT NULL COMMENT '支付渠道退款订单号',
  `time_succeed` bigint(20) DEFAULT NULL COMMENT '退款成功时间',
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
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;