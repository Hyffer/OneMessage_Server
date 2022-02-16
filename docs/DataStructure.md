# 存储结构

## 概述

## 联系人

字段 | 类型 | 注释
---|---|---
_CID | 正整数 | 联系人编号
type | Friend / Group / Member / Stranger | 好友 / 群组 / 群成员<sup>(1)</sup> / 陌生人
id | | QQ号 / 群号
~~avatar~~|-|-
name| | 昵称 / 群名
remark| | 备注
total|
unread|
pinned| |置顶
lastMsgTime|

(1): **群成员** 表示有共同的群组，而又不是好友的人。
一个群成员可能存在于多个群组中，但在联系人数据表中只有一条记录。

## 消息记录

每一张消息记录数据表对应于一个联系人

字段 | 类型 | 注释
---|---|---
_MID| 正整数 | 消息编号（不同联系人的消息编号可以相同）
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
