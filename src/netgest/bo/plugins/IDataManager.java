/*Enconding=UTF-8*/
package netgest.bo.plugins;
import java.util.ArrayList;
import java.util.List;

import netgest.bo.data.DataSet;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

public interface IDataManager 
{

    /**
     * Método chamado pelo frameWork para carregar dados de um objecto.
     * 
     * @return  DataSet com os dados obtidos.
     * @param isboql    - true se a query recebida é BOQL
     * @param arguments - ArrayList com os argumentos para a query
     * @param pageSize  - Tamanho da página para DataSet's multiplos
     * @param page      - Página requisitada    
     * @param sqlquery  - SQL/BOQL Query. Se for SQL a query é apenas uma clausula Where
     * @param objdef    - Definição do objecto requsitado
     * @param ctx       - Contexto do pedido (EboContext)
     */
    public DataSet execute( 
                                EboContext      ctx, 
                                boDefHandler    objdef, 
                                String          sqlquery, 
                                int             page, 
                                int             pageSize, 
                                List       		arguments, 
                                boolean         isboql
                          ) 
                          throws boRuntimeException;
                          
                          
    /**
     * Método que é chamado para actualizar os dados de um objecto.
     * 
     * @throws netgest.bo.runtime.boRuntimeException
     * @return boolean - se correu tudo bem
     * @param  Se for detectadas concurrencia nos dados se faz o merge.
     * @param Objecto a ser gravado
     */
    public boolean updateObjectData( boObject object ) throws boRuntimeException;
    
    
    /**
     * 
     * @throws netgest.bo.runtime.boRuntimeException
     * @param object
     */
    public void     beforeObjectLoad( boObject object ) throws boRuntimeException;
    /**
     * 
     * @throws netgest.bo.runtime.boRuntimeException
     * @param object
     */
    public void     afterObjectLoad( boObject object ) throws boRuntimeException;
    
}