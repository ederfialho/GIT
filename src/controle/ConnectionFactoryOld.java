package controle;

import java.sql.*;

public class ConnectionFactoryOld {  
     public static Connection getConnection() throws SQLException{  
         try{  
        	 	//Indica qual driver de conex�o ser� usado para conectar ao SQL Server
        	 	Class.forName("net.sourceforge.jtds.jdbc.Driver");       
        	 	//Parametros necess�rios para conectar ao banco de dados.
        	 	return DriverManager.getConnection("jdbc:jtds:sqlserver://chsupsw12:1433/suricato","suricato","suricato");     
	         }catch (Exception e) {throw new SQLException(e.getMessage());}     
	     }  
	 } 
