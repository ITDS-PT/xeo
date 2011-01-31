/*Enconding=UTF-8*/
package netgest.bo.def.v2;
import java.util.ArrayList;

import java.util.Hashtable;
import java.util.Vector;
import netgest.bo.boException;
import netgest.bo.builder.boBuildDBUtils;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefClsEvents;
import netgest.bo.def.boDefDataTypeMapping;
import netgest.bo.def.boDefDocument;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefMethod;
import netgest.bo.def.boDefObjectFilter;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.def.v2.boDefBridgeImpl;
import netgest.bo.def.v2.boDefHandlerImpl;
import netgest.bo.runtime.boRuntimeException2;
import netgest.bo.transformers.Transformer;

import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;

import oracle.xml.parser.v2.XMLDocument;
import netgest.bo.system.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class boDefAttributeImpl extends ngtXMLHandler implements boDefAttribute
{

    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.def.boDefAttribute");

    public static final byte TYPE_ATTRIBUTE=0;
    public static final byte TYPE_OBJECTATTRIBUTE=1;
    public static final byte TYPE_STATEATTRIBUTE=2;
    
    
    public static final byte NO_RELATION=0;
    public static final byte RELATION_1_TO_1=1;
    public static final byte RELATION_1_TO_N=2;
    public static final byte RELATION_1_TO_N_WBRIDGE=3;
    public static final byte RELATION_MULTI_VALUES=4;
    
    public static final byte RELATED_OBJECT_ORPHAN_NO=0;
    public static final byte RELATED_OBJECT_ORPHAN_YES=1;

    public static final byte CAN_READ=1;
    public static final byte CAN_WRITE=2;
    public static final byte CAN_DELETE=4;
    public static final byte CAN_ADD=8;
    
    public static final byte VALUE_UNKNOWN=0;
    public static final byte VALUE_CHAR=9;
    public static final byte VALUE_BOOLEAN=1;
    public static final byte VALUE_DURATION=2;
    public static final byte VALUE_CURRENCY=3;
    public static final byte VALUE_NUMBER=4;
    public static final byte VALUE_DATETIME=5;
    public static final byte VALUE_DATE=6;
    public static final byte VALUE_CLOB=7;
    public static final byte VALUE_BLOB=8;
    public static final byte VALUE_IFILELINK=10;
    public static final byte VALUE_SEQUENCE=11;
    
    public static final byte SET_PARENT_YES=0;
    public static final byte SET_PARENT_NO=1;
    public static final byte SET_PARENT_DEFAULT=2;

    private boDefHandlerImpl    p_defhandler;
    
    // General
    private String p_name        = null;
    private String p_type        = null;
    private String p_declaredType = null;
    private byte   p_attType;
    private byte   p_valueType;
    private String p_label       = null;
    private String p_description = null;
    private String p_tooltip     = null;
    
    private boolean p_showLookup  = false;
    private Boolean p_orphanRelation 	= null;
    
    private String  p_bh_image    = null;
    private String  p_bh_script   = null;
    
    
    private int p_len         = 10;
    private int p_decimals    = 0;
    private int p_mindecimals = 0;
    
    private int p_maxOccurs   = 1;
    private int p_minOccurs   = 0;
    
    private int p_runtimeMaxOccurs;
    
    // Render Options
    private boolean p_clock         = false;
    private boolean p_renderAsLov   = false;
    private boolean p_renderAsCheck = false;
    private String  p_editorType;
    

    // TextIndex
    private boolean p_textIndex_active          = true;
    private boolean p_textIndex_appendChilds    = false;
    private boolean p_textIndex_onlyCardId      = false;
    private int     p_textIndex_deep            = 1;

    // Database
    private String  	p_db_fieldname  	= null;
    private boolean 	p_db_unique     	= false;
    private boolean 	p_db_tabled     	= false;
    private boolean 	p_db_indexed    	= false;
    private boolean 	p_db_binding    	= true;
    private boolean 	p_db_constraint 	= true;
    private boolean 	p_db_required   	= false;
    private String[] 	p_db_relationKeys	= null;
    
    // Exclusivos do tipo objecto
    private String          p_referencedObjectName;
    private boDefHandler[]  p_objects;
    private String[]        p_objectsName;
    private boDefBridgeImpl p_bridge;

    private boDefObjectFilterImpl[] p_objectFilters;
    
    private String  p_transformer_from;
    private String  p_transformer_class;
    
    private byte    p_setparent = SET_PARENT_DEFAULT;
    
    
    // Exclusivos do tipo numerico
    private long    p_max = Long.MAX_VALUE;
    private long    p_min = Long.MIN_VALUE;
    private boolean p_digit_grouping = false;
    
    
    // Bridge Behavior;
    boolean p_bridge_manual_operation = true;    
    boolean p_bridge_manual_add       = true;
    boolean p_bridge_manual_create    = true;
    
    // LOV's 
    private String              p_lovName           = null;
    private String              p_lovSql           	= null;
    
    private String              p_lovSqlIdField     = null;
    private String              p_lovSqlDescField   = null;

    private boolean             p_lovRetainValues   = false;
    private boDefXeoCodeImpl    p_lovCanChange      = null;
    private boDefXeoCodeImpl    p_lovCondition      = null;
    private ngtXMLHandler       p_lovItems          = null;
    
    // Metodos    
    private boDefMethodImpl[]   p_methods;

    // Eventos / Formulas
    private boDefXeoCodeImpl p_required       = null;
    private boDefXeoCodeImpl p_recomended     = null;
    private boDefXeoCodeImpl p_valid          = null;
    private boDefXeoCodeImpl p_defaultValue   = null;
    private boDefXeoCodeImpl p_disableWhen    = null;
    private boDefXeoCodeImpl p_onChangeSubmit = null;
    private boDefXeoCodeImpl p_totalRefresh = null;
    private boDefXeoCodeImpl p_hiddenWhen     = null;    
    private boDefXeoCodeImpl p_formula        = null;
    
    private boDefClsEvents[] p_events = null;  
    private Hashtable        p_eventsHash = null;
    //Document definitions
    private boDefDocument 	p_documentDef;

    public boDefAttributeImpl(boDefHandlerImpl bodef,Node x) 
    {
        super(x);        
        p_defhandler = bodef;
        parse();
    }
    
    private void parse() 
    {
        String nodeName = getNodeName();
        if( !(this instanceof boDefClsStateImpl) )
        {
            p_name           = getAttribute("name");
            
            p_len            = GenericParseUtils.parseInt( getChildNodeText("len","10") );
            p_decimals       = GenericParseUtils.parseInt( getChildNodeText("decimals","0") );
    
            p_declaredType   = getNodeName();
            p_type           = GeneralParseUtils.parseAttributeNode( getNodeName(), getChildNodeText( "type" , null ), p_len, p_decimals );
            if( p_type == null )
            {
                logger.severe("Not valid:" + p_type );
            }
            p_attType        = p_type.indexOf("object.")==0?TYPE_OBJECTATTRIBUTE:TYPE_ATTRIBUTE;
            
            p_mindecimals    = GenericParseUtils.parseInt( getChildNodeText("minDecimals","0") );
            p_label          = getChildNodeText("label", p_name );
            p_valueType      = GeneralParseUtils.parseValueType( p_type );
    
            // Object Specific attributes
            if( p_attType == TYPE_OBJECTATTRIBUTE ) p_referencedObjectName = p_type.substring(7);
    
            p_setparent      = GeneralParseUtils.parseSetParent( getChildNodeText("setParent", null ) );
            
            p_minOccurs         = GenericParseUtils.parseInt( getChildNodeText( "minOccurs", "0" ) );
            p_maxOccurs         = GenericParseUtils.parseIntOrN( getChildNodeText( "maxOccurs", "1" ) );
            p_runtimeMaxOccurs  = GenericParseUtils.parseIntOrN( getChildNodeText( "runtimeMaxOccurs", String.valueOf( p_maxOccurs ) ) );
            p_renderAsLov       = GenericParseUtils.parseBoolean( getChildNodeText("renderAsLov","false") );
            p_renderAsCheck     = GenericParseUtils.parseBoolean( getChildNodeText("renderAsCheck","false") );
    
            p_editorType     = getChildNodeText("editor","text").toLowerCase();
            
            p_showLookup     = GenericParseUtils.parseBoolean( getChildNodeText("showLookup","false") );
            
            String sChildIsOrphan = getChildNodeText("orphanRelation", null );
            if( sChildIsOrphan != null ) {
            	p_orphanRelation     = GenericParseUtils.parseBoolean( sChildIsOrphan )?Boolean.TRUE:Boolean.FALSE;
            }
            
            // Numeric Specific attributes
            p_max           = GenericParseUtils.parseLong( getChildNodeText("max", String.valueOf( Long.MAX_VALUE ) ) );
            p_min           = GenericParseUtils.parseLong( getChildNodeText("min", String.valueOf( Long.MIN_VALUE ) ) );
            p_digit_grouping= GenericParseUtils.parseBoolean( getChildNodeText("grouping", String.valueOf( Long.MIN_VALUE ) ) );
            
            // Bridge Beahvior
            p_bridge_manual_operation = GenericParseUtils.parseBoolean( getChildNodeText("manualOperation","true") );
            p_bridge_manual_add       = GenericParseUtils.parseBoolean( getChildNodeText("manualAdd","true") );
            p_bridge_manual_create    = GenericParseUtils.parseBoolean( getChildNodeText("manualCreate","true") );
            
            //Parse Behavior
            ngtXMLHandler node=super.getChildNode("behavior");
            if ( node != null )
            {
                p_bh_image      = node.getChildNode("img").getText();
                p_bh_script     = node.getChildNode("script").getText();
            }
            
            //Parse TextIndex
            node = super.getChildNode("textIndex");
            if( node != null )
            {
                p_textIndex_active      = GenericParseUtils.parseBoolean( node.getChildNodeText( "active", "true" ) );
                p_textIndex_appendChilds= GenericParseUtils.parseBoolean( node.getChildNodeText( "appendChilds", "false" ) );
                p_textIndex_deep        = GenericParseUtils.parseInt( node.getChildNodeText( "deep", "0" ) );
                p_textIndex_onlyCardId  = GenericParseUtils.parseBoolean( node.getChildNodeText( "onlyCardId", "false" ) );
            }
             
            // Parse Database Mode
            node = super.getChildNode("database");
            if(node != null) 
            {
                if( p_attType == TYPE_OBJECTATTRIBUTE ) 
                    p_db_fieldname = GeneralParseUtils.parseDbFieldName( node.getChildNodeText("fieldname", p_name+"$" ), p_name+"$" ).toUpperCase();
                else
                    p_db_fieldname = GeneralParseUtils.parseDbFieldName( node.getChildNodeText("fieldname", p_name ), p_name ).toUpperCase();
                
    //            p_db_indexfull = GenericParseUtils.parseBoolean( node.getChildNodeText("indexfull", "true" ) );
                p_db_unique    = GenericParseUtils.parseBoolean( node.getChildNodeText("unique", "false" ) );
                p_db_tabled    = GenericParseUtils.parseBoolean( node.getChildNodeText("tabled", "false" ) );
                p_db_indexed   = GenericParseUtils.parseBoolean( node.getChildNodeText("indexed", "false" ) );
                p_db_binding   = GenericParseUtils.parseBoolean( node.getChildNodeText("binding", "true" ) );
                p_db_constraint= GenericParseUtils.parseBoolean( node.getChildNodeText("constraint", "true" ) );
                p_db_required  = GenericParseUtils.parseBoolean( node.getChildNodeText("required", "false" ) );
                
                ngtXMLHandler relationNode = node.getChildNode("relation");
                if( relationNode != null ) {
                	ngtXMLHandler[] h = relationNode.getChildNodes();
                	p_db_relationKeys = new String[ h.length ];
                	for( int k=0;k < h.length; k++) {
                		p_db_relationKeys[k] = h[k].getText();
                	}
                }
            }
            else
            {
                // Fill default values
                p_db_fieldname = p_name.toUpperCase();
                if( p_attType == TYPE_OBJECTATTRIBUTE ) p_db_fieldname += "$";
            }
            
            // Parse Methods
            node = super.getChildNode("methods");
            if(node != null) 
            {
                ngtXMLHandler[] mths = node.getChildNodes();
                if(mths!=null) 
                {
                    p_methods = new boDefMethodImpl[mths.length];
                    for(byte i=0;i<mths.length;i++) 
                    {
                        p_methods[i]=new boDefMethodImpl( p_defhandler, mths[i].getNode(), this );
                    }
                }
            }
            
            // Node Objects
            node = super.getChildNode("objects");
            if(node != null) 
            {
                ngtXMLHandler[] objs = node.getChildNodes();
                if(objs!=null) 
                {
                    p_objectsName = new String[ objs.length ];
                    Vector relObjects = new Vector( objs.length );
                    for(byte i=0;i<objs.length;i++)
                    {
                        if( !p_defhandler.getName().equals(objs[i].getText()) )
                        {
                            p_objectsName[i] = objs[i].getText();
                            if ( boDefHandler.getBoDefinition( objs[i].getText() )!=null )
                            {
                                if(!this.getName().equals(objs[i].getText()))
                                {
                                    relObjects.add( (boDefHandlerImpl)boDefHandlerImpl.getBoDefinition( objs[i].getText() ) );
                                }
                                else
                                {
                                    relObjects.add( p_defhandler );
                                }
                            }
                        }
                        else 
                        {
                            relObjects.add( p_defhandler );
                        }
                    }
                    p_objects = (boDefHandler[])relObjects.toArray( new boDefHandler[ relObjects.size() ] );
                }
            }
            
            // LOV's
            p_lovName="";
            node = getChildNode( "lov" );
            if( node != null )
            {
                p_lovName         = node.getAttribute("name");
                
                ngtXMLHandler sqlNode = node.getChildNode( "sql" );
                
                if( sqlNode != null ) {
                	p_lovSql         	= sqlNode.getText();
                	p_lovSqlIdField  	= sqlNode.getAttribute("id","");
                	p_lovSqlDescField  	= sqlNode.getAttribute("description","");
                }
                
                p_lovRetainValues = GenericParseUtils.parseBoolean( node.getAttribute("retainValues") ); 
                p_lovCanChange    = GeneralParseUtils.parseCode( node.getChildNode( "editable" ) );    
                p_lovCondition    = GeneralParseUtils.parseCode( node.getChildNode( "lovCondition" ) );    
                p_lovItems        = node.getChildNode("items");
            }
            
            // Parse properties that can have code;
            p_onChangeSubmit = GeneralParseUtils.parseCode( getChildNode( "onChangeSubmit" ) );
            p_totalRefresh = GeneralParseUtils.parseCode( getChildNode( "totalRefresh" ) );
            p_formula        = GeneralParseUtils.parseCode( getChildNode( "formula" ) );
            p_hiddenWhen     = GeneralParseUtils.parseCode( getChildNode( "hiddenWhen" ) );
            p_defaultValue   = GeneralParseUtils.parseCode( getChildNode( "defaultValue" ) );
            p_valid          = GeneralParseUtils.parseCode( getChildNode( "valid" ) );
            p_disableWhen    = GeneralParseUtils.parseCode( getChildNode( "disableWhen" ) );
            p_required       = GeneralParseUtils.parseCode( getChildNode( "required" ) );
            p_recomended     = GeneralParseUtils.parseCode( getChildNode( "recomended" ) );
            
            // Object Specific attributes
    
            // Object Filter
            if( "gestor".equals( p_name ) )
            {
                boolean tobreak = true;
            }
            node = getChildNode( "objectFilter" );
            if( node != null ) 
            {
                ngtXMLHandler[] filterNodes = node.getChildNodes();
                
                this.p_objectFilters = new boDefObjectFilterImpl[ filterNodes.length ];
                
                for (int i = 0; i < filterNodes.length; i++) 
                {
                    boDefXeoCodeImpl code   = GeneralParseUtils.parseCode( filterNodes[i].getChildNode("condition") );
                    String  xeoql           = filterNodes[i].getChildNodeText("xeoql",null);
                    String  forObject       = filterNodes[i].getAttribute("forObject","");
                    this.p_objectFilters[i] = new boDefObjectFilterImpl( forObject, xeoql, code );
                }
            }
            
            // Object transformer
            node = getChildNode( "transformer" );
            if( node != null )
            {
                p_transformer_from  = node.getChildNodeText( "from", null );
                p_transformer_class = node.getChildNodeText( "class", null );
            }
    
            // Parse Bridge
            if( this.getAtributeType() == TYPE_OBJECTATTRIBUTE && this.getMaxOccurs() > 1 )
            {
                p_bridge = new boDefBridgeImpl(this);
            }
            
            // Parse Events
            node = getChildNode( "events" );
            if( node != null )
            {
                ngtXMLHandler[] nodeEvent = node.getChildNodes();
                p_eventsHash = new Hashtable( nodeEvent.length );
                Vector events = new Vector( nodeEvent.length );
                for (int i = 0; i < nodeEvent.length; i++) 
                {
                    boDefClsEvents att_event = new boDefClsEventsImpl( p_defhandler, nodeEvent[i].getNode(), p_name );
                    events.add( att_event );
                    p_eventsHash.put( att_event.getEventName().toUpperCase().substring(2), att_event );
                }
                p_events = (boDefClsEvents[])events.toArray( new boDefClsEvents[ events.size() ] );
            }
            
            //Binary Documents (boDefDocument)
            node = getChildNode("ecmRepository");
            if (node != null)
            p_documentDef = new boDefDocumentImpl(node);
            
        }
        
    }
    
    public boDefClsEvents[] getEvents()
    {
        return p_events;
    }
    
    public boDefClsEvents getEvent( String name )
    {
        if( p_eventsHash != null )
        {
            return (boDefClsEvents)p_eventsHash.get( name );
        }
        return null;
    }
    
    public boDefMethod[] getMethods() 
    {
        return p_methods;        
    }
    
    public boDefHandler[] getObjects() 
    {
        return p_objects;        
    }

    public String[] getObjectsName() 
    {
        return p_objectsName;        
    }
    
    public boDefObjectFilter[] getObjectFilter()
    {
        return p_objectFilters;
    }

    public boDefObjectFilter getObjectFilter( String forObject )
    {
        boDefObjectFilter ret = null;
        for (int i = 0;p_objectFilters!= null && i < p_objectFilters.length; i++) 
        {
            if( "".equals( p_objectFilters[i].getForObject() )  )
            {
                ret = p_objectFilters[i];
            }
            if( forObject.equals( p_objectFilters[i].getForObject() ) )
            {
                ret = p_objectFilters[i];
                break;
            }
        }
        return ret;
    }

    public boDefHandler[] getTransformObjects()
    {
        boDefHandler[] toRet = null;
        if(getTransformObject() != null && !"".equals(getTransformObject()))
        {
            String transfObj = getTransformObject();
            int st = 0;
            if((st = transfObj.toLowerCase().indexOf("interface")) != -1)
            {
                transfObj =  transfObj.substring(st + 10);
                String[] objsName = boDefInterfaceImpl.getInterfaceDefinition(transfObj).getImplObjects();
                toRet = new boDefHandler[objsName.length];
                for (int i = 0; i < objsName.length; i++) 
                {
                    toRet[i] = boDefHandler.getBoDefinition(objsName[i]);
                }
            }
            else if((st = transfObj.toLowerCase().indexOf("object")) != -1)
            {
                transfObj =  transfObj.substring(st + 7);                
                toRet = new boDefHandler[]{boDefHandler.getBoDefinition(transfObj)};
            }
            
        }
        return toRet;
    }

    public Transformer getTransformClassMap() throws boRuntimeException2
    {
        if(getTransformClassMapName() != null && !"".equals(getTransformClassMapName()))
        {
            try
            {
                try
                {
                    Class c = Class.forName(getTransformClassMapName());
                    return (Transformer)c.newInstance();
                }
                catch (InstantiationException e)
                {
                    logger.severe("Transformer não identificado (" + getTransformClassMapName() + ")");
                    throw new boRuntimeException2("Transformer não identificado (" + getTransformClassMapName() + ")");
                }
                catch (IllegalAccessException e)
                {
                    logger.severe("Transformer não identificado (" + getTransformClassMapName() + ")");
                    throw new boRuntimeException2("Transformer não identificado (" + getTransformClassMapName() + ")");
                }
            }
            catch (ClassNotFoundException e)
            {
                logger.severe("Interface Class Map (" + getTransformClassMapName() + ") not found");
                throw new boRuntimeException2("Interface Class Map (" + getTransformClassMapName() + ") not found");
            }
        }
        return null;
    }

    public boolean getChildIsOrphan()
    {
    	return getChildIsOrphan( getReferencedObjectName() );
    }
    
    public boolean getChildIsOrphan( String objectName )
    {
    	if ( p_orphanRelation == null ) {
	        boDefHandler refdef = boDefHandler.getBoDefinition( objectName );
	        if( refdef != null )
	        {
	            if( refdef.getBoCanBeOrphan() )
	            {
	                 return true;
	            }
	            else 
	            {
	                return false;
	            }
	        }
	        else
	        {
	            // Retornae o quÊ??? o objecto relacionado não exite.
	            return true;
	        }
    	} else {
    		return p_orphanRelation.booleanValue();
    	}
    }

    public byte getSetParent()
    {
        return p_setparent;
    }
    
    public boolean getShowLookup()
    {
        return p_showLookup;
    }

    public byte getRelationType()
    {
        byte toRet = NO_RELATION; 
        if( getReferencedObjectDef()!=null )
        {
            if( this.getDbIsTabled() )
            {
                return RELATION_MULTI_VALUES;
            }
            else
            {
                if( getMaxOccurs() > 1 )
                {
                    // Se existirem atributos na Bridge é sempre criada uma bridge.
                    if ( getReferencedObjectDef().getBoCanBeOrphan() || this.getBridge().haveBridgeAttributes() )
                    {
                        toRet = RELATION_1_TO_N_WBRIDGE;
                    }
                    else
                    {
                        toRet = RELATION_1_TO_N_WBRIDGE;
                    } 
                }
                else
                {
                    toRet = RELATION_1_TO_1;
                }
            }
        }
        return toRet;
    }
    
    public String getName() 
    {
        return p_name;
    }
    
    public boolean getDbIsBinding() 
    {
        return p_db_binding;
    }
    
    public boolean getDbRequired()
    {
        return p_db_required;
    }
    
    public String[] getDbRelationKeys() {
    	return p_db_relationKeys;    	
    }
    
    public boolean getDbIsUnique() 
    {
        return p_db_unique;
    }
    
//    public boolean getDbIsFullTextIndexed() 
//    {
//        return p_db_indexfull;
//    }
//    
    public boolean getDbIsIndexed() 
    {
        return p_db_indexed;
    }

    public boolean getDbIsTabled() 
    {
        return p_db_tabled;
    }
    
    public String getDbTableChildFieldName()
    {
        return this.getDbName();
    }
    
    public String getDbTableFatherFieldName()
    {
        return "T$PARENT$";
    }
    
    public String getDbTableName() 
    {
        return (p_defhandler.getBoPhisicalMasterTable() + "$" + getDbName()).toUpperCase();        
    }

    public String getBeahvior_Img()
    {
        return p_bh_image;
    }

    public String getBeahvior_Script()
    {
        return p_bh_script;
    }
    
    public String getTableName()
    {
        return p_defhandler.getBoMasterTable( ) + "$" + getDbName( );        
    }

    public String getDbName() 
    {
        return p_db_fieldname;
    }
    
    public boolean getDbCreateConstraints()
    {
        return p_db_constraint;
    }
    
    public String getType() 
    {
        return p_type;     
//TODO: Não está a fazer o throw desta excepção.        
/*         if ( p_type == null )
         {
             ngtXMLHandler xmlh = super.getChildNode("type");
             if(xmlh==null) {
                 throw new boException(this.getClass().getName()+".getType()","BO-1403",null,this.getName());
             }
             p_type = xmlh.getText();
             if ( p_type == null ) throw new boException(this.getClass().getName()+".getType()","BO-1403",null,this.getName());
             
         }
         return p_type;
*/         
    }
    public String[] getTypeArguments()
    {
//TODO:Optimizar isto,.    
        String type = getChildNodeText( "type" , "sequence" );
        ArrayList args = new ArrayList();
        StringBuffer word = new StringBuffer();
        byte deep=0;
        for (int i = 0; i < type.length(); i++) 
        {
            switch  ( type.charAt(i) )
            {
                case '(':
                case '[':
                case '{':
                    deep++;
                    break;
                case ')':
                case ']':
                case '}':
                    deep--;
                    break;
                case ',':
                    if( deep == 1 )
                    {
                        args.add( word.toString() );
                        word.delete(0,word.length());
                    }
                    break;
                default:    
                    if( deep > 0 )
                    {
                        word.append( type.charAt( i ) );
                    }
            }
        }
        if( word.length() > 0 )
        {
            args.add( word.toString().trim() );
        }
        return (String[])args.toArray( new String[ args.size() ] );
    }
     
    public int getLen()
    {  
        return p_len;
/*            
        String[] ft = boBuildDBUtils.parseFieldType(getType());
        return ClassUtils.convertToInt(ft[1]);*/
    }
    
    public String getSizeToRender()
    {
        String type = getType();
        String ret="10";
        if (type.startsWith("OBJECT.")) {
            ret = "10";
        } else if(type.startsWith("CHAR")) {
            ret = type.substring(4, type.indexOf(')')) ;
        } else if (type.startsWith("BOOLEAN")) {
            ret = "1";
        } else if (type.startsWith("DURATION")) {
            ret = "10";
        } else if (type.startsWith("CURRENCY")) {
            ret = "10";
        } else if (type.startsWith("NUMBER")) {
            ret = "10";
        } else if (type.startsWith("DATETIME")) {
            ret = "10";
        } else if (type.startsWith("DATE")) {
            ret = "10";
        } else if (type.startsWith("CLOB") || type.startsWith("LONGTEXT")) {
            ret = "60";
        } else if (type.startsWith("BLOB") || type.startsWith("LONGBINARY")) {
            ret = "50";
        } else if (type.startsWith("IFILE")) {
            ret = "50";
        }
        return ret;
    }
    
    public String getLabel() 
    {
        return p_label;
    }

    public String getTransformObject()
    {
        return p_transformer_from;
    }

    public String getTransformClassMapName()
    {
        return p_transformer_class;
    }

    public boolean hasTransformer() 
    {
        return p_transformer_class != null && p_transformer_from != null ;
    }
    
    public boDefXeoCode getRecommend() 
    {
        return p_recomended;
    }
  /*
    public ngtXMLHandler[] getRecommendNode() {
        return super.getChildNode("constraints")!=null? super.getChildNode("constraints").getChildNode("recommend").getChildNodes():null;
    }
    */

/*    
    public String getMask() 
    {
        return super.getChildNode("mask")!=null?super.getChildNode("mask").getText():"";
    }
*/    

    public boolean supportManualOperation() 
    {
        return p_bridge_manual_operation;
    }
    
    public boolean supportManualAdd() 
    {
        return p_bridge_manual_add;
    }

    public boolean supportManualCreate() 
    {
        return p_bridge_manual_create;
    }
    
    public String getTransform() 
    {
        return super.getChildNode("transform")!=null?super.getChildNode("transform").getText():"";
    }

    public boolean getClock() 
    {
        return p_clock;
    }

    public String getAtributeDeclaredType() 
    {
        return p_declaredType;
    }

    public byte getAtributeType() 
    {
        return p_attType;
    }
    
    public String getDescription() 
    {
        return p_description;
    }

    public String getTooltip() 
    {
        return p_tooltip;
    }
    
    public String getReferencedObjectName() 
    {
        return p_referencedObjectName;
    }
    
    public boolean isMemberOfInterface() 
    {
        if(super.getAttribute("implementedby","").length()>0)
        {
            return true;
        }
        return false;
    }
    public String[] getInterfaces( )
    {
        String intfs;
        if((intfs = super.getAttribute("implementedby","")).length()>0)
        {
            String[] intnames = intfs.split(",");
            return intnames;
        }
        return null;
    }
    
    public boolean isMemberOfInterface( String interfacename )
    {
        String[] intnames = getInterfaces();
        if ( intnames != null )
        {
            for (byte i = 0; i < intnames.length ; i++)
            {
                if( intnames.equals( interfacename ) )
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boDefHandler getReferencedObjectDef() 
    {
        boDefHandler xbobdef=null;
        if( p_attType == TYPE_OBJECTATTRIBUTE ) 
        {
            xbobdef = boDefHandler.getBoDefinition( p_referencedObjectName );
        } 
        else 
        {
            throw new boException(this.getClass().getName()+".getReferencedObjectDef()","BO-1401",null, p_type );
        }
        return xbobdef;
    }

    public String getMax() 
    {
        return String.valueOf( p_max );
    }
    public String getMin() 
    {
        return String.valueOf( p_min );
    }
    
    public int getMaxOccurs() 
    {
        return p_maxOccurs;
    }
    
    public int getMinOccurs() 
    {
        return p_minOccurs ;
    }

    public byte getPermissions()
    {
        return boDefAttribute.CAN_READ+boDefAttribute.CAN_WRITE+boDefAttribute.CAN_DELETE+boDefAttribute.CAN_ADD;
    }
    
    public boDefBridge getBridge() 
    {
        return p_bridge;
    }
    
    public boDefHandler getBoDefHandler() 
    {
        return p_defhandler;
    }
    
    public byte getValueType() 
    {
        return p_valueType;   
    }
    
    public String getExtendsClass() 
    {
        //TODO:Not Implemented
        return null;
    }
    
    public String getClassName() 
    {
        // TODO:Não definido
        return "Handler"+this.getName(); 
    }
    
    public int getDecimals() 
    {
        return p_decimals;
    }
    
    public boolean renderAsCheckBox() 
    {
        return p_renderAsCheck;
    }

    public int getMinDecimals() 
    {
        return p_mindecimals;
    }

    public String getGrouping() 
    {
        return p_digit_grouping?"Y":"N";
    }
 
//new XML Format Methdos
    public boDefXeoCode getValid()
    {
        return p_valid;
    }
    
    public boDefXeoCode getDefaultValue()
    {
        return p_defaultValue;
    }

    public boDefXeoCode getDisableWhen()
    {
        return p_disableWhen;
    }   

    public boDefXeoCode getOnChangeSubmit()
    {
        return p_onChangeSubmit;
    }
    
    public boDefXeoCode getTotalRefresh()
    {
        return p_totalRefresh;
    }

    public boDefXeoCode getRequired()
    {
        return p_required;
    }

    public boDefXeoCode getFormula()
    {
        //TODO:Campos com auto sequencia.
        return p_formula;
    }

    public boDefXeoCode getHiddenWhen()
    {
        return p_hiddenWhen;
    } 

    public boolean renderAsLov()
    {
        return p_renderAsLov;
    } 
    
    public boolean textIndex()
    {
        return p_textIndex_active;
    }
    
    public boolean indexOnlyCardId()
    {
        return p_textIndex_onlyCardId;
    }

    public int getRuntimeMaxOccurs() 
    {
        return p_runtimeMaxOccurs;
    }

    public String getLOVName() 
    {
        return p_lovName;
    }

    public String getLOVSql() 
    {
        return p_lovSql;
    }

    public String getLOVSqlIdField() 
    {
        return p_lovSqlDescField;
    }

    public String getLOVSqlDescField() 
    {
        return p_lovSqlIdField;
    }
    
    public boDefXeoCode getLovCondition()
    {
        return p_lovCondition;
    }
    
    public String getEditorType()
    {
        return p_editorType;
    }

    public boDefXeoCode getLovEditable()
    {
        return p_lovCanChange;
    }
    
    public ngtXMLHandler getLovItems()
    {
        return p_lovItems; 
    }
    
    public boolean getLovRetainValues()
    {   
        return p_lovRetainValues;
    }    
 
    public boolean needsClass()
    {
         // Check if the type of the attribute is Supported

         // the Attribute is a Bridge
         if( getBridge() != null )
         {
             return true;
         }

         // the Attribute is in a Bridge
         if( super.getNode().getParentNode().getParentNode().getNodeName().equals("bridge") )
         {
             return true;
         }

         // the Attribute is a State Attribute
         if( getAtributeType() == TYPE_STATEATTRIBUTE )
         {
             return true;
         }
         
         // the Attribute is Tabled
         if ( getDbIsTabled() )
         {
             return true;
         }
         
         // the Attribute is iFile
         if ( getType().toUpperCase().equals("IFILE") )
         {
             return true;
         }

         // UnSupported DataType         
         if( getAtributeType() != TYPE_OBJECTATTRIBUTE  && "BL".indexOf( boDefDataTypeMapping.getDbmType( getType() ) ) > -1 )
         {
             return true;
         }
         
         // Eventos definidos na tag events do Objecto
         boDefClsEvents[] events = getBoDefHandler().getBoClsEvents();
         for (int i = 0;events != null && i < events.length; i++) 
         {
            if( this.getName().equals( events[i].getAttributeName() ) )
            {
                return true;
            }
         }

         // Eventos definidos na tag events do Atributo
         events = getEvents();
         for (int i = 0;events != null && i < events.length; i++) 
         {
            if( this.getName().equals( events[i].getAttributeName() ) )
            {
                return true;
            }
         }
         
         
         
         boolean ret=true;

         // required
         ret = ret && ( p_required == null || !p_required.needsClass() );

         // disabledWhen
         ret = ret && ( p_disableWhen == null || !p_disableWhen.needsClass()  );

         // hiddenWhen 
         ret = ret && ( p_hiddenWhen == null || !p_hiddenWhen.needsClass() );

        // defaultValue
         ret = ret && ( p_defaultValue == null ||  !p_defaultValue.needsClass() );
        
        // formula
         ret = ret && ( p_formula == null || !p_formula.needsClass() );
        
        // Lov Condition
         ret = ret && ( p_lovCondition == null || !p_lovCondition.needsClass() );
        
        // Lov Editable
         ret = ret && ( p_lovCanChange == null || !p_lovCanChange.needsClass() );
        
        // Methods
         ret = ret && ( getMethods() == null || getMethods().length == 0 );
         
        // Validation
         ret = ret && ( p_valid == null || !p_valid.needsClass() );
      
         ret = !ret;  

         return ret;
   }
   
   public String className()
   {
        if( !needsClass() )
        {
            if( getAtributeType() == TYPE_OBJECTATTRIBUTE )
            {
                return "netgest.bo.runtime.attributes.boAttributeObject";
            }
            else
            {
                String ft = boDefDataTypeMapping.getDbmType( getType() );
                if( ft.equals("C") ||  ft.equals("CL")  )
                {
                    return "netgest.bo.runtime.attributes.boAttributeString";
                }
                else if ( ft.equals("D") )
                {
                    return "netgest.bo.runtime.attributes.boAttributeDate";
                }
                else if ( ft.equals("N") )
                {
                    return "netgest.bo.runtime.attributes.boAttributeNumber";
                }
                else
                {
                    throw new RuntimeException("Bug in ( Standart Attributes ) in boDefAttribute.needsClass" );                    
                }
            }
        }
        else
        { 
            return "Handler"+getName();
        }
   }
   
    public boolean isFinder()
    {
        return false;
    }
    
    public String getBridgeFilter()
    {
        return null;
    }

	@Override
	public boDefDocument getECMDocumentDefinitions() {
		return p_documentDef;
	}
   
    
}