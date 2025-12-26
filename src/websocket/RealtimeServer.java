package websocket;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class RealtimeServer extends WebSocketServer {

    private static Set<WebSocket> clients = new CopyOnWriteArraySet<>();

    public RealtimeServer() {
        super(new InetSocketAddress(8887));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.add(conn);
        System.out.println("Client connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
        System.out.println("Client disconnected");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message received: " + message);
        broadcastMessage(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket Server started on port 8887");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    // 🔥 JANGAN PAKAI NAMA broadcast()
    private void broadcastMessage(String msg) {
        for (WebSocket client : clients) {
            if (client.isOpen()) {
                client.send(msg);
            }
        }
    }

    public void start() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }
}