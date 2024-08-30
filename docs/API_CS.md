# 客户端与服务器之间的协议

## 概述
用户登录基于 Http 协议，登录成功后，
客户端向服务器发起 WebSocket 连接，用于数据传输。

## 鉴权

### 登录

#### 请求

Request URL: `/auth`

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

负载-1:

| 键        | 类型  | 注释                   | 缺省值                          |
|----------|-----|----------------------|------------------------------|
| _CID_l   | 自然数 | 区间左端点                | 可缺省，缺省时等价于 0                 |
| _CID_r   | 正整数 | 区间右端点                | 可缺省，缺省时等价于 `max(type(_CID))` |
| pre_cOrd | 正整数 | 按 changeOrder 分页的起点  | 可缺省                          |
| pre_sOrd | 正整数 | 按 stateOrder 分页的起点   | 可缺省                          |
| all_attr | 布尔  | 是否包含 `@Transient` 属性 | false                        |
| limit    | 自然数 | 内容长度（0 表示无限制）        | 20                           |

1. 当 pre_cOrd 和 pre_sOrd 不存在时，获取 `_CID_l < _CID <= _CID_r` 的数据，按 _CID 从小到大排列
2. pre_cOrd 存在，pre_sOrd 不存在时，获取 `changeOrder > pre_cOrd && _CID_l < _CID <= _CID_r` 的数据<sup>(1)</sup>，按 changeOrder 从小到大排列
3. pre_cOrd 不存在，pre_sOrd 存在时，获取 `stateOrder > pre_sOrd && _CID_l < _CID <= _CID_r` 的数据<sup>(1)</sup>，按 stateOrder 从小到大排列
4. **pre_cOrd 和 pre_sOrd 都存在时，limit 必须为 0**，获取 `(changeOrder > pre_cOrd || stateOrder > pre_sOrd) && _CID_l < _CID <= _CID_r` 的数据<sup>(1)</sup>。
   由于 changeOrder 和 stateOrder 是不同的顺序关系，两者间无法同时排序，数据没有排序保证，无法分片

(1): 数据一定包含变更的属性，可能不包含所有属性。

负载-2:

| 键           | 类型  | 注释                   | 缺省值                                     |
|-------------|-----|----------------------|-----------------------------------------|
| pinned      | 布尔  | 获取置顶联系人              | 不可缺省                                    |
| post_lMRank | 正整数 | 按 lastMsgRank 分页的起点  | 可缺省，缺省时等价于 `max(type(lastMsgRank)) + 1` |
| all_attr    | 布尔  | 是否包含 `@Transient` 属性 | false                                   |
| limit       | 自然数 | 内容长度（0 表示无限制）        | 20                                      |

使用 post_lMRank 分页，获取 `!pinned (or pinned) && lastMsgRank < post_lMRank` 的数据，结果按 lastMsgRank 由大到小排序

负载-3:

| 键        | 类型  | 注释                   | 缺省值        |
|----------|-----|----------------------|------------|
| key      | 字符串 | 搜索关键字                | 不可缺省，不可为空串 |
| all_attr | 布尔  | 是否包含 `@Transient` 属性 | false      |
| limit    | 自然数 | 内容长度（0 表示无限制）        | 20         |

响应：
```json
{
  "code": 200,
  "contacts": [...]
}
```

contacts 为有序数组，其中每一个元素表示一个联系人。

#### 获取消息
该操作不改变服务器存储的数据

命令：
```text
get_messages
```

负载-1：

| 键        | 类型  | 注释                   | 缺省值                          |
|----------|-----|----------------------|------------------------------|
| _MID_l   | 自然数 | 区间左端点                | 可缺省，缺省时等价于 0                 |
| _MID_r   | 正整数 | 区间右端点                | 可缺省，缺省时等价于 `max(type(_MID))` |
| pre_rank | 正整数 | 按 rank 分页的起点         | 可缺省                          |
| pre_cOrd | 正整数 | 按 contentOrder 分页的起点 | 可缺省                          |
| limit    | 自然数 | 内容长度（0 表示无限制）        | 20                           |

1. 当 pre_rank 和 pre_cOrd 不存在时，获取 `_MID_l < _MID <= _MID_r` 的数据，按 _MID 从小到大排列
2. pre_rank 存在，pre_cOrd 不存在时，获取 `rank > pre_rank && _MID_l < _MID <= _MID_r` 的数据<sup>(1)</sup>，按 rank 从小到大排列
3. pre_rank 不存在，pre_cOrd 存在时，获取 `contentOrder > pre_cOrd && _MID_l < _MID <= _MID_r` 的数据<sup>(1)</sup>，按 contentOrder 从小到大排列
4. **pre_rank 和 pre_cOrd 同时存在时，limit 必须为 0**，获取 `(rank > pre_rank || contentOrder > pre_cOrd) && _MID_l < _MID <= _MID_r` 的数据<sup>(1)</sup>。
   由于 rank 和 contentOrder 是不同的顺序关系，两者间无法同时排序，数据没有排序保证，无法分片

(1): 数据一定包含变更的属性，可能不包含所有属性。

负载-2：

| 键         | 类型  | 注释            | 缺省值                              |
|-----------|-----|---------------|----------------------------------|
| _CID      | 正整数 | 联系人编号         | 不可缺省                             |
| post_rank | 正整数 | 按 rank 分页的起点  | 可缺省，缺省时等价于 `max(type(rank)) + 1` |
| limit     | 自然数 | 内容长度（0 表示无限制） | 20                               |

获取与 _CID 指定联系人关联的 `rank < post_rank` 的数据，结果按 rank 从小到大排列

响应：
```json
{
  "code": 200,
  "messages": [...]
}
```

messages 为有序数组，其中每一个元素表示一条消息。


#### 更新状态

命令：
```text
update_state
```

负载：

| 键    | 类型  | 注释      | 缺省值  |
|------|-----|---------|------|
| _CID | 正整数 | 联系人编号   | 不可缺省 |
| read | 布尔  | 已读 / 未读 | 可缺省  |

响应：
```json
{
  "code": 200
}
```

#### 发送消息（异步）

命令：
```text
post_message
```

负载：

| 键       | 类型                     | 注释             | 缺省值         |
|---------|------------------------|----------------|-------------|
| _CID    | 正整数                    | 联系人编号          | 不可缺省        |
| _CiID   | 自然数                    | 指定发送渠道（0 表示自动） | 0           |
| content | `List<MessageSegment>` | 消息             | 不可缺省，不可为空数组 |

响应：
```json
{
  "code": 202,
  "_MID": 123
}
```

### 异常

响应：
```json
{
  "code": 4xx/5xx,
  "msg": ""
}
```

异常码

| code | msg                       | 原因        |
|------|---------------------------|-----------|
| 401  | Authentication failed.    | 身份认证失败    |
| 400  | Request format incorrect. | 请求格式不符合要求 |
| 422  | Unexpected value.         | 参数不合逻辑    |
| 501  | Not implemented yet.      | 功能尚未实现    |
| 500  | Internal error.           | 服务器内部错误   |

### 推送式

#### 状态推送
服务器通知客户端内容发生变动。

状态推送有几种不同的工作模式：
1. 完全内容推送：推送联系人和消息的所有变更<sup>(1)</sup>
2. 联系人内容推送：推送联系人的变更内容<sup>(1)</sup>和消息变更的元信息<sup>(2)</sup>
3. （同上）与此同时，联系人的变更内容包含 `@Transient` 属性
4. 精简通知推送：仅推送启用通知的联系人变更内容<sup>(1)</sup>和其消息变更的元信息<sup>(2)</sup>
5. （同上）与此同时，联系人的变更内容包含 `@Transient` 属性

| 键       | 类型        | 注释     |
|---------|-----------|--------|
| contact | `Contact` | 变更的联系人 |
| message | `Message` | 变更的消息  |

(1): 数据一定包含变更的属性，可能不包含所有属性，一定不包含 `@Transient` 属性。

(2): 数据只包含索引、序列等元信息，不包含变更的具体内容。
