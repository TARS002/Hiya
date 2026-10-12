package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import util.HttpUtil;
import static util.HttpUtil.sendJson;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** POST /api/echo – echo back whatever JSON was sent */
public class EchoHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpUtil.handleMethods(exchange, null, e -> {
            String body = HttpUtil.readBody(e);
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("method", "POST");
            resp.put("path", e.getRequestURI().getPath());
            resp.put("timestamp", System.currentTimeMillis());

            // Try to parse as JSON, otherwise echo raw string
            try {
                Object parsed = JsonParser.parseString(body);
                resp.put("received", parsed);
            } catch (JsonSyntaxException ex) {
                resp.put("received", body);
                resp.put("note", "Body was not valid JSON, echoed as raw string");
            }

            sendJson(e, 200, resp);
        });
    }
}
