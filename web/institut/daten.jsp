<%@page import="de.shj.UP.data.shjCore"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" errorPage="err.html"%><!DOCTYPE html> 
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<%
    // Besteht eine gültige Sitzung?
    if (user.getDozentNachname() == null) {
        throw new Exception("Sorry, Sie müssen sich neu anmelden");
    }
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
        <!-- wysihtml5 parser rules -->
        <script src="js/wysihtml/simple.js"></script>
        <!-- Library -->
        <!-- <script src="js/wysihtml/wysihtml5-0.4.0pre.min.js"></script> -->
        <script src="js/wysihtml/wysihtml5-0.3.min.js"></script>
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

        <link href="css/bootstrap-fileupload.min.css" rel="stylesheet"> 

        <!-- Le fav and touch icons --> 
        <link rel="shortcut icon" href="img/signup.ico"> 
        <link rel="apple-touch-icon" href="images/apple-touch-icon.png"> 
        <link rel="apple-touch-icon" sizes="72x72" href="images/apple-touch-icon-72x72.png"> 
        <link rel="apple-touch-icon" sizes="114x114" href="images/apple-touch-icon-114x114.png"> 
    </head> 

    <body> 

        <span class="contextmenu" id="transkriptContextMenu" style="width:350px; font-size:0.7em; display: none;">
            <fieldset id="template_rubrik">
                <div class="control-group hp_rubrik"> 
                    <div class="controls">
                        <input type="text" name="rubrik" placeholer="Rubrik (Publikationen, Projekte, Interessen...)" class="input-xlarge"></input>
                        <button title="Löschen" id="b2" class="btn btn-danger pull-right" type="button">-</button>
                        <button title="Speichern" id="b3" class="btn btn-success pull-right" type="button"><i class="icon-ok icon-white"></i></button>
                    </div> 
                    <hr>
                </div>

                <div id="template_wysihtml5-toolbar" style="display: none;" class="btn-group">
                    <a data-wysihtml5-command="bold" class="btn"><i class="icon-bold"></i></a>
                    <a data-wysihtml5-command="italic" class="btn"><i class="icon-italic"></i></a>
                    <a data-wysihtml5-command="insertUnorderedList" class="btn" data-original-title="List"><i class="icon-list"></i></a>
                    <a data-wysihtml5-command="createLink" class="btn"><i class="icon-link"></i></a>

                    <div data-wysihtml5-dialog="createLink" style="display: none;">
                        <label>
                            Link:
                            <input data-wysihtml5-dialog-field="href" value="http://">
                        </label>
                        <a class="btn" data-wysihtml5-dialog-action="save">OK</a>&nbsp;<a class="btn" data-wysihtml5-dialog-action="cancel">Cancel</a>
                    </div>
                </div> 
                <textarea class="span12" rows="5" id="template_hp-text" placeholder="Enter your text ..." autofocus></textarea>
            </fieldset>
        </span>
        <input type="hidden" id="logged-in-user-name" value="<%=user.getDozentNachname()%>" />
        <input type="hidden" id="logged-in-user-seminar" value="<%=user.getSdSeminarID()%>" />
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
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Angemeldet als <%=user.getDozentTitel() + " " + user.getDozentVorname() + " " + user.getDozentNachname()%> <b class="caret"></b></a> 
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
                        <div class="span10">
                            <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++ -->
                            <!-- Formular zum Ändern von Passwort und E-Mail Adresse -->
                            <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++ -->
                            <div class="span2" style="text-align: center">
                                <a href="#" target="_blank" class="thumbnail" id="picplace"><img id="dozent_photo" src="img/openclipart_dagobert83_user_icon.png" alt=""></img></a><hr>
                                <button class="btn btn-warning btn-small" id="cmdUpdatePic" onclick="$('#modIMGUpload').modal('show');"><i class="icon-picture"></i> Bild ändern</button>
                                <button title="Bild löschen" class="btn btn-danger btn-small" id="cmdDeletePic"><i class="icon-trash"></i></button>
                            </div>

                            <div class="span8">
                                <div class="tabbable">
                                    <ul class="nav nav-tabs">
                                        <li class="active"><a data-toggle="tab" href="#tab1">Stammdaten</a></li>
                                        <li><a data-toggle="tab" href="#tab2">Homepage</a></li>
                                    </ul>
                                    <div class="tab-content">
                                        <div id="tab1" class="tab-pane active">
                                            <form id="fEditData" class="form-horizontal" style="text-align:left" action="#" method="post">
                                                <fieldset>
                                                    <div class="control-group" id="cgTitel"> 
                                                        <label class="control-label" for="titel">Titel</label> 
                                                        <div class="controls"> 
                                                            <input type="text" id="titel" name="titel" value="<%=user.getDozentTitel()%>" class="input-xlarge"></input>
                                                        </div> 
                                                    </div>
                                                    <div class="control-group" id="cgTelefon"> 
                                                        <label class="control-label" for="telefon">Telefon</label> 
                                                        <div class="controls"> 
                                                            <input type="text" id="telefon" name="telefon" value="<%=user.getDozentTelefon()%>" class="input-xlarge"></input>
                                                        </div> 
                                                    </div>    
                                                    <div class="control-group" id="cgZimmer"> 
                                                        <label class="control-label" for="zimmer">Zimmer</label> 
                                                        <div class="controls"> 
                                                            <input type="text" id="zimmer" name="zimmer" value="<%=user.getDozentZimmer()%>" class="input-xlarge"></input>
                                                        </div> 
                                                    </div>                       
                                                    <div class="control-group" id="cgEMail"> 
                                                        <label class="control-label" for="email">E-Mail</label> 
                                                        <div class="controls"> 
                                                            <input type="text" id="email" name="email" value="<%=user.getDozentEmail()%>" class="input-xlarge" ></input>
                                                        </div> 
                                                    </div>
                                                    <div class="control-group" id="cgSprechstunde"> 
                                                        <label class="control-label" for="sprechstunde">Sprechstunde</label> 
                                                        <div class="controls"> 
                                                            <textarea rows="3" id="sprechstunde" name="sprechstunde" class="input-xlarge"><%=user.getDozentSprechstunde()%></textarea>
                                                        </div> 
                                                    </div>


                                                    <div class="control-group" id="cgPassword"> 
                                                        <label class="control-label" for="txtPasswort">Passwort</label> 
                                                        <div class="controls"> 
                                                            <input type="password" id="txtPasswort" name="txtPasswort" value="*****" class="input-xlarge"></input>
                                                        </div> 
                                                    </div>

                                                    <div class="control-group" id="cgPasswordWdh"> 
                                                        <label class="control-label" for="txtPasswortWdh">Passwort Wdh.</label> 
                                                        <div class="controls"> 
                                                            <input type="password" id="txtPasswortWdh" name="txtPasswortWdh" value="*****" class="input-xlarge"></input>
                                                        </div> 
                                                    </div>
                                                </fieldset>
                                                <div class="form-actions">
                                                    <button type="submit" id="cmdSave" class="btn btn-primary">Speichern</button>
                                                </div>
                                            </form>
                                        </div> <!-- tab1 -->
                                        <div id="tab2" class="tab-pane">
                                            <div class="tabbable">
                                                <ul class="nav nav-tabs">
                                                    <li class="active"><a data-toggle="tab" href="#tab_hp1">Inhalt</a></li>
                                                    <li id="tabDownloads"><a data-toggle="tab" href="#tab_hp2">Downloads</a></li>
                                                </ul>
                                                <div class="tab-content">
                                                    <div id="tab_hp1" class="tab-pane active">
                                                        <fieldset>
                                                            <div class="control-group" id="cgHPAddress"> 
                                                                <label class="control-label" for="address">Adresse</label> 
                                                                <div class="controls">
                                                                    <select name="cboAddress" id="cboAddress">
                                                                        <option value="">Keine Homepage</option>
                                                                        <option value="hd">Homepage automatisch erzeugen</option>
                                                                        <option value="ex">Externe Homepage</option>
                                                                    </select>
                                                                    <input style="display:none" type="text" id="address" name="address" value="<%=user.getDozentHomepage()%>" class="input-xlarge"></input>
                                                                    <select style="display:none" name="cboKvv" id="cboKvv">
                                                                        <option value="">Keine Lehrveranstaltungen auf Homepage</option>
                                                                        <option value="aktuell">Nur aktuelle Lehrveranstaltungen auf Homepage</option>
                                                                        <option value="alle">Alte und aktuelle Lehrveranstaltungen auf Homepage</option>
                                                                    </select>
                                                                    <a id="hp_link" target="_blank" href="<%=user.getDozentHomepage()%>" title="Homepage besuchen" class="btn btn-primary"><i class="icon-share-alt"></i></a>
                                                                </div> 

                                                            </div><%--
                                                            <div class="control-group" id="cgHPKvv"> 
                                                                <label class="control-label" for="cboKvv">Zeige...</label> 
                                                                <div class="controls">
                                                                    <select name="cboKvv" id="cboKvv">
                                                                        <option value="">Keine Lehrveranstaltungen auf Homepage</option>
                                                                        <option value="aktuell">Nur aktuelle Lehrveranstaltungen auf Homepage</option>
                                                                        <option value="alle">Alte und aktuelle Lehrveranstaltungen auf Homepage</option>
                                                                    </select>
                                                                </div> 
                                                            </div>--%>
                                                        </fieldset>
                                                        <div id="showRubriken">
                                                        <hr><h2>Rubriken <button id="b1" class="btn btn-info pull-right" type="button">+</button></h2>
                                                        <form id="fRubriken" style="text-align:left" action="#" method="post">
                                                            <div id="rubrik_-1"></div>
                                                        </form>
                                                        </div>
                                                    </div>
                                                    <div id="tab_hp2" class="tab-pane">
                                                        <h2>Downloads<button id="cmdAddDownload" class="btn btn-info pull-right" type="button">+</button></h2>
                                                        <div id="Downloads">                       
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <!-- end tab2 -->
                                        </div>
                                    </div></div><!-- end tabContent -->
                            </div><!-- end of tabbable -->
                        </div>

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
                            Modalfenster Allg.: Erfolg ("OK")
                            ----------------------------------------------- 
                        --%>	            
                        <div class="modal hide fade" id="modShj_SignUp_OK" class="signUp_viewToggle">
                            <div class="modal">
