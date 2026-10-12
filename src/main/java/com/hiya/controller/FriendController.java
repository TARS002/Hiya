package com.hiya.controller;

import com.hiya.common.Result;
import com.hiya.dto.AddFriendRequest;
import com.hiya.dto.FriendDecisionRequest;
import com.hiya.entity.Friend;
import com.hiya.service.FriendService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 好友控制器。
 *
 *   GET    /api/friend/list?userId=1              好友列表
 *   GET    /api/friend/online                     在线用户 ID 列表
 *   POST   /api/friend/request?userId=1           申请加好友 {friendId}
 *   POST   /api/friend/accept?userId=1            同意申请 {requesterId}
 *   POST   /api/friend/reject?userId=1            拒绝申请 {requesterId}
 *   DELETE /api/friend/3?userId=1                 删除好友
 *
 * 申请 → 处理 的流程：
 *   A 申请加 B → 服务器通过 WebSocket 通知 B（B 不在线就收不到）
 *   B 同意/拒绝 → 服务器通过 WebSocket 告诉 A 结果
 */
@RestController
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    /** 查好友列表：GET /api/friend/list?userId=1 */
    @GetMapping("/list")
    public Result<List<Friend>> list(@RequestParam("userId") Long userId) {
        return Result.ok(friendService.getFriendList(userId));
    }

    /** 查在线用户：GET /api/friend/online */
    @GetMapping("/online")
    public Result<Set<Long>> online() {
        return Result.ok(friendService.getOnlineUserIds());
    }

    /** 申请加好友：POST /api/friend/request?userId=1，body {"friendId":3} */
    @PostMapping("/request")
    public Result<Void> request(@RequestParam("userId") Long userId,
                                @Valid @RequestBody AddFriendRequest request) {
        friendService.addFriendRequest(userId, request.getFriendId());
        return Result.ok();
    }

    /** 同意申请：POST /api/friend/accept?userId=1，body {"requesterId":3} */
    @PostMapping("/accept")
    public Result<Void> accept(@RequestParam("userId") Long userId,
                               @Valid @RequestBody FriendDecisionRequest request) {
        friendService.acceptFriendRequest(userId, request.getRequesterId());
        return Result.ok();
    }

    /** 拒绝申请：POST /api/friend/reject?userId=1，body {"requesterId":3} */
    @PostMapping("/reject")
    public Result<Void> reject(@RequestParam("userId") Long userId,
                               @Valid @RequestBody FriendDecisionRequest request) {
        friendService.rejectFriendRequest(userId, request.getRequesterId());
        return Result.ok();
    }

    /** 删除好友：DELETE /api/friend/3?userId=1 */
    @DeleteMapping("/{friendId}")
    public Result<Void> delete(@PathVariable("friendId") Long friendId,
                               @RequestParam("userId") Long userId) {
        friendService.deleteFriend(userId, friendId);
        return Result.ok();
    }
}
