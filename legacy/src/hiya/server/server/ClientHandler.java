package hiya.server.server;

import hiya.common.command.*;
import hiya.server.handler.CommandDispatcher;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 客户端连接处理器，每个客户端连接对应一个该类的实例，运行在独立线程中。
 * 负责与单个客户端的网络通信：接收消息并交给CommandDispatcher处理，
 * 提供sendResponse和sendNotify方法方便Handler构建响应并发送。
 */
public class ClientHandler implements Runnable {
    private Socket socket;                    // 与该客户端通信的Socket
    private BufferedReader reader;            // 输入流读取器
    private PrintWriter writer;               // 输出流写入器
    private HiyaServer server;                // 服务器实例引用，用于访问在线客户端列表
    private CommandDispatcher dispatcher;     // 指令分发器
    private Long userId;                      // 该连接对应的用户ID，登录成功后设置
    private volatile boolean running = false; // 该连接是否正在运行的标志

    /**
     * 构造客户端连接处理器。
     * @param socket 客户端Socket连接
     * @param server 服务器实例
     */
    public ClientHandler(Socket socket, HiyaServer server) {
        this.socket = socket;
        this.server = server;
        // 创建指令分发器，传入自身引用
        this.dispatcher = new CommandDispatcher(this);
    }

    /**
     * 线程运行方法。初始化输入输出流，循环读取客户端发来的消息并分发处理。
     */
    @Override
    public void run() {
        try {
            // 初始化输入输出流
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            running = true;

            String line; // 存储读取到的一行消息
            // 循环读取客户端消息
            while (running && (line = reader.readLine()) != null) {
                try {
                    // 将JSON字符串解析为Command对象
                    Command command = Command.fromJson(line);
                    // 将指令交给分发器处理
                    dispatcher.dispatch(command);
                } catch (Exception e) {
                    // 解析或处理异常，打印但不中断连接
                    System.err.println("处理消息异常: " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // 连接异常
            System.out.println("用户" + userId + "连接异常，将其下线。设计失误，没有设计用户主动下线操作。");
            // e.printStackTrace();
        } finally {
            // 连接结束，清理资源
            cleanup();
        }
    }

    /**
     * 构建并发送一条RESPONSE类型的指令给客户端。
     * @param requesterId 接收者的用户ID
     * @param cmd 指令类型
     * @param code 结果码
     * @param data 携带的数据
     */
    public void sendResponse(Long requesterId, Cmd cmd, ResultCode code, Object data) {
        // 构建RESPONSE类型的指令
        Command response = new Command(requesterId, MsgType.RESPONSE, code, cmd, data);
        // 发送给客户端
        send(response);
    }

    /**
     * 构建并发送一条NOTIFY类型的指令给客户端。
     * @param targetId 接收者的用户ID
     * @param cmd 指令类型
     * @param data 携带的数据
     */
    public void sendNotify(Long targetId, Cmd cmd, Object data) {
        // 构建NOTIFY类型的指令
        Command notify = new Command(targetId, MsgType.NOTIFY, ResultCode.SUCCESS, cmd, data);
        // 发送给客户端
        send(notify);
    }

    /**
     * 将Command对象序列化为JSON字符串并通过输出流发送。
     * @param command 要发送的指令对象
     */
    private void send(Command command) {
        if (writer != null && running) {
            // 序列化为JSON并发送，末尾加换行符作为消息分隔
            writer.println(command.toJson());
        }
    }

    /**
     * 清理连接资源。从在线客户端列表中移除，关闭输入输出流和Socket。
     */
    private void cleanup() {
        running = false;
        // 如果已登录，从在线列表中移除
        if (userId != null) {
            server.getOnlineClients().remove(userId);
            System.out.println("用户[" + userId + "]已下线");
        }
        // 关闭资源
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取该连接绑定的用户ID。
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置该连接绑定的用户ID，登录成功后调用。
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取服务器实例。
     * @return HiyaServer实例
     */
    public HiyaServer getServer() {
        return server;
    }
}