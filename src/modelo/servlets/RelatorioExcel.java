package modelo.servlets;

import java.sql.*;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import controle.*;
import modelo.DataEHora;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RelatorioExcel extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public RelatorioExcel() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Connection con = null;
		try {
			con = ConnectionFactory.getConnection();
		} catch (SQLException e1) {e1.printStackTrace();}
		
		String matricula = ""; matricula = (String)request.getParameter("matricul");
		String tipocolaborador = "";
		
		tipocolaborador = (String)request.getParameter("tipocola");
		if (tipocolaborador.equals(""))
			tipocolaborador = "1";
		
		String datainic = (String)request.getParameter("datainic");
		String horainic = (String)request.getParameter("horainic");
		String datafina = (String)request.getParameter("datafina");
		String horafina = (String)request.getParameter("horafina");
		
		String sql = "select f.tipocola Tipo_Pessoa, "+
		"f.codimatr Matricula, "+
		"p.nomepess Nome, "+
		"a.dataaces Data, "+
		"ltrim(rtrim(replace(str(a.horaaces/60,2),' ','0')))+':'+ltrim(rtrim(replace(str((a.horaaces - (a.horaaces/60 * 60)),2),' ','0'))) Hora, "+
		"a.DireAces Direcao_Acesso, "+
		"a.tipoaces Tipo_Acesso, "+
		"t.desctipoaces Desc_Tipo_Acesso, "+
		"a.codiplan Planta, "+
		"a.codicole Codigo_Coletor, "+
		"r.desccole Desc_Coletor "+
		"from tbmarcaAcess a inner join  "+
		"tbhistocrach c on a.icard = c.icard inner join  "+
		"tbcolab f on c.idcolab = f.idcolab inner join  "+
		"tbpessoa p on f.idpessoa = p.idpessoa  inner join  "+
		"tbcodin r on r.codiplan = a.codiplan and r.codicole = a.codicole inner join  "+
		"tbtipoacess t on t.tipoaces = a.tipoaces  "+
		"where (dateadd(minute,c.horainic,c.datainic) <= dateadd(minute,a.horaaces,a.dataaces)) and  "+
		"(c.datafina is null or c.datafina = convert(datetime,'1900-12-31') or  "+
		"dateadd(minute,c.horafina,c.datafina) >= dateadd(minute,a.horaaces,a.dataaces))  "+
		"AND dateadd(minute,a.horaaces,a.dataaces) >= dateadd(minute,"+DataEHora.converteHorasEmMinutos(horainic)+",'"+DataEHora.converteHoraBanco(datainic)+"') "+
		"AND dateadd(minute,a.horaaces,a.dataaces) <= dateadd(minute,"+DataEHora.converteHorasEmMinutos(horafina)+",'"+DataEHora.converteHoraBanco(datafina)+"') "+
		"AND f.tipocola in ("+tipocolaborador+") ";
		
		if (!(matricula.equals("")))
		sql += "AND f.codimatr in ("+matricula+") ";
		
		String[] equipamentos = request.getParameterValues("ListaEquipamentos");
		String retorno = "";
		
		if(equipamentos!= null){
			for (String s : equipamentos) {
				retorno  += s+",";
			}
		retorno = retorno.substring(0,(retorno.length()-1));
		}
		
		if(!(retorno.equals(""))){
			sql += "AND r.codicole in ("+retorno+") ";
			
		}

		sql += "order by Data, Hora";
		
		
		String localrelatorios = getServletContext().getRealPath("/relatorios/")+"/";
		String nomearquivo = request.getSession().getId()+".csv";
		byte[] arquivo = null;
		
		//A estrutura try-catch é usada pois o objeto BufferedWriter exige que as
		//excessões sejam tratadas
		try{
			//Criação de um buffer para a escrita em uma stream
			BufferedWriter StrW = new BufferedWriter(new FileWriter(localrelatorios+"saida/"+nomearquivo));
			//Escrita dos dados da tabela
			StrW.write("Matricula;Nome;Data;Hora;Tipo de Acesso;Equipamento;Direção\n");
			
			PreparedStatement pstm = con.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			String linha = "";
			while (rs.next())
			{ 
				linha = (rs.getString("Matricula")+";"+
						   rs.getString("Nome")+";"+
						   DataEHora.converteHoraNormal(rs.getString("Data"))+";"+
						   rs.getString("Hora")+";"+
						   rs.getString("Tipo_Acesso")+"-"+rs.getString("Desc_Tipo_Acesso")+";"+
						   rs.getString("Desc_Coletor")+";");
				
				if ( (rs.getString("Direcao_Acesso")).equals("S") )
					linha+="SAÍDA\n";
				else
					linha+="ENTRADA\n";
				
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
	        
	        if (arquivo.length < 909)
	        {

	        		response.sendRedirect("acesso.jsp?semresultado=1");
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
