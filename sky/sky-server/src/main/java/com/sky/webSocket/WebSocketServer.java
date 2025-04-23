package com.sky.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint("/ws/{sid}")
@Slf4j
public class WebSocketServer {
    private static Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 连接开启调用的方法
     *
     * @param sid
     * @param session
     */
    @OnOpen
    public void open(@PathParam("sid") String sid, Session session) {
        log.info("建立连接:{}", sid);
        sessionMap.put(sid, session);
    }

    /**
     * 收到客户端消息调用的方法
     *
     * @param message
     * @param sid
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("收到客户端:{}的信息:{}", sid, message);
    }

    /**
     * 连接关闭调用的方法
     *
     * @param sid
     */
    @OnClose
    public void close(@PathParam("sid") String sid) {
        log.info("断开连接:{}", sid);
        sessionMap.remove(sid);
    }

    /**
     * 向每个与服务端建立socket连接的客户端发送信息
     * @param message
     */
    public void sendToAllClient(String message)  {
        Collection<Session> sessionCollection = sessionMap.values();
        for (Session session : sessionCollection) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
