package hiya.server;

import hiya.server.server.HiyaServer;

/**
 * 服务器端启动类。创建HiyaServer实例并启动。
 */
public class ServerApp {
    /**
     * 服务器程序入口。
     * @param args 命令行参数，可指定端口号
     */
    public static void main(String[] args) {
        int port = 8080;
        // 创建服务器并启动
        HiyaServer server = new HiyaServer(port);
        server.start();
    }
}