/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.math.BigDecimal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import netgest.bo.data.DataRow;
import netgest.bo.data.DataSet;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.system.boApplication;
import netgest.bo.system.boRepository;

import netgest.utils.ClassUtils;

/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 */
public final class boReferencesManager
{
    public static final byte ARRAY_TO_ADD = 0;
    public static final byte ARRAY_TO_REMOVE = 1;


    public static final void updateReferences( boObject obj )
    {
        updateReferences( obj, false );
    }


    /**
    * Update the object references, in the table EBO_REFERENCES.
    * @param boctx the current EboContext
    * @param obj the boObject to be updated
    */
    public static final void updateReferences(boObject obj, boolean rebuild )
    {
        ArrayList[] refs = buildArraLists( obj, rebuild );
        if( refs != null )
        {
            daoUpdateReferences( rebuild , obj, refs[ ARRAY_TO_ADD ], refs[ ARRAY_TO_REMOVE ] );
        }
    }

    public static final ArrayList[] getReferencesList( boObject obj )
    {
        return buildArraLists( obj, false );
    }

    public static final void updateReferencesLists( boObject obj, ArrayList addbouis, ArrayList rembouis )
    {
        daoUpdateReferences( false, obj, addbouis, rembouis );
    }

    private static final ArrayList[] buildArraLists( boObject obj, boolean rebuild )
    {
        ArrayList[] ret = null;
        if( !obj.getName().equals("Ebo_TextIndex") && !obj.getName().equals("Ebo_Registry") )
        {
            ArrayList addvect = new ArrayList();
            ArrayList remvect = new ArrayList();
            Enumeration oEnum = obj.getAttributes().elements();
            BigDecimal refboui;
            while(oEnum.hasMoreElements())
            {
                AttributeHandler att = (AttributeHandler)oEnum.nextElement();
                if( att.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && !(att instanceof boBridgeMasterAttribute) )
                {
                    if( att.getDefAttribute().getDbIsBinding() )
                    {
                        if( att.getDefAttribute().getDbIsTabled())
                        {
                            DataSet dataSet = obj.getDataRow().getChildDataSet( obj.getEboContext(), att.getName() );
                            verifyRowChanges( rebuild , addvect, remvect, dataSet, att.getName(), att.getDefAttribute().getDbName(), att.getDefAttribute(), false );
                        }
                        else
                        {
                            verifyRowChanges( rebuild , addvect, remvect, obj.getDataSet(), att.getName() , att.getDefAttribute().getDbName(), att.getDefAttribute(), false );
                        }
                    }
                }
            }
            boDefAttribute[] allatts = obj.getBoDefinition().getAttributesDef();
            for (int i = 0; i < allatts.length; i++)
            {
                if( allatts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && allatts[i].getMaxOccurs() > 1 && !allatts[i].getDbIsTabled() )
                {
                	bridgeHandler bridge;
                	if( !rebuild || obj.getEboContext().isInModeBatch() ) {
                		bridge = obj.getBridges().get( allatts[i].getName() );
                	}
                	else {
                		bridge = obj.getBridge( allatts[i].getName() );
                	}
                    if( bridge != null )
                    {
                        verifyRowChanges( rebuild, addvect , remvect , bridge.getRslt().getDataSet() , bridge.getName() + "." + allatts[i].getName() , allatts[i].getBridge().getChildFieldName(), bridge.getDefAttribute(), true );

                        boDefAttribute[] bridgeatts = bridge.getDefAttribute().getBridge().getBoAttributes();
                        for (int z = 0; z < bridgeatts.length ; z++)
                        {
                            if( bridgeatts[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                            {
                                verifyRowChanges(rebuild, addvect , remvect , bridge.getRslt().getDataSet() , bridge.getName() + "." + bridgeatts[z].getName() , bridgeatts[z].getDbName(), bridgeatts[z], true );
                            }
                        }
                    }
                }
            }

            ret = new ArrayList[2];
            ret[ ARRAY_TO_ADD    ] = addvect;
            ret[ ARRAY_TO_REMOVE ] = remvect;

        }
        return ret;
    }


    private static void verifyRowChanges( boolean rebuild, ArrayList vectadd, ArrayList vectrem, DataSet dataSet, String att,String dbName, boDefAttribute attdef, boolean isBridge )
    {
        for ( int i = 0; i < dataSet.getRowCount() ; i++ )
        {
            DataRow row = dataSet.rows( i + 1 );
            BigDecimal newvalue = row.getBigDecimal( dbName );
            BigDecimal oldvalue = null;


            String xattname = att;

            if( rebuild )
            {
                if( newvalue != null && newvalue.longValue() > 0 )
                {
                    vectadd.add( new referenceHandler( ( isBridge?xattname+"."+row.getLong("LIN"):xattname )  , newvalue, attdef ) );
                }
            }
            else
            {
                if( !row.isNew() && row.getFlashBackRow() != null  )
                {
                    oldvalue = row.getFlashBackRow().getBigDecimal( dbName );
                    if(
                        !ClassUtils.compare( newvalue, oldvalue )
                        ||
                        (isBridge?!row.getBigDecimal("LIN").equals( row.getFlashBackRow().getBigDecimal("LIN") ):false)
                      )
                    {
                        if( oldvalue != null )
                        {
                            vectrem.add( new referenceHandler( ( isBridge?xattname+"."+row.getFlashBackRow().getLong("LIN"):xattname ) , oldvalue, attdef  ) );
                        }
                        if ( newvalue != null )
                        {
                            if( newvalue.longValue() > 0 )
                            {
                                vectadd.add( new referenceHandler( ( isBridge?xattname+"."+row.getLong("LIN"):xattname ) , newvalue, attdef ) );
                            }
                        }
                    }
                }
                else if ( row.isNew() )
                {
                    if ( newvalue != null && newvalue.longValue() > 0 )
                    {
                        vectadd.add( new referenceHandler(  ( isBridge?xattname+"."+row.getLong("LIN"):xattname ) , newvalue, attdef ) );
                    }
                }
            }
        }

        if( !rebuild )
        {
            for (int i = 0; i < dataSet.getDeletedRowsCount() ; i++)
            {
                DataRow row = dataSet.deletedRows( i + 1 );
                BigDecimal oldvalue = row.getBigDecimal( dbName );
                String xattname = ( isBridge?att+"."+row.getLong("LIN"):att );

                if( row.getFlashBackRow() != null )
                {
                    oldvalue = row.getFlashBackRow().getBigDecimal( dbName );
                    xattname = ( isBridge?att+"."+row.getFlashBackRow().getLong("LIN"):att );
                }
                if( oldvalue != null )
                {
                    vectrem.add( new referenceHandler( xattname , oldvalue, attdef ) );
                }
            }
        }
    }



    /**
     * Remove all references of the object
     * @param obj object to be remove from references
     */
    public static final void removeReferences(boObject obj)
    {
        CallableStatement cstm=null;
        Connection cn=null;
        try
        {
            long boui = obj.getBoui();
            cn = obj.getEboContext().getConnectionData();

            cstm = cn.prepareCall(getDaoSqlDeleteAllRefs(obj.getEboContext().getApplication()));
            cstm.setLong( 1 , boui );
            cstm.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new boRuntimeException2("Erro em "+boReferencesManager.class.getName()+".daoUpdateReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
        }
        finally
        {
            try
            {
                if(cstm!=null) cstm.close();
            }
            catch (Exception e)
            {
                throw new boRuntimeException2("Execption cstm.close() Erro em "+boReferencesManager.class.getName()+".removeReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
            }
        }

    }

    private static final String getDaoSqlDeleteAllRefs(boApplication app)
    {
        StringBuffer toRet = new StringBuffer("DELETE FROM ");
        String defaultSchema = boRepository.getRepository(app, "default").getSchemaName();
        if(defaultSchema != null && !"".equals(defaultSchema))
        {
            toRet.append(defaultSchema).append(".");
        }
        toRet.append("EBO_REFERENCES WHERE BOUI=?");
        return toRet.toString();
    }
    /**
     * Remove one reference of the object
     * @param obj object to be remove from references
     */
    public static final void removeReference(EboContext ctx, referenceHandler reference)
    {
        CallableStatement cstm=null;
        Connection cn=null;
        try
        {
            cn = ctx.getConnectionData();
            cstm = cn.prepareCall(getDaoSqlDeleteForOneRef(ctx.getApplication()));
            cstm.setObject( 1 , reference.object_boui );
            cstm.setObject( 2 , reference.object_refBoui );
            cstm.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new boRuntimeException2("Erro em "+boReferencesManager.class.getName()+".daoUpdateReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
        }
        finally
        {
            try
            {
                if(cstm!=null) cstm.close();
            }
            catch (Exception e)
            {
                throw new boRuntimeException2("Execption cstm.close() Erro em "+boReferencesManager.class.getName()+".removeReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
            }
        }
    }
    private static final String getDaoSqlDeleteForOneRef(boApplication app)
    {
        StringBuffer toRet = new StringBuffer("DELETE FROM ");
        String defaultSchema = boRepository.getRepository(app, "default").getSchemaName();
        if(defaultSchema != null && !"".equals(defaultSchema))
        {
            toRet.append(defaultSchema).append(".");
        }
        toRet.append("EBO_REFERENCES WHERE BOUI = ? AND  REFBOUI$ = ?");
        return toRet.toString();
    }
    private static final String getDaoSqlInsertStr(boApplication app)
    {
        StringBuffer toRet = new StringBuffer("INSERT INTO ");
        String defaultSchema = boRepository.getRepository(app, "default").getSchemaName();
        if(defaultSchema != null && !"".equals(defaultSchema))
        {
            toRet.append(defaultSchema).append(".");
        }
        toRet.append("EBO_REFERENCES (BOUI,REFBOUI$,ATTRIBUTE) VALUES (?,?,?)");
        return toRet.toString();
    }

    private static final String getDaoSqlDeleteRefs(boApplication app)
    {
        StringBuffer toRet = new StringBuffer("DELETE FROM ");
        String defaultSchema = boRepository.getRepository(app, "default").getSchemaName();
        if(defaultSchema != null && !"".equals(defaultSchema))
        {
            toRet.append(defaultSchema).append(".");
        }
        toRet.append("EBO_REFERENCES WHERE BOUI=? AND REFBOUI$=? AND ATTRIBUTE=?");
        return toRet.toString();
    }
/*
    private static final String DAO_SQL_DELETEALLREFS = "DELETE FROM EBO_REFERENCES WHERE BOUI=?";
    private static final String DAO_SQL_INSERTSTR  = "INSERT INTO EBO_REFERENCES (BOUI,REFBOUI$,ATTRIBUTE) VALUES (?,?,?)";
    private static final String DAO_SQL_DELETEREFS = "DELETE FROM EBO_REFERENCES WHERE BOUI=? AND REFBOUI$=? AND ATTRIBUTE=?";
*/

    private static final void daoUpdateReferences( boolean rebuild, boObject obj, ArrayList addbouis, ArrayList rembouis )
    {
        if( rebuild )
        {
            removeReferences( obj );
        }

        CallableStatement cstm=null;
        Connection cn=null;
        try
        {
            long boui = obj.getBoui();
            cn = obj.getEboContext().getConnectionData();

            if( rembouis != null && rembouis.size() > 0 )
            {
                cstm = cn.prepareCall(getDaoSqlDeleteRefs(obj.getEboContext().getApplication()));
                Iterator enumrem = rembouis.iterator();
                while( enumrem.hasNext() )
                {
                    referenceHandler att = ( referenceHandler )enumrem.next();

                    cstm.setLong( 1 , boui );
                    cstm.setObject( 2,  att.attributeValue );
                    cstm.setObject( 3 , att.attributeName );
                    cstm.addBatch();
                }
                cstm.executeBatch();
                cstm.close();
            }

            if( addbouis != null && addbouis.size() > 0 )
            {
                Iterator enumadd = addbouis.iterator();
                cstm = cn.prepareCall(getDaoSqlInsertStr(obj.getEboContext().getApplication()));
                while( enumadd.hasNext() )
                {
                    referenceHandler att = (referenceHandler)enumadd.next();
                    cstm.setLong( 1 , boui );
                    cstm.setObject( 2 , att.attributeValue );
                    cstm.setObject( 3 , att.attributeName );
                    cstm.addBatch( );
                }
                cstm.executeBatch();
                cstm.close();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException( e );
        }
        finally
        {
            try
            {
                if(cstm!=null) cstm.close();
            }
            catch (Exception e)
            {
                 throw new boRuntimeException2("Execption cstm.close() Erro em "+boReferencesManager.class.getName()+".daoUpdateReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
            }
//            try
//            {
//                if(cn!=null) cn.close();
//            }
//            catch (Exception e)
//            {
//                throw new boRuntimeException2("Execption cn.close() Erro em "+boReferencesManager.class.getName()+".daoUpdateReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
//            }
        }
    }


    private static final byte TYPE_REFERENCES=0;
    private static final byte TYPE_REFERENCEDBY=1;


    private static final String DAO_SQL_SELECTREFERENCES = "SELECT REFBOUI$, ATTRIBUTE FROM OEBO_REFERENCES WHERE BOUI=?";
    private static final String DAO_SQL_SELECTREFERENCEDBY = "SELECT BOUI, ATTRIBUTE FROM OEBO_REFERENCES WHERE REFBOUI$=?";


    /**
     * Get the objects referenced by certain object
     * @param ctx Actual EvoContext
     * @param boui Boui of the object
     * @return Array with the <i>BOUIS</i> of the objects referenced by the object passed as the second argument.
     */
    public static final referenceHandler[] getReference(EboContext ctx,long boui)
    {
        return getReferences(ctx,boui,TYPE_REFERENCES);
    }

    /**
     * Get the objects referenced by certain object
     * @param obj the Object to be updated
     * @return Array with the <i>BOUIS</i> of the objects referenced by the object passed as the second argument.
     */
    public static final referenceHandler[] getReference(boObject obj)
    {
        return getReferences(obj.getEboContext(),obj.getBoui(),TYPE_REFERENCES);
    }
    public static final referenceHandler[] getReferences(EboContext ctx,long boui,byte type)
    {
        Connection cn = null;
        PreparedStatement pstm = null;
        ResultSet rslt = null;
        ArrayList list = new ArrayList();

        try
        {
            cn = ctx.getConnectionData();
            if(type==TYPE_REFERENCES)
                pstm = cn.prepareStatement(DAO_SQL_SELECTREFERENCES,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            else if (type == TYPE_REFERENCEDBY)
                pstm = cn.prepareStatement(DAO_SQL_SELECTREFERENCEDBY,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);

            pstm.setLong(1,boui);
            rslt = pstm.executeQuery();
            while(rslt.next())
            {
                list.add( new referenceHandler( rslt.getString(2), rslt.getBigDecimal(1), null ) );
            }
        }
        catch (SQLException e)
        {
            throw new boRuntimeException2("Erro em "+boReferencesManager.class.getName()+".getReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
        }
        finally
        {
            try
            {
                if(rslt!=null) rslt.close();
            }
            catch (Exception e)
            {
                throw new boRuntimeException2("Erro rslt.close() em "+boReferencesManager.class.getName()+".getReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
            }
            try
            {
                if(pstm!=null) pstm.close();
            }
            catch (Exception e)
            {
                throw new boRuntimeException2("Erro pstm.close() em "+boReferencesManager.class.getName()+".getReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
            }
//            try
//            {
//                if(cn!=null) cn.close();
//            }
//            catch (Exception e)
//            {
//                throw new boRuntimeException2("Erro cn.close() em "+boReferencesManager.class.getName()+".getReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
//            }
        }
        referenceHandler[] ret = new referenceHandler[list.size()];
        list.toArray( ret );
//        for (short i = 0; i < ret.length; i++)
//        {
//            ret[i]=((BigDecimal)list.get(i)).longValue();
//        }
        return ret;
    }

    private static final boObject[] getObjectsFromBoui(boObject obj,referenceHandler[] refs)
    {
        try
        {
            boObject[] ret = new boObject[refs.length];
            for (int i = 0; i < refs.length; i++)
            {
                ret[i] = obj.getObject( refs[i].attributeValue.longValue() );
            }
            return ret;
        }
        catch (boRuntimeException e)
        {
            throw new boRuntimeException2("Erro em "+boReferencesManager.class.getName()+".geObjectsFromBoui() \n"+e.getClass().getName()+"\n"+e.getMessage());
        }
    }

    /**
     * Get the objects referenced by certain object
     * @param boctx the current EboContext
     * @param obj the instance of the Object to lookup references
     * @return Array with the <i>boObjects</i> that are referenced by the object passed as the second argument.
     */
    public static final boObject[] getReferenceObjects(boObject obj)
    {
        return getObjectsFromBoui(obj,getReference(obj));
    }


    /**
     * Get the objects who reference this instance of the object
     * @param ctx Current EboContext
     * @param boui of the Object to lookup references
     * @return Array with the <i>BOUIS</i> who reference the instance passed.
     */
    public static final referenceHandler[] getReferencedBy(EboContext ctx,long boui)
    {
        return getReferences(ctx,boui,TYPE_REFERENCES);
    }

    /**
     * Get the objects who reference this instance of the object
     * @param boctx the current EboContext
     * @param obj the instance of the Object to lookup references
     * @return Array with the <i>BOUIS</i> who reference the instance passed.
     */
    public static final referenceHandler[] getReferencedBy(boObject obj)
    {
        return getReferences(obj.getEboContext(),obj.getBoui(),TYPE_REFERENCEDBY);
    }
    /**
     * Get the objects who reference this instance of the object
     * @param boctx the current EboContext
     * @param obj the instance of the Object to lookup references
     * @return Array with the <i>boObjects</i> who reference the instance passed.
     */
    public static final boObject[] getReferencedByObjects(boObject obj)
    {
        try
        {
            return getObjectsFromBoui(obj,getReferences(obj.getEboContext(),obj.getBoui(),TYPE_REFERENCEDBY));
        }
        catch (Exception e)
        {
            throw new boRuntimeException2("Erro em "+boReferencesManager.class.getName()+".getReferencedByObjects\n"+e.getClass().getName()+"\n"+e.getMessage());
        }
    }

    public static final void rebuilReferences( EboContext ctx, long boui ) throws boRuntimeException
    {
        boObject object = boObject.getBoManager().loadObject( ctx, boui );
        updateReferences( object, true );
    }


    /**
     * Get the objects referenced by certain object
     * @param obj the Object to be updated
     * @return Array with the <i>BOUIS</i> of the objects referenced by the object passed as the second argument.
     */
    public static final String SELECTREFERENCES = "SELECT REFBOUI$,ATTRIBUTE,BOUI FROM OEBO_REFERENCES WHERE BOUI=?";
    public static final String SELECTREFERENCEDBY = "SELECT REFBOUI$,ATTRIBUTE,BOUI FROM OEBO_REFERENCES WHERE REFBOUI$=?";

//não utilizei a getRefrences por eventuais problemas de compatibilidade
    public static final referenceHandler[] getObjectReferences(EboContext ctx,long boui,byte type) throws boRuntimeException
    {
        Connection cn = null;
        PreparedStatement pstm = null;
        ResultSet rslt = null;
        ArrayList list = new ArrayList();

        try
        {
            cn = ctx.getConnectionData();
            if(type==TYPE_REFERENCES)
            {
                pstm = cn.prepareStatement( SELECTREFERENCES ,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            }
            else if (type == TYPE_REFERENCEDBY)
            {
                pstm = cn.prepareStatement( SELECTREFERENCEDBY ,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            }

            pstm.setLong(1,boui );
            rslt = pstm.executeQuery();
            String objectName = "";

            boDefHandler bodef = null;
            while(rslt.next())
            {
                String attributeX = rslt.getString(2);
                String attributeName = "";
                boDefAttribute atrdef=null;
                if ( attributeX.indexOf('.') > 1 )
                {
                    String[] atrsname = attributeX.split("\\.");
                    attributeName = atrsname[0];
                }
                else
                {
                    attributeName = attributeX;
                }
                if ( type == TYPE_REFERENCES )
                {
                    objectName = boObject.getBoManager().getClassNameFromBOUI( ctx , rslt.getBigDecimal(3).longValue() );
                    bodef = boDefHandler.getBoDefinition( objectName );
                }
                else
                {
                    objectName = boObject.getBoManager().getClassNameFromBOUI( ctx , rslt.getBigDecimal(3).longValue() );
                    bodef = boDefHandler.getBoDefinition( objectName );

                }
                if(bodef != null)
                {
                    atrdef = bodef.getAttributeRef( attributeName );
                    list.add( new referenceHandler( attributeX , rslt.getBigDecimal(1), rslt.getBigDecimal(3), atrdef ) );
                }
            }
        }
        catch (SQLException e)
        {
            throw new boRuntimeException2("Erro em "+boReferencesManager.class.getName()+".getReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
        }
        finally
        {
            try
            {
                if(rslt!=null) rslt.close();
            }
            catch (Exception e)
            {
                throw new boRuntimeException2("Erro rslt.close() em "+boReferencesManager.class.getName()+".getReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
            }
            try
            {
                if(pstm!=null) pstm.close();
            }
            catch (Exception e)
            {
                throw new boRuntimeException2("Erro pstm.close() em "+boReferencesManager.class.getName()+".getReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
            }
//            try
//            {
//                if(cn!=null) cn.close();
//            }
//            catch (Exception e)
//            {
//                throw new boRuntimeException2("Erro cn.close() em "+boReferencesManager.class.getName()+".getReferences\n"+e.getClass().getName()+"\n"+e.getMessage());
//            }
        }
        referenceHandler[] ret = new referenceHandler[list.size()];
        list.toArray( ret );
//        for (short i = 0; i < ret.length; i++)
//        {
//            ret[i]=((BigDecimal)list.get(i)).longValue();
//        }
        return ret;
    }

    public static class referenceHandler
    {

        public  referenceHandler(String attributeName,BigDecimal attributeValue, boDefAttribute defatt )
        {
            this.attributeName  = attributeName;
            this.attributeValue = attributeValue;
            this.attributeDef   = defatt;
            this.object_refBoui= attributeValue;
            this.object_boui = null;
        }
        public referenceHandler(String attributeName,BigDecimal attributeValue, BigDecimal boui ,boDefAttribute defatt )
        {
            this.attributeName  = attributeName;
            this.attributeValue = attributeValue;
            this.attributeDef   = defatt;
            this.object_boui=boui;
            this.object_refBoui= attributeValue;
        }
        public int hashCode()
        {
            return this.attributeValue.hashCode();
        }

        public boolean equals( Object  object )
        {
            if ( object instanceof referenceHandler )
            {
                return ((referenceHandler)object).attributeValue.equals( this.attributeValue );
            }
            return false;
        }


        public String           attributeName;
        public BigDecimal       attributeValue;
        public BigDecimal       object_refBoui;
        public BigDecimal       object_boui;
        public boDefAttribute   attributeDef;
    }

}