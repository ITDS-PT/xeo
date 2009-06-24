/*Enconding=UTF-8*/
package netgest.bo.security;


import java.util.List;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

/**
 * <p>Title: ISecurityClassKeys </p>
 * <p>Description: Interface a implementar para devolver chaves de segurança programáticamente </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @Company: Enlace3 </p>
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 1.0
 */
public interface ISecurityClassKeys 
{
    public long[] getReadKeys(boObject object) throws boRuntimeException;
    public long[] getWriteKeys(boObject object) throws boRuntimeException;
    public long[] getDeleteKeys(boObject object) throws boRuntimeException;
    public long[] getFullControlKeys(boObject object) throws boRuntimeException;
}