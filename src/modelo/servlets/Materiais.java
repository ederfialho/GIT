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

@WebServlet("/Materiais")
public class Materiais extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public Materiais() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Connection con = null;
		try {
			con = ConnectionFactoryOld.getConnection();
		} catch (SQLException e1) {e1.printStackTrace();}
		
		String localrelatorios = getServletContext().getRealPath("/relatorios/")+"/";
		String raiz = getServletContext().getRealPath("/")+"/";
		String localimagens = raiz+"imagens/";
		String separador="";
		Map<String, Object> parametros = new HashMap<String, Object>();
		int opcao = Integer.parseInt((String)request.getParameter("acaoserv"));
		int tipoarquivo = Integer.parseInt( (String)request.getParameter("expoarqu")  );
		String nomearquivo = request.getSession().getId()+".pdf";
		Collection<String> numeropatrimonio = new ArrayList<String>();
		byte[] arquivo = null;
		
		switch(opcao){
		
		case 1:
			try{	
			String datinic = (String)request.getParameter("datainic");
			String datfina = (String)request.getParameter("datafina");
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
			Date datainicial =  new java.sql.Date(((java.util.Date)formatter.parse(datinic)).getTime());
			Date datafinal =  new java.sql.Date(((java.util.Date)formatter.parse(datfina)).getTime());
			
			String numepatr = "";
			numepatr += (String)request.getParameter("numepatr");
			//Preenchendo os parametros do relatório.
			
			
			if (!(numepatr.equals("")))
			{
				numepatr = "AND m.NumePatr IN("+numepatr+")";
				
			}
			int tiposaida = Integer.parseInt((String)request.getParameter("tipopesq"));
			
			String saida = "";
			
			switch (tiposaida){
			
			case 2:
				
				saida = "AND m.datent is NOT NULL";
				
				
				break;
				
			case 3:
			
				saida = "AND m.datent is NULL";
				
				
				break;
			
			default:
				
				break;
			}
				
			
			//Preenchendo os parametros recebidos
			parametros.put("Data_Saida_Inicio", datainicial);
			parametros.put("Data_Saida_Fim", datafinal);
			parametros.put("Numero_Patrimonio",numepatr);
			parametros.put("Em_Aberto", saida);
			parametros.put("Imagem", localimagens);
			
			JasperPrint impressao = JasperFillManager.fillReport(localrelatorios+"jasper/Relatorio_IPSEMG_Materiais.jasper", parametros, con);
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
	        		response.sendRedirect("acesso.jsp?semresultado=1");
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
