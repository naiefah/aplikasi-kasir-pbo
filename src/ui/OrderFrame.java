package ui;

import dao.MenuDAO;
import dao.OrderDAO;
import net.miginfocom.swing.MigLayout;
import websocket.RealtimeClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OrderFrame extends JFrame {

    JComboBox<String> cbMenu;
    JTextField txtHarga, txtQty;
    JTable table;
    DefaultTableModel model;
    JLabel lblTotal;

    double total = 0;

    // 🔥 SIMPAN SEMUA MENU (UNTUK SEARCH)
    private final List<String> allMenus = new ArrayList<>();

    public OrderFrame() {
        setTitle("Coffee Shop - Kasir");
        setSize(950, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ================= ROOT =================
        JPanel root = new JPanel(new MigLayout(
                "fill, insets 15",
                "[grow][grow]",
                "[][grow][]"
        ));
        setContentPane(root);

        // ================= HEADER =================
        JLabel lblTitle = new JLabel("TRANSAKSI PEMESANAN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JButton btnLogout = new JButton("Logout");

        JPanel header = new JPanel(new MigLayout("fill", "[grow][right]"));
        header.add(lblTitle, "left");
        header.add(btnLogout, "right");

        root.add(header, "span, growx, wrap");

        // ================= FORM KIRI =================
        JPanel formCard = new JPanel(new MigLayout(
                "wrap 2, insets 15",
                "[right][grow]"
        ));
        formCard.setBorder(BorderFactory.createTitledBorder("Input Pesanan"));

        JButton btnCari = new JButton("Cari Menu 🔍");
        cbMenu = new JComboBox<>();

        txtHarga = new JTextField();
        txtHarga.setEditable(false);

        txtQty = new JTextField();
        JButton btnTambah = new JButton("Tambah");

        loadMenuFromDB();

        // 🔥 BUTTON CARI DI ATAS MENU
        formCard.add(new JLabel(""));
        formCard.add(btnCari, "growx");

        formCard.add(new JLabel("Menu"));
        formCard.add(cbMenu, "growx");

        formCard.add(new JLabel("Harga"));
        formCard.add(txtHarga, "growx");

        formCard.add(new JLabel("Jumlah"));
        formCard.add(txtQty, "growx");

        formCard.add(new JLabel(""));
        formCard.add(btnTambah, "growx");

        root.add(formCard, "growx, growy");

        // ================= TABLE KANAN =================
        model = new DefaultTableModel(
                new String[]{"Menu", "Harga", "Qty", "Subtotal"}, 0
        );

        table = new JTable(model);
        table.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Pesanan"));
        root.add(scroll, "grow, wrap");

        // ================= FOOTER =================
        JPanel footer = new JPanel(new MigLayout(
                "fill, insets 10",
                "[grow][right]"
        ));

        lblTotal = new JLabel("Total : Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnReset = new JButton("Reset");
        JButton btnSimpan = new JButton("Simpan Transaksi");

        footer.add(new JLabel(), "grow");
        footer.add(btnReset, "gapright 10");
        footer.add(btnSimpan, "gapright 20");
        footer.add(lblTotal);

        root.add(footer, "span, growx");

        // ================= EVENT =================
        cbMenu.addActionListener(e -> setHarga());
        btnTambah.addActionListener(e -> tambahPesanan());
        btnReset.addActionListener(e -> resetPesanan());
        btnSimpan.addActionListener(e -> simpanTransaksi());
        btnLogout.addActionListener(e -> logout());
        btnCari.addActionListener(e -> openSearchDialog());

        // ================= WEBSOCKET =================
        RealtimeClient.init();
        RealtimeClient.addListener(msg -> {
            if ("MENU_UPDATE".equals(msg)) {
                SwingUtilities.invokeLater(() -> {
                    allMenus.clear();
                    cbMenu.removeAllItems();
                    loadMenuFromDB();
                });
            }
        });

        setVisible(true);
    }

    // ================= SEARCH DIALOG (TANPA TOMBOL PILIH) =================
    private void openSearchDialog() {
        JDialog dialog = new JDialog(this, "Cari Menu", true);
        dialog.setSize(380, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        // ================= HEADER =================
        // JLabel lblTitle = new JLabel("CARI MENU", JLabel.CENTER);
        // lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        // lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        // ================= SEARCH FIELD =================
        JTextField txtCari = new JTextField();
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCari.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
        searchPanel.add(new JLabel("Ketik nama menu:"), BorderLayout.NORTH);
        searchPanel.add(txtCari, BorderLayout.CENTER);

        // ================= LIST MENU =================
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> listMenu = new JList<>(listModel);
        listMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listMenu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listMenu.setFixedCellHeight(32);

        JScrollPane scroll = new JScrollPane(listMenu);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        // isi awal
        allMenus.forEach(listModel::addElement);

        // ================= FILTER =================
        txtCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }

            private void filter() {
                String key = txtCari.getText().toLowerCase();
                listModel.clear();
                for (String menu : allMenus) {
                    if (menu.toLowerCase().contains(key)) {
                        listModel.addElement(menu);
                    }
                }
            }
        });

        // ================= CLICK SELECT =================
        listMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1 && listMenu.getSelectedValue() != null) {
                    cbMenu.setSelectedItem(listMenu.getSelectedValue());
                    setHarga();
                    dialog.dispose();
                }
            }
        });

        // ================= LAYOUT =================
        // dialog.add(lblTitle, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.add(searchPanel, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        dialog.add(center, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    // ================= METHOD =================
    private void loadMenuFromDB() {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                ResultSet rs = new MenuDAO().getAllMenu();
                while (rs.next()) {
                    publish(rs.getString("nama_menu") + " - " + rs.getDouble("harga"));
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String item : chunks) {
                    allMenus.add(item);
                    cbMenu.addItem(item);
                }
                if (cbMenu.getItemCount() > 0) {
                    cbMenu.setSelectedIndex(0);
                    setHarga();
                }
            }
        };
        worker.execute();
    }

    private void setHarga() {
        if (cbMenu.getSelectedItem() == null) return;
        String harga = cbMenu.getSelectedItem().toString().split("-")[1].trim();
        txtHarga.setText(harga);
    }

    private void tambahPesanan() {
        try {
            String menu = cbMenu.getSelectedItem().toString().split("-")[0].trim();
            double harga = Double.parseDouble(txtHarga.getText());
            int qty = Integer.parseInt(txtQty.getText());

            double subtotal = harga * qty;
            total += subtotal;

            model.addRow(new Object[]{menu, harga, qty, subtotal});
            lblTotal.setText("Total : Rp " + total);
            txtQty.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Input tidak valid!");
        }
    }

    private void resetPesanan() {
        model.setRowCount(0);
        total = 0;
        lblTotal.setText("Total : Rp 0");
    }

    private void simpanTransaksi() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Pesanan kosong!");
            return;
        }

        OrderDAO dao = new OrderDAO();
        int idOrder = dao.insertOrder(total);

        for (int i = 0; i < model.getRowCount(); i++) {
            dao.insertDetail(
                    idOrder,
                    model.getValueAt(i, 0).toString(),
                    Double.parseDouble(model.getValueAt(i, 1).toString()),
                    Integer.parseInt(model.getValueAt(i, 2).toString()),
                    Double.parseDouble(model.getValueAt(i, 3).toString())
            );
        }

        JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!");
        resetPesanan();
        RealtimeClient.sendMessage("ORDER_UPDATE");
    }

    private void logout() {
        new LoginFrame();
        dispose();
    }
}