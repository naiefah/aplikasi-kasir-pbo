package dao;

import config.DatabaseConnection;
import java.sql.*;

public class OrderDAO {

    public int insertOrder(double total) {
        int idOrder = 0;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO orders (tanggal, total) VALUES (CURDATE(), ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, total);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idOrder = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idOrder;
    }

    public void insertDetail(int idOrder, String menu, double harga, int qty, double subtotal) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO order_detail VALUES (NULL, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idOrder);
            ps.setString(2, menu);
            ps.setDouble(3, harga);
            ps.setInt(4, qty);
            ps.setDouble(5, subtotal);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getAllOrders() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement st = conn.createStatement();
            return st.executeQuery(
                "SELECT id_order, tanggal, total FROM orders ORDER BY id_order DESC"
            );
        } catch (Exception e) {
            return null;
        }
    }

    public ResultSet getOrdersByDate(String start, String end) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM orders WHERE tanggal BETWEEN ? AND ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, start);
        ps.setString(2, end);
        return ps.executeQuery();
    }

}