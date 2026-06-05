package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class GUI extends JFrame{
	private static final long serialVersionUID = 1;
	
	private JTabbedPane tabbedPane;
	private InsertionPanel insertionPanel;
	private ModificationPanel modificationPanel;
	private QueryPanel queryPanel;
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				GUI frame = new GUI();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
//		initialize();
		setTitle("Stock Trade Management System");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(120, 80, 980, 700);
		setMinimumSize(new Dimension(820, 580));
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int choice = JOptionPane.showConfirmDialog(GUI.this, "Exit the applicaiton? ", "Confirm Exit. ", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					DatabaseConnection.closeConnection();
					dispose();
					System.exit(0);
				}
			}
		});
		// Status bar at bottom.
		JLabel statusBar = new JLabel("  Ready");
		statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
		statusBar.setPreferredSize(new Dimension(getWidth(), 22));
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		// Header banner.
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
		headerPanel.setBackground(new Color(45, 45, 60));
		JLabel headerLabel = new JLabel("Stock Trade & Account Management");
		headerLabel.setForeground(Color.WHITE);
		headerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		headerPanel.add(headerLabel);
		getContentPane().add(headerPanel, BorderLayout.NORTH);
		
		// Three Tabs on the side.
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 13));
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		insertionPanel = new InsertionPanel();
		modificationPanel = new ModificationPanel();
		queryPanel = new QueryPanel();
		
		
		tabbedPane.addTab("Data Insertion ", null, insertionPanel, "Insert new investors, companies, or stocks.");
		tabbedPane.addTab("Data Modification", null, modificationPanel, "Modify existing investors, companies, or stocks.");
		tabbedPane.addTab("Data Query", null, queryPanel, "Filter stored database records.");
		
/*		tabbedPane.addChangeListener(e -> {
			int idx = tabbedPane.getSelectedIndex();
			if (idx == 1) modificationPanel.refreshCombos();
			if (idx == 2) queryPanel.refreshCombos();
		});*/
	}

	/**
	 * Initialize the contents of the frame.
	 */
	/* private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}*/

}
