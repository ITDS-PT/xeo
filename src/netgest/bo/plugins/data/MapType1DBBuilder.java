/*Enconding=UTF-8*/
package netgest.bo.plugins.data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import netgest.bo.boConfig;
import netgest.bo.builder.boBuildDB;
import netgest.bo.builder.boBuildRepository;
import netgest.bo.builder.boBuilder;
import netgest.bo.def.boDefHandler;
import netgest.bo.plugins.IDataBuilderDB;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException2;
import netgest.utils.DataUtils;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;

import org.w3c.dom.Element;

public class MapType1DBBuilder implements IDataBuilderDB
{
    private boBuildRepository       p_repository;
    private boBuildDB               p_dbb;
    private int                     p_mode;
    private EboContext              p_eboctx;
    private Hashtable               p_objectInterfaceMap;
    private boolean                 p_createdFwdMethods;

    private void buildExternalTables( boDefHandler def )
    {
        MapType1Def allds = netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( def );
        MapType1Def.ObjectDataSource ds = allds.getObjectDataSources();
        
        String escapeInit = p_eboctx.getDataBaseDriver().getEscapeCharStart();
        String escapeEnd  = p_eboctx.getDataBaseDriver().getEscapeCharStart();
        
        Object[] props = getObjectTableAndLoadExternalAttributes(def);
        String botable = (String) props[0];

        if ((ds != null) && allds.haveLocalTable())
        {
            if (((Boolean) props[2]).booleanValue())
            {
                p_dbb.addTable(botable, def.getDescription());

                if (p_mode == boBuildDB.BUILD_ATTRIBUTES)
                {
                    p_dbb.addPrimaryKey("PK_" + botable,
                        "Primary key of " + def.getDescription(), botable,
                        "BOUI");
                }

                StringBuffer xvwhere = new StringBuffer();
                StringBuffer xvfields = new StringBuffer();
                StringBuffer xtables = new StringBuffer();

                Enumeration oEnum = ((Hashtable) props[1]).elements();

                while (oEnum.hasMoreElements())
                {
                    Vector cds = (Vector) oEnum.nextElement();

                    for (int i = 0; i < cds.size(); i++)
                    {
                        if (xvfields.length() > 0)
                        {
                            xvfields.append(',');
                        }

                        MapType1Def.ObjectDS cods = (MapType1Def.ObjectDS) cds.get(i);

                        String[] xra = cods.getSQLExpressions();
                        String[] xla = cods.getLocalAttributes();

                        for (int z = 0; z < xla.length; z++)
                        {
                            xvfields.append( cods.getSourceObject() );
                            xvfields.append( "." );
                            xvfields.append(xra[z])
                                    .append(' ').append(xla[z]);

                            xvfields.append(',');
                        }

                        String[] fkeys = cods.getKeys();
                        String[] fkeystypes = cods.getKeysDataTypes();

                        for (int z = 0; z < fkeys.length; z++)
                        {
                            if( z > 0 )
                            {
                                xvfields.append(',');
                            }

                            xvfields.append( fkeys[z] )
                                    .append(' ').append( fkeys[z] + "$R" );

                            xvfields.append( ',' );

                            xvfields.append( fkeys[z] )
                                    .append(' ').append( fkeys[z] + "$L" );

                        }


                        if (xtables.length() > 0)
                        {
                            xtables.append(',');
                        }

                        if (cods.getSchema().length() > 0)
                        {
                            xtables.append(cods.getSchema()).append('.');
                        }

                        xtables.append(cods.getSourceObject());


                        if (xvwhere.length() > 0)
                        {
                            xvwhere.append(' ').append(" AND ");
                        }

                        xvwhere.append('(');

                        StringBuffer indexkeys = new StringBuffer();

                        for (byte z = 0; z < fkeys.length; z++)
                        {
                            p_dbb.addField(botable, fkeys[z] + "$L", fkeystypes[z],
                                "Related table foreign key", true, "", "", "");



                            if (indexkeys.length() > 0)
                            {
                                indexkeys.append(',');
                            }

                            indexkeys.append(fkeys[z]).append("$L");

                            if (z > 0)
                            {
                                xvwhere.append(" AND ");
                            }

                            xvwhere.append(cods.getSourceObject()).append('.')
                                   .append(fkeys[z]).append('=').append(botable)
                                   .append('.').append(fkeys[z]).append("$L");
                        }

                        xvwhere.append(')');

                        if (this.p_mode == boBuildDB.BUILD_CONSTRAINTS)
                        {
                            p_dbb.addUnique("UN_" + def.getName() + "_" + cods.getSourceObject() + "_KEYS",
                                "Index for external keys of the object " +
                                def.getName(), botable, indexkeys.toString());
                        }
                    }
                }

                StringBuffer localfields = new StringBuffer();
                localfields.append(botable).append('.').append( escapeInit )
                           .append("SYS_USER").append(escapeEnd).append(',')
                           .append(botable).append('.').append(escapeInit)
                           .append("SYS_ICN").append(escapeEnd);
                if(def.getBoMarkInputType())
                {
                    localfields.append(',').append(botable)
                               .append('.').append(escapeInit)
                               .append("SYS_FLDINPUTTYPE")
                               .append(escapeEnd);
                }

                Enumeration enumlf = p_dbb.getLocalAttributes().elements();

                while (enumlf.hasMoreElements())
                {

                    String fldname = (String) enumlf.nextElement();

                    if (fldname.equalsIgnoreCase("CLASSNAME"))
                    {
                        localfields.append(',').append('\'')
                                   .append(def.getBoName()).append("' AS CLASSNAME");
                    }
                    else
                    {
                        localfields.append(',').append(botable).append('.')
                                   .append(escapeInit).append(fldname).append(escapeEnd);
                    }
                }

                String viewexpr = "SELECT " + localfields.toString() + ", " +
                    xvfields.toString();
                viewexpr += (" FROM " + botable + "," + xtables.toString() +
                " WHERE " + xvwhere);

                String viewname = def.getBoPhisicalMasterTable();

                if (this.p_mode == boBuildDB.BUILD_ATTRIBUTES)
                {
                    p_dbb.addView(viewname,
                        "View to merge object " + def.getDescription(), viewexpr);
                }

                xvwhere.length();
            }
        }
        else if ( ds != null && !allds.isDefault() )
        {
            MapType1Def.ObjectDS[] xtds = ds.getDataSources();
            for (int i = 0; i < xtds.length; i++)
            {
                String[] keys = xtds[i].getKeys();
                String[] keyst = xtds[i].getKeysDataTypes();

                String[] keysl = new String[ keys.length ];
                for (int k = 0; k < keys.length ; k++)
                {
                    keysl[k] = keys[k]+"$L";
                    if (this.p_mode == boBuildDB.BUILD_ATTRIBUTES)
                    {
                        p_dbb.addField(botable, keys[k] + "$L", keyst[k],
                            "Related table foreign key", true, "", "", "");
                    }
                }
                if (this.p_mode == boBuildDB.BUILD_CONSTRAINTS)
                {
                    p_dbb.addUnique("UN_" + def.getName() + "_" + xtds[i].getSourceObject() + "_KEYS",
                        "Index for external keys of the object " +
                        def.getName(), botable, DataUtils.concatFields( p_eboctx.getDataBaseDriver(), keysl ) );
                }
            }

        }

        if( allds.getChecksums() != null )
        {
            MapType1Def.ChangeDetectionHandler cdh = allds.getChecksums();
            String[] montables = cdh.getTables();

            for (int i = 0; i < montables.length; i++)
            {
                p_dbb.addField(botable, cdh.getFieldName( montables[i] ) , "N",
                    "Related table foreign key", true, "", "", "");
            }
        }
        if( allds.getTriggers() != null )
        {
            p_dbb.addField(botable, "SYS_MPDC_TRG_TIMESTAMP" , "datetime",
                "Related table foreign key", true, "", "", "");
            p_dbb.addField(botable, "SYS_MPDC_LASTSYNC" , "datetime",
                "Related table foreign key", true, "", "", "");
        }
    }


