import com.sun.net.httpserver.HttpServer;
import handler.RootHandler;
import handler.HelloHandler;
import handler.EchoHandler;
import handler.UsersHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Hiya HTTP Server entry point.
 *
 * # 编译
 *   javac -d bin -cp "lib\gson-2.13.2.jar" src\App.java
 *
 * # 运行（默认 8080 端口）
 *   java -cp "bin;lib\gson-2.13.2.jar" App
 *
 * # 指定端口
 *   java -cp "bin;lib\gson-2.13.2.jar" App 9090
 */
public class App {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Register routes
        server.createContext("/", new RootHandler());
        server.createContext("/api/hello", new HelloHandler());
        server.createContext("/api/echo", new EchoHandler());
        server.createContext("/api/users", new UsersHandler());

        // Use a thread pool for handling requests
        server.setExecutor(Executors.newFixedThreadPool(4));

        server.start();
        System.out.println("=== Hiya HTTP Server started on http://localhost:" + port + " ===");
        System.out.println("Try these endpoints:");
        System.out.println("  GET  /api/hello            - simple greeting");
        System.out.println("  GET  /api/hello?name=World  - greeting with name");
        System.out.println("  POST /api/echo              - echo back JSON body");
        System.out.println("  GET  /api/users             - list users");
        System.out.println("  POST /api/users             - add a user");
        System.out.println("  GET  /                      - server info");
    }
}
