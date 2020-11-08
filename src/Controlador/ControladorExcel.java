package Controlador;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import Vista.ConsultaSQL;
import Modelo.ModeloExcel;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Esta clase define objetos que permiten controlar los eventos disparados por el usuario a nivel 
 * del menu de la aplicacion, mediante la implementacion de la interfase ActionListener y el 
 * metodo actionPerformed
 * @author Jonathan Neves
 * @version 06/10/2020/A
 * @see <a href = "http://chuwiki.chuidiang.org/index.php?title=ActionListener" /> Los ActionListener </a>
 */
public class ControladorExcel implements ActionListener { // se implementa la interfase ActionListener

	//Campo de la clase
	ModeloExcel modeloE;
	ConsultaSQL vistaE;
	JFileChooser selecArchivo = new JFileChooser();
	File archivo;
	int contAccion=0;
	
	/**
	 * Constructor que permite crear objetos que reaccionan con la interaccion del usuario y el menu de 
	 * la aplicacion, y en funcion a ello ejecutar los métodos correspondientes.
	 * @param vistaE Parametro que contiene la GUI de la aplicacion, que abarca el menu de opciones
	 * @param modeloE Parametro que contiene objetos que permiten emplear los metodos invocados en los menus de la aplicacion
	 */
	public ControladorExcel(ConsultaSQL vistaE, ModeloExcel modeloE) {
		this.vistaE = vistaE;
		this.modeloE = modeloE;
		this.vistaE.menuItem_importFromExcel.addActionListener(this); //añadir esta clase al componente java usando su método addActionListener()
		this.vistaE.menuItem_exportToSQL.addActionListener(this);     //que permitira llamar al metodo actionPerformed y ejecutar las funciones requeridas
		this.vistaE.menuItem_exportToExcel.addActionListener(this);
		this.vistaE.menuItem_estructuraBD.addActionListener(this);
		this.vistaE.menuItem_imprimir.addActionListener(this);
		
	}
	
	public void AgregarFiltro() {
		selecArchivo.setFileFilter(new FileNameExtensionFilter("Excel (*.xls)", "xls"));
		selecArchivo.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		contAccion++;
		
		if(contAccion==1)AgregarFiltro();
		
		if((e.getSource() == vistaE.menuItem_importFromExcel) && (contAccion % 2 == 1)) {
			if(selecArchivo.showDialog(null, "Seleccionar archivo")==JFileChooser.APPROVE_OPTION) {
				archivo=selecArchivo.getSelectedFile();
				if(archivo.getName().endsWith("xls") || archivo.getName().endsWith("xlsx")) {
					JOptionPane.showMessageDialog(null, modeloE.Importar(archivo, vistaE.tableResultadoSQL));
					JOptionPane.showMessageDialog(null, modeloE.GrabarJTable3(vistaE.getMiConexion(), vistaE.getBdseleccionada(), vistaE.tableResultadoSQL));
					vistaE.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(null, "Eliga un formato válido");
				}
			}
		}
		
		
		if(e.getSource() == vistaE.menuItem_exportToExcel) { 
			if(selecArchivo.showDialog(null, "Exportar")==JFileChooser.APPROVE_OPTION) {
				archivo=selecArchivo.getSelectedFile(); 
				if(archivo.getName().endsWith("xls") || archivo.getName().endsWith("xlsx")) { 
					JOptionPane.showMessageDialog(null, modeloE.Exportar(archivo, vistaE.tableResultadoSQL)); 
				} else {
					JOptionPane.showMessageDialog(null, "Eliga un formato válido"); 
				}
			} 
		}
        
		if(e.getSource() == vistaE.menuItem_exportToSQL) {
			JOptionPane.showMessageDialog(null, modeloE.GrabarJTable3(vistaE.getMiConexion(), vistaE.getBdseleccionada(), vistaE.tableResultadoSQL));
			vistaE.setVisible(true);
		}
		
		if(e.getSource() == vistaE.menuItem_estructuraBD) {
			JOptionPane.showMessageDialog(null, modeloE.mostrarEstructuraBD(vistaE.getMiConexion(), vistaE.tableResultadoSQL)); 
			vistaE.setVisible(true);
		}
		
		if(e.getSource() == vistaE.menuItem_imprimir) {
			JOptionPane.showMessageDialog(null, modeloE.printJTable(vistaE.tableResultadoSQL)); 
			vistaE.setVisible(true);
		}
	}
}