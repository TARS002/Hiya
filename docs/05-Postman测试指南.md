# Postman 测试指南（已备好导入文件）

不想手敲的话，直接用我生成的两个文件，**导入后 25 个接口全都在**：

| 文件 | 作用 |
|---|---|
| `postman/Hiya.postman_collection.json` | 接口集合（5 个分组、25 个请求） |
| `postman/Hiya-local.postman_environment.json` | 环境变量（baseUrl、userId、chatId…） |

---

## 一、导入（1 分钟）

1. 打开 Postman → 左上角 **`Import`**
2. 把上面两个文件（或 `postman` 整个文件夹）拖进去 → **Import**
3. 右上角的 **环境下拉框** 选 **`Hiya 本地环境`**（不选的话 `{{baseUrl}}` 不会生效）

> Apifox 也能导：`导入` → 选 Postman → 选集合文件，操作一样。

## 二、使用顺序

```
① 先跑「板块1 注册登录 → 1.2 登录」（旧账号 bababoye/bababoye）
      ↳ 这个请求带一段脚本，登录成功会自动把 userId 存进环境变量
② 之后随便点哪个请求都能直接跑（URL 里的 {{userId}} 已经自动填好了）
```

想看脚本：点开 `1.2 登录` → **Scripts → Post-response**，内容就是"把返回的 userId 存起来"：

```js
const res = pm.response.json();
if (res.code === 200) {
    pm.environment.set('userId', res.data.userId);
}
```

同理，**`2.2 建群`** 成功后会自动把新 `chatId` 存进变量，后面 `加群 / 退群 / 发消息 / 聊天记录` 用的都是这个新群。

## 三、集合内容（5 组 25 个请求）

| 分组 | 请求 |
|---|---|
| **00 首页** | 首页（服务状态 + 接口清单） |
| **板块1 注册登录** | 1.1 注册 / 1.2 登录 / 1.3 查自己 / 1.4 查自己+资料 / 1.5 改资料 / 1.6 改密码 / 1.7 查别人 |
| **板块2 聊天室** | 2.1 会话列表 / 2.2 建群 / 2.3 私聊 / 2.4 加群 / 2.5 退群 / 2.6 群成员 / 2.7 聊天记录 / 2.8 发消息 |
| **板块3 好友** | 3.1 好友列表 / 3.2 在线用户 / 3.3 申请 / 3.4 同意 / 3.5 拒绝 / 3.6 删除 |
| **反例** | X1 忘了带 userId / X2 密码写错 / X3 用户名留空 |

路径里的 `:chatId`、`:friendId`、`:targetUserId` 都是**路径变量**，值取自环境变量，改一处全局生效。

## 四、环境变量（改这里就行）

左侧 **`Environments` → `Hiya 本地环境`**：

| 变量 | 默认值 | 说明 |
|---|---|---|
| `baseUrl` | `http://localhost:8080` | 换端口只改这一处 |
| `userId` | `1` | 登录/注册后自动更新 |
| `chatId` | `1` | 建群后自动更新；也可手动改成已有会话（1=kittis，2=memes） |
| `friendId` | `3` | 好友相关接口用 |
| `targetUserId` | `2` | 查别人资料用（2=Mambo，3=ugliOrange） |

## 五、WebSocket 怎么用 Postman 测

集合里放不了 WebSocket，需要手动建一个：

1. Postman 左上 **`New` → `WebSocket Request`**
2. 地址填：`ws://localhost:8080/ws?userId=1`
3. 点 **`Connect`** → 状态显示 `Connected`
4. 在消息框输入 `{"type":"ping"}` → 点 **Send** → 应该收到 `{"type":"pong"}`

**测实时推送**：保持这个 WebSocket 连着，然后用集合里的 **`2.8 发消息`**（给会话 1 或你建的群）发一条消息 —— WebSocket 窗口会立刻弹出：

```json
{"type":"BROADCAST_MESSAGE","data":{"chatId":1,"senderId":1,"content":"...","sendTime":"..."}}
```

## 六、两个必须记住的点

1. **业务错误的 HTTP 状态码也是 200**，错误码在 body 里：
   ```json
   {"code":1002,"message":"用户名或密码错误","data":null}
   ```
   所以别只看 Postman 右上角的 `200 OK`，要看 body 的 `code`。
2. **身份是 URL 上的 `?userId=`**，不是请求头。需要身份的接口忘了带，会返回：
   ```json
   {"code":400,"message":"缺少参数: userId","data":null}
   ```

## 七、常见报错对照

| 现象 | 原因 |
|---|---|
| `{"code":400,"message":"请求体 JSON 格式错误"}` | Body 没选 `raw + JSON`，或 JSON 括号/引号写错 |
| `{"code":400,"message":"缺少参数: userId"}` | URL 上少了 `?userId=` |
| `{"code":1002,...}` | 用户名或密码不对 |
| HTTP 405 | 方法选错（如登录必须 POST） |
| HTTP 404 | 地址拼错（注意 `/api` 前缀） |
| `Could not send request` | 服务端没启动，或 `baseUrl` 端口不对 |
| 变量没被替换（地址里还是 `{{userId}}`） | 右上角没选「Hiya 本地环境」 |

> **已验证**：集合里所有只读请求（首页、登录、查自己、查资料、会话、成员、记录、好友、在线 + 两个反例）都用脚本实跑过，返回 code 全部符合预期。
