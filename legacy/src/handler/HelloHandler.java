package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.HttpUtil;
import static util.HttpUtil.sendJson;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** GET /api/hello?name=xxx */
public class HelloHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpUtil.handleMethods(exchange, e -> {
            Map<String, String> query = HttpUtil.parseQuery(e.getRequestURI().getQuery());
            String name = query.getOrDefault("name", "Guest");
            Map<String, String> resp = new LinkedHashMap<>();
            resp.put("message", "Hello, " + name + "!");
            resp.put("timestamp", String.valueOf(System.currentTimeMillis()));
            resp.put("code", "200");
            sendJson(e, 200, resp);
        }, null);
    }
}
