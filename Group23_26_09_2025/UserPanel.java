package fleet.organization;

import fleet.util.DB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserPanel extends JPanel implements ActionListener {
    JTextField
            nameTxt = new JTextField(),
            phoneTxt = new JTextField(),
            emailTxt = new JTextField(),
    idTxt = new JTextField();
    JPasswordField passTxt = new JPasswordField();

    JButton addBtn = new JButton("Add"),
            updateBtn = new JButton("Update"),
            deleteBtn = new JButton("Delete"),
            loadBtn = new JButton("Load");

    JTable table;
    DefaultTableModel model;

    public UserPanel() {
        setLayout(null);
        String[] labels = {"ID", "Organization Name", "Phone", "Email", "Role"};
        model = new DefaultTableModel(labels, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 230, 800, 300);

        int y = 20;
        addField("Organization Name", nameTxt, y); y += 30;
        addField("Password", passTxt, y); y += 30;
        addField("Phone", phoneTxt, y); y += 30;
        addField("Email", emailTxt, y); y += 30;
        addField("user Id", idTxt, y); y += 30;

        idTxt.setEditable(false);

        addButtons();
        add(sp);


        // Load users automatically on startup
        loadUsers();

        // Fill form when a row is clicked
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    idTxt.setText(model.getValueAt(row, 0).toString());
                    nameTxt.setText(model.getValueAt(row, 1).toString());
                    passTxt.setText(model.getValueAt(row, 2).toString());
                    phoneTxt.setText(model.getValueAt(row, 3).toString());
                    emailTxt.setText(model.getValueAt(row, 4).toString());
                }
            }
        });
    }

    private void addField(String lbl, JComponent txt, int y) {
        JLabel l = new JLabel(lbl);
        l.setBounds(20, y, 80, 25);
        txt.setBounds(100, y, 150, 25);
        add(l); add(txt);
    }

    private void addButtons() {
        addBtn.setBounds(300, 20, 100, 30);
        updateBtn.setBounds(300, 60, 100, 30);
        deleteBtn.setBounds(300, 100, 100, 30);
        loadBtn.setBounds(300, 140, 100, 30);
        add(addBtn); add(updateBtn); add(deleteBtn); add(loadBtn);
        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        loadBtn.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e) {
        try (Connection con = DB.getConnection()) {
            if (e.getSource() == addBtn) {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO users(organization_name,password,phone,email,role) VALUES(?,?,?,?,?)"
                );
                ps.setString(1, nameTxt.getText());
                ps.setString(2, new String(passTxt.getPassword()));
                ps.setString(3, phoneTxt.getText());
                ps.setString(4, emailTxt.getText());
                ps.setString(5,"organization_admin");
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Organization Added!");
                setfieldsEmpty();
                loadUsers();
            }
            else if (e.getSource() == updateBtn) {
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE users SET organization_name=?, password=?, phone=?, email=? WHERE id=?"
                );
                ps.setString(1, nameTxt.getText());
                ps.setString(2, new String(passTxt.getPassword()));
                ps.setString(3, phoneTxt.getText());
                ps.setString(4, emailTxt.getText());
                ps.setInt(5, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User Updated!");
                setfieldsEmpty();
                loadUsers();
            }
            else if (e.getSource() == deleteBtn) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE id=?");
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User Deleted!");
                setfieldsEmpty();
                loadUsers();
            }
            else if (e.getSource() == loadBtn) {
                setfieldsEmpty();
                loadUsers();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Utility to refresh table
    private void loadUsers() {
        try (Connection con = DB.getConnection()) {
            model.setRowCount(0);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users where role!=?");
            ps.setString(1,"role");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("organization_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("role")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
private void setfieldsEmpty(){
    nameTxt.setText("");
            phoneTxt.setText("");
            emailTxt .setText("");
            idTxt .setText("");
            passTxt.setText("");
}

}