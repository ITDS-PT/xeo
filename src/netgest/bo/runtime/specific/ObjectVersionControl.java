/*Enconding=UTF-8*/
package netgest.bo.runtime.specific;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boObject;
/**
 * Interface a implementar para controlar as versões de um objecto de negócio.
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 1.0
 */ 
public interface ObjectVersionControl 
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
    
    public boolean checkOut() throws boRuntimeException;
    public boolean undoCheckOut() throws boRuntimeException;
    public boolean checkIn() throws boRuntimeException;                
    public boolean canCheckOut() throws boRuntimeException;
    public boolean canCheckIn() throws boRuntimeException;
    public boolean canUndoCheckOut() throws boRuntimeException;

    public boolean updateCheckOut() throws boRuntimeException;    
    public boObject getObjectFromCheckOut() throws boRuntimeException;
    public boObject getObjectFromVersion(long version) throws boRuntimeException;
    public boolean isDeserialized();
    public boolean delete() throws boRuntimeException;
}