package netgest.bo.def;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import netgest.bo.boConfig;
import netgest.bo.builder.boBuildRepository;
import netgest.bo.builder.boBuilder;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.system.boApplication;
import netgest.bo.system.boRepository;
import netgest.utils.IOUtils;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Linitações:
 * Não converte OPL de metodos e eventos...  ( OK, opl não apenas Security de Class )
 * Não verifica o atributo serverOnly... ( Ainda não sei abordagem exacta para isto )
 * Não está a serializar o LOVCondition
 * 
 * Verificações:
 * objectFilter -> filter -> bridge     Não está a ser serializado.
 */

public class boDefSerializer 
{

    private XMLDocument xml = new XMLDocument(); 
    
    public static boolean VALIDATE_XEOMODEL = false;
    
    public boDefSerializer()
    {
    }
    
    public static void main(String[] args) throws Exception
    {
/*        boApplication app = boApplication.getApplicationFromStaticContext("XEO");
        boRepository rep = boRepository.getDefaultRepository( app );
        boBuildRepository brep = new boBuildRepository( rep );
        
        String sbofile = brep.getPathProvider().getBOPath("holiday");
        File xbofile = new File(sbofile);
        XMLDocument xmldoc = ngtXMLUtils.loadXMLFile(sbofile);
        boDefHandler def = boDefHandler.loadFromXml( xmldoc );
        
        //boDefHandler def = boBuilder.getUndeployedDefinitions( brep, "holiday", null, false );
        //boDefHandler.getBoDefinition( "holiday" )
        boDefSerializer defs = new boDefSerializer();
        defs.init( def );
        defs.xml.print( new FileOutputStream("c:\\projects\\itds\\xeov2\\netgest\\teste.xml") );
*/
        boDefHandler.VERSION = boDefHandler.VERSION_1;
        
        boApplication.getApplicationFromStaticContext("XEO").addAContextToThread();
        
        copyStateDir( new File( boConfig.getDefinitiondir() ) );

        convertDir( new File( boConfig.getDefinitiondir() ) );
        
        System.exit( 0 );
    }
    
    private static final void convertDir( File path )
    {
        File[] file = path.listFiles();
        for (int i = 0;file != null && i < file.length; i++) 
        {
            if( file[i].isDirectory() )
            {
                convertDir( file[i] );
            }
            else
            {
                if( file[i].getName().toLowerCase().endsWith("$bo.xml") )
                {
                    convertFile( file[i] );
                }
                else if ( file[i].getName().toLowerCase().endsWith("$interface.xml") )
                {
                    convertFile( file[i] );
                }
                else if ( file[i].getName().toLowerCase().endsWith("$state.xml")  )
                {
                    convertFile( file[i] );
                }
                else if ( file[i].getName().toLowerCase().endsWith("$lov.xml") )
                {
                    convertLovFile( file[i] );
                    //copyFileToNewExt( file[i], "$lov.xml", ".xeolov" );
                }
                else if ( file[i].getName().toLowerCase().endsWith("$sc.xml") )
                {
                    copyFileToNewExt( file[i], "$sc.xml", ".xeodeploy" );
                }
                else if ( file[i].getName().toLowerCase().endsWith("$desc.xml") )
                {
                    copyFileToNewExt( file[i], "$desc.xml", ".xeodesc" );
                }
                else if ( file[i].getName().toLowerCase().endsWith("$wsd.xml") )
                {
                    copyFileToNewExt( file[i], "$wsd.xml", ".xeowds" );
                }
                else if ( file[i].getName().toLowerCase().endsWith("$ds.xml") )
                {
                    copyFileToNewExt( file[i], "$ds.xml", ".xeods" );
                }
            }
        }
    }
    
    private static final void copyFileToNewExt( File file, String oldExt, String newExt )
    {
        String fileName = file.getName();
        fileName = fileName.substring(0,fileName.indexOf( oldExt ));
        fileName = file.getParent()+ File.separator + fileName + newExt;
        IOUtils.copy( file, fileName );
        System.out.println(MessageLocalizer.getMessage("CONVERTING_FILE") + file.getAbsolutePath() + " "+MessageLocalizer.getMessage("TO")+" " + fileName + newExt);
    }
    
    private static final void copyStateDir( File path )
    {
        File[] file = path.listFiles();
        for (int i = 0;file != null && i < file.length; i++) 
        {
            if( file[i].isDirectory() )
            {
                copyStateDir( file[i] );
            }
            copyStateFile( file[i] );
        }
    }
    private static final void copyStateFile( File file )
    {
        if( file.getName().endsWith("$state.xml") ) 
        {
            File deploymentState = new File(boConfig.getDeploymentDir() + file.getName());
            
            if( !deploymentState.exists() )
            {
                IOUtils.copy( file, deploymentState );
            }
        }
    }

    
    private static final void convertFile( File file )
    {
        System.out.println( MessageLocalizer.getMessage("CONVERTING_FILE") + file.getAbsolutePath() );
        XMLDocument xmldoc = ngtXMLUtils.loadXMLFile( file.getAbsolutePath() );
        
        String name = file.getName().substring(0,file.getName().indexOf("$"));
        
        boDefHandler def;
        
        if( file.getName().toLowerCase().endsWith( "$interface.xml" ) )
            def = boDefHandler.loadInterfaceFromXml( name, xmldoc );
        else
            def = boDefHandler.loadFromXml( xmldoc, 0 );

        boDefSerializer defs = new boDefSerializer();
        defs.init( def );
        
        
        String fileExt;
//        String fileExt = file.getName().endsWith("$bo.xml")?".xeomodel":".xeoimodel";
        if( file.getName().endsWith("$state.xml") ) 
            fileExt = ".xeostate";
        switch ( def.getClassType() )
        {
            case boDefHandler.TYPE_ABSTRACT_CLASS:
            case boDefHandler.TYPE_CLASS:
                fileExt = ".xeomodel";
                if( file.getName().endsWith("$state.xml") ) 
                    fileExt = ".xeostate";
                break;
            case boDefHandler.TYPE_INTERFACE:
                fileExt = ".xeoimodel";
                break;
            default:
                fileExt = ".unknown";
        }
        
        String fileToSave = file.getParentFile().getAbsolutePath()+"\\"+def.getName()+fileExt;
        System.out.println(MessageLocalizer.getMessage("SAVING") + fileToSave );
        ngtXMLUtils.saveXML( defs.xml, fileToSave );
        if( VALIDATE_XEOMODEL )
        {
            System.out.println(MessageLocalizer.getMessage("VALIDATING") + fileToSave );
            ngtXMLUtils.validateXmlwithSchema( fileToSave, "C:\\projects\\itds\\xeov2\\netgest\\xeoModel.xsd" );
        }
        
    
    }
    
