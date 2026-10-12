package hiya.server.handler;

import hiya.common.command.Command;

/**
 * 所有业务Handler的公共接口，定义处理指令的统一方法。
 */
public interface Handler {
    /**
     * 处理客户端发来的指令。
     * @param command 客户端发来的指令对象
     */
    void handle(Command command);
}