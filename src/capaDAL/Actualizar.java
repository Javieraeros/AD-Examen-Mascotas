package capaDAL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.*;


public class Actualizar {
	/**
	 * Método que actualiza todas las tablas de la base de datos
	 * a partir de la tabla:BI_Actualizaciones
	 * 
	 * En caso de que surga un error , se avisará por la consola. en caso de que todo salga bien, también mostrará
	 * un mensaje.
	 */
	public void actualizaTablas(){
		DbConnexion miConexion=DbConnexion.getDbConnexion();
		Statement selectActualizaciones,actualizable;
		PreparedStatement preparedStatement,insert;
		ResultSet resultado,insertActualizable;
		
		String miSelect="Select Fecha,Temperatura,Peso,CodigoMascota,Raza,"+
		"Especie,FechaNacimiento,FechaFallecimiento,Alias,CodigoPropietario,"+
		"Enfermedad from BI_Actualizaciones";
		
		String selectMascotas="Select Codigo,Raza,Especie,FechaNacimiento,"+
		"FechaFallecimiento,Alias,CodigoPropietario from BI_Mascotas";
		
		//Nombre, FechaDiagnosis,CodigoMascota
		String prepared="Execute InsertaEnfermedadMascota ?,?,?";
		
		String insertCadena="Insert Into BI_Visitas([Fecha],[Temperatura],[Peso],[Mascota]) "+
     " VALUES(?,?,?,?)";
		try {
			miConexion.openConnection();
			
			//Select de la tabla actualizaciones
			selectActualizaciones=miConexion.getConexionBasedeDatos().createStatement();
			
			//Aqui guardamos los datos de dicha tabla
			resultado=selectActualizaciones.executeQuery(miSelect);
			
			
			//Necesario para que podamos actualizar desde el resulset
			actualizable=miConexion.getConexionBasedeDatos().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			
			//Resultset actualizable
			insertActualizable=actualizable.executeQuery(selectMascotas);
			
			//Inserts con consultas preparadas
			preparedStatement=miConexion.getConexionBasedeDatos().prepareStatement(prepared);
			insert=miConexion.getConexionBasedeDatos().prepareStatement(insertCadena);
			
			//Mientras haya filas en nuestro resulset:
			while(resultado.next()){
				
				//Al hacer la conversión de datos, el null de sql pasa a ser 0 en Java
				//Insertamos en caso de que la mascota no exista en la tabla mascotas
				if(resultado.getInt("CodigoPropietario")!=0){
					//Mejora 2.0 usar variables en vez de cadenas 
					insertActualizable.moveToInsertRow();
					insertActualizable.updateString("Codigo",resultado.getString("CodigoMascota"));
					insertActualizable.updateString("Raza",resultado.getString("Raza"));
					insertActualizable.updateString("Especie",resultado.getString("Especie"));
					insertActualizable.updateString("FechaNacimiento",resultado.getString("FechaNacimiento"));
					insertActualizable.updateString("FechaFallecimiento",resultado.getString("FechaFallecimiento"));
					insertActualizable.updateString("Alias",resultado.getString("Alias"));
					insertActualizable.updateInt("CodigoPropietario",resultado.getInt("CodigoPropietario"));
					insertActualizable.insertRow();
				}
				
				
				//en caso de que tenga uan enfermedad insertamos en Mascotas_Enfermedad
				if(resultado.getString("Enfermedad")!=null){
					preparedStatement.setString(1,resultado.getString("Enfermedad"));
					preparedStatement.setString(2,resultado.getString("Fecha"));
					preparedStatement.setString(3,resultado.getString("CodigoMascota"));
					preparedStatement.executeUpdate();
				}
				
				//Por último insertamos la visita
				insert.setString(1,resultado.getString("Fecha"));
				insert.setShort(2,resultado.getShort("Temperatura"));
				insert.setInt(3,resultado.getInt("Peso"));
				insert.setString(4,resultado.getString("CodigoMascota"));
			}
			
			
		} catch (SQLException e) {
			System.out.println(e);
		} finally{
			miConexion.closeConnection();
			System.out.println("Todo actualizado perfectamente. Bienvenido a la era de Internet!");
		}
		
	}
}
