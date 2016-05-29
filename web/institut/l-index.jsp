<%@ page language="java" contentType="text/html; charset=UTF-8"
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
    <title>UP -- Lehrende</title> 
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
      .leistungname:hover{
      	text-decoration:underline;
      	cursor:pointer;
      }
      .linked-def:hover{
		background-color:#ddd ! important;
		color:#000;
		cursor:pointer;          
      }
      .linked-tablerow:hover td{
		background-color:#ddd ! important;
		color:#000;
		cursor:pointer;
	   }
	  .tdredalert td{
		background-color:#abc ! important;
		color:#000;
		cursor:pointer;
	   }
	  .tdinactive td{
		background-color:#ddd ! important;
		color:#333 ! important;
		cursor:pointer;
		display:none;
	   }
	  .tdinactivevisible td{
		background-color:#ddd ! important;
		color:#333 ! important;
		cursor:pointer;
	   }
	  .tdinactivevisible:hover td{
		background-color:#fff ! important;
		color:#333 ! important;
		cursor:pointer;
	   }	   
            .editable {padding-left:25px;} 
            .editable:hover{background-color:#ababef;padding-left:25px;}
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
            
            .popover {
              max-width: 600px;
              width:auto;
            }
</style>
     
    <!-- Le fav and touch icons --> 
    <link href="js/tsort/style.css" rel="stylesheet">
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
<%--
    ######################
    Name und SeminarID des angemeldeten
    Benutzers
    ######################
--%>
<input type="hidden" id="logged-in-user-name" value="<%=user.getDozentNachname() %>" />
<input type="hidden" id="logged-in-user-vorname" value="<%=user.getDozentVorname() %>" />
<input type="hidden" id="logged-in-user-seminar" value="<%=user.getSdSeminarID() %>" />
<input type="hidden" id="logged-in-user-accesslevel" value="<%=user.getDozentAccessLevel() %>" />
    <div class="navbar navbar-fixed-top"> 
      <div class="navbar-inner"> 
        <div class="container-fluid"> 
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> 
            <span class="icon-bar"></span> 
            <span class="icon-bar"></span> 
            <span class="icon-bar"></span> 
          </a> 
            <a class="brand" href="#">U<span style="color:red">:</span>P</a> 
          <div class="nav-collapse"> 
            <ul class="nav"> 
              <li class="active"><a href="#"><i class="icon-home"></i> Home</a></li> 
              <li><a href="hilfe-studierende.jsp"><i class="icon-question-sign"></i> Hilfe</a></li> 
            </ul> 
<%--
    ############################################
    Suchformular "Studierende suchen"
    ############################################
--%>              
            <form class="navbar-search pull-left form-inline" id="fFindStudents" action="#" name="fFindStudents">
                <i class="icon-search"></i>&nbsp; <input type="text" id="txtSuche" class="search-query" placeholder="Studierende suchen..."></input>
                <label>
                    <span>  &nbsp;&nbsp;<input type="checkbox" id="chkEhemalige" onchange="findStudents()" name="chkEhemalige" style="margin-top:-3px;"> Ehemalige</span>
                </label>
            </form>
            <ul class="nav pull-right"> 
                <li class="divider-vertical"></li> 
                <li class="dropdown"> 
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Angemeldet als <%=user.getDozentTitel() + " " + user.getDozentVorname() + " " + user.getDozentNachname()%> <b class="caret"></b></a> 
                    <ul class="dropdown-menu"> 
                        <li><a href="./daten.jsp">Daten</a></li> 
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
        <jsp:include page="fragments/navigationLeftMaster.jsp" />
<%--
    ############################################
    Unsichtbare Templates
    ############################################
--%>       <div id="InvisibleTemplates" style="display:none">
    
    <%--
        ############################################
        Template: Tabelle gefundenen Studierenden
        ############################################
    --%> 
        <table>
            <tr id="template_studierende" class="linked-tablerow" onclick="showDetails($(this).data('shj_item').matrikelnummer,$(this).data('shj_item'))">
                <td class="student_nachname"></td>
                <td class="student_vorname"></td>
                <td class="student_matrikelnummer"></td>
                <td class="student_studiengang"></td>
                <td class="student_info Altbestand-ausblenden"></td>
            </tr>
        </table>
    <%--
        ############################################
        Template: Tabelle Kurssuche-
        ############################################
    --%>               
    <table> 

    </table>
 
    <%--
        ############################################
        Template: Tabelle mit absolvierten Prüfungen
        ############################################
    --%>
            <table>
                <tr id="template_pruefung">
                    <td class="pruefung_bezeichnung"></td>
                    <td class="pruefung_datum"></td>
                    <td class="pruefung_semester"></td>
                    <td class="pruefung_note"></td>
                    <td title="wtf">
                        <a title="Drucken" href="#" class="btn btn-primary" 
                           onclick="printPruefung($(this).parents('tr').data('shj_item'))"><i class="icon-print icon-white"></i></a>&nbsp;
                        <a title="Löschen" href="#" class="btn btn-danger" 
                           onclick="deletePruefung($(this).parents('tr').data('shj_item'))"><i class="icon-trash icon-white"></i></a>&nbsp;
                        <a onclick="showPruefungCalculations($(this).parents('tr').data('shj_item').notenberechnung)" title="Notenberechnung" href="#" class="btn btn-info notenberechnung"><i class="icon-info-sign icon-white"></i></a>
                    </td>                       
                </tr>
                <tr id="template_fehlendeLeistungen">
                    <td class="fehlendeLeistung_name"></td>
                </tr>
                <tr class="linked-tablerow" id="template_leistung" onclick="showLeistung( $(this).data('shj_item'));">
                    <td class="leistung_name" 
                        title=""></td>
                    <td class="leistung_note"></td>
                    <td class="leistung_datum shjDataCol"></td>
                </tr>
            <tr id="template_kurs" class="linked-tablerow"  onclick="coursePicked($(this).data('shj_item'))">
                <td class="kurssuche_semester_indicator"></td>
                <td class="kurssuche_leistung"></td>
                <td class="kurssuche_lehrende"></td>
                <td class="kurssuche_titel"></td>
            </tr>                
                <tr class="linked-tablerow" id="template_anmeldung" onclick="showAnmeldung( $(this).data('shj_item'));">
                    <td class="anmeldung_name" 
                        title=""></td>
                    <td class="anmeldung_datum"></td>
                </tr>           
                <tr id="template_transkript_absolvierte" class="with_contextmenu">
                    <td>
                        <span class="template_transkript_absolvierte_name leistungname" onclick="showLeistung($(this).parents('tr').data('shj_item'))"></span><br />
                        <span style="padding-left:25px;" class="editable template_transkript_absolvierte_kurstitel" onclick="showEditMode($(this))"></span><br />
                        <span style="padding-left:25px;" class="editable template_transkript_absolvierte_kurstitel_en" onclick="showEditMode_en($(this))">></span>
                    </td>
                    <td><span class="editable template_transkript_absolvierte_ects" onclick="showEditModeLP($(this))"></span></td>
                    <td class="template_transkript_absolvierte_note"></td>
                </tr>     
                <tr id="template_transkript_offene">
                    <td>
                        <span class="template_transkript_offene_name muted"></span><br />
                    </td>
                    <td></td>
                    <td><button class="btn btn-primary" onclick="transcriptAddLeistung($(this).parents('tr').data('shj_item'), $(this).parents('table').attr('id').substring(4))">Neu</button></td>
                </tr> 
            </table>
            
    <%--
        ############################################
        Template: Überschrift Student Details
        ############################################
    --%>             
            <div class="row-fluid" id="template_subStudentHeadlines">
                <div class="span2"><a href="#" target="_blank" class="thumbnail" id="template_picplace"><img id="template_studentPicture" src="img/openclipart_dagobert83_user_icon.png" alt=""></img></a></div>
                <div class="span4" id="studentInfoBox">
                    <p><span class="tpl_studentWell_matrikelnummer"></span></p>
                    <strong><p><span class="tpl_studentWell_anrede"></span> <span class="tpl_studentWell_vorname"></span> <span class="tpl_studentWell_nachname"></span></p></strong>
                    <p><span class="tpl_studentWell_ziel"></span> <span class="tpl_studentWell_fachname"></span></p>
                    <p><span class="tpl_studentWell_fachsemester"></span>. Fachsemester <span style="display:none" class="altbestand">Altbestand (nicht aktuell immatrikuliert)</span></p>
                </div>
                <div class="span3" id="tpl_studentFachnote"></div>
            </div>
    <%--
        ############################################
        Template: Student Details
        ############################################
    --%> 
            <div class="row-fluid" id="template_studentDetails">
                <div class="span4">
                <%--
                    -----------------------------------------------
                    Spalte "Person"
                    ----------------------------------------------- 
                --%> 
                <div class="well">
                    <h2 class="muted">Person</h2>
                    <%if(user.getDozentAccessLevel()>=500){%>                    
                    <p><button class="btn btn-success btn-small" accesskey="l" onclick="login()" class="template_studentDetails_cmdLogIn"><i class="icon-ok"></i> <u>L</u>og in</button></p><hr>
                    <p><a target="_blank" class="btn btn-inverse btn-mini zuv_heidelberg_ext" href="#">ZUV</a></p>
                    <%}%>
                    <hr>
                    <dl>
                        <dt>Kontakt</dt>
                        <dd><strong><span class="template_studentDetails_vorname"></span> <span class="template_studentDetails_nachname"></span></strong><br />
                            <p><span class="template_studentDetails_strasse"></span><br />
                                <span class="template_studentDetails_plz"></span> <span class="template_studentDetails_ort"></span><br /><br />
                                Telefon: <span class="template_studentDetails_telefon"></span><br />
                                Mobil: <span class="template_studentDetails_mobil"></span><br /></p>
                            <hr>
                        <dt>E-Kontakt</dt>
                        <dd><p><a class="template_studentDetails_email" href="mailto:E-Mail">E-Mail</a><br />
                                <a class="template_studentDetails_homepage" href="">Homepage</a></p>
                        </dd>
                        <hr>
                        <dt>Daten</dt>
                        <dd>* <span class="template_studentDetails_geburtstag"></span> in <span class="template_studentDetails_geburtsort"></span><br />
                            Staat: <span class="template_studentDetails_staat"></span><br /></dd>
                    </dl>
                </div>
            </div>
                <%--
                    -----------------------------------------------
                    Spalte "Studium"
                    ----------------------------------------------- 
                --%>                 
            <div class="span4">
                <div class="well">
                    <h2 class="text-warning">Studium</h2>
                    <%if(user.getDozentAccessLevel()>=500){%>
                    <p><button class="btn btn-warning btn-small" onclick="showStudiengangwechsel()" class="template_studentDetails_cmdFachwechsel"><i class="icon-ok"></i> Fach wechseln</button></p><hr />
                    <%}%>
                    <dl>
                        <dt>Fach</dt>
                        <dd><span class="template_studentDetails_fachname" /></dd>
                        <dt>Fachsemester</dt>
                        <dd><span class="template_studentDetails_fachsemester" /></dd>
                        <hr>
                        <dt>Ziel</dt>
                        <dd><span class="template_studentDetails_ziel" /></dd>                        
                        <hr>
                        <dt>Erstimmatrikulation</dt>
                        <dd><span class="template_studentDetails_erstimmatrikulation" /></dd>
                        <dt>Zuletzt bestätigt</dt>
                        <dd><span class="template_studentDetails_update" /></dd>
                        <hr>
                        <dt>Urlaubssemester</dt>
                        <dd><span class="template_studentDetails_urlaub" /></dd>
                    </dl>
                    <hr />

                </div>
            </div>
                <%--
                    -----------------------------------------------
                    Spalte "Bemerkungen"
                    ----------------------------------------------- 
                --%>             
            <%if(user.getDozentAccessLevel()>=500){ %>
            <div class="span4">
                <div class="well" id="template_studentDetails_Bemerkungen">
                    <h2 class="text-info">Bemerkungen</h2>
                    <p><button accesskey="b" class="btn btn-primary btn-small" onclick="showBemerkung()" class="template_studentDetails_cmdShowAddBemerkung"><i class="icon-ok"></i> Neue <u>B</u>emerkung</button></p>
                    <div id="template_studentDetails_BemerkungListeContainer"><dl id="template_studentDetails_BemerkungListe">
                        <dt><span class="template_studentDetails_Bemerkungautor"></span> <span class="template_studentDetails_Bemerkungdatum"></span></dt>
                        <dd onclick="showBemerkung($(this).parent('dl').data('shj_item'))" class="linked-def"><span class="template_studentDetails_Bemerkungbemerkung"></span></dd><hr />
                    </dl></div>
                </div>
            </div>
            <%}%>
            </div>
        </div>

        <div class="span10"> 
            <div class="row-fluid"> 
                <div class="span10">
                    <div class="well" id="studentInfo" style="display:none"></div>                
   <%--
        ############################################
        Ergebnisliste Studierendensuche
        ############################################
    --%> 
   	

                <div id="liste" class="signUp_viewToggle">
                    <table class="table table-striped table-hover" id="results" style="display:none">
                        <thead>
                        <th>Name</th>
                        <th>Vorname</th>
                        <th>Matrikelnr</th>
                        <th>Studiengang</th>
                        <th>Bem.</th>
                        </thead>
                        <tbody>
                        </tbody>
                    </table> 
                </div>
                
   <%--
        ############################################
        Anzeige einzelne(r) Studierende(r): Register
        ############################################
    --%> 
            <div id="singleStudent" style="display:none">      
			  <ul class="nav nav-tabs">
               <%--
                    -----------------------------------------------
                    Register "Details"
                    ----------------------------------------------- 
                --%>                                            
				  <li id="studentDetails" class="signUp_Student_Register">
				    <a href="#" onclick="showDetails('same')">Details</a>
				  </li>
               <%--
                    -----------------------------------------------
                    Register "Leistungen"
                    ----------------------------------------------- 
                --%>              
				  <li id="studentLeistungen" class="dropdown" class="signUp_Student_Register">
				    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Leistungen <b class="caret"></b></a>
				    <ul class="dropdown-menu">
				     	<li><a href="#" id="studentLeistungListe"><i class="icon-list-alt"></i> Liste</a></li>
				     	<li><a href="#" onclick="showKurssuche(false)"><i class="icon-plus"></i> Neu</a></li>
				    	<li><a href="#" onclick="showEmptyCreditForm()"><i class="icon-plus-sign"></i> Neu (ohne Kurs)</a></li>
				    </ul>
				  </li>
               <%--
                    -----------------------------------------------
                    Register "Anmeldungen"
                    ----------------------------------------------- 
                --%>              
				  <li id="studentAnmeldungen" class="dropdown" class="signUp_Student_Register">
				  	<a href="#" class="dropdown-toggle" data-toggle="dropdown">Anmeldungen <b class="caret"></b></a>
				    <ul class="dropdown-menu">
				     	<li><a href="#" id="studentAnmeldungListe"><i class="icon-list-alt"></i> Liste</a></li>
				    	<li><a href="#" onclick="showKurssuche(true)"><i class="icon-plus"></i> Neu</a></li>
				    </ul>
				  </li>
               <%--
                    -----------------------------------------------
                    Register "Transkript"
                    ----------------------------------------------- 
                --%>          <%if(user.getDozentAccessLevel()>=500){%>    
				  <li id="studentTranskript" class="dropdown" class="signUp_Student_Register">
				  	<a href="#" class="dropdown-toggle" data-toggle="dropdown">Transkript <b class="caret"></b></a>
				    <ul class="dropdown-menu">
                                        <li><a href="#" id="cmdPrintDocxGen" data-template="template_exp.docx"><i class="icon-print"></i> ...Experimentell</a></li>
                                        <li><a href="#" id="cmdPrintDocxGen_en" data-template="template_exp_en.docx"><i class="icon-print"></i> ...Experimentell (engl.)</a></li>
                                        <li><a href="#" id="cmdShowFinalTranscripts"><i class="icon-file"></i> ...permanente Transkripte</a></li>
				    	<li><a href="#" onclick="showTranscript()"><i class="icon-edit"></i> Bearbeiten</a></li>
				    </ul>				  
				  </li>
                                  
               <%--
                    -----------------------------------------------
                    Register "Prüfungen"
                    ----------------------------------------------- 
                --%>              
				  <li id="studentPruefungen" class="dropdown" class="signUp_Student_Register">
				    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Prüfungen <b class="caret"></b></a>
				  	<ul class="dropdown-menu">
				  		<li><a href="#" id="cmdShowPruefungen"><i class="icon-list-alt"></i> Liste</a></li>
				  		<li><a href="#" onclick="showPruefungOptions()"><i class="icon-plus"></i> Neu</a></li>
				  	</ul>
				  </li>
                                  <%}%>
				</ul>
                                  
   <%-- 
        ############################################
        Register INHALTE
        ############################################
    --%> 
                <%--
                    -----------------------------------------------
                    Inhalt Register "Details"
                    ----------------------------------------------- 
                --%>
                <div id="details" class="signUp_viewToggle">
		</div>
                
               <%--
                    -----------------------------------------------
                    Inhalt Register "Leistungen": Tabelle
                    ----------------------------------------------- 
                --%>	      		
                <div id="divLeistungen" style="display:none" class="signUp_viewToggle">
                    <table class="table table-striped tableSorter" id="tLeistungen">  
                        <thead><tr>
                        <th>Leistung</th>
                        <th>Note</th>
                        <th>Datum</th></tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table> 
                    <div class="pull-right"><span style="color:red;font-size:1.5em">*</span><span> = Leistung anerkannt</span></div>
                </div>
               <%--
                    -----------------------------------------------
                    Inhalt Register "Prüfungen": Tabelle
                    ----------------------------------------------- 
                --%>             
                <%if(user.getDozentAccessLevel()>=500){%>
                <div id="divPruefungen" style="display:none" class="signUp_viewToggle">
                    <table class="table table-striped" id="tPruefungen">
                        <thead>
                        <th>Prüfung</th>
                        <th>Datum</th>
                        <th>Semester</th>
                        <th>Note</th>
                        <th></th>
                        </thead>
                        <tbody>
                        </tbody>
                    </table> 
                </div>
                <%}%>
               <%--
                    -----------------------------------------------
                    Inhalt Register "Anmeldungen": Tabelle
                    ----------------------------------------------- 
                --%>
                <div id="divAnmeldungen" style="display:none" class="signUp_viewToggle">
                    <table class="table table-striped" id="tAnmeldungen">
                        <thead>
                        <th>Anmeldg. für</th>
                        <th>Datum</th>
                        </thead>
                        <tbody>
                        </tbody>
                    </table> 
                </div>
                <%--
                    -----------------------------------------------
                    Inhalt Register "Transkript"
                    ----------------------------------------------- 
                --%>
                <%if(user.getDozentAccessLevel()>=500){%>
                <div id="transkript" style="display:none" class="signUp_viewToggle">

                </div>	
                <div style="display:none">
                    <table class="table table-striped table-condensed" id="tStudienbilanz">
                                <thead>
                                <th><span class="template_ModulSummary_modulname"></span><br /><span class="template_ModulSummary_computedInfo"></span></th>
                                <th style="width: 10%">LP</th>
                                <th style="width: 10%">Note</th>
                                </thead>
                                <tbody></tbody>
                            </table>
                </div>
                <%}%>
   <%--
        ############################################
        MODALE Dialoge
        ############################################
    --%> 	 
                <%if(user.getDozentAccessLevel()>=500){%>
                <%--
                    -----------------------------------------------
                    Modalfenster Finale Transkripte
                    ----------------------------------------------- 
                --%>	           
                <div class="modal hide fade" id="modFinalTranscripts" class="signUp_viewToggle">
                    <div class="modal">
                        <div class="modal-header">
                            <a class="close" data-dismiss="modal">×</a>
                            <h3>Abschlusstranskripte</h3>
                        </div>
                        <div class="modal-body" id="listePermanenteTranskripte">

                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-info" onclick="addFinalTranscript(false)" data-loading-text="Wird gespeichert...">Dt. Transkript</button>
                            <button class="btn btn-primary" onclick="addFinalTranscript(true)" data-loading-text="Wird gespeichert...">Engl. Transkript</button>
                            <button class="btn btn-warning" onclick="addFinalTranscript(true, true)" data-loading-text="Wird gespeichert...">GPA-Daten</button>
                        </div>
                    </div>
                    </form>
                </div>
                
                <%--
                    -----------------------------------------------
                    Modalfenster Bemerkung edieren/löschen/neu
                    ----------------------------------------------- 
                --%>	           
                <div class="modal hide fade" id="modBemerkungErfassen" class="signUp_viewToggle">
                    <form id="fBemerkungEdit" accept-charset="UTF-8" >
                    <div class="modal">
                        <div class="modal-header">
                            <a class="close" data-dismiss="modal">×</a>
                            <h3>Bemerkung</h3>
                        </div>
                        <div class="modal-body">
                                <textarea class="span10" id="bemerkung_bemerkung" name="txtBemerkung"
                                          placeholder="Bemerkung eingeben" rows="5"></textarea>
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-info shj_lifecycle_add" onclick="addBemerkung()" data-loading-text="Wird gespeichert..." accesskey="a" id="cmdAddBemerkung"><u>S</u>peichern</button>
                            <button class="btn btn-info shj_lifecycle_update" onclick="updateBemerkung($('#fBemerkungEdit').data('shj_item'))" data-loading-text="Wird gespeichert..." accesskey="u" id="cmdUpdateBemerkung"><u>U</u>pdate</button>
                            <button class="btn btn-danger shj_lifecycle_delete" onclick="deleteBemerkung($('#fBemerkungEdit').data('shj_item'))" id="cmdDeleteBemerkung" accesskey="n" type="submit">Lösche<u>n</u></button>
                        </div>
                    </div>
                    </form>
                </div>
	          
                <%--
                    -----------------------------------------------
                    Modalfenster Neue Bemerkung eingeben
                    ----------------------------------------------- 
                --%>	            
                <div class="modal hide fade" id="modBemerkungNeuErfassen" class="signUp_viewToggle">
                    <div class="modal">
                        <div class="modal-header">
                            <a class="close" data-dismiss="modal">×</a>
                            <h3>Neue Bemerkung</h3>
                        </div>
                        <div class="modal-body">
                            <form accept-charset="UTF-8" action="" method="POST">
                                <textarea class="span12" id="txtBemerkungNeu" name="txtBemerkungNeu"
                                          placeholder="Bemerkung eingeben" rows="5"></textarea>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-info" accesskey="s" data-loading-text="Wird gespeichert..." id="cmdAddBemerkung"><u>S</u>peichern</button>
                        </div>
                            
                    </div>
                </div>                 
                <%}%>
                <%--
                    -----------------------------------------------
                    Modalfenster Allg.: Wirklich löschen?
                    ----------------------------------------------- 
                --%>	            
                <div class="modal hide fade" id="modShj_SignUp_ConfirmDelete" class="signUp_viewToggle">
                    <div class="modal">
                        <div class="modal-header">
                            <a class="close" data-dismiss="modal">×</a>
                            <h3 class="text-warning shj_SignUp_ConfirmDelete_Header"></h3>
                        </div>
                        <div class="modal-body shj_SignUp_ConfirmDelete_Body"></div>
                        <div class="modal-footer">
                            <button class="btn btn-danger" data-loading="Wird gelöscht..." id="cmdShj_SignUp_ConfirmDelete"><u>W</u>irklich löschen</button>
                            <button class="btn btn-warning" id="cmdShj_SignUp_ConfirmDelete_Cancel"><u>A</u>bbrechen</button>
                        </div>
                    </div>
                </div>

                <%if(user.getDozentAccessLevel()>=500){%>
                <%--
                    -----------------------------------------------
                    Modalfenster Zeige Notenberechnung
                    ----------------------------------------------- 
                --%>	            
                <div class="modal hide fade" id="modPruefungNotenberechnung" class="signUp_viewToggle">
                    <div class="modal">
                        <div class="modal-header">
                            <a class="close" data-dismiss="modal">×</a>
                            <h3>Berechnung der Note</h3>
                        </div>
                        <div class="modal-body"></div>
                    </div>
                </div>	
                <%}%>
                <%--
                    -----------------------------------------------
                    Modalfenster LEISTUNGSDETAILS
                    ----------------------------------------------- 
                --%>
                <div class="modal hide fade" id="modLeistungDetails" class="signUp_viewToggle">
                    <div class="modal">
                        <div class="modal-header"><a class="close" data-dismiss="modal">×</a><h3>Leistung</h3></div>
                        <div class="modal-body">
                            <form class="form-horizontal" id="fLeistungDetails">
                                <fieldset>
                                    <div class="control-group" id="cgLeistung"> 
                                        <label class="control-label" for="leistung_name">Leistung</label> 
                                        <div class="controls"> 
                                            <input type="text" id="leistung_name" name="idxLeistungsID" value="" data-provide="typeahead" class="input-xlarge"></input>
                                        </div> 
                                    </div>
                                    <div class="control-group" id="cgKurs"> 
                                        <label class="control-label" for="txtKurstitel">Kurs Deutsch<br />Englisch</label> 
                                        <div class="controls"> 
                                            <input type="text" class="input-xlarge" id="leistung_kurstitel" name="txtSLKursTitel"></input><br />
                                            <input type="text" class="input-xlarge" id="leistung_kurstitel_en" name="txtSLKursTitel_en""></input> 
                                        </div> 
                                    </div>
                                    <div class="control-group" id="cgModul"> 
                                        <label class="control-label" for="cboModul">Modul</label> 
                                        <div class="controls"> 
                                            <select id="cboModul" name="cboModulID"></select> 
                                        </div> 
                                    </div>
                                    <div class="control-group" id="cgAussteller"> 
                                        <label class="control-label" for="leistung_aussteller">Aussteller</label> 
                                        <div class="controls"> 
                                            <input type="text" id="leistung_aussteller" value="" data-provide="typeahead" class="input-xlarge"></input>
                                        </div> 
                                    </div>
                                    <div class="control-group" id="cgNote"> 
                                        <label class="control-label" for="v">Note/Datum/LP</label> 
                                        <div class="controls"> 
                                            <select id="leistung_noten_id" name="cboNoteID" class="span4"></select>
                                            <input type="text" id="leistung_datum" name="txtStudentLeistungAusstellungsdatum" value="" class="span4"></input>
                                            <input type="text" id="leistung_ects" name="txtStudentLeistungCreditPts" value="" class="span2"></input> 
                                        </div> 
                                    </div>
                                    <div class="control-group" id="cgLeistungstyp"> 
                                        <label class="control-label" for="txtLeistung">Leistung</label> 
                                        <div class="controls"> 
                                            <select class="span3" id="leistung_leistungstyp" name="cboStudentLeistungCustom3" ><option value="">--</option><option value="HA">Hausarbeit</option><option value="KL">Klausur</option></select>
                                            <input type="text" class="span6" id="leistung_details" name="txtStudentLeistungDetails" style="display:none"></input>
                                        </div> 
                                    </div>	
                                    <div class="control-group" id="cgBemerkung"> 
                                        <label class="control-label" for="txtBemerkung">Bemerkung</label> 
                                        <div class="controls"> 
                                            <input type="text" class="input-xlarge" id="leistung_bemerkung" name="txtStudentLeistungBemerkung" class="input-xlarge"></input>
                                            <input type="hidden" name="idxLeistungIDOrig" id="idxLeistungIDOrig" value=""></input>
                                            <input type="hidden" name="txtDozentID" id="txtDozentID" value=""></input>
                                            <input type="hidden" name="idxLeistungCountOrig" id="idxLeistungCountOrig" value=""></input>
                                            <input type="hidden" name="idxLeistungIDNew" id="idxLeistungIDNew" value=""></input>
                                            <input type="hidden" name="txtMatrikelnummer" id="txtMatrikelnummer" value=""></input>
                                        </div> 
                                    </div>
                                    <div class="control-group" id="cgAnerkannt"> 
                                        <label class="control-label" for="leistung_anerkannt">Anerkannt?</label> 
                                        <div class="controls"> 
                                            <input type="checkbox" id="leistung_anerkannt" value="" class="input-xlarge"></input>
                                        </div> 
                                    </div>
                                </fieldset>
                            </form>
                        </div>
                        <div class="modal-footer" id="divCreditDetailOptions">
                            <a href="#" id="cmdUpdateCredit" class="btn btn-success disabled" data-loading-text="Wird gespeichert..." onclick="updateLeistung($('#fLeistungDetails').data('shj_item'), false)"><i class="icon-ok icon-white"></i> Speichern</a>
                            <a href="#" id="cmdDeleteCredit" class="btn btn-danger" onclick="deleteLeistung($('#fLeistungDetails').data('shj_item'))"><i class="icon-trash icon-white"></i> Löschen</a>
                            <a href="#" id="cmdPrintCredit" class="btn btn-primary" data-loading-text="Moment bitte..." onclick="updateLeistung($('#fLeistungDetails').data('shj_item'), true)"><i class="icon-print icon-white"></i> Drucken</a>
                        </div>
                    </div>
                </div>

                <%--
                    -----------------------------------------------
                    Modalfenster KURSSUCHE
                    ----------------------------------------------- 
                --%>
                <div class="modal hide fade" id="modKurssuche" class="signUp_viewToggle">
                    <div class="modal">
                        <div class="modal-header"><a class="close" data-dismiss="modal">×</a><h3>Kurssuche</h3></div>
                        <form class="form-horizontal" id="fKurssuche" name="fKurssuche" action="#">
                            <div class="modal-body" id="Kurssuchformular">
                                <fieldset>
                                    <div class="control-group" id="cgKurssucheSemester"> 
                                        <label class="control-label" for="cboKurssucheSemester">Semester (*)</label> 
                                        <div class="controls"> 
                                            <select id="cboKurssucheSemester" name="cboKurssucheSemester" class="span9"><option value="current">aktuell</option></select>
                                        </div> 
                                    </div>
                                    <div class="control-group" id="cgKurssucheLeistung"> 
                                        <label class="control-label" for="cboKurssucheLeistung">Leistung</label> 
                                        <div class="controls"> 
                                            <input type="text" id="cboKurssucheLeistung" name="idxKurssucheLeistung" value="" data-provide="typeahead" class="input-xlarge"></input>
                                        </div> 
                                    </div>
                                    <div class="control-group" id="cgKurssucheKurs"> 
                                        <label class="control-label" for="txtKurssucheKurstitel">Kurs</label> 
                                        <div class="controls"> 
                                            <input type="text" class="input-xlarge" id="txtKurssucheKurstitel" name="txtSLKurssucheKursTitel"></input> 
                                        </div> 
                                    </div>				          
                                    <div class="control-group" id="cgAussteller"> 
                                        <label class="control-label" for="txtDozent">Lehrende(r)</label> 
                                        <div class="controls"> 
                                            <input type="text" id="txtKurssucheDozent" name="txtKurssucheDozent" value="" class="input-xlarge"></input>
                                        </div> 
                                    </div>
                                </fieldset>
                            </div>
                            <div class="modal-body" id="Kurssuchergebnis" style="display:none">
                                <table id="tKurssuchergebnistabelle" class="table table-striped" >
                                    <thead>
                                    <th class="span1">Semester</th>
                                    <th class="span4">Leistung</th>
                                    <th class="span4">Lehrende(r)</th>
                                    <th class="span3">Kurstitel</th>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                            <div class="modal-footer" id="divKurssucheAktion">
                                <input type="hidden" id="bCommitment" value="" />
                                <input type="submit" id="cmdSearch" data-loading-text="Suche läuft..." class="btn btn-primary" value="Suchen" />
                            </div>
                        </form>
                    </div>
                </div>
<%if(user.getDozentAccessLevel()>=500){%>
                <%--
                    -----------------------------------------------
                    Modalfenster Fachwechsel
                    ----------------------------------------------- 
                --%>	            
                <div class="modal hide fade" id="modStudiengangswechsel" class="signUp_viewToggle">
                    <div class="modal">
                        <div class="modal-header">
                            <a class="close" data-dismiss="modal">×</a>
                            <h3>Wechsel des Studiengangs</h3>
                        </div>
                        <form class="form-horizontal" id="fStudiengangwechsel" name="fStudiengangwechsel" action="#">
                            <div class="modal-body" id="StudiengangswechselFormular">
                                <fieldset>
                                    <div class="control-group" id="cgStudiengangswechsel"> 
                                        <label class="control-label" for="cboFach">Studiengang</label> 
                                        <div class="controls"> 
                                            <select id="cboFach" name="cboFach" class="span9"></select>
                                        </div> 
                                    </div>
                                </fieldset>
                            </div>
                            <div class="modal-footer" id="divStudiengangswechselAktionen">
                                <button data-loading-text="Wird gespeichert..." id="cmdWechsel" class="btn btn-primary"><i class="icon-refresh icon-white"></i> Umschreiben</button>
                            </div>
                        </form>
                    </div>
                </div>
<%}%>	
                <%--
                    -----------------------------------------------
                    Modalfenster LOGIN ERNEUERN
                    ----------------------------------------------- 
                --%>	            
                <div class="modal hide fade signUp_viewToggle" id="modReLogin">
                    <div class="modal">
                        <div class="modal-header">
                            <a class="close" data-dismiss="modal">×</a>
                            <h3>Login erneuern</h3>
                        </div>
                        <form class="form-horizontal" id="fRenewLogin" name="fRenewLogin" action="#">
                            <div class="modal-body" id="fRenewLoginFormular">
                                <p>Das Login ist abgelaufen, sorry. Sie können die Sitzung wieder aufnehmen, 
                                    wenn Sie Ihr Passwort erneut eingeben.<br />
                                    Bitte beachten Sie, dass Sie möglicherweise die unterbrochene Aktion
                                    (Löschen oder Hinzufügen von Daten) <em>erneut</em> ausführen müssen.</p> 
                                <fieldset>
                                    <div class="control-group" id="cgRenewLogin"> 
                                        <label class="control-label" for="txtPwd">Passwort</label> 
                                        <div class="controls"> 
                                            <input id="txtPwd" name="txtPwd" class="span9" type="password" value=""></input>
                                        </div> 
                                    </div>
                                </fieldset>
                            </div>
                            <div class="modal-footer" id="divRenewLoginAktionen">
                                <button type="submit" id="cmdRenewLogin" class="btn btn-primary"><i class="icon-refresh icon-white"></i> Anmeldung erneuern</button>
                                <a href="logout.jsp" id="cmdLogOut" class="btn btn-warning"><i class="icon-off icon-white"></i> Abmelden</a>
                            </div>
                        </form>
                    </div>
                </div>
<%if(user.getDozentAccessLevel()>=500){%>				
                <%--
                    -----------------------------------------------
                    Modalfenster Neue Prüfung 
                    ----------------------------------------------- 
                --%>	            
                <div class="modal hide fade" id="modPruefungAddOptions" class="signUp_viewToggle">
                    <div class="modal">
                        <div class="modal-header">
                            <a class="close" data-dismiss="modal">×</a>
                            <h3>Neue Prüfung</h3>
                        </div>
                        <form class="form-horizontal" id="fPruefungAdd" name="fPruefungAdd" action="#">
                            <div class="modal-body" id="PruefungAddFormular">
                                <fieldset>
                                    <div class="control-group" id="cgPruefungAdd"> 
                                        <label class="control-label" for="cboPruefungAdd">Prüfung</label> 
                                        <div class="controls"> 
                                            <select id="cboPruefungAdd" name="cboPruefungAdd" class="span9"></select>
                                            <span id="lblPruefungAddWarning" style="display:none" class="help-inline">Es fehlen noch Leistungen!</span>
                                        </div> 
                                    </div>
                                </fieldset>
                                <div id="missingCreditList" style="display:none">
                                    <div class="alert alert-error">
                                    <fieldset>
                                        <label class="control-label" for="chkIgnore"></label> 
                                        <div class="controls"><strong>Fehlende Leistungen</strong><br />
                                            <table id="tFehlendeLeistungen" class="table table-condensed" >
                                                <thead>
                                                <th></th>
                                                </thead>
                                                <tbody></tbody>
                                            </table></div>
                                    </fieldset>
                                        </div>
                                    <div class="alert alert-warning">
                                    <fieldset>
                                        <div class="control-group" id="cgIgnoreMissingNote"> 
                                            <label class="control-label" for="txtNote">Note (nicht berechenbar)</label> 
                                            <div class="controls"> 
                                                <input type="text" id="txtNote" name="txtNote"></input>
                                            </div> 
                                        </div>
                                    </fieldset>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer" id="divPruefungAddAktionen">
                                <input type="hidden" id="flagIgnoreMissingCredits" value="false"></input>
                                <button id="cmdPruefungAdd" data-loading-text="Wird gespeichert..." class="btn btn-success"><i class="icon-ok icon-white"></i> Ausstellen</button>
                            </div>
                        </form>
                    </div>
                </div>
                <%}%>
            </div><!-- /singleStudent -->
            
                </div>
             
          </div><!--/row--> 
        </div><!--/span--> 
      </div><!--/row--> 
 
      <hr> 
 
      <footer> 
        <p>&copy; shj-online 2012</p> 
      </footer> 

    <div class="signUp_waitmsg-modal"><!-- Place at bottom of page --></div>

    </div><!--/.fluid-container--> 
    
    <!-- Le javascript
    ================================================== --> 
    <!-- Placed at the end of the document so the pages load faster --> 
    <!-- Placed at the end of the document so the pages load faster -->
    <jsp:include page="fragments/libs.jsp"></jsp:include>
    <!-- 
    <script src="js/signup-faculty-student.min.js"></script>
    -->

        <script type="text/javascript" src="js/signup-faculty-student-dom-common.js">
	</script>
        
        <script src="js/FileSaver.js" type="text/javascript"></script>
        <script src="js/docxgen.min.js" type="text/javascript"></script>
        <script src="js/jszip-utils.js" type="text/javascript"></script>
        <script src="js/signup-faculty-common.js" type="text/javascript"></script>
	<script type="text/javascript" src="js/signup-faculty-student.js">
	</script>


        <script src="js/tsort/tsort.min.js"> </script>
        <script src="js/IndexedDBShim.min.js"> </script>

        <%if(request.getParameter("refer_matrikelnummer")!=null){%>
        <script>
            $(document).ready(function() {
                
            $.signUpGlobal.info.seminar.faecher = new shj.signUp.seminar.Faecher(
                $.signUpGlobal.seminar_id,
                function(data) {
                    $('#txtSuche').val('<%=request.getParameter("refer_matrikelnummer")%>');
                    $('#txtSuche').blur();
                    // Wenn register='transkript' wird die Bilanz angzeiegt, 
                    // Wenn register='anmeldungen' die Anmeldungen.
                    if('<%=request.getParameter("register")%>'=='anmeldungen') findStudents(showAnmeldungen);
                    if('<%=request.getParameter("register")%>'=='transkript') findStudents(showTranscript);
                });                
            });
            
         </script>
        <%}%>
  </body> 
</html> 