    public final void afterDataBaseScripts( boDefHandler def )
    {
        deployMappingTriggers( def );
    }


    private final void deployMappingTriggers( boDefHandler def )
    {
        if( p_mode == boBuildDB.BUILD_CONSTRAINTS )
        {
            MapType1Def.ChangeDetectionHandler cd =  netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( def ).getTriggers();
            if( cd != null )
            {
                String templatesdir = p_eboctx.getApplication().getApplicationConfig().getTemplatesDir();

                try
                {
                    FileReader fr = new FileReader( templatesdir + "mappingtrigger.txt" );
                    StringBuffer sb = new StringBuffer();
                    char[] buff = new char[4096];
                    int br = 0;
                    while( (br=fr.read(buff)) > 0)
                    {
                        sb.append( buff,0,br );
                    }
                    String[] tables = cd.getTables();
                    for (int i = 0; i < tables.length; i++)
                    {
                        String trigger = sb.toString();
                        trigger = trigger.replaceAll("#TRIGGERNAME#", cd.getTriggerName( tables[i] ).replaceAll("\\$","\\\\\\$") );
                        trigger = trigger.replaceAll("#REMOTETABLE#", cd.getFullTableName( tables[i] ).replaceAll("\\$","\\\\\\$") );

                        trigger = trigger.replaceAll("#LOCALTABLE#", p_dbb.getTableName().replaceAll("\\$","\\\\\\$") );

                        if( cd.getTableFields( tables[i] ).length > 0 )
                        {
                            String[] fields = cd.getTableFields( tables[i] );
                            StringBuffer str = new StringBuffer ("OF ");
                            int z=0;
                            for ( ; z < fields.length - 1 ; z++)
                            {
                                str.append( fields[z] ).append(',');
                            }
                            str.append( fields[z] );


                            trigger = trigger.replaceAll("#OF_FIELDS#", str.toString().replaceAll("\\$","\\\\\\$") );

                        }
                        else
                        {
                            trigger = trigger.replaceAll("#OF_FIELDS#", "" );
                        }
                        String[] keys = cd.getTableKeys( tables[i] );
                        StringBuffer sbwhere = new StringBuffer();
                        for (int z = 0; z < keys.length; z++)
                        {
                            if( z < keys.length - 1 )
                            {
                                sbwhere.append(" AND ");
                            }
                            sbwhere.append( p_dbb.getTableName() ).append('.').append( MapType1Def.getDataSourceDefinition( def ).getObjectDataSources().getDataSources()[0].getKeys()[z] )
                            .append("$L")
                            .append(" = :NEW.").append( keys[z] );
                        }

                        String xx = sbwhere.toString().replaceAll("\\$","\\\\\\$");

                        trigger = trigger.replaceAll( "#WHERE#" , xx );

                        //** Tenta fazer o deploy do trigger, TODO:Passar para o OracleDBM
                        String depdir = p_eboctx.getApplication().getApplicationConfig().getDeploymentDir();
                        FileWriter fw = new FileWriter( depdir + cd.getTriggerName( tables[i] ) +".SQL" );
                        fw.write( trigger.toCharArray() );
                        fw.close();

                        Connection cn;

                        try
                        {
                            cn = p_repository.getRepository().getDedicatedConnection();

                            CallableStatement cstm = cn.prepareCall( trigger );
                            cstm.execute();
                        }
                        catch (SQLException e)
                        {
                            p_dbb.logger.warn("Error deploying MappingTrigger on ["+cd.getFullTableName( tables[i] )+"]");
                        }

                    }
                }
                catch (FileNotFoundException e)
                {
                    throw new boRuntimeException2("Template of triggers not found, please check this file ["+templatesdir + "mappingtrigger.txt"+"].");
                }
                catch (IOException e)
                {
                    throw new boRuntimeException2( e );
                }
            }
        }

    }

