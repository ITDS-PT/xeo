/*Enconding=UTF-8*/
package netgest.bo.plugins.data;

import java.io.*;

import java.util.*;

import netgest.bo.*;
import netgest.bo.builder.*;
import netgest.bo.def.*;
import netgest.bo.runtime.*;

import netgest.utils.*;

import oracle.xml.parser.v2.*;

import netgest.bo.system.Logger;
import org.w3c.dom.*;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class MapType2Def
{
    public static final Logger logger = Logger.getLogger( MapType2Def.class.getName() );
    
    private static final Object DUMMY = new Object();

    private String p_defaultDS = "DATA";
    private String p_defaultSchema = "";
    private MapType2Def.ObjectDataSource p_readerds;
    private MapType2Def.ObjectDataSource p_writerds;
    private MapType2Def.ObjectDataSource p_objectds;
    private boDefHandler p_bodef;
    private boolean p_isdefault;
    String p_phisicalmastertable = null;
    
    private String p_eventsclass = null;
    private String p_sqlbeforesync = null;
    private String p_sqlaftersync = null;
    private String p_type = null;
    
    private boolean p_localObjects              = true;
    private boolean p_preRegister               = false;
    private String  p_scheduleInterval          = "1 dia";
    private boolean p_syncTextIndex             = false;
    private String  p_syncTextIndexInterval     = "7 dias";
    
    private boObjectFinder[] p_finders                  = null;
    
    private MapType2Def.ChangeDetectionHandler p_tabletriggers   = null;
    private MapType2Def.ChangeDetectionHandler p_tablechecksums  = null;
    
    private static Hashtable p_cacheDefs       = new Hashtable();
    private static Hashtable p_cacheUndepDefs  = new Hashtable();
    
    public MapType2Def(boDefHandler parent, XMLDocument xml)
    {
        p_bodef = parent;
        p_isdefault = xml == null;

        try
        {
            if (xml != null)
            {
                p_type = xml.getDocumentElement().getAttribute( "type" );
                if( p_type == null || "".equals( p_type ) )
                {
                    p_type = "1";
                }
            
                ngtXMLHandler storageNode = new ngtXMLHandler(((XMLNode) xml.getDocumentElement()).selectSingleNode(
                            "storage"));

                if (storageNode.getNode() != null) 
                {
                    p_defaultDS = storageNode.getChildNodeText("dataSource", "");

                    if (p_defaultDS.trim().length() == 0)
                    {
                        p_defaultDS = "DATA";
                    }

                    p_defaultSchema = storageNode.getChildNodeText("schema", "");
                }
                ngtXMLHandler eventsNode = new ngtXMLHandler( ((XMLNode) xml.getDocumentElement()).selectSingleNode("events") ); 
                if( eventsNode.getNode() != null )
                {
                    p_eventsclass = eventsNode.getChildNodeText("class",null);

                    ngtXMLHandler beforeNode = eventsNode.getChildNode("beforesync");
                    if( beforeNode != null )
                    {
                        p_sqlbeforesync = beforeNode.getChildNodeText("sql",null);
                    }
                    ngtXMLHandler afterNode = eventsNode.getChildNode("aftersync");
                    if( afterNode != null )
                    {
                        p_sqlaftersync = afterNode.getChildNodeText("sql",null);
                    }
                }
                
                XMLNode cd = (XMLNode)((XMLNode)xml.getDocumentElement()).selectSingleNode("changedetection");
                if( cd != null )
                {
                    XMLNode xnode;
                    
                    xnode = (XMLNode)cd.selectSingleNode("trigger");
                    if( xnode != null )
                    {
                        p_tabletriggers = new MapType2Def.ChangeDetectionHandler( xnode );
                    }
                    
                    xnode = (XMLNode)cd.selectSingleNode("checksum");
                    if( xnode != null )
                    {
                        p_tablechecksums = new MapType2Def.ChangeDetectionHandler( xnode );
                    }
                }
                
                XMLNode find = (XMLNode)((XMLNode)xml.getDocumentElement()).selectSingleNode("finders");
                if( find != null )
                {
                    
                    NodeList nl_finders = find.selectNodes("finder");
                    
                    ArrayList list = new ArrayList();
                    
                    for (int i = 0; i < nl_finders.getLength(); i++) 
                    {
                        
                        ngtXMLHandler xh_finder = new ngtXMLHandler( nl_finders.item( i ) );
                        ngtXMLHandler xh_atts[] = xh_finder.getChildNode( "attributes" ).getChildNodes();
                        String[] atts = new String[ xh_atts.length ];
                        String[] attsLbl = new String[ xh_atts.length ];
                        for (int j = 0; j < atts.length; j++) 
                        {
                            atts[j] = xh_atts[j].getText();
                            boDefAttribute defatt = p_bodef.getAttributeRef( atts[j] );
                            if( defatt != null )
                            {
                                attsLbl[j] = defatt.getLabel();
                            }
                            
                        }
                        list.add( new boObjectFinder(
                                                boObjectFinder.UNIQUE, 
                                                xh_finder.getAttribute( "id" ), 
                                                atts,
                                                attsLbl
                                            )  
                                        );
                    }
                    p_finders = (boObjectFinder[])list.toArray( new boObjectFinder[ list.size() ] );
                }


                XMLNode conf = (XMLNode)((XMLNode)xml.getDocumentElement()).selectSingleNode("configuration");
                if( conf != null )
                {
                    XMLNode confreg = (XMLNode)conf.selectSingleNode("register");
                    if( confreg != null )
                    {
                        XMLNode auxnode;
                        auxnode = (XMLNode)confreg.selectSingleNode("preRegister");
                        if( auxnode != null && auxnode.getText() != null )
                        {
                            p_preRegister = auxnode.getText().toUpperCase().startsWith("Y");
                        }
                        auxnode = (XMLNode)confreg.selectSingleNode("registerInterval");
                        if( auxnode != null && auxnode.getText() != null )
                        {
                            p_scheduleInterval = auxnode.getText();
                        }
                        
                        auxnode = (XMLNode)confreg.selectSingleNode("syncTextIndex");
                        if( auxnode != null && auxnode.getText() != null )
                        {
                            p_syncTextIndex = auxnode.getText().toUpperCase().startsWith("Y");
                        }

                        auxnode = (XMLNode)confreg.selectSingleNode("syncTextIndexInterval");
                        if( auxnode != null && auxnode.getText() != null )
                        {
                            p_syncTextIndexInterval = auxnode.getText();
                        }

                    }
                    XMLNode conflocobj = (XMLNode)conf.selectSingleNode("localObjects");
                    if( conflocobj != null && conflocobj.getText() != null )
                    {
                        p_localObjects = conflocobj.getText().toUpperCase().startsWith("Y");
                    }
                }
            }

            p_objectds = new MapType2Def.ObjectDataSource(new ngtXMLHandler((xml == null)
                        ? null
                        : ((XMLNode) xml.getDocumentElement()).selectSingleNode(
                            "sources")), (byte) 0);
            
            
            
            p_readerds = new MapType2Def.ObjectDataSource(new ngtXMLHandler((xml == null)
                        ? null
                        : ((XMLNode) xml.getDocumentElement()).selectSingleNode(
                            "readers")), (byte) 0);
            p_writerds = new MapType2Def.ObjectDataSource(new ngtXMLHandler((xml == null)
                        ? null
                        : ((XMLNode) xml.getDocumentElement()).selectSingleNode(
                            "writers")), (byte) 1);
        }
        catch (XSLException e)
        {
            e.printStackTrace();
        }
    }
    
    
    public String getBeforeEventSQL()
    {
        return p_sqlbeforesync;
    }
    
    public String getEventsClass()
    {
        return p_eventsclass;
    }
    
    public String getAfterEventSQL()
    {
        return p_sqlaftersync;
    }
    
    public MapType2Def.ChangeDetectionHandler getTriggers()
    {
        return p_tabletriggers; 
    }
    
    public String getMapType()
    {
        return p_type;
    }

    public MapType2Def.ChangeDetectionHandler getChecksums()
    {
        return p_tablechecksums;
    }
    
    public static void clearCache()
    {
        p_cacheDefs.clear();
        p_cacheUndepDefs.clear();
    }
    
    public boObjectFinder[] getFinders()
    {
        return p_finders;
    }

    /**
     *
     * @since
     */
    public static final MapType2Def getDataSourceDefinition(
        boDefHandler objectDef)
    {
        String boname = objectDef.getName();
        Object xret = p_cacheDefs.get(boname);
        if( xret == null )
        {
            boConfig boconf = new boConfig();
            File xfile = new File(boconf.getDeploymentDir() + boname + boBuilder.TYPE_DS);
            XMLDocument doc = null;
    
            if (xfile.exists())
            {
                doc = ngtXMLUtils.loadXMLFile(boconf.getDeploymentDir() + boname +
                        boBuilder.TYPE_DS);
                xret = new MapType2Def(objectDef, doc);
                p_cacheDefs.put( boname, xret );
            }
            else
            {
                p_cacheDefs.put( boname, DUMMY );
            }
        }
        return xret instanceof MapType2Def?(MapType2Def)xret:null;
    }

    public static final MapType2Def getUndeployedDataSourceDefinition(
        boBuildRepository repository, boDefHandler objectDef)
    {
        String boname = objectDef.getName();
        Object obj = p_cacheUndepDefs.get(boname);
        if( obj == null )
        {
            boConfig boconf = new boConfig();
    
            //            File xfile = new File(boconf.getDefinitiondir()+"xeo$1.0/"+boname+"$ds.xml");
            File xfile = repository.getDataSourceFileFromDefinition(boname);
            XMLDocument doc = null;
    
            if ((xfile != null) && xfile.exists())
            {
                doc = ngtXMLUtils.loadXMLFile(xfile.getAbsolutePath());
                obj = new MapType2Def(objectDef, doc);
                p_cacheUndepDefs.put( boname, obj );
            }
            else
            {
                p_cacheUndepDefs.put( boname, new Boolean( false ) );
            }
        }
        if( obj instanceof Boolean ) obj = null;
        MapType2Def ret = (MapType2Def)obj;
        
        return ret;
    }

    public final boolean isDefault()
    {
        return p_isdefault;
    }
    
    public final boolean haveMappingFields()
    {
        boolean ret = false;
        if( !this.isDefault() )
        {
            MapType2Def.ObjectDS[] ds = this.getObjectDataSources().getDataSources();
            for (int i = 0; i < ds.length; i++) 
            {
                if ( ds[i].getLocalAttributes() != null && ds[i].getLocalAttributes().length > 0 )
                {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }
    
    public final boolean haveLocalTable()
    {
        return haveMappingFields();   
    }

    public final String getDefaultDataSource()
    {
        return p_defaultDS;
    }

    public final String getDefaultSchema()
    {
        return p_defaultSchema;
    }

    public final MapType2Def.ObjectDataSource getReaderDataSource()
    {
        return p_readerds;
    }

    public final MapType2Def.ObjectDataSource getWriterDataSource()
    {
        return p_writerds;
    }

    public final MapType2Def.ObjectDataSource getObjectDataSources()
    {
        return p_objectds;
    }
    
    public final String getScheduleInterval()
    {
        return p_scheduleInterval;   
    }
    
    public boolean canHaveLocalObjects()
    {
        return p_localObjects;
    }
    
    public boolean getPreRegisterObjects()
    {
        return p_preRegister;
    }
    
    public boolean getSyncTextIndex()
    {
        return p_syncTextIndex;
    }

    public String getSyncTextIndexInterval()
    {
        return p_syncTextIndexInterval;
    }
    
    
    
    

    //    public final String getPhisicalMasterTable()
    //    {
    //        if (p_phisicalmastertable == null )
    //        {
    //            if( this.isDefault() )
    //            {
    //                boolean multi = false;
    //                boDefHandler tmpmostdef = p_bodef;
    //                boDefHandler pmostdef = p_bodef;
    //                String xsuper;
    //                while( tmpmostdef!= null && (xsuper = tmpmostdef.getBoSuperBo()) != null )
    //                {
    //                    tmpmostdef = boDefHandler.getBoDefinition( xsuper );
    //                    if( tmpmostdef!= null && tmpmostdef.getClassType() == boDefHandler.TYPE_CLASS )
    //                    {
    //                        if( tmpmostdef.getBoMasterTable().equals( p_bodef.getBoMasterTable() ) )
    //                        {
    //                            pmostdef = tmpmostdef;
    //                        }
    //                    }
    //                }
    //                boDefHandler[] subs = pmostdef.getBoSubClasses();
    //                for (int i = 0; i < subs.length; i++) 
    //                {
    //                    if( subs[i].getClassType() == boDefHandler.TYPE_CLASS )
    //                    {
    //                        if( !subs[i].getBoMasterTable().equals( pmostdef.getBoMasterTable() ) )                        
    //                        {
    //                            multi = true;
    //                            break;
    //                        }
    //                    }
    //                }
    //                if( multi )
    //                {
    //                    p_phisicalmastertable =p_bodef.getBoMasterTable()+"_DATA";
    //                }
    //                else
    //                {
    //                    p_phisicalmastertable = p_bodef.getBoMasterTable();
    //                }
    //            }
    //            else
    //            {
    //                p_phisicalmastertable = p_bodef.getBoMasterTable();//+"_LOCAL";
    //            }
    //        }
    //        return p_phisicalmastertable;
    //    }
    public class ObjectDataSource
    {
        private ngtXMLHandler   p_node;
        private Hashtable       p_ds;
        private String          p_classname;
        private boolean         p_fireevents = false;
        private byte p_type;

        public ObjectDataSource(ngtXMLHandler sourcesNode, byte type)
        {
            p_node = sourcesNode;
            p_type = type;
            init();
        }

        private final void init()
        {
            p_ds = new Hashtable();

            if (p_node.getNode() != null)
            {
                ngtXMLHandler[] nodes = p_node.getChildNode("tables").getChildNodes();
                for (byte i = 0; i < nodes.length; i++)
                {
                    MapType2Def.ObjectDS ds = new MapType2Def.ObjectDS(nodes[i], p_type);
                    p_ds.put(ds.getName(), ds);
                }
                ngtXMLHandler classnode = p_node.getChildNode("class");
                if( classnode != null )
                {
                    p_classname  = classnode.getAttribute( "name",null );
                    String xfire = classnode.getAttribute( "fireObjectEvents" , "yes" );
                    if( xfire.equalsIgnoreCase("yes") || xfire.equalsIgnoreCase("true") || xfire.equalsIgnoreCase("y") )
                    {
                        p_fireevents = true;
                    }
                }
            }
            else
            {
                p_ds.put("default", new MapType2Def.ObjectDS(null, p_type));
            }
        }

        public final String[] getDataSourcesNames()
        {
            Set set = p_ds.keySet();

            return (String[]) set.toArray(new String[set.size()]);
        }

        public final MapType2Def.ObjectDS[] getDataSources()
        {
            String[] names = getDataSourcesNames();
            MapType2Def.ObjectDS[] ds = new MapType2Def.ObjectDS[names.length];

            for (byte i = 0; i < names.length; i++)
            {
                ds[i] = getDataSource(names[i]);
            }

            return ds;
        }

        public final MapType2Def.ObjectDS getDataSource(String name)
        {
            return (ObjectDS) p_ds.get(name);
        }
        
        public final String getClassName()
        {
            return p_classname;            
        }
        
        public final boolean getFireEvents()
        {
            return p_fireevents;            
        }

    }

    public class ObjectDS
    {
        String p_schema;
        String p_name;
        String p_srcobject;
        String p_where;
        String p_dataSource;
        String[] p_keys;
        String[] p_lkeys;
        String[] p_keysView;
        String[] p_icnFields;
        String[] p_keysdatatypes;
        
        boolean p_readOnly;
        
        boolean p_canInsert = true;
        boolean p_canUpdate = true;
        boolean p_publishFinder = false;
        
        String  p_writerClass;
        boolean p_useDefaultWriter;
        
        
        MapType2Def.Mapping p_mapping;
        ngtXMLHandler p_def;
        byte p_type;

        public ObjectDS(ngtXMLHandler node, byte type)
        {
            p_def = node;
            p_type = type; // 0 - Reader , 1 - Writer 
            parse();
        }

        private final void parse()
        {
            if (p_def != null)
            {
                p_name = p_def.getNodeName();

                ngtXMLHandler p_dsprops = p_def.getChildNode("source");

                p_dataSource = p_dsprops.getChildNodeText("dataSource", "");
                p_schema = p_dsprops.getChildNodeText("schema", "");
                p_srcobject = p_dsprops.getChildNodeText("object", "");
                p_where = p_dsprops.getChildNodeText("where", "");

                if( getMapType().equals("1") )
                {
                    p_readOnly = p_dsprops.getChildNodeText("readOnly", "n")
                                          .toLowerCase().startsWith("y");
                }

                ngtXMLHandler[] defkeys = p_dsprops.getChildNode("keys")
                                                   .getChildNodes();
                p_keys          = new String[defkeys.length];
                p_lkeys         = new String[defkeys.length]; 
                p_keysView      = new String[defkeys.length];
                p_keysdatatypes = new String[defkeys.length];

                for (byte i = 0; i < defkeys.length; i++)
                {
                    p_keysdatatypes[i] = defkeys[i].getAttribute("dataType",
                            "CHAR(100)");
                    p_keys[i] = defkeys[i].getText();
                    p_keysView[i] = p_srcobject == null ? "\"" + defkeys[i].getText() + "\"" : p_srcobject + "." + "\"" + defkeys[i].getText() + "\"";
                }

                //                p_keys       = p_dsprops.getChildNodeText("keys","").split(",");

                p_canInsert = p_def.getAttribute("canInsert", "n")
                                      .toLowerCase().startsWith("y");
                p_canUpdate = p_def.getAttribute("canUpdate", "n")
                                      .toLowerCase().startsWith("y");
                                      
                p_publishFinder = p_def.getAttribute("publishFinder", "n")
                                      .toLowerCase().startsWith("y");
                                      
                p_useDefaultWriter = p_def.getAttribute("useDefaultWriter", "y")
                                      .toLowerCase().startsWith("y");
                                      
                p_writerClass      = p_def.getAttribute("writerClass",null);
                                      
                p_mapping = new MapType2Def.Mapping(p_srcobject, p_def.getChildNode("mappings"));
                
                for (int i = 0; i < p_keys.length; i++) 
                {
                    for (int z = 0; z < p_mapping.p_remoteAttributes.length; z++) 
                    {
                        if( p_keys[i].equalsIgnoreCase( p_mapping.p_remoteAttributes[z] ) )
                        {
                            p_lkeys[i] = p_mapping.p_localAttributes[z].toUpperCase();
                        }
                    }
                }
                
            }
            else
            {
                p_name = "default";
                p_dataSource = getDefaultDataSource();
                p_schema = getDefaultSchema();

                if (p_type == 1)
                {
                    p_srcobject = p_bodef.getBoPhisicalMasterTable();
                }
                else
                {
                    p_srcobject = p_bodef.getBoMasterTable();
                }

                p_where = "";
                p_keys = new String[] { "BOUI" };
                p_keysView = new String[] { "BOUI" };
                p_keysdatatypes = new String[] { "NUMBER" };
                p_icnFields = new String[] { "SYS_ICN" };
                p_mapping = new MapType2Def.Mapping(p_srcobject);
            }
        }

        public final String getName()
        {
            return p_name;
        }

        public final String getDataSource()
        {
            return (p_dataSource.length() == 0) ? "DATA" : p_dataSource;
        }

        public final String getSchema()
        {
            return p_schema;
        }

        public final String getXMLObjName()
        {
            return p_bodef.getName();
        }

        public final String getSourceObject()
        {
            return p_srcobject;
        }

        public final String getWhereClause()
        {
            return p_where;
        }

        public final String[] getICNFields()
        {
            return p_icnFields;
        }

        public final String[] getKeys()
        {
            return p_keys;
        }
        public final String[] getLocalKeys()
        {
            return p_lkeys;
        }

        public final String[] getKeysView()
        {
            return p_keysView;
        }

        public final String[] getKeysDataTypes()
        {
            return p_keysdatatypes;
        }

        public final String[] getSQLExpressions()
        {
            return p_mapping.p_sqlexpressions;    
        }
        
        public final String[] getLocalAttributes()
        {
            return p_mapping.p_localAttributes;
        }

        public final String[] getRemoteAttributes()
        {
            return p_mapping.p_remoteAttributes;
        }

        public final boolean isReadOnly()
        {
            return p_readOnly;
        }
        
        public final boolean canUpdate()
        {
            return p_canUpdate;
        }
        
        public final boolean publishFinder()
        {
            return p_publishFinder;
        }
        
        public final boolean canInsert()
        {
            return p_canInsert;
        }
        
        public final String  getWriterClass()
        {
            return p_writerClass; 
        }
        
        public final boolean useDefaulfWriter()
        {
            return p_useDefaultWriter;
        }
        
        
        
        public final String[][] getObjectRelationLocalKeys()
        {
            return p_mapping.p_attlockeys;
        }
        
        public final String[][] getObjectRelationLocalKeysLiteral()
        {
            return p_mapping.p_attlocliteral;
        }
        
        public final String[]   getBridgeTableName()
        {
            return p_mapping.p_bridgeTableName;
        }
        
        public final String[][]   getBridgeLocalFields()
        {
            return p_mapping.p_bridgeLocalFields;
        }
        
        public final String[][]   getBridgeRemoteFields()
        {
            return p_mapping.p_bridgeRemoteFields;
        }
        

        public final String[][] getObjectRelationRemoteKeys()
        {
            return p_mapping.p_attremkeys;
        }
        
        public final String[]   getObjectRelationExtraQuery()
        {
            return p_mapping.p_extraQuery;
        }
        
        public final String[]   getObjectRelationOrderBy()
        {
            return p_mapping.p_orderBy;
        }
        
    }

    private class Mapping
    {
        String[]    p_localAttributes;
        String[]    p_sqlexpressions;
        String[]    p_remoteAttributes;
        
        String[]    p_extraQuery;
        String[]    p_orderBy;

        String[][]  p_attlockeys;
        String[][]  p_attremkeys;
        
        String[][]  p_attlocliteral;

        String[][]  p_bridgeLocalFields;
        String[][]  p_bridgeRemoteFields;

        String[]    p_bridgeTableName;

        String p_srcobject;

        public Mapping(String p_srcobject)
        {
            boDefAttribute[] atts = p_bodef.getBoAttributes();
            int i = 0;

            this.p_srcobject = p_srcobject;

            Vector flds = new Vector();
            flds.add("SYS_ICN");
            flds.add("SYS_USER");
            if(p_bodef.getBoMarkInputType())
            {
                flds.add("SYS_FLDINPUTTYPE");
            }
            
            // Se o objecto for orfão então cria adiciona o atributo LIN
            // porque a bridge é feita directamente na tabela do objecto orfão
//            if( !p_bodef.getBoCanBeOrphan() )
//            {
//                flds.add("LIN");
//            }
            

            //            String objectName = atts[i].getBoDefHandler().getName();
            if (atts == null)
            {
                boolean breaka = true;
            }

            for (; i < atts.length; i++)
            {
                String attribute = atts[i].getDbName();

                if (atts[i].getDbIsBinding())
                {
                    if (!(atts[i].getDbIsTabled() ||
                            ((atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                            (atts[i].getMaxOccurs() > 1))))
                    {
                        if (((atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                (atts[i].getReferencedObjectDef() != null)) ||
                                (atts[i].getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE))
                        {
                            flds.add(atts[i].getDbName());
                        }
                    }
                }
            }

            if (p_bodef.getBoClsState() != null)
            {
                boDefClsState[] satt = p_bodef.getBoClsState()
                                              .getChildStateAttributes();

                for (i = 0; i < satt.length; i++)
                {
                    if (flds.indexOf(satt[i].getDbName()) == -1)
                    {
                        flds.add(satt[i].getDbName());
                    }
                }
            }

            p_localAttributes  = (String[]) flds.toArray(new String[flds.size()]);
            p_remoteAttributes = p_localAttributes;
            p_sqlexpressions   = p_localAttributes;
        }

        public Mapping(String p_srcobject, ngtXMLHandler node)
        {
            this.p_srcobject = p_srcobject;
            if (node != null && node.getNode() != null)
            {
            
                if( "1".equals(getMapType()) )
                {
                    try
                    {
                        XMLNode maps = (XMLNode) node.getNode();
                        NodeList nl = maps.selectNodes("map");
    
                        p_localAttributes = new String[nl.getLength()];
                        p_remoteAttributes = new String[nl.getLength()];
                        p_sqlexpressions = new String[nl.getLength()];
                        String aux;
    
                        for (short i = 0; i < p_localAttributes.length; i++)
                        {
                            XMLNode cnode = (XMLNode) nl.item(i);
                            p_localAttributes[i] = ((XMLNode) cnode.selectSingleNode(
                                    "attribute")).getText();
                            if(cnode.selectSingleNode("expression") != null)
                            {
                                aux = ((XMLNode) cnode.selectSingleNode(
                                        "expression")).getText();
    
                                p_remoteAttributes[i]   = aux;
                                p_sqlexpressions[i]     = aux;
                                //p_srcobject == null ? "\"" + aux + "\"" : p_srcobject + "." + "\"" + aux + "\"";
                                
                            }
                            else
                            {
                                    p_sqlexpressions[i]     = ((XMLNode) cnode.selectSingleNode("sql")).getText();     
                                    p_remoteAttributes[i]   = p_localAttributes[i];
                            }
                            
                        }
                    }
                    catch (XSLException e)
                    {
                    }
                    
                }
                else if ( "2".equals(getMapType()) )
                {
                    try
                    {
                        XMLNode maps = (XMLNode) node.getNode();
                        NodeList nl = maps.selectNodes("map");
    
                        p_localAttributes  = new String[nl.getLength()];
                        p_remoteAttributes = new String[nl.getLength()];
                        p_bridgeTableName  = new String[nl.getLength()];

                        p_sqlexpressions = new String[nl.getLength()];
                        p_attlockeys     = new String[nl.getLength()][];
                        p_attremkeys     = new String[nl.getLength()][];
                        p_attlocliteral  = new String[nl.getLength()][];
                        
                        p_bridgeLocalFields     = new String[nl.getLength()][];
                        p_bridgeRemoteFields    = new String[nl.getLength()][];
    
                        p_orderBy     = new String[nl.getLength()];
                        p_extraQuery  = new String[nl.getLength()];
                        
                        String aux;
                        for (short i = 0; i < p_localAttributes.length; i++)
                        {
                            XMLNode cnode = (XMLNode) nl.item(i);
                            p_localAttributes[i] = ((XMLNode) cnode.selectSingleNode(
                                    "attribute")).getText();
                            
                            boDefAttribute defAtt = p_bodef.getAttributeRef(  p_localAttributes[ i ] );
                            if( defAtt != null )
                            {
                                if( 
                                    defAtt.getAtributeType()== boDefAttribute.TYPE_ATTRIBUTE  
                                    ||
                                    defAtt.getAtributeType()== boDefAttribute.TYPE_STATEATTRIBUTE  
                                )
                                {
                                    if( cnode.selectSingleNode("expression") != null )
                                    {
                                        aux = ((XMLNode) cnode.selectSingleNode(
                                                "expression")).getText().toUpperCase();
            
                                        p_remoteAttributes[i]   = aux;
                                        if( "1".equals(getMapType()) )
                                        {
                                            p_sqlexpressions[i]     = aux;
                                        }
                                    }
                                    else
                                    {
                                        p_sqlexpressions[i]     = ((XMLNode) cnode.selectSingleNode("sql")).getText();     
                                        p_remoteAttributes[i]   = p_localAttributes[i];
                                    }
                                }
                                else
                                {
                                    XMLNode   kkeys = (XMLNode)((XMLElement)cnode).selectSingleNode( "keys" );
    
                                    XMLNode nodeOrderBy    = (XMLNode)cnode.selectSingleNode( "orderBy" );
                                    XMLNode nodeExtraQuery = (XMLNode)cnode.selectSingleNode( "extraQuery" );
                                    if( nodeOrderBy != null )
                                    {
                                        p_orderBy[ i ] = nodeOrderBy.getText();
                                    }
                                    if( nodeExtraQuery != null )
                                    {
                                        p_extraQuery[ i ] = nodeExtraQuery.getText();
                                    }
    
                                    NodeList  kkeyn  = kkeys.selectNodes( "key" );
                                    
                                    String[]  remObjKeys = new String[ kkeyn.getLength() ];
                                    String[]  locObjKeys = new String[ kkeyn.getLength() ];
                                    String[]  locObjKeysLit = new String[ kkeyn.getLength() ];
                                    
                                    for (int x=0;x < kkeyn.getLength(); x++ )
                                    {
                                        XMLNode knode = (XMLNode)kkeyn.item( x );
                                        
                                        XMLNode auxNode     = (XMLNode)knode.selectSingleNode("localField");
                                        if( auxNode == null )
                                        {
                                            auxNode         = (XMLNode)knode.selectSingleNode("localLiteral");
                                            if( auxNode != null )
                                            {
                                                locObjKeysLit[x]   = auxNode.getText();
                                            }
                                        }
                                        else
                                        {
                                            String locField     = auxNode.getText();
                                            locObjKeys[x] = locField;  
                                        }
                                        String remField   = ((XMLNode)knode.selectSingleNode("remoteAttribute")).getText();
                                        remObjKeys[x] = remField;
                                    }
                                    p_attlocliteral[i]      = locObjKeysLit;
                                    p_attlockeys[i]         = locObjKeys;
                                    p_attremkeys[i]         = remObjKeys;
                                    
                                    
                                    if ( defAtt.getRelationType() == defAtt.RELATION_1_TO_N_WBRIDGE )
                                    {
                                        XMLNode auxnode      = (XMLNode)cnode.selectSingleNode("bridgeTable");
                                        if( auxnode != null )
                                        {
                                            p_bridgeTableName[i] = auxnode.getText();
                                        }
                                        
                                        auxnode      = (XMLNode)cnode.selectSingleNode("bridgeKeys");
                                        if( auxnode != null )
                                        { 
                                            NodeList bmaps = auxnode.selectNodes("key");

                                            String[]  bridgeLocKeys = new String[ bmaps.getLength() ];
                                            String[]  bridgeRemKeys = new String[ bmaps.getLength() ];

                                            for (int z = 0; z < bmaps.getLength(); z++) 
                                            {
                                            
                                                XMLElement elem = (XMLElement)bmaps.item( z );
                                                 
                                                XMLNode auxNode = null;
                                                
                                                auxNode     = (XMLNode)elem.selectSingleNode("bridgeLocalField");
                                                if( auxNode != null )
                                                {
                                                    bridgeLocKeys[z]= auxNode.getText();
                                                }
        
                                                auxNode     = (XMLNode)elem.selectSingleNode("bridgeRemoteField");
                                                if( auxNode != null )
                                                {
                                                    bridgeRemKeys[z]= auxNode.getText();
                                                }
                                                
                                            }
                                            p_bridgeLocalFields[i]  = bridgeLocKeys;
                                            p_bridgeRemoteFields[i] = bridgeRemKeys;
                                        }
                                    }
                                }
                            }
                            else
                            {   
                                p_remoteAttributes[i] = p_localAttributes[i];
//                                logger.warn( "O Atributo ["+p_localAttributes[i]+"] não existe no Object ["+p_bodef.getName()+"] ");
                            }
                        }
                    }
                    catch (XSLException e)
                    {
                    }
                }
            }
            else
            {
                this.p_localAttributes = new String[0];
                this.p_remoteAttributes= new String[0];
                this.p_sqlexpressions  = new String[0];
            }
        }
    }
    
    public static final int DETECT_CHANGE_BY_TRIGGER  = 0;
    public static final int DETECT_CHANGE_BY_CHECKSUM = 1;
    
    public static class ChangeDetectionHandler
    {
        XMLNode p_node;
        
        
        
        public ChangeDetectionHandler( XMLNode node )
        {
            p_node = node;
        }
        
        public int getDetectChangeType()
        {
            String nodeName = p_node.getNodeName();
            
            if( nodeName.equals("trigger") )
            {
                return DETECT_CHANGE_BY_TRIGGER;    
            }
            if( nodeName.equals("trigger") )
            {
                return DETECT_CHANGE_BY_CHECKSUM;    
            }
            
            return -1;
        }
        private String getFieldPrefix(  )
        {
            int type = getDetectChangeType();
            return type==DETECT_CHANGE_BY_CHECKSUM?"SYS_MPDC_CHK_":"SYS_MPDC_TRG";
        }
        
        public String getTableSchema( String tablename )
        {
            XMLNode xnode = findTable( tablename );
            return xnode.getAttributes().getNamedItem("schema").getNodeValue();
        }
        
        public String[] getTableKeys( String tablename )
        {
            return getNodeListAsStringArray( findTable( tablename ), "keys" );
        }

        public String[] getTableFields( String tablename )
        {
            return getNodeListAsStringArray( findTable( tablename ), "fields" );
        }
        
        public String[] getTableGroups( String tablename )
        {
            return getNodeListAsStringArray( findTable( tablename ), "groups" );
        }
        
        public String getFieldName( String tableName )
        {
            return boBuildDB.encodeObjectName(getFieldPrefix() + "_" + tableName.toUpperCase());
        }
        
        public String getTriggerName( String tableName )
        {
            String schema = getTableSchema( tableName );
            if( schema != null && schema.length() > 0 )
            {
                tableName = schema + "_" + tableName;
            }
            return  netgest.bo.builder.boBuildDB.encodeObjectName("TGRMPCD_"+tableName.toUpperCase());
        }
        
        public String getFullTableName( String tableName )
        {
            String schema = getTableSchema( tableName );
            if( schema != null && schema.length() > 0 )
            {
                tableName = schema + "." + tableName;
            }
            return tableName.toUpperCase();
        }
        
        private String[] getNodeListAsStringArray( XMLNode node, String nodeName )
        {
            try
            {
                node = (XMLNode)node.selectSingleNode( nodeName ).getFirstChild();
                ArrayList list = new ArrayList();
                while( node != null )
                {
                    list.add( node.getText() );
                    node = (XMLNode)node.getNextSibling();
                }
                return (String[])list.toArray(new String[ list.size() ]);
            }
            catch (XSLException e)
            {
                throw new boRuntimeException2(e);
            }
        }
        
        public String[] getTables()
        {
            try
            {
                ArrayList list = new ArrayList();
                Node node = p_node.selectSingleNode("tables").getFirstChild();
                while( node != null )
                {
                    list.add( node.getAttributes().getNamedItem("name").getNodeValue() );
                    node = node.getNextSibling();
                }
                return (String[])list.toArray(new String[ list.size() ]);
            }
            catch (XSLException e)
            {
                throw new boRuntimeException2(e);
            }
        }
        
        private XMLNode findTable( String tablename )
        {
            try
            {
                XMLNode node = (XMLNode)p_node.selectSingleNode("tables").getFirstChild();
                while( node != null && !node.getAttributes().getNamedItem("name").getNodeValue().equals(tablename) )
                {
                    node = (XMLNode)node.getNextSibling();
                }
                return node;
                
            }
            catch (XSLException e)
            {
                throw new boRuntimeException2(e);
            }
        }
    }
    
    
//    public static class Finder
//    {
//        private String      p_id;
//        private String[]    p_atts;
//        public Finder( String id, String[] attributes  )
//        {
//            p_id    = id;
//            p_atts  = attributes;
//        }
//        public String getId()
//        {
//            return p_id;
//        }
//        public String[] getAttributes()
//        {
//            return p_atts;
//        }
//    }
    
}
