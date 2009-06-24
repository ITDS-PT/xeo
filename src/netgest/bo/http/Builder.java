/*Enconding=UTF-8*/
package netgest.bo.http;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Enumeration;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.transaction.UserTransaction;

import netgest.bo.boConfig;
import netgest.bo.builder.ITask;
import netgest.bo.builder.boBuilder;
import netgest.bo.def.Tasks;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import java.io.FileReader;

public class Builder extends HttpServlet  {
    //logger
    private static Logger logger = Logger.getRootLogger();

    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appenderName = response.toString();
        Level sv = logger.getLevel();
        try {
            EboContext boctx=null;

            try {                                                
                
                
                boApplication  boapp = boApplication.getApplicationFromStaticContext("XEO");
                boSession bosess = null;
                
                boolean useInternalAuthentication=false;

                if (!isXEOUpAndRunning()) 
                  useInternalAuthentication=true;
                
                WriterAppender appender = null;
                try {
                    PatternLayout layout = new PatternLayout("%d (%F:%L) - %m%n");
                    appender = new WriterAppender(layout, response.getOutputStream());
                    appender.setName(appenderName);
                } catch(Exception e)
                {
                    logger.error("Error: ", e);
                }
                
                logger.addAppender(appender);
                logger.setLevel(Level.DEBUG); 
                               
                if (useInternalAuthentication)
                  bosess = boapp.boLogin( "SYSTEM", boLoginBean.getSystemKey(), boapp.getDefaultRepositoryName()  );
                else
                {
                  bosess = (boSession)request.getSession().getAttribute("boSession");
                  if (bosess==null || !bosess.getUser().getUserName().equalsIgnoreCase("SYSUSER"))
                  {
                    logger.error("Authentication was not performed or your Session TimedOut. You must authenticate before you can run Builder.");
                    return;
                  }
                }
                
                
                
                boctx = bosess.createRequestContext(request,response,null);

                UserTransaction ut=null;
                boolean ok = false;
                try
                {

//                    boctx.endTransaction();
                    logger.info("Starting suspending Agents");
                    boctx.getApplication().suspendAgents();
                    logger.info("Ended suspending Agents");
                    //boctx.beginContainerTransaction();
                    final InitialContext ic = new InitialContext();
                    ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");
                    ut.setTransactionTimeout(6000000);
                    ut.begin();
                    treatRequest(boctx, request, false);
//                    boctx.close();
                    ok = true;

                }
                catch (NamingException e)
                {
                    throw new RuntimeException(e.getClass().getName() + "\n" + e.getMessage()  );
                }
                finally
                {
                    if(ok)
                    {
                        ut.commit();
                        //boctx.commitContainerTransaction();
                    }
                    else
                    {
                        ut.rollback();
                        //boctx.rollbackContainerTransaction();
                    }
                    try
                    {
                        logger.info("Starting Agents");
                        boctx.getApplication().startAgents();
                        logger.info("Ended starting Agents");
                    }
                    catch (Exception e)
                    {
                        //ignore
                    }
                    boctx.close();
                }
                treatRequest(boctx, request, true);

                // After Build

                // packages
                boBuilder.setPackagesDescription(boctx);
                
                
                boolean isxeoupandrunning = isXEOUpAndRunning();
                String module = request.getParameter("module");
                // rebuild default workplaces
                if(module != null && module.indexOf("rebuildWorkplacesDefault") != -1) 
                {
                    logger.debug("Starting Rebuild Default WorkPlaces ...");
                    boBuilder.buildWorkPlaceDefault(boctx);
                    logger.debug("Ended Rebuild Default WorkPlaces");
                }

                // remove user workplaces
                if(module != null && module.indexOf("removeUserWorkplaces") != -1)
                {
                    logger.debug("Starting Remove User WorkPlaces ...");
                    boBuilder.removeUserWorkPlaces(boctx);
                    logger.debug("Ended Remove User WorkPlaces");
                }
                //Create System Users and Groups
                boBuilder.createSystemUsersandGroups(boctx);
                
                //Rebuild workplaces the first the system is deployed
                if (!isxeoupandrunning)
                {
                  logger.debug("Starting Rebuild Default WorkPlaces ...");
                  boBuilder.buildWorkPlaceDefault(boctx);
                   logger.debug("Ended Rebuild Default WorkPlaces");
                }
                
                String taskName = null;
                ITask task = null;

                if(module != null && module.indexOf("taskValidation") != -1)
                {
                    logger.debug("Starting Tasks Validation ...");
                    Enumeration oEnum = Tasks.getInstance().getActiveTasksNames();
                    while(oEnum.hasMoreElements())
                    {
                        taskName = (String)oEnum.nextElement();
                        task = Tasks.getInstance().getClass(taskName);
                        if(!task.done(boctx))
                        {
                            logger.debug("Starting [" + taskName + "] Validation");
                            if(task.validation(boctx))
                            {
                                logger.debug("Validation [" + taskName + "] : OK" );
                            }
                            else
                            {
                                logger.debug("Validation [" + taskName + "] : NOT OK" );
                            }
                            logger.debug("Ended [" + taskName + "] Validation");
                        }
                        else
                        {
                            logger.debug("Task [" + taskName + "] : DONE ALREADY");
                        }
                    }
                    logger.debug("Ended Tasks Validation");
                }

                if(module != null && module.indexOf("taskExecution") != -1)
                {
                    logger.debug("Starting Tasks Execution ...");
                    Enumeration oEnum = Tasks.getInstance().getActiveTasksNames();
                    while(oEnum.hasMoreElements())
                    {
                        taskName = (String)oEnum.nextElement();
                        task = Tasks.getInstance().getClass(taskName);
                        if(!task.done(boctx))
                        {
                            logger.debug("Starting [" + taskName + "] Task");
                            if(task.execute(boctx))
                            {
                                logger.debug("Execution [" + taskName + "] : OK" );
                            }
                            else
                            {
                                logger.debug("Execution [" + taskName + "] : NOT OK" );
                            }
                            logger.debug("Ended [" + taskName + "] Task.");
                        }
                        else
                        {
                            logger.debug("Task [" + taskName + "] : DONE ALREADY");
                        }
                    }
                    logger.debug("Ended Tasks Execution");
                }


/*                response.setContentType(CONTENT_TYPE);
                ScheduleAgentThread.suspendAgent();
                InitialContext ic = new InitialContext();
                UserTransaction ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");
                ut.begin();
                netgest.bo.builder.boBuilder bob = new netgest.bo.builder.boBuilder(boctx);
                bob.buildAll(bob);
                boctx.close();
                ut.commit();
                out.println("<html>");
                out.println("<head><title>boBuilder</title></head>");
                out.println("<body>");
                out.println("<p>The servlet has received a GET. This is the reply.</p>");
                out.println("</body></html>");
                out.close();
                ScheduleAgentThread.resumeAgent();*/
                //netgest.bo.system.RestartApplication.restartApplication();
                //Runtime.getRuntime()                ;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(boctx != null) boctx.close();
                logger.setLevel(sv);
            }
        } catch (Exception e) {
            PrintWriter out = response.getWriter();
            out.println("<PRE>");
            response.setStatus(response.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace(out);
            out.println("</PRE>");
        }
        finally
        {
            logger.removeAppender(appenderName);
        }
    }