    private void buildInheritDataSources( boDefHandler bodef, boDefHandler[] allbo, boolean createdFwdMethods)
    {
        // Adds super fields;
        for (short i = 0; i < allbo.length; i++)
        {
            if (!bodef.getBoName().equals(allbo[i].getBoName()))
            {
                if (allbo[i].getBoPhisicalMasterTable().equalsIgnoreCase( p_dbb.getTableName()))
                {
                    deployDataSource(p_dbb, p_repository, boBuilder.getUndeployedDefinitions(
                            p_repository, allbo[i].getBoName(), p_objectInterfaceMap, createdFwdMethods));

                    buildExternalTables( boBuilder.getUndeployedDefinitions(
                            p_repository, allbo[i].getBoName(), p_objectInterfaceMap, createdFwdMethods));

                    deployMappingTriggers( boBuilder.getUndeployedDefinitions(
                            p_repository, allbo[i].getBoName(), p_objectInterfaceMap, createdFwdMethods) );

                    String xsuper = allbo[i].getBoSuperBo();

                    while (xsuper != null)
                    {
                        boDefHandler defsup = boBuilder.getUndeployedDefinitions(p_repository,
                                xsuper, p_objectInterfaceMap, createdFwdMethods);

                        if (defsup != null)
                        {
                            if (defsup.getClassType() == boDefHandler.TYPE_CLASS)
                            {
                                if (!defsup.getBoPhisicalMasterTable().equals( p_dbb.getTableName() ))
                                {
                                    deployDataSource(p_dbb, p_repository,  boBuilder.getUndeployedDefinitions(
                                            p_repository, xsuper, p_objectInterfaceMap, createdFwdMethods) );

                                    buildExternalTables(boBuilder.getUndeployedDefinitions(
                                            p_repository, xsuper, p_objectInterfaceMap, createdFwdMethods));

                                    deployMappingTriggers( boBuilder.getUndeployedDefinitions(
                                            p_repository, xsuper, p_objectInterfaceMap, createdFwdMethods));

                                }
                            }

                            xsuper = defsup.getBoSuperBo();
                        }
                        else
                        {
                            xsuper = null;
                        }
                    }
                }
            }
        }
    }

