/*Enconding=UTF-8*/
package netgest.bo.impl.states;
import java.sql.*;

import java.util.*;

import netgest.bo.impl.*;
import netgest.bo.runtime.*;

import netgest.utils.*;

/**
 * 
 * @Company Enlace3
 * @author Luís Eduadro Moscoso Barreira
 * @version 1.0
 */
public abstract class requestObjectImpl extends actionObjectImpl 
{

    public String[] getStateMethods( boObject object ) throws boRuntimeException
    {/*
        if(object.getParent()!=null)
          return super.getStateMethods(object);
          
        if(object.getParents().length > 0)
          return super.getStateMethods(object);
         */
        return null;
    }
   
}