package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/tubes_pbo"
                   + "?useSSL=false"
                   + "&serverTimezone=UTC";

        return DriverManager.getConnection(url, "root", "");
    }
}