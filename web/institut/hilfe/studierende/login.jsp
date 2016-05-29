<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en"> 
    <head> 
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
        <meta charset="utf-8"> 
        <title>SignUp Hilfe -- Anglistisches Seminar</title> 
        <meta name="description" content=""> 
        <meta name="author" content="H. Jakubzik (shj-online)"> 

        <!-- Le HTML5 shim, for IE6-8 support of HTML elements --> 
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]--> 

        <!-- Le styles --> 
        <!--<link href="css/bootstrap.min.css" rel="stylesheet"> -->

        <jsp:include page="../../fragments/css.jsp"></jsp:include>

            <!-- Le fav and touch icons --> 
            <link rel="shortcut icon" href="../../img/signup.ico"> 
            <link rel="apple-touch-icon" href="images/apple-touch-icon.png"> 
            <link rel="apple-touch-icon" sizes="72x72" href="images/apple-touch-icon-72x72.png"> 
            <link rel="apple-touch-icon" sizes="114x114" href="images/apple-touch-icon-114x114.png"> 
        </head> 

        <body id="todo"> 

        <jsp:include page="./fragments/student-navbar-top.html" />

        <div class="container"> 
            <br /><br />
            <div class="row"> 
                <div class="span12 well"> 
                    <h2>Hilfe zu: SignUp Login</h2> 
                </div>
            </div>
            <div class="row">
                <div class="span4"> 
                    <h2>Login Checkliste</h2> 
                </div> 
                <div class="span8"> 
                    <p>Vier Dinge mu&szlig; man wissen: <br />
                    <ol type="i">
                        <li>SignUp achtet auf Gro&szlig;- und Kleinschreibung, </li>
                        <li>Als Passwort m&uuml;ssen Sie das Passwort Ihrer UniID [<a href="http://www.urz.uni-heidelberg.de/zugang/ben-verw/uni-id.html" target="_blank">?</a>] eingeben, </li> 
                        <li>SignUp arbeitet mit den Daten des Studentensekretariats.</li>
                        <li>Sonderzeichen und Umlaute (&Auml;, &auml;, &Ouml;, &ouml;, &Uuml;, &uuml;, &szlig;) im Passwort k&ouml;nnen das Login behindern. Bitte <a href="http://change.rzuser.uni-heidelberg.de/">&auml;ndern Sie das Passwort Ihrer UniID</a>, so dass es keine Sonderzeichen enth&auml;lt.</li> 
                    </ol>
                    Achten Sie also beim Login auf korrekte Gro&szlig;- und Kleinschreibung 
                    Ihres (Nach-)namens. Kontrollieren Sie die Schreibweise 
                    anhand Ihrer Immatrikulationsbescheinigung. (Ist Ihr Name dort falsch 
                    geschrieben, m&uuml;ssen Sie sich trotzdem in dieser Schreibweise einloggen).
                    </p>
                    <p>
                        Login-Daten k&ouml;nnen z.B. so aussehen:<br />
                        Name: Schmidt<br />
                        Matrikelnr.: 8765432<br />
                        Passwort: urzpwd3<br /><br />
                        Um zu &uuml;berpr&uuml;fen, ob Ihr Passwort korrekt ist, loggen 
                        Sie sich z.B. testweise unter <a href="http://mail.uni-hd.de">http://mail.uni-hd.de</a> ein. 
                        (Achtung: dort ist als Name im Gegensatz zu SignUp nicht der richtige Name, sondern 
                        die UniID einzugeben)</p>
                </div> 
            </div>
            <div class="row"> 
                <div class="span4"> 
                    <h2>Neu immatrikuliert?</h2> 
                </div> 
                <div class="span8"> 
                    <p>Wer sich frisch (für dieses Studienfach) immatrikuliert hat, kann 
                        sich aus verwaltungstechnischen Gründen erst ca. eine Woche 
                        nach der Immatrikulierung in SignUp einloggen! Warten Sie im 
                        Zweifel etwas ab und versuchen das Login in einigen Tagen erneut.</p>
                    <p>Stellen Sie auch sicher, dass Sie Ihre UniID <a target="_blank" href="http://www.urz.uni-heidelberg.de/zugang/ben-verw/uni-id.html">freigeschaltet haben</a>.</p>
                </div> 
            </div>
            <hr> 
            <div class="row">
                <div class="span4"> 
                    <h2>Passwort vergessen</h2> 
                </div> 
                <div class="span8"> 
                    <p>Wenn Sie das Passwort zu Ihrer UniID vergessen haben, wenden Sie 
                        sich ans <a href="http://www.urz.uni-heidelberg.de" target="_blank">Universitätsrechenzentrum</a>.
                    </p>
                </div> 
            </div> 
            <hr> 
            <div class="row">
                <div class="span4"> 
                    <h2>Anderes Problem</h2> 
                </div> 
                <div class="span8"> 
                    <p>Wenn Ihnen diese Hinweise nicht weiterhelfen, senden Sie <a href="mailto:info@shj-online.de">uns eine E-Mail</a>. Bitte 
                        nennen Sie in der E-Mail Ihre Matrikelnummer und den Studiengang, in dem 
                        Sie sich anzumelden versuchen. Wir melden uns schnellstmöglich bei Ihnen.
                    </p>
                </div> 
            </div>

            <%--
              -----------------------------------------------
              Modalfenster für Video und Bilder
              ----------------------------------------------- 
            --%>	            
            <div class="modal hide fade" tabindex='-1' id="modShj_SignUp_Lightbox">
                <div class="modal-header">
                    <a class="close" data-keyboard="true" data-dismiss="modal">×</a>
                    <h3 class="text-warning shj_SignUp_ConfirmDelete_Header"></h3>
                </div>
                <!-- <div class="modal-body shj_SignUp_ConfirmDelete_Body"> -->
                <video width="100%" poster="" controls autoplay autobuffer>
                    <source src="#" type="video/ogg" type='video/ogg; codecs="theora, vorbis"'/>

                </video>
                <!-- </div> -->
            </div> 
            <div class="modal hide fade" tabindex='-1' id="modShj_SignUp_LightboxPic">
                <div class="modal-header">
                    <a class="close" data-keyboard="true" data-dismiss="modal">×</a>
                    <h3 class="text-warning shj_SignUp_ConfirmDelete_Header"></h3>
                </div>
                <img src="#" width="100%" />
            </div>
            <hr> 

            <footer> 
                <p>&copy; shj-online 2013</p> 
            </footer> 

        </div> <!-- /container --> 

        <!-- Le javascript
        ================================================== --> 
        <!-- Placed at the end of the document so the pages load faster -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
        <script src="../../js/2.1.1/bootstrap.min.js"></script>

    <script>
	$(document).ready(function() {
                $('#modShj_SignUp_LightboxPic').modal({
                        backdrop: true,
                        keyboard: true,
                        show: false
                    }).css({
                        // make width 90% of screen
                       'width': function () { 
                           return ($(document).width() * .9) + 'px';  
                       },
                        // center model
                       'margin-left': function () { 
                           return -($(this).width() / 2); 
                       }
                });
		// Automatische Validierung
		// fürs Formular einschalten:
                $('img').on('click', function(){
                    $('#modShj_SignUp_LightboxPic img').attr('src', $(this).attr('src'));
                    $('#modShj_SignUp_LightboxPic').modal('show');
                });

	});
</script>
  </body> 
</html> 