    private void init( boDefHandler def )
    {
        if( "Ebo_Perf".equalsIgnoreCase( def.getName() ) )
        {
            boolean toBreak = true;
        }
        
        xml.appendChild( xml.createComment( MessageLocalizer.getMessage("FILE_XEOMODEL_V2_CONVERTED_AT")+(new java.util.Date()) ) );
        Element xeoModel = createAndAppend( "xeoModel", xml );
        serializeGeneral( xeoModel, def );
        serializeOpl( def, xeoModel  );
        serializeStates( def, xeoModel );
        serializeMethods( def, xeoModel  );
        serializeEvents( def, xeoModel );
        serializeFwdObjects( def, xeoModel );
        serializeAttributes( def, xeoModel );
        serializeViewers( def, xeoModel );
    }
    
    private void serializeGeneral( Element model, boDefHandler def )
    {
        Element general = createAndAppend( "general", model );
        general.setAttribute("name", def.getName() );
        
        switch( def.getClassType() ) 
        {
            case boDefHandler.TYPE_ABSTRACT_CLASS:
                general.setAttribute("type", "abstract" );
                break;
            case boDefHandler.TYPE_INTERFACE:
                general.setAttribute("type", "interface" );
                if( def.getInterfaceType() == boDefHandler.INTERFACE_OPTIONAL )
                {
                    general.setAttribute("optionalInterface", "true" );
                }
                else if ( def.getInterfaceType() == boDefHandler.INTERFACE_STANDARD )
                {
                    general.setAttribute("optionalInterface", "false" );
                }
                break;
            case boDefHandler.TYPE_CLASS:
                general.setAttribute("type", "object" );
                break;
        }
        
        general.setAttribute("version", def.getBoMajorVersion() + "." + def.getBoMinorVersion() );
        general.setAttribute("extends", def.getBoSuperBo() );
        
        if( !def.getBoPhisicalMasterTable().equalsIgnoreCase( def.getName() ) )
        {
            general.setAttribute("mastertable", def.getBoPhisicalMasterTable() );
        }
        
        general.setAttribute("orphan", String.valueOf( def.getBoCanBeOrphan() ) );
        general.setAttribute("multiparent", String.valueOf( def.getBoHaveMultiParent() ) );
        general.setAttribute("markInputType", String.valueOf( def.getBoMarkInputType() ) );
        general.setAttribute("extendsJavaClass", def.getBoExtendsClass() );
        
        Element local = createAndAppend( "locale",general );
        local.setAttribute("language","pt");
        local.setAttribute("country","PT");
        local.setAttribute("variant","");

        Element impl  = createAndAppend( "implements", general );

        String[] ints = def.getImplements();
        if( ints != null && ints.length > 0 )
        {
            for (int i = 0; i < ints.length; i++) 
            {
                Element intNode = createAndAppend( "interface", impl );
                intNode.appendChild( xml.createTextNode( ints[i] ) );
            }
        }
        
        // business Implements version 1...
        ngtXMLHandler bimplNode = def.getChildNode("general").getChildNode("business_implements");
        if( bimplNode != null ) 
        {
            ngtXMLHandler[] bimplNodes = bimplNode.getChildNodes();
            for (int i = 0; i < bimplNodes.length; i++) 
            {
                Element intNode = createAndAppend( "interface", impl );
                intNode.appendChild( xml.createTextNode( bimplNodes[i].getText() ) );
            }
        }
        
        
        String[] inff = def.getImplements();
        if( ints != null && ints.length > 0 )
        {
            for (int i = 0; i < ints.length; i++) 
            {
                Element intNode = createAndAppend( "interface", impl );
                intNode.setAttribute("optional","true");
                intNode.appendChild( xml.createTextNode( ints[i] ) );
            }
        }
        
        

        Element dbObjNode = createAndAppend( "database", general );
        boDefDatabaseObject[] obobs = def.getBoDatabaseObjects();
        if( obobs != null && obobs.length > 0 )
        {
            for (int i = 0; i < obobs.length; i++) 
            {
                Element objNode = createAndAppend( "object", dbObjNode );
                
                switch( obobs[i].getType() )
                {
                    case boDefDatabaseObject.DBOBJECT_INDEX:
                        objNode.setAttribute("type","index");
                        break;
                    case boDefDatabaseObject.DBOBJECT_UNIQUEKEY:
                        objNode.setAttribute("type","unique");
                        break;
                }
                objNode.setAttribute("id", obobs[i].getId() );
                createTextNode( objNode, "label", obobs[i].getLabel() );
                createTextNode( objNode, "expression", obobs[i].getExpression() );
            }
        }

        Element verNode = createAndAppend( "versioning", general );
        verNode.setAttribute("active", b(def.haveVersionControl()) );
        if( def.haveVersionControl() )
        {
            Element chkOptions = createAndAppend( "checkoutOptions", verNode );
            chkOptions.setAttribute("active","false");
            chkOptions.setAttribute("allowMultiple","false");
            
    
            Element chkWkf = createAndAppend( "workflows", verNode );
            createTextNode( chkWkf, "destroyWorkflow", "" );
            createTextNode( chkWkf, "modifyWorkflow", "" );
            createTextNode( chkWkf, "createWorkflow", "" );
        }
        

        Element textIndexNode = createAndAppend("textIndex", general);
        
        textIndexNode.setAttribute( "active", b( def.isTextIndexActive() ) );
        textIndexNode.setAttribute( "appendChilds", b( def.getIfIndexChilds() > 0 ) );
        textIndexNode.setAttribute( "deep", l( def.getIfIndexChilds()==0?1:def.getIfIndexChilds() ) );
        
        Element  tiProcess   = createAndAppend("process", textIndexNode );
        String[] tiAtts       = ((netgest.bo.def.v1.boDefHandlerImpl)def).getIndexProcessChild();
        for (int i = 0;tiAtts != null && i < tiAtts.length; i++) 
        {
            Element tiAtt = createAndAppend( "attribute", tiProcess );
            tiAtt.setAttribute("onlyCardID", b( def.indexOnlyCardID( tiAtts[i] ) ) );
            tiAtt.appendChild( xml.createTextNode( tiAtts[i] ) );
        }
    
        if( def.getCastToClassName()!=null && def.getCastToClassName().length() > 0 )
        {
            Element castToNode = createAndAppend( "castTo", general );
            castToNode.setAttribute("class", def.getCastToClassName() );
            String[] canCastTo = def.canCastTo();

            for (int i = 0;canCastTo != null && i < canCastTo.length; i++) 
            {
                createTextNode( castToNode, "interface", canCastTo[i] );
            }
        }

        if( !def.getLabel().equals( def.getBoDescription() ) )
            createTextNode( general, "description", def.getBoDescription() );
            
        createTextNode( general, "cardID", def.getCARDID() );
        createTextNode( general, "label", def.getLabel() );
        
        //((XMLNode)defInt.getNode()).selectSingleNode("business_implements");
        
        if( def instanceof boDefInterface )
        {
            boDefInterface defInt = (boDefInterface)def;
            String[] implObjs = defInt.getImplObjects();
            Element objectsNode = createAndAppend( "objects", general );
            for (int i = 0;defInt != null && i < implObjs.length; i++) 
            {
                createTextNode( objectsNode, "object", implObjs[i] );
            }
        }
        
    }
    
