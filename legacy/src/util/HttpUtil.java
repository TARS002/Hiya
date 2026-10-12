package util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Utility helpers for HTTP request handling.
 */
public class HttpUtil {

    /** Shared Gson instance for JSON serialization. */
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Functional interface for route handlers. */
    @FunctionalInterface
    public interface Handler {
        void handle(HttpExchange exchange) throws IOException;
    }

    /** Parse query string into a map. */
    public static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isBlank()) return params;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String value = kv.length > 1
                    ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
                    : "";
            params.put(key, value);
        }
        return params;
    }

    /** Read the full request body as a UTF-8 string. */
    public static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    /** Send a JSON response. */
    public static void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        String json = GSON.toJson(body);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");
        headers.set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /** Send a plain-text response. */
    public static void sendText(HttpExchange exchange, int statusCode, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "text/plain; charset=UTF-8");
        headers.set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /** Handle CORS preflight (OPTIONS) requests. */
    public static void handleCors(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(204, -1); // No Content
    }

    /**
     * Resolve the request method and delegate to the appropriate handler,
     * or return 405 for unsupported methods.
     */
    public static void handleMethods(HttpExchange exchange,
                                      Handler getHandler,
                                      Handler postHandler) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        switch (method) {
            case "OPTIONS" -> handleCors(exchange);
            case "GET" -> {
                if (getHandler != null) getHandler.handle(exchange);
                else sendText(exchange, 405, "GET not supported on this endpoint");
            }
            case "POST" -> {
                if (postHandler != null) postHandler.handle(exchange);
                else sendText(exchange, 405, "POST not supported on this endpoint");
            }
            default -> sendText(exchange, 405, "Method " + method + " not allowed");
        }
    }
}
