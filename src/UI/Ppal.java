package UI;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Objects;

import javax.swing.JOptionPane;

import com.mysql.cj.xdevapi.Result;

import entities.*;


public class Ppal {

	public static void main(String[] args) {
		
		// registrar el conector
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
		Menu();
	}
		
	public static void Menu() {
			
		Boolean salir = false;
		while (!salir) {
			
			JOptionPane.showMessageDialog(null,
						"1) Listar productos\n2) Buscar producto\n3) Nuevo producto\n"
					+   "4) Eliminar producto\n5) Modificar producto\n0) Salir ");
				
			String op = JOptionPane.showInputDialog("Ingrese una opcion: ");
			
			try {
				
				switch (op) {
				case "0":
					salir = true;
					break;

				case "1":
					listarProductos();
					break;
				
				case "2":
					buscarProducto();
					break;
				
				case "3":
					altaProducto();
					break;
					
				case "4":
					bajaProducto();
					break;
					
				case "5":
					actualizaProducto();
					break;
					
				default:
					JOptionPane.showMessageDialog(null, "Las opciones son entre 0 y 5", null, JOptionPane.ERROR_MESSAGE);
				}
				
			} catch(NullPointerException e){
				JOptionPane.showMessageDialog(null,"Debes introducir un valor.", null,
				JOptionPane.ERROR_MESSAGE);
				
			}
		
		}
	
	}
	
