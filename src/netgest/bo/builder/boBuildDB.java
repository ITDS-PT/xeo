package netgest.bo.builder;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.CRC32;

import javax.naming.InitialContext;

import netgest.bo.boException;
import netgest.bo.data.DataManager;
import netgest.bo.data.DataResultSet;
import netgest.bo.data.DataSet;
import netgest.bo.data.DataSetMetaData;
import netgest.bo.data.DriverManager;
import netgest.bo.data.DriverUtils;
import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefClsState;
import netgest.bo.def.boDefDatabaseObject;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefInterface;
import netgest.bo.plugins.DataPluginManager;
import netgest.bo.plugins.IDataBuilderDB;
import netgest.bo.plugins.IDataPlugin;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boRepository;

import netgest.bo.system.Logger;

public class boBuildDB
{
    //logger
    public static Logger logger = Logger.getLogger("netgest.bo.builder.boBuildDB");

    public static final byte        BUILD_ATTRIBUTES            = 0;
    public static final byte        BUILD_CONSTRAINTS           = 1;
    public static final String      DBTYPE_BOUI                 = "NUMBER";
    public static final int         RELTABLE_ALLOWDUPLICATES    = 0;
    public static final int         RELTABLE_NODUPLICATES       = 1;


    private static final int EVENT_BEFORE_DATABASE_SCRIPTS  = 0;
    private static final int EVENT_AFTER_DATABASE_SCRIPTS   = 1;
    private static final int EVENT_BUILD_INHERIT            = 2;
    private static final int EVENT_BEFORE_INHERIT_VIEWS     = 3;
    private static final int EVENT_AFTER_INHERIT_VIEWS      = 4;


    private static final String[] BRIDGE_COLUMNS =
    {
        "SYS_USER", "SYS_ICN",
        "SYS_DTCREATE", "SYS_DTSAVE", "PARENT$", "CHILD$",
        "SYS_FLDINPUTTYPE"
    };

    private DataResultSet       p_ngtdic            = null;
    private DataResultSet       p_dropngtdic;

    private String[]            wksdef              = { "default" };
    private String              p_tablename;
    private EboContext          p_eboctx;
    private boDefHandler        p_masterdef;
    private byte                p_mode = 0;
    private Vector              p_tomigrate         = new Vector();
    private Hashtable           p_externalatts      = new Hashtable();
    private Hashtable           p_localatts         = new Hashtable();
    private Hashtable           p_includedobjects   = new Hashtable();

    private boBuildRepository   p_repository;
    private String              actualRepositoryName;
    private Hashtable           p_objectInterfaceMap;


    private IDataBuilderDB[] p_builders;

    public boBuildDB(EboContext eboctx)
    {
        p_eboctx = eboctx;
        p_repository = new boBuildRepository(eboctx.getBoSession()
                                                   .getRepository());
        actualRepositoryName = p_repository.getName();
        wksdef[0] = p_repository.getName();
        createDictionaryWks();
    }

    public boBuildDB(EboContext eboctx, boBuildRepository repository, Hashtable objectInterfaceMap)
    {
        p_eboctx = eboctx;
        p_objectInterfaceMap = objectInterfaceMap;
        p_repository = repository;
        actualRepositoryName = p_repository.getName();
        wksdef[0] = p_repository.getName();
        createDictionaryWks();
    }

    private void initializePlugIns( boDefHandler def )
    {
        ArrayList plugs = new ArrayList();
        IDataPlugin[] x = DataPluginManager.getPlugIns();
        for (int i = 0; i < x.length; i++)
        {
            IDataBuilderDB xplugin;
            if( (xplugin = x[i].getBuilderDB( p_repository, def )) != null )
            {
                plugs.add( xplugin );
            }
        }
        p_builders = (IDataBuilderDB[])plugs.toArray(new IDataBuilderDB[(plugs.size())]);
    }


    public Hashtable getBuildedObjects()
    {
        return p_includedobjects;
    }

    private static final DataResultSet loadVirtualTable(String table,
        String query, int page, int pageSize, String orderBy, EboContext ctx,
        boBuildRepository repository)
    {
        StringBuffer sql = new StringBuffer("SELECT * FROM ");
        sql.append(table);

        if ((query != null) && (query.trim().length() > 0))
        {
            sql.append(" WHERE ").append(query);
        }

        if ((orderBy != null) && (orderBy.trim().length() > 0))
        {
            sql.append(" ORDER BY ").append(orderBy);
        }

        DataResultSet ret = new DataResultSet(DataManager.executeNativeQuery(
                    ctx, repository.getDefDriver().getName(), sql.toString(),
                    page, pageSize, null));

        return ret;
    }

    private void createDictionaryWks()
    {
        //        wksdef = p_eboctx.getWorkSpaceManager().getWorkspaceIds();
        //        wkssuf = p_eboctx.getWorkSpaceManager().getWorkspaceSuffixes();
        try
        {
            p_ngtdic = loadVirtualTable("NGTDIC", "0=1", 1, 99999, "",
                    p_eboctx, p_repository);
            p_dropngtdic = loadVirtualTable("NGTDIC", "0=1", 1, 99999, "",
                    p_eboctx, p_repository);
        }
        catch (Exception e)
        {
            throw new boException(this.getClass().getName() +
                "createDictionaryWks", "", e);
        }
    }

