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
     height:90%;
     width:90%;
     top: 0px;
     left: 0px;
   }
  </style>
</head>

<body>
 <iframe id="ifrm" src="lite/?host=LOCAL" frameborder="0"  height="90%" width="90%">Your browser doesn't support iFrames.</iframe>
</body>

<script type="text/javascript" charset="UTF-8" src="lite/lib/jquery-2.1.4.min.js"></script>
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

</html>
