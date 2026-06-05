package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class QueryPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

    // ── Pattern 1  
    private JComboBox<String> cmbP1Artist;
    private JComboBox<String> cmbP1Sort;
    private JTable            tblP1;
    private DefaultTableModel mdlP1;
    private JLabel            lblP1Count;

    // ── Pattern 2
    private JTextField        txtP2FromYear;
    private JTextField        txtP2ToYear;
    private JComboBox<String> cmbP2Sort;
    private JTable            tblP2;
    private DefaultTableModel mdlP2;
    private JLabel            lblP2Count;


	
	public QueryPanel() {
		setLayout(new BorderLayout(0, 0));

        JLabel hint = new JLabel("  Set filters and click Run Query to display results.");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        hint.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
        add(hint, BorderLayout.NORTH);

        JTabbedPane subTabs = new JTabbedPane(JTabbedPane.LEFT);
        subTabs.addTab("Songs by Artist",       buildPattern1());
        subTabs.addTab("Albums by Year Range",  buildPattern2());
        
        add(subTabs, BorderLayout.CENTER);
		
	}

	private Component buildPattern2() {
		// TODO Auto-generated method stub
		return null;
	}

	private Component buildPattern1() {
		// TODO Auto-generated method stub
		return null;
	}
}
