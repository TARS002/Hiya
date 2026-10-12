package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.HttpUtil;
import static util.HttpUtil.sendJson;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** GET / – server info */
public class RootHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpUtil.handleMethods(exchange, e -> {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("server", "Hiya HTTP Server");
            info.put("version", "1.0");
            info.put("endpoints", List.of(
                    "GET  /api/hello",
                    "GET  /api/hello?name=...",
                    "POST /api/echo",
                    "GET  /api/users",
                    "POST /api/users"
            ));
            sendJson(e, 200, info);
        }, null);
    }
}
