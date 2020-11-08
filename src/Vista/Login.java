package Vista;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Controlador.ConexionBD;
import Vista.ConsultaSQL;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.ItemSelectable;

import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

/**
 * Esta es la clase principal del proyecto, que instancia objeto JFrame que consiste en 
 * una ventana donde se solicitan los parametros de conexión con el motor de base de 
 * datos SQL Server y se procede a realizar la conexión con dichos parámetros.  
 * @author Jonathan Neves Leiros
 * @version 06/10/2020/A
 */

public class Login extends JFrame {

	//Campos de la clase
	ConexionBD con = new ConexionBD();
	private JPanel contentPane;
	private JTextField txtUsuario;
	private JPasswordField pwdPassword;
	Connection miConexion;
	private JTextField textField_Servidor;
	private JTextField txtPuerto;
	private Object item;

	/**
	 * Constructor para la ventana de login a la base de datos
	 */
	public Login() {
		setTitle("[ Login ]");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 478, 340);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTitulo = new JLabel("Ingresa tus datos");
		lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblTitulo.setBounds(108, 22, 223, 29);
		contentPane.add(lblTitulo);
		
		txtPuerto = new JTextField();
		txtPuerto.setBounds(353, 80, 71, 20);
		contentPane.add(txtPuerto);
		txtPuerto.setColumns(10);
		
		JLabel lblPuerto = new JLabel("Puerto:");
		lblPuerto.setBounds(366, 54, 46, 14);
		contentPane.add(lblPuerto);
		
		JLabel lblServidor = new JLabel("Servidor");
		lblServidor.setBounds(55, 83, 71, 14);
		contentPane.add(lblServidor);
		
		textField_Servidor = new JTextField();
		textField_Servidor.setBounds(145, 80, 198, 20);
		contentPane.add(textField_Servidor);
		textField_Servidor.setColumns(10);
		JLabel lblUsuario = new JLabel("Usuario:");
		lblUsuario.setBounds(55, 124, 60, 14);
		contentPane.add(lblUsuario);
		
		txtUsuario = new JTextField();
		txtUsuario.setForeground(Color.BLACK);
		txtUsuario.setBounds(145, 121, 198, 17);
		contentPane.add(txtUsuario);
		txtUsuario.setColumns(10);
		
		JLabel lblPassword = new JLabel("Contrase\u00F1a:");
		lblPassword.setBounds(55, 164, 74, 14);
		contentPane.add(lblPassword);
		
		pwdPassword = new JPasswordField();
		pwdPassword.setForeground(Color.BLACK);
		pwdPassword.setBounds(145, 162, 198, 17);
		contentPane.add(pwdPassword);
		
		JComboBox cmbBD = new JComboBox();
		cmbBD.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent itemEvent) {
		    	  
		    	  JComboBox comboBox =  (JComboBox) itemEvent.getSource();
		    	  
		    	  item = itemEvent.getItem(); //se obtiene la base de datos seleccionada por el usuario
		    	      	  
		    	  if (itemEvent.getStateChange() == ItemEvent.SELECTED) {

		    		  if (item.toString().isBlank()==false){ //si hay seleccionada una base de datos...
		        									
							setVisible(false); //...se invisibiliza la ventana login
							String s = textField_Servidor.getText();
							String p = txtPuerto.getText();
							String u = txtUsuario.getText();
							String c = pwdPassword.getText();
							
							//Se establece nueva conexion con la base de datos seleccionada por el usuario
							ConsultaSQL dialogo = new ConsultaSQL(con,s,p,u,c,item.toString());
							
							dialogo.setModal(true);
							dialogo.setVisible(true); //... y se visibiliza la ventana de consultas
							setVisible(true);
		    		  }
		    	  }
		      }
		});
		cmbBD.setBounds(145, 239, 198, 22);
		contentPane.add(cmbBD);
				
		JButton btnAcceder = new JButton("Acceder");
		//Este escuchador establece conexion con motor de BD y consulta BD disponibles
		//actualizando el contenido de la lista desplegable
		btnAcceder.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				try {
					String s = textField_Servidor.getText();
					String p = txtPuerto.getText();
					String u = txtUsuario.getText();
					String c = pwdPassword.getText();
					
					//Se establece conexion con motor BD y se consultan las BD disponibles
					miConexion=con.ConectarSQLServer(s, p, u, c,"");
					con.resultado = con.sentencia.executeQuery("SELECT name FROM master.sys.databases;");
					cmbBD.removeAllItems();
					cmbBD.addItem("");
					while(con.resultado.next()) {
						//Se agregan a la lista desplegable las BD disponibles
						cmbBD.addItem(con.resultado.getString(1)); 
					}
				} catch (SQLException ex) {
					Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		btnAcceder.setBounds(200, 198, 89, 23);
		contentPane.add(btnAcceder);
		
		JLabel lblBasesdeDatos = new JLabel("Base de Datos:");
		lblBasesdeDatos.setBounds(46, 243, 89, 14);
		contentPane.add(lblBasesdeDatos);
		
	} //Cierre del constructor
	
	//Metodos Getters y Setters
	public Object getItem() {
		return item;
	}

	public ConexionBD getCon() {
		return con;
	}
	
	/**
	 * Método Main que permite el inicio de la aplicación
	 * @param args Paso de argumentos por consola al método main
	 */
	public static void main(String[] args) {
		
		Login acceso = new Login();
		acceso.setVisible(true);
		
	} //Cierre del método
} //Cierre de la clase