    public void buildObject(boDefHandler bodef, boolean fullbuild, byte mode)
        throws boRuntimeException
    {
        try
        {
            initializePlugIns( bodef );

            if (bodef.getName().equalsIgnoreCase("letter"))
            {
                int x = 0;
            }

            if (bodef.getClassType() == boDefHandler.TYPE_ABSTRACT_CLASS)
            {
                return;
            }

            if (!((bodef.getBoSuperBo() == null) ||
                    (bodef.getBoSuperBo().trim().length() == 0)))
            {
                boDefHandler objsuper = boDefHandler.getBoDefinition(bodef.getBoSuperBo());

                if (bodef.getClassType() == boDefHandler.TYPE_ABSTRACT_CLASS)
                {
                    return;
                }
            }

            p_mode = mode;

            p_masterdef = bodef;
//VERIFICAR
            if( bodef.getName().startsWith("Ebo_Perf") )
            {
                String toStop="yes";
            }
            p_tablename = getDataTableName( bodef );

            // Build all object in the same table
            boDefHandler[] allbo = boBuilder.listUndeployedDefinitions(p_repository, p_objectInterfaceMap);
            for (short i = 0; i < allbo.length; i++)
            {
                if( !allbo[i].getName().equals( bodef.getName() ) )
                {
                    if ( getDataTableName( allbo[i] ).equals( p_tablename ) )
                    {
                        p_includedobjects.put( allbo[i].getName(),allbo[i].getName() );
                        makeObject( allbo[i], false );
                        String xsuper = allbo[i].getBoSuperBo();
                        while (xsuper != null)
                        {
                            boDefHandler defsup = boBuilder.getUndeployedDefinitions(p_repository,
                                    xsuper, p_objectInterfaceMap, false );
                            if (defsup.getClassType() == boDefHandler.TYPE_CLASS)
                            {
                                String xtable_nam = getDataTableName( defsup );
                                if (!xtable_nam.equals(p_tablename))
                                {
                                    p_includedobjects.put( defsup.getName(), defsup.getName() );
                                    makeObject(defsup, false);
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
            makeObject(bodef, false);

            generateScripts(fullbuild);

            fireEvent( EVENT_AFTER_DATABASE_SCRIPTS, bodef );
//            deployMappingTriggers( bodef );

        }
        catch (boException e)
        {
            /*            if(!fullbuild && e.getErrorCode().equals("BO-1304")) {
                            createDictionaryWks();
                            buildObject(bodef,true);
                        } else {
                            throw(e);
                        }*/
            throw (e);
        }
    }

    private void makeObject(boDefHandler bodef, boolean createdFwdMethods) throws boRuntimeException
    {
        if (bodef != null)
        {

//            Object[] props = getObjectTableAndLoadExternalAttributes(bodef);
//            p_tablename = (String) props[0];
            initializePlugIns( bodef );
            initializeBuilders( createdFwdMethods );

//            p_tablename = bodef.getBoPhisicalMasterTable();
//
//            for (int i = 0; i < p_builders.length; i++)
//            {
//                // Vai a todos os builders para verificar se algum tem um nome diferente para
//                // a tabela
//                String btable_name = p_builders[i].getPhisicalTableName( bodef );
//                if( btable_name != null )
//                {
//                    p_tablename = btable_name;
//                }
//            }



//            addObjectDesc(bodef);
            addTable(p_tablename, bodef.getDescription());

            if (p_mode == BUILD_CONSTRAINTS)
            {
                if (bodef.getBoName().equalsIgnoreCase("Ebo_Registry"))
                {
                    addIndex("IDX_" + p_tablename + "_BOUI",
                        "TO index BOUI of " + bodef.getDescription(),
                        p_tablename, "BOUI");
                }
                if( containsMoreThanOneClass( bodef ) )
                {
                    addIndex("IDX_" + p_tablename,
                        "Index for [" + p_tablename + "] CLASSNAME", p_tablename,
                        "CLASSNAME");
                }
            }

            if (p_mode == BUILD_ATTRIBUTES)
            {
                if (bodef.getBoName().equalsIgnoreCase("Ebo_Registry"))
                {
                    addPrimaryKey(p_tablename,
                        "Primary key of " + bodef.getDescription(),
                        p_tablename, "UI$");
                }
                else
                {
                    addPrimaryKey(p_tablename,
                        "Primary key of " + bodef.getDescription(),
                        p_tablename, "BOUI");
                }

                if(bodef.getBoMarkInputType())
                {
                    addField(p_tablename, "SYS_FLDINPUTTYPE", "RAW(2000)",
                        "User or auto input of field", false, "", "", "");
                }

            }

            String boname = bodef.getBoName();

            String description = bodef.getBoDescription();
            String defaultlang = bodef.getBoDefaultLanguage();

            buildObject( bodef );

            // First put inherit Attributes then own Attributes
//            scanAndBuildInherit(bodef,
//                boBuilder.listUndeployedDefinitions(p_repository, p_objectInterfaceMap), false, createdFwdMethods);

        }
    }

    private final void buildObject( boDefHandler bodef ) throws boRuntimeException
    {
        // Create the table and primary key for Object if this is not a object sharing a table;
        // Create Attributes
        buildAttributes(bodef);

        // Create global indexes,uniques, etc...
        buildDatabaseObjects(bodef.getBoDatabaseObjects(), p_tablename);

        // Add system attributes
        addSystemFields(bodef);

        // Create state attributes and tables
        buildStates(bodef);

        // Drop duplicated itens in NGTDIC (In inherit Child Fields Remain)
        dropDuplicates();

        // Drop unused Attributes
        // Deploy DataSources Definitions
        fireEvent( EVENT_BEFORE_DATABASE_SCRIPTS, bodef );

    }


//    private void scanAndBuildInherit(boDefHandler bodef, boDefHandler[] allbo,
//        boolean inherit, boolean createdFwdMethods) throws boRuntimeException
//    {
//        // Adds super fields;
//        for (short i = 0; i < allbo.length; i++)
//        {
//            if (!bodef.getBoName().equals(allbo[i].getBoName()))
//            {
//
////                String obj_table_name = getTableForConstraints( boDefHandler.getBoDefinition( allbo[i].getName() ) );
//
//                if ( allbo[i].getBoPhisicalMasterTable().equals( bodef.getBoPhisicalMasterTable() ) )
//                {
//                    p_includedobjects.put(allbo[i].getBoName(), "");
//                    buildAttributes(boBuilder.getUndeployedDefinitions(
//                            p_repository, allbo[i].getBoName(), p_objectInterfaceMap, createdFwdMethods));
//                    buildDatabaseObjects(boBuilder.getUndeployedDefinitions(
//                            p_repository, allbo[i].getBoName(), p_objectInterfaceMap, createdFwdMethods)
//                                                  .getBoDatabaseObjects(),
//                        p_tablename);
//
//                    addSystemFields( allbo[i] );
//
//                    buildStates(boBuilder.getUndeployedDefinitions(
//                            p_repository, allbo[i].getBoName(), p_objectInterfaceMap, createdFwdMethods));
//
//
//                    // Manada enventos para outros builders
//                    fireEvent( EVENT_BUILD_INHERIT, boBuilder.getUndeployedDefinitions(
//                            p_repository, allbo[i].getBoName(), p_objectInterfaceMap, createdFwdMethods) );
//
//                    String xsuper = allbo[i].getBoSuperBo();
//
//                    while (xsuper != null)
//                    {
//                        boDefHandler defsup = boBuilder.getUndeployedDefinitions(p_repository,
//                                xsuper, p_objectInterfaceMap, createdFwdMethods);
//
//                        if (defsup != null)
//                        {
//                            if (defsup.getClassType() == boDefHandler.TYPE_CLASS)
//                            {
//
////                                String xtable_nam = getTableForConstraints( boDefHandler.getBoDefinition( defsup.getName() ) );
//                                String xtable_nam = defsup.getBoPhisicalMasterTable();
//                                if (!xtable_nam.equals(p_tablename))
//                                {
//                                    boDefHandler cbodef  = boBuilder.getUndeployedDefinitions(
//                                            p_repository, xsuper, p_objectInterfaceMap, createdFwdMethods);
//
//                                    addSystemFields( cbodef );
//
//                                    buildAttributes( cbodef );
//                                    buildStates( cbodef );
//
//
//                                    // Manada enventos para outros builders
//                                    fireEvent( EVENT_BUILD_INHERIT, cbodef );
//
//                                }
//                            }
//
//                            xsuper = defsup.getBoSuperBo();
//                        }
//                        else
//                        {
//                            xsuper = null;
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    /*    private void buildSuperBo(String boname,boDefHandler[] allbo) throws boRuntimeException {
//            for (short i = 0; i < allbo.length; i++)  {
//                if (allbo[i].getBoSuperBo() != null && allbo[i].getBoSuperBo().equals(boname)) {
//                    buildAttributes(allbo[i]);
//                    buildSuperBo(allbo[i].getBoName(),allbo);
//                }
//            }
//        }*/
    private void buildStates(boDefHandler bodef)
    {
        boDefClsState state = bodef.getBoClsState();

        if (state != null)
        {
            boDefClsState[] xx = state.getChildStateAttributes();

            for (int i = 0; i < xx.length; i++)
            {
                p_localatts.put(xx[i].getName().toUpperCase(),
                    xx[i].getName().toUpperCase());

                if (p_mode == BUILD_ATTRIBUTES)
                {
                    addField(p_tablename, xx[i].getName(), "NUMBER",
                        xx[i].getDescription(), false, "", "", "");
                }
            }
        }

        //String tablename = bodef.getBoMasterTable()+"$$STATE";
        //addTable(tablename,"States for "+bodef.getBoDescription());
        //addField(tablename,"BOUI",boBuildDB.DBTYPE_BOUI,"BOUI of "+bodef.getBoName(),true,"","","");
        //addForeignKey("FK_" +bodef.getBoMasterTable(),"Foreign Key to "+bodef.getBoName(),tablename,"BOUI",bodef.getBoMasterTable(),"BOUI");
        //        buildStateNode(state,bodef,/*tablename,*/true);
    }

    /*    private void buildStateNode(boDefClsState state,boDefHandler bodef,boolean build) {
            if(build && !state.getMethod().equals(boDefClsState.METHOD_PARALLEL)) {
                addField(bodef.getBoMasterTable(),state.getName(),"NUMBER",state.getDescription(),false,"","","");
                //addField(tablename,state.getName(),"NUMBER",state.getDescription(),false,"","","");
            } else {
                build=false;
            }
            boDefClsState[] childstates=state.getChildStates();
            for(int i=0;i<childstates.length;i++) {
                buildStateNode(childstates[i],bodef,!build);
            }
        }*/
    private void buildAttributes(boDefHandler bodef) throws boRuntimeException
    {
        String tablename = p_tablename;
        boDefAttribute[] attribs = bodef.getBoAttributes();

        for (int i = 0; i < attribs.length; i++)
        {
            boDefAttribute catt = attribs[i];

            if (p_externalatts.get(catt.getDbName().toUpperCase()) == null)
            {
                if (!catt.getName().equals("BOUI"))
                {
                    if (catt.getDbIsTabled())
                    {
                        buildFieldTabled(tablename, catt);
                    }
                    else if (catt.getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE)
                    {
                        if (catt.getDbIsBinding())
                        {
                            p_localatts.put(catt.getDbName(), catt.getDbName());

                            if (this.p_mode == BUILD_ATTRIBUTES)
                            {
                                //boolean req = (catt.getRequired().toUpperCase()
                                //                   .equals("Y") ||
                                //    catt.getRequired().toUpperCase().equals("YES"))
                                //   ? true : false;

                                addField(tablename, catt.getDbName(),
                                    catt.getType(), catt.getLabel(), false,
                                    "", "", "");
                            }
                        }
                    }
                    else if (catt.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                    {
                        boDefHandler xbobdef = null;
                        xbobdef = catt.getReferencedObjectDef();

                        if (xbobdef != null)
                        {
                            if ( catt.getMaxOccurs() > 1  )
                            {
                                if ( catt.getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE )
                                {
                                    makeRelationTable(catt, bodef, xbobdef,
                                        boBuildDB.RELTABLE_NODUPLICATES);
                                }
                            }
                            else /*if (xbobdef.getCanBeOrphan())*/
                            {
                                p_localatts.put(catt.getDbName(),
                                    catt.getDbName());

                                if (this.p_mode == BUILD_ATTRIBUTES)
                                {
//                                    boolean req = (catt.getRequired()
//                                                       .toUpperCase().equals("Y") ||
//                                        catt.getRequired().toUpperCase().equals("YES"))
//                                        ? true : false;

                                    addField(tablename, catt.getDbName(),
                                        boBuildDB.DBTYPE_BOUI,
                                        "BOUI for Object " +
                                        xbobdef.getBoName(), false, "", "", "");
                                }
                                else
                                {
                                    addForeignKey( catt, tablename );
                                }
                            }
                        }
                    }

                    if ((this.p_mode == BUILD_CONSTRAINTS) &&
                            (((catt.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                            (catt.getReferencedObjectDef() != null)) ||
                            (catt.getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE)))
                    {
                        if (catt.getDbIsUnique())
                        {
                            addUnique(tablename + "_" + catt.getDbName() +
                                "_UN", "Unique key for " + catt.getLabel(),
                                tablename, catt.getDbName());
                        }
                        else if (catt.getDbIsIndexed())
                        {
                            addIndex(tablename + "_" + catt.getDbName() +
                                "_IDX", "Index for " + catt.getLabel(),
                                tablename, catt.getDbName());
                        }
                    }
                }
                else
                {
                    p_localatts.put(catt.getDbName(), catt.getDbName());
                }
            }
        }
    }

    public void buildFieldTabled(String tablename, boDefAttribute catt)
    {
        if (!catt.getBoDefHandler().getBoPhisicalMasterTable().equals(p_tablename))
        {
            return;
        }

        if (this.p_mode == BUILD_ATTRIBUTES)
        {
            addTable(tablename + "$" + catt.getDbName(),
                "Table for multiple value for field " + catt.getDescription());

            if (catt.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
            {
                addField(catt.getDbTableName(),
                    catt.getDbTableChildFieldName(), DBTYPE_BOUI,
                    "Referenced boui of " +
                    catt.getBoDefHandler().getBoDescription(), true, "", "", "");
            }
            else
            {
                addField(catt.getDbTableName(),
                    catt.getDbTableChildFieldName(), catt.getType(),
                    "Referenced boui of " +
                    catt.getBoDefHandler().getBoDescription(), true, "", "", "");
            }

            addField(catt.getDbTableName(), catt.getDbTableFatherFieldName(),
                DBTYPE_BOUI,
                "Field to hold parent boui of " +
                catt.getBoDefHandler().getBoDescription(), true, "", "", "");

            MigrationInformation mig = new MigrationInformation();
            mig.moveColumnToTable(tablename, catt.getDbTableName(),
                catt.getDbName(), catt.getDbName(), "BOUI", "T$PARENT$",
                "OBJECTTYPE='F' AND TABLENAME='" + (tablename.toUpperCase()) +
                "' AND OBJECTNAME='" + (catt.getDbName().toUpperCase()) + "'");
            p_tomigrate.add(mig);
        }

        if (p_mode == BUILD_ATTRIBUTES)
        {
            addPrimaryKey(catt.getDbTableName() + "$" + catt.getDbName(),
                "Primary for multiple values of " + catt.getDescription(),
                catt.getDbTableName(), "T$PARENT$," + catt.getDbName());
        }
        else
        {
            addForeignKey( catt, tablename );
        }
    }

    public void buildDatabaseObjects(boDefDatabaseObject[] dbobjects,
        String tablename)
    {
        if (this.p_mode == BUILD_CONSTRAINTS)
        {
            for (int i = 0; (dbobjects != null) && (i < dbobjects.length);
                    i++)
            {
                if (dbobjects[i].getType() == boDefDatabaseObject.DBOBJECT_INDEX)
                {
                    addIndex(tablename + "_" + dbobjects[i].getId(),
                        dbobjects[i].getLabel(), tablename,
                        dbobjects[i].getExpression());
                }
                else if (dbobjects[i].getType() == boDefDatabaseObject.DBOBJECT_UNIQUEKEY)
                {
                    addUnique(tablename + "_" + dbobjects[i].getId(),
                        dbobjects[i].getLabel(), tablename,
                        dbobjects[i].getExpression());
                }
            }
        }
    }

    public void addView(String viewname, String tabledesc, String expression)
    {
        try
        {
            for (int i = 0; i < wksdef.length; i++)
            {
                p_ngtdic.moveToInsertRow();
                p_ngtdic.updateString("TABLENAME", viewname.toUpperCase());
                p_ngtdic.updateString("OBJECTNAME", viewname.toUpperCase());
                p_ngtdic.updateString("OBJECTTYPE", "V");
                p_ngtdic.updateString("FRIENDLYNAME", tabledesc);
                p_ngtdic.updateString("SCHEMA", wksdef[i]);
                p_ngtdic.updateString("EXPRESSION", expression);
                p_ngtdic.insertRow();
            }
        }
        catch (java.sql.SQLException e)
        {
            String[] arg = { viewname };
            throw new boException(this.getClass().getName() +
                ":addTable(String)", "BO-1301", e, arg);
        }
    }
// Acho que isto não faz nada que faça falta JP: 14-05-2005
//    public void addObjectDesc(boDefHandler bodef)
//    {
//        try
//        {
//            for (int i = 0; i < wksdef.length; i++)
//            {
//                p_ngtdic.moveToInsertRow();
//                p_ngtdic.updateString("TABLENAME", bodef.getName());
//                p_ngtdic.updateString("OBJECTNAME", bodef.getName());
//
//                if (bodef.getASPMode() == boDefHandler.ASP_PRIVATE)
//                {
//                    p_ngtdic.updateString("OBJECTTYPE", "PV");
//                }
//
//                if (bodef.getASPMode() == boDefHandler.ASP_SEMI_PRIVATE)
//                {
//                    p_ngtdic.updateString("OBJECTTYPE", "SP");
//                }
//
//                if (bodef.getASPMode() == boDefHandler.ASP_CONTROLLER)
//                {
//                    p_ngtdic.updateString("OBJECTTYPE", "CT");
//                }
//                else
//                {
//                    p_ngtdic.updateString("OBJECTTYPE", "GL");
//                }
//
//                p_ngtdic.updateString("FRIENDLYNAME", bodef.getName());
//                p_ngtdic.updateString("SCHEMA", wksdef[i]);
//                p_ngtdic.insertRow();
//            }
//        }
//        catch (java.sql.SQLException e)
//        {
//            String[] arg = { bodef.getName() };
//            throw new boException(this.getClass().getName() +
//                ":addTable(String)", "BO-1301", e, arg);
//        }
//    }

    public void addTable(String tablename, String tabledesc)
    {
        try
        {
            for (int i = 0; i < wksdef.length; i++)
            {
                p_ngtdic.moveToInsertRow();
                p_ngtdic.updateString("TABLENAME", tablename.toUpperCase());
                p_ngtdic.updateString("OBJECTNAME", tablename.toUpperCase());
                p_ngtdic.updateString("OBJECTTYPE", "T");
                p_ngtdic.updateString("FRIENDLYNAME", tabledesc);
                p_ngtdic.updateString("SCHEMA", wksdef[i]);
                p_ngtdic.insertRow();
            }
        }
        catch (java.sql.SQLException e)
        {
            String[] arg = { tablename };
            throw new boException(this.getClass().getName() +
                ":addTable(String)", "BO-1301", e, arg);
        }
    }

    public void addField(String tablename, String fieldname, String fieldtype,
        String description, boolean required, String picture,
        String macrofield, String defaultvalue)
    {
        try
        {
            String[] ft = boBuildDBUtils.parseFieldType(fieldtype);

            for (int i = 0; i < wksdef.length; i++)
            {
                String[] keys =
                {
                    "SCHEMA", "TABLENAME", "OBJECTNAME", "OBJECTTYPE"
                };
                String[] keysv =
                {
                    wksdef[i], tablename.toUpperCase(), fieldname.toUpperCase(),
                    "F"
                };

                if (!p_ngtdic.locatefor(keys, keysv))
                {
                    p_ngtdic.moveToInsertRow();
                    p_ngtdic.updateString("TABLENAME", tablename.toUpperCase());
                    p_ngtdic.updateString("OBJECTNAME", fieldname.toUpperCase());
                    p_ngtdic.updateString("OBJECTTYPE", "F");
                    p_ngtdic.updateString("FIELDTYPE", ft[0].toUpperCase());
                    p_ngtdic.updateString("FIELDSIZE", ft[1].toUpperCase());
                    p_ngtdic.updateString("FRIENDLYNAME", description);

                    //                p_ngtdic[i].updateString("REQUIRED",p_eboctx.getWorkSpaceManager().have(i,ngtWorkspaceManager.HAVE_NOTNULL) && required?"S":"N");
                    p_ngtdic.updateString("REQUIRED", required ? "S" : "N");
                    p_ngtdic.updateString("PICTURE", picture);
                    p_ngtdic.updateString("MACROFIELD", macrofield);
                    p_ngtdic.updateString("DEFAULTVALUE", defaultvalue);
                    p_ngtdic.updateString("SCHEMA", wksdef[i]);
                    p_ngtdic.insertRow();
                }
            }
        }
        catch (java.sql.SQLException e)
        {
            String[] arg = { fieldname };
            throw new boException(this.getClass().getName() + ":addField(...)",
                "BO-1302", e, arg);
        }
    }

    public String getTableForConstraints(boDefHandler def)
    {
        String toRet = null;

//        if (def.getBoDataSources().haveLocalTable())
//        {
//            toRet = def.getBoPhisicalMasterTable() + "_LOCAL";
//        }
//        else
//        {
            IDataPlugin[] plugIns = DataPluginManager.getPlugIns();
            for (int i = 0; i < plugIns.length; i++)
            {
                IDataBuilderDB plugIn = plugIns[i].getBuilderDB( p_repository, def );
                if( plugIn != null )
                {
                    plugIn.initialize( p_eboctx, this, p_repository, p_mode, this.p_objectInterfaceMap, false );
                    if ( plugIn.getPhisicalTableName( def ) != null )
                    {
                        toRet = plugIn.getPhisicalTableName( def );
                    }
                }

            }

            if( toRet == null )
            {
                toRet = def.getBoPhisicalMasterTable();
            }
//        }

        /*if(p_repository.getParentRepository() != null)
        {
            StringBuffer whereClause = new StringBuffer("objecttype = 'T' and tablename = '");
            whereClause.append(def.getBoPhisicalMasterTable()).append("'");
            DataResultSet query = loadVirtualTable("NGTDIC",
                    whereClause.toString(), 1, 1, "", p_eboctx, p_repository);
            if(query.getRowCount() == 0)
            {
                toRet = p_repository.getParentRepository().getSchemaName() + "." + toRet;
            }
        }*/
        return toRet;
    }

    public static final String encodeObjectName(String name)
    {
        if (name.length() > 30)
        {
            name = name.toUpperCase();

            CRC32 xcrc = new CRC32();
            String xname = name.substring(0, 10);
            xcrc.update(name.getBytes());
            name = xname + "_" + xcrc.getValue();
        }

        return name;
    }

    public static final String encodeObjectName_25(String name)
    {
        if (name.length() > 25)
        {
            name = name.toUpperCase();

            CRC32 xcrc = new CRC32();
            String xname = name.substring(0, 10);
            xcrc.update(name.getBytes());
            name = xname + "_" + xcrc.getValue();
        }

        return name;
    }

    public void addForeignKey(String name, String friendlyname,
        String tablename, String localfields, String tablereferenced,
        String fieldrefreced)
    {
        try
        {
            for (int i = 0; i < wksdef.length; i++)
            {
                //                if(p_eboctx.getWorkSpaceManager().have(i,ngtWorkspaceManager.HAVE_FOREIGN_KEYS)) {
                p_ngtdic.moveToInsertRow();
                p_ngtdic.updateString("TABLENAME", tablename.toUpperCase());

                String encname = encodeObjectName(name).toUpperCase();
                p_ngtdic.updateString("OBJECTNAME", encname);
                p_ngtdic.updateString("OBJECTTYPE", "FK");
                p_ngtdic.updateString("FRIENDLYNAME", friendlyname);
                p_ngtdic.updateString("EXPRESSION", localfields.toUpperCase());
                p_ngtdic.updateString("TABLEREFERENCE",
                    tablereferenced.toUpperCase());
                p_ngtdic.updateString("FIELDREFERENCE",
                    fieldrefreced.toUpperCase());
                p_ngtdic.updateString("DELETECASCADE", "N");
                p_ngtdic.updateString("SCHEMA", wksdef[i]);
                p_ngtdic.insertRow();

                //                }
            }
        }
        catch (java.sql.SQLException e)
        {
            String[] arg = { name };
            throw new boException(this.getClass().getName() +
                ":addForeignKey(...)", "BO-1303", e, arg);
        }
    }

    public void addPrimaryKey(String name, String friendlyname,
        String tablename, String fields)
    {
        try
        {
            for (int i = 0; i < wksdef.length; i++)
            {
                //                if(p_eboctx.getWorkSpaceManager().have(i,ngtWorkspaceManager.HAVE_PRIMARY_KEYS)) {
                p_ngtdic.moveToInsertRow();
                p_ngtdic.updateString("TABLENAME", tablename.toUpperCase());
                p_ngtdic.updateString("OBJECTNAME",                
                    encodeObjectName(p_repository.getDriver().getDriverUtils().getPKConstraintName(name)).toUpperCase());
                p_ngtdic.updateString("OBJECTTYPE", "PK");
                p_ngtdic.updateString("FRIENDLYNAME", friendlyname);
                p_ngtdic.updateString("EXPRESSION", fields.toUpperCase());
                p_ngtdic.updateString("SCHEMA", wksdef[i]);
                p_ngtdic.insertRow();

                //                }
            }
        }
        catch (java.sql.SQLException e)
        {
            String[] arg = { name };
            throw new boException(this.getClass().getName() +
                ":addForeignKey(...)", "BO-1303", e, arg);
        }
    }

    public void addIndex(String name, String friendlyname, String tablename,
        String fields)
    {
        try
        {
            if (name.length() > 30)
            {
//                logger.warn("Warning foreign key name [" + name +
//                    "] exceeds 30 characters, some constraints may be loose");
            }

            for (int i = 0; i < wksdef.length; i++)
            {
                //                if(p_eboctx.getWorkSpaceManager().have(i,ngtWorkspaceManager.HAVE_PRIMARY_KEYS)) {
                p_ngtdic.moveToInsertRow();
                p_ngtdic.updateString("TABLENAME", tablename.toUpperCase());
                p_ngtdic.updateString("OBJECTNAME",
                    encodeObjectName(name).toUpperCase());
                p_ngtdic.updateString("OBJECTTYPE", "IDX");
                p_ngtdic.updateString("FRIENDLYNAME", friendlyname);
                p_ngtdic.updateString("EXPRESSION", fields.toUpperCase());
                p_ngtdic.updateString("SCHEMA", wksdef[i]);
                p_ngtdic.insertRow();

                //                }
            }
        }
        catch (java.sql.SQLException e)
        {
            String[] arg = { name };
            throw new boException(this.getClass().getName() + ":addIndex(...)",
                "BO-1303", e, arg);
        }
    }

    public void addUnique(String name, String friendlyname, String tablename,
        String fields)
    {
        try
        {
            if (name.length() > 30)
            {
                logger.warn("Warning foreign key name [" + name +
                    "] exceeds 30 characters, some constraints may be loose");
            }

            for (int i = 0; i < wksdef.length; i++)
            {
                //                if(p_eboctx.getWorkSpaceManager().have(i,ngtWorkspaceManager.HAVE_PRIMARY_KEYS)) {
                p_ngtdic.moveToInsertRow();
                p_ngtdic.updateString("TABLENAME", tablename.toUpperCase());
                p_ngtdic.updateString("OBJECTNAME",
                    encodeObjectName(name).toUpperCase());
                p_ngtdic.updateString("OBJECTTYPE", "UN");
                p_ngtdic.updateString("FRIENDLYNAME", friendlyname);
                p_ngtdic.updateString("EXPRESSION", fields.toUpperCase());
                p_ngtdic.updateString("SCHEMA", wksdef[i]);
                p_ngtdic.insertRow();

                //                }
            }
        }
        catch (java.sql.SQLException e)
        {
            String[] arg = { name };
            throw new boException(this.getClass().getName() + ":addIndex(...)",
                "BO-1303", e, arg);
        }
    }

    public String generateScripts(boolean fullbuild)
    {
        OracleDBM dbm = null;

        try
        {
            //            dbmagf dbm = new dbmagf();
            //            dbm.setEnvironment(p_eboctx,""/*"SQLSCRIPTS"*/);
            //DropFields(false /*true && this.p_mode == BUILD_ATTRIBUTES */);

            migrateData(true);

            if (!fullbuild && (p_tomigrate.size() == 0))
            {
                AnalyzeDictionarys();
            }

            if (!fullbuild)
            {
                //                backupTables();
            }

            // If the table was changed
            // checkIfTableWasChanged( p_masterdef  );
            if (this.p_mode == BUILD_CONSTRAINTS)
            {
                //                p_dropngtdic.getOwnerDocument().setParameter("agf_customclass",
                //                    "netgest.applications.system.dbmagf");
                //                p_dropngtdic.getOwnerDocument().setModo("3");
                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_dropngtdic, "3");
                dbm.close();

                //                p_dropngtdic.getOwnerDocument().updatedoc();
            }

            DataResultSet tablePks = loadVirtualTable("NGTDIC", "0=1", 1,
                    99999, "", p_eboctx, p_repository);

            if (this.p_mode == BUILD_ATTRIBUTES)
            {
                p_ngtdic.beforeFirst();

                while (p_ngtdic.next())
                {
                    if (p_ngtdic.getString("OBJECTTYPE").equals("PK"))
                    {
                        tablePks.moveToInsertRow();
                        copyRowData(p_ngtdic, tablePks);
                        p_ngtdic.deleteRow();
                        tablePks.insertRow();
                    }
                }
            }

            dbm = p_repository.getDriver().getDBM();
            dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
            dbm.close();

            //            p_ngtdic.getOwnerDocument().setParameter("agf_customclass",
            //                "netgest.applications.system.dbmagf");
            //            p_ngtdic.getOwnerDocument().setModo("2");
            //            p_ngtdic.setCheckSysIcn(false);
            //            p_ngtdic.getOwnerDocument().updatedoc();


            if (this.p_mode == BUILD_ATTRIBUTES)
            {
                migrateData(false);

                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_dropngtdic, "3");
                dbm.close();

                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, tablePks, "2");
                dbm.close();
            }

            return ""; //dbm.getDDLScript("DAT");
        }
        catch (java.sql.SQLException e)
        {
//            String[] arg = new String[1];

//            try
//            {
//                arg[0] = p_ngtdic.getString("OBJECTTYPE") + "-" +
//                    p_ngtdic.getString("TABLENAME") + "." +
//                    p_ngtdic.getString("OBJECTNAME");
//            }
//            catch (java.sql.SQLException esql)
//            {
//                arg[0] = "unknown " + esql.getMessage();
//            }
//
            try
            {
                if (p_ngtdic.getString("OBJECTTYPE").equals("IDX"))
                {
                    logger.warn("Warning index key name [" + p_ngtdic.getString("OBJECTNAME") +
                        "] expected error: " + e.getMessage());
                    return "";
                }
                else
                {
                    logger.severe("ERROR ON GENERATE SCRIPTS [" + p_ngtdic.getString("OBJECTNAME") +
                        " -" + p_ngtdic.getString("OBJECTTYPE") +
                        "] expected error: " + e.getMessage());
                    throw new boException(this.getClass().getName() +
                        ":generateScripts()", "BO-1304", e );
                }
            }
            catch (Exception _e)
            {
                //ignore
                return "";
            }
        }
        catch (Exception e)
        {
            throw new boException(this.getClass().getName() +
                ":generateScripts()", "BO-1304", e, "");
        }
        finally
        {
            if (dbm != null)
            {
                dbm.close();
            }
        }
    }

    public void migrateData(boolean onlycheck) throws Exception
    {
        OracleDBM dbm = null;

        if (this.p_tomigrate != null)
        {
            Connection cn = null;

            try
            {
                final InitialContext ic = new InitialContext();

                cn = p_repository.getRepository().getDedicatedConnection();

                for (short i = 0; i < p_tomigrate.size(); i++)
                {
                    MigrationInformation migr = (MigrationInformation) p_tomigrate.get(i);

                    if (p_dropngtdic.locatefor(migr.dicquery))
                    {
                        if (!onlycheck)
                        {
                            PreparedStatement pstm = cn.prepareStatement(
                                    "select COLUMN_NAME,data_type,DATA_LENGTH,DATA_PRECISION,DATA_SCALE from user_tab_columns where table_name=? and COLUMN_NAME=?");
                            pstm.setString(1, migr.srctable);
                            pstm.setString(2, migr.srcfield);

                            ResultSet rslt = pstm.executeQuery();

                            if (rslt.next())
                            {
                                DataResultSet dropconst = loadVirtualTable("NGTDIC",
                                        "(TABLENAME='" +
                                        (migr.srctable.toUpperCase()) +
                                        "' OR TABLENAME='" +
                                        (migr.desttable.toUpperCase()) +
                                        "') AND SCHEMA='DATA' AND OBJECTTYPE IN ('PK','FK')",
                                        1, 99999, "", p_eboctx, p_repository);

                                if (dropconst.getRowCount() > 0)
                                {
                                    dbm = p_repository.getDriver().getDBM();
                                    dbm.createDabaseObjects(p_eboctx,
                                        dropconst, "3");
                                    dbm.close();
                                }

                                PreparedStatement pstm2 = cn.prepareStatement(
                                        "select COLUMN_NAME,data_type,DATA_LENGTH,DATA_PRECISION,DATA_SCALE from user_tab_columns where table_name=? and COLUMN_NAME=?");
                                pstm2.setString(1, migr.desttable);
                                pstm2.setString(2, migr.destfield);

                                ResultSet rslt2 = pstm2.executeQuery();

                                if (rslt2.next())
                                {
                                    if (migr.migtype == MigrationInformation.TYPE_MOVE_COLUMN)
                                    {
                                        CallableStatement csm = cn.prepareCall(
                                                "UPDATE " + migr.srctable +
                                                " SET " + migr.destfield + "=" +
                                                migr.srcfield + " WHERE " +
                                                migr.srcfield + " IS NOT NULL");
                                        int nrecs = csm.executeUpdate();
                                        logger.finest("Migrating data from " +
                                            migr.srctable + "." +
                                            migr.srcfield + "->" +
                                            migr.desttable + "." +
                                            migr.destfield + " [" + nrecs +
                                            " Rows]");
                                        csm.close();
                                        cn.commit();
                                    }
                                    else if (migr.migtype == MigrationInformation.TYPE_MOVE_COLUMN_TO_TABLE)
                                    {
                                        CallableStatement csm = null;

                                        try
                                        {
                                            csm = cn.prepareCall("INSERT INTO " +
                                                    migr.desttable + " ( " +
                                                    migr.destfield + "," +
                                                    migr.destrelfield +
                                                    " ) ( SELECT " +
                                                    migr.srcfield + "," +
                                                    migr.srcrelfield +
                                                    " FROM " + migr.srctable +
                                                    " WHERE " + migr.srcfield +
                                                    " IS NOT NULL)");

                                            int nrecs = csm.executeUpdate();
                                            csm.close();
                                            csm = cn.prepareCall("UPDATE " +
                                                    migr.srctable + " SET " +
                                                    migr.srcfield + "= NULL");
                                            nrecs = csm.executeUpdate();
                                            logger.finest(
                                                "Migrating data from " +
                                                migr.srctable + "." +
                                                migr.srcfield + "->" +
                                                migr.desttable + "." +
                                                migr.destfield + " [" + nrecs +
                                                " Rows]");
                                            csm.close();
                                            cn.commit();
                                        }
                                        catch (Exception e)
                                        {
                                            cn.rollback();
                                            csm.close();
                                            throw e;
                                        }
                                    }
                                    else if (migr.migtype == MigrationInformation.TYPE_MOVE_TABLE_TO_COLUMN)
                                    {
                                    }
                                }

                                rslt2.close();
                                pstm2.close();
                            }

                            rslt.close();
                            pstm.close();
                        }
                    }
                    else
                    {
                        p_tomigrate.remove(i);
                        i--;
                    }
                }
            }
            finally
            {
                if (dbm != null)
                {
                    dbm.close();
                }

                try
                {
                    if (cn != null)
                    {
                        cn.close();
                    }
                }
                catch (SQLException e)
                {
                }
            }
        }
    }

    public void addSystemFields(boDefHandler bodef)
    {

        if (p_mode == BUILD_ATTRIBUTES)
        {
            addField(p_tablename, "BOUI", boBuildDB.DBTYPE_BOUI,
                "BO Unique Identifier", true, "", "", "");

//            if( !bodef.getBoCanBeOrphan() )
//            {
//                addField(p_tablename, "LIN", boBuildDB.DBTYPE_BOUI,
//                    "Lin of the relation", true, "", "", "");
//            }
        }


    }

    public void makeRelationTable(boDefAttribute attlnk, boDefHandler xfather,
        boDefHandler xchild, int relationtype)
    {
        if (!attlnk.getBoDefHandler().getBoPhisicalMasterTable().equals(xfather.getBoPhisicalMasterTable()))
        {
            return;
        }

        //String ftable = xfather.getBoMasterTable();
        String ftable = p_tablename;
        String ctable = xchild.getBoPhisicalMasterTable();

        boDefBridge bobridge = attlnk.getBridge();

        String reltable = bobridge.getBoPhisicalMasterTable();

        String ffield = bobridge.getFatherFieldName();
        String cfield = bobridge.getChildFieldName();

        // Work around to change the fields name in the bridge
        MigrationInformation mig = new MigrationInformation();
        mig.moveColumn(reltable, bobridge.OLDgetChildFieldName().toUpperCase(),
            "CHILD$",
            "OBJECTTYPE='F' AND TABLENAME='" + (reltable.toUpperCase()) +
            "' AND OBJECTNAME='" +
            (bobridge.OLDgetChildFieldName().toUpperCase()) + "' ");
        this.p_tomigrate.add(mig);
        mig = new MigrationInformation();
        mig.moveColumn(reltable,
            bobridge.OLDgetFatherFieldName().toUpperCase(), "PARENT$",
            "OBJECTTYPE='F' AND TABLENAME='" + (reltable.toUpperCase()) +
            "' AND OBJECTNAME='" +
            (bobridge.OLDgetFatherFieldName().toUpperCase()) + "' ");
        this.p_tomigrate.add(mig);

        addTable(reltable,
            "Relation between [" + xfather.getBoName() + "] and [" +
            xchild.getBoName() + "]");

        if (this.p_mode == BUILD_ATTRIBUTES)
        {
            addField(reltable, ffield, boBuildDB.DBTYPE_BOUI,
                "BOUI of " + xfather.getBoName(), true, "", "", "");

            addField(reltable, cfield, boBuildDB.DBTYPE_BOUI,
                "BOUI of " + xchild.getBoName(), true, "", "", "");

            //Decisão para criar SYS_FLDINPUTTYPE
            //Caso alguma classe que extenda a actual tenha markInputType e esteja a utilizar a mesma 
            //tabela então deverá ser criado
            //Possivel problema quando estivermos a niveis mais baixos da hierarquia
            boDefHandler[] subclasses=xfather.getBoSubClasses();            
            boolean markInputType=false;
            

            for (int i=0;i<subclasses.length;i++)
            {
            	if (subclasses[i].getBoMarkInputType() && 
            			subclasses[i].getBoPhisicalMasterTable().equals(xfather.getBoPhisicalMasterTable()))
            	{
            		markInputType=true;
            		break;
            	}

            }
            
            if(xfather.getBoMarkInputType() || markInputType)
            {
                addField(reltable, "SYS_FLDINPUTTYPE", "RAW(2000)",
                    "User or auto input of field", false, "", "", "");
            }
        }

        addForeignKey( attlnk, reltable, bobridge.getChildFieldName() );

        if( this.p_mode == boBuildDB.BUILD_CONSTRAINTS )
        {
            if( getChildTables( xfather ).length == 1 )
            {
                addForeignKey("FK" + ffield + reltable,
                "Relation with [" + xfather.getBoName() + "]", reltable,
                ffield, p_tablename, "BOUI");
            }
        }

        boDefAttribute[] atts = bobridge.getBoAttributes();

        for (int i = 0; (atts != null) && (i < atts.length); i++)
        {
            if ((atts[i].getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) &&
                    atts[i].getDbIsBinding())
            {
                if (this.p_mode == BUILD_ATTRIBUTES)
                {
//                    boolean req = (atts[i].getRequired().toUpperCase().equals("Y") ||
//                        atts[i].getRequired().toUpperCase().equals("YES"))
//                        ? true : false;

                    addField(reltable, atts[i].getDbName(), atts[i].getType(),
                        atts[i].getLabel(), false, "", "", "");
                }

                if (this.p_mode == BUILD_CONSTRAINTS)
                {
                    if (atts[i].getDbIsUnique())
                    {
                        addUnique(reltable + "_" + atts[i].getDbName() + "_UN",
                            "Unique key for " + atts[i].getLabel(), reltable,
                            atts[i].getDbName());
                    }
                }
            }
            else if (atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
            {
                boDefHandler xbobdef = null;

                try
                {
                    xbobdef = atts[i].getReferencedObjectDef();

                    if (this.p_mode == BUILD_ATTRIBUTES)
                    {
//                        boolean req = (atts[i].getRequired().toUpperCase()
//                                              .equals("Y") ||
//                            atts[i].getRequired().toUpperCase().equals("YES"))
//                            ? true : false;

                       boolean rqDb =  atts[i].getDbRequired();
//                       if ( !rqDb.equals("default") && rqDb.length()>0 )
//                       {
//                           req = rqDb.equalsIgnoreCase("Y");
//                       }

                        addField(reltable, atts[i].getDbName(),
                            boBuildDB.DBTYPE_BOUI,
                            "BOUI for Object " + xbobdef.getBoName(), rqDb, "",
                            "", "");
                    }
                }
                catch (Exception e)
                {
                    logger.warn("Business Object [" + atts[i].getType() +
                        "] doesn't exist");
                }

                if (xbobdef != null)
                {
                    //if(!xbobdef.getCanBeOrphan()) {
                    //    throw new boException(this.getClass().getName()+"makeRelationTable(...)","BO-1402",null,xfather.getBoName());
                    // }
                }
            }
        }

        if (relationtype == RELTABLE_NODUPLICATES)
        {
            //            if(this.p_mode == BUILD_ATTRIBUTES)
            //            {
            if (p_mode == BUILD_ATTRIBUTES)
            {
                addPrimaryKey(reltable,
                    "Primary key of relation between [" + xfather.getBoName() +
                    "] and [" + xchild.getBoName() + "]", reltable,
                    ffield + "," + cfield);
            }

            //            }
        }
    }

    private void DropFields(boolean loadtablefields)
    {
        OracleDBM dbm = null;

        try
        {
            String filter = "";
            String sqlin = "";

            if (p_mode == BUILD_ATTRIBUTES)
            {
                filter = ",F,PK,V,";
                sqlin = "('F','PK','V')";
            }

            if (p_mode == BUILD_CONSTRAINTS)
            {
                filter = ",FK,UN,IDX,";
                sqlin = "('FK','UN','IDX')";
            }

            DataResultSet node = p_ngtdic;
            Vector tables = new Vector();
            node.beforeFirst();

            while (node.next())
            {
                if (tables.indexOf(node.getString("TABLENAME")) == -1)
                {
                    tables.add(node.getString("TABLENAME"));
                }

                if (node.getString("OBJECTTYPE").equals("V"))
                {
                    tables.remove(node.getString("TABLENAME"));
                }
            }

            if (loadtablefields)
            {
                //                dbmagf agf = new dbmagf();
                dbm = p_repository.getDriver().getDBM();
                dbm.setEnvironment(p_eboctx);
                dbm.createDictionaryFromTable((String[]) tables.toArray(
                        new String[tables.size()]), "default");
                dbm.close();
            }

            for (int i = 0; i < tables.size(); i++)
            {
                String ctable = ((String) tables.get(i)).toUpperCase();
                DataResultSet tmpdic = loadVirtualTable("NGTDIC",
                        "TABLENAME='" + ctable +
                        "' AND OBJECTTYPE IN " + sqlin, 1,
                        99999, "", p_eboctx, p_repository);
                tmpdic.beforeFirst();

                String[] keys =
                {
                    "TABLENAME", "OBJECTNAME", "OBJECTTYPE"
                };
                String[] keysv = new String[4];

                while (tmpdic.next())
                {
                    String objecttype = tmpdic.getString("OBJECTTYPE")
                                              .toUpperCase();

                    if (filter.indexOf("," + objecttype + ",") > -1)
                    {
                        String schema = tmpdic.getString("SCHEMA").toUpperCase();
                        String tablename = tmpdic.getString("TABLENAME")
                                                 .toUpperCase();
                        String objectname = tmpdic.getString("OBJECTNAME")
                                                  .toUpperCase();
                        keysv[0] = tablename;
                        keysv[1] = objectname;
                        keysv[2] = objecttype;

                        if (!(objectname.startsWith("SYS_") ||
                                objectname.equalsIgnoreCase("FULLTEXTCOL")))
                        {
                            if (!node.locatefor(keys, keysv) /*node.locatefor("SCHEMA='"+schema+"' AND TABLENAME='"+tablename+"' AND OBJECTNAME='"+objectname+
                                "' AND OBJECTTYPE='"+objecttype+"'")*/)
                            {
                                p_dropngtdic.moveToInsertRow();
                                copyRowData(tmpdic, p_dropngtdic);
                                p_dropngtdic.insertRow();
                            }
                        }

                        if( ",SYS_APP,SYS_GROUP,SYS_EVERYONE,SYS_ORG,".indexOf( ","+objectname+"," ) != -1 )
                        {
                            p_dropngtdic.moveToInsertRow();
                            copyRowData(tmpdic, p_dropngtdic);
                            p_dropngtdic.insertRow();
                        }
                    }
                }

                node.first();
            }
        }
        catch (Exception e)
        {
            throw new boException(this.getClass().getName() + "DropFields",
                "BO-1307", e);
        }
        finally
        {
            if (dbm != null)
            {
                dbm.close();
            }
        }
    }

    public void dropDuplicates()
    {
        DataResultSet node = p_ngtdic;

        try
        {
            String schema;
            String tablename;
            String objectname;
            String objecttype;
            node.beforeFirst();

            String[] keys = { "SCHEMA", "TABLENAME", "OBJECTNAME", "OBJECTTYPE" };
            String[] keysv = new String[4];

            while (node.next())
            {
                schema = node.getString("SCHEMA").toUpperCase();
                tablename = node.getString("TABLENAME").toUpperCase();
                objectname = node.getString("OBJECTNAME").toUpperCase();
                objecttype = node.getString("OBJECTTYPE").toUpperCase();
                keysv[0] = schema;
                keysv[1] = tablename;
                keysv[2] = objectname;
                keysv[3] = objecttype;

                int idxloc = node.getRow();

                if (node.locatefor(keys, keysv, idxloc) /*node.locatefor("SCHEMA='"+schema+"' AND TABLENAME='"+tablename+"' AND OBJECTNAME='"+objectname+"' AND OBJECTTYPE='"+objecttype+"'",idxloc)*/)
                {
                    node.deleteRow();
                    node.absolute(idxloc - 1);
                }
                else
                {
                    node.absolute(idxloc);
                }
            }
        }
        catch (SQLException e)
        {
        }

        ;
    }

    public void AnalyzeDictionarys()
    {
        if( true ) return;
//        try
//        {
//            DataResultSet node = p_ngtdic;
//            node.beforeFirst();
//
//            Vector tables = new Vector();
//
//            Vector schemas = new Vector();
//            Vector objectnames = new Vector();
//            Vector objecttypes = new Vector();
//
//            while (node.next())
//            {
//                if (tables.indexOf(node.getString("TABLENAME")) == -1)
//                {
//                    tables.add(node.getString("TABLENAME"));
//                }
//
//                if ("T,F".indexOf(node.getString("OBJECTTYPE")) == -1)
//                {
//                    if (schemas.indexOf(node.getString("SCHEMA")) == -1)
//                    {
//                        schemas.add(node.getString("SCHEMA"));
//                    }
//
//                    if (objectnames.indexOf(node.getString("objectname")) == -1)
//                    {
//                        objectnames.add(node.getString("objectname"));
//                    }
//
//                    if (objecttypes.indexOf(node.getString("objecttype")) == -1)
//                    {
//                        objecttypes.add(node.getString("objecttype"));
//                    }
//                }
//            }
//
//            for (int i = 0; i < tables.size(); i++)
//            {
//                String[] keys =
//                {
//                    "SCHEMA", "TABLENAME", "OBJECTNAME", "OBJECTTYPE"
//                };
//                String[] keysv = new String[4];
//
//                String ctable = ((String) tables.get(i)).toUpperCase();
//                DataResultSet tmpdic = loadVirtualTable("NGTDIC",
//                        "TABLENAME='" + ctable + "' AND SCHEMA='"+wksdef[0].toUpperCase()+"'", 1,
//                        99999, "", p_eboctx, p_repository);
//                node.first();
//
//                boolean changed;
//
//                do
//                {
//                    changed = false;
//
//                    String schema = node.getString("SCHEMA").toUpperCase();
//                    String tablename = node.getString("TABLENAME").toUpperCase();
//                    String objectname = node.getString("OBJECTNAME")
//                                            .toUpperCase();
//                    String objecttype = node.getString("OBJECTTYPE")
//                                            .toUpperCase();
//                    keysv[0] = schema;
//                    keysv[1] = tablename;
//                    keysv[2] = objectname;
//                    keysv[3] = objecttype;
//
//                    if (node.getString("TABLENAME").toUpperCase().equals(ctable) &&
//                            tmpdic.locatefor(keys, keysv) /* && tmpdic.locatefor("SCHEMA='"+schema+"' AND TABLENAME='"+tablename+"' AND OBJECTNAME='"+objectname+
//                        "' AND OBJECTTYPE='"+objecttype+"'")*/)
//                    {
//                        if (!changed &&
//                                !compareField(node, tmpdic, "REQUIRED"))
//                        {
//                            changed = true;
//                        }
//
//                        if (!changed &&
//                                !compareField(node, tmpdic, "fieldsize"))
//                        {
//                            changed = true;
//                        }
//
//                        if (!changed &&
//                                !compareField(node, tmpdic, "fieldtype"))
//                        {
//                            changed = true;
//                        }
//
//                        //if(!changed && !compareField(node,tmpdic,"friendlyname")) changed=true;
//                        if (!changed &&
//                                !compareField(node, tmpdic, "expression"))
//                        {
//                            changed = true;
//                        }
//
//                        if (!changed && !compareField(node, tmpdic, "picture"))
//                        {
//                            changed = true;
//                        }
//
//                        if (!changed &&
//                                !compareField(node, tmpdic, "tablereference"))
//                        {
//                            changed = true;
//                        }
//
//                        if (!changed &&
//                                !compareField(node, tmpdic, "fieldreference"))
//                        {
//                            changed = true;
//                        }
//
//                        if (!changed &&
//                                !compareField(node, tmpdic, "macrofield"))
//                        {
//                            changed = true;
//                        }
//
//                        if (!changed &&
//                                !compareField(node, tmpdic, "cachettl"))
//                        {
//                            changed = true;
//                        }
//                    }
//                    else
//                    {
//                        changed = true;
//                    }
//
//                    if (!changed)
//                    {
//                        node.deleteRow();
//                        tmpdic.beforeFirst();
//                    }
//                    else
//                    {
//                        String todebug = "";
//                    }
//                }
//                while ((node.getRowCount() > 0) && node.next());
//            }
//        }
//        catch (Exception e)
//        {
//            throw new boException(this.getClass().getName() +
//                "AnalyzeDictionarys", "BO-1307", e);
//        }
    }

    public static final boolean compareField(ResultSet node, ResultSet node1,
        String fieldname)
    {
        try
        {
            String v1 = node.getString(fieldname);
            String v2 = node1.getString(fieldname);

            if ((v1 == null) && (v2 == null))
            {
                return true;
            }
            else if ((v1 == null) && (v2.length() == 0))
            {
                return true;
            }
            else if ((v2 == null) && (v1.length() == 0))
            {
                return true;
            }
            else if ((v2 == null) || (v1 == null))
            {
                return false;
            }
            else
            {
                return v1.equalsIgnoreCase(v2);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void buildSystemStables()
    {
        this.p_mode = BUILD_ATTRIBUTES;

        // Create a table to handle references of the objects
        addTable("EBO_REFERENCES", "Table with all references of the Objects");
        addField("EBO_REFERENCES", "BOUI", DBTYPE_BOUI, "Object BOUI", true,
            "", "", "");
        addField("EBO_REFERENCES", "ATTRIBUTE", "CHAR(255)",
            "Attribute where the referece exists", true, "", "", "");
        addField("EBO_REFERENCES", "REFBOUI$", DBTYPE_BOUI, "Reference BOUI",
            true, "", "", "");

        if(!doneMigration(this.p_eboctx) || isCleanInstallation(this.p_eboctx))
        {
            addTable("DBFS_FILE", "Table to Store File Objects");
            addField("DBFS_FILE", "ID", "number", "Sequence", true, "", "", "");
            addField("DBFS_FILE", "FILENAME", "CHAR(255)", "File Name", true, "",
                "", "");
            addField("DBFS_FILE", "BINDATA", "BLOB", "Data", false, "", "", "");
            addPrimaryKey("DBFS_FILE", "Primary Key for Table DBFS_FILE",
                "DBFS_FILE", "ID");
        }
        else
        {

        addTable("DBFS_FILE", "Table to Store File Objects");
        addField("DBFS_FILE", "FILENAME", "CHAR(255)", "File Name", true, "",
            "", "");

        //addField("DBFS_FILE","BINDATA","BLOB","Object BOUI",false,"","","");
        addField("DBFS_FILE","KEY", "NUMBER","Chave Unica", true, "",
            "", "");
        addField("DBFS_FILE", "DATECREATE", "DATE", "Reference BOUI", true, "",
            "", "");
        addField("DBFS_FILE", "USERCREATE", DBTYPE_BOUI, "Creation User", true,
            "", "", ""); //new ?? tirar pq já esta na versao??
        addField("DBFS_FILE", "DATEMODIFIED", "DATE", "Reference BOUI", false,
            "", "", ""); //para tirar e por na versao??
        addField("DBFS_FILE", "ID", "number", "Reference BOUI", true, "", "", "");
        addField("DBFS_FILE", "PARENT_ID", "number", "Reference BOUI", true,
            "", "", "");
        addField("DBFS_FILE", "TYPE", "number", "Reference BOUI", true, "", "",
            "");
        addField("DBFS_FILE", "ACTIVE", "number", "Active Version", true, "",
            "", "");
        addField("DBFS_FILE", "STATUS", "number", "File Status", false, "", "",
            ""); //new
        addField("DBFS_FILE", "STATUSUSER", DBTYPE_BOUI, "Status User", false,
            "", "", ""); //new
        addPrimaryKey("DBFS_FILE", "Primary Key 0for Table DBFS_FILE",
            "DBFS_FILE", "ID,FILENAME");

        addTable("DBFS_VERSION", "Table to Store File Objects Version");
        addField("DBFS_VERSION","KEY", "NUMBER","Chave Unica", true, "",
            "", "");
        addField("DBFS_VERSION", "ID", "number", "Reference BOUI", true, "",
            "", "");
        addField("DBFS_VERSION", "VERSION", "number", "Version", true, "", "",
            "");
        addField("DBFS_VERSION", "VERSIONDATE", "DATE", "Version Date", false,
            "", "", "");
        addField("DBFS_VERSION", "VERSIONUSER", DBTYPE_BOUI, "Version User",
            true, "", "", "");
        addField("DBFS_VERSION", "BINDATA", "BLOB", "Data", false, "", "", "");

        //addField("DBFS_VERSION","DATEMODIFIED","DATE","Reference BOUI",false,"","","");
        //addField("DBFS_VERSION","PARENT_ID","number","Reference BOUI",true,"","","");
        addPrimaryKey("DBFS_VERSION", "Primary Key 0for Table DBFS_VERSION",
            "DBFS_VERSION", "ID,VERSION");

        }



        addTable("SYSNGT_SEQUENCES", "Table to Store Transational Sequences");
        addField("SYSNGT_SEQUENCES", "SEQCHAVE", "CHAR(255)", "Sequence key",
            true, "", "", "");
        addField("SYSNGT_SEQUENCES", "COUNTER", "number", "Sequence Number",
            false, "", "", "");
        addPrimaryKey("SYSNGT_SEQUENCES",
            "Primary Key for Table SYSNGT_SEQUENCES", "SYSNGT_SEQUENCES",
            "SEQCHAVE");

        addTable("EBO_TEXTINDEX_QUEUE", "Table to store objects pending of textindex");
        addField("EBO_TEXTINDEX_QUEUE", "ENQUEUETIME", "TIMESTAMP", "Sequence key",
            true, "", "", "");
        addField("EBO_TEXTINDEX_QUEUE", "BOUI", "NUMBER", "Sequence key",
            true, "", "", "");
        addField("EBO_TEXTINDEX_QUEUE", "STATE", "NUMBER(1)", "Sequence key",
            true, "", "", "");
        addField("EBO_TEXTINDEX_QUEUE", "MESSAGE", "CHAR(4000)", "Sequence key",
            true, "", "", "");


        generateScripts(true);
    }

    public void buildSystemViews() throws boRuntimeException
    { //SYS_USER, SYS_ORG, SYS_EVERYONE, SYS_ICN, SYS_APP, SYS_DTCREATE, SYS_DTSAVE, SYS_GROUP

        String schemaParent = (p_repository.getParentRepository() != null)
            ? p_repository.getParentRepository().getSchemaName() : null;
        String schemaParentName = (p_repository.getParentRepository() != null)
            ? p_repository.getParentRepository().getName() : null;
        String schemaName = p_repository.getSchemaName();
        StringBuffer sb = new StringBuffer();

        if(!doneMigration(this.p_eboctx) || isCleanInstallation(this.p_eboctx))
        {
            sb.append("SELECT ")
              .append("\"ID\" AS \"ID\",\"FILENAME\" AS \"FILENAME\",\"BINDATA\" AS \"BINDATA\" FROM DBFS_FILE");
            if (schemaParent != null)
            {
                sb.append("\n\tUNION ALL ").append("SELECT ")
                .append("\"ID\" AS \"ID\",\"FILENAME\" AS \"FILENAME\",\"BINDATA\" AS \"BINDATA\" FROM ").append(schemaParent).append(".DBFS_FILE");
            }
        }
        else
        {
            sb.append("SELECT ")
              .append("\"SYS_ICN\" AS \"SYS_ICN\",\"SYS_USER\" AS \"SYS_USER\", ")
              .append("\"SYS_DTSAVE\" AS \"SYS_DTSAVE\",\"SYS_DTCREATE\" AS \"SYS_DTCREATE\", ")
              .append("\"FILENAME\" AS \"FILENAME\",\"DATECREATE\" AS \"DATECREATE\",\"USERCREATE\"")
              .append(" AS \"USERCREATE\",\"DATEMODIFIED\" AS \"DATEMODIFIED\",\"ID\" AS \"ID\",\"PARENT_ID\"")
              .append(" AS \"PARENT_ID\",\"TYPE\" AS \"TYPE\",\"ACTIVE\" AS \"ACTIVE\",\"STATUS\" AS \"STATUS\", ")
              .append("\"STATUSUSER\" AS \"STATUSUSER\", '" + actualRepositoryName +
                "' AS \"SYS_ORIGIN\" FROM DBFS_FILE ");

            if (schemaParent != null)
            {
                sb.append("\n\tUNION ALL ").append("SELECT ")
                  .append("\"SYS_ICN\" AS \"SYS_ICN\",\"SYS_USER\" AS \"SYS_USER\", ")
                  .append("\"SYS_DTSAVE\" AS \"SYS_DTSAVE\",\"SYS_DTCREATE\" AS \"SYS_DTCREATE\", ")
                  .append("\"FILENAME\" AS \"FILENAME\",\"DATECREATE\" AS \"DATECREATE\",\"USERCREATE\"")
                  .append(" AS \"USERCREATE\",\"DATEMODIFIED\" AS \"DATEMODIFIED\",\"ID\" AS \"ID\",\"PARENT_ID\"")
                  .append(" AS \"PARENT_ID\",\"TYPE\" AS \"TYPE\",\"ACTIVE\" AS \"ACTIVE\",\"STATUS\" AS \"STATUS\", ")
                  .append("\"STATUSUSER\" AS \"STATUSUSER\", '" + schemaParentName +
                    "' AS \"SYS_ORIGIN\" FROM ").append(schemaParent).append(".DBFS_FILE");
            }
        }
        addView("O" + "DBFS_FILE", "System table Union [DBFS_FILE]",
            sb.toString());

/*
        sb.delete(0, sb.length());

        sb.append("SELECT ")
          .append("\"SYS_ICN\" AS \"SYS_ICN\",\"SYS_USER\" AS \"SYS_USER\",\"SYS_ORG\" AS \"SYS_ORG\", ")
          .append("\"SYS_EVERYONE\" AS \"SYS_EVERYONE\",\"SYS_APP\" AS \"SYS_APP\",\"SYS_GROUP\" AS ")
          .append("\"SYS_GROUP\",\"SYS_DTSAVE\" AS \"SYS_DTSAVE\",\"SYS_DTCREATE\" AS \"SYS_DTCREATE\", ")
          .append("\"ID\" AS \"ID\",\"VERSION\" AS \"VERSION\",\"VERSIONDATE\"")
          .append(" AS \"VERSIONDATE\",\"VERSIONUSER\" AS \"VERSIONUSER\",\"BINDATA\" AS \"BINDATA\" ")
          .append(", '" + actualRepositoryName + "' AS \"SYS_ORIGIN\"").append("FROM DBFS_VERSION ");

        if (schemaParent != null)
        {
            sb.append("\n\tUNION ALL ").append("SELECT ")
              .append("\"SYS_ICN\" AS \"SYS_ICN\",\"SYS_USER\" AS \"SYS_USER\",\"SYS_ORG\" AS \"SYS_ORG\", ")
              .append("\"SYS_EVERYONE\" AS \"SYS_EVERYONE\",\"SYS_APP\" AS \"SYS_APP\",\"SYS_GROUP\" AS ")
              .append("\"SYS_GROUP\",\"SYS_DTSAVE\" AS \"SYS_DTSAVE\",\"SYS_DTCREATE\" AS \"SYS_DTCREATE\", ")
              .append("\"ID\" AS \"ID\",\"VERSION\" AS \"VERSION\",\"VERSIONDATE\"")
              .append(" AS \"VERSIONDATE\",\"VERSIONUSER\" AS \"VERSIONUSER\",\"BINDATA\" AS \"BINDATA\"")
              .append(", '" + schemaParentName + "' AS \"SYS_ORIGIN\" FROM ")
              .append(schemaParent).append(".DBFS_VERSION");
        }

        addView("O" + "DBFS_VERSION", "System table Union [DBFS_VERSION]",
            sb.toString());
            */

        sb.delete(0, sb.length());
        sb.append("SELECT ")
          .append("\"SYS_ICN\" AS \"SYS_ICN\",\"SYS_USER\" AS \"SYS_USER\", ")
          .append("\"SYS_DTSAVE\" AS \"SYS_DTSAVE\",\"SYS_DTCREATE\" AS \"SYS_DTCREATE\", ")
          .append("\"BOUI\" AS \"BOUI\",\"ATTRIBUTE\" AS \"ATTRIBUTE\",\"REFBOUI$\"")
          .append(" AS \"REFBOUI$\"").append(", '" + actualRepositoryName +
            "' AS \"SYS_ORIGIN\" FROM EBO_REFERENCES");

        //EBO_REFERENCES VIEW
        if (schemaParent != null)
        {
            sb.append("\n\tUNION ALL ").append("SELECT ")
              .append("\"SYS_ICN\" AS \"SYS_ICN\",\"SYS_USER\" AS \"SYS_USER\", ")
              .append("\"SYS_DTSAVE\" AS \"SYS_DTSAVE\",\"SYS_DTCREATE\" AS \"SYS_DTCREATE\", ")
              .append("\"BOUI\" AS \"BOUI\",\"ATTRIBUTE\" AS \"ATTRIBUTE\",\"REFBOUI$\"")
              .append(" AS \"REFBOUI$\"")
              .append(", '" + schemaParentName + "' AS \"SYS_ORIGIN\" FROM ")
              .append(schemaParent).append(".EBO_REFERENCES");
        }

        addView("O" + "EBO_REFERENCES", "System table Union [EBO_REFERENCES]",
            sb.toString());

        OracleDBM dbm = null;

        try
        {
            dbm = p_repository.getDriver().getDBM();
            dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
            dbm.close();
        }
        catch (Exception e)
        {
            throw new boRuntimeException("boBuildDB.createInheritViews",
                "BO-1304", e);
        }
        finally
        {
            if (dbm != null)
            {
                dbm.close();
            }
        }
    }

    public void buildSystemStablesForPrivateSchema()
    {
        this.p_mode = BUILD_ATTRIBUTES;

        addTable("DBFS_FILE", "Table to Store File Objects");
        addField("DBFS_FILE", "FILENAME", "CHAR(255)", "File Name", true, "",
            "", "");

        //addField("DBFS_FILE","BINDATA","BLOB","Object BOUI",false,"","","");
        addField("DBFS_FILE", "DATECREATE", "DATE", "Reference BOUI", true, "",
            "", "");
        addField("DBFS_FILE", "USERCREATE", DBTYPE_BOUI, "Creation User", true,
            "", "", ""); //new ?? tirar pq já esta na versao??
        addField("DBFS_FILE", "DATEMODIFIED", "DATE", "Reference BOUI", false,
            "", "", ""); //para tirar e por na versao??
        addField("DBFS_FILE", "ID", "number", "Reference BOUI", true, "", "", "");
        addField("DBFS_FILE", "PARENT_ID", "number", "Reference BOUI", true,
            "", "", "");
        addField("DBFS_FILE", "TYPE", "number", "Reference BOUI", true, "", "",
            "");
        addField("DBFS_FILE", "ACTIVE", "number", "Active Version", true, "",
            "", "");
        addField("DBFS_FILE", "STATUS", "number", "File Status", false, "", "",
            ""); //new
        addField("DBFS_FILE", "STATUSUSER", DBTYPE_BOUI, "Status User", false,
            "", "", ""); //new
        addPrimaryKey("DBFS_FILE", "Primary Key 0for Table DBFS_FILE",
            "DBFS_FILE", "ID,FILENAME");

        addTable("DBFS_VERSION", "Table to Store File Objects Version");
        addField("DBFS_VERSION", "ID", "number", "Reference BOUI", true, "",
            "", "");
        addField("DBFS_VERSION", "VERSION", "number", "Version", true, "", "",
            "");
        addField("DBFS_VERSION", "VERSIONDATE", "DATE", "Version Date", false,
            "", "", "");
        addField("DBFS_VERSION", "VERSIONUSER", DBTYPE_BOUI, "Version User",
            true, "", "", "");
        addField("DBFS_VERSION", "BINDATA", "BLOB", "Data", false, "", "", "");

        //addField("DBFS_VERSION","DATEMODIFIED","DATE","Reference BOUI",false,"","","");
        //addField("DBFS_VERSION","PARENT_ID","number","Reference BOUI",true,"","","");
        addPrimaryKey("DBFS_VERSION", "Primary Key 0for Table DBFS_VERSION",
            "DBFS_VERSION", "ID,VERSION");

        generateScripts(true);
    }

    public void buildSystemKeys()
    {
        this.p_mode = BUILD_CONSTRAINTS;
        addIndex("IDX_EBO_REFERENCES", "Index  for BOUI in EboRefrences",
            "EBO_REFERENCES", "BOUI");
        addIndex("IDX_EBO_REFERENCES_REFBOUI",
            "Index for REFBOUI in EboReferences", "EBO_REFERENCES", "REFBOUI$");

        if (p_repository.getParentRepository() != null)
        {
            //Nos repositório filhos não são construídas fk
        }
        else
        {
            addForeignKey("FK_EBO_REFERENCES_BOUI",
                "Foreign key for refereced by objects ", "EBO_REFERENCES",
                "REFBOUI$", "EBO_REGISTRY", "UI$");
            addForeignKey("FK_EBO_REFERENCES_REFBOUI",
                "Foreign key for refececed objects", "EBO_REFERENCES", "BOUI",
                "EBO_REGISTRY", "UI$");
        }

        generateScripts(true);

        // Create Ebo_Template Index
        OracleDBM dbm = p_repository.getDriver().getDBM();
        dbm.createEbo_TemplateIndex( p_eboctx, p_repository.getSchemaName() );
        dbm.close();


    }

    public static String[] getInterfaceTables(boDefHandler def)
    {
        ArrayList tables = new ArrayList();
        ArrayList objectsincluded = new ArrayList();

        if (def.getClassType() == boDefHandler.TYPE_INTERFACE)
        {
            boDefHandler[] defs = boDefHandler.listBoDefinitions();

            for (int i = 0; i < defs.length; i++)
            {
                if (defs[i].getClassType() == boDefHandler.TYPE_CLASS)
                {
                    if (objectsincluded.indexOf(defs[i].getName()) == -1)
                    {
                        if (defs[i].getBoImplements(def.getName()))
                        {
                            tables.add(defs[i].getBoMasterTable());

                            boDefHandler defh = defs[i];

                            while (defh.getBoSuperBo() != null)
                            {
                                defh = boDefHandler.getBoDefinition(defh.getBoSuperBo());

                                if (defh != null)
                                {
                                    int idx;

                                    if ((idx = objectsincluded.indexOf(
                                                    defh.getName())) > -1)
                                    {
                                        tables.remove(boDefHandler.getBoDefinition(
                                                objectsincluded.get(idx)
                                                               .toString()));
                                    }

                                    objectsincluded.add(defh.getName());
                                }
                            }
                        }
                    }
                }
            }
        }

        return (String[]) tables.toArray(new String[tables.size()]);
    }

    private final boolean containsMoreThanOneClass(String tableName)
    {
        boolean ret = false;
        boolean one = false;
        boDefHandler[] alldef = boDefHandler.getAllBoDefinition();
        for (int i = 0; i < alldef.length; i++)
        {
            if(tableName.equalsIgnoreCase( alldef[i].getBoPhisicalMasterTable() ) )
            {
                if( one )
                {
                    ret = true;
                    break;
                }
                one = true;
            }
        }
        return ret;
    }

    private final boolean containsMoreThanOneClass(boDefHandler def)
    {
        boolean ret = false;
        String tableName = def.getBoPhisicalMasterTable();
        String objName = def.getName();
        boDefHandler[] alldef = boDefHandler.getAllBoDefinition();
        for (int i = 0; i < alldef.length; i++)
        {
            if( !objName.equals( alldef[i].getName()) )
            {
                if(tableName.equalsIgnoreCase( alldef[i].getBoPhisicalMasterTable() ) )
                {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

    public String[] getChildTables(boDefHandler def)
    {
        //Vector vsubs = def.getBoAllSubClasses();
        boDefHandler[] subs = def.getTreeSubClasses(true);

        //boDefHandler[] subs = (boDefHandler[]) vsubs.toArray(new boDefHandler[vsubs.size()]);
        ArrayList ht = new ArrayList();

        if (def.getClassType() == boDefHandler.TYPE_CLASS)
        {
            ht.add( getXeoTableName( def ) );
        }

        for (int i = 0; i < subs.length; i++)
        {
            if (subs[i].getClassType() == boDefHandler.TYPE_CLASS)
            {
                String tablename;
//                tablename = subs[i].getBoPhisicalMasterTable();
                tablename = getXeoTableName( subs[i] );

                if (ht.indexOf(tablename) == -1)
                {
                    ht.add(tablename);
                }
            }
        }

        String[] tables = (String[]) ht.toArray(new String[ht.size()]);

        return tables;
    }

    public void createInheritViewsForInterfaces(boDefHandler def)
        throws boRuntimeException
    {
        String[] tables = getInterfaceTables(def);
        DriverUtils dutils = p_repository.getDriver().getDriverUtils();

        if (tables.length > 0)
        {
            Vector commonfields = new Vector();
            boDefAttribute[] atts = def.getAttributesDef();

            for (int i = 0; i < atts.length; i++)
            {
                if (atts[i].getDbIsBinding())
                {
                    if (!((atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                            (atts[i].getMaxOccurs() > 1)))
                    {
                        if(!"PARENT$".equals(atts[i].getDbName()) &&
                            !"PARENTCTX$".equals(atts[i].getDbName()) &&
                            !"TEMPLATE$".equals(atts[i].getDbName())
                        )
                        commonfields.add(atts[i].getDbName());
                    }
                }
            }

            StringBuffer sbfields = new StringBuffer();

            for (int i = 0; i < commonfields.size(); i++)
            {
                if (i > 0)
                {
                    sbfields.append(',').append(' ');
                }
                sbfields.append( '"' );
                sbfields.append( commonfields.get(i) );
                sbfields.append( '"' );
            }

            StringBuffer viewtext = new StringBuffer();

            for (int i = 0; i < tables.length; i++)
            {
                StringBuffer sbselect = new StringBuffer();
                sbselect.append("SELECT ").append(sbfields).append(" FROM ")
                        .append(tables[i]).append(" WHERE ").append(" \"I$")
                        .append( def.getName().toUpperCase() ).append("\"='S'");

                if (i > 0)
                {
                    viewtext.append('\n');
                    viewtext.append(" UNION ALL ");
                }

                viewtext.append(sbselect);
            }

            addView("O" + def.getName(),
                "Union with object [" + def.getBoDescription() +
                "] and all child objects ", viewtext.toString());

            OracleDBM dbm = null;

            try
            {
                AnalyzeDictionarys();
                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
            }
            catch (Exception e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsForInterfaces",
                    "BO-1304", e);
            }
            finally
            {
                if (dbm != null)
                {
                    dbm.close();
                }
            }
        }
    }

    public void createInheritViewsForMandatoryInterfaces(String mandInterface, ArrayList tables)
        throws boRuntimeException
    {
        boDefInterface boDefI = boDefHandler.getInterfaceDefinition(mandInterface);
        if( boDefI==null || boDefI.getInterfaceType() == boDefHandler.INTERFACE_OPTIONAL )
        {
            return;
        }
        boDefAttribute[] intfAtts = boDefI.getImplAttributes();

        //Views para as bridges
        if (boDefI.getBoAttributes() != null)
        {
            boDefAttribute[] attributes = boDefI.getAttributesDef();
            byte typeRelation;
            boBuildDB aux;

            for (int i = 0; i < attributes.length; i++)
            {
                if ((attributes[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                        !attributes[i].getDbIsTabled())
                {
                    if (attributes[i].getMaxOccurs() > 1)
                    {
                        aux = new boBuildDB(p_eboctx);
                        aux.createInheritedBridgeViewForMandatoryInterfaces(boDefI.getName(),
                            attributes[i], tables);
                    }
                }
                else if (attributes[i].getDbIsTabled())
                {
                    aux = new boBuildDB(p_eboctx);
                    aux.createInheritedBridgeTabledView(boDefI.getName(),
                        attributes[i]);
                }
            }
        }
        DriverUtils dutils = p_repository.getDriver().getDriverUtils();
        StringBuffer sb = new StringBuffer("SELECT ");
        boDefHandler boDef = null;
        if(tables.size() > 0)
        {
            boDef = boDefHandler.getBoDefinition((String)tables.get(0));
        }
        if(intfAtts != null)
        {
            boDefAttribute boAtt;
            for (int j = 0; j < intfAtts.length; j++)
            {
                boAtt = boDef.getAttributeRef( intfAtts[j].getName() );
                 if ((((boAtt.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                (boAtt.getRelationType() == boDefAttribute.RELATION_1_TO_1)) ||
                                (boAtt.getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) ||
                                (boAtt.getAtributeType() == boDefAttribute.TYPE_STATEATTRIBUTE)) &&
                                !boAtt.getDbIsTabled())
                {
                    sb.append("\"").append(boAtt.getDbName())
                      .append("\"").append(" AS ").append("\"").append(boAtt.getDbName()).append("\"");
                    sb.append(",\n\t");
                }
            }
        }

        sb.append("\"SYS_ICN\" AS \"SYS_ICN\",\"SYS_USER\" AS \"SYS_USER\"");
        sb.append(" FROM ");

        StringBuffer total = new StringBuffer();
        if (tables!= null && tables.size() > 0)
        {
            for (int i = 0; i < tables.size(); i++)
            {
                total.append(sb)
                     .append("O" + (String)tables.get(i));
                if((i+1) < tables.size())
                {
                    if(i > 0)
                    {
                        total.append(") UNION ALL (");
                    }
                    else
                    {
                        total.append(" UNION ALL (");
                    }
                }
                else if(tables.size() > 1)
                {
                    total.append(")");
                }
            }
        }
        addView("O" + mandInterface,
                "Mandatory Interface view for " + mandInterface +
                "]", total.toString());

        createExtendedViewsForInterfaces(mandInterface, tables);

        OracleDBM dbm = null;

        try
        {
            AnalyzeDictionarys();
            dbm = p_repository.getDriver().getDBM();
            dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
        }
        catch (Exception e)
        {
            throw new boRuntimeException("boBuildDB.createInheritViewsForInterfaces",
                    "BO-1304", e);
        }
        finally
        {
            if (dbm != null)
            {
                dbm.close();
            }
        }
    }

    public void createExtendedViewsForInterfaces(String mandInterface, ArrayList tables)
    {
        Hashtable tablesdef = new Hashtable();
        Hashtable tclasses = new Hashtable();
        boDefHandler[] sdefs = new boDefHandler[tables.size()];

        for (int i = 0; i < tables.size(); i++)
        {
            sdefs[i] = boDefHandler.getBoDefinition((String)tables.get(i));
        }


        for (int i = 0; i < sdefs.length; i++)
        {
            // Create first the starter object attributes;
            boDefHandler cdef= sdefs[i];

            if (cdef.getClassType() == boDefHandler.TYPE_CLASS)
            {
                String tablesrc = cdef.getName();
                ArrayList fields = (ArrayList) tablesdef.get(tablesrc);

                if (fields == null)
                {
                    fields = new ArrayList();
                    tablesdef.put(tablesrc, fields);
                }

                boDefAttribute[] atts = cdef.getBoAttributes();

                for (int z = 0; z < atts.length; z++)
                {
                    if (atts[z].getDbIsBinding())
                    {
                        if ((((atts[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                (atts[z].getRelationType() == boDefAttribute.RELATION_1_TO_1)) ||
                                (atts[z].getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) ||
                                (atts[z].getAtributeType() == boDefAttribute.TYPE_STATEATTRIBUTE)) &&
                                !atts[z].getDbIsTabled())
                        {
                            fields.add(atts[z].getDbName());
                        }
                    }
                }

                ArrayList listclass = (ArrayList) tclasses.get(tablesrc);

                if (listclass == null)
                {
                    tclasses.put(tablesrc, listclass = new ArrayList());
                }

                listclass.add(cdef.getName());
            }
        }

        // Calibrate the fields in all tables;
        ArrayList allfields = new ArrayList();
        Enumeration oEnum = tablesdef.elements();

        while (oEnum.hasMoreElements())
        {
            ArrayList tatts = (ArrayList) oEnum.nextElement();

            for (short i = 0; i < tatts.size(); i++)
            {
                Object cfield = tatts.get(i);

                if (allfields.indexOf(cfield) == -1)
                {
                    allfields.add(cfield);
                }
            }
        }

        Hashtable nwtables = new Hashtable();
        oEnum = tablesdef.keys();

        while (oEnum.hasMoreElements())
        {
            String table = (String) oEnum.nextElement();
            ArrayList fields = (ArrayList) tablesdef.get(table);
            ArrayList nwfields = new ArrayList();
            nwtables.put(table, nwfields);

            for (int i = 0; i < allfields.size(); i++)
            {
                String cfield = (String) allfields.get(i);

                if (fields.indexOf(cfield) > -1)
                {
                    nwfields.add(cfield);
                }
                else
                {
                    nwfields.add(null);
                }
            }
        }

        oEnum = nwtables.keys();

        ArrayList unions = new ArrayList();

        while (oEnum.hasMoreElements())
        {
            String ctable = (String) oEnum.nextElement();
            ArrayList tfields = (ArrayList) nwtables.get(ctable);
            StringBuffer sb = new StringBuffer();

            for (short i = 0; i < allfields.size(); i++)
            {
                if (sb.length() > 0)
                {
                    sb.append(", ");
                }

                String calias = (String) allfields.get(i);

                if ("SYS_ORIGIN".equals(calias))
                {
                    //juntar o tipo da view
                    sb.append("'" + actualRepositoryName +
                        "' AS \"SYS_ORIGIN\"");
                }
                else if (tfields.get(i) == null)
                {
                    sb.append("NULL AS ").append('"').append(calias).append('"');
                }
                else
                {
                    sb.append('"').append(calias).append('"');
                    sb.append(" AS ").append('"').append(calias).append('"');
                }
            }

            StringBuffer xsql = new StringBuffer("SELECT ");

            xsql.append(sb.toString()).append(" \n \t\t FROM  ").append("OE" + ctable);


            unions.add(xsql);
        }

        StringBuffer finalquery = new StringBuffer();

        for (short i = 0; i < unions.size(); i++)
        {
            if (finalquery.length() > 0)
            {
                finalquery.append("\nUNION ALL\n");
            }

            finalquery.append(unions.get(i));
        }

        addView("OE" + mandInterface,
           "Mandatory Interface view for " + mandInterface +
                "] and all child objects, all child field are included ",
            finalquery.toString());
    }
    public void createExtendedViews(boDefHandler def)
    {
        createExtendedViews(def, false);
    }
    public void createExtendedViews(boDefHandler def, boolean special)
    {
        Hashtable tablesdef = new Hashtable();
        Hashtable tclasses = new Hashtable();
        boDefHandler[] sdefs = def.getTreeSubClasses();
        boolean master = false;
        DriverUtils  dutils =  p_repository.getDriver().getDriverUtils();

        for (int i = 0; !master || (i < sdefs.length); i++)
        {
            // Create first the starter object attributes;
            boDefHandler cdef;

            if ((i == 0) && !master)
            {
                cdef = def;
                i--;
                master = true;
            }
            else
            {
                cdef = sdefs[i];
            }

            if (cdef.getClassType() == boDefHandler.TYPE_CLASS)
            {
                String tablesrc = getXeoTableName( cdef );
                ArrayList fields = (ArrayList) tablesdef.get(tablesrc);

                if (fields == null)
                {
                    fields = new ArrayList();
                    tablesdef.put(tablesrc, fields);
                }

                boDefAttribute[] atts = cdef.getBoAttributes();

                for (int z = 0; z < atts.length; z++)
                {
                    if (atts[z].getDbIsBinding())
                    {
                        if ((((atts[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                (atts[z].getRelationType() == boDefAttribute.RELATION_1_TO_1)) ||
                                (atts[z].getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) ||
                                (atts[z].getAtributeType() == boDefAttribute.TYPE_STATEATTRIBUTE)) &&
                                !atts[z].getDbIsTabled())
                        {
                            fields.add(atts[z].getDbName());
                        }
                    }
                }



                ArrayList listclass = (ArrayList) tclasses.get(tablesrc);

                if (listclass == null)
                {
                    tclasses.put(tablesrc, listclass = new ArrayList());
                }

                listclass.add(cdef.getName());
            }
        }

        // Calibrate the fields in all tables;
        ArrayList allfields = new ArrayList();
        Enumeration oEnum = tablesdef.elements();

        while (oEnum.hasMoreElements())
        {
            ArrayList tatts = (ArrayList) oEnum.nextElement();

            for (short i = 0; i < tatts.size(); i++)
            {
                Object cfield = tatts.get(i);

                if (allfields.indexOf(cfield) == -1)
                {
                    allfields.add(cfield);
                }
            }
        }

        Hashtable nwtables = new Hashtable();
        oEnum = tablesdef.keys();

        while (oEnum.hasMoreElements())
        {
            String table = (String) oEnum.nextElement();
            ArrayList fields = (ArrayList) tablesdef.get(table);
            ArrayList nwfields = new ArrayList();
            nwtables.put(table, nwfields);

            for (int i = 0; i < allfields.size(); i++)
            {
                String cfield = (String) allfields.get(i);

                if (fields.indexOf(cfield) > -1)
                {
                    nwfields.add(cfield);
                }
                else
                {
                    nwfields.add(null);
                }
            }
        }

        oEnum = nwtables.keys();

        ArrayList unions = new ArrayList();

        while (oEnum.hasMoreElements())
        {
            String ctable = (String) oEnum.nextElement();
            ArrayList tfields = (ArrayList) nwtables.get(ctable);
            StringBuffer sb = new StringBuffer();

            for (short i = 0; i < allfields.size(); i++)
            {
                if (sb.length() > 0)
                {
                    sb.append(", ");
                }

                String calias = (String) allfields.get(i);

                if ("SYS_ORIGIN".equals(calias))
                {
                    //juntar o tipo da view
                    sb.append("'" + actualRepositoryName +
                        "' AS \"SYS_ORIGIN\"");
                }
                else if (tfields.get(i) == null)
                {
                    sb.append("NULL AS ").append('"').append(calias).append('"');
                }
                else
                {
                    sb.append('"').append(calias).append('"');
                    sb.append(" AS ").append('"').append(calias).append('"');
                }
            }

            StringBuffer xsql = new StringBuffer("SELECT ");

            boolean haveWhere = false;
            boolean putAnd    = false;

            xsql.append(sb.toString()).append(" \n \t\t FROM  ").append(ctable);
            if("Ebo_ClsReg".equals(def.getBoName()) || "Ebo_Package".equals(def.getBoName()))
            {
                if(!special)
                {
                    xsql.append(" WHERE ");
                    haveWhere = true;
                    xsql.append("\"DEPLOYED\" = 1");
                    putAnd = true;
                }
            }

            ArrayList classes = (ArrayList) tclasses.get(ctable);
            if( containsMoreThanOneClass( ctable ) )
            {
                if( classes.size() > 0 )
                {
                    if( !haveWhere  )
                    {
                        xsql.append(" WHERE ");
                    }
                    if( putAnd  )
                    {
                        xsql.append( " AND " );
                    }
                    xsql.append(" ( ");
                    for (short i = 0; i < classes.size(); i++)
                    {
                        if (i > 0)
                        {
                            xsql.append(" OR ");
                        }

                        xsql.append('"').append("CLASSNAME\"='").append(classes.get(i))
                            .append('\'');
                    }
                    xsql.append(" ) ");
                }
            }
            unions.add(xsql);
        }

        StringBuffer finalquery = new StringBuffer();

        for (short i = 0; i < unions.size(); i++)
        {
            if (finalquery.length() > 0)
            {
                finalquery.append("\nUNION ALL\n");
            }

            finalquery.append(unions.get(i));
        }

        addView("OE" + def.getName(),
            "Union with object [" + def.getBoDescription() +
            "] and all child objects, all child field are included ",
            finalquery.toString());
    }

    public void createInheritViews(boDefHandler def) throws boRuntimeException
    {
        createInheritViews(def, false);
    }

    public void createInheritViews(boDefHandler def, boolean special) throws boRuntimeException
    {
        initializePlugIns( def );
        initializeBuilders( false );
        fireEvent( EVENT_BEFORE_INHERIT_VIEWS, def );

        if (def.getClassType() == boDefHandler.TYPE_INTERFACE)
        {
            createInheritViewsForInterfaces(def);
        }

        //Views para as bridges
        if (def.getClassType() == def.TYPE_CLASS)
        {
            boDefAttribute[] attributes = def.getAttributesDef();
            byte typeRelation;
            boBuildDB aux;

            if (def.getClassType() == def.TYPE_CLASS)
            {
                for (int i = 0; i < attributes.length; i++)
                {
                    if ((attributes[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                            !attributes[i].getDbIsTabled())
                    {
                        if (attributes[i].getMaxOccurs() > 1)
                        {
                            aux = new boBuildDB(p_eboctx);
                            aux.createInheritedBridgeView(def.getName(), def.getBoMarkInputType(),
                                attributes[i]);
                        }
                    }
                    else if (attributes[i].getDbIsTabled())
                    {
                        aux = new boBuildDB(p_eboctx);
                        aux.createInheritedBridgeTabledView(def.getName(),
                            attributes[i]);
                    }
                }
            }
        }

        String[] tables = getChildTables(def);

        if (tables.length > 0)
        {
//            boDefObjectDS.ObjectDataSource ds = def.getBoDataSources()
//                                                   .getReaderDataSource();
//            boDefObjectDS.ObjectDS[] ds1 = ds.getDataSources();
            String[] xatts = getObjectAttributes( def );

            // Create the union view
            ArrayList unionquerys = new ArrayList();

            // Create the union with all child tables
            DataSetMetaData basemetadata = null;

            for (int i = 0; i < tables.length; i++)
            {
                String tablename = tables[i];
                StringBuffer sb = new StringBuffer();

                DriverUtils dutils = p_repository.getDriver().getDriverUtils();

                sb.append("SELECT ");

                // Get the base column types to conver unions if necessary.
                for (int z = 0; z < xatts.length; z++)
                {
                    if (z > 0)
                    {
                        sb.append(',');
                    }

                    sb.append('"').append(xatts[z]).append("\"");
                }

                sb.append("\n\t FROM ").append(
                    tables[i]
                );
                sb.append( " WHERE 0=1 " );

                DataSetMetaData metadata = null;

                if (i == 0)
                {
                    basemetadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                    metadata = basemetadata;
                }
                else
                {
                    metadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                }

                // clear the select to get metadata
                sb.delete(0, sb.length());

                sb.append("SELECT ");

                for (int z = 0; z < xatts.length; z++)
                {
                    String att = "\"" + xatts[z] + "\"";

                    if (i > 0)
                    {
                        int baseidx = basemetadata.findColumn(xatts[z]);
                        int curridx = metadata.findColumn(xatts[z]);

                        if (basemetadata.getColumnType(baseidx) != metadata.getColumnType(
                                    curridx))
                        {
                            att = dutils.convert(metadata.getColumnType(curridx),
                                    basemetadata.getColumnType(baseidx), att);
                        }
                    }

                    if (z > 0)
                    {
                        sb.append(',');
                    }

                    if ("SYS_ORIGIN".equals(xatts[z]))
                    {
                        //juntar o tipo da view
                        sb.append("'" + actualRepositoryName +
                            "' AS \"SYS_ORIGIN\"");
                    }
                    else
                    {
                        sb.append(att).append(" AS \"").append(xatts[z]).append('"');
                    }
                }

                sb.append("\n\t FROM ").append(tablename);

                boolean putwhere = true;
                boolean putand   = true;

                if (def.getClassType() == boDefHandler.TYPE_CLASS)
                {
                    if ( tablename.equalsIgnoreCase( getXeoTableName( def ) ) )
                    {
                        if( containsMoreThanOneClass( tablename ) )
                        {
                            sb.append("\n\t\t WHERE CLASSNAME='")
                              .append(def.getName()).append('\'');
                            putwhere = false;
                        }
                        if("Ebo_ClsReg".equals(def.getBoName()) || "Ebo_Package".equals(def.getBoName()))
                        {
                            if(!special)
                            {
                                if( putwhere )
                                {
                                    sb.append("\n\t\t WHERE \"DEPLOYED\" = 1");
                                    putwhere = false;
                                }
                                else
                                {
                                    sb.append(" and \"DEPLOYED\" = 1");
                                }
                            }
                        }
                    }
                }

                boDefHandler[] childsdef = (boDefHandler[]) def.getTreeSubClasses(true);

                //boDefHandler[] childsdef = (boDefHandler[]) def.getBoAllSubClasses()
                //                                               .toArray(new boDefHandler[0]);
                for (int z = 0; z < childsdef.length; z++)
                {
                    if (childsdef[z].getClassType() == boDefHandler.TYPE_CLASS)
                    {
                        if (tablename.equalsIgnoreCase( getXeoTableName( childsdef[z] ) ) )
                        {
                            if (putwhere)
                            {
                                sb.append("\n\t\t WHERE ");
                                putwhere = false;
                            }
                            else
                            {
                                sb.append(" OR ");
                            }

                            sb.append("CLASSNAME='")
                              .append(childsdef[z].getBoName()).append("'");
                        }
                    }
                }

                unionquerys.add(sb.toString());
            }

            // Make the union query
            StringBuffer union = new StringBuffer();

            for (int i = 0; i < unionquerys.size(); i++)
            {
                if (i > 0)
                {
                    union.append("\n\nUNION ALL\n\n");
                }

                union.append(unionquerys.get(i));
            }

            addView("O" + def.getName(),
                "Union with object [" + def.getBoDescription() +
                "] and all child objects ", union.toString());

            // Create Extended Views
            createExtendedViews(def, special);

            OracleDBM dbm = null;

            try
            {

//                AnalyzeDictionarys();

                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
                dbm.close();
            }
            catch (Exception e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViews",
                    "BO-1304", e);
            }
            finally
            {
                if (dbm != null)
                {
                    dbm.close();
                }
            }
        }
        fireEvent( EVENT_AFTER_INHERIT_VIEWS, def );

    }

    //novos métodos referentes a criação de vários esquemas
    public void createInheritViewsForSemiPrivate(boDefHandler def)
        throws boRuntimeException
    {
        boRepository parentRepository = p_repository.getParentRepository();
        String schemaParent = parentRepository.getName();
        StringBuffer sbparentView = new StringBuffer();

        if (def.getClassType() == boDefHandler.TYPE_INTERFACE)
        {
            createInheritViewsForInterfacesForSemiPrivate(def);
        }

        if (def.getClassType() == def.TYPE_CLASS)
        {
            boDefAttribute[] attributes = def.getAttributesDef();
            byte typeRelation;
            boBuildDB aux;

            for (int i = 0; i < attributes.length; i++)
            {
                if ((attributes[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                        !attributes[i].getDbIsTabled())
                {
                    if (attributes[i].getMaxOccurs() > 1)
                    {
                        aux = new boBuildDB(p_eboctx);
                        aux.createInheritedBridgeViewSemiPrivate(def.getName(), def.getBoMarkInputType(),
                            attributes[i]);
                    }
                }
                else if (attributes[i].getDbIsTabled())
                {
                    aux = new boBuildDB(p_eboctx);
                    aux.createInheritedBridgeTabledViewSemiPrivate(def.getName(),
                        attributes[i]);
                }
            }
        }

        String[] tables = getChildTables(def);

        if (tables.length > 0)
        {
//            boDefObjectDS.ObjectDataSource ds = def.getBoDataSources()
//                                                   .getReaderDataSource();
//            boDefObjectDS.ObjectDS[] ds1 = ds.getDataSources();
//            String[] xatts = ds1[0].getRemoteAttributes();
            String[] xatts = getObjectAttributes( def );

            // Create the union view
            ArrayList unionquerys = new ArrayList();

            // Create the union with all child tables
            DataSetMetaData basemetadata = null;

            for (int i = 0; i < tables.length; i++)
            {
                String tablename = tables[i];
                StringBuffer sb = new StringBuffer();
                StringBuffer sbParentUnion = new StringBuffer();

                DriverUtils dutils = p_repository.getDriver().getDriverUtils();

                sb.append("SELECT ");

                // Get the base column types to conver unions if necessary.
                for (int z = 0; z < xatts.length; z++)
                {
                    if (z > 0)
                    {
                        sb.append(',');
                    }

                    sb.append('"').append(xatts[z]).append("\"");
                }

                sb.append("\n\t FROM ").append(tablename);
                sb.append( " WHERE O=1 " );

                DataSetMetaData metadata = null;

                if (i == 0)
                {
                    basemetadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                    metadata = basemetadata;
                }
                else
                {
                    metadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                }

                // clear the select to get metadata
                sb.delete(0, sb.length());

                sb.append("SELECT ");
                sbparentView.append(sb);

                for (int z = 0; z < xatts.length; z++)
                {
                    String att = "\"" + xatts[z] + "\"";

                    if (i > 0)
                    {
                        int baseidx = basemetadata.findColumn(xatts[z]);
                        int curridx = metadata.findColumn(xatts[z]);

                        if (basemetadata.getColumnType(baseidx) != metadata.getColumnType(
                                    curridx))
                        {
                            att = dutils.convert(metadata.getColumnType(curridx),
                                    basemetadata.getColumnType(baseidx), att);
                        }
                    }

                    if (z > 0)
                    {
                        sb.append(',');
                        sbparentView.append(',');
                    }

                    if ("SYS_ORIGIN".equals(xatts[z]))
                    {
                        //juntar o tipo da view
                        sb.append("'" + actualRepositoryName +
                            "' AS \"SYS_ORIGIN\"");
                        sbparentView.append("'" + schemaParent +
                            "' AS \"SYS_ORIGIN\"");
                    }
                    else
                    {
                        sb.append(att).append(" AS \"").append(xatts[z]).append('"');
                        sbparentView.append(att).append(" AS \"")
                                    .append(xatts[z]).append('"');
                    }
                }

                sb.append("\n\t FROM ").append(tablename);

                boolean putwhere = true;

                if (def.getClassType() == boDefHandler.TYPE_CLASS)
                {
                    if (def.getBoPhisicalMasterTable().equalsIgnoreCase(tablename))
                    {
                        if( containsMoreThanOneClass( tablename ) )
                        {
                            putwhere = false;
                            sb.append("\n\t\t WHERE CLASSNAME='")
                              .append(def.getName()).append('\'');
                        }
                    }
                }

                boDefHandler[] childsdef = (boDefHandler[]) def.getTreeSubClasses(true);

                //boDefHandler[] childsdef = (boDefHandler[]) def.getBoAllSubClasses()
                //                                               .toArray(new boDefHandler[0]);
                for (int z = 0; z < childsdef.length; z++)
                {
                    if (childsdef[z].getClassType() == boDefHandler.TYPE_CLASS)
                    {
                        if (childsdef[z].getBoPhisicalMasterTable()
                                            .equalsIgnoreCase(tablename))
                        {
                            if (putwhere)
                            {
                                sb.append("\n\t\t WHERE ");
                                putwhere = false;
                            }
                            else
                            {
                                sb.append(" OR ");
                            }

                            sb.append("CLASSNAME='")
                              .append(childsdef[z].getBoName()).append("'");
                        }
                    }
                }

                unionquerys.add(sb.toString());
            }

            // Make the union query
            StringBuffer union = new StringBuffer();

            for (int i = 0; i < unionquerys.size(); i++)
            {
                if (i > 0)
                {
                    union.append("\n\nUNION ALL\n\n");
                }

                union.append(unionquerys.get(i));
            }

            if (parentRepository != null)
            {
                sbparentView.append("\nFROM ")
                            .append(parentRepository.getSchemaName()).append(".")
                            .append(def.getBoPhisicalMasterTable())
                            .append("\n\t\t WHERE CLASSNAME='")
                            .append(def.getName()).append('\'');

                if (tables.length > 0)
                {
                    union.append("\n\nUNION ALL\n\n");
                }

                union.append(sbparentView);
            }

            addView("O" + def.getName(),
                "Union with object [" + def.getBoDescription() +
                "] and all child objects ", union.toString());

            // Create Extended Views
            createExtendedViewsForSemiPrivate(def);

            OracleDBM dbm = null;

            try
            {
                AnalyzeDictionarys();
                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
            }
            catch (SQLException e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViews",
                    "BO-1304", e);
            }
            catch (Exception e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViews",
                    "BO-1304", e);
            }
            finally
            {
                if (dbm != null)
                {
                    dbm.close();
                }
            }
        }
    }

    public void createInheritedBridgeTabledView(String objectName,
        boDefAttribute def) throws boRuntimeException
    {
        String[] tables = { def.getDbTableName() };

        if (tables.length > 0)
        {
            String[] xatts =
            {
                "SYS_USER", "SYS_ICN",
                "SYS_DTCREATE", "SYS_DTSAVE", def.getDbName(),
                "T$PARENT$"
            };

            // Create the union view
            ArrayList unionquerys = new ArrayList();

            // Create the union with all child tables
            DataSetMetaData basemetadata = null;

            for (int i = 0; i < tables.length; i++)
            {
                String tablename = tables[i];
                StringBuffer sb = new StringBuffer();

                DriverUtils dutils = p_repository.getDriver().getDriverUtils();

                sb.append("SELECT ");

                for (int z = 0; z < xatts.length; z++)
                {
                    if (z > 0)
                    {
                        sb.append(',');
                    }

                    sb.append('"').append(xatts[z]).append("\"");
                }

                sb.append("\n\t FROM ").append(tablename);
                sb.append( " WHERE 0=1 " );

                DataSetMetaData metadata = null;

                if (i == 0)
                {
                    basemetadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                    metadata = basemetadata;
                }
                else
                {
                    metadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                }

                // clear the select to get metadata
                sb.delete(0, sb.length());

                sb.append("SELECT ");

                for (int z = 0; z < xatts.length; z++)
                {
                    String att = "\"" + xatts[z] + "\"";

                    if (i > 0)
                    {
                        int baseidx = basemetadata.findColumn(xatts[z]);
                        int curridx = metadata.findColumn(xatts[z]);

                        if (basemetadata.getColumnType(baseidx) != metadata.getColumnType(
                                    curridx))
                        {
                            att = dutils.convert(metadata.getColumnType(curridx),
                                    basemetadata.getColumnType(baseidx), att);
                        }
                    }

                    if (z > 0)
                    {
                        sb.append(',');
                    }

                    sb.append(att).append(" AS \"").append(xatts[z]).append('"');
                }

                sb.append("\n\t FROM ").append(tablename);
                unionquerys.add(sb.toString());
            }

            // Make the union query
            StringBuffer union = new StringBuffer();

            for (int i = 0; i < unionquerys.size(); i++)
            {
                if (i > 0)
                {
                    union.append("\n\nUNION ALL\n\n");
                }

                union.append(unionquerys.get(i));
            }

            addView(def.getTableName(),
                "Bridge view [" + objectName + "." + def.getName() + "]",
                union.toString());

            OracleDBM dbm = null;

            try
            {
                AnalyzeDictionarys();
                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
            }
            catch (SQLException e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            catch (Exception e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            finally
            {
                if (dbm != null)
                {
                    dbm.close();
                }
            }
        }
    }
    public void createInheritedBridgeViewForMandatoryInterfaces(String mandIntfName, boDefAttribute def, ArrayList tables)
        throws boRuntimeException
    {
        String[] brTables = { def.getBridge().getBoPhisicalMasterTable() };

        if (brTables.length > 0)
        {
            boDefAttribute[] xatts = def.getBridge().getBridgeAttributes();


            // Create the union view
            ArrayList unionquerys = new ArrayList();
            StringBuffer sb = new StringBuffer();
            ArrayList froms = new ArrayList();
            String aux_t;

            for (int i = 0; i < brTables.length; i++)
            {
                String tablename = brTables[i];
                for (int j = 0; j < tables.size(); j++)
                {
                    sb.delete(0, sb.length());
                    aux_t = boDefHandler.getBoDefinition((String)tables.get(j)).getAttributeRef(def.getName()).getBridge().getBoMasterTable();
                    if(!froms.contains(aux_t))
                    {
                        froms.add(aux_t);
                        sb.append("SELECT \"SYS_ICN\" AS \"SYS_ICN\",\"SYS_USER\" AS \"SYS_USER\"," +
                        "\"SYS_DTCREATE\" AS \"SYS_DTCREATE\",\"SYS_DTSAVE\" AS \"SYS_DTSAVE\", "+
                        "\"PARENT$\" AS \"PARENT$\",\"CHILD$\" AS \"CHILD$\"");
                        for (int z = 0; xatts != null && z < xatts.length; z++)
                        {
                            if(z==0)
                            {
                              sb.append(", \"");
                            }
                            else
                            {
                                sb.append("\"");
                            }
                            sb.append( xatts[z].getDbName())
                            .append("\" AS ")
                            .append("\"")
                            .append( xatts[z].getDbName())
                            .append("\"");
                            if(z+1 < xatts.length)
                            {
                                sb.append(", ");
                            }
                        }

                        unionquerys.add(sb.toString() + " FROM " + aux_t);


                    }
                }
            }
            // Make the union query
            StringBuffer union = new StringBuffer();

            for (int i = 0; i < unionquerys.size(); i++)
            {
                if (i > 0)
                {
                    union.append("\n\nUNION ALL\n\n");
                }

                union.append(unionquerys.get(i));
            }
            addView("O" + mandIntfName + "$" + def.getName(),
                "Mandatory Interface view for " + mandIntfName +
                " and bridge" + def.getName() +"]", union.toString());

            OracleDBM dbm = null;

            try
            {
                AnalyzeDictionarys();
                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
            }
            catch (SQLException e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            catch (Exception e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            finally
            {
                if (dbm != null)
                {
                    dbm.close();
                }
            }
        }

    }

    public void createInheritedBridgeView(String objectName, boolean markInputType, boDefAttribute def)
        throws boRuntimeException
    {
        // Se o objecto não tiver bridge não cria view
        if( def.getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE )
        {
            String[] tables = { def.getBridge().getBoPhisicalMasterTable() };

            if (tables.length > 0)
            {
                boDefAttribute[] xatts = def.getBridge().getBridgeAttributes();

                // Create the union view
                ArrayList unionquerys = new ArrayList();

                // Create the union with all child tables
                DataSetMetaData basemetadata = null;

                for (int i = 0; i < tables.length; i++)
                {
                    String tablename = tables[i];
                    StringBuffer sb = new StringBuffer();

                    DriverUtils dutils = new DriverManager(p_eboctx.getApplication()).getDriverUtils(
                            "DATA");

                    sb.append("SELECT ");

                    // Get the base column types to conver unions if necessary.
                    for (int z = 0; z < BRIDGE_COLUMNS.length; z++)
                    {
                        if(markInputType || z < (BRIDGE_COLUMNS.length -1))
                        {
                            if (z > 0)
                            {
                                sb.append(',');
                            }

                            sb.append('"').append(BRIDGE_COLUMNS[z]).append("\"");
                        }
                    }

                    for (int z = 0; z < xatts.length; z++)
                    {
                        sb.append(',').append('"').append(xatts[z].getDbName())
                          .append("\"");
                    }

                    sb.append("\n\t FROM ").append(tablename);
                    sb.append( " WHERE 0=1 " );

                    DataSetMetaData metadata = null;

                    if (i == 0)
                    {
                        basemetadata = dutils.getMetaDataForSelect(p_eboctx,
                                sb.toString(), null);
                        metadata = basemetadata;

                    }
                    else
                    {
                        metadata = dutils.getMetaDataForSelect(p_eboctx,
                                sb.toString(), null);
                    }

                    // clear the select to get metadata
                    sb.delete(0, sb.length());

                    sb.append("SELECT ");

                    for (int z = 0; z < BRIDGE_COLUMNS.length; z++)
                    {
                        if(markInputType || z < (BRIDGE_COLUMNS.length -1))
                        {
                            String att = "\"" + BRIDGE_COLUMNS[z] + "\"";

                            if (i > 0)
                            {
                                int baseidx = basemetadata.findColumn(BRIDGE_COLUMNS[z]);
                                int curridx = metadata.findColumn(BRIDGE_COLUMNS[z]);

                                if (basemetadata.getColumnType(baseidx) != metadata.getColumnType(
                                            curridx))
                                {
                                    att = dutils.convert(metadata.getColumnType(curridx),
                                            basemetadata.getColumnType(baseidx), att);
                                }
                            }

                            if (z > 0)
                            {
                                sb.append(',');
                            }

                            sb.append(att).append(" AS \"").append(BRIDGE_COLUMNS[z])
                              .append('"');
                        }
                    }

                    for (int z = 0; z < xatts.length; z++)
                    {
                        String att = "\"" + xatts[z].getDbName() + "\"";

                        if (i > 0)
                        {
                            int baseidx = basemetadata.findColumn(xatts[z].getDbName());
                            int curridx = metadata.findColumn(xatts[z].getDbName());

                            if (basemetadata.getColumnType(baseidx) != metadata.getColumnType(
                                        curridx))
                            {
                                att = dutils.convert(metadata.getColumnType(curridx),
                                        basemetadata.getColumnType(baseidx), att);
                            }
                        }

                        sb.append(',').append(att).append(" AS \"")
                          .append(xatts[z].getDbName()).append('"');
                    }

                    sb.append("\n\t FROM ").append(tablename);
                    unionquerys.add(sb.toString());
                }

                // Make the union query
                StringBuffer union = new StringBuffer();

                for (int i = 0; i < unionquerys.size(); i++)
                {
                    if (i > 0)
                    {
                        union.append("\n\nUNION ALL\n\n");
                    }

                    union.append(unionquerys.get(i));
                }

                addView(def.getBridge().getBoMasterTable(),
                    "Bridge view [" + objectName + "." + def.getName() + "]",
                    union.toString());

                OracleDBM dbm = null;

                try
                {
                    AnalyzeDictionarys();
                    dbm = p_repository.getDriver().getDBM();
                    dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
                }
                catch (SQLException e)
                {
                    throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                        "BO-1304", e);
                }
                catch (Exception e)
                {
                    throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                        "BO-1304", e);
                }
                finally
                {
                    if (dbm != null)
                    {
                        dbm.close();
                    }
                }
            }
        }
    }

    public void createInheritedBridgeTabledViewSemiPrivate(String objectName,
        boDefAttribute def) throws boRuntimeException
    {
        String[] tables = { def.getDbTableName() };
        boRepository parentRepository = p_repository.getParentRepository();
        String schemaParent = parentRepository.getName();

        if (tables.length > 0)
        {
            String[] xatts =
            {
                "SYS_USER", "SYS_ICN",
                "SYS_DTCREATE", "SYS_DTSAVE", def.getDbName(),
                "T$PARENT$"
            };

            // Create the union view
            ArrayList unionquerys = new ArrayList();

            // Create the union with all child tables
            DataSetMetaData basemetadata = null;
            StringBuffer parentView = new StringBuffer();

            for (int i = 0; i < tables.length; i++)
            {
                String tablename = tables[i];
                StringBuffer sb = new StringBuffer();

                DriverUtils dutils = p_repository.getDriver().getDriverUtils();

                sb.append("SELECT ");

                // Get the base column types to conver unions if necessary.
                for (int z = 0; z < xatts.length; z++)
                {
                    if (z > 0)
                    {
                        sb.append(',');
                    }

                    sb.append('"').append(xatts[z]).append("\"");
                }

                sb.append("\n\t FROM ").append(tablename);

                DataSetMetaData metadata = null;

                if (i == 0)
                {
                    basemetadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                    metadata = basemetadata;
                }
                else
                {
                    metadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                }

                // clear the select to get metadata
                sb.delete(0, sb.length());

                sb.append("SELECT ");
                parentView.append(sb);

                for (int z = 0; z < xatts.length; z++)
                {
                    String att = "\"" + xatts[z] + "\"";

                    if (i > 0)
                    {
                        int baseidx = basemetadata.findColumn(xatts[z]);
                        int curridx = metadata.findColumn(xatts[z]);

                        if (basemetadata.getColumnType(baseidx) != metadata.getColumnType(
                                    curridx))
                        {
                            att = dutils.convert(metadata.getColumnType(curridx),
                                    basemetadata.getColumnType(baseidx), att);
                        }
                    }

                    if (z > 0)
                    {
                        sb.append(',');
                        parentView.append(',');
                    }

                    sb.append(att).append(" AS \"").append(xatts[z]).append('"');
                    parentView.append(att).append(" AS \"").append(xatts[z])
                              .append('"');
                }

                parentView.append("\n\t FROM ");
                parentView.append(parentRepository.getSchemaName()).append(".")
                          .append(tablename);
                sb.append("\n\t FROM ").append(tablename);
                unionquerys.add(sb.toString());
                unionquerys.add(parentView.toString());
            }

            // Make the union query
            StringBuffer union = new StringBuffer();

            for (int i = 0; i < unionquerys.size(); i++)
            {
                if (i > 0)
                {
                    union.append("\n\nUNION ALL\n\n");
                }

                union.append(unionquerys.get(i));
            }

            addView(def.getTableName(),
                "Bridge view [" + objectName + "." + def.getName() + "]",
                union.toString());

            OracleDBM dbm = null;

            try
            {
                AnalyzeDictionarys();
                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
            }
            catch (SQLException e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            catch (Exception e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            finally
            {
                if (dbm != null)
                {
                    dbm.close();
                }
            }
        }
    }

    public void createInheritedBridgeViewSemiPrivate(String objectName, boolean markInputType,
        boDefAttribute def) throws boRuntimeException
    {
        String[] tables = { def.getBridge().getBoPhisicalMasterTable() };
        boRepository parentRepository = p_repository.getParentRepository();
        String schemaParent = parentRepository.getName();

        if (tables.length > 0)
        {
            boDefAttribute[] xatts = def.getBridge().getBridgeAttributes();

            // Create the union view
            ArrayList unionquerys = new ArrayList();

            // Create the union with all child tables
            DataSetMetaData basemetadata = null;
            StringBuffer parentView = new StringBuffer();

            for (int i = 0; i < tables.length; i++)
            {
                String tablename = tables[i];
                StringBuffer sb = new StringBuffer();

                DriverUtils dutils = p_repository.getDriver().getDriverUtils();

                sb.append("SELECT ");

                // Get the base column types to conver unions if necessary.
                for (int z = 0; z < BRIDGE_COLUMNS.length; z++)
                {
                    if(markInputType || z < (BRIDGE_COLUMNS.length -1))
                    {
                        if (z > 0)
                        {
                            sb.append(',');
                        }

                        sb.append('"').append(BRIDGE_COLUMNS[z]).append("\"");
                    }
                }

                for (int z = 0; z < xatts.length; z++)
                {
                    sb.append(',').append('"').append(xatts[z].getDbName())
                      .append("\"");
                }

                sb.append("\n\t FROM ").append(tablename);

                DataSetMetaData metadata = null;

                if (i == 0)
                {
                    basemetadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                    metadata = basemetadata;
                }
                else
                {
                    metadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                }

                // clear the select to get metadata
                sb.delete(0, sb.length());

                sb.append("SELECT ");
                parentView.append(sb);

                for (int z = 0; z < BRIDGE_COLUMNS.length; z++)
                {
                    if(markInputType || z < (BRIDGE_COLUMNS.length -1))
                    {
                        String att = "\"" + BRIDGE_COLUMNS[z] + "\"";

                        if (i > 0)
                        {
                            int baseidx = basemetadata.findColumn(BRIDGE_COLUMNS[z]);
                            int curridx = metadata.findColumn(BRIDGE_COLUMNS[z]);

                            if (basemetadata.getColumnType(baseidx) != metadata.getColumnType(
                                        curridx))
                            {
                                att = dutils.convert(metadata.getColumnType(curridx),
                                        basemetadata.getColumnType(baseidx), att);
                            }
                        }

                        if (z > 0)
                        {
                            sb.append(',');
                            parentView.append(',');
                        }

                        sb.append(att).append(" AS \"").append(BRIDGE_COLUMNS[z])
                          .append('"');
                        parentView.append(att).append(" AS \"")
                                  .append(BRIDGE_COLUMNS[z]).append('"');
                    }
                }


                for (int z = 0; z < xatts.length; z++)
                {
                    String att = "\"" + xatts[z].getDbName() + "\"";

                    if (i > 0)
                    {
                        int baseidx = basemetadata.findColumn(xatts[z].getDbName());
                        int curridx = metadata.findColumn(xatts[z].getDbName());

                        if (basemetadata.getColumnType(baseidx) != metadata.getColumnType(
                                    curridx))
                        {
                            att = dutils.convert(metadata.getColumnType(curridx),
                                    basemetadata.getColumnType(baseidx), att);
                        }
                    }

                    sb.append(',').append(att).append(" AS \"")
                      .append(xatts[z].getDbName()).append('"');
                    parentView.append(',').append(att).append(" AS \"")
                              .append(xatts[z].getDbName()).append('"');
                }

                parentView.append("\n\t FROM ");
                parentView.append(parentRepository.getSchemaName()).append(".")
                          .append(tablename);
                sb.append("\n\t FROM ").append(tablename);
                unionquerys.add(sb.toString());
                unionquerys.add(parentView.toString());
            }

            // Make the union query
            StringBuffer union = new StringBuffer();

            for (int i = 0; i < unionquerys.size(); i++)
            {
                if (i > 0)
                {
                    union.append("\n\nUNION ALL\n\n");
                }

                union.append(unionquerys.get(i));
            }

            addView(def.getBridge().getBoMasterTable(),
                "Bridge view [" + objectName + "." + def.getName() + "]",
                union.toString());

            OracleDBM dbm = null;

            try
            {
                AnalyzeDictionarys();
                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
            }
            catch (SQLException e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            catch (Exception e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            finally
            {
                if (dbm != null)
                {
                    dbm.close();
                }
            }
        }
    }

    public void createInheritedBridgeViewToParent(String objectName, boolean markInputType,
        boDefAttribute def) throws boRuntimeException
    {
        boRepository parentRepository = p_repository.getParentRepository();
        String schemaParent = parentRepository.getName();

        String[] tables = { def.getBridge().getBoPhisicalMasterTable() };

        if (tables.length > 0)
        {
            boDefAttribute[] xatts = def.getBridge().getBridgeAttributes();

            // Create the union view
            ArrayList unionquerys = new ArrayList();

            // Create the union with all child tables
            DataSetMetaData basemetadata = null;

            for (int i = 0; i < tables.length; i++)
            {
                String tablename = tables[i];
                StringBuffer sb = new StringBuffer();

                DriverUtils dutils = p_repository.getDriver().getDriverUtils();

                sb.append("SELECT ");

                // Get the base column types to conver unions if necessary.
                for (int z = 0; z < BRIDGE_COLUMNS.length; z++)
                {
                    if(markInputType || z < (BRIDGE_COLUMNS.length -1))
                    {
                        if (z > 0)
                        {
                            sb.append(',');
                        }

                        sb.append('"').append(BRIDGE_COLUMNS[z]).append("\"");
                    }
                }

                for (int z = 0; z < xatts.length; z++)
                {
                    sb.append(',').append('"').append(xatts[z].getDbName())
                      .append("\"");
                }

                sb.append("\n\t FROM ").append(parentRepository.getSchemaName())
                  .append(".").append(tablename);

                DataSetMetaData metadata = null;

                if (i == 0)
                {
                    basemetadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                    metadata = basemetadata;
                }
                else
                {
                    metadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                }

                // clear the select to get metadata
                sb.delete(0, sb.length());

                sb.append("SELECT ");

                for (int z = 0; z < BRIDGE_COLUMNS.length; z++)
                {
                    if(markInputType || z < BRIDGE_COLUMNS.length -1)
                    {
                        String att = "\"" + BRIDGE_COLUMNS[z] + "\"";

                        if (i > 0)
                        {
                            int baseidx = basemetadata.findColumn(BRIDGE_COLUMNS[z]);
                            int curridx = metadata.findColumn(BRIDGE_COLUMNS[z]);

                            if (basemetadata.getColumnType(baseidx) != metadata.getColumnType(
                                        curridx))
                            {
                                att = dutils.convert(metadata.getColumnType(curridx),
                                        basemetadata.getColumnType(baseidx), att);
                            }
                        }

                        if (z > 0)
                        {
                            sb.append(',');
                        }

                        sb.append(att).append(" AS \"").append(BRIDGE_COLUMNS[z])
                          .append('"');
                    }
                }

                for (int z = 0; z < xatts.length; z++)
                {
                    String att = "\"" + xatts[z].getDbName() + "\"";

                    if (i > 0)
                    {
                        int baseidx = basemetadata.findColumn(xatts[z].getDbName());
                        int curridx = metadata.findColumn(xatts[z].getDbName());

                        if (basemetadata.getColumnType(baseidx) != metadata.getColumnType(
                                    curridx))
                        {
                            att = dutils.convert(metadata.getColumnType(curridx),
                                    basemetadata.getColumnType(baseidx), att);
                        }
                    }

                    sb.append(',').append(att).append(" AS \"")
                      .append(xatts[z].getDbName()).append('"');
                }

                sb.append("\n\t FROM ").append(parentRepository.getSchemaName())
                  .append(".").append(tablename);
                unionquerys.add(sb.toString());
            }

            // Make the union query
            StringBuffer union = new StringBuffer();

            for (int i = 0; i < unionquerys.size(); i++)
            {
                if (i > 0)
                {
                    union.append("\n\nUNION ALL\n\n");
                }

                union.append(unionquerys.get(i));
            }

            addView(def.getBridge().getBoMasterTable(),
                "Bridge view [" + objectName + "." + def.getName() + "]",
                union.toString());

            OracleDBM dbm = null;

            try
            {
                AnalyzeDictionarys();
                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
            }
            catch (SQLException e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            catch (Exception e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            finally
            {
                if (dbm != null)
                {
                    dbm.close();
                }
            }
        }
    }

    public void createInheritedBridgeTabledViewToParent(String objectName,
        boDefAttribute def) throws boRuntimeException
    {
        boRepository parentRepository = p_repository.getParentRepository();
        String schemaParent = parentRepository.getName();

        String[] tables = { def.getDbTableName() };

        if (tables.length > 0)
        {
            String[] xatts =
            {
                "SYS_USER", "SYS_ICN",
                "SYS_DTCREATE", "SYS_DTSAVE", def.getDbName(),
                "T$PARENT$"
            };

            // Create the union view
            ArrayList unionquerys = new ArrayList();

            // Create the union with all child tables
            DataSetMetaData basemetadata = null;

            for (int i = 0; i < tables.length; i++)
            {
                String tablename = tables[i];
                StringBuffer sb = new StringBuffer();

                DriverUtils dutils = p_repository.getDriver().getDriverUtils();

                sb.append("SELECT ");

                for (int z = 0; z < xatts.length; z++)
                {
                    if (z > 0)
                    {
                        sb.append(',');
                    }

                    sb.append('"').append(xatts[z]).append("\"");
                }

                sb.append("\n\t FROM ").append(parentRepository.getSchemaName())
                  .append(".").append(tablename);

                DataSetMetaData metadata = null;

                if (i == 0)
                {
                    basemetadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                    metadata = basemetadata;
                }
                else
                {
                    metadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                }

                // clear the select to get metadata
                sb.delete(0, sb.length());

                sb.append("SELECT ");

                for (int z = 0; z < xatts.length; z++)
                {
                    String att = "\"" + xatts[z] + "\"";

                    if (i > 0)
                    {
                        int baseidx = basemetadata.findColumn(xatts[z]);
                        int curridx = metadata.findColumn(xatts[z]);

                        if (basemetadata.getColumnType(baseidx) != metadata.getColumnType(
                                    curridx))
                        {
                            att = dutils.convert(metadata.getColumnType(curridx),
                                    basemetadata.getColumnType(baseidx), att);
                        }
                    }

                    if (z > 0)
                    {
                        sb.append(',');
                    }

                    sb.append(att).append(" AS \"").append(xatts[z]).append('"');
                }

                sb.append("\n\t FROM ").append(parentRepository.getSchemaName())
                  .append(".").append(tablename);
                unionquerys.add(sb.toString());
            }

            // Make the union query
            StringBuffer union = new StringBuffer();

            for (int i = 0; i < unionquerys.size(); i++)
            {
                if (i > 0)
                {
                    union.append("\n\nUNION ALL\n\n");
                }

                union.append(unionquerys.get(i));
            }

            addView(def.getTableName(),
                "Bridge view [" + objectName + "." + def.getName() + "]",
                union.toString());

            OracleDBM dbm = null;

            try
            {
                AnalyzeDictionarys();
                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
            }
            catch (SQLException e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            catch (Exception e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            finally
            {
                if (dbm != null)
                {
                    dbm.close();
                }
            }
        }
    }

    public void createInheritedViewsToParent(boDefHandler def)
        throws boRuntimeException
    {
        boRepository parentRepository = p_repository.getParentRepository();
        String schemaParent = parentRepository.getName();

        if (def.getClassType() == boDefHandler.TYPE_INTERFACE)
        {
            //createInheritViewsForInterfaces(def);
        }

        if (def.getClassType() == def.TYPE_CLASS)
        {
            boDefAttribute[] attributes = def.getAttributesDef();
            byte typeRelation;
            boBuildDB aux;

            for (int i = 0; i < attributes.length; i++)
            {
                if ((attributes[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                        !attributes[i].getDbIsTabled())
                {
                    if (attributes[i].getMaxOccurs() > 1)
                    {
                        aux = new boBuildDB(p_eboctx);
                        aux.createInheritedBridgeViewToParent(def.getName(), def.getBoMarkInputType(),
                            attributes[i]);
                    }
                }
                else if (attributes[i].getDbIsTabled())
                {
                    aux = new boBuildDB(p_eboctx);
                    aux.createInheritedBridgeTabledViewToParent(def.getName(),
                        attributes[i]);
                }
            }
        }

        String[] tables = getChildTables(def);
        ArrayList privatePart = new ArrayList();
        StringBuffer privateSb = null;

        String selectSave;

        if (tables.length > 0)
        {
//            boDefObjectDS.ObjectDataSource ds = def.getBoDataSources()
//                                                   .getReaderDataSource();
//            boDefObjectDS.ObjectDS[] ds1 = ds.getDataSources();
//            String[] xatts = ds1[0].getRemoteAttributes();
            String[] xatts = getObjectAttributes( def );

            // Create the union view
            ArrayList unionquerys = new ArrayList();

            // Create the union with all child tables
            DataSetMetaData basemetadata = null;

            for (int i = 0; i < tables.length; i++)
            {
                String tablename = tables[i];
                StringBuffer sb = new StringBuffer();

                DriverUtils dutils = p_repository.getDefDriver().getDriverUtils();
                sb.append("SELECT ");

                // Get the base column types to conver unions if necessary.
                for (int z = 0; z < xatts.length; z++)
                {
                    if (z > 0)
                    {
                        sb.append(',');
                    }

                    sb.append('"').append(xatts[z]).append("\"");
                }

                sb.append("\n\t FROM ").append(parentRepository.getSchemaName())
                  .append(".").append(tablename);

                DataSetMetaData metadata = null;

                if (i == 0)
                {
                    basemetadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                    metadata = basemetadata;
                }
                else
                {
                    metadata = dutils.getMetaDataForSelect(p_eboctx,
                            sb.toString(), null);
                }

                // clear the select to get metadata
                sb.delete(0, sb.length());

                sb.append("SELECT ");

                for (int z = 0; z < xatts.length; z++)
                {
                    String att = "\"" + xatts[z] + "\"";

                    if (i > 0)
                    {
                        int baseidx = basemetadata.findColumn(xatts[z]);
                        int curridx = metadata.findColumn(xatts[z]);

                        if (basemetadata.getColumnType(baseidx) != metadata.getColumnType(
                                    curridx))
                        {
                            att = dutils.convert(metadata.getColumnType(curridx),
                                    basemetadata.getColumnType(baseidx), att);
                        }
                    }

                    if (z > 0)
                    {
                        sb.append(',');
                    }

                    if ("SYS_ORIGIN".equals(xatts[z]))
                    {
                        //juntar o tipo da view
                        sb.append("'" + schemaParent + "' AS \"SYS_ORIGIN\"");
                    }
                    else
                    {
                        sb.append(att).append(" AS \"").append(xatts[z]).append('"');
                    }
                }

                selectSave = sb.toString() + "\n\t";
                sb.append("\n\t FROM ").append(parentRepository.getSchemaName())
                  .append(".").append(tablename);

                boolean putwhere = true;

                if (def.getClassType() == boDefHandler.TYPE_CLASS)
                {
                    if (def.getBoPhisicalMasterTable().equalsIgnoreCase(tablename))
                    {
                        if (def.getASPMode() != def.ASP_PRIVATE)
                        {
                            putwhere = false;
                            sb.append("\n\t\t WHERE CLASSNAME='")
                              .append(def.getName()).append('\'');
                        }

                        if ((def.getASPMode() == def.ASP_PRIVATE) ||
                                (def.getASPMode() == def.ASP_SEMI_PRIVATE))
                        {
                            privateSb = new StringBuffer();
                            privateSb.append(selectSave).append(" FROM ")
                                     .append(tablename)
                                     .append(" WHERE CLASSNAME='")
                                     .append(def.getName()).append('\'');
                            privatePart.add(privateSb.toString());
                        }
                    }
                }

                boDefHandler[] childsdef = (boDefHandler[]) def.getTreeSubClasses(true);

                //                boDefHandler[] childsdef = (boDefHandler[]) def.getBoAllSubClasses()
                //                                                               .toArray(new boDefHandler[0]);
                for (int z = 0; z < childsdef.length; z++)
                {
                    if (childsdef[z].getClassType() == boDefHandler.TYPE_CLASS)
                    {
                        if (childsdef[z].getBoPhisicalMasterTable()
                                            .equalsIgnoreCase(tablename))
                        {
                            if (childsdef[z].getASPMode() != def.ASP_PRIVATE)
                            {
                                if (putwhere)
                                {
                                    sb.append("\n\t\t WHERE ");
                                    putwhere = false;
                                }
                                else
                                {
                                    sb.append(" OR ");
                                }

                                sb.append("CLASSNAME='")
                                  .append(childsdef[z].getBoName()).append("'");
                            }

                            if ((childsdef[z].getASPMode() == childsdef[z].ASP_PRIVATE) ||
                                    (childsdef[z].getASPMode() == childsdef[z].ASP_SEMI_PRIVATE))
                            {
                                privateSb = new StringBuffer();
                                privateSb.append(selectSave).append(" FROM ")
                                         .append(tablename)
                                         .append(" WHERE CLASSNAME='")
                                         .append(childsdef[z].getBoName())
                                         .append('\'');
                                privatePart.add(privateSb.toString());
                            }
                        }
                    }
                }

                unionquerys.add(sb.toString());
            }

            for (int i = 0; i < privatePart.size(); i++)
            {
                unionquerys.add(privatePart.get(i));
            }

            // Make the union query
            StringBuffer union = new StringBuffer();

            for (int i = 0; i < unionquerys.size(); i++)
            {
                if (i > 0)
                {
                    union.append("\n\nUNION ALL\n\n");
                }

                union.append(unionquerys.get(i));
            }

            addView("O" + def.getName(),
                "Union with object [" + def.getBoDescription() +
                "] and all child objects ", union.toString());

            // Create Extended Views
            createExtendedViewsToParent(def);

            OracleDBM dbm = null;

            try
            {
                AnalyzeDictionarys();
                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
            }
            catch (SQLException e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            catch (Exception e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsToParent",
                    "BO-1304", e);
            }
            finally
            {
                if (dbm != null)
                {
                    dbm.close();
                }
            }
        }
    }

    public void createInheritViewsForInterfacesForSemiPrivate(boDefHandler def)
        throws boRuntimeException
    {
        boRepository parentRepository = p_repository.getParentRepository();
        String[] tables = getInterfaceTables(def);
        DriverUtils dutils = p_repository.getDriver().getDriverUtils();

        if (tables.length > 0)
        {
            Vector commonfields = new Vector();
            boDefAttribute[] atts = def.getAttributesDef();

            for (int i = 0; i < atts.length; i++)
            {
                if (atts[i].getDbIsBinding())
                {
                    if (!((atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                            (atts[i].getRelationType() == boDefAttribute.NO_RELATION)))
                    {
                        commonfields.add(atts[i].getDbName());
                    }
                }
            }

            StringBuffer sbfields = new StringBuffer();

            for (int i = 0; i < commonfields.size(); i++)
            {
                if (i > 0)
                {
                    sbfields.append(',').append(' ');
                }

                sbfields.append(commonfields.get(i));
            }

            StringBuffer viewtext = new StringBuffer();

            for (int i = 0; i < tables.length; i++)
            {
                StringBuffer sbselect = new StringBuffer();
                sbselect.append("SELECT ").append(sbfields).append(" FROM ")
                        .append(tables[i]).append(" WHERE ").append(" I$")
                        .append(def.getName()).append("='S'");

                if (i > 0)
                {
                    viewtext.append('\n');
                    viewtext.append(" UNION ALL ");
                }

                viewtext.append(sbselect);
            }

            if (parentRepository != null)
            {
                StringBuffer sbparentView = new StringBuffer();
                sbparentView.append("SELECT ").append(sbfields).append(" FROM ")
                            .append(parentRepository.getSchemaName()).append(".")
                            .append("O").append(def.getName());

                if (tables.length > 0)
                {
                    viewtext.append('\n');
                    viewtext.append(" UNION ALL ");
                }

                viewtext.append(sbparentView);
            }

            addView("O" + def.getName(),
                "Union with object [" + def.getBoDescription() +
                "] and all child objects ", viewtext.toString());

            OracleDBM dbm = null;

            try
            {
                AnalyzeDictionarys();
                dbm = p_repository.getDriver().getDBM();
                dbm.createDabaseObjects(p_eboctx, p_ngtdic, "2");
            }
            catch (SQLException e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsForInterfaces",
                    "BO-1304", e);
            }
            catch (Exception e)
            {
                throw new boRuntimeException("boBuildDB.createInheritViewsForInterfaces",
                    "BO-1304", e);
            }
            finally
            {
                if (dbm != null)
                {
                    dbm.close();
                }
            }
        }
    }

    public void createExtendedViewsForSemiPrivate(boDefHandler def)
    {
        boRepository parentRepository = p_repository.getParentRepository();
        String schemaParent = parentRepository.getName();
        StringBuffer sbparentView = new StringBuffer();
        Hashtable tablesdef = new Hashtable();
        Hashtable tclasses = new Hashtable();
        boDefHandler[] sdefs = def.getTreeSubClasses();

        //        boDefHandler[] sdefs = def.getBoAllSubClassesArray();
        boolean master = false;

        for (int i = 0; !master || (i < sdefs.length); i++)
        {
            // Create first the starter object attributes;
            boDefHandler cdef;

            if ((i == 0) && !master)
            {
                cdef = def;
                i--;
                master = true;
            }
            else
            {
                cdef = sdefs[i];
            }

            if (cdef.getClassType() == boDefHandler.TYPE_CLASS)
            {
                String tablesrc = cdef.getBoPhisicalMasterTable();
                ArrayList fields = (ArrayList) tablesdef.get(tablesrc);

                if (fields == null)
                {
                    fields = new ArrayList();
                    tablesdef.put(tablesrc, fields);
                }

                boDefAttribute[] atts = cdef.getBoAttributes();

                for (int z = 0; z < atts.length; z++)
                {
                    if (atts[z].getDbIsBinding())
                    {
                        if ((((atts[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                (atts[z].getRelationType() == boDefAttribute.RELATION_1_TO_1)) ||
                                (atts[z].getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) ||
                                (atts[z].getAtributeType() == boDefAttribute.TYPE_STATEATTRIBUTE)) &&
                                !atts[z].getDbIsTabled())
                        {
                            fields.add(atts[z].getDbName());
                        }
                    }
                }

                ArrayList listclass = (ArrayList) tclasses.get(tablesrc);

                if (listclass == null)
                {
                    tclasses.put(tablesrc, listclass = new ArrayList());
                }

                listclass.add(cdef.getName());
            }
        }

        // Calibrate the fields in all tables;
        ArrayList allfields = new ArrayList();
        Enumeration oEnum = tablesdef.elements();

        while (oEnum.hasMoreElements())
        {
            ArrayList tatts = (ArrayList) oEnum.nextElement();

            for (short i = 0; i < tatts.size(); i++)
            {
                Object cfield = tatts.get(i);

                if (allfields.indexOf(cfield) == -1)
                {
                    allfields.add(cfield);
                }
            }
        }

        Hashtable nwtables = new Hashtable();
        oEnum = tablesdef.keys();

        while (oEnum.hasMoreElements())
        {
            String table = (String) oEnum.nextElement();
            ArrayList fields = (ArrayList) tablesdef.get(table);
            ArrayList nwfields = new ArrayList();
            nwtables.put(table, nwfields);

            for (int i = 0; i < allfields.size(); i++)
            {
                String cfield = (String) allfields.get(i);

                if (fields.indexOf(cfield) > -1)
                {
                    nwfields.add(cfield);
                }
                else
                {
                    nwfields.add(null);
                }
            }
        }

        oEnum = nwtables.keys();

        ArrayList unions = new ArrayList();

        while (oEnum.hasMoreElements())
        {
            String ctable = (String) oEnum.nextElement();
            ArrayList tfields = (ArrayList) nwtables.get(ctable);
            StringBuffer sb = new StringBuffer();
            StringBuffer parentView = new StringBuffer();

            for (short i = 0; i < allfields.size(); i++)
            {
                if (sb.length() > 0)
                {
                    sb.append(", ");
                    parentView.append(", ");
                }

                String calias = (String) allfields.get(i);

                if (tfields.get(i) == null)
                {
                    sb.append("NULL AS ").append('"').append(calias).append('"');
                    parentView.append("NULL AS ").append('"').append(calias)
                              .append('"');
                }
                else
                {
                    if ("SYS_ORIGIN".equals(calias))
                    {
                        //juntar o tipo da view
                        sb.append("'" + actualRepositoryName +
                            "' AS \"SYS_ORIGIN\"");
                        parentView.append("'" + schemaParent +
                            "' AS \"SYS_ORIGIN\"");
                    }
                    else
                    {
                        sb.append('"').append(calias).append('"');
                        parentView.append('"').append(calias).append('"');
                        sb.append(" AS ").append('"').append(calias).append('"');
                        parentView.append(" AS ").append('"').append(calias)
                                  .append('"');
                    }
                }
            }

            StringBuffer xsql = new StringBuffer("SELECT ");
            sbparentView.append("SELECT ").append(parentView.toString());
            xsql.append(sb.toString()).append(" \n \t\t FROM  ").append(ctable);
            xsql.append(" WHERE ");

            ArrayList classes = (ArrayList) tclasses.get(ctable);

            for (short i = 0; i < classes.size(); i++)
            {
                if (i > 0)
                {
                    xsql.append(" OR ");
                }

                xsql.append('"').append("CLASSNAME\"='").append(classes.get(i))
                    .append('\'');
            }

            unions.add(xsql);
        }

        StringBuffer finalquery = new StringBuffer();

        for (short i = 0; i < unions.size(); i++)
        {
            if (finalquery.length() > 0)
            {
                finalquery.append("\nUNION ALL\n");
            }

            finalquery.append(unions.get(i));
        }

        if (parentRepository != null)
        {
            sbparentView.append("\nFROM ")
                        .append(parentRepository.getSchemaName()).append(".")
                        .append(def.getBoPhisicalMasterTable())
                        .append("\n\t\t WHERE CLASSNAME='").append(def.getName())
                        .append('\'');

            if (finalquery.length() > 0)
            {
                finalquery.append("\nUNION ALL\n");
            }

            finalquery.append(sbparentView);
        }

        addView("OE" + def.getName(),
            "Union with object [" + def.getBoDescription() +
            "] and all child objects, all child field are included ",
            finalquery.toString());
    }

    public void createExtendedViewsToParent(boDefHandler def)
    {
        Hashtable tablesdef = new Hashtable();
        Hashtable tclasses = new Hashtable();
        Hashtable asp = new Hashtable();
        boDefHandler[] sdefs = def.getTreeSubClasses();

        //        boDefHandler[] sdefs = def.getBoAllSubClassesArray();
        boRepository parentRepository = p_repository.getParentRepository();
        String schemaParent = parentRepository.getName();
        boolean master = false;

        for (int i = 0; !master || (i < sdefs.length); i++)
        {
            // Create first the starter object attributes;
            boDefHandler cdef;

            if ((i == 0) && !master)
            {
                cdef = def;
                i--;
                master = true;
            }
            else
            {
                cdef = sdefs[i];
            }

            if (cdef.getClassType() == boDefHandler.TYPE_CLASS)
            {
                String tablesrc = cdef.getBoPhisicalMasterTable();
                ArrayList fields = (ArrayList) tablesdef.get(tablesrc);

                if (fields == null)
                {
                    fields = new ArrayList();
                    tablesdef.put(tablesrc, fields);
                }

                boDefAttribute[] atts = cdef.getBoAttributes();

                for (int z = 0; z < atts.length; z++)
                {
                    if (atts[z].getDbIsBinding())
                    {
                        if ((((atts[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                (atts[z].getRelationType() == boDefAttribute.RELATION_1_TO_1)) ||
                                (atts[z].getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) ||
                                (atts[z].getAtributeType() == boDefAttribute.TYPE_STATEATTRIBUTE)) &&
                                !atts[z].getDbIsTabled())
                        {
                            fields.add(atts[z].getDbName());
                        }
                    }
                }

                ArrayList listclass = (ArrayList) tclasses.get(tablesrc);

                if (listclass == null)
                {
                    tclasses.put(tablesrc, listclass = new ArrayList());
                }

                listclass.add(cdef.getName());

                if ((cdef.getASPMode() == cdef.ASP_PRIVATE) ||
                        (cdef.getASPMode() == cdef.ASP_SEMI_PRIVATE))
                {
                    asp.put(cdef.getName() + "ASP", "PRIVATE");
                }

                if (cdef.getASPMode() == cdef.ASP_SEMI_PRIVATE)
                {
                    asp.put(cdef.getName() + "ASP", "SEMIPRIVATE");
                }
            }
        }

        // Calibrate the fields in all tables;
        ArrayList allfields = new ArrayList();
        Enumeration oEnum = tablesdef.elements();

        while (oEnum.hasMoreElements())
        {
            ArrayList tatts = (ArrayList) oEnum.nextElement();

            for (short i = 0; i < tatts.size(); i++)
            {
                Object cfield = tatts.get(i);

                if (allfields.indexOf(cfield) == -1)
                {
                    allfields.add(cfield);
                }
            }
        }

        Hashtable nwtables = new Hashtable();
        oEnum = tablesdef.keys();

        while (oEnum.hasMoreElements())
        {
            String table = (String) oEnum.nextElement();
            ArrayList fields = (ArrayList) tablesdef.get(table);
            ArrayList nwfields = new ArrayList();
            nwtables.put(table, nwfields);

            for (int i = 0; i < allfields.size(); i++)
            {
                String cfield = (String) allfields.get(i);

                if (fields.indexOf(cfield) > -1)
                {
                    nwfields.add(cfield);
                }
                else
                {
                    nwfields.add(null);
                }
            }
        }

        oEnum = nwtables.keys();

        ArrayList unions = new ArrayList();

        while (oEnum.hasMoreElements())
        {
            String ctable = (String) oEnum.nextElement();
            ArrayList tfields = (ArrayList) nwtables.get(ctable);
            StringBuffer sb = new StringBuffer();

            for (short i = 0; i < allfields.size(); i++)
            {
                if (sb.length() > 0)
                {
                    sb.append(", ");
                }

                String calias = (String) allfields.get(i);

                if (tfields.get(i) == null)
                {
                    sb.append("NULL AS ").append('"').append(calias).append('"');
                }
                else
                {
                    if ("SYS_ORIGIN".equals(calias))
                    {
                        //juntar o tipo da view
                        sb.append("'" + schemaParent + "' AS \"SYS_ORIGIN\"");
                    }
                    else
                    {
                        sb.append('"').append(calias).append('"');
                        sb.append(" AS ").append('"').append(calias).append('"');
                    }
                }
            }

            StringBuffer xsql = new StringBuffer("SELECT ");
            xsql.append(sb.toString()).append(" \n \t\t FROM  ")
                .append(parentRepository.getSchemaName()).append(".").append(ctable);
            xsql.append(" WHERE ");

            ArrayList classes = (ArrayList) tclasses.get(ctable);
            ArrayList privateArr = new ArrayList();
            StringBuffer privateSb;

            for (short i = 0; i < classes.size(); i++)
            {
                if (!"PRIVATE".equals(asp.get(classes.get(i) + "ASP")))
                {
                    if (i > 0)
                    {
                        xsql.append(" OR ");
                    }

                    xsql.append('"').append("CLASSNAME\"='")
                        .append(classes.get(i)).append('\'');
                }

                if ("PRIVATE".equals(asp.get(classes.get(i) + "ASP")) ||
                        "SEMIPRIVATE".equals(asp.get(classes.get(i) + "ASP")))
                {
                    privateSb = new StringBuffer();
                    privateSb.append("SELECT ").append(sb.toString())
                             .append(" \n \t\t FROM  ").append(ctable)
                             .append(" where ").append('"')
                             .append("CLASSNAME\"='").append(classes.get(i))
                             .append('\'');
                    privateArr.add(privateSb.toString());
                }
            }

            unions.add(xsql);

            for (int i = 0; i < privateArr.size(); i++)
            {
                unions.add(privateArr.get(i));
            }
        }

        StringBuffer finalquery = new StringBuffer();

        for (short i = 0; i < unions.size(); i++)
        {
            if (finalquery.length() > 0)
            {
                finalquery.append("\nUNION ALL\n");
            }

            finalquery.append(unions.get(i));
        }

        addView("OE" + def.getName(),
            "Union with object [" + def.getBoDescription() +
            "] and all child objects, all child field are included ",
            finalquery.toString());
    }

    public void setGrantsOnParent() throws SQLException
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection parentcon = null;
        OracleDBM odbm = null;
        boBuildDB dbd;

        try
        {
            boRepository parentRepository = p_repository.getParentRepository();

            if (parentRepository != null)
            {
                parentcon = parentRepository.getDedicatedConnectionDef();
                ps = parentcon.prepareStatement(
                        "select tablename from ngtdic where objecttype = 'V' or objecttype = 'T'");
                rs = ps.executeQuery();
                odbm = p_repository.getDriver().getDBM();
                odbm.setEnvironment(p_eboctx);

                String dml;

                while (rs.next())
                {
                    logger.finest("Setting grants on table (" + rs.getString(1) + ")");
                    dml = "grant select, insert, update, delete, references on " +
                        rs.getString(1) + " to " + p_repository.getUserName();
                    odbm.executeDDL(dml, parentRepository.getName());
                }
            }
        }
        finally
        {
            if (odbm != null)
            {
                odbm.close();
            }

            try
            {
                if (rs != null)
                {
                    rs.close();
                }
            }
            catch (Exception e)
            {
                //ignore
            }

            try
            {
                if (ps != null)
                {
                    ps.close();
                }
            }
            catch (Exception e)
            {
                //ignore
            }

            try
            {
                if (parentcon != null)
                {
                    parentcon.close();
                }
            }
            catch (Exception e)
            {
                //ignore
            }
        }
    }

    public void copyRowData(ResultSet orig, ResultSet dest)
    {
        try
        {
            int colcnt = orig.getMetaData().getColumnCount();

            for (int i = 1; i <= colcnt; i++)
            {
                dest.updateObject(i, orig.getObject(i));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static class MigrationInformation
    {
        public static final byte TYPE_MOVE_COLUMN = 0;
        public static final byte TYPE_MOVE_COLUMN_TO_TABLE = 1;
        public static final byte TYPE_MOVE_TABLE_TO_COLUMN = 1;
        public byte migtype;
        public String srctable;
        public String desttable;
        public String dicquery;
        public String srcrelfield;
        public String destrelfield;
        public String srcfield;
        public String destfield;

        public void moveColumn(String srcTable, String srcField,
            String destField, String dicQuery)
        {
            this.srcfield = srcField.toUpperCase();
            this.destfield = destField.toUpperCase();
            this.srctable = srcTable.toUpperCase();
            this.desttable = srcTable.toUpperCase();
            this.migtype = TYPE_MOVE_COLUMN;
            this.dicquery = dicQuery;
        }

        public void moveColumnToTable(String srcTable, String destTable,
            String srcField, String destField, String srcRelField,
            String destRelField, String dicQuery)
        {
            this.srcfield = srcField.toUpperCase();
            this.destfield = destField.toUpperCase();
            this.srctable = srcTable.toUpperCase();
            this.desttable = destTable.toUpperCase();
            this.srcrelfield = srcRelField.toUpperCase();
            this.destrelfield = destRelField.toUpperCase();
            this.migtype = TYPE_MOVE_COLUMN_TO_TABLE;
            this.dicquery = dicQuery;
        }
    }
    public Hashtable getLocalAttributes()
    {
        return p_localatts;
    }

    public String getTableName()
    {
        return p_tablename;
    }

    public Hashtable getExternalAttributes()
    {
        return p_externalatts;
    }

    private final void fireEventAddViewFields( ArrayList fields, boDefHandler def )
    {
        for (int i = 0; i < p_builders.length; i++ )
        {
            p_builders[i].addViewFields( fields, def );
        }
    }


    private final void fireEvent( int eventType, boDefHandler def )
    {
        for (int i = 0; i < p_builders.length; i++ )
        {
            switch ( eventType )
            {
                case EVENT_BEFORE_DATABASE_SCRIPTS:
                    p_builders[i].beforeDataBaseScripts( def );
                    break;
                case EVENT_AFTER_DATABASE_SCRIPTS:
                    p_builders[i].afterDataBaseScript( def );
                    break;
                case EVENT_BUILD_INHERIT:
                    p_builders[i].inheritObject( def );
                    break;
                case EVENT_BEFORE_INHERIT_VIEWS:
                    p_builders[i].beforeInheritViewes( def );
                    break;
                case EVENT_AFTER_INHERIT_VIEWS:
                    p_builders[i].afterInheritViewes( def );
                    break;
                default:
                    // Erro Evento não definido.
            }
        }
    }

    public void initializeBuilders( boolean createdFwdMethods )
    {
        for (int i = 0; i < p_builders.length; i++)
        {
            p_builders[i].initialize( p_eboctx, this, p_repository, p_mode, p_objectInterfaceMap, createdFwdMethods );
        }

    }

    public DataSet getNgtDicDataSet()
    {
        return p_ngtdic.getDataSet();
    }

    public String[] getObjectAttributes( boDefHandler bodef )
    {
        boDefAttribute[] atts = bodef.getBoAttributes();
        int i = 0;

        ArrayList flds = new ArrayList();
        flds.add("SYS_ICN");
        flds.add("SYS_USER");
        if(bodef.getBoMarkInputType())
            flds.add("SYS_FLDINPUTTYPE");

        // Se o objecto for orfão então cria adiciona o atributo LIN
        // porque a bridge é feita directamente na tabela do objecto orfão
//        if( !bodef.getBoCanBeOrphan() )
//        {
//            flds.add("LIN");
//        }


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

        if (bodef.getBoClsState() != null)
        {
            boDefClsState[] satt = bodef.getBoClsState()
                                          .getChildStateAttributes();

            for (i = 0; i < satt.length; i++)
            {
                if (flds.indexOf(satt[i].getDbName()) == -1)
                {
                    flds.add(satt[i].getDbName());
                }
            }
        }

        fireEventAddViewFields( flds, bodef );

        return (String[])flds.toArray( new String[ flds.size() ] );
    }


    public String getDataTableName( boDefHandler defh )
    {
        String ret_tablename = defh.getBoPhisicalMasterTable();
//        String ori_tablename = defh.getBoPhisicalMasterTable();
//        boDefHandler[] def = boDefHandler.listBoDefinitions();
//        for (int d = 0; d < def.length; d++)
//        {
//            if( def[d].getBoPhisicalMasterTable().equals( ori_tablename )  )
//            {
                IDataPlugin[] plugin = DataPluginManager.getPlugIns();
                for (int i = 0; i < plugin.length; i++)
                {
                    String otable = plugin[i].getDataTableName( this.p_repository ,defh );
                    if( otable != null )
                    {
                        ret_tablename = otable;
                    }
                }
//            }
//        }
        return ret_tablename;
    }


    public String getXeoTableName( boDefHandler defh )
    {
        String ret_tablename = defh.getBoPhisicalMasterTable();
//        String ori_tablename = defh.getBoPhisicalMasterTable();
//        boDefHandler[] def = boDefHandler.listBoDefinitions();
//        for (int d = 0; d < def.length; d++)
//        {
//            if( def[d].getBoPhisicalMasterTable().equals( ori_tablename )  )
//            {
                IDataPlugin[] plugin = DataPluginManager.getPlugIns();
                for (int i = 0; i < plugin.length; i++)
                {
                    String otable = plugin[i].getXeoTableName( defh );
                    if( otable != null )
                    {
                        ret_tablename = otable;
                    }
                }
//            }
//        }
        return ret_tablename;
    }

    /**
     *@deprecated to be removed after all dbs_file migration
     */
    private boolean doneMigration(EboContext context)
    {
        boolean result = false;
        Connection cn = null;
        try
        {
            cn = context.getConnectionData();
            result =  execute(cn,"SELECT type FROM dbfs_file",false);
        }
        catch (SQLException e)
        {
            //ignore
        }
        finally
        {
            try
            {
                if(cn != null)cn.close();
            }
            catch (Exception e)
            {

            }
        }
        return result;
    }
    /**
     *@deprecated to be removed after all dbs_file migration
     */
    private boolean isCleanInstallation(EboContext context)
    {
        boolean result = false;
        int count = 0;
        PreparedStatement pstm = null;
        ResultSet r = null;
        try
        {
            pstm = context.getConnectionData().prepareStatement("SELECT COUNT(*) FROM dbfs_file");
            r = pstm.executeQuery();
            r.next();
            count = r.getInt(1);
            if(count == 0)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            //ignore
        }
        finally
        {
            try
            {
                if(r != null) r.close();
                if(pstm != null) pstm.close();
            }
            catch (SQLException e)
            {
            }
        }
        return result;
    }
    /**
     *@deprecated to be removed after all dbs_file migration
     */
    private boolean execute(Connection connection,String sql,boolean required) throws SQLException
    {
        boolean result = false;
        PreparedStatement pstm = null;
        try
        {
            pstm = connection.prepareStatement(sql);
            pstm.execute();
            result = true;
        }
        catch (SQLException e)
        {
            if(required)
            {
                throw e;
            }
        }
        finally
        {
            try
            {
                if(pstm != null) pstm.close();
            }
            catch (SQLException e)
            {
                throw e;
            }
        }
        return result;
    }

    public boolean addForeignKey( boDefAttribute catt, String tableName )
    {
        return addForeignKey( catt, tableName, catt.getDbName() );
    }

    public boolean addForeignKey( boDefAttribute catt, String tableName, String localFields )
    {
        boolean ret = false;
        if( this.p_mode == boBuildDB.BUILD_CONSTRAINTS )
        {
            boDefHandler refDef = catt.getReferencedObjectDef();
            if( catt.getDbCreateConstraints() )
            {
                if( refDef != null )
                {
                    if( !"boObject".equalsIgnoreCase( refDef.getName() ) )
                    {
                        if( refDef.getClassType() == boDefHandler.TYPE_CLASS )
                        {
                            if( getChildTables( refDef ).length == 1 )
                            {
                                ret = true;
                                addForeignKey("FK" +
                                    catt.getDbName() + tableName,
                                    "Foreign Key for ", tableName,
                                    localFields,
                                    getTableForConstraints(
                                        catt.getReferencedObjectDef()),
                                    "BOUI");
                                addIndex("IDX_FK_" + catt.getDbName() + tableName,
                                    "Index FK for [" + tableName + "] "+catt.getDbName(), tableName,
                                    localFields );
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }



}
