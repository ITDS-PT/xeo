/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netgest.bo.boConfig;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefClsEvents;
import netgest.bo.def.boDefDocument;
import netgest.bo.def.boDefObjectFilter;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.security.securityRights;
import netgest.bo.system.Logger;
import netgest.io.iFile;
import netgest.io.iFileConnector;
import netgest.io.iFilePermissionDenied;

/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public abstract class AttributeHandler implements boIEvents
{

    public static final byte INPUT_FROM_UNITIALIZED=0;
    public static final byte INPUT_FROM_INTERNAL=1;
    public static final byte INPUT_FROM_USER=2;
    public static final byte INPUT_FROM_TEMPLATE=3;
    public static final byte INPUT_FROM_DEFAULT=4;


	private static Logger logger = Logger.getLogger( AttributeHandler.class );


    protected byte p_attinputtype = -1;
    private byte    p_state;

    private boolean p_isvalid=true;

    private boObject        p_parent;
    private boDefAttribute  p_bodefatt;

    private String p_invalidreason;

    private boolean     p_isVisible;
    private boolean     p_isDisable;

    private boolean     p_isEnabledforRequest   = false;
    private boolean     p_eventsDisabled        = false;
    private ArrayList   p_eventListeners;
    
    /**
     * The iFile object for an iFile that is stored
     * in a Java Content Repository (JCR)
     */
    protected iFile p_valueIFileECM;

    public AttributeHandler(boObject parent,boDefAttribute def)
    {
        p_parent       = parent;
        p_bodefatt     = def;
        reset();
    }

    public void reset()
    {
        p_isVisible    = true;
        p_isDisable    = false;
        //p_isRequired   = p_bodefatt.getRequired();
        //p_isRecomended = p_bodefatt.getRecommend();
    }

    public void   setValuesString( String[] newval, byte type ) throws boRuntimeException
    {
        if(canAlter(type))
        {
            setValuesString(newval);
            setInputType(type);
        }
    }

    public void   setValuesString( String[] newval ) throws boRuntimeException
    {

    }
    public abstract void   setValueString( String newval ) throws boRuntimeException;
    public abstract void   setValueObject( Object value )  throws boRuntimeException;

    public void  setValueString( String value, byte type ) throws boRuntimeException
    {
        if(canAlter(type))
        {
            setValueString(value);
            setInputType(type);
        }
    }

    public void  setValueObject( Object value, byte type ) throws boRuntimeException
    {
        if(canAlter(type))
        {
            setValueObject(value);
            setInputType(type);
        }
    }

    public void setValuesiFile( iFile[] files, byte type ) throws boRuntimeException
    {
        setValueObject(files, type);
    }

    public void setValuesiFile( iFile[] files ) throws boRuntimeException
    {
        setValuesiFile( files, AttributeHandler.INPUT_FROM_USER );
    }

    public iFile changeECMIFile(iFile newVal) throws boRuntimeException {

		boDefDocument ecmDef = getDefAttribute().getECMDocumentDefinitions();
			
		//Get the default repository name
		String repName = boConfig.getApplicationConfig().
			getDefaultFileRepositoryConfiguration().getName();
		
		//Check if this attribute uses a different repository
		if (ecmDef.getRepositoryName()!= null)
			repName = ecmDef.getRepositoryName();
		
		//Retrieve the FileConnector
		iFileConnector con = boConfig.getApplicationConfig().
				getFileRepositoryConfiguration(repName).getConnector(this);
	
		if ( newVal != null ) {
			try {
				//create the new file
				iFile file = con.createIFileInContext( newVal , this ) ;
				file.setBinaryStream( newVal.getInputStream() );
				this.p_valueIFileECM = file;
			} catch ( iFilePermissionDenied e ) {
				logger.warn( "Permissio denied in Attribute %s of object %s with boui %d", 
						this.getName(), 
						this.getParent().getTextCARDID().toString(),
						this.getParent().getBoui()
				);
			}
     	}
    	else {
    		p_valueIFileECM = null;
    	}
		
    	return newVal;    	
    }
    
    public void setValueiFile(iFile file, byte type)  throws boRuntimeException
    {
    	if (getDefAttribute().getECMDocumentDefinitions() != null){
    		changeECMIFile(file);
    	}
    	else
    		setValueObject( file, type );
    }

    public void setValueiFile(iFile file)  throws boRuntimeException
    {
        setValueiFile(file, AttributeHandler.INPUT_FROM_USER);
    }

    public void setValuesLong(long[] values, byte type)  throws boRuntimeException
    {
        BigDecimal[] bg = null;
        if( values != null )
        {
            bg = new BigDecimal[ values.length ];
            for (int i = 0; i < values.length; i++)
            {
                bg[i] = BigDecimal.valueOf( values[i] );
            }
        }
        setValueObject( bg, type );
    }

    public void setValuesLong(long[] values)  throws boRuntimeException
    {
        setValuesLong(values, AttributeHandler.INPUT_FROM_USER);
    }

    public boObject getObject() throws boRuntimeException
    {
        return null;
    }
    public boObject[] getObjects() throws boRuntimeException
    {
        return null;
    }

    public void setObject(boObject obj)  throws boRuntimeException
    {
        setObject(obj, INPUT_FROM_USER);
    }

    public void setObject(boObject obj, byte type)  throws boRuntimeException
    {
        if(obj == null)
            setValueObject(obj, type);
        else
            setValueLong(obj.getBoui(), type);
    }
    
    
    /**
     * 
     * Sets the value of the attribute as a boolean value
     * 
     * @param value The boolean value
     * 
     * @throws boRuntimeException 
     */
    public void setValueBoolean(boolean value) throws boRuntimeException
    {
    	if (value)
    		setValueObject("1");
    	else
    		setValueObject("0");
    }
    
    /**
     * 
     * Retrieves the value of the attribute as a boolean value
     * 
     * @return The value of the attribute as a boolean
     * 
     * @throws boRuntimeException
     */
    public Boolean getValueBoolean() throws boRuntimeException
    {
    	String booleanVal = String.valueOf(this.getValueObject());
        if( booleanVal != null )
        {
            if (booleanVal.equalsIgnoreCase("1") || booleanVal.equalsIgnoreCase("true"))
            	return new Boolean(true);
            else
            	return new Boolean(false);
        }
        return null;
    }
    

    public void setValueLong(long value, byte type)  throws boRuntimeException
    {
        setValueObject(new BigDecimal(value), type);
    }

    public void setValueLong(long value)  throws boRuntimeException
    {
        setValueLong(value, INPUT_FROM_USER);
    }

    public void setValuesDouble( double[] values, byte type ) throws boRuntimeException
    {
        BigDecimal[] bg = null;
        if( values != null )
        {
            bg = new BigDecimal[ values.length ];
            for (int i = 0; i < values.length; i++)
            {
                bg[i] = new BigDecimal( String.valueOf(values[i]) );
            }
        }
        setValueObject( bg, type);
    }

    public void setValuesDouble( double[] values ) throws boRuntimeException
    {
        setValuesDouble(values, AttributeHandler.INPUT_FROM_USER);
    }

    public void setValueDouble(double value, byte type)  throws boRuntimeException
    {
        setValueObject(new BigDecimal(String.valueOf(value)), type);
    }

    public void setValueDouble(double value)  throws boRuntimeException
    {
        setValueDouble(value, AttributeHandler.INPUT_FROM_USER);
    }

    public void setValuesDate(java.util.Date[] date, byte type)  throws boRuntimeException
    {
       Timestamp[] ts=null;
        if( date != null )
        {
        	ts = new Timestamp[ date.length ];
            for (int i = 0;date != null && i < date.length; i++)
            {
                if( date[i] != null )
                {
                    ts[i] = new Timestamp( date[i].getTime() );
                }
            }
        }
        setValueObject( ts, type );
    }

    public void setValuesDate(java.util.Date[] date)  throws boRuntimeException
    {
        setValuesDate(date, AttributeHandler.INPUT_FROM_USER);
    }

    public void setValueDate(java.util.Date date, byte type)  throws boRuntimeException
    {
        setValueObject(new Timestamp(date.getTime()), type);
    }

    public void setValueDate(java.util.Date date)  throws boRuntimeException
    {
        setValueDate(date, AttributeHandler.INPUT_FROM_USER);
    }

    public void setValues(BigDecimal[] newvalue) throws boRuntimeException
    {

    }
    public void setValues(BigDecimal[] newvalue, byte type) throws boRuntimeException
    {
        if(canAlter(type))
        {
            setValues(newvalue);
            setInputType(type);
        }
    }

    public abstract Object getValueObject() throws boRuntimeException;
    public abstract String getValueString() throws boRuntimeException; //{

    public iFile getValueiFile()  throws boRuntimeException
    {
        if (getDefAttribute().getECMDocumentDefinitions() != null){
        	return p_valueIFileECM;
        }
        else
        	return (iFile)getValueObject();
    }

    public double getValueDouble()  throws boRuntimeException
    {
        BigDecimal bd = (BigDecimal)this.getValueObject();
        if( bd != null )
            return bd.doubleValue();
        return 0;
    }
    public long getValueLong()  throws boRuntimeException
    {
        BigDecimal bd = (BigDecimal)this.getValueObject();
        if( bd != null )
            return bd.longValue();
        return 0;
    }
    public java.util.Date getValueDate()  throws boRuntimeException
    {
        Timestamp bd = (Timestamp)this.getValueObject();
        if( bd != null )
            return new Date(bd.getTime());
        return null;
    }
    public long[] getValuesLong() throws boRuntimeException
    {
        BigDecimal[] values = (BigDecimal[])getValueObject();
        long[] ret = null;
        if( values != null )
        {
            if( values != null )
            {
                ret = new long[values.length];
                for (short i = 0; i < values.length; i++)
                {
                    ret[i] = values[i].longValue();
                }

            }
        }
        return ret;
    }



    public boolean isValid() {
        return p_isvalid;
    }
    public void setInvalid() {
        p_isvalid=false;
    }
    public void setInvalid(String reason) {
        p_invalidreason=reason;
        p_isvalid=false;
    }
    public void setInvalid(String reason,String value) {
        p_invalidreason=reason;
        p_isvalid=false;
    }
    public void setValid() {
        p_isvalid=true;
        p_invalidreason = null;
    }

    public void setDisabled(){
        p_isDisable=true;
    }

    public boolean isDisabled() throws boRuntimeException{
        if( this.p_parent.getMode() == boObject.MODE_EDIT_TEMPLATE ) return false;
        else return (p_isDisable || disableWhen() ||(!securityRights.hasRights(this.p_parent,this.p_parent.getName(),this.getName(),this.p_parent.getEboContext().getBoSession().getPerformerBoui()
                ,securityRights.WRITE))) && !p_isEnabledforRequest;
    }


    public void setEnabledforRequest(){
        p_isEnabledforRequest=true;
    }

    public void setDisabledforRequest(){
        p_isEnabledforRequest=false;
    }

    public boolean isVisible() throws boRuntimeException{
        if( this.p_parent.getMode() == boObject.MODE_EDIT_TEMPLATE ) return true;
        else return p_isVisible && !hiddenWhen() && (securityRights.hasRights(this.p_parent,this.p_parent.getName(),this.getName(),this.p_parent.getEboContext().getBoSession().getPerformerBoui()
                ,securityRights.READ));
    }


    public void setEnabled(){
        p_isDisable=false;
    }

    public void setVisible(){
        p_isVisible=true;
    }

    public void setHidden(){
        p_isVisible=false;
    }
    public String getErrorMessage() {
        return p_invalidreason;
    }

    public boObject getParent() {
        return p_parent;
    }

    public boolean hasRights() throws boRuntimeException
    {

        long performer=p_parent.getEboContext().getBoSession().getPerformerBoui();
        return securityRights.hasRights( this.p_parent, this.p_parent.getName() , p_bodefatt.getName() , performer  );
    }

    public boolean canAccess() throws boRuntimeException
    {
        return hasRights();
    }
    public boolean hasError() {
        return false;
    }

    public boDefAttribute getDefAttribute() {
        return (boDefAttribute)p_bodefatt;
    }

    public boolean getRecommend()
    {
        //return p_isRecomended;
        return false;
    }

    public String getName()
    {
        return p_bodefatt.getName();
    }

    public String[] getDependsFrom() {
        return null;
    }

    public String[] getUsedBy() {
        return null;
    }

    public String getFilterBOQL_query()
    {
      return getFilterBOQL_query("");
    }

    public String getFilterBOQL_query(String forObject)
    {
        String result = "";
        Object resultObject = null;

        boDefObjectFilter[] filters = p_bodefatt.getObjectFilter();
        for (int i = 0;filters != null && i < filters.length; i++)
        {
            boDefObjectFilter filter = filters[i];
            String forObjectAtt = filter.getForObject();
            boolean condition = true; // irá conter se o filter é válido para este estado do atributo. (A FAZER)

            if(!condition)
            continue;

            if( forObjectAtt!=null && forObjectAtt.length() >0 && forObject!="" && !forObjectAtt.equalsIgnoreCase(forObject))
            continue;

            boDefXeoCode codeCondition = filters[i].getCondition();
            if( codeCondition != null && codeCondition.getSource() != null)
            {
                try
                {
                    boXEPEval xeoEval = new boXEPEval( codeCondition, getEboContext() );
                    xeoEval.addThisObject( this.getParent() );
                    resultObject = xeoEval.eval();
                    if(resultObject != null)
                    {
                        result = resultObject.toString();
                    }
                    else
                    {
                        result = "";
                    }
                }
                catch (Exception e)
                {
                    result = "";
                }
            }
            else if ( filters[i].getXeoQL() != null && !"".equals( filters[i].getXeoQL() ) )
            {
                String xeoql = filters[i].getXeoQL() + " ";
                try
                {
                    String tok = null;
                    String tokAux = null;
                    String xepStr = null;
                    String pattern = "(this\\..*?)['|\\s]";
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher( xeoql  );
                    while (m.find())
                    {
                        tok = m.group(1);
                        if(tok.indexOf("this") != -1)
                        {
                            tokAux = tok.replaceAll("this","THIS");
                            tokAux = tokAux.replaceAll("boui","BOUI()");
                            boXEPEval eval = new boXEPEval( tokAux+ ";", boDefXeoCode.LANG_XEP, getEboContext() );
                            eval.addThisObject( this.getParent() );
                            try
                            {
                                Object aux = null;
                                try
                                {
                                    aux = eval.eval();
                                    if(aux != null)
                                    {
                                        xepStr = aux.toString();
                                    }
                                }
                                catch(Exception e)
                                {
                                    if(aux != null)
                                    {
                                        xepStr = (String)aux;
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                logger.severe(LoggerMessageLocalizer.getMessage("ERROR_EVALUATING_XEOQL")+tokAux+";",e);
                            }
                            xeoql = xeoql.replaceAll(tok,xepStr == null ? "-1":xepStr);
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    result = "";
                }
                result = xeoql.replaceAll("this",String.valueOf(this.getParent().getBoui()));
            }
        }
        return result;
    }

    public boolean valid() throws boRuntimeException
    {
           p_invalidreason=null;
           p_isvalid=true;

            if(this.getDefAttribute().isMemberOfInterface())
            {
              String[] intfs = this.getDefAttribute().getInterfaces();
              boolean imp=false;
              for (int i = 0; i < intfs.length; i++)
              {
                if(this.getParent().getAttribute("implements_"+intfs[i]).getValueString().equals("S"))
                  imp = imp || true;
              }
              if(!imp)
                return true;
            }

            boolean req = required();
            //Check for required
            if(req && ( this.getValueObject() == null || this.getValueString().length() == 0 ) )
            {
                this.setInvalid( MessageLocalizer.getMessage("ATTRIBUTE_REQUIRED") );
                this.getParent().addErrorMessage(this, MessageLocalizer.getMessage("ATTRIBUTE_REQUIRED"));
            }
            else
            {

              if( this.getValueObject() != null )
              {
                  boObject[] objs = null;
                  if( this.getDefAttribute().getDbIsTabled() )
                  {
                      objs = this.getObjects();
                  }
                  else
                  {
                      objs = new boObject[] { this.getObject() };
                  }
                  for (int i = 0; i < objs.length; i++)
                  {
                      if ( objs[i] != null && !objs[i].getBoDefinition().getBoCanBeOrphan() && !objs[i].getBoDefinition().hasAttribute(this.getName()) && !objs[i].valid() )
                      {
                          StringBuffer error = new StringBuffer();
                          ArrayList list = objs[i].getObjectErrors();
                          if( list != null )
                          {
                              ListIterator ilt = list.listIterator();
                              while ( ilt.hasNext() )
                              {
                                  error.append("<span class='error' >")
                                  .append(ilt.next())
                                  .append("</span>\n");
                              }
                          }

                          Hashtable ht = objs[i].getAttributeErrors();

                          if( ht != null )
                          {
                              Enumeration oEnum = ht.keys();
                              while ( oEnum.hasMoreElements() )
                              {
                                  AttributeHandler att = ( AttributeHandler )  oEnum.nextElement();

                                  error
                                  .append( "<span class='error'   oncclick=\"activeField('" )
                                  .append( att.getName() )
                                  .append( "')\" >" )
                                  .append( att.getDefAttribute().getLabel() )
                                  .append( " : " )
                                  .append( ht.get( att ) )
                                  .append("</span>\n");
                              }
                          }
                          objs[i].clearErrors();
                          this.setInvalid( error.toString() );
                      }
                  }
                  if(this.isBridge())
                  {
                    boAttributesArray brArr = this.getParent().getBridge(this.getName()).getAllAttributes();
                    if(brArr != null)
                    {
                        Enumeration oEnum = brArr.elements();
                        AttributeHandler brAtr;
                        String auxS;
                        String auxL;
                        while (p_isvalid && oEnum.hasMoreElements())
                        {
                            brAtr = ( AttributeHandler ) oEnum.nextElement();
                            auxS = this.getName() + "." + this.getName();
                            auxL = this.getName() + ".LIN";
                            if(!brAtr.getName().startsWith(auxS) && !brAtr.getName().startsWith(auxL))
                            {
                                if(!brAtr.valid())
                                {
                                    p_invalidreason = brAtr.getDefAttribute().getLabel()+" : " + brAtr.p_invalidreason;
                                    p_isvalid = false;
                                }
                            }
                        }
                    }
                  }
              }


            }

      //Check any valid rule that may exist 
      p_isvalid = p_isvalid && validate();
            
      return this.p_isvalid;

    }

    protected void setMode(byte newmode) {
        p_state = newmode;
    }

    public byte getMode() {
        return p_state;
    }


   public boolean isBridge()
   {
     boolean toRet=false;
     byte atype = this.p_bodefatt.getAtributeType();
     if (atype == boDefAttribute.TYPE_OBJECTATTRIBUTE)
     {
        byte typeRelation = this.p_bodefatt.getRelationType();
        if (typeRelation == boDefAttribute.RELATION_1_TO_N ||typeRelation == boDefAttribute.RELATION_1_TO_N_WBRIDGE)
          toRet=true;
     }
     return toRet;

   }

   public boolean isObject()
   {
     boolean toRet=false;
     byte atype = this.p_bodefatt.getAtributeType();
     if (atype == boDefAttribute.TYPE_OBJECTATTRIBUTE)
     {
        byte typeRelation = this.p_bodefatt.getRelationType();
        if (typeRelation == boDefAttribute.RELATION_1_TO_1)
          toRet=true;
     }
     return toRet;

   }

    public String toString() {
        try {
            return this.getValueString();
        } catch (boRuntimeException e) {
            throw new boRuntimeException2(e.getMessage());
        }
    }

    public final boEvent fireEvent(byte eventType, Object value) throws boRuntimeException
    {
        boEvent event = new boEvent( eventType, this, value );
        fireEvent( event );
        return event;
    }

    public final boolean fireBeforeChangeEvent(Object sourceobject, Object oldvalue, Object newvalue) throws boRuntimeException
    {
        boolean wasCanceled = fireEvent( boEvent.EVENT_BEFORE_CHANGE, oldvalue, newvalue ).wasCanceled();
        if(oldvalue==null && newvalue != null)
            wasCanceled = fireEvent( boEvent.EVENT_BEFORE_ADD, newvalue ).wasCanceled();
        if(newvalue==null && oldvalue!=null)
            wasCanceled = fireEvent( boEvent.EVENT_BEFORE_REMOVE, oldvalue ).wasCanceled();
        return !wasCanceled;
    }

    public final void fireAfterChangeEvent(Object sourceobject, Object oldvalue, Object newvalue) throws boRuntimeException
    {
        fireEvent( boEvent.EVENT_AFTER_CHANGE, oldvalue, newvalue ).wasCanceled();
        if(oldvalue==null && newvalue != null)
            fireEvent( boEvent.EVENT_AFTER_ADD, newvalue ).wasCanceled();
        if(newvalue==null && oldvalue!=null)
            fireEvent( boEvent.EVENT_AFTER_REMOVE, oldvalue ).wasCanceled();
    }


    public final boEvent fireEvent(byte eventType, Object oldvalue, Object newvalue) throws boRuntimeException
    {
        boEvent event = new boEvent(eventType, this, oldvalue, newvalue);
        fireEvent( event );
        return event;
    }

    private synchronized void fireEvent( boEvent event ) throws boRuntimeException
    {
        if( !this.p_eventsDisabled && getEboContext().isInModeBatch( EboContext.MODE_BATCH_EXECUTE_ATTRIBUTES_EVENT ) )
        {
            try
            {
                // Desliga os eventos... para evitar recursividade.
                this.p_eventsDisabled = true;

                // Evia o evento para os listeners
                if( p_eventListeners != null )
                {
                    Iterator it = p_eventListeners.iterator();
                    while( it.hasNext() && !event.wasCanceled() )
                    {
                        (( boEventListener )it.next()).onEvent( event );
                    }
                }

                // Verifica se o evento não foi cancelado anteriormente.
                if( !event.wasCanceled() )
                {
                    byte eventType = event.getEvent();
                    switch( eventType )
                    {
                        case boEvent.EVENT_AFTER_ADD:
                            onAfterAdd( event );
                            break;
                        case boEvent.EVENT_AFTER_CHANGE:
                            onAfterChange( event );
                            break;
                        case boEvent.EVENT_AFTER_CHANGE_ORDER:
                            onAfterChangeOrder( event );
                            break;
                        case boEvent.EVENT_AFTER_REMOVE:
                            onAfterRemove( event );
                            break;
                        case boEvent.EVENT_BEFORE_ADD:
                            if(!onBeforeAdd( event ))
                            {
                                event.cancelEvent();
                            }
                            break;
                        case boEvent.EVENT_BEFORE_CHANGE:
                            if(!onBeforeChange( event ))
                            {
                                event.cancelEvent();
                            }
                            break;
                        case boEvent.EVENT_BEFORE_CHANGE_ORDER:
                            if(!onBeforeChangeOrder( event ))
                            {
                                event.cancelEvent();
                            }
                            break;
                        case boEvent.EVENT_BEFORE_REMOVE:
                            if(!onBeforeRemove( event ))
                            {
                                event.cancelEvent();
                            }
                            break;
                    }
                }
            }
            finally
            {
                this.p_eventsDisabled = false;
            }
        }
    }

    public void addEventListener( boEventListener listener )
    {
        if( p_eventListeners == null ) p_eventListeners = new ArrayList(1);

        // Evita que existam listeners duplicados
        if( p_eventListeners.indexOf( listener ) == -1 )
        {
            p_eventListeners.add( listener );
        }
    }

    public void removeEventListener( boEventListener listener )
    {
        if( p_eventListeners != null )
        {
            p_eventListeners.remove( listener );
        }
    }

    public ArrayList getEventListeners()
    {
        return p_eventListeners;
    }

    private boolean evalEventCode( boEvent event ) throws boRuntimeException
    {
        boDefClsEvents eventHandler = p_bodefatt.getEvent( boEvent.EVENT_NAME[ event.getEvent() ].toUpperCase() );
        if( eventHandler != null )
        {
            boDefXeoCode code = eventHandler.getEventCode();
            if( code != null && code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                if( code.getLanguage() == boDefXeoCode.LANG_XEP )
                {
                    boXEPEval eval = new boXEPEval( code, getEboContext() );
                    eval.addObjectAttributeValue( "value", this );
                    eval.addThisObject( this.getParent() );
                    eval.eval();
                    return eval.getReturnBoolean();
                }
            }
        }
        return true;
    }

    public boolean onBeforeChange(boEvent event)   throws boRuntimeException
    {
        return evalEventCode( event );
    }

    public void    onAfterChange(boEvent event)    throws boRuntimeException
    {
        evalEventCode( event );
    }

    public boolean onBeforeRemove(boEvent event)   throws boRuntimeException
    {
        return evalEventCode( event );
    }

    public void    onAfterRemove(boEvent event)    throws boRuntimeException
    {
        evalEventCode( event );
    }

    public boolean onBeforeAdd(boEvent event)      throws boRuntimeException
    {
        return evalEventCode( event );
    }

    public void    onAfterAdd(boEvent event)       throws boRuntimeException
    {
        evalEventCode( event );
    }

    public boolean  onBeforeChangeOrder(boEvent event)       throws boRuntimeException
    {
        return evalEventCode( event );
    }

    public void     onAfterChangeOrder(boEvent event)       throws boRuntimeException
    {
        evalEventCode( event );
    }


    public boolean methodIsHidden(String methodName)
    {
        return false;
    }

    /**
     * 
     * Retrieves the EboContext associated to the parent {@link boObject}
     * of this AttributeHandler
     * 
     * @return
     */
    public EboContext getEboContext()
    {
        return getParent().getEboContext();
    }

    public byte getInputType(  )
    {
        if( p_attinputtype == -1 )
        {
            p_attinputtype = boObjectUtils.getAttributeInputType( getParent().p_bodata,  this.getName() );
        }
        return p_attinputtype;
    }

    public void setInputType( byte inputType )
    {
        p_attinputtype = inputType;
    }

    public boolean canAlter(byte inputType)
    {
        // se fôr o utilizador pode alterar sempre
        if(INPUT_FROM_USER == inputType)
        {
            return true;
        }
        //se valor que se encontra colocado fôr do utilizador não pode alterar
        if(INPUT_FROM_USER == getInputType())
        {
            return false;
        }
        //template não altera o valor colocado pelas formulas
        if(INPUT_FROM_TEMPLATE == inputType && INPUT_FROM_INTERNAL == getInputType())
        {
            return false;
        }
        return true;
    }

    //to Override
    public boolean validate() throws boRuntimeException
    {
        boolean ret = true;
        boDefXeoCode code = p_bodefatt.getValid();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addVariableString( "message", null );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this );
                eval.eval();
                ret = eval.getReturnBoolean();
                if( !ret )
                {
                    this.getParent().addErrorMessage( this, String.valueOf( eval.getVariable("message") ) );
                }
            }
        }
        if (!p_isvalid)
        	return false;
        return ret;
    }
    public boolean required() throws boRuntimeException
    {
        boolean ret = false;
        boDefXeoCode code = p_bodefatt.getRequired();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this );
                eval.eval();
                ret = eval.getReturnBoolean();
            }
            else
            {
                return code.getBooleanValue();
            }
        }
        return ret;
    }
    public boolean disableWhen() throws boRuntimeException
    {
        boolean ret = false;
        boDefXeoCode code = p_bodefatt.getDisableWhen();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this );
                eval.eval();
                ret = eval.getReturnBoolean();
            }
            else
            {
                return code.getBooleanValue();
            }
        }
        return ret;
    }
    public boolean hiddenWhen() throws boRuntimeException
    {
        boolean ret = false;
        boDefXeoCode code = p_bodefatt.getHiddenWhen();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this );
                eval.eval();
                ret = eval.getReturnBoolean();
            }
            else
            {
                return code.getBooleanValue();
            }
        }
        return ret;
    }

    public boolean canChangeLov() throws boRuntimeException
    {
        boolean ret = false;
        boDefXeoCode code = p_bodefatt.getLovEditable();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this );
                eval.eval();
                ret = eval.getReturnBoolean();
            }
            else
            {
                return code.getBooleanValue();
            }
        }
        return ret;
    }

    public String defaultValue() throws boRuntimeException
    {
        String ret = null;
        boDefXeoCode code = p_bodefatt.getDefaultValue();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this );
                Object evalRet = eval.eval();
                if( evalRet != null )
                {
                    ret = String.valueOf( evalRet );
                }
            }
        }
        return ret;
    }

    public String formula() throws boRuntimeException
    {
        String ret = null;
        boDefXeoCode code = p_bodefatt.getFormula();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this );
                Object evalRet      = eval.eval();
                if( evalRet != null )
                {
                    ret = String.valueOf( evalRet );
                }
            }
        }
        return ret;
    }

    public String[] condition() throws boRuntimeException
    {
        //TODO:Lov Condition
        return null;
    }

    public boolean haveDefaultValue()
    {
        boDefXeoCode code = p_bodefatt.getDefaultValue();
        if( code != null )
        {
            return true;
        }
        return false;
    }
    
    /**
     * 
     * Checks whether or not this Attribute has a valid boObject
     * as a value
     * 
     * @return True if the attribute has a valid boObject as value false otherwise
     */
    public boolean hasObject() throws boRuntimeException {
			return this.getValueLong() > 0;
    }
    
    /**
     * 
     * Checks whether the value of the attribute is null or not
     * 
     * @return True if the value is null and false otherwise
     */
    public boolean isValueNull() throws boRuntimeException {
			return getValueObject() == null;
    }

    
    
    
}
