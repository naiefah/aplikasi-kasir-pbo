package dao;

import config.DatabaseConnection;
import java.sql.*;
import model.Menu;

public class MenuDAO {

    public void updateMenu(Menu menu) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE menu SET nama_menu=?, kategori=?, harga=? WHERE id_menu=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, menu.getNamaMenu());
            ps.setString(2, menu.getKategori());
            ps.setDouble(3, menu.getHarga());
            ps.setInt(4, menu.getIdMenu());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMenu(int id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM menu WHERE id_menu=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getAllMenu() throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        Statement st = conn.createStatement();
        return st.executeQuery("SELECT * FROM menu");
    }

    public ResultSet getMenuByKategori(String kategori) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM menu WHERE kategori=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, kategori);
        return ps.executeQuery();
    }

    public void insertMenu(Menu menu) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertMenu'");
    }
}