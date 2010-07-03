package netgest.bo.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netgest.bo.data.DataManager;
import netgest.bo.data.DataResultSet;
import netgest.bo.data.DataRow;
import netgest.bo.data.DataSet;
import netgest.bo.data.IXEODataManager;
import netgest.bo.data.ObjectDataManager;
import netgest.bo.data.WriterException;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefClsEvents;
import netgest.bo.def.boDefForwardObject;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefMethod;
import netgest.bo.def.boDefViewer;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.dochtml.HtmlField;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.viewerImpl.ObjectViewer;
import netgest.bo.ejb.boManagerLocal;
import netgest.bo.ejb.impl.boManagerBean;
import netgest.bo.ejb.impl.boSecurityManagerBean;
import netgest.bo.impl.Ebo_TemplateImpl;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.impl.templates.boTemplateManager;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.plugins.DataPluginManager;
import netgest.bo.plugins.IDataManager;
import netgest.bo.plugins.IDataPlugin;
import netgest.bo.runtime.specific.ObjectBinary;
import netgest.bo.runtime.specific.ObjectRes;
import netgest.bo.runtime.specific.ObjectVersionControl;
import netgest.bo.security.securityOPL;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.transformers.CastInterface;
import netgest.io.BasiciFile;
import netgest.io.DBiFile;
import netgest.io.FSiFile;
import netgest.io.iFile;
import netgest.io.iFilePermissionDenied;
import netgest.io.iFileServer;
import netgest.utils.ParametersHandler;
import netgest.utils.ngtXMLHandler;
import netgest.xwf.common.xwfActionHelper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public abstract class boObject extends boObjectContainer implements Serializable {
	
    private static final String CARD_NAO = "Não";
    private static final String CARD_SIM = "Sim";
    private static final String CARD_Y = "Y";

    public static final byte MODE_NEW = 0; // Modes of the Object
    public static final byte MODE_VIEW = 1;
    public static final byte MODE_EDIT = 2;
    public static final byte MODE_EDIT_TEMPLATE = 3;
    public static final byte MODE_DESTROY = 4;
    public static final byte MODE_MAKE_REQUEST = 5;
    public static final byte STATE_VIEW = 1; // States of the Object
    public static final byte STATE_ALTER = 2;
    public static final byte STATE_HIDDEN = 4;
    public static final byte STATE_DISABLED = 8;
    public static final byte STATE_RIGHTS_GRANTED = 0; // Result of security rules applied
    public static final byte STATE_RIGHTS_DENIED = 0;
    
    public static final byte READYSTATE_CREATING = 1;
    public static final byte READYSTATE_LOADING = 2;
    public static final byte READYSTATE_COMPLETE = 3;
    public static final byte UPDATESTATUS_IDLE = 0;
    public static final byte UPDATESTATUS_UPDATING = 1;
    public static final byte UPDATESTATUS_UPDATING_REFERENCES = 2;
    public static final byte UPDATESTATUS_WAITING_ENDTRANSACTION = 3;
    public static final byte UPDATESTATUS_WAITING_COMMIT = 4;

    // WARNING -- IMPORTANT !!!!!
    // WHEN A NEW FIELD IS ADDED, PLEASE CHECK THE CLASS boObjectSavePoint TO CREATE THE SAVEPOINT AND THE ROLLBACK CODE
    public long bo_boui; // BOUI of the object
    public String bo_name; // Name of the object
    public String bo_version; // Version of the object
    public String bo_classregboui; // BOUI of Ebo_ClsReg
    public int bo_major_version; // Major Version
    public int bo_minor_version; // Minor Version
    public boDefHandler bo_definition; // Definition of the current object
    public boObjectState bo_statemanager; // Manager for states;
    protected boolean p_clone = false; // Mode of the Object
    protected byte p_mode; // Mode of the Object
    public boBridgeRow p_parentBridgeRow; // Array of bridges
    public boolean p_exists; // Variable that tells if the object already exists in persistent store.
    public boolean p_forceCheck = false; // Variable that tells if the docHtml will force the check errors
    protected byte p_state; // Current state of the object

    //protected String p_viewerName = "general"; // a ser trabalhado;
    protected long p_template; // Template used for this object

    protected boBridgesArray 	p_bridges 		= new boBridgesArray(); // Array with the bridges of the object
    protected boAttributesArray p_attributes 	= new boAttributesArray(); // Array with attributes outside of briges
    protected boAttributesArray p_states 		= new boAttributesArray(); // Array with all State Attributes
    protected ParametersHandler p_parameters 	= new ParametersHandler(); // User defined parameters.
    
    private boolean p_ischanged = false; // Variable to save if the object was changed
    protected ArrayList p_errors; // to Store object error messages;
    protected Hashtable p_atterrors; // to Store attributes error messages;
    protected Hashtable p_attadvertisemsgs; // to Store advertise messages to the attributes;
    protected ArrayList p_advertisemsgs; // to Store advertise messages to the object;
    protected boObjectUpdateQueue p_updatequeue;
    protected boObjectSavePoint p_savepoint; // Store the save point of the object when transaction begins
    protected DataRow p_bodata; // Current row of the object
    protected DataSet p_dataSet; // Current DataSet used
    protected boolean p_forceInstanceUnread = false;
    private boolean checkSecurity = false;
    protected boolean versioning = true; //if the object implements versioning
    private boolean p_mustCast = false;
    public byte p_readystate = READYSTATE_COMPLETE;
    private byte isInOnSave = 0;

    public boolean isEnabled = true;
    public boolean isEnabledforRequest = true;
    public boolean isOkToSave = true;
    public boolean wasSerialChecked = false;//for xwf Serialization control only

	private String sendRedirect = null;

    private boolean     haveObjectErrors = false;
    private boolean     haveAttributeErrors = false;
    private ArrayList   p_dependencesFields = null;

    public ObjectViewer p_viewerutils;

    private ObjectBinary            objectBinary = null;
    private ObjectRes               objectRes = null;
    private ObjectVersionControl    objectVersionControl = null;

    protected ArrayList p_eventListeners = null;
	
	private boolean  myTransaction = false;

	//logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.boObject");

    public boObject() {
        super(null);
    }

    public boObjectState getStateManager() {
        return bo_statemanager;
    }

    public void set_IsInOnSave( byte status)
     {
         isInOnSave = status;
        // logger.finest(this.getName() + " IS in ONSAVE = "+status );
     }

    public byte get_IsInOnSave( )
     {
         return isInOnSave;
         //logger.finest(this.getName() + " IS in ONSAVE = "+status );
     }
    public void bindRemoteData(DataSet data, long boui)
        throws boRuntimeException {
        // Sub Class Overwrited
    }

    public void init() throws boRuntimeException {
        if (p_dataSet.getRowCount() > 1) {
            String[] args = { bo_name, "BOUI" };
            throw new boRuntimeException(this,
                this.getClass().getName() + ".init()", "BO-3017", null, args);
        }

        if (p_dataSet.getRowCount() == 0) {
            p_bodata = p_dataSet.createRow();
            p_dataSet.insertRow(p_bodata);
            p_exists = false;
        } else {
            p_bodata = p_dataSet.rows(1);
            p_exists = !p_dataSet.rows(1).isNew();
        }

        bo_boui = p_bodata.getLong("BOUI");


        // Add dependes fields for the object.
        String[] dependences = addDefaultDependencesFields();
        if( dependences != null )
        {
            p_dependencesFields = new ArrayList(1);
            p_dependencesFields.addAll( Arrays.asList( dependences ) );
        }

        // Sub Class Overwrited
    }

    public void bindObject(DataSet dataSet) throws boRuntimeException {
        p_dataSet = dataSet;
        init();
    }

    public void transactionBegins() throws boRuntimeException {
        if (p_savepoint == null) {
            p_savepoint = boObjectSavePoint.createSavePoint("transaction", this);
        }
    }

    public void transactionEnds(boolean commited) throws boRuntimeException {
        if (commited && (p_savepoint != null)) {
            // Clear the update queue and restore the bridges position
        	if (isInOnSave != boObject.UPDATESTATUS_IDLE) {
        		if( p_savepoint != null ) {
        			p_savepoint.commit();
        		}
                if( p_updatequeue != null )
                {
                    p_updatequeue.clear();
                }
                setChanged(false);
            }
        	
        } else if (!commited && (p_savepoint != null)) {
            p_savepoint.rollbackObject();
        }
        isInOnSave = boObject.UPDATESTATUS_IDLE; //para garantir que o objecto fica marcada fora da transaçao
        p_savepoint = null;
    }

    public boolean haveReferencesToObject_in_Memory(long referencedBoui)
        throws boRuntimeException {
        boolean found = false;

        Enumeration oEnum = this.getAllAttributes().elements();

        while (oEnum.hasMoreElements() && !found) {
            AttributeHandler at = (AttributeHandler) oEnum.nextElement();

            if (at.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                // a 2ª parte do if é por causa dos não orfaos
                if ((at.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1) ||
                        at instanceof BridgeObjAttributeHandler) {
                    long boui = at.getValueLong();

                    if (boui == referencedBoui) {
                        found = true;
                    }
                } else {
                    long[] bouis = at.getValuesLong();

                    if (bouis != null) {
                        for (int i = 0; i < bouis.length; i++) {
                            if (referencedBoui == bouis[i]) {
                                found = true;
                            }
                        }
                    }
                }
            }
        }

        return found;
    }

    /* Static methods to Create a new Instance of Object */
    public static boManagerLocal getBoManager() throws boRuntimeException {
//        try {
            //return ((boManagerLocalHome) boContextFactory.getContext().lookup("java:comp/env/ejb/boManagerLocal")).create();
            return new boManagerBean();
//        } catch (NamingException e) {
//            throw new boRuntimeException2(e.getMessage());
//        } catch (CreateException e) {
//            throw new boRuntimeException2(e.getMessage());
//        }
    }

    public static boManagerLocal getBoSecurityManager()
        throws boRuntimeException {
            return new boSecurityManagerBean();
//        try {
//            return ((boManagerLocalHome) boContextFactory.getContext().lookup("java:comp/env/ejb/boSecurityManagerLocal")).create();
//        } catch (NamingException e) {
//            throw new boRuntimeException2(e.getMessage());
//        } catch (CreateException e) {
//            throw new boRuntimeException2(e.getMessage());
//        }
    }

    // End of static methods;
    public void load(String query) throws boRuntimeException {
        load(query, null);
    }

    public void load(String query, ArrayList args) throws boRuntimeException
    {

        IDataPlugin[] plugins = DataPluginManager.getPlugIns();
        for (int i = 0; i < plugins.length; i++)
        {
            IDataManager xplugin = plugins[i].getDataManager( getBoDefinition() );
            if( xplugin != null )
            {
                xplugin.beforeObjectLoad( this );
            }
        }

        if (
        	getEboContext().isInModeBatch(EboContext.MODE_BATCH_EXECUTE_BEFORELOAD_EVENT) ||
        	this.onBeforeLoad(new boEvent(this, "onBeforeLoad", query))) 
        {

            p_dataSet = ObjectDataManager.executeNativeQuery(super.getEboContext(),
                    bo_definition, query, args);

        }

        p_mode = boObject.MODE_EDIT;
        init();


        for (int i = 0; i < plugins.length; i++)
        {
            IDataManager xplugin = plugins[i].getDataManager( getBoDefinition() );
            if( xplugin != null )
            {
                xplugin.afterObjectLoad( this );
            }
        }
        
        if( getEboContext().isInModeBatch( EboContext.MODE_BATCH_EXECUTE_AFTERLOAD_EVENT ) )
        {
	        this.onAfterLoad(new boEvent(this, "onAfterLoad",
	                p_dataSet.rows(1).getBigDecimal("BOUI")));
	        if (bo_statemanager != null) {
	            bo_statemanager.fireEventOnLoad(this);
	        }
        }
    }

    public void load(DataSet data) throws boRuntimeException {

        IDataPlugin[] plugins = DataPluginManager.getPlugIns();
        for (int i = 0; i < plugins.length; i++)
        {
            IDataManager xplugin = plugins[i].getDataManager( getBoDefinition() );
            if( xplugin != null )
            {
                xplugin.beforeObjectLoad( this );
            }
        }


        if (
        	getEboContext().isInModeBatch(EboContext.MODE_BATCH_EXECUTE_BEFORELOAD_EVENT) ||
        	this.onBeforeLoad(new boEvent(this, "onBeforeLoad", data))) 
        {
            p_dataSet = data;
        }


        p_mode = boObject.MODE_EDIT;
        init();


        for (int i = 0; i < plugins.length; i++)
        {
            IDataManager xplugin = plugins[i].getDataManager( getBoDefinition() );
            if( xplugin != null )
            {
                xplugin.afterObjectLoad( this );
            }
        }

        if( getEboContext().isInModeBatch( EboContext.MODE_BATCH_EXECUTE_AFTERLOAD_EVENT ) )
        {
	        this.onAfterLoad(new boEvent(this, "onAfterLoad",
	                p_dataSet.rows(1).getBigDecimal("BOUI")));
	
	        if (bo_statemanager != null) {
	            bo_statemanager.fireEventOnLoad(this);
	        }
        }
    }
    
    /**
     * 
     * Retrieves a copy of the current object with the values that are stored in the database
     * It's used when there's the need to know the differences between an object being edited
     * in a form/viewer and the object currently saved in the database
     * 
     * IMPORTANT: Do not use this object to make updates/save
     * 
     * @return A copy of the current object in which the attributes have the values that are
     * saved in the database, or null if the object was not changed
     * 
     * @throws boRuntimeException If a problem occurs while creating the copy (in clone) 
     */
    public boFlashBackHandler getFlashBackHandler() throws boRuntimeException
    {
    	return new boFlashBackHandler(this);
    }
    
   
    public void load(long xboui) throws boRuntimeException {
        ArrayList args = new ArrayList(1);
        args.add(BigDecimal.valueOf(xboui));
        load("BOUI=?", args);
    }

    public void create(long boui) throws boRuntimeException {
        create(boui, false);
    }

    public void create(long boui, boolean modeEditTemplate)
        throws boRuntimeException {
        create(boui, modeEditTemplate, null);
    }

    public void create(long boui, DataSet data) throws boRuntimeException {
        create(boui, false, data);
    }
    public void create(long boui, boolean modeEditTemplate, DataSet data)
        throws boRuntimeException {
        create(boui,modeEditTemplate,data,-1);
    }

    public void create(long boui, boolean modeEditTemplate, DataSet data,long parent)
        throws boRuntimeException {
        try {
            p_readystate = boObject.READYSTATE_CREATING;

            IDataPlugin[] plugins = DataPluginManager.getPlugIns();
            for (int i = 0; i < plugins.length; i++)
            {
                IDataManager xplugin = plugins[i].getDataManager( getBoDefinition() );
                if( xplugin != null )
                {
                    xplugin.beforeObjectLoad( this );
                }
            }

            if (onBeforeCreate( new boEvent( boEvent.EVENT_BEFORE_DESTROY, this, null ) )) {
                //EFORM this.bo_data = NGTDocumentParser.loadEFORM(this.bo_eformid,"0=1","",super.getEboContext()).getFirstChild();
                if (data == null) {
                	
                	if( this.getBoDefinition().getDataBaseManagerXeoCompatible() )
                	{
	                    this.p_dataSet = ObjectDataManager.getEmptyDataSet(getEboContext(),
	                            bo_definition);
                	}
                	else {
	                    this.p_dataSet = ObjectDataManager.createEmptyObjectDataSet( getEboContext(),
	                            bo_definition);
                	}
                } else {
                    p_clone = true;
                    p_dataSet = data;
                }
            }

            p_mode = boObject.MODE_NEW;

            if (modeEditTemplate) {
                this.setModeEditTemplate();
            }

            if (p_dataSet.getRowCount() == 0) {
                p_bodata = p_dataSet.createRow();
                p_dataSet.insertRow(p_bodata);
            } else {
                p_bodata = p_dataSet.rows(1);
            }

            this.setBoui(boui);

            if( parent > 0 )
            {
                this.addParent( this.getObject ( parent ) );
            }

            init();

            p_exists = false;

            if (!modeEditTemplate) {
                setModeAlter();
            }

            for (int i = 0; i < plugins.length; i++)
            {
                IDataManager xplugin = plugins[i].getDataManager( getBoDefinition() );
                if( xplugin != null )
                {
                    xplugin.afterObjectLoad( this );
                }
            }
            onAfterCreate( new boEvent( boEvent.EVENT_AFTER_CREATE, this, null ) );

            if (bo_statemanager != null) {
                bo_statemanager.fireEventOnCreate(this);
            }

            p_readystate = boObject.READYSTATE_COMPLETE;
        } catch (Exception e) {
        	e.printStackTrace();
            String[] args = { bo_name, (new Long(bo_boui)).toString() };
            throw new boRuntimeException(this,
                this.getClass().getName() + ".load(EboContext,long)",
                "BO-3001", e, args);
        }
    }

    public DataRow getDataRow() {
        return p_bodata;
    }

    public DataSet getDataSet() {
        return p_dataSet;
    }

    public boObjectUpdateQueue getUpdateQueue() {
        if (p_updatequeue == null) {
            p_updatequeue = new boObjectUpdateQueue();
        }

        return p_updatequeue;
    }

    public void setUpdateMode(byte mode) {
        this.p_mode = mode;
    }

    public void destroy() throws boRuntimeException {
        clearErrors();
        
    	boolean checkSec = isCheckSecurity();
    	try {
    		setCheckSecurity( false );
        
	        byte lmode = this.p_mode;
	
	        try {
	            this.p_mode = 4;
	            boObject.getBoManager().updateObject(this.getEboContext(), this);
	        } finally {
	            this.p_mode = lmode;
	        }
        }
        finally {
    		setCheckSecurity( checkSec );
        }
    }

    public void destroyForce() throws boRuntimeException {
        clearErrors();
        byte lmode = this.p_mode;
        try {
            this.p_mode = 4;
            onBeforeDestroy(new boEvent( boEvent.EVENT_BEFORE_DESTROY, this, null ));
            boObject.getBoManager().destroyForced(this.getEboContext(), this);
            onAfterDestroy(new boEvent( boEvent.EVENT_AFTER_DESTROY, this, null ));
            //            this.p_exists = !p_bodata.isNew();
        } finally {
            this.p_mode = lmode;
        }
    }


    public void update() throws boRuntimeException {
        update(true, false);
    }

    public void update(boolean runEvents, boolean forceAllInTransaction)
        throws boRuntimeException {
    	boolean checkSec = isCheckSecurity();
    	try {
    		setCheckSecurity( false );
    	
	        if(this.getBoDefinition().haveVersionControl() && getObjectVersionControl().canCheckIn())
	        {
	            this.getObjectVersionControl().updateCheckOut();
	            this.p_ischanged = false;
	        }
	        else
	        {
	            if (!this.checkSecurity) {
	                boObject.getBoManager().updateObject(super.getEboContext(), this,
	                    runEvents, forceAllInTransaction);
	            } else {
	                boObject.getBoSecurityManager().updateObject(super.getEboContext(),
	                    this, runEvents, forceAllInTransaction);
	            }
	        }
        
    	} finally {
    		setCheckSecurity( checkSec );
    	}
        
    }

    public boolean doWorkBeforeUpdate(boolean runEvent,
        boolean forceAllInTransaction) throws boRuntimeException {
        boolean cont = true;

        if ((this.getAttribute("CREATOR") != null) &&
                (this.getAttribute("CREATOR").getValueObject() == null) &&
                (this.getAttribute("CREATOR").getInputType() != AttributeHandler.INPUT_FROM_USER) &&
                (getEboContext().getBoSession().getPerformerBoui() != 1) &&
                (getEboContext().getBoSession().getPerformerBoui() != 0)) {
            this.getAttribute("CREATOR").setValueLong(getEboContext()
                                                          .getBoSession()
                                                          .getPerformerBoui(), AttributeHandler.INPUT_FROM_INTERNAL );
        }

        if(this.getAttribute("SYS_FROMOBJ") != null && this.getAttribute("SYS_FROMOBJ").getValueObject() != null)
        {
              onSaveFwdObject(null);
        }
        if (p_mode != MODE_DESTROY) {
            processTemplate();
        }

        if (runEvent && !forceAllInTransaction) {
        	
        	if( getEboContext().isInModeBatch( EboContext.MODE_BATCH_EXECUTE_BEFORESAVE_EVENT ) ) {
	            if (p_mode == MODE_DESTROY)
	            {
	                cont = cont && this.onBeforeDestroy(new boEvent( boEvent.EVENT_BEFORE_DESTROY, this, null ));
	            }
	            else
	            {
	                cont = cont && this.onBeforeSave(new boEvent( boEvent.EVENT_BEFORE_SAVE, this, null )) && boTemplateManager.doTemplateAction(this);
	            }

	            if (bo_statemanager != null) {
	                if (p_mode == MODE_DESTROY) {
	                    bo_statemanager.fireEventOnDestroy(this);
	                } else {
	                    bo_statemanager.fireEventOnSave(this);
	                }
	            }
        	}
        }
        if (cont && ((p_mode == MODE_DESTROY) || valid())) {
            //computeSecurityKeys(); estão a ser calculadas no boManager
            // Update AttributeInputTypes in localFields
            boolean markInputType = this.getBoDefinition().getBoMarkInputType();
            if(markInputType)
            {
                boObjectUtils.updateAttributeInputType(p_bodata,
                    p_attributes.elements(), p_attributes.p_attributes.size());
            }
            boObjectUtils.updateSequenceAttributes(this,
                p_attributes.elements(), false);

            // Update AttributeInputTypes in localFields
            Enumeration benum = p_bridges.elements();

            while (benum.hasMoreElements()) {
                bridgeHandler bh = (bridgeHandler) benum.nextElement();

                if (!(bh instanceof bridgeReverseHandler ||
                        bh instanceof ObjectMultiValues)) {
                    boBridgeIterator it = bh.iterator();

                    while (it.next()) {
                        if(markInputType)
                        {
                            boObjectUtils.updateAttributeInputType(it.currentRow()
                                                                     .getDataRow(),
                                it.currentRow().getLineAttributes().elements(),
                                it.currentRow().getLineAttributes().p_attributes.size(),
                                true);
                        }
                        boObjectUtils.updateSequenceAttributes(this,
                            it.currentRow().getLineAttributes().elements(), true);
                    }
                }
            }

            if (this.p_mode == MODE_NEW)
            {
                p_mode = MODE_EDIT;
            }
        } else {
            StringBuffer errors = new StringBuffer();

            if (this.getAttributeErrors() != null) {
                Enumeration list = this.getAttributeErrors().keys();

                if (list != null) {
                    while (list.hasMoreElements()) {
                        AttributeHandler att = (AttributeHandler) list.nextElement();
                        errors.append(att.getDefAttribute().getDescription() +
                            " : " + this.getAttributeErrors().get(att));
                    }
                }
            }

            throw new boRuntimeException(this,
                boObject.class.getName() + ".update()", "BO-3021", null,
                "The errors found are in object [" + this.getName() + ":" +
                this.getBoui() + "] " +
                this.getBoDefinition().getDescription() + " are:\n" + errors);
        }

        return cont;
    }

    /*
        public boolean doWorkBeforeUpdate() throws boRuntimeException
        {
            boolean cont = true;

            if ((this.getAttribute("CREATOR") != null) && (this.getAttribute("CREATOR").getValueObject() == null) &&
                    (getEboContext().getBoSession().getPerformerBoui() != 1) &&
                    (getEboContext().getBoSession().getPerformerBoui() != 0))
            {
                this.getAttribute("CREATOR").setValueLong(getEboContext().getBoSession().getPerformerBoui());
            }

            if (p_mode != MODE_DESTROY)
            {
                processTemplate();
            }

            if (p_mode == MODE_DESTROY)
            {
                cont = cont && this.onBeforeDestroy(null);
            }
            else
            {
                cont = cont && this.onBeforeSave(null);
            }

            if (bo_statemanager != null)
            {
                if (p_mode == MODE_DESTROY)
                {
                    bo_statemanager.fireEventOnDestroy(this);
                }
                else
                {
                    bo_statemanager.fireEventOnSave(this);
                }
            }

            if (cont && ((p_mode == MODE_DESTROY) || valid()))
            {
                //computeSecurityKeys(); estão a ser calculadas no boManager
                // Update AttributeInputTypes in localFields

                boObjectUtils.updateAttributeInputType( p_bodata, p_attributes.elements(), p_attributes.p_attributes.size() );
                boObjectUtils.updateSequenceAttributes( this , p_attributes.elements(), false );


                // Update AttributeInputTypes in localFields
                Enumeration benum = p_bridges.elements();
                while( benum.hasMoreElements() )
                {
                    bridgeHandler bh = (bridgeHandler)benum.nextElement();
                    if( !(bh instanceof bridgeReverseHandler ) )
                    {
                        boBridgeIterator it = bh.iterator();
                        while( it.next() )
                        {
                            boObjectUtils.updateAttributeInputType( it.currentRow().getDataRow() , it.currentRow().getLineAttributes().elements() , it.currentRow().getLineAttributes().p_attributes.size(), true );
                            boObjectUtils.updateSequenceAttributes( this , it.currentRow().getLineAttributes().elements() , true );

                        }

                    }
                }

                if (this.p_mode == MODE_NEW)
                {
                    p_mode = MODE_EDIT;
                }
            }
            else
            {
                StringBuffer errors = new StringBuffer();

                if (this.getAttributeErrors() != null)
                {
                    Enumeration list = this.getAttributeErrors().keys();

                    if (list != null)
                    {
                        while (list.hasMoreElements())
                        {
                            AttributeHandler att = (AttributeHandler) list.nextElement();
                            errors.append(att.getDefAttribute().getDescription() + " : " + this.getAttributeErrors().get(att));
                        }
                    }
                }

                throw new boRuntimeException(this, boObject.class.getName() + ".update()", "BO-3021", null,
                    "The errors found are in object [" + this.getName() + ":" + this.getBoui() + "] " + this.getBoDefinition().getDescription() + " are:\n" +
                    errors);
            }

            return cont;
        }
    */
    public void deleteBridgesAndObjectAttributes() throws boRuntimeException {
        boDefAttribute[] atts = bo_definition.getBoAttributes();

        for (int i = 0; i < atts.length; i++) {
            if (atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                String attname = atts[i].getName();

                if (atts[i].getMaxOccurs() > 1) {
                    bridgeHandler bridge = getBridge(attname);

                    if (bridge != null) {
                        bridge.first();

                        int bcnt = bridge.getRowCount();
                        for (int z = 0; z < bcnt; z++) {
                        	bridge.getObject();
                        }
                        for (int z = 0; z < bcnt; z++) {
                        	bridge.last();
                            bridge.remove();
                        }
                    }
                } else {
                    AttributeHandler att = getAttribute(attname);

                    if (att != null) {
                        att.setValueObject(null);
                    }
                }
            }
        }
    }

    public void doWorkAfterUpdate(boolean runEvent,
        boolean forceAllInTransaction) throws boRuntimeException {
        //        p_exists = true;
        if(!this.exists())
        {
            if(boDefHandler.getBoDefinition("xwfStarterConfig") != null)
            {
                xwfActionHelper.autoQueueStartProgram(this);
            }
        }
        if (runEvent && !forceAllInTransaction) {
        	if( getEboContext().isInModeBatch( EboContext.MODE_BATCH_EXECUTE_AFTERSAVE_EVENT ) ) {
	            if (p_mode == MODE_DESTROY)
	            {
	                onAfterDestroy(new boEvent( boEvent.EVENT_AFTER_DESTROY, this, null ));
	            }
	            else
	            {
	                onAfterSave(new boEvent( boEvent.EVENT_AFTER_SAVE, this, null ));
	
	            }
        	}
        }
    }

    /*

        public void doWorkAfterUpdate() throws boRuntimeException
        {
    //        p_exists = true;

            if (p_mode == MODE_DESTROY)
            {
                onAfterDestroy(null);
            }
            else
            {
                onAfterSave(null);
            }

    // passou para o COMMI
    //        p_updatequeue.clear();
    //        setChanged(false);
    //
    //        if (this.poolIsStateFull())
    //        {
    //            this.poolUnSetStateFull();
    //        }
        }

    */
    public void setVersioning(boolean state) {
        this.versioning = state;
    }

    public boolean getVersioning() {
        return (versioning && getBoDefinition().getVersioning());
    }

    public void setBoui(long boui) throws boRuntimeException {
        p_bodata.updateLong("BOUI", boui);
        bo_boui = boui;
    }

    public long getBoui() {
        return p_bodata.getLong("BOUI");
    }

    public void addErrorMessage(AttributeHandler att, String message) {
        if (p_atterrors == null) {
            p_atterrors = new Hashtable(1);
        }
		setHaveAttributeErrors(true);
        p_atterrors.put(att, message);
    }

    public void addErrorMessage(String errormessage) {
        if (p_errors == null) {
            p_errors = new ArrayList(1);
        }
		setHaveObjectErrors(true);
        p_errors.add(errormessage);
    }
	public boolean haveErrors()
    {
        return haveObjectErrors() && haveAttributeErrors();
    }
    public boolean haveObjectErrors()
    {
        return haveObjectErrors;
    }
    public boolean haveAttributeErrors()
    {
        return haveAttributeErrors;
    }
    public void setHaveObjectErrors(boolean isInObjectError)
    {
        this.haveObjectErrors = isInObjectError;
    }
    public void setHaveAttributeErrors(boolean isInAttributeError)
    {
        this.haveAttributeErrors = isInAttributeError;
    }
    public Hashtable getAttributeErrors() {
        return p_atterrors;
    }

    public ArrayList getObjectErrors() {
        return p_errors;
    }
    public void clearErrors()
    {
        clearObjectErrors();
        clearAttributeErrors();
    }
    public void clearObjectErrors()
    {
        if (p_errors != null)
        {
            p_errors.clear();
            haveObjectErrors = false;
        }
    }
    public void clearAttributeErrors()
    {
        if (p_atterrors != null)
        {
            p_atterrors.clear();
            haveAttributeErrors = false;
        }
    }
    public void clearAdvertiseMessages()
    {
        if (p_advertisemsgs != null)
        {
            p_advertisemsgs.clear();
        }
    }
    public ArrayList getAdvertiseMessages() {
        return p_advertisemsgs;
    }

    public Hashtable getAdvertiseAttributeMessages() {
        return p_attadvertisemsgs;
    }

    public void addAdvertiseMessage(String message) {
        if (p_advertisemsgs == null) {
            p_advertisemsgs = new ArrayList(1);
        }

        p_advertisemsgs.add(message);
    }

    public void addAdvertiseMessage(AttributeHandler att, String message) {
        if (p_attadvertisemsgs == null) {
            p_attadvertisemsgs = new Hashtable(2);
        }

        p_attadvertisemsgs.put(att, message);
    }

    public void setParentBridgeRow(boBridgeRow bridge) {
        p_parentBridgeRow = bridge;
    }

    //so activar dp
    //    public boBridgeRow getParentBridge()
    //    {
    //        return getParentBridgeRow().getBridge;
    //    }
    public boBridgeRow getParentBridgeRow() {
        //         Big shitt.... cause whe are seving a reference to the object... it must be loaded before use.
        if(p_parentBridgeRow != null)
        {
            boObject xx = p_parentBridgeRow.getBridge().getParent();

            try {
                getBoManager().loadObject(getEboContext(), xx.bo_boui);
                p_parentBridgeRow.getBridge().setEboContext( getEboContext() );
                p_parentBridgeRow.getBridge().getParent().setEboContext( getEboContext() );
            } catch (boRuntimeException e) {
                //
                throw new boRuntimeException2(this.getClass().getName() +
                    "getParentBridgeRow() error.\n" + e.getClass().getName() +
                    "\n" + e.getMessage());
            }
        }
        return p_parentBridgeRow;
    }

    public String getName() {
        return this.bo_name;
    }

    //    public String getViewerName()
    //    {
    //        return p_viewerName;
    //    }
    public boolean valid() throws boRuntimeException {
    	if( getEboContext().isInModeBatch( EboContext.MODE_BATCH_VALIDATE_OBJECTS ) ) {
    		boolean chkSec = isCheckSecurity();
    		try {
    			setCheckSecurity( false );
		        clearErrors();
		        
		        Enumeration br = this.getBridges().elements();
		        boolean ret = true;
		
		        while (br.hasMoreElements()) {
		            ret = ret && ((bridgeHandler) br.nextElement()).valid();
		        }
		
		        br = this.getAttributes().elements();
		
		        while (br.hasMoreElements()) {
		            AttributeHandler att = (AttributeHandler) br.nextElement();
		
		            if (!att.valid()) {
		                this.addErrorMessage(att, att.getErrorMessage());
		                ret = false;
		            } else if (!att.validate()) {
		                ret = false;
		            }
		        }
		
		        if (bo_statemanager != null) {
		            ret = ret && bo_statemanager.fireEventOnValid(this);
		        }
		        return ret;
    		} finally {
    			setCheckSecurity( chkSec );
    		}
    	}
    	return true;
    }

    public boolean exists() throws boRuntimeException {
        return p_exists;
    }

    public void getActionMethods(Node xnode) {
        return;
    }

    public boObjectList referencedByReadOnly(String boql)
        throws boRuntimeException {
        boObjectList list = referencedBy(boql);
        list.setReadOnly(true);

        return list;
    }

    public boObjectList referencedBy(String boql) throws boRuntimeException {
        boql = boql.replaceAll("this", this.getBoui() + "");

        return boObjectList.list(getEboContext(), boql);

        /*
        if(boql.startsWith("BOQL"))
        {
          boql = boql.replaceAll("this",this.getBoui()+"").replaceFirst("BOQL ","");
          return boObjectList.list(getEboContext(),boql);
        }

        String upperBoql = boql.toUpperCase();
        String object;
        String clause;
        String[] pathToObject = null;
        String[] pathToClause;
        boObjectList list;

        if (upperBoql.indexOf("WHERE") == -1)
        {
            bridgeHandler bh = referencedBy();
            object = (boql.substring(upperBoql.indexOf("SELECT") + 6)).trim();

            if (!"*".equals(object))
            {
                pathToObject = netgest.utils.ClassUtils.splitToArray(object, ".");
            }

            list = referencedBy();
        }
        else
        {
            object = (boql.substring(upperBoql.indexOf("SELECT") + 6, upperBoql.indexOf("WHERE"))).trim();
            clause = (boql.substring(upperBoql.indexOf("WHERE") + 5)).trim();

            pathToObject = netgest.utils.ClassUtils.splitToArray(object, ".");
            pathToClause = netgest.utils.ClassUtils.splitToArray(clause, ".");

            pathToClause[0] = pathToObject[0];

            String[] path = new String[pathToObject.length];
            StringBuffer sb = new StringBuffer();
            sb.append(pathToObject[0]);

            for (int i = 1; i < pathToClause.length; i++)
            {
                sb.append(".");

                if ((i + 1) == pathToClause.length)
                {
                    if (pathToClause[i].indexOf("=") != -1)
                    {
                        sb.append(pathToClause[i].substring(0, pathToClause[i].indexOf("=")).trim());
                    }
                }
                else
                {
                    sb.append(pathToClause[i]);
                }
            }

            list = referencedByCaller(sb.toString());
        }

        if ((pathToObject != null) && (pathToObject.length > 1))
        {
            return getObjects(pathToObject[0], list, pathToObject, 1);
        }

        return list;
        }

        private boObjectList getObjects(String objType, boObjectList list, String[] objPath, int pos)
        throws boRuntimeException
        {
        list.beforeFirst();

        boObject aux;
        boObject aux2;
        String type = null;
        ArrayList bouiList = new ArrayList();
        boDefHandler bobj = boDefHandler.getBoDefinition(objType);
        boDefAttribute[] boDefAtt = bobj.getBoAttributes();
        boolean found = false;

        for (int j = 0; (j < boDefAtt.length) && !found; j++)
        {
            if (boDefAtt[j].getName().equals(objPath[pos]))
            {
                type = boDefAtt[j].getType();
                found = true;
            }
        }

        long recordCount = list.getRecordCount();

        for (int i = 0; i < recordCount; i++)
        {
            aux = list.getObject();

            AttributeHandler att = aux.getAttribute(objPath[pos]);

            if (att != null)
            {
                if (att.getDefAttribute().getRelationType() == att.getDefAttribute().RELATION_1_TO_1)
                {
                    if (att.getObject() != null)
                    {
                        bouiList.add(new Long(att.getObject().getBoui()));
                    }
                }
                else
                {
                    bridgeHandler bh = aux.getBridge(objPath[pos]);

                    if ((bh != null) && !bh.haveVL())
                    {
                        bh.beforeFirst();

                        while (bh.next())
                        {
                            aux2 = bh.getObject();

                            if (aux != null)
                            {
                                bouiList.add(new Long(aux2.getBoui()));
                            }
                        }
                    }
                }
            }
        }

        if ((pos + 1) == objPath.length)
        {
            if (type.indexOf("object") != -1)
            {
                type = type.substring(7);
            }

            if (bouiList.size() > 0)
            {
                return boObjectList.list(getEboContext(), type, toLong(bouiList));
            }
            else
            {
                return boObjectList.list(getEboContext(), "select " + type + " where 0=1");
            }
        }
        else
        {
            return getObjects(type, boObjectList.list(getEboContext(), type, toLong(bouiList)), objPath, pos + 1);
        }
        */
    }

    public String toString()
    {
      return Long.toString(getBoui());
    }

    public bridgeHandler referencedBy(String objectname, String attname)
        throws boRuntimeException {
        bridgeHandler ret;
        String bridgename = "dynbrige_" + objectname;

        if (objectname.equalsIgnoreCase("boObject")) {
            ret = bridgeReverseHandler.create(bridgename, this, objectname,
                    attname);
        } else {
            ret = p_bridges.get(bridgename);

            if (ret == null) {
                ret = bridgeReverseHandler.create(bridgename, this, objectname,
                        attname);
                p_bridges.add(ret);

                boBridgeMasterAttribute ma = new boDynBridgeMasterAttribute(this,
                        ret.getDefAttribute(), bridgename);
                p_attributes.add(ma);
                this.poolSetStateFull();
            }
        }

        return ret;
    }

    public bridgeHandler referencedBy() throws boRuntimeException {
        return referencedBy("boObject", null);
    }

    public boDefHandler getBoDefinition() {
        return bo_definition;
    }

    public iFile getiFile(String file) {
            /*novo para retirar*/
        iFile result = null;
        if(file != null)
        {
            if(file.startsWith("//"+DBiFile.IFILE_SERVICE_NAME) || file.startsWith("//"+BasiciFile.IFILE_SERVICE_NAME))
            {
//                iFileServer fs = this.getiFileServer();
                iFileServer fs = new iFileServer();
                fs.mount();
                result = fs.getFile(file);
            }
/*
 * 
 *  LUSITANIA - ApplicationXtender
 
            //psantos ini
            else if (file.startsWith(("//"+AppxiFile.IFILE_SERVICE_NAME)))
            {
                AppxiFileProvider service = new AppxiFileProvider();
                //file entra com //appx/sinistro/12345/1
                // temp fica com sinistro/12345/1
                String temp = file.substring(2+AppxiFile.IFILE_SERVICE_NAME.length()+1, file.length());
                // app fica com sinistro
                String app = temp.substring(0,temp.indexOf('/'));
                service.open(app,"imagem");
                return new AppxiFile(service,file);
            }
            //psantos fim
*/
            else if(file.indexOf(File.separator) > -1 || file.indexOf("//") > -1 )
            {
                result=new FSiFile(null,file,file);
            }
            else if (file.length()>0)
            {
                result=new FSiFile(null,new File(DocumentHelper.getTempDir().concat(File.separator).concat(file)),null);
            }
        }
        return result;

            /*novo*/
//        return null;
    }

    // Inner Classes
    public boObject getParent() throws boRuntimeException {
        if (!bo_definition.getBoHaveMultiParent()) {
            return (this.getAttribute("PARENT").getValueObject() != null)
            ? this.getAttribute("PARENT").getObject() : null;
        }

        return null;
    }

    public boObject[] getParents() throws boRuntimeException {
        if (this.getAttribute("PARENT").getValueObject() != null) {
            if (!this.bo_definition.getBoHaveMultiParent()) {
                if (this.getAttribute("PARENT").getValueObject() != null) {
                    return new boObject[] {
                        getObject(((BigDecimal) this.getAttribute("PARENT")
                                                    .getValueObject()).longValue())
                    };
                }
            } else {
                BigDecimal[] refs = (BigDecimal[]) this.getAttribute("PARENT")
                                                       .getValueObject();
                boObject[] obj = new boObject[refs.length];

                for (short i = 0; i < refs.length; i++) {
                    obj[i] = getObject(refs[i].longValue());
                }

                return obj;
            }
        }

        return new boObject[0];
    }

    public boObject getObject(long boui) throws boRuntimeException {
        boObject ret = super.getObject(boui);
        getThread().add(BigDecimal.valueOf(getBoui()), new BigDecimal(boui));

        return ret;
    }

    public void removeParent(boObject parent, boolean orphanRelation)
        throws boRuntimeException {
        if (!this.getName().equalsIgnoreCase("Ebo_Template") &&
                !this.getName().equalsIgnoreCase("Ebo_TextIndex")) {
            //parent.getUpdateQueue().add(this, boObjectUpdateQueue.MODE_SAVE_FORCED);
            if (!bo_definition.getBoHaveMultiParent()) {
                this.getAttribute("PARENT").setValueObject(null);

                //                if ( !( orphanRelation || bo_definition.getBoCanBeOrphan()) && this.exists()  )
                //                {
                //                    parent.getUpdateQueue().add(this, boObjectUpdateQueue.MODE_DESTROY);
                //                }
            } else {
                BigDecimal[] oldv = (BigDecimal[]) this.getAttribute("PARENT")
                                                       .getValueObject();

                if (oldv != null) {
                    short i;

                    for (i = 0; i < oldv.length; i++) {
                        if (oldv[i] != null && oldv[i].longValue() == parent.getBoui()) {
                            break;
                        }
                    }

                    if (i < oldv.length) {
                        if (oldv.length > 1) {
                            BigDecimal[] newv = new BigDecimal[oldv.length - 1];

                            if (i > 0) {
                                System.arraycopy(oldv, 0, newv, 0, i);
                            }

                            if (i == (oldv.length - 1)) {
                                System.arraycopy(oldv, i, newv, i,
                                    newv.length - i);
                            }

                            this.getAttribute("PARENT").setValueObject(newv);
                        } else {
                            this.getAttribute("PARENT").setValueObject(null);

                            //                            if ( !( orphanRelation || bo_definition.getBoCanBeOrphan()) && this.exists()  )
                            //                            {
                            //                                parent.getUpdateQueue().add(this, boObjectUpdateQueue.MODE_DESTROY);
                            //                            }
                            //                            else
                            //                            {
                            //                                parent.getUpdateQueue().add(this, boObjectUpdateQueue.MODE_SAVE);
                            //                            }
                        }
                    }
                } else {
                    //                    if ( !( orphanRelation || bo_definition.getBoCanBeOrphan()) && this.exists()  )
                    //                    {
                    //                        parent.getUpdateQueue().add(this, boObjectUpdateQueue.MODE_DESTROY);
                    //                    }
                }
            }
        }
    }

    public void addParent(boObject parent) throws boRuntimeException {
        if (!(parent.getMode() == MODE_EDIT_TEMPLATE) &&
                !this.getName().equalsIgnoreCase("Ebo_Template") &&
                !this.getName().equalsIgnoreCase("Ebo_TextIndex")) {
            if (!parent.getName().equalsIgnoreCase("Ebo_Template") &&
                    !parent.getName().equalsIgnoreCase("Ebo_TextIndex") &&
                    !parent.getName().equalsIgnoreCase("Ebo_Announce") &&
                    !parent.getName().equalsIgnoreCase("Ebo_AnnounceDetails")) {
                if (!bo_definition.getBoHaveMultiParent()) {
                    if (this.getAttribute("PARENT").getValueLong() != parent.getBoui()) {
                        this.getAttribute("PARENT").setValueLong(parent.getBoui());

                        //                        parent.getUpdateQueue().add(this, boObjectUpdateQueue.MODE_SAVE);
                    }
                } else {
                    BigDecimal[] oldv = (BigDecimal[]) this.getAttribute(
                            "PARENT").getValueObject();

                    if (oldv != null) {
                        short i;

                        for (i = 0; i < oldv.length; i++) {
                            if (oldv[i].longValue() == parent.getBoui()) {
                                break;
                            }
                        }

                        if (i >= oldv.length) {
                            BigDecimal[] newv = new BigDecimal[oldv.length + 1];
                            System.arraycopy(oldv, 0, newv, 0, oldv.length);
                            newv[oldv.length] = new BigDecimal(parent.getBoui());
                            this.getAttribute("PARENT").setValueObject(newv);

                            //                            parent.getUpdateQueue().add(this, boObjectUpdateQueue.MODE_SAVE);
                        }
                    } else {
                        this.getAttribute("PARENT").setValueObject(new BigDecimal[] {
                        		BigDecimal.valueOf(parent.getBoui())
                            });

                        //                        parent.getUpdateQueue().add(this, boObjectUpdateQueue.MODE_SAVE);
                    }
                }
            }
        }
    }

    /**
     * 
     * Returns a {@link boBridgesArray} with all the bridge attributes of the
     * 
     * 
     * @return
     */
    public boBridgesArray getBridges() {
        return p_bridges;
    }

    public bridgeHandler getBridge(String bridgename) {
        bridgeHandler ret = p_bridges.get(bridgename);

        if ((getBoDefinition().getAttributeRef(bridgename) != null) &&
                getBoDefinition().getAttributeRef(bridgename).getDbIsTabled()) {
            ret = new ObjectMultiValues(this,
                    getBoDefinition().getAttributeRef(bridgename),
                    new DataResultSet(this.p_bodata.getRecordChild(
                            getEboContext(), bridgename)));
            p_bridges.add(ret);
        }

        return ret;
    }

    public DataSet getBridgeDataSet( String bridgeName ) {
    	try {
			DataSet ret = null;
			if( !bo_definition.getDataBaseManagerXeoCompatible() ) {
				
				IXEODataManager dataManager = getEboContext().getApplication().getXEODataManager( bo_definition );
				ret = ObjectDataManager.createEmptyObjectBridgeDataSet(getEboContext(),
						bo_definition, 
						bo_definition.getAttributeRef( bridgeName )
					); 
 
				dataManager.fillBridgeDataSet( 
						getEboContext(), 
						ret,
						this,
						bo_definition.getAttributeRef(  bridgeName ) 
					);
				
				getDataRow().addChildDataSet( bridgeName, ret );
				ret.reset();

			}
			
			if( ret == null ) {
				ret = getDataRow().getRecordChild( getEboContext() , bridgeName );
			}
			return ret;
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
    }
    
    private static final Pattern ATT_BRIDGE_PATTERN = Pattern.compile( "\\.[^\\.]{1,}\\.[1-9]{1,}$" );

    public AttributeHandler getAttribute(String attributeName) {
        AttributeHandler toRet = null;

        toRet = this.p_attributes.get(attributeName);
        if( toRet != null ) {
            Matcher m = ATT_BRIDGE_PATTERN.matcher( attributeName );
            if ( m.find() ) {
            	String bridgeName = m.group(1);
            	bridgeHandler b = this.p_bridges.get( bridgeName );
	            toRet = b.getAllAttributes().get(attributeName);
            }
        }
        return toRet;

    }
    
	public AttributeHandler getExtendAttribute(String attributeName) throws boRuntimeException
    {
        AttributeHandler toRet = null;
        boObject extendAttr = null;
        String type = null;
        boolean found = false;
        bridgeHandler bridge = this.p_bridges.get("extendAttribute");
        if(bridge != null)
        {
            bridge.beforeFirst();
            while (bridge.next() && !found)
            {
                extendAttr = (boObject)bridge.getObject();
                if(attributeName.equals(extendAttr.getAttribute("shortAlias").getValueString()))
                {
                    found = true;
                    type = getExtendAttributrType(extendAttr);
                    if(type != null && !"valueList".equals(type))
                    {
                        toRet = extendAttr.getAttribute(type);
                    }

                }
            }
        }
        return toRet;
    }
    public bridgeHandler getExtendAttributeBridge(String attributeName) throws boRuntimeException
    {
        bridgeHandler toRet = null;
        boObject extendAttr = null;
        String type = null;
        boolean found = false;
        bridgeHandler bridge = this.p_bridges.get("extendAttribute");
        if(bridge != null)
        {
            bridge.beforeFirst();
            while (bridge.next() && !found)
            {
                extendAttr = (boObject)bridge.getObject();
                if(attributeName.equals(extendAttr.getAttribute("shortAlias").getValueString()))
                {
                    found = true;
                    type = getExtendAttributrType(extendAttr);
                    if(type != null && "valueList".equals(type))
                    {
                        toRet = extendAttr.getBridge(type);
                    }

                }
            }
        }
        return toRet;
    }

    private String getExtendAttributrType(boObject extendAttr) throws boRuntimeException
    {
        long cadinalidade =  extendAttr.getAttribute("attributeCardinal").getValueLong();
        int attributeType = Integer.parseInt(extendAttr.getAttribute("attributeType").getValueString());

        switch(attributeType) {
            case 0: if(cadinalidade == 1) return "valueObject";
                    else return "valueList";

            case 1: return "valueBoolean";

            case 4: return "valueNumber";

            case 5: return "valueDateTime";

            case 6: return "valueDate";

            case 9: return "valueText";

            case 12: return "valueLov";
        }
        return null;
    }
    public boAttributesArray getAttributes() {
        return p_attributes;
    }

    public boAttributesArray getStateAttributes() {
        return p_states;
    }

    public boObjectStateHandler getStateAttribute(String statename) {
        return (boObjectStateHandler) p_states.get(statename);
    }

    public AttributeHandler getPrimaryState() {
        return this.getStateAttributes().get(getBoDefinition().getBoClsState()
                                                 .getName());
    }

    // Instance methods
    public void computeSecurityKeys(boolean toUpdateObject)
        throws boRuntimeException {
        if ((this.exists() || toUpdateObject) &&
                bo_definition.implementsSecurityRowObjects()) {
            securityOPL.setSecurityKeys(this);
        }
    }

    /**
     * 
     * Returns all attributes of the object (bridges and normal attributes)
     * Also, system attributes are also included in the return array
     * 
     * @return A boAttributesArray with all the attribute of the object
     */
    public boAttributesArray getAllAttributes() {
        boAttributesArray aatts = new boAttributesArray();
        Enumeration atts = this.p_attributes.elements();

        while (atts.hasMoreElements()) {
            aatts.add((AttributeHandler) atts.nextElement());
        }

        Enumeration bridges = this.p_bridges.elements();

        while (bridges.hasMoreElements()) {
            atts = ((bridgeHandler) bridges.nextElement()).getAllAttributes()
                    .elements();

            while (atts.hasMoreElements()) {
                aatts.add((AttributeHandler) atts.nextElement());
            }
        }

        return aatts;
    }

    private void setMode(byte newmode) {
        p_state = STATE_ALTER;
    }

    public void setModeEditTemplate() {
        p_mode = boObject.MODE_EDIT_TEMPLATE;
    }

    public void setModeAlter() {
        setMode(boObject.MODE_EDIT);
    }

    public void setModeView() {
        setMode(boObject.MODE_VIEW);
    }

    public void revertToSaved() throws boRuntimeException {
        this.load(this.getBoui());
    }

    public void saveAsTemplate() throws boRuntimeException {
        throw new RuntimeException(
            "Cannot invoke saveAsTemplate without template name.");
    }

    public void saveAsTemplate(String templatename) throws boRuntimeException {
        boObject objtemp = boObject.getBoManager().createObject(super.getEboContext(),
                "Ebo_Template");

        objtemp.getAttribute("id").setValueString(templatename);

        objtemp.getAttribute("masterObjectClass").setValueLong(Long.parseLong(
                this.bo_classregboui));
        objtemp.update();

        setTemplate(objtemp.getBoui());
    }

    public void edit() throws boRuntimeException {
        setModeAlter();
    }

    public boObject cloneObject() throws boRuntimeException {
        return getBoManager().createObject(getEboContext(), this);
    }

    public byte getState() {
        return p_state;
    }

    public byte getMode() {
        return p_mode;
    }

    public void addEventListener( boEventListener listener )
    {
        if( p_eventListeners == null )
        {
            p_eventListeners = new ArrayList(1);
        }
        if( p_eventListeners.indexOf( listener ) == -1 )
        {
            p_eventListeners.add( listener );
        }
    }
    public void fireEvent( boEvent event )
    {
        if( p_eventListeners != null )
        {
            for (int i = 0; i < p_eventListeners.size(); i++)
            {
                ((boEventListener)p_eventListeners.get( i )).onEvent( event );
            }
        }
    }

    public boolean onBeforeCreate(boEvent event) throws boRuntimeException
    {
        return evalEventCode( event );
    }

    public void onCommit() throws boRuntimeException {
    }

    public void onRollBack() throws boRuntimeException {
    }

    public void onAfterCreate(boEvent event) throws boRuntimeException
    {
        evalEventCode( event );
    }

    public void onBeforeClone(boEvent event) throws boRuntimeException
    {
        evalEventCode( event );
    }

    public void onAfterClone(boEvent event) throws boRuntimeException
    {
        evalEventCode( event );
    }

    public boolean onBeforeSave(boEvent event) throws boRuntimeException
    {
        beforeSaveIFiles();
        return true;
    }

    private boolean evalEventCode( boEvent event ) throws boRuntimeException
    {
        boDefClsEvents eventHandler = getBoDefinition().getBoClsEvent( boEvent.EVENT_NAME[ event.getEvent() ].toUpperCase() );
        if( eventHandler != null )
        {
            boDefXeoCode code = eventHandler.getEventCode();
            if( code != null && code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                if( code.getLanguage() == boDefXeoCode.LANG_XEP )
                {
                    boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                    eval.addThisObject( this );
                    eval.eval( );
                    return eval.getReturnBoolean();
                }
            }
        }
        return true;
    }


    public void onAfterSave(boEvent event) throws boRuntimeException
    {
        evalEventCode( event );
    }

    private void onSaveFwdObject(boEvent event) throws boRuntimeException {
        try
        {
            boObject originObject = this.getAttribute("SYS_FROMOBJ").getObject();
            if(originObject != null)
            {
                boDefHandler bodef = originObject.getBoDefinition();
                boDefForwardObject[] fwds = bodef.getForwardObjects();
                if(fwds != null)
                {
                    for (int i = 0; i < fwds.length; i++)
                    {
                        if(this.getName().equals(fwds[i].toBoObject()))
                        {
                            Method meth = originObject.getClass().getMethod(fwds[i].getOnSaveFwdObjectMethodName(), new Class[]{boObject.class});
                            meth.invoke(originObject , new Object[]{this});
                        }
                    }
                }
            }
        }
        catch(Exception e){logger.severe(e); /*Ignore*/}
    }

    public boolean onBeforeDestroy(boEvent event) throws boRuntimeException
    {
        boolean result = true;
        if(getBoDefinition().haveVersionControl())
        {
            result = getObjectVersionControl().delete();
        }
        result = result && evalEventCode( event );
        return result;
    }

    public void onAfterDestroy(boEvent event) throws boRuntimeException
    {
		EboContext ctx;
    	try {
    		ctx = boApplication.currentContext().getEboContext();
    		if( ctx == null ) {
    			boApplication.currentContext().addEboContext( getEboContext() );
    		}
    		
    		
	        evalEventCode( event );
	        iFile file = null;
	        List iFilesAttributes = getAttributes(boDefAttribute.VALUE_IFILELINK);
	        try
	        {
	            for (int i = 0; i < iFilesAttributes.size(); i++)
	            {
	                file = ((AttributeHandler)iFilesAttributes.get(i)).getValueiFile();
	                if(file != null && file.exists())
	                {
	                    file.delete();
	                }
	            }
	        }
	        catch (iFilePermissionDenied e)
	        {
	            logger.severe("Não foi possível remover do repositório, o ficheiro " + file.getName(),e);
	        }
    	}
    	finally {
    	}
    }

    public void onAfterLoad(boEvent event) throws boRuntimeException
    {
        evalEventCode( event );
    }

    public boolean onBeforeLoad(boEvent event) throws boRuntimeException
    {
        return evalEventCode( event );
    }

    public boolean runActivity(String activityname) throws boRuntimeException
    {
        return false;
    }

    public EboContext removeEboContext() {
        EboContext xret = super.removeEboContext();
        boBridgesArray bridges = this.getBridges();

        if (bridges != null) {
            Enumeration oEnum = bridges.elements();

            while (oEnum.hasMoreElements()) {
                ((bridgeHandler) oEnum.nextElement()).removeEboContext();
            }
        }

        return xret;
    }

    public void setEboContext(EboContext boctx) {
        super.setEboContext(boctx);

        boBridgesArray bridges = this.getBridges();

        if (bridges != null) {
            Enumeration oEnum = bridges.elements();

            while (oEnum.hasMoreElements()) {
                ((bridgeHandler) oEnum.nextElement()).setEboContext(boctx);
            }
        }
    }

    public void setTemplate(long templateboui) {
        if (templateboui != 0) {
            p_template = templateboui;
            setMode(boObject.MODE_EDIT_TEMPLATE);
        }
    }

    public long getTemplate() {
        return p_template;
    }

    private void processTemplate() throws boRuntimeException {
        if ((this.getAttribute("TEMPLATE") != null) &&
                (this.getAttribute("TEMPLATE").getValueLong() != 0)) {
            Ebo_TemplateImpl template = (Ebo_TemplateImpl) ((ObjAttHandler) this.getAttribute(
                    "TEMPLATE")).getObject();
            boObject[] parents = this.getParents();

            if ((parents != null) && (parents.length >= 1)) {
                template.processTemplate(parents[0], this);
            } else {
                template.processTemplate(null, this);
            }
        }
    }

    public boDefHandler[] getSubClasses() {
        return this.getBoDefinition().getBoSubClasses();
    }

    public boDefMethod[] getPublicUserMethods() {
        return new boDefMethod[0];
    }

    public boDefMethod[] getMenuMethods() {
        boDefMethod[] m = this.getBoDefinition().getBoMethods();
        ArrayList r = new ArrayList(m.length);

        for (int i = 0; i < m.length; i++) {
            if (m[i].getIsMenu()) {
                r.add(m[i]);
            }
        }

        m = new boDefMethod[r.size()];

        for (int i = 0; i < r.size(); i++) {
            m[i] = (boDefMethod) r.get(i);
        }

        return m;
    }

    public static boObjectFinder[] getFinders( boDefHandler def ) throws boRuntimeException
    {
        ArrayList finders = new ArrayList();
        IDataPlugin plugins[] = DataPluginManager.getPlugIns();
        for (int i = 0; i < plugins.length; i++)
        {
            boObjectFinder[] plugFinders = plugins[i].getFinders( def );
            if( plugFinders != null )
            {
                finders.addAll( Arrays.asList( plugFinders ) );
            }
        }
        if( finders.size() > 0 )
        {
            return (boObjectFinder[])finders.toArray( new boObjectFinder[ finders.size() ] );
        }
        return null;
    }

    public boDefMethod[] getToolbarMethods() {
        boDefMethod[] m = this.getBoDefinition().getBoMethods();
        ArrayList r = new ArrayList(m.length);

        for (int i = 0; i < m.length; i++) {
            if (m[i].getIsToolbar()) {
                r.add(m[i]);
            }
        }

        m = new boDefMethod[r.size()];

        for (int i = 0; i < r.size(); i++) {
            m[i] = (boDefMethod) r.get(i);
        }

        return m;
    }

    public ParametersHandler getParametersHandler() {
        return p_parameters;
    }

    public String[] getParametersNames() {
        return p_parameters.getParametersNames();
    }

    public String[] getParametersValues() {
        return p_parameters.getParametersValues();
    }

    public String getParameter(String parametername) {
        String ret = p_parameters.getParameter(parametername);

        if (ret == null) {
            ret = p_parameters.getParameter(parametername);
        }

        return ret;
    }

    public void setParameter(String parametername, String parametervalue) {
        p_parameters.setParameter(parametername, parametervalue);
    }

    public void setParameters(String[] parametersnames,
        String[] parametersvalues) {
        p_parameters.setParameters(parametersnames, parametersvalues);
    }

    public void removeParameter(String parname) {
        p_parameters.removeParameter(parname);
    }

    //    public long[] getReferences()
    //    {
    //        return boReferencesManager.getReference(this);
    //    }
    public boObject[] getReferencesObjects() throws boRuntimeException {
        return boReferencesManager.getReferenceObjects(this);
    }

    //    public long[] getReferencedBy()
    //    {
    //        return boReferencesManager.getReferencedBy(this);
    //    }
    public boObject[] getReferencedByObjects() {
        return boReferencesManager.getReferencedByObjects(this);
    }
    
    /**
     * 
     * Retrieves a list of objects referenced by the current object (within a certain range)
     * 
     * @param startRange The start of the range ( for instance, 0,1...2000), must be > 0
     * @param endRange The end of the range (50, 2000, 5000) must be > 0 and > <code>start</code>
     * 
     * @return An array of objects referenced by the current object within the range passed as parameter
     */
    public boObject[] getReferencedByObjects(long startRange, long endRange)
    {
    	return boReferencesManager.getReferencedByObjects(this,startRange,endRange);
    }
    
    /**
     * 
     * Retrieves a list of objects which reference the current object, within a certain range (i.e. The first 2000 objects, or the 
     * objects between 1000 and 4000)
     * 
     * @param startRange The start of the range
     * @param endRange The end of the range
     * @return An array of objects which reference the current object (within the range passed as parameter)
     * @throws boRuntimeException
     */
    public boObject[] getReferencesObjects(long startRange, long endRange) throws boRuntimeException 
    {
        return boReferencesManager.getReferenceObjects(this,startRange,endRange);
    }

    public void setChanged(boolean changed) throws boRuntimeException {
        if (this.p_readystate == boObject.READYSTATE_COMPLETE) {
            if (p_ischanged != changed) {
                p_ischanged = changed;

                if (p_ischanged) {
                    if (!this.poolIsStateFull()) {
                        this.poolSetStateFull();
                    }

                    getThread().setChanged(getEboContext(),
                    		BigDecimal.valueOf(this.getBoui()));
                } else {
                    if (exists()) {
                        this.poolUnSetStateFull();
                    } else {
                        if (!this.poolIsStateFull()) {
                            this.poolSetStateFull();
                        }
                    }
                }
            }
        }
    }

    public boolean isChanged() throws boRuntimeException {
        // boolean ret = p_ischanged;
        //        if (!ret)
        //        {
        //            ret = checkChanged(this, new ArrayList());
        //        }
        return p_ischanged;
    }

    public void markAsRead() throws boRuntimeException {
        try {
            //JBOSS
        	boolean isInTrans = !getEboContext().getConnectionManager().isContainerTransactionActive();
        	if( !isInTrans ) {
            	getEboContext().beginContainerTransaction();
        	}
        	try 
        	{
	            bridgeHandler b = this.getBridge("READLIST");
	
	            if ((b != null) && !p_forceInstanceUnread) {
	                Long performer = this.getEboContext().getBoSession()
	                                     .getPerformerBouiLong();
	
	                if (exists() && !b.haveBoui(performer.longValue())) {
	                    //                      b.add( performer );
	                    b.getRslt().moveToInsertRow();
	                    b.getRslt().updateLong("PARENT$", this.getBoui());
	                    b.getRslt().updateLong("CHILD$", performer.longValue());
	                    b.getRslt().insertRow();
	                    DataManager.updateDataSet(getEboContext(),
	                        b.getRslt().getDataSet(), false);
	                    getEboContext().commitContainerTransaction();
	                    b.refreshBridgeData();
	
	                    //b.getRslt().updateRow();
	                    cacheBouis.put_userReadThisBoui(performer, this.bo_boui);
	                }
	            }
        	}
        	finally {
            	if( !isInTrans ) {
                	getEboContext().commitContainerTransaction();
            	}
        	}
        } catch (WriterException e) {
            String[] args = { bo_name, (new Long(bo_boui)).toString() };
            throw new boRuntimeException(this,
                this.getClass().getName() + ".maskAsRead()", "BO-3057", e, args);
        } catch (SQLException e) {
            String[] args = { bo_name, (new Long(bo_boui)).toString() };
            throw new boRuntimeException(this,
                this.getClass().getName() + ".maskAsRead()", "BO-3057", e, args);
        }
    }

    public void markAsUnRead() throws boRuntimeException {
    	boolean trans = false;
        try {
            //JBOSS
        	boolean isInTrans = !getEboContext().getConnectionManager().isContainerTransactionActive();
        	if( !isInTrans ) {
            	getEboContext().beginContainerTransaction();
        	}
        	try 
        	{
            bridgeHandler b = this.getBridge("READLIST");
        
            if (b != null) {
                Long performer = this.getEboContext().getBoSession()
                                     .getPerformerBouiLong();

                if (b.haveBoui(performer.longValue())) {
                    //b.remove();
                    b.getRslt().deleteRow();
                    DataManager.updateDataSet(getEboContext(),
                        b.getRslt().getDataSet(), false);
                    getEboContext().getConnectionData().commit();
                    b.refreshBridgeData();

                    cacheBouis.put_userUnReadThisBoui(performer, this.bo_boui);
                }
            }
        	}
        	finally {
            	if( !isInTrans ) {
                	getEboContext().commitContainerTransaction();
            	}
        	}
	            
        } catch (WriterException e) {
            String[] args = { bo_name, (new Long(bo_boui)).toString() };
            throw new boRuntimeException(this,
                this.getClass().getName() + ".maskAsUnRead()", "BO-3058", e,
                args);
        } catch (SQLException e) {
			if(e.getMessage() != null && e.getMessage().indexOf("ORA-000001") != -1 )
            {
                //unique constraint
                //ignore
            }
            else
            {
				String[] args = { bo_name, (new Long(bo_boui)).toString() };
				throw new boRuntimeException(this,
					this.getClass().getName() + ".maskAsUnRead()", "BO-3058", e,
					args);
			}
        }
	    finally {
	    	if( trans )
	    		getEboContext().commitContainerTransaction();
	    }
    }

    public void markAsUnRead(boolean forceInThisRequest)
        throws boRuntimeException {
        markAsUnRead();
        p_forceInstanceUnread = true;
    }

    public boolean userReadThis() throws boRuntimeException {
        boolean toRet = true;
        Long performer = this.getEboContext().getBoSession()
                             .getPerformerBouiLong();
        Boolean read = cacheBouis.get_userReadThisBoui(performer, this.bo_boui);

        if (read == null) {
            bridgeHandler b = this.getBridge("READLIST");

            if (b != null) {
                if (b.haveBoui(performer.longValue())) {
                    toRet = true;
                    cacheBouis.put_userReadThisBoui(performer, this.bo_boui);
                } else {
                    toRet = false;
                    cacheBouis.put_userUnReadThisBoui(performer, this.bo_boui);
                }
            }
        } else {
            toRet = read.booleanValue();
        }

        return toRet;
    }

    public void poolObjectActivate() {
    }

    public void poolObjectPassivate() {
        try {
            if (!exists()) {
                super.poolDestroyObject();
                ;
            }
        } catch (boRuntimeException e) {
            throw new boRuntimeException2(this.getClass().getName() +
                "Error activiting Object.\n" + "\n" + e.getMessage());
        }
    }

    public boObject getRequestObject() throws boRuntimeException {
        boObject ret = null;
        String mp = this.getBoDefinition().getModifyProtocol();

        if (mp != null) {
            long template;
            long bouiClass = getBoManager()
                                 .loadObject(getEboContext(),
                    "SELECT Ebo_ClsReg WHERE Ebo_ClsReg.name='" + getName() +
                    "'").getBoui();
            boObjectList list = boObjectList.list(getEboContext(),
                    "SELECT Ebo_RequestMap WHERE Ebo_RequestMap.classe=" +
                    bouiClass);

            if (list.next()) {
                template = list.getObject().getAttribute("protocol")
                               .getValueLong();
                ret = ((Ebo_TemplateImpl) boObject.getBoManager().loadObject(getEboContext(),
                        template)).loadTemplate();
            } else {
                ret = boObject.getBoManager().createObject(super.getEboContext(),
                        mp);
            }
        }

        return ret;
    }

  /*  public StringBuffer getCARDIDwNoIMG() throws boRuntimeException {
        StringBuffer toRet = new StringBuffer();
        StringBuffer xC = new StringBuffer();
        xC.append(mergeAttributes(this.getBoDefinition().getCARDID(), this));

        if ((xC.toString().trim().length() < 1) && !this.exists()) {
            xC.setLength(0);
            xC.append("Novo(a) " + getBoDefinition().getLabel());
        }

        if (xC.length() > 46) {
            toRet.append(xC.substring(0, 45) + "...");
        } else {
            toRet.append(xC);
        }

        //        }
        return toRet;
    }

    public String getSrcForIcon16() throws boRuntimeException {
        String toRet = "";

        if (this.getName().equals("runtimeAddress")) {
            boObject oref = this.getAttribute("refObj").getObject();

            if (oref != null) {
                toRet = "resources/" + oref.getName() + "/ico16.gif";
            }
        } else {
            toRet = "resources/" + this.getName() + "/ico16.gif";
        }

        return toRet;
    }

    public StringBuffer getCARDID() throws boRuntimeException {
        StringBuffer toRet = new StringBuffer();
        toRet.append(
            "<img style='cursor:hand' hspace='3' border='0' align='absmiddle' class='lui' title='");

        if (this.getName().equals("Ebo_Template")) {
            //toRet.append("Objecto ");
            toRet.append(this.getBoDefinition().getLabel());

            boObject o = this.getBoManager().loadObject(this.getEboContext(),
                    "Ebo_ClsReg",
                    this.getAttribute("masterObjectClass").getValueLong());
            toRet.append(" de " +
                o.getAttribute("description").getValueString());
            toRet.append("' src='" + this.getEboContext().getApplicationUrl() +
                "/resources/" + o.getAttribute("name").getValueString() +
                "/ico16tmpl.gif");
            toRet.append("' width='16' height='16'/>");
        } else if (this.getName().equals("Ebo_ClsReg")) {
            toRet.append("Classe do objecto ");
            toRet.append(this.getBoDefinition().getLabel());
            toRet.append("' src='" + this.getEboContext().getApplicationUrl() +
                "/resources/" + this.getAttribute("name").getValueString() +
                "/ico16.gif");
            toRet.append("' width='16' height='16'/>");
        } else if (this.getName().equals("runtimeAddress")) {
            boObject oref = this.getAttribute("refObj").getObject();
            toRet.append(this.getBoDefinition().getLabel());

            if (oref != null) {
                toRet.append("' src='" +
                    this.getEboContext().getApplicationUrl() + "/resources/" +
                    oref.getName() + "/ico16.gif");
            } else {
                toRet.append("' src='" +
                    this.getEboContext().getApplicationUrl() + "/resources/" +
                    this.getName() + "/ico16.gif");
            }

            toRet.append("' width='16' height='16'/>");
        } else {
            // toRet.append("Objecto ");
            toRet.append(this.getBoDefinition().getLabel());
            toRet.append("' src='" + this.getEboContext().getApplicationUrl() +
                "/resources/" + this.getName() + "/ico16.gif");
            toRet.append("' width='16' height='16'/>");
        }

        //        if ( !this.exists() )
        //        {
        //            toRet.append("<span>");
        //            toRet.append(this.getBoDefinition().getLabel());
        //            toRet.append("</span>");
        //        }
        //        else
        //        {
        StringBuffer xC = new StringBuffer();
        xC.append(mergeAttributes(this.getBoDefinition().getCARDID(), this));

        if ((xC.toString().trim().length() < 1) && !this.exists()) {
            xC.setLength(0);
            xC.append("Novo(a) " + getBoDefinition().getLabel());
        }

        toRet.append("<span title='");
        toRet.append(xC);
        toRet.append("'>");

        if (xC.length() > 46) {
            toRet.append(xC.substring(0, 45) + "...");
        } else {
            toRet.append(xC);
        }

        toRet.append("</span>");

        //        }
        return toRet;
    }
*/

  /*  public StringBuffer getCARDIDwLink() throws boRuntimeException {
        return getCARDIDwLink(false, null);
    }

    public StringBuffer getCARDIDwLink(boolean doubleEscape)
        throws boRuntimeException {
        return getCARDIDwLink(false, null);
    }

    public StringBuffer getCARDIDwLink(boolean doubleEscape,
        String extraParameters) throws boRuntimeException {
        StringBuffer toRet = new StringBuffer();

        //    toRet.append("<table id='");
        //  toRet.append(this.bo_boui);
        //   toRet.append("' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>");
        //    toRet.append("<div class='lu ro'><span class='lui' onclick=\"");
        if (doubleEscape) {
            toRet.append("<span class='lui' onclick=\\\"");
        } else {
            toRet.append("<span class='lui' onclick=\"");
        }

        toRet.append("winmain().openDoc('medium','");
        toRet.append(this.getName().toLowerCase());
        toRet.append("','edit','" +
            ((extraParameters == null) ? "" : (extraParameters + "&")) +
            "method=edit&boui=");
        toRet.append(this.bo_boui);
        toRet.append("','");
        toRet.append("");
        toRet.append("','");
        toRet.append("");
        toRet.append("','");
        toRet.append(this.getName());

        if (doubleEscape) {
            toRet.append("',window.windowIDX)\\\">");
        } else {
            toRet.append("',window.windowIDX)\">");
        }

        toRet.append(
            "<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' title='");
        toRet.append("Imagem representativa do objecto ");
        toRet.append(this.getBoDefinition().getLabel());
        toRet.append("' src='resources/" + this.getName() + "/ico16.gif");
        toRet.append("' width='16' height='16'/><span ");

        //  if ( !this.exists() )
        //  {
        //      toRet.append('>');
        //      toRet.append(this.getBoDefinition().getLabel());
        //      toRet.append("</span>");
        // }
        //  else
        //  {
        StringBuffer xC = new StringBuffer();
        xC.append(mergeAttributes(this.getBoDefinition().getCARDID(), this));
        toRet.append("title='");
        toRet.append(xC);
        toRet.append("'>");
        toRet.append(xC);
        toRet.append("</span></span>");

        //  }
        // toRet.append("</td></tr></tbody></table>");
        return toRet;
    }

    public StringBuffer getURL() throws boRuntimeException {
        StringBuffer toRet = new StringBuffer();
        toRet.append("<a  href='");
        toRet.append(this.getEboContext().getApplicationUrl());
        toRet.append("/");
        toRet.append("__viewObject.jsp");
        toRet.append("?method=edit&boui=");
        toRet.append(this.bo_boui);
        toRet.append("&object=");
        toRet.append(this.getName());
        toRet.append("'>");

        toRet.append(
            "<img style='cursor:hand' hspace='3' align='absmiddle' border='0' title='");
        toRet.append("Imagem representativa do objecto ");
        toRet.append(this.getBoDefinition().getLabel());
        toRet.append("' src='" + this.getEboContext().getApplicationUrl() +
            "/resources/" + this.getName() + "/ico16.gif");
        toRet.append("' width='16' height='16'/><span ");

        //  if ( !this.exists() )
        //  {
        //      toRet.append('>');
        //      toRet.append(this.getBoDefinition().getLabel());
        //      toRet.append("</span>");
        // }
        //  else
        //  {
        StringBuffer xC = new StringBuffer();
        xC.append(mergeAttributes(this.getBoDefinition().getCARDID(), this));
        toRet.append("title='");
        toRet.append(xC);
        toRet.append("'>");
        toRet.append(xC);
        toRet.append("</a>");

        //  }
        // toRet.append("</td></tr></tbody></table>");
        return toRet;
    }

    public StringBuffer getExplainProperties() throws boRuntimeException {
        StringBuffer toRet = new StringBuffer();

        if (getMode() == boObject.MODE_EDIT_TEMPLATE)
        {
        }
        else if ((getMode() == boObject.MODE_EDIT) ||
                (getMode() == boObject.MODE_NEW))
        {
            if (this.getAttribute("TEMPLATE") != null)
            {
                if (this.getAttribute("TEMPLATE").getValueLong() == 0)
                {
                    toRet.append(
                        "Para atribuir a actividade  <span class='lui' onclick=\"winmain().openDocUrl('tall','__applyTemplate.jsp','?clientIDX='+getIDX()+'&operation=applyTemplate&docid='+getDocId()+'&bouiToApplyTemplate=" +
                        this.getBoui() + "','lookup') \" >clique aqui</span>");
                }
                else
                {
                    toRet.append(" Este objecto foi criado com o modelo ");
                    toRet.append(this.getObject(getAttribute("TEMPLATE")
                                                    .getValueLong())
                                     .getCARDIDwLink());
                }
                if("email".equalsIgnoreCase(this.getName()))
                {
                    if(this.exists() && !"E".equalsIgnoreCase(this.getAttribute("nature").getValueString()))
                    {
                        if(!"1".equals(this.getAttribute("already_send").getValueString()))
                        {
                            toRet.append("</td><tr><td colspan='4'>")
                            .append("<font color=\"#FF0000\">Esta mensagem não foi enviada.</font>");
                        }
                        else
                        {
                            toRet.append("</td><tr><td colspan='4'>")
                                .append("Esta mensagem foi enviada.");
                        }
                    }
                    if(this.exists() && "E".equalsIgnoreCase(this.getAttribute("nature").getValueString()))
                    {
                        //prioridade
                        if("0".equals(this.getAttribute("priority").getValueString()))
                        {
                            toRet.append("</td><tr><td colspan='4'>");
                            //toRet.append("<IMG id=\"idImgStatusBar\" SRC=\"resources/emailInfo.gif\" VALIGN=\"middle\">&nbsp;");
                            toRet.append("Esta mensagem foi enviada com o grau de importância baixa.");
                        }
                        if("2".equals(this.getAttribute("priority").getValueString()))
                        {
                            toRet.append("</td><tr><td colspan='4'>");
                            toRet.append("Esta mensagem foi enviada com o grau de importância alta.");
                        }

                        if("1".equals(this.getAttribute("send_read_receipt").getValueString()) &&
                            this.getAttribute("send_date_read_receipt").getValueDate() == null)
                        {
                            toRet.append("</td><tr><td colspan='4'>");
                            toRet.append("O remetente desta mensagem solicitou um recibo de leitura. ")
                            .append("<span class='lui' onclick=\"boForm.executeMethod('sendReceipt')\"")
                            .append(" >Clique aqui para enviar um recibo</span>");

                        }
                        else if("1".equals(this.getAttribute("send_read_receipt").getValueString()) &&
                            this.getAttribute("send_date_read_receipt").getValueDate() != null)
                        {
                            String dt = null;
                            try
                            {
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                dt = df.format(this.getAttribute("send_date_read_receipt").getValueDate());
                            }catch(Exception e)
                            {
                                logger.warn("Erro ao efectuar o set das datas do email.", e);
                            }
                            if(dt != null)
                            {
                                toRet.append("</td><tr><td colspan='4'>");
                                toRet.append("Recibo de leitura enviado na data ").append(dt).append(".");
                            }
                        }
                    }
                }
            }
        }

        return toRet;
    }

    public StringBuffer getCARDIDwState() throws boRuntimeException {
        StringBuffer toRet = new StringBuffer();

        //toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' src='");
        //toRet.append("resources/"+this.getName()+"/ico16.gif");
        //toRet.append("' width='16' height='16'/> ");
        toRet.append(
            "<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' title='");

        if (this.getName().equals("Ebo_Template")) {
            toRet.append("Imagem representativa do objecto ");

            toRet.append(this.getBoDefinition().getLabel());

            boObject o = this.getBoManager().loadObject(this.getEboContext(),
                    "Ebo_ClsReg",
                    this.getAttribute("masterObjectClass").getValueLong());
            toRet.append(" de " +
                o.getAttribute("description").getValueString());
            toRet.append("' src='" + this.getEboContext().getApplicationUrl() +
                "/resources/" + o.getAttribute("name").getValueString() +
                "/ico16tmpl.gif");
            toRet.append("' width='16' height='16'/>");
        } else if (this.getName().equals("Ebo_ClsReg")) {
            toRet.append("Imagem representativa da Classe do objecto ");
            toRet.append(this.getBoDefinition().getLabel());
            toRet.append("' src='" + this.getEboContext().getApplicationUrl() +
                "/resources/" + this.getAttribute("name").getValueString() +
                "/ico16.gif");
            toRet.append("' width='16' height='16'/>");
        } else {
            toRet.append("Imagem representativa do objecto ");
            toRet.append(this.getBoDefinition().getLabel());
            toRet.append("' src='" + this.getEboContext().getApplicationUrl() +
                "/resources/" + this.getName() + "/ico16.gif");
            toRet.append("' width='16' height='16'/>");
        }

        //        if ( !this.exists() )
        //        {
        //            toRet.append("<span>");
        //            toRet.append(this.getBoDefinition().getLabel());
        //            toRet.append("</span>");
        //        }
        //        else
        //        {
        StringBuffer xC = new StringBuffer();
        xC.append(mergeAttributes(this.getBoDefinition().getCARDID(), this));
        toRet.append(this.getICONComposedState());

        // toRet.append("<img align='absmiddle' hspace='1' src='resources/");
        // toRet.append(getStringComposedState());
        //  toRet.append(".gif' width=16 height=16 />");
        toRet.append("<span ");
        toRet.append("title='");
        toRet.append(xC);
        toRet.append("'>");
        toRet.append(xC);
        toRet.append("</span>");

        //     }
        return toRet;
    }
*/
   /* public StringBuffer getSTATUS() throws boRuntimeException, SQLException {
        StringBuffer toRet = new StringBuffer();

        if (!this.exists() && (this.p_mode != this.MODE_EDIT_TEMPLATE)) {
            toRet.append("&nbsp;ESTADO : <b>Novo</b>");
        } else if (this.p_mode == this.MODE_EDIT) {
            long xboui = this.getAttribute("CREATOR").getValueLong();

            if (xboui > 0) {
                boObject u1 = this.getObject(xboui);
                toRet.append("&nbsp;ESTADO : <b>Em edição</b>" + " ( " +
                    this.p_bodata.getLong("SYS_ICN") + " ) Criado por :");
                toRet.append(u1.getCARDID());
                toRet.append(" Em ");

                toRet.append(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                        DateFormat.MEDIUM).format(p_bodata.getDate(
                            "SYS_DTCREATE")));
            } else {
                toRet.append("&nbsp;ESTADO : <b>Em edição</b>" + " ( " +
                    this.p_bodata.getLong("SYS_ICN") + " )");
            }
        } else if (this.p_mode == this.MODE_EDIT_TEMPLATE) {
            boObject tmpl = this.getAttribute("TEMPLATE").getObject();

            if (tmpl.exists()) {
                toRet.append("&nbsp;ESTADO : <b>A editar modelo</b> ");
                toRet.append(" ( ").append(tmpl.p_bodata.getLong("SYS_ICN"))
                     .append(" ) Criado por ");

                long xboui = tmpl.getAttribute("CREATOR").getValueLong();

                if (xboui > 0) {
                    boObject u1 = this.getObject(xboui);
                    toRet.append(u1.getCARDID());
                    toRet.append(" Em ");
                    toRet.append(DateFormat.getDateTimeInstance(
                            DateFormat.MEDIUM, DateFormat.MEDIUM).format(tmpl.p_bodata.getDate(
                                "SYS_DTCREATE")));
                    toRet.append(" Última Act. em  ");
                    toRet.append(DateFormat.getDateTimeInstance(
                            DateFormat.MEDIUM, DateFormat.MEDIUM).format(tmpl.p_bodata.getDate(
                                "SYS_DTSAVE")));
                }

                //toRet.append("</span>");
            } else {
                toRet.append("&nbsp;ESTADO : <b>A criar modelo</b>");
            }
        }


        return toRet;
    }

    public StringBuffer getTextCARDID() throws boRuntimeException {
        StringBuffer toRet = new StringBuffer();

        toRet.append(mergeAttributes(this.getBoDefinition().getCARDID(), this));

        return toRet;
    }
*/
    /**
      *    PRESENTATION LAYER OF OBJECT
      */
    public ObjectViewer getViewerUtils()
    {
        if( p_viewerutils == null )
        {
            try
            {
                boDefViewer oViewer = bo_definition.getViewer("General");
                if( oViewer != null )
                {
                    p_viewerutils = (ObjectViewer)Class.forName( oViewer.getObjectViewerClass() ).newInstance();
                }
                else {
                    p_viewerutils = new netgest.bo.dochtml.viewerImpl.ObjectViewerImpl();   
                }
            }
            catch (Exception e)
            {
                throw new boRuntimeException2( e );
            }
            p_viewerutils.setContextObject( this );
        }
        return p_viewerutils;
    }

    public static StringBuffer mergeAttributes(String text, boObject obj)
        throws boRuntimeException {
        StringBuffer toRet;

        if (text != null) {
            toRet = new StringBuffer();

            StringBuffer wordAttribute = new StringBuffer();
            StringBuffer othersAttribute = new StringBuffer();

            //String[] arrstr=ClassUtils.splitToArray(text,"+");
            AttributeHandler xatr = null;
            final char tokenBegin = '[';
            final char tokenEnd = ']';
            final char tokenAtt = '.';
            final char escapeChar = '\\';
            char[] ch = text.toCharArray();
            boolean toAdd = false;
            boolean inBuildExpr = false;
            boolean toProcess = false;
            boolean toOthers = false;
            char lastChar = ' ';

            for (int i = 0; i < ch.length; i++) {
                switch (ch[i]) {
                case tokenBegin:

                    if (lastChar == escapeChar) {
                        toAdd = true;
                    } else {
                        if (!inBuildExpr) {
                            toAdd = false;

                            inBuildExpr = true;
                        }
                    }

                    break;

                case tokenEnd:

                    if (lastChar == escapeChar) {
                        toAdd = true;
                    } else {
                        if (inBuildExpr) {
                            toAdd = false;
                            inBuildExpr = false;
                            toProcess = true;
                        } else {
                            toAdd = true;
                        }
                    }

                    break;

                case escapeChar:

                    if (lastChar == escapeChar) {
                        toAdd = true;
                    } else {
                        toAdd = false;
                    }

                    break;
                case tokenAtt:
                    if( inBuildExpr )
                    {
                        toOthers = true;
                        toAdd = false;
                    }
                    else
                    {
                        toAdd = true;
                    }
                    break;

                default:
                    toAdd = true;
                }

                if (inBuildExpr) {
                    if (toAdd) {
                        if(!toOthers)
                            wordAttribute.append(ch[i]);
                        else
                            othersAttribute.append(ch[i]);
                    }
                } else {
                    if (toProcess) {
                        if (wordAttribute.toString().equals("_CLSICO")) {
                            xatr = obj.getAttribute("name");
                            toRet.append(
                                "<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' title='");
                            toRet.append("Imagem representativa do objecto ");
                            toRet.append(obj.getAttribute("description")
                                            .getValueString());
                            toRet.append("' src='resources/" +
                                getValue(obj, xatr) + "/ico16.gif");
                            toRet.append("' width='16' height='16'/>");
                            xatr = null;
                        } else if (wordAttribute.toString().equalsIgnoreCase("parent")) {
                            String aux = othersAttribute.toString();

                            if (obj.getParent() != null) {
                                boObject parentAux = obj.getParent();
                                xatr = parentAux.getAttribute(aux);
                            }
                        } else {
                            xatr = obj.getAttribute(wordAttribute.toString());
                        }

                        if (xatr != null) {
                            if (xatr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                                if (xatr.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1) {
                                    ObjAttHandler x = (ObjAttHandler) obj.getAllAttributes()
                                                                         .get(wordAttribute.toString());

                                    if (x.getObject() != null) {
                                        if(toOthers)
                                            toRet.append(getValue(x.getObject(), x.getObject().getAttribute(
                                                        othersAttribute.toString())));
                                        else
                                            toRet.append(x.getObject()
                                                      .getTextCARDID());
                                    }
                                } else if (xatr.getDefAttribute()
                                                   .getRelationType() == boDefAttribute.RELATION_1_TO_N) {
                                    bridgeHandler b = (bridgeHandler) obj.getBridge(wordAttribute.toString());

                                    //<bridgeHandler b = x.getBridge();
                                    boBridgeIterator it = b.iterator();

                                    while (it.next()) {
                                        toRet.append(it.currentRow().getObject()
                                                       .getTextCARDID());
                                        toRet.append(' ');
                                    }
                                } else if (xatr.getDefAttribute()
                                                   .getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE) {
                                    bridgeHandler b = (bridgeHandler) obj.getBridge(wordAttribute.toString());

                                    //<bridgeHandler b = x.getBridge();
                                    //b.beforeFirst();
                                    boBridgeIterator it = b.iterator();

                                    while (it.next()) {
                                        toRet.append(it.currentRow().getObject()
                                                       .getTextCARDID());
                                        toRet.append(' ');
                                    }
                                }
                            } else {
                                String xx = getValue(xatr.getParent(), xatr);
                                xx = xx.replaceAll("'", " ");
                                xx = xx.replaceAll("\"", "&#34;");
                                xx = xx.replaceAll("<", "&#60;");
                                xx = xx.replaceAll(">", "&#62;");
                                toRet.append(xx);
                            }
                        }

                        wordAttribute.setLength(0);
                        othersAttribute.setLength(0);
                        toOthers=false;
                    }

                    if (toAdd) {
                        toRet.append(ch[i]);
                    }
                }

                lastChar = ch[i];
            }
        } else {
            return new StringBuffer();
        }

        return new StringBuffer(HtmlField.escapeCode(toRet.toString()));
    }


    public StringBuffer getCARDIDwNoIMG() throws boRuntimeException
    {
        return getViewerUtils().getCARDIDwNoIMG();
    }

    public StringBuffer getCARDIDwNoIMG(boolean cut) throws boRuntimeException
    {
        return getViewerUtils().getCARDIDwNoIMG(cut);
    }

    public String getSrcForIcon16() throws boRuntimeException
    {
        return getViewerUtils().getSrcForIcon16();
    }

    public StringBuffer getCARDID() throws boRuntimeException
    {
        return getViewerUtils().getCARDID(true);
    }

    public Element getCARDID(ngtXMLHandler xmlToPrint, Element root, boolean cut) throws boRuntimeException
    {
        return getViewerUtils().getCARDID(xmlToPrint, root, cut);
    }

    public StringBuffer getCARDID(boolean cut) throws boRuntimeException
    {
        return getViewerUtils().getCARDID(cut);
    }

    public StringBuffer getCARDIDwLink() throws boRuntimeException
    {
        return getViewerUtils().getCARDIDwLink();
    }

    public StringBuffer getCARDIDwLink(boolean doubleEscape) throws boRuntimeException
    {
        return getViewerUtils().getCARDIDwLink( doubleEscape );
    }

    public StringBuffer getCARDIDwLink(boolean doubleEscape,String extraParameters )
        throws boRuntimeException
    {
        return getViewerUtils().getCARDIDwLink( doubleEscape, extraParameters );
    }

    public StringBuffer getCARDIDwLink(boolean newPage, boolean doubleEscape,String extraParameters )
        throws boRuntimeException
    {
        return getViewerUtils().getCARDIDwLink(newPage, doubleEscape, extraParameters );
    }

    public StringBuffer getURL() throws boRuntimeException
    {
        return getViewerUtils().getURL();
    }

    public StringBuffer getExplainProperties(docHTML doc) throws boRuntimeException
    {
        return getViewerUtils().getExplainProperties(doc);
    }

    public StringBuffer getCARDIDwState() throws boRuntimeException
    {
        return getViewerUtils().getCARDIDwState();
    }

    public StringBuffer getCARDIDwStatewLink() throws boRuntimeException
    {
        return getViewerUtils().getCARDIDwStatewLink();
    }

    public StringBuffer getSTATUS() throws boRuntimeException, SQLException
    {
        return getViewerUtils().getSTATUS();
    }

    public StringBuffer getTextCARDID() throws boRuntimeException
    {
        return getViewerUtils().getTextCARDID();
    }

    public String getICONComposedState() throws boRuntimeException
    {
        return getViewerUtils().getICONComposedState();
    }
    public String getLabel() throws boRuntimeException
    {
        return getViewerUtils().getLabel();
    }

    public String getStringComposedState() throws boRuntimeException
    {
        return getViewerUtils().getStringComposedState();
    }

    /**
     * END PRESENTATTION
     */

/*
    public String getICONComposedState() throws boRuntimeException {
        String toRet = "";

        if (bo_statemanager != null) {
            toRet = bo_statemanager.getStateHTMLICON(this);
        } else {
            //toRet="none";
            toRet = "<IMG src='resources/none.gif' height=16 width=16 />";
        }

        return toRet;
    }
*/
    public void applyTemplate(String lastClassName, long templateBoui) throws boRuntimeException
	{
        this.getAttribute("TEMPLATE").setValueLong(templateBoui);

        if ((this.getAttribute("TEMPLATE") != null) &&
                (this.getAttribute("TEMPLATE").getValueLong() != 0)) {
            Ebo_TemplateImpl template = (Ebo_TemplateImpl) ((ObjAttHandler) this.getAttribute(
                    "TEMPLATE")).getObject();
            template.loadTemplate(null, this);

            boObject[] parents = this.getParents();

            if ((parents != null) && (parents.length >= 1)) {
                template.processTemplate(parents[0], this);
            } else {
                template.processTemplate(null, this);
            }
        }
    }

    public boolean mustCast() {
        return p_mustCast;
    }
	public void setMustCast(boolean mustCast)
    {
        this.p_mustCast = mustCast;
    }
    public void castTo(String className) throws boRuntimeException
	{
        CastInterface castI = this.getBoDefinition().getCastToClass();

        castI.beforeCast(this);
        cacheBouis.putBoui(this.getBoui(), className, true);
        p_mustCast = true;

        this.getAttribute("CLASSNAME").setValueString(className);
        bo_definition = boDefHandler.getBoDefinition(className);
        bo_name = bo_definition.getName();

        castI.afterCast(this);
		if(castI.isToRefresh(this))
        {
            StringBuffer sb = new StringBuffer();
            sb.append(className.toLowerCase())
                .append("_generaledit.jsp?method=edit&boui=")
                .append(getBoui());
            setSendRedirect(sb.toString());
        }
    }
/*
    public String getStringComposedState() throws boRuntimeException {
        String toRet = "";

        if (bo_statemanager != null) {
            toRet = bo_statemanager.getStateString(this);
        } else {
            toRet = "none";
        }

        return toRet;
    }
*/
    // to override
    public String[] getDependences(String attributeName) {
        // Overrided on generated objects
        return null;
    }

    public boolean isCheckSecurity() {
        return checkSecurity;
    }

    public void setCheckSecurity(boolean checkSecurity) {
        this.checkSecurity = checkSecurity;
    }

    // psantos ini
    /*
    public void setDisabled() throws boRuntimeException {
        Enumeration oEnum = this.p_attributes.elements();

        while (oEnum.hasMoreElements()) {
            AttributeHandler ah = (AttributeHandler) oEnum.nextElement();

            ah.setDisabled();

            if (ah.isBridge()) {
                if (this.getBridge(ah.getName()) != null) {
                    Enumeration atts = this.getBridge(ah.getName())
                                           .getAllAttributes().elements();

                    while (atts.hasMoreElements()) {
                        ah = (AttributeHandler) atts.nextElement();

                        ah.setDisabled();
                    }
                }
            } else if (ah.isObject()) {
                if (ah.getValueObject() != null) {
                    Enumeration atts = ah.getObject().getAllAttributes()
                                         .elements();

                    //Enumeration atts = this.getBridge(ah.getName()).getAllAttributes().elements();
                    while (atts.hasMoreElements()) {
                        ah = (AttributeHandler) atts.nextElement();

                        ah.setDisabled();
                    }
                }
            }
        }

        this.isEnabled = false;
        this.isEnabledforRequest = false;
    }
    */

    public void setDisabled() throws boRuntimeException {
        setDisabled( true );
    }

    public void setDisabled( boolean deep ) throws boRuntimeException {
        Enumeration oEnum = this.p_attributes.elements();

        while (oEnum.hasMoreElements()) {
            AttributeHandler ah = (AttributeHandler) oEnum.nextElement();
            ah.setDisabled();
                if( deep )
                {
                    if (ah.isBridge())
                    {
                        if (this.getBridge(ah.getName()) != null)
                        {
                            Enumeration atts = this.getBridge(ah.getName())
                                                   .getAllAttributes().elements();

                            while (atts.hasMoreElements()) {
                                ah = (AttributeHandler) atts.nextElement();

                                ah.setDisabled();
                            }
                        }
                    }
                    else if (ah.isObject() )
                    {
                        if (ah.getValueObject() != null)
                        {
                            boObject refObj = ah.getObject();
                            if( refObj != null )
                            {
                                Enumeration atts = ah.getObject().getAllAttributes()
                                                     .elements();

                                ah.getObject().isEnabled = false;

                                //Enumeration atts = this.getBridge(ah.getName()).getAllAttributes().elements();
                                while (atts.hasMoreElements()) {
                                    ah = (AttributeHandler) atts.nextElement();
                                    ah.setDisabled();
                                }
                            }
                        }
                    }
                }
        }

        this.isEnabled = false;
        this.isEnabledforRequest = false;
    }

    public void setEnabledforRequest() throws boRuntimeException {
        _setEnabled(true,true);
    }

    public void setEnabled() throws boRuntimeException {
        _setEnabled(false,true);
    }

    public void setEnabled( boolean deep ) throws boRuntimeException {
        _setEnabled(false, deep );
    }

    private void _setEnabled(boolean forRequest, boolean deep) throws boRuntimeException {
        Enumeration oEnum = this.p_attributes.elements();

        while (oEnum.hasMoreElements()) {
            AttributeHandler ah = (AttributeHandler) oEnum.nextElement();

            if (!ah.disableWhen())
            {
                if (forRequest) {
                    ah.setEnabledforRequest();
                } else {
                    ah.setEnabled();
                }
            }

            if( deep )
            {
                if (ah.isBridge())
                {
                    Enumeration atts = this.getBridge(ah.getName())
                                           .getAllAttributes().elements();

                    while (atts.hasMoreElements()) {
                        ah = (AttributeHandler) atts.nextElement();

                        if (!ah.disableWhen()) {
                            if (forRequest) {
                                ah.setEnabledforRequest();
                            } else {
                                ah.setEnabled();
                            }
                        }
                    }
                } else if (ah.isObject()) {
                    if (ah.getValueObject() != null)
                    {
                        if ( ah.getObject() != null )
                        {
                            Enumeration atts = ah.getObject().getAllAttributes()
                                                 .elements();

                            //Enumeration atts = this.getBridge(ah.getName()).getAllAttributes().elements();
                            while (atts.hasMoreElements()) {
                                ah = (AttributeHandler) atts.nextElement();

                                if (!ah.disableWhen()) {
                                    if (forRequest) {
                                        ah.setEnabledforRequest();
                                    } else {
                                        ah.setEnabled();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        this.isEnabled = true;

        if (forRequest) {
            this.isEnabledforRequest = true;
        }
    }

 public void setVisible() throws boRuntimeException {
        Enumeration oEnum = this.p_attributes.elements();

        while (oEnum.hasMoreElements()) {
            AttributeHandler ah = (AttributeHandler) oEnum.nextElement();

            if (ah.hiddenWhen()) {
                    ah.setVisible();
            }

            if (ah.isBridge()) {
                Enumeration atts = this.getBridge(ah.getName())
                                       .getAllAttributes().elements();

                while (atts.hasMoreElements()) {
                    ah = (AttributeHandler) atts.nextElement();

                    if (ah.hiddenWhen()) {
                            ah.setVisible();

                    }
                }
            } else if (ah.isObject()) {
                if (ah.getValueObject() != null) {
                    Enumeration atts = ah.getObject().getAllAttributes()
                                         .elements();

                    //Enumeration atts = this.getBridge(ah.getName()).getAllAttributes().elements();
                    while (atts.hasMoreElements()) {
                        ah = (AttributeHandler) atts.nextElement();

                        if (ah.hiddenWhen()) {
                                ah.setVisible();
                        }
                    }
                }
            }
        }

    }


    /**
     * END PRESENTATTION
     */

    // --- Bridge Calcalute Helpers
    public void calculateFormulaForBridge(String bridgeName,
        String attributeName) throws boRuntimeException {
        bridgeHandler bh = getBridge(bridgeName);
        boBridgeIterator it = bh.iterator();

        while (it.next()) {
            if (!it.currentRow().getObject().getBoDefinition().getBoCanBeOrphan()) {
                it.currentRow().getAttribute(attributeName).setValueObject(it.currentRow()
                                                                             .getAttribute(attributeName)
                                                                             .formula());
            }
        }
    }

    public void callObjBridgeCalculate(String from) throws boRuntimeException {
        boBridgesArray bridArr = getBridges();
        
        if( from != null && from.split("\\.").length > 5 ) {
        	return;
        }
        
        Enumeration oEnum = bridArr.elements();
        String fromAux = (from != null)
            ? ("parent_" + this.getBoDefinition().getName() + "." + from) : null;

        while (oEnum.hasMoreElements()) {
            bridgeHandler bh = (bridgeHandler) oEnum.nextElement();

            if ((bh.getDefAttribute().getType().indexOf("boObject") == -1) &&
                    !bh.getBoDef().getBoCanBeOrphan() &&
                    !bh.getBoDef().getBoHaveMultiParent()) {
                //bh.beforeFirst();
                boBridgeIterator it = bh.iterator();

                while (it.next()) {
                    it.currentRow().getObject().calculateFormula(fromAux);
                }
            }
        }
    }

    public boolean onChangeSubmitBridge(String attributeName) {
    	if( !getEboContext().isInModeBatch() ) {
	        try {
	            String fromAux = (attributeName != null)
	                ? ("parent_" + this.getBoDefinition().getName() + "." +
	                attributeName) : null;
	            String fromAux2 = getName() + "." + attributeName;
	            boAttributesArray boArr = getAttributes();
	            Enumeration oEnum = boArr.elements();
	            AttributeHandler attHandler;
	            bridgeHandler bh;
	
	            while (oEnum.hasMoreElements()) {
	                attHandler = (AttributeHandler) oEnum.nextElement();
	
	                if (attHandler.isBridge()) {
	                    if ((attHandler.getDefAttribute().getType().indexOf("boObject") == -1) &&
	                            !attHandler.getDefAttribute()
	                                           .getReferencedObjectDef()
	                                           .getBoCanBeOrphan() &&
	                            !attHandler.getDefAttribute()
	                                           .getReferencedObjectDef()
	                                           .getBoHaveMultiParent()) {
	                        bh = getBridge(attHandler.getName());
	
	                        //int rowPos=bh.getRow();
	                        //bh.beforeFirst();
	                        boBridgeIterator it = bh.iterator();
	
	                        while (it.next() &&
	                                (it.currentRow().getValue() == null)) {
	                        }
	
	                        if (!it.isAfterLast()) {
	                            if (itDepends(it.currentRow().getObject()
	                                                .dependencesFields(), fromAux)) {
	                                //      bh.moveTo( rowPos );
	                                return true;
	                            }
	                        }
	
	                        //bh.moveTo( rowPos );
	                    }
	                } else {
	                    if (attHandler.isObject()) {
	                    	boObject refObj = attHandler.getObject();
	                        if ( refObj != null) {
	                        	if( itDepends(  refObj.dependencesFields(), fromAux ) ) {
	                        		return true;
	                        	}
	                        }
	                    }
	                }
	            }
	
	            if (!this.getBoDefinition().getBoCanBeOrphan() &&
	                    !this.getBoDefinition().getBoHaveMultiParent()) {
	                if ((this.getParent() != null) &&
	                        itDepends(this.getParent().dependencesFields(), fromAux2)) {
	                    return true;
	                }
	            }
	
	            return parentDepends(attributeName);
	        } catch (boRuntimeException e) {
	            //ignore
	            logger.severe("", e);
	            return false;
	        }
    	}
    	return false;
    }

    public boolean parentDepends(String attributeName) {
        try {
            String fromAux2 = getName() + "." + attributeName;

            if (!this.getBoDefinition().getBoCanBeOrphan() &&
                    !this.getBoDefinition().getBoHaveMultiParent()) {
                if ((this.getParent() != null) &&
                        itDepends(this.getParent().dependencesFields(), fromAux2)) {
                    return true;
                }
            }
        } catch (boRuntimeException e) {
            //ignore
        }

        return false;
    }

    private boolean itDepends(String[] list, String attName) {
        if (list == null) {
            return false;
        }

        for (int i = 0; i < list.length; i++) {
            if (attName.equals(list[i])) {
                return true;
            }
        }

        return false;
    }

    public BigDecimal sumAttribute(String bridgeName, String attributeName,
        boolean bridgeAttribute) throws boRuntimeException {
        bridgeHandler bh = getBridge(bridgeName);
        BigDecimal ret = BigDecimal.valueOf(0);
        boBridgeIterator it = bh.iterator();

        //        int rowPos=bh.getRow();
        //        bh.beforeFirst();
        //        if (!bh.haveVL())
        //        {
        while (it.next()) {
            ret = bridgeAttribute
                ? ret.add((BigDecimal) it.currentRow()
                                         .getAttribute(attributeName)
                                         .getValueObject())
                : ret.add((BigDecimal) it.currentRow().getObject()
                                         .getAttribute(attributeName)
                                         .getValueObject());
        }

        //        }
        //        bh.moveTo( rowPos );
        return ret;
    }

    public BigDecimal sumAttribute(AttributeHandler ob, boolean bridgeAttribute)
        throws boRuntimeException {
        String s = ob.getName();
        bridgeHandler bh = getBridge(ob.getName());
        BigDecimal ret = BigDecimal.valueOf(0);
        boBridgeIterator it = bh.iterator();

        //        int rowPos=bh.getRow();
        //        bh.beforeFirst();
        //        if (!bh.haveVL())
        //        {
        while (it.next()) {
            ret = bridgeAttribute
                ? ret.add((BigDecimal) it.currentRow().getAttribute(s)
                                         .getValueObject())
                : ret.add((BigDecimal) it.currentRow().getObject()
                                         .getAttribute(s).getValueObject());
        }

        //        }
        //        bh.moveTo( rowPos );
        return ret;
    }

    public BigDecimal subtractAttribute(String bridgeName,
        String attributeName, boolean bridgeAttribute)
        throws boRuntimeException {
        bridgeHandler bh = getBridge(bridgeName);
        BigDecimal ret = BigDecimal.valueOf(0);
        boBridgeIterator it = bh.iterator();

        //        bh.beforeFirst();
        while (it.next()) {
            ret = bridgeAttribute
                ? ret.subtract((BigDecimal) it.currentRow()
                                              .getAttribute(attributeName)
                                              .getValueObject())
                : ret.subtract((BigDecimal) it.currentRow().getObject()
                                              .getAttribute(attributeName)
                                              .getValueObject());
        }

        return ret;
    }

    public boolean allCalculated(Hashtable hist) {
        Enumeration oEnum = hist.keys();
        String aux = null;

        while (oEnum.hasMoreElements()) {
            aux = (String) oEnum.nextElement();

            if (!"y".equals(hist.get(aux))) {
                return false;
            }
        }

        return true;
    }

    public boolean alreadyCalculated(Hashtable hist, String[] dependence) {
        for (int i = 0; (dependence != null) && (i < dependence.length); i++) {
            if (!"y".equals(hist.get(dependence[i]))) {
                return false;
            }
        }

        return true;
    }

    public boolean dependsFromWaiting(Hashtable hist, String[] dependence) {
        for (int i = 0; (dependence != null) && (i < dependence.length); i++) {
            if (isWaiting(hist, (dependence[i]))) {
                return true;
            }
        }

        return false;
    }

    public void setWaiting(Hashtable hist, String attName) {
        if ((attName != null) && (hist != null)) {
            hist.put(attName, "w");
        }
    }

    public boolean isWaiting(Hashtable hist, String attName) {
        if ((attName != null) && (hist != null)) {
            return "w".equals(hist.get(attName));
        }

        return false;
    }

    public void setCalculated(Hashtable hist, String attName) {
        if (attName != null) {
            hist.put(attName, "y");
        }
    }

    public void clear(Hashtable hist, String attName) {
        if (attName != null) {
            hist.remove(attName);
        }
    }

    public void setCalculated(Hashtable hist, String[] dependence) {
        for (int i = 0; (dependence != null) && (i < dependence.length); i++) {
            setCalculated(hist, dependence[i]);
        }
    }

    public boolean isCalculated(Hashtable hist, String attName) {
        if ((attName != null) && (hist != null)) {
            return "y".equals(hist.get(attName));
        }

        return false;
    }

    // once override not now
    public boolean onChangeSubmit(String attributeName) {
        // Overrided on generated objects
        if(p_dependencesFields != null)
        {
            if(p_dependencesFields.contains(attributeName))
            {
                return true;
            }
        }
        return onChangeSubmitBridge(attributeName);
    }

    public String[] dependencesFields()
    {
        String[] ret = null;
        if( p_dependencesFields != null )
        {
            ret = (String[])p_dependencesFields.toArray( new String[ p_dependencesFields.size() ] );
        }
        return ret;
    }

    public void calculateFormula() throws boRuntimeException {
        calculateFormula(new Hashtable(), null);
    }

    public void calculateFormula(String from) throws boRuntimeException {
        calculateFormula(new Hashtable(), from);
    }

    public void calculateFormula(Hashtable table, String from)
        throws boRuntimeException {
        // Overrided on generated objects
        return;
    }

    public boolean methodIsHidden(String methodName) throws boRuntimeException {
        return false;
    }

    public boolean havePoolChilds() {
        return false;
	}

    public void setSendRedirect(String to)
    {
        this.sendRedirect = to;
    }

    public String getSendRedirect()
    {
        return sendRedirect;
    }

    public void cleanSendRedirect()
    {
        sendRedirect = null;
    }
    public boolean isParentInTemplateMode()
    {
        boolean result = false;
        byte mode;
        try
        {
            boObject parent = this.getParent();
            if(parent != null)
            {
                mode = parent.getMode();
                if(MODE_EDIT_TEMPLATE == mode)
                {
                    result = true;
                }
            }
        }
        catch (Exception ex)
        {
        }
        return result;
    }
    public void removeTemplate() throws boRuntimeException
    {
        AttributeHandler attHandler =  this.getAttribute("TEMPLATE");
        if(attHandler != null)
        {
            attHandler.setValueObject(null);
        }
    }

    public String[] addDefaultDependencesFields()
    {
        return  null;
    }

    public void addDependenceField( String attributeName )
    {
        if( p_dependencesFields == null )
        {
            p_dependencesFields = new ArrayList();
        }
        p_dependencesFields.add( attributeName );
     }

    public ObjectBinary getObjectBinary()
    {
        if( this.objectBinary == null )
        {
            try
            {
                objectBinary = (ObjectBinary)Class.forName( bo_definition.getObjectBinaryClass() ).newInstance();
                objectBinary.setContextObject( this );
            }
            catch (Exception e){}
        }
        return objectBinary;
    }
    public ObjectRes getObjectRes()
    {
        if( this.objectRes == null )
        {
            try
            {
                objectRes = (ObjectRes)Class.forName( bo_definition.getObjectResClass() ).newInstance();
                objectRes.setContextObject( this );
            }
            catch (Exception e){}
        }
        return objectRes;
    }
    private static String getValue(boObject parent, AttributeHandler attr) throws boRuntimeException
    {
        if("boolean".equalsIgnoreCase(attr.getDefAttribute().getType()))
        {
            String value = attr.getValueString();
            if("0".equals(value))
            {
                //falta verificar a lingua
                return CARD_NAO;
            }
            else if("1".equals(value))
            {
                return CARD_SIM;
            }
            return value;
       }
       else if(attr.getDefAttribute().getLOVName() != null &&
                !"".equals(attr.getDefAttribute().getLOVName()))
       {
            String xlov = attr.getDefAttribute().getLOVName();
            String value = attr.getValueString();
            if(value != null && !"".equals(value))
            {
                lovObject lovObj = LovManager.getLovObject(attr.getParent().getEboContext(), xlov);
                if(lovObj != null)
                {
                    lovObj.beforeFirst();
                    while(lovObj.next())
                    {
                        if(value.equalsIgnoreCase(lovObj.getCode()))
                        {
                            return lovObj.getDescription();
                        }
                    }
                }
            }
            return attr.getValueString();
         }
         else if("dateTime".equalsIgnoreCase(attr.getDefAttribute().getType()))
         {
            Date d = null;
            if((d = attr.getValueDate()) != null)
            {
                 SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
                 return formatter.format(d);
            }
            return "";
         }
         else if("date".equalsIgnoreCase(attr.getDefAttribute().getType()))
         {
            Date d = null;
            if((d = attr.getValueDate()) != null)
            {
                 SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy");
                 return formatter.format(d);
            }
            return "";
         }
//         else if("operator".equals(attr.getName()) && attr.getParent() != null && "Ebo_FilterQuery".equals(attr.getParent().getName()))
//         {
//            return getOperatorValue(attr.getValueString());
//         }
//         else if("joinQuery".equals(attr.getName()) && attr.getParent() != null && "Ebo_FilterQuery".equals(attr.getParent().getName()))
//         {
//            return getJoinValue(attr.getValueString());
//         }
//         else if("attributeName".equals(attr.getName()) && attr.getParent() != null && "Ebo_FilterQuery".equals(attr.getParent().getName()))
//         {
//            return getAttributeDescription(parent, attr);
//         }
//         else if("value".equals(attr.getName()) && attr.getParent() != null && "Ebo_FilterQuery".equals(attr.getParent().getName()))
//         {
//            StringBuffer v = new StringBuffer();
//            if(getAttributeValue(parent, attr, v))
//            {
//                sb.append("Y");
//            }
//            return v.toString();
//         }
         else
         {
            NumberFormat currencyFormatter = NumberFormat.getInstance();
            if(attr.getDefAttribute().getDecimals() != 0)
            {
                //currency
                currencyFormatter.setParseIntegerOnly(false);
                if(CARD_Y.equalsIgnoreCase(attr.getDefAttribute().getGrouping()))
                {
                    currencyFormatter.setGroupingUsed(true);
                }
                currencyFormatter.setMaximumFractionDigits(attr.getDefAttribute().getDecimals());
                currencyFormatter.setMinimumFractionDigits(attr.getDefAttribute().getMinDecimals());
                currencyFormatter.setMinimumIntegerDigits(1);
                return currencyFormatter.format(attr.getValueDouble());
            }
            else if(CARD_Y.equalsIgnoreCase(attr.getDefAttribute().getGrouping()))
            {
                currencyFormatter.setParseIntegerOnly(false);
                currencyFormatter.setMinimumIntegerDigits(1);
                currencyFormatter.setGroupingUsed(true);
                return currencyFormatter.format(attr.getValueDouble());
            }
            if(attr.getValueString() != null && "".equals(attr.getValueString().trim()))
            {
                return "";
            }
            return attr.getValueString();
         }
    }

    public ObjectVersionControl getObjectVersionControl()
    {
        if( this.objectVersionControl == null && getBoDefinition().haveVersionControl())
        {
            try
            {
                objectVersionControl = (ObjectVersionControl)Class.forName( bo_definition.getObjectVersionControlClass() ).newInstance();
                objectVersionControl.setContextObject( this );
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return objectVersionControl;
    }
    public void checkOut() throws boRuntimeException
    {
        if( this.getObjectVersionControl() != null )
        {
            this.objectVersionControl.checkOut();
        }
    }
    public void undoCheckOut() throws boRuntimeException
    {
        if( this.getObjectVersionControl() != null )
        {
            if(this.objectVersionControl.undoCheckOut())
            {
                this.p_ischanged = false;
            }
        }
    }
    public void checkIn() throws boRuntimeException
    {
        if( this.getObjectVersionControl() != null )
        {
            this.objectVersionControl.checkIn();
        }
    }
    /**
     * Para retirar daqui para um nova classe que controla os ficheiros
     */
    private boolean beforeSaveIFiles() throws boRuntimeException
    {
    	//OLD CODE
    	List iFilesAttributes = getAttributes(boDefAttribute.VALUE_IFILELINK);
        for (int i = 0; i < iFilesAttributes.size(); i++)
        {
        	uploadFile((AttributeHandler)iFilesAttributes.get(i));
    	}  
    	return true;
    	
    	//Keep the repository name to use, can be the default or
    	//another one
    	/*String repositoryName = null;
    	
    	//Get the default repository name
    	RepositoryConfig repository = boConfig.getApplicationConfig().getDefaultECMRepositoryConfiguration();
    	if (repository != null)
    		repositoryName = repository.getName();
    	else 
    		throw new boRuntimeException2("There's no configuration for ECM Repositories");
    	//Iterate all file attributes 
        List iFilesAttributes = getAttributes(boDefAttribute.VALUE_IFILELINK);
        for (int i = 0; i < iFilesAttributes.size(); i++)
        {
        	//Retrieve the current attribute handler
        	AttributeHandler currHandler = (AttributeHandler) iFilesAttributes.get(i);
        	//Check if we have an JCR Repository linked to this attribute
        	
        	if (currHandler.getDefAttribute().getDocumentDefinitions() != null)
        	{
        		/*String overrideName = currHandler.getDefAttribute().getDocumentDefinitions().getRepositoryName();
        		if (overrideName != null)
        			repositoryName = overrideName;
        		//Session for the repository
            	Session current = this.getEboContext().getBoSession().
        				getECMRepositorySession(repositoryName);
            	
            	iFile currentFile = currHandler.getValueiFile();
            	currentFile.save();
            	
            }
        	else //If not, it's a regular binary attribute
        		uploadFile((AttributeHandler)iFilesAttributes.get(i));
        	
        }
        return true;*/
    }
    /**
     * Devolve uma lista com os <code>AttributeHandler</code> do tipo pretendido
     * @param type tipo de attributo (<code>boDefAttribute</code>)
     * @return lista com os <code> AttributeHandler </code> do tipo pretendido
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public List getAttributes(byte attributeType) throws boRuntimeException
    {
        List result = new ArrayList();
        AttributeHandler attHandler = null;
        Enumeration oEnum = this.getAttributes().elements();
        while( oEnum.hasMoreElements()  )
        {
            attHandler = (AttributeHandler)oEnum.nextElement();
            if( attHandler.getDefAttribute().getValueType() == attributeType )
            {
                result.add(attHandler);
            }
        }
        return result;
    }
    /**
     * Para retirar daqui para um nova classe que controla os ficheiros
     */
    protected boolean uploadFile(AttributeHandler iFileAttribute) throws boRuntimeException
    {
        boolean result = false;
        String fileUri = iFileAttribute.getValueString();
        if( fileUri != null && fileUri.length() > 0 )
        {
            //String uri = file.getURI();
            if(
                fileUri != null && fileUri != null && 
                //!fileUri.startsWith("//"+AppxiFile.IFILE_SERVICE_NAME) && 
                !fileUri.startsWith("//"+DBiFile.IFILE_SERVICE_NAME) && 
                !fileUri.startsWith("//"+BasiciFile.IFILE_SERVICE_NAME)
            )
            {
                try
                {
                    iFileServer fs = new iFileServer();
                    fs.mount();
                    iFile file = iFileAttribute.getValueiFile();
                    String filedir = "//" + BasiciFile.IFILE_SERVICE_NAME + "/" + file.getName();
                    iFile xfiledir = fs.getFile(filedir);
                    if(!xfiledir.exists())
                    {
                        InputStream  inp=file.getInputStream();
                        xfiledir.setBinaryStream(inp);
                        inp.close();
                        iFileAttribute.setValueiFile(xfiledir);
                        result = true;
                    }
                }
                catch (IOException e)
                {
                    throw new RuntimeException("Erro updating file.\n"+e.getClass().getName()+"\n"+e.getMessage());
                }
                catch (iFilePermissionDenied e)
                {
                    throw new RuntimeException("Erro updating file.\n"+e.getClass().getName()+"\n"+e.getMessage());
                }
            }
        }
        return result;
   }

  public void setMyTransaction(boolean myTransaction)
  {
    this.myTransaction = myTransaction;
  }


  public boolean isMyTransaction()
  {
    return myTransaction;
  }   
}
