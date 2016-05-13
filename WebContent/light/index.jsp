<%@page import="io.greenscreens.data.TnConstants"%>
<%@ page session="true" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
//JSP code
//Used to prevent JS/CSS caching for debug
//long ts =  System.currentTimeMillis();
// for production use version, on every new release, don't forget to update version
String ts = TnConstants.VERSION;
%>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <meta name="mobile-web-app-capable" content="yes">
  <meta name="apple-mobile-web-app-capable" content="yes">

  <link id="tntheme" rel="stylesheet" href="css/as400.css?<%=ts %>" type="text/css" />

  <title>GreenScreens Terminal - web terminal emulator for 5250</title>
</head>

<body class="">
    <div class="ghost-select"><span></span></div>
	<div class="view"></div>
	<div class="tnstatus">
		<table>
		<tbody>

		<tr>

		<td colspan="2" class="white sts left">OFFLINE</td>
		<td colspan="2" class="white action left"></td>
		<!-- red or yellow -->
		<td colspan="8" class="msg yellow left"></td>
		<td class="msgw_icon"></td>
		<td class="white" colspan="3">&nbsp;</td>
		<td class="white pos">0:0</td>
		<td class="white wrk" colspan="2">&nbsp;</td>
		<td class="white mod">OWR</td>
		<td class="white lng">LTR</td>
		<td class="prt_icon">&nbsp;</td>
		<td class="white psts" colspan="2"></td>

		</tr>

		</tbody>
		</table>
	</div>

	<div class="kbar" >
		<table id="tnkbar">
		  <tr>
		      <td ><span data="PF1" type="button">PF01</span></td>
		      <td ><span data="PF2" type="button">PF02</span></td>
		      <td ><span data="PF3" type="button">PF03</span></td>
		      <td ><span data="PF4" type="button">PF04</span></td>
		      <td ><span data="PF5" type="button">PF05</span></td>
		      <td ><span data="PF6" type="button">PF06</span></td>
		      <td ><span data="PF7" type="button">PF07</span></td>
		      <td ><span data="PF8" type="button">PF08</span></td>
		      <td ><span data="PF9" type="button">PF09</span></td>
		      <td ><span data="PF10" type="button">PF10</span></td>
		      <td ><span data="PF11" type="button">PF11</span></td>
		      <td ><span data="PF12" type="button">PF12</span></td>

		      <td ><span data="ENTER" type="button">Enter</span></td>
		      <td ><span data="ATTN" type="button">Attn</span></td>
		      <td ><span data="rtl" type="button">FldRev</span></td>
		      <td ><span data="ower" type="button">Insert</span></td>
		  </tr>
		  <tr>
		      <td ><span data="PF13" type="button">PF13</span></td>
		      <td ><span data="PF14" type="button">PF14</span></td>
		      <td ><span data="PF15" type="button">PF15</span></td>
		      <td ><span data="PF16" type="button">PF16</span></td>
		      <td ><span data="PF17" type="button">PF17</span></td>
		      <td ><span data="PF18" type="button">PF18</span></td>
		      <td ><span data="PF19" type="button">PF19</span></td>
		      <td ><span data="PF20" type="button">PF20</span></td>
		      <td ><span data="PF21" type="button">PF21</span></td>
		      <td ><span data="PF22" type="button">PF22</span></td>
		      <td ><span data="PF23" type="button">PF23</span></td>
		      <td ><span data="PF24" type="button">PF24</span></td>

		      <td ><span data="CLEAR" type="button">Clear</span></td>
		      <td ><span data="SYSREQ" type="button">SysReq</span></td>
		      <td ><span data="del" type="button">Delete</span></td>
		      <td ><span data="fex" type="button">FldExit</span></td>
		  </tr>
		</table>
	</div>


	<div class="overlay"></div>
	<audio src="../sounds/beep.mp3" id="beep" style="visibility:hidden;" type="audio/mpeg" ></audio>
</body>

<script type="text/javascript" charset="UTF-8" src="lib/jquery-2.1.4.min.js"></script>
<script type="text/javascript" charset="UTF-8" src="lib/microevent.js"></script>
<script type="text/javascript" charset="UTF-8" src="lib/jss.min.js"></script>

<script type="text/javascript" charset="UTF-8" src="js/Tn5250.WS.js?<%=ts %>"></script>
<script type="text/javascript" charset="UTF-8" src="js/Tn5250.API.js?<%=ts %>"></script>
<script type="text/javascript" charset="UTF-8" src="js/Tn5250.Decoder.js?<%=ts %>"></script>
<script type="text/javascript" charset="UTF-8" src="js/Tn5250.Keyboard.js?<%=ts %>"></script>
<script type="text/javascript" charset="UTF-8" src="js/Tn5250.KeyBar.js?<%=ts %>"></script>
<script type="text/javascript" charset="UTF-8" src="js/Tn5250.Selector.js?<%=ts %>"></script>
<script type="text/javascript" charset="UTF-8" src="js/Tn5250.Renderer.js?<%=ts %>"></script>
<script type="text/javascript" charset="UTF-8" src="js/Tn5250.OIA.js?<%=ts %>"></script>
<script type="text/javascript" charset="UTF-8" src="js/Tn5250.UI.js?<%=ts %>"></script>
<script type="text/javascript" charset="UTF-8" src="js/Tn5250.Application.js?<%=ts %>"></script>

</html>
