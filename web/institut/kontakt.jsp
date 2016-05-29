<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en"> 
  <head> 
    <meta charset="utf-8"> 
    <title>UP &mdash; Universitäre Prüfungsverwaltung</title> 
    <meta name="description" content=""> 
    <meta name="author" content="H. Jakubzik (shj-online)"> 
 
    <!-- Le HTML5 shim, for IE6-8 support of HTML elements --> 
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]--> 
 
    <!-- Le styles --> 
    <style type="text/css"> 
      body {
        padding-top: 60px;
        padding-bottom: 40px;
      }
    </style> 
    <jsp:include page="fragments/css.jsp" />
 	
    <!-- Le fav and touch icons --> 
    <link rel="shortcut icon" href="img/signup.ico"> 
    <link rel="apple-touch-icon" href="images/apple-touch-icon.png"> 
    <link rel="apple-touch-icon" sizes="72x72" href="images/apple-touch-icon-72x72.png"> 
    <link rel="apple-touch-icon" sizes="114x114" href="images/apple-touch-icon-114x114.png"> 
  </head> 
 
  <body id="todo"> 
 
    <div class="navbar navbar-fixed-top"> 
      <div class="navbar-inner"> 
        <div class="container"> 
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> 
            <span class="icon-bar"></span> 
            <span class="icon-bar"></span> 
            <span class="icon-bar"></span> 
          </a> 
          <a class="brand active" href="./index.jsp">U<span style="color: red">:</span>P</a> 
          <div class="nav-collapse"> 
            <ul class="nav"> 
              <li><a href="http://www.shj-online.de">U<span style="color: red">:</span>P-Home</a></li> 
              <li><a href="about-up.jsp">About</a></li> 
              <li><a href="kontakt.jsp">Kontakt</a></li> 
            </ul> 
          </div><!--/.nav-collapse --> 
        </div> 
      </div> 
    </div> 
 
    <div class="container"> 
 
      <!-- Main hero unit for a primary marketing message or call to action --> 
      <div> 
        <h1>U<span style="color: red">:</span>P</h1>
        <h2>Kontakt</h2> 

        <form class="well span8" id="fContact">
        <div class="row">
		<div class="span3">
			<label>Name</label>
			<input type="text" name="name" id="name" class="span3" placeholder="Vor- und Nachname">
			<label>Matrikelnummer</label>
			<input type="text" class="span3" id="matrikelnummer" name="matrikelnummer" placeholder="Matrikelnummer">
			<label>* E-Mail</label>
			<input type="text" name="email" id="txtEmail" class="span3 required" placeholder="E-Mail Adresse">
			<label>Betreff
			<select id="subject" name="subject" id="subject" class="span3">
				<option value="na" selected="">--bitte wählen--</option>
				<option value="problem">Problem melden</option>
				<option value="vorschlag">Verbesserungsvorschlag</option>
				<option value="anfrage">Anfrage</option>
			</select>
			</label>
		</div>
		<div class="span5">
			<label>* Nachrichtentext</label>
			<textarea name="message" id="message" class="input-xlarge span5 required" rows="10"></textarea>
		</div>
	
		<button type="submit" id="cmdSend" class="btn btn-primary pull-right">Senden</button>
	</div>
        </form>
      </div> 
      
      <br /><br />
      <br /><br />
      <br /><br />
        <div class="modal hide fade" id="modSendMsg" class="signUp_viewToggle">
           <div class="modal">
               <div class="modal-header">
                   <a class="close" data-dismiss="modal">×</a>
                   <h3 class="text-warning shj_SignUp_Confirm_Header"></h3>
               </div>
               <div class="modal-body shj_SignUp_Confirm_Body"></div>
               <div class="modal-footer">
                   <button class="btn btn-danger cmdShj_SignUp_Confirm_Act" id="cmdShj_SignUp_Confirm_Act"><u>W</u>irklich senden</button>
                   <button class="btn btn-warning cmdShj_SignUp_ConfirmAct_Cancel" id="cmdShj_SignUp_ConfirmAct_Cancel"><u>A</u>bbrechen</button>
               </div>
           </div>
       </div>
      <!-- Example row of columns --> 
      <div class="row"> 
        <div class="span4"> 
          <h2>U<span style="color: red">:</span>P</h2> 
           <p>Das ist die quelloffene Prüfungsverwaltungs-Software (früher "SignUp") an der Ruprecht-Karls-Universität Heidelberg.</p> 
          <p><a class="btn" href="about-up.jsp">Mehr Information &raquo;</a></p> 
        </div> 
        <div class="span4"> 
          <h2>Funktionsweise</h2> 
           <p>U<span style="color: red">:</span>P speichert Leistungsdaten (Scheine) von Studierenden, verwaltet Informationen zu Lehrenden und Lehrveranstaltungen und Prüfungsordnungen.</p> 
          <p><a class="btn" href="funktionsweise-up.jsp">Mehr Information &raquo;</a></p> 
       </div> 
        <div class="span4"> 
          <h2>Zugänge</h2> 
          <p>Es gibt drei Zugänge: für Studierende, für Lehrende und für (Prüfungs-)Sekretariate. Jeder Zugang soll dem Nutzerkreis eine möglichst vollständige und klare Sicht auf die für ihn interessanten Daten geben.</p> 
          <p><a class="btn" href="zugaenge-up.jsp">Mehr Information &raquo;</a></p> 
        </div> 
      </div> 
 
      <hr> 
 
      <footer> 
        <p>&copy; shj-online 2016</p> 
      </footer> 
 
    </div> <!-- /container --> 
 
    <!-- Le javascript
    ================================================== --> 
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
    <jsp:include page="fragments/libs.jsp"></jsp:include>
	<script src="js/signup-faculty-common.js"></script>
    <script>
	$(document).ready(function() {
		// Automatische Validierung
		// fürs Formular einschalten:
		//$("#fContact").validate();
		$('#fContact').on('submit', function(e){
                    e.preventDefault();
                    var sErr='';
                    if($('#message').val().length < 5) {alert('Bitte geben Sie einen Nachrichtentext ein.'); $('#message').focus(); return false;}
                    if(!shj.signUp.toolbox.isValidEmail($('#txtEmail').val())) {alert('Bitte geben Sie eine gültige E-Mail Adresse ein.'); $('#txtEmail').focus();return false;}
                    if(sErr==='') askViaModal();
                    else{
                        alert(sErr);
                    }
                    return false;
            });
            
            function askViaModal(){
                shj.signUp.toolbox.confirm(
                        $('#modSendMsg'), 
                      SignUp  'E-Mail wirklich absenden?', 
                        'Möchten Sie Ihre E-Mail so absenden?',
                        function(){sendEmail();}, function(){});
            };
            
            function sendEmail(){
                var sending={};
                sending.sender=$('#txtEmail').val();
                sending.msg=$('#message').val();
                sSignUpending.subject=$('#subject').val();
                sending.name=$('#name').val();
                sending.matrikelnummer=$('#matrikelnummer').val();
                    $.ajax({
                         data: sending,
                         url: 'json/sendMeMail.jsp',
                         type: "POST",
                         success: function(data){
                             alert('Ok, verschickt. Vielen Dank.');
                        },
                        timeout: 20000,
                        contentType: "application/x-www-form-urlencoded;charset=utf8",
                        dataType: 'json',
                        cache: false,
                        error: function(xhr,status,dings){
                            alert('Sorry, die Nachricht konnte nicht verschickt werden. Bitte versuchen Sie es später noch einmal.');
                        }
                    });
            }
	});
</script>
  </body> 
</html> 