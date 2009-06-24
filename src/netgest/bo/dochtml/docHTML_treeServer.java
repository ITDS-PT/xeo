/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import java.io.*;
import java.sql.*;
import java.util.*;
import netgest.bo.*;
import netgest.bo.def.*;
import javax.servlet.jsp.*;
import netgest.bo.ql.*;
import netgest.bo.runtime.*;
import netgest.utils.*;
import oracle.xml.parser.v2.*;

public class docHTML_treeServer 
{
    private static Hashtable runtimeTrees;
    public docHTML_treeServer()
    {
    }
    public static final void clearCache() {
        if(runtimeTrees != null) runtimeTrees.clear();
    }
    
    public static docHTML_treeRuntime getTree(boDefHandler bodef , ngtXMLHandler defTree,docHTML DOC )
    {
    
        if( runtimeTrees == null )   runtimeTrees=new Hashtable();
        
        String key=defTree.getAttribute("name")+DOC.getEboContext().getBoSession().getPerformerBoui();
        
        docHTML_treeRuntime toRet;
        toRet=(docHTML_treeRuntime)runtimeTrees.get( key );
        
        if ( toRet == null || toRet.p_haveErrors )
        {
            ngtXMLHandler defTreeUser=defTree;
            
            boConfig config=new boConfig();
            String xDir=config.getDeploymentDir();
            long pBoui= DOC.getEboContext().getBoSession().getPerformerBoui();
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
             
         
            
            toRet = new docHTML_treeRuntime( key , DOC, bodef , defTree , defTreeUser  );
            runtimeTrees.put( key , toRet );
        }
         
        return toRet;
    }
  
    public static docHTML_treeRuntime getTempTree(boDefHandler bodef , ngtXMLHandler defTree,docHTML DOC )
    {
        String key=defTree.getAttribute("name")+DOC.getEboContext().getBoSession().getPerformerBoui()+"TEMP";
        
        docHTML_treeRuntime toRet;
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
             
        toRet = new docHTML_treeRuntime( key , DOC, bodef , defTree , defTreeUser  );
        runtimeTrees.put( key , toRet );
        
        return toRet;
    }
    
    public static docHTML_treeRuntime getTree( String key )
    {
    
        if( runtimeTrees == null )   runtimeTrees=new Hashtable();
        
        
        
        docHTML_treeRuntime toRet;
        toRet=(docHTML_treeRuntime)runtimeTrees.get( key );
        return toRet;
    }
    public static void processTree ( String keyTree ,
                                    String drag_to_col_header ,
                                    String drag_to_col_group,
                                    String orderCol ,
                                    String openGroup,
                                    String closeGroup,
                                    int toggleOrderGroup,
                                    String fulltext,
                                    String textUserQuery,
                                    long bouiUserQuery,
                                    String treeOperation,
                                    String parametersQuery,
                                    docHTML DOC ) throws boRuntimeException
    {
        docHTML_treeRuntime tree = ( docHTML_treeRuntime )runtimeTrees.get( keyTree );
        if ( tree != null )
        {
            if ( drag_to_col_header != null && !drag_to_col_header.equals("") )
            {
                String xsrc=drag_to_col_header.split(":")[0];
                String xdest=drag_to_col_header.split(":")[1];
                tree.moveAttributeToColHeader( xsrc , xdest );
                
                saveUserTree(DOC.getEboContext().getBoSession().getPerformerBoui(),tree);
                
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
                tree.openGroup( openGroup );
            }
            else if ( closeGroup!=null && !closeGroup.equals("") )
            {
                tree.closeGroup( closeGroup );
            }
            if ( fulltext!= null )
            {
                if ( ! (fulltext.length() == 0 && tree.p_textFullSearch.length()== 0) )
                {
                    tree.p_textFullSearch=fulltext.trim();
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
            
            if ( toggleOrderGroup != -1 && toggleOrderGroup < tree.p_groups_order.length )
            {
               if ( tree.p_groups_order[ toggleOrderGroup ] == docHTML_treeRuntime.ORDER_ASC )
               {
                   tree.p_groups_order[ toggleOrderGroup ] = docHTML_treeRuntime.ORDER_DESC; 
               }
               else
               {
                   tree.p_groups_order[ toggleOrderGroup ] = docHTML_treeRuntime.ORDER_ASC;
               }
               tree.p_htmlCurrentPage=1;
            }
        }
        
    }
    
    public static void saveUserTree(long pBoui, docHTML_treeRuntime tree)
    {
         
         ngtXMLHandler treeUser=tree.buildUserXML();
         boConfig config=new boConfig();
         String xDir=config.getDeploymentDir();
         File file = new File(xDir+File.separator+pBoui+File.separator);
         file.mkdirs();
         ngtXMLUtils.saveXML( ( XMLDocument )treeUser.getNode(), file.getAbsolutePath()+File.separator+tree.p_key+".xml" );
    }


    public static docHTML_treeRuntime loadTree(EboContext ctx , long boui, docHTML DOC  ) throws boRuntimeException,java.io.IOException
    {
        boObject treeDef = boObject.getBoManager().loadObject(ctx,boui);
      
    
        boDefHandler xbodef = boDefHandler.getBoDefinition( treeDef.getAttribute("objectType").getValueString());
        docHTML_treeRuntime xtree=getTempTree(xbodef , xbodef.getPath(treeDef.getAttribute("viewer").getValueString()), DOC );    
        
        xtree.p_textUserQuery = treeDef.getAttribute("userQuery").getValueString().replaceAll("&gt;",">").replaceAll("&lt;","<");
		if(xtree.p_textUserQuery.equals(""))
          xtree.p_textUserQuery=null;
        xtree.p_filterName = treeDef.getAttribute("filter").getValueString();

        docHTML_treeAttribute[] atts = xtree.getAttributes();

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
            if(atts[i].p_name.equals(name))
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
        xtree.p_cols = (docHTML_treeAttribute[])colsVec.toArray(new docHTML_treeAttribute[colsVec.size()]);


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
            if(atts[i].p_name.equals(name))
            {
              goupsVec.add(atts[i]);
              break;
            }
        }
        xtree.p_groups = (docHTML_treeAttribute[])goupsVec.toArray(new docHTML_treeAttribute[goupsVec.size()]);
        String ordersArr[] = orders.split("##");
        xtree.p_groups_order = new byte[ordersArr.length];
        for (int i = 0; i < ordersArr.length; i++) 
        {
          if(ordersArr[i].equals("ASC"))
            xtree.p_groups_order[i] = docHTML_treeRuntime.ORDER_ASC;
          else
            xtree.p_groups_order[i] = docHTML_treeRuntime.ORDER_DESC;
        } 
        return xtree;
    }
    
    public static docHTML_treeRuntime copyTreeToTemp(docHTML_treeRuntime tree, docHTML DOC) 
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
      
      docHTML_treeRuntime toRet = new docHTML_treeRuntime(key,DOC,tree.p_bodef,tree.p_treeDef, defTreeUser);
      runtimeTrees.put( key , toRet );
      return toRet;
    }
}