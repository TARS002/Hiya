package hiya.common.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 指令封装类，所有客户端与服务器之间的通信都以该对象的JSON形式传输。
 * 包含请求者ID、消息类型、结果码、指令类型和携带数据五个核心字段。
 * {
 *   "requesterId": 123,
 *   "msgType": "CHAT_MESSAGE",
 *   "code": 200,
 *   "cmd": "SEND_MESSAGE",
 *   "data": { ... }
 * }
 */
public class Command {
    private static final Gson gson = new GsonBuilder().serializeNulls().create(); // JSON序列化工具

    private Long requesterId;    // 请求者的用户ID，服务器据此识别是谁发起的请求
    private String msgType;      // 消息类型，对应MsgType枚举的名称
    private Integer code;        // 消息结果码，对应ResultCode枚举的数值
    private String cmd;          // 指令类型，对应Cmd枚举的名称
    private Object data;         // 指令携带的数据，可以是任意Java对象，GSON会自动序列化为JSON对象

    public Command() {
    }

    /**
     * 构建一条指令。
     * @param requesterId 请求者ID
     * @param msgType 消息类型枚举
     * @param code 结果码枚举
     * @param cmd 指令类型枚举
     * @param data 携带的数据对象
     */
    public Command(Long requesterId, MsgType msgType, ResultCode code, Cmd cmd, Object data) {
        this.requesterId = requesterId;
        this.msgType = msgType.name();
        this.code = code.getCode();
        this.cmd = cmd.name();
        this.data = data;
    }

    /**
     * 将该指令对象序列化为JSON字符串。
     * @return JSON格式的字符串
     */
    public String toJson() {
        return gson.toJson(this);
    }

    /**
     * 将JSON字符串反序列化为Command对象。
     * @param json JSON格式的字符串
     * @return 解析后的Command对象
     */
    public static Command fromJson(String json) {
        return gson.fromJson(json, Command.class);
    }

    /**
     * 将data字段反序列化为指定的Java类型。
     * @param dataClass 目标Java类的Class对象
     * @return 反序列化后的Java对象
     */
    public <T> T getDataAs(Class<T> dataClass) {
        return gson.fromJson(gson.toJson(this.data), dataClass);
    }
    
    public <T> List<T> getDataAsList(Class<T> elementClass) {
        // 通过TypeToken构造带有泛型参数的Type，例如List<Chat>、List<Message>、List<User>
        Type listType = TypeToken.getParameterized(List.class, elementClass).getType();
        return gson.fromJson(gson.toJson(this.data), listType);
    }

    /* 以下为getter、setter。 */
    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getCmd() { return cmd; }
    public void setCmd(String cmd) { this.cmd = cmd; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    /**
     * 获取MsgType枚举对象，方便业务层直接使用枚举进行判断。
     * @return 对应的MsgType枚举值
     */
    public MsgType getMsgTypeEnum() {
        return MsgType.valueOf(this.msgType);
    }

    /**
     * 获取Cmd枚举对象，方便业务层直接使用枚举进行判断。
     * @return 对应的Cmd枚举值
     */
    public Cmd getCmdEnum() {
        return Cmd.valueOf(this.cmd);
    }
}