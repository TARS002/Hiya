package com.hiya.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器：把 Controller / Service 层抛出的异常统一"翻译"成 Result 返回给前端。
 *
 * 好处：Controller / Service 里不用写 try/catch，出错时前端拿到的格式永远一致。
 *
 * 目前处理 4 种情况：
 *   ① BusinessException            → 业务错误（用户名已注册、密码错、用户不存在…）
 *   ② 参数校验失败（@Valid）        → 400 + 具体哪一项不合格
 *   ③ 缺少必要参数（如忘了写 ?userId=）→ 400 + 提示少了哪个参数
 *   ④ 其他所有异常                  → 500，服务器日志里记详细堆栈，前端只看到一句提示
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 日志对象：SLF4J 是 Java 界事实标准的日志门面，SpringBoot 内置支持
     */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常：Service 层 throw 的 BusinessException 都会到这里。
     * 业务异常是"可预期"的错误，用 warn 级别记录即可，不需要打印完整堆栈。
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常：Controller 参数上加了 @Valid 注解后，
     * 如果校验不通过（比如用户名没填），Spring 会抛出 MethodArgumentNotValidException。
     * 这里把第一条校验失败的提示信息返回给前端。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        // getBindingResult() 里装着所有校验失败的信息，取第一条给用户看就够友好了
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("参数校验失败");
        return Result.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理请求体格式错误：前端传的 JSON 无法解析（缺括号、类型不对等）。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException e) {
        return Result.error(ResultCode.BAD_REQUEST.getCode(), "请求体 JSON 格式错误");
    }

    /**
     * 处理"缺参数"：比如调 /api/me 时忘了写 ?userId=1。
     * 不加这个的话会走到下面的兜底，前端只会看到"服务器内部错误"，很难排查。
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.error(ResultCode.BAD_REQUEST.getCode(), "缺少参数: " + e.getParameterName());
    }

    /**
     * 兜底异常：所有没被上面匹配到的异常都走这里，防止异常信息直接泄露给前端。
     * error 级别 + 完整堆栈，方便开发时排查。
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ResultCode.ERROR);
    }
}