    private static void deployDataSource(boBuildDB builddb, boBuildRepository repos, boDefHandler def)
    {

        MapType1Def allds = netgest.bo.plugins.data.MapType1Def.getUndeployedDataSourceDefinition( repos, def );
        MapType1Def.ObjectDataSource oads = allds.getObjectDataSources();

        if ((oads != null) && !allds.isDefault())
        {
            XMLDocument xml = new XMLDocument();
            Element root = xml.createElement("root");
            xml.appendChild(root);

            Element tableNode = null;
            Element writers = null;

            Element eds = null;
            Element auxnode = null;
            Element auxnode2 = null;

            MapType1Def.ObjectDS[] ods = oads.getDataSources();

            for (int flag = 0; flag < 2; flag++)
            {
                if (flag == 0)
                {
                    tableNode = xml.createElement("readers");

                }

                if (flag == 1)
                {
                    tableNode = xml.createElement("writers");
                }
                root.appendChild(tableNode);

                writers = xml.createElement("tables");
                tableNode.appendChild( writers );

                eds = xml.createElement("default");
                auxnode = xml.createElement("source");

                auxnode2 = xml.createElement("dataSource");
                auxnode2.appendChild( xml.createTextNode("DATA") );
                auxnode.appendChild(auxnode2);

                auxnode2 = xml.createElement("schema");
                auxnode2.appendChild(xml.createTextNode(""));
                auxnode.appendChild(auxnode2);

                auxnode2 = xml.createElement("object");

                if (flag == 0)
                {
                    auxnode2.appendChild(xml.createTextNode(
                            def.getBoMasterTable()));
                }
                else if (flag == 1)
                {
                    if( allds.haveLocalTable() )
                    {
                        auxnode2.appendChild(xml.createTextNode(def.getBoPhisicalMasterTable() +
                                "_LOCAL"));
                    }
                    else
                    {
                        auxnode2.appendChild( xml.createTextNode( def.getBoPhisicalMasterTable() ) );
                    }
                }

                auxnode.appendChild(auxnode2);

                auxnode2 = xml.createElement("keys");

                Element keyelem = xml.createElement("key");
                keyelem.setAttribute("dataType", boBuildDB.DBTYPE_BOUI);
                keyelem.appendChild(xml.createTextNode("BOUI"));
                auxnode2.appendChild(keyelem);
                auxnode.appendChild(auxnode2);
                eds.appendChild(auxnode);

                auxnode = xml.createElement("mappings");
                eds.appendChild(auxnode);

                for (int flagatts = flag; flagatts < 2; flagatts++)
                {
                    Enumeration oEnum = null;

                    if (flagatts == 0)
                    {
                        oEnum = builddb.getExternalAttributes().keys();
                    }
                    else
                    {
                        oEnum = builddb.getLocalAttributes().elements();
                    }

                    while (oEnum.hasMoreElements())
                    {
                        String locfld = (String) oEnum.nextElement();
                        String remfld = (flagatts == 0)
                            ? (String) builddb.getExternalAttributes().get(locfld)
                            : (String) builddb.getLocalAttributes().get(locfld);

                        if (flag == 0)
                        {
                            remfld = locfld;
                        }

                        auxnode2 = xml.createElement("map");

                        Element att = xml.createElement("attribute");
                        att.appendChild(xml.createTextNode(locfld));

                        Element exp = xml.createElement("expression");
                        exp.appendChild(xml.createTextNode(remfld));
                        auxnode2.appendChild(att);
                        auxnode2.appendChild(exp);
                        auxnode.appendChild(auxnode2);
                    }
                }

                if (flag == 0)
                {
                    String[] keys = "SYS_USER,SYS_ICN,SYS_FLDINPUTTYPE".split(
                            ",");

                    for (int z = 0; z < keys.length; z++)
                    {
                        if(def.getBoMarkInputType() || z < (keys.length-1))
                        {
                            auxnode2 = xml.createElement("map");

                            Element att = xml.createElement("attribute");
                            att.appendChild(xml.createTextNode(keys[z]));

                            Element exp = xml.createElement("expression");
                            exp.appendChild(xml.createTextNode(keys[z]));
                            auxnode2.appendChild(att);
                            auxnode2.appendChild(exp);
                            auxnode.appendChild(auxnode2);

                            String[] extkeys = ods[0].getKeys();
                        }
                    }
                }

                String[] extkeys = ods[0].getKeys();
//                if (flag == 1)
//                {
//                    String[] xratt = ods[0].getRemoteAttributes();
//                    String[] xrsql = ods[0].getSQLExpressions();
//                    String[] xlatt = ods[0].getLocalAttributes();
////                    String[] intkeys = new String[extkeys.length];
//
////                    for (byte y = 0; y < intkeys.length; y++)
////                    {
////                        for (short k = 0; k < xratt.length; k++)
////                        {
////                            if (xratt[k].equals(extkeys[y]))
////                            {
////                                intkeys[y] = xlatt[k];
////
////                                break;
////                            }
////                        }
////                    }
//
//                }
                if( flag == 0 )
                {
                    for (int z = 0; z < extkeys.length; z++)
                    {
                        auxnode2 = xml.createElement("map");

                        Element att = null;//xml.createElement("attribute");
//                        att.appendChild(xml.createTextNode(extkeys[z] + "$L"));

                        Element exp = null;//xml.createElement("expression");
//                        exp.appendChild(xml.createTextNode(extkeys[z] + "$L"));
//                        auxnode2.appendChild(att);
//                        auxnode2.appendChild(exp);
//                        auxnode.appendChild(auxnode2);

                        if( allds.haveMappingFields() )
                        {
                            auxnode2 = xml.createElement("map");

                            att = xml.createElement("attribute");
                            att.appendChild(xml.createTextNode(extkeys[z] + "$R"));

                            exp = xml.createElement("expression");
                            exp.appendChild(xml.createTextNode(extkeys[z] + "$R"));
                            auxnode2.appendChild(att);
                            auxnode2.appendChild(exp);
                            auxnode.appendChild(auxnode2);
                        }
                        else
                        {
                            auxnode2 = xml.createElement("map");

                            att = xml.createElement("attribute");
                            att.appendChild(xml.createTextNode(extkeys[z] + "$L"));

                            exp = xml.createElement("expression");
                            exp.appendChild(xml.createTextNode(extkeys[z] + "$L"));
                            auxnode2.appendChild(att);
                            auxnode2.appendChild(exp);
                            auxnode.appendChild(auxnode2);
                        }
                    }
                }
                else if ( flag == 1 )
                {
                    if( allds.haveMappingFields() )
                    {
                        for (int z = 0; z < extkeys.length; z++)
                        {
                            auxnode2 = xml.createElement("map");

                            Element att = xml.createElement("attribute");
                            att.appendChild(xml.createTextNode(extkeys[z] + "$R"));

                            Element exp = xml.createElement("expression");
                            exp.appendChild(xml.createTextNode(extkeys[z] + "$L"));
                            auxnode2.appendChild(att);
                            auxnode2.appendChild(exp);
                            auxnode.appendChild(auxnode2);
                        }
                    }
                    else
                    {
                        for (int z = 0; z < extkeys.length; z++)
                        {
                            auxnode2 = xml.createElement("map");

                            Element att = xml.createElement("attribute");
                            att.appendChild(xml.createTextNode(extkeys[z] + "$L"));

                            Element exp = xml.createElement("expression");
                            exp.appendChild(xml.createTextNode(extkeys[z] + "$L"));
                            auxnode2.appendChild(att);
                            auxnode2.appendChild(exp);
                            auxnode.appendChild(auxnode2);
                        }
                    }
                }
                writers.appendChild(eds);
            }

            for (int i = 0; i < ods.length; i++)
            {
                eds = xml.createElement(ods[i].getName());
                auxnode = xml.createElement("source");
                eds.appendChild(auxnode);

                auxnode2 = xml.createElement("dataSource");
                auxnode2.appendChild(xml.createTextNode(ods[i].getDataSource()));
                auxnode.appendChild(auxnode2);

                auxnode2 = xml.createElement("schema");
                auxnode2.appendChild(xml.createTextNode(ods[i].getSchema()));
                auxnode.appendChild(auxnode2);

                auxnode2 = xml.createElement("object");
                auxnode2.appendChild(xml.createTextNode(
                        ods[i].getSourceObject()));
                auxnode.appendChild(auxnode2);

                auxnode2 = xml.createElement("readOnly");
                auxnode2.appendChild(xml.createTextNode("Yes"));
                auxnode.appendChild(auxnode2);

                auxnode2 = xml.createElement("keys");

                String[] keys = ods[i].getKeys();
                String[] keystypes = ods[i].getKeysDataTypes();

                for (byte z = 0; z < keys.length; z++)
                {
                    Element knode = xml.createElement("key");
                    knode.setAttribute("dataType", keystypes[z]);
                    knode.appendChild(xml.createTextNode(keys[z]));
                    auxnode2.appendChild(knode);
                }

                auxnode.appendChild(auxnode2);
                writers.appendChild(eds);

                String[] latt = ods[i].getLocalAttributes();
                String[] ratt = ods[i].getRemoteAttributes();

                auxnode = xml.createElement("mappings");
                eds.appendChild(auxnode);

                for (short z = 0; z < latt.length; z++)
                {
                    auxnode2 = xml.createElement("map");

                    Element att = xml.createElement("attribute");
                    att.appendChild(xml.createTextNode(latt[z]));

                    Element exp = xml.createElement("expression");
                    exp.appendChild(xml.createTextNode(ratt[z]));
                    auxnode2.appendChild(att);
                    auxnode2.appendChild(exp);
                    auxnode.appendChild(auxnode2);
                }
            }

            File xfile = repos.getDataSourceFile(def.getName());

            XMLDocument sdoc = ngtXMLUtils.loadXMLFile(xfile.getAbsolutePath());

            sdoc.getDocumentElement().appendChild(sdoc.importNode(
                    root.getFirstChild(), true));
            sdoc.getDocumentElement().appendChild(sdoc.importNode(
                    root.getFirstChild().getNextSibling(), true));

            ngtXMLUtils.saveXML(sdoc,
                boConfig.getDeploymentDir() + def.getName() + boBuilder.TYPE_DS);
        }
    }

