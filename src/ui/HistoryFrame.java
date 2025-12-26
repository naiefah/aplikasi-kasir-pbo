package ui;

import dao.OrderDAO;
import service.ReportGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryFrame extends JFrame {

    JFrame parent;
    DefaultTableModel model;
    JSpinner startDate, endDate;

    public HistoryFrame(JFrame parent) {
        this.parent = parent;

        setTitle("Riwayat Transaksi");
        setSize(720, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(root);

        // ================= HEADER =================
        JLabel title = new JLabel("RIWAYAT TRANSAKSI", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        root.add(title, BorderLayout.NORTH);

        // ================= FILTER PANEL =================
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        startDate = new JSpinner(new SpinnerDateModel());
        endDate = new JSpinner(new SpinnerDateModel());

        startDate.setEditor(new JSpinner.DateEditor(startDate, "yyyy-MM-dd"));
        endDate.setEditor(new JSpinner.DateEditor(endDate, "yyyy-MM-dd"));

        JButton btnFilter = new JButton("Filter");

        filterPanel.add(new JLabel("Dari"));
        filterPanel.add(startDate);
        filterPanel.add(new JLabel("Sampai"));
        filterPanel.add(endDate);
        filterPanel.add(btnFilter);

        root.add(filterPanel, BorderLayout.BEFORE_FIRST_LINE);

        // ================= TABLE =================
        model = new DefaultTableModel(
                new String[]{"ID Order", "Tanggal", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Data Transaksi"));
        root.add(scroll, BorderLayout.CENTER);

        // ================= FOOTER =================
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton btnPdf = new JButton("Export PDF");
        JButton btnBack = new JButton("Kembali");

        footer.add(btnPdf);
        footer.add(btnBack);

        root.add(footer, BorderLayout.SOUTH);

        // ================= EVENT =================
        btnFilter.addActionListener(e -> filterData());

        btnPdf.addActionListener(e -> exportPdf());

        btnBack.addActionListener(e -> {
            parent.setVisible(true);
            dispose();
        });

        loadAll();
        setVisible(true);
    }

    // ================= DATA =================

    private void loadAll() {
        model.setRowCount(0);
        try {
            ResultSet rs = new OrderDAO().getAllOrders();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_order"),
                        rs.getDate("tanggal"),
                        rs.getDouble("total")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterData() {
        model.setRowCount(0);
        try {
            Date start = (Date) startDate.getValue();
            Date end = (Date) endDate.getValue();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ResultSet rs = new OrderDAO().getOrdersByDate(
                    sdf.format(start),
                    sdf.format(end)
            );

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_order"),
                        rs.getDate("tanggal"),
                        rs.getDouble("total")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportPdf() {
        try {
            ReportGenerator.generatePdf(
                    (Date) startDate.getValue(),
                    (Date) endDate.getValue()
            );
            JOptionPane.showMessageDialog(
                    this,
                    "PDF berhasil dibuat!\nFile: laporan_penjualan.pdf"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal export PDF"
            );
        }
    }
}