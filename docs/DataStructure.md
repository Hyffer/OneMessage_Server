# 存储结构

## 概述

该项目同时依赖两个数据库：关系型数据库 MariaDB 和非关系型数据库 MongoDB。
关系型数据库用于存储联系人信息和消息记录，而实际消息内容存放于非关系型数据库。

理论上，无需任何修改，可直接使用 MySQL 替代 MariaDB，但并未测试。

## 联系人

字段 | 类型 | 注释
---|---|---
_CID | 正整数 | 联系人编号
type | Friend / Group / Member / Stranger | 好友 / 群组 / 群成员<sup>(1)</sup> / 陌生人
~~avatar~~|-|-
remark| | 备注
total|
unread|
pinned| |置顶
lastMsgTime|

(1): **群成员** 表示有共同的群组，而又不是好友的人。
一个群成员可能存在于多个群组中，但在联系人数据表中只有一条记录。

每一个联系人对应于多个消息源的联系方式

字段 | 类型 | 注释
---|---|---
_CID | 正整数 | 联系人编号
_SID | 正整数 | 消息源编号
id | | 在该消息源的帐号
name| | 昵称 / 群名

## 消息记录

每一张消息记录数据表对应于一个联系人

字段 | 类型 | 注释
---|---|---
_MID| 正整数 | 消息编号（不同联系人的消息编号可以相同）
_SID| 正整数 | 消息源编号
time|
messageId| | ?
internalId| | ?（关于撤回和引用回复）<sup>*(1)*</sup>
direction|
quoteId| | ?
type<sup>(2)</sup>| Normal / Anonymous | 常规 / 匿名
senderId<sup>(2)</sup>|
senderName<sup>(2)</sup>|

> *(1):*
> https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/MessageSource.kt#L46

(2): 为群消息属性，仅在群消息记录中有意义。

### 消息内容
消息内容存放于 NoSQL

## 数据库配置

### MariaDB

#### 设置字符集

将默认字符集设置为 utf8mb4:
```
[client]
default-character-set = utf8mb4

[mysql]
default-character-set = utf8mb4

[mysqld]
character-set-client-handshake = FALSE
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
init_connect='SET NAMES utf8mb4'

[server]
character-set-server=utf8mb4
```

或者在所有数据表创建语句中出现 varchar 的地方指定 CHARACTER SET utf8mb4

#### 创建数据库与用户

```mysql
create database onemessage;

create user onemessage@localhost identified by '...';
grant all on onemessage.* to onemessage@localhost;
flush privileges;
```

#### 创建数据表

消息源表、联系人数据表：
```mysql
use onemessage;

create table source(
    _SID int unsigned auto_increment primary key,
    name varchar(20) not null unique
);

create table contact(
    _CID int unsigned auto_increment primary key,
    type enum('Friend', 'Group', 'Member', 'Stranger') not null,
    remark varchar(100) not null,
    total int unsigned not null default 0,
    unread int unsigned not null default 0,
    pinned boolean not null default false,
    lastMsgTime timestamp null
);

create table contact_info(
    _CID int unsigned,
    _SID int unsigned,
    id bigint unsigned not null,
    name varchar(100) not null,
    index(_CID)
);
```

[//]: # (TODO: deal with groups with the same name)

消息记录数据表会在消息源接入时自动维护，不需要手动创建

### MongoDB

#### 创建数据库与用户

```mongodb
use onemessage

db.createUser({
    user: 'onemessage',
    pwd: '...',
    roles: [{role: 'readWrite', db: 'onemessage'}]
})
```

## 测试数据

<details>
<summary>以下操作仅作测试用途</summary>

### 创建消息记录数据表

用户消息记录：
```mysql
create table message_1(
    _MID int unsigned auto_increment primary key,
    time timestamp not null,
    direction enum('In', 'Out') not null
);
```

群消息记录：
```mysql
create table message_2(
    _MID int unsigned auto_increment primary key,
    time timestamp not null,
    direction enum('In', 'Out') not null,
    type enum('Normal', 'Anonymous') not null,
    senderId bigint unsigned not null,
    senderName varchar(100) not null
);
```

### 插入联系人

```mysql
insert into contact(type, id, name, remark) values('Friend', 1234567890, '好友', '备注');
insert into contact(type, id, name, remark, lastMsgTime) values('Friend', 1111111111, '好友1', '备注1', '2019-02-01 22:10:30');
insert into contact(type, id, name, remark, lastMsgTime) values('Friend', 2222222222, '好友2', '备注2', '2022-02-01 22:10:30');
insert into contact(type, id, name, remark) values('Group', 3333333333, '群3', '备注3');
insert into contact(type, id, name, remark, lastMsgTime) values('Group', 4444444444, '群4', '备注4', '2021-02-01 22:10:30');
```

### 插入消息记录

```mysql
update contact set total = 3 where _CID = 1;

insert into message_1(time, direction) values('2022-02-01 10:30:30', 'In');
insert into message_1(time, direction) values('2022-02-01 10:30:32', 'In');
insert into message_1(time, direction) values('2022-02-01 10:31:30', 'Out');
```

### 插入消息内容

```mongodb
db.msgcontent_1.insert([
    {
        _id: 1,
        segments: [
            {
                type: "plaintext",
                content: {
                    _class: "xyz.hyffer.onemessage_server.storage.component.MessageSegmentContent.Plaintext",
                    text: "This is a Plaintext."
                }
            }
        ]
    },
    {
        _id: 2,
        segments: [
            {
                type: "image",
                content: {
                    _class: "xyz.hyffer.onemessage_server.storage.component.MessageSegmentContent.Image",
                    url: "https://example.com/img.jpg"
                }
            }
        ]
    },
    {
        _id: 3,
        segments: [
            {
                type: "image",
                content: {
                    _class: "xyz.hyffer.onemessage_server.storage.component.MessageSegmentContent.Image",
                    url: "https://example.com/img.jpg"
                }
            },
            {
                type: "plaintext",
                content: {
                    _class: "xyz.hyffer.onemessage_server.storage.component.MessageSegmentContent.Plaintext",
                    text: "Text under a photo"
                }
            }
        ]
    }
])
```
</details>