    private Object[] getObjectTableAndLoadExternalAttributes(boDefHandler def)
    {
        Hashtable xods = null;
        if( p_dbb == null )
        {
            String x = "To Stop";
        }


        p_dbb.getExternalAttributes().clear();

        MapType1Def allds = netgest.bo.plugins.data.MapType1Def.getUndeployedDataSourceDefinition(p_repository, def);
        MapType1Def.ObjectDataSource ds = allds.getObjectDataSources();

        if ((ds != null) && allds.haveMappingFields())
        {
            MapType1Def.ObjectDS[] ods = ds.getDataSources();
            Vector vds = new Vector();

            for (byte i = 0; (ods != null) && (i < ods.length); i++)
            {
                vds.add(ods[i]);

                String[] latts = ods[i].getLocalAttributes();
                String[] lextatt = ods[i].getRemoteAttributes();

                for (short z = 0; z < latts.length; z++)
                {
                    p_dbb.getExternalAttributes().put(latts[z].toUpperCase(),
                        lextatt[z].toUpperCase());
                    p_dbb.getLocalAttributes().remove( latts[z].toUpperCase() );
                }
            }

            xods = new Hashtable();

            for (byte i = 0; i < vds.size(); i++)
            {
                // Check if can a view can be created
                MapType1Def.ObjectDS xds = (MapType1Def.ObjectDS) vds.get(i);
                String dsname = xds.getDataSource();
                Vector odssrc;

                if ((odssrc = (Vector) xods.get(dsname)) == null)
                {
                    xods.put(dsname, odssrc = new Vector());
                    odssrc.add(xds);
                }

                for (int z = (i + 1); z < vds.size(); z++)
                {
                    if (((MapType1Def.ObjectDS) vds.get(z)).getDataSource()
                             .equals(dsname))
                    {
                        odssrc.add(xds);
                    }
                }
            }
        }

        String botable = def.getBoPhisicalMasterTable();

        if (allds.haveLocalTable() )
        {
            botable += "_LOCAL";
        }
        return new Object[] { botable, xods, new Boolean( allds.haveLocalTable() ) };
    }




