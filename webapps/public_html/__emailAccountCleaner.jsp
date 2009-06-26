<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="javax.mail.*"%>
<%@ page import="javax.mail.internet.*"%>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Verificador das contas de email</title>
  </head>
  <body>  
    <% String result=null;
    if(request.getParameter("user")!=null && request.getParameter("password")!=null
				&& request.getParameter("host")!=null && request.getParameter("protocol")!=null) {
        String foldername;
        Date untilD = new Date();
        String msgid = null;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
        boolean cleanAll = false;
        if(request.getParameter("date")!=null && !"".equals(request.getParameter("date")))
        {
            untilD = sdf.parse(request.getParameter("date"));        
        }
        if(request.getParameter("msgid")!=null && !"".equals(request.getParameter("msgid")))
        {
            msgid = request.getParameter("msgid");        
        }
        String s = request.getParameter("all");
        if("ON".equalsIgnoreCase(request.getParameter("all")))
        {
            cleanAll = true;
        }
        if(request.getParameter("folder")!=null)
        {
            foldername = request.getParameter("folder");
        }
        else
        {
            foldername = "INBOX";
        }
                    Session _session=null;
					Store store=null;
                    Folder folder = null;
                    int nDelete = 0;
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
                        folder = store.getFolder(foldername);
                        folder.open(Folder.READ_WRITE);
                        int messageCount=folder.getMessageCount();
                        Message msg;
                        boolean stop = false;
                        for (int i = 1; !stop && i <= messageCount; i++) 
                        {
                            msg = folder.getMessage(i);
                            if(msgid != null)
                            {
                                msg.setFlag(Flags.Flag.DELETED, true);
                                nDelete++;
                                if(msgid.equalsIgnoreCase(((MimeMessage)msg).getMessageID()))
                                {
                                    %><!--STOPPING--><%
                                    stop = true;
                                }
                            }
                            else if(cleanAll)
                            {
                                msg.setFlag(Flags.Flag.DELETED, true);
                                nDelete++;
                            }
                            else if(msg.getReceivedDate() != null)
                            {
                                if(msg.getReceivedDate().before(untilD))
                                {
                                    msg.setFlag(Flags.Flag.DELETED, true);
                                    nDelete++;
                                }
                            }
                        }
//                        try
//                        {
//                            folder.expunge();
//                        }
//                        catch(Exception e)
//                        {<!--<%=e.getMessage() -->}
						result = "<p>Foram Eliminadas " + nDelete + " mensagens de " + messageCount + "</p>";
					}
                    catch(Exception e)
					{
                        result = "<p><font color=\"#FF0000\">Erro: " + e.getMessage() + "</font></p>";
                    }
                    finally
                    {
                        try
                        {
                            if(folder != null)
                            {
                                folder.close(true);
                            }
                        }catch(Exception _e)
                        {%><!--<%=_e.getMessage()%> --><%}
                        try
                        {
                            store.close();
                        }catch(Exception _e)
                        {%><!--<%=_e.getMessage()%> --><%}						
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
		<p> Limpar a directoria:
            <input type="text" name="folder" size="50">
        </p>
        <p>
            <input type="checkbox" name="all">Apagar todas as Mensagens
        </p>
        <p> Apagar todas MSGS até MSG com o id:
            <input type="text" name="msgid" size="50">
        </p>
		<p> Apagar emails até Data('dd-mm-yyyy'):
            <input type="text" name="date" size="50">
        </p>
        <p>
            <input type="submit" value="Apagar">
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
