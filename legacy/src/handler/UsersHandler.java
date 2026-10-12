package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import util.HttpUtil;
import static util.HttpUtil.sendJson;

import java.io.IOException;
import java.util.*;

/** GET /api/users – list all users; POST /api/users – add a user */
public class UsersHandler implements HttpHandler {

    /** In-memory user store for demo purposes */
    private static final List<Map<String, Object>> users = new ArrayList<>();

    static {
        users.add(Map.of("id", 1, "name", "Alice", "email", "alice@example.com"));
        users.add(Map.of("id", 2, "name", "Bob", "email", "bob@example.com"));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpUtil.handleMethods(exchange,
                // GET handler
                e -> {
                    Map<String, Object> resp = new LinkedHashMap<>();
                    resp.put("users", users);
                    resp.put("count", users.size());
                    sendJson(e, 200, resp);
                },
                // POST handler
                e -> {
                    String body = HttpUtil.readBody(e);
                    try {
                        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                        String name = json.has("name") ? json.get("name").getAsString() : null;
                        String email = json.has("email") ? json.get("email").getAsString() : null;

                        if (name == null || name.isBlank()) {
                            sendJson(e, 400, Map.of("error", "name is required"));
                            return;
                        }

                        Map<String, Object> newUser = new LinkedHashMap<>();
                        newUser.put("id", users.size() + 1);
                        newUser.put("name", name);
                        newUser.put("email", email != null ? email : "");
                        users.add(newUser);

                        sendJson(e, 201, Map.of(
                                "message", "User created",
                                "user", newUser
                        ));
                    } catch (JsonSyntaxException | IllegalStateException ex) {
                        sendJson(e, 400, Map.of("error", "Invalid JSON body"));
                    }
                });
    }
}