    public void initialize(EboContext ctx, boBuildDB dbbuilder, boBuildRepository repository,
                            int mode,
                            Hashtable objectInterfaceMap,
                            boolean createdFwdMethods
                        )
    {
        p_mode                  = mode;
        p_dbb                   = dbbuilder;
        p_repository            = repository;
        p_eboctx                = ctx;
        p_objectInterfaceMap    = objectInterfaceMap;
        p_createdFwdMethods     = createdFwdMethods;
        netgest.bo.plugins.data.MapType1Def.clearCache();
    }

    public void afterDataBaseScript(boDefHandler boDef)
    {
        MapType1Def def = netgest.bo.plugins.data.MapType1Def.getUndeployedDataSourceDefinition( p_repository, boDef );
        if( def != null && "1".equals( def.getMapType() ) )
        {
            deployMappingTriggers( boDef );
        }
    }


    public String getPhisicalTableName(boDefHandler boDef)
    {
        MapType1Def def = netgest.bo.plugins.data.MapType1Def.getUndeployedDataSourceDefinition( p_repository, boDef );
        if( def != null && "1".equals( def.getMapType() ) )
        {
            Object[] props = getObjectTableAndLoadExternalAttributes( boDef );
            return (String) props[0];
        }
        return null;
    }

    public void beforeDataBaseScripts( boDefHandler boDef)
    {
        MapType1Def def = netgest.bo.plugins.data.MapType1Def.getUndeployedDataSourceDefinition( p_repository, boDef );
        if( p_mode == boBuildDB.BUILD_ATTRIBUTES )
        {
            if( def != null && "1".equals( def.getMapType() ) )
            {
                // Read the deployed Object Definitions
                boDef = boDefHandler.getBoDefinition( boDef.getName() );

                deployDataSource(p_dbb, p_repository,  boDef );

                // TODO:beforeDabaseScripts
                buildExternalTables(boDef );

                // TODO:beforeDabaseScripts
                buildInheritDataSources( boDef ,boBuilder.listUndeployedDefinitions(p_repository, p_objectInterfaceMap), p_createdFwdMethods );
            }
        }
    }

