package UI;

import java.sql.*;
import java.util.InputMismatchException;
import javax.swing.JOptionPane;
import entities.*;


public class main {

	public static void main(String[] args) {
		Menu();
	}
		
	public static void Menu() {
			
		Boolean salir = false;
		while (!salir) {
			
			JOptionPane.showMessageDialog(null,
						"1) Listar productos\n2) Buscar producto\n3) Nuevo producto\n"
					+   "4) Eliminar producto\n5) Modificar producto\n6) Salir ");
				
			String op = JOptionPane.showInputDialog("Ingrese una opcion: ");
			
			try {
				
				switch (op) {
				case "6":
					salir = true;
					break;

				case "1":
//					listarProductos();
					break;
				
				case "2":
//					buscarProducto();
					break;
				
				case "3":
					altaProducto();
					break;
					
				default:
					JOptionPane.showMessageDialog(null, "Las opciones son entre 1 y 6", null, JOptionPane.ERROR_MESSAGE);
				}
				
			} catch(NullPointerException e){
				JOptionPane.showMessageDialog(null,"Debes introducir un valor.", null,
				JOptionPane.ERROR_MESSAGE);
				
				
				
				
			}
		
		}
	
	}
	
	public static void altaProducto() {
		
		Connection conn = null;
		
		Product pIns = new Product();
		pIns.setName(JOptionPane.showInputDialog("Ingrese el nombre del producto: "));
		pIns.setDescription(JOptionPane.showInputDialog("Ingrese descripcion: "));
		pIns.setPrice(Integer.parseInt(JOptionPane.showInputDialog("Ingrese el precio: ")));
		pIns.setStock(Integer.parseInt(JOptionPane.showInputDialog("Ingrese el stock: ")));
		
		int shipping = JOptionPane.showConfirmDialog(null, "¿Envío incluido?: ");
		if (shipping == 1) {pIns.setShippingIncluded(false);}
		else{pIns.setShippingIncluded(true);}
		
		try {
			//crear conexion
			conn = DriverManager.getConnection("jdbc:mysql://localhost/javaMarket","root","root");
			
			//definir query
			 PreparedStatement pstmt = conn.prepareStatement(
	            "INSERT INTO Product(name,description,price,stock,shippingIncluded) values (?,?,?,?,?)"
	           	,PreparedStatement.RETURN_GENERATED_KEYS
	         );
			 
			 //setear valores
			 pstmt.setString(1, pIns.getName());
			 pstmt.setString(2, pIns.getDescription());
			 pstmt.setDouble(3, pIns.getPrice());
			 pstmt.setInt(4, pIns.getStock());
			 pstmt.setBoolean(5, pIns.isShippingIncluded());
			 
			 pstmt.executeUpdate();
			 
			 ResultSet keyResultSet=pstmt.getGeneratedKeys();

	         if(keyResultSet!=null && keyResultSet.next()) {
	        	 int id=keyResultSet.getInt(1);
	             System.out.println("ID: "+id);
	             pIns.setId(id);
	         }


	         if(keyResultSet!=null){keyResultSet.close();}
	         if(pstmt!=null){pstmt.close();}

	         conn.close();
	         
	         System.out.println(pIns);
			 System.out.println();System.out.println();
			    
			
		} catch(SQLException ex){
			 // Manejo de errores
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
