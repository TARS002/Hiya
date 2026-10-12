package com.hiya.common;

/**
 * 统一响应体（Result / ApiResponse）。
 *
 * 为什么需要它？
 *   前后端分离项目中，前端需要一种"稳定、可预期"的响应格式来判断请求是否成功。
 *   如果不统一，每个接口各自返回不同的结构（有的直接返回数据、有的返回 Map），
 *   前端每个接口都要单独写解析逻辑，非常痛苦。
 *
 * 统一后的格式约定（JSON 示例）：
 *   {
 *     "code": 200,           // 业务状态码，200 表示成功，非 200 表示失败（见 ResultCode）
 *     "message": "操作成功",  // 给前端展示或排查用的提示信息
 *     "data": { ... }        // 真正的业务数据，可以是对象、数组；失败时通常为 null
 *   }
 *
 * 泛型 <T> 表示 data 字段的类型，让返回的数据在编译期就有类型约束，避免手抖写错。
 *
 * 使用方式：
 *   return Result.ok(user);                          // 成功并携带数据
 *   return Result.ok();                              // 成功但不需要返回数据（如删除操作）
 *   return Result.error(ResultCode.LOGIN_FAILED);    // 业务失败（用枚举预设的错误码）
 *
 * @param <T> data 的数据类型
 */
public class Result<T> {

    /**
     * 业务状态码：200 成功，其余见 ResultCode 枚举
     */
    private Integer code;

    /**
     * 提示信息：成功时为"操作成功"，失败时为具体原因
     */
    private String message;

    /**
     * 业务数据：成功时携带，失败时通常为 null
     */
    private T data;

    // 私有构造器：不允许外部 new，统一走下面的静态工厂方法，保证状态可控
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    
    /**
     * 成功 + 数据：最常用的成功返回。
     * 
     * @param <T>
     * @param data
     * @return
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功 + 无数据：适合删除、修改这类不需要回传数据的操作
     * 
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 按枚举失败：适合常见业务错误（用户名已存在、登录失败等）
     * 
     * @param <T>
     * @param resultCode
     * @return
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 自定义错误码 + 自定义消息：适合需要动态提示的失败场景（如参数错误带具体原因）
     * 
     * @param <T>
     * @param code
     * @param message
     * @return
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // ===== Getter / Setter =====
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