    public void inheritObject( boDefHandler boDef )
    {
        MapType1Def def = netgest.bo.plugins.data.MapType1Def.getUndeployedDataSourceDefinition( p_repository, boDef );
        if( def != null && "1".equals( def.getMapType() ) )
        {
            // TODO: Inherit Deploy
            deployDataSource(p_dbb, p_repository,  boDef );

            // TODO: Inherit Deploy
            buildExternalTables( boDef );

            // TODO: Inherit Deploy
            deployMappingTriggers( boDef );
        }
    }

    public void beforeInheritViewes(boDefHandler boDef)
    {
    }

    public void afterInheritViewes(boDefHandler boDef)
    {
    }

    public void addViewFields(ArrayList flds, boDefHandler boDef)
    {
        MapType1Def def = netgest.bo.plugins.data.MapType1Def.getUndeployedDataSourceDefinition( p_repository, boDef );
        if( def != null && "1".equals( def.getMapType() ) )
        {
            MapType1Def.ObjectDS ds = def.getObjectDataSources().getDataSources()[0];
            if( def.haveLocalTable() )
            {
                String[] fkeys = ds.getKeys();
                String[] fkeystypes = ds.getKeysDataTypes();
                for (int z = 0; z < fkeys.length; z++)
                {
                    flds.add( fkeys[z] + "$R" );
                }
            }
            else
            {
                String[] fkeys = ds.getKeys();
                String[] fkeystypes = ds.getKeysDataTypes();
                for (int z = 0; z < fkeys.length; z++)
                {
                    flds.add( fkeys[z] + "$L" );
                }
            }

        }

    }
}