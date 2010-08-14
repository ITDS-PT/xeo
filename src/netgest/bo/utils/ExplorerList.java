/*Enconding=UTF-8*/
package netgest.bo.utils;
import java.io.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import netgest.bo.def.*;
import netgest.bo.impl.*;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.presentation.render.elements.ColumnProvider;
import netgest.bo.presentation.render.elements.ColumnsProvider;
import netgest.bo.presentation.render.elements.Explorer;
import netgest.bo.runtime.*;
import netgest.bo.utils.ExplorerPrintResultSet;
import netgest.utils.*;
import javax.servlet.http.HttpServletResponse;

import oracle.xml.parser.v2.*;
import netgest.bo.dochtml.*;
import java.sql.*;
import netgest.bo.system.Logger;
import java.util.Vector;

import netgest.bo.localized.JSPMessages;


public final class ExplorerList  implements Serializable {
    
    private Connection cn = null;
    private Vector groups = new Vector();
    private int nrgroups=0;
    private ColumnsProvider colProvider;
    private Explorer tree = null;
    private EboContext ctx;
    private Hashtable lovs;
    private static final String OBJECT_ERROR = "<span class='lui'><img style='cursor:hand' hspace='3' border='0' align='absmiddle' class='lui' title='Inválido' src='resources/critical.gif' width='16' height='16'/><IMG src='resources/none.gif' height=16 width=16 /><span title='"+ JSPMessages.getString("ExplorerList.2")+" "+ JSPMessages.getString("ExplorerList.3")+"'>"+ JSPMessages.getString("ExplorerList.2")+"</span></span>";
    private static final String OBJECT_ERROR_NO_IMG = "<span class='lui'><span title='"+ JSPMessages.getString("ExplorerList.2")+" "+ JSPMessages.getString("ExplorerList.3")+"'>"+ JSPMessages.getString("ExplorerList.2")+"</span></span>";
    
    private int start = 25;
    private int max_size = 0;
    private int group_s = 0;
    
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.utils.ExplorerList");
    
    public ExplorerList(EboContext ctx, Explorer tree)  throws boRuntimeException, SQLException
    {
      cn= ctx.getConnectionData();
      colProvider = tree.getColumnsProvider();
      groups = new Vector();
      nrgroups=tree.getGroupProvider().groupSize();
      this.tree=tree;
      this.ctx=ctx;

      String sqlGroups=tree.getSqlGroups( ctx );
        
      if ( sqlGroups != null )
      {
          //Connection cn= DOC.getEboContext().getConnectionData();
          PreparedStatement pr=null;
          ResultSet rslt=null;
          try{
              pr=cn.prepareStatement( sqlGroups , ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );
              for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
              {
                  pr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
              }
              rslt= pr.executeQuery();
              while(rslt.next())
              { 
                  String group[] = new String[nrgroups+1];
                  for (int i = 0; i < nrgroups; i++) 
                  {
                          group[i] = tree.getGroupValue(ctx, rslt, i+1);
                  }
                  group[nrgroups] = ""+rslt.getInt(nrgroups+1);
                  putGroup(group);
              }
          }
          finally
          {
            if(rslt!=null)
              rslt.close();
            if(pr!=null)
              pr.close();
          }
      }
      


//*****************************
      //construir hashtable de lovs, caso seja necessário
      String lovCond = null;
      for (int i = 0; i < colProvider.columnsSize(); i++) 
      {
         if(colProvider.getColumn(i).getDefAttribute()!=null && !"".equals(colProvider.getColumn(i).getDefAttribute().getLOVName()) && colProvider.getColumn(i).getDefAttribute().getLOVName()!=null)
        {
          if(lovCond == null)
            lovCond = colProvider.getColumn(i).getDefAttribute().getLOVName();
          else
            lovCond += "##"+colProvider.getColumn(i).getDefAttribute().getLOVName();
        }
      }
      for (int i = 0; i < tree.getGroupProvider().groupSize(); i++) 
      {
        if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && !tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName().equals(""))
        {
          if(lovCond == null)
            lovCond = tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName();
          else
            lovCond += "##"+tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName();
        }
      }
      if(lovCond!=null)
      {
        String[] names = lovCond.split("##");
        String qryLov = "select A.name || '##' || C.value, C.description "+
                        "from ebo_lov A, ebo_lov$details B, ebo_lovdetails C "+
                        "where (B.parent$ = A.boui) AND (B.child$ = C.boui) AND (" ;
        for (int i = 0; i < names.length; i++) 
        {
          qryLov += "(A.name = '"+ names[i] +"')";
          if(i!=(names.length-1))
            qryLov += " OR ";  
        }
        
        qryLov += ")";  
        
        lovs = new Hashtable();
        PreparedStatement prLov=null;
        ResultSet rsltLov=null;
        try{
            prLov=cn.prepareStatement( qryLov , ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY );                             
            rsltLov= prLov.executeQuery();
            
            while(rsltLov.next())
            {
              lovs.put(rsltLov.getString(1), rsltLov.getString(2));
            }
        }
        finally
        {
          if(rsltLov!=null)
            rsltLov.close();
          if(prLov!=null)
            prLov.close();
        }
              
      }
//*************************************
      //sizes
      calculateSizes();

      
    }
    
    private void putGroup(String[] group)
    {
      boolean update = false;
      int i = 0;
      for (; (i < groups.size()) && !update; i++) 
      {
        String[] gr = (String[]) groups.elementAt(i);
        boolean match = true;
        for (int j = 0; (j < (group.length -1)) && match; j++) 
        {
          if(!gr[j].equals(group[j]))
            match=false;
        }
        if( match )
          update=true;
      }
      
      if(update)
      {
        String[] gr = (String [])groups.elementAt(i-1);
        gr[gr.length-1] = ""+(Integer.parseInt(gr[gr.length-1]) + Integer.parseInt(group[group.length-1]));
        groups.remove(i-1);
        groups.insertElementAt(gr, i-1);
      }
      else
      {
        groups.add(group);
      }
      
    }
    
    public StringBuffer saveCSV_1Line_1Column(String separator, boolean headers) throws boRuntimeException, SQLException
    {

      StringBuffer sb = new StringBuffer();
      
      if(headers)
      {
        //imprimir cabeçalhos
        sb.append("\"Objecto\""+separator);
        for (int i = 0; i < tree.getGroupProvider().groupSize(); i++) 
        {
          sb.append("\""+tree.getGroupProvider().getGroup(i).getLabel()+"\""+separator);        
        }
        for (int i = 0; i < colProvider.columnsSize(); i++) 
        {
          if(i>0)
            sb.append(separator);
            
          sb.append("\""+colProvider.getColumn(i).getLabel()+"\"");
        }
        sb.append("\n");
      }
      
      //imprimir dados
      int counter = 0;
      for (int ig = 0; (ig < groups.size()) || ((ig==0) && (nrgroups==0)); ig++) 
      {          
          //retirar os grupos
          String qryG[] = new String[nrgroups];
          if(nrgroups>0)
          {
            int qryGCounter = 0;
            String aux[] = (String[])groups.elementAt(ig);
            for (int i = 0; i < nrgroups; i++) 
            {
              qryG[i] = aux[i];
            }
            qryGCounter = Integer.parseInt(aux[nrgroups]);
          }
          
          PreparedStatement pr=null;
          ResultSet rslt=null;          
        
          //calcular o sql
          String[] qry = tree.getSqlForExportWBridges( ctx, qryG );
          Hashtable sqlBr = new Hashtable();
          Vector prVec = new Vector();
          try{
              for (int i = 1; i < qry.length; i+=2) 
              {
                  PreparedStatement prBr;
                  ResultSet rsltBr;
              
                  //preencher os parameteros
                  prBr=cn.prepareStatement( qry[i+1] , ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY );                             
                  prBr.setFetchDirection(ResultSet.FETCH_UNKNOWN);
                  for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
                  {
                      prBr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
                  }
                  rsltBr= prBr.executeQuery();
                  //rsltBr.setFetchDirection(ResultSet.FETCH_UNKNOWN);
                  //rsltBr.beforeFirst();
                  prVec.add(prBr);
                  sqlBr.put(qry[i],rsltBr);
              }
    
              
              //preencher os parameteros
              pr=cn.prepareStatement( qry[0] , ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );                             

              for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
              {
                  pr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
              }
    
              //percorrer todoas as linhas                   
              rslt= pr.executeQuery();
              boDefHandler bodef = null;
              long oldboui = 0;
              while(rslt.next())
              {
                counter ++;
                long boui = rslt.getLong(2);
                if(boui==oldboui)
                  continue;
                oldboui=boui;
                
                //colocar o nome do objecto
                String classname = rslt.getString(1);
                if(bodef==null || !bodef.getName().equalsIgnoreCase(classname))
                  bodef = boDefHandler.getBoDefinition(classname);
    
                sb.append("\""+bodef.getLabel()+"\"" + separator);
                
                //imprimir os dados dos grupos
                for (int i = 0; i < nrgroups; i++) 
                {
                    if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && tree.getGroupProvider().getGroup(i).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                    {                  
                        if(qryG[i]!=null && !qryG[i].equals(""))
                        {
                          boObject object = boObject.getBoManager().loadObject(ctx,Long.parseLong(qryG[i]));
                          sb.append("\""+object.getCARDIDwNoIMG(false).toString() +"\""+ separator);
                        }
                        else
                          sb.append("\"( )\""+ separator);
                    }
                    else
                    {
                      if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && !tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName().equals(""))
                        sb.append("\""+(String)lovs.get(tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName()+"##"+qryG[i])+"\"" + separator);
                      else if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && tree.getGroupProvider().getGroup(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                      {
                        String val = tree.getGroupProvider().getGroup(i).getValueResult(qryG[i], null);
                        if(val!=null && val.equals("0"))
                          val="Não";
                        else if(val!=null && val.equals("1"))
                          val="Sim";
                        else
                          val="";
                        sb.append("\""+val+"\"" + separator); 
                      }
                      else
                       sb.append("\""+tree.getGroupProvider().getGroup(i).getValueResult(qryG[i], null)+"\"" + separator);
                    }
                }
                
                for (int i = 0,r=0; i < colProvider.columnsSize(); i++,r++) 
                {
                    sb.append("\"");
                    if ( colProvider.getColumn(i).isAttribute()  )
                    {
                          if(sqlBr.containsKey(colProvider.getColumn(i).getName()))
                          {
                            r--;
                            int n_cols = -1;
                            int countCols = 0;
                            if(tree.p_maxCols.containsKey(colProvider.getColumn(i).getName()))
                              n_cols = ((Integer)tree.p_maxCols.get(colProvider.getColumn(i).getName())).intValue();
    
                            ResultSet rsltBr = (ResultSet)sqlBr.get(colProvider.getColumn(i).getName());
                            long bouiBr = -1;
                            if((n_cols!=-1) && (countCols>=n_cols))
                            {
                              sb.append("\""+separator);
                              continue;
                            }
                            if(!rsltBr.next())
                            {
                                rsltBr.beforeFirst();
                            }
                            else
                            {
                                bouiBr = rsltBr.getLong(1);
                            }
                            if(boui != bouiBr && setResultSet(rsltBr, boui))
                            {
                                bouiBr = rsltBr.getLong(1); 
                            }
                            else
                            {
                                sb.append("\""+separator);
                            }
                            boolean first=true;
                            
                            while(boui==bouiBr)
                            {
                              countCols++;
                              if(!first)
                                sb.append("  ;  ");
                              if(colProvider.getColumn(i).getDefAttribute().getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE)
                              {
                                 if(colProvider.getColumn(i).getDefAttribute()!=null && !"".equals(colProvider.getColumn(i).getDefAttribute().getLOVName()) && colProvider.getColumn(i).getDefAttribute().getLOVName()!=null)
                                 {
                                  String value = rsltBr.getString(2);
                                  if(lovs.get(colProvider.getColumn(i).getDefAttribute().getLOVName()+"##"+value)!=null)
                                    value = (String)lovs.get(colProvider.getColumn(i).getDefAttribute().getLOVName()+"##"+value);
                                  sb.append(value);
                                 }
                                else if(colProvider.getColumn(i).getDefAttribute()!=null && colProvider.getColumn(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                                {
                                  String val = rsltBr.getString(2);
                                  if(val!=null && val.equals("0"))
                                    val="Não";
                                  else if(val!=null && val.equals("1"))
                                    val="Sim";
                                  else
                                    val="";
                                  sb.append(val); 
                                }
                                else
                                {
                                  String value = rsltBr.getString(2);
                                  if(value!=null)
                                  {
                                    sb.append(value.replaceAll("\"","'"));
                                  }
                                }
                              }
                              else if(rsltBr.getLong(2)>0)
                              {
                                boObject object = boObject.getBoManager().loadObject(ctx,rsltBr.getLong(2));
                                sb.append(object.getCARDIDwNoIMG(false));
                              }
                              
                              if(rsltBr.next() && !((n_cols!=-1) && (countCols>=n_cols)))
                                bouiBr = rsltBr.getLong(1);
                              else
                                bouiBr=0;
                              first=false;
                            }
                            rsltBr.previous();                        
                          }
                          else if(colProvider.getColumn(i).getDefAttribute()!=null && (colProvider.getColumn(i).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) && (colProvider.getColumn(i).getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1))
                          {
                            String attName = colProvider.getColumn(i).getDefAttribute().getName();
                            long bouiObj = rslt.getLong(r+3);
                            
                            if(bouiObj!=0)
                            {
                              boObject object = boObject.getBoManager().loadObject(ctx,bouiObj);
                              sb.append(object.getCARDIDwNoIMG(false));
                            }
                          }
                          else
                          {
                            String attName = colProvider.getColumn(i).getDefAttribute().getName();
                            String value = rslt.getString(r+3);
                            if(value!=null)
                              value=value.replaceAll("\"","'");
                             if(value!=null && colProvider.getColumn(i).getDefAttribute()!=null && !"".equals(colProvider.getColumn(i).getDefAttribute().getLOVName()) && colProvider.getColumn(i).getDefAttribute().getLOVName()!=null)
                             {
                              if(lovs.get(colProvider.getColumn(i).getDefAttribute().getLOVName()+"##"+value)!=null)
                                value = (String)lovs.get(colProvider.getColumn(i).getDefAttribute().getLOVName()+"##"+value);
                              sb.append(value);
                             }
                            else if (colProvider.getColumn(i).getDefAttribute()!=null && colProvider.getColumn(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                            {
                              if(value!=null && value.equals("0"))
                                value="Não";
                              else if(value!=null && value.equals("1"))
                                value="Sim";
                              else
                                value="";
                              sb.append(value);                           
                            }
                            else
                              sb.append(tree.getColumnsProvider().getColumn(i).getValueResult(value, null));
                          }
                     }
                     else
                     {
                        String value = tree.getColumnsProvider().getColumn(i).getValueResult(rslt.getString(r+3), null);
                        //String value = "";//rslt.getString(colProvider.getColumn(i).getName());;
                        sb.append(value);                           
                     }
                     sb.append("\""+separator);
                }
                sb.append("\n"); 
                
                if( counter % 100 == 0 )
                {
                    ctx.releaseAllObjects();
                    ctx.getThread().clear();
                }
                
              }
          }
          finally
          {
              if(rslt!=null)
                rslt.close();
              if(pr!=null)
                pr.close();
              
              for (int i = 1; i < qry.length; i+=2) 
              {
                if(sqlBr.get(qry[i])!=null)
                    ((ResultSet)sqlBr.get(qry[i])).close();;
                if(prVec.elementAt(i/2)!=null)
                    ((PreparedStatement)prVec.elementAt(i/2)).close();;
              }
          }
      }
      
      return sb;
      
    }
    
    public StringBuffer saveCSV_1Line_NColumns(String separator, boolean headers) throws boRuntimeException, SQLException
    {

      StringBuffer sb = new StringBuffer();
      
      //imprimir cabeçalhos
      if(headers)
      {
        sb.append("\"Objecto\""+separator);
        for (int i = 0; i < tree.getGroupProvider().groupSize(); i++) 
        {
            sb.append("\""+tree.getGroupProvider().getGroup(i).getLabel()+"\""+separator);          
        }
        
        
        for (int i = 0; i < colProvider.columnsSize(); i++) 
        {
          if(tree.p_maxCols.get(colProvider.getColumn(i).getName())==null)
          {
            sb.append("\""+colProvider.getColumn(i).getLabel()+"\""+separator);  
            continue;
          }
            
          int n_cols = ((Integer)tree.p_maxCols.get(colProvider.getColumn(i).getName())).intValue();
          
          for (int j = 0; j < n_cols; j++) 
          {
            sb.append("\""+colProvider.getColumn(i).getLabel()+"::"+(j+1)+"\""+separator);
          }
  
        }
        
        sb.append("\n");
      }

      //imprimir dados
      for (int ig = 0; (ig < groups.size()) || ((ig==0) && (nrgroups==0)); ig++) 
      {          
          //retirar os grupos
          String qryG[] = new String[nrgroups];
          if(nrgroups>0)
          {
            int qryGCounter = 0;
            String aux[] = (String[])groups.elementAt(ig);
            for (int i = 0; i < nrgroups; i++) 
            {
              qryG[i] = aux[i];
            }
            qryGCounter = Integer.parseInt(aux[nrgroups]);
          }
          
          PreparedStatement pr=null;
          ResultSet rslt=null;
                
          //calcular o sql
          String[] qry = tree.getSqlForExportWBridges( ctx, qryG );
          Hashtable sqlBr = new Hashtable();
          Vector prVec = new Vector();
          try{
              for (int i = 1; i < qry.length; i+=2) 
              {
                  PreparedStatement prBr;
                  ResultSet rsltBr;
              
                  //preencher os parameteros
                  prBr=cn.prepareStatement( qry[i+1] , ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY );                             
                  prBr.setFetchDirection(ResultSet.FETCH_UNKNOWN);
                  for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
                  {
                      prBr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
                  }
                  rsltBr= prBr.executeQuery();
                  //rsltBr.setFetchDirection(ResultSet.FETCH_UNKNOWN);
                  //rsltBr.beforeFirst();
                  prVec.add(prBr);
                  sqlBr.put(qry[i],rsltBr);
              }
    
              
              //preencher os parameteros
              pr=cn.prepareStatement( qry[0] , ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );                             

              for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
              {
                  pr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
              }
    
              //percorrer todoas as linhas                   
              rslt= pr.executeQuery();
              boDefHandler bodef = null;
              long oldboui = 0;
              while(rslt.next())
              {
                long boui = rslt.getLong(2);
                if(boui==oldboui)
                  continue;
                oldboui=boui;
                
                
                //colocar o nome do objecto
                String classname = rslt.getString(1);
                if(bodef==null || !bodef.getName().equalsIgnoreCase(classname))
                  bodef = boDefHandler.getBoDefinition(classname);
    
                sb.append("\""+bodef.getLabel()+"\""+separator);
                
                //imprimir os dados dos grupos
                for (int i = 0; i < nrgroups; i++) 
                {
                    if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && tree.getGroupProvider().getGroup(i).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                    {                  
                        if(qryG[i]!=null && !qryG[i].equals(""))
                        {
                          boObject object = boObject.getBoManager().loadObject(ctx,Long.parseLong(qryG[i]));
                          sb.append("\""+object.getCARDIDwNoIMG(false).toString()+"\""+ separator);
                        }
                        else
                          sb.append("\"( )\""+ separator);
                    }
                    else
                    {
                      if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && !tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName().equals(""))
                        sb.append("\""+(String)lovs.get(tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName()+"##"+qryG[i])+"\""+separator);
                      else if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && tree.getGroupProvider().getGroup(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                      {
                        String val = tree.getGroupProvider().getGroup(i).getValueResult(qryG[i], null);
                        if(val!=null && val.equals("0"))
                          val="Não";
                        else if(val!=null && val.equals("1"))
                          val="Sim";
                        else
                          val="";
                        sb.append("\""+val+"\"" + separator); 
                      }                    
                      else
                        sb.append("\""+tree.getGroupProvider().getGroup(i).getValueResult(qryG[i], null)+"\"" + separator);
                    }
                }
                for (int i = 0,r=0; i < colProvider.columnsSize(); i++,r++) 
                {
                    
                    if ( colProvider.getColumn(i).isAttribute()  )
                    {
                          if(sqlBr.containsKey(colProvider.getColumn(i).getName()))
                          {
                            r--;
                            ResultSet rsltBr = (ResultSet)sqlBr.get(colProvider.getColumn(i).getName());
                            int n_cols = ((Integer)tree.p_maxCols.get(colProvider.getColumn(i).getName())).intValue();
                            int in;
                            long bouiBr = -1;

                            if(!rsltBr.next())
                            {
                                rsltBr.beforeFirst();
                            }
                            else
                            {
                                bouiBr = rsltBr.getLong(1);
                            }
                            if(boui != bouiBr && setResultSet(rsltBr, boui))
                            {
                                bouiBr = rsltBr.getLong(1); 
                            }
                            else
                            {
                                for(in=0;in<n_cols;in++)
                                    sb.append("\"\""+separator);
                                continue;
                            }
                            
                            for(in=0;boui==bouiBr;in++)
                            {
                              if((in!=0) && (in==n_cols))
                                break;
                                
                              if(colProvider.getColumn(i).getDefAttribute().getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE)
                              {
                                 if(colProvider.getColumn(i).getDefAttribute()!=null && !"".equals(colProvider.getColumn(i).getDefAttribute().getLOVName()) && colProvider.getColumn(i).getDefAttribute().getLOVName()!=null)
                                 {
                                  String value = rsltBr.getString(2);
                                  if(lovs.get(colProvider.getColumn(i).getDefAttribute().getLOVName()+"##"+value)!=null)
                                    value = (String)lovs.get(colProvider.getColumn(i).getDefAttribute().getLOVName()+"##"+value);
                                  sb.append("\""+value+ "\"" + separator);
                                 }
                                else if(colProvider.getColumn(i).getDefAttribute()!=null && colProvider.getColumn(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                                {
                                  String val = rsltBr.getString(2);
                                  if(val!=null && val.equals("0"))
                                    val="Não";
                                  else if(val!=null && val.equals("1"))
                                    val="Sim";
                                  else
                                    val="";
                                  sb.append("\""+val+"\""+separator); 
                                }                              
                                else
                                {
                                  String value = rsltBr.getString(2);
                                  if(value!=null)
                                    value=value.replaceAll("\"","'");
                                  else
                                    value="";
                                  sb.append("\""+value+ "\"" + separator);
                                }
                              }
                              else if(rsltBr.getLong(2)>0)
                              {
                                boObject object = boObject.getBoManager().loadObject(ctx,rsltBr.getLong(2));
                                sb.append("\""+object.getCARDIDwNoIMG(false).toString()+"\""+separator);
                              }
                              else
                                sb.append("\"\""+separator);
                              
                              if(rsltBr.next())
                                bouiBr = rsltBr.getLong(1);
                              else
                                bouiBr=0;
                            }
                            
                            for(;in<n_cols;in++)
                              sb.append("\"\""+separator);
                            
                            while(boui==bouiBr)
                            {
                              if(rsltBr.next())
                                bouiBr = rsltBr.getLong(1);
                              else
                                bouiBr=0;                          
                            }
                            
                            rsltBr.previous();                        
                          }
                          else if(colProvider.getColumn(i).getDefAttribute()!=null && (colProvider.getColumn(i).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) && (colProvider.getColumn(i).getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1))
                          {
                            String attName = colProvider.getColumn(i).getDefAttribute().getDbName();
                            long bouiObj = rslt.getLong(r+3);
                            
                            if(bouiObj!=0)
                            {
                              boObject object = boObject.getBoManager().loadObject(ctx,bouiObj);
                              sb.append("\""+object.getCARDIDwNoIMG(false).toString()+"\""+separator);
                            }
                          }
                          else
                          {
                            String attName = colProvider.getColumn(i).getDefAttribute().getDbName();
                            String value = rslt.getString(r+3);
                            if(value!=null)
                              value=value.replaceAll("\"","'");
                             if(value!=null && colProvider.getColumn(i).getDefAttribute()!=null && !"".equals(colProvider.getColumn(i).getDefAttribute().getLOVName()) && colProvider.getColumn(i).getDefAttribute().getLOVName()!=null)
                             {
                              if(lovs.get(colProvider.getColumn(i).getDefAttribute().getLOVName()+"##"+value)!=null)
                                value = (String)lovs.get(colProvider.getColumn(i).getDefAttribute().getLOVName()+"##"+value);
                              sb.append("\""+value+"\""+separator);
                             }
                            else if (colProvider.getColumn(i).getDefAttribute()!=null && colProvider.getColumn(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                            {
                              if(value!=null && value.equals("0"))
                                value="Não";
                              else if(value!=null && value.equals("1"))
                                value="Sim";
                              else
                                value="";
                              sb.append("\""+value+"\""+separator);   
                            }                          
                            else
                              sb.append("\""+tree.getColumnsProvider().getColumn(i).getValueResult(value, null)+"\""+separator);
                          }
                     }
                     else
                     {
                        String value = tree.getColumnsProvider().getColumn(i).getValueResult(rslt.getString(r+3), null);
                        //String value = "";//rslt.getString(colProvider.getColumn(i).getName());;
                        sb.append("\""+value+"\""+separator);   
                     }
                     
                }
                sb.append("\n");
              }
          }
          finally
          {
              if(rslt!=null)
                rslt.close();
              if(pr!=null)
                pr.close();
              
              for (int i = 1; i < qry.length; i+=2) 
              {
                if(sqlBr.get(qry[i])!=null)
                    ((ResultSet)sqlBr.get(qry[i])).close();;
                if(prVec.elementAt(i/2)!=null)
                    ((PreparedStatement)prVec.elementAt(i/2)).close();;
              }
          }
      }
      
      return sb;
      
    }

    public StringBuffer saveCSV_NLine_1Column(String separator, boolean headers) throws boRuntimeException, SQLException
    {

      StringBuffer sb = new StringBuffer();
      
      //imprimir cabeçalhos
      if(headers)
      {
        sb.append("\"Objecto\""+separator);
        for (int i = 0; i < tree.getGroupProvider().groupSize(); i++) 
        {
          sb.append("\""+tree.getGroupProvider().getGroup(i).getLabel()+"\""+separator);
        }
        for (int i = 0; i < colProvider.columnsSize(); i++) 
        {
          if(i>0)
            sb.append(separator);
            
          sb.append("\""+colProvider.getColumn(i).getLabel()+"\"");
        }
        sb.append("\n");
      }

      //imprimir dados
      for (int ig = 0; (ig < groups.size()) || ((ig==0) && (nrgroups==0)); ig++) 
      {          
          //retirar os grupos
          String qryG[] = new String[nrgroups];
          if(nrgroups>0)
          {
            int qryGCounter = 0;
            String aux[] = (String[])groups.elementAt(ig);
            for (int i = 0; i < nrgroups; i++) 
            {
              qryG[i] = aux[i];
            }
            qryGCounter = Integer.parseInt(aux[nrgroups]);
          }
        
          //calcular o sql
          String qry = tree.getSqlForExport( ctx, qryG );

          PreparedStatement pr=null;
          ResultSet rslt=null;
          
          try{
              //preencher os parameteros
              pr=cn.prepareStatement( qry , ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );                             
              for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
              {
                  pr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
              }
    
              //percorrer todoas as linhas                   
              rslt= pr.executeQuery();
              boDefHandler bodef = null;
              while(rslt.next())
              {
                //colocar o nome do objecto
                String classname = rslt.getString(1);
                if(bodef==null || !bodef.getName().equalsIgnoreCase(classname))
                  bodef = boDefHandler.getBoDefinition(classname);
    
                sb.append("\""+bodef.getLabel()+"\"" + separator);
                
                //imprimir os dados dos grupos
                for (int i = 0; i < nrgroups; i++) 
                {
                    if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && tree.getGroupProvider().getGroup(i).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                    {                  
                        if(qryG[i]!=null && !qryG[i].equals(""))
                        {
                          boObject object = boObject.getBoManager().loadObject(ctx,Long.parseLong(qryG[i]));
                          sb.append("\""+object.getCARDIDwNoIMG(false).toString()+"\"" + separator);
                        }
                        else
                          sb.append("\"( )\""+separator);
                    }
                    else
                    {
                    if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && !tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName().equals(""))
                      sb.append("\""+(String)lovs.get(tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName()+"##"+qryG[i])+"\"" + separator);
                    else if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && tree.getGroupProvider().getGroup(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                    {
                      String val = tree.getGroupProvider().getGroup(i).getValueResult(qryG[i], null);
                      if(val!=null && val.equals("0"))
                        val="Não";
                      else if(val!=null && val.equals("1"))
                        val="Sim";
                      else
                        val="";
                      sb.append("\""+val+"\"" + separator); 
                    }                  
                    else
                      sb.append("\""+tree.getGroupProvider().getGroup(i).getValueResult(qryG[i], null)+"\""+ separator);
                    }
                }
                
                for (int i = 0; i < colProvider.columnsSize(); i++)
                {
                  if ( colProvider.getColumn(i).isAttribute()  )
                  {
                      if(colProvider.getColumn(i).getDefAttribute()!=null && colProvider.getColumn(i).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                      {
                          sb.append("\"");
                          long bouiObj = rslt.getLong(i+3);
                          if(bouiObj>0)
                          {
                            boObject object = boObject.getBoManager().loadObject(ctx,bouiObj);
                            sb.append(object.getCARDIDwNoIMG(false));
                          }
                          sb.append("\"");
                          sb.append(separator);
                      }
                      else           
                      {
                        String value = rslt.getString(i+3);
                        if(value!=null)
                          value = value.replaceAll("\"","'");
                         if(value!=null && colProvider.getColumn(i).getDefAttribute()!=null && !"".equals(colProvider.getColumn(i).getDefAttribute().getLOVName()) && colProvider.getColumn(i).getDefAttribute().getLOVName()!=null)
                         {
                            value = (String)lovs.get(colProvider.getColumn(i).getDefAttribute().getLOVName()+"##"+value);
                         }
                         else if (colProvider.getColumn(i).getDefAttribute()!=null && colProvider.getColumn(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                         {
                             if(value!=null && value.equals("0"))
                                value="Não";
                             else if(value!=null && value.equals("1"))
                                value="Sim";
                             else
                                value="";
                         }                                             
                        else
                            value = tree.getColumnsProvider().getColumn(i).getValueResult(value, null);
                        if(value!=null)
                          sb.append("\""+value+"\"");
                        else
                          sb.append("\"\"");
                        sb.append(separator);
                      }
                  }
                  else
                  {
                    String value = tree.getColumnsProvider().getColumn(i).getValueResult(rslt.getString(i+3), null);
                    //String value = "";//rslt.getString(colProvider.getColumn(i).getName());
                    sb.append("\""+value+"\"");
                    sb.append(separator);
                  }
                }
                sb.append("\n");
              }
          }
          catch(Exception e)
          {
              e.printStackTrace();
          }
          finally
          {
              if(rslt!=null)
                rslt.close();
              if(pr!=null)
                pr.close();
          }
      }
      
      return sb;
      
    }
    
    public XMLDocument saveXML(String user) throws boRuntimeException, SQLException
    {
      XMLDocument xmldoc = new XMLDocument();
      xmldoc.setEncoding("UTF-8");
      xmldoc.setVersion("1.0");
        
      XMLElement xmlelem=null;
        
      //escrever as tags principais
      XMLElement xmlelemP = (XMLElement)xmldoc.createElement("XEO");
      xmlelemP.setAttribute("date",(new java.util.Date()).toString());
      xmlelemP.setAttribute("action","export");
      xmlelemP.setAttribute("type","list");
      if(user!=null)
         xmlelemP.setAttribute("creator",user);
      xmldoc.appendChild(xmlelemP);  
      
      
      //imprimir dados
      for (int ig = 0;  (ig < groups.size()) || ((ig==0) && (nrgroups==0)); ig++) 
      {          
          //retirar os grupos
          String qryG[] = new String[nrgroups];
          int qryGCounter = 0;
          if(nrgroups>0)
          {
            String aux[] = (String[])groups.elementAt(ig);
            for (int i = 0; i < nrgroups; i++) 
            {
              qryG[i] = aux[i];
            }
            qryGCounter = Integer.parseInt(aux[nrgroups]);
          }

            XMLElement elemPar=xmlelemP;
            
            //criar ou percorrer as tags dos grupos
            for (int i = 0; i < nrgroups; i++) 
            {
              //grupo ainda não foi ciado
              org.w3c.dom.NodeList groupList = elemPar.getChildrenByTagName("Group");
              int gInd=-1;
              for (int g = 0; g < groupList.getLength(); g++) 
              {
                String grpValue = null;
                if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && tree.getGroupProvider().getGroup(i).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                {
                    if(qryG[i]!=null && !qryG[i].equals(""))
                    {
                      boObject object = boObject.getBoManager().loadObject(ctx,Long.parseLong(qryG[i]));
                      grpValue = object.getCARDIDwNoIMG(false).toString(); 
                    }
                    else
                      grpValue = "( )"; 
                }
                else
                {
                    if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && !tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName().equals(""))
                      grpValue =  (String)lovs.get(tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName()+"##"+qryG[i]);
                    else if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && tree.getGroupProvider().getGroup(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                    {
                      String val = tree.getGroupProvider().getGroup(i).getValueResult(qryG[i], null);
                      if(val!=null && val.equals("0"))
                        grpValue="Não";
                      else if(val!=null && val.equals("1"))
                        grpValue="Sim";
                      else
                        grpValue="-";
                    }                      
                    else
                      grpValue = tree.getGroupProvider().getGroup(i).getValueResult(qryG[i], null);
                }
                
                if(((XMLElement)groupList.item(g)).getAttribute("value").equals(grpValue))
                {
                  gInd=g;
                  break;
                }
              }
              
              if(gInd==-1)
              {
                xmlelem = (XMLElement)xmldoc.createElement("Group");
                xmlelem.setAttribute("name",""+tree.getGroupProvider().getGroup(i).getName()); 
                xmlelem.setAttribute("description",""+tree.getGroupProvider().getGroup(i).getLabel());
                if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && tree.getGroupProvider().getGroup(i).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                {
                    if(qryG[i]!=null && !qryG[i].equals(""))
                    {
                      boObject object = boObject.getBoManager().loadObject(ctx,Long.parseLong(qryG[i]));
                      xmlelem.setAttribute("value",""+object.getCARDIDwNoIMG(false).toString()); 
                    }
                    else
                      xmlelem.setAttribute("value",""); 
                }
                else
                {
                     if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && !tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName().equals(""))
                        xmlelem.setAttribute("value",(String)lovs.get(tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName()+"##"+qryG[i]));
                      else if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && tree.getGroupProvider().getGroup(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                      {
                        String val = tree.getGroupProvider().getGroup(i).getValueResult(qryG[i], null);
                        if(val!=null && val.equals("0"))
                          val="Não";
                        else if(val!=null && val.equals("1"))
                          val="Sim";
                        else
                          val="-";
                        xmlelem.setAttribute("value",val);
                      }                              
                      else
                          xmlelem.setAttribute("value",""+tree.getGroupProvider().getGroup(i).getValueResult(qryG[i], null)); 
                }
                xmlelem.setAttribute("elements",""+qryGCounter); 
                elemPar.appendChild(xmlelem);
                elemPar=xmlelem;
              }
              else
              {
                elemPar = (XMLElement)groupList.item(gInd);
                elemPar.setAttribute("elements",""+(Integer.parseInt(elemPar.getAttribute("elements"))+qryGCounter)); 
              }
            }

          PreparedStatement pr=null;
          ResultSet rslt=null;
        
          //calcular o sql
          String[] qry = tree.getSqlForExportWBridges( ctx, qryG );
          Hashtable sqlBr = new Hashtable();
          Vector prVec = new Vector();
          try{
              for (int i = 1; i < qry.length; i+=2) 
              {
                  PreparedStatement prBr=null;
                  ResultSet rsltBr=null;
              
                  //preencher os parameteros
                  prBr=cn.prepareStatement( qry[i+1] , ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY );                             
                  prBr.setFetchDirection(ResultSet.FETCH_UNKNOWN);
                  for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
                  {
                      prBr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
                  }
                  rsltBr= prBr.executeQuery();
                  //rsltBr.setFetchDirection(ResultSet.FETCH_UNKNOWN);
                  //rsltBr.beforeFirst();
                  prVec.add(prBr);
                  sqlBr.put(qry[i],rsltBr);
              }
    
              
              //preencher os parameteros
              pr=cn.prepareStatement( qry[0] , ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );                             

              for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
              {
                  pr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
              }
    
              //percorrer todoas as linhas                   
              rslt= pr.executeQuery();
              boDefHandler bodef = null;
              while(rslt.next())
              {
                
                long boui = rslt.getLong(2);
    
                //colocar a tag do objecto
                String classname = rslt.getString(1);
                if(bodef==null || !bodef.getName().equalsIgnoreCase(classname))
                  bodef = boDefHandler.getBoDefinition(classname);
    
                xmlelem = (XMLElement)xmldoc.createElement("Object");
                xmlelem.setAttribute("boui",""+boui); 
                xmlelem.setAttribute("name",bodef.getName()); 
                xmlelem.setAttribute("description",bodef.getLabel()); 

                elemPar.appendChild(xmlelem);
                elemPar=xmlelem;
    
                for (int i = 0,r=0; i < colProvider.columnsSize(); i++,r++) 
                {
                    if(sqlBr.containsKey(colProvider.getColumn(i).getName()))
                        xmlelem = (XMLElement)xmldoc.createElement("MultiValueAttribute");
                    else
                        xmlelem = (XMLElement)xmldoc.createElement("Attribute");
                    xmlelem.setAttribute("name",colProvider.getColumn(i).getName()); 
                    xmlelem.setAttribute("description",colProvider.getColumn(i).getLabel()); 
                    elemPar.appendChild(xmlelem);
                    elemPar=xmlelem;
    
                    if ( colProvider.getColumn(i).isAttribute()  )
                    {
                          if(sqlBr.containsKey(colProvider.getColumn(i).getName()))
                          {
                            r--;
                            ResultSet rsltBr = (ResultSet)sqlBr.get(colProvider.getColumn(i).getName());
                            long bouiBr = -1;
                            
                            if(!rsltBr.next())
                            {
                                rsltBr.beforeFirst();
                            }
                            else
                            {
                                bouiBr = rsltBr.getLong(1);
                            }
                            if(boui != bouiBr && setResultSet(rsltBr, boui))
                            {
                                bouiBr = rsltBr.getLong(1); 
                            }
                            else
                            {
                                elemPar=(XMLElement)elemPar.getParentNode();
                                continue;
                            }
                            
                            
                            while(boui==bouiBr)
                            {
    
                              xmlelem = (XMLElement)xmldoc.createElement("Value");
                              elemPar.appendChild(xmlelem);
                              elemPar=xmlelem;
    
                              if(colProvider.getColumn(i).getDefAttribute().getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE)
                              {
                                 if(colProvider.getColumn(i).getDefAttribute()!=null && !"".equals(colProvider.getColumn(i).getDefAttribute().getLOVName()) && colProvider.getColumn(i).getDefAttribute().getLOVName()!=null)
                                 {
                                    String value = rsltBr.getString(2);
                                    if(value!=null)
                                      value=value.replaceAll("\"","'");
                                    else
                                      value="";
                                    elemPar.addText((String)lovs.get(colProvider.getColumn(i).getDefAttribute().getLOVName()+"##"+value));
                                 }
                                  else if(colProvider.getColumn(i).getDefAttribute()!=null && colProvider.getColumn(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                                  {
                                    String val = rsltBr.getString(2);
                                    if(val!=null && val.equals("0"))
                                      val="Não";
                                    else if(val!=null && val.equals("1"))
                                      val="Sim";
                                    else
                                      val="";
                                    elemPar.addText(val);
                                  }                              
                                  else
                                  {
                                    String value = rsltBr.getString(2);
                                    if(value!=null)
                                      value=value.replaceAll("\"","'");
                                    else
                                      value="";
                                    elemPar.addText(value);
                                  }
                              }
                              else if(rsltBr.getLong(2)>0)
                              {
                                boObject object = boObject.getBoManager().loadObject(ctx,rsltBr.getLong(2));
                                elemPar.addText(object.getCARDIDwNoIMG(false).toString());
                              }
                              
                              if(rsltBr.next())
                                bouiBr = rsltBr.getLong(1);
                              else
                                bouiBr=0;                          
                              
                              elemPar=(XMLElement)elemPar.getParentNode();
                            }
                            rsltBr.previous();                        
                          }
                          else if(colProvider.getColumn(i).getDefAttribute()!=null && (colProvider.getColumn(i).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) && (colProvider.getColumn(i).getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1))
                          {
                            String attName = colProvider.getColumn(i).getDefAttribute().getDbName();
                            long bouiObj = rslt.getLong(r+3);
                            
                            if(bouiObj!=0)
                            {
                              boObject object = boObject.getBoManager().loadObject(ctx,bouiObj);
                              elemPar.addText(object.getCARDIDwNoIMG(false).toString());
                            }
                          }
                          else
                          {
                            String attName = colProvider.getColumn(i).getDefAttribute().getDbName();
                            String value = rslt.getString(r+3);
                            if(value!=null)
                              value=value.replaceAll("\"","'");
                             if(value!=null && colProvider.getColumn(i).getDefAttribute()!=null && !"".equals(colProvider.getColumn(i).getDefAttribute().getLOVName()) && colProvider.getColumn(i).getDefAttribute().getLOVName()!=null)
                             {
                              if(lovs.get(colProvider.getColumn(i).getDefAttribute().getLOVName()+"##"+value)!=null)
                                value = (String)lovs.get(colProvider.getColumn(i).getDefAttribute().getLOVName()+"##"+value);
                              elemPar.addText(value);
                             }
                            else if (colProvider.getColumn(i).getDefAttribute()!=null && colProvider.getColumn(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                            {
                              if(value!=null && value.equals("0"))
                                  value="Não";
                              else if(value!=null && value.equals("1"))
                                  value="Sim";
                              if(value!=null)
                                elemPar.addText(value);    
                            }                          
                            else if(value!=null)
                              elemPar.addText(tree.getColumnsProvider().getColumn(i).getValueResult(value, null));
                          }
                     }
                     else
                     {
                        String value = tree.getColumnsProvider().getColumn(i).getValueResult(rslt.getString(r+3), null);
                        //String value = "";//rslt.getString(colProvider.getColumn(i).getName());;
                        elemPar.addText(value);    
                     }
                     
                    elemPar=(XMLElement)elemPar.getParentNode();
                }
                elemPar=(XMLElement)elemPar.getParentNode();
              }
          }
          finally
          {
              if(rslt!=null)
                rslt.close();
              if(pr!=null)
                pr.close();
              
              for (int i = 1; i < qry.length; i+=2) 
              {
                if(sqlBr.get(qry[i])!=null)
                    ((ResultSet)sqlBr.get(qry[i])).close();;
                if(prVec.elementAt(i/2)!=null)
                    ((PreparedStatement)prVec.elementAt(i/2)).close();;
              }
          }
      }
      
      return xmldoc;
    }

    private String getLovValue(String key)
    {
        return lovs.get(key) == null ? "":(String)lovs.get(key);
    }

    public ExplorerPrintResultSet getPrintResultSet() throws boRuntimeException, SQLException
    {
        return new ExplorerPrintResultSet(this);
    } 
    protected void writeHeader(StringBuffer buffer)
    {
        buffer.append("<html>");
        buffer.append("<head>");
		buffer.append("<style type=\"text/css\">");
        buffer.append(".footer{width:100%;border-top:2px  #6297E5;}");
        buffer.append(".credentials{font-size:7pt;}");
        buffer.append(".labelDst{color:green;font-weight:bold}");

        buffer.append(".grid{ width:100%;cellpadding:2pt; cellmargin:1;font-size:9pt; table-layout:fixed }");
        buffer.append(".gridCHeader{ font-weight:normal; background-color:#C9DBF7;border:1px solid #6297E5;padding:1px}");
        buffer.append(".gridCBody{ border-bottom:1px solid #CCCCCC }");

        buffer.append("body { font-size:8pt; font-family:Helvetica, Symbol ;padding:0pt;background-color:#D9E5E8;margin:0}");

        buffer.append("div.titleReport{font-size:21px;font:arial;text-align:center;width:100%;background-color:#FFFFFF;border-top:1px solid #475A76;border-left:1px solid #475A76;border-right:1px solid #475A76   }");
        buffer.append("div.reportBody{ background-color:#FFFFFF;padding:5px;border-right:1px solid #475A76;border-left:1px solid #475A76;border-bottom:1px solid #475A76;width:100%;}");
        buffer.append("div.report{ background-color:#D9E5E8;font:Arial;padding:10pt;width:100%;}");

        buffer.append("@media print{");
        buffer.append("  .print { display:none }");
        buffer.append("  .noprint{display:none}");
        buffer.append("  .toolbar{display:none}");
        buffer.append("}");
        buffer.append("@media screen{");
        buffer.append("  .print {");
        buffer.append("  	border:1px solid #FFFFFF;PADDING-RIGHT: 5px;color:#000000;PADDING-LEFT: 5px;FONT:13px;");
        buffer.append("  	background-color:#C9DBF7;");
        buffer.append("	CURSOR: hand;");
        buffer.append("  }");
        buffer.append("  .toolbar{ ");
        buffer.append("  BORDER-RIGHT: #113469 1px solid;");
        buffer.append("	BORDER-LEFT: #A4C3F0 1px solid;");
        buffer.append("	BORDER-BOTTOM:#113469 1px solid;");
        buffer.append("	BORDER-TOP:#A4C3F0 1px solid;");
        buffer.append("	WIDTH: 100%; COLOR: #FFFFFF;"); 
        buffer.append("	BACKGROUND-COLOR: #6297E5;");
        buffer.append("	padding:3px;");  
        buffer.append("   }");
        buffer.append("}");
        buffer.append("</style>");
        buffer.append("		<title>"+ JSPMessages.getString("ExplorerList.4")).append(this.tree.p_bodef.getLabel()).append("</title>");
        buffer.append("	</head>");
        buffer.append("	<body margin=\"0pt\" scroll=\"auto\">");
        buffer.append("<table width='100%'>");
        buffer.append("<tr>");
        buffer.append("<td width='100%'>");

        buffer.append("		<div class=\"toolbar\">");
        buffer.append("			<button class=\"print\" onclick=\"window.print()\">"+ JSPMessages.getString("ExplorerList.1")+"</button>");
        buffer.append("			<br class=\"noprint\"/>");
        buffer.append("		</div>");
    
        buffer.append("		<div class=\"report\">");
        buffer.append("			<div class=\"titleReport\">");
        buffer.append("				<br/>"+ JSPMessages.getString("ExplorerList.5")+" ").append(this.tree.p_bodef.getLabel()).append(" <br/>");
        buffer.append("      </div>");
        buffer.append("			<div class=\"reportBody\">");        
    }
    protected void writeFooter(StringBuffer buffer)
    {
        buffer.append("</div>");
        buffer.append("      <div class=\"credentials\">");
        buffer.append("        "+ JSPMessages.getString("ExplorerList.6")).append(new java.util.Date()).append(JSPMessages.getString("ExplorerList.7"));
        try 
        {        
            buffer.append(boObject.getBoManager().loadObject(getContext(),getContext().getBoSession().getPerformerBoui()).getAttribute("username").getValueString());    
        } 
        catch (Exception ex) 
        {
        }
                
        buffer.append("      </div> "); 
        buffer.append("    </div>");
		
        buffer.append("    <br class=\"noprint\"/>");
        buffer.append("		<div class=\"toolbar\">");
        buffer.append("			<button class=\"print\" onclick=\"window.print()\">"+JSPMessages.getString("ExplorerList.1")+"</button>");
        buffer.append("		<br class=\"noprint\"/>");
        buffer.append("		</div>");
        buffer.append("	</td>");
        buffer.append("	</tr>");
        buffer.append("	</table>");	
        buffer.append("  </body>");
        buffer.append("</html>");        
    }
    protected void writeGroupsTitle(StringBuffer buffer)
    {
        if(nrgroups > 0)
        {
            buffer.append("<table border='0' cellpadding='0' cellspacing='0' style='border-collapse: collapse' id='GR_HEADER'>\n");
            buffer.append("<tr>\n");
            buffer.append("\n");
        
            for (int i = 0; i < nrgroups; i++) 
            {
                  buffer.append("<td class='gridCHeader'>"+((tree.getGroupProvider().getGroup(i).getDefAttribute()==null || !tree.getGroupProvider().getGroup(i).isAttribute())?tree.getGroupProvider().getGroup(i).getLabel():tree.getGroupProvider().getGroup(i).getDefAttribute().getLabel())+"</td>\n");  
                  buffer.append("<td width='5'>&nbsp;</td>\n");
            }

            buffer.append("<td class='label'><font color='#C0C0C0'><b>"+JSPMessages.getString("docHTML_treeView.466")+"</b></font></td>\n");
            buffer.append("</tr>\n");
            buffer.append("</table>\n");
        }           
        buffer.append("<br>\n");
    }
    protected void writeColumnsTitle(StringBuffer buffer)
    { 
        buffer.append("<table border='0' class='grid' cellpadding='0' cellspacing='0'  id='table1'>\n");
        buffer.append("<tr>\n");
        buffer.append("<td class='gridCHeader' width='"+start+"%'>&nbsp;</td>\n");      

        buffer.append("<td width='5'>&nbsp;</td>\n");                  
      
        for (int i = 0; i <  colProvider.columnsSize(); i++) 
        {
            buffer.append("<td class='gridCHeader' width='"+((100*colProvider.getColumn(i).getWidth())/max_size)+"%'>"+((colProvider.getColumn(i).getDefAttribute()==null || !colProvider.getColumn(i).isAttribute())?colProvider.getColumn(i).getLabel():colProvider.getColumn(i).getDefAttribute().getLabel())+"</td>\n");
            buffer.append("<td width='5'>&nbsp;</td>\n");
        }

        buffer.append("</tr>\n");
        buffer.append("</table>\n");
        buffer.append("<br>\n");
    }
    protected void writeGroup(StringBuffer buffer,int index) throws boRuntimeException
    {
        if(nrgroups>0)
        {
            int qryGCounter = 0;
            boObject object = null;
            String qryG[] = new String[nrgroups];        
        
            String aux[] = (String[])groups.elementAt(index);
            for (int i = 0; i < nrgroups; i++) 
            {
                qryG[i] = aux[i];
            }
            
            qryGCounter = Integer.parseInt(aux[nrgroups]);
            
          //imprimir os grupos
          
            for (int i = 0; i < nrgroups; i++) 
            {
                object = null;
                buffer.append("<table border='0' cellpadding='0' cellspacing='0' style='border-collapse: collapse' id='GR-"+(index+1)+"-"+(i+1)+"'>\n");
                buffer.append("<tr>\n");
                if(i>0)
                {
                    buffer.append("<td width='").append((i*20)).append("'>&nbsp;</td>\n");    
                }
                    
                if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && tree.getGroupProvider().getGroup(i).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                {
                   if(aux[i]!=null && !aux[i].equals(""))
                  {
                    object = boObject.getBoManager().loadObject(ctx,Long.parseLong(aux[i]));
                    if((i+1) >= nrgroups)
                    {
                        buffer.append("<td class='labelDst'>").append(object.getCARDID(false).toString()).append(" (").append(aux[aux.length-1]).append(")").append("</td>\n");
                    }
                    else
                    {
                        buffer.append("<td class='labelDst'>").append(object.getCARDID(false).toString()).append("</td>\n");
                    }
                  }
                  else
                  {
                    if((i+1) >= nrgroups)
                    {
                        if(aux[aux.length-1] != null)
                        {
                            buffer.append("<td class='labelDst'>").append(" (").append(aux[aux.length-1]).append(")").append("</td>\n");
                        }
                        {
                            buffer.append("<td class='labelDst'>").append(" ()").append("</td>\n");
                        }
                    }
                    else
                    {
                        buffer.append("<td class='labelDst'>").append(" ()").append("</td>\n");
                    }
                  }
                }
                else
                {
                    if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && !tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName().equals(""))
                    {
                        String val = (String)lovs.get(tree.getGroupProvider().getGroup(i).getDefAttribute().getLOVName()+"##"+qryG[i]);
                        if((i+1) >= nrgroups)
                        {
                            buffer.append("<td class='labelDst'>").append(val).append(" (").append(aux[aux.length-1]).append(")").append("</td>\n");
                        }
                        else
                        {
                            buffer.append("<td class='labelDst'>").append(val).append("</td>\n");
                        }
                        
                    }                      
                    else if(tree.getGroupProvider().getGroup(i).getDefAttribute()!=null && tree.getGroupProvider().getGroup(i).getDefAttribute().getType().equalsIgnoreCase("boolean"))
                    {
                        String val = tree.getGroupProvider().getGroup(i).getValueResult(qryG[i], null);
                        if(val!=null && val.equals("0"))
                        {
                            val=JSPMessages.getString("docHTML_renderFields.2");   
                        }
                        else if(val!=null && val.equals("1"))
                        {
                            val=JSPMessages.getString("docHTML_renderFields.1");    
                        }
                        if((i+1) >= nrgroups)
                        {
                            buffer.append("<td class='labelDst'>").append(val).append(" (").append(aux[aux.length-1]).append(")").append("</td>\n");
                        }
                        else
                        {
                            buffer.append("<td class='labelDst'>").append(val).append("</td>\n");
                        }
                    }                                      
                    else
                    {
                        String value = tree.getGroupProvider().getGroup(i).getValueResult(qryG[i], null);
                        if(value == null || value.length() == 0)
                        {
                            value = "("+aux[1]+")";
                        }
                        if((i+1) >= nrgroups)
                        {
                            buffer.append("<td class='labelDst'>").append(value).append(" (").append(aux[aux.length-1]).append(")").append("</td>\n");
                        }
                        else
                        {
                            buffer.append("<td class='labelDst'>").append(value).append("</td>\n");
                        }
                    }                
                }
                buffer.append("<td>&nbsp;</td>\n");
                buffer.append("</tr>\n");
                buffer.append("</table>\n");
            }
        }       
   }
   private String[] getRow(long boui, ResultSet rslt,Hashtable sqlBr) throws boRuntimeException, SQLException
   {
        String line[] = new String[ colProvider.columnsSize()+1];
        boObject object = null;        
        for (int i = 0,r=0; i < colProvider.columnsSize(); i++,r++) 
        {
            colProvider.getColumn(i).setCount_Br(1);
            line[i] = "";
            if ( colProvider.getColumn(i).isAttribute()  )
            {
                String aux = colProvider.getColumn(i).getName(); 
                if(sqlBr.containsKey(aux))
                {
                    r--;
                    ResultSet rsltBr = (ResultSet)sqlBr.get(colProvider.getColumn(i).getName());
                    long bouiBr = -1;
                    if(!rsltBr.next())
                    {
                        rsltBr.beforeFirst();
                    }
                    else
                    {
                        bouiBr = rsltBr.getLong(1);
                    }
                    if(boui != bouiBr && setResultSet(rsltBr, boui))
                    {
                        bouiBr = rsltBr.getLong(1); 
                    }

                    boolean first=true;
                        
                    while(boui==bouiBr)
                    {
                        object = null;   
                        if(!first)
                        {
                            line[i] += "  ##  ";
                            colProvider.getColumn(i).setCount_Br(colProvider.getColumn(i).count_Br()+1);
                        }
                          line[i] += getStringValue(ctx, rsltBr, 2, colProvider.getColumn(i).getDefAttribute());

                          if(rsltBr.next())
                            bouiBr = rsltBr.getLong(1);
                          else
                            bouiBr=0;
                          first=false;
                        }
                        
                        rsltBr.previous();                        
                      }
                      else
                      {
                        line[i] += getStringValue(ctx, rslt, r+3, colProvider.getColumn(i).getDefAttribute());
                      }
                 }
                 else
                 {
                    String value = rslt.getString(r+3);
                    String val = tree.getColumnsProvider().getColumn(i).getValueResult(value, null);
                    line[i] += (val==null)?"":val;
                 }
            }    
            return line;
    }
    
    private static boolean setResultSet(ResultSet rs, long boui) throws SQLException
    {
        rs.beforeFirst();
        while(rs.next())
        {
            if(rs.getLong(1) == boui)
            {
                return true;
            }
        }
        return false;
    }
    
    private void writeObject(StringBuffer buffer,String[] line, String classname) throws boRuntimeException, SQLException
    {             
            
        int maxcols = 1;
        for (int i = 0; i < colProvider.columnsSize(); i++) 
        {
            if(colProvider.getColumn(i).count_Br() > maxcols)
            {
                maxcols = colProvider.getColumn(i).count_Br();   
            }                              
            colProvider.getColumn(i).setCount_Br(0);
        }
        
        //calcular as colunas e os valores para cada celula
        String lines[][] = new String[colProvider.columnsSize()][maxcols];
        for (int i = 0; i < colProvider.columnsSize(); i++) 
        {
            String vals[] =  line[i].split(" ## ");                
            for (int j = 0; j < vals.length; j++) 
            {
                lines[i][j] = vals[j];
            }
        }
        
        //imprimir a tabela com os dados
        for (int i = 0; i <maxcols; i++) 
        {
            buffer.append("<tr>\n");
                  
              if(nrgroups>0)
              {
                if(i==0)
                  buffer.append("<td class='gridCBody' width='"+(start-(2500/max_size))+"%'>&nbsp;</td>\n");
                else
                  buffer.append("<td width='"+(start-(25000/max_size))+"%'>&nbsp;</td>\n");
              }
              
              
              if(i==0) 
                buffer.append("<td class='gridCBody' width='"+2500/max_size+"%'><IMG src='"+boDefHandler.getBoDefinition(classname).getSrcForIcon16()+"' height=16 width=16 /></td>\n");
              else
                buffer.append("<td width='"+2500/max_size+"%'>&nbsp;</td>\n");
                
              buffer.append("<td width='5'>&nbsp;</td>\n");                  
              
              for (int j = 0; j < colProvider.columnsSize(); j++) 
              {
                if((lines[j][i]!=null) && (!lines[j][i].equals("")))
                {
                  buffer.append("<td class='gridCBody' width='"+((100*colProvider.getColumn(j).getWidth())/max_size)+"%'>"+lines[j][i]+"</td>\n");
                  buffer.append("<td width='5'>&nbsp;</td>\n");
                }
                else
                {
                  if(i==0) 
                    buffer.append("<td class='gridCBody' width='"+((100*colProvider.getColumn(j).getWidth())/max_size)+"%'>&nbsp;</td>\n");
                  else
                    buffer.append("<td width='"+((100*colProvider.getColumn(j).getWidth())/max_size)+"%'>&nbsp;</td>\n");
                    
                  buffer.append("<td width='5'>&nbsp;</td>\n");                  
                }
              }
              buffer.append("</tr>\n");
        }   
    }
    protected void writeBeginGroupLine(StringBuffer buffer,int groupIndex) throws boRuntimeException, SQLException
    {
        buffer.append("<table border='0' class='grid' cellpadding='0' cellspacing='0' id='GRC-"+(groupIndex+1)+"'>\n");        
    }
    protected void writeEndGroupLine(StringBuffer buffer) throws boRuntimeException, SQLException
    {
        buffer.append("</table>\n");        
    }    
    private void writeLines(StringBuffer buffer,ResultSet rslt,Hashtable sqlBr,int groupIndex) throws boRuntimeException, SQLException
    {            
        long oldboui = 0;
        long boui = -1;
        String[] line = null;
        String classname = null;
        while(rslt.next())
        {
            boui = rslt.getLong(2);        
            if(oldboui != boui)
            {
                oldboui =  boui;
                classname = rslt.getString(1);
                line = getRow(boui,rslt,sqlBr);                          
                writeObject(buffer,line, classname);
            }          
        }                  
    }
    protected void writeLine(StringBuffer buffer,ResultSet rslt,Hashtable sqlBr,int groupIndex) throws boRuntimeException, SQLException
    {            
        String[] line = null;
        String className = null;
        if(rslt.next())
        {            
            className = rslt.getString("CLASSNAME");
            line = getRow(rslt.getLong("BPR"),rslt,sqlBr);                          
            writeObject(buffer,line, className);
        }                  
    }
    private void calculateSizes()
    {
        start = 25;
        max_size = 0;
        for (int i = 0; i < nrgroups; i++,start+=20);
        
        //calcular tamano máximo de colunas para poder achar percentagem
        max_size = start;
        for (int i = 0; i <  colProvider.columnsSize(); i++) 
            max_size += colProvider.getColumn(i).getWidth() + 5;
        start = (100*start) / max_size;
        group_s = 20000/max_size;
    }
    public StringBuffer getPrintList() throws boRuntimeException, SQLException
    {
        long tInicial = System.currentTimeMillis();
        long tFinal = 0;
        StringBuffer result = new StringBuffer();
        
        //imprimir cabeçalhos
      
        //imprimir grupos
        writeGroupsTitle(result);        
            
        //imprimir titulos de colunas
        writeColumnsTitle(result);          
      
        //imprimir dados
        for (int groupIndex = 0; (groupIndex < groups.size()) || ((groupIndex==0) && (nrgroups==0)); groupIndex++) 
        {          
            
            writeGroup(result,groupIndex);
            
            String qryG[] = new String[nrgroups];
            if(nrgroups>0)
            {             
                String aux[] = (String[])groups.elementAt(groupIndex);
                for (int i = 0; i < nrgroups; i++) 
                {
                    qryG[i] = aux[i];
                }
            }
            
            PreparedStatement pr=null;
            ResultSet rslt=null;
          
            //calcular o sql
            String[] qry = tree.getSqlForExportWBridges( ctx, qryG );
            Hashtable sqlBr = new Hashtable();
            Vector prVec = new Vector();
            try{
                for (int i = 1; i < qry.length; i+=2) 
                  {
                      PreparedStatement prBr;
                      ResultSet rsltBr;
                  
                      //preencher os parameteros
                      prBr=cn.prepareStatement( qry[i+1] , ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY );                             
                      prBr.setFetchDirection(ResultSet.FETCH_UNKNOWN);
                      for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
                      {
                          prBr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
                      }
                      rsltBr= prBr.executeQuery();
                      //rsltBr.setFetchDirection(ResultSet.FETCH_UNKNOWN);
                      //rsltBr.beforeFirst();
                      prVec.add(prBr);
                      sqlBr.put(qry[i],rsltBr);
                  }
                //preencher os parameteros
                pr=cn.prepareStatement( qry[0] , ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );                                                          
                for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
                {
                  pr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
                }
                
                //percorrer todoas as linhas                   
                rslt= pr.executeQuery();
                boDefHandler bodef = null;
                
                writeBeginGroupLine(result,groupIndex);
                writeLines(result,rslt,sqlBr,groupIndex);
                writeEndGroupLine(result);
            }
            finally
            {
                if(rslt!=null)
                    rslt.close();
                if(pr!=null)
                    pr.close();
                  
              for (int i = 1; i < qry.length; i+=2) 
              {
                if(sqlBr.get(qry[i])!=null)
                    ((ResultSet)sqlBr.get(qry[i])).close();;
                if(prVec.elementAt(i/2)!=null)
                    ((PreparedStatement)prVec.elementAt(i/2)).close();;
              }
            }
        }
        tFinal = System.currentTimeMillis();
//        logger.finer("Tempo Total Print : " + (tFinal-tInicial)/1000 +"s" );
        logger.finer("Tempo Total Print : " + String.valueOf(tFinal-tInicial)  );
      
        return result;      
    }
    protected Explorer getExplorer()
    {
        return this.tree;
    }

    protected Vector getGroups()
    {
        return groups;
    }

    protected int getNrGroups()
    {
        return nrgroups;
    }
    protected EboContext getContext()
    {
        return this.ctx;
    }
    protected Connection getConnection()
    {
        return this.cn;
    }    
    private int getWidth()
    {
        int start = 25;
        for (int i = 0; i < nrgroups; i++,start+=20);      
        return start;
    }
    
    public String getStringValue( EboContext ctx, ResultSet rs, int index, boDefAttribute attDef) throws boRuntimeException, SQLException
    {
         if (attDef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
         {
             long b=rs.getLong(index);
             if ( b > 0 )
             {
                 boObject o =  boObject.getBoManager().loadObject( ctx, b );
                 return o.getCARDID().toString();
             }
             else return "";
         }
         else if("boolean".equalsIgnoreCase(attDef.getType()))
         {
            String value = rs.getString(index);
            if("0".equals(value))
            {
                //falta verificar a lingua
                return "Não";
            }
            else if("1".equals(value))
            {
                return "Sim";
            }
            return value;
         }             
         else if(attDef.getLOVName() != null &&  
            !"".equals(attDef.getLOVName()))
         {
            String xlov = attDef.getLOVName(); 
            String value = rs.getString(index);
            if(value != null && !"".equals(value))
            {
                String val = (String)lovs.get(attDef.getLOVName()+"##"+value);
                if(val != null && !"".equals(val))  return val;
                lovObject lovObj = LovManager.getLovObject(ctx, xlov);
                if(lovObj != null)
                {
                    lovObj.beforeFirst();
                    while(lovObj.next())
                    {
                        if(value.equalsIgnoreCase(lovObj.getCode()))
                        {
                            return lovObj.getDescription();
                        }
                    }
                }
            }
            return "";
         }
         else if("dateTime".equalsIgnoreCase(attDef.getType()))
         {
            Date d = rs.getTimestamp(index);            
            if(d != null)
            {
                 SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
                 return formatter.format(d);
            }
            return "";
         }
         else if("date".equalsIgnoreCase(attDef.getType()))
         {
            Date d = rs.getTimestamp(index);            
            if(d != null)
            {
                 SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy");
                 return formatter.format(d);
            }
            return "";
         }
         else
         {
            NumberFormat currencyFormatter = NumberFormat.getInstance();
            if(attDef.getDecimals() != 0)
            {
                //currency
                currencyFormatter.setParseIntegerOnly(false);
                if("Y".equalsIgnoreCase(attDef.getGrouping()))
                {
                    currencyFormatter.setGroupingUsed(true);
                }
                currencyFormatter.setMaximumFractionDigits(attDef.getDecimals());
                currencyFormatter.setMinimumFractionDigits(attDef.getMinDecimals());
                currencyFormatter.setMinimumIntegerDigits(1);
                return currencyFormatter.format(rs.getDouble(index));
            }
            else if("Y".equalsIgnoreCase(attDef.getGrouping()))
            {
                currencyFormatter.setParseIntegerOnly(false);
                currencyFormatter.setMinimumIntegerDigits(1);
                currencyFormatter.setGroupingUsed(true);
                return currencyFormatter.format(rs.getDouble(index));
            }
            String toRet = rs.getString(index); 
            return toRet == null ? "": toRet;
         }
    }    
    public String getStringValue( boObject obj, String p_atrName ) throws boRuntimeException
    {        
         AttributeHandler attr=obj.getAttribute( p_atrName );
         if ( attr.getDefAttribute().getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE )
         {
             long b=obj.getAttribute( p_atrName ).getValueLong();
             if ( b > 0 )
             {
                 boObject o = obj.getBoManager().loadObject( obj.getEboContext(), b );
                 return o.getCARDID().toString();
             }
             else return "";
         }
         else if("boolean".equalsIgnoreCase(attr.getDefAttribute().getType()))
         {
            String value = obj.getAttribute( p_atrName ).getValueString();
            if("0".equals(value))
            {
                //falta verificar a lingua
                return "Não";
            }
            else if("1".equals(value))
            {
                return "Sim";
            }
            return value;
         }             
         else if(obj.getAttribute( p_atrName ).getDefAttribute().getLOVName() != null &&  
            !"".equals(obj.getAttribute( p_atrName ).getDefAttribute().getLOVName()))
         {
            String xlov = obj.getAttribute( p_atrName ).getDefAttribute().getLOVName(); 
            String value = obj.getAttribute( p_atrName ).getValueString();
            if(value != null && !"".equals(value))
            {
                lovObject lovObj = LovManager.getLovObject(obj.getEboContext(), xlov);
                if(lovObj != null)
                {
                    lovObj.beforeFirst();
                    while(lovObj.next())
                    {
                        if(value.equalsIgnoreCase(lovObj.getCode()))
                        {
                            return lovObj.getDescription();
                        }
                    }
                }
            }
            return obj.getAttribute( p_atrName ).getValueString();
         }
         else if("dateTime".equalsIgnoreCase(attr.getDefAttribute().getType()))
         {
            Date d = null;            
            if((d = attr.getValueDate()) != null)
            {
                 SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
                 return formatter.format(d);
            }
            return "";
         }
         else if("date".equalsIgnoreCase(attr.getDefAttribute().getType()))
         {
            Date d = null;            
            if((d = attr.getValueDate()) != null)
            {
                 SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy");
                 return formatter.format(d);
            }
            return "";
         }
         else
         {
            NumberFormat currencyFormatter = NumberFormat.getInstance();
            if(obj.getAttribute( p_atrName ).getDefAttribute().getDecimals() != 0)
            {
                //currency
                currencyFormatter.setParseIntegerOnly(false);
                if("Y".equalsIgnoreCase(obj.getAttribute( p_atrName ).getDefAttribute().getGrouping()))
                {
                    currencyFormatter.setGroupingUsed(true);
                }
                currencyFormatter.setMaximumFractionDigits(obj.getAttribute( p_atrName ).getDefAttribute().getDecimals());
                currencyFormatter.setMinimumFractionDigits(obj.getAttribute( p_atrName ).getDefAttribute().getMinDecimals());
                currencyFormatter.setMinimumIntegerDigits(1);
                return currencyFormatter.format(obj.getAttribute( p_atrName ).getValueDouble());
            }
            else if("Y".equalsIgnoreCase(obj.getAttribute( p_atrName ).getDefAttribute().getGrouping()))
            {
                currencyFormatter.setParseIntegerOnly(false);
                currencyFormatter.setMinimumIntegerDigits(1);
                currencyFormatter.setGroupingUsed(true);
                return currencyFormatter.format(obj.getAttribute( p_atrName ).getValueDouble());
            }
            return obj.getAttribute( p_atrName ).getValueString();
         }
    }
    
}