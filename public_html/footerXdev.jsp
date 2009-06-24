<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML><HEAD>
<META http-equiv=Content-Type content="text/html; charset=utf-8"><LINK 
href="bo_global.css" type=text/css rel=stylesheet>
<STYLE>TD {
	FONT-WEIGHT: bold; FONT-SIZE: 10px; CURSOR: default; COLOR: #ddddee;
}
DIV {
	FONT-WEIGHT: normal; FONT-SIZE: 9px
}
TD.pad {
	PADDING-LEFT: 6px; CURSOR: hand
}
IMG.ico {
	FILTER: Alpha(opacity=60); FLOAT: left; MARGIN-RIGHT: 5px
}


TABLE.tablemain {
	
	BORDER-RIGHT: #113469 1px solid;
    BORDER-bottom: #113469 1px solid;
	BORDER-LEFT: #A4C3F0 1px solid;
	padding-top:1px;
	BORDER-TOP:#A4C3F0 1px solid;
	WIDTH: 100%; COLOR: #FFFFFF;  POSITION: relative; HEIGHT: 23px;
	BACKGROUND-COLOR: #6297E5;

}

body{
	BORDER-RIGHT: 0px;
	BORDER-TOP: 0px;
	FONT-SIZE: 11px;
	MARGIN: 0px;
	BORDER-LEFT:0px;
	CURSOR: default;
	BORDER-BOTTOM: 0px;
	FONT-FAMILY: Verdana, Arial;
	BACKGROUND-COLOR: #FFFFFF;
}

</STYLE>



<SCRIPT language=JavaScript>

	var _oLast;

	function window.onload()
	{
		setActive(tdHome, true);
	}

	function getElm()
	{
		var o = event.srcElement;

		switch (o.tagName)
		{
			case "DIV": case "IMG": o = o.parentElement;
		}

		return o;
	}

	function load(obj)
	{
		top.main.closeWALL();
        top.main.openWALL(obj);
				
	}

	function setActive(o, bLoad)
	{
		if (_oLast && o != _oLast) glow(false, _oLast, true);

		//o.background = "_imgs/bar_bottom_down.gif";
		_oLast = o;

		if (!bLoad)
		{
			glow(true, o);
		}
	}

	function glow(bOn, o, bOverride)
	{
		if (!o)
		{
			var o = getElm();
			if (o.className != "pad") return false;
		}

		if (bOn)
		{
			if (o != _oLast)
			{
				o.background = "_imgs/bar_bottom_over.gif";
			}

			o.style.color = "#ffff00";
			o.children[0].style.filter = "Alpha(opacity=100)";
		}
		else if (o != _oLast || bOverride)
		{
			o.background = "";
			o.style.color = "#ddddee";
			o.children[0].style.filter = "Alpha(opacity=60)";
		}
	}

</SCRIPT>

<META content="MSHTML 6.00.2800.1226" name=GENERATOR></HEAD>
<BODY style="background-color:#6194DF" ba-ckground=footerimg/bar_bottom_bk.gif>
<TABLE class='tablemain' style="TABLE-LAYOUT: fixed; Z-INDEX: 10; WIDTH: 100%; HEIGHT: 24px" 
cellSpacing=0 cellPadding=0>
  <COLGROUP>
  <COL width=42>
  <COL width=165>
  <COL>
  <COL width=165>
  <COL>
  <COL width=165>
  <COL width=25>
  <TBODY>
  <TR onmouseover=glow(true); onmouseout=glow(false);>
    <TD>&nbsp;</TD>
    
    <TD class=pad id=tdHome title="" style="COLOR: #ffff00" onclick="load('XDV_WallObjMap');" noWrap>
       <IMG class=ico vspace="2" style="FILTER: Alpha(opacity=100)" src="footerimg//bar_bottom_ico_home.gif">Home Page
       <DIV>As minhas actividades</DIV>
    </TD>
      
    <TD><BR></TD>
    
    
    <TD class=pad id=tdWorkplace title="" onclick="" noWrap>
      <IMG class=ico vspace="2" src="footerimg//bar_bottom_ico_workplace.gif">Mesa de Trabalho
      <DIV>Calend౩o </DIV>
    </TD>
    
    <TD><BR></TD>
    
    <TD class=pad id=tdReports title=""  onclick="" noWrap>
        <IMG class=ico vspace="2" src="footerimg//bar_bottom_ico_reports.gif">Relat򱨯s
        <DIV>Mapas, Estatisticas</DIV>
        <DIV></DIV>
    </TD>
    <TD>&nbsp</TD>
  </TR>
  </TBODY></TABLE></BODY></HTML>
