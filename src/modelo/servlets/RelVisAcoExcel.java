package modelo.servlets;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controle.ConnectionFactory;
import controle.ConnectionFactoryOld;

import modelo.DataEHora;

/**
 * Servlet implementation class RelVisAcoExcel
 */
@WebServlet("/RelVisAcoExcel")
public class RelVisAcoExcel extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public RelVisAcoExcel() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Connection con = null;
		try {
			con = ConnectionFactory.getConnection();
		} catch (SQLException e1) {e1.printStackTrace();}
		/*Pegando os parametros vindos da página JSP e enviando ao relatório*/
		String selefaix = (String)request.getParameter("selefaix");
		String datainic = (String)request.getParameter("datainic");
		String datafina = (String)request.getParameter("datafina");	
		String aux = "";
		int crachainicial = 0, crachafinal = 0;
		String sql = "";
		
		for (int I = 0; I<selefaix.length(); I++)
		{
			if( selefaix.charAt(I) == ';' )
			{
				crachainicial = Integer.parseInt(aux);
				aux  = "";
			}
			else
				aux += selefaix.charAt(I);
		}
		crachafinal = Integer.parseInt(aux);
		
		sql = "select a.icard Cracha, "+
		"vi.NumeDocuVisi Documento, "+
		"pe.NomePess Nome, "+
		"ta.DescReduAces TipoAcesso, "+
		"a.DataAces Data, "+
		"ltrim(rtrim(replace(str((a.HoraAces/60),2),' ','0')))+':'+ltrim(rtrim(replace(str(a.HoraAces - ((a.HoraAces/60) * 60),2),' ','0'))) Hora, "+
		"co.DescCole Dispositivo "+
		"from TbMarcaAcess a inner join "+
		"TbCadasCrach cc on a.Icard = cc.Icard inner join "+
		"TbESVisit es on es.Icard = a.Icard inner join "+
		"TbVisit vi on vi.IdVisi = es.IdVisi inner join "+
		"TbPessoa pe on vi.IdPessoa = pe.IdPessoa inner join "+
		"TbCodin co on co.CodiPlan = a.CodiPlan and co.CodiCole = a.CodiCole inner join "+
		"TbTipoAcess ta on ta.TipoAces = a.TipoAces "+
		"where  cc.UsoFaixCrac = 4 and "+
		"a.DataAces >= '"+DataEHora.converteHoraBanco(datainic)+"' and "+
		"a.DataAces <= '"+DataEHora.converteHoraBanco(datafina)+"' and "+
		"(a.Icard between "+crachainicial +" and "+  crachafinal+") and "+
		"(DATEADD(minute,es.HoraEntr,es.DataEntr) <= DATEADD(minute,a.HoraAces,a.DataAces)) and "+
		"(DATEADD(minute,es.HoraSaid,es.DataSaid) >= DATEADD(minute,a.HoraAces,a.DataAces) or es.DataSaid = CONVERT(datetime,'1900-12-31') or es.DataSaid is null) ";
		
		//Busca os equipamentos a serem filtrados.
		String[] equipamentos = request.getParameterValues("ListaEquipamentos");
		String retorno = "";
		if(equipamentos!= null){
			for (String s : equipamentos) {
				retorno  += s+",";
			}
		retorno = retorno.substring(0,(retorno.length()-1));
		}
		
		if(!(retorno.equals(""))){
			sql += "AND co.codicole in ("+retorno+") ";
			
		}
		
		
		sql +="order by a.DataAces asc,a.HoraAces asc";
		
		String localrelatorios = getServletContext().getRealPath("/relatorios/")+"/";
		String nomearquivo = request.getSession().getId()+".csv";
		byte[] arquivo = null;
		
		//A estrutura try-catch é usada pois o objeto BufferedWriter exige que as
		//excessões sejam tratadas
		try{
			//Criação de um buffer para a escrita em uma stream
			BufferedWriter StrW = new BufferedWriter(new FileWriter(localrelatorios+"saida/"+nomearquivo));
			//Escrita dos dados da tabela
			StrW.write("Cracha;Documento;Nome;Tipo de Acesso;Data;Hora;Dispositivo\n");
			
			PreparedStatement pstm = con.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			String linha = "";
			while (rs.next())
			{ 
				linha = (rs.getString("Cracha")+";"+
						 rs.getString("Documento")+";"+
						 rs.getString("Nome")+";"+
						 rs.getString("TipoAcesso")+";"+
						   DataEHora.converteHoraNormal(rs.getString("Data"))+";"+
						   rs.getString("Hora")+";"+
						   rs.getString("Dispositivo")+"\n");
				
				StrW.write(linha);
				linha = "";
			}
				
		//Fechamos o buffer
		StrW.close(); 
		}catch (FileNotFoundException ex)
		{
		ex.printStackTrace(); 
		}catch (IOException e)
		{
		e.printStackTrace(); } catch (SQLException e) {
			e.printStackTrace();
		} 
		
		//Preparando para exibir o arquivo no navegador
		
		 File file = new File(localrelatorios+"saida/"+nomearquivo);

		 try {  
	            arquivo = fileToByte(file);  
	        } catch (Exception e) { e.printStackTrace();}  
	        
	        if (arquivo.length < 1)
	        {

	        		response.sendRedirect("acessovisaco.jsp?semresultado=1");
	        }
	        else{
	        	
	        	String nomedoarquivo = "attachment; filename=Relatorio Acesso "+DataEHora.buscaData()+"_"+DataEHora.buscaHora()+".csv";
	        	response.setContentType("text/csv"); 
	        	response.setHeader("Content-Disposition", nomedoarquivo);
	        	response.setContentLength(arquivo.length);  
	        	ServletOutputStream ouputStream = response.getOutputStream();  
	        	ouputStream.write(arquivo, 0, arquivo.length);  
	        	ouputStream.flush();  
	        	ouputStream.close(); 
	        }
		
	}
	
	public static byte[] fileToByte(File imagem) throws Exception {  
        FileInputStream fis = new FileInputStream(imagem);  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        byte[] buffer = new byte[8192];  
        int bytesRead = 0;  
        while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {  
            baos.write(buffer, 0, bytesRead);  
        }  
        return baos.toByteArray();  
    }  

}