    public void treatRequest(EboContext boctx, HttpServletRequest request, boolean m) throws boRuntimeException {
        String force = request.getParameter("force");
        String object = request.getParameter("object");
        String clean = request.getParameter("clean");
        String schema = request.getParameter("schema");
        String module = request.getParameter("module");

        if("true".equalsIgnoreCase(clean) && !m)
        {
            boBuilder.cleanboDefDeployment();
        }
        if(force != null && force.trim().length() > 0)
        {
            boBuilder.forceDeploy(force.trim());
        }
        if(!m)
        {
            /*
            if(object != null && object.trim().length() > 0)
            {
                int ret;
                if(schema != null && schema.trim().length() > 0)
                {
                    ret = boBuilder.build(boctx,object.trim(),schema,module);
                }
                else
                {
                    ret = boBuilder.build(boctx,object.trim(),schema,module);
                }
                if(ret == -1)
                {
                    logger.info("Objecto desconhecido");
                }
                else if(ret == -2)
                {
                    logger.info("Existe um build a decorrer");
                }
                else if(ret == -3)
                {
                    logger.info("Objecto está actualizado");
                }
            }
            else
            */
            {
                if(schema != null && schema.trim().length() > 0)
                {
                    boObject.getBoManager().makeAllObject(boctx, schema);
                }
                else
                {
                    boObject.getBoManager().makeAllObject(boctx);
                }

            }
        }
        else
        {
            boBuilder.setDeployed(boctx, module);
        }
    }
    /**
     * Temporario
     */
     /*
    public class MigrateDBFile
    {
        private EboContext context = null;
        private boConnectionManager connection = null;

        public MigrateDBFile(EboContext context)
        {
            this.context = context;
        }
        public boolean execute()
        {
            boolean result = false;
            connection = context.getConnectionManager();
            try
            {
                if(doMigration())
                {
                    Connection cn = connection.getConnection();
                    connection.beginContainerTransaction();
                    logger.info("Start migrating dbfs_file to new the version.");
                    execute(cn,"alter table dbfs_file add (KEY NUMBER)",false);
//                    execute(connection,"drop table dbfs_file_aux",false);
                    if(!execute(cn,"select id from dbfs_file_aux",false))
                    {
                        execute(cn,"create table dbfs_file_aux (ID NUMBER, FILENAME VARCHAR(250), BINDATA BLOB)",false);
                    }
                    result = copyTable(cn,"dbfs_file","dbfs_file_aux");
                    if(result)
                    {
                        boObjectList list = alterDocuments(cn);
                        if(list != null)
                        {
                            result = alterTableName(cn,"dbfs_file","dbfs_file_old");
                            if(result)
                            {
                                result = alterTableName(cn,"dbfs_version","dbfs_version_old");
                                if(result)
                                {
                                    result = alterTableName(cn,"dbfs_file_aux","dbfs_file");
                                    list.beforeFirst();
                                    boObject d = null;
                                    while(list.next())
                                    {
                                        try
                                        {
                                            d = list.getObject();
                                            if(isValid(d, true))
                                            {
                                                d.update();
                                            }
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    logger.info("Ended migrating dbfs_file to new the version.");
                }
                else
                {
                    result = true;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if(result)
                    {
                        connection.commitContainterTransaction();
                    }
                    else
                    {
                        connection.rollbackContainerTransaction();
                    }
                }
                catch (Exception e)
                {

                }
            }
            return result;
        }
        private boolean isValid(boObject object, boolean verbose)
        {
            boolean result = true;
            AttributeHandler attHandler = null;
            bridgeHandler bridge = null;
            boObject objAux = null;
            try
            {
                Enumeration oEnum = object.getAttributes().elements();
                while( oEnum.hasMoreElements() && result )
                {
                    attHandler = (AttributeHandler)oEnum.nextElement();
                    if(attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_OBJECTATTRIBUTE)
                    {
                        if(attHandler.getDefAttribute().getRelationType() == attHandler.getDefAttribute().RELATION_1_TO_1)
                        {
                            objAux = attHandler.getObject();
                            if(objAux != null)
                            {
                                if(!objAux.exists())
                                {
                                    result = false;
                                }
                            }
                        }
                        else
                        {
                            bridge = object.getBridge(attHandler.getName());
                            bridge.beforeFirst();
                            while(bridge.next())
                            {
                                objAux = bridge.getObject();
                                if(!objAux.exists() && result)
                                {
                                    result = false;
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                result = false;
            }
            try
            {
                if(!object.valid())
                {
                    result = false;
                }
            }
            catch (boRuntimeException e)
            {
                result = false;
            }
            if(!result && verbose)
            {
                logger.error("Object attributes invalid : " + object.getBoui());
            }
            return result;
        }
        private boolean doMigration()
        {
            boolean result = false;
            Connection cn = null;
            try
            {
                cn = this.context.getConnectionData();
                result =  execute(cn,"SELECT type FROM dbfs_file",false);
            }
            catch (SQLException e)
            {
                //ignore
            }
            finally
            {
                try
                {
                    if(cn != null)cn.close();
                }
                catch (Exception e)
                {

                }
            }
            return result;
        }
        public boolean roolBack()
        {
            boolean result = false;
            try
            {
                connection = this.context.getConnectionManager();
                boObjectList list = roolbackDocuments(connection.getConnection());
                if(list != null)
                {
                    result = execute(connection.getConnection(),"drop table dbfs_file",true);
                    if(result)
                    {
                        result = alterTableName(connection.getConnection(),"dbfs_file_old","dbfs_file");
                        if(result)
                        {
                            result = alterTableName(connection.getConnection(),"dbfs_version_old","dbfs_version");
                            if(result)
                            {
                                result = execute(connection.getConnection(),"update dbfs_file set key = null",true);
                                connection.commitContainterTransaction();
                                list.beforeFirst();
                                while(list.next())
                                {
                                    list.getObject().update();
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if(result)
                    {
                        connection.commitContainterTransaction();
                    }
                    else
                    {
                        connection.rollbackContainerTransaction();
                    }
                }
                catch (Exception e)
                {

                }
            }
            return result;
        }
        private boolean alterTableName(Connection connection,String fromTableName, String toTableName) throws SQLException
        {
            boolean result = false;
            StringBuffer sql = new StringBuffer("alter table ");
            sql.append(fromTableName).append(" RENAME TO ").append(toTableName);
            PreparedStatement pstm = null;
            try
            {
                pstm = connection.prepareStatement(sql.toString());
                pstm.execute();
                result = true;
            }
            catch (SQLException e)
            {
                throw e;
            }
            finally
            {
                try
                {
                    if(pstm != null) pstm.close();
                }
                catch (SQLException e)
                {
                     throw e;
                }
            }
            return result;
        }
        private boolean execute(Connection connection,String sql,boolean required) throws SQLException
        {
            boolean result = false;
            PreparedStatement pstm = null;
            try
            {
                pstm = connection.prepareStatement(sql);
                pstm.execute();
                result = true;
            }
            catch (SQLException e)
            {
                if(required)
                {
                    throw e;
                }
            }
            finally
            {
                try
                {
                    if(pstm != null) pstm.close();
                }
                catch (SQLException e)
                {
                    throw e;
                }
            }
            return result;
        }
        private boObjectList alterDocuments(Connection connection)
        {
            boolean result = false;
            boObjectList documentList = null;
            try
            {
                boObject document = null;
                iFile file = null;
                String path = null;
                documentList = boObjectList.list(this.context,"SELECT Ebo_DocBase WHERE file LIKE '//ngtbo/%'",1,999999,null,false,false);
                documentList.beforeFirst();
                while(documentList.next())
                {
                    document = documentList.getObject();
                    try
                    {
                        if(document.valid())
                        {
                            if(document.getAttribute("file") != null)
                            {
                                file = document.getAttribute("file").getValueiFile();
                                path = "//basic/" + file.getKey() + "/" + file.getName();
                                logger.info("Changing Ebo_DocBase : " + document.getBoui() + " , File : " + path);
                                document.getAttribute("file").setValueString(path);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("Cannot change document : " + document.getBoui() );
                    }
                }
                result = true;
            }
            catch (boRuntimeException e)
            {
                e.printStackTrace();
            }
            return documentList;
        }
        private boObjectList roolbackDocuments(Connection connection)
        {
            boolean result = false;
            boObjectList documentList = null;
            try
            {
                boObject document = null;
                String path = null;
                documentList = boObjectList.list(this.context,"SELECT Ebo_DocBase WHERE 1=1",false,false);
                documentList.beforeFirst();
                while(documentList.next())
                {
                    document = documentList.getObject();
                    path = "//ngtbo/Ebo_Documents/" + document.getBoui() + "/" + document.getAttribute("fileName").getValueString();
                    document.getAttribute("file").setValueString(path);
                }
                result = true;
            }
            catch (boRuntimeException e)
            {
                e.printStackTrace();
            }
            return documentList;
        }
        private boolean copyTable(Connection connection,String fromTableName, String toTableName) throws SQLException
        {
            boolean result = false;
            String fileName = null;
            long id = -1;
            long newId = -1;
            PreparedStatement pstm = null;
            ResultSet rslt = null;
            StringBuffer sql = new StringBuffer("SELECT ID,FILENAME FROM ");
            sql.append(fromTableName).append(" WHERE TYPE = 1 AND KEY IS NULL ORDER BY ID ASC");
            try
            {
                pstm = connection.prepareStatement(sql.toString());
                rslt = pstm.executeQuery();
                while( rslt.next() )
                {
                    id = rslt.getLong(1);
                    fileName = rslt.getString(2);
                    newId = DataUtils.getDBSequence(connection,fromTableName + "_SEQ","nextval");

                    if(createKey(connection,newId,fileName))
                    {
                        if(copyStream(getInputStream(connection,id),getOutputStream(connection,newId)))
                        {
                            insertKey(connection,id,newId);
                            logger.debug("Already copy ID : " + id  + " KEY : " + newId);
                        }
                    }
                }
                result = true;
            }
            catch (SQLException e)
            {
                throw e;
            }
            finally
            {
                try {if(rslt!=null) rslt.close();} catch (SQLException e) {};
                try {if(pstm!=null) pstm.close();} catch (SQLException e) {};
            }
            return result;
        }
        private OutputStream getOutputStream(Connection connection, long id)
        {
            OutputStream result =null;
            BLOB blob = null;
            ResultSet rslt=null;
            PreparedStatement pstm=null;
            try
            {
                connection.setAutoCommit(false);
                pstm = connection.prepareStatement("SELECT BINDATA FROM dbfs_file_aux WHERE ID = ? ");
                pstm.setLong(1,id);

                rslt = pstm.executeQuery();
                if(rslt.next())
                {
                    blob = (BLOB)rslt.getBlob(1);
                    result = blob.setBinaryStream(0);

                    // RCAMPOS -> removed due to deprecation, using newer method
                    //   compatible with JDBC 3.0
                    // result = blob.getBinaryOutputStream(0);
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try {if(rslt!=null) rslt.close();} catch (SQLException e) {};
                try {if(pstm!=null) pstm.close();} catch (SQLException e) {};
            }
            return result;
        }
        private InputStream getInputStream(Connection connection, long id)
        {

            InputStream result = null;
            Blob blob = null;
            ResultSet rslt = null;
            PreparedStatement pstm = null;
            try
            {
                pstm = connection.prepareStatement("SELECT BINDATA FROM dbfs_version WHERE ID=? AND VERSION = (SELECT MAX(VERSION) FROM dbfs_version WHERE ID = ?) FOR UPDATE WAIT 300");
                pstm.setLong(1,id);
                pstm.setLong(2,id);
                rslt = pstm.executeQuery();
                if(rslt.next())
                {
                    blob = rslt.getBlob(1);
                    result = blob.getBinaryStream();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try {if(rslt!=null) rslt.close();} catch (SQLException e) {};
                try {if(pstm!=null) pstm.close();} catch (SQLException e) {};
            }
            return result;
        }
        private boolean createKey(Connection connection,long newId,String fileName)
        {
            boolean result = false;
            String sql = "INSERT INTO dbfs_file_aux(ID,FILENAME,BINDATA) VALUES (?,?,?)";
            PreparedStatement pstm = null;
            try
            {
                pstm = connection.prepareStatement(sql);
                pstm.setLong(1,newId);
                pstm.setString(2,fileName);
                pstm.setBlob(3,BLOB.empty_lob());
                pstm.execute();
                result = true;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if(pstm != null) pstm.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            return result;
        }
        private boolean insertKey(Connection connection,long id, long key)
        {
            boolean result = false;
            String sql = "UPDATE dbfs_file SET KEY = ? WHERE ID = ?";
            PreparedStatement pstm = null;
            try
            {
                pstm = connection.prepareStatement(sql);
                pstm.setLong(1,key);
                pstm.setLong(2,id);
                pstm.execute();
                result = true;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if(pstm != null) pstm.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            return result;
        }
        private boolean copyStream(InputStream input, OutputStream output)
        {
            boolean result = false;
            try
            {
                if(input != null && output != null)
                {
                    byte buff[]= new byte[8192];
                    int br;
                    while((br=input.read(buff))>0)
                    {
                        output.write(buff,0,br);
                    }
                    output.close();
                    input.close();
                    result = true;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return result;
        }
        public void validation()
        {
            boolean ok = false;
            boObjectList documentList = null;
            try
            {
                boObject document = null;
                iFile file = null;
                String path = null;
                documentList = boObjectList.list(this.context,"SELECT Ebo_DocBase WHERE file LIKE '//ngtbo/%'",1,999999,null,false,false);
                documentList.beforeFirst();
                if(documentList.getRecordCount() > 0)
                {
                    while(documentList.next())
                    {
                        document = documentList.getObject();
                        try
                        {
                            if(document.getAttribute("file") != null)
                            {
                                if(isValid(document,false))
                                {
                                    ok = true;
                                }
                            }
                        }
                        catch (Exception e)
                        {
                        }
                        if(!ok)
                        {
                            logger.debug("Object invalid for migration: " + document.getBoui() + ", ClassName : " + document.getName() );
                        }
                    }
                }
                else
                {
                    logger.debug(" Migration already done ");
                }
            }
            catch (boRuntimeException e)
            {
                e.printStackTrace();
            }
            if(ok)
            {
                logger.debug("Validation successfull, you can now migrate.");
            }
        }
    }
    */
    
    public boolean isXEOUpAndRunning()
    {
      FileReader ixeouser = null;
      try
      {
       ixeouser=new FileReader(boConfig.getDeploymentDir()+"iXEOUser.xeoimodel");
       boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");
       boapp.boLogin( "SYSUSER" , boLoginBean.getSystemKey() , boapp.getDefaultRepositoryName() );  
       ixeouser.close();
       return true; 
      }
      catch (Exception e)
      {
        try
        {
          if (ixeouser!=null)ixeouser.close();          
        }
        catch (Exception ex)
        {
          
        }
        return false;
      }
    }
}
