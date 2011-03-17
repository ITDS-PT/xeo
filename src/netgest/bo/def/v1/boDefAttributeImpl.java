/*Enconding=UTF-8*/
package netgest.bo.def.v1;
import java.util.ArrayList;
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
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.boRuntimeException2;
import netgest.bo.system.Logger;
import netgest.bo.transformers.Transformer;
import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class boDefAttributeImpl extends ngtXMLHandler implements boDefAttribute
{

    public String getLOVSql() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLOVSqlDescField() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLOVSqlIdField() {
		// TODO Auto-generated method stub
		return null;
	}

	//logger
    private static Logger logger = Logger.getLogger("netgest.bo.def.boDefAttribute");

    private boDefHandlerImpl p_defhandler;
    private boDefMethodImpl[] p_methods;
    private boDefHandlerImpl[] p_objects;

	private String[]        p_objectsName;
    private boDefBridgeImpl p_bridge=null;
    
    
    private String p_label      = null;
    private String p_policies   = null;

    private byte p_recommended  = -1;
    
    private byte p_hasClock     = -1;
    private byte p_lovRequired  = -1;
    private int p_runtimeMaxOccurs = -1;
    private String p_lovName    = null;
    private String p_tooltip    = null;
    private String p_referencObjectName = null;

    private int p_maxOccurs          = -1;
    private int p_minOccurs          = -1;

    private String p_max          = null;
    private String p_min          = null;
    
    private String  p_description     = null;
    private String  p_type            = null;
    private Boolean p_istabled       = null;
    private String  p_dbname         = null;
    
    //filtros
    private String p_bridgeFilter;
    private int p_manualOperation  = -1;
    private int p_manualAdd  = -1;
    private int p_manualCreate  = -1;

    //validação
    private boDefXeoCode p_valid           = null;
    //private String p_valid_depends[] = null;
    //private boolean p_valid_codeJava = false;
    
    private boDefXeoCode p_defaultValue    = null;
//    private String p_defaultValue_depends[] = null;
//    private boolean p_defaultValue_codeJava = false;
    
    private boDefXeoCode p_disableWhen     = null;
//    private String p_disableWhen_depends[] = null;
//    private boolean p_disableWhen_codeJava = false;
    
    private boDefXeoCode p_onChangeSubmit      = null;
//    private String p_onChangeSubmit_depends[] = null;
//    private boolean p_onChangeSubmit_codeJava = false;
    
    private boDefXeoCode p_hiddenWhen      = null;    
//    private String p_hiddenWhen_depends[] = null;
//    private boolean p_hiddenWhen_codeJava = false;
    
    private Boolean p_renderAsLov    = null;
    private Boolean p_renderAsMultipleList    = null;    
    
    private boDefXeoCode p_lovCanChange   = null;
//    private String p_lovCanChange_depends[] = null;
//    private boolean p_lovCanChange_codeJava = false;    
    
    private boDefXeoCode p_required       = null;
//    private String p_required_depends[] = null;
//    private boolean p_required_codeJava = false;
    
    private boDefXeoCode p_formula         = null;
//    private String p_formula_depends[] = null;
//    private boolean p_formula_codeJava = false;
    
    private boDefXeoCode p_lovCondition    = null;
//    private String p_lovCondition_depends[] = null;
//    private boolean p_lovCondition_codeJava = false;
    
    private byte p_valueType=-1;
    
    //transformer attributes
    private Boolean p_hasTransformer = null;
    private String p_from = null;
    private String p_class = null;
    //falta implementar os mappings

    //textIndex
    private Boolean p_textIndex    = null;
    //textIndex
    private Boolean p_indexOnlyCardid    = null;
    
    //finder
    private Boolean p_finder = null;
    
    public boDefAttributeImpl(boDefHandlerImpl bodef,Node x) 
    {
        super(x);        
        p_defhandler = bodef;
        parse();
    }
    
    private void parse() {
        ngtXMLHandler node = super.getChildNode("Methods");
        if(node != null) {
            ngtXMLHandler[] mths = node.getChildNodes();
            if(mths!=null) {
                p_methods = new boDefMethodImpl[mths.length];
                for(byte i=0;i<mths.length;i++) {
                    p_methods[i]=new boDefMethodImpl(p_defhandler,mths[i].getNode());
                }
            }
        }
        node = super.getChildNode("objects");
        if(node != null) {
            ngtXMLHandler[] objs = node.getChildNodes();
            if(objs!=null) 
            {
                p_objects       = new boDefHandlerImpl[objs.length];
                p_objectsName   = new String[ objs.length ];
                byte x=0;
                for(byte i=0;i<objs.length;i++) 
                {
                    p_objectsName[ i ] = objs[i].getText();
                    if(!p_defhandler.getName().equals(objs[i].getText()))
                    {
                        if ( boDefHandler.getBoDefinition( objs[i].getText() )!=null )
                        {
                         x++ ;
                        }
                    }
                    else 
                    {
                        x++; 
                    }
                }
                p_objects = new boDefHandlerImpl[x];
                x=0;
                for(byte i=0;i<objs.length;i++)
                {
                    if(!p_defhandler.getName().equals(objs[i].getText()))
                    {
                    
                     if ( boDefHandler.getBoDefinition( objs[i].getText() )!=null )
                     {
                        if(!this.getName().equals(objs[i].getText()))
                        {
                            p_objects[x++] = (boDefHandlerImpl)boDefHandlerImpl.getBoDefinition( objs[i].getText() );
                        }
                        else
                        {
                            p_objects[x++] = p_defhandler;
                        }
                     }
                    }
                    else 
                      p_objects[x++] = p_defhandler;
                }
                
            }
        }
        if( this.getAtributeType() == TYPE_OBJECTATTRIBUTE && this.getMaxOccurs() > 1 )
        {
            p_bridge = new boDefBridgeImpl(this);
        }
        
    }
    
    public boDefMethod[] getMethods() 
    {
        return p_methods;        
    }
    
    public boDefClsEvents getEvent( String eventName )
    {
        return null;
    }
    public boDefClsEvents[] getEvents()
    {
        Vector toRet = new Vector();
        boDefClsEvents[] ev = this.p_defhandler.getBoClsEvents();
        for (int i = 0;ev != null && i < ev.length; i++) 
        {
            if( ev[i].getEventName().endsWith(  "." + getName() ) )
            {
                toRet.add( ev[i] );
            }
        }
        if( toRet.size() > 0 )
            return (boDefClsEvents[])toRet.toArray( new boDefClsEvents[ toRet.size() ] );
        
        return null;
    }
    
    public boDefHandler[] getObjects() {
        return p_objects;        
    }

    public String[] getObjectsName() {
        return p_objectsName;        
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
                    logger.severe(LoggerMessageLocalizer.getMessage("UNIDENTIFIED_TRANSFORMER")+"  (" + getTransformClassMapName() + ")");
                    throw new boRuntimeException2(MessageLocalizer.getMessage("UNIDENTIFIED_TRANSFORMER")+" (" + getTransformClassMapName() + ")");
                }
                catch (IllegalAccessException e)
                {
                    logger.severe(LoggerMessageLocalizer.getMessage("UNIDENTIFIED_TRANSFORMER")+"  (" + getTransformClassMapName() + ")");
                    throw new boRuntimeException2(MessageLocalizer.getMessage("UNIDENTIFIED_TRANSFORMER")+"  (" + getTransformClassMapName() + ")");
                }
            }
            catch (ClassNotFoundException e)
            {
                logger.severe(LoggerMessageLocalizer.getMessage("INTERFACE_CLASS_MAP")+" (" + getTransformClassMapName() + ") "+LoggerMessageLocalizer.getMessage("NOT_FOUND"));
                throw new boRuntimeException2("Interface Class Map (" + getTransformClassMapName() + ") "+MessageLocalizer.getMessage("NOT_FOUND"));
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
        
    }
    public byte getSetParent()
    {
        String text = super.getChildNodeText("setParent","");
        if( text.equalsIgnoreCase("y") || text.equalsIgnoreCase("Yes") )
        {
            return SET_PARENT_YES;
        }
        else if ( text.equalsIgnoreCase("n") || text.equalsIgnoreCase("no") )
        {
            return SET_PARENT_NO;            
        }
        return SET_PARENT_DEFAULT;
    }
    
    public boolean getShowLookup()
    {
        String text = super.getChildNodeText("showLookup","");
        if( text.equalsIgnoreCase("y") || text.equalsIgnoreCase("Yes") )
        {
            return true;
        }
        return false;
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
                    //
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
    
    public String getName() {
        return super.getNodeName();
    }
    public boolean getDbIsBinding() {
        boolean ret = true;
        ngtXMLHandler xdb=super.getChildNode("database");
        if(xdb != null && xdb.getChildNodeText("binding","Y").toUpperCase().startsWith("N")) {
            ret = false;
        }
        return ret;
    }
    public String getBeahvior_Img()
    {
        String toRet=null;
        ngtXMLHandler xdb=super.getChildNode("behavior");
        if ( xdb != null )
        {
            toRet=xdb.getChildNode("img").getText();
        }
        return toRet;
    }
    public String getBeahvior_Script()
    {
        String toRet=null;
        ngtXMLHandler xdb=super.getChildNode("behavior");
        if ( xdb != null )
        {
            toRet=xdb.getChildNode("script").getText();
        }
        return toRet;
    }
    
    public boolean getDbIsUnique() {
        boolean ret = false;
        ngtXMLHandler xdb=super.getChildNode("database");
        if(xdb != null && xdb.getChildNodeText("unique","N").toUpperCase().startsWith("Y")) {
            ret = true;
        }
        return ret;
    }
    public boolean getDbIsFullTextIndexed() {
        boolean ret = false;
        ngtXMLHandler xdb=super.getChildNode("database");
        if(xdb != null && xdb.getChildNodeText("indexfull","N").toUpperCase().startsWith("Y")) {
            ret = true;
        }
        return ret;
    }
    public boolean getDbIsIndexed() {
        boolean ret = false;
        ngtXMLHandler xdb=super.getChildNode("database");
        if(xdb != null && xdb.getChildNodeText("indexed","N").toUpperCase().startsWith("Y")) {
            ret = true;
        }
        return ret;
    }
    public boolean getDbIsTabled() 
    {
        if( p_istabled == null )
        {
            p_istabled  = new Boolean(false);
            ngtXMLHandler xdb=super.getChildNode("database");
            if(xdb != null && xdb.getChildNodeText("tabled","N").toUpperCase().startsWith("Y")) {
                p_istabled = new Boolean( true );
            }
        }
        return p_istabled.booleanValue();
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
        return (p_defhandler.getBoPhisicalMasterTable()+"$"+getDbName()).toUpperCase();        
    }
    
    public String getTableName()
    {
        return p_defhandler.getBoMasterTable( ) + "$" + getDbName( );        
    }

    public String getDbName() {
        if( p_dbname==null )
        {
            String ret = getName();
            ngtXMLHandler xnode = super.getChildNode("database");
            if( xnode != null )
            {
                String fn = xnode.getChildNodeText("fieldname","");
                if( fn.length() > 0 )
                {
                    ret = fn;
                }
            }
            if(this.getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                ret+="$";
            }
            p_dbname = ret.toUpperCase();
        }
        return p_dbname;
    }
    
    public boolean getDbCreateConstraints()
    {
        boolean ret = true;
        ngtXMLHandler xnode = super.getChildNode("database");
        if( xnode != null )
        {
            String fn = xnode.getChildNodeText("constraint","");
            if( fn.length() > 0 && fn.toUpperCase().startsWith("N") )
            {
                ret = false;
            }
        }
        return ret;
    }
    
    public String getType() {
         
         if ( p_type == null )
         {
             ngtXMLHandler xmlh = super.getChildNode("type");
             if(xmlh==null) {
                 throw new boException(this.getClass().getName()+".getType()","BO-1403",null,this.getName());
             }
             p_type = xmlh.getText();
             if ( p_type == null ) throw new boException(this.getClass().getName()+".getType()","BO-1403",null,this.getName());
             
         }
         return p_type;
    }
    public String[] getTypeArguments()
    {
        String type = this.getType();
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
            args.add( word.toString() );
        }
        return (String[])args.toArray( new String[ args.size() ] );
    }
     
    public int getLen()
    {
        String[] ft = boBuildDBUtils.parseFieldType(getType());
        return ClassUtils.convertToInt(ft[1]);
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
    
    public String getLabel() {
        if ( p_label != null ) return p_label;
        
        String ret=null;
        ngtXMLHandler labnode = super.getChildNode("label");
        
        if((labnode.getChildNode(p_defhandler.getBoLanguage()))!=null)
            ret = (labnode.getChildNode(p_defhandler.getBoLanguage())).getText();
            
        else if (labnode.getChildNode(p_defhandler.getBoDefaultLanguage())!=null)
            ret = (labnode.getChildNode(p_defhandler.getBoDefaultLanguage())).getText();
        
        p_label=ret;    
        return ret;      
        
//        return &&super.getChildNode(p_defhandler.getBoLanguage()).getChildNode("label")!=null?super.getChildNode(p_defhandler.getBoLanguage()).getChildNode("label").getText():super.getChildNode("label")!=null?super.getChildNode("label").getText():super.getNodeName();
    }

    public String getTransformObject()
    {
        if(p_from != null || hasTransformer()) return p_from;
        return null;
    }

    public String getTransformClassMapName()
    {
        if(p_class != null || hasTransformer()) return p_class;
        return null;
    }

    public boolean hasTransformer() {
        if ( p_hasTransformer != null ) return p_hasTransformer.booleanValue();
        
        ngtXMLHandler transfnode = super.getChildNode("transformer");
        if(transfnode == null)
        {
            p_hasTransformer = Boolean.FALSE;
            return false;
        }
        
        ngtXMLHandler fromnode = transfnode.getChildNode("from");
        if(fromnode == null)
        {
            p_hasTransformer = Boolean.FALSE;
            return false;
        }
        else
        {
            p_from = fromnode.getText();
        }
        
        ngtXMLHandler classnode = transfnode.getChildNode("class");
        if(classnode == null)
        {
            //verificar se existe mappings
//            if(mappings)
//            {
//            
//            }
//            else
//            {
                p_hasTransformer = Boolean.FALSE;
                return false;
//            }
        }
        else
        {
            p_class = classnode.getText();
        }
        p_hasTransformer = Boolean.TRUE;
        return true;
    }
    
    public boDefXeoCode getRecommend() 
    {
        return null;
    }
  /*
    public ngtXMLHandler[] getRecommendNode() {
        return super.getChildNode("constraints")!=null? super.getChildNode("constraints").getChildNode("recommend").getChildNodes():null;
    }
    */
    public String getMask() {
        return super.getChildNode("mask")!=null?super.getChildNode("mask").getText():"";
    }
    
    public boolean supportManualOperation() {
        if(p_manualOperation == -1 )
        {
            p_manualOperation= super.getChildNode("manualOperation")==null?1:
                super.getChildNode("manualOperation").getText().equalsIgnoreCase("n") ||
                super.getChildNode("manualOperation").getText().equalsIgnoreCase("no")?0:1 ;
        }
        return p_manualOperation==1?true:false;
    }
    
    public boolean supportManualAdd() {
        if(p_manualAdd == -1 )
        {
            p_manualAdd= super.getChildNode("manualAdd")==null?1:
                super.getChildNode("manualAdd").getText().equalsIgnoreCase("n") ||
                super.getChildNode("manualAdd").getText().equalsIgnoreCase("no")?0:1 ;
        }
        return p_manualAdd==1?true:false;
    }

    public boolean supportManualCreate() {
        if(p_manualCreate == -1 )
        {
            p_manualCreate= super.getChildNode("manualCreate")==null?1:
                super.getChildNode("manualCreate").getText().equalsIgnoreCase("n") ||
                super.getChildNode("manualCreate").getText().equalsIgnoreCase("no")?0:1 ;
        }
        return p_manualCreate==1?true:false;
    }
    
    public String getTransform() {
        return super.getChildNode("transform")!=null?super.getChildNode("transform").getText():"";
    }

    /*
    public String getGUIgroup() {
        return super.getChildNode("gui")!=null&&super.getChildNode("gui").getChildNode("group")!=null?
          super.getChildNode("gui").getChildNode("group").getText():"";
    }
    public String getGUIindividual() {
        return super.getChildNode("gui")!=null&&super.getChildNode("gui").getChildNode("individual")!=null?
          super.getChildNode("gui").getChildNode("individual").getText():"";
    }
*/

//    public String getLOVName() {
//                
//        if ( p_lovName==null )
//        {
//            p_lovName="";
//            if( super.getChildNode("constraints") !=null){
//                 if(super.getChildNode("constraints").getChildNode("lov") !=null){
//                   if(super.getChildNode("constraints").getChildNode("lov").getChildNode("name") !=null)
//                   {
//                        p_lovName = super.getChildNode("constraints").getChildNode("lov").getChildNode("name").getText();
//                        if ( p_lovName == null) p_lovName="";
//                   }
//               }
//            }
//        }
//        return p_lovName;
//    }
//    
    public boolean getClock() {
      
         if ( p_hasClock == -1 )
         {
             ngtXMLHandler xmlh = super.getChildNode("clock");
             if(xmlh==null)
             {
                 p_hasClock = 1;
             }
             else
             {
             
                  String x=xmlh.getText();
                  if ( x== null || x.equalsIgnoreCase("Y") )
                  {
                        p_hasClock = 1;
                  }
                  else
                  {
                        p_hasClock = 0;
                  }
             }
         }
         return p_hasClock == 1;
    }
//    public boolean getLOVrequired() {
//
//        if ( p_lovRequired == -1 )
//        {
//            p_lovRequired=0;            
//            if( super.getChildNode("constraints") !=null){
//                 if(super.getChildNode("constraints").getChildNode("lov") !=null){
//                   if(super.getChildNode("constraints").getChildNode("lov").getChildNode("required") !=null){
//                      if(super.getChildNode("constraints").getChildNode("lov").getChildNode("required").getText() !=null)
//                         if ( super.getChildNode("constraints").getChildNode("lov").getChildNode("required").getText().equalsIgnoreCase("Y"))
//                         {
//                             p_lovRequired = 1;
//                         }
//                   }
//               }
//            }
//        }
//        return p_lovRequired==1;
//    }


/*    public ngtXMLHandler[] getDefaultValues() {
        ngtXMLHandler[] toReturn=super.getChildNode("defaultValues").getChildNodes();
        return toReturn;
        
    }
  */  
    public byte getAtributeType() {
        String ty = getType();
        if(ty.indexOf("object.")==0) {
            return TYPE_OBJECTATTRIBUTE;
        } else {
            return TYPE_ATTRIBUTE;
        }
    }
    
    public String getAtributeDeclaredType() {
        return null;
    }
    
    public String getDescription() {
        
        if ( p_description == null )
        {
            ngtXMLHandler labnode = super.getChildNode("description");
            
            if ( labnode != null)
            {
                if((labnode.getChildNode(p_defhandler.getBoLanguage()))!=null)
                    p_description = (labnode.getChildNode(p_defhandler.getBoLanguage())).getText();
                else if (labnode.getChildNode(p_defhandler.getBoDefaultLanguage())!=null)
                    p_description = (labnode.getChildNode(p_defhandler.getBoDefaultLanguage())).getText();
            }
            
            if ( p_description == null)
            {
                p_description=getLabel();
            }
        }
        return p_description;
    }

    public String getTooltip() {
        if ( p_tooltip==null )
        {   
            ngtXMLHandler labnode = super.getChildNode("tooltip");
            if( labnode != null )
            {
                if((labnode.getChildNode(p_defhandler.getBoLanguage()))!=null)
                    p_tooltip = (labnode.getChildNode(p_defhandler.getBoLanguage())).getText();
                else if (labnode.getChildNode(p_defhandler.getBoDefaultLanguage())!=null)
                    p_tooltip = (labnode.getChildNode(p_defhandler.getBoDefaultLanguage())).getText();
                
                if ( p_tooltip == null)
                {
                    p_tooltip=getLabel();
                }
            }
        }
        return p_tooltip;
    }
    
    public String getReferencedObjectName() {
        if ( p_referencObjectName == null )
        {
            String ftype=this.getType();
            if(getAtributeType()==TYPE_OBJECTATTRIBUTE) {
               p_referencObjectName = ftype.substring(7);
            } else {
                throw new boException(this.getClass().getName()+".getReferencedObjectDef()","BO-1401",null,ftype);
            }
        }
        return p_referencObjectName;
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
    
    public boDefHandler getReferencedObjectDef() {
        String ftype=this.getType();
        boDefHandler xbobdef=null;
        if(getAtributeType()==TYPE_OBJECTATTRIBUTE) {
            String refboname = ftype.substring(7);
            xbobdef = boDefHandler.getBoDefinition(refboname);
        } else {
            throw new boException(this.getClass().getName()+".getReferencedObjectDef()","BO-1401",null,ftype);
        }
        return xbobdef;
    }

    public String getMax() {
        
        if ( p_max != null ) return p_max;
        
        String ret=null;
        ngtXMLHandler max = super.getChildNode("max");
        
        if(max != null)
            p_max = max.getText();
        else
            p_max = "";        
 
        return p_max;
    }
    public String getMin() {
        if ( p_min != null ) return p_min;
        
        String ret=null;
        ngtXMLHandler min = super.getChildNode("min");
        
        if(min != null)
            p_min = min.getText();
        else
            p_min = "-999999999";        
 
        return p_min;
    }
    
    public int getMaxOccurs() {
        
        if ( p_maxOccurs == -1 )
        {
            ngtXMLHandler noccurs = (ngtXMLHandler)super.getChildNode("maxoccurs");
            String soccurs=null;
            if(noccurs != null) {
                soccurs = noccurs.getText();
            }
            p_maxOccurs = 1;
            if(soccurs != null && !soccurs.equalsIgnoreCase("N")) {
                p_maxOccurs = Integer.parseInt(soccurs);
            } else if (soccurs != null && soccurs.equalsIgnoreCase("N")) {
                p_maxOccurs = 999999999;
            }
        }
        return p_maxOccurs;
    }
    public int getMinOccurs() {
        if ( p_minOccurs == -1 )
        {
            ngtXMLHandler noccurs = (ngtXMLHandler) super.getChildNode("minoccurs");
            String soccurs=null;
            if(noccurs != null) {
                soccurs = noccurs.getText();
            }
            p_minOccurs = 0;
            if(soccurs != null) {
                p_minOccurs = Integer.parseInt(soccurs);
            }
        }
        return p_minOccurs ;
    }

    public byte getPermissions(){
        return boDefAttribute.CAN_READ+boDefAttribute.CAN_WRITE+
        boDefAttribute.CAN_DELETE+boDefAttribute.CAN_ADD;
    }
    
    public boDefBridge getBridge() {
        return p_bridge;
    }
    public boDefHandler getBoDefHandler() {
        return p_defhandler;
    }
    
    public byte getValueType() {
        byte ret = 0;
        if ( p_valueType==-1)
        {
            p_valueType = this.getValueType( this.getType() );
        }
        return p_valueType;
        
    }
    
    public byte getValueType( String type) {
        return boDefDataTypeMapping.getValueType( type );
    }
    
    
    public String getExtendsClass() {
        return super.getChildNodeText("classextends",null);
    }
    
    public String getClassName() {
        String ret= super.getChildNodeText("classname",null);
        if(ret==null)
            return "Handler"+this.getName(); 
            
        return ret;
    }
//    public boolean getCanAlter() {
//        if ( p_canAlter == null )
//        {
//            ngtXMLHandler noccurs = (ngtXMLHandler) super.getChildNode("canAlter");
//            String soccurs="Y";
//            if(noccurs != null) {
//                soccurs = noccurs.getText();
//            }
//            p_canAlter = Boolean.FALSE;
//            if(soccurs != null) {
//                p_canAlter = "N".equalsIgnoreCase(soccurs) ? Boolean.FALSE:Boolean.TRUE;
//            }
//        }
//        return p_canAlter.booleanValue();
//    }    
    
    public String getBridgeFilter() {

        if( p_bridgeFilter != null ) return p_bridgeFilter;

        String ret = null;
        ngtXMLHandler objNode = super.getChildNode("objectfilter");
        if(objNode != null) {
            ngtXMLHandler filterNode = objNode.getChildNode("filter");
            if(filterNode != null){
                ngtXMLHandler bridgeNode = filterNode.getChildNode("bridge");
                ret = bridgeNode == null ? null:bridgeNode.getText();
            }
        }
        p_bridgeFilter=ret;
        return ret;
    }

    public int getDecimals() {
        try{
            return super.getChildNode("decimals")!=null?Integer.parseInt(super.getChildNode("decimals").getText()):0;
        }catch(NumberFormatException e){
            //ignora
        }
        return 0;
    }
    
    public boolean renderAsCheckBox() {
        
        if ( super.getChildNode("renderAsCheck")!=null)
        {
            return super.getChildNode("renderAsCheck").getText().equalsIgnoreCase("Y") || super.getChildNode("renderAsCheck").getText().equalsIgnoreCase("YES"); 
        }
        
        return false;
    }

    public int getMinDecimals() {
        try{
            return super.getChildNode("minDecimals")!=null?Integer.parseInt(super.getChildNode("minDecimals").getText()):getDecimals();
        }catch(NumberFormatException e){
            //ignora
        }
        return getDecimals();
    }

    public String getGrouping() {
        return super.getChildNode("grouping")!=null?super.getChildNode("grouping").getText():"";
    }
 
//new XML Format Methdos
    public String[] getValidCodeJavaDepends()
    {
        return getValid().getDepends();
    }

    public boDefXeoCode getValid()
    {
        if ( p_valid==null )
        {
            p_valid = parseXeoCode( getChildNode("valid"),null,"y" );
        }
        return p_valid;
    }
    
    public boDefXeoCode getDefaultValue()
    {
        if ( p_defaultValue==null )
        {
            p_defaultValue = parseXeoCode( getChildNode("defaultValue"),null, "" );
        }
        return p_defaultValue;
    }

    public boDefXeoCode getDisableWhen()
    {
        if ( p_disableWhen==null )
        {
            p_disableWhen = parseXeoCode( getChildNode("disableWhen"),null,"n" );
        }
        return p_disableWhen;
    }   

    public boDefXeoCode getHiddenWhen()
    {
        if ( p_hiddenWhen==null )
        {
            p_hiddenWhen = parseXeoCode( getChildNode("hiddenWhen"),null,"n" );
        }
        return p_hiddenWhen;
    } 
    
    public boDefXeoCode getOnChangeSubmit()
    {
        if ( p_onChangeSubmit==null )
        {
            p_onChangeSubmit = parseXeoCode( getChildNode("onChangeSubmit"),null,"n" );
        }
        return p_onChangeSubmit;
    } 
    public boDefXeoCode getTotalRefresh()
    {
        if ( p_onChangeSubmit==null )
        {
            p_onChangeSubmit = parseXeoCode( getChildNode("totalRefresh"),null,"n" );
        }
        return p_onChangeSubmit;
    } 
    public boolean renderAsLov()
    {
        if ( p_renderAsLov==null )
        {
            ngtXMLHandler labnode = super.getChildNode("renderAsLov");
            if(labnode != null)
            {
                p_renderAsLov =  ("y".equalsIgnoreCase(labnode.getText()) || "Yes".equalsIgnoreCase(labnode.getText())) ? Boolean.TRUE:Boolean.FALSE;
            }
            else
            {
                p_renderAsLov =  Boolean.FALSE;
            }

        }
        return p_renderAsLov.booleanValue();
    } 
    
    public boolean textIndex()
    {
        if ( p_textIndex==null )
        {
            ngtXMLHandler labnode = super.getChildNode("textIndex");
            if(labnode != null)
            {
                p_textIndex =  ("y".equalsIgnoreCase(labnode.getText()) || "Yes".equalsIgnoreCase(labnode.getText())) ? Boolean.TRUE:Boolean.FALSE;
            }
            else
            {
                p_textIndex =  Boolean.TRUE;
            }

        }
        return p_textIndex.booleanValue();
    }
    
    public boolean indexOnlyCardId()
    {
        if(p_indexOnlyCardid == null)
        {
            if(textIndex())
            {
                ngtXMLHandler labnode = super.getChildNode("textIndex");
                if(labnode != null)
                {
                    String value = labnode.getAttribute("onlyCardID", "N");
                    p_indexOnlyCardid =  ("y".equalsIgnoreCase(value) || "Yes".equalsIgnoreCase(value)) ? Boolean.TRUE:Boolean.FALSE;
                }
                else
                {
                    p_indexOnlyCardid =  Boolean.FALSE;
                }
            }
            else
            {
                p_indexOnlyCardid = Boolean.FALSE;
            }
        }
        return p_indexOnlyCardid.booleanValue();
    }
    
    

    public boolean renderAsMultipleList()
    {
        if ( p_renderAsMultipleList==null )
        {
            ngtXMLHandler labnode = super.getChildNode("renderAsMultipleList");
            if(labnode != null)
            {
                p_renderAsMultipleList =  ("y".equalsIgnoreCase(labnode.getText()) || "Yes".equalsIgnoreCase(labnode.getText())) ? Boolean.TRUE:Boolean.FALSE;
            }
            else
            {
                p_renderAsMultipleList =  Boolean.FALSE;
            }

        }
        return p_renderAsMultipleList.booleanValue();
    }


    public boolean getDbRequired()
    {
//        ngtXMLHandler labnode = super.getChildNode("requiredDB");
//        if ( labnode!= null ) 
//        {
//            return labnode.getText();
//        }
        return false;
    }

    public int getRuntimeMaxOccurs() 
    {

        if ( p_runtimeMaxOccurs == -1 )
        {
            ngtXMLHandler noccurs = (ngtXMLHandler)super.getChildNode("runtimeMaxoccurs");
            String soccurs=null;
            if(noccurs != null) {
                soccurs = noccurs.getText();
            }
            p_runtimeMaxOccurs = 999999999;
            if(soccurs != null && !soccurs.equalsIgnoreCase("N")) {
                p_runtimeMaxOccurs = Integer.parseInt(soccurs);
            } else if (soccurs != null && soccurs.equalsIgnoreCase("N")) {
                p_runtimeMaxOccurs = 999999999;
            }
        }
        return p_runtimeMaxOccurs;
    }
    public boDefXeoCode getRequired()
    {
        if ( p_required==null )
        {
            p_required = parseXeoCode( getChildNode("required"),null, "n" );
        }
        return p_required;
    }

    public boDefXeoCode getFormula()
    {
        if ( p_formula==null )
        {
            String xcode = null ;
            ngtXMLHandler labnode = super.getChildNode("formula");
            
            if( (labnode == null || ((labnode.getText() == null || labnode.getText().length() == 0) &&
                (labnode.getChildNode("CODE_JAVA") == null || labnode.getChildNode("CODE_JAVA").getText() == null ||
                labnode.getChildNode("CODE_JAVA").getText().length() == 0)))
                && this.getAtributeType() == TYPE_ATTRIBUTE && this.getType().toUpperCase().startsWith("SEQUENCE") )
            {
                xcode = "CODE_JAVA(return boObjectUtils.initializeSequenceField(this);)";  
            } 
            else  if(labnode != null) 
            {
                if(labnode.getChildNode("CODE_JAVA") != null && 
                    labnode.getChildNode("CODE_JAVA").getText() != null && 
                    labnode.getChildNode("CODE_JAVA").getText().length() > 0)
                {
                    xcode = "CODE_JAVA(" + labnode.getChildNode("CODE_JAVA").getText() + ")";    
                }
                else
                {
                    xcode = labnode.getText();
                }
            }
            p_formula = parseXeoCode( getChildNode("formula"), xcode, "" );
        }
        return p_formula;
    }
    
    public String getLOVName() {
                
        if ( p_lovName==null )
        {
            p_lovName="";
            if( super.getChildNode("lov") !=null){
               if(super.getChildNode("lov").getAttribute("name") !=null)
               {
                    p_lovName = super.getChildNode("lov").getAttribute("name");
                    if ( p_lovName == null) p_lovName="";
               }
            }
        }
        return p_lovName;
    }
    

    public boDefXeoCode getLovCondition()
    {
        if ( p_lovCondition==null )
        {
            p_lovCondition = parseXeoCode( getChildNode("lovCondition"), null, "" );
        }
        return p_lovCondition;
    }
    
    public String getEditorType()
    {
      String toRet="html";
      Element elem=(Element)this.getNode();
      NodeList listn=elem.getElementsByTagName("editor");
      for (int i=0;i<listn.getLength();i++)
      {
        toRet=listn.item(i).getFirstChild().getNodeValue();
      }
      return toRet;
    }

    public boDefXeoCode getLovEditable()
    {
        if ( p_lovCanChange==null )
        {
            // Atenção que este está debaixo da lov
            ngtXMLHandler node = getChildNode("lov");
            if( node != null )
                p_lovCanChange = parseXeoCode( node.getChildNode("editable"), null,"n" );
            else
                p_lovCanChange = parseXeoCode( null, null,"n" );
            
        }
        return p_lovCanChange;
    }
/*    
    public boolean isLovEditable()
    {
        String lovvalue = getLovEditable();
        return (lovvalue.equalsIgnoreCase("Y")
                    ||lovvalue.equalsIgnoreCase("YES")) 
                    ? true:false;
    }
*/
    public ngtXMLHandler getLovItems()
    {
        if( super.getChildNode("lov") !=null)
        {
            return super.getChildNode("lov").getChildNode("items");
        }
        return null;  
    }
    
    public boolean getLovRetainValues()
    {   
        if( super.getChildNode("lov") !=null)
        {
            String aux = super.getChildNode("lov").getAttribute("retainValues", "n");
            return (aux.equalsIgnoreCase("Y")
                    ||aux.equalsIgnoreCase("YES")) 
                    ? true:false;
        }
        return false;
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
         
         boDefClsEvents[] events = getBoDefHandler().getBoClsEvents();
         for (int i = 0;events != null && i < events.length; i++) 
         {
            if( this.getName().equals( events[i].getAttributeName() ) )
            {
                return true;
            }
         }
         
         
         
         boolean ret=true;

         // required
         ret = ret && ( !getRequired().needsClass() );

         // disabledWhen
         ret = ret && ( !getDisableWhen().needsClass() );

         // hiddenWhen 
         ret = ret && ( !getHiddenWhen().needsClass() );

        // defaultValue
         ret = ret && ( !getDefaultValue().needsClass() );
        
        // formula
         ret = ret && ( !getFormula().needsClass() );
        
        // Lov Condition
         ret = ret && ( !getLovCondition().needsClass() );
        
        // Lov Editable
         ret = ret && ( !getLovEditable().needsClass() );
        
        // Methods
         ret = ret && ( getMethods() == null || getMethods().length == 0 );
         
        // Validation
         ret = ret && ( !getValid().needsClass() );
      
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
                    throw new RuntimeException(MessageLocalizer.getMessage("BUG_IN__INBODEFATTRIBUTE_NEEDSCLASS") );                    
                }
            }
        }
        else
        {
            return "Handler"+getName();
        }
    }
    
    public static final boDefXeoCode parseXeoCode( ngtXMLHandler node, String pcode, String def )
    {
        boDefXeoCodeImpl ret = null;
        if( node != null )
        {
            if( pcode == null )
            {
                pcode = node.getText();
            }
            
            String[] dep  = null;
            boolean isJava = false;
            ngtXMLHandler cjnode = node.getChildNode( "CODE_JAVA" );
            if(node.getChildNode("depends") != null)
            {
                ngtXMLHandler[] attributes = node.getChildNode("depends").getChildNodes();
                if(attributes!=null) {
                    byte x=0;
                    for(byte i=0;i<attributes.length;i++) 
                    {
                        if (attributes[i].getText() != null && attributes[i].getText().length() > 0)
                        {
                             x++;
                        }
                    }
                    dep = new String[x];
                    x=0;
                    for(byte i=0;i<attributes.length;i++)
                    {
                        if (attributes[i].getText() != null && attributes[i].getText().length() > 0)
                        {
                            dep[x++] = attributes[i].getText();
                        }
                    }
                }
            }
            
            
            if( cjnode != null )
            {
                isJava = true;
                pcode = cjnode.getText();
            }
            else if ( pcode != null && pcode.startsWith( "CODE_JAVA" ) )
            {
                pcode = pcode.substring( pcode.indexOf( "CODE_JAVA" ) + 10, pcode.lastIndexOf(")"));
                isJava = true;
            }
            
            if( pcode == null )
            {
                if( checkBoolean( def ) )
                {
                    ret = new boDefXeoCodeImpl( boDefXeoCode.LANG_BOL, dep, def );
                }
            }
            else if( isJava ) 
            {
                ret = new boDefXeoCodeImpl( boDefXeoCode.LANG_JAVA, dep, pcode );
            }
            else if ( checkBoolean( pcode ) )
            {
                ret = new boDefXeoCodeImpl( boDefXeoCode.LANG_BOL, dep, pcode );
            }
            else
            {
                ret = new boDefXeoCodeImpl( boDefXeoCode.LANG_BOL, dep, pcode );
            }
        }
        else if ( def != null )  
        {
            if( def.trim().length() == 0 )
                ret = new boDefXeoCodeImpl( boDefXeoCode.LANG_BOL, null, def );
        }
        return ret;
    }
    
    private static final boolean checkBoolean( String text )
    {
        boolean ret = false;
        if( text != null )
        {
            text = text.toLowerCase();
            ret = ret || text.length() == 0;
            ret = ret || text.equals("y");
            ret = ret || text.equals("yes");
            ret = ret || text.equals("n");
            ret = ret || text.equals("no");
        }
        else
        {
            ret = true;
        }
        return ret;
    }
    
     public boDefObjectFilter[] getObjectFilter()
    {
        boDefObjectFilter[]  ret = null;
        ngtXMLHandler objFilterNode = getChildNode("objectfilter");
        if( objFilterNode != null )
        {
            Vector filtersVector = new Vector();
            ngtXMLHandler[] filterNode = objFilterNode.getChildNodes();
            for (int i = 0; i < filterNode.length; i++) 
            {

                String bol_condition   = filterNode[i].getChildNodeText( "BOL_condition", null );
                String boql_query      = filterNode[i].getChildNodeText( "BOQL_query", null );
                int exp_lang           = boDefXeoCode.LANG_BOL; 

                if( boql_query != null && boql_query.startsWith("CODE_JAVA") )
                {
                    int startPar = boql_query.indexOf("(");
                    int endPar = boql_query.lastIndexOf(")");
                    bol_condition = boql_query.substring(startPar + 1, endPar);
                    exp_lang   = boDefXeoCode.LANG_JAVA;
                    boql_query = null;
                }
                
                String forObject        = filterNode[i].getAttribute("forObject","");
                if( bol_condition != null || boql_query != null )
                {
                    filtersVector.add( new boDefObjectFilterImpl( exp_lang, bol_condition, boql_query, forObject ) );
                }
                
            }
            if( filtersVector.size() > 0 )  
            {
                ret = (boDefObjectFilter[])filtersVector.toArray( new boDefObjectFilter[ filtersVector.size() ] );
            }
        }
        return ret;
    }
    public boDefObjectFilter getObjectFilter( String forObject )
    {
        boDefObjectFilter ret = null;
        boDefObjectFilter[] p_objectFilters = getObjectFilter();
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

   public boolean isFinder()
    {
        return false;
    }

   public String[] getDbRelationKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boDefDocument getECMDocumentDefinitions() {
		// Not supported in this version
		return null;
	}

}