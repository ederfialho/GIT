package modelo.servlets;

import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import controle.*;
import modelo.DataEHora;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.*;

public class RelatorioHTML extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public RelatorioHTML() {
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
		
		String localrelatorios = getServletContext().getRealPath("/relatorios/")+"/";
		String raiz = getServletContext().getRealPath("/")+"/";
		String localimagens = raiz+"imagens/";
		String separador="";
		Map<String, Object> parametros = new HashMap<String, Object>();
		int opcao = Integer.parseInt((String)request.getParameter("acaoserv"));
		String nomearquivo = request.getSession().getId()+".html";
		Collection<String> tipcol = new ArrayList<String>();
		Collection<String> matricula = new ArrayList<String>();
		byte[] arquivo = null;
		
		switch(opcao){
		
		case 1:
		try{
			/*Pegando os parametros vindos da página JSP e enviando ao relatório*/
			String datinic = (String)request.getParameter("datainic");
			String datfina = (String)request.getParameter("datafina");
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
			Date datainicial =  new java.sql.Date(((java.util.Date)formatter.parse(datinic)).getTime());
			Date datafinal =  new java.sql.Date(((java.util.Date)formatter.parse(datfina)).getTime());
			//Buscando parametros de Tipo de Colaborador e inserindo em uma Collection
			String tipocolaborador = "";
			tipocolaborador = (String)request.getParameter("tipocola");
			
			if (tipocolaborador.equals(""))
				tipocolaborador = "1";
			
			//Buscando parametros de Matrícula  e inserindo em uma Collection
			String matri = "";
			matri = (String)request.getParameter("matricul");
			//Preenchendo os parametros do relatório.
			tipcol.add(tipocolaborador);
			matricula.add(matri);
			
			//Testa a função de conversão de Horas
			
			
			parametros.put("TipCol", tipcol);
			parametros.put("Matricula", matricula);
			parametros.put("DatIni", datainicial);
			parametros.put("HorIni", DataEHora.converteHorasEmMinutos((String)request.getParameter("horainic")));
			parametros.put("DatFim", datafinal);
			parametros.put("HorFim", DataEHora.converteHorasEmMinutos((String)request.getParameter("horafina")));
			parametros.put("Imagem", localimagens);
			
			//Criando o relatório e o salvando em PDF
			if (matri.equals("")){
			
				JasperPrint impressao = JasperFillManager.fillReport(localrelatorios+"jasper/Relatorio_IPSEMG_Acesso_Sem_Matricula.jasper", parametros, con);
				JasperExportManager.exportReportToHtmlFile(impressao,localrelatorios+"saida/"+nomearquivo);
				
			}else{
				
				JasperPrint impressao = JasperFillManager.fillReport(localrelatorios+"jasper/Relatorio_IPSEMG_Acesso.jasper", parametros, con);
				JasperExportManager.exportReportToHtmlFile(impressao,localrelatorios+"saida/"+nomearquivo);
				
			}
		}catch (Exception e){e.printStackTrace();}
		finally {try {con.close();} catch (SQLException e) {e.printStackTrace();}}
		
		break;
		
		}
		
		//Preparando para exibir o arquivo no navegador
		
		 File file = new File(localrelatorios+"saida/"+nomearquivo);

		 try {  
	            arquivo = fileToByte(file);  
	        } catch (Exception e) { e.printStackTrace();}  
	        
	        if (arquivo.length < 909)
	        {
	        	switch(opcao){
	        	
	        	case 1:
	        		response.sendRedirect("acesso.jsp?semresultado=1");
	        		break;
	        	}
	        	
	        }
	        else{
	        
	        	response.setContentType("application/html");  
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