    public void serializeOpl( boDefHandler def, Element xeoModel )
    {
        Element opl = createAndAppend( "opl", xeoModel );
        opl.setAttribute("active", b( def.implementsSecurityRowObjects() ) );
        if( def.implementsSecurityRowObjects() )
        {
            boDefOPL oplDef = def.getBoOPL();
            if( oplDef != null )
            {
                String[] rkeys = oplDef.getReadKeyAttributes();
                String[] rwrite = oplDef.getWriteKeyAttributes();
                String[] rdelete = oplDef.getDeleteKeyAttributes();
                String[] rfc = oplDef.getFullControlKeyAttributes();
                if(   
                    ( rkeys != null && rkeys.length > 0 )
                    ||
                    ( rwrite != null && rwrite.length > 0 )
                    ||
                    ( rdelete != null && rdelete.length > 0 )
                    ||
                    ( rfc != null && rfc.length >0 )
                )
                {
                    Element clsKeys = createAndAppend( "classKeys", opl );
                    Element attKeys = createAndAppend( "attributeKeys", opl );

                    Element keysRead = createAndAppend( "read", attKeys );
                    Element keysWrite = createAndAppend( "write", attKeys );
                    Element keysDeleteKey = createAndAppend( "delete", attKeys );
                    Element keysFullControl = createAndAppend( "fullcontrol", attKeys );
                
                
                    String cls[]    = oplDef.getClassKeys();
                    if( cls != null )
                    {
                        for (int i = 0; i < cls.length; i++) 
                        {
                            Element clsKey = createAndAppend( "class", clsKeys );
                            clsKey.setAttribute( "active","true" );
                            clsKey.setAttribute( "name",cls[i] );
                        }
                    }
                    
                    serializeOplAttKeys( oplDef.getReadKeyAttributes(), "read", keysRead );
                    serializeOplAttKeys( oplDef.getWriteKeyAttributes(), "write", keysWrite );
                    serializeOplAttKeys( oplDef.getDeleteKeyAttributes(), "delete", keysDeleteKey );
                    serializeOplAttKeys( oplDef.getFullControlKeyAttributes(), "fullcontrol", keysFullControl );
                }
            }
        }
    }
    
    public final void serializeOplAttKeys( String[] keys, String nodeName, Element keysNode )
    {
        
        for (int i = 0;keys != null && i < keys.length; i++) 
        { 
            Element attNode = createAndAppend( "attribute", keysNode );
            attNode.setAttribute( "active","true" );
            attNode.appendChild( xml.createTextNode( keys[i] ) );
        }
    }

