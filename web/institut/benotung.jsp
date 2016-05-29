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
    <title>SignUp -- Lehrende</title> 
    <meta name="description" content="Prüfungsverwaltung"> 
    <meta name="author" content="Heiko Jakubzik"> 
 
    <!-- Le HTML5 shim, for IE6-8 support of HTML elements --> 
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]--> 
 
    <!-- Le styles --> 
    <jsp:include page="fragments/css.jsp" />
    <link href="css/bootstrap-fileupload.min.css" rel="stylesheet"> 
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
      .linked:hover{
		background-color:#ddd ! important;
		color:#000;
		cursor:pointer;          
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
        <div class="span10"> 
            <h2>Benotung</h2>
                <div id="divLeistungFilterOpts" class="span10 well well-small" style="margin-left:0px">
                        <!-- <div class="row-fluid"> -->
                        <div class="span5">
                        <form class="form-horizontal">
                            <div class="control-group">
                            <label class="control-label" for="cboLeistung">Leistung</label>
                            <div class="controls">
                                <select id="cboLeistung" class="span12">
                                    <option value="">bitte wählen</option>
                                </select>
                            </div>
                            </div>
                            <div class="control-group">
                            <label class="control-label" for="cboDozent">Aussteller [optional]</label>
                            <div class="controls">
                                <select id="cboDozent" class="span12">
                                    <option value="">--alle--</option>
                                </select>
                            </div>
                            </div>
                            <div class="control-group">
                            <label class="control-label" for="txtSemesterF">Kurs [optional]</label>
                            <div class="controls">
                                <select id="cboKurs" name="cboKurs" class="span12">
                                    <option value="" selected="selected">bitte wählen</option>
                                </select>
                            </div>
                            </div> 
                            <div class="control-group">
                            <label class="control-label" for="chkHASwitch">Leistungsart</label>
                            <div class="controls">
                                <select id="chkHASwitch" name="chkHASwitch" class="span12">
                                    <option value="0" selected="selected">k.A.</option>
                                    <option value="KL">Klausur</option>
                                    <option value="MP">mündliche Prüfung</option>
                                    <option value="HA">Hausarbeit</option>
                                </select>
                            </div>
                            </div> 
                        </form>
                        </div>
                        <div class="span5">
                         <form class="form-horizontal" id="fDownUpload" action="#">
                            <div class="control-group" id="cgTermin">
                            <label class="control-label" for="txtAusstellungsd">Ausstellungsdatum</label>
                            <div class="controls">
                                <div class="input"><input type="date" id="txtAusstellungsd" name="txtAusstellungsd" placeholder="2015-1-1" /></div>
                            </div>
                            </div> 
                            <div class="control-group">
                            <label class="control-label" for="cboSemester">Semester [optional]</label>
                            <div class="controls">
                                <select id="cboSemester" name="cboSemester" class="input-xlarge">
                                    <option value="" selected="selected">bitte wählen</option>
                                </select>
                            </div>
                            </div>
                            <div class="control-group">
                            <div class="controls">
                                <button id="cmdDownload" name="cmdDownload" class="btn btn-primary">Download (Excel)
                                </button>
                                <button id="cmdUpload" name="cmdUpload" class="btn btn-primary">Upload (Excel)
                                </button>
                            </div>
                            </div>
                         </form>
                        </div>
                        <!-- </div> -->
            </div>
          <div class="row-fluid">  
            <div class="span7">

                <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++ -->
                <!-- Box zum Auswählen des Antragstyps -->
                <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++ -->

                <div id="divAnmeldungen">
                </div>
                <div id="divExcelUpload" style="display:none">
                    <button class="btn btn-warning"
                            id="cmdUploadAct">Diese Noten jetzt speichern!</button><br /><br />
                    <table class="table table-striped" id="tblExcelUpload">
                        <tr>
                            <th>Matrikelnummer</th>
                            <th>Leistung</th>
                            <th>Note</th>
                            <th>SignUp</th>
                        </tr>
                    </table>
                </div>
      		   <!-- ================================================================ -->
      		<!-- 				END								  -->
      		<!-- ================================================================ -->   
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
                <%--
                    -----------------------------------------------
                    Modalfenster Allg.: Wirklich speichern?
                    ----------------------------------------------- 
                --%>	            
                <div class="modal hide fade" id="modShj_SignUp_ConfirmSave" class="signUp_viewToggle">
                    <div class="modal">
                        <div class="modal-header">
                            <a class="close" data-dismiss="modal">×</a>
                            <h3 class="text-warning shj_SignUp_Confirm_Header"></h3>
                        </div>
                        <div class="modal-body shj_SignUp_Confirm_Body">
                            
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-success cmdShj_SignUp_Confirm_Act" data-loading="Wird gespeichert..."><u>W</u>irklich speichern?</button>
                            <button class="btn btn-warning cmdShj_SignUp_ConfirmAct_Cancel"><u>A</u>bbrechen</button>
                        </div>
                    </div>
                </div> 
                
                <%-- ================================================================ -->
                <!-- 	Excel-Datei LADEN -->
                <!-- ================================================================ --%>
                <div class="modal hide fade" id="modXLSUpload" class="signUp_viewToggle">
                    <div class="modal fileinputs">
                        <form class="form-horizontal" id="fXLSUpload" method="POST" action="#"  enctype="multipart/form-data">
                            <div class="modal-header">
                                <a class="close" data-dismiss="modal">×</a>
                                <h3>Datei hochladen</h3>
                            </div>
                            <div class="modal-body">
                                <div id="xls_progress_bar" style="display:none" class="progress progress-striped active">
                                    <div class="bar" style="width: 0%;"></div>
                                </div>
                                <div id="xls_wait" style="display:none" class="alert">
                                    <b>Warte auf Antwort vom Server...</b>
                                </div>
                                <div id="cgXLSSuchen"> 
                                    <div class="fileupload fileupload-new" data-provides="fileupload">
                                        <div class="input-append">
                                            <div class="uneditable-input span3"><i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span></div><span class="btn btn-file"><span class="fileupload-new">XLSX wählen</span><span class="fileupload-exists">Ändern</span><input  id="fileXLS" type="file"></input></span><a href="#" class="btn fileupload-exists" id="xlsReset" data-dismiss="fileupload">Löschen</a>
                                        </div>
                                    </div>
                                </div>
                                <div id="cgXLS"> 
                                    <input id="pdf" class="span8" placeholder="Bezeichnung" style="display:none" name="pdf" class="input-xlarge"></input>
                                </div>
                            </div>
                            <%--
                            <div class="modal-footer">
                                <input type="hidden" name="dozentxls" id="dozentxls" value="dozentxls" />
                                <input type="submit" href="#" id="cmdXLSUpload" class="btn btn-success disabled"><i class="icon-upload icon-white"></i> </input>
                                <a href="#" id="cmdCancelXLS" class="btn btn-danger" data-dismiss="modal"><i class="icon-trash icon-white"></i> Abbrechen</a>
                            </div>
                            --%>
                        </form>
                    </div>
                </div>
 
                <%--
                    ############################################
                    Templates: 
                    ############################################
                --%>                
                <div style="display:none">
                    <div id="template_anmeldung" class="well span12 contains_item">

                        <span class="student pull-left linked span4"></span>
                        <span class="span6">
                        <select id="cboNote" class="input-medium cboNote">
                            <option value="" selected="selected">bitte wählen</option>
                        </select><br /><div class="shj_leistungsdetail" style="display:none">Hausarbeitsthema:</div>
                        <input class="input-medium shj_leistungsdetail" style="display:none; width:100%" placeholer="Hausarbeitstitel" type="text"></input>
                        </span>
                        <span class="span2">
                        <a href="#" class="shj_delete pull-right btn btn-mini btn-danger"><i class="icon-ban-circle"></i> Löschen</a>
                        </span>  
       

                    </div>
                    <table id="template_excel_upload">
                        <tr class="linked-tablerow">
                            <td class="col_matrikelnummer"></td>
                            <td class="col_leistung"></td>
                            <td class="col_note"></td>
                            <td class="col_signup"></td>
                        </tr>
                    </table>
                    <form id='studenten_akte' method='post' target="_blank" action='l-index.jsp'>
                        <input type='text' name='refer_matrikelnummer' id='refer_matrikelnummer' value=''></input>
                        <input type='text' name='register' id='refer_register' value='anmeldungen'></input>
                    </form>
 
                </div>
            </div>
            </div> 
             
          </div><!--/row--> 
        </div><!--/span--> 
      </div><!--/row--> 

      <hr> 
 
      <footer> 
        <p>&copy; shj-online 2013</p> 
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
    <script src="js/bootstrap-fileupload.min.js"></script>
    <script src="js/FileSaver.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.8.0/xlsx.core.min.js"></script>
    <script>
            // TODO
            // 
            // HOCHLADEN:
            // - die Anzahl der Excel-Zeilen sollte angegeben werden, und auch
            // - die Anzahl der erfolgreich gespeicherten Noten.
            // 
            // - Anstatt die Spalte "LeistungID" im Excel-Dokument anzugeben 
            //   sollte es auch möglich sein, eine Leistung aus der Cbo 
            //   auszuwählen.
            // 
            // (Beim Analysieren des Excel-Dokuments wird ohne weitere Warnung 
            //  abgebrochen, wenn eine Zeile fehlerhaft ist, z.B. ohne KursID.
            //  Deshalb habe ich nur die Hälfte der Writing Ergebnisse hoch-
            //  geladen).
            //  
            // - Nach dem Upload wäre es gut, wenn die Ergebnisse 
            //   heruntergeladen werden könnten. So kann man nachweisen, was 
            //   man tatsächlich hochgeladen hat.
            //   
            // - Es braucht eine Art Fortschrittsbalken beim Loop mit 
            //   Ajax, also beim tatsächlichen Speichern auf dem Server. Sonst 
            //   ist das verwirrend.
            //   
            // - Die Spaltenbezeichnungen des Excel-Dokuments müssen dokumentiert
            //   sein
            //   
            // 
            // FILTERN:
            // - falls Aussteller gewählt, Liste der Kurse/Semester einschränken
            // 
            // cboDozent muss deaktiviert werden, falls AccessLevel<500
            // 
            // Check security: wenn die Anmeldungen von einer Person mit 
            // Access-Level=1 aufgelistet werden und die Studierendenakte
            // angewählt wird -- ist das okay?                          ........ 25.9.2013 getestet: OK
            
            /**
             * benotung.jsp
             * 
             * @USER
             * Hier können Anmeldungen aufgelistet und gefiltert 
             * werden (z.B. alle Proseminare Literaturwissenschaft eines 
             * bestimmten Semesters oder Ausstellers).
             * 
             * Für all diese Anmeldungen kann dann ein Ausstellungsdatum
             * eingegeben werden; auch der Leistungstyp (Hausarbeit, mündliche
             * Prüfung oder Klausur) kann an _einer_ zentralen Stelle
             * festgelegt werden.
             * 
             * Das Ausstellen der Leistung funktioniert dann durch Eingabe 
             * der Note.
             * 
             * Außerdem gibt es die Möglichkeit, die Ausstellungsdaten 
             * als Excel-Datei (.xslx) herunter- und hochzuladen.
             * 
             * @CODER
             * 
             */
            
    $(document).ready(function() {
            // User init
            var me = {};
            var g_iDEBUG_LEVEL=6;   // 6 = ALL
                                    // 5 = DEBUG
                                    // 4 = INFO
                                    // 3 = WARNING
                                    // 2 = ERROR
                                    // 0 = SHUT UP
            
            // *****************************************************************
            // Initialisierung 
            // *****************************************************************
            $.signUpGlobal.seminar_id = $('#logged-in-user-seminar').val();
            $.signUpGlobal.info={};
            $.signUpGlobal.info.seminar={};
            
            var g_kurse=[];
            var g_sKursIDs="";

            // Das stellt die Ajax Wartemitteilung an.
            $('body').on({
                ajaxStart: function() {
                    $(this).addClass('loading').delay(1500).queue(function(next){
                        if($(this).hasClass('loading')){
                            $(this).removeClass('loading');
                        }
                        next();
                    });
                },
                ajaxStop: function() {
                    $(this).removeClass('loading');
                }
            });
                
            /**
             * Anzeige: wird als Leistungstyp "Hausarbeit" gewählt,
             * wird überall das Input-Feld eingeblendet, um ein 
             * HA-Thema eingeben zu können.
             */
            $('#chkHASwitch').on('click', function(){
               if($(this).val()=='HA') $('.shj_leistungsdetail').show(); //optLeistungsdetails
               else $('.shj_leistungsdetail').hide(); //optLeistungsdetails
            });
            
            // "Exportiere" Excel-Dokument
            $('#cmdDownload').on('click',function(){
               exportExcel();
            });
            
            // #################################################################
            // :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
            // -----------------------------------------------------------------
            // A: THEMA: Noten aus Excel-Datei in SignUp speichern
            // 
            //    Die Datei wird lokal durch den Browser analysiert,
            //    dann zeilenweise über student/leistung/add-bulk.jsp
            //    hochgeladen.
            //    
            //    ACHTUNG: Note 800 (unbenotet bestanden) wird hart-
            //             kodiert mit der ID 13 umkodiert
            // -----------------------------------------------------------------
            // :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
            // #################################################################
            
            // *****************************************************************
            // Lade Excel File (lokal, kein Serverkontakt)
            // *****************************************************************
            $('#fDownUpload').off();
            $('#fDownUpload').on('submit', function(e){e.preventDefault();return true;});
            xlsUploadResetView();
            
            // Öffne Modalfenster zur Dateiauswahl, 
            // falls das Ausstellungsdatum eingegeben ist.
            // Sonst: erzwinge erst Auswahl.
            $('#cmdUpload').on('click', function(){
                if(isPlausibleIssueDate()===false){
                    $('#txtAusstellungsd').focus();
                    alert('Geben Sie bitte zuerst ein gültiges Ausstellungsdatum ein!');
                    return;
                }
                $('#modXLSUpload').modal('show');
                $('input[type=file]').off();
                $('input[type=file]').change(function(e) {      // Validiere neu ausgewählte
                    var sPath = $('input[type=file]').val();    // Datei (Foto oder PDF)
                    if (sPath.length < 4)
                        return;
                    if ((sPath).substr(sPath.length - 5).toLowerCase() != ".xlsx") {
                        alert("Die Datei muss die Endung .xlsx haben.");
                    } else {
                        $('#divLeistungFilterOpts').fadeOut();
                        $('#modXLSUpload').modal('hide');
                        $('#divExcelUpload').show();
                        $('#cmdUploadAct').off();
                        $('#cmdUploadAct').on('click', function(){confirm_uploadExcelFile();});
                        loadExcelFile(e);
                    }
                });
            });

            /**
             * Ist das Ausstellungsdatum plausibel 
             * (d.h. weniger als zwei Jahre her und liegt 
             * nicht in der Zukunft?)
             * @returns {boolean}
             */
            function isPlausibleIssueDate(){
                var oReturn={};
                if($('#txtAusstellungsd').val().length<5) return false;
                
                var dtm=new Date($('#txtAusstellungsd').val());
                var dRef=new Date();
                
                if(dtm.getYear()<(dRef.getYear()-2)) return false;
                if(dtm.getTime()>dRef.getTime()) return false;
                oReturn.datum_de=dtm.getDate() + '.' 
                            + (parseInt(dtm.getMonth())+1)  + '.' + dtm.getFullYear();
                return oReturn;
            }
            
            function confirm_uploadExcelFile(){
                shj.signUp.toolbox.confirm(
                    '#modShj_SignUp_ConfirmSave',
                    'Diese Noten jetzt hochladen?',
                    'Sollen die aufgelisteten Noten jetzt '
                    + 'mit dem Ausstellungsdatum >' 
                    + isPlausibleIssueDate().datum_de
                    + '< in '
                    + 'SignUp gespeichert werden?',
                    function() {
                        uploadExcelFile();
                    },
                    function(){
                        // Abbruch
                    });
            }
            
            /**
             * Liefert die NotenID der Klartext-Note "sNote".
             * (Z.B. liefert sNote="1,3" den Wert 2;
             *  insbes. liefert sNote="800" den Wert 13.
             *  ACHTUNG: 800 zu 13 ist hartkodiert!
             *           Insofern taugt das NUR FÜR DIE ANGLISTIK HD)
             * @returns ID der Note, 13 bei 800, im Zweifel -1
             */
            function getNotenID(sNote){
                var oReturn={};
                oReturn.id=-1;
                oReturn.bestanden=false;
                for(var ii=0,ij=$.signUpGlobal.info.seminar.noten.noten.length;ii<ij;ii++){
                    if($.signUpGlobal.info.seminar.noten.noten[ii].wert == sNote){
                        oReturn.id = $.signUpGlobal.info.seminar.noten.noten[ii].id;
                        oReturn.bestanden=$.signUpGlobal.info.seminar.noten.noten[ii].bestanden;
                        return oReturn;
                        break;
                    }
                    if(sNote=='800'){
                        oReturn.id=13;
                        oReturn.bestanden=true;
                        break;     
                    }
                }
                return oReturn;
            }
            
            /** 
             * Durchläuft die Zeilen der Info aus der 
             * Excel-Datei und lädt die einzelnen Noten 
             * auf den Server.
             * 
             * Dazu wird die Sonderfkt. student/leistung/add-bulk.jsp
             * verwendet, die wiederum die Sonderfkt. 
             * StoredProc "forceCommitment" verwendet, um 
             * ggf. eine passende Anmeldung zu erzeugen.
             * 
             * Zeilen, die bei Aufruf als Fehler (.error) 
             * markiert sind, werden übersprungen.
             * 
             * Es wird nur hochzuladen versucht, wenn die 
             * Note erkannt wurde (siehe .getNotenID()).
             * @returns {void}
             */ 
            function uploadExcelFile(){
                // Iteriere Tabellenzeilen
                var ii=0;
                $('#tblExcelUpload tr').each(function(){
                    if(!$(this).hasClass('error')&&ii>0){
                        var $this=$(this).data('shj_item');
                        var $row=$(this);

                        var sending={};
                        sending.matrikelnummer=$this.matrikelnummer;
                        sending.leistung_id=$this.leistung_id;
                        sending.noten_id=getNotenID($this.note).id;
                        sending.note_bestanden=getNotenID($this.note).bestanden;
                        sending.datum=isPlausibleIssueDate().datum_de;
                        sending.kurs_id=$this.kurs_id;
                        if(sending.noten_id==-1){
                            $row.addClass('error');
                            $row.find("td:nth-child(4)").text('Fehler: ' 
                                    + 'Note >' + $this.note 
                                    + '< ist nicht bekannt.');
                        }else{
                            $.ajax({
                                data:sending,
                                url: 'json/student/leistung/add-bulk.jsp', //server script to process data
                                type: 'POST',
                                success: function(answer) {
                                    if(answer.success=='true'){
                                        $row.addClass('success')
                                        $row.find("td:nth-child(4)").text('OK');
                                    }
                                },
                                error: function(a, b, c) {
                                        $row.addClass('error');
                                        $row.find("td:nth-child(4)").text('Fehler: ' 
                                                + $.parseJSON(a.responseText).error);
                                },
                                cache: false,
                                timeout: 20000,
                                contentType: "application/x-www-form-urlencoded;charset=utf8",
                                dataType: 'json'
                            });
                        }
                    }
                    ii++;
                });   
            }
            
            /**
             * Stellt das hochgeladene Worksheet im HTML-Format dar, 
             * (das im Fall der Auslösung dann mit Erfolgs- und 
             * Fehlermeldungen versehen werden kann).
             * 
             * Matrikelnummer | Leistung | Note  | SignUp
             * 
             * 
             * Dabei wird eine Zeile rot (.error) markiert,
             * wenn die Note fehlt oder sonstige Fehler 
             * bereits ohne Serverkontakt feststellbar sind.
             * 
             * Die Zeile (tr) hat die id=<matrikelnummer>-<kurs-id> 
             * (ohne spitze Klammern)
             * 
             * Die Zeilen enthalten außerdem das data-Element 
             * 'shj_item' mit den Eigenschaften:
             *      .leistung_id
             *      .note
             *      .kurs_id
             *      .matrikelnummer
             * 
             * @returns {undefined}
             */
            function displayExcelUpload(oWorkbook){
                var aMapper=mapExcelCols(oWorkbook);
                g_sKursIDs=";";
                if(aMapper==null){
                    alert('Sorry, das Excel-Dokument konnte nicht analysiert werden.');
                    return null;
                }
                var aTbl=[];

                // Es kann derzeit nur zu einer Leistung
                // hochgeladen werden:
                var iLeistung=parseInt(oWorkbook[aMapper.col_leistung_id+2].v);
                var bContinue=true,ii=2,sLeistungBezeichnung=getLeistungBez(iLeistung);

                // Arbeite die Zeile des geladenen
                // Excel-Dokuments ab:
                while(bContinue){
                    if(oWorkbook[aMapper.col_matrikelnummer+(ii+1)]===undefined) bContinue=false;
                    
                    var tmp=$('#template_excel_upload tbody').clone();
                    var tmp_id=oWorkbook[aMapper.col_matrikelnummer+ii].v + '-'
                            + oWorkbook[aMapper.col_kurs_id+ii].v;
                    tmp.find('tr').attr('id', tmp_id);
                    
                    var shj_data={};
                    shj_data.matrikelnummer=oWorkbook[aMapper.col_matrikelnummer+ii].v;
                    shj_data.kurs_id=('' + oWorkbook[aMapper.col_kurs_id+ii].v).trim();
                    
                    tmp.find('.col_matrikelnummer').text(oWorkbook[aMapper.col_matrikelnummer+ii].v);

                    if(oWorkbook[aMapper.col_leistung_id+ii]!==undefined){
                        if(oWorkbook[aMapper.col_leistung_id+ii].v!=iLeistung) throw Exception('Nö');
                        tmp.find('.col_leistung').text(sLeistungBezeichnung);
                        shj_data.leistung_id=oWorkbook[aMapper.col_leistung_id+ii].v;
                    }else{
                        tmp.find('.col_leistung').text('--').parents('tr').addClass('error');
                        tmp.find('td:nth-child(4)').text('Angabe der Leistung fehlt.');
                    }
                    if(oWorkbook[aMapper.col_note+ii]!==undefined){
                        tmp.find('.col_note').text(oWorkbook[aMapper.col_note+ii].v);
                        shj_data.note=oWorkbook[aMapper.col_note+ii].v;
                    }else{
                        tmp.find('.col_note').text('--').parents('tr')
                                 .addClass('error')
                                 .find('td:nth-child(4)').text('Angabe der Note fehlt.');
                    }
                    ii++;
                    tmp.find('tr').data('shj_item',shj_data)
                            .on('click', function(){
                                window.open('./l-index.jsp?refer_matrikelnummer='
                                    + $(this).data('shj_item').matrikelnummer 
                                    + '&register=anmeldungen', '_blank');
                            });
                    
                    $('#tblExcelUpload').append(tmp);

                    tmp=null;
                }
                $('#divExcelUpload').show();
            }      
            
            // #################################################################
            // :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
            // -----------------------------------------------------------------
            // B: THEMA: Anmeldungen als Excel-Datei herunterladen
            // 
            //    Die Anmeldungen liegen lokal als Array vor 
            //    und werden (lokal, durch den Client) in eine 
            //    Excel-Datei aufbereitet und exportiert.
            //    
            // -----------------------------------------------------------------
            // :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
            // #################################################################
            
            
            // *****************************************************************
            // Produziere Excel aus Array zum "Download"
            // *****************************************************************

            /**
             * Stelle eine Excel-Datei mit 
             * Matnr. | Name Stud | KursID | LeistungID | Aussteller | Note | Leistung Name
             * zuammen und pusht sie per .saveAs an den Browser.
             * 
             * In diese Datei soll man die Note eintragen, um sie dann 
             * wieder hochladen zu können.
             * 
             * Die gefilterten Anmeldungen werden vor dem Download 
             * noch nach Namen der Studierenden alphabetisch sortiert.
             */
            function exportExcel(){

                // Hole die Anmeldungen des derzeit
                // vom Nutzer eingestellten Filters:
                var aAnmeldungen=applyFilter();
                var ws={};
                
                // Name der Leistung für den Excel-Export
                var sLeistung=$('#cboLeistung option:selected').text();

                var range = {s: {c:10000000, r:10000000}, e: {c:0, r:0 }};

                /** Init: Excel Tabellenüberschriften */
                setZelle(ws, range, 0, 0, 'Matrikelnummer');
                setZelle(ws, range, 0, 1, 'Studierende(r)');
                setZelle(ws, range, 0, 2, 'KursID');
                setZelle(ws, range, 0, 3, 'LeistungID');
                setZelle(ws, range, 0, 4, 'Aussteller');
                setZelle(ws, range, 0, 5, 'Note');
                setZelle(ws, range, 0, 6, 'Leistung');
                
                // Sortiere nach dem Nachnamen der Studierenden
                aAnmeldungen.sort(function(a,b){
                   return ((a.student_nachname <= b.student_nachname) ? -1 : 1);
                });
                
                for(var ii=0, len=aAnmeldungen.length; ii<len; ii++){
                    setZelle(ws, range, ii+1, 0, aAnmeldungen[ii].matrikelnummer);
                    setZelle(ws, range, ii+1, 1, aAnmeldungen[ii].student_nachname 
                            + ' ' + aAnmeldungen[ii].student_vorname);
                    setZelle(ws, range, ii+1, 2, aAnmeldungen[ii].kurs_id);
                    setZelle(ws, range, ii+1, 3, aAnmeldungen[ii].id);
                    setZelle(ws, range, ii+1, 4, aAnmeldungen[ii].aussteller_nachname
                            + ', ' + aAnmeldungen[ii].aussteller_vorname);
                    setZelle(ws, range, ii+1, 6, sLeistung);
                } 

                if(range.s.c < 10000000) ws['!ref'] = XLSX.utils.encode_range(range);

                function Workbook() {
                        if(!(this instanceof Workbook)) return new Workbook();
                        this.SheetNames = [];
                        this.Sheets = {};
                }

                var wb = new Workbook();

                /* Füge worksheet zum workbook hinzu */
                wb.SheetNames.push('Noten');
                wb.Sheets['Noten'] = ws;

                /* Schreibe Datei */
                var wbout = XLSX.write(wb, {bookType:'xlsx', bookSST:false, type: 'binary'});
                function s2ab(s) {
                  var buf = new ArrayBuffer(s.length);
                  var view = new Uint8Array(buf);
                  for (var i=0; i!=s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;
                  return buf;
                }

                /* FileSave.js */
                saveAs(new Blob([s2ab(wbout)],{type:""}), "Noten.xlsx");
            }
            
            // #################################################################
            // :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
            // -----------------------------------------------------------------
            // C: THEMA: Darstellung der Anmeldungen im Browser
            // 
            //    
            // -----------------------------------------------------------------
            // :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
            // #################################################################
            
            /**
             * Darstellg. der Anmeldungen vom Server (von json/seminar/anmeldung/get.jsp)
             * 
             * Beim Iterieren werden gleich die Auswahlboxen 
             * für die Kurse und Semester befüllt.
             * 
             * @param {boolean} bFirst: true, wenn die von den Anmeldungen 
             * abhängigen Arrays und Cbos berechnet werden sollen (z.B. 
             * die Kurse, Dozenten, Semester).
             * @returns {undefined}
             */
            function _renderAnmeldungen(bFirst){
                var tmp=';'; var tmpS=';';var tmpD=';';var kurse=[];var semester=[];var dozenten=[];
                var aGewaehlteAnm=applyFilter();

                kurse.push({"id":"-1","name":"-- ALLE --"});
                semester.push({"id":"-1","name":"-- ALLE --"});
                dozenten.push({"id":"-1","name":"-- ALLE --"});

                
                // Lade die Vorlage zur Darstellung einer 
                // einzelnen Anmeldung
                var template_general = $('#template_anmeldung').clone();
                template_general.removeAttr('id');
                template_general.find('#cboNote').removeAttr('id');
                
                // Lösche die derzeitige Anzeige
                $('#divAnmeldungen').empty();
                
                // Durchlaufe die Anmeldungen zur gewählten Leistung,
                // und
                // (1) Befülle die Kurs-Auswahlbox (über das Array kurse),
                // (2) Befülle die Semester-Auswahlbox (über das Array semester),
                for(var ii=0,jj=aGewaehlteAnm.length; ii<jj; ii++){
                    
                    // Befülle Kurse
                    if (tmp.indexOf(';' + aGewaehlteAnm[ii].kurs_id + ';') < 0) {
                        tmp += aGewaehlteAnm[ii].kurs_id + ';';
                        kurse.push({"id":aGewaehlteAnm[ii].kurs_id,"name":aGewaehlteAnm[ii].kurstitel});
                    }

                    // Befülle Dozenten
                    var oTmpDozent=getDozentByName(aGewaehlteAnm[ii].aussteller_vorname 
                            + ' ' 
                            + aGewaehlteAnm[ii].aussteller_nachname);
                    console.log(oTmpDozent);
                    if (tmpD.indexOf(';' + oTmpDozent.id + ';') < 0) {
                        tmpD += ';' + oTmpDozent.id + ';';
                        dozenten.push({"id":oTmpDozent.id, "name":oTmpDozent.vorname
                            + ' ' 
                            + oTmpDozent.name, "sort_name":oTmpDozent.name+oTmpDozent.vorname});
                    }

                    // Befülle Semester
                    if (tmpS.indexOf(';' + aGewaehlteAnm[ii].kurs_semester + ';') < 0) {
                        tmpS += aGewaehlteAnm[ii].kurs_semester + ';';
                        semester.push({"id":aGewaehlteAnm[ii].kurs_semester,"name":aGewaehlteAnm[ii].kurs_semester});
                    }
                    
                    var template=template_general.clone();

                    // Stelle Werte dar
                    template.find('.student').text(aGewaehlteAnm[ii].student_vorname + ' ' + aGewaehlteAnm[ii].student_nachname);
                    template.data('shj_item', aGewaehlteAnm[ii]);
                    template.find('.student').on('click', function(){showAnmeldungen($(this).parents('div').data('shj_item'))});
                    template.find('.shj_delete').on('click', function(){dropAnmeldung($(this).parents('div').data('shj_item'))});
                    template.find('.cboNote').on('change', function(){updateAnmeldung($(this).parents('div').data('shj_item'), $(this).val(), $(this).parents('div').find('input').val());});
                    template.attr('id', 'anmeldung-' + aGewaehlteAnm[ii].id + '-' + aGewaehlteAnm[ii].matrikelnummer);

                    // Hänge in DOM ein
                    $('#divAnmeldungen').append(template);

                }
                
                // Lade die Kurse und Semester in die Auswahlboxen
                $('#cboKurs').empty();
                shj.signUp.toolbox.populateCbo(kurse, {"identifyer":"id","text":"name","cboIdentifyer":"#cboKurs"});
                shj.signUp.toolbox.populateCbo(semester, {"identifyer":"id","text":"name","cboIdentifyer":"#cboSemester"});

                // sortiere Dozenten alphabetisch
                dozenten.sort(function(a, b){
                    if(a.id==-1) return -1;
                    return (a.sort_name<=b.sort_name ? -1 : 1);
                });
                
                var iTmp=$('#cboDozent').val();
                $('#cboDozent').off();
                $('#cboDozent').empty();
                shj.signUp.toolbox.populateCbo(dozenten, {"identifyer":"id","text":"name","cboIdentifyer":"#cboDozent"});
                if(iTmp===null || isNaN(iTmp)) iTmp=-1;
                $('#cboDozent').val(iTmp);
                
                // Wird ein Kurs auswgewählt, soll das
                // entsprechende Semester in der Semester
                // Auswahlbox angezeigt werden; 
                // wird umgekehrt ein Semester gewählt,
                // soll die aktuelle Anzeige der Kurse
                // auf "Alle" zurückgesetzt werden.
                $('#cboKurs').on('change', function(){
                    var self=$(this);
                    // Wähle das Semester des gewählten Kurses
                    $('#cboSemester').val(getAnmeldungByKurs(self.val()).kurs_semester);

                    // Filtere die Anmeldungen:
                    $('#divAnmeldungen div.contains_item').each(function(){
                        if(self.val()>0 && $(this).data('shj_item').kurs_id !== self.val()) $(this).hide();
                        else $(this).show();
                    });
                });
                $('#cboSemester').on('change', function(){
                    var self=$(this);
                    $('#cboKurs').val(-1);  // Setze Kurs DropDown auf "Alle"
                    // Filtere die Anmeldungen:
                    $('#divAnmeldungen div.contains_item').each(function(){
                        if(self.val()!='-1' && $(this).data('shj_item').kurs_semester != self.val()) $(this).hide();
                        else $(this).show();
                    })
                });
                $('#cboDozent').on('change', function(){
                   _renderAnmeldungen();
                   return;
                   var self=$(this);
                   // @TODO: die Cbo's von Semester und Kurs 
                   // zurücksetzen?
                   $('#divAnmeldungen div.contains_item').each(function(){
                       var lID=getDozentByName($(this).data('shj_item').aussteller_vorname 
                               + ' ' 
                               + $(this).data('shj_item').aussteller_nachname).id;
                       console.log('#check if ' + lID + ' = ' + self.val());
                       if(self.val()!='-1' && lID != self.val()) $(this).hide();
                   });
                });
            }
            
              /**
               * Lädt die Benotete Anmeldung nach Rückfrage als Leistung auf den Server 
               * (student/anmeldung/update.jsp --> student/anmeldung/leistung/update.jsp)
               * 
               * @param {type} anmeldung
               * @param {type} note
               * @param {type} ha_thema
               * @returns {unresolved}
               */
              function updateAnmeldung(anmeldung, note_id, ha_thema){

                // ----------------------------------------------------------------------------
                // Plausibilitätstests
                // (1) Ausstellungsdatum eingegeben?
                if($('#txtAusstellungsd').val().length<5){
                    $('#txtAusstellungsd').focus();
                    alert('Geben Sie bitte ein gültiges Ausstellungsdatum ein.');
                    return;
                }
                
                // (2) HA-Thema angegeben, falls HA als Leistungstyp spezifiziert?
                if($('#chkHASwitch').val()=='HA' && (ha_thema==null || ha_thema=='')){
                    alert('Geben Sie bitte ein Hausarbeitsthema an oder wählen Sie eine andere "Leistungsart".');
                    return;
                    
                }
                // Stelle Rückfrage "Wirklich?" und lade dann ggf. hoch
                var tmpNote=$.signUpGlobal.info.seminar.noten.get(note_id); // Notendetails für Rückfrage

                shj.signUp.toolbox.confirm(
                    '#modShj_SignUp_ConfirmSave',
                    'Schein speichern?',
                    anmeldung.student_vorname + ' ' + anmeldung.student_nachname + ' bekommt <br />' + 
                            'die Note ' + tmpNote.name + ' (' + tmpNote.wert + ')?',
                    function() {
                        // Rückfrage "Wirklich" wurde abgenickt:
                        // Jetzt wird das hochzuladende JSON
                        // zusammengestellt, aus
                        // - der zugrundeliegenden Anmeldung,
                        // - dem Ausstellungsdatum,
                        // - der NotenID,
                        // - den Informationen zu Leistungsart bzw. Hausarbeit
                        var d=new Date($('#txtAusstellungsd').val());
                        var zu_benotende_anmeldung=new shj.signUp.student.Anmeldung(anmeldung.matrikelnummer,anmeldung);
                        zu_benotende_anmeldung.noten_id=note_id;
                        zu_benotende_anmeldung.datum=d.getDate() + '.' + (d.getMonth()+1) + '.' + d.getFullYear();
                        if($('#chkHASwitch').val()!='false') zu_benotende_anmeldung.leistungstyp=$('#chkHASwitch').val();
                        if($('#chkHASwitch').val()=='HA') zu_benotende_anmeldung.details=ha_thema;
                        
                        // Serverkontakt und
                        // nehme im Erfolgsfall
                        // diese Anmeldung aus dem 
                        // Array curr_anmeldungen heraus.
                        zu_benotende_anmeldung.update(function(){
                            for(var ii=0;ii<$.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen.length;ii++){
                                if($.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen[ii].anmeldung.id==anmeldung.anmeldung.id && $.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen[ii].anmeldung.count==anmeldung.anmeldung.count){
                                    $.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen.splice(ii, 1);
                                    break;
                                }
                            }
                            // Blende im Erfolgsfall diese Anmeldung aus.
                            var tmp_id='anmeldung-' + anmeldung.id + '-' + anmeldung.matrikelnummer;
                            $('#'+tmp_id).slideUp(400);
                    });
                    },
                    function(){
                         //Nicht geändert, nothing happened
                    });
              }; // [end of updateAnmeldung]
            
             /**
              * Löscht die Anmeldung nach Rückfrage
              * und zeichnet neu.
              * @param {type} anmeldung
              * @returns {undefined}
              */
              function dropAnmeldung(anmeldung){
                shj.signUp.toolbox.confirmDelete(
                    '#modShj_SignUp_ConfirmDelete',
                    'Anmeldung löschen?',
                    'Soll diese Anmeldung von ' + anmeldung.student_vorname + ' ' + anmeldung.student_nachname + ' wirklich unwiderruflich gelöscht werden?',
                    function() {
                        var delete_me=new shj.signUp.student.Anmeldung(anmeldung.matrikelnummer,anmeldung.anmeldung);
                        delete_me.drop(function(){
                        for(var ii=0;ii<$.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen.length;ii++){
                            if($.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen[ii].anmeldung.id==anmeldung.anmeldung.id && $.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen[ii].anmeldung.count==anmeldung.anmeldung.count){
                                $.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen.splice(ii, 1);
                                break;
                            }
                        }
                        var tmp_id='anmeldung-' + anmeldung.id + '-' + anmeldung.matrikelnummer;
                        $('#'+tmp_id).slideUp(1000);
                    });
                    },
                    function(){
                         //Nicht gelöscht, nothing happened
                    });
              }
            
            /**
             * Wendet den über die Cbos vom Nutzer eingestellten 
             * Filter (nach Dozent, Semester, Kurs, Leistung)
             * auf "curr_anmeldungen" an und liefert die Treffer 
             * als Array zurück.
             * 
             * @TODO: derzeit wird nur nach Dozent gefiltert.
             * 
             * @Verwendet von: matrikelnummer
             * 
             * * _renderAnmeldungen()
             * * _exportXLS()
             * 
             * @returns {undefined}
             */
            function applyFilter(){
                var aReturn=[];
                var lDozentID=parseInt($('#cboDozent').val());
                for(var ii=0,jj=$.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen.length; ii<jj; ii++){
                    // Es werden nur die Templates des ausgewählten Dozenten
                    // eingehangen:
                    var bShow = (isNaN(lDozentID) || lDozentID===-1 || 
                            lDozentID===parseInt(getDozentByName($.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen[ii].anmeldung.aussteller_vorname 
                                   + ' ' 
                                   + $.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen[ii].anmeldung.aussteller_nachname).id)
                            );
                    
                    if(bShow) aReturn.push($.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen[ii].anmeldung);
                }
                return aReturn;
            }
            
            // Input: Vorname Leerzeichen Nachname
            function getDozentByName(sName_IN){
                for(var ii=0,jj=$.signUpGlobal.info.seminar.dozenten.dozenten.length;ii<jj;ii++){
                    if($.signUpGlobal.info.seminar.dozenten.dozenten[ii].vorname 
                            + ' ' 
                            + $.signUpGlobal.info.seminar.dozenten.dozenten[ii].name == sName_IN){
                        return $.signUpGlobal.info.seminar.dozenten.dozenten[ii];
                        break;
                    }
                }
                return {};
            }
            
            /**
             * Es wurde eine Leistung ausgewählt (z.B. "Proseminar Literatur").
             * Lade von /seminar/anmeldungen/get.jsp alle Anmeldungen zu 
             * dieser Leistung und dieser/m Lehrenden.
             */
            $('#cboLeistung').on('change', function(){
                var sending={};
                sending.leistung_id=$('#cboLeistung').val();                
                sending.dozent_id='*';
                
                // Setze die derzeitige Anzeige zurück
                $('#cboKurs').empty();
                $('#cboSemester').empty();
                $('#cboDozent').empty();
                
                $.ajax({
                    data:sending,
                    url: 'json/seminar/anmeldung/get.jsp', //server script to process data
                    type: 'POST',
                    success: function(answer) {
                        if(answer.anmeldungen.length<=0){ alert('Keine Anmeldungen gefunden!'); return;}
                        $.signUpGlobal.info.seminar.curr_anmeldungen=answer;
                        _renderAnmeldungen(true);
                    },
                    error: function(a, b, c) {
                        alert('Fehler beim Versuch, die Anmeldungen zu diesem Kurstyp herunterzuladen');
                    },
                    cache: false,
                    timeout: 20000,
                    contentType: "application/x-www-form-urlencoded;charset=utf8",
                    dataType: 'json'
                });
                 // alert('program me ... done');
              });
              
              /**
               * Öffnet die Anmeldung im Browser
               * per Verweis auf .index.
               * Würde man die Seiten zusammenführen, 
               * ließe sich das sauberer lösen.
               * @param {type} anmeldung
               * @returns {nothing}               */
              function showAnmeldungen(anmeldung){
                $('#refer_matrikelnummer').val(anmeldung.matrikelnummer);
                $('#studenten_akte').submit();
              }
            
            /** 
             *°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
             * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
             * BORING CODE
             * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
             * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
             **/

            /**
             * Helfer: für den Export der Modulnoten als Excel-Dokument 
             * werden hier Zellen gesetzt.
             * 
             * oWorksheet und range werden byReference übergeben.
             */
            function setZelle(oWorksheet, range, iZeile, iSpalte, sWert){
                var cell = {v: sWert};
                var cell_ref = XLSX.utils.encode_cell({c:iSpalte,r:iZeile});
                cell.t = 's';
                oWorksheet[cell_ref] = cell;
                        if(range.s.r > iZeile) range.s.r = iZeile;
                        if(range.s.c > iSpalte) range.s.c = iSpalte;
                        if(range.e.r < iZeile) range.e.r = iZeile;
                        if(range.e.c < iSpalte) range.e.c = iSpalte;
            }
            
            /**
             * Reaktion auf Auswahl der Excel-Datei durch Nutzer
             * nach Klick auch den Button "Upload".
             * 
             * Ruft .displayExcelUpload(workbook) auf.
             * @returns {undefined}
             */
            function loadExcelFile(e){
                var files = e.target.files;
                var i,f;
                for (i = 0, f = files[i]; i != files.length; ++i) {
                  var reader = new FileReader();
                  var name = f.name;
                  reader.onload = function(e) {
                    var data = e.target.result;
                    var workbook = XLSX.read(data, {type: 'binary'});
                    //displayExcelUpload(workbook.Sheets['Noten']);
                    displayExcelUpload(workbook.Sheets[workbook.SheetNames[0]]);
                  };
                  reader.readAsBinaryString(f);
                }
            }
            
            /**
             * Setze PDFUploadAnsicht zurück
             * @returns {undefined}
             */
            function xlsUploadResetView(){
                $('#xlsReset').click();         // FileUpload Formularfeld zurücksetzen
                $('#xls_progress_bar').hide();  
                $('#xls_progress_bar').find('.bar').attr("style", "width:0%");
                $('#xls').val('').hide();       // Name des Downloads
                $('#cgXLSSuchen').show();       
                $('#cmdXLSUpload').addClass('disabled').show();
                $('#cmdCancelXLS').show();
                $('#xls_wait').hide();
            }
            
            /**
             * Gibt zur ID die Bezeichnung der Leistung zurück.
             * 
             * Wird anhand der ID keine Leistung gefunden, 
             * wird "Leistung <ID>: unbekannt" zurückgegeben.
             * @param {int} iLeistungID
             * @returns {String} Name der Leistung, oder 
             * "Leistung <ID>: unbekannt".
             */
            function getLeistungBez(iLeistungID){
                for(var ii=0, jj=$.signUpGlobal.info.seminar.leistungen.length;ii<jj;ii++){
                    if($.signUpGlobal.info.seminar.leistungen[ii].id==iLeistungID){
                        return $.signUpGlobal.info.seminar.leistungen[ii].name;
                        break;
                    }
                }
                return 'Leistung ' + iLeistungID + ': unbekannt!';
            }
            
            /**
             * Sieht die hochgeladene Excel-Datei auf Plausibilität 
             * durch und liefert ein Objekt zurück mit den 
             * Eigenschaften:
             * col_matrikelnummer,
             * col_kurs_id,
             * col_leistung_id,
             * col_note
             * @returns {Objekt}
             */
            function mapExcelCols(oWorksheet){
                var aReturn={};
                aReturn.col_matrikelnummer='#';
                aReturn.col_kurs_id='#';
                aReturn.col_leistung_id='#';
                aReturn.col_note='#';
                var sCols=['A','B','C','D','E','F','G','H','I','J'];

                // Suche Matrikelnummern
                // also: Überschrift "Matrikel%" und 
                //       erste Zeile 7-stellig und numerisch
                for(var ii=0;ii<10;ii++){
                    if(oWorksheet[sCols[ii]+'1'] === undefined){
                        if(g_iDEBUG_LEVEL>3) console.log('[ERROR] hochgeladenes '
                            + 'xslx enthält in keiner Spalte die Matrikelnr. ');
                        break;
                    }else{
                        if(oWorksheet[sCols[ii]+'1'].v.indexOf('Matrikel')==0){
                            var sRow1=oWorksheet[sCols[ii]+'2'].v;
                            if(('' + sRow1).length==7 && sRow1==parseInt(sRow1,10)){
                                aReturn.col_matrikelnummer=sCols[ii];
                                if(g_iDEBUG_LEVEL>5) console.log('[INFO] hochgeladenes '
                                        + 'xslx enthält Matrikelnr. in Spalte ' 
                                        + aReturn.col_matrikelnummer);
                                break;
                            }else{
                                if(g_iDEBUG_LEVEL>3) console.log('[ERROR] hochgeladenes ' 
                                        + 'xslx enthält Matrikelnr. in Spalte ' 
                                        + sCols[ii] 
                                        + ' (' + oWorksheet[sCols[ii] + '2'].v + '), '
                                        + 'aber Matnr schein nicht 7-stellig und numerisch');
                                break;
                            }
                        }
                    }
                }
                if(aReturn.col_matrikelnummer==='#'){
                    console.log('[ERROR!] hochgeladenes XLSX enthält keine Matrikelnummer');
                    alert('Sorry -- das hochgeladene Excel-Dokument scheint '
                            + 'keine Matrikelnummern zu enthalten');
                    return null;
                }

                // Suche KursID, also
                // Überschrift "KursID" und erste 
                // Zeile numerisch.
                for(var ii=0;ii<10;ii++){
                    if(oWorksheet[sCols[ii]+'1'] === undefined){
                        if(g_iDEBUG_LEVEL>3) console.log('[ERROR] hochgeladenes '
                            + 'xslx enthält in keiner Spalte die KursID. ');
                        break;
                    }else{
                        if(oWorksheet[sCols[ii]+'1'].v.indexOf('KursID')==0){
                            var sRow1=oWorksheet[sCols[ii]+'2'].v;
                            if(sRow1==parseInt(sRow1,10)){
                                aReturn.col_kurs_id=sCols[ii];
                                if(g_iDEBUG_LEVEL>5) console.log('[INFO] hochgeladenes '
                                    + 'xslx enthält KursID in Spalte ' 
                                    + aReturn.col_kurs_id);
                                break;
                            }else{
                                if(g_iDEBUG_LEVEL>3) console.log('[ERROR] hochgeladenes '
                                    + 'xslx enthält KursID in Spalte ' 
                                    + aReturn.col_kurs_id) 
                                    + ', aber KursID schein nicht numerisch';
                                break;
                            }
                        }
                    }
                }
                if(aReturn.col_kurs_id==='#'){
                    console.log('[ERROR!] hochgeladenes XLSX enthält keine KursID');
                    alert('Sorry -- das hochgeladene Excel-Dokument scheint '
                            + 'keine KursID zu enthalten');
                    return null;
                }

                // Suche LeistungsID
                // also
                // Überschrift "LeistungID" und erste 
                // Zeile numerisch.
                for(var ii=0;ii<10;ii++){
                    if(oWorksheet[sCols[ii]+'1'] === undefined){
                        if(g_iDEBUG_LEVEL>3) console.log('[ERROR] hochgeladenes '
                            + 'xslx enthält in keiner Spalte die LeistungID. ');
                        break;
                    }else{
                        if(oWorksheet[sCols[ii]+'1'].v.indexOf('LeistungID')==0){
                            var sRow1=oWorksheet[sCols[ii]+'2'].v;
                            if(sRow1==parseInt(sRow1,10)){
                                aReturn.col_leistung_id=sCols[ii];
                                if(g_iDEBUG_LEVEL>5) console.log('[INFO] hochgeladenes '
                                    + 'xslx enthält LeistungID in Spalte ' 
                                    + aReturn.col_leistung_id);
                                break;
                            }else{
                                if(g_iDEBUG_LEVEL>3) console.log('[ERROR] hochgeladenes '
                                    + 'xslx enthält LeistungID in Spalte ' 
                                    + aReturn.col_leistung_id
                                    + ', aber LeistungID scheint nicht numerisch');
                                break;
                            }
                        }
                    }
                }
                if(aReturn.col_leistung_id==='#'){
                    console.log('[ERROR!] hochgeladenes XLSX enthält keine LeistungID');
                    alert('Sorry -- das hochgeladene Excel-Dokument scheint '
                            + 'keine LeistungID zu enthalten');
                    return null;
                }

                // Suche Note
                // also
                // Überschrift "Note" und erste 
                // Zeile numerisch.
                for(var ii=0;ii<10;ii++){
                    if(oWorksheet[sCols[ii]+'1'] === undefined){
                        if(g_iDEBUG_LEVEL>3) console.log('[ERROR] hochgeladenes '
                            + 'xslx enthält in keiner Spalte die Note. ');
                        break;
                    }else{
                        if(oWorksheet[sCols[ii]+'1'].v.indexOf('Note')==0){
                            if(oWorksheet[sCols[ii]+'2']!==undefined &&  
                                    !isNaN(parseFloat(oWorksheet[sCols[ii]+'2'].w.replace(/,/g, '.')))){
                                aReturn.col_note=sCols[ii];
                                if(g_iDEBUG_LEVEL>5) console.log('[INFO] hochgeladenes '
                                    + 'xslx enthält Note in Spalte ' 
                                    + aReturn.col_note);
                                break;
                            }else{
                                if(g_iDEBUG_LEVEL>3) console.log('[ERROR] hochgeladenes '
                                    + 'xslx enthält Note in Spalte ' 
                                    + sCols[ii]
                                    + ', aber das Notenformat ist nicht lesbar');
                                break;
                            }
                        }
                    }
                }
                if(aReturn.col_note==='#'){
                    console.log('[ERROR!] hochgeladenes XLSX enthält keine Noten');
                    alert('Sorry -- das hochgeladene Excel-Dokument scheint '
                            + 'keine Noten zu enthalten');
                    return null;
                }

                return aReturn;
            }
            
            /**
             * Lade die Studiengänge des Seminars mit 
             * den Modularisierungen vom Server
             * 
             * Die Select-Box #cboFach wird mit den 
             * Fächern des Seminars gefüllt.
             * @see json/seminar/fach/get.jsp
             * @see shj.signUp.seminar.Faecher
             */ 
            $.signUpGlobal.info.seminar.faecher = new shj.signUp.seminar.Faecher(
                    $.signUpGlobal.seminar_id,
                    function(data) {
                        $.signUpGlobal.info.seminar.leistungen=data.getLeistungen();
                        shj.signUp.toolbox.populateCbo($.signUpGlobal.info.seminar.leistungen, {"identifyer":"id","text":"name","cboIdentifyer":"#cboLeistung"});
            });

            /**
             * Lade Lehrende dieses Seminars vom Server
             * und befülle #cboDozent
             * @deprecated zwar soll $.signUpGlobal.info.seminar.dozenten
             *     gesetzt werden, aber fürs Rendering gibt es eine 
             *     andere Zuständigkeit (Juni 2015)
             */
            $.signUpGlobal.info.seminar.dozenten = new shj.signUp.seminar.Dozenten(
                    $.signUpGlobal.seminar_id, function(data) {
                        var tmp = [];
                        for (var ii = 0,jj=data.dozenten.length; ii < jj; ii++) {
                            tmp[ii] = {};
                            tmp[ii].id = data.dozenten[ii].id;
                            tmp[ii].name = data.dozenten[ii].vorname + ' ' + data.dozenten[ii].name;
                        }
                        shj.signUp.toolbox.populateCbo(tmp,
                                {'identifyer': 'id', 'text': 'name', 'cboIdentifyer': '#cboDozent'});
                    }
            );
              
            /**
             * Lade Noten dieses Seminars vom Server herunter 
             * und befülle die Select-Box #leistung_noten_id 
             * (in der Detailansicht der Leistung bzw. Anmeldung).
             * @see shj.signUp.seminar.Noten
             * @see json/seminar/note/get.jsp
             */
            $.signUpGlobal.info.seminar.noten = new shj.signUp.seminar.Noten(
                    $.signUpGlobal.seminar_id, function(data) {
                // Damit Notenwert und Name 
                // angezeigt werden, Array umkopieren:
                var tmp = [];
                for (var ii = 0,jj=data.noten.length; ii < jj; ii++) {
                    tmp[ii] = {};
                    tmp[ii].id = data.noten[ii].id;
                    tmp[ii].name = data.noten[ii].wert + ' -- ' + data.noten[ii].name;
                }
                shj.signUp.toolbox.populateCbo(tmp,
                        {'identifyer': 'id', 'text': 'name', 'cboIdentifyer': '.cboNote'});
            });
            
            /**
             * Sucht aus den derzeit aktuellen Anmeldungen 
             * (in $.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen)
             * diejenige Anmeldung für den Kurs i_kurs_id heraus und 
             * gibt diese Anmeldung zurück.
             * Wenn keine Anmeldung gefunden wird, wird {} zurückgegeben.
             * @param {type} i_kurs_id
             * @returns {unresolved}
             */
            function getAnmeldungByKurs(i_kurs_id){
                var oReturn={};
                for(var ii=0;ii<$.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen.length;ii++){
                    if($.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen[ii].anmeldung.kurs_id===i_kurs_id){
                        oReturn=$.signUpGlobal.info.seminar.curr_anmeldungen.anmeldungen[ii].anmeldung;
                        break;
                    }
                }
                return oReturn;
            }
            });
    </script>
  </body> 
</html>             
