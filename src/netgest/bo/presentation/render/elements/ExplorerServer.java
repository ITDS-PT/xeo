/*Enconding=UTF-8*/
package netgest.bo.presentation.render.elements;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import netgest.bo.*;
import netgest.bo.def.*;
import javax.servlet.jsp.*;
import netgest.bo.presentation.render.elements.cache.Cache;
import netgest.bo.ql.*;
import netgest.bo.runtime.*;
import netgest.bo.dochtml.*;
import netgest.utils.*;
import oracle.xml.parser.v2.*;

public class ExplorerServer 
{
    private static Cache runtimeExplorer = new Cache("Runtime Explorers", 60, 3200, 200);
    public ExplorerServer()
    {
    }
    public static void clearCache() {
        runtimeExplorer.clear();
    }
    
    public static int cacheSize() {
        return runtimeExplorer.getSize();
    }
    
    public static int cacheShrinktime() {
        return runtimeExplorer.getShrinkTimes();
    }
    
    public static String getCacheName() {
        return runtimeExplorer.getName();
    }
    
    public static String getState() {
        return runtimeExplorer.getState();
    }
    
    public static final void setDefaultExplorer()
    {
        //primeiro vou apagar todos os ficheiros nas directorias de definições explorer
        boConfig config=new boConfig();
        String xDir=config.getDeploymentDir();
        File deployDir = new File(xDir);
        File all[] = deployDir.listFiles();
        File treeXML[];
        String auxName;
        boolean numericDir;
        for (int i = 0; i < all.length; i++) 
        {
            numericDir = false;
            if(all[i].isDirectory())
            {
                auxName = all[i].getName();
                try
                {
                    Long.parseLong(auxName);
                    numericDir = true;
                }catch(Exception e){/*IGNORE*/}
                if(numericDir)
                {
                    treeXML = all[i].listFiles();
                    for (int j = 0 ;j < treeXML.length ; j++) 
                    {
                        if(treeXML[j].isFile() &&
                           treeXML[j].getName() != null && 
                           treeXML[j].getName().toUpperCase().endsWith(".XML") &&
                           treeXML[j].canWrite()
                        )
                        {
                            treeXML[j].delete();
                        }
                    }
                }
            }
        }
        
        //vou limpar todas as arvores da cache
        runtimeExplorer.clear();
    }
    
    public static final void setDefaultExplorer(long user)
    {
        if(user < 1) return;
        //primeiro vou apagar todos os ficheiros das definições explorer
        boConfig config=new boConfig();
        String xDir=config.getDeploymentDir();
        File userDir = new File(xDir+File.separator+user);
        if(userDir.exists() && userDir.canWrite() && userDir.isDirectory())
        {
            File[] f = userDir.listFiles();
            for (int i = 0; f != null && i < f.length; i++) 
            {
                if(f[i].isFile() && f[i].exists() && f[i].canWrite())
                {
                    f[i].delete();
                }
            }
        }

        //vou limpar todas as arvores da cache deste utilizador
        runtimeExplorer.remove(user);
    }
    
    public static final void setDefaultExplorer(String keyTree)
    {
        if(keyTree == null || keyTree.trim().length() == 0) return;
        int barIndex = -1;
        long userBoui = -1;
        barIndex = keyTree.lastIndexOf("-");
        if(barIndex != -1)
        {
            try
            {
                userBoui = Long.parseLong(keyTree.substring(barIndex + 1));
                setDefaultExplorer(keyTree, userBoui);
            }
            catch (Exception e)
            {
                //ignore
            }
        }
    }
    
    public static final void setDefaultExplorer(String keyTree,long user)
    {
        if(keyTree == null || keyTree.trim().length() == 0) return;
        if(user < 1) return;
        //primeiro vou apagar o ficheiro das definições
        boConfig config=new boConfig();
        String xDir=config.getDeploymentDir();
        File file = new File(xDir+File.separator+user+File.separator+keyTree+".xml");
        if(file.isFile() && file.exists() && file.canWrite())
        {
            file.delete();
        }

        //vou limpar da cache esta árvore
        runtimeExplorer.remove(keyTree);
    }
    
