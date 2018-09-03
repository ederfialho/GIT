package modelo.servlets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import modelo.DataEHora;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import controle.ConnectionFactory;
import controle.ConnectionFactoryOld;

/**
 * Servlet implementation class RelVisAco
 */
@WebServlet("/RelVisAco")
public class RelVisAco extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RelVisAco() {
        super();
        // TODO Auto-generated constructor stub
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
		int tipoarquivo = Integer.parseInt( (String)request.getParameter("expoarqu")  );
		String nomearquivo = request.getSession().getId()+".pdf";
		Collection<String> tipcol = new ArrayList<String>();
		Collection<String> matricula = new ArrayList<String>();
		byte[] arquivo = null;
		int tiporelatorio = Integer.parseInt((String)request.getParameter("tiporela"));
			
		if (tipoarquivo == 2)
		{
			getServletContext().getRequestDispatcher("/relatorios/jsp/RelVisAcoExcel").forward(request,response); 
		}else{
		switch(opcao){
		
		case 1:
		try{
			/*Pegando os parametros vindos da página JSP e enviando ao relatório*/
			String selefaix = (String)request.getParameter("selefaix");
			String datinic = (String)request.getParameter("datainic");
			String datfina = (String)request.getParameter("datafina");
			String aux = "";
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
			Date datainicial =  new java.sql.Date(((java.util.Date)formatter.parse(datinic)).getTime());
			Date datafinal =  new java.sql.Date(((java.util.Date)formatter.parse(datfina)).getTime());
			int crachainicial = 0, crachafinal = 0;
			
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
			
			//Busca os equipamentos a serem filtrados.
			String[] equipamentos = request.getParameterValues("ListaEquipamentos");
			String retorno = "";
			if(equipamentos!= null){
				for (String s : equipamentos) {
					retorno  += s+",";
				}
			retorno = retorno.substring(0,(retorno.length()-1));
			}
			String equips = "";
			
			if(!(retorno.equals(""))){
				equips = "AND co.codicole in ("+retorno+")";
				
			}
							
			parametros.put("DatIni", datainicial);
			parametros.put("DatFim", datafinal);
			parametros.put("CraInicial", crachainicial);
			parametros.put("CraFinal", crachafinal);
			parametros.put("Imagem", localimagens);
			parametros.put("Equipamentos", equips );
			
			//Criando o relatório e o salvando em PDF
			JasperPrint impressao = null;
			
				switch(tiporelatorio){
				case 1:
				impressao = JasperFillManager.fillReport(localrelatorios+"jasper/Relatorio_IPSEMG_VIS_ACO_ANA.jasper", parametros, con);
					break;
				case 2:
					impressao = JasperFillManager.fillReport(localrelatorios+"jasper/Relatorio_IPSEMG_VIS_ACO_SIN.jasper", parametros, con);
					break;
				}
				
				JasperExportManager.exportReportToPdfFile(impressao,localrelatorios+"saida/"+nomearquivo);


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
	        		response.sendRedirect("acessovisaco.jsp?semresultado=1");
	        		break;
	        	}
	        	
	        }
	        else{
	        	
	        	String nomedoarquivo = "filename=Relatorio Acesso "+DataEHora.buscaData()+"_"+DataEHora.buscaHora()+".pdf";
	        	
	        	switch (tipoarquivo){
	        	
	        	case 1:
	        		response.setContentType("application/pdf");
	        		break;
	        	case 3:
	        		response.setContentType("attachment/pdf");
	        		break;
	        	}
	        	
	        	response.setHeader("Content-Disposition", nomedoarquivo);
	        	response.setContentLength(arquivo.length);  
	        	ServletOutputStream ouputStream = response.getOutputStream();  
	        	ouputStream.write(arquivo, 0, arquivo.length);  
	        	ouputStream.flush();  
	        	ouputStream.close(); 
	        }
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
