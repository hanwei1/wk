create database wkdb;

grant all privileges on wkdb.* to "wk_appuser_"@"%" identified by "qscGU2150";

----微信用户表
create table weixin_user(
id				int(10)					primary key auto_increment,
nickname		varchar(64)				not null unique			comment '昵称',
head_img_url	varchar(128)			not null default ''		comment '头像',
wxid			varchar(64)				not null				comment '微信ID',
phone			varchar(11)				not null default ''		comment '手机号',
real_name		varchar(16)				not null default ''		comment '真实姓名',
status			tinyint					not null default 0		comment '状态，0正常',
sex				tinyint					not null default 0		comment '性别，1男2女0未知',
city			varchar(16)				not null default ''		comment '用户所在城市',
country			varchar(16)				not null default ''		comment '用户所在国家',
province		varchar(16)				not null default ''		comment '用户所在省份',
unionid			varchar(64)				not null default ''		comment '微信unionid',
remark			varchar(256)			not null default ''		comment '微信unionid',
create_time		datetime				not null				comment ''
)ENGINE=MyISAM  COMMENT '微信用户表';