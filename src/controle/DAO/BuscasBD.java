package controle.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import controle.ConnectionFactory;

/* Esta classe cont�m todas as consultas ao banco de dados que o sistema precisa executar.
 * � interessante manter tudo em uma s� classe, pois se algum dia houver mudan�a no banco de dados,
 * s� preciaremos alterar este arquivo.
 * Os t�tulos de cada m�todo est�o determinando qual consulta far�. 
 * Todos o m�todos de busca sempre retornam alguma coisa, nem que seja uma vari�vel boolean,
 * para que a camada MODELO saiba qual decis�o tomar.
 * OBS: Esta classe, n�o sei porque, n�o funciona com objetos comuns a classe toda, como no m�todo InserirBD.
 * Por isso, em todos os m�todos, os objetos s�o criados novamente.*/
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

