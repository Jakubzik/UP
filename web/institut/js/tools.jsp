<%@ page language="java" contentType="text/html; charset=UTF-8" errorPage="err.html"
    pageEncoding="UTF-8"%><!DOCTYPE html> 
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<%	
	// Besteht eine gültige Sitzung?
	if(user.getDozentNachname()==null) throw new Exception("Sorry, Sie müssen sich neu anmelden");
%>
<html lang="en"> 
  <head> 
    <meta charset="utf-8"> 
    <meta contentType="application/x-www-form-urlencoded"></meta>
    <title>SignUp -- Lehrende</title> 
    <meta name="description" content="Prüfungsverwaltung"> 
    <meta name="author" content="Heiko Jakubzik"> 
 
    <!-- Le HTML5 shim, for IE6-8 support of HTML elements --> 
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]--> 
 
    <!-- Le styles --> 
    <jsp:include page="fragments/css.jsp" />
    <style type="text/css"> 
        
      /*
      * This is a bootstrap issue
      * Wenn man's löscht, erscheint das 
      * Type-ahead hinter dem Modal, 
      * z.B. beim Einfügen einer neuen 
      * Leistung ohne Kurs
      * 
      * See: https://github.com/twitter/bootstrap/issues/5113
      */
      .modal-open .typeahead.dropdown-menu {
            z-index: 2050;
      }
      body {
        padding-top: 60px;
        padding-bottom: 40px;
      }
      .sidebar-nav {
        padding: 9px 0;
        }
            /* Start by setting display:none to make this hidden.
               Then we position it in relation to the viewport window
               with position:fixed. Width, height, top and left speak
               speak for themselves. Background we set to 80% white with
               our animation centered, and no-repeating */
            .signUp_waitmsg-modal {
                display:    none;
                position:   fixed;
                z-index:    1000;
                top:        0;
                left:       0;
                height:     100%;
                width:      100%;
                background: rgba( 255, 255, 255, .8 ) 
                            url('img/ajax-loader.gif') 
                            50% 50% 
                            no-repeat;
            }

            /* When the body has the loading class, we turn
               the scrollbar off with overflow:hidden */
            body.loading {
                overflow: hidden;   
            }

            /* Anytime the body has the loading class, our
               modal element will be visible */
            body.loading .signUp_waitmsg-modal {
                display: block;
            }
      </style>
 
     
    <!-- Le fav and touch icons --> 
    <link rel="shortcut icon" href="img/signup.ico"> 
    <link rel="apple-touch-icon" href="images/apple-touch-icon.png"> 
    <link rel="apple-touch-icon" sizes="72x72" href="images/apple-touch-icon-72x72.png"> 
    <link rel="apple-touch-icon" sizes="114x114" href="images/apple-touch-icon-114x114.png"> 
  </head> 
 
  <body> 
 
<span class="contextmenu" id="transkriptContextMenu" style="width:350px; font-size:0.7em; display: none;">
	<ul>
	</ul>
