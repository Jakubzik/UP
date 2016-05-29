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
          <h2>Hilfe zu: SignUp Leistungsanzeige</h2> 
        </div>
      </div>
      <div class="row"> 
        <div class="span4"> 
          <h2>Anzeige der Leistungen</h2> 
        </div> 
        <div class="span8"> 
           <p>Sie können sich Ihre Leistungen in SignUp auflisten 
               lassen. Wenn Sie den Mauszeiger länger über einer 
               Zeile ruhen lassen, werden weitere Details zum 
               Kurs, Modul etc. angezeigt (im Bsp. zur Phonetik).</p> 
           <img src="leistungen.png" alt="screenshot" style="padding:1px;border:thin solid gray;" width="100%" />
           <p style="padding-top: 2em;">Sie können Ihre Leistungen per Klick auf die 
               entsprechende Überschrift alphabetisch, nach 
               Note oder nach Datum <a href='#' id='show_sortiert'>sortiert darstellen</a>.</p> 
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
                   <source src="./sortiere_leistungen.ogv" type="video/ogg" type='video/ogg; codecs="theora, vorbis"'/>

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
            $('#modShj_SignUp_Lightbox').modal({
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
                $('#show_sortiert').on('click', function(){
                    $('#modShj_SignUp_Lightbox').modal('show');
                });
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