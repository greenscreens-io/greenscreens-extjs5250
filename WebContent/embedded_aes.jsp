<!DOCTYPE html>
<%@ page session="true" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

  <style>
  body {
    margin:0px;
    padding:0px;
    overflow:hidden;
   }

   iframe {
     overflow:hidden;
     border: 0px;
     position:absolute;
     height:100%;
     width:100%;
     top: 0px;
     left: 0px;
   }
  </style>
</head>

<body>
 <iframe id="ifrm" src="light/?host=LOCAL&displayName=PHP00010&user=TOM&password=0c78c58ea1d17890eaaeb39244bc9b895b7e4db67550de776dd35877009789fb"
         frameborder="0"  height="100%" width="100%">Your browser doesn't support iFrames.</iframe>
</body>

<script type="text/javascript" charset="UTF-8" src="light/lib/jquery-2.1.4.min.js"></script>
<script type="text/javascript">

    document.oncontextmenu = function (e) {
        e.preventDefault();
        return false;
    };

    $(document).keydown(function (e) {
    	if (e.which >=112 && e.which<=123) {
    		e.stopPropagation();
    		e.preventDefault();
    	}
    });

	$(document).ready(function(){
		  var ifrm = document.getElementById('ifrm');
		  ifrm.onload = function() {
			 ifrm.contentWindow.focus();
		  }
	});
</script>

<script type="text/javascript">
  /* It ispossible to call it from parent
  var ifrm = document.getElementById('ifrm');
  ifrm.onload = function() {
	 ifrm.contentWindow.initialize('LOCAL', 'PHP00010', 'user', 'password');

  }
  */
  function onClientClosed() {
	  var ifr = document.querySelector('#ifrm');
	  if (ifr) ifr.remove();
	  return false;  // will prevent calling redirect for url redirect parameter
  }
</script>

</html>