    public void serializeStates( boDefHandler def, Element xeoModel )
    {
        Element statesNode = createAndAppend( "states", xeoModel );
        boDefClsState state = def.getBoClsState();
        if( state != null )
        {
            if( def.getStateNameRefer() != null && def.getStateNameRefer().trim().length() > 0 )
            {
                statesNode.setAttribute("refers",def.getStateNameRefer());   
            }
            else
            {
                serializeState( state, statesNode );
            }
        }
    }
    
    public void serializeState( boDefClsState state, Element parent )
    {
        Element stateNode = createAndAppend( "state", parent );
        String stateName = state.getName();
        stateNode.setAttribute("name", state.getName() );
        createTextNode(stateNode, "label", state.getLabel() );
        
        Element stateOptionsNode = createAndAppend( "options", stateNode );
        boDefClsState[] childs = state.getChildStates();
        for (int i = 0;childs != null && i < childs.length; i++) 
        {
            if( childs[i].getParent() == state )  
            {
                Element stateOptionNode = createAndAppend("option", stateOptionsNode );
                String optionName       = childs[i].getName();
                stateOptionNode.setAttribute( "name", optionName );
                stateOptionNode.setAttribute( "value", l(childs[i].getNumericForm()) );
                stateOptionNode.setAttribute( "active", "true" );
                
                createTextNode( stateOptionNode, "label", childs[i].getLabel() );
                boDefClsState[] grandChild = childs[i].getChildStates();
                Element subStates = createAndAppend( "subStates", stateOptionNode );
                for (int z = 0;grandChild!= null &&  z < grandChild.length; z++) 
                {
                    serializeState( grandChild[z], subStates );
                }
            }
        }
    }
    
    public final void serializeMethods( boDefHandler def, Element xmlModel )
    {
        boDefMethod[] meth = def.getBoMethods();
        serializeMethod( xmlModel, meth, true );
    }
    
    public final void serializeEvents( boDefHandler def, Element xeoModel )
    {
        createEventsNode( xeoModel, def.getBoClsEvents() );
    }

    public final void serializeFwdObjects( boDefHandler def, Element xeoModel )
    {
        Element fwdsNode = createAndAppend( "fwdObjects", xeoModel );
        boDefForwardObject[] fwdObject = def.getForwardObjects();
        for (int i = 0;fwdObject != null && i < fwdObject.length; i++) 
        {
            Element fwdNode = createAndAppend( "fwdObject", fwdsNode );
            fwdNode.setAttribute("name", fwdObject[i].toBoObject() );
            fwdNode.setAttribute("openDoc", b(fwdObject[i].openDoc()));
            fwdNode.setAttribute("label",fwdObject[i].getLabel());
            HashMap map = fwdObject[i].getMaps();
            if( map != null )
            {
                Element mapsNode = createAndAppend("maps", fwdNode );
                Iterator it = map.keySet().iterator();
                String ckey;
                while( it.hasNext() )
                {
                    ckey=(String)it.next();
                    Element mapNode = createAndAppend("map",mapsNode);
                    createTextNode( mapNode, "attr_origin", ckey );
                    createTextNode( mapNode, "attr_destiny", (String)map.get( ckey ) );
                }
            }
            
            createCDataNode( fwdNode, "afterMapClass", fwdObject[i].getAfterMapClass() );
            createCDataNode( fwdNode, "beforeMapClass", fwdObject[i].getBeforeMapClass() );
            createCDataNode( fwdNode, "onSaveFwdObject", fwdObject[i].getOnSaveFwdObject() );
        }
    }
    
    public final void serializeAttributes( boDefHandler def, Element xmlModel )
    {
    
        Element attsNode = createAndAppend( "attributes", xmlModel );
        boDefAttribute[] attDef = def.getBoAttributes();
        for (int i = 0;attDef != null && i < attDef.length; i++) 
        {
            boDefAttribute att = attDef[i];
            if( attDef[i].getAtributeType() != boDefAttribute.TYPE_STATEATTRIBUTE )
            {
                serializeAttribute( att, attsNode );
            }
        }
        
    }

