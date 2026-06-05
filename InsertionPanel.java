package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

public class InsertionPanel extends JPanel{
	
	// Investor fields
	private JTextField txtInvestorFirstName;
	private JTextField txtInvestorLastName;
	private JTextField txtInvestorEmail;
	private JTextField txtInvestorPhone;
	private JLabel lblInvestorStatus;
	
	// Company fields
	private JTextField txtCompanyName;
	private JTextField txtCompanyIndustry;
	private JTextField txtCompanyHeadquarters;
	private JTextField txtCompanyYear;
	private JLabel lblCompanyStatus;
	
	// Stock fields
	private JTextField txtStockTickerSymbol;
	private JTextField txtStockExchangeName;
	private JTextField txtStockCurrentPrice;
	private JComboBox<String> cmbStockCompanyID;
	private JLabel lblStockStatus;
	
	public InsertionPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JLabel hint = new JLabel("Fields marked with * are required. Others may be left blank. ");
		hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
		hint.setForeground(Color.GRAY);
		hint.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
		add(hint, BorderLayout.NORTH);
		
		JTabbedPane subTabs = new JTabbedPane(JTabbedPane.LEFT);
		subTabs.addTab("Investor", buildInvestorPanel());
		subTabs.addTab("Company", buildCompanyPanel());
		subTabs.addTab("Stock", buildStockTab());
		
		subTabs.addChangeListener(e -> {
			int i = subTabs.getSelectedIndex();
			if (i == 1) loadCompanyIntoCombo(cmbStockCompanyID);
		});
		
