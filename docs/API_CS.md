# 客户端与服务器之间的协议

## 概述
用户登录基于 Http 协议，登录成功后，
客户端向服务器发起 WebSocket 连接，用于数据传输。

## 鉴权

### 登录

#### 请求

Request URL: `/login`

Request Method: `POST`

Payload (Form Data):
```text
username: $username
password: $password
```

#### 响应

若登录成功，则 Status Code 为 200

若登录失败，则 Status Code 为 403

### 建立连接

客户端携带 Cookie 向 `/app` 发起 WebSocket 连接

## 数据传输
客户端与服务器之间的数据传输大多数均为 **请求-应答** 式，即由客户端发起请求，服务器作出应答。
仅有少数情况服务器会主动向客户端发送数据，称为 **推送** 式。

 - 客户端发起的请求的格式：
    ```json
    {
      "cmd": "",
      "body": {}
    }
    ```
   cmd 表示命令（包含请求动词和资源），body 为负载


 - 服务器向客户端发送的数据的格式：
    ```json
    {
      "type": "",
      "body": {}
    }
    ```
   服务器发给客户端的数据可能是响应，也可能是推送，由 type 的值区分。
   response 表示响应，push 表示推送。

### 请求-应答式
该部分默认服务器正常响应

#### 获取联系人列表
该操作不改变服务器存储的数据

命令: 
```text
get_contacts
```

负载:

键 | 值 | 注释
---|---|---
sort| Default / Search| 默认 / 搜索
key| 字符串| 搜索内容（仅在搜索时有意义）
num | 自然数|内容长度（0 表示无限制）

响应：
```json
{
  "code": 0,
  "contacts": [...]
}
```

contacts 为有序数组，其中每一个元素表示一个联系人。

不论是默认情况下按照最近通讯时间排序还是搜索时按照相关性排序，联系人的**排序逻辑在服务器侧完成**，客户端只负责渲染。

联系人元素有以下结构：

键 | 值 | 注释
---|---|---
_CID|
remark|
unread|
pinned|
lastMsgTime|


#### 获取消息
该操作不改变服务器存储的数据

命令：
```text
get_messages
```

负载：

键 | 值 | 注释
---|---|---
_CID | | 联系人编号
lastMsg_MID | | 获取消息的起始编号（0 表示最新消息）
num | | 获取消息条数

响应：
```json
{
  "code": 0,
  "messages": [...]
}
```

messages 为有序数组，其中每一个元素表示一条消息。


#### 更新状态

命令：
```text
update_status
```

负载：

键 | 值 | 注释
---|---|---
_CID | | 联系人编号
status | Read / Pin / UnPin |已读 / 置顶 / 取消置顶

响应：
```json
{
  "code": 0
}
```

#### 发送消息

命令：
```text
post_message
```

负载：

键 | 值 | 注释
---|---|---
_CID | | 联系人编号
_SID | | 消息源编号（0 表示自动）
message | | 消息

响应：
```json
{
  "code": 0
}
```

### 异常

响应：
```json
{
  "code": 1,
  "msg": ""
}
```

异常码

code | msg | 原因
---|---|---
1 | Authenticate failed. | 身份认证失败
2 | Request cannot be resolved. | 命令或负载不正确，请求JSON文本无法反序列化
3 | Unexpected value. | 请求值异常


### 推送式

#### 状态推送
状态推送操作只是服务器通知客户端有内容发生变动，并不传递具体变动的内容。
接下来的执行逻辑由客户端定义。比如通过 “请求-应答式” 接口获取变动的内容。

键 | 值 | 注释
---|---|---
event | ReceiveMsg / StatusChange | 收到消息 / 联系人未读、置顶等状态改变
_CID | | 与该事件关联的联系人编号
