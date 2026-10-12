-- ============================================================
-- Hiya 数据库初始化脚本（hiya_db）
-- 来源：从旧版 Hiya 真实运行库提取的 CREATE TABLE（MySQL 8.0）
-- 用途：新环境建库（执行前先 CREATE DATABASE hiya_db;）
-- 说明：SpringBoot 端配置了 spring.jpa.hibernate.ddl-auto=none，
--       表结构以本文件为准，Hibernate 不会自动改表。
-- ============================================================

CREATE TABLE `hiya_user` (
  `user_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '账号ID，主键',
  `user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户自定义名称，用户可修改',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码，用户输入部分',
  `is_banned` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否被ban，0没ban，1ban',
  `unban_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '何时之后解封',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账号创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '该数据最后更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '账号是否被用户删除，0没删，1删',
  PRIMARY KEY (`user_id`) USING BTREE,
  KEY `idx_user_name` (`user_name`),
  KEY `idx_status_deleted` (`is_banned`,`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Hiya用户主表';

CREATE TABLE `hiya_user_profile` (
  `user_profile_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `nick_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'Hiya昵称',
  `avatar_url` varchar(256) DEFAULT NULL COMMENT '头像OSS地址',
  `signature` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '个性签名',
  `gender` tinyint unsigned DEFAULT NULL COMMENT '性别，0不便透露，1男，2女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  PRIMARY KEY (`user_profile_id`) USING BTREE,
  KEY `fk_hiya_user_id` (`user_id`),
  CONSTRAINT `fk_hiya_user_id` FOREIGN KEY (`user_id`) REFERENCES `hiya_user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='这是用户个人界面个性化设置用的。';

CREATE TABLE `hiya_user_login_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `hiya_chat` (
  `chat_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '一个会话，会话号',
  `type` tinyint unsigned NOT NULL COMMENT '是私聊还是群聊：1私聊、2群聊',
  `chat_title` varchar(64) DEFAULT NULL COMMENT '群名称：可以为空，为空时显示成员',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '会话创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '群最后更新时间',
  PRIMARY KEY (`chat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `hiya_chat_member` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `chat_id` bigint unsigned NOT NULL COMMENT '群号。',
  `user_id` bigint NOT NULL COMMENT '群员Hiya号。',
  `role` tinyint NOT NULL DEFAULT '0' COMMENT '群角色，0群员，1绿标，2群主。',
  `join_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入群时间。',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_muted` tinyint NOT NULL DEFAULT '0' COMMENT '被禁言了吗？0没有，1禁言了。不过好像只在群聊里有用。私聊没用。',
  `mute_until` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '解禁时间。',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_group_member` (`chat_id`,`user_id`) COMMENT '一个用户只能在同个群里出现一次',
  KEY `idx_user` (`user_id`),
  KEY `idx_group` (`chat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `hiya_friend` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '好友关系id',
  `user_id` bigint NOT NULL COMMENT '当前用户ID',
  `friend_id` bigint NOT NULL COMMENT '该用户好友ID',
  `status` tinyint(1) NOT NULL COMMENT '好友状态，0好友，1拉黑',
  `remark` varchar(64) DEFAULT NULL COMMENT '好友备注',
  `group_tag` varchar(16) DEFAULT NULL COMMENT '好友分组',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '关系创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '关系更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ralationship` (`user_id`,`friend_id`) COMMENT '甲和乙的关系一定是unique的，不然理还乱。',
  KEY `user` (`user_id`) COMMENT '用户方',
  KEY `friend` (`friend_id`) COMMENT '朋友方'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `hiya_message` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `sender_id` bigint unsigned NOT NULL COMMENT '消息发送者',
  `chat_id` bigint unsigned DEFAULT NULL COMMENT '发给会话',
  `msg_type` tinyint(1) NOT NULL COMMENT '消息类型，1文本，2图片，3文件，4语音，5系统指令',
  `content` text COMMENT '消息内容',
  `send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '消息是否被撤回，0没撤回，1撤回了',
  PRIMARY KEY (`id`),
  KEY `idx_sender` (`sender_id`),
  KEY `idx_group` (`chat_id`),
  KEY `idx_send_time` (`send_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 文件消息表（旧系统预留：msg_type=3 文件消息用；旧代码未实现，仅保留建表）
CREATE TABLE `hiya_chat_file` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `uploader_id` bigint unsigned NOT NULL COMMENT '发送人',
  `origin_name` varchar(255) NOT NULL COMMENT '发送者发送文件时该文件的命名',
  `stored_name` varchar(255) DEFAULT NULL COMMENT '文件发送至服务器后可能会有重名文件需要改名，改名后的文件名',
  `file_url` varchar(512) NOT NULL COMMENT '文件在服务器中的位置',
  `file_type` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件后缀',
  `file_size` bigint unsigned DEFAULT NULL,
  `related_msg_id` bigint unsigned NOT NULL,
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
