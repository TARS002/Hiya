# HiyaServer（SpringBoot 重构版）

Hiya 聊天软件的服务端。将旧版「Socket 直连 + 自定义 JSON 协议 + 手写 JDBC」架构
重构为 **B-S 架构**（浏览器 - 服务器）：

- **REST API**（Spring MVC）— 登录注册、好友、聊天等业务接口
- **WebSocket** — 实时消息推送（替代旧版 Socket 长连接，浏览器原生支持）
- **Spring Data JPA** — 持久化（替代手写 JDBC）
- **用户标识**（简化设计）— REST 和 WebSocket 都用 URL 参数 `userId`

## 技术栈

| 组件 | 技术 |
|---|---|
| 框架 | Spring Boot 4.1.0 |
| 语言 | Java 17 |
| Web | spring-boot-starter-webmvc（REST API） |
| 实时通信 | spring-boot-starter-websocket |
| 持久层 | Spring Data JPA + Hibernate |
| 数据库 | MySQL（库名 hiya_db，沿用旧表结构） |
| 身份标识 | URL 参数 `userId`（REST: ?userId=1；WebSocket: /ws?userId=1）；无认证 |

## 目录结构与五个入口

```
src/main/java/com/hiya
├── HiyaServerApplication.java   # 启动类（main 入口）
├── controller  # 表现层：5 个入口（见下表）
├── service     # 业务层：3 个业务模块（接口 + impl 实现）
├── repository  # 数据访问层：7 个 Repository（对应 7 张表）
├── entity      # JPA 实体：对应数据库表
├── dto         # 数据传输对象：请求/响应参数（含校验注解）
├── common      # 通用：统一响应 Result、状态码、业务异常、全局异常处理
├── config      # 配置类：CORS 跨域、WebSocket 注册
└── ws          # WebSocket：在线会话管理、消息处理器
```

按业务分成三块：**登录注册 / 聊天 / 好友**，对应 5 个 Controller 入口：

| # | 入口 | Controller | Service | 说明 |
|---|---|---|---|---|
| 1 | `GET /` | HomeController | — | 首页：服务状态 + 接口清单（打开浏览器就能确认服务在跑） |
| 2 | `POST /api/register` | RegisterController | UserService | 注册 |
| 3 | `POST /api/login`、`/api/me`、`/api/info/{id}` | LoginController | UserService | 登录、查询/修改自己的数据、查询别人的数据 |
| 4 | `/api/chat/**` | ChatController | ChatService | 建群、加群/退群、私聊、成员、发消息、聊天记录 |
| 5 | `/api/friend/**` | FriendController | FriendService | 好友列表、在线、申请/同意/拒绝、删除 |
| — | `/ws?userId=` | ChatWebSocketHandler | — | 实时推送通道（好友通知、消息广播） |

### 三大板块与接口对照

**板块1 注册登录**：`POST /api/register`、`POST /api/login`、`GET /api/me`、`GET /api/me/info`、
`PUT /api/me`（改自己的资料）、`PUT /api/me/password`（改自己的密码）、`GET /api/info/{userId}`（查别人）

**板块2 聊天室**：`POST /api/chat/room`（建群）、`POST /api/chat/{id}/join`（加群）、
`POST /api/chat/{id}/leave`（退群）、`GET /api/chat/{id}/members`（成员）、
`POST /api/chat/{id}/messages`（发消息）、`GET /api/chat/{id}/messages`（记录）、
`GET /api/chat/list`（我的会话）、`POST /api/chat/private`（私聊）

**板块3 好友**：`GET /api/friend/list`、`GET /api/friend/online`、`POST /api/friend/request`（申请）、
`POST /api/friend/accept`（同意）、`POST /api/friend/reject`（拒绝）、`DELETE /api/friend/{id}`（删除）

> ⚠️ 前端注意：业务错误是 **HTTP 200 + body 里的 code**（如 1002 密码错误、409 已是好友），
> 判断成败要看 `code` 字段，不能只看 HTTP 状态码。

> **Controller / Service / Repository 不必一一对应**：Controller 按"入口"分（5）、
> Service 按"业务模块"分（3）、Repository 按"数据库表"分（7）。
> 例如 RegisterController 与 LoginController 共用 UserService；
> ChatService 一次业务会用到 chat / chat_member / message 三张表。
> 详见 [docs/01-架构设计.md](docs/01-架构设计.md) 第 4 节。

## 如何运行