</span>
 	<input type="hidden" id="logged-in-user-name" value="<%=user.getDozentNachname() %>" />
 	<input type="hidden" id="logged-in-user-seminar" value="<%=user.getSdSeminarID() %>" />
    <div class="navbar navbar-fixed-top"> 
      <div class="navbar-inner"> 
        <div class="container-fluid"> 
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> 
            <span class="icon-bar"></span> 
            <span class="icon-bar"></span> 
            <span class="icon-bar"></span> 
          </a> 
          <a class="brand" href="#">SignUp</a> 
          <div class="nav-collapse"> 
            <ul class="nav"> 
              <li class="active"><a href="#"><i class="icon-home icon-white"></i> Home</a></li> 
              <li><a href="hilfe-studierende.jsp"><i class="icon-question-sign icon-white"></i> Hilfe</a></li> 
            </ul> <!--
            <form class="navbar-search pull-left" id="fFindStudents" action="#" name="fFindStudents">
  				<i class="icon-search icon-white"></i>&nbsp; <input type="text" id="txtSuche" class="search-query" placeholder="Studierende suchen..."></input>
  				<span class="help-inline">Studierende suchen</span>
			</form>-->
            <ul class="nav pull-right"> 
            <li class="divider-vertical"></li> 
            <li class="dropdown"> 
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Angemeldet als <%=user.getDozentTitel() + " " + user.getDozentVorname() + " " + user.getDozentNachname() %> <b class="caret"></b></a> 
              <ul class="dropdown-menu"> 
                <li><a href="#">Daten</a></li> 
                <li class="divider"></li> 
                <li id="logOutLink"><a href="logout.jsp"><i class="icon-off"></i> Abmelden</a></li> 
              </ul> 
            </li> 
          </ul> 
          </div><!--/.nav-collapse --> 
        </div> 
      </div> 
    </div> 
 
    <div class="container-fluid"> 
        <div class="row-fluid"> 
          <jsp:include page="fragments/navigationLeftMaster.jsp" />
            <div class="span8"> 
                <!-- seltsam in Bootstrap: brauche ein leeres first-child, sonst wird das erste Element eingerückt -->
                <div></div>
                
                <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++ -->
                <!-- Formular zum Ändern von Passwort und E-Mail Adresse -->
                <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++ -->
                <div class="well span8">
                    <a href="termine.jsp">SignUp Termine (derzeit nur Writing Resources Center)</a><br />
                    Ermöglicht das Freischalten von Terminen zur Online-Buchung.
                </div><br />
                <div class="well span8">
                    <a href="print/signup-kurswahl-statistik.rtf">SignUp Kurswahl Statistik</a><br />
                    Enthält die Zahlen der zugeteilten Studierenden pro Kurstyp (durchschnittl. Belegzahlen).
                </div><br />
                <div class="well span8">
                    <a href="print/signup-kurswahl-bericht-ps_style.rtf">SignUp Kurswahl Proseminare</a><br />
                    Enthält Informationen zur Kurswahl &quot;Proseminar-Style&quot;
                </div><br />
                <div class="well span8">
                    <a href="print/signup-kurswahl-kurszahlen.rtf">SignUp Kurswahl Kursbelegung</a><br />
                    Enthält die Zahlen der zugeteilten Studierenden pro Kurs (zur Suche nach freien Plätzen).
                </div>
                <div class="well span8">
                    <a href="print/lsf-export.csv">SignUp Kvv2LSF</a>, die Rahmendatei ist <a href="../hilfe/VVZImportVorlage_V1.xls">hier</a>.<br />
                    Experimentell.
                </div>
                <div class="well span8">
                    Modulhandbücher (dynamisch erzeugt, RTF)
                    <form method="post" action="print/modulhandbuch.rtf" id="fModulhandbuch">
                        <select id="fach_id" name="txtFach" onchange="$('#fModulhandbuch').submit()">
                            <option value="" selected="selected">Alle</option>
                            <option value="200216">BA 75%</option>
                            <option value="200317">BA 50% 1. Hauptfach</option>
                            <option value="200418">BA 50% 1. Hauptfach</option>
                            <option value="200519">BA 25% Kulturwissenschaft</option>
                            <option value="200620">BA 25% Literaturwissenschaft</option>
                            <option value="200721">BA 25% Sprachwissenschaft</option>
                            <option value="210915">MA HF: Schwerpunkt Linguistik (anderes Beifach)</option>                            
                            <option value="211016">MA HF: Schwerpunkt Literaturwiss. (anderes Beifach)</option>                      
                            <option value="211117">MA BF: Begleitfach Linguistik</option>    
                            <option value="211218">MA HF: Begleitfach Literaturwiss. </option>    
                        </select>
                    </form>
                </div>
                <div class="well span8">
                    <a href="print/kurstypentabelle.rtf">Leistungen und Beschreibungen</a><br />
                    Liste der Leistungen mit Lernzielen.
                </div>
                <div class="well span8">
                    Examinierte (MA und BA) nach diesem Datum:
                    <form method="post" action="print/examinierte.rtf" id="fExaminierte">
                        <input type="date" name="txtDatumNachISO" placeholder="2013-1-1" onblur="$('#fExaminierte').submit()" />
                    </form>
                </div>
                <div class="well span8">
                    <a target="_blank" href="print/ma-studierende.rtf">Liste der MA-Studierenden Anglistik</a>.
                </div>
                <div class="well span8">
                    <select name="cboSparte" id="cboSparte">
                        <option selected="selected" value="">komplett</option>
                        <option value="lw">Literaturwissenschaft</option>
                        <option value="sw">Sprachwissenschaft</option>
                        <option value="kw">Kulturwissenschaft</option>
                        <option value="sx">Sprachpraxis</option>
                        <option value="ma-fak">MA Alte und Neue Litwiss</option>
                        <option value="hca">HCA</option>
                        <option value="vv">Vv</option>
                    </select>
                    <a target="_blank" href="#" id="cmdLoad">Kvv (Planungssemester)</a>.
                </div>
                <div class="well span8">
                    <a target="_blank" href="print/op-missing.rtf">Anschreiben an Studierende</a>, die die OP-Frist versäumt haben oder 
                    an <a target="_blank" href="print/zp-missing.rtf">die, die die ZP-Frist versäumt</a> haben (<a target="_blank" href="print/zp-missing.rtf?omit_op=true">hier klicken um nur diejenigen ZP-Versäumer anzuschreiben, die 
                    nicht auch die OP-Frist versäumt haben</a>).<br />
                    Oder eine <a href="print/p-missing.rtf">Übersicht</a>.
                </div>


      		   <!-- ================================================================ -->
      		<!-- 				END								  -->
      		<!-- ================================================================ -->      			
	 
             

        </div><!--/span--> 
      </div><!--/row--> 
 
      <hr> 
 
      <footer> 
        <p>&copy; shj-online 2012</p> 
        <input type="hidden" id="d_id" value="<%=user.getDozentID() %> " />
      </footer> 
    <div class="signUp_waitmsg-modal"><!-- Place at bottom of page --></div>
    </div><!--/.fluid-container--> 
    
    <!-- Le javascript
    ================================================== --> 
    <!-- Placed at the end of the document so the pages load faster --> 
    <!-- Placed at the end of the document so the pages load faster -->
    <jsp:include page="fragments/libs.jsp"></jsp:include>
    <script src="js/signup-faculty-common.js"></script>
    <script>
            $(document).ready(function() {
                $('#fEditData').bind('submit', function(event){event.preventDefault();changeData();});
                // User init
                var me = {};
                
                                // Das stellt die Ajax Wartemitteilung an.
                $('body').on({
                    ajaxStart: function() {
                        $(this).addClass('loading');
                    },
                    ajaxStop: function() {
                        $(this).removeClass('loading');
                    }
                });
                
                $('#cmdUpdatePic').on("click", function(){
                    $('#modFotoUpload').modal('show');
                });
                
                        $('input[type=file]').change(function(e){
                var sPath=$('input[type=file]').val();
                if(sPath.length<4) return;

                if( (sPath).substr(sPath.length - 4).toLowerCase()!=".jpg"){
                        alert("Das Foto muss im JPG-Format hochgeladen werden.");
                        $('#Reset1').click();
                }else{
                        $('#cmdFotoUpload').removeClass('disabled');
                }
        });
        
                        // Teste auf das Vorhandensein eines Fotos ...
                // (Das hier gibt Probleme in Firefox)
                $.ajax({
                    url:'../../studentPhoto/dozent' + $('#d_id').val().trim() + '.jpg',
                    type:'HEAD',
                    success: function(){
                        // ... es GIBT EIN Foto, zeige es an, und setze
                        // Link zum Vergrößern bei Klick:
                        $('#dozent_photo').attr("src",function(){
                            return "../../studentPhoto/dozent" + $('#d_id').val().trim() + ".jpg";
                        });
                        $('#picplace').attr("href",function(){
                            return "../../studentPhoto/dozent" + $('#d_id').val().trim() + ".jpg";
                        });
                        $('#dozent_photo').show();
                    },
                    error:function(){
                        // ... es GIBT KEIN Foto, zeige stattdessen 
                        // ein gendered-generic default
                        $('#studentInfo').show();
                        //if(student.anrede=='Herr') $('#studentPhoto').attr("src","img/acspike_male_user_icon.png");
                    }
                });
                
                function changeData(){
                    var dozent=new shj.signUp.seminar.Dozent($('#logged-in-user-seminar').val());
                    dozent.id=$('#d_id').val();
                    dozent.sprechstunde=$('#sprechstunde').val();
                    
                    dozent.passwort=$('#txtPasswort').val();
                    if($('#txtPasswort').val()!=='*****'){
                        // Passwort geändert:
                        if($('#txtPasswort').val()!==$('#txtPasswortWdh').val()){
                            alert("Password und Wiederholung sind nicht identisch -- bitte geben\n\
                                Sie zu Ihrer eigenen Sicherheit gleiche Werte an.");
                            return;
                        }
                    //dozent.passwort_wdh=$('#txtPasswortWdh').val();
                    }else{
                        dozent.passwort='*****';
                    }
                    dozent.email=$('#email').val();
                    dozent.titel=$('#titel').val();
                    dozent.update(function(data){
                            shj.signUp.toolbox.render(data.dozent, {
                            "item": "dozent",
                            "emptyFirst": true,
                            "actOnDom": true,
                            "stripPrefixOffId": false, // Das Formular wird direkt im HTML ersetzt
                            "HTML_template": "#fEditData",
                            "HTML_template_class_prefix": ''}
                        );
                        $('#txtPasswort').val('*****');
                        $('#txtPasswortWdh').val('*****');
                    }, function(){alert('Versuch, den Dozenten zu updaten fehlgeschlagen');});
                }
            });
	</script>
        <script src="js/FileSaver.js"></script>
        <script src="js/docxgen.min.js"></script>
        <script src="js/jszip-utils.js"></script>
        <script charset="UTF-8" type="text/javascript" src="js/kvv.js"></script>
  </body> 
</html> 