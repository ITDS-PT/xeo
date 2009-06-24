/*Enconding=UTF-8*/
package netgest.bo.impl;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import netgest.bo.def.*;
import netgest.bo.runtime.*;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.templates.*;
import netgest.utils.*;
import oracle.xml.parser.v2.XMLNode;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import netgest.bo.impl.templates.boExpressionEval;

/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 */
public abstract class Ebo_TemplateImpl extends boObject
{

    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.impl.Ebo_TemplateImpl");

    public boObject processTemplate(boObject parent,boObject toObject) throws boRuntimeException
    {
        return _processTemplate(parent,toObject,true);
    }


    public boObject loadTemplate() throws boRuntimeException
    {
        return loadTemplate(null,null);
    }
    public boObject loadTemplate( boObject toObject ) throws boRuntimeException
    {
        return loadTemplate( null, toObject );
    }
    public boObject loadTemplate(boObject parent, boObject toObject) throws boRuntimeException
    {

        boObject relBo =_processTemplate(null,toObject,false);


        boObject tmpl=this;

        boolean ch=relBo.isChanged();
        //copy DAO && extendAttribute
        bridgeHandler bridgeExtAttr = tmpl.getBridge("extendAttribute");
        bridgeHandler bridgeDAO = tmpl.getBridge("DAO");


         // Atenção o tmpl_bridgeRO refere-se ao objecto e não ao template
         // Atenção o tmpl_bridgeDAO refere-se ao objecto e não ao template

        bridgeHandler tmpl_bridgeExtAttr  = relBo.getBridge("extendAttribute");
        bridgeHandler tmpl_bridgeDAO = relBo.getBridge("DAO");

        if ( bridgeExtAttr != null && !bridgeExtAttr.isEmpty() )
        {
            if( relBo.getMode() != boObject.MODE_EDIT_TEMPLATE )
            {
                cloneExtendAttributes(tmpl,relBo);
            }
            else
            {
                bridgeExtAttr.beforeFirst();
                while ( bridgeExtAttr.next() )
                {
                    Enumeration xatb = bridgeExtAttr.getLineAttributes().elements();
                    if ( !tmpl_bridgeExtAttr.haveBoui( bridgeExtAttr.getValueLong() ) )
                    {
                        tmpl_bridgeExtAttr.add( bridgeExtAttr.getValueLong() );
                    }

    //                tmpl_bridgeRO.edit();

                    while ( xatb.hasMoreElements() )
                    {
                        AttributeHandler xat = ( AttributeHandler ) xatb.nextElement() ;
                        tmpl_bridgeExtAttr.getAttribute( xat.getDefAttribute().getName() ).setValueObject( xat.getValueObject(), AttributeHandler.INPUT_FROM_TEMPLATE );
                    }
                }
            }
        }

        if ( bridgeDAO != null && !bridgeDAO.isEmpty() )
        {
            bridgeDAO.beforeFirst();
            while ( bridgeDAO.next() )
            {
            try
               {
                    Enumeration xatb = bridgeDAO.getLineAttributes().elements();
                    //int pos=tmpl_bridgeDAO.getRow();

                    if ( !tmpl_bridgeDAO.haveBoui( bridgeDAO.getValueLong() ) )
                    {

                            boObject obdao = bridgeDAO.getObject();
                            tmpl_bridgeDAO.add( obdao.getBoui() );


                    }

    //                tmpl_bridgeDAO.edit();

                    while ( xatb.hasMoreElements() )
                    {
                        AttributeHandler xat_template = ( AttributeHandler ) xatb.nextElement() ;
                        AttributeHandler xat_target = ( AttributeHandler ) tmpl_bridgeDAO.getAttribute( xat_template.getDefAttribute().getName() );

                        if(relBo.getMode() != boObject.MODE_EDIT_TEMPLATE)
                        {
                            if( xat_template.getDefAttribute().getName().equalsIgnoreCase( "config" ) && xat_template.getValueObject()!=null )
                            {
                                boObject fromObj = xat_template.getObject();
                               // if ( !relBo.exists() && fromObj.exists() )
//                                  {
                                    boObject objTo = boObject.getBoManager().createObject( fromObj.getEboContext(), fromObj );
                                 //   cloneConfigAttributes( fromObj , objTo );
                                 // o clone do config já faz este trabalho
                                    xat_target.setValueLong( objTo.getBoui(),AttributeHandler.INPUT_FROM_TEMPLATE );

                                  //  (( ObjAttHandler )xat_target).edit();
//                                }
                            }
                            else if( xat_template.getDefAttribute().getName().equalsIgnoreCase( "DAO" ) )
                            {
                                //ignore
                            }
                            else
                            {
                                xat_target.setValueObject( xat_template.getValueObject(),AttributeHandler.INPUT_FROM_TEMPLATE );
                            }
                        }
                        else
                        {
                            xat_target.setValueObject( xat_template.getValueObject(), AttributeHandler.INPUT_FROM_TEMPLATE );
                        }
                    }

                }
                catch( boRuntimeException e)
                {
                    //oobjecto já não existe

                    bridgeDAO.remove();
                }
            }
        }
        if( relBo.getMode() != boObject.MODE_EDIT_TEMPLATE )
        {
            _processTemplate( parent , relBo ,true);
        }
        if( !relBo.exists() )  relBo.setChanged( false );

        return relBo;
    }