1. 启动本机 MySQL，执行 `docs/03-数据库初始化.sql` 创建数据库和表（先 `CREATE DATABASE hiya_db;`）
2. 确认 `src/main/resources/application.properties` 里的数据库账号密码
3. 启动应用：`.\mvnw.cmd spring-boot:run`（或直接运行 `HiyaServerApplication` 的 main）
4. 验证服务是否正常：浏览器打开 **http://localhost:8080/test.html**（后端自带的测试页，可点着测接口）
5. 接口契约见 `docs/02-需求与协议分析.md`：第 3 节（WebSocket 推送协议）、第 4 节（REST API 一览）

> 说明：`spring.jpa.hibernate.ddl-auto=none`，表结构以 `docs/03-数据库初始化.sql` 为准
> （Hibernate 的 update 模式会改动旧表结构，实测会把 TEXT 改成 varchar(255)，已在配置注释中说明）。

## 没有前端时怎么测？

见 [`docs/04-接口测试指南.md`](docs/04-接口测试指南.md)，几种方式任选：

| 方式 | 说明 |
|---|---|
| **Postman / Apifox（推荐）** | 导入 `postman/` 下的两个文件，25 个接口全在，登录后自动保存 userId。详见 [`docs/05-Postman测试指南.md`](docs/05-Postman测试指南.md) |
| **后端自带测试页** | 启动后访问 http://localhost:8080/test.html（可点着测登录/好友/会话/发消息/WS 推送） |
| VS Code REST Client | 打开根目录 `api-test.http`，点 `Send Request` |
| PowerShell | `Invoke-RestMethod` 一行行复制（指南里有现成命令） |
| curl | 命令行方式 |

> 提示：打开 `http://localhost:8080/` 会看到**首页**（服务状态 + 全部接口清单）；
> 打开 `http://localhost:8080/test.html` 是**可点击的测试页**。


## 前端（Vue3）对接三步

```
① 登录：POST /api/login  {"userName":"xxx","password":"xxx"}
   → {"code":200,"data":{"userId":1,"userName":"xxx"}}

② 之后所有 REST 请求都带请求头：?userId=1
   （每次请求自己拼上，例如 /api/friend/list?userId=）

③ 建立 WebSocket 长连接收推送：ws://localhost:8080/ws?userId=1
   收到 {"type":"BROADCAST_MESSAGE","data":{...}} 即为聊天消息推送
```

## 当前进度

- [x] 工程骨架（统一响应 Result、业务异常、全局异常处理、CORS、WebSocket）
- [x] 结构：5 个入口（`/`、`/api/register`、`/api/login`、`/api/chat/**`、`/api/friend/**`）+ 3 个业务 Service
- [x] **板块1 注册登录**：注册 / 登录（查用户名密码 + 写登录日志）/ 查自己的数据 /
      **改自己的资料（PUT /api/me）** / **改自己的密码（PUT /api/me/password）** / 查别人的数据
- [x] **板块2 聊天室**：**建群（POST /api/chat/room，创建者自动入群当群主）** /
      **加群（/join）· 退群（/leave）** / 成员列表 / 私聊 / 发消息（落库 + WS 广播）/ 聊天记录
- [x] **板块3 好友**：列表 / 在线 / 申请（WS 通知）/ 同意 / 拒绝 / 删除
- [x] 旧库对齐：7 张旧表实体映射（LocalDateTime 对齐 DATETIME），真实旧数据直连验证通过
- [x] **身份就是 URL 上的 `userId` 参数**（`/api/me?userId=1`），没有拦截器、没有请求头、没有 Token
- [x] 端到端验证：首页 → 注册 → 登录 → 改资料/改密码 → 建群 → 加群/退群 → 好友 → 发消息 → WS 收到广播

### 为了"只做基本功能"，刻意没做的东西

| 没做 | 说明 |
|---|---|
| 认证 / 鉴权 | 谁都能用任何 userId 请求（自己用，无所谓） |
| 事务（@Transactional） | 建群等"两步操作"万一半路失败，可能留下不完整数据；真遇到了再加 |
| 各种参数校验 | 比如"不能加自己为好友""只能加入群聊""重复加群报错"——前端不会这么点，就不写 |
| 好友申请落库 | 对方不在线就收不到申请（旧系统也是这样） |
| 离线消息 | 不在线就收不到推送，重新打开聊天记录能看到 |
| 空值/异常兜底 | 未遇到的情况不处理 |

> 想加回哪一条，跟我说一声就行。
