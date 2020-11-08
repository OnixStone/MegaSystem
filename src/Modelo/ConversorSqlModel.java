package Modelo;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.swing.table.DefaultTableModel;

/**
 * Convierte un ResultSet en un DefaultTableModel
 */
public class ConversorSqlModel
{
    public static void rellena(ResultSet rs, DefaultTableModel modelo)
    {
        configuraColumnas(rs, modelo);
        vaciaFilasModelo(modelo);
        anhadeFilasDeDatos(rs, modelo);
    }

    private static void anhadeFilasDeDatos(ResultSet rs,
            DefaultTableModel modelo)
    {
        int numeroFila = 0;
        try
        {
            while (rs.next())
            {
                Object[] datosFila = new Object[modelo.getColumnCount()];
                for (int i = 0; i < modelo.getColumnCount(); i++)
                    datosFila[i] = rs.getObject(i + 1);
                modelo.addRow(datosFila);
                numeroFila++;
            }
            rs.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void vaciaFilasModelo(final DefaultTableModel modelo)
    {
        while (modelo.getRowCount() > 0)
        	modelo.removeRow(0);
    }

    public static void configuraColumnas(final ResultSet rs,
            final DefaultTableModel modelo)
    {
    	try {
            ResultSetMetaData metaDatos = rs.getMetaData();
            int numeroColumnas = metaDatos.getColumnCount();
            Object[] etiquetas = new Object[numeroColumnas];
            for (int i = 0; i < numeroColumnas; i++)
            {
                etiquetas[i] = metaDatos.getColumnLabel(i + 1);
            }
            modelo.setColumnIdentifiers(etiquetas);

    	} catch (Exception e)
            {
                e.printStackTrace();
            }
   }
}
