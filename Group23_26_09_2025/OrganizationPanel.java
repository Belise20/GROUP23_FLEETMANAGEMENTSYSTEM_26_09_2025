package fleet.organization;
import javax.swing.*;
import fleet.util.DB;

import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrganizationPanel extends JPanel implements ActionListener {
    JTextField
            typeTxt = new JTextField(),
            modelTxt = new JTextField(),
            yearTxt = new JTextField(),
    fuelTypeTxt = new JTextField(),
    idTxt = new JTextField(),
    priceTxt = new JTextField();

    JButton addBtn = new JButton("Add"),
            updateBtn = new JButton("Update"),
            deleteBtn = new JButton("Delete"),
            loadBtn = new JButton("Load");

    JTable table;
    DefaultTableModel model;
    int userId;

    public OrganizationPanel(int userId) {
        this.userId=userId;
        setLayout(null);
        String[] labels = {"ID", "Model"," Type", "FuelType", "year", "Price"};
        model = new DefaultTableModel(labels, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 230, 800, 300);

        int y = 20;
        addField("Model", modelTxt, y); y += 30;
        addField("Type", typeTxt, y); y += 30;
        addField("Fuel Type", fuelTypeTxt, y); y += 30;
        addField("Year", yearTxt, y); y += 30;
        addField("Price ", priceTxt, y); y += 30;
        addField("Id ", idTxt, y); y += 30;

        idTxt.setEditable(false);

        addButtons();
        add(sp);


        // Load users automatically on startup
        loadFleets();

        // Fill form when a row is clicked
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    idTxt.setText(model.getValueAt(row, 0).toString());
                    typeTxt.setText(model.getValueAt(row, 2).toString());
                    modelTxt.setText(model.getValueAt(row, 1).toString());
                    fuelTypeTxt.setText(model.getValueAt(row, 3).toString());
                    yearTxt.setText(model.getValueAt(row, 4).toString());
                    priceTxt.setText(model.getValueAt(row,5).toString());
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
                        "INSERT INTO fleet(type,model,year,fuelType,price,user_id) VALUES(?,?,?,?,?,?)"
                );
                ps.setString(1, typeTxt.getText());
                ps.setString(2, modelTxt.getText());
                ps.setString(3, yearTxt.getText());
                ps.setString(4, fuelTypeTxt.getText());
                ps.setString(5,priceTxt.getText());
                ps.setInt(6,userId);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Fleet Added!");
                setfieldsEmpty();
                loadFleets();
            }
            else if (e.getSource() == updateBtn) {
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE fleet SET type=?, model=?, fuelType=?, price=?,year=? WHERE id=?"
                );
                ps.setString(1, typeTxt.getText());
                ps.setString(2, modelTxt.getText());
                ps.setString(5, yearTxt.getText());
                ps.setString(3, fuelTypeTxt.getText());
                ps.setString(4,priceTxt.getText());
                ps.setString(6,idTxt.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Fleet Updated!");
                setfieldsEmpty();
                loadFleets();
            }
            else if (e.getSource() == deleteBtn) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM fleet WHERE id=?");
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Fleet Deleted!");
                setfieldsEmpty();
                loadFleets();
            }
            else if (e.getSource() == loadBtn) {
                setfieldsEmpty();
                loadFleets();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Utility to refresh table
    private void loadFleets() {
        try (Connection con = DB.getConnection()) {
            model.setRowCount(0);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM fleet where user_id=?");
            ps.setInt(1,userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("model"),
                        rs.getString("type"),
                        rs.getString("fuelType"),
                        rs.getString("year"),
                        rs.getString("price")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void setfieldsEmpty(){
        modelTxt.setText("");
        typeTxt.setText("");
        priceTxt .setText("");
        idTxt .setText("");
        fuelTypeTxt.setText("");
        yearTxt.setText("");
    }

}
