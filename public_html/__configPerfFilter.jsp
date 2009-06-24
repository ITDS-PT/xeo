<%@ page contentType="text/html;charset=UTF-8"%>
<%
    netgest.bo.http.PerformanceConfigBean configBean = netgest.bo.http.PerformanceConfigBean.processRequest( request );
%>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Configuration do Performance Filter</title>
    <style type="text/css">
@import url('xeo.css');
body{
	BORDER-RIGHT: 0px;
	BORDER-TOP: 0px;
	FONT-SIZE: 11px;
	PADDING: 10px;
	BORDER-LEFT:0px; 
	CURSOR: default;
	BORDER-BOTTOM: 0px;
	FONT-FAMILY: Verdana, Arial;
	BACKGROUND-COLOR: #CCCCCC;
    SCROLL:NO;
}
h1{
	FONT-FAMILY: Verdana, Arial;
    FONT-SIZE:13px;
    color:#0009FF;
}
</style>
  </head>
  <body scroll="no">
    <div style="overflow:auto;height:100%;border:1px solid #333333;padding:2px;background-color:#FFFFFF">
      <h1>Configura&ccedil;&otilde;es do Filtro de m&eacute;tricas</h1>
      <form action="__configPerfFilter.jsp"> 
        <input type="HIDDEN" value="<%=configBean.prfconfigid%>" name="prfconfigid">
      <table>
        <tr>
          <td>
            <table>
              <tr>
                <td>Metricas de JSP</td>
              </tr>
              <tr>
                <td>
                  <table>
                    <tr>
                      <td>Activo</td>
                      <td>
                        <input name="metricjsp" type="CHECKBOX" <%=configBean.metricJSP?"checked=checked":""%> value="true"/>
                      </td>
                    </tr>
                    <tr>
                        <td>
                            Adicionar JSP
                        </td>
                        <td>
                            <input name="excludeJSPAdd">&nbsp;<input type="SUBMIT" value='>>' name="addExcludeJSP" />
                        </td>
                    </tr>
                    <tr>
                      <td>Exclus&otilde;es</td>
                      <td>
                        <select size="10" style="width='200px'" name="excludeJSP">
                          <%=configBean.getJspExcludes()%>
                        </select>
                      </td>
                    </tr>
                    <tr>
                        <td>
                        </td>
                        <td>
                            <input type="SUBMIT" value='Remover' name="removeExcludeJSP" />
                        </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </td>

          <td>
            <table>
              <tr>
                <td>Metricas de HTML</td>
              </tr>
              <tr>
                <td>
                  <table>
                    <tr>
                      <td>Activo</td>
                      <td>
                        <input name="metrichtml" type="CHECKBOX" <%=configBean.metricHTML?"checked=checked":""%> value="true"/>
                      </td>
                    </tr>
                    <tr>
                        <td>
                            Adicionar JSP
                        </td>
                        <td>
                            <input name="excludeHTMLAdd">&nbsp;<input type="SUBMIT" value='>>' name="addExcludeHTML" />
                        </td>
                    </tr>
                    <tr>
                      <td>Exclus&otilde;es</td>
                      <td>
                        <select size="10" style="width='200px'" name="excludeHTML">
                          <%=configBean.getHTMLExcludes()%>
                        </select>
                      </td>
                    </tr>
                    <tr>
                        <td>
                        </td>
                        <td>
                            <input type="SUBMIT" value='Remover' name="removeExcludeHTML" />
                        </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
            <td colspan="2">
                <hr>
            </td>
        </tr>
        <tr>
            <td align="center" colspan="2">
                <input name="apply" class="button" style="width=100px" type="SUBMIT" value="Aplicar" />
                <input name="save" class="button" style="width=100px" type="SUBMIT" value="Gravar" />
                <input name="revert" class="button" style="width=100px" type="SUBMIT" value="Reverter" />
            </td>
        </tr>
      </table>
      </form>

    </div>
  </body>
</html>