<!--                                <div class="modal-header">
                                    <a class="close" data-dismiss="modal">×</a>
                                    <h3 class="text-warning shj_SignUp_OK_Header"></h3>
                                </div>-->
                                <div class="modal-body shj_SignUp_OK_Body">
                                    <h1 class="text-success" style="text-align:center">OK</h1>
                                </div>
                            </div>
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
                                  <div class="fileupload-new thumbnail" style="width: 200px; height: 150px;"><img id="dozent_photo_thumbnail" src="http://www.placehold.it/200x150/EFEFEF/AAAAAA&text=<%=user.getDozentNachname()%>" /></div>
                                  <div class="fileupload-preview fileupload-exists thumbnail" style="max-width: 200px; max-height: 150px; line-height: 20px;"></div>
                                  <div>
                                    <span class="btn btn-file"><span class="fileupload-new">Bild wählen</span><span class="fileupload-exists">Ändern</span><input type="file" id="fileImg" shj_allow="jpg" /></span>
                                    <a href="#" id="cmdResetIMG" class="btn fileupload-exists" data-dismiss="fileupload">Löschen</a>
                                  </div>
                                </div>
  
                                    </div>
                                    <div class="modal-footer">
                                        <input type="hidden" name="dozentfoto" id="dozentfoto" value="dozentfoto" />
                                        <button href="#" id="cmdIMGUpload" class="btn btn-success"><i class="icon-upload icon-white"></i>Hochladen</button>
                                        <a href="#" id="cmdIMGCancel" class="btn btn-danger" data-dismiss="modal"><i class="icon-trash icon-white"></i> Abbrechen</a>
                                    </div>
                                </form>
                            </div>
                        </div>                        
                        
                        <%-- ================================================================ -->
                        <!-- 				Datei (PDF) hochladen/ändern								  -->
                        <!-- ================================================================ --%>
                        <div class="modal hide fade" id="modPDFUpload" class="signUp_viewToggle">
                            <div class="modal fileinputs">
                                <form class="form-horizontal" id="fPDFUpload" method="POST" action="util/pdfUpload.jsp"  enctype="multipart/form-data">
                                    <div class="modal-header">
                                        <a class="close" data-dismiss="modal">×</a>
                                        <h3>Datei hochladen</h3>
                                    </div>
                                    <div class="modal-body">
                                        <div id="pdf_progress_bar" style="display:none" class="progress progress-striped active">
                                            <div class="bar" style="width: 0%;"></div>
                                        </div>
                                        <div id="pdf_wait" style="display:none" class="alert">
                                            <b>Warte auf Antwort vom Server...</b>
                                        </div>
                                        <div id="cgPDFSuchen"> 
                                            <div class="fileupload fileupload-new" data-provides="fileupload">
                                                <div class="input-append">
                                                    <div class="uneditable-input span3"><i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span></div><span class="btn btn-file"><span class="fileupload-new">PDF wählen</span><span class="fileupload-exists">Ändern</span><input  id="filePDF" shj_allow="pdf" type="file"></input></span><a href="#" class="btn fileupload-exists" id="pdfReset" data-dismiss="fileupload">Löschen</a>
                                                </div>
                                            </div>
                                        </div>
                                        <div id="cgPDF"> 
                                            <input id="pdf" class="span8" placeholder="Bezeichnung" style="display:none" name="pdf" class="input-xlarge"></input>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <input type="hidden" name="dozentpdf" id="dozentpdf" value="dozentpdf" />
                                        <input type="submit" href="#" id="cmdPDFUpload" class="btn btn-success disabled"><i class="icon-upload icon-white"></i> </input>
                                        <a href="#" id="cmdCancelPDF" class="btn btn-danger" data-dismiss="modal"><i class="icon-trash icon-white"></i> Abbrechen</a>
                                    </div>
                                </form>
                            </div>
                        </div>
                        <!-- ================================================================ -->
                        <!-- 				END								  -->
                        <!-- ================================================================ -->      			

                    </div>
                </div>
                <!-- ================================================================ -->
                <!-- 				Texteingabe für die Homepage  	  -->
                <!-- ================================================================ -->  

            </div><!--/row--> 
        </div><!--/span--> 
    </div><!--/row--> 

    <hr> 

    <footer> 
        <p>&copy; shj-online 2012</p> 
        <input type="hidden" id="d_id" value="<%=user.getDozentID()%> " />
        <input type="hidden" id="d_homepage" value="<%=user.getDozentHomepage() %>" />
        <input type="hidden" id="d_interessen" value="<%=shjCore.string2JSON(shjCore.normalize(user.getDozentInteressen()).replace('\"', '|')) %>" />
        <input type="hidden" id="d_hpOptionen" value="<%=shjCore.string2JSON(shjCore.normalize(user.getDozentHomepageOptions()).replace('\"', '|').replaceAll("null", "")) %>" />
    </footer> 
    <div class="signUp_waitmsg-modal"><!-- Place at bottom of page --></div>
</div><!--/.fluid-container--> 

<!-- Le javascript
================================================== --> 
<!-- Placed at the end of the document so the pages load faster --> 
<!-- Placed at the end of the document so the pages load faster -->
<jsp:include page="fragments/libs.jsp"></jsp:include>
    <script src="js/signup-faculty-common.js"></script>
    <script src="js/signup-faculty-daten.js"></script>
    <script src="js/bootstrap-fileupload.min.js"></script>

</body> 
</html> 