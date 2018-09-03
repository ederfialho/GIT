<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Relatório de Materiais</title>
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
    $('#relmater').validate({
        // define regras para os campos
        rules: {
        	datainic: {
                required: true,
                date: true
            },
            datafina: {
                required: true,
                date: true
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
	
    });



function exibirmensagem(mensagem){
                var msg = document.getElementById("msg")
                msg.innerHTML = mensagem
            }
            
</script>
</head>

<body>
<form id="relmater" name="relmater" method="post" action="Materiais">
<p>Data Inicial.:
<input name="datainic" maxlength="10" type="text" class="inputcampos" id="datainic"/>
<p>Data Final.:
<input name="datafina" maxlength="10" type="text" class="inputcampos" id="datafina"/>
<p>Número de Patrimônio (+).:
<input name="numepatr" type="text" maxlength="40" class="inputcampos" id="numepatr" onmouseover="exibirmensagem('Para mais de um Número de Patrimônio, separe por vírgula os números. ')" onmouseout="exibirmensagem('')"/><div id="msg"></div> 
				
</p><p>Saída.:
<select name="tipopesq" id="tipopesq" class="inputcampos">
                <option value="1">Todos</option>
                <option value="3">Saídas em Aberto</option>
                <option value="2">Saídas Fechadas</option>
            </select>
</p>
<p><input type="submit" name="botasalv" id="botasalv" value="Gerar" class="inputbotaosalvar" /></p>
<input type="hidden" name="acaoserv" id="acaoserv" value="1"/>
<input type="hidden" name="expoarqu" id="expoarqu" value="1"/>
</form>
 <%String erro = ""; erro += request.getParameter("semresultado"); 
 if (erro.equals("1")){%>
 <p><center><strong>O relatório solicitado não gerou resultado algum. Relatório não será exibido.</strong></center><br />
 <%}%>
</body>
</html>
