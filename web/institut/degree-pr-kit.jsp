<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%><!DOCTYPE html> 
<html lang="en"> 
    <head> 
        <meta charset="utf-8"> 
        <meta contentType="application/x-www-form-urlencoded"></meta>
        <title>SignUp -- Studiengang-Baukasten</title> 
        <meta name="description" content="Prüfungsverwaltung"> 
        <meta name="author" content="Heiko Jakubzik"> 
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Le HTML5 shim, for IE6-8 support of HTML elements --> 
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]--> 

        <!-- Le styles --> 
        <jsp:include page="fragments/css-experimental.jsp" />
        <style type="text/css"> 
            body {
                padding-top: 60px;
                padding-bottom: 40px;
                /* font-size:1.2em;*/
            }
            .shj_hover{
                background-color: #DDD;
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
                background: url(img/SignUp3.png) no-repeat;
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
            
            .panel-group .panel {
                overflow: visible;
             }

        </style>
        
        <!-- Le fav and touch icons --> 
        <link rel="shortcut icon" href="img/signup.ico"> 
        <link rel="apple-touch-icon" href="images/apple-touch-icon.png"> 
        <link rel="apple-touch-icon" sizes="72x72" href="images/apple-touch-icon-72x72.png"> 
        <link rel="apple-touch-icon" sizes="114x114" href="images/apple-touch-icon-114x114.png"> 
    </head> 

    <body> 


        <nav class="navbar navbar-fixed-top navbar-inverse">
            <div class="navbar-header">
                <a class="navbar-brand" href="#">SignUp</a>
                 <a class="navbar-toggle" data-toggle="collapse" data-target=".nav-collapse"> 
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>

                 </a>  <a class="navbar-brand" href="#">&nbsp; </a> 
            </div>
 <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="#about">About</a></li>
            <li><a href="#" id="cmdRTF">Word</a></li>
            <li><a href="#contact">Contact</a></li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Alle Module...<b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="#" id="cmdAddAllModules">... eintragen</a></li>
                <li><a href="#" id="cmdDropAllModules">... austragen</a></li>
                <li><a href="#" id="cmdCollapseAllModules">... einklappen</a></li>
                <li><a href="#" id="cmdExpandAllModules">... ausklappen</a></li>
                <li><a href="#" id="cmdCreateTxt">... als .txt</a></li>
                <li class="divider"></li>
                <!-- <li class="dropdown-header">Nav header</li> -->
                <li><a href="#" id="cmdSaveDegreeProgram">Als neuer Studiengang speichern</a></li>
                <!-- <li><a href="#">One more separated link</a></li> -->
              </ul>
            </li>
          </ul>
            <div class="navbar-header navbar-right">
                  <p class="navbar-text">
                  <a href="#" class="navbar-link"></a>
                  </p>
            </div>
            <div class="navbar-collapse collapse">
              <ul class="nav navbar-nav navbar-right">

              </ul>
            </div>
        </nav>

                <!--/.navbar-collapse -->


<div class="container"> 
    <div class="row"><div class="col-md-12 col-sd-12"><h2>Studiengang-Design</h2></div></div>
    <div class="row">
        <div class="col-md-3 col-sd-3">
            <p><select class="form-control" id="cboFach"><option value="">-- bitte Studiengang als Basis wählen --</option></select></p>
        </div>
        <div class="col-md-5 col-sd-5">
            <p><span id="txtLPAktuell">0</span> Leistungspunkte</p>
        </div>
        <div class="col-md-4 col-sd-4">
            <!-- <p>More Information</p> -->
        </div>
    </div>

  <div class="row"> 
                <%--
                    ######################
                    Navigationsleiste links
                    ######################
                --%>

<div class="col-md-3 col-sd-3">
    <div class="panel-group" id="accordion">
        <div class="panel panel-default" style="max-height: 400px; overflow-y: scroll;">
            <div class="panel-heading">
                <h4 class="panel-title">
                    <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne"><span class="glyphicon glyphicon-folder-close">
                        </span>&nbsp;Module</a>
                </h4>
            </div>
            <div id="collapseOne" class="panel-collapse collapse shj_not_generally_collapsible">
                <div class="panel-body">
                    <table class="table table-condensed" id="tModule">

                    </table>
                </div>
            </div>
        </div>
        <div class="panel panel-default" style="max-height: 400px; overflow-y: scroll;">
            <div class="panel-heading">
                <h4 class="panel-title">
                    <a data-toggle="collapse" data-parent="#accordion" href="#collapseTwo"><span class="glyphicon glyphicon-th">
                    </span>&nbsp;Leistungen</a>
                </h4>
            </div>
            <div id="collapseTwo" class="panel-collapse collapse shj_not_generally_collapsible">
                <div class="panel-body">
                    <table class="table" id="tLeistungen">

                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="playground_1" class="col-md-3 col-sd-3 well droppable_modules">

</div>
<div id="playground_2" class="col-md-3 col-sd-3 well droppable_modules">

</div>
<div id="playground_3" class="col-md-3 col-sd-3 well droppable_modules">

</div>


                </div>
    <hr> 
    <%-- 
    ######################################## INVISIBLE TEMPLATES #####################################
        
    --------------------------------------------------------------------------------------------------
    Modal zum Edieren von Modulen
    --------------------------------------------------------------------------------------------------
    --%><!-- Modal -->
<div class="modal fade" id="modEdiereModul" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Aufbaumodul Dings- und Bums</h4>
        <span id="txtModulLP">n</span> LP, Gewichtung: <span id="txtModulGewichtung">n</span>
      </div>
        
      <div class="modal-body">
        Layout
        
      </div>
        <!-- derzeit nicht benötigt 
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Save changes</button>
      </div>-->
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

    <%-- ------------------------------------------------------------------------------------------------
    Modal zum Edieren von Modulen
    --------------------------------------------------------------------------------------------------
    --%><!-- Modal -->
<div class="modal fade" id="modStudiengangNeu" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title">Neuer Studiengang</h4>
      </div>
      <div class="modal-body">
          <div class="form-group">
            <label for="txtStudiengangName">Studiengang</label>
            <input type="text" class="form-control" id="txtStudiengangName" placeholder="Name des Studiengangs">
          </div>
          <div class="form-group">
            <label for="txtStudiengangVariante">Variante/Version</label>
            <input type="text" class="form-control" id="txtStudiengangVariante" placeholder="Hauptfach, Begleitfach ... / Version 0.9.1 ...">
          </div>
          <div class="form-group">
            <label for="txtStudiengangBeschreibung">Beschreibung</label>
            <textarea rows="5" class="form-control" id="txtStudiengangBeschreibung" placeholder=""></textarea>
          </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Schließen</button>
        <button type="button" class="btn btn-primary" id="cmdSaveOnServer">Speichern</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
    
    <%-- --------------------------------------------------------------------------------------------------
    Template fürs Modul
    --------------------------------------------------------------------------------------------------
    --%>
    <div style="display:none" class="shj_templates">

    <div id="template_modul">
    <div class="panel-group" id="accordion-menu">
        <div class="panel panel-success shj_color">
  
            <div class="panel-heading dropdown" style="font-size:0.9em" >
                <a data-toggle="collapse" href="#collapseMod" class="template_modulname"></a><span class="template_modul_lp"></span>
                 <a data-toggle="dropdown" href="#" class="pull-right"><span class="caret"></span></a>
                 <ul class="dropdown-menu pull-right" role="menu">
                     <li class="shj_deleteModule"><a href="#"><i class="glyphicon glyphicon-trash"></i> Modul entfernen</a></li>
                     <li class="divider"></li>
                     <li class="shj_LeistungenLP"><a href="#"><i class="glyphicon glyphicon-list"></i> Leistungen und LP</a></li>
                     <li><a href="#"><i class="glyphicon glyphicon-comment"></i> Qualifikationsziele</a></li>
                     <li class="shj_paintRed"><a href="#"><span style="background-color: red">&nbsp;&nbsp;&nbsp;</span> rot</a></li>
                     <li class="shj_paintYellow"><a href="#"><span style="background-color: yellow">&nbsp;&nbsp;&nbsp;</span> gelb</a></li>
                     <li class="shj_paintGreen"><a href="#"><span style="background-color: green">&nbsp;&nbsp;&nbsp;</span> grün</a></li>
                 </ul>
            </div>
            <div id="collapseMod" class="shj_mass-collapsible panel-collapse collapse in" >
                <div class="panel-body shjLeistungsContainer">
                    <table class="table table-condensed" style="font-size:0.8em">

                    </table>
                </div>
            </div>
        </div>
    </div>
        

    </div><!-- End of display:none -->
    <div class="row" ></div>
<footer class="text-center">&copy; 2014 shj-online.de</footer>

<div class="signUp_waitmsg-modal"><!-- Place at bottom of page --></div>
</div><!--/.fluid-container--> 

<!-- Le javascript
================================================== --> 
<!-- Placed at the end of the document so the pages load faster --> 
<!-- Placed at the end of the document so the pages load faster -->
<jsp:include page="fragments/libs-student-experimental.jsp"></jsp:include>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
<script src="js/degree-pr-kit.js"></script>
<script src="js/FileSaver.js"></script>
<script>
    $(document).ready(function(){
        DEGREE_KIT.init();
    });
</script>
</body> 
</html> 