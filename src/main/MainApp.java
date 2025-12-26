package main;

import com.formdev.flatlaf.FlatLightLaf;
import ui.LoginFrame;
import websocket.RealtimeServer;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {

      
        new Thread(() -> {
            RealtimeServer server = new RealtimeServer();
            server.start();
            System.out.println("✅ WebSocket Server running...");
        }).start();

    
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

      
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}