    public static final void clearCacheExplorerWPrefix(String prefix)
    {
        if(prefix == null || prefix.trim().length() == 0) return;

        //vou limpar da cache esta árvore
        runtimeExplorer.removeWPrefix(prefix);
    }
    
    public static Explorer getExplorer(boDefHandler bodef , ngtXMLHandler defTree, docHTML doc, docHTML_controler docList, String pageName,String subkey)
    {
        
        String key=defTree.getAttribute("name")+subkey+"-"+ doc.getEboContext().getBoSession().getPerformerBoui();
        
        Explorer toRet;
        toRet=(Explorer)runtimeExplorer.get( key );
        
        if ( toRet == null || toRet.p_haveErrors )
        {
            ngtXMLHandler defTreeUser=defTree;
            
            boConfig config=new boConfig();
            String xDir=config.getDeploymentDir();
            long pBoui= doc.getEboContext().getBoSession().getPerformerBoui();
            File file = new File(xDir+File.separator+pBoui+File.separator+key+".xml");
            boolean readDefUser=true;
            if ( toRet != null ) 
            {
                readDefUser=false;
            }
            if ( file.exists() && readDefUser )
            {
             defTreeUser = new ngtXMLHandler( ngtXMLUtils.loadXMLFile( file.getAbsolutePath() ));
             defTreeUser = defTreeUser.getFirstChild();
            }
             
         
            
            toRet = new Explorer( key , doc, docList, bodef , defTree , defTreeUser, pageName);
            runtimeExplorer.put( key , toRet, doc.getEboContext().getBoSession().getPerformerBoui() );
        }
         
        return toRet;
    }
    
    public static Explorer getExplorerWKey(boDefHandler bodef , ngtXMLHandler defTree, docHTML doc, docHTML_controler docList, String pageName,String key)
    {
        
        Explorer toRet;
        toRet=(Explorer)runtimeExplorer.get( key );
        
        if ( toRet == null || toRet.p_haveErrors )
        {
            ngtXMLHandler defTreeUser=defTree;
            
            boConfig config=new boConfig();
            String xDir=config.getDeploymentDir();
            long pBoui= doc.getEboContext().getBoSession().getPerformerBoui();
            File file = new File(xDir+File.separator+pBoui+File.separator+key+".xml");
            boolean readDefUser=true;
            if ( toRet != null ) 
            {
                readDefUser=false;
            }
            if ( file.exists() && readDefUser )
            {
             defTreeUser = new ngtXMLHandler( ngtXMLUtils.loadXMLFile( file.getAbsolutePath() ));
             defTreeUser = defTreeUser.getFirstChild();
            }
             
         
            
            toRet = new Explorer( key , doc, docList, bodef , defTree , defTreeUser, pageName);
            runtimeExplorer.put( key , toRet, doc.getEboContext().getBoSession().getPerformerBoui() );
        }
         
        return toRet;
    }
    
    public static boolean hasExplorer(String defTreeName, long performerBoui, String subKey)
    {
        Explorer toRet = (Explorer)runtimeExplorer.get( defTreeName+subKey+"-"+ performerBoui );
        if(toRet == null)
        {
            return false;
        }
        return true;
    }
  
    public static Explorer getTempExplorer(boDefHandler bodef , ngtXMLHandler defTree,docHTML DOC, docHTML_controler docList, String pageName )
    {
        String key=defTree.getAttribute("name")+DOC.getEboContext().getBoSession().getPerformerBoui()+"TEMP";
        Explorer toRet;
        ngtXMLHandler defTreeUser=defTree;
            
        boConfig config=new boConfig();
        String xDir=config.getDeploymentDir();
        long pBoui= DOC.getEboContext().getBoSession().getPerformerBoui();
        File file = new File(xDir+File.separator+pBoui+File.separator+key+".xml");

        if ( file.exists()  )
        {
          defTreeUser = new ngtXMLHandler( ngtXMLUtils.loadXMLFile( file.getAbsolutePath() ));
          defTreeUser = defTreeUser.getFirstChild();
        }  
        toRet = new Explorer( key , DOC, docList, bodef , defTree , defTreeUser, pageName  );
        runtimeExplorer.put( key , toRet, DOC.getEboContext().getBoSession().getPerformerBoui() );
        
        return toRet;
    }
    
    public static Explorer getTempExplorerForAutoGenerated(boDefHandler bodef,Explorer explorer,docHTML DOC,docHTML_controler docList, String pageName)
    {
        String key=explorer.getExplorerName()+"TEMP";
        ngtXMLHandler defTreeUser=null;
        Explorer toRet=explorer;
            
        boConfig config=new boConfig();
        String xDir=config.getDeploymentDir();
        long pBoui= DOC.getEboContext().getBoSession().getPerformerBoui();
        File file = new File(xDir+File.separator+pBoui+File.separator+key+".xml");

        if ( file.exists()  )
        {
          defTreeUser = new ngtXMLHandler( ngtXMLUtils.loadXMLFile( file.getAbsolutePath() ));
          defTreeUser = defTreeUser.getFirstChild();
          toRet = new Explorer( key , DOC, docList, bodef , defTreeUser , defTreeUser, pageName  );
        }  
        
        runtimeExplorer.put( key , toRet, DOC.getEboContext().getBoSession().getPerformerBoui() );
        
        return toRet;      
    }
    
    public static Explorer getExplorer( String key )
    {
        
        Explorer toRet;
        toRet=(Explorer)runtimeExplorer.get( key );
        return toRet;
    }
    public static void processExplorer ( String keyTree ,
                                    String drag_to_col_header ,
                                    String drag_to_col_group,
                                    String orderCol ,
                                    String openGroup,
                                    String closeGroup,
                                    int toggleOrderGroup,
                                    String fulltext,
                                    String textUserQuery,
                                    long bouiUserQuery,
                                    long bouiSvExplorer,
                                    String treeOperation,
                                    String parametersQuery,
                                    long bouiPreview,
                                    String toExecute,
                                    docHTML DOC ) throws boRuntimeException
    {
        Explorer tree = ( Explorer )runtimeExplorer.get( keyTree );
        if ( tree != null )
        {
            if ( drag_to_col_header != null && !drag_to_col_header.equals("") )
            {
                String xsrc=drag_to_col_header.split(":")[0];
                String xdest=drag_to_col_header.split(":")[1];
                tree.moveAttributeToColHeader( xsrc , xdest );
                
                saveUserTree(DOC.getEboContext().getBoSession().getPerformerBoui(),tree);
                if(tree.p_svExplorer > 0)
                {
                    tree.setBouiSvExplorer( DOC.getEboContext() , -1 );
                    tree.p_svExplorerName="";
                }
                
            }
            else if ( drag_to_col_group!=null && !drag_to_col_group.equals(""))
            {
                String xsrc=drag_to_col_group.split(":")[0];
                String xdest=drag_to_col_group.split(":")[1];
                tree.moveAttributeToColGroup( xsrc , xdest );
                saveUserTree(DOC.getEboContext().getBoSession().getPerformerBoui(),tree);       
                
            }
            else if ( orderCol!=null && !orderCol.equals("") )
            {
                tree.setOrderCol( orderCol );
                saveUserTree(DOC.getEboContext().getBoSession().getPerformerBoui(),tree);   
            }
            else if ( openGroup!=null && !openGroup.equals("") )
            {
                tree.openGroup( openGroup, 1 );
            }
            else if ( closeGroup!=null && !closeGroup.equals("") )
            {
                tree.closeGroup( closeGroup );
            }
            if ( fulltext!= null )
            {
                if ( ! (fulltext.length() == 0 && tree.p_textFullSearch.length()== 0) )
                {                    
                    tree.setExplorerFullText(fulltext.trim());
                    tree.p_htmlCurrentPage=1;
                }
            }
            if ( textUserQuery!=null && !textUserQuery.equals("") )
            {
                if ( textUserQuery.indexOf("cleanFilter")!=-1 )
                {
                    tree.setTextUserQuery( DOC.getEboContext(), null);
                }
                else tree.setTextUserQuery( DOC.getEboContext(), textUserQuery );
                tree.p_htmlCurrentPage=1;
       //         tree.p_bouiUserQuery=-1;
                
            }
            if ( parametersQuery!=null )
            {
            
                tree.setParametersQuery( parametersQuery );    
            }
            
            if ( treeOperation!=null && !treeOperation.equals("") )
            {
                if ( treeOperation.equals("nextPage") )
                {
                    tree.p_htmlCurrentPage++;
                }
                else if ( treeOperation.equals("previousPage") )
                {
                    tree.p_htmlCurrentPage--;
                    if ( tree.p_htmlCurrentPage <=0 ) 
                    {
                        tree.p_htmlCurrentPage=1;
                    }
                }
               else if ( treeOperation.equals("firstPage") )
               {
                    tree.p_htmlCurrentPage=1;
               }
            }

            
            if (  bouiUserQuery!=-1 )
            {
                if (bouiUserQuery==0 )
                {
                     //tree.p_bouiUserQuery=-1;
                     tree.setBouiUserQuery( DOC.getEboContext() , -1 );
                     tree.p_filterName="";
                }
                else  tree.setBouiUserQuery( DOC.getEboContext() , bouiUserQuery );//=bouiUserQuery;
                
            }
            
            if (  bouiSvExplorer!=-1 )
            {
                if (bouiSvExplorer==0 )
                {
                     //tree.p_bouiUserQuery=-1;
                     tree.setBouiSvExplorer( DOC.getEboContext() , -1 );
                     tree.p_svExplorerName="";
                }
                else  tree.setBouiSvExplorer( DOC.getEboContext() , bouiSvExplorer );//=bouiUserQuery;
                
            }
            
            if (  bouiPreview > 0 )
            {                
                tree.setPreviewObject(bouiPreview);
            }
//            else
//            {
//                tree.setPreviewObject(-1);
//            }
            
            if ( toggleOrderGroup != -1 && toggleOrderGroup < tree.getGroupProvider().groupOrderSize())
            {
               if ( tree.getGroupProvider().getGroupOrder(toggleOrderGroup) == Explorer.ORDER_ASC )
               {
                   tree.getGroupProvider().setGroupOrder( toggleOrderGroup, Explorer.ORDER_DESC); 
               }
               else
               {
                   tree.getGroupProvider().setGroupOrder( toggleOrderGroup, Explorer.ORDER_ASC);
               }
               tree.p_htmlCurrentPage=1;
            }
            
            if(toExecute != null && toExecute.startsWith("STATIC-openDocFromExplorer"))
            {
                //STATIC-openDocFromExplorer;90906167|90906168
                //to: STATIC-netgest.bo.impl.document.DocumentHelper.openDocumentInClient;this|90906168
                String[] statOut = toExecute.split("-");    
                String[] mths = statOut[statOut.length -1].split(";");
                String[] parms = mths[mths.length -1].split("\\|");
                String[] path = mths[0].split("\\.");
                
                boObject lastobj= boObject.getBoManager().loadObject(DOC.getEboContext(), Long.parseLong(parms[0]));
                String execute = "STATIC-netgest.bo.impl.document.DocumentHelper.openDocumentInClient;this|"+parms[1];
                try
                {
                    executeStaticMethod(DOC, lastobj, execute);
                }
                catch (Exception e)
                {
                    throw new boRuntimeException("ExplorerServer", "processExplorer", e);
                }
                
            }
        }
        
    }
    
    public static void saveUserTree(long pBoui, Explorer tree)
    {

         ngtXMLHandler treeUser=tree.buildUserXML();
         boConfig config=new boConfig();
         String xDir=config.getDeploymentDir();
         File file = new File(xDir+File.separator+pBoui+File.separator);
         file.mkdirs();
         ngtXMLUtils.saveXML( ( XMLDocument )treeUser.getNode(), file.getAbsolutePath()+File.separator+tree.p_key+".xml" );
         System.out.println("Tree def a escrever:" + file.getAbsolutePath()+File.separator+tree.p_key+".xml");

    }


    public static Explorer loadTree(EboContext ctx , long boui, docHTML doc, docHTML_controler docList, String pageName  ) throws boRuntimeException,java.io.IOException
    {
        boObject treeDef = boObject.getBoManager().loadObject(ctx,boui);
      
    
        boDefHandler xbodef = boDefHandler.getBoDefinition( treeDef.getAttribute("objectType").getValueString());
        //O nome de um explorador é descoberto através do atributo form que é passado ao explorer.jsp
        //no entanto caso esse atributo não seja passado então o nome do explorador no explorer.jsp é: nome do objecto + boui do user
        //Logo este explorador não deve ser visto nas definições do Objecto mas na pasta de deploy
        Explorer xtree=null;
        try
        {
          xtree=getTempExplorer(xbodef , xbodef.getPath(treeDef.getAttribute("viewer").getValueString()), doc, docList, pageName );    
        }
        catch (Exception e)
        {
          //Do Nothing and continue processing
          if (xtree==null)
          {
            
            Explorer explorer = Explorer.getExplorer(xbodef.getName(), doc, docList, pageName);
            xtree=getTempExplorerForAutoGenerated(xbodef,explorer,doc,docList,pageName);                  
          }
        }         
        
        xtree.p_textUserQuery = treeDef.getAttribute("userQuery").getValueString().replaceAll("&gt;",">").replaceAll("&lt;","<");
		if(xtree.p_textUserQuery.equals(""))
          xtree.p_textUserQuery=null;
        xtree.p_filterName = treeDef.getAttribute("filter").getValueString();
        xtree.p_svExplorerName = treeDef.getAttribute("name").getValueString();

        ColumnProvider[] atts = xtree.getAttributes();

        Vector colsVec = new Vector();
        bridgeHandler treeCols = treeDef.getBridge("cols");
        treeCols.beforeFirst();
        while(treeCols.next())
        {
          boObject attDef = treeCols.getObject();
          String name = attDef.getAttribute("name").getValueString();
          long width = attDef.getAttribute("width").getValueLong();
          String order = attDef.getAttribute("order").getValueString();
          long orderPos = attDef.getAttribute("orderPos").getValueLong();
          
          for (int i = 0; i < atts.length; i++)
          {
            if(atts[i].getName().equals(name))
            {
              atts[i].setWidth((int)width);
              colsVec.add(atts[i]);
              if(orderPos>0)
              {
                xtree.p_orders[(int)orderPos-1] = name;
                xtree.p_ordersDirection[(int)orderPos-1] = order;
              }
              break;
            }
          }
        }
        xtree.getColumnsProvider().setColumns((ColumnProvider[])colsVec.toArray(new ColumnProvider[colsVec.size()]));


        Vector goupsVec = new Vector();
        String orders = "";
        bridgeHandler treeGroups = treeDef.getBridge("groups");
        treeGroups.beforeFirst();
        while(treeGroups.next())
        {
          boObject attDef = treeGroups.getObject();
          String name = attDef.getAttribute("name").getValueString();
          String order = attDef.getAttribute("order").getValueString();
          orders += order + "##";
            
          for (int i = 0; i < atts.length; i++)
            if(atts[i].getName().equals(name))
            {
              goupsVec.add(atts[i]);
              break;
            }
        }
        xtree.getGroupProvider().setGroups((ColumnProvider[])goupsVec.toArray(new ColumnProvider[goupsVec.size()]));
        String ordersArr[] = orders.split("##");
        xtree.getGroupProvider().setGroupOrder(new byte[ordersArr.length]);
        for (int i = 0; i < ordersArr.length; i++) 
        {
          if(ordersArr[i].equals("ASC"))
            xtree.getGroupProvider().setGroupOrder(i, Explorer.ORDER_ASC);
          else
            xtree.getGroupProvider().setGroupOrder(i, Explorer.ORDER_DESC);
        } 
        return xtree;
    }
    
    public static Explorer copyTreeToTemp(Explorer tree, docHTML DOC, docHTML_controler docList, String pageName) 
    { 
      String key = tree.p_key + "TEMP";
      
      ngtXMLHandler defTreeUser=tree.p_treeDef;
            
      boConfig config=new boConfig();
      String xDir=config.getDeploymentDir();
      long pBoui= DOC.getEboContext().getBoSession().getPerformerBoui();
      File file = new File(xDir+File.separator+pBoui+File.separator+tree.p_key+".xml");
      if ( file.exists() )
      {
        defTreeUser = new ngtXMLHandler( ngtXMLUtils.loadXMLFile( file.getAbsolutePath() ));
        defTreeUser = defTreeUser.getFirstChild();
      }      
      
      Explorer toRet = new Explorer(key,DOC,docList, tree.p_bodef,tree.p_treeDef, defTreeUser,pageName);
      runtimeExplorer.put( key , toRet, DOC.getEboContext().getBoSession().getPerformerBoui() );
      return toRet;
    }
    
    public static void bindData(String values)
    {
        ngtXMLHandler xx=(new ngtXMLHandler(values));
        ngtXMLHandler[] childnodes = xx.getChildNodes();            
        if (childnodes.length > 0) 
        {
            String explorerName = childnodes[0].getAttribute("name");
            String menuName = childnodes[0].getAttribute("menuName");
            String menuItem = null;
            String menuItemValue = null;
            Explorer e = (Explorer)getExplorer(explorerName);
            if(e != null)
            {
                childnodes = childnodes[0].getChildNodes();
                if (childnodes.length > 0)
                {
                    menuItem = childnodes[0].getNodeName();
                    menuItemValue = childnodes[0].getNode().getFirstChild().getNodeValue();
                }
                if("group".equals(menuItem))
                {                    
                    if("true".equals(menuItemValue))
                    {
                        e.getGroup().setDisplay(true);
                    }
                    else
                    {
                        e.getGroup().setDisplay(false);
                    }
                }
                else if("parameters".equals(menuItem))
                {
                    if("true".equals(menuItemValue))
                    {
                        e.getParameters().setDisplay(true);
                    }
                    else
                    {
                        e.getParameters().setDisplay(false);
                    }
                }
                else if("preview".equals(menuItem))
                {
                    if("right".equals(menuItemValue))
                    {
                        e.setPreviewRight();
                    }
                    else if("down".equals(menuItemValue))
                    {
                        e.setPreviewDown();
                    }
                    else
                    {
                        e.setShowPreview(false);
                    }
                }
            }
        }
    }
	
	public static void addExplorer(String key, Explorer e)
	{
		runtimeExplorer.put(key,e, e.getUser());
	}
    
    private static boolean executeStaticMethod(docHTML doc, boObject lastobj, String toExecute) throws ClassNotFoundException,NoSuchMethodException,InvocationTargetException,IllegalAccessException
    {
        boolean result = false;
        String[] statOut = toExecute.split("-");    
        String[] mths = statOut[statOut.length -1].split(";");
        String[] parms = mths[mths.length -1].split("\\|");
        String[] path = mths[0].split("\\.");
        String cl = "";
        for (int i = 0; i < path.length -1; i++) 
        {
            cl +=path[i];
            if((i+1)< (path.length -1))
            {
                cl += ".";
            }
        }
        
        Class clss = Class.forName(cl);                           
        Class partypes[] = new Class[parms.length ];                    
        for(int i = 0; i < parms.length  ; i++)
        {
            if("this".equals(parms[i]))
            {                    
                partypes[i] = boObject.class;                    
            }
            else
            {
                partypes[i] = String.class;       
            }                        
        }
        Method ometh = clss.getMethod(path[path.length -1],partypes);                    
        
        Object args[] = new Object[parms.length];                                        
        for(int i = 0; i < parms.length  ; i++)
        {
            if("this".equals(parms[i])){                    
                args[i] = lastobj; 
            }
            else if("docid".equals(parms[i])){                    
                args[i] = ""+doc.getDocIdx(); 
            }
            else if("pooluniquedocid".equals(parms[i])){                    
                args[i] = doc.poolUniqueId(); 
            }
            else
            {
                args[i] = parms[i];       
            }                        
        }                    
        ometh.invoke(null,args);        
        result = true;
        return result;
    }
}