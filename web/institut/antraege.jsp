<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" errorPage="err.html"%><!DOCTYPE html> 
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<%	
	// Besteht eine gültige Sitzung?
	if(user.getDozentNachname()==null) throw new Exception("Sorry, Sie müssen sich neu anmelden");
%>
<html lang="en"> 
  <head> 
    <meta charset="utf-8"> 
    <meta contentType="application/x-www-form-urlencoded"></meta>
    <title>U:P -- Admin/Anträge</title> 
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
        <div class="span10"> 
          <div class="row-fluid"> 
            <div class="span5">
                <h2>Anträge bearbeiten</h2>
                <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++ -->
                <!-- Box zum Auswählen des Antragstyps -->
                <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++ -->
                <div class="control-group">
                <label class="control-label">Antragstyp</label>
                    <div class="controls">
                        <select id="cboAntragtyp" name="cboAntragtyp" class="input-xlarge">
                            <option value="" selected="selected">bitte wählen</option>
                            <option value="1">Druck: Transkript Deutsch</option>
                            <option value="2">Druck: Transkript Englisch</option>
                            <option value="3">Druck: unmodulares Transkript</option>
                            <option value="4">Druck: Bescheinigung fürs GPA über fehlende Leistungen GymPO</option>
                        </select>
                    </div>
                </div>
                <div id="divAntraege">
                    <table class="table table-striped table-hover" id="tblAntraege">
                        <thead>
                        <th></th>
                        <th></th>
                        </thead>
                        <tbody>
                        </tbody>
                    </table> 
                </div>
      		   <!-- ================================================================ -->
      		<!-- 				END								  -->
      		<!-- ================================================================ -->     
                <%--
                    ############################################
                    Template: Tabelle der Anträge
                    ############################################
                --%>                
                <div style="display:none">
                    <table>
                        <tr id="template_antrag">
                            <td class="antrag_antragsteller"></td>
                             <td>
                                 <div class="btn-group">
                                     <button class="btn printbutton"><i class="icon-print"> </i></button>
                                     <button class="btn btn-success"><i class="icon-ok"> </i></button>
                                     <%-- <button class="btn btn-warning">Problem</button> --%>
                                   </div>
                             </td>
                         </tr>
                    </table>
                </div>
                <form id='studenten_akte' method='post' target="_blank" style="display:none" action='l-index.jsp'>
                    <input type='text' name='refer_matrikelnummer' id='refer_matrikelnummer' value=''></input>
                    <input type='text' name='register' id='refer_register' value='transkript'></input>
                </form>
            </div>
            </div>
             
          </div><!--/row--> 
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
    <script src="js/jszip-utils.js"></script>
    <script src="js/docxgen.min.js"></script>
    <script src="js/FileSaver.js"></script>
    <script src="js/signup-faculty-common.js"></script>
    <script src="js/signup-faculty-student-dom-common.js"></script>
    <script src="js/signup-faculty-student.js"></script>
    <script>
            $(document).ready(function() {
                // @TODO: es gibt eine Kopie von 
                // diesem Befehl innerhalb des Namespaces. 
                // eine von beiden muss wech.
                $.ajaxSetup({
                    type: "POST",
                    data:{"signup_expected_backend_version":'1-0-0-2'},
                    timeout: 20000,
                    contentType: "application/x-www-form-urlencoded;charset=utf8",
                    dataType: 'json',
                    cache: false,
                    error: function(xhr,status,dings){
                        // Achtung
                        // 1. sollte die fehlerauslösende Seite (!)
                        //    den contentType "text/json" haben
                        //    (oder jedenfalls nicht HTML).
                        // 2. sollte die fehlerauslösende Seite (!)
                        //    keine Leerzeilen ausliefern (also 
                        //    irrelevante Zeilenumbrüche für die 
                        //    Lesbarkeit o.ä.
                            //if(true==true) alert("Fehler: " + xhr.responseText);
                            var msg=eval("(" + xhr.responseText.substring(xhr.responseText.indexOf('{')) + ")");
                            if(msg.errorcode==1){
                                    // alert("Hey, ein Fehler mit dem Code 1 ... seltsam");
                            }else if (msg.errorcode==11){
                                // Falls das Login Studierender abgelaufen ist,
                                // wird auf die Login-Seite umgelenkt
                                document.location="./index-studierende.jsp";
                            }else{
                                    alert("Sorry, ein unerwarteter Fehler ist aufgetreten. Der Server meldet: \n\n" + msg.error);
                                    //if(true==true) alert("Debug Info: " + msg.errorDebug);
                            }
                    }    
                });

                
                // Das stellt die Ajax Wartemitteilung an.
                $('body').on({
                    ajaxStart: function() {
                        $(this).addClass('loading');
                    },
                    ajaxStop: function() {
                        $(this).removeClass('loading');
                    }
                });

              /**
               * Öffnet die Anmeldung im Browser
               * per Verweis auf .index.
               * Würde man die Seiten zusammenführen, 
               * ließe sich das sauberer lösen.
               * @param {type} anmeldung
               * @returns {nothing}               */
              function showBilanz(antrag){
                $('#refer_matrikelnummer').val(antrag.matrikelnummer);
                $('#studenten_akte').submit();
              }
                
                // User init
                var me = {};
                var antraege=null;
                
                $('#cboAntragtyp').on('click', function(){
                    showAntraegeTabelle($('#cboAntragtyp').val());
                });
                
                
                function showAntraegeTabelle(iTyp_IN){
                    // Lade ggf. _alle_ offenen Anträge herunter,
                    // (und filtere später beim Rendern).
                    if(antraege==null){
                        // Lade Leistungen vom Server und
                        // forme sie zu HTML
                        //var tmp=iTyp_IN;
                        antraege = new shj.signUp.student.Antraege('Gesamt',
                                function(item) {
                                    // Stelle die Leistungen in der Tabelle dar:
                                    _renderAntraegeTabelle(item,iTyp_IN);
                                });
                    }else{
                        _renderAntraegeTabelle(antraege,iTyp_IN);
                    }
                    // Formular anhalten:
                    return false;
                };  
                
               /**
                * 
                * @param {type} item enthält noch alle Anträge
                * @param {type} iTyp_IN
                * @returns {undefined}                 */
                function _renderAntraegeTabelle(item, iTyp_IN){
                    // Filtern des Arrays:
                    var oAntraegeTyp=new Array();
                    for(var ii=0; ii<item.antraege.length; ii++){
                        if(item.antraege[ii].typ_id==iTyp_IN) oAntraegeTyp.push(item.antraege[ii]);
                    }

                    oAntraegeTyp.shj_rendering = {'item': item,
                        'emptyFirst': true,
                        'HTML_template': '#template_antrag',
                        'HTML_table_container': '#divAntraege',
                        'HTML_template_class_prefix': 'antrag_'};
                    shj.signUp.toolbox.render(oAntraegeTyp);
                    
                    // Erstelle einen Link zum Druckerzeugnis
                    // (der hängt vom Antragstyp iTyp_IN ab)
                    var sDocPath='';
                    if(parseInt(iTyp_IN)===$.signUpGlobal.iTYP_ANTRAG_TRANSKRIPT_DE) sDocPath='transcript.rtf';
                    else if(parseInt(iTyp_IN)===$.signUpGlobal.iTYP_ANTRAG_TRANSKRIPT_EN) sDocPath='transcript_e.rtf';
                    else if(parseInt(iTyp_IN)===$.signUpGlobal.iTYP_ANTRAG_TRANSKRIPT_UNMODULAR) sDocPath='transcript_unmodular.rtf';
                    else if(parseInt(iTyp_IN)===$.signUpGlobal.iTYP_ANTRAG_GYMPO_MISSING_CREDITS) sDocPath='transcript_gpa_missing_credits.rtf';
                    
                    $('#divAntraege .printbutton').on('click', function(){
                        alert("Drucke...");
                        console.warn($(this).parents('tr').data('shj_item'));
                        var oLil = $(this).parents('tr').data('shj_item');
                        
                        // Mit Absicht global:
                        // js-libs müssen überarbeitet 
                        // werden!
                        student = [];
                        student.matrikelnummer = oLil.matrikelnummer;
                        student.fach_id = oLil.fach_id;
                        student.fachsemester = oLil.fachsemester;
                        student.vorname = oLil.vorname;
                        student.nachname= oLil.nachname;
                        student.geburtstag = oLil.geburtstag;
                        student.geburtsort = oLil.geburtsort;
                        student.anrede = oLil.anrede;
                        student.fach=$.signUpGlobal.info.seminar.faecher.get(student.fach_id);
                        student.fachname=student.fach.name;
                        // Problem: printDocx möchte die Leistungen 
                        // herunterladen, übergibt aber keine 
                        // Matrikelnummer. Das hier (below) 
                        // funktioniert dabei leider nicht!
                        
                        $.signUpGlobal.info.TEMPLATE = 'template_exp.docx'
                        $.signUpGlobal.printDocx(true);
                        // window.location = 'print/' + sDocPath + '?matrikelnummer=' + $(this).parents('tr').data('shj_item').matrikelnummer;
                    });
                    
                    // Link vom Antragsteller zur Bilanz-Ansicht in der Studierendenakte
                    $('#divAntraege .antrag_antragsteller').on('click', function(){showBilanz($(this).parents('tr').data('shj_item'))});
                    
                    $('#divAntraege .btn-success').on('click', function(){
                        var tmp=$(this).parents('tr').data('shj_item');
                        var na=$(this).parents('tr').children();
                        tmp.antrag_status_abschluss=true;
                        tmp.update(function(){
                            // reset to reload:
                            antraege=null;
                            
                            // markiere als bearbeitet
                            na.css("text-decoration","line-through");
                            na.find('.btn').addClass("disabled");
                        });
                    });
                }
            });
    </script>
  </body> 
</html> 