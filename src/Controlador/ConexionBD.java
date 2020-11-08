package Controlador;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

import Vista.ConsultaSQL;

/**
 * Esta clase define objetos mediante su método constructor que permiten 
 * establecer la conexion con bases de datos relacionales en SQL Server 
 * mediante Microsoft JDBC Driver para SQL Server. Número de versión: 8.2.2.
 * @author Jonathan Neves Leiros
 * @version 06/10/2020/A
 */
public class ConexionBD {
	
	//Campos de la clase
	public Connection conexion=null;
	public Statement sentencia;
	public ResultSet resultado;
	
	/**
	 *Constructor para establecer conexiones JDBC para SQL Server
	 *@param servidor Nombre del servidor de base de datos
	 *@param puerto Puerto de enlace del servidor (por defecto: 1433)
	 *@param user Nombre del usuario del servidor de base de datos
	 *@param password Clave necesaria para acceder al servidor de base de datos
	 *@param bd Nombre de la base de datos específica a la cual se desea conectar 
	 *@return conexion Objeto de la clase Connection, que permite vincular Java con la base de datos conforme los parámetros establecidos por el usuario 
	 */
	public Connection ConectarSQLServer(String servidor, String puerto, String user, String password,String bd) {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			
			String connectionURL = "jdbc:sqlserver://"+servidor+":"+puerto+";databaseName="+bd+";user="+user+";password="+password+";";
			conexion = DriverManager.getConnection(connectionURL);
			sentencia = conexion.createStatement();
			if (conexion != null) {
				System.out.println("Conexion a SQL Server exitosa");
			}

			
		} catch (ClassNotFoundException | SQLException e) {
			JOptionPane.showMessageDialog(null,e.getMessage(), "Error en la conexion, verifique su usuario y password",
					JOptionPane.ERROR_MESSAGE);
		}
		return conexion;
	}
}
