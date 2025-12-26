package dao;

import config.DatabaseConnection;
import model.User;
import java.sql.*;

public class UserDAO {

    public User login(String username, String password) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("=== KONEKSI DATABASE BERHASIL ===");

            // CEK ISI TABEL USERS
            Statement st = conn.createStatement();
            ResultSet rsAll = st.executeQuery("SELECT * FROM users");

            System.out.println("=== DATA DI DATABASE ===");
            while (rsAll.next()) {
                System.out.println(
                    rsAll.getInt("id_user") + " | " +
                    rsAll.getString("username") + " | " +
                    rsAll.getString("password") + " | " +
                    rsAll.getString("role")
                );
            }

            // LOGIN QUERY
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("=== LOGIN BERHASIL ===");
                return new User(
                    rs.getString("username"),
                    rs.getString("role")
                );
            } else {
                System.out.println("=== LOGIN GAGAL: DATA TIDAK DITEMUKAN ===");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}