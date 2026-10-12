package com.hiya.common;

/**
 * 业务异常：代表"业务规则不满足"的错误。
 *
 * 为什么需要自定义异常？
 *   Service 里遇到业务错误时（如用户名已存在），可以直接
 *   throw new BusinessException(ResultCode.USERNAME_EXISTS)，
 *   而不需要在每一层写 if/else 判断再手动组装 Result 返回。
 *   异常会被后面的 GlobalExceptionHandler 统一捕获并转成 Result 返回给前端，
 *   这样 Service 代码可以专注于业务逻辑本身，非常清爽。
 *
 * 它继承 RuntimeException（运行时异常）而不是 Exception（受检异常）：
 *   - 方法签名上不需要强制 throws，代码更简洁；
 *   - Spring 事务回滚默认只针对运行时异常（保证数据一致性）。
 */
public class BusinessException extends RuntimeException {

    /**
     * 对应的业务状态码
     */
    private final Integer code;

    /**
     * 根据枚举构造：使用枚举里预设的错误码和提示
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 根据枚举 + 自定义消息构造：错误码固定，提示信息可以更具体
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