	public static void altaProducto() {
		
		Connection conn = null;
		
		// Instanciamos el nuevo objeto Producto en Java y seteamos los datos
		Product pIns = new Product();
		pIns.setName(JOptionPane.showInputDialog("Ingrese el nombre del producto: "));
		pIns.setDescription(JOptionPane.showInputDialog("Ingrese descripcion: "));
		pIns.setPrice(Integer.parseInt(JOptionPane.showInputDialog("Ingrese el precio: ")));
		pIns.setStock(Integer.parseInt(JOptionPane.showInputDialog("Ingrese el stock: ")));
		
		int shipping = JOptionPane.showConfirmDialog(null, "¿Envío incluido?: ");
		if (shipping == 1) {pIns.setShippingIncluded(false);}
		else{pIns.setShippingIncluded(true);}
		
		try {
			//Creamos la conexion
			conn = DriverManager.getConnection("jdbc:mysql://localhost/javaMarket","root","root");
			 	 
				 /*
				 
				 Como el IdProduct es un atributo autoincremental, no le asignamos un numero arbitrario,
				 se lo dejamos a la DB.
				 De todas formas, debemos recuperar el valor para utilizarlo
				 
				 Malas practicas: Utilizar Select max id para obtenber el ultimo (consume muchos recursos)
				 
				 
				 */
			
				//Definimos la query
				PreparedStatement pstmt = conn.prepareStatement(
				"INSERT INTO Product(name,description,price,stock,shippingIncluded) values (?,?,?,?,?)"
	           	,PreparedStatement.RETURN_GENERATED_KEYS);
			
				 //Mapeamos los valores ?1, ?2, ...
				 pstmt.setString(1, pIns.getName());
				 pstmt.setString(2, pIns.getDescription());
				 pstmt.setDouble(3, pIns.getPrice());
				 pstmt.setInt(4, pIns.getStock());
				 pstmt.setBoolean(5, pIns.isShippingIncluded());
				 
				 //Ejecutamos la Query
				 pstmt.executeUpdate();
				 
				 
				 ResultSet keyResultSet=pstmt.getGeneratedKeys();
	
				 //Obtengo el id autoincremental de la DB
		         if(keyResultSet!=null && keyResultSet.next()) {
		        	 int id=keyResultSet.getInt(1);
//		             System.out.println("ID: "+id);
		             pIns.setId(id);
		         }
		         
		    
	         // Cerrar conexion
		         if(keyResultSet!=null){keyResultSet.close();}
		         if(pstmt!=null){pstmt.close();}
		         conn.close();
	         
	         JOptionPane.showMessageDialog(null, "Registro agregado con exito");

			
		} catch(SQLException ex){
			 // Manejo de errores
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		
		
		
	} // end altaProducto
	
public static void buscarProducto() {
		
		Connection conn = null;
		
		try {
			
			//Creamos la conexion
			conn = DriverManager.getConnection("jdbc:mysql://localhost/javaMarket","root","root");
			
			// Definimos la query
            
			PreparedStatement stmt = conn.prepareStatement("select * from Product where idProduct=?"); 
            /*
			
            Utilizamos PreparedSatements para setear los parametros sin tener que concatenar en la query
            Una concatenacion en una query es una muy mala práctica --> SQL INJECTION
			
			Ante cualquier input de un usuario, es obligatorio usar una statement que utilice
			parametros y los sanitice.
			Escapamos de caracteres sql que pueden volverse maliciosos.
			
			*/
			
			//Seteamos los parametros de la Query
			stmt.setInt(1, Integer.parseInt(JOptionPane.showInputDialog("Ingrese el id del producto: ")));
			
			Product p = new Product();
			
			// Ejecutamos la Query
			ResultSet rs = stmt.executeQuery();
			
			//Mapeamos los datos del ResulSet a un objeto
			
			if(rs.next()) {
				p.setId(rs.getInt("idProduct"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getInt("price"));
                p.setStock(rs.getInt("stock"));
                p.setShippingIncluded(rs.getBoolean("shippingIncluded"));
			}
			
			// Cerramos los recursos
			if(rs!=null){rs.close();}
            if(stmt!=null){stmt.close();}
			conn.close();

			// Mostramos el producto
			if (p.getId() != 0) {
				System.out.println("Id        Name                  Description         Price      Stock      shippingIncluded");
				System.out.println("------------------------------------------------------------------------------------------");    
				System.out.println(p.mostrarDatos());
			}
			else {
				JOptionPane.showMessageDialog(null, "Id inexistente");
			}
		   
			
		} catch (SQLException ex) {
			// Manejo de errores
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		
		
	} // end buscarProductos
	
	public static void listarProductos() {
		
		Connection conn = null;
		LinkedList<Product> products = new LinkedList<>();
		
		try {
			//crear conexion
			conn = DriverManager.getConnection("jdbc:mysql://localhost/javaMarket","root","root");
				
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select * from Product"); //El 'puntero' rs apunta a un registro -1
				
				while(rs.next()) { // rs pasa a apuntar al registro 0
					
					Product p=new Product();
					
					// mapeo: 
					//recupero los atributos de la DB y los asigno a un nuevo objeto Java
	                p.setId(rs.getInt("idProduct"));
	                p.setName(rs.getString("name"));
	                p.setDescription(rs.getString("description"));
	                p.setPrice(rs.getInt("price"));
	                p.setStock(rs.getInt("stock"));
	                p.setShippingIncluded(rs.getBoolean("shippingIncluded"));

	                products.add(p);
	            }
				
			// cerrar conexion
				
			if(rs!=null){rs.close();}
            if(stmt!=null){stmt.close();}
			conn.close();
			
		} catch (SQLException ex) {
			// Manejo de errores
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		// mostrar info
		
		
	    System.out.println("Id        Name                  Description         Price      Stock      shippingIncluded");
	    System.out.println("------------------------------------------------------------------------------------------");
	    
	    for (Product product : products) {
			System.out.println(product.mostrarDatos());
		}
	    
		
	} // end listarProductos
	
	
	public static void bajaProducto() {
		
		Connection conn = null;
		
		try {
			
			// Creamos conexion
			conn = DriverManager.getConnection("jdbc:mysql://localhost/javaMarket","root","root");
			
			// Definimos la query
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM Product WHERE idProduct=?", PreparedStatement.RETURN_GENERATED_KEYS);
			
			// Seteamos el parámetro a la query
			stmt.setInt(1, Integer.parseInt(JOptionPane.showInputDialog("Ingrese el id del producto que desea eliminar")));
			
			// Ejecutamos la query
			stmt.executeUpdate();
			
			//Cerramos recursos
            if(stmt!=null){stmt.close();}
			conn.close();
			
			JOptionPane.showMessageDialog(null, "Registro eliminado con exito");
			
		} catch (SQLException ex) {
			// Manejo de errores
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
	} // end bajaProducto
	

	public static void actualizaProducto() {
		
		 Connection conn = null;
		 ResultSet rs = null;
		 
		 try {
			 
			 conn = DriverManager.getConnection("jdbc:mysql://localhost/javaMarket","root","root");
			 
			 Integer idBuscado = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el id: "));
			 
			 PreparedStatement pstmtShow = conn.prepareStatement("SELECT * FROM Product WHERE idProduct=?");
			 
			 pstmtShow.setInt(1, idBuscado);
			 
			 Product p = new Product();
			 p.setName(JOptionPane.showInputDialog("Ingrese el nombre del producto: "));
			 p.setDescription(JOptionPane.showInputDialog("Ingrese descripcion: "));
			 p.setPrice(Integer.parseInt(JOptionPane.showInputDialog("Ingrese el precio: ")));
			 p.setStock(Integer.parseInt(JOptionPane.showInputDialog("Ingrese el stock: ")));
			 int shipping = JOptionPane.showConfirmDialog(null, "¿Envío incluido?: ");
			 if (shipping == 1) {p.setShippingIncluded(false);}
			 else{p.setShippingIncluded(true);}
			 
			 rs = pstmtShow.executeQuery();
			 
			 if(rs.next()) {
					p.setId(rs.getInt("idProduct"));
	                p.setName(rs.getString("name"));
	                p.setDescription(rs.getString("description"));
	                p.setPrice(rs.getInt("price"));
	                p.setStock(rs.getInt("stock"));
	                p.setShippingIncluded(rs.getBoolean("shippingIncluded"));
				}
			 
			 JOptionPane.showMessageDialog(null, p.mostrarDatos());
			 
			 
			 PreparedStatement pstmUpdate = conn.prepareStatement("UPDATE Product SET name=?,description=?, price=?, stock=?, shippingIncluded=? WHERE idProduct=?", 
				PreparedStatement.RETURN_GENERATED_KEYS);
			 			 
			 pstmUpdate.setString(1, p.getName());
			 pstmUpdate.setString(2, p.getDescription());
			 pstmUpdate.setDouble(3, p.getPrice());
			 pstmUpdate.setInt(4, p.getStock());
			 pstmUpdate.setBoolean(5, p.isShippingIncluded());
			 pstmUpdate.setInt(6, idBuscado);
			 
			 pstmUpdate.executeUpdate();
			 
			 rs = pstmUpdate.getGeneratedKeys();
			 
			 if(rs != null && rs.next()) {
	        	 int id = rs.getInt(1);
	             p.setId(id);
			 }
			 
			 p.setId(idBuscado);
			 
			 JOptionPane.showMessageDialog(null, p.mostrarDatos());
			 			 
			 
		} catch (SQLException ex) {
			// Manejo de errores
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		
		
		
	} // end actualizaProducto
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
