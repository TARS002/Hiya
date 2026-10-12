package com.hiya.controller;

import com.hiya.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页
 */
@RestController
public class HomeController {

    /** 时间格式化器（模式与实体里的时间字段保持一致） */
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * GET /
     * 返回服务状态 + 接口清单。
     */
    @GetMapping("/")
    public Result<Map<String, Object>> index() {
        // LinkedHashMap：保留放入顺序，返回的 JSON 字段顺序和代码一致，方便阅读
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("service", "Hiya 聊天服务端");
        data.put("status", "运行中");
        data.put("time", LocalDateTime.now().format(TIME_FORMATTER));
        data.put("testPage", "http://localhost:8080/test.html");
        data.put("identity", "除 /、/api/register、/api/login 外，请求需带请求头 X-User-Id；WebSocket 用 ?userId=");
        data.put("apis", List.of(
                "===== 板块1：注册登录 =====",
                "POST   /api/register              注册 {userName, password}",
                "POST   /api/login                 登录（返回 userId）{userName, password}",
                "GET    /api/me                    查询我自己的账号信息",
                "GET    /api/me/info               查询我自己的账号+资料",
                "PUT    /api/me                    修改我自己的资料 {nickName?, avatarUrl?, signature?, gender?, birthday?}",
                "PUT    /api/me/password           修改我自己的密码 {oldPassword, newPassword}",
                "GET    /api/info/{targetUserId}   查询别人的资料",
                "===== 板块2：聊天室 =====",
                "GET    /api/chat/list             我加入的所有会话（私聊+群聊）",
                "POST   /api/chat/room             创建群聊 {chatTitle}",
                "POST   /api/chat/private          创建/获取私聊 {friendId}",
                "GET    /api/chat/{chatId}/members 查看群成员",
                "POST   /api/chat/{chatId}/join    加入群聊",
                "POST   /api/chat/{chatId}/leave   退出群聊",
                "GET    /api/chat/{chatId}/messages 聊天记录",
                "POST   /api/chat/{chatId}/messages 发送消息 {content}",
                "===== 板块3：好友 =====",
                "GET    /api/friend/list           好友列表",
                "GET    /api/friend/online         在线用户 ID 列表",
                "POST   /api/friend/request        申请加好友 {friendId}",
                "POST   /api/friend/accept         同意好友申请 {requesterId}",
                "POST   /api/friend/reject         拒绝好友申请 {requesterId}",
                "DELETE /api/friend/{friendId}     删除好友",
                "===== 实时通道 =====",
                "WS     /ws?userId=1               WebSocket 连接（收好友通知、消息广播）"
        ));
        return Result.ok(data);
    }
}
