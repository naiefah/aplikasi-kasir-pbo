package ui;

import dao.MenuDAO;
import model.Menu;
import net.miginfocom.swing.MigLayout;
import websocket.RealtimeClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;

public class MenuListFrame extends JFrame {

    JTable table;
    DefaultTableModel model;
    JComboBox<String> cbFilterKategori;

    @SuppressWarnings("unused")
    private HistoryFrame historyFrame;

    public MenuListFrame() {
        setTitle("Admin - Daftar Menu");
        setSize(780, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new MigLayout(
                "fill, insets 20",
                "[grow]",
                "[]10[grow][]"
        ));
        setContentPane(root);

        // ================= HEADER =================
        JLabel title = new JLabel("DAFTAR MENU (ADMIN)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        cbFilterKategori = new JComboBox<>(new String[]{
                "Semua", "Coffee", "Non-Coffee", "Cemilan", "Dessert", "Paket Makanan"
        });

        JButton btnHistory = new JButton("Riwayat Transaksi");
        JButton btnLogout = new JButton("Logout");

        JPanel top = new JPanel(new MigLayout(
                "fill",
                "[left][grow][right]"
        ));
        top.add(title);
        top.add(new JLabel(), "grow");

        // 🔥 FILTER DI SAMPING RIWAYAT
        top.add(new JLabel("Filter:"));
        top.add(cbFilterKategori, "gapright 10");
        top.add(btnHistory, "split 2");
        top.add(btnLogout);

        root.add(top, "growx, wrap");

        // ================= TABLE =================
        model = new DefaultTableModel(
                new String[]{"ID", "Nama Menu", "Harga", "Kategori"}, 0
        );

        table = new JTable(model);
        table.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Data Menu"));

        root.add(scroll, "grow, wrap");

        // ================= ACTION =================
        JButton btnTambah = new JButton("Tambah Menu");
        JButton btnEdit = new JButton("Edit Menu");
        JButton btnHapus = new JButton("Hapus Menu");

        JPanel action = new JPanel(new MigLayout(
                "fill",
                "[grow][right]"
        ));
        action.add(new JLabel(), "grow");
        action.add(btnTambah, "split 3");
        action.add(btnEdit);
        action.add(btnHapus);

        root.add(action, "growx");

        // ================= LOAD DATA =================
        loadMenu();

        // ================= EVENT =================
        btnTambah.addActionListener(e -> new MenuFormFrame(this, null));
        btnEdit.addActionListener(e -> editMenu());
        btnHapus.addActionListener(e -> hapusMenu());

        // 🔥 FILTER ACTION
        cbFilterKategori.addActionListener(e -> loadMenu());

        btnHistory.addActionListener(e -> {
            historyFrame = new HistoryFrame(this);
            setVisible(false);
        });

        btnLogout.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        // ================= WEBSOCKET =================
        RealtimeClient.init();
        RealtimeClient.addListener(msg -> {
            if ("MENU_UPDATE".equals(msg)) {
                SwingUtilities.invokeLater(this::loadMenu);
            }
        });

        setVisible(true);
    }

    // ================= METHOD =================
    public void loadMenu() {
        model.setRowCount(0);
        try {
            MenuDAO dao = new MenuDAO();
            String kategori = cbFilterKategori.getSelectedItem().toString();

            ResultSet rs = kategori.equals("Semua")
                    ? dao.getAllMenu()
                    : dao.getMenuByKategori(kategori);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_menu"),
                        rs.getString("nama_menu"),
                        rs.getDouble("harga"),
                        rs.getString("kategori")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal load menu");
        }
    }

    private void editMenu() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih menu terlebih dahulu");
            return;
        }

        Menu menu = new Menu(
                Integer.parseInt(model.getValueAt(row, 0).toString()),
                model.getValueAt(row, 1).toString(),
                model.getValueAt(row, 3).toString(), // kategori
                Double.parseDouble(model.getValueAt(row, 2).toString())
        );

        new MenuFormFrame(this, menu);
    }

    private void hapusMenu() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih menu terlebih dahulu");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Hapus menu ini?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            new MenuDAO().deleteMenu(id);
            RealtimeClient.sendMessage("MENU_UPDATE");
        }
    }
}
