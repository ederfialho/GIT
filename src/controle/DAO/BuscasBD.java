package controle.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import controle.ConnectionFactory;

/* Esta classe contém todas as consultas ao banco de dados que o sistema precisa executar.
 * É interessante manter tudo em uma só classe, pois se algum dia houver mudança no banco de dados,
 * só preciaremos alterar este arquivo.
 * Os títulos de cada método estão determinando qual consulta fará. 
 * Todos o métodos de busca sempre retornam alguma coisa, nem que seja uma variável boolean,
 * para que a camada MODELO saiba qual decisão tomar.
 * OBS: Esta classe, não sei porque, não funciona com objetos comuns a classe toda, como no método InserirBD.
 * Por isso, em todos os métodos, os objetos são criados novamente.*/
public class BuscasBD {
	
	public String getNomeDoHardware (int ID){
		String retorno = "";
		String sql = "select a.tipo_hard_nome from Tipo_Hardware a, Entrada_Hardware b, Saida_Hardware c where "+
					 "b.IDentradahardware = c.IDentradahardware and "+
					 "b.IDtipohardware = a.IDtipohardware and "+
					 "c.IDsaidahardware = ?";
		
		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement pstm = con.prepareStatement(sql);
			pstm.setInt(1, ID);
			
			ResultSet rs = pstm.executeQuery();
			
			while (rs.next())
				retorno = rs.getString("tipo_hard_nome");
					
			con.close();
			} catch (SQLException e) {e.printStackTrace(); retorno = "";}
			return retorno;
		
	}
	
	
	
}