    public final void serializeAttribute( boDefAttribute att, Element attsNode )
    {
        String attNodeName = "attribute";
        String len         = null;
        byte attType = -1;
        if( att.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
        {
            if( att.getMaxOccurs() > 1 ) 
            {
                attNodeName = "attributeObjectCollection";
            }
            else
            {
                attNodeName = "attributeObject";
            }
        }
        else
        {
            attType = att.getValueType();
            switch( att.getValueType() )
            {
                case boDefAttribute.VALUE_BOOLEAN:
                    attNodeName = "attributeBoolean";
                    break;
                case boDefAttribute.VALUE_CHAR:
                    attNodeName = "attributeText";
                    len = l(att.getLen());
                    break;
                case boDefAttribute.VALUE_CLOB:
                    attNodeName = "attributeLongText";
                    break;
                case boDefAttribute.VALUE_CURRENCY:
                    attNodeName = "attributeCurrency";
                    len = l(att.getLen());
                    break;
                case boDefAttribute.VALUE_DATE:
                    attNodeName = "attributeDate";
                    break;
                case boDefAttribute.VALUE_DATETIME:
                    attNodeName = "attributeDateTime";
                    break;
                case boDefAttribute.VALUE_DURATION:
                    attNodeName = "attributeDuration";
                    break;
                case boDefAttribute.VALUE_IFILELINK:
                    attNodeName = "attributeBinaryData";
                    break;
                case boDefAttribute.VALUE_NUMBER:
                    attNodeName = "attributeNumber";
                    len = l(att.getLen());
                    break;
                case boDefAttribute.VALUE_SEQUENCE:
                    attNodeName = "attributeSequence";
                    len = l(att.getLen());
                    break;
                default:
                    throw new RuntimeException(MessageLocalizer.getMessage("ERROR_IN_ATTRIBUTE")+" " + att.getName() );
                
            }
        }
        
        String dt = "N";
        if(  att.getAtributeType()!=boDefAttribute.TYPE_OBJECTATTRIBUTE )
        {
            dt = boDefDataTypeMapping.getDbmType( att.getType() );
        }
        
        Element attNode = createAndAppend( attNodeName, attsNode );
        attNode.setAttribute("name",att.getName() );
        
        
        if( attType == -1 )
            createTextNode( attNode, "type", att.getType() );
        
        if( attType == boDefAttribute.VALUE_SEQUENCE )
        {
            createTextNode( attNode, "type", att.getType() );
        }
            
        
        if( len != null )
        {
            createTextNode( attNode, "len", len );
        }

        if( attType == boDefAttribute.VALUE_NUMBER )
        {
            if( !"-999999999".equals( att.getMin()) ) 
                createTextNode( attNode, "min", att.getMin() );
            if( !"".equals( att.getMax() ) )
                createTextNode( attNode, "max", att.getMax() );
            createTextNode( attNode, "grouping", b(att.getGrouping().toLowerCase().startsWith("y")) );
            createTextNode( attNode, "decimals", l(att.getDecimals()) );
            createTextNode( attNode, "minDecimals", l(att.getMinDecimals()) );
        }
        createTextNode( attNode, "label", att.getLabel() );
        if( !att.getLabel().equals( att.getDescription() ) )
            createTextNode( attNode, "description", att.getDescription() );

        if( 
            (attType == boDefAttribute.VALUE_CHAR && att.getLen() == 4000 )
            || 
            attType == boDefAttribute.VALUE_CLOB 
            
           )
            createTextNode( attNode, "editor", att.getEditorType().toUpperCase() );

        if( attType == boDefAttribute.VALUE_DURATION )
            createTextNode( attNode, "clock", b(att.getClock()) );
            
        if( attType == boDefAttribute.VALUE_BOOLEAN )
            createTextNode( attNode, "renderAsCheck", b(att.renderAsCheckBox()) );
            
        if( attType == -1 )
            createTextNode( attNode, "renderAsLov", b(att.renderAsLov()) );
            
        createTextNode( attNode, "tooltip", att.getTooltip() );
        
        if( attType == -1 && att.getReferencedObjectDef() != null && !att.getReferencedObjectDef().getBoCanBeOrphan() )
        {
            createTextNode( attNode, "showLookup", b(att.getShowLookup()) );
        }
        
        if( attType == -1 )
        {
            if( !att.supportManualAdd() )
                createTextNode( attNode, "manualAdd", b(att.supportManualAdd()) );
            if( !att.supportManualOperation() )
                createTextNode( attNode, "manualOperation", b(att.supportManualOperation()) );
            
            if( "attributeObjectCollection".equals( attNodeName ) )
            {
                if( 999999999 != att.getRuntimeMaxOccurs() )
                    createTextNode( attNode, "runtimeMaxOccurs", l(att.getRuntimeMaxOccurs() ) );
               
                if( att.getMaxOccurs() == 999999999 )
                {
                    createTextNode( attNode, "maxOccurs", "N" );
                }
                else
                {
                    createTextNode( attNode, "maxOccurs", l(att.getMaxOccurs()) );
                }
                createTextNode( attNode, "minOccurs", l(att.getMinOccurs()) );
            }
            
            if( att.getSetParent() == boDefAttribute.SET_PARENT_YES ) 
                createTextNode( attNode, "setParent", "true" );
            else if ( att.getSetParent() == boDefAttribute.SET_PARENT_NO )
                createTextNode( attNode, "setParent", "false" );
        }
        
        
        if( attType != -1 )
        {
            if( !att.textIndex() )
            {
                Element textIndexNode = createAndAppend( "textIndex", attNode );
                textIndexNode.setAttribute( "active", b(att.textIndex()) );
            }
        }
        else
        {
            boolean ti_active=att.textIndex();
            boolean ti_onlyCardId=att.indexOnlyCardId();
            if( !ti_active || ti_onlyCardId )
            {
                Element textIndexNode = createAndAppend( "textIndex", attNode );
                textIndexNode.setAttribute( "active", b( ti_active ) );
                textIndexNode.setAttribute( "onlyCardID", b( ti_onlyCardId ) );
                textIndexNode.setAttribute( "appendChilds", "false" );
                textIndexNode.setAttribute( "deep", "1" );
            }
            String[] objects =att.getObjectsName();
            if( objects != null )
            {
                Element objsNode = createAndAppend( "objects", attNode );
                for (int z = 0; z < objects.length; z++) 
                {
                    createTextNode( objsNode, "object", objects[z] );
                }
            }

            Element objsFilter = createAndAppend( "objectFilter", attNode );
            boDefObjectFilter[] filters = att.getObjectFilter();
            for (int i = 0;filters != null && i < filters.length; i++) 
            {
                Element filter = createAndAppend( "filter", objsFilter );
                Element cond   = createAndAppend( "condition", filter );
                if( filters[i].getCondition() != null )
                {
                    createXeoCodeNode( filter, "condition", null, filters[i].getCondition(), false );
                }
                createTextNode( filter, "xeoql", filters[i].getXeoQL() );
            }
        }
        
        String bhImg    = att.getBeahvior_Img();
        String bhScript = att.getBeahvior_Script();
        if( 
            bhImg != null && bhImg.length() > 0 
            ||
            bhScript != null && bhScript.trim().length() == 0
        )
        {
            Element bhNode = createAndAppend( "behavior", attNode );
            createTextNode( bhNode, "img", bhImg );
            createTextNode( bhNode, "script", bhScript );
        }
        
        String lovName = att.getLOVName();
        if( lovName != null && lovName.trim().length() > 0 )
        {
            Element lovNode = createAndAppend( "lov", attNode );
            lovNode.setAttribute("name",lovName);
            lovNode.setAttribute("retainValues",b(att.getLovRetainValues()));
            createXeoCodeNode( lovNode, "editable", Boolean.FALSE, att.getLovEditable(), true );
        }
        
        if( attType == -1 )
        {
            if( att.hasTransformer() )
            {
                String transf_class = att.getTransformClassMapName();
                String transf_from  = att.getTransformObject();
                Element trfNode = createAndAppend( "transformer", attNode );
                createTextNode( trfNode, "from", transf_from );
                createTextNode( trfNode, "class", transf_class );
            }
        }
        
        if(att.getEvents() != null && att.getEvents().length > 0 )
        {
            createEventsNode( attNode, att.getEvents() );       
        }
        serializeMethod( attNode, att.getMethods(), false );
        
        String  db_fieldname    = att.getDbName();
        String  xdb_fieldname    = att.getName();
        if( attType == -1 )
        {
            xdb_fieldname = xdb_fieldname+"$";
        }
        boolean db_unique       = att.getDbIsUnique();
        boolean db_indexed      = att.getDbIsIndexed();
        boolean db_tabled       = att.getDbIsTabled();
        boolean db_constraint   = att.getDbCreateConstraints();
        boolean db_binding      = att.getDbIsBinding();
        
        if( 
            !db_fieldname.equalsIgnoreCase( xdb_fieldname ) 
            ||
            db_unique
            ||
            db_indexed
            ||
            db_tabled
            ||
            !db_constraint
            ||
            !db_binding
           )
        {
            Element dataBaseNode = createAndAppend("database", attNode);
            createTextNode( dataBaseNode, "fieldname", db_fieldname );
            createTextNode( dataBaseNode, "unique", b(db_unique) );
            createTextNode( dataBaseNode, "indexfull", "true" );
            createTextNode( dataBaseNode, "indexed", b(db_indexed) );
            createTextNode( dataBaseNode, "tabled", b(db_tabled) );
            createTextNode( dataBaseNode, "constraint", b(db_constraint) );
            createTextNode( dataBaseNode, "binding", b(db_binding) );
            
        }
        
        createXeoCodeNode( attNode, "required", Boolean.FALSE, att.getRequired(), false );
        createXeoCodeNode( attNode, "recommend", Boolean.FALSE, att.getRecommend(), false );
        
        if( !attNodeName.equals( boDefAttribute.ATTRIBUTE_SEQUENCE ) )
        {
            createXeoCodeNode( attNode, "formula", null,att.getFormula(), true );
        }
            
        createXeoCodeNode( attNode, "defaultValue", null, att.getDefaultValue(), true );
        createXeoCodeNode( attNode, "valid",Boolean.TRUE,att.getValid(), true );
        createXeoCodeNode( attNode, "disableWhen",Boolean.FALSE, att.getDisableWhen(), true );
        createXeoCodeNode( attNode, "hiddenWhen",Boolean.FALSE, att.getHiddenWhen(), true );
        createXeoCodeNode( attNode, "onChangeSubmit",Boolean.FALSE, att.getOnChangeSubmit(), false );
        
        if( "attributeObjectCollection".equals( attNodeName ) ) 
        {
            serializeBridge( att.getBridge(), attNode );
        }
    }
    
    
    public final void serializeBridge( boDefBridge bridge, Element attNode )
    {
        Element bridgeNode = createAndAppend("bridge", attNode);
        Element bridgeAttsNode = createAndAppend("attributes", bridgeNode );
        boDefAttribute[] atts = bridge.getBoAttributes();
        for (int i = 0;atts != null && i < atts.length; i++) 
        {
            serializeAttribute( atts[i], bridgeAttsNode );
        }
    }
    
    public final void serializeViewers( boDefHandler def, Element xmlModel )
    {
        
        Element viewersNode = createAndAppend( "viewers", xmlModel );
        
        boDefViewer[] viewers = def.getBoViewers();
        for ( int i = 0;(viewers == null && i==0) || ( viewers != null && i < viewers.length); i++ )
        {
            Element viewerNode = createAndAppend( "viewer", viewersNode );
            viewerNode.setAttribute( "name", "general" );
            
            Element formsNode  = createAndAppend( "forms", viewerNode ); 
            Element catsNode = createAndAppend( "categories", viewerNode );
            if(viewers!= null && viewers[i] != null ) 
            {
                if( viewers[i].getChildNode("forms") != null )
                {
                    ngtXMLHandler[] formXml = viewers[i].getChildNode("forms").getChildNodes();
                    for (int z = 0; z < formXml.length; z++) 
                    {
                        String xfn = formXml[z].getNode().getNodeName();
                        Element formNode = createAndAppend( "form", formsNode );
                        // Para que seja o primeiro atributo do nó.
                        formNode.setAttribute("name", xfn );
                        
                        repairViewerTags( (Element)formXml[z].getNode() );
                        cloneChild( (Element)formXml[z].getNode(), formNode );
                        // Se por acaso for subscrito na clonagem forçar novamente o valor com o nome do form
                        formNode.setAttribute("name", xfn );
                    }
                }
                if( viewers[i].getChildNode( "categories" ) != null )
                {
                    ngtXMLHandler[] cats = viewers[i].getChildNode( "categories" ).getChildNodes();
                    for (int z = 0; z < cats.length; z++) 
                    {
                        serializeCategories( viewers[ i ].getCategory( cats[z].getNodeName() ), catsNode );
                    }
                }
            }
        }
        
    }
    
    public final void serializeCategories( boDefViewerCategory cat, Element parent )
    {
        Element catNode = createAndAppend( "category", parent );
        catNode.setAttribute("name", cat.getName() );
        catNode.setAttribute("label", cat.getLabel() );
        catNode.setAttribute("description", cat.getDescription() );
        String[] atts = cat.getAttributesName();
        Element attsNode = createAndAppend( "attributes", catNode );
        for (int i = 0;atts != null && i < atts.length; i++) 
        {
            createTextNode( attsNode, "attribute", atts[i] );
        }
        
        boDefViewerCategory[] childs = cat.getChildCategories();
        if( childs != null )
        {
            Element catsNode = createAndAppend( "categories", catNode );
            for (int i = 0; i < childs.length; i++) 
            {
                serializeCategories( childs[i], catsNode );
            }
        }
    }
    
    private final void repairViewerTags( Element element ) 
    {
        try
        {
            Document   xmlDoc   = element.getOwnerDocument();
            XMLElement vElement = (XMLElement)element;
            // Remove Grid Label, Not read as Node, only as element.
            NodeList list = vElement.selectNodes("//grid/label");
            for (int i = 0; i < list.getLength(); i++) 
            {
                list.item( i ).getParentNode().removeChild( list.item( i ) );
            }
            
            // Renomeia o atributo atribute -> para attribute, foi encontrado este erro em 
            // alguns ficheiros.
            list = vElement.selectNodes("//cols/col/atribute");
            for (int i = 0; i < list.getLength(); i++) 
            {
                Element oldNode = (Element)list.item(i);
                Element newNode = xmlDoc.createElement("attribute");
                cloneChild( oldNode, newNode );
                oldNode.getParentNode().replaceChild( newNode, oldNode );
            }

            // Renomeia o atributo atribute -> para attribute, foi encontrado este erro em 
            // alguns ficheiros.
            list = vElement.selectNodes("explorer/attributes/*");
            for (int i = 0; i < list.getLength(); i++) 
            {
                Element oldNode = (Element)list.item(i);
                Element newNode = xmlDoc.createElement("attribute");
                newNode.appendChild( xmlDoc.createTextNode( oldNode.getNodeName() ) );
                cloneChild( oldNode, newNode );
                oldNode.getParentNode().replaceChild( newNode, oldNode );
            }

            
        }
        catch (XSLException e)
        {
            throw new RuntimeException( e );
        }
    }
    
    
    private final void createEventsNode( Element parent, boDefClsEvents[] events )
    {
        Element eventsNode = createAndAppend( "events",parent );
        for (int i = 0;events != null && i < events.length; i++) 
        {
            if( events[i].getEventCode() != null  )
            {
                Element eventNode = createAndAppend( "event", eventsNode );
                eventNode.setAttribute("name", events[i].getEventName() );
                
                Element body = createCDataNode( eventNode, "body", events[i].getEventCode().getSource() );
                body.setAttribute("language", events[i].getEventCode().getLanguage()==boDefXeoCode.LANG_JAVA?"JAVA":"XEP" );
                
            }
        }
    }
    
    public final void serializeMethod( Element parent, boDefMethod[] meth, boolean createEmpty )
    {
        if( meth != null )
        {
            Element methodsNode = createAndAppend( "methods", parent );
            boolean  have=false;
            for (int i = 0; i < meth.length; i++) 
            {
               if( !meth[i].getIsNative() || meth[i].getIsNativeOverwrited() )
                {
                    have = true;
                    Element methodNode = createAndAppend( "method", methodsNode );
                    methodNode.setAttribute( "name", meth[i].getName() );
                    methodNode.setAttribute( "public", "false" ); //TODO:Antenção
                    methodNode.setAttribute( "menu", b( meth[i].getIsMenu() ) );
                    methodNode.setAttribute( "toolbar", b( meth[i].getIsToolbar() ) );
                    methodNode.setAttribute( "requiredTransaction", b( meth[i].getRequireTransaction() ) );
                    methodNode.setAttribute( "serverOnly","false"  );
                    methodNode.setAttribute( "openDoc", b(meth[i].openDoc()) );
                    methodNode.setAttribute( "modeTemplate", b(meth[i].templateMode()) );
                    methodNode.setAttribute( "toObject", meth[i].getObjectName() );
                    
                    Element label = createTextNode( methodNode, "label", meth[i].getLabel() );
                    
                    String javaSBefore = meth[i].getJavaScriptToRunBefore( "edit" );
                    String javaSAfter = meth[i].getJavaScriptToRunAfter( "edit" );
                    if( 
                        javaSBefore != null && javaSBefore.trim().length() == 0 
                        ||
                        javaSAfter != null && javaSAfter.trim().length() == 0 
                    
                    )
                    {
                        Element jsRunNode = createAndAppend( "javascriptToRun", methodNode );
                        Element jsViewer  = createAndAppend( "viewer", jsRunNode );
                        jsViewer.setAttribute("name", "edit");
                        createCDataNode( jsViewer, "before", javaSBefore );
                        createCDataNode( jsViewer, "after", javaSAfter );
                    }
                    
                    if( meth[i].getHiddenWhen() != null )
                    {
                        createXeoCodeNode( methodNode, "hiddenWhen", null, meth[i].getHiddenWhen(), false );
                    }
                    
                    
                    Element assNode = createAndAppend( "assinature", methodNode );
                    assNode.setAttribute("return", meth[i].getReturnType() );
                    String[] assNames = meth[i].getAssinatureArgNames();
                    String[] assClass = meth[i].getAssinatureClassNames();
                    for( int z = 0; assNames != null && z < assNames.length; z++ )
                    {
                        Element argumentNode = createAndAppend("argument", assNode );
                        argumentNode.setAttribute("name", assNames[z] );
                        argumentNode.setAttribute("type", assClass[z] );
                        // TODO: Não coloca nomes de parametros e tipos                
                    }
                    
                    Element code = createCDataNode( methodNode, "body", meth[i].getBody() );
                    code.setAttribute("language","JAVA");
                }
            }
            
            if( !have && !createEmpty )
            {
                parent.removeChild( methodsNode );
            }
        }
    }

    private final void createXeoCodeNode( Element parent, String nodeName, Boolean boolDefault, boDefXeoCode xeoCode, boolean depends )
    {
        if( xeoCode!=null && xeoCode.getSource() != null && xeoCode.getSource().trim().length() > 0 )
        {
            Element code = createAndAppend( nodeName, parent );
            String langName = "BOL";
            
            switch( xeoCode.getLanguage() )
            {
                case boDefXeoCode.LANG_BOL:
                    langName="BOL";
                    break;
                case boDefXeoCode.LANG_JAVA:
                    langName="JAVA";
                    break;
                case boDefXeoCode.LANG_XEP:
                    langName="XEP";
                    break;
            }
            code.setAttribute("language", langName );
            String[] deps = xeoCode.getDepends();
            if( depends )
            {
                Element depsNode = createAndAppend("depends", code );
                for (int i = 0;deps != null && i < deps.length; i++) 
                {
                    createTextNode( depsNode, "attribute", deps[i] );
                }
            }
            if( "BOL".equals( langName ) )
            {
                if( xeoCode.needsClass() )
                {
                        code.appendChild( 
                            xml.createTextNode( xeoCode.getSource()  )
                        );
                }
                else
                {
                    boolean value = "y".equalsIgnoreCase( xeoCode.getSource()) ||
                                    "s".equalsIgnoreCase( xeoCode.getSource()) ||
                                    "t".equalsIgnoreCase( xeoCode.getSource());
                    
                    if( boolDefault == null || value != boolDefault.booleanValue())
                    {
                        code.appendChild( 
                            xml.createTextNode( b( value )  )
                        );
                    }
                    else
                    {
                        // Se o valor for igual ao por omissão, retira o elemento
                        parent.removeChild( code );
                    }
                }
            }
            else
            {
                code.appendChild( xml.createTextNode( xeoCode.getSource() ) );
            }
        }
    }
    
    
    private static final void convertLovFile( File file )
    {
        try
        {
            XMLDocument doc = ngtXMLUtils.loadXMLFile( file.getAbsolutePath() );
            XMLNode node = (XMLNode)doc.selectSingleNode("Lov/*/description");
            if( node != null )
            {
                XMLNode ptNode = (XMLNode)node.selectSingleNode("pt");
                if( ptNode != null )
                {
                    String description = ptNode.getText();
                    Node nodeDesc = doc.createElement("description");
                    nodeDesc.appendChild( doc.createTextNode( description ) );
                    node.getParentNode().replaceChild( nodeDesc, node );
                }
            }
            NodeList list = doc.selectNodes("Lov/*/details/item");
            for (int i = 0; i < list.getLength(); i++) 
            {
                node = (XMLNode)((XMLNode)list.item( i )).selectSingleNode( "label" );
                XMLNode ptNode = (XMLNode)node.selectSingleNode("pt");
                if( ptNode != null )
                {
                    String desc = ptNode.getText();
                    Node nodeDesc = doc.createElement("label");
                    nodeDesc.appendChild( doc.createTextNode( desc ) );
                    node.getParentNode().replaceChild( nodeDesc, node );
                }
            }
            String fileName = file.getName();
            fileName = fileName.substring(0, fileName.indexOf( "$lov.xml" ) );
            fileName = file.getParent()+ File.separator + fileName + ".xeolov";
            FileOutputStream fout = new FileOutputStream( fileName );
            doc.print( fout );
            fout.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    
    private final void cloneChild( Element src, Element dest )
    {
        Document doc = dest.getOwnerDocument();
        NamedNodeMap mapAtts = src.getAttributes();
        for (int i = 0; i < mapAtts.getLength(); i++) 
        {
            dest.setAttribute( mapAtts.item( i ).getNodeName(),mapAtts.item( i ).getNodeValue() );
        }
        NodeList childs = src.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) 
        {
            dest.appendChild( doc.importNode( childs.item( i ), true ) );
        }
    }

    private final Element createAndAppend( String nodeName, Element node )
    {
        return (Element)node.appendChild(
            xml.createElement( nodeName )
        );
    }
    
    private final Element createTextNode( Element node, String nodeName, String value )
    {
        Element elem = xml.createElement( nodeName );
        node.appendChild( elem );
        elem.appendChild( xml.createTextNode( value ) );
        return elem;
    }
    private final Element createCDataNode( Element node, String nodeName, String value )
    {
        Element elem = xml.createElement( nodeName );
        node.appendChild( elem );
        elem.appendChild( xml.createCDATASection( value ) );
        return elem;
    }
    
    private static final String b( boolean val )
    {
        return String.valueOf( val );
    }

    private static final String l( long val )
    {
        return String.valueOf( val );
    }
    
}