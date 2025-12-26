package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Coffee Shop - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // tengah layar
        setLayout(new BorderLayout());

        // ================= HEADER =================
        JLabel lblTitle = new JLabel("COFFEE SHOP", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));

        JLabel lblSubtitle = new JLabel("Login Sistem", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSubtitle, BorderLayout.SOUTH);

        // ================= FORM =================
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        form.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        form.add(txtPass, gbc);

        // ================= BUTTON =================
        JButton btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(100, 30)); // 🔥 lebih kecil & proporsional

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        footer.add(btnLogin);

        // ================= MAIN =================
        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // ================= ACTION =================
        btnLogin.addActionListener(e -> {
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword()).trim();

            User user = new UserDAO().login(username, password);

            if (user != null) {
                JOptionPane.showMessageDialog(this,
                        "Login sebagai " + user.getRole());

                if (user.getRole().equals("ADMIN")) {
                    new MenuListFrame();
                } else {
                    new OrderFrame();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Username atau password salah!");
            }
        });

        setVisible(true);
    }
}