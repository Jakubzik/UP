<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%><!DOCTYPE html> 
<jsp:useBean id="student" class="de.shj.UP.logic.StudentData" scope="session"></jsp:useBean>
<%
    // Besteht eine gültige Sitzung?
    if (student.getStudentNachname() == null) {
        %><jsp:forward page="index-studierende.jsp" /><%
        throw new Exception("Sorry, Sie müssen sich neu anmelden");
    }
%>
<html lang="en"> 
    <head> 
        <meta charset="utf-8"> 
        <meta contentType="application/x-www-form-urlencoded"></meta>
        <title>UP -- Studierende</title> 
        <meta name="description" content="Prüfungsverwaltung"> 
        <meta name="author" content="Heiko Jakubzik"> 
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Le HTML5 shim, for IE6-8 support of HTML elements --> 
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]--> 

        <!-- Le styles --> 
        <jsp:include page="fragments/css.jsp" />
        <style type="text/css"> 
            body {
                padding-top: 60px;
                padding-bottom: 40px;
            }
            .sidebar-nav {
                padding: 9px 0;
            }
            .linked-tablerow:hover td{
                background-color:#ddd ! important;
                color:#000;
                cursor:pointer;
            }
            .navbar .brand {
                display: inline-block;
                width: 115px;
                height: 40px;
                padding: 0;
                margin: 0 10px 0 0;
                background: url(img/UP.png) no-repeat;
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
        <link href="js/tsort/style.css" rel="stylesheet">
        <link href="css/bootstrap-fileupload.min.css" rel="stylesheet"> 
        
        <!-- Le fav and touch icons --> 
        <link rel="shortcut icon" href="img/signup.ico"> 
        <link rel="apple-touch-icon" href="images/apple-touch-icon.png"> 
        <link rel="apple-touch-icon" sizes="72x72" href="images/apple-touch-icon-72x72.png"> 
        <link rel="apple-touch-icon" sizes="114x114" href="images/apple-touch-icon-114x114.png"> 
    </head> 

    <body> 
<%--
    ######################
    Name und SeminarID des angemeldeten
    Benutzers
    ######################
--%>
        <input type="hidden" id="logged-in-user-matrikelnummer" value="<%=student.getMatrikelnummer()%>" />
        <input type="hidden" id="logged-in-user-seminar" value="<%=student.getSeminarID()%>" />
        <input type="hidden" id="logged-in-user-fach_id" value="<%=student.getFachID(student.getSeminarID()) %>" />
        <div class="navbar navbar-fixed-top navbar-inverse"> 
            <div class="navbar-inner"> 
                <div class="container-fluid"> 
                    <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> 
                        <span class="icon-bar"></span> 
                        <span class="icon-bar"></span> 
                        <span class="icon-bar"></span> 
                    </a> 
                    <a class="brand" href="#">&nbsp; </a> 
                    <div class="nav-collapse"> 
                        <ul class="nav"> 
                            <li><a id="help_url" href="hilfe/studierende/help_data.jsp"><i class="icon-question-sign icon-white"></i> Hilfe</a></li> 
                        </ul> 

                        <ul class="nav pull-right"> 
                            <li class="divider-vertical"></li> 
                            <li class="dropdown"> 
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown"><%=student.getStudentVorname() + " " + student.getStudentNachname()%> <b class="caret"></b></a> 
                                <ul class="dropdown-menu"> 
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
                <%--
                    ######################
                    Navigationsleiste links
                    ######################
                --%>
                <div class="span2">
                <jsp:include page="fragments/navigationLeftMaster_student.jsp" />
                </div>
                <div class="span10"> 
                    <div class="row-fluid">
                        <div class="span10">
                            <div id="Transkriptdruck" style="display:none" class="signUp_viewToggle">
                                  <h1>Transkripte</h1>
                                  <a id="cmdPrintTranskript" class="btn btn-primary"  href="#">Neues Transkript</a>
                                  <a id="cmdPrintTranskript_en" class="btn btn-primary"  href="#">Neues Transkript (Englisch)</a><br />
                                      
                                    <%-- ================================================================ -->
                                    <!-- 				Gedruckte Transkripte										  -->
                                    <!-- ================================================================ --%>
                                    <div id="divTranskriptdruck">
                                        <h2>Meine Transkripte</h2>
                                        <table class="table table-striped tableSorter" id="tTranskriptdruck">
                                            <thead>
                                                <tr>
                                            <th>Typ</th>
                                            <th>Datum</th>
                                            <th>Aktion</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            </tbody>
                                        </table> 
                                    </div>
                                  </div>
                              <div id="AntragTranskript" style="display:none" class="signUp_viewToggle">
                                  <h1>Anträge</h1>
                                  <ul><li><a id="cmdNeuesTranskript" href="#">Transkript ausstellen</a>.
                                          <%if(student.getFachID(1)==200822){%><li><a id="cmdNeuesGymPOMissingCredits" href="#">Besch. zu fehlenden Leistungen (GymPO) ausstellen</a>.<%}%>
                                  </ul><br />
                                  <div id="AntragTranskript_meineAntraege">
                                      <h2>Meine Anträge</h2>
                                      
                                        <div class="accordion" id="divAntraege">

                                        </div>
                                  </div>
                              </div>
                                <div id="AntragTranskriptStep1" style="display:none" class="signUp_viewToggle">
                                  <h1>Transkript beantragen</h1>
                                 <!-- <div class="progress progress-striped active">
                                    <div class="bar" style="width: 30%;"></div>
                                </div> -->
                                  <h2>1. Schritt: Kontrolle der Daten</h2>
                                  <form>
                                      <label class="checkbox">
                                          <input type="checkbox" id="chkDatenKorrekt"/> Alle Daten sind korrekt.
                                      </label>
                                      <div id='divSprachwahl' style='display: none'>
                                      <label class="checkbox shjModularOnly">
                                          <input type="checkbox" id="chkTranskript_de" onclick="$('#cmdApplyForTranscript').removeClass('disabled');$('#chkTranskript_en').prop('checked', false);"> beantrage deutsches Transkript
                                      </label>
                                      <label class="checkbox shjModularOnly">
                                          <input type="checkbox" id="chkTranskript_en" onclick="$('#cmdApplyForTranscript').removeClass('disabled');$('#chkTranskript_de').prop('checked', false);"> beantrage englisches Transkript
                                      </label>
                                      <label class="checkbox shjUnmodularOnly" style="display:none">
                                          <input type="checkbox" id="chkTranskript_unmodular" onclick="$('#cmdApplyForTranscript').removeClass('disabled');"> beantrage Transkript
                                      </label>
                                      </div>
                                  <a href="#" id="cmdApplyForTranscript" class="btn btn-success disabled">Antrag absenden</a>
                                  <a href="#" id="cmdAntragProblem" class="btn btn-danger">Problem melden</a>
                                  </form>
                                  <div id="AntragTranskript_Bsp" class="well shjUnmodularOnly" style="display:none">
                                      Bitte überprüfen Sie, ob <ul><li>alle Leistungen und </li><li>alle Prüfungen</li></ul>
                                      korrekt in &quot;U<span style="color:red">:</span>P&quot; verzeichnet sind.
                                  </div>
                                  <div id="AntragTranskript_Bsp" class="well shjModularOnly">
                                      <div id="AntragTranskript_header"></div>
                                      <div class='template_Antrag_Modul_name'><b></b></div>
                                      <div class='template_Antrag_leistungname' style="margin-left:10px"><span class="muted template_Antrag_kurstitel"></span> <span class="pull-right template_Antrag_ects">&nbsp;</span> <span class="pull-right template_Antrag_note"></span> </div>
                                  </div>
                              </div>
                            <%--
                                  ######################
                                  unsichtbare Templates
                                  ######################
                              --%>
                              
                              <div id="InvisibleTemplates" style="display:none">
                                  <table>
                                <tr id="template_kurs" class="linked-tablerow" onclick="coursePicked($(this).data('shj_item'))">
                                    <td class="kurssuche_leistung"></td>
                                    <td class="kurssuche_lehrende"></td>
                                    <td class="kurssuche_titel"></td>
                                    <td class="kurssuche_termin"></td>
                                </tr>   
                                </table>
                                <table>
                                <tr id="template_transkriptdruck">
                                    <td class="dokument_dok_name"></td>
                                    <td class="dokument_datum"></td>
                                    <td>
                                        <button id="cmdTranskriptAnsicht" onclick="showDokument($(this).parents('tr').data('shj_item'))" class="btn btn-small btn-info">Ansicht</button>
                                        <button id="cmdTranskriptPrint" onclick="printDokument($(this).parents('tr').data('shj_item'))" class="btn btn-small btn-primary">PDF</button>
                                        <button id="cmdTranskriptDelete" onclick="deleteDokument($(this).parents('tr').data('shj_item'))" class="btn btn-small btn-danger">Löschen</button>
                                    </td>
                                </tr>   
                                </table>
                                  
                                  <%--
                                    -----------------------------------------------
                                    Transkript Kontrollvorlage ... Heading ...
                                    ----------------------------------------------- 
                                --%> 
                                <div id="templatekontrollvorlage_sd">
                                  <h2>Leistungsübersicht</h2>
                                  <h3>für <span class="template_kontrollvorlage_student_anrede"></span> <span class="template_kontrollvorlage_student_vorname"></span> <span class="template_kontrollvorlage_student_nachname"></span></h3>
                                  <b><span class="template_kontrollvorlage_student_fachname"></b><br />                                       
                                  <p><span class="template_kontrollvorlage_student_anrede"></span> <span class="template_kontrollvorlage_student_vorname"></span> <span class="template_kontrollvorlage_student_nachname"></span>, im <span class="template_kontrollvorlage_student_fachsemester"></span> Fachsemester, hat im o.g. Studiengang folgende Leistungen erbracht:</p>
                                </div>
                                
                                  <%--
                                    -----------------------------------------------
                                    ...Leistung...
                                    ----------------------------------------------- 
                                --%> 
                                <div id="template_kontrollvorlage_leistung">- <span class='template_kontrollvorlage_leistung_name' style="margin-left:10px"></span> (<span class="muted template_kontrollvorlage_leistung_datum"></span>) <span class="muted template_kontrollvorlage_leistung_kurstitel"></span> (<span class="template_kontrollvorlage_leistung_ects">&nbsp;</span> LP)<span class="pull-right template_kontrollvorlage_leistung_note"></span> </div>
                                
                                <%--
                                    -----------------------------------------------
                                    ...Eintrag in der Liste der Anträge
                                    ----------------------------------------------- 
                                --%> 
                                <div id="template_divAntrag" class="accordion-group">
                                  <div class="accordion-heading">
                                    <a class="accordion-toggle template_Antrag_antrag" data-toggle="collapse" data-parent="#accordion2" href="#collapseOne">
                                      
                                    </a>
                                  </div>
                                  <div id="collapseOne" class="accordion-body collapse">
                                    <div class="accordion-inner shj_antrag_stati">
                                    </div>
                                  </div>
                                </div>
                                
                                  <%--
                                    -----------------------------------------------
                                    Tabelle für die Studienbilanz (siehe auch unten)
                                    ----------------------------------------------- 
                                --%> 
                                <table>
                                <tr id="template_transkript_absolvierte">
                                    <td>
                                        <span class="template_transkript_absolvierte_name leistungname"></span><br />
                                        <span style="padding-left:25px;" class="editable template_transkript_absolvierte_kurstitel"></span><br />
                                        <span style="padding-left:25px;" class="editable template_transkript_absolvierte_kurstitel_en"></span>
                                    </td>
                                    <td><span class="editable template_transkript_absolvierte_ects"></span></td>
                                    <td class="template_transkript_absolvierte_note"></td>
                                </tr>     
                                <tr id="template_transkript_offene">
                                    <td>
                                        <span class="template_transkript_offene_name muted"></span><br />
                                    </td>
                                    <td></td>
                                    <td></td>
                                </tr> 
                                </table>
                                <%--
                                    -----------------------------------------------
                                    Tabelle Ergebnisse Kurssuche (Anmeldungen)
                                    ----------------------------------------------- 
                                --%>             
                            </table>
                                <%--
                                    -----------------------------------------------
                                    Tabelle für Leistungen ...
                                    ----------------------------------------------- 
                                --%> 
                            <table>
                                <tr class="linked-tablerow" id="template_leistung">
                                    <td class="leistung_name" 
                                        title=""></td>
                                    <td class="leistung_note"></td>
                                    <td class="leistung_datum shjDataCol"></td>
                                </tr>
                                <%--
                                    -----------------------------------------------
                                    ... Anmeldungen, ...
                                --%> 

                                <tr class="linked-tablerow" id="template_anmeldung">
                                    <td class="anmeldung_name" 
                                        title=""></td>
                                    <td class="anmeldung_datum"></td>
                                    <td class="anmeldungDelete"><a title="Löschen" 
                                                                   onclick="deleteAnmeldung($(this).parents('tr').data('shj_item'));" href="#" class="btn btn-danger"><i class="icon-trash icon-white"></i></a></td>
                                </tr>
                                <%--
                                    ... und Prüfungen
                                    ----------------------------------------------- 
                                --%> 
                                
                                <tr id="template_pruefung">
                                    <td class="pruefung_bezeichnung"></td>
                                    <td class="pruefung_datum"></td>
                                    <td class="pruefung_semester"></td>
                                    <td class="pruefung_note"></td>
                                </tr>

                                 <%--
                                    -----------------------------------------------
                                    Tabellenzeile "Studienbilanz" (siehe auch oben)
                                    ----------------------------------------------- 
                                --%> 
                                <tr id="template_StudienbilanzLeistung">
                                    <td class="template_StudienbilanzLeistung_name template_StudienbilanzLeistung_showPopover"><br /><span style="padding-left:10px" class="template_StudienbilanzLeistung_kurs.titel"><small></small></span><br /><span style="padding-left:10px" class="template_StudienbilanzLeistung_kurs.titel_en"><small></small></span></td>
                                    <td class="template_StudienbilanzLeistung_ects"></td>
                                    <td class="template_StudienbilanzLeistung_notenwert"></td>
                                </tr>
                            </table>                                 
                            
                            <%--
                               -----------------------------------------------
                               Ansicht "Daten" (Startansicht)
                               ----------------------------------------------- 
                           --%>                                 
                            <div id="template_subStudentHeadlines" class="row-fluid">
                                <div class="span2" style="text-align: center">
                                        <a href="#" target="_blank" class="thumbnail" id="template_picplace"><img id="template_studentPicture" src="img/openclipart_dagobert83_user_icon.png" alt=""></img></a><hr>
                                        <button class="btn btn-warning btn-small" id="cmdUpdatePic" onclick="$('#modIMGUpload').modal('show');">Bild ändern</button>
                                        <button title="Bild löschen" class="btn btn-danger btn-small" onclick="dropImage()" id="tpl_cmdDeletePic"><i class="icon-trash"></i></button>
                                </div>
  
                                <div class="span2">
                                    <p><strong><span class="tpl_studentWell_vorname">asdf</span> <span class="tpl_studentWell_nachname"></span></strong></p>
                                    <p><span class="tpl_studentWell_strasse"></span><br />
                                    <span class="tpl_studentWell_plz">asdf</span> <span class="tpl_studentWell_ort">asdf</span></p>
                                    <p><span class="tpl_studentWell_fach">asdf</span></p>
                                </div>
                                
                                <div class="span8">
                                <form class="form-horizontal pull-left">
                                  <div class="control-group">
                                    <label class="control-label" for="template_studentDetails_email">Email</label>
                                    <div class="controls">
                                      <input type="text" id="template_studentDetails_email" class="template_studentDetails_change" name="email" placeholder="Email"></input>
                                    </div>
                                  </div>
                                  <div class="control-group">
                                    <label class="control-label" for="template_studentDetails_handy">Mobiltel.</label>
                                    <div class="controls">
                                      <input type="text" id="template_studentDetails_mobil" name="mobil" class="template_studentDetails_change" placeholder="Mobiltelefon"></input>
                                    </div>
                                  </div>

                                    <div class="control-group">
                                        <div class="controls">
                                            <label class="checkbox inline">
                                            <input type="checkbox" id="template_studentDetails_infoEmail" class="template_studentDetails_change"> E-Mail Nachricht bei neuem Schein
                                            </label>
                                        </div>
                                    </div>                                  
                                  <div class="control-group">
                                    <div class="controls">
                                      <button type="submit" class="btn btn-primary btn-small disabled">Speichern</button>
                                    </div>
                                  </div>
                                </form>
                                </div>
                           </div>

                        </div>
                                    <%--
<%-- ######################
    LAYOUT
    ######################
--%>
                        <%-- ================================================================ -->
                        <!-- 				Studentendetails 								  -->
                        <!-- ================================================================ --%>
                        <div id="divDaten" class="signUp_viewToggle" style="display:none">
                            
                        </div>                        


                        <%-- ================================================================ -->
                        <!-- 				Leistungen										  -->
                        <!-- ================================================================ --%>
                        <div id="divLeistungen" style="display:none"  class="signUp_viewToggle">
                            <h2>Leistungen</h2>
                            <table class="table table-striped tableSorter" id="tLeistungen">
                                <thead>
                                    <tr>
                                <th>Leistung</th>
                                <th>Note</th>
                                <th>Datum</th>
                                    </tr>
                                </thead>
                                <tbody>
                                </tbody>
                            </table> 
                        </div>
                        <%-- ================================================================ -->
                        <!-- 				Anmeldungen										  -->
                        <!-- ================================================================ --%>
                        <div id="divAnmeldungen" style="display:none" class="signUp_viewToggle">
                            <h2>Anmeldungen</h2>
                            <table class="table table-striped" id="tAnmeldungen">
                                <thead>
                                <th>Angemeldet zu</th>
                                <th>Datum</th>
                                <th><a title="Neue Anmeldung" href="#" id="cmdAnmeldungAdd" class="btn btn-info"><i class="icon-plus icon-white"></i></a></th>
                                </thead>
                                <tbody>
                                </tbody>
                            </table> 
                        </div>
                                        <%--
                    -----------------------------------------------
                    Modalfenster Allg.: Wirklich löschen?
                    ----------------------------------------------- 
                --%>	            
                <div class="modal hide fade signUp_viewToggle" id="modShj_SignUp_ConfirmDelete">
                    <div class="modal">
                        <div class="modal-header">
                            <a class="close" data-dismiss="modal">×</a>
                            <h3 class="text-warning shj_SignUp_ConfirmDelete_Header"></h3>
                        </div>
                        <div class="modal-body shj_SignUp_ConfirmDelete_Body"></div>
                        <div class="modal-footer">
                            <button class="btn btn-danger" data-loading="Wird gelöscht..." id="cmdShj_SignUp_ConfirmDelete"><u>J</u>a</button>
                            <button class="btn btn-warning" id="cmdShj_SignUp_ConfirmDelete_Cancel"><u>A</u>bbrechen</button>
                        </div>
                    </div>
                </div>
                                        <%--
                    -----------------------------------------------
                    Modalfenster Problem Antrag Transkript
                    ----------------------------------------------- 
                --%>	            
                <div class="modal hide fade" id="modAntragProblem" class="signUp_viewToggle">
                    <div class="modal">
                        <div class="modal-header">
                            <a class="close" data-dismiss="modal">×</a>
                            <h3 class="text-warning">Problem melden</h3>
                        </div>
                        <div class="modal-body">
                            <h2>Um welches Problem handelt es sich?</h2>
                            <ul><li><strong>Modulzuordnung stimmt nicht</strong>: wenn Leistungen falschen 
                                    Modulen zugeordnet sind, können Sie das entweder in der Sprechstunde 
                                    der Fachstudienberatung korrigieren lassen. Oder Sie schildern das 
                                    Problem ganz exakt in einer <a href="mailto:hier-email@angeben">E-Mail an die Fachstudienberatung</a>.
                                </li>
                                <li><strong>Ein Schein ist nicht in &quot;U<span style="color:red">:</span>P&quot; eingetragen</strong>: schicken Sie 
                                    in diesem Fall entweder eine E-Mail an den Aussteller des Scheins und bitten 
                                    Sie darum, die Note in &quot;U<span style="color:red">:</span>P&quot; nachzutragen. Oder (falls z.B. der Aussteller 
                                    nicht mehr in Heidelberg beschäftigt ist) Sie zeigen den Papierschein in 
                                    der Sprechstunde der Fachstudienberatung vor und lassen die Note in &quot;U<span style="color:red">:</span>P&quot;
                                    nachtragen.</li>
                                <li><strong>Persönliche Daten oder der Studiengang stimmen nicht</strong>: schicken 
                                    Sie in dem Fall eine <a href="mailto:hier-email@angeben">E-Mail mit Bitte um Korrektur an den Studienberater</a>
                        </div>
                        <div class="modal-footer">
                            <%-- <button class="btn btn-danger" data-loading="Wird gelöscht..." id="cmdShj_SignUp_ConfirmDelete"><u>J</u>a</button>
                            --%><button id="cmdModAntragProblemClose" class="btn btn-success"><u>O</u>K</button>
                        </div>
                    </div>
                </div>
                        
                        <%-- ================================================================ -->
                        <!-- 				Prüfungen								  -->
                        <!-- ================================================================ --%>
                        <div id="divPruefungen" style="display:none"  class="signUp_viewToggle">
                            <h2>Prüfungen</h2>
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
                        
                        <%-- ================================================================ -->
                        <!-- 				Studienbilanz								  -->
                        <!-- ================================================================ --%>
                        <div id="transkript" style="display:none" class="signUp_viewToggle">
                            <h2>Studienbilanz</h2>
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

                        
                        <%-- ================================================================ -->
                        <!-- 				Foto hochladen/ändern								  -->
                        <!-- ================================================================ --%>
                        <div class="modal hide fade" id="modIMGUpload" class="signUp_viewToggle">
                            <div class="modal">
                                <form class="form-horizontal" id="fFotoUpload" method="POST" enctype="multipart/form-data">
                                    <div class="modal-header">
                                        <a class="close" data-dismiss="modal">×</a>
                                        <h3>Bild hochladen</h3>
                                    </div>
                                    <div class="modal-body">
                                        <div id="img_progress_bar" style="display:none" class="progress progress-striped active">
                                            <div class="bar" style="width: 0%;"></div>
                                        </div>
                                        <div id="img_wait" style="display:none" class="alert">
                                            <b>Warte auf Antwort vom Server...</b>
                                        </div>
                                <div class="fileupload fileupload-new" data-provides="fileupload">
                                  <div class="fileupload-new thumbnail" style="width: 200px; height: 150px;"><img id="studentPhoto_thumbnail" src="img/openclipart_dagobert83_user_icon.png" /></div>
                                  <div class="fileupload-preview fileupload-exists thumbnail" style="max-width: 200px; max-height: 150px; line-height: 20px;"></div>
                                  <div>
                                    <span class="btn btn-file"><span class="fileupload-new">Bild wählen</span><span class="fileupload-exists">Ändern</span><input type="file" id="fileImg" shj_allow="jpg" /></span>
                                    <a href="#" id="cmdResetIMG" class="btn fileupload-exists" data-dismiss="fileupload">Löschen</a>
                                  </div>
                                </div>
  
                                    </div>
                                    <div class="modal-footer">
                                        <input type="hidden" name="studentfoto" id="studentfoto" value="studentfoto" />
                                        <button href="#" id="cmdIMGUpload" class="btn btn-success"><i class="icon-upload icon-white"></i>Hochladen</button>
                                        <a href="#" id="cmdIMGCancel" class="btn btn-danger" data-dismiss="modal"><i class="icon-trash icon-white"></i> Abbrechen</a>
                                    </div>
                                </form>
                            </div>
                        </div> 
                        <!-- ================================================================ -->
                        <!-- 				END								  -->
                        <!-- ================================================================ -->
                        <%-- ================================================================ -->
                        <!-- 				Kurs suchen										  -->
                        <!-- ================================================================ --%>
                        <div class="modal hide fade" id="modKurssuche">
                            <div class="modal">
                                <div class="modal-header">
                                    <a class="close" data-dismiss="modal">×</a>
                                    <h3 id="lblKurssuche_Modulwahl">Kurssuche</h3>
                                </div>
                                <form class="form-horizontal" id="fKurssuche" name="fKurssuche" action="#">
                                    <div class="modal-body" id="Kurssuchformular">
                                        <fieldset>
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
                                            <th class="span3">Leistung</th>
                                            <th class="span3">Lehrende(r)</th>
                                            <th class="span4">Kurstitel</th>
                                            <th class="span2">Termin</th>
                                            </thead>
                                            <tbody>
                                            </tbody>
                                        </table>
                                    </div>
                                    <div class="modal-footer" id="divKurssucheAktion">
                                        <input type="hidden" id="bCommitment" value="" />
                                        <input type="submit" id="cmdSearch" class="btn btn-primary" value="Suchen" />
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- ================================================================ -->
                        <!-- 				END								  -->
                        <!-- ================================================================ -->      			

                    </div><!-- /singleStudent -->
                </div>
                </div>
            </div><!--/row--> 
        </div><!--/span--> 
    </div><!--/row--> 

    <hr> 

    <footer> 
        <p>&copy; shj-online 2013</p> 
    </footer> 
<div class="signUp_waitmsg-modal"><!-- Place at bottom of page --></div>
</div><!--/.fluid-container--> 

<!-- Le javascript
================================================== --> 
<!-- Placed at the end of the document so the pages load faster --> 
<!-- Placed at the end of the document so the pages load faster -->
<jsp:include page="fragments/libs-student.jsp"></jsp:include>

<script type="text/javascript">
$(document).ready(function() {
    initSignUp();    
});
</script>
    
</body> 
</html> 