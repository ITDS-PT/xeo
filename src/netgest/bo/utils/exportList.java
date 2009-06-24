/*Enconding=UTF-8*/
package netgest.bo.utils;
import java.io.*;
import java.util.*;
import netgest.bo.def.*;
import netgest.bo.impl.*;
import netgest.bo.runtime.*;
import netgest.utils.*;
import javax.servlet.http.HttpServletResponse;
import oracle.xml.parser.v2.*;
import netgest.bo.dochtml.*;
import java.sql.*;


public final class exportList  implements Serializable {
    
    private Connection cn = null;
    private Vector groups = new Vector();
    private int nrgroups=0;
    private docHTML_treeAttribute[] p_cols;
    private docHTML_treeRuntime tree;
    private EboContext ctx;
    private Hashtable lovs;
    
    public exportList(EboContext ctx, docHTML_treeRuntime tree)  throws boRuntimeException, SQLException
    {
      cn= ctx.getConnectionData();
      p_cols = tree.p_cols;       
      groups = new Vector();
      nrgroups=tree.p_groups.length;
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
                          group[i] = tree.getGroupValue(rslt, i+1);
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
      for (int i = 0; i < p_cols.length; i++) 
      {
         if(p_cols[i].p_defatr!=null && !"".equals(p_cols[i].p_defatr.getLOVName()) && p_cols[i].p_defatr.getLOVName()!=null)
        {
          if(lovCond == null)
            lovCond = p_cols[i].p_defatr.getLOVName();
          else
            lovCond += "##"+p_cols[i].p_defatr.getLOVName();
        }
      }
      for (int i = 0; i < tree.p_groups.length; i++) 
      {
        if(tree.p_groups[i].p_defatr!=null && !tree.p_groups[i].p_defatr.getLOVName().equals(""))
        {
          if(lovCond == null)
            lovCond = tree.p_groups[i].p_defatr.getLOVName();
          else
            lovCond += "##"+tree.p_groups[i].p_defatr.getLOVName();
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
        for (int i = 0; i < tree.p_groups.length; i++) 
        {
          sb.append("\""+tree.p_groups[i].getLabel()+"\""+separator);        
        }
        for (int i = 0; i < p_cols.length; i++) 
        {
          if(i>0)
            sb.append(separator);
            
          sb.append("\""+p_cols[i].getLabel()+"\"");
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
    
                sb.append("\""+bodef.getLabel()+"\"" + separator);
                
                //imprimir os dados dos grupos
                for (int i = 0; i < nrgroups; i++) 
                {
                    if(tree.p_groups[i].p_defatr!=null && tree.p_groups[i].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
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
                      if(tree.p_groups[i].p_defatr!=null && !tree.p_groups[i].p_defatr.getLOVName().equals(""))
                        sb.append("\""+(String)lovs.get(tree.p_groups[i].p_defatr.getLOVName()+"##"+qryG[i])+"\"" + separator);
                      else if(tree.p_groups[i].p_defatr!=null && tree.p_groups[i].p_defatr.getType().equalsIgnoreCase("boolean"))
                      {
                        String val = tree.p_groups[i].getValueResult(qryG[i], null);
                        if(val!=null && val.equals("0"))
                          val="Não";
                        else if(val!=null && val.equals("1"))
                          val="Sim";
                        else
                          val="";
                        sb.append("\""+val+"\"" + separator); 
                      }
                      else
                       sb.append("\""+tree.p_groups[i].getValueResult(qryG[i], null)+"\"" + separator);
                    }
                }
                
                for (int i = 0,r=0; i < p_cols.length; i++,r++) 
                {
                    sb.append("\"");
                    if ( p_cols[i].p_isAttribute  )
                    {
                          if(sqlBr.containsKey(p_cols[i].p_name))
                          {
                            r--;
                            int n_cols = -1;
                            int countCols = 0;
                            if(tree.p_maxCols.containsKey(p_cols[i].p_name))
                              n_cols = ((Integer)tree.p_maxCols.get(p_cols[i].p_name)).intValue();
    
                            ResultSet rsltBr = (ResultSet)sqlBr.get(p_cols[i].p_name);
                            if(!rsltBr.next() || ((n_cols!=-1) && (countCols>=n_cols)))
                            {
                              sb.append("\""+separator);
                              continue;
                            }
                            
                            long bouiBr = rsltBr.getLong(1);
                            boolean first=true;
                            
                            while(boui==bouiBr)
                            {
                              countCols++;
                              if(!first)
                                sb.append("  ;  ");
                              if(p_cols[i].p_defatr.getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE)
                              {
                                 if(p_cols[i].p_defatr!=null && !"".equals(p_cols[i].p_defatr.getLOVName()) && p_cols[i].p_defatr.getLOVName()!=null)
                                 {
                                  String value = rsltBr.getString(2);
                                  if(lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value)!=null)
                                    value = (String)lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value);
                                  sb.append(value);
                                 }
                                else if(p_cols[i].p_defatr!=null && p_cols[i].p_defatr.getType().equalsIgnoreCase("boolean"))
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
                          else if(p_cols[i].p_defatr!=null && (p_cols[i].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) && (p_cols[i].p_defatr.getRelationType() == boDefAttribute.RELATION_1_TO_1))
                          {
                            String attName = p_cols[i].p_defatr.getName();
                            long bouiObj = rslt.getLong(r+3);
                            
                            if(bouiObj!=0)
                            {
                              boObject object = boObject.getBoManager().loadObject(ctx,bouiObj);
                              sb.append(object.getCARDIDwNoIMG(false));
                            }
                          }
                          else
                          {
                            String attName = p_cols[i].p_defatr.getName();
                            String value = rslt.getString(r+3);
                            if(value!=null)
                              value=value.replaceAll("\"","'");
                             if(value!=null && p_cols[i].p_defatr!=null && !"".equals(p_cols[i].p_defatr.getLOVName()) && p_cols[i].p_defatr.getLOVName()!=null)
                             {
                              if(lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value)!=null)
                                value = (String)lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value);
                              sb.append(value);
                             }
                            else if (p_cols[i].p_defatr!=null && p_cols[i].p_defatr.getType().equalsIgnoreCase("boolean"))
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
                              sb.append(tree.p_cols[i].getValueResult(value, null));
                          }
                     }
                     else
                     {
                        String value = tree.p_cols[i].getValueResult(rslt.getString(r+3), null);
                        //String value = "";//rslt.getString(p_cols[i].p_name);;
                        sb.append(value);                           
                     }
                     sb.append("\""+separator);
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
    
    public StringBuffer saveCSV_1Line_NColumns(String separator, boolean headers) throws boRuntimeException, SQLException
    {

      StringBuffer sb = new StringBuffer();
      
      //imprimir cabeçalhos
      if(headers)
      {
        sb.append("\"Objecto\""+separator);
        for (int i = 0; i < tree.p_groups.length; i++) 
        {
            sb.append("\""+tree.p_groups[i].getLabel()+"\""+separator);          
        }
        
        
        for (int i = 0; i < p_cols.length; i++) 
        {
          if(tree.p_maxCols.get(p_cols[i].p_name)==null)
          {
            sb.append("\""+p_cols[i].getLabel()+"\""+separator);  
            continue;
          }
            
          int n_cols = ((Integer)tree.p_maxCols.get(p_cols[i].p_name)).intValue();
          
          for (int j = 0; j < n_cols; j++) 
          {
            sb.append("\""+p_cols[i].getLabel()+"::"+(j+1)+"\""+separator);
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
                    if(tree.p_groups[i].p_defatr!=null && tree.p_groups[i].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
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
                      if(tree.p_groups[i].p_defatr!=null && !tree.p_groups[i].p_defatr.getLOVName().equals(""))
                        sb.append("\""+(String)lovs.get(tree.p_groups[i].p_defatr.getLOVName()+"##"+qryG[i])+"\""+separator);
                      else if(tree.p_groups[i].p_defatr!=null && tree.p_groups[i].p_defatr.getType().equalsIgnoreCase("boolean"))
                      {
                        String val = tree.p_groups[i].getValueResult(qryG[i], null);
                        if(val!=null && val.equals("0"))
                          val="Não";
                        else if(val!=null && val.equals("1"))
                          val="Sim";
                        else
                          val="";
                        sb.append("\""+val+"\"" + separator); 
                      }                    
                      else
                        sb.append("\""+tree.p_groups[i].getValueResult(qryG[i], null)+"\"" + separator);
                    }
                }
                
                for (int i = 0,r=0; i < p_cols.length; i++,r++) 
                {
                    
                    if ( p_cols[i].p_isAttribute  )
                    {
                          if(sqlBr.containsKey(p_cols[i].p_name))
                          {
                            r--;
                            ResultSet rsltBr = (ResultSet)sqlBr.get(p_cols[i].p_name);
                            int n_cols = ((Integer)tree.p_maxCols.get(p_cols[i].p_name)).intValue();
                            int in;
    
                            if(!rsltBr.next())
                            {
                              for(in=0;in<n_cols;in++)
                                sb.append("\"\""+separator);
                              continue;
                            }
                            long bouiBr = rsltBr.getLong(1);
                            for(in=0;boui==bouiBr;in++)
                            {
                              if((in!=0) && (in==n_cols))
                                break;
                                
                              if(p_cols[i].p_defatr.getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE)
                              {
                                 if(p_cols[i].p_defatr!=null && !"".equals(p_cols[i].p_defatr.getLOVName()) && p_cols[i].p_defatr.getLOVName()!=null)
                                 {
                                  String value = rsltBr.getString(2);
                                  if(lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value)!=null)
                                    value = (String)lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value);
                                  sb.append("\""+value+ "\"" + separator);
                                 }
                                else if(p_cols[i].p_defatr!=null && p_cols[i].p_defatr.getType().equalsIgnoreCase("boolean"))
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
                          else if(p_cols[i].p_defatr!=null && (p_cols[i].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) && (p_cols[i].p_defatr.getRelationType() == boDefAttribute.RELATION_1_TO_1))
                          {
                            String attName = p_cols[i].p_defatr.getDbName();
                            long bouiObj = rslt.getLong(r+3);
                            
                            if(bouiObj!=0)
                            {
                              boObject object = boObject.getBoManager().loadObject(ctx,bouiObj);
                              sb.append("\""+object.getCARDIDwNoIMG(false).toString()+"\""+separator);
                            }
                          }
                          else
                          {
                            String attName = p_cols[i].p_defatr.getDbName();
                            String value = rslt.getString(r+3);
                            if(value!=null)
                              value=value.replaceAll("\"","'");
                             if(value!=null && p_cols[i].p_defatr!=null && !"".equals(p_cols[i].p_defatr.getLOVName()) && p_cols[i].p_defatr.getLOVName()!=null)
                             {
                              if(lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value)!=null)
                                value = (String)lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value);
                              sb.append("\""+value+"\""+separator);
                             }
                            else if (p_cols[i].p_defatr!=null && p_cols[i].p_defatr.getType().equalsIgnoreCase("boolean"))
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
                              sb.append("\""+tree.p_cols[i].getValueResult(value, null)+"\""+separator);
                          }
                     }
                     else
                     {
                        String value = tree.p_cols[i].getValueResult(rslt.getString(r+3), null);
                        //String value = "";//rslt.getString(p_cols[i].p_name);;
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
        for (int i = 0; i < tree.p_groups.length; i++) 
        {
          sb.append("\""+tree.p_groups[i].getLabel()+"\""+separator);        
        }
        for (int i = 0; i < p_cols.length; i++) 
        {
          if(i>0)
            sb.append(separator);
            
          sb.append("\""+p_cols[i].getLabel()+"\"");
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
                    if(tree.p_groups[i].p_defatr!=null && tree.p_groups[i].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
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
                    if(tree.p_groups[i].p_defatr!=null && !tree.p_groups[i].p_defatr.getLOVName().equals(""))
                      sb.append("\""+(String)lovs.get(tree.p_groups[i].p_defatr.getLOVName()+"##"+qryG[i])+"\"" + separator);
                    else if(tree.p_groups[i].p_defatr!=null && tree.p_groups[i].p_defatr.getType().equalsIgnoreCase("boolean"))
                    {
                      String val = tree.p_groups[i].getValueResult(qryG[i], null);
                      if(val!=null && val.equals("0"))
                        val="Não";
                      else if(val!=null && val.equals("1"))
                        val="Sim";
                      else
                        val="";
                      sb.append("\""+val+"\"" + separator); 
                    }                  
                    else
                      sb.append("\""+tree.p_groups[i].getValueResult(qryG[i], null)+"\""+ separator);
                    }
                }
                
                for (int i = 0; i < p_cols.length; i++)
                {
                  if ( p_cols[i].p_isAttribute  )
                  {
                  
                      if(p_cols[i].p_defatr!=null && p_cols[i].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
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
                         if(value!=null && p_cols[i].p_defatr!=null && !"".equals(p_cols[i].p_defatr.getLOVName()) && p_cols[i].p_defatr.getLOVName()!=null)
                         {
                            value = (String)lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value);
                         }
                         else if (p_cols[i].p_defatr!=null && p_cols[i].p_defatr.getType().equalsIgnoreCase("boolean"))
                         {
                             if(value!=null && value.equals("0"))
                                value="Não";
                             else if(value!=null && value.equals("1"))
                                value="Sim";
                             else
                                value="";
                         }                                             
                        else
                            value = tree.p_cols[i].getValueResult(value, null);
                        if(value!=null)
                          sb.append("\""+value+"\"");
                        else
                          sb.append("\"\"");
                        sb.append(separator);
                      }
                  }
                  else
                  {
                    String value = tree.p_cols[i].getValueResult(rslt.getString(i+3), null);
                    //String value = "";//rslt.getString(p_cols[i].p_name);
                    sb.append("\""+value+"\"");
                    sb.append(separator);
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
          }
      }
      
      return sb;
      
    }
    
    public XMLDocument saveXML(String user) throws boRuntimeException, SQLException
    {
      
      
      XMLDocument xmldoc = new XMLDocument();
      xmldoc.setEncoding("ISO-8859-1");
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
                if(tree.p_groups[i].p_defatr!=null && tree.p_groups[i].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
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
                    if(tree.p_groups[i].p_defatr!=null && !tree.p_groups[i].p_defatr.getLOVName().equals(""))
                      grpValue =  (String)lovs.get(tree.p_groups[i].p_defatr.getLOVName()+"##"+qryG[i]);
                    else if(tree.p_groups[i].p_defatr!=null && tree.p_groups[i].p_defatr.getType().equalsIgnoreCase("boolean"))
                    {
                      String val = tree.p_groups[i].getValueResult(qryG[i], null);
                      if(val!=null && val.equals("0"))
                        grpValue="Não";
                      else if(val!=null && val.equals("1"))
                        grpValue="Sim";
                      else
                        grpValue="-";
                    }                      
                    else
                      grpValue = tree.p_groups[i].getValueResult(qryG[i], null);
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
                xmlelem.setAttribute("name",""+tree.p_groups[i].getName()); 
                xmlelem.setAttribute("description",""+tree.p_groups[i].getLabel());
                if(tree.p_groups[i].p_defatr!=null && tree.p_groups[i].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
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
                     if(tree.p_groups[i].p_defatr!=null && !tree.p_groups[i].p_defatr.getLOVName().equals(""))
                        xmlelem.setAttribute("value",(String)lovs.get(tree.p_groups[i].p_defatr.getLOVName()+"##"+qryG[i]));
                      else if(tree.p_groups[i].p_defatr!=null && tree.p_groups[i].p_defatr.getType().equalsIgnoreCase("boolean"))
                      {
                        String val = tree.p_groups[i].getValueResult(qryG[i], null);
                        if(val!=null && val.equals("0"))
                          val="Não";
                        else if(val!=null && val.equals("1"))
                          val="Sim";
                        else
                          val="-";
                        xmlelem.setAttribute("value",val);
                      }                              
                      else
                          xmlelem.setAttribute("value",""+tree.p_groups[i].getValueResult(qryG[i], null)); 
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
    
    
                for (int i = 0,r=0; i < p_cols.length; i++,r++) 
                {
                    if(sqlBr.containsKey(p_cols[i].p_name))
                        xmlelem = (XMLElement)xmldoc.createElement("MultiValueAttribute");
                    else
                        xmlelem = (XMLElement)xmldoc.createElement("Attribute");
                    xmlelem.setAttribute("name",p_cols[i].getName()); 
                    xmlelem.setAttribute("description",p_cols[i].getLabel()); 
                    elemPar.appendChild(xmlelem);
                    elemPar=xmlelem;
    
                    if ( p_cols[i].p_isAttribute  )
                    {
                          if(sqlBr.containsKey(p_cols[i].p_name))
                          {
                            r--;
                            ResultSet rsltBr = (ResultSet)sqlBr.get(p_cols[i].p_name);
                            if(!rsltBr.next())
                            {
                              elemPar=(XMLElement)elemPar.getParentNode();
                              continue;
                            }
                            long bouiBr = rsltBr.getLong(1);
                            
                            while(boui==bouiBr)
                            {
    
                              xmlelem = (XMLElement)xmldoc.createElement("Value");
                              elemPar.appendChild(xmlelem);
                              elemPar=xmlelem;
    
                              if(p_cols[i].p_defatr.getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE)
                              {
                                 if(p_cols[i].p_defatr!=null && !"".equals(p_cols[i].p_defatr.getLOVName()) && p_cols[i].p_defatr.getLOVName()!=null)
                                 {
                                    String value = rsltBr.getString(2);
                                    if(value!=null)
                                      value=value.replaceAll("\"","'");
                                    else
                                      value="";
                                    elemPar.addText((String)lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value));
                                 }
                                  else if(p_cols[i].p_defatr!=null && p_cols[i].p_defatr.getType().equalsIgnoreCase("boolean"))
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
                          else if(p_cols[i].p_defatr!=null && (p_cols[i].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) && (p_cols[i].p_defatr.getRelationType() == boDefAttribute.RELATION_1_TO_1))
                          {
                            String attName = p_cols[i].p_defatr.getDbName();
                            long bouiObj = rslt.getLong(r+3);
                            
                            if(bouiObj!=0)
                            {
                              boObject object = boObject.getBoManager().loadObject(ctx,bouiObj);
                              elemPar.addText(object.getCARDIDwNoIMG(false).toString());
                            }
                          }
                          else
                          {
                            String attName = p_cols[i].p_defatr.getDbName();
                            String value = rslt.getString(r+3);
                            if(value!=null)
                              value=value.replaceAll("\"","'");
                             if(value!=null && p_cols[i].p_defatr!=null && !"".equals(p_cols[i].p_defatr.getLOVName()) && p_cols[i].p_defatr.getLOVName()!=null)
                             {
                              if(lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value)!=null)
                                value = (String)lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value);
                              elemPar.addText(value);
                             }
                            else if (p_cols[i].p_defatr!=null && p_cols[i].p_defatr.getType().equalsIgnoreCase("boolean"))
                            {
                              if(value!=null && value.equals("0"))
                                  value="Não";
                              else if(value!=null && value.equals("1"))
                                  value="Sim";
                              if(value!=null)
                                elemPar.addText(value);    
                            }                          
                            else if(value!=null)
                              elemPar.addText(tree.p_cols[i].getValueResult(value, null));
                          }
                     }
                     else
                     {
                        String value = tree.p_cols[i].getValueResult(rslt.getString(r+3), null);
                        //String value = "";//rslt.getString(p_cols[i].p_name);;
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


   public StringBuffer getPrintList() throws boRuntimeException, SQLException
    {

      StringBuffer sb = new StringBuffer();
      int max_size = 0;
      
      //imprimir cabeçalhos
      
      //imprimir grupos
      if(nrgroups>0)
      {
        sb.append("<table border='0' cellpadding='0' cellspacing='0' style='border-collapse: collapse' id='GR_HEADER'>\n");
        sb.append("<tr>\n");
				sb.append("\n");
        
        for (int i = 0; i < nrgroups; i++) 
        {
          sb.append("<td class='gridCHeader'>"+((tree.p_groups[i].p_defatr==null)?tree.p_groups[i].getLabel():tree.p_groups[i].p_defatr.getLabel())+"</td>\n");  
          sb.append("<td width='5'>&nbsp;</td>\n");
        }

				sb.append("<td class='label'><font color='#C0C0C0'><b>GRUPOS</b></font></td>\n");
        sb.append("</tr>\n");
        sb.append("</table>\n");

      }
      
			sb.append("<br>\n");

      int start = 25;
      for (int i = 0; i < nrgroups; i++,start+=20);
      
      //calcular tamano máximo de colunas para poder achar percentagem
      max_size = start;
      for (int i = 0; i <  p_cols.length; i++) 
        max_size += p_cols[i].getWidth() + 5;
      start = (100*start) / max_size;
      int group_s = 20000/max_size;
      
      
      //imprimir titulos de colunas
      sb.append("<table border='0' class='grid' cellpadding='0' cellspacing='0'  id='table1'>\n");
      sb.append("<tr>\n");
      sb.append("<td class='gridCHeader' width='"+start+"%'>&nbsp;</td>\n");      

      sb.append("<td width='5'>&nbsp;</td>\n");                  
      
      for (int i = 0; i <  p_cols.length; i++) 
      {
        sb.append("<td class='gridCHeader' width='"+((100*p_cols[i].getWidth())/max_size)+"%'>"+((p_cols[i].p_defatr==null)?p_cols[i].getLabel():p_cols[i].p_defatr.getLabel())+"</td>\n");
        sb.append("<td width='5'>&nbsp;</td>\n");
      }

      sb.append("</tr>\n");
      sb.append("</table>\n");
      sb.append("<br>\n");

  //-----------------------------------------------------
  
      
      //imprimir dados
      for (int ig = 0; (ig < groups.size()) || ((ig==0) && (nrgroups==0)); ig++) 
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
          
          //imprimir os grupos
          
            for (int i = 0; i < nrgroups; i++) 
            { 
              sb.append("<table border='0' cellpadding='0' cellspacing='0' style='border-collapse: collapse' id='GR-"+(ig+1)+"-"+(i+1)+"'>\n");
              sb.append("<tr>\n");
              if(i>0)
                sb.append("<td width='"+(i*20)+"'>&nbsp;</td>\n");
              if(tree.p_groups[i].p_defatr!=null && tree.p_groups[i].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
              {                  
                  if(aux[i]!=null && !aux[i].equals(""))
                  {
                    boObject object = boObject.getBoManager().loadObject(ctx,Long.parseLong(aux[i]));
                    sb.append("<td class='labelDst'>"+object.getCARDID(false).toString()+"</td>\n");
                  }
                  else
                    sb.append("<td class='labelDst'>( )</td>\n");
              }
              else
              {
                if(tree.p_groups[i].p_defatr!=null && !tree.p_groups[i].p_defatr.getLOVName().equals(""))
                {
                    String val = (String)lovs.get(tree.p_groups[i].p_defatr.getLOVName()+"##"+qryG[i]);
                    if(val==null)
                      val="()";
                    sb.append("<td class='labelDst'>"+val+"</td>\n");
                }
                else if(tree.p_groups[i].p_defatr!=null && tree.p_groups[i].p_defatr.getType().equalsIgnoreCase("boolean"))
                {
                  String val = tree.p_groups[i].getValueResult(qryG[i], null);
                  if(val!=null && val.equals("0"))
                    val="Não";
                  else if(val!=null && val.equals("1"))
                    val="Sim";
                  else
                    val="";
                  sb.append("<td class='labelDst'>"+val+"</td>\n");
                }                                      
                else
                    sb.append("<td class='labelDst'>"+tree.p_groups[i].getValueResult(qryG[i], null)+"</td>\n");
              }
              sb.append("<td>&nbsp;</td>\n");
              sb.append("</tr>\n");
              sb.append("</table>\n");
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
              
              sb.append("<table border='0' class='grid' cellpadding='0' cellspacing='0' id='GRC-"+(ig+1)+"'>\n");  
              
              long oldboui=0;
              while(rslt.next())
              {
                long boui = rslt.getLong(2);
                if(boui==oldboui)
                  continue;
                oldboui=boui;
    
                String classname = rslt.getString(1);
                
                String line[] = new String[ p_cols.length+1];
                
                for (int i = 0,r=0; i < p_cols.length; i++,r++) 
                {
                    p_cols[i].p_countBr = 1;
                    line[i] = "";
                    if ( p_cols[i].p_isAttribute  )
                    {
                          if(sqlBr.containsKey(p_cols[i].p_name))
                          {
                            r--;
                            ResultSet rsltBr = (ResultSet)sqlBr.get(p_cols[i].p_name);
                            if(!rsltBr.next())
                            {
                              continue;
                            }
                            long bouiBr = rsltBr.getLong(1);
                            boolean first=true;
                            
                            while(boui==bouiBr)
                            {
                              if(!first)
                              {
                                line[i] += "  ##  ";
                                p_cols[i].p_countBr++;
                              }
                              if(p_cols[i].p_defatr.getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE)
                              {
                                    if(p_cols[i].p_defatr!=null && !"".equals(p_cols[i].p_defatr.getLOVName()) && p_cols[i].p_defatr.getLOVName()!=null)
                                    {
                                      String value = rslt.getString(2);
                                      if(lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value)!=null)
                                        value = (String)lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value);
                                      line[i] += value;
                                    }
                                    else if(p_cols[i].p_defatr!=null && p_cols[i].p_defatr.getType().equalsIgnoreCase("boolean"))
                                    {
                                      String val = rsltBr.getString(2);
                                      if(val!=null && val.equals("0"))
                                        val="Não";
                                      else if(val!=null && val.equals("1"))
                                        val="Sim";
                                      else
                                        val="";
                                      line[i] += val;
                                    }                                                              
                                    else
                                      line[i] += rsltBr.getString(2);
                              }
                              else if(rsltBr.getLong(2)>0)
                              {
                                boObject object = boObject.getBoManager().loadObject(ctx,rsltBr.getLong(2));
                                line[i] += object.getCARDID(false);
                              }
                              
                              if(rsltBr.next())
                                bouiBr = rsltBr.getLong(1);
                              else
                                bouiBr=0;
                              first=false;
                            }
                            
                            rsltBr.previous();                        
                          }
                          else if(p_cols[i].p_defatr!=null && (p_cols[i].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) && (p_cols[i].p_defatr.getRelationType() == boDefAttribute.RELATION_1_TO_1))
                          {
                            String attName = p_cols[i].p_defatr.getDbName();
                            long bouiObj = rslt.getLong(r+3);
                            
                            if(bouiObj!=0)
                            {
                              boObject object = boObject.getBoManager().loadObject(ctx,bouiObj);
                              line[i] += object.getCARDID(false);
                            }
                          }
                          else
                          {
                            String attName = p_cols[i].p_defatr.getDbName();
                            String value = rslt.getString(r+3);
                             if(value!=null && p_cols[i].p_defatr!=null && !"".equals(p_cols[i].p_defatr.getLOVName()) && p_cols[i].p_defatr.getLOVName()!=null)
                             {
                              String val = (String)lovs.get(p_cols[i].p_defatr.getLOVName()+"##"+value); 
                              line[i] += (val==null)?value:val;
                             }
                            else if (value!=null && p_cols[i].p_defatr!=null && p_cols[i].p_defatr.getType().equalsIgnoreCase("boolean"))
                            {
                              if(value!=null && value.equals("0"))
                                  value="Não";
                              else if(value!=null && value.equals("1"))
                                  value="Sim";
                              else
                                  value="";
                              line[i] += value;    
                            }                             
                            else
                            {
                              String val = tree.p_cols[i].getValueResult(value, null);
                              line[i] += (val==null)?"":val;
                            }
                          }
                     }
                     else
                     {
                        String value = rslt.getString(r+3);
                        String val = tree.p_cols[i].getValueResult(value, null);
                        line[i] += (val==null)?"":val;
                     }
                     
                }
                int maxcols =1;
                for (int i = 0; i < p_cols.length; i++) 
                {
                  if(p_cols[i].p_countBr > maxcols)
                    maxcols = p_cols[i].p_countBr;
                  
                  p_cols[i].p_countBr=0;
                }
                
                //calcular as colunas e os valores para cada celula
                String lines[][] = new String[p_cols.length][maxcols];
                for (int i = 0; i < p_cols.length; i++) 
                {
                  String vals[] =  line[i].split(" ## ");
                  int j;
                  for (j = 0; j < vals.length; j++) 
                  {
                    lines[i][j] = vals[j];
                  }
                }
                
                //imprimir a tabela com os dados
                for (int i = 0; i <maxcols; i++) 
                {
                  sb.append("<tr>\n");
                  
                  if(nrgroups>0)
                  {
                    if(i==0)
                      sb.append("<td class='gridCBody' width='"+(start-(2500/max_size))+"%'>&nbsp;</td>\n");
                    else
                      sb.append("<td width='"+(start-(25000/max_size))+"%'>&nbsp;</td>\n");
                  }
                  
                  
                  if(i==0) 
                    sb.append("<td class='gridCBody' width='"+2500/max_size+"%'><IMG src='"+boDefHandler.getBoDefinition(classname).getSrcForIcon16()+"' height=16 width=16 /></td>\n");
                  else
                    sb.append("<td width='"+2500/max_size+"%'>&nbsp;</td>\n");
                    
                  sb.append("<td width='5'>&nbsp;</td>\n");                  
                  
                  for (int j = 0; j < p_cols.length; j++) 
                  {
                    if((lines[j][i]!=null) && (!lines[j][i].equals("")))
                    {
                      sb.append("<td class='gridCBody' width='"+((100*p_cols[j].getWidth())/max_size)+"%'>"+lines[j][i]+"</td>\n");
                      sb.append("<td width='5'>&nbsp;</td>\n");
                    }
                    else
                    {
                      if(i==0) 
                        sb.append("<td class='gridCBody' width='"+((100*p_cols[j].getWidth())/max_size)+"%'>&nbsp;</td>\n");
                      else
                        sb.append("<td width='"+((100*p_cols[j].getWidth())/max_size)+"%'>&nbsp;</td>\n");
                        
                      sb.append("<td width='5'>&nbsp;</td>\n");                  
                    }
                  }
                  sb.append("</tr>\n");
                }
        
              }
              sb.append("</table>\n");  
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
    
}