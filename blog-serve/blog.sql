create database if not exists blog;

create table if not exists blog.md
(
md_id long not null comment 'markdown的ID',
md_content longtext not null comment 'markdown内容'
) comment 'markdown格式存储';

create table if not exists blog.blog
(
blog_id bigint auto_increment comment '博文ID',
blog_title varchar(100) not null comment '博文标题',
blog_date datetime not null comment '博文发布时间',
blog_content longtext not null comment '博文内容',
blog_cover varchar(100) not null comment '博文封面',
blog_description varchar(200) not null comment '博文摘要',
user_id bigint not null comment '用户ID',
blog_show boolean not null comment '访客是否可见',
primary key (blog_id)
) comment '博文表';

create table if not exists blog.blogdata
(
blog_id bigint not null comment '博文ID',
blog_likes int not null comment '点赞数',
blog_browse int not null comment '浏览数'
) comment '博客点赞数据';

create table if not exists blog.category
(
category_id int auto_increment comment '分类ID',
category_name varchar(50) not null comment '分类名称',
primary key (category_id)
) comment '分类表';

create table if not exists blog.category_blog
(
category_id int not null comment '分类ID',
blog_id bigint not null comment '博文ID'
) comment '博客ID和分类ID关联表';

create table if not exists blog.label
(
label_id int auto_increment comment '标签ID',
label_name varchar(50) not null comment '标签名称',
primary key (label_id)
) comment '标签表';

create table if not exists blog.label_blog
(
label_id int not null comment '标签ID',
blog_id bigint not null comment '博文ID'
) comment '博客ID和标签ID关联表';

create table if not exists blog.reply
(
reply_id bigint auto_increment comment '回复ID',
reply_content varchar(200) not null comment '回复内容',
reply_user bigint not null comment '发表内容用户',
response_user bigint comment '被回复的用户',
blog_id bigint comment '在哪一篇博客下面发表的评论',
replytime datetime not null  comment '发布留言时间',
fisrt_name varchar(100) not null comment '留言者昵称',
second_name varchar(100) comment '被回复者昵称',
primary key (reply_id)
) comment '留言表';

create table if not exists blog.reply
(
reply_id bigint auto_increment comment '回复ID',
replyed_id bigint comment '被回复的留言ID',
reply_content varchar(200) not null comment '回复内容',
reply_user bigint not null comment '发表内容用户',
response_user bigint comment '被回复的用户',
blog_id bigint comment '在哪一篇博客下面发表的评论',
reply_time datetime not null  comment '发布留言时间',
first_name varchar(100) not null comment '留言者昵称',
second_name varchar(100) comment '被回复者昵称',
primary key (reply_id)
) comment '留言表';

create table if not exists blog.delete_reply
(
id bigint auto_increment comment '回复ID',
replyed_id bigint comment '被回复的留言ID',
reply_content varchar(200) not null comment '回复内容',
reply_user bigint not null comment '发表内容用户',
response_user bigint comment '被回复的用户',
blog_id bigint comment '在哪一篇博客下面发表的评论',
replytime datetime not null  comment '发布留言时间',
first_name varchar(100) not null comment '留言者昵称',
second_name varchar(100) comment '被回复者昵称',
primary key (id)
) comment '被删除的留言表';

create table if not exists blog.users
(
user_id bigint auto_increment comment '用户ID',
user_name varchar(50) not null comment '用户昵称',
user_status int not null comment '用户权限，访客、管理员、普通用户',
user_email varchar(30) not null comment '用户邮箱',
user_creattime datetime not null comment '用户创建时间',
primary key(user_id)
) comment '用户表';

create table if not exists blog.status
(
status_id int auto_increment comment '权限ID',
status_name varchar(20) not null comment '权限名称、访客、管理员、用户',
primary key(status_id)
) comment '权限表';

create table if not exists blog.user_info
(
user_id bigint not null comment '用户ID（非主键）',
user_description varchar(100)  comment '用户描述',
user_password varchar(20) not null comment '用户账号密码',
user_birthday date not null comment '用户出生时间',
user_sex int not null comment '用户性别（1：男 0：女）',
user_image varchar(100)  comment '用户头像',
user_account varchar(20) not null unique comment '用户登录账号'
) comment '网站正式用户信息表';

create table if not exists blog.surf
(
surf_id bigint auto_increment comment '访问记录ID',
ip varchar(20)  comment '访问用户IP',
surf_time datetime  comment '访问时间',
device_system varchar(20)   comment '设备操作系统',
browsername varchar(20)  comment '浏览器',
province varchar(20)  comment 'IP所在省份',
city varchar(20) comment 'IP所在城市',
device varchar(20)  comment '设备',
primary key(surf_id)
) comment '访客信息表';

create table if not exists blog.img
(
img_id int auto_increment comment '图片ID',
img_url varchar(100) not null comment '图片相对地址',
primary key(img_id)
) comment '图片表';

create table if not exists blog.my_redis
(
id int auto_increment comment 'key的ID',
key_name varchar(30) not null comment 'key的名称',
expire_time int not null comment '过期时间',
primary key(id)
) comment '调整Redis中key的过期时间';

create table if not exists blog.link
(
id int auto_increment comment '主键ID',
imgurl varchar(100) not null comment '网站图标',
link varchar(100) not null comment '网站链接',
name varchar(100) not null comment '网站名称',
info varchar(100) not null comment '网站介绍',
is_show boolean not null comment '是否首页显示',
apply_time timestamp not null comment '申请时间',
primary key(id)
) comment '友链';

create table if not exists blog.homes
(
id int auto_increment ,
title varchar(100)  comment '网页首页标题',
info varchar(100)  comment '网站首页信息',
imgurl varchar(100) not null comment '网站首页图片轮播',
home_show boolean default true comment '是否显示',
link varchar(100) comment '链接',
primary key(id)
) ;


create table if not exists blog.sysmess
(
id int auto_increment,
title varchar(100) comment '公告标题',
content longtext not null comment '公告内容',
sys_show boolean not null comment '是否显示',
primary key(id)
) comment '网站公告';

create table if not exists blog.project
(
id int auto_increment comment '主键ID',
imgurl varchar(100) not null comment '项目图片',
link varchar(100) not null comment '项目链接',
name varchar(100) not null comment '项目名称',
info varchar(200) not null comment '项目基本情况',
project_show boolean not null comment '是否首页显示',
create_time timestamp not null comment '创建时间',
primary key(id)
) comment '我的项目';

create table if not exists blog.essay
(
id int auto_increment comment '主键ID',
imgurl varchar(100)  comment '随笔封面',
title varchar(100) not null comment '随笔标题',
info varchar(200) not null comment '随笔信息',
essay_show boolean not null comment '是否首页显示',
create_time timestamp not null comment '创建时间',
primary key(id)
) comment '我的随笔';



