/*
MySQL Data Transfer
Source Host: db-iit-dev02.db01.baidu.com
Source Database: sparkle
Target Host: db-iit-dev02.db01.baidu.com
Target Database: sparkle
Date: 2010/5/31 10:26:53
*/

SET FOREIGN_KEY_CHECKS=0;
--
-- Table structure for table `card`
--

DROP TABLE IF EXISTS `card`;
CREATE TABLE `card` (
  `id` bigint(20) NOT NULL auto_increment COMMENT '卡片ID',
  `space_id` bigint(20) default NULL COMMENT '所属空间',
  `super_id` bigint(20) default NULL COMMENT '上级卡片ID',
  `card_type` bigint(20) default NULL COMMENT '卡片类型',
  `title` varchar(1024) default NULL COMMENT '标题',
  `detail` mediumtext COMMENT '描述',
  `created_user` bigint(20) default NULL COMMENT '创建人',
  `created_time` datetime default NULL COMMENT '创建时间',
  `last_modified_user` bigint(20) default NULL COMMENT '最后更新人',
  `last_modified_time` datetime default NULL COMMENT '最后更新时间',
  `sequence` bigint(11) default NULL COMMENT '卡片序号',
  `history_id` bigint(20) default NULL COMMENT '历史ID',
  `project_id` bigint(20) default NULL COMMENT '可能关联到的iCafe项目',
  PRIMARY KEY  (`id`),
  KEY `card_super_id` (`super_id`),
  KEY `card_card_type` (`card_type`),
  KEY `card_created_user` (`created_user`),
  KEY `card_last_modified_user` (`last_modified_user`),
  KEY `card_project_id` (`project_id`),
  KEY `card_space_id` (`space_id`),
  KEY `card_history_id` (`history_id`),
  CONSTRAINT `card_super_id` FOREIGN KEY (`super_id`) REFERENCES `card` (`id`),
  CONSTRAINT `card_card_type` FOREIGN KEY (`card_type`) REFERENCES `card_type` (`id`),
  CONSTRAINT `card_created_user` FOREIGN KEY (`created_user`) REFERENCES `users` (`id`),
  CONSTRAINT `card_last_modified_user` FOREIGN KEY (`last_modified_user`) REFERENCES `users` (`id`),
  CONSTRAINT `card_project_id` FOREIGN KEY (`project_id`) REFERENCES `icafe_project` (`id`),
  CONSTRAINT `card_space_id` FOREIGN KEY (`space_id`) REFERENCES `spaces` (`id`),
  CONSTRAINT `card_history_id` FOREIGN KEY (`history_id`) REFERENCES `card_history` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卡片';


DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment` (
  `id` bigint(20) NOT NULL auto_increment COMMENT '附件id',
  `name` varchar(200) NOT NULL COMMENT '附件名',
  `original_name` varchar(200) NOT NULL COMMENT '附件原始文件名',
  `path` varchar(400) NOT NULL COMMENT '附件路径',
  `type` varchar(20) default NULL COMMENT '附件类型',
  `upload_user` bigint(20) default NULL COMMENT '附件上传人',
  `upload_time` datetime default NULL COMMENT '附件上传时间',
  `card_id` bigint(20) default NULL COMMENT '附件所属卡片',
  `status` int(11) default NULL COMMENT '附件状态0表示正常1表示删除',
  `old_attach_id` bigint(20) default NULL COMMENT '附件替换的老附件id',
  `note`  text COMMENT '附件备注',
  PRIMARY KEY  (`id`),
  CONSTRAINT `attachment_user_id` FOREIGN KEY (`upload_user`) REFERENCES `users` (`id`),
  CONSTRAINT `attachment_card_id` FOREIGN KEY (`card_id`) REFERENCES `card` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '附件表';


DROP TABLE IF EXISTS `configurations`;

CREATE TABLE `configurations` (
  `config_key` varchar(100) NOT NULL COMMENT '键',
  `config_value` varchar(500) NOT NULL COMMENT '值',
  PRIMARY KEY (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '系统环境变量表';


DROP TABLE IF EXISTS `card_history`;
CREATE TABLE `card_history` (
  `id` bigint(20) NOT NULL auto_increment COMMENT '历史ID',
  `card_id` bigint(20) default NULL COMMENT '对应卡片ID',
  `op_user_id` bigint(20) default NULL COMMENT '操作人',
  `op_time` datetime default NULL COMMENT '操作时间',
  `op_type` int(11) default NULL COMMENT '操作类型',
  `title` varchar(1024) default NULL COMMENT '标题',
  `detail` mediumtext COMMENT '描述',
  `data` mediumtext COMMENT '当前状态的序列化文本',
  `diff_data` mediumtext COMMENT '与上个版本的diff信息',
  PRIMARY KEY  (`id`),
  KEY `card_history_card_id` (`card_id`),
  KEY `card_history_op_user_id` (`op_user_id`),
  CONSTRAINT `card_history_op_user_id` FOREIGN KEY (`op_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `card_history_card_id` FOREIGN KEY (`card_id`) REFERENCES `card` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卡片更新历史表';

DROP TABLE IF EXISTS `card_type`;

CREATE TABLE `card_type` (
  `id` bigint(20) NOT NULL auto_increment COMMENT '卡片类型id',
  `local_id` bigint(20) NOT NULL COMMENT '空间内标识',
  `name` varchar(255) default NULL COMMENT '卡片类型名称',
  `space_id` bigint(20) default NULL COMMENT '所属空间',
  `parent_card_type_id` bigint(20) default NULL COMMENT '上级卡片类型id',
  `recursive` tinyint(1) default NULL COMMENT '是否级联',
  `color` varchar(10) default '#FFF' COMMENT '显示颜色',
  PRIMARY KEY  (`id`),
  KEY `card_type_space_id` (`space_id`),
  KEY `card_type_parent_card_type_id` (`parent_card_type_id`),
  CONSTRAINT `card_type_space_id` FOREIGN KEY (`space_id`) REFERENCES `spaces` (`id`),
  CONSTRAINT `card_type_parent_card_type_id` FOREIGN KEY (`parent_card_type_id`) REFERENCES `card_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卡片类型表';


DROP TABLE IF EXISTS `card_property`;
CREATE TABLE `card_property` (
  `id` bigint(20) NOT NULL auto_increment COMMENT '卡片属性ID',
  `local_id` bigint(20) NOT NULL COMMENT '空间内标识',
  `space_id` bigint(20) NOT NULL COMMENT '空间ID',
  `name` varchar(255) default NULL COMMENT '属性名称',
  `type` varchar(20) default NULL COMMENT '属性类型',
  `sort` int(11) default NULL COMMENT '属性顺序号',
  `info` text COMMENT '辅助信息',
  `hidden` tinyint(1) default NULL COMMENT '是否隐藏',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卡片属性表';



DROP TABLE IF EXISTS `card_property_value`;

CREATE TABLE `card_property_value` (
  `id` bigint(20) NOT NULL auto_increment COMMENT '卡片属性值表',
  `card_id` bigint(20) default NULL COMMENT '卡片ID',
  `card_property_id` bigint(20) default NULL COMMENT '卡片属性ID',
  `type` varchar(20) default NULL COMMENT '值类型',
  `intvalue` bigint(20) default NULL COMMENT '数字型值',
  `strvalue` text COMMENT '字符串值',
  `timevalue` datetime default NULL COMMENT '日期型值',
  PRIMARY KEY  (`id`),
  KEY `card_property_value_card_property_id` (`card_property_id`),
  KEY `card_property_value_card_id` (`card_id`),
  CONSTRAINT `card_property_value_card_property_id` FOREIGN KEY (`card_property_id`) REFERENCES `card_property` (`id`),
  CONSTRAINT `card_property_value_card_id` FOREIGN KEY (`card_id`) REFERENCES `card` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卡片属性值表';


DROP TABLE IF EXISTS `card_type_property`;
CREATE TABLE `card_type_property` (
  `card_type_id` bigint(20) NOT NULL default '0',
  `card_property_id` bigint(20) NOT NULL default '0',
  PRIMARY KEY  (`card_type_id`,`card_property_id`),
  KEY `card_type_property_type` (`card_type_id`),
  KEY `card_type_property_property` (`card_property_id`),
  CONSTRAINT `card_type_property_card_type` FOREIGN KEY (`card_type_id`) REFERENCES `card_type` (`id`),
  CONSTRAINT `card_type_property_card_property` FOREIGN KEY (`card_property_id`) REFERENCES `card_property` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*
DROP TABLE IF EXISTS `card_resource_map`;

CREATE TABLE `card_resource_map` (
  `map_id` bigint(20) NOT NULL auto_increment COMMENT '映射id',
  `res_type` int(11) default NULL COMMENT '资源类型',
  `res_id` bigint(20) default NULL COMMENT '对应资源id',
  `card_id` bigint(20) default NULL COMMENT '需求点id',
  `op_time` datetime default NULL COMMENT '添加时间',
  PRIMARY KEY  (`map_id`),
  KEY `FK_Reference_15` (`card_id`),
  CONSTRAINT `FK_Reference_15` FOREIGN KEY (`card_id`) REFERENCES `card` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='需求及其他资源映射表';
*/

/*
DROP TABLE IF EXISTS `card_status_conf`;

CREATE TABLE `card_status_conf` (
  `id` bigint(20) NOT NULL auto_increment COMMENT '卡片类型状态id',
  `card_type_id` bigint(20) default NULL COMMENT '卡片类型id',
  `status_name` varchar(255) default NULL COMMENT '状态名称',
  `status_value` int(11) default NULL COMMENT '状态值',
  PRIMARY KEY  (`id`),
  KEY `FK_Reference_31` (`card_type_id`),
  CONSTRAINT `FK_Reference_31` FOREIGN KEY (`card_type_id`) REFERENCES `card_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卡片类型的状态定义表';
*/
/*
DROP TABLE IF EXISTS `card_thread_map`;

CREATE TABLE `card_thread_map` (
  `map_id` bigint(20) NOT NULL auto_increment COMMENT '映射id',
  `card_id` bigint(20) default NULL COMMENT '需求id',
  `thread_id` bigint(20) default NULL COMMENT '讨论帖子id',
  `space_id` bigint(20) default NULL COMMENT '空间id',
  PRIMARY KEY  (`map_id`),
  KEY `FK_Reference_6` (`card_id`),
  CONSTRAINT `FK_Reference_6` FOREIGN KEY (`card_id`) REFERENCES `card` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='需求讨论映射';
*/

/*
DROP TABLE IF EXISTS `group_right_map`;

CREATE TABLE `group_right_map` (
  `map_id` bigint(20) NOT NULL auto_increment COMMENT '映射id',
  `user_id` bigint(20) default NULL COMMENT '用户id',
  `group_id` bigint(20) default NULL COMMENT '用户组id',
  `right_type_id` int(11) default NULL COMMENT '权限类型id',
  PRIMARY KEY  (`map_id`),
  KEY `FK_Reference_27` (`user_id`),
  KEY `FK_Reference_28` (`group_id`),
  KEY `FK_Reference_29` (`right_type_id`),
  CONSTRAINT `FK_Reference_29` FOREIGN KEY (`right_type_id`) REFERENCES `right_type` (`id`),
  CONSTRAINT `FK_Reference_27` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_Reference_28` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户组权限映射';
*/



DROP TABLE IF EXISTS `group_user`;

CREATE TABLE `group_user` (
  `map_id` bigint(20) NOT NULL auto_increment COMMENT '映射id',
  `group_id` bigint(20) default NULL COMMENT '组id',
  `user_id` bigint(20) default NULL COMMENT '人员id',
  PRIMARY KEY  (`map_id`),
  KEY `group_user_group_id` (`group_id`),
  KEY `group_user_user_id` (`user_id`),
  CONSTRAINT `group_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `group_user_group_id` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户组人员映射';

DROP TABLE IF EXISTS `group_space`;

CREATE TABLE `group_space` (
  `map_id` bigint(20) NOT NULL auto_increment COMMENT '映射id',
  `group_id` bigint(20) default NULL COMMENT '组id',
  `space_id` bigint(20) default NULL COMMENT '空间id',
  PRIMARY KEY  (`map_id`),
  KEY `group_space_group_id` (`group_id`),
  KEY `group_space_space_id` (`space_id`),
  CONSTRAINT `group_space_space_id` FOREIGN KEY (`space_id`) REFERENCES `spaces` (`id`),
  CONSTRAINT `group_space_group_id` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户组空间映射';

/*
DROP TABLE IF EXISTS `op_log`;

CREATE TABLE `op_log` (
  `id` bigint(20) NOT NULL auto_increment COMMENT 'id',
  `user_id` bigint(20) default NULL COMMENT '用户id',
  `op_user_id` bigint(20) default NULL COMMENT '操作人',
  `target_type` int(11) default NULL COMMENT '目标类型',
  `op_type` int(11) default NULL COMMENT '操作类型',
  `space_id` bigint(20) default NULL COMMENT '空间id',
  `card_id` bigint(20) default NULL COMMENT '需求点id',
  `history_id` bigint(20) default NULL COMMENT '对应历史id',
  PRIMARY KEY  (`id`),
  KEY `FK_Reference_10` (`user_id`),
  KEY `FK_Reference_11` (`space_id`),
  KEY `FK_Reference_12` (`history_id`),
  KEY `FK_Reference_16` (`card_id`),
  CONSTRAINT `FK_Reference_16` FOREIGN KEY (`card_id`) REFERENCES `card` (`id`),
  CONSTRAINT `FK_Reference_10` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_Reference_11` FOREIGN KEY (`space_id`) REFERENCES `spaces` (`id`),
  CONSTRAINT `FK_Reference_12` FOREIGN KEY (`history_id`) REFERENCES `card_history` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='操作日志';
*/

/*
DROP TABLE IF EXISTS `right_type`;

CREATE TABLE `right_type` (
  `id` int(11) NOT NULL auto_increment COMMENT '权限类型id',
  `right_name` varchar(255) default NULL COMMENT '权限名称',
  `right_desc` varchar(1024) default NULL COMMENT '权限描述',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限类型';
*/


DROP TABLE IF EXISTS `groups`;

CREATE TABLE `groups` (
  `id` bigint(20) NOT NULL auto_increment COMMENT '用户组id',
  `space_id` bigint(20) default NULL COMMENT '空间id',
  `name` varchar(255) default NULL COMMENT '组名',
  `locked` tinyint(1) default NULL COMMENT '是否失效',
  `exposed` tinyint(1) default NULL COMMENT '是否公开',
  PRIMARY KEY  (`id`),
  KEY `groups_space_id` (`space_id`),
  CONSTRAINT `groups_space_id` FOREIGN KEY (`space_id`) REFERENCES `spaces` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='空间用户组';


/*
DROP TABLE IF EXISTS `space_product_map`;

CREATE TABLE `space_product_map` (
  `id` int(11) NOT NULL auto_increment COMMENT '映射id',
  `space_id` bigint(20) default NULL COMMENT '空间id',
  `product_id` bigint(20) default NULL COMMENT '产品线id',
  PRIMARY KEY  (`id`),
  KEY `FK_Reference_2` (`space_id`),
  CONSTRAINT `FK_Reference_2` FOREIGN KEY (`space_id`) REFERENCES `spaces` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='空间产品线对应关系';
*/


DROP TABLE IF EXISTS `space_sequence`;

CREATE TABLE `space_sequence` (
  `id` bigint(20) NOT NULL,
  `next_card_seq_num` bigint(20) default 1,
  `next_card_type_local_id` bigint(20) default 1,
  `next_card_property_local_id` bigint(20) default 1,
  `next_list_value_local_id` bigint(20) default 1,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




DROP TABLE IF EXISTS `spaces`;

CREATE TABLE `spaces` (
  `id` bigint(20) NOT NULL auto_increment COMMENT '空间ID',
  `name` varchar(255) default NULL COMMENT '空间名称',
  `prefix_code` varchar(255) default NULL COMMENT '空间简称',
  `description` text,
  `type` int(11) default NULL COMMENT '空间类型',
  `is_public` boolean default false COMMENT '是否公开',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='需求空间';


/*
DROP TABLE IF EXISTS `spark_conf`;

CREATE TABLE `spark_conf` (
  `conf_id` bigint(20) NOT NULL auto_increment COMMENT '配置id',
  `conf_type` int(11) default NULL COMMENT '配置类型',
  `obj_id` int(11) default NULL COMMENT '实体id',
  `conf_key` varchar(255) default NULL COMMENT '配置key',
  `conf_value` text COMMENT '配置数据',
  PRIMARY KEY  (`conf_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置表';
*/

/*
DROP TABLE IF EXISTS `status_transition`;

CREATE TABLE `status_transition` (
  `map_id` bigint(20) NOT NULL auto_increment COMMENT '映射id',
  `from_status_id` bigint(20) default NULL COMMENT '起始状态',
  `to_status_id` bigint(20) default NULL COMMENT '目标状态',
  `info` varchar(1024) default NULL COMMENT '信息',
  PRIMARY KEY  (`map_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='状态转换表';
*/

/*
DROP TABLE IF EXISTS `user_favorite`;

CREATE TABLE `user_favorite` (
  `id` bigint(20) NOT NULL auto_increment COMMENT '收藏id',
  `user_id` bigint(20) default NULL COMMENT '用户id',
  `type` int(11) default NULL COMMENT '收藏类型',
  `url` varchar(1024) default NULL COMMENT '收藏对应url',
  `obj_id` varchar(255) default NULL COMMENT '收藏对???对象id',
  PRIMARY KEY  (`id`),
  KEY `FK_Reference_23` (`user_id`),
  CONSTRAINT `FK_Reference_23` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='我的收藏';
*/


DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL auto_increment COMMENT '用户id',
  `uic_id` bigint(20) default NULL COMMENT 'uicid',
  `username` varchar(255) NOT NULL,
  `name` varchar(255) default NULL COMMENT '用户姓名',
  `email` varchar(255) default NULL COMMENT '邮箱',
  `locked` tinyint(1) default NULL COMMENT '是否有效',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

SET FOREIGN_KEY_CHECKS = 0;

-- -------------------------------------
-- Tables
DROP TABLE IF EXISTS `acl_class`;
CREATE TABLE `acl_class` (
  `ID` BIGINT NOT NULL auto_increment,
  `CLASS` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE INDEX `SYS_IDX_49` (`ID`),
  UNIQUE INDEX `SYS_IDX_UNIQUE_UK_2_51` (`CLASS`)
)
ENGINE = INNODB
CHARACTER SET utf8 COLLATE utf8_general_ci;

ALTER TABLE `acl_class` AUTO_INCREMENT =100;

DROP TABLE IF EXISTS `acl_entry`;
CREATE TABLE `acl_entry` (
  `ID` BIGINT NOT NULL auto_increment,
  `ACL_OBJECT_IDENTITY` BIGINT NOT NULL,
  `ACE_ORDER` INT NOT NULL,
  `SID` BIGINT NOT NULL,
  `MASK` INT NOT NULL,
  `GRANTING` BOOLEAN NOT NULL,
  `AUDIT_SUCCESS` BOOLEAN NOT NULL,
  `AUDIT_FAILURE` BOOLEAN NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE INDEX `SYS_IDX_61` (`ID`),
  UNIQUE INDEX `SYS_IDX_UNIQUE_UK_4_63` (`acl_object_identity`, `ACE_ORDER`),
  INDEX `SYS_IDX_64` (`acl_object_identity`),
  INDEX `SYS_IDX_66` (`SID`),
  CONSTRAINT `FOREIGN_FK_4` FOREIGN KEY `FOREIGN_FK_4` (`acl_object_identity`)
    REFERENCES `acl_object_identity` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FOREIGN_FK_5` FOREIGN KEY `FOREIGN_FK_5` (`SID`)
    REFERENCES `acl_sid` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB
CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `acl_entry` AUTO_INCREMENT =100;

DROP TABLE IF EXISTS `acl_object_identity`;
CREATE TABLE `acl_object_identity` (
  `ID` BIGINT NOT NULL auto_increment,
  `OBJECT_ID_CLASS` BIGINT NOT NULL,
  `OBJECT_ID_IDENTITY` BIGINT NOT NULL,
  `PARENT_OBJECT` BIGINT NULL,
  `OWNER_SID` BIGINT NULL,
  `ENTRIES_INHERITING` BOOLEAN NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE INDEX `SYS_IDX_52` (`ID`),
  UNIQUE INDEX `SYS_IDX_UNIQUE_UK_3_54` (`OBJECT_ID_CLASS`, `OBJECT_ID_IDENTITY`),
  INDEX `SYS_IDX_55` (`PARENT_OBJECT`),
  INDEX `SYS_IDX_57` (`OBJECT_ID_CLASS`),
  INDEX `SYS_IDX_59` (`OWNER_SID`),
  CONSTRAINT `FOREIGN_FK_2` FOREIGN KEY `FOREIGN_FK_2` (`OBJECT_ID_CLASS`)
    REFERENCES `acl_class` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FOREIGN_FK_1` FOREIGN KEY `FOREIGN_FK_1` (`PARENT_OBJECT`)
    REFERENCES `acl_object_identity` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FOREIGN_FK_3` FOREIGN KEY `FOREIGN_FK_3` (`OWNER_SID`)
    REFERENCES `acl_sid` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB
CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `acl_object_identity` AUTO_INCREMENT =100;


DROP TABLE IF EXISTS `acl_sid`;
CREATE TABLE `acl_sid` (
  `ID` BIGINT NOT NULL auto_increment,
  `PRINCIPAL` BOOLEAN NOT NULL,
  `SID` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE INDEX `SYS_IDX_46` (`ID`),
  UNIQUE INDEX `SYS_IDX_UNIQUE_UK_1_48` (`SID`, `PRINCIPAL`)
)
ENGINE = INNODB
CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `acl_sid` AUTO_INCREMENT =100;



/*CREATE TABLE `AUTHORITIES` (
  `USERNAME` VARCHAR(50) NOT NULL,
  `AUTHORITY` VARCHAR(50) NOT NULL,
  UNIQUE INDEX `IX_AUTH_USERNAME` (`USERNAME`, `AUTHORITY`),
  INDEX `SYS_IDX_71` (`USERNAME`),
  CONSTRAINT `FK_AUTHORITIES_USERS` FOREIGN KEY `FK_AUTHORITIES_USERS` (`USERNAME`)
    REFERENCES `USERS` (`USERNAME`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB
CHARACTER SET utf8 COLLATE utf8_general_ci;


DROP TABLE IF EXISTS `view`;
CREATE TABLE `view` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT  ,
  `name` varchar(255) DEFAULT NULL,
  `view_url` varchar(2085) DEFAULT NULL ,
  `type` varchar(20) DEFAULT NULL  ,
  `created_time` date DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `space_id` bigint(20) DEFAULT NULL  ,
  `user_id` bigint(20) DEFAULT NULL  ,
  PRIMARY KEY (`id`) ,
  KEY `view_space_id` (`space_id`),
  KEY `view_user_id` (`user_id`),
  CONSTRAINT `view_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `view_space_id` FOREIGN KEY (`space_id`) REFERENCES `spaces` (`id`)
) 
ENGINE=InnoDB DEFAULT CHARSET=utf8  ;
*/

DROP TABLE IF EXISTS `icafe_project`;
CREATE TABLE `icafe_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标识',
  `icafe_project_id` bigint(20) NOT NULL COMMENT 'iCafe项目ID',
  `name` varchar(255) default NULL COMMENT 'iCafe项目名称',
  `space_id` bigint(20) COMMENT '外间，关联到空间',
  PRIMARY KEY  (`id`),
  KEY `icafe_project_space_id` (`space_id`),
  CONSTRAINT `icafe_project_space_id` FOREIGN KEY (`space_id`) REFERENCES `spaces` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='iCafe的项目表';


Alter table spaces add unique  (prefix_code);

Alter table card add index card_index (space_id,sequence);

ALTER table card_type add index card_type_index(space_id,local_id);

ALTER table card_property add index card_property_index(space_id,local_id);
	
Alter table users add index (username);

SET FOREIGN_KEY_CHECKS = 1;

/**
Adun add at 2010-11-02. Two tables for Space Group
*/
DROP TABLE IF EXISTS `spacegroups`;
CREATE TABLE `spacegroups` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(128) NOT NULL,
  `description` varchar(512) default NULL,
  `user_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `spacegroups_user` (`user_id`),
  CONSTRAINT `spacegroups_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `spacegroup_space`;
CREATE TABLE `spacegroup_space` (
  `id` bigint(20) NOT NULL auto_increment,
  `spacegroup_id` bigint(20) NOT NULL,
  `space_id` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `spacegroup_space_space` (`space_id`),
  KEY `spacegroup_space_spacegroup` (`spacegroup_id`),
  CONSTRAINT `spacegroup_space_spacegroup` FOREIGN KEY (`spacegroup_id`) REFERENCES `spacegroups` (`id`),
  CONSTRAINT `spacegroup_space_space` FOREIGN KEY (`space_id`) REFERENCES `spaces` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `space_view`;
CREATE TABLE `space_view` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `view_url` varchar(2085) DEFAULT NULL COMMENT 'URL',
  `created_time` date DEFAULT NULL COMMENT '创建时间',
  `sort` int(11) DEFAULT NULL COMMENT '顺序',
  `space_id` bigint(20) DEFAULT NULL COMMENT '所属空间',
  `user_id` bigint(20) DEFAULT NULL COMMENT '创建用户',
  PRIMARY KEY (`id`) ,
  KEY `space_view_space_id` (`space_id`),
  KEY `space_view_user_id` (`user_id`),
  CONSTRAINT `space_view_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `space_view_space_id` FOREIGN KEY (`space_id`) REFERENCES `spaces` (`id`)
) 
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '空间视图收藏表' ;

DROP TABLE IF EXISTS `user_view`;
CREATE TABLE `user_view` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT  COMMENT 'ID',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `view_url` varchar(2085) DEFAULT NULL COMMENT 'URL',
  `created_time` date DEFAULT NULL COMMENT '创建时间',
  `sort` int(11) DEFAULT NULL COMMENT '顺序',
  `space_id` bigint(20) DEFAULT NULL  COMMENT '所属空间',
  `user_id` bigint(20) DEFAULT NULL  COMMENT '创建用户',
  PRIMARY KEY (`id`) ,
  KEY `user_view_space_id` (`space_id`),
  KEY `user_view_user_id` (`user_id`),
  CONSTRAINT `user_view_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `user_view_space_id` FOREIGN KEY (`space_id`) REFERENCES `spaces` (`id`)
) 
ENGINE=InnoDB DEFAULT CHARSET=utf8  COMMENT '个人视图收藏表' ;