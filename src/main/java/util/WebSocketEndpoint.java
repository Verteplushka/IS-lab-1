package util;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/updates")
public class WebSocketEndpoint {
    // Множество всех подключённых клиентов
    private static final Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());

    // При подключении нового клиента
    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
    }

    // При получении сообщения от клиента
    @OnMessage
    public void onMessage(String message, Session session) {
        // Обработка сообщений от клиента (например, может быть пустым)
    }

    // При закрытии соединения с клиентом
    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
    }

    // При ошибке в соединении
    @OnError
    public void onError(Session session, Throwable throwable) {
        clients.remove(session);
    }

    // Метод для отправки обновлений всем подключённым клиентам
    public static void sendUpdateToAllClients(String message) {
        synchronized (clients) {
            for (Session client : clients) {
                if (client.isOpen()) {
                    try {
                        client.getBasicRemote().sendText(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
