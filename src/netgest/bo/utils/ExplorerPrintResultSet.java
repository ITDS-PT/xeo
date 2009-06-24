/*Enconding=UTF-8*/
package netgest.bo.utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.presentation.render.elements.Explorer;
import netgest.bo.runtime.boRuntimeException;

import org.apache.log4j.Logger;

public class ExplorerPrintResultSet
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.utils.ExplorerResultSet");
    
    private final static int FETCH_SIZE_MAX = 300;
    private ResultSet resultSet = null;
    private PreparedStatement pstm = null;
    private ExplorerList explorerList = null;
    private Explorer explorer = null;
    private List positionInfo = null;
    private int position = -1;    
    private int groupPosition = -1;
    private int positionGroup = -1;    
    private int total = 0;    
    private int lastFetchSize = 0;
    private int totalFetchSize = 0;
    private Hashtable sqlBr = null;
    
    public ExplorerPrintResultSet(ExplorerList explorerList) throws boRuntimeException, SQLException
    {
        this.positionInfo = new ArrayList(50);        
        this.explorerList = explorerList;
        this.explorer = explorerList.getExplorer();
        this.preLoadIformation();
    }
    /**
     * Calcula os dados necessários para o resultset.
     * @throws netgest.bo.runtime.boRuntimeException
     * @throws java.sql.SQLException
     */
    private void preLoadIformation() throws boRuntimeException, SQLException
    {        
        Vector groups = explorerList.getGroups();
        if(groups.size() > 0)
        {                    
            int nrgroups = explorerList.getNrGroups();            
            String[] group = null;
            Long positionAux = null;
            for (int i = 0; i < groups.size(); i++) 
            {
                group = (String[])groups.get(i);
                positionAux = new Long(this.total);
                positionInfo.add(positionAux);
                total = total + Integer.parseInt(group[group.length - 1]) + 1;
            }
        }
        else
        {        
          
          PreparedStatement pstm = null;
          ResultSet rslt = null;             
          pstm = explorerList.getConnection().prepareStatement( explorer.getSqlForCount(explorerList.getContext())[0] , ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );
          for (int ip = 0; ip < explorer.p_parameters.size() ; ip++) 
          {
              pstm.setObject(ip+1, (Object)explorer.p_parameters.get(ip) );   
          }                             
          rslt = pstm.executeQuery();                              
          if(rslt.next())
          {
            total = rslt.getInt(1);                                                                          
          }                                
          rslt.close();
          pstm.close();
      }    
    }
    /**
     * Devolve se existe ou não mais um regidto.
     * @return TRUE se existe masis um registo, FALSE caso contrário. 
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public boolean next() throws boRuntimeException
    {
        boolean result = false;
        try 
        {
            if(this.position < this.total - 1)
            {
                this.position ++;
                this.groupPosition++;
                if(this.positionInfo.contains(new Long(this.position)))
                {           
                    this.positionGroup++;  
                    this.close();                                
                } 
                this.findNextData();
                result = true;
            }
            else
            {                
                this.close();
            }            
        } 
        catch (SQLException ex) 
        {
            throw new RuntimeException(ex.getMessage());
        }
        return result;
    }
    /**
     * Devolve a posição em que se encontra o resultset.
     * @return posição em que se encontra.
     */
    public long getRow()
    {
        return this.position;
    }
    /**
     * Devolve o objecto relativo a posição em que se encontra.
     * @return objecto nesta posição.
     */
    public Object getObject()
    {
        StringBuffer result = new StringBuffer();
        if(this.position == 0)
        {
            this.explorerList.writeHeader(result);
            this.explorerList.writeGroupsTitle(result);
            this.explorerList.writeColumnsTitle(result);
            this.getObject(result);
        }
        else if(this.position == this.total - 1)
        {
            this.getObject(result);
            this.explorerList.writeFooter(result);
        }
        else
        {
            this.getObject(result);
        }
        return result.toString();
    }
    private void getObject(StringBuffer result)
    {
        try 
        {
            if(this.positionInfo.contains(new Long(this.position)))
            {                           
                this.explorerList.writeGroup(result,this.positionGroup);                
            }     
            else
            {                
                if(this.canWriteBeginGroup())
                {
                    this.explorerList.writeBeginGroupLine(result,this.positionGroup);
                }                 
                
                this.explorerList.writeLine(result,resultSet,this.sqlBr,this.positionGroup);

                if(this.canWriteEndGroup())                
                {
                    this.explorerList.writeEndGroupLine(result);   
                }                                 
            }            
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
        }         
    }
    private void findNextData() throws boRuntimeException, SQLException  
    {
        boolean moreData = false; 
        if(this.resultSet == null)
        {
           moreData = true; 
        }
        else if(this.explorerList.getGroups().size() == 0 && this.position - 1 == getLastFetchSize())
        {
            moreData = true;                      
        }
        else
        {
            if( this.groupPosition - 1 == getLastFetchSize())
            {
               moreData = true;     
            }
        }
        
        if(moreData)
        {
            this.getData();
        }
    }    
    private void getData() throws boRuntimeException, SQLException    
    {
        int lastFetchSizeHere = this.getLastFetchSize();
        int totalFetchSizeHere = this.getTotalFetchSize();
        
        String[] qry = null;
        Vector prVec = null;
        if(this.resultSet == null)
        {
            Vector groups = explorerList.getGroups();
            int nrgroups = explorerList.getNrGroups();                        
                
            String qryG[] = new String[nrgroups];
            if(nrgroups>0)
            {             
                String aux[] = (String[])groups.elementAt(this.positionGroup);
                for (int i = 0; i < nrgroups; i++) 
                {
                    qryG[i] = aux[i];
                }
            }
                
            //calcular o sql
            qry = this.explorer.getSqlForExportWBridges( explorerList.getContext(), qryG );
            this.sqlBr = new Hashtable();
            prVec = new Vector();
            for (int i = 1; i < qry.length; i+=2) 
            {
                PreparedStatement prBr;
                ResultSet rsltBr;
          
              //preencher os parameteros
              prBr = explorerList.getConnection().prepareStatement( qry[i+1] , ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY );                             
              prBr.setFetchDirection(ResultSet.FETCH_UNKNOWN);
              for (int ip = 0; ip < this.explorer.p_parameters.size() ; ip++) 
              {
                  prBr.setObject(ip+1, (Object)this.explorer.p_parameters.get(ip) );   
              }
              rsltBr= prBr.executeQuery();
              prVec.add(prBr);
              sqlBr.put(qry[i],rsltBr);
           }
                  
              
            //preencher os parameteros
            this.pstm = explorerList.getConnection().prepareStatement( qry[0] , ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY );
            
            this.pstm.setFetchSize(getFetchSize());              
            
            for (int ip = 0; ip < this.explorer.p_parameters.size() ; ip++) 
            {
                this.pstm.setObject(ip+1, (Object)this.explorer.p_parameters.get(ip) );   
            }
                       
            resultSet = this.pstm.executeQuery();
        }
        
        if(lastFetchSizeHere  > 0)
        {
            resultSet.absolute(totalFetchSizeHere);   
        }                

        if(this.lastFetchSize == 0)
        {
            for (int i = 1; i < qry.length; i+=2) 
            {
                ((ResultSet)sqlBr.get(qry[i])).close();;
                ((PreparedStatement)prVec.elementAt(i/2)).close();;
            }            
        }                    
    }            
    /**
     * Fecha e inicializa os dados.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    private void close() throws boRuntimeException    
    {
        try 
        {
            this.lastFetchSize = 0;
            this.totalFetchSize = 0;
            this.groupPosition = 0;
        
            if(this.resultSet != null)
            {
                this.resultSet.close();                
            }        
            if(this.pstm != null)
            {
                this.pstm.close();                
            }                    
        } 
        catch (Exception ex) 
        {
           throw new RuntimeException(ex.getMessage());
        } 
        finally 
        {
            this.resultSet = null;
            this.pstm = null;
        }
        
    }
    /**
     * Devolve o fetch size em contexto.
     * @return número de rows a devolver.
     */
    private int getFetchSize()
    {
        int result = FETCH_SIZE_MAX;  
        int aux = 0;
        if(this.explorerList.getGroups().size() == 0)
        {
            aux = this.total - this.explorerList.getGroups().size();
            if(aux < FETCH_SIZE_MAX)
            {
                result = aux;
            }
            else if(this.total - this.position > 0 && this.total - this.position < FETCH_SIZE_MAX)
            {
                result = this.total - this.position;
            }                
        }
        else
        {
            String[] group = (String[])explorerList.getGroups().get(this.positionGroup);
            aux = Integer.parseInt(group[group.length - 1]);
            if(aux < FETCH_SIZE_MAX)
            {
                result = aux;
            }
            else if(aux - this.groupPosition > 0 && aux - this.groupPosition < FETCH_SIZE_MAX)
            {
                result = aux - this.groupPosition;
            }                
            
        }
        
        if(this.lastFetchSize != result)
        {
            this.totalFetchSize = this.totalFetchSize + result;
            this.lastFetchSize = result; 
        }
        return result;
    }
    /**
     * Devolve o último número de rows a devolvidos.
     * @return último número de rows a devolvidos.
     */
    private int getLastFetchSize()
    {
        return this.lastFetchSize;
    }
    /**
     * Devolve o total número de rows a devolvidos.
     * @return total número de rows a devolvidos.
     */
    private int getTotalFetchSize()
    {
        return this.totalFetchSize;
    }  
    /**
     * Devolve se é ou não, o inicio de um grupo.     
     * @return TRUE caso seja o inicio de um grupo, FALSE caso contrário.
     */
    private boolean canWriteBeginGroup()
    {
        boolean result = false;
        if(this.position - 1 == -1 || this.positionInfo.contains(new Long(this.position - 1)))
        {
            result = true;
        }
        return result;
    }
    /**
     * Devolve se é ou não, o fim de um grupo.     
     * @return TRUE caso seja o fim de um grupo, FALSE caso contrário.
     */    
    private boolean canWriteEndGroup()
    {
        boolean result = false;
        if(this.explorerList.getGroups().size() == 0 && this.position == this.total - 1)
        {
            result = true;
        }
        else if(this.explorerList.getGroups().size() > 0 )
        {
            if(this.positionInfo.contains(new Long(this.position + 1)))
            {
                result = true;    
            }
            else if(this.position + 1 == this.total)
            {
                result = true;
            }
        }
        return result;        
    }    
    /**
     * Devolve o resultado total.
     * @return resultado.
     */
    public String toString() 
    {
        StringBuffer result = new StringBuffer();
        try 
        {
            while( this.next() )
            {
                result.append(this.getObject());
            }                    
        } 
        catch (Exception ex) 
        {
            
        } 
        return result.toString();          
    }
    /**
     * Devolve um ficheiro com o resultado.
     * @return ficheiro com o resultado.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public File getFileResults()  throws boRuntimeException    
    {       
        File result = null;
        try 
        {
            String fileName = DocumentHelper.getTempDirHelper() + File.separator + System.currentTimeMillis() + ".html";
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fileWriter);
            while( this.next() )
            {
                out.write(this.getObject().toString());
            }                
            out.close();
            result = new File(fileName);
        } 
        catch (IOException e) 
        {
        }
        return result;        
    }
}