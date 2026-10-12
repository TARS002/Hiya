# legacy — 旧版 Hiya 参考资料

本目录用于存放旧版 Hiya 项目的参考资料，供重构时还原功能需求和消息协议。

请把旧资料直接丢进这个文件夹（原样保留即可，不用整理），能放多少放多少：

## 1. 旧版服务端代码（Java）
- Socket 服务端、消息分发器（Dispatcher）、各 Service（UserService / MessageService / FriendService 等）、JDBC 数据访问层
- 这是还原「功能清单 + 数据流」最重要的依据

## 2. 旧版客户端代码
- 登录/注册、聊天窗口、好友管理等界面逻辑
- 用于确认客户端实际发送/接收的 JSON 消息格式

## 3. 自定义 JSON 消息协议
- 消息类型（如 login / register / send_msg / add_friend ...）、字段含义、示例报文
- 如果散落在代码里，直接放代码即可，我来提取

## 4. 数据库建表 SQL（hiya_db）
- 旧表的 CREATE TABLE 语句
- 用于 JPA 实体与旧表字段一一对应，保证数据可迁移

## 5. 其他文档
- 旧项目的 README、设计文档、截图等
