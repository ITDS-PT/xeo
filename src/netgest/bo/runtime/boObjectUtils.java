/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import netgest.bo.data.DataRow;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.utils.StringUtils;

import netgest.utils.DataUtils;
import netgest.utils.HexUtils;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boObjectUtils 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
     
     public static final byte getAttributeInputType( DataRow row, String attName ) 
     {
          int i = getAttributeInputTypeIndex( row, attName );
          if( i > 0)
          {
              byte[] attinptypes = row.getBytes( "SYS_FLDINPUTTYPE" );
              return attinptypes[i+4]; 
          }
          return AttributeHandler.INPUT_FROM_UNITIALIZED;
     }
    
     private static final int getAttributeInputTypeIndex( DataRow row, String attName )
     {
         byte[] attinptypes = null;
        
         try{attinptypes = row.getBytes( "SYS_FLDINPUTTYPE" );}catch (Exception e){/*não existe a coluna SYS_FLDINPUTTYPE*/}
         
         if( attinptypes != null )
         {
             int hashValue = attName.hashCode();
             for (int i = 0; i < attinptypes.length; i+=5) 
             {
                  byte[] cdef = new byte[4];
                  System.arraycopy( attinptypes, i,cdef, 0, 4 );
                  int chashcode = HexUtils.intFromByteArray( cdef ); 
                  if( chashcode == hashValue )
                  {
                      return i;
                  }
             }
         }
         return -1;
     }

     public static final void updateAttributeInputType(DataRow row,  Enumeration attributes, int size )
     {
         updateAttributeInputType( row,  attributes, size, false );
     }
     public static final void updateAttributeInputType(DataRow row,  Enumeration attributes, int size, boolean fromBridge )
     {
     
        if (row.getDataSet().findColumn( "SYS_FLDINPUTTYPE") > 0 )
        {
        ByteArrayOutputStream   bout = new ByteArrayOutputStream( size * 5 / 2 );
        //byte[] array = new byte[ size * 5 ];
//        short startIndex = 0;
        try 
        {
            while( attributes.hasMoreElements() )
            {
                AttributeHandler att = (AttributeHandler)attributes.nextElement();
                int hashCode = att.getName().hashCode();
                if ( fromBridge || !att.isBridge() )
                {
                    byte cstate = att.getInputType();
                    if( cstate != AttributeHandler.INPUT_FROM_UNITIALIZED )
                    {
                        String xx = att.getName();
                        byte[] adef = HexUtils.intToByteArray( hashCode );
                        //System.arraycopy( adef, 0, array, 0, 4 );
                        //array[ startIndex+4 ] = cstate;
                        //startIndex += 5;
                        bout.write( adef );
                        bout.write( cstate );
                    }
                }
            }
        }
        catch (IOException e)
        {
            e=e;
        }
            row.updateBytes( "SYS_FLDINPUTTYPE" , bout.toByteArray() );
        }
     }
     
     public static final void updateSequenceAttributes(boObject object,  Enumeration attributes, boolean bridgeAtt ) throws boRuntimeException
     {
        try
        {
            while( attributes.hasMoreElements() )
            {
                AttributeHandler att = (AttributeHandler)attributes.nextElement();
                boIBridgeAttribute batt = null;
                bridgeHandler bridge = null;
                if( bridgeAtt )
                {
                    batt = (boIBridgeAttribute)att;
                    bridge = batt.getBridge();
                }
                
                if( att.getDefAttribute().getType().toUpperCase().startsWith("SEQUENCE") )
                {
                    StringBuffer key = new StringBuffer( att.getDefAttribute().getBoDefHandler().getName() );
                    String[] keys = att.getDefAttribute().getTypeArguments();
                    for (int i = 0; i < keys.length; i++) 
                    {
                        if( bridgeAtt )
                        {
                            if( keys[i].toUpperCase().startsWith("PARENT") )
                            {
                                String[] x = netgest.utils.StringUtils.parseArguments( keys[i] );
                                key.append('[').append( att.getParent().getAttribute(x[0]) ).append(']');
                            }
                            else
                            {
                                if( !netgest.utils.StringUtils.isLiteral( keys[i] ) )
                                {
                                    key.append('[').append( bridge.getAttribute( keys[i]) ).append(']');
                                }
                                else
                                {
                                    key.append('[').append( netgest.utils.StringUtils.removeQuotes( keys[i] )  ).append(']');
                                }
                            }
                        }
                        else
                        {
                            if( !netgest.utils.StringUtils.isLiteral( keys[i] ) )
                            {
                                key.append('[').append( att.getParent().getAttribute(keys[i]) ).append(']');
                            }
                            else
                            {
                                key.append('[').append( netgest.utils.StringUtils.removeQuotes( keys[i] )  ).append(']');
                            }
                        }
                    }
                    
                    boolean setValue;
                    
                    if ( att.getInputType() == AttributeHandler.INPUT_FROM_INTERNAL || att.getInputType() == AttributeHandler.INPUT_FROM_UNITIALIZED)
                    {
                        if( bridgeAtt )
                        {
                            if( bridge.getRslt().getDataSet().rows( bridge.getRow() ).isNew() )
                            {
                                att.setValueLong( DataUtils.GetSequenceNextVal( object.getEboContext().getApplication(), object.getEboContext().getConnectionData(), key.toString() ) );
                            }
                        }
                        else 
                        {
                            if( !att.getParent().exists() )
                            {
                                att.setValueLong( DataUtils.GetSequenceNextVal( object.getEboContext().getApplication(), object.getEboContext().getConnectionData(), key.toString() ) );
                            }
                        }
                    }
                    if ( att.getValueObject() != null && att.getInputType() == AttributeHandler.INPUT_FROM_USER )
                    {
                        DataUtils.updateSequenceNextVal( object.getEboContext().getApplication(), object.getEboContext().getConnectionData(), key.toString(), att.getValueLong() ); 
                    }
                    
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
     }
     public static final String initializeSequenceField( AttributeHandler att ) throws boRuntimeException 
     {
        if( !att.getParent().exists() && (att.getInputType() != AttributeHandler.INPUT_FROM_USER))
        {
            StringBuffer key = new StringBuffer( att.getDefAttribute().getBoDefHandler().getName() );
            String[] keys = att.getDefAttribute().getTypeArguments();
            for (int i = 0; i < keys.length; i++) 
            {
                if( !netgest.utils.StringUtils.isLiteral( keys[i] ) )
                {
                    key.append('[').append( att.getParent().getAttribute(keys[i]) ).append(']');
                }
                else
                {
                    key.append('[').append( netgest.utils.StringUtils.removeQuotes( keys[i] )  ).append(']');
                }
            }
            try
            {
                return ""+DataUtils.checkSequenceNextVal( att.getParent().getEboContext().getApplication(), att.getParent().getEboContext().getConnectionData() , key.toString()  );
            }
            catch (Exception e)
            {
                throw new RuntimeException( e.getMessage() );
            }
        }
        else
        {
            return att.getValueString();
        }
     }
     
    public static String getLovDescription(EboContext ctx,String lovname, String value) 
    {
      try
      {
          lovObject lov= LovManager.getLovObject(ctx,lovname);
          if( lov != null)
          {
              lov.beforeFirst();
              while(lov.next())
              {
                String code = lov.getCode();
                if( code == null && value == null )
                {
                    return lov.getDescription();
                }
                else if ( code != null && code.equals( value ) )
                {
                    return lov.getDescription();
                }
              }
          }
          return value;
          
      }
      catch (boRuntimeException e)
      {
          return value;
      }
    }     
}