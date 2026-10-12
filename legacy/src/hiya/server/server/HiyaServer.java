package hiya.server.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hiya服务器主类。监听指定端口，接受客户端连接，
 * 为每个客户端创建独立的ClientHandler线程进行处理。
 * 维护所有在线客户端的连接映射表。
 */
public class HiyaServer {
    private ServerSocket serverSocket;                                    // 服务器Socket，用于监听端口
    private ConcurrentHashMap<Long, ClientHandler> onlineClients;         // 在线客户端映射表，key为用户ID，value为对应的连接处理器
    private int port;                                                     // 服务器监听端口
    private volatile boolean running = false;                             // 服务器是否正在运行的标志

    /**
     * 构造服务器，初始化在线客户端映射表。
     * @param port 监听端口号
     */
    public HiyaServer(int port) {
        this.port = port;
        this.onlineClients = new ConcurrentHashMap<>(); // 使用并发安全的HashMap
    }

    /**
     * 启动服务器。创建ServerSocket并循环接受客户端连接。
     */
    public void start() {
        try {
            // 创建ServerSocket，绑定到指定端口
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("Hiya服务器已启动，监听端口: " + port);

            // 循环接受客户端连接
            while (running) {
                // 阻塞等待客户端连接
                java.net.Socket clientSocket = serverSocket.accept();
                System.out.println("客户端已连接: " + clientSocket.getInetAddress());
                // 为新连接创建ClientHandler线程
                ClientHandler handler = new ClientHandler(clientSocket, this);
                new Thread(handler).start(); // 启动处理线程
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        } finally {
            stop();
        }
    }

    /**
     * 停止服务器，关闭ServerSocket并清理资源。
     */
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // 关闭ServerSocket
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Hiya服务器已停止");
    }

    /**
     * 获取在线客户端映射表。
     * @return 在线客户端的ConcurrentHashMap
     */
    public ConcurrentHashMap<Long, ClientHandler> getOnlineClients() {
        return onlineClients;
    }
}