    private boObject _processTemplate(boObject parent,boObject toObject,boolean processexpr) throws boRuntimeException
    {
        boObject targetobj;
        if(toObject == null)
        {
            targetobj = boObject.getBoManager().createObject(
                                        getEboContext() ,
                                        getAttribute( "masterObjectClass" ).getValueLong()
                            );
        }
        else
        {
            targetobj = toObject;
        }
//      if( parent != null ) targetobj.getAttribute("PARENT").setValueLong(parent.getBoui());
        targetobj.getAttribute("TEMPLATE").setValueLong(this.getBoui());

        Hashtable expresatts = new Hashtable();
        Hashtable fixValues  = new Hashtable();

        processMapAttributes(targetobj,expresatts,fixValues,processexpr);

        if( processexpr )
        {
            // Process DAO
            {
                bridgeHandler obj_dao = targetobj.getBridge("DAO");
                //bridgeHandler tmp_dao = this.getBridge("DAO");
                if (obj_dao!=null)
                {
                  obj_dao.beforeFirst();
                  while( obj_dao.next() ) //&& !obj_dao.isEmpty() )
                  {
                      boObject daoobj = obj_dao.getObject();
                      if(daoobj.getName().equals("Ebo_Template"))
                      {

                          int row = obj_dao.getRow();

                          Ebo_TemplateImpl xtmpl = ((Ebo_TemplateImpl)daoobj);
                          daoobj = xtmpl.loadTemplate( targetobj, null );

                          obj_dao.moveTo( row );

                      }
                      else
                      {

  //                      This is to refresh the Expression attributes in the related objects.
  //
  //                        int row = obj_dao.getRow();
  //
  //                        if ( daoobj.getAttribute("TEMPLATE").getValueObject() != null
  //                             &&
  //                             daoobj.getAttribute("PARENT").getValueLong() == targetobj.bo_boui )
  //                        {
  //                            Ebo_TemplateImpl xtmpl = (Ebo_TemplateImpl)((ObjAttHandler)daoobj.getAttribute("TEMPLATE")).getObject();
  //                            xtmpl.processTemplate( targetobj , daoobj );
  //
  //                        }
  //                        obj_dao.moveTo( row );


                      }

                      Enumeration oEnum = obj_dao.getLineAttributes().elements();
                      boObject objFrom = null;
                      boObject objTo = null;
                      while(oEnum.hasMoreElements())
                      {
                          AttributeHandler batt = (AttributeHandler)oEnum.nextElement();
                          String sbatt = batt.getDefAttribute().getName();
                          if(!sbatt.equals(obj_dao.getDefAttribute().getName())) {

                              if ( batt.getDefAttribute().getName().equalsIgnoreCase( "dao_assignedqueue" ) )
                              {
                                  if ( batt.getValueLong() > 0)
                                  {
                                      if( daoobj.getAttribute( "assignedQueue" ).getInputType() == AttributeHandler.INPUT_FROM_DEFAULT ||
                                          daoobj.getAttribute( "assignedQueue" ).getInputType() == AttributeHandler.INPUT_FROM_UNITIALIZED)
                                      {
                                          daoobj.getAttribute( "assignedQueue" ).setValueLong( batt.getValueLong(), AttributeHandler.INPUT_FROM_DEFAULT );
                                      }
                                  }
                              }
                              else
                              {
                                  if( batt.getValueObject() != null )
                                  {
                                      obj_dao.getAllAttributes().get( batt.getName() ).setValueObject( batt.getValueObject() );
                                  }
                              }
                          }

                      }

                      obj_dao.setValue(daoobj.getBoui());
                      //obj_dao.edit();

                  }
                }
            }

            if( expresatts.size() > 0 )
            {
                Hashtable relobjs = new Hashtable();
                relobjs.put( "ctxobj" , targetobj );
                if( parent != null )
                {
                    relobjs.put("parent",parent);
                }

                Stack stack = new Stack();
                Enumeration oEnum = expresatts.keys();
                while ( oEnum.hasMoreElements() )
                {
                    netgest.bo.impl.templates.boExpressionEval.computeProcessStack( stack , (String)oEnum.nextElement() , expresatts );
                }

                while( stack.size() > 0 )
                {

                    String nextatt = (String)stack.pop();

                    Node xmlexpr = (Node)expresatts.get( nextatt );
                    nextatt = nextatt.substring(nextatt.indexOf('.') + 1 );
                    AttributeHandler hatt = targetobj.getAttribute( nextatt );

                    if ( hatt != null )
                    {

                        String pval = hatt.getValueString();
                        if ( hatt.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                        {
                                if( hatt.getInputType() == AttributeHandler.INPUT_FROM_DEFAULT ||
                                    hatt.getInputType() == AttributeHandler.INPUT_FROM_TEMPLATE ||
                                    hatt.getInputType() == AttributeHandler.INPUT_FROM_UNITIALIZED ||
                                    hatt.getInputType() == AttributeHandler.INPUT_FROM_INTERNAL
                                )
                                {
                                    netgest.bo.impl.templates.boExpressionEval.setExpressionAttribute( relobjs , xmlexpr , hatt, AttributeHandler.INPUT_FROM_TEMPLATE );
                                    //hatt.setInputType( AttributeHandler.INPUT_FROM_TEMPLATE );
                                }
                        }
                        else
                        {
                                if ( (hatt.getInputType() == AttributeHandler.INPUT_FROM_DEFAULT ||
                                    hatt.getInputType() == AttributeHandler.INPUT_FROM_TEMPLATE ||
                                    hatt.getInputType() == AttributeHandler.INPUT_FROM_UNITIALIZED ||
                                    hatt.getInputType() == AttributeHandler.INPUT_FROM_INTERNAL
                                    ) //&&
                                     /*(pval == null || pval.length() == 0)*/ )
                                {
                                    netgest.bo.impl.templates.boExpressionEval.setExpressionAttribute( relobjs , xmlexpr , hatt ,AttributeHandler.INPUT_FROM_TEMPLATE  );
                                    String newValue=hatt.getValueString();
                                    if (( newValue== null || newValue.length() ==0 ))
                                    {
                                        hatt.setValueString( pval, AttributeHandler.INPUT_FROM_TEMPLATE );
                                    }
                                }
    //                            String newValue=hatt.getValueString();
    //                            if (( newValue== null || newValue.length() ==0 ) && fixValues.get( nextatt )!=null )
    //                            {
    //                                hatt.setValueString("*"+(String) fixValues.get(nextatt) );
    //                            }
                        }

                    }

                }
            }
        }
        else
        {
            //COLOCAR OS FIXVALUES QUNADO ESTA EM MODO TEMPLATE OU EM LOAD TEMPLATE
                Enumeration oEnum = fixValues.keys();
                while ( oEnum.hasMoreElements() )
                {
                    String nextatt = (String)oEnum.nextElement();
                    String v = ( String ) fixValues.get( nextatt );
                    String xatt=nextatt.split("\\.")[1];
                    AttributeHandler hatt = targetobj.getAttribute( xatt );
                    if ( hatt != null  )
                    {
                        if ( targetobj.getMode() == boObject.MODE_EDIT_TEMPLATE )
                        {
                          hatt.setValueString( v, AttributeHandler.INPUT_FROM_TEMPLATE );
                        }
                        else
                        {
                           if ( hatt.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE
                                && ( hatt.getDefAttribute().getValueType() == boDefAttribute.VALUE_CHAR
                                || hatt.getDefAttribute().getValueType() == boDefAttribute.VALUE_CLOB )
                                )
                           {
                                hatt.setValueString( v, AttributeHandler.INPUT_FROM_TEMPLATE );
                           }
                        }
                    }
                }
        }

        return targetobj;
    }
    private void processMapAttributes(boObject targetobj,Hashtable expresatts,Hashtable fixValues,boolean processexpr) throws boRuntimeException
    {
        bridgeHandler maps = getBridge( "mappingAttributes" );
        maps.beforeFirst();

        String targetobjname = targetobj.getName();
        while( maps.next() )
        {

            boObject mapObj  = maps.getObject();
            String targetatt    = mapObj.getAttribute( "objectAttributeName" ).getValueString();

            String value     = mapObj.getAttribute( "value" ).getValueString();
            String transf    = mapObj.getAttribute( "transformation" ).getValueString();
            Document xml=null;
            if ( transf.length() > 0)
            {
                xml = ngtXMLUtils.loadXML( transf );
                if ( xml.getFirstChild().getChildNodes().getLength() ==0 )
                {
                    xml=null;
                }
            }

            if( xml==null )
            {
                if( !processexpr )
                {
                    // Static value found, simple put the value in target object

                    // Check if it's a bridge Attribute
                    if( targetatt.indexOf( '.' ) > -1 )
                    {
                        // BridgeAttribute
                        String[] xbatt = targetatt.split("\\.");
                        if(xbatt.length==3)
                        {
                            bridgeHandler bridge = targetobj.getBridge( xbatt[0] );
                            if( bridge != null )
                            {
                                if( !bridge.getName().equals("KEYS") )
                                {
                                    try
                                    {
                                        if ( boObject.getBoManager().getClassNameFromBOUI( getEboContext(), Long.parseLong(value)) !=null)
                                        {
                                            bridge.add(Long.parseLong(value));
                                        }
                                    }
                                    catch ( boRuntimeException e)
                                    {
                                        //
                                    }

                                }
                            }
                        }
                    }
                    else
                    {
                        // Normal attribute
                        AttributeHandler xatt = targetobj.getAttribute( targetatt );

                        if( xatt != null )
                        {
                            //se for um attributo do tipo objecto(s) deve-se testar se continua válido
                            if ( xatt.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                            {

                            //  boObjectList listobj=boObjectList.list( targetobj.getEboContext(),"Select Ebo_Registry where ui="+value );
                            //  if( !listobj.p_vl && listobj.getRowCount() >0)
                            //  {
                             if ( value!=null && value.length() > 0 )
                             {

                                  try
                                  {
                                      if ( boObject.getBoManager().getClassNameFromBOUI( getEboContext(), Long.parseLong(value)) !=null)
                                      {
                                          xatt.setValueString( value, AttributeHandler.INPUT_FROM_TEMPLATE );
                                          //if ( xatt.getValueString() == null || xatt.getValueString().length() == 0 )
                                          //  {
                                          //      xatt.setValueString( value );
                                          //  }
                                      }
                                  }
                                  catch (Exception e)
                                  {
                                      //
                                  }
                              }

                            }
                            else
                            {
                                //if ( xatt.getValueString() == null || xatt.getValueString().length() == 0 || xatt.getValueString().startsWith("*"))
                                if( xatt.getInputType() == AttributeHandler.INPUT_FROM_DEFAULT ||
                                    xatt.getInputType() == AttributeHandler.INPUT_FROM_TEMPLATE ||
                                    xatt.getInputType() == AttributeHandler.INPUT_FROM_UNITIALIZED)
                                {
                                    xatt.setValueString( value, AttributeHandler.INPUT_FROM_TEMPLATE );
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                // Expression found, create a table with expression to process

                expresatts.put( targetobjname + "." + targetatt, xml.getDocumentElement() );
                fixValues.put( targetobjname + "." + targetatt, value );
            }

        }
    }
    private void cloneExtendAttributes(boObject template, boObject targetObj)  throws boRuntimeException
    {

        bridgeHandler tHandler = template.getBridge( "extendAttribute" );
        boObject template_extatt = tHandler.getObject();

        boObject extObj = null;
        bridgeHandler bHandler = targetObj.getBridge("extendAttribute");
        tHandler.beforeFirst();
        while( tHandler.next() )
        {
            extObj = tHandler.getObject();
           // if( !targetObj.exists() && template_extatt.exists() )
            //{
                extObj = extObj.getBoManager().createObject( extObj.getEboContext(), extObj );
                bHandler.add( extObj.getBoui() );
            //    bHandler.edit();
            //}
        }
    }
    // O CLONE do config já faz este trabalho
//    private void cloneConfigAttributes(boObject sourceObj,boObject targetObj)  throws boRuntimeException
//    {
//        boObject extParm = null;
//        bridgeHandler bhFrom = sourceObj.getBridge("extAtt_parameter");
//        bridgeHandler bhTo = targetObj.getBridge("extAtt_parameter");
//
//        bhTo.beforeFirst();
//        bhFrom.beforeFirst();
//
//        while( bhTo.next() && bhFrom.next()  ) //&& !bhFrom.haveVL()
//        {
//            extParm = bhFrom.getObject();
//            String xx1=extParm.getAttribute("extAttrAliasParent").getValueString();
//            extParm = extParm.getBoManager().createObject( extParm.getEboContext(), extParm );
//            bhTo.setValue(extParm.getBoui());
//            //bhTo.edit();
//
//        }
//
//        bhFrom = sourceObj.getBridge("linedepends");
//        bhTo = targetObj.getBridge("linedepends");
//        bhTo.beforeFirst();
//        bhFrom.beforeFirst();
//
//        boObject x = bhTo.getObject();
//
//        while( bhTo.next()&& bhFrom.next()  ) //&& !bhFrom.haveVL()
//        {
//            extParm = bhFrom.getObject();
//            extParm = extParm.getBoManager().createObject( extParm.getEboContext(), extParm );
//            bhTo.setValue(extParm.getBoui());
//            //bhTo.edit();
//
//        }
//        targetObj.getAttribute("logicOperator").setValueString(sourceObj.getAttribute("logicOperator").getValueString());
//    }

//    public void onAfterSave(boEvent event) throws boRuntimeException
//    {
//        rebuildIndex(super.getEboContext());
//    }

    public static synchronized boolean rebuildIndex( EboContext ctx ) throws netgest.bo.runtime.boRuntimeException
    {
        boolean ret = true;
        Connection cn=null;
        CallableStatement cstm=null;
        try
        {
            cn = ctx.getConnectionData();
            boDefHandler defh = boDefHandler.getBoDefinition("Ebo_Template");
            String sql = "ALTER INDEX SYS_IM_"+defh.getBoPhisicalMasterTable()+" REBUILD ONLINE PARAMETERS('sync memory 45M')";
            cstm  = cn.prepareCall(sql);
            cstm.execute();
        }
        catch (SQLException e)
        {
            logger.warn("Error rebuilding Ebo_Template FullText index.\n"+e.getMessage());
        }
        finally
        {
            try
            {
                if(cstm!=null) cstm.close();
            }
            catch (Exception e)
            {
            }
        }
        return ret;
    }
}