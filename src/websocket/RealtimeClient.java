package websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RealtimeClient extends WebSocketClient {

    private static RealtimeClient instance;
    private static final List<Consumer<String>> listeners = new ArrayList<>();

    private RealtimeClient() throws Exception {
        super(new URI("ws://localhost:8887"));
    }

    // 🔥 GANTI NAMA METHOD (BUKAN connect)
    public static synchronized void init() {
        try {
            if (instance == null) {
                instance = new RealtimeClient();
                instance.connect(); // ini MILIK WebSocketClient (instance)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    public static void sendMessage(String msg) {
        if (instance != null && instance.isOpen()) {
            instance.send(msg);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("✅ WebSocket Connected");
    }

    @Override
    public void onMessage(String message) {
        for (Consumer<String> listener : listeners) {
            listener.accept(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("❌ WebSocket Closed");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}