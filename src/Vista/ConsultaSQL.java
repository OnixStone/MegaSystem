package Vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import Controlador.ConexionBD;
import Modelo.ConversorSqlModel;
import Modelo.ModeloExcel;
import Controlador.ControladorExcel;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Esta clase define el objeto JDialog que sirve de GUI para realizar las consultas
 * a base de datos, visualizar los resultados obtenidos e interactuar con los menús
 * de opciones, que permiten ejecutar las funcionalidades adicionales descritas en 
 * la documentación del proyecto.   
 * @author Jonathan Neves Leiros
 * @version 06/10/2020/A
 */
public class ConsultaSQL extends JDialog {

	//Campos de la clase
	private final JPanel contentPanel = new JPanel();
	ConexionBD con = new ConexionBD();
	Connection miConexion;
	Statement st, st2, st3, st4;
	ResultSet rs, rs2, rs3;
	int rs4;
	public JTable tableResultadoSQL;
	public JTextArea textAreaConsulta;
	DefaultTableModel modelo = new DefaultTableModel();
	ModeloExcel file = new ModeloExcel();
	JMenuBar barra;
	JMenu menu_archivo;
	JMenu menu_opciones;
	JMenu menu_ayuda;
	public JMenuItem menuItem_importFromExcel;
	public JMenuItem menuItem_exportToExcel;
	public JMenuItem menuItem_exportToSQL;
	public JMenuItem menuItem_estructuraBD;
	public JMenuItem menuItem_imprimir;
	public JMenuItem menuItem_configuracion;
	public JMenuItem menuItem_ayuda;
	public JMenuItem menuItem_acercade;
	private String bdseleccionada;
	JLabel lblNewLabel_1;

	//Métodos Getter y Setter
	public String getBdseleccionada() {
		return bdseleccionada;
	}

	public Connection getMiConexion() {
		return miConexion;
	}

	public Statement getSt() {
		return st;
	}

	public void setSt(Statement st) {
		this.st = st;
	}

	public ResultSet getRs() {
		return rs;
	}

	public void setRs(ResultSet rs) {
		this.rs = rs;
	}

	/**
	 * Constructor de la GUI principal de la aplicación
	 * @param con Parámetro que proporciona una conexion a base de datos conforme lo especificado por el usuario 
	 * @param servidor Parámetro que proporciona el nombre del servidor conforme lo especificado por el usuario
	 * @param puerto Parámetro que proporciona el número del puerto de enlace con la base de datos conforme lo especificado por el usuario
	 * @param usuario Parámetro que proporciona el nombre del usuario autorizado conforme lo especificado por el usuario de la aplicación
	 * @param pwd Parámetro que proporciona la clave de acceso al servidor de base de datos conforme lo especificado por el usuario
	 * @param bd Parámetro que proporciona el nombre de la base de datos específica conforme lo indicado por el usuario
	 */
	public ConsultaSQL(ConexionBD con, String servidor, String puerto, String usuario, String pwd, String bd) {
		bdseleccionada = bd;
		setTitle("Consultas SQL");
		setBounds(100, 100, 1084, 562);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(8, 164, 1050, 305);
			contentPanel.add(scrollPane);

			tableResultadoSQL = new JTable(modelo); //se instancia un JTable y se le pasa al constructor como parámetro el modelo
			scrollPane.setViewportView(tableResultadoSQL);
			tableResultadoSQL.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

			tableResultadoSQL.setModel(modelo); //se establece el DefaultTableModel como modelo del JTable

			textAreaConsulta = new JTextArea();
			textAreaConsulta.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			textAreaConsulta.setBounds(10, 32, 1048, 93);

			textAreaConsulta.addKeyListener(new KeyAdapter() { //se emplea un escuchador del teclado en el area de consultas
				@Override
				public void keyPressed(KeyEvent e) { //se obtiene el evento de presionar una tecla...
					if (e.getKeyCode() == KeyEvent.VK_ENTER) { // ... y si esa tecla es Enter se ejecuta el bloque de código
						try {
							miConexion = con.ConectarSQLServer(servidor, puerto, usuario, pwd, bd);

							String sql2 = textAreaConsulta.getText(); //se almacena la sentencia SQL introducida por el usuario
							String hora="";

							st = miConexion.createStatement();
							long startTime = System.nanoTime(); //Se mide con precision al nanosegundo el instante en que se lanza...
							rs = st.executeQuery(sql2); // ... la consulta SQL contra el servidor de base de datos...
							long endTime = System.nanoTime() - startTime; // ... y se mide nuevamente el instante de tiempo en que termina, y se determina el tiempo transcurrido.
							System.out.println("Tiempo de respuesta de la consulta [nanosegundos] =" + endTime);
							lblNewLabel_1.setText("Resultado de la consulta procesado en " + (endTime / 1000000) + " milisegundos"
									+ " a las "+ hora + " horas." ); // se presentan los resultados como actualización de la etiqueta señalada

							ConversorSqlModel.rellena(rs, modelo); // llamado del metodo estatico que permite pasar el ResulSet obtenido al modelo
							tableResultadoSQL.setModel(modelo); // y el modelo rellenado con el ResultSet pasado a la JTable, actualizandola automáticamente

							rs.close();

						} catch (SQLException sqle) {

							// sqle.printStackTrace();
							System.out.println("Código de Error: " + sqle.getErrorCode() + "\n" + "SLQState: "
									+ sqle.getSQLState() + "\n" + "Mensaje: " + sqle.getMessage() + "\n");

							JOptionPane.showMessageDialog(null, sqle.getMessage());

						}
					}
				}
			});

			contentPanel.add(textAreaConsulta);

			JLabel lblNewLabel = new JLabel("Introduzca su consulta SQL (Pulse Intro para ejecutar):");
			lblNewLabel.setForeground(new Color(0, 0, 255));
			lblNewLabel.setBounds(10, 11, 323, 14);
			contentPanel.add(lblNewLabel);

			lblNewLabel_1 = new JLabel("Resultado de la consulta:");
			lblNewLabel_1.setForeground(new Color(0, 0, 255));
			lblNewLabel_1.setBounds(10, 139, 602, 14);
			contentPanel.add(lblNewLabel_1);

			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton SalirButton = new JButton("Salir");
				SalirButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				SalirButton.setActionCommand("Salir");
				buttonPane.add(SalirButton);
				getRootPane().setDefaultButton(SalirButton);
			}
			{
				JButton volverButton = new JButton("Volver");
				volverButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				volverButton.setActionCommand("Volver");
				buttonPane.add(volverButton);
			}
		}

		crearMenu(this);  //Llamada al metodo que crea el Menu de la aplicacion
		
	}//Cierre del Constructor

	/**
	 * Metodo que permite crear el menu y los submenus de la aplicacion
	 * @param vista Parametro que contiene el objeto que representa la GUI de consultas y resultados de la aplicacion
	 */
	public void crearMenu(ConsultaSQL vista) {
		barra = new JMenuBar();
		menu_archivo = new JMenu("Archivo");
		menu_opciones = new JMenu("Opciones");
		menu_ayuda = new JMenu("Ayuda");
		menuItem_importFromExcel = new JMenuItem("Importar Excel a SQL Server");
		menuItem_exportToExcel = new JMenuItem("Exportar JTable a Excel");
		menuItem_exportToSQL = new JMenuItem("Exportar JTable a SQL Server");
		menuItem_estructuraBD = new JMenuItem("Mostrar estructura BD");
		menuItem_imprimir = new JMenuItem("Imprimir JTable");
		menuItem_ayuda = new JMenuItem("Ayuda");
		menuItem_acercade = new JMenuItem("Acerca de");
		menu_archivo.add(menuItem_importFromExcel);
		menu_archivo.add(menuItem_exportToExcel);
		menu_archivo.add(menuItem_exportToSQL);
		menu_opciones.add(menuItem_estructuraBD);
		menu_opciones.add(menuItem_imprimir);
		menu_ayuda.add(menuItem_ayuda);
		menu_ayuda.add(menuItem_acercade);
		barra.add(menu_archivo);
		barra.add(menu_opciones);
		barra.add(menu_ayuda);
		setJMenuBar(barra);

		// Eventos de Menú
		ControladorExcel CE = new ControladorExcel(this, file); //se instancia un objeto de la clase donde se definen las acciones en funcion 
																//de los eventos que se generen al interactuar con las opciones del menu

	} //Cierre del metodo

} //Cierre de la clase
