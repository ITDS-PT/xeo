/*Enconding=UTF-8*/
package netgest.bo.runtime.specific;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
/**
 * Interface a implementar para a relação com registo do objecto.
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 1.0
 */ 
public interface ObjectRes
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
   * Gera código para boObject em contexto. 
   * @throws netgest.bo.runtime.boRuntimeException
   */         
    public void generateCode() throws boRuntimeException;
    /**
     * Devolve o código do objecto no contexto.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return código do boObject no contexto.
     */
    public Object getCode() throws boRuntimeException;
  /**
   * Define o código do objecto no contexto. 
   * @throws netgest.bo.runtime.boRuntimeException
   * @param code para o boObject no contexto.
   * @return true se é válido, false caso contrário.
   */       
    public boolean setCode(Object code) throws boRuntimeException;
  /**
   * Devolve se o código do objecto no contexto é válido
   * @throws netgest.bo.runtime.boRuntimeException
   * @return true se é válido, false caso contrário.
   */         
    public boolean isValid() throws boRuntimeException;      
    /**
     * Devolve se o attributo é para estar disabled.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return true disabled , false caso contrário.
     */
    public boolean isDisabled()  throws boRuntimeException;
}