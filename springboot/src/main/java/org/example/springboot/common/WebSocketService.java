package org.example.springboot.common;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chatServer/{userId}")
@Component
public class WebSocketService {
    /**
     * 打印日志
     */
    private static final Logger log =  LoggerFactory.getLogger(WebSocketService.class);


    /** userId -> WebSocket Session */
    private static final Map<Integer, Session> ONLINE_USERS = new ConcurrentHashMap<>();



    /**
     * 建立连接
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Integer userId) {

        ONLINE_USERS.put(userId, session);
        log.info("用户加入，userId={}",userId);
        broadcastSession();
    }

    /**
     * 接收消息
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") Integer userId)  {
       log.info("服务端收到用户userId={}的消息:{}", userId,message);

        JSONObject obj = JSONUtil.parseObj(message);

        String text = obj.getStr("text");
        String type = obj.getStr("type");
        String time = obj.getStr("time");
        Integer fromUserId = obj.getInt("fromUserId");
        Integer toUserId = obj.getInt("toUserId");


        Session toSession = ONLINE_USERS.get(toUserId);

        if (toSession != null && toSession.isOpen()) {
            JSONObject jsonObject =new JSONObject();
            jsonObject.set("text",text);
            jsonObject.set("type",type);
            jsonObject.set("time",time);
            jsonObject.set("fromUserId",fromUserId);
            jsonObject.set("toUserId",toUserId);
            jsonObject.set("messageType","chat");

            sendMessage(toSession, jsonObject.toString());
            log.info("发送给用户userId={},消息：{}",toUserId,jsonObject.toString());

        }else {
            log.info("发送失败,未找到用户userId={}的session",toUserId);
        }

    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session session, @PathParam("userId") Integer userId) {
        ONLINE_USERS.remove(userId, session);
        log.info("用户退出，userId={}", userId);
        broadcastSession();
    }

    /**
     * 异常处理
     */
    @OnError
    public void onError(Session session, Throwable error) {
       log.error(error.getMessage());
    }

    /**
     * 广播给所有在线连接：当前所有在线用户id
     */
    private void broadcastSession(){
        JSONObject jsonObject = new JSONObject();
        //获取当前所有在线用户id
        jsonObject.set("userIds", ONLINE_USERS.keySet());
        jsonObject.set("messageType","broadcast");
        //广播给所有在线连接
        for (Session session : ONLINE_USERS.values()) {
            sendMessage(session, jsonObject.toString());
        }
        log.info("广播给所有在线连接：当前所有在线用户id");
    }

    public void sendMessage(Session toSession, String message) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getBasicRemote().sendText(message);
        }catch (Exception e){
            log.error("服务端发送消息给客户端失败",e);
        }

    }

}