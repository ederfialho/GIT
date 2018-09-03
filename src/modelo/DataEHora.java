package modelo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataEHora {
	
	public static String buscaData(){
		
		String retorno="";
		
		//Pegar Data Atual
		Date data = new Date(System.currentTimeMillis());    
		SimpleDateFormat formatarDate = new SimpleDateFormat("dd/MM/yyyy");
		retorno += formatarDate.format(data);
		
		return retorno;
		
	}
	public static String buscaHora(){
		
		String retorno="";
		//Pegar Hora Atual
		Date data = new Date(System.currentTimeMillis());    
		SimpleDateFormat formatarHour = new SimpleDateFormat("HH:mm");
		retorno += formatarHour.format(data);
		
		return retorno;
	}
	public static String buscaDataBanco(){
		
		String retorno="";
		
		//Pegar Data Atual e deixar no formato para salvar em banco de dados
		Date data = new Date(System.currentTimeMillis());    
		SimpleDateFormat formatarDate = new SimpleDateFormat("yyyy-MM-dd");
		retorno += formatarDate.format(data);
		
		return retorno;
		
	}
	public static String converteHoraBanco(String data){
		
		return data.substring(6, 10)+"-"+data.substring(3, 5)+"-"+data.substring(0, 2);
		
	}
	public static String converteHoraNormal(String data){
		
		return data.substring(8,10)+"/"+data.substring(5,7)+"/"+data.substring(0,4);
		
	}
	public static int converteHorasEmMinutos(String hora){
		
		int horas = 0;
		int minutos = 0;
		String hor = hora.substring(0,2);
		String min = hora.substring(3,5);
		horas = (Integer.parseInt(hor))*60;
		minutos = Integer.parseInt(min);
		
		return horas+minutos;
	}

}
