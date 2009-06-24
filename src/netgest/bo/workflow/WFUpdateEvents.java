/*Enconding=UTF-8*/
package netgest.bo.workflow;
import java.sql.SQLException;
import java.util.*;
import netgest.bo.def.*;
import netgest.bo.impl.*;
import netgest.bo.runtime.*;
import netgest.utils.*;
import netgest.utils.DataUtils;

/**
 * 
 * @author JMF
 * @version 
 * @see 
 */
public class WFUpdateEvents 
{
    /**
     * Classe que contem os eventos quando um WorkFlow e gravado ou destruido na BD
     * 
     * @see 
     */
    public WFUpdateEvents()
    {
    }
    
    
    /**
     * Antes de ser invocado e metodo DESTROY da Actvity
     * @param object
     * @see 
     */
    public static boolean beforeDestroy(boObject activity)
        throws boRuntimeException
    {
        bridgeHandler subWkfl = activity.getBridge("DAO");
        while ( subWkfl.getRowCount() >0 )
        {
            subWkfl.remove();
        }
        
        return true;
    }
    
    
    /**
     * Antes de ser invocado o metodo SAVE da activity
     * @param object
     * @see 
     */
    public static boolean beforeSave(boObject object) throws boRuntimeException
    {
        bridgeHandler wrk = object.getBridge("workHistory");
        AttributeHandler dur = object.getAttribute("duration");
        AttributeHandler journal = object.getAttribute("journal");
        AttributeHandler durt = object.getAttribute("totalDuration");
        
        double v = dur.getValueDouble();

        if ((v != 0) && !Double.isNaN(v))
        {
            double xdurt = durt.getValueDouble();

            if (Double.isNaN(xdurt))
            {
                xdurt = 0;
            }

            durt.setValueDouble(xdurt + v);
            dur.setValueDouble(0);

            boObject it = wrk.addNewObject();
            it.getAttribute("duration").setValueDouble(v);
            if(journal!=null)
            {
              it.getAttribute("journal").setValueString(journal.getValueString());
              journal.setValueString(null);
            }
            if(object.getEboContext().getBoSession().getPerformerBoui() > 0)
            {
                it.getAttribute("performer").setValueLong(object.getEboContext().getBoSession().getPerformerBoui());
            }
            it.getAttribute("workDate").setValueDate(new java.util.Date());
        }

        int x = 1;
        bridgeHandler xdelegate = object.getBridge("delegate");
        boolean isnew = object.getMode() == boObject.MODE_NEW;

        bridgeHandler dao = object.getBridge("DAO");

        if (xdelegate.getRowCount() > 0)
        {
            long controlBy = object.getAttribute("controlBy").getValueLong();

            if (controlBy == 0)
            {
                if(object.getEboContext().getBoSession().getPerformerBoui() > 0)
                {
                    object.getAttribute("controlBy").setValueLong(object.getEboContext().getBoSession().getPerformerBoui());
                }
            }

            boolean addtoDAO = true;
            xdelegate.beforeFirst();

            while (xdelegate.next() )
            {
                long delegateBoui = xdelegate.getObject().getBoui();
                dao.beforeFirst();
                addtoDAO = true;

                while (dao.next() && addtoDAO  )
                {
                    long xassign = dao.getAttribute("DAO_assignedQueue").getValueLong();
                    long xparent = dao.getObject().getAttribute("PARENTCTX").getValueLong();

                    if ((xparent == object.getBoui()) && (xassign == delegateBoui))
                    {
                        addtoDAO = false;
                    }
                }

                if (addtoDAO)
                {
                    boObject newObject = object.getBoManager().createObject(object.getEboContext(), object.getName());
                    boAttributesArray xatrs = object.getAttributes();
                    Enumeration xenum = xatrs.elements();

                    while (xenum.hasMoreElements())
                    {
                        AttributeHandler xat;
                        xat = (AttributeHandler) xenum.nextElement();

                        if (((xat.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) ||
                                ((xat.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                (xat.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1))) && !xat.getName().equalsIgnoreCase("BOUI") &&
                                !xat.getName().equalsIgnoreCase("performer"))
                        {
                            String xvalue = xat.getValueString();

                            if ((xvalue != null) && !xvalue.equals(""))
                            {
                                AttributeHandler at = newObject.getAttribute(xat.getName());
                                at.setValueString(xvalue);
                            }
                        }
                    }

                    dao.add(newObject.getBoui());
                    //dao.edit();

                    dao.getAttribute("DAO_assignedQueue").setValueLong(xdelegate.getObject().getBoui());
                    newObject.getAttribute("assignedQueue").setValueLong(xdelegate.getObject().getBoui());

                    newObject.getAttribute("PARENTCTX").setValueLong(object.getBoui());

                    //newObject.getAttribute("PARENT").setValueLong( object.getBoui() );
                }
            }
        }

        //se foram apagados delegate's

        /*
        dao.beforeFirst();
        while ( dao.next())
        {
           long xassign  =dao.getAttribute("DAO_assignedQueue").getValueLong();
           long xparent    = dao.getObject().getAttribute("PARENTCTX").getValueLong();

           if ( xparent != 0 && xparent== object.getBoui() )
           {
               xdelegate.beforeFirst();
               boolean found=false;
               while ( xdelegate.next() && !found )
               {
                   long delegateBoui=xdelegate.getObject().getBoui();
                   if ( xassign== delegateBoui ) found=true;
               }
               if ( !found )
               {
                   dao.remove();
               }

           }


        }*/

        // PROCESSAMENTO DE INFORMS
        bridgeHandler xinform = object.getBridge("inform");

        if (xinform.getRowCount() > 0)
        {
            boolean addtoDAO = true;
            xinform.beforeFirst();

            while (xinform.next() )
            {
                long informBoui = xinform.getObject().getBoui();
                dao.beforeFirst();
                addtoDAO = true;

                while (dao.next() && addtoDAO )
                {
                    long xassign = dao.getAttribute("DAO_assignedQueue").getValueLong();
                    long xparent = dao.getObject().getAttribute("PARENTCTX").getValueLong();

                    if ((xparent == object.getBoui()) && (xassign == informBoui))
                    {
                        addtoDAO = false;
                    }
                }

                if (addtoDAO)
                {
                    boObject newObject = object.getBoManager().createObject(object.getEboContext(), "note");
                    boAttributesArray xatrs = object.getAttributes();
                    Enumeration xenum = xatrs.elements();

                    while (xenum.hasMoreElements())
                    {
                        AttributeHandler xat;
                        xat = (AttributeHandler) xenum.nextElement();

                        if (((xat.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) ||
                                ((xat.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                (xat.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1))) && !xat.getName().equalsIgnoreCase("BOUI") &&
                                !xat.getName().equalsIgnoreCase("performer") && !xat.getName().equalsIgnoreCase("controlBy"))
                        {
                            String xvalue = xat.getValueString();

                            if ((xvalue != null) && !xvalue.equals(""))
                            {
                                AttributeHandler at = newObject.getAttribute(xat.getName());

                                if (at != null)
                                {
                                    at.setValueString(xvalue);
                                }
                            }
                        }
                    }

                    dao.add(newObject.getBoui());
                    //dao.edit();

                    dao.getAttribute("DAO_assignedQueue").setValueLong(xinform.getObject().getBoui());
                    newObject.getAttribute("assignedQueue").setValueLong(xinform.getObject().getBoui());

                    newObject.getAttribute("PARENTCTX").setValueLong(object.getBoui());

                    //newObject.getAttribute("PARENT").setValueLong( object.getBoui() );
                    newObject.getAttribute("name").setValueString("(FYI) " + newObject.getAttribute("name").getValueString());
                }
            }
        }

        //se foram apagados inform's
        /*
        dao.beforeFirst();
        while ( dao.next())
        {
           long xassign  =dao.getAttribute("DAO_assignedQueue").getValueLong();
           long xparent    = dao.getObject().getAttribute("PARENTCTX").getValueLong();

           if ( xparent != 0 )
           {
               xinform.beforeFirst();
               boolean found=false;
               while ( xinform.next() && !found )
               {
                   long informBoui=xinform.getObject().getBoui();
                   if ( xassign== informBoui ) found=true;
               }
               if ( !found )
               {
                   dao.remove();
               }

           }


        }

        */
        
        beforeSaveFinal(object,dao);
        
        return netgest.bo.workflow.Parser.doParse(object);
    }

    public final static void beforeSaveFinal(boObject object,bridgeHandler dao)  throws boRuntimeException
    {
        dao.beforeFirst();

        while (dao.next() )
        {
            boObject daoobj = dao.getObject();

            if (daoobj.getName().equals("Ebo_Template"))
            {
                int row = dao.getRow();

                Ebo_TemplateImpl xtmpl = ((Ebo_TemplateImpl) daoobj);
                daoobj = xtmpl.loadTemplate();
                xtmpl.processTemplate(object, daoobj);

                dao.moveTo(row);
                dao.setValue(daoobj.getBoui());
                //dao.edit();
            }

            // else
            // {
            if (dao.getAttribute("DAO_assignedQueue").getValueObject() == null)
            {
                if (dao.getObject().getAttribute("assignedQueue").getValueObject() != null)
                {
                    dao.getAttribute("DAO_assignedQueue").setValueObject(dao.getObject().getAttribute("assignedQueue").getValueObject());
                    dao.getAttribute("DAO_assignedQueue").setInputType(AttributeHandler.INPUT_FROM_INTERNAL);
                }
                else
                {
                    if(object.getEboContext().getBoSession().getPerformerBoui() > 0)
                    {
                        dao.getAttribute("DAO_assignedQueue").setValueLong(dao.getEboContext().getBoSession().getPerformerBoui());
                    }
                    dao.getAttribute("DAO_assignedQueue").setInputType(AttributeHandler.INPUT_FROM_INTERNAL);
                    dao.getObject().getAttribute("assignedQueue").setValueObject(dao.getAttribute("DAO_assignedQueue").getValueObject());
                    dao.getObject().getAttribute("assignedQueue").setInputType(AttributeHandler.INPUT_FROM_INTERNAL);
                }                
            }
            
            if(!ClassUtils.isEqual(dao.getObject().getAttribute("assignedQueue").getValueObject(), 
                        dao.getAttribute("DAO_assignedQueue").getValueObject()))
            {
                if(dao.getAttribute("DAO_assignedQueue").getInputType() == AttributeHandler.INPUT_FROM_USER)
                {
                    if(dao.getObject().getAttribute("assignedQueue").getValueObject() == null || 
                        dao.getObject().getAttribute("assignedQueue").getInputType() != AttributeHandler.INPUT_FROM_USER)
                    {
                        dao.getObject().getAttribute("assignedQueue").setValueObject(dao.getAttribute("DAO_assignedQueue").getValueObject());
                        dao.getObject().getAttribute("assignedQueue").setInputType(AttributeHandler.INPUT_FROM_INTERNAL);
                    }
                    else
                    {
                        dao.getAttribute("DAO_assignedQueue").setValueObject(dao.getObject().getAttribute("assignedQueue").getValueObject());
                        dao.getAttribute("DAO_assignedQueue").setInputType(AttributeHandler.INPUT_FROM_INTERNAL);
                    }
                }
                else if(dao.getObject().getAttribute("assignedQueue").getValueObject() != null)
                {
                    dao.getAttribute("DAO_assignedQueue").setValueObject(dao.getObject().getAttribute("assignedQueue").getValueObject());
                    dao.getAttribute("DAO_assignedQueue").setInputType(AttributeHandler.INPUT_FROM_INTERNAL);
                }
                else
                {
                    dao.getObject().getAttribute("assignedQueue").setValueObject(dao.getAttribute("DAO_assignedQueue").getValueObject());
                    dao.getObject().getAttribute("assignedQueue").setInputType(AttributeHandler.INPUT_FROM_INTERNAL);
                }
            }
                        
            
            //                    if( dao.getObject().getAttribute( "PARENT" ).getValueObject() == null )
            //                    {
            //                        dao.getObject().getAttribute( "PARENT" ).setValueLong( dao.getParent().getBoui() );
            //                    }
            // }
        }

        //gerar numero sequencial
        if (!object.exists())
        {
//            try
//            {
				if(object.getAttribute("nrdoc").getValueLong()<=0)
				{
					long nrdoc = DataUtils.GetSequenceNextVal(object.getEboContext().getApplication(), object.getEboContext().getConnectionData(), object.getName());
					object.getAttribute("nrdoc").setValueLong(nrdoc);
				}
//            }
//            catch (SQLException e)
//            {
//                String[] args = { object.getName() };
//                throw new boRuntimeException("sysBridges.beforeSave", "BO-3121", e, args);
//            }
        }        
    }

    /**
     * Antes de ser invocado e metodo DESTROY do ExtendAttribute, remove no DAOConfig
     * @param object Actividade
     * @return True if ok, False if not 
     */
    public static boolean removeExtendAttr(boObject extendAttr) throws boRuntimeException
    {        
        boBridgeRow bridgeRow = extendAttr.getParentBridgeRow();
        if(bridgeRow != null)
        {
            boObject template = bridgeRow.getParent();
            bridgeHandler dao = template.getBridge("DAO");
            bridgeHandler parameterList = null;
            boObject parameter = null;
            dao.beforeFirst();
            while(dao.next())
            {
                boObject config = dao.getAttribute("config").getObject();
                if(config != null)
                {        
                    parameterList = config.getBridge("extAtt_parameter");
                    parameterList.beforeFirst();
                    while(parameterList.next())
                    {
                        parameter = parameterList.getObject();
                        if(parameter.getAttribute("extAttrParentBoui").getValueLong() == extendAttr.getBoui())
                        {
                            parameterList.remove();
                        }
                    }
                }
            }
        } 
        return true;
    }
    
}