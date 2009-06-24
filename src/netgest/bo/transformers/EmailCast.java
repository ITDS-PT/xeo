/*Enconding=UTF-8*/
package netgest.bo.transformers;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;
import netgest.utils.DataUtils;
import netgest.bo.runtime.boObject;
import org.apache.log4j.Logger;
/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class EmailCast implements CastInterface
{

    private static Logger logger = Logger.getLogger("netgest.bo.transformers.EmailCast");
    /**
     * 
     * @see 
     */
    public EmailCast()
    {
    }
    
    public void beforeCast(boObject object) throws netgest.bo.runtime.boRuntimeException
    {        
    }
    
    public void afterCastToSr(boObject object, Connection con) throws netgest.bo.runtime.boRuntimeException
    {
        object.getAttribute("media").setValueString("email");
        boObject runtimeObj = object.getAttribute("mail_from").getObject();
        if(runtimeObj != null)
        {
            try
            {
                String email = runtimeObj.getAttribute("email").getValueString();
                BigDecimal boui = null;
                if(email != null && !"".equals(email.trim()))
                {
                    boui = findWorker(object.getEboContext(), email);
                }
                if(boui != null)
                {
                    object.getAttribute("mastersolicitor").setValueObject(boui);
                }
            }
            catch (SQLException e)
            {
                logger.error("", e);
            }
            catch (boRuntimeException e)
            {
                logger.error("", e);
            }
        }
    }
    public void afterCastToReclamacoes(boObject object, Connection con) throws netgest.bo.runtime.boRuntimeException
    {
        long nrdoc = DataUtils.GetSequenceNextVal(object.getEboContext().getApplication(),  con, object.getName());
        object.getAttribute("nrdoc").setValueLong(nrdoc);
        object.getAttribute("media").setValueString("email");
        boObject runtimeObj = object.getAttribute("mail_from").getObject();
        if(runtimeObj != null)
        {
            try
            {
                String email = runtimeObj.getAttribute("email").getValueString();
                BigDecimal boui = null;
                if(email != null && !"".equals(email.trim()))
                {
                    boui = findEntity(object.getEboContext(), email);
                }
                if(boui == null)
                {
                    if(email != null && !"".equals(email.trim()))
                    {
                        boui = createEntity(object.getEboContext(), runtimeObj.getAttribute("email").getValueString(), runtimeObj.getAttribute("name").getValueString(), runtimeObj.getAttribute("lastname").getValueString());
                    }
                    else
                    {
                        boui = createEntity(object.getEboContext(), null, runtimeObj.getAttribute("name").getValueString(), runtimeObj.getAttribute("lastname").getValueString());
                    }
                }
                object.getAttribute("entity").setValueObject(boui);
            }
            catch (SQLException e)
            {
                logger.error("", e);
            }
            catch (boRuntimeException e)
            {
                logger.error("", e);
            }
        }
    }
    
    public void afterCast(boObject object) throws netgest.bo.runtime.boRuntimeException
    {   Connection con = object.getEboContext().getDedicatedConnectionData();
        try{
            if("sr".equals(object.getAttribute("CLASSNAME").getValueString()))
            {
                afterCastToSr(object, con);
            }
            else if("ITED_ResponsibilityTerm".equals(object.getAttribute("CLASSNAME").getValueString())
                    || "ITED_CertConformity".equals(object.getAttribute("CLASSNAME").getValueString()))
            {
                afterCastToITED(object, con);
            }              
            else
            {
                afterCastToReclamacoes(object, con);
            }
            
        }finally
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {
                logger.error("", e);
            }
        }
    }


    private BigDecimal findWorker( EboContext ctx, String email ) throws SQLException
    { 
        email    = email.trim().toUpperCase();
        
        PreparedStatement pstm = null;
        ResultSet         rslt = null;

        long fnd = -1;
        String typ = null;
        
        BigDecimal ret = null;

        try
        {
            pstm = ctx.getConnectionData().prepareStatement("SELECT boui FROM OEbo_Contact WHERE UPPER( EMAIL ) LIKE ? ");
            pstm.setString( 1, email );
            rslt = pstm.executeQuery();
            long nfnd;
            while( rslt.next() )
            {
                nfnd = rslt.getLong(1);

                if( fnd != -1 && fnd != nfnd )
                {
                    fnd = -1;
                    break;
                }
                fnd = nfnd;
            }
            pstm.close();
            rslt.close();
            if( fnd != -1 )
            {
                ret = BigDecimal.valueOf(fnd);
            }
        }
        finally
        {
            if( rslt != null )
            {
                rslt.close();
            }
            if( pstm != null )
            {
                pstm.close();
            }
        }
        
        
        return ret;
    }

    
    private BigDecimal findEntity( EboContext ctx, String email ) throws SQLException
    { 
        email    = email.trim().toUpperCase();
        
        PreparedStatement pstm = null;
        ResultSet         rslt = null;

        String fnd = null;
        String typ = null;
        
        BigDecimal ret = null;

        try
        {
            pstm = ctx.getConnectionData().prepareStatement("SELECT cod_ent,type FROM v_mapentidadesmoradas WHERE UPPER( EMAIL ) LIKE ? ");
            pstm.setString( 1, email );
            rslt = pstm.executeQuery();
            String nfnd;
            while( rslt.next() )
            {
                nfnd = rslt.getString(1);
                
                typ  = rslt.getString(2);
                
                if( fnd != null && !fnd.equals( nfnd ) )
                {
                    fnd = null;
                    break;
                }
                fnd = nfnd;
            }
            pstm.close();
            rslt.close();
            if( fnd != null )
            {
                pstm = ctx.getConnectionData().prepareStatement("SELECT boui FROM ANC_ENTITY WHERE COD_ENT$L=? AND TYPE$L=?");
                pstm.setString( 1, fnd );
                pstm.setString( 2, typ ); 
                rslt = pstm.executeQuery();
                if( rslt.next() )
                {
                    ret = rslt.getBigDecimal( 1 );
                }
            }
        }
        finally
        {
            if( rslt != null )
            {
                rslt.close();
            }
            if( pstm != null )
            {
                pstm.close();
            }
        }
        
        
        return ret;
    }
    
    private BigDecimal createEntity(EboContext ctx, String email, String name, String lastName) throws boRuntimeException
    {
        boObject entityobj = boObject.getBoManager().createObject( ctx, "ANC_entity" );
        if((name != null && name.length() > 0) || (lastName != null && lastName.length() > 0))
        {
            String _name = "";
            if(name != null) _name = name.trim();
            if(lastName != null) _name += " " + lastName.trim();
            entityobj.getAttribute( "name" ).setValueString( name );
        }
        else
        {
            entityobj.getAttribute( "name" ).setValueString( email );
        }
        boObject address = boObject.getBoManager().createObject( ctx, "address" );
        if(email != null)
        {
            address.getAttribute( "email" ).setValueString( email );
            entityobj.getAttribute( "email" ).setValueString( email );            
        }
        entityobj.getAttribute( "correspondencia_address" ).setValueLong( address.getBoui() );
        //id que vai ser sincronizado pelo SyncEntidades
        entityobj.getAttribute( "id" ).setValueString( "0" );
        return BigDecimal.valueOf( entityobj.getBoui() );
    }
    
    public boolean isToRefresh(boObject obj) throws netgest.bo.runtime.boRuntimeException
    {
        if("email".equals(obj.getAttribute("CLASSNAME").getValueString()))
        {
            return false;
        }
        return true;
    }

    public void afterCastToITED(boObject object, Connection con) throws netgest.bo.runtime.boRuntimeException
    {
        long nrdoc = DataUtils.GetSequenceNextVal(object.getEboContext().getApplication(),  con, object.getName());
        object.getAttribute("nrdoc").setValueLong(nrdoc);
        object.getAttribute("media").setValueString("email");
    }    
}