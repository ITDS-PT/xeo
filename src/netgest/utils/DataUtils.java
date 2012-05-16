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
import netgest.bo.localizations.MessageLocalizer;
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
        throw(new SQLException(MessageLocalizer.getMessage("ERROR_GENERATING_A_SEQUENTIAL_NUMBER_FOR")+" ["+table+"].["+pkeys[i]+"]\n"+MessageLocalizer.getMessage("THE_COLUMN_HAS_TO_BE_NUMERIC")));
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
              throw(new RuntimeException(MessageLocalizer.getMessage("THE_TABLE_DOESNT_EXIT_IN_THE_DATATABLE_SCHEME_NOT_POSSIBLE_")));
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
          
          boolean createSeq = false;
          boolean updateSeq = true;
          
          if(nextval > Long.MIN_VALUE) {
              pstm = cn.prepareStatement("SELECT COUNTER FROM " + seqFullTableName + " WHERE SEQCHAVE=?");
              pstm.setString(1,seqchave);
              rslt = pstm.executeQuery();
              if(rslt.next()) 
                updateSeq = nextval == Long.MIN_VALUE || rslt.getLong(1) < nextval;
              else
                createSeq = true;

              rslt.close();
              pstm.close();                          
          }
          
          if(updateSeq && !createSeq) {
              if( nextval == Long.MIN_VALUE ) {
                  pstm = cn.prepareStatement("UPDATE " + seqFullTableName + " SET COUNTER=COUNTER+1 WHERE SEQCHAVE=?");
                  pstm.setString(1,seqchave);
              }
              else {
                  pstm = cn.prepareStatement("UPDATE " + seqFullTableName + " SET COUNTER=? WHERE SEQCHAVE=?");
                  pstm.setLong(1,nextval);
                  pstm.setString(2,seqchave);
              }
              
              int linesUpdated = pstm.executeUpdate();
              pstm.close();
              if(linesUpdated>0) {
                  pstm = cn.prepareStatement("SELECT COUNTER FROM " + seqFullTableName + " WHERE SEQCHAVE=?");
                  pstm.setString(1,seqchave);
                  rslt = pstm.executeQuery();
                  rslt.next();
                  retval = rslt.getLong(1);
                  rslt.close();
                  pstm.close();
              }
              else
                createSeq = true;            
          }
          
          if(createSeq) {
              if( nextval == Long.MIN_VALUE )
              {
                  retval = 1;
              }
              else
              {
                  retval = nextval;
              }
              
              if( rslt != null ) 
            	  rslt.close();
              if( pstm != null )
            	  pstm.close();
              
              pstm = cn.prepareStatement("INSERT INTO " + seqFullTableName+ " (SEQCHAVE,COUNTER) VALUES(?,?)");
              pstm.setString(1,seqchave);
              pstm.setLong(2,retval);
              pstm.executeUpdate();
              pstm.close();
          }
          
          /*
          pstm = cn.prepareStatement("SELECT COUNTER FROM " + seqFullTableName + " WHERE SEQCHAVE=? FOR UPDATE WAIT 60");
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
          }*/
      } 
      catch (SQLException e) 
      {
          if (e.getMessage().indexOf("ORA-00942")>-1) {
              throw(new RuntimeException(MessageLocalizer.getMessage("THE_TABLE_DOESNT_EXIT_IN_THE_DATATABLE_SCHEME_NOT_POSSIBLE_")));
          } else {
              throw(new RuntimeException(MessageLocalizer.getMessage("ERROR_IN_DATAUTILS_GETTING_NEXT_VALUE_FROM_SEQUENCE")+":\n"+e.getMessage()));
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


}