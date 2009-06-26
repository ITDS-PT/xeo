<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="javax.mail.*"%>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Verificador das contas de email</title>
  </head>
  <body>  
    <% String result=null;
    if(request.getParameter("user")!=null && request.getParameter("password")!=null
				&& request.getParameter("host")!=null && request.getParameter("protocol")!=null) {
                    Session _session=null;
					Store store=null;
					try 
					{
						Properties props = new Properties();
						String user = request.getParameter("user");
						String pwd = request.getParameter("password");
						String host = request.getParameter("host");
						String protocol = request.getParameter("protocol");
	      		
						_session = Session.getDefaultInstance(props, null);
                        store = _session.getStore(protocol);
                        store.connect(host,user,pwd);
                        Folder folder = store.getFolder("INBOX");
                        folder.open(Folder.READ_ONLY);
                        result = "<p>Existem "+folder.getMessageCount()+" mensagens</p>";
                        result += "<p>Existem "+folder.getUnreadMessageCount()+" por lêr..</p>";
                        result += "<p>Existem "+folder.getNewMessageCount()+" novas mensagens...</p>";
                        folder.close(false);
						result = result + "<p>Ligação efectuada com sucesso</p>";
					}
                    catch(Exception e)
					{                     
                        try
                        {
                            if(store != null)
                            {
                                store.close();
                            }
                        }catch(Exception _e)
                        {%><!--<%=_e.getMessage()%> --><%}
						result = "<p><font color=\"#FF0000\">Erro: " + e.getMessage() + "</font></p>";
					}
   }

            

%>
    <h4>Dados da Conta de email</h4>
    <form>
        <p> Nome do Utilizador:
            <input type="text" name="user" size="50">            
        </p>
				<p> Password:
            <input type="password" name="password" size="50">
        </p>
				<p> Host:
            <input type="text" name="host" size="50">
        </p>
				<p> Protocolo:
            <input type="text" name="protocol" size="50">
        </p>
				<p>
					<input type="submit" value="Testar">
				</p>
    </form>    
    <p>
      <% if(result !=null) {%>
      
      <center><h4>Resultado</h4></center>
    </p>
    <div>
      <%= result%>
    </div>    
    <% }%>
  </body>
</html>
