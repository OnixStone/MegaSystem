package Modelo;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import Controlador.ConexionBD;
import Vista.ConsultaSQL;
import Vista.SubMenuImportar;


public class ModeloExcel {
	Workbook wb;
	SubMenuImportar tablaSeleccionada;
	
	public String Importar(File archivo, JTable tablaD){
		String respuesta="No se pudo realizar la importación.";
		DefaultTableModel modeloT = new DefaultTableModel();
		tablaD.setModel(modeloT);
		try {
			wb = WorkbookFactory.create(new FileInputStream(archivo));
			Sheet hoja = wb.getSheetAt(0);
			Iterator filaIterator = hoja.rowIterator();
			int indiceFila = -1;
			while (filaIterator.hasNext()) {
				indiceFila++;
				Row fila = (Row) filaIterator.next();
				Iterator columnaIterator = fila.cellIterator();
				
				Object[] listaColumna = new Object[tablaD.getColumnCount()];
				int indiceColumna = -1;
				while (columnaIterator.hasNext()) {
					indiceColumna++;
					Cell celda = (Cell) columnaIterator.next();
					if(indiceFila==0) {
						modeloT.addColumn(celda.getStringCellValue());
					} else {
						if(celda!=null) {
							switch(celda.getCellType()) {
								case NUMERIC:
									listaColumna[indiceColumna]=(int)Math.round(celda.getNumericCellValue());
									break;
								case STRING:
									listaColumna[indiceColumna]=celda.getStringCellValue();
									break;
								case BOOLEAN:
									listaColumna[indiceColumna]=celda.getBooleanCellValue();
									break;
								default:
									listaColumna[indiceColumna]=celda.getDateCellValue();
									break;							
							}
						}
					}
				}
				if(indiceFila!=0)modeloT.addRow(listaColumna);
			}
			respuesta="Carga exitosa de archivo Microsoft Excel";
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return respuesta;
		
	}
	
	public String Exportar(File archivo, JTable tablaD) {
		String respuesta="No se realizó con éxito la exportación.";
		int numFila=tablaD.getRowCount(), numColumna=tablaD.getColumnCount();
		if(archivo.getName().endsWith("xls")) {
			wb = new HSSFWorkbook();
		} else {
			wb = new XSSFWorkbook();
		}
		Sheet hoja = wb.createSheet("Pruebita");
		try {
			for (int i = -1; i < numFila; i++) {
				Row fila = hoja.createRow(i+1);
				for (int j = 0; j < numColumna; j++) {
					Cell celda = fila.createCell(j);
					if(i==-1) {
						celda.setCellValue(String.valueOf(tablaD.getColumnName(j)));
					} else {
						celda.setCellValue(String.valueOf(tablaD.getValueAt(i, j)));
					}
					wb.write(new FileOutputStream(archivo));
				}
			}
			respuesta="Exportacion completada exitosamente.";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return respuesta;
		
	}

	public String GrabarJTable3(Connection con, String bd, JTable tablaD) {
		
		int cont = 0;
		String respuesta="No se realizó con éxito la exportación del JTable a SQL Server.";
		String tablaSeleccionada = "?";
				
		if(tablaD.getRowCount()>0) {

			SubMenuImportar vImport = new SubMenuImportar(con, bd);
			
			vImport.setModal(true);
			vImport.setVisible(true);
			
			tablaSeleccionada=vImport.getTablaSeleccionada();
			
			if(tablaSeleccionada!=null) {
				try {
					
	
					String parametros = "?,";
					
					String preConsulta = "INSERT INTO "+tablaSeleccionada+" VALUES (";
					
					for (int i = 0; i < tablaD.getColumnCount(); i++) {
						if(i<(tablaD.getColumnCount()-1)) {
							preConsulta += parametros;
						} else
							preConsulta += "?)";
					}
					System.out.println(preConsulta);
					
					PreparedStatement psInsertar = con.prepareStatement(preConsulta);
					for (int i = 0; i < tablaD.getRowCount(); i++) {
						for (int j = 1; j <= tablaD.getColumnCount(); j++) {
							System.out.println("Coordenada Tabla ("+i+", "+(j-1)+") = "+tablaD.getValueAt(i, (j-1)));
							psInsertar.setString(j, ""+tablaD.getValueAt(i, (j-1)));
						}
						psInsertar.executeUpdate();
						cont++;
					}
	
				} catch (Exception e) {
					e.printStackTrace();
				}
				respuesta = Integer.toString(cont) + " registros insertados en la tabla: "+ tablaSeleccionada +" de SQL Server.";
			} else {
				respuesta = " No se insertaron registros. Operación cancelada por el usuario.";
			}
		} else {
			JOptionPane.showMessageDialog(null, "La tabla se encuentra vacia");
		}
		return respuesta;
	}
	
	public String mostrarEstructuraBD(Connection con, JTable tablaD) {
		String respuesta="No se realizó con éxito la exportación.";		
		String consulta = "select schema_name(tab.schema_id) + '.' + tab.name as [table],"
				+ " col.column_id,"
				+ " col.name as column_name,"
				+ " case when fk.object_id is not null then '>-' else null end as rel,"
				+ " schema_name(pk_tab.schema_id) + '.' + pk_tab.name as primary_table,"
				+ " pk_col.name as pk_column_name,"
				+ " fk_cols.constraint_column_id as no,"
				+ " fk.name as fk_constraint_name"
				+ " from sys.tables tab"
				+ " inner join sys.columns col"
				+ " on col.object_id = tab.object_id"
				+ " left outer join sys.foreign_key_columns fk_cols"
				+ " on fk_cols.parent_object_id = tab.object_id"
				+ " and fk_cols.parent_column_id = col.column_id"
				+ " left outer join sys.foreign_keys fk"
				+ " on fk.object_id = fk_cols.constraint_object_id"
				+ " left outer join sys.tables pk_tab"
				+ " on pk_tab.object_id = fk_cols.referenced_object_id"
				+ " left outer join sys.columns pk_col"
				+ " on pk_col.column_id = fk_cols.referenced_column_id"
				+ " and pk_col.object_id = fk_cols.referenced_object_id"
				+ " order by schema_name(tab.schema_id) + '.' + tab.name,"
				+ " col.column_id";
		
		try {
			ConsultaSQL estructuraBD;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(consulta);
			
			DefaultTableModel modeloT = new DefaultTableModel();
			tablaD.setModel(modeloT);
			ConversorSqlModel.rellena(rs, modeloT);
			
			respuesta="Operación exitosa";
			
		} catch (SQLException sqle) {

			System.out.println("Código de Error: " + sqle.getErrorCode() + "\n" +
					  "SLQState: " + sqle.getSQLState() + "\n" +
					  "Mensaje: " + sqle.getMessage() + "\n");
			
			JOptionPane.showMessageDialog(null, sqle.getMessage());
			
		}

		return respuesta;
	}

	public String printJTable(JTable tablaD) {
		String respuesta="No se realizó con éxito la impresión.";
		
		try {
			
			tablaD.print();
			respuesta="Tabla enviada para impresión...";
			
        } catch (PrinterException ex) {
        	
        	ex.printStackTrace();
			System.out.println("Mensaje de Error: " + ex.getMessage() + "\n");
			
			JOptionPane.showMessageDialog(null, ex.getMessage());
        }
		
		return respuesta;
	}
}