		add(subTabs, BorderLayout.CENTER);
	}

	private void loadCompanyIntoCombo(JComboBox<String> cmb) {
		cmb.removeAllItems();
		
		try (Statement st = DatabaseConnection.getConnection().createStatement();
	             ResultSet rs = st.executeQuery("SELECT CompanyID, CompanyName FROM Company ORDER BY CompanyName")) {
	            while (rs.next()) {
	                cmb.addItem(rs.getInt(1) + " – " + rs.getString(2));
	            }
	        } catch (SQLException ex) {
	            cmb.addItem("(connection error)");
	        }
		
	}

		
	private JPanel buildStockTab() {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(new TitledBorder("Insert new Stock. "));
		GridBagConstraints gbc = defaultGBC();
		
		txtStockTickerSymbol = new JTextField(24);
		txtStockExchangeName = new JTextField(24);
		txtStockCurrentPrice = new JTextField(24);
		cmbStockCompanyID = new JComboBox<>();
		
		loadCompanyIntoCombo(cmbStockCompanyID);
		
		addRow(p, gbc, 0, "Stock Ticker Symbol *: ", txtStockTickerSymbol);
		addRow(p, gbc, 1, "Exchange Name *: ", txtStockExchangeName);
		addRow(p, gbc, 2, "Stock Price *: ", txtStockCurrentPrice);
		addRow(p, gbc, 3, "Company *: ", cmbStockCompanyID);
		
		JButton btnInsert = new JButton("Insert Stock ");
		btnInsert.setBackground(new Color(60, 130, 200));
		btnInsert.setForeground(Color.black);
		btnInsert.setFocusPainted(false);
		
		JButton btnClear = new JButton("Clear ");
		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		btnRow.add(btnInsert);
		btnRow.add(btnClear);
		
		gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
		gbc.insets = new Insets(14, 8, 4, 8);
		p.add(btnRow, gbc);
		
		lblStockStatus = new JLabel(" ");
		lblStockStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
		gbc.gridy = 6; gbc.insets = new Insets(2, 10, 8, 8);
		p.add(lblStockStatus, gbc);
		
		// Listeners
		btnInsert.addActionListener(e -> insertStock());
		btnClear.addActionListener(e -> {
			clearFields(txtStockTickerSymbol, txtStockExchangeName, txtStockCurrentPrice);
			lblStockStatus.setText(" ");
		});
		
		return p;
	}

	private void insertStock() {
		String selected = (String) cmbStockCompanyID.getSelectedItem();
		String tickerSymbol = txtStockTickerSymbol.getText().trim();
		String exchangeName = txtStockExchangeName.getText().trim();
		String currPriceStr = txtStockCurrentPrice.getText().trim();
		
		
		if (selected == null || tickerSymbol.isEmpty() || exchangeName.isEmpty() || currPriceStr.isEmpty()) {
			showStatus(lblStockStatus, "All fields are required. ", Color.RED);
			return;
		}
		
		int companyID = 0;
		double currPrice = 0;
		
		try {
			companyID = extractID(selected);
			currPrice = Double.parseDouble(currPriceStr);
		} catch (NumberFormatException ex) {
			showStatus(lblStockStatus, "Current Price must be a decimal. ", Color.RED);
		}
		
		String sql = "INSERT INTO Stock(TickerSymbol, ExchangeName, CurrentPrice, CompanyID) VALUES(?, ?, ?, ?)";
		try {PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
			ps.setString(1, tickerSymbol);
			ps.setString(2, exchangeName);
			ps.setDouble(3, currPrice);
			ps.setInt(4, companyID);
			ps.executeUpdate();
			showStatus(lblStockStatus, "Stock " + tickerSymbol+ " inserted successfully.", new Color(0, 140, 0));
            txtStockTickerSymbol.setText("");
            txtStockExchangeName.setText("");
            txtStockCurrentPrice.setText("");
			
		}catch (SQLException ex) {
			showStatus(lblStockStatus, "DB Error: " + ex.getMessage(), Color.RED);
		}
		
		return;
	}

	private int extractID(String item) {
		return 	Integer.parseInt(item.split(" - ")[0].trim());
	}

	private JPanel buildCompanyPanel() {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(new TitledBorder("Insert new Company. "));
		GridBagConstraints gbc = defaultGBC();
		
		txtCompanyName= new JTextField(24);
		txtCompanyIndustry = new JTextField(24);
		txtCompanyHeadquarters = new JTextField(24);
		txtCompanyYear = new JTextField(24);
		
		addRow(p, gbc, 0, "Company Name *: ", txtCompanyName);
		addRow(p, gbc, 1, "Company Industry *: ", txtCompanyIndustry);
		addRow(p, gbc, 2, "Headquarters *: ", txtCompanyHeadquarters);
		addRow(p, gbc, 3, "Year Founded: ", txtCompanyYear);
		
		JButton btnInsert = new JButton("Insert Company ");
		btnInsert.setBackground(new Color(60, 130, 200));
		btnInsert.setForeground(Color.black);
		btnInsert.setFocusPainted(false);
		
		JButton btnClear = new JButton("Clear ");
		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		btnRow.add(btnInsert);
		btnRow.add(btnClear);
		
		gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
		gbc.insets = new Insets(14, 8, 4, 8);
		p.add(btnRow, gbc);
		
		lblCompanyStatus = new JLabel(" ");
		lblCompanyStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
		gbc.gridy = 6; gbc.insets = new Insets(2, 10, 8, 8);
		p.add(lblCompanyStatus, gbc);
		
		// Listeners
		btnInsert.addActionListener(e -> insertCompany());
		btnClear.addActionListener(e -> {
			clearFields(txtCompanyName, txtCompanyIndustry, txtCompanyHeadquarters, txtCompanyYear);
			lblCompanyStatus.setText(" ");
		});
		
		return p;
	}
	
	

	private void insertCompany() {
		String Name = txtCompanyName.getText().trim();
		String industry = txtCompanyIndustry.getText().trim();
		String headquarters = txtCompanyHeadquarters.getText().trim();
		String yearStr = txtCompanyYear.getText().trim();
		
		// Validation
		if (Name.isEmpty() || industry.isEmpty() || headquarters.isEmpty()) {
			showStatus(lblCompanyStatus, "All required fields are not filled. ", Color.RED);
			
			return;
		}
		
		int year;
		try {
			year = Integer.parseInt(yearStr);
		}catch (NumberFormatException ex) {
			showStatus(lblCompanyStatus, "Founded year must be an integer.", Color.RED);
			return;
		}
		String sql = "INSERT INTO Company (CompanyName, Industry, Headquarters, FoundedYear) VALUES(?, ?, ?, ?)";
		try { PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);{
			ps.setString(1, Name);
			ps.setString(2, industry);
			ps.setString(3, headquarters);
			ps.setInt(4, year);
			ps.executeUpdate();
			showStatus(lblCompanyStatus, "Company " + Name + " inserted successfully. ", new Color(0, 140, 0));
			clearFields(txtCompanyName, txtCompanyIndustry,
					txtCompanyHeadquarters,
					txtCompanyYear);
		}} catch (SQLException ex){
			showStatus(lblCompanyStatus, "DB Error: " + ex.getMessage(), Color.RED);
		}
	}

	private JPanel buildInvestorPanel() {
		
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(new TitledBorder("Insert new Investor. "));
		GridBagConstraints gbc = defaultGBC();
		
		txtInvestorFirstName = new JTextField(24);
		txtInvestorLastName = new JTextField(24);
		txtInvestorEmail = new JTextField(24);
		txtInvestorPhone = new JTextField(24);
		
		addRow(p, gbc, 0, "First Name *: ", txtInvestorFirstName);
		addRow(p, gbc, 1, "Last Name *: ", txtInvestorLastName);
		addRow(p, gbc, 2, "Email *: ", txtInvestorEmail);
		addRow(p, gbc, 3, "Phone: ", txtInvestorPhone);
		
		JButton btnInsert = new JButton("Insert Investor ");
		btnInsert.setBackground(new Color(60, 130, 200));
		btnInsert.setForeground(Color.black);
		btnInsert.setFocusPainted(false);
		
		JButton btnClear = new JButton("Clear ");
		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		btnRow.add(btnInsert);
		btnRow.add(btnClear);
		
		gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
		gbc.insets = new Insets(14, 8, 4, 8);
		p.add(btnRow, gbc);
		
		lblInvestorStatus = new JLabel(" ");
		lblInvestorStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
		gbc.gridy = 6; gbc.insets = new Insets(2, 10, 8, 8);
		p.add(lblInvestorStatus, gbc);
		
		// Listeners
		btnInsert.addActionListener(e -> insertInvestor());
		btnClear.addActionListener(e -> {
			clearFields(txtInvestorFirstName, txtInvestorLastName, txtInvestorEmail, txtInvestorPhone);
			lblInvestorStatus.setText(" ");
		});
		
		return p;
	}
	
	// Database Loaders
	
	

	private void insertInvestor() {
		
		String Fname = txtInvestorFirstName.getText().trim();
		String LName = txtInvestorLastName.getText().trim();
		String email = txtInvestorEmail.getText().trim();
		String phone = txtInvestorPhone.getText().trim();
		
		// Validation
		if (Fname.isEmpty() || LName.isEmpty() || email.isEmpty()) {
			showStatus(lblInvestorStatus, "All required fields are not filled. ", Color.RED);
			
			return;
		}
		
		String sql = "INSERT INTO Investor (FirstName, LastName, Email, Phone) VALUES(?, ?, ?, ?)";
		try { PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);{
			ps.setString(1, Fname);
			ps.setString(2, LName);
			ps.setString(3, email);
			ps.setString(4, phone);
			ps.executeUpdate();
			showStatus(lblInvestorStatus, "Investor " + Fname + " inserted successfully. ", new Color(0, 140, 0));
			clearFields(txtInvestorFirstName, txtInvestorLastName,
					txtInvestorEmail,
					txtInvestorPhone);
		}} catch (SQLException ex){
			showStatus(lblInvestorStatus, "DB Error: " + ex.getMessage(), Color.RED);
		}
	}

	private void showStatus(JLabel lbl, String msg, Color color) {
		lbl.setText(msg);
		lbl.setForeground(color);
		
	}

	// Utilities
	
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
