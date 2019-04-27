CREATE TABLE `card_type_property` (
  `card_type_id` bigint(20) NOT NULL default '0',
  `card_property_id` bigint(20) NOT NULL default '0',
  PRIMARY KEY  (`card_type_id`,`card_property_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

Alter table `card_property`
add column `space_id` bigint(20) default NULL;

update `card_property` p set space_id = (
	select space_id from `card_type` t where p.card_type_id = t.id
);

insert into `card_type_property` (card_type_id, card_property_id) 
select card_type_id, id from card_property where card_type_id is not null;

--------以下还没写明白,暂时不处理也没啥大问题.

删除card_property表的card_type_id字段的外键.给space_id字段加上外键

Alter table `card_property`
drop column `card_type_id`;
