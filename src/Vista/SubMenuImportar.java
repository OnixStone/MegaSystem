package Vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Controlador.ConexionBD;

import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class SubMenuImportar extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldNombreBD;
	private JTextField textFieldTablaSeleccionada;
	private String tablaSeleccionada;
	//private SubMenuImportar jonathan = new SubMenuImportar(con, bd);
	
	private ConsultaSQL actualBD;
	DefaultListModel modeloLista = new DefaultListModel();
	
	/**
	 * Create the dialog.
	 */
	public SubMenuImportar(Connection con, String bd) {
		setTitle("SubMenu Importar Excel a SQL Server");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblListaTablasBD = new JLabel("Seleccione una de las tablas existentes en la base de datos:");
		lblListaTablasBD.setBounds(25, 47, 352, 14);
		contentPanel.add(lblListaTablasBD);
		
		JLabel lblNombreBD = new JLabel("Base de Datos Actual:");
		lblNombreBD.setBounds(25, 11, 137, 14);
		contentPanel.add(lblNombreBD);
		
		textFieldNombreBD = new JTextField();
		textFieldNombreBD.setForeground(Color.BLUE);
		textFieldNombreBD.setEditable(false);
		textFieldNombreBD.setBounds(172, 8, 232, 20);
		contentPanel.add(textFieldNombreBD);
		textFieldNombreBD.setColumns(10);
		textFieldNombreBD.setText(bd);
		
		textFieldTablaSeleccionada = new JTextField();
		textFieldTablaSeleccionada.setBounds(150, 194, 254, 20);
		contentPanel.add(textFieldTablaSeleccionada);
		textFieldTablaSeleccionada.setColumns(10);
				
		JList listTablasBD = new JList();
		listTablasBD.setBounds(59, 72, 318, 107);
		listTablasBD.setModel(modeloLista);
		String consultaSQLTablas = "SELECT * FROM information_schema.tables";
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(consultaSQLTablas);
			listTablasBD.removeAll();
			while(rs.next()) {
				modeloLista.addElement(rs.getString(3));

				System.out.println("Tabla = "+rs.getString(3));

			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		contentPanel.add(listTablasBD);
		listTablasBD.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int index = listTablasBD.getSelectedIndex();
				String tablaSeleccionada = (String) modeloLista.getElementAt(index);
				textFieldTablaSeleccionada.setText(tablaSeleccionada);
			}
		});
		
		
		JLabel lblTablaBDseleccionada = new JLabel("Tabla seleccionada:");
		lblTablaBDseleccionada.setBounds(25, 197, 106, 14);
		contentPanel.add(lblTablaBDseleccionada);
		

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton_Sm = new JButton("OK");
				okButton_Sm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						tablaSeleccionada=textFieldTablaSeleccionada.getText();
						System.out.println(textFieldTablaSeleccionada.getText());
						setVisible (false);
					}
				});
				okButton_Sm.setActionCommand("OK");
				buttonPane.add(okButton_Sm);
				getRootPane().setDefaultButton(okButton_Sm);
			}
			{
				JButton cancelButton_SM = new JButton("Cancel");
				cancelButton_SM.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
						//tablaSeleccionada=textFieldTablaSeleccionada.getText();
					}
				});
				cancelButton_SM.setActionCommand("Cancel");
				buttonPane.add(cancelButton_SM);
			}
		}
	}

	public String getTablaSeleccionada() {
		return tablaSeleccionada;
	}

}
