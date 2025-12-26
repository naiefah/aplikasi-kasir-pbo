package ui;

import dao.MenuDAO;
import model.Menu;
import net.miginfocom.swing.MigLayout;
import websocket.RealtimeClient;

import javax.swing.*;

public class MenuFormFrame extends JFrame {

    JTextField txtNama, txtHarga;
    JComboBox<String> cbKategori;

    MenuListFrame parent;
    Menu menu;

    public MenuFormFrame(MenuListFrame parent, Menu menu) {
        this.parent = parent;
        this.menu = menu;

        setTitle(menu == null ? "Tambah Menu" : "Edit Menu");
        setSize(400, 270);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new MigLayout(
                "wrap 2, insets 20",
                "[right][grow]"
        ));
        setContentPane(root);

        // ================= FORM =================
        txtNama = new JTextField();
        txtHarga = new JTextField();

        cbKategori = new JComboBox<>(new String[]{
                "Coffee", "Non-Coffee", "Cemilan", "Dessert", "Paket Makanan"
        });

        root.add(new JLabel("Nama Menu"));
        root.add(txtNama, "growx");

        root.add(new JLabel("Harga"));
        root.add(txtHarga, "growx");

        root.add(new JLabel("Kategori"));
        root.add(cbKategori, "growx");

        // ================= BUTTON =================
        JButton btnSimpan = new JButton("Simpan");
        JButton btnBatal  = new JButton("Batal");

        root.add(new JLabel(""));
        root.add(btnSimpan, "split 2, growx");
        root.add(btnBatal, "growx");

        // ================= DATA EDIT =================
        if (menu != null) {
            txtNama.setText(menu.getNamaMenu());
            txtHarga.setText(String.valueOf(menu.getHarga()));
            cbKategori.setSelectedItem(menu.getKategori());
        }

        // ================= EVENT =================
        btnSimpan.addActionListener(e -> simpan());
        btnBatal.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void simpan() {
        try {
            String nama = txtNama.getText();
            String kategori = cbKategori.getSelectedItem().toString();
            double harga = Double.parseDouble(txtHarga.getText());

            if (menu == null) {
                new MenuDAO().insertMenu(new Menu(0, nama, kategori, harga));
            } else {
                new MenuDAO().updateMenu(
                        new Menu(menu.getIdMenu(), nama, kategori, harga)
                );
            }

            RealtimeClient.sendMessage("MENU_UPDATE");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Input tidak valid");
        }
    }
}
