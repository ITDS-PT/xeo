/*Enconding=UTF-8*/
package netgest.utils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Hashtable;
import java.util.Vector;

import netgest.bo.data.Driver;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.system.boRepository;

import netgest.exceptions.NGTException;

public class DataUtils
{
	
	public static final byte SEQUENCE_NEXTVAL = Driver.SEQUENCE_NEXTVAL;
	public static final byte SEQUENCE_CURRENTVAL = Driver.SEQUENCE_CURRENTVAL;
	
	public DataUtils()
	{
	}
  public static String GetLastNumber(Connection conn,String table,String[] pkeys, String[] pkvals) throws SQLException {
     String where="";
     if (pkeys.length > 1) where = " Where ";
     int i=0;
     for (i=0;i < pkeys.length-1;i++) {
          where += pkeys[i] + "='" + pkvals[i] + "' ";
          if (i < pkeys.length-2) where += " AND ";
     }
     try {
         ResultSet xrslt=null;
         Statement stm = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
         xrslt = stm.executeQuery("Select max(TO_NUMBER(" + pkeys[i]+")+1) From " + table + where );
         long xret=0;

         if (xrslt.next()) xret = xrslt.getLong(1);
         if(xret==0) xret=1;
         
         xrslt.close();
         stm.close();
         stm = null;
         return ""+xret;
     } catch (SQLException e) {
        throw(new SQLException("Erro a gerar um número sequencial para ["+table+"].["+pkeys[i]+"]\nA coluna tem que ser numérica."));
     }
  }
  public static final long checkSequenceNextVal( boApplication app, Connection cn, String seqchave )
  {
      PreparedStatement pstm=null;
      ResultSet rslt=null;
      long retval=1;
      String seqFullTableName = "";
      String schemaName = boRepository.getDefaultSchemaName(app);
      if(schemaName != null && !"".equals(schemaName))
      {
        seqFullTableName = schemaName + ".";
      }
      seqFullTableName += "SYSNGT_SEQUENCES";
      try {
          pstm = cn.prepareStatement("SELECT COUNTER FROM " + seqFullTableName + " WHERE SEQCHAVE=?");
          pstm.setString(1,seqchave);
          rslt = pstm.executeQuery();
          if (rslt.next()) {
              retval = rslt.getLong(1)+1;
          }
      } catch (SQLException e){
          if (e.getMessage().indexOf("ORA-00942")>-1) {
              throw(new RuntimeException("A tabela [SYSNGT_SEQUENCES] não existe no esquema da tabela dados, não é possivel gerar próximo numero"));
          } else {
              throw new RuntimeException(e.getMessage());
          }
      } 
      finally 
      {
          try
          {
              rslt.close();
          }
          catch (Exception e)
          {
              
          }
          try
          {
              pstm.close();
          }
          catch (Exception e)
          {
              
          }
      }
      return retval;
      
  }

  public static final long updateSequenceNextVal(boApplication app, Connection cn, String seqchave, long value )
  {
       return sequenceNextVal(app, cn, seqchave, value );
  }
  public static final long GetSequenceNextVal(boApplication app,Connection cn, String seqchave)
  {
       return sequenceNextVal(app, cn, seqchave, Long.MIN_VALUE );
  }

  public static final long sequenceNextVal(boApplication app, Connection cn, String seqchave, long nextval ) 
  {
      long retval = 1;
      PreparedStatement pstm=null;
      ResultSet rslt=null;      
      try {
          String schemaName = boRepository.getDefaultSchemaName(app);
          String seqFullTableName = "";
          if(schemaName != null && !"".equals(schemaName))
          {
            seqFullTableName = schemaName + ".";
          }
          seqFullTableName += "SYSNGT_SEQUENCES";
          pstm = cn.prepareStatement("SELECT COUNTER FROM " + seqFullTableName + " WHERE SEQCHAVE=? FOR UPDATE");
          pstm.setString(1,seqchave);
          rslt = pstm.executeQuery();
          if (rslt.next()) {
              if( nextval == Long.MIN_VALUE || rslt.getLong(1) < nextval )
              {
                  if( nextval == Long.MIN_VALUE )
                  {
                      retval = rslt.getLong(1)+1;
                  }
                  else
                  {
                      retval = nextval;
                  }
                  rslt.close();
                  pstm.close();
                  pstm = cn.prepareStatement("UPDATE " + seqFullTableName + " SET COUNTER=? WHERE SEQCHAVE=?");
                  pstm.setLong(1,retval);
                  pstm.setString(2,seqchave);
                  pstm.executeUpdate();
                  pstm.close();
              } 
              else
              {
                  rslt.close();
                  pstm.close();
              }
          } else {
              if( nextval == Long.MIN_VALUE )
              {
                  retval = 1;
              }
              else
              {
                  retval = nextval;
              }
              rslt.close();
              pstm.close();
              pstm = cn.prepareStatement("INSERT INTO " + seqFullTableName+ " (SEQCHAVE,COUNTER) VALUES(?,?)");
              pstm.setString(1,seqchave);
              pstm.setLong(2,retval);
              pstm.executeUpdate();
              pstm.close();
          }
      } 
      catch (SQLException e) 
      {
          if (e.getMessage().indexOf("ORA-00942")>-1) {
              throw(new RuntimeException("A tabela [SYSNGT_SEQUENCES] não existe no esquema da tabela dados, não é possivel gerar próximo numero"));
          } else {
              throw(new RuntimeException("Error in DataUtils geting next value from sequence:\n"+e.getMessage()));
          }
      } 
      finally
      {
         try
         {
             rslt.close();
         }
         catch (Exception e)
         {
             
         }
         try
         {
             pstm.close();
         }
         catch (Exception e)
         {
             
         }
      }
      return retval;
  }


  public static final long getDataDBSequence( EboContext ctx, String seqname, byte OPER) {
	  return ctx.getDataBaseDriver().getDBSequence( ctx, seqname, Driver.SEQUENCE_DATADS ,OPER );
  }

  public static final long getSystemDBSequence( EboContext ctx, String seqname,byte OPER) {
	  return ctx.getDataBaseDriver().getDBSequence( ctx, seqname, Driver.SEQUENCE_SYSTEMDS, OPER );
  }

  /*
  public static final long getDBSequence(Connection cn,String seqname,String operation) throws SQLException {
      if (cn.getAutoCommit()==true) cn.setAutoCommit(false);
      long ret = 0;
      String seqFullTableName = "";
      String sql = "SELECT "+seqname+"."+operation+" FROM DUAL";
      PreparedStatement pstm = null;
      try {
          ResultSet rslt = (pstm=cn.prepareStatement(sql)).executeQuery();
          if(rslt.next()) {
              ret = rslt.getLong(1);
              //cn.commit();
              rslt.close();
              pstm.close();
              return ret; 
          }
          else {
              rslt.close();
              pstm.close();
              throw(new SQLException("Erro a obter sequencia ["+seqname+"]"));
          }
      } catch (SQLException e) {
          if(e.getMessage().indexOf("08002")==-1) {
              pstm.close();              
              pstm = cn.prepareStatement("CREATE SEQUENCE "+seqname+" CACHE 20 NOCYCLE ORDER");
              pstm.execute();
              //cn.commit();
              pstm.close();
          }
          if(!operation.equalsIgnoreCase("nextval")) {
              pstm = cn.prepareStatement("SELECT "+seqname+".nextval FROM DUAL");
              pstm.execute();
              //cn.commit();
              pstm.close();
          }
          ResultSet rslt = (pstm=cn.prepareStatement(sql)).executeQuery();
          if(rslt.next()) {
              ret = rslt.getLong(1);
              //cn.commit();
              rslt.close();
              pstm.close();
              return ret;
          }
          else {
              rslt.close();
              pstm.close();
              throw(new SQLException("Erro a obter sequencia ["+seqname+"]"));
          }
      }
  }


  public static final String[] getPrimaryKeys(Connection conn,String xtable) throws SQLException,NGTException  {
      return getPrimaryKeys(conn,xtable,true);
  }


  public static final String sql_getprimarykeys = "Select b.Column_Name from User_constraints a,User_Cons_Columns b where a.Table_Name=? And a.Constraint_Type='P' AND b.Constraint_Name=a.Constraint_Name order by position";
  
  public static final String[] getPrimaryKeys(Connection conn,String xtable,boolean exception) throws SQLException,NGTException  {
      PreparedStatement pstm = conn.prepareStatement(sql_getprimarykeys,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
      pstm.setString(1,xtable);
      ResultSet rslt = pstm.executeQuery();
      Vector xflds = new Vector();
      while (rslt.next()) {
          xflds.add(rslt.getString(1));
      }
      rslt.close();
      pstm.close();
      if(xflds.size()==0) {
          if(!exception) return null;
          throw(new NGTException("NGT-01011",xtable));
      }
      String[] xretval = new String[xflds.size()];
      int i=0;
      for (i=0;i < xflds.size();i++) xretval[i] = (String)xflds.get(i);
      pstm = null;
      return xretval;
  }
  public static final String getVtblTableName(Connection conn2,String xtable) throws SQLException,NGTException  {
     String retVal;
     ResultSet rslt = null;
     Statement stm = conn2.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
     if (xtable.indexOf(".")!=-1) xtable=xtable.substring(xtable.indexOf(".")+1,xtable.length());

     rslt = stm.executeQuery("SELECT STABLENAME FROM NGTVIRTUALTABLESX WHERE SVIRTUALTABLE='" + xtable + "'");
     if (!rslt.next()) throw(new NGTException("NGT-01012",xtable));;
     retVal = rslt.getString("STABLENAME");
     rslt.close();
     stm.close();
     stm=null;
     return retVal;
  }
  public static final String getbdocnodetablename = "SELECT STABLENAME FROM NGT_BDOCS A,NGT_EFORMS B,NGTVIRTUALTABLESX C WHERE A.ID_BDOC = ? AND A.EFORMID=B.EFORMID AND B.NODENAME = ? AND B.VTABLE = C.SVIRTUALTABLE";
  public static final String getBODCNodeTableName(String bdoc,String nodename,EboContext eboctx) throws SQLException {
      PreparedStatement pstm = eboctx.getConnectionDef().prepareStatement(getbdocnodetablename);
      pstm.setString(1,bdoc);
      pstm.setString(2,nodename);
      ResultSet rslt = pstm.executeQuery();
      String ret;
      if (rslt.next()) {
          ret = rslt.getString(1);
      } else {
          rslt.close();
          pstm.close();
          throw(new SQLException("Não foi possivel obter a tabela para o Business Documenr ["+bdoc+"."+nodename+"]"));
      }
      rslt.close();
      pstm.close();
      return ret;
  }
*/
  
  public static final String buildQuery(String[] primarykeys) {
     int i;
     String xret=" ";
     for (i=0;i<primarykeys.length-1;i++) {
        xret += primarykeys[i] + "=? AND ";
     }
     xret += primarykeys[i] + "=? ";
     return xret;
  }
  
  public static final String concatFields( Driver driver, String[] xfields) {
    int i;
    StringBuffer ret = new StringBuffer();
    if (xfields!=null) {
        
    	for(i=0;i<xfields.length-1;i++) 
            ret.append( driver.getEscapeCharStart() ).append(xfields[i]).append(driver.getEscapeCharEnd()).append(",");
        
        ret.append( driver.getEscapeCharStart() ).append( xfields[i] ).append( driver.getEscapeCharEnd() );
    }
    return ret.toString();
  }

/*
  public static final String getVTBLFieldsAndTable1 = "SELECT A.STABLENAME,B.SFIELDNAME FROM NGTVIRTUALTABLES B,NGTVIRTUALTABLESX A WHERE A.SVIRTUALTABLE=? AND A.SVIRTUALTABLE=B.SVIRTUALTABLE";


  private static Hashtable vtblfldht = new Hashtable();
  public static final Object[] getStringVTBLFieldsAndTable(EboContext eboctx,String vtablename) throws SQLException {
      Object[] ret;
      if((ret=(Object[])vtblfldht.get(eboctx.getApplication().getName()+vtablename))==null) {
          ret = new Object[2];
          String[] xfields;

          Connection cn = eboctx.getConnectionDef();
          PreparedStatement pstm = cn.prepareStatement(getVTBLFieldsAndTable1);
          pstm.setString(1,vtablename);
          ResultSet rslt = pstm.executeQuery();
          Vector fields = new Vector();
          if(rslt.next()) {
              ret[0] = rslt.getString(1);
              do {
                  fields.add(rslt.getString(2));
              }while(rslt.next());
          } else {
              rslt.close();
              pstm.close();
              throw(new SQLException("A Virtual Table ["+vtablename+"] não foi encontrada"));
          }
          rslt.close();
          pstm.close();
          ret[1] = (String[])fields.toArray(new String[0]);
          //ret[1] = tools.VectorToString(fields);
          vtblfldht.put(eboctx.getApplication().getName()+vtablename,ret);
      }
      return ret;
  }
  public static final String getPrimaryKeysValuesString(ResultSet rslt,String[] p_pkeys) throws SQLException {
      String ret="";
      int i;
      for (i=0;i<p_pkeys.length;i++) {
          ret += rslt.getString(p_pkeys[i])+"+";
      }
      return ret;
  }
  public static final String getFieldDescription(Connection cndef,String tablename,String fieldname) throws SQLException {
      String ret;
      PreparedStatement pstm=cndef.prepareStatement("SELECT FRIENDLYNAME FROM NGTDIC WHERE TABLENAME=? AND OBJECTNAME=?");
      pstm.setString(1,tablename);
      pstm.setString(2,fieldname);
      ResultSet rslt = pstm.executeQuery();
      if(!(rslt.next() && (ret=rslt.getString(1))!=null)) {
          ret = fieldname+" - Descrição não disponível.";
      }
      rslt.close();
      pstm.close();
      return ret;
  }
*/

}