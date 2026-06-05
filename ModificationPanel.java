package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class ModificationPanel extends JPanel {
	
	private JTextField txtInvestorFirstName;
	private JTextField txtInvestorLastName;
	private JTextField txtInvestorEmail;
	private JTextField txtInvestorPhone;
	private JTextField txtInvestorID;
	private JLabel lblInvestorStatus;
	
	// Company fields
	private JTextField txtCompanyName;
	private JTextField txtCompanyIndustry;
	private JTextField txtCompanyHeadquarters;
	private JTextField txtCompanyYear;
	private JTextField txtCompanyID;
	private JLabel lblCompanyStatus;
	
	// Stock fields
	private JTextField txtStockTickerSymbol;
	private JTextField txtStockExchangeName;
	private JTextField txtStockCurrentPrice;
	private JComboBox<String> cmbStockCompanyID;
	private JTextField txtStockID;
	private JLabel lblStockStatus;
	
	public ModificationPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JLabel hint = new JLabel("Enter an ID and click Search to load a record. Primary keys");
		hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
		hint.setForeground(Color.GRAY);
		hint.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
		add(hint, BorderLayout.NORTH);
		
		JTabbedPane subTabs = new JTabbedPane(JTabbedPane.LEFT);
		subTabs.addTab("Investor", buildInvestorPanel());
		subTabs.addTab("Company", buildCompanyPanel());
		subTabs.addTab("Stock", buildStockTab());
		
		
		add(subTabs, BorderLayout.CENTER);
	}
	
	private JPanel buildStockTab() {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(new TitledBorder("Update Stock. "));
		GridBagConstraints gbc = defaultGBC();
		
		txtStockID = new JTextField(8);
        JButton btnSearch = new JButton("Search");
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchRow.add(new JLabel("Stock ID:"));
        searchRow.add(txtStockID);
        searchRow.add(btnSearch);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        p.add(searchRow, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 8, 8, 8);
        p.add(new JSeparator(), gbc);
		
		txtStockTickerSymbol = new JTextField(24);
		txtStockExchangeName = new JTextField(24);
		txtStockCurrentPrice = new JTextField(24);
		cmbStockCompanyID = new JComboBox<>();
		
		//loadCompanyIntoCombo(cmbStockCompanyID);
		
		addRow(p, gbc, 1, "Stock Ticker Symbol *: ", txtStockTickerSymbol);
		addRow(p, gbc, 2, "Exchange Name *: ", txtStockExchangeName);
		addRow(p, gbc, 3, "Stock Price *: ", txtStockCurrentPrice);
		addRow(p, gbc, 4, "Company *: ", cmbStockCompanyID);
		
		JButton btnUpdate = new JButton("Update Stock ");
		btnUpdate.setBackground(new Color(60, 130, 200));
		btnUpdate.setForeground(Color.black);
		btnUpdate.setFocusPainted(false);
		
		JButton btnClear = new JButton("Clear ");
		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		btnRow.add(btnUpdate);
		btnRow.add(btnClear);
		
		gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
		gbc.insets = new Insets(14, 8, 4, 8);
		p.add(btnRow, gbc);
		
		lblStockStatus = new JLabel(" ");
		lblStockStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
		gbc.gridy = 6; gbc.insets = new Insets(2, 10, 8, 8);
		p.add(lblStockStatus, gbc);
		
		// Listeners
		btnSearch.addActionListener(e -> {
            if (searchStock(txtStockID.getText().trim())) {
                setFieldsEnabled(true, txtStockTickerSymbol, txtStockExchangeName, txtStockCurrentPrice);
                btnUpdate.setEnabled(true);
            }
        });
        btnUpdate.addActionListener(e -> updateStock(txtStockID.getText().trim(), btnUpdate));
        btnClear.addActionListener(e -> {
            txtStockID.setText("");
            clearFields(txtStockTickerSymbol, txtStockExchangeName, txtStockCurrentPrice);
            setFieldsEnabled(false, txtStockTickerSymbol, txtStockExchangeName, txtStockCurrentPrice);
            btnUpdate.setEnabled(false);
            lblStockStatus.setText(" ");
        });
		
		return p;
	}
	
	private void updateStock(String trim, JButton btnUpdate) {
		String selected = (String) cmbStockCompanyID.getSelectedItem();
		String tickerSymbol = txtStockTickerSymbol.getText().trim();
		String exchangeName = txtStockExchangeName.getText().trim();
		String currPriceStr = txtStockCurrentPrice.getText().trim();
		
		if (selected == null || tickerSymbol.isEmpty() || exchangeName.isEmpty() || currPriceStr.isEmpty()) {
			showStatus(lblStockStatus, "All fields are required. ", Color.RED);
			return;
		}
		
		int companyID = extractID(selected);
		double currPrice = 0;
		try {
			currPrice = Double.parseDouble(currPriceStr);
		} catch (NumberFormatException ex) {
			showStatus(lblStockStatus, "Current Price must be a decimal. ", Color.RED);
		}

		try {
			
        	
            String sql = "UPDATE Stock SET TickerSymbol=?, ExchangeName=?, CurrentPrice=?, CompanyID=? WHERE StockID=?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setString(1, tickerSymbol);
                ps.setString(2, exchangeName);
                ps.setDouble(3, currPrice);
                ps.setInt(4, Integer.parseInt(trim));
                ps.executeUpdate();
                showStatus(lblStockStatus, "Stock updated successfully.", new Color(0, 140, 0));
                btnUpdate.setEnabled(false);
                setComponentEnabled(false, cmbStockCompanyID, txtStockTickerSymbol, txtStockExchangeName,
                        txtStockCurrentPrice);
            }
        } catch (SQLException ex) {
            showStatus(lblStockStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
		return;
	}

	private boolean searchStock(String idStr) {
        if (idStr.isEmpty()) { showStatus(lblStockStatus, "✖  Enter an Stock ID.", Color.RED); return false; }
        try {
            int id = Integer.parseInt(idStr);
            String sql = "SELECT TickerSymbol, ExchangeName, CurrentPrice, CompanyID FROM Stock WHERE StockID = ?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtStockTickerSymbol.setText(rs.getString("TickerSymbol"));
                    txtStockExchangeName.setText(rs.getString("ExchangeName"));
                    txtStockCurrentPrice.setText(String.valueOf(rs.getDouble("CurrentPrice")));
                    selectComboById(cmbStockCompanyID, rs.getInt("CompanyID"));
                    showStatus(lblStockStatus, "Stock found. Edit and click Update.", new Color(0, 100, 180));
                    return true;
                } else {
                    showStatus(lblStockStatus, "No stock found with ID " + id + ".", Color.RED);
                }
            }
        } catch (NumberFormatException ex) {
            showStatus(lblStockStatus, "Stock ID must be an integer.", Color.RED);
        } catch (SQLException ex) {
            showStatus(lblStockStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
        return false;
    }
	

	private JPanel buildCompanyPanel() {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(new TitledBorder("Update Company. "));
		GridBagConstraints gbc = defaultGBC();
		
		txtCompanyID = new JTextField(8);
        JButton btnSearch = new JButton("Search");
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchRow.add(new JLabel("Company ID:"));
        searchRow.add(txtCompanyID);
        searchRow.add(btnSearch);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        p.add(searchRow, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 8, 8, 8);
        p.add(new JSeparator(), gbc);
		
		txtCompanyName= new JTextField(24);
		txtCompanyIndustry = new JTextField(24);
		txtCompanyHeadquarters = new JTextField(24);
		txtCompanyYear = new JTextField(24);
		
		addRow(p, gbc, 1, "Company Name *: ", txtCompanyName);
		addRow(p, gbc, 2, "Company Industry *: ", txtCompanyIndustry);
		addRow(p, gbc, 3, "Headquarters *: ", txtCompanyHeadquarters);
		addRow(p, gbc, 4, "Year Founded: ", txtCompanyYear);
		
		JButton btnUpdate = new JButton("Update Company ");
		btnUpdate.setBackground(new Color(60, 130, 200));
		btnUpdate.setForeground(Color.black);
		btnUpdate.setFocusPainted(false);
		
		JButton btnClear = new JButton("Clear ");
		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		btnRow.add(btnUpdate);
		btnRow.add(btnClear);
		
		gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
		gbc.insets = new Insets(14, 8, 4, 8);
		p.add(btnRow, gbc);
		
		lblCompanyStatus = new JLabel(" ");
		lblCompanyStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
		gbc.gridy = 6; gbc.insets = new Insets(2, 10, 8, 8);
		p.add(lblCompanyStatus, gbc);
		
		// Listeners
		btnSearch.addActionListener(e -> {
            if (searchCompany(txtCompanyID.getText().trim())) {
                setFieldsEnabled(true, txtCompanyName, txtCompanyIndustry, txtCompanyHeadquarters,
                                       txtCompanyYear);
                btnUpdate.setEnabled(true);
            }
        });
        btnUpdate.addActionListener(e -> updateCompany(txtCompanyID.getText().trim(), btnUpdate));
        btnClear.addActionListener(e -> {
            txtCompanyID.setText("");
            clearFields(txtCompanyName, txtCompanyIndustry, txtCompanyHeadquarters,
                    txtCompanyYear);
            setFieldsEnabled(false, txtCompanyName, txtCompanyIndustry, txtCompanyHeadquarters,
                    txtCompanyYear);
            btnUpdate.setEnabled(false);
            lblCompanyStatus.setText(" ");
        });
		
		return p;
	}
	

	private boolean searchCompany(String idStr) {
        if (idStr.isEmpty()) { showStatus(lblCompanyStatus, "✖  Enter an Company ID.", Color.RED); return false; }
        try {
            int id = Integer.parseInt(idStr);
            String sql = "SELECT CompanyName, Industry, Headquarters, FoundedYear FROM Company WHERE CompanyID = ?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtCompanyName.setText(rs.getString("CompanyName"));
                    txtCompanyIndustry.setText(rs.getString("Industry"));
                    txtCompanyHeadquarters.setText((rs.getString("Headquarters")));
                    txtCompanyYear.setText(String.valueOf(rs.getInt("FoundedYear")));
                    showStatus(lblCompanyStatus, "Company found. Edit and click Update.", new Color(0, 100, 180));
                    return true;
                } else {
                    showStatus(lblCompanyStatus, "No company found with ID " + id + ".", Color.RED);
                }
            }
        } catch (NumberFormatException ex) {
            showStatus(lblCompanyStatus, "Company ID must be an integer.", Color.RED);
        } catch (SQLException ex) {
            showStatus(lblCompanyStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
        return false;
    }
	
	private void updateCompany(String idStr, JButton btnUpdate) {
		String name = txtCompanyName.getText().trim();
		String industry = txtCompanyIndustry.getText().trim();
		String headquarters = txtCompanyHeadquarters.getText().trim();
		String yearStr = txtCompanyYear.getText().trim();
		
		if (name.isEmpty() || industry.isEmpty() || headquarters.isEmpty()) {
            showStatus(lblCompanyStatus, "Name, industry, and headquarters are required.", Color.RED); return;
        }
		
		
        try {
        	
        	int year = Integer.parseInt(yearStr);
            String sql = "UPDATE Company SET CompanyName=?, Industry=?, Headquarters=?, FoundedYear=? WHERE CompanyID=?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, industry);
                ps.setString(3, headquarters);
                ps.setInt(4, year);
                ps.setInt(5, Integer.parseInt(idStr));
                ps.executeUpdate();
                showStatus(lblCompanyStatus, "Company updated successfully.", new Color(0, 140, 0));
                btnUpdate.setEnabled(false);
                setComponentEnabled(false, txtCompanyName, txtCompanyIndustry, txtCompanyHeadquarters,
                        txtCompanyYear);
            }
        } catch (SQLException ex) {
            showStatus(lblCompanyStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
		return;
	}

	private JPanel buildInvestorPanel() {
		
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(new TitledBorder("Update Investor. "));
		GridBagConstraints gbc = defaultGBC();
		
		// Search row
        txtInvestorID = new JTextField(8);
        JButton btnSearch = new JButton("Search");
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchRow.add(new JLabel("Investor ID:"));
        searchRow.add(txtInvestorID);
        searchRow.add(btnSearch);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        p.add(searchRow, gbc);

        // Separator
        gbc.gridy = 1; gbc.insets = new Insets(0, 8, 8, 8);
        p.add(new JSeparator(), gbc);
		
		txtInvestorFirstName = new JTextField(24);
		txtInvestorLastName = new JTextField(24);
		txtInvestorEmail = new JTextField(24);
		txtInvestorPhone = new JTextField(24);
		
		addRow(p, gbc, 1, "First Name *: ", txtInvestorFirstName);
		addRow(p, gbc, 2, "Last Name *: ", txtInvestorLastName);
		addRow(p, gbc, 3, "Email *: ", txtInvestorEmail);
		addRow(p, gbc, 4, "Phone: ", txtInvestorPhone);
		
		JButton btnUpdate = new JButton("Update Investor ");
		btnUpdate.setBackground(new Color(60, 130, 200));
		btnUpdate.setForeground(Color.black);
		btnUpdate.setFocusPainted(false);
		
		JButton btnClear = new JButton("Clear ");
		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		btnRow.add(btnUpdate);
		btnRow.add(btnClear);
		
		gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
		gbc.insets = new Insets(14, 8, 4, 8);
		p.add(btnRow, gbc);
		
		lblInvestorStatus = new JLabel(" ");
		lblInvestorStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
		gbc.gridy = 6; gbc.insets = new Insets(2, 10, 8, 8);
		p.add(lblInvestorStatus, gbc);
		
		// Listeners
		btnSearch.addActionListener(e -> {
            if (searchInvestor(txtInvestorID.getText().trim())) {
                setFieldsEnabled(true, txtInvestorFirstName, txtInvestorLastName, txtInvestorEmail,
                                       txtInvestorPhone);
                btnUpdate.setEnabled(true);
            }
        });
        btnUpdate.addActionListener(e -> updateInvestor(txtInvestorID.getText().trim(), btnUpdate));
        btnClear.addActionListener(e -> {
            txtInvestorID.setText("");
            clearFields(txtInvestorFirstName, txtInvestorLastName, txtInvestorEmail,
                    txtInvestorPhone);
            setFieldsEnabled(false, txtInvestorFirstName, txtInvestorLastName, txtInvestorEmail,
                    txtInvestorPhone);
            btnUpdate.setEnabled(false);
            lblInvestorStatus.setText(" ");
        });
		return p;
	}
	

	private boolean searchInvestor(String idStr) {
        if (idStr.isEmpty()) { showStatus(lblInvestorStatus, "✖  Enter an Investor ID.", Color.RED); return false; }
        try {
            int id = Integer.parseInt(idStr);
            String sql = "SELECT FirstName, LastName, Email, Phone FROM Investor WHERE InvestorID = ?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtInvestorFirstName.setText(rs.getString("FirstName"));
                    txtInvestorLastName.setText(rs.getString("LastName"));
                    txtInvestorEmail.setText(rs.getString("Email"));
                    txtInvestorPhone.setText(rs.getString("Phone"));
                    showStatus(lblInvestorStatus, "Investor found. Edit and click Update.", new Color(0, 100, 180));
                    return true;
                } else {
                    showStatus(lblInvestorStatus, "No Investor found with ID " + id + ".", Color.RED);
                }
            }
        } catch (NumberFormatException ex) {
            showStatus(lblInvestorStatus, "Investor ID must be an integer.", Color.RED);
        } catch (SQLException ex) {
            showStatus(lblInvestorStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
        return false;
    }
	
	private void updateInvestor(String idStr, JButton btnUpdate) {
		String Fname = txtInvestorFirstName.getText().trim();
		String LName = txtInvestorLastName.getText().trim();
		String email = txtInvestorEmail.getText().trim();
		String phone = txtInvestorPhone.getText().trim();
		
		if (Fname.isEmpty() || LName.isEmpty() || email.isEmpty()) {
            showStatus(lblInvestorStatus, "FirstName, LastName, and email are required.", Color.RED); return;
        }
        try {
            
            String sql = "UPDATE Investor SET FirstName=?, LastName=?, Email=?, Phone=? WHERE InvestorID=?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setString(1, Fname);
                ps.setString(2, LName);
                ps.setString(3, email);
                ps.setString(4, phone);
                ps.setInt(5, Integer.parseInt(idStr));
                ps.executeUpdate();
                showStatus(lblInvestorStatus, "Investor updated successfully.", new Color(0, 140, 0));
                btnUpdate.setEnabled(false);
                setComponentEnabled(false, txtInvestorFirstName, txtInvestorLastName, txtInvestorEmail, txtInvestorPhone);
            }
        } catch (SQLException ex) {
            showStatus(lblInvestorStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
		return;
	}

	private void showStatus(JLabel lbl, String msg, Color color) {
		lbl.setText(msg);
		lbl.setForeground(color);
		
	}

	// Utilities
	
	private void setComponentEnabled(boolean enabled, JComponent... comps) {
        for (JComponent c : comps) c.setEnabled(enabled);
    }
	
	private void setFieldsEnabled(boolean enabled, JTextField... fields) {
        for (JTextField f : fields) f.setEnabled(enabled);
    }
	
	
	private int extractID(String item) {
		return 	Integer.parseInt(item.split(" - ")[0].trim());
	}
	
	/** Selects the combo item whose leading ID matches targetID. */
    private void selectComboById(JComboBox<String> cmb, int targetId) {
        for (int i = 0; i < cmb.getItemCount(); i++) {
            if (extractID(cmb.getItemAt(i)) == targetId) { cmb.setSelectedIndex(i); return; }
        }
    }
	
	private void clearFields(JTextField... fields) {
		for (JTextField f : fields) {f.setText("");}
		
	}
	
	private GridBagConstraints defaultGBC() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 8, 6, 8);
		gbc.anchor = GridBagConstraints.WEST;
		
		return gbc;
	}
	private void addRow(JPanel p, GridBagConstraints gbc, int row, String labelText, JComponent field) {
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
		gbc.insets = new Insets(6, 10, 6, 4);
		JLabel lbl = new JLabel(labelText);
		lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
		p.add(lbl, gbc);
		
		gbc.gridx = 1; gbc.weightx = 1.0;
		gbc.insets = new Insets(6, 4, 6, 12);
		p.add(field, gbc);
	}
}
