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
          <h2>Hilfe zu: SignUp Studienbilanz</h2> 
        </div>
      </div>
      <div class="row"> 
        <div class="span12"> 
          <h2>Studienbilanz</h2> 
           <p>In der Studienbilanz werden alle Ihre in 
               SignUp gespeicherten <a href="./leistungen.jsp">Leistungen</a> nach 
               Modulen sortiert aufgelistet. Dabei wird der Modulname farbig 
               hinterlegt dargestellt, und darunter werden alle Leistungen aufgelistet,
               die zum Modul gehören. (Achtung: das können durchaus mehr sein als 
               Sie belegen müssen).</p>
           <p><strong>Bitte kontrollieren Sie regelmäßig Ihre Studienbilanz und 
               reklamieren Sie Fehler umgehend beim zuständigen Studienberater!</strong></p>
               <p>Achten Sie besonders darauf, ob
               <ul><li>alle absolvierten Leistungen aufgelistet sind,
                   <li>alle Leistungen den richtigen Modulen zugeordnet sind,
                   <li>die Anzahl der Leistungspunkte stimmt.</ul>
        </div>
      </div>
       <br><hr><br>
       <div class="row"> 
        <div class="span12"> 
          <h2>unvollständiges Modul</h2> 
        </div>
      </div>
       <div class="row">
           <div class="span4">
               <img src="bilanz_modul_unvollst.png" style="background-color:#ccc;padding:1px;border:thin solid gray;" alt="unvollständiges Modul" width="100%">
        </div> 
        <div class="span8"> 
            <p>Ein Modul, in dem noch Leistungen zu fehlen scheinen, wird mit einem 
                gelben Balken markiert.
            </p>
            <p>Das bedeutet, dass die Summe der Leistungspunkte noch nicht der 
                geforderten Summe in diesem Modul entspricht. D.h. der Balken wird 
                auch dann gelb, wenn eine Leistung fehlerhaft mit zu wenigen LP 
                gespeichert wurde.
            </p>
            <p>Ausgegraut werden alle Leistungen aufgelistet, die zum Modul 
                gehören. Die müssen Sie nicht unbedingt alle belegen. Um genau 
                zu entscheiden, welche davon belegt werden müssen, konsultieren 
                Sie bitte das Modulhandbuch.
       </div> 
      </div>
       <br><hr><br>
      <div class="row"> 
        <div class="span12"> 
          <h2>vollständiges Modul</h2> 
        </div>
      </div>
       <div class="row">
           <div class="span4">
          <img src="bilanz_modul_abgeschlossen.png" style="background-color:#ccc;padding:1px;border:thin solid gray;" alt="abgeschlossenes Modul" width="100%">
        </div> 
        <div class="span8"> 
            <p>Ein vollständig absolviertes Modul wird mit einem grünen 
                Balken markiert, in dem unter dem Modulnamen "OK" steht.
            </p>
            <p>Wenn Sie sich zur Prüfung anmelden, sollten die meisten 
                Ihrer Module einen grünen Balken aufweisen.
            </p>
       </div> 
      </div>
       <br><hr><br>
      <div class="row"> 
        <div class="span12"> 
          <h2>überbuchtes Modul</h2> 
        </div>
      </div>
       <div class="row">
           <div class="span4">
               <img src="bilanz_modul_zuvoll.png" style="background-color:#ccc;padding:1px;border:thin solid gray;" alt="überbuchtes Modul" width="100%">
        </div> 
        <div class="span8"> 
            <p>Ein Modul mit zu vielen Leistungspunkten ist fehlerhaft und 
                wird rot hinterlegt.
            </p>
            <p>Dem Modul sind entweder zu viele Leistungen zugeordnet, oder 
                mindestens eine der Leistungen ist mit einer zu hohen 
                LP-Zahl gespeichert. Kontaktieren Sie Ihren Studienberater!
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