# 消息段

## 概述
消息段是组成一条消息的基本元素，接收与发送的消息均由消息段组成

> 参考：
> 
> [mirai/Messages.md](https://github.com/mamoe/mirai/blob/dev/docs/Messages.md#%E6%B6%88%E6%81%AF%E7%B1%BB%E5%9E%8B)
> 
> [mirai-api-http/MessageType.md](https://github.com/project-mirai/mirai-api-http/blob/master/docs/api/MessageType.md)
> 
> [onebot-11/segment.md](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md)

## 消息段类型

### 纯文本

```json
{
  "type": "plaintext",
  "content": {
    "text": "This is plain text."
  }
}
```

### 图片

```json
{
  "type": "image",
  "content": {
    "url": "https://example.com/img.jpg"
  }
}
```
