<%@ page language="java" import= "java.sql.*, controle.ConnectionFactory;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Relatório de Acesso de Visitante/Acompanhante</title>
<link href="acesso.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="jquery-1.11.0.js"></script>
<script type="text/javascript" src="jquery.maskedinput.js"/></script> 
<script type="text/javascript" src="jquery.validate.js"/></script> 
<script type="text/javascript" src="additional-methods.js"/></script> 
<link rel="shortcut icon" href="favicon.ico" /> 
<style type="text/css">
* { font-family: Verdana; font-size: 96%; }
label { display: block; margin-top: 10px; }
label.error { float: none; color: red; margin: 0 .5em 0 0; vertical-align: top; font-size: 10px }
p { clear: both; }
.submit { margin-top: 1em; }
em { font-weight: bold; padding-right: 1em; vertical-align: top; }
</style>

<script language="JavaScript">

function Verifica_Hora(campo)
{
 var vCampo = campo.value;
 if (vCampo.length == 5 && vCampo.substring(2,3) == ":" )
 {
  var retorno = false;
  var Hora = parseInt(vCampo.substring(0,2));
  var Minuto = parseInt(vCampo.substring(3,5));
  retorno = ((Hora >=0 && Hora <=23) && (Minuto >=0 && Minuto <=59) ) ;
  if (!retorno)
  {
   alert("Hora inválida");
   document.getElementById(campo.id).focus();
  }
 }
 else
 {
  alert("Hora válida");
 }
}

jQuery(function($){
	// valida o formulário
    $('#relacesso').validate({
        // define regras para os campos
        rules: {
        	datainic: {
                required: true,
                date: true
            },
            horainic: {
                required: true
            },
            datafina: {
                required: true,
                date: true
            },
            horafina: {
                required: true
                
            }
        },
        // define messages para cada campo
        messages: {
            datainic: "Data Inválida",
            horainic: "Hora Inválida",
            datafina: "Data Inválida",
            horafina: "Hora Inválida"
        }
    });
	
	
	
	$("#datainic").mask("99/99/9999");
	$("#datafina").mask("99/99/9999");
    $("#horainic").mask("99:99");
	$("#horafina").mask("99:99");
    });



function exibirmensagem(mensagem){
                var msg = document.getElementById("msg")
                msg.innerHTML = mensagem
            }
            
</script>
</head>
<%

String sql = "select CodiCole, DescCole from TBCodin order by DescCole";

try {
Connection con = ConnectionFactory.getConnection();
PreparedStatement pstm = con.prepareStatement(sql);

ResultSet rs = pstm.executeQuery();

%>
<body>
<form id="relacesso" name="relacesso" method="post" action="RelVisAco">
<p>Tipo.:
  <select name="selefaix" id="selefaix" class="inputcampos">
    <option value="80000;80100">Visitantes</option>
    <option value="80101;80200">Acompanhantes</option>
  </select>
</p>
<p>Data Inicial.:
<input name="datainic" maxlength="10" type="text" class="inputcampos" id="datainic"/>
<p>Data Final.:
<input name="datafina" maxlength="10" type="text" class="inputcampos" id="datafina"/>
<p>Equipamentos.:</p>
<div style="overflow-x:auto; height:150px; width:365px">
<input type="hidden" value="${opcoes}" name="opcao"/>
<% while(rs.next()){%>
<INPUT TYPE=CHECKBOX VALUE="<%=rs.getInt("CodiCole")%>" name="ListaEquipamentos"><%=rs.getString("DescCole") %><BR>  
<%}
con.close();
} catch (SQLException e) {e.printStackTrace();}%>
</div>

<p>Saída.:
<select name="expoarqu" id="expoarqu" class="inputcampos">
                <option value="1">Visualizar em PDF</option>
                <option value="3">Salvar em PDF</option>
                <option value="2">Exportar para CSV</option>   
            </select>
</p>
<p><input type="radio" name="tiporela" id="tiporela" value="1" checked="checked" />
Relatório Analítico
<input type="radio" name="tiporela" id="tiporela" value="2" />
Relatório Sintético
</p>
<p><input type="submit" name="botasalv" id="botasalv" value="Gerar" class="inputbotaosalvar" /></p>
<input type="hidden" name="acaoserv" id="acaoserv" value="1"/>
</form>
 <%String erro = ""; erro += request.getParameter("semresultado"); 
 if (erro.equals("1")){%>
 <p><center><strong>O relatório solicitado não gerou resultado algum. Relatório não será exibido.</strong></center><br />
 <%}%>
</body>
</html>
