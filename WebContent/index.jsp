<!DOCTYPE html>
<%@ page session="true" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-capable" content="yes">
	<title>GreenScreens Terminal - web terminal emulator for 5250</title>

    <link rel="stylesheet" href="css/bootstrap/css/font-awesome.min.css" type="text/css"/>
    <link rel="stylesheet" href="css/bootstrap/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="css/landing.css" type="text/css"/>
</head>

<body>

<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#">
        <div class="image"><i class="fa fa-desktop"></i></div>
      </a>
      <p class="navbar-text"><b>GreenScreens Terminal</b> - Web 5250 terminal emulator</p>
    </div>

    <button type="button" class="btn btn-default navbar-btn pull-right">
       <div class="image"><i class="fa fa-cog"></i></div>
    </button>

  </div>
</nav>

<div class="container">
	<div class="row">

	</div>
</div>



<div class="container">
	<div class="row">
        <!-- Boxes de Acoes -->
    	<div class="col-xs-12 col-sm-6 col-lg-4">
			<div class="box doc-item">
				<div class="icon">
					<div class="image"><i class="fa fa-desktop"></i></div>
					<div class="info">
						<h3 class="title">Default</h3>
						<p>
							Default GreenScreens full page demo.
						</p>
						<div class="more">
							<a href="lite/?host=LOCAL" title="Default" target="_blank">
								Open <i class="fa fa-angle-double-right"></i>
							</a>
						</div>
					</div>
				</div>
				<div class="space"></div>
			</div>
	</div>
    	<div class="col-xs-12 col-sm-6 col-lg-4">
			<div class="box doc-item">
				<div class="icon">
					<div class="image"><i class="fa fa-clock-o"></i></div>
					<div class="info">
						<h3 class="title">Timestamp</h3>
						<p>
							Server timestamp service.
						</p>
						<div class="more">
							<a href="timestamp" title="Manual" target="_blank">
								Open <i class="fa fa-angle-double-right"></i>
							</a>
						</div>
					</div>
				</div>
				<div class="space"></div>
			</div>
	</div>

    	<div class="col-xs-12 col-sm-6 col-lg-4">
			<div class="box doc-item">
				<div class="icon">
					<div class="image"><i class="fa fa-info-circle"></i></div>
					<div class="info">
						<h3 class="title">Documentation</h3>
						<p>
							Click here to see user manual page.
						</p>
						<div class="more">
							<a href="docs" title="Manual" target="_blank">
								Open <i class="fa fa-angle-double-right"></i>
							</a>
						</div>
					</div>
				</div>
				<div class="space"></div>
			</div>
	</div>
        <div class="col-xs-12 col-sm-6 col-lg-4"></div>

	<!-- /Boxes de Acoes -->
	</div>
</div>


<div class="container">
	<div class="row">
        <!-- Boxes de Acoes -->
    	<div class="col-xs-12 col-sm-6 col-lg-4">
			<div class="box doc-item">
				<div class="icon">
					<div class="image"><i class="fa fa-desktop"></i></div>
					<div class="info">
						<h3 class="title">Embedded</h3>
						<p>
							Default embedded demo showing ExtTn520 with basic IFRAME inclusion.
						</p>
						<div class="more">
							<a href="embedded.jsp" title="Default" target="_blank">
								Open <i class="fa fa-angle-double-right"></i>
							</a>
						</div>
					</div>
				</div>
				<div class="space"></div>
			</div>
		</div>

        <div class="col-xs-12 col-sm-6 col-lg-4">
			<div class="box doc-item">
				<div class="icon">
					<div class="image"><i class="fa fa-desktop"></i></div>
					<div class="info">
						<h3 class="title">AES Embeded</h3>
    					        <p>
							Demo showing GreenScreens in IFRAME with AES encryption for auto-signon.
						</p>
						<div class="more">
							<a href="embedded_aes.jsp" title="AES Embeded" target="_blank">
								Open <i class="fa fa-angle-double-right"></i>
							</a>
						</div>
					</div>
				</div>
				<div class="space"></div>
			</div>
		</div>

        <div class="col-xs-12 col-sm-6 col-lg-4">
			<div class="box doc-item">
				<div class="icon">
					<div class="image"><i class="fa fa-desktop"></i></div>
					<div class="info">

					</div>
				</div>
				<div class="space"></div>
			</div>
		</div>
		<!-- /Boxes de Acoes -->
	</div>
</div>


     <div class="container">
      <div class="row">
       <div class="span12">
        <div id="footer">

           <ul class="footer">
            <li>&copy; 2016. GreenScreens Ltd. &nbsp;&nbsp;<a href="https://github.com/greenscreens-io/greenscreens-tn5250" target="_blank">
                <div class="image"><i class="fa fa-github"></i></div></a></li>
           </ul>

        </div>
     </div>
  </div>
</div>

</body>

</html>
