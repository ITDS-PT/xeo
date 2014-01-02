/*Enconding=UTF-8*/
package netgest.bo.runtime.specific;

import java.util.List;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

/**
 * Interface a implementar para a relação fisica de um boObject.
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 1.0
 */  
public interface ObjectBinary  
{
  /**
   * Define qual o boObject em contexto. 
   * @param object boObject no contexto.
   */ 
    public void setContextObject(boObject object);
  /**
   * Devolve o boObject em contexto. 
   * @return object boObject no contexto.
   */     
    public boObject getContextObject();
  /**
   * Devolve uma lista de binary objects do boObject em contexto. 
   * @return lista de objectos binarios do boObject no contexto.
   */     
    public List getBinary() throws boRuntimeException;
  /**
   * Devolve um determinado binary object através do boui. 
   * @return boObject que é um binário do boObject no contexto.
   */      
    public boObject getBinary(long boui) throws boRuntimeException;  
  /**
   * Define quais os objectos binarios através de uma lista. 
   * @param objects, List de objectos binarios.
   */    
    public void setBinary(List objects) throws boRuntimeException;
  /**
   * Define um objecto binário através deum objecto. 
   * @param binary objecto binário.
   */      
    public void setBinary(Object binary) throws boRuntimeException;
  /**
   * Remove um objecto binário através de um objecto. 
   * @param binary objecto binário.
   */          
    public void remove(Object binary) throws boRuntimeException;
  /**
   * Devolve um objecto binário através de um boui. 
   * @param objecto fisico.
   */    
    public Object getFisical(long boui) throws boRuntimeException;
}