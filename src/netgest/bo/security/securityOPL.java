/*Enconding=UTF-8*/
package netgest.bo.security;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefOPL;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.utils.ClassUtils;


public class securityOPL
{
    public static final byte READ_KEY = 1;
    public static final byte WRITE_KEY = 2;
    public static final byte DELETE_KEY = 4;
    public static final byte FULL_CONTROL = 8;
    public static final int MASK_READ = 0xFE; //254
    public static final int MASK_WRITE = 0xFD; // 253
    public static final int MASK_DELETE = 0xFC; //252
    public static final int MASK_FULL_CONTROL = 0xFB; //251

    public securityOPL()
    {
    }

    public static void setSecurityKeys(boObject o) throws boRuntimeException
    {
        boDefOPL os = o.getBoDefinition().getBoOPL();
        String[] deleteKeys = os.getDeleteKeyAttributes();
        String[] readKeys = os.getReadKeyAttributes();
        String[] fullKeys = os.getFullControlKeyAttributes();
        String[] writeKeys = os.getWriteKeyAttributes();

        //Keys for the attributes cannot be null, create the empty array
        if (readKeys == null)
        	readKeys = new String[0];
        if (deleteKeys == null)
        	deleteKeys = new String[0];
        if (writeKeys== null)
        	writeKeys = new String[0];
        if (fullKeys == null)
        	fullKeys = new String[0];
        
        // Keys nos
        String[] executeKeysMethods = os.getMethodsExecuteKeys();
        String[] executeKeysEvents = os.getEventsExecuteKeys();
        
        long[] keys = new long[10];
        int[] keysValue = new int[10];
        int INDEX = -1;
        long key;
        long[][] classKeys = null;
        
        classKeys = getSecurityClassKeys(o);
        
        //Martelada deverá ser implementado de outra forma
        //As classkeys para que sejam definidas necessitam que existam atributeKeys para cada tipo
        //de permissão isto não deveria acontecer. As alterações aqui efectuadas destinam-se a permitir
        //esse comportamento sem alterar o código efectuado, os arrays dos attributeKeys estarão sempre preenchidos
        //mas com um valor dummy que se for encontrado não processará os attributeKeys
        // Tony 2/07/2008
        
        if ( o.getBoDefinition().getBoOPL().getClassKeys().length>0)
        {
          if (readKeys.length==0) readKeys=new String[]{"#dummy#"};
          if (deleteKeys.length==0) deleteKeys=new String[]{"#dummy#"};
          if (fullKeys.length==0) fullKeys=new String[]{"#dummy#"};
          if (writeKeys.length==0) writeKeys=new String[]{"#dummy#"};
        }
        
        for (int i = 0; i < readKeys.length; i++)
        {
            long[] keysATR =null;
            if (i==0 && readKeys[i].equals("#dummy#"))
              keysATR = new long[]{};
            else
              keysATR = securityOPL.getKeys(o, readKeys[i]);
                       
            if(classKeys[0] != null && classKeys[0].length > 0)
            {
                long[] result = new long[keysATR.length + classKeys[0].length];
                System.arraycopy(keysATR,0,result,0,keysATR.length);
                System.arraycopy(classKeys[0],0,result,keysATR.length,classKeys[0].length);            
                keysATR = result;
            }
            for (int z = 0; z < keysATR.length; z++)
            {
                key = keysATR[z];

                boolean found = false;
                int indice = 0;

                for (int j = 0; !found && (j <= INDEX); j++)
                {
                    if (keys[j] == key)
                    {
                        found = true;
                        indice = j;
                    }
                }

                if (!found)
                {
                    INDEX++;

                    if (INDEX > keys.length-1)
                    {
                        keys = ClassUtils.growLongArray(keys, 10);
                        keysValue = ClassUtils.growIntArray(keysValue, 10);
                    }

                    indice = INDEX;
                    keys[INDEX] = key;
                    keysValue[INDEX] = 0;
                }

                keysValue[indice] = keysValue[indice] | securityOPL.READ_KEY;
            }
        }

        for (int i = 0; i < writeKeys.length; i++)
        {          
            long[] keysATR =null;
            if (i==0 && writeKeys[i].equals("#dummy#"))
              keysATR = new long[]{};
            else
              keysATR = securityOPL.getKeys(o, writeKeys[i]);
            
            if(classKeys[1] != null && classKeys[1].length > 0)
            {
                long[] result = new long[keysATR.length + classKeys[1].length];
                System.arraycopy(keysATR,0,result,0,keysATR.length);
                System.arraycopy(classKeys[1],0,result,keysATR.length,classKeys[1].length);            
                keysATR = result;
            }
            
            for (int z = 0; z < keysATR.length; z++)
            {
                key = keysATR[z];

                boolean found = false;
                int indice = 0;

                for (int j = 0; !found && (j <= INDEX); j++)
                {
                    if (keys[j] == key)
                    {
                        found = true;
                        indice = j;
                    }
                }

                if (!found)
                {
                    INDEX++;

                    if (INDEX > keys.length-1)
                    {
                        keys = ClassUtils.growLongArray(keys, 10);
                        keysValue = ClassUtils.growIntArray(keysValue, 10);
                    }

                    indice = INDEX;
                    keys[INDEX] = key;
                    keysValue[INDEX] = 0;
                }

                keysValue[indice] = keysValue[indice] | securityOPL.WRITE_KEY;
            }
        }

        for (int i = 0; i < deleteKeys.length; i++)
        {           
            long[] keysATR =null;
            if (i==0 && deleteKeys[i].equals("#dummy#"))
              keysATR = new long[]{};
            else
              keysATR = securityOPL.getKeys(o, deleteKeys[i]);
              
            if(classKeys[2] != null && classKeys[2].length > 0)
            {
                long[] result = new long[keysATR.length + classKeys[2].length];
                System.arraycopy(keysATR,0,result,0,keysATR.length);
                System.arraycopy(classKeys[2],0,result,keysATR.length,classKeys[2].length);            
                keysATR = result;
            }
            for (int z = 0; z < keysATR.length; z++)
            {
                key = keysATR[z];

                boolean found = false;
                int indice = 0;

                for (int j = 0; !found && (j <= INDEX); j++)
                {
                    if (keys[j] == key)
                    {
                        found = true;
                        indice = j;
                    }
                }

                if (!found)
                {
                    INDEX++;

                    if (INDEX > keys.length-1)
                    {
                        keys = ClassUtils.growLongArray(keys, 10);
                        keysValue = ClassUtils.growIntArray(keysValue, 10);
                    }

                    indice = INDEX;
                    keys[INDEX] = key;
                    keysValue[INDEX] = 0;
                }

                keysValue[indice] = keysValue[indice] | securityOPL.DELETE_KEY;
            }
        }

        for (int i = 0; i < fullKeys.length; i++)
        {            
            long[] keysATR =null;
            if (i==0 && fullKeys[i].equals("#dummy#"))
              keysATR = new long[]{};
            else
              keysATR = securityOPL.getKeys(o, fullKeys[i]);
              
            if(classKeys[3] != null && classKeys[3].length > 0)
            {
                long[] result = new long[keysATR.length + classKeys[3].length];
                System.arraycopy(keysATR,0,result,0,keysATR.length);
                System.arraycopy(classKeys[3],0,result,keysATR.length,classKeys[3].length);            
                keysATR = result;
            }
            for (int z = 0; z < keysATR.length; z++)
            {
                key = keysATR[z];

                boolean found = false;
                int indice = 0;

                for (int j = 0; !found && (j <= INDEX); j++)
                {
                    if (keys[j] == key)
                    {
                        found = true;
                        indice = j;
                    }
                }

                if (!found)
                {
                    INDEX++;

                    if (INDEX > keys.length-1)
                    {
                        keys = ClassUtils.growLongArray(keys, 10);
                        keysValue = ClassUtils.growIntArray(keysValue, 10);
                    }

                    indice = INDEX;
                    keys[INDEX] = key;
                    keysValue[INDEX] = 0;
                }

                keysValue[indice] = keysValue[indice] |
                    securityOPL.FULL_CONTROL;
            }
        }
              
        
        bridgeHandler bkeys = o.getBridge("KEYS");        
        bkeys.beforeFirst();
        
        //long[] keysprocessed= new long[ keys.length ];
        while (!bkeys.isEmpty() && bkeys.next())
        {
            long keyb=-1;
            try
            {
            keyb=bkeys.getValueLong();
            }
            catch(Exception e)
            {
                e=e;
            }
            if (keyb!=-2)
            {
              boObject x = bkeys.getObject(); //tem que ser mudado 
              keyb = x.getBoui();
            }
            int v = ClassUtils.convertToInt(bkeys.getAttribute("securityCode")
                                                 .getValueString(), -1);

            /* verifica se esta key é para manter e faz o set do valor */
            boolean found = false;

            for (int i = 0; !found && (i < keys.length); i++)
            {
                if (keys[i] == keyb)
                {
                    found = true;
                    keys[i] = -9;
                    bkeys.getAttribute("securityCode").setValueLong((long) keysValue[i]);
                }
            }

            if (!found)
            {
            	//FIXME: Estou a mudar isto, mas não sei se é só isto
            	if (bkeys.getAttribute("securityType") != null)
            	{
            		if (bkeys.getAttribute("securityType").getValueLong() == 0)
            		{ //Só remove caso
            			bkeys.remove();
    	                bkeys.previous();
            		}
            	
            	}
            	else
            	{
	                bkeys.remove();
	                bkeys.previous();
            	}
            }
        }

        for (int i = 0; i <= INDEX; i++)
        {
            if (keys[i] != -9)
            {
                bkeys.add(keys[i]);

                //boObject x = bkeys.getObject(); /**tem que ser mudado */
                bkeys.getAttribute("securityCode").setValueLong((long) keysValue[i]);
            }
        }                
    }
    private static long[][] getSecurityClassKeys( boObject o ) throws boRuntimeException
    {
        long[][] returnKeys = null;
        
        boDefOPL os = o.getBoDefinition().getBoOPL();
        String[] className  = os.getClassKeys();
        returnKeys = getValuesFromSecurityClasses( o , className );
//        if(securityOPL.READ_KEY == typeKey)
//        {
//            className = os.getClassForReadKeys();
//            if(className != null)
//            {
//                returnKeys = getValuesFromSecurityClasses(o,className,typeKey);   
//            }                    
//        }
//        else if(securityOPL.WRITE_KEY == typeKey)
//        {
//            className = os.getClassForWriteKeys();
//            if(className != null)
//            {            
//                returnKeys = getValuesFromSecurityClasses(o,className,typeKey);
//            }
//        }
//        else if(securityOPL.DELETE_KEY == typeKey)
//        {
//            className = os.getClassForDeleteKeys();
//            if(className != null)
//            {
//                returnKeys = getValuesFromSecurityClasses(o,className,typeKey);
//            }
//        }
//        else if(securityOPL.FULL_CONTROL == typeKey)
//        {
//            className = os.getClassForFullControlKeys();
//            if(className != null)
//            {            
//                returnKeys = getValuesFromSecurityClasses(o,className,typeKey);
//            }
//        }                
        return returnKeys;   
    }
    private static long[][] getValuesFromSecurityClasses(boObject o,String[] className ) throws boRuntimeException
    {
        
        long[][] returnKeys = new long[4][];
        long[][] keys = null;

        ISecurityClassKeys securityClassKeys = null;
        
        if (className != null){
        	
            for (int i = 0; i < className.length; i++)
	        {
	            securityClassKeys = getSecurityClass(o,className[i]);
	            
	            long[] keysr = securityClassKeys.getReadKeys(o);
	            long[] keysw = securityClassKeys.getWriteKeys(o);
	            long[] keysd = securityClassKeys.getDeleteKeys(o);
	            long[] keysf = securityClassKeys.getFullControlKeys(o);
	            
	            returnKeys[0] = keysr;
	            returnKeys[1] = keysw;
	            returnKeys[2] = keysd;
	            returnKeys[3] = keysf;
	        }
        }
        return returnKeys;
    }      
    
    public static final long[] appendArray( long[] orig, long[] toappend )
    {
        long[] ret = orig;
        if( toappend != null && orig != null )
        {
            long[] narr = new long[ orig.length + toappend.length ];
            System.arraycopy( orig, 0, narr, 0, orig.length );
            System.arraycopy( toappend, 0, narr, orig.length, toappend.length );
            ret = narr;
        }
        else if( toappend != null )
        {
            ret = toappend;
        }
        return ret;
    }
    
    private static ISecurityClassKeys getSecurityClass(boObject obj,String code)  throws boRuntimeException
    {
        ISecurityClassKeys result = null;         
        try
        {          
            Class cls = obj.getClass().getClassLoader().loadClass(code);
            result = (ISecurityClassKeys)cls.newInstance();                                                       
        }
        catch (Exception e)
        {
            throw new boRuntimeException(code, "SecurityClassKeys", e);
        }    
        return result;
    }  
    private static long[] getKeys(boObject o, String attributeName)
        throws boRuntimeException
    {
       //JA PERMITE TODAS AS SITUACOES
       
        long[] toRet = new long[0];
        AttributeHandler atr=null;
        long key = 0;        
        
        if (attributeName.equalsIgnoreCase("_public"))
        {
          toRet=new long[1];
          toRet[0]=-2;
          return toRet;
        }
       // int ipoint=attributeName.indexOf(".");
        
        
//        attributeName=attrs[attrs.length-1];
        String[] attrs= attributeName.split("\\.");
        if ( attrs.length == 1) 
        {
          atr = o.getAttribute(attributeName);
          toRet=getBouis(o,atr);
        }
        else
        {          
          
          AttributeHandler attr=o.getAttribute( attrs[0] );
          if ( attr.isBridge() )
          {
             
             bridgeHandler bridgeAtr=o.getBridge( attrs[0]  );
             String attributeStr="";
             for (int i = 1; i < attrs.length ; i++) 
             {
                attributeStr+=attrs[i];
                if ( i+1 < attrs.length ) attributeStr+=".";
             }
             
             toRet=getKeys( bridgeAtr , attributeStr );
          }
          else
          {
              if ( attr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
              {
                    
                    boObject obj=attr.getObject();
                    if (obj!=null)
                    {
                        
                        String attributeStr="";
                        for (int i = 1; i < attrs.length ; i++) 
                        {
                            attributeStr+=attrs[i];
                            if ( i+1 < attrs.length ) attributeStr+=".";
                        }
                        toRet=getKeys( obj , attributeStr );
                    }
                         
              }
              else
              {
                  //ERRO
                  
              }
          }
              
        
        }
        
        return toRet;
    }

    private static long[] getKeys(bridgeHandler o, String attributeName)
        throws boRuntimeException
    {
        //Parte-se do principo que so pode ira ao 2 nivel e que no caso de se ir ao 2 nivel ex:(obj.obj2) o atributo de primeiro 
        // nivel sera sempre um objecto enquanto que o primeiro podera ser uma bridge. No caso de existir apenas um nivel
        // o atributo podera ser uma bridge ou um objecto. ALTERAR PARA PERMITIR TODAS AS SITUACOES!!!!
        long[] toRet = new long[0];
        
        long[] bridgeKeys = new long[0];
        AttributeHandler atr=null;
        long key = 0;        
        
//        int ipoint=attributeName.indexOf(".");
        
        
//        attributeName=attrs[attrs.length-1];
        int record = o.getRow();
        if( !o.isEmpty() )
        {
            o.beforeFirst(); 
            while( o.next() )
            {
        
                String[] attrs= attributeName.split("\\."); 
                if ( attrs.length == 1) 
                {
                
                  atr = o.getAttribute(attributeName);
                  toRet=getBouis(o,atr);
                  if( toRet != null && toRet.length > 0 )
                  {
                      long[] tmp = new long[ bridgeKeys.length + toRet.length ];
                      System.arraycopy( bridgeKeys, 0, tmp, 0, bridgeKeys.length );
                      System.arraycopy( toRet, 0, tmp, bridgeKeys.length , toRet.length );
                      bridgeKeys = tmp;
                  }
                  else
                  {
                    atr = o.getObject().getAttribute(attributeName);
                    toRet=getBouis(o,atr);
                    if( toRet != null && toRet.length > 0 )
                    {
                        long[] tmp = new long[ bridgeKeys.length + toRet.length ];
                        System.arraycopy( bridgeKeys, 0, tmp, 0, bridgeKeys.length );
                        System.arraycopy( toRet, 0, tmp, bridgeKeys.length , toRet.length );
                        bridgeKeys = tmp;
                    }
                  }
                }
                else
                {          
                  
                  AttributeHandler attr=o.getAttribute( attrs[0] );
                  if ( attr.isBridge() )
                  {
                     ///???COMO? NAO PODE SER BRIDGE-BRIDGE
                     
                  }
                  else
                  {
                      if ( attr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                      {
                            
                            boObject obj=o.getAttribute( attrs[1] ).getObject();
                            if (obj!=null)
                            {
                                String attributeStr="";
                                for (int i = 1; i < attrs.length ; i++) 
                                {
                                    attributeStr+=attrs[i];
                                    if ( i+1 < attrs.length ) attributeStr+=".";
                                }
                                toRet=getKeys( obj , attributeStr );
                                  if( toRet != null && toRet.length > 0 )
                                  {
                                      long[] tmp = new long[ bridgeKeys.length + toRet.length ];
                                      System.arraycopy( bridgeKeys, 0, tmp, 0, bridgeKeys.length );
                                      System.arraycopy( toRet, 0, tmp, bridgeKeys.length , toRet.length );
                                      bridgeKeys = tmp;
                                  }
                            }
                                 
                      }
                      else
                      {
                          //ERRO
                          
                      }
                  }
                      
                
                }
            }
        }
        o.moveTo( record );
        return bridgeKeys;
    }

    private static long[] getBouis(boObject o,AttributeHandler atr) throws boRuntimeException
    {      
      long[] toRet=new long[0];
      if (atr!=null && o!=null)
      {
        String attributeName=atr.getName();
        if (atr.isBridge())
        {
          bridgeHandler bh=o.getBridge(attributeName);          
          toRet=new long[bh.getRowCount()];
          int i=0;
          if (bh!=null)
          {
            bh.beforeFirst();
            while (bh.next())
            {
              toRet[i]=bh.getObject().getBoui();
              i++;
            }
          }
        }
        else
        {
          atr = o.getAttribute(attributeName);
          long key=0;
          if (atr != null)
          {
              key = atr.getValueLong();
          }
  
          if (key > 0)
          {
              toRet = new long[1];
              toRet[0] = key;
          }
          else
          {
              toRet = new long[0];
          }
        }
      }
      
      return toRet;
    }
    
     private static long[] getBouis(bridgeHandler o,AttributeHandler atr) throws boRuntimeException
    {      
      long[] toRet=new long[0];
      if (atr!=null && atr.getValueObject() != null )
      {
          long key = atr.getValueLong();
          if (key > 0)
          {
              toRet = new long[1];
              toRet[0] = key;
          }
      }
      return toRet;
    }
    
    private static boolean _can(boObject o, byte type) throws boRuntimeException
    {
      boolean hasKeys=false;
      boolean hasKeysPermissions=false;
      
      if( o.getEboContext().getRequest() != null )
      {
          if ( o.getEboContext().getRequest().getRequestURI().endsWith("__explorer.jsp") )
          {
              return true;
          }
      } 
      
      bridgeHandler bkey = o.getBridge("KEYS");
      if (bkey!=null && bkey.getRowCount()!=0) hasKeys=true;
      
      bkey = o.getBridge("KEYS_PERMISSIONS");
      if (bkey!=null && bkey.getRowCount()!=0) hasKeysPermissions=true;
      
      boolean toRet=false;
      
      if (!hasKeys && !hasKeysPermissions)
      {
        toRet=true;
        if (type==securityOPL.FULL_CONTROL) toRet=false;
      }
      else if (hasKeys && !hasKeysPermissions)
        toRet=_canKeys(o,type);
      else if (!hasKeys && hasKeysPermissions)
        toRet=_canKeysPermissions(o,type);
      else 
        toRet=_canKeys(o,type) || _canKeysPermissions(o,type);
      
      return toRet;
    }
    private static boolean _canKeys(boObject o, byte type)
        throws boRuntimeException
    {
        long perf = o.getEboContext().getBoSession().getPerformerBoui();
        long[] performerKeys = securityRights.getPerformerKeys(o, perf);
        bridgeHandler bkey = o.getBridge("KEYS");
        boolean toRet = false;
        //if has no keys then the security assumes that the action can be performed
        if ((bkey != null) && (bkey.getRowCount() != 0) &&
                !securityRights.isSupervisor(o.getEboContext()) && o.getBoDefinition().implementsSecurityRowObjects())
        {
            bkey.beforeFirst();

            while (!toRet && bkey.next())
            {
                long obk = bkey.getCurrentBoui();
                int _bvalue = ClassUtils.convertToInt(bkey.getAttribute(
                            "securityCode").getValueString());                  
                if( _bvalue >= type ) 
                {
                    for (int i = 0; !toRet && (i < performerKeys.length);i++)
                    {
                        if (performerKeys[i] == obk) 
                        {
                          toRet = true;
                        }
                    }
                }
            }
        }
        else
            return true;
      return toRet;
    }

    private static boolean _canKeysPermissions(boObject o, byte type)
        throws boRuntimeException
    {
        long perf = o.getEboContext().getBoSession().getPerformerBoui();
        long[] performerKeys = securityRights.getPerformerKeys(o, perf);
        bridgeHandler bkey = o.getBridge("KEYS_PERMISSIONS");
        boolean toRet = false;
        if (o.getBoDefinition().implementsSecurityRowObjects() && !securityRights.isSupervisor(o.getEboContext()))
        {
          if ((bkey != null) && (bkey.getRowCount() != 0))
          {
              bkey.beforeFirst();
  
              while (!toRet && bkey.next())
              {
                  long obk = bkey.getObject().getBoui();
                  int _bvalue = ClassUtils.convertToInt(bkey.getAttribute(
                              "securityCode").getValueString());                  
                  if( _bvalue >= type ) 
                  {
                      for (int i = 0; !toRet && (i < performerKeys.length);i++)
                      {
                          if (performerKeys[i] == obk) 
                          {
                            toRet = true;
                          }
                      }
                  }
              }
          }
          else return true;
        }                
        else
            return true;
      return toRet;
    }

    public static boolean canRead(boObject o) throws boRuntimeException
    {
        return securityOPL._can(o, securityOPL.READ_KEY);
    }

    public static boolean canDelete(boObject o) throws boRuntimeException
    {
        return securityOPL._can(o, securityOPL.DELETE_KEY);
    }

    public static boolean canWrite(boObject o) throws boRuntimeException
    {
        return securityOPL._can(o, securityOPL.WRITE_KEY);
    }

    public static boolean hasFullControl(boObject o) throws boRuntimeException
    {
        return securityOPL._can(o, securityOPL.FULL_CONTROL);
    }
}
