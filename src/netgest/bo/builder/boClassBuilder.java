/*Enconding=UTF-8*/
package netgest.bo.builder;
import netgest.bo.def.*;
import netgest.bo.*;
import java.io.*;
import java.sql.*;
import netgest.bo.parser.CodeJavaConstructor;
import netgest.bo.parser.beautifier.CallBeautifier;
import netgest.utils.*;
import com.ibm.regex.*;
import java.util.*;
import netgest.bo.runtime.*;
import netgest.bo.def.boDefViewer;

import org.apache.log4j.Logger;

public class boClassBuilder
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.builder.boClassBuilder");

    protected static final byte TYPE_OBJECT=0;
    protected static final byte TYPE_SIMPLE=1;
    protected static final byte TYPE_DATA=2;

    protected static final int LINELENGTH = 80;
    protected static final int SPACENUM = 4;
    protected static final int MAXINDENT = 4;
    protected static final boolean BRACKETINDENT = true;
    protected static final boolean SWITCHINDENT = true;
    protected static final boolean TABINDENT = false;
    protected static final int COMPILE_MAX = 50;
    private Hashtable codeJavaHash;

/*    private static Vector compileQueue = new Vector();
    public static void compileClasses(boConfig bocfg) {
        try {
            boClassCompiler comp = new boClassCompiler();
            comp.compile(bocfg.getDeploymentsrcdir(),(File[])compileQueue.toArray(new File[compileQueue.size()]),bocfg.getDeploymentclassdir());
        } catch (SQLException e) {
            throw new boException(boClassBuilder.class.getName()+"compileClasses(String)","BO-1601",e,"<any>");
        }
        compileQueue.clear();
    }*/
    public boClassBuilder() {
    }
    public void build(boDefHandler[] bodefs,long[] clsregboui) throws boRuntimeException {
        boDefHandler bodef=null;
        try {
            Vector srcfiles = new Vector();
            boConfig bocfg = new boConfig();
            for(int i=0;i < bodefs.length;i++) {
                bodef = bodefs[i];

                if( bodef.getClassType() != boDefHandler.TYPE_INTERFACE )
                {
                    String p_srcdir = bocfg.getDeploymentsrcdir();
                    FileReader fr = new FileReader(bocfg.getTemplatesDir()+"boTemplate.java");

                    StringBuffer sbtemp = new StringBuffer();

                    char[] cbuff = new char[4096];
                    int br;
                    while((br=fr.read(cbuff))>0) {
                        sbtemp.append(cbuff,0,br);
                    }
                    fr.close();
                    String srctemp = generateSrcFile(sbtemp.toString(),bodef,""+clsregboui);

                    String version="v"+bodef.getBoVersion();
                    version = version.replace('.','_');
                    File srcdir = new File(bocfg.getDeploymentsrcdir()+version);
                    srcdir.mkdirs();

                    //Beautfier code

    //                File tempFile = new File(bocfg.getDeploymentsrcdir()+version+File.separator+bodef.getBoName()+".temp");
    //                tempFile.createNewFile();

    //                FileWriter fw = new FileWriter(tempFile);
    //                fw.write(srctemp);
    //                fw.close();

                    File srcfile = new File(bocfg.getDeploymentsrcdir()+version+File.separator+bodef.getBoName()+".java");


                    OutputStreamWriter fw;
                    String encoding = (new boConfig()).getEncoding();
                    if(encoding!=null)
                      fw= new OutputStreamWriter(new FileOutputStream(srcfile), encoding);
                    else
                      fw= new OutputStreamWriter(new FileOutputStream(srcfile));

                    //FileWriter fw = new FileWriter(srcfile);
                    fw.write(srctemp);
                    fw.close();

    //                long time1 = System.currentTimeMillis();

    //                CallBeautifier cb = new CallBeautifier(
    //                    LINELENGTH,
    //                    SPACENUM,
    //                    MAXINDENT,
    //                    BRACKETINDENT,
    //                    SWITCHINDENT,
    //                    TABINDENT, tempFile.getAbsolutePath(), srcfile.getAbsolutePath());

    //                long time2 = System.currentTimeMillis();
    //                long total = (time2 - time1)/1000;
    //                logger.info("Beautifier time = " + total + " (s)");
                    //remover o ficheiro temporario
    //                tempFile.delete();


                   // logger.debug("Compile "+ version+" "+bodef.getLabel() );
                    srcfiles.add(srcfile);
                }
            }

            File[] a_srcfiles= new File[srcfiles.size()];
            srcfiles.toArray(a_srcfiles);

            boClassCompiler comp = new boClassCompiler();
            ArrayList auxL = new ArrayList(COMPILE_MAX);
            File[] aux = new File[COMPILE_MAX];
            int pos = 0;
            logger.info("Started Compiling " + a_srcfiles.length + " classes...");
            while(pos < a_srcfiles.length)
            {
                pos = compileMax(a_srcfiles, auxL, pos, COMPILE_MAX);
                aux = new File[auxL.size()];
                auxL.toArray(aux);
                comp.compile(bocfg.getDeploymentsrcdir(),
                    aux,
                    bocfg.getDeploymentclassdir());
            }
            logger.info("Ended Compiling...");


        } catch (IOException e) {
            logger.error(e);
            throw new boException(this.getClass().getName()+"build(String)","BO-1601",e,bodef.getBoName());
        } catch (RuntimeException e) {
            logger.error(e);
            throw new boException(this.getClass().getName()+"build(String)","BO-1601",e,bodef.getBoName());
        } catch(Exception e)
        {
            logger.error(e);
            throw new boException(this.getClass().getName()+"build(String)","BO-1601",e,bodef.getBoName());
        }
    }

    private static int compileMax(File[] fromFiles, ArrayList toFiles, int from, int max)
    {
        int j = 0;
        toFiles.clear();
        for(int i = from; i < fromFiles.length && j < max; i++, j++)
        {
            toFiles.add(fromFiles[i]);
        }
        return from + j;
    }

    private static void clear(File[] f, int from)
    {
        for(int i = from; i < f.length; i++)
        {
            if(f[i] != null)
            {
                f[i] = null;
            }
        }
    }

    private String generateSrcFile(String srccode,boDefHandler bodef,String clsregboui) throws boRuntimeException
    {
        RegularExpression regexp = new RegularExpression("");

        String version="v"+bodef.getBoVersion();
        version = version.replace('.','_');

        srccode = "package "+version+";\n\r"+srccode;
        srccode = StringUtils.replacestr(srccode,"_boTemplate_",bodef.getBoName());

        String[] interfs = bodef.getBoImplementsJavaInterfaces();
        StringBuffer sbInt = new StringBuffer();
        for (int i = 0; i < interfs.length; i++)
        {
            if( i > 0 ) sbInt.append( ", " );
            sbInt.append( interfs[i] );
        }
        srccode = StringUtils.replacestr(srccode,"#BO.IMPLEMENTS#", sbInt.toString() );

        if(bodef.getBoExtendsClass()!=null) {
            srccode = StringUtils.replacestr(srccode,"#BO.SUPERCLASS#",bodef.getBoExtendsClass());
        } else {
            if(bodef.getBoSuperBo() !=null) {
                srccode = StringUtils.replacestr(srccode,"#BO.SUPERCLASS#",bodef.getBoSuperBo());
            } else {
                srccode = StringUtils.replacestr(srccode,"#BO.SUPERCLASS#","boObject");
            }
        }


        // Remove any extra data in template
        String starttag = "//@REMOVE";
        String endtag = "//@ENDREMOVE";
        while(srccode.indexOf(starttag)>-1) {
            srccode = srccode.substring(0,srccode.indexOf(starttag)-1)+srccode.substring(srccode.indexOf(endtag)+endtag.length(),srccode.length());
        }

        // Replace business object properties
        Hashtable htbo = fillRptObject(bodef,clsregboui);
        Enumeration enumbo = htbo.keys();
        while(enumbo.hasMoreElements()) {
            String key = (String)enumbo.nextElement();
            String value = (String)htbo.get(key);
            if(value.indexOf("$")>-1) {
                value = StringUtils.replacestr(value,"$","\\$");
            }
            regexp.setPattern(key);
            srccode = REUtil.substitute(regexp,value,true,srccode);
        }


        // For each attribute no multi line and no object reference

        // For each attribute object reference
        codeJavaHash = new Hashtable();
        CodeJavaConstructor cjc = new CodeJavaConstructor();
        boDefHandler bodefDeploy = boDefHandler.getBoDefinition(bodef.getName());
        this.parseBridgesCodeJava(bodefDeploy,cjc);
        this.parseObjectAttributesCodeJava(bodefDeploy,false,cjc);
        this.parseAttributesCodeJava(bodefDeploy, bodefDeploy.getBoAttributes(),false, null, cjc);


        srccode = this.parseBridges(bodef,srccode);
        srccode = this.parseObjectAttributes(bodef,srccode,false);
        srccode = this.parseAttributes(bodef, bodef.getBoAttributes(),srccode,false, null);
        srccode = this.parseStandartAttributes(bodef, bodef.getBoAttributes(),srccode,false, null);
        //if(bodef.getBoClsState()!=null) {
            srccode = this.parseStateAttributes(bodef.getBoClsState(),srccode);
        //}

        // For each attribute object multi reference

        // Build Methods activities and events
        String methods = buildClassMethods(bodef.getBoMethods(),true);
        //going to build methods for attributes
//        if(!bodef.getBoIsSubBo() && (bodef.getBoExtendsClass() == null
//            || "".equals(bodef.getBoExtendsClass().trim())))
//        {
//            methods += "\n" + cjc.getRefreshCodeJava();
            methods += "\n" + cjc.getDependences();
            methods += "\n" + cjc.getDependencesFields();
            methods += "\n" + cjc.getFormulaCode();
         boDefForwardObject[] fwds = bodef.getForwardObjects();
         if(fwds != null)
         {
             StringBuffer onSave = new StringBuffer();
             for (int i = 0; i < fwds.length; i++)
             {
                onSave.append("\npublic void ").append(fwds[i].getOnSaveFwdObjectMethodName())
                    .append("(boObject fwdObject){\n");
                if(fwds[i].getOnSaveFwdObject() == null)
                {
                    onSave.append("return;");
                }
                else
                {
                    onSave.append(fwds[i].getOnSaveFwdObject());
                }
                onSave.append("\n}\n");
             }
             methods +=onSave.toString();
         }

//        }

        String activities = getActivitiesCode(bodef);
        String events = getEvents( bodef.getBoClsEvents() );

        srccode = StringUtils.replacestr(srccode,"#BO.IMPLEMENTEDMETHODS#",methods+"\n\t"+activities+"\n\t"+events);
        return srccode;
    }

    private String buildClassMethods(boDefMethod[] meth,boolean createasync/*boDefHandler bodef*/) {
        StringBuffer smeth = new StringBuffer();
//        boDefMethod[] meth = bodef.getBoMethods();

        ArrayList codeHidden = new ArrayList();
        ArrayList methName = new ArrayList();
        CodeJavaConstructor cjc = new CodeJavaConstructor();
        String auxCode;
        for (byte i = 0;meth != null && i < meth.length; i++)
        {
            if( meth[i].getHiddenWhen() != null )
            {
                auxCode = CodeJavaConstructor.treatCodeJava( meth[i].getHiddenWhen().getSource() );
                if(auxCode != null)
                {
                    codeHidden.add(auxCode);
                    methName.add(meth[i].getName());
                }
            }
        }

        if(meth != null && meth.length > 0 && codeHidden.size() > 0 )
        {
            StringBuffer methodIsHidden = new StringBuffer();
            methodIsHidden.append("\n\r\tpublic boolean ").append("methodIsHidden(String methodName) throws boRuntimeException{");

            for(int i = 0; i < codeHidden.size(); i++)
            {
                methodIsHidden.append("\n if( methodName.equals(\"").append(methName.get(i)).append("\")) {");
                methodIsHidden.append(codeHidden.get(i));
                methodIsHidden.append("}");
            }
            methodIsHidden.append("\n return super.methodIsHidden(methodName);}");
            smeth.append(methodIsHidden);
        }
        for (byte i = 0;meth != null && i < meth.length; i++)  {
            String body = meth[i].getBody();
            StringBuffer ass=new StringBuffer();
            ass.append("\n\r\tpublic ").append(meth[i].getReturnType()).append(" ").append(meth[i].getName()).append("(");
            Class   [] xc  = meth[i].getAssinatureClasses();
            String  [] xan = meth[i].getAssinatureArgNames();
            for (byte z=0;z<xc.length;z++)  {
                ass.append(xc[z].getName()).append(" ").append(xan[z]);
                if(z<xc.length-1)
                    ass.append(",");
            }
            ass.append(") throws boRuntimeException {\n\r\t");
            if((meth[i].getIsNativeOverwrited()||meth[i].getIsNative()) && (body==null || body.trim().length()==0) )  {
                ass.append("super.").append(meth[i].getName()).append("(");
                for (byte z=0;z<xc.length;z++)  {
                    ass.append(xan[z]);
                    if(z<xc.length-1)
                        ass.append(",");
                }
                ass.append(");");
            } else if(body != null) {
                ass.append(body);
            }
            ass.append("\n\r\t}");
            smeth.append(ass);
            // Create Async_*** Method for asyncronous execute.
//            if(createasync) {
//                ass.delete(0,ass.length());
//                ass.append("\n\r\tpublic ").append("void").append(" ").append("Async_"+meth[i].getName()).append("(");
//                for (byte z=0;z<xc.length;z++)  {
//                    ass.append(xc[z].getName()).append(" ").append(xan[z]);
//                    if(z<xc.length-1)
//                        ass.append(",");
//                }
//                ass.append(") throws boRuntimeException {\n\r\t");
//                ass.append("Object[] args = new Object["+xc.length+"];\n\r\t");
//                for (byte z=0;z<xc.length;z++)  {
//                    ass.append("args["+z+"] = netgest.utils.ClassArgumentsUtils.toObject("+xan[z]+");\n\r\t");
//                }
//                ass.append("addAsynchMethod(\""+meth[i].getName()+"\",args);\n\r\t");
//                ass.append("}\n\r");
//                smeth.append(ass);
//            }
        }
        return smeth.toString();
    }
    private String getEvents(boDefClsEvents[] events) {
        StringBuffer smeth = new StringBuffer();
//        boDefClsEvents[] events = bodef.getBoClsEvents();
        for (byte i = 0;events != null && i < events.length; i++)
        {
            boDefXeoCode code = events[i].getEventCode();
            if( code != null && code.getLanguage() == boDefXeoCode.LANG_JAVA )
            {
                String body = code.getSource();
                String name = events[i].getEventName();
                if(body != null && body.trim().length()>0) {
                    if(name.indexOf('.') >-1)
                    {
                        name = name.substring(0,name.indexOf('.'));
                    }
                    smeth.append("public ");
                    if(events[i].hasBooleanReturn()) {
                        smeth.append("boolean ");
                    }
                    else
                    {
                        smeth.append("void ");
                    }
                    smeth.append(name).append("(boEvent event) throws boRuntimeException {\n\r\t");
                    smeth.append(body);
                    smeth.append("\n\r}\n\r");
                }
            }
        }
        return smeth.toString();
    }
    private static final Hashtable fillRptAttributes(boDefAttribute att,boolean bridge) {
        Hashtable ht = new Hashtable();
        ht.put("#ATT.NAME#",att.getName());

        ht.put("#ATT.CLASSNAME#",att.getClassName());

        ht.put("#ATT.DBNAME#",att.getDbName());
        ht.put("#ATT.DBBINDING#",att.getDbIsBinding()?"true":"false");
        ht.put("#ATT.JAVADATATYPEOBJ#",parseJavaDataType(att.getType(),TYPE_OBJECT));
        ht.put("#ATT.JAVADATATYPE#",parseJavaDataType(att.getType(),TYPE_SIMPLE));
        ht.put("#ATT.PRIMITIVEDATATYPE#",parseJavaDataType(att.getType(),TYPE_DATA));
        ht.put("#ATT.TYPE#",att.getType());
        if(att.getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE)
        {
            ht.put("#ATT.REFOBJECTNAME#",att.getReferencedObjectName());
            if(bridge) {
                ht.put("#ATT.EXTENDS#","BridgeObjAttributeHandler");
            }
            else
            {
                ht.put("#ATT.EXTENDS#","ObjAttHandler");
            }
        }
        else
        {
            if(bridge) {
                ht.put("#ATT.EXTENDS#","BridgeAttributeHandler");
            }
            else
            {
                if( att.getType() == boDefAttribute.ATTRIBUTE_SEQUENCE )
                {
                    ht.put("#ATT.EXTENDS#","netgest.bo.runtime.attributes.boAttributeSequence");
                }
                else
                {
                    ht.put("#ATT.EXTENDS#","AttributeHandler");
                }
            }
        }
        if(att.getExtendsClass()!=null)
            ht.put("#ATT.EXTENDS#",att.getExtendsClass());

        return ht;
    }

    private static final Hashtable fillRptStateAttributes(boDefClsState att) {
        Hashtable ht = new Hashtable();
        ht.put("#ATT.NAME#",att.getName());
        ht.put("#ATT.DBNAME#",att.getDbName());
        ht.put("#ATT.DBBINDING#",att.getDbIsBinding()?"true":"false");
        ht.put("#ATT.JAVADATATYPEOBJ#",parseJavaDataType(att.getType(),TYPE_OBJECT));
        ht.put("#ATT.JAVADATATYPE#",parseJavaDataType(att.getType(),TYPE_SIMPLE));
        ht.put("#ATT.PRIMITIVEDATATYPE#",parseJavaDataType(att.getType(),TYPE_DATA));
        ht.put("#ATT.NUMERICFORM#",""+att.getNumericForm());
        ht.put("#ATT.TYPE#",att.getType());

//        if(att.getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE)
//            ht.put("#ATT.REFOBJECTNAME#",att.getReferencedObjectName());
        return ht;
    }


    private static final Hashtable fillRptObject(boDefHandler bodef,String boui) {
        Hashtable ht = new Hashtable();
        ht.put("#BO.VERSION#",bodef.getBoMajorVersion()+"."+bodef.getBoMinorVersion());
        ht.put("#BO.NAME#",bodef.getBoName());
        ht.put("#BO.MODIFIERS#",bodef.getClassType()==boDefHandler.TYPE_ABSTRACT_CLASS?"abstract":"");
        //ht.put("#BO.MODIFIERS#",bodef.getBoIsAbstract()?"":"");
        return ht;
    }
    private static final Hashtable fillRptBridge(boDefAttribute batt) {
        boDefBridge brdef = batt.getBridge();
        Hashtable ht = new Hashtable();
        ht.put("#BRIDGE.FATHERFIELDNAME#",brdef.getFatherFieldName());
        ht.put("#BRIDGE.CHILDFIELDNAME#",brdef.getChildFieldName());
        ht.put("#BRIDGE.REFOBJECTNAME#",batt.getName());
        ht.put("#BRIDGE.NAME#",batt.getName());
        return ht;
    }
    private static final Hashtable fillRptBridgeAttribute(boDefAttribute att) {
        Hashtable ht = new Hashtable();
        ht.put("#ATT.NAME#",att.getName());
        ht.put("#ATT.DBNAME#",att.getDbName());
        ht.put("#ATT.JAVADATATYPEOBJ#",parseJavaDataType(att.getType(),TYPE_OBJECT));
        ht.put("#ATT.JAVADATATYPE#",parseJavaDataType(att.getType(),TYPE_SIMPLE));
        ht.put("#ATT.PRIMITIVEDATATYPE#",parseJavaDataType(att.getType(),TYPE_DATA));
        ht.put("#ATT.TYPE#",att.getType());
        return ht;
    }
    protected static final String parseJavaDataType(String type,byte objtype) {
        if(type.startsWith("object."))
            return "BigDecimal";

        if(objtype==TYPE_OBJECT)
        {
            return boDefDataTypeMapping.getObjectType(type);
        }
        else if (objtype==TYPE_SIMPLE)
        {
            return boDefDataTypeMapping.getSimpleType(type);
        }
        else
        {
            return boDefDataTypeMapping.getDataType(type);
        }

    }
    public final String parseObjectAttributes(boDef bodef,String srccode,boolean all) throws boRuntimeException{
        return parseObjectAttributes(bodef, bodef.getBoAttributes(),srccode,all,false,null);
    }

    public final void parseObjectAttributesCodeJava(boDef bodef,boolean all,
        CodeJavaConstructor cjc) throws boRuntimeException{
        parseObjectAttributesCodeJava(bodef, bodef.getBoAttributes(),all,false,null, cjc);
    }

    public final String getAttributeMethods(boDef bodef, boDefAttribute att, boDefAttribute[] atts,
            CodeJavaConstructor cjc, boolean bridge, String attName)
        throws boRuntimeException
    {
        StringBuffer sb = new StringBuffer();
        cjc.resetHaveDeault();

        boDefXeoCode code;

        //Valid
        code = att.getValid();
        if( code != null)
        {
            cjc.setValues(att.getValid(), "validate", bodef, att, atts, bridge, attName );
            cjc.setCodeJavaDepends(att.getValid().getDepends(), bodef, att);
            String javaCode = cjc.getJava();
            if(code.needsClass())
            {
                sb.append(javaCode);
                sb.append("\n");
            }
        }


        //required
        code = att.getRequired();
        if( code != null)
        {
            cjc.setValues(att.getRequired(), "required", bodef, att, atts, bridge, attName);
            cjc.setCodeJavaDepends(att.getRequired().getDepends(), bodef, att);
            String javaCode = cjc.getJava();
            if(code.needsClass())
            {
                sb.append(javaCode);
                sb.append("\n");
            }
        }

        //disableWhen
        if( "gestor".equalsIgnoreCase( att.getName()) )
        {
            boolean tobreak=true;
        }
        code = att.getDisableWhen();
        if( code != null)
        {
            cjc.setValues(att.getDisableWhen(), "disableWhen", bodef, att, atts, bridge, attName);
            cjc.setCodeJavaDepends(att.getDisableWhen().getDepends(), bodef, att);
            String javaCode = cjc.getJava();
            if(code.needsClass())
            {
                sb.append(javaCode);
                sb.append("\n");
            }
        }

        //hiddenWhen
        code = att.getHiddenWhen();
        if( code != null)
        {
            cjc.setValues(att.getHiddenWhen(), "hiddenWhen", bodef, att, atts, bridge, attName);
            cjc.setCodeJavaDepends(att.getHiddenWhen().getDepends(), bodef, att );
            String javaCode = cjc.getJava();
            if(code.needsClass())
            {
                sb.append(javaCode);
                sb.append("\n");
            }
        }

        //haveDefault
        code = att.getDefaultValue();
        if( code != null)
        {
            cjc.setValues(att.getDefaultValue(), "defaultValue", bodef, att, atts, bridge, attName);
            cjc.setCodeJavaDepends(att.getDefaultValue().getDepends(), bodef, att);
            String javaCode = cjc.getJava();
            if(code.needsClass())
            {
                sb.append(javaCode);
                sb.append("\n");
            }
        }

        //haveDefault
        sb.append(cjc.getHaveDefault());
        sb.append("\n");

        //condition
        code = att.getLovCondition();
        if( code != null)
        {
            cjc.setValues(att.getLovCondition(), "condition", bodef, att, atts, bridge, attName);
            cjc.setCodeJavaDepends(att.getLovCondition().getDepends(), bodef, att);
            String javaCode = cjc.getJava();
            if(code.needsClass())
            {
                sb.append(javaCode);
                sb.append("\n");
            }
        }

        //canChangeLov
        code = att.getLovEditable();
        if( code != null)
        {
            cjc.setValues(att.getLovEditable(), "canChangeLov", bodef, att, atts, bridge, attName);
            cjc.setCodeJavaDepends(att.getLovEditable().getDepends(), bodef, att);
            String javaCode = cjc.getJava();
            if(code.needsClass())
            {
                sb.append(javaCode);
                sb.append("\n");
            }
        }

        //onChangeSubmit
        code = att.getOnChangeSubmit();
        if( code != null)
        {
            cjc.setValues(att.getOnChangeSubmit(), "onChangeSubmit", bodef, att, atts, bridge, attName);
            cjc.setCodeJavaDepends(att.getOnChangeSubmit().getDepends(), bodef, att);
            String javaCode = cjc.getJava();
            if(code.needsClass())
            {
                sb.append(javaCode);
                sb.append("\n");
            }
        }

        //formula
        code = att.getFormula();
//        if( code != null )
//        {
            cjc.setValues(att.getFormula(), "formula", bodef, att, atts, bridge, attName);
            if( code != null )
            {
                cjc.setCodeJavaDepends(att.getFormula().getDepends(), bodef, att);
            }
            sb.append(cjc.getJava());
            sb.append("\n");
//        }
        if( sb.toString().trim().startsWith("null") )
        {
            logger.error("ERRO.. codigo gerado a null");
        }
        return sb.toString();

    }


    public final void parseObjectAttributesCodeJava(boDef bodef, boDefAttribute[] atts,
        boolean all,boolean bridge, String attBridgeName, CodeJavaConstructor cjc) throws boRuntimeException{
            StringBuffer sb = new StringBuffer();
            for(int i=0;atts != null && i<atts.length;i++)
            {
                if(atts[i].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE && ( ( atts[i].getMaxOccurs()==1 || atts[i].getDbIsTabled() )  || all ) && atts[i].getReferencedObjectDef()!=null )
                {
                    sb = sb.delete(0, sb.length());
                    sb.append(getAttributeMethods(bodef,atts[i], atts, cjc, bridge, attBridgeName)).append("\n");
                    if(bridge)
                    {
                        codeJavaHash.put(attBridgeName + "." + atts[i].getName(), sb.toString());
                    }
                    else
                    {
                        codeJavaHash.put(atts[i].getName(), sb.toString());
                    }
                }
            }
    }

    public final String parseObjectAttributes(boDef bodef, boDefAttribute[] atts,String srccode,
        boolean all,boolean bridge, String attBridgeName) throws boRuntimeException{
        String starttag = "//@FOREACH OBJATTRIBUTE";
        String endtag = "//@ENDFOREACH OBJATTRIBUTE";
        StringBuffer sb = new StringBuffer();
        boolean have = false;
        while(checktags(starttag,endtag,srccode)>-1) {
            String rarea ="";
            for(int i=0;atts != null && i<atts.length;i++) {
                have = false;
                String area = srccode.substring(srccode.indexOf(starttag)+starttag.length(),srccode.indexOf(endtag));
                while( area.indexOf( "//@IF ARRAY" )>-1)
                {
                    if( atts[i].getDbIsTabled() )
                    {
                        String xarea = area.substring( 0 , area.indexOf("//@IF ARRAY")  );
                        xarea += area.substring( area.indexOf("//@IF ARRAY")+11  , area.indexOf("//@ELSEIF ARRAY")  );
                        area = xarea + area.substring( area.indexOf( "//@ENDIF ARRAY" )+14 , area.length() );
                    }
                    else
                    {
                        String xarea = area.substring( 0 , area.indexOf( "//@IF ARRAY" )  );
                        area = xarea + area.substring( area.indexOf("//@ELSEIF ARRAY")+15 , area.indexOf( "//@ENDIF ARRAY" ) )
                             + area.substring( area.indexOf("//@ENDIF ARRAY")+14 , area.length() );
                    }
                }

         //                if(atts[i].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE && ( ( atts[i].getMaxOccurs()==1 || atts[i].getDbIsTabled() )  || all ) && atts[i].getReferencedObjectDef()!=null )  {
                if(atts[i].needsClass() && atts[i].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE && ( ( atts[i].getMaxOccurs()==1 || atts[i].getDbIsTabled() )  || all ) && atts[i].getReferencedObjectDef()!=null )
                {
                    Hashtable ht = fillRptAttributes(atts[i],bridge);
                    Enumeration oEnum = ht.keys();
                    while(oEnum.hasMoreElements()) {
                        String key = (String)oEnum.nextElement();
                        area = StringUtils.replacestr(area,key,(String)ht.get(key));
                        //area = REUtil.substitute(regexp,(String)ht.get(key),true,area);
                    }
                    have=true;
                }
                if(have) {
                    sb = sb.delete(0, sb.length());
                    sb.append(this.buildClassMethods(atts[i].getMethods(),false)).append("\n");
                    //sb.append(getAttributeMethods(bodef,atts[i], atts, cjc, bridge, attBridgeName)).append("\n");
                    if(bridge)
                    {
                        sb.append(codeJavaHash.get(attBridgeName + "." + atts[i].getName())).append("\n");
                    }
                    else
                    {
                        sb.append(codeJavaHash.get(atts[i].getName())).append("\n");
                    }
                    area = StringUtils.replacestr(area,"#OBJECT.METHODS#", sb.toString());
                    area = StringUtils.replacestr(area,"#OBJECT.EVENTS#",this.getEvents( atts[i].getEvents() ) );
                    rarea += area;
                }

            }
            srccode = srccode.substring(0,srccode.indexOf(starttag)-1)+rarea+srccode.substring(srccode.indexOf(endtag)+endtag.length(),srccode.length());
        }
        return srccode;
    }

    public static final String parseStateAttributes(boDefClsState clsstates,String srccode) {
        String starttag = "//@FOREACH STATE";
        String endtag = "//@ENDFOREACH STATE";
        RegularExpression regexp = new RegularExpression("");
        while(checktags(starttag,endtag,srccode)>-1) {
            String rarea ="";
            boDefClsState[] atts=null;
            if(clsstates!=null) atts = clsstates.getChildStateAttributes();
            for(int i=0;atts != null && i<atts.length;i++) {
                boolean have = false;
                String area = srccode.substring(srccode.indexOf(starttag)+starttag.length(),srccode.indexOf(endtag));

                area = parseStateOptions(atts[i],area);

                Hashtable ht = fillRptStateAttributes(atts[i]);
                Enumeration oEnum = ht.keys();
                while(oEnum.hasMoreElements()) {
                    String key = (String)oEnum.nextElement();
                    String value = (String)ht.get(key);
                    if(value.indexOf("$")>-1) {
                        value = StringUtils.replacestr(value,"$","\\$");
                    }
                    regexp.setPattern(key);
                    area = REUtil.substitute(regexp,value,true,area);
                }

                have=true;
                if(have) rarea += area;
            }
            srccode = srccode.substring(0,srccode.indexOf(starttag)-1)+rarea+srccode.substring(srccode.indexOf(endtag)+endtag.length(),srccode.length());
        }
        return srccode;
    }
    public static final String parseStateOptions(boDefClsState state,String srccode) {
        boDefClsState[] atts = state.getChildStates();
        String starttag = "//@FOREACH OPTIONSTATE";
        String endtag = "//@ENDFOREACH OPTIONSTATE";
        RegularExpression regexp = new RegularExpression("");
        while(checktags(starttag,endtag,srccode)>-1) {
            String rarea ="";
            for(int i=0;atts != null && i<atts.length;i++) {
                boolean have = false;
                String area = srccode.substring(srccode.indexOf(starttag)+starttag.length(),srccode.indexOf(endtag));
                Hashtable ht = fillRptStateAttributes(atts[i]);
                Enumeration oEnum = ht.keys();
                while(oEnum.hasMoreElements()) {
                    String key = (String)oEnum.nextElement();
                    String value = (String)ht.get(key);
                    if(value.indexOf("$")>-1) {
                        value = StringUtils.replacestr(value,"$","\\$");
                    }
                    regexp.setPattern(key);
                    area = REUtil.substitute(regexp,value,true,area);
                }
                have=true;
                if(have) rarea += area;
            }
            srccode = srccode.substring(0,srccode.indexOf(starttag)-1)+rarea+srccode.substring(srccode.indexOf(endtag)+endtag.length(),srccode.length());
        }
        return srccode;
    }
    public final void parseAttributesCodeJava(boDef bodef,boDefAttribute[] atts,
        boolean bridge, String attName, CodeJavaConstructor cjc) throws boRuntimeException
    {
        StringBuffer sb = new StringBuffer();
        for(int i=0;atts != null && i<atts.length;i++) {
            if(atts[i].getAtributeType()==boDefAttribute.TYPE_ATTRIBUTE && ( atts[i].getMaxOccurs()==1 || atts[i].getDbIsTabled() ) )
            {
                sb = sb.delete(0, sb.length());
                sb.append(getAttributeMethods(bodef,atts[i], atts, cjc, bridge, attName)).append("\n");
                if(bridge)
                {
                    codeJavaHash.put(attName + "." + atts[i].getName(), sb.toString());
                }
                else
                {
                    codeJavaHash.put(atts[i].getName(), sb.toString());
                }
            }
        }
    }


  public final String parseStandartAttributes(boDef bodef,boDefAttribute[] atts,String srccode,
        boolean bridge, String attName) throws boRuntimeException
    {
        String starttag = "//@FOREACH STDATTRIBUTE";
        String endtag = "//@ENDFOREACH STDATTRIBUTE";
        RegularExpression regexp = new RegularExpression("");
        StringBuffer sb = new StringBuffer();
        while(checktags(starttag,endtag,srccode)>-1) {
            String rarea ="";
            for(int i=0;atts != null && i<atts.length;i++) {
                String area = srccode.substring(srccode.indexOf(starttag)+starttag.length(),srccode.indexOf(endtag));
                if( !atts[i].needsClass() )
                {
                    area = area.replaceAll("#ATT.PRIMITIVEDATATYPE#", parseJavaDataType(atts[i].getType(),TYPE_DATA) );
                    area = area.replaceAll("#ATT.CLASSNAME#", atts[i].className() );
                    area = area.replaceAll("#ATT.NAME#", atts[i].getName() );
                    rarea += area;
                }
            }
            srccode = srccode.substring(0,srccode.indexOf(starttag)-1)+rarea+srccode.substring(srccode.indexOf(endtag)+endtag.length(),srccode.length());
        }
        return srccode;
    }

 public final String parseAttributes(boDef bodef,boDefAttribute[] atts,String srccode,
        boolean bridge, String attName) throws boRuntimeException
    {
        String starttag = "//@FOREACH ATTRIBUTE";
        String endtag = "//@ENDFOREACH ATTRIBUTE";
        RegularExpression regexp = new RegularExpression("");
        StringBuffer sb = new StringBuffer();
        while(checktags(starttag,endtag,srccode)>-1) {
            String rarea ="";
            for(int i=0;atts != null && i<atts.length;i++) {

                boolean have = false;
                String area = srccode.substring(srccode.indexOf(starttag)+starttag.length(),srccode.indexOf(endtag));
                while( area.indexOf( "//@IF ARRAY" )>-1)
                {
                    if( atts[i].getDbIsTabled() )
                    {
                        String xarea = area.substring( 0 , area.indexOf("//@IF ARRAY")  );
                        xarea += area.substring( area.indexOf("//@IF ARRAY")+11  , area.indexOf("//@ELSEIF ARRAY")  );
                        area = xarea + area.substring( area.indexOf( "//@ENDIF ARRAY" )+14 , area.length() );
                    }
                    else
                    {
                        String xarea = area.substring( 0 , area.indexOf( "//@IF ARRAY" )  );
                        area = xarea + area.substring( area.indexOf("//@ELSEIF ARRAY")+15 , area.indexOf( "//@ENDIF ARRAY" ) )
                             + area.substring( area.indexOf("//@ENDIF ARRAY")+14 , area.length() );
                    }
                }
            //    if(atts[i].getAtributeType()==boDefAttribute.TYPE_ATTRIBUTE && ( atts[i].getMaxOccurs()==1 || atts[i].getDbIsTabled() ) )  {
                 if((atts[i].needsClass() || bridge) && atts[i].getAtributeType()==boDefAttribute.TYPE_ATTRIBUTE && ( atts[i].getMaxOccurs()==1 || atts[i].getDbIsTabled() ) )
                 {
                    Hashtable ht = fillRptAttributes(atts[i],bridge);
                    boolean simple = true;
                    if( !boDefDataTypeMapping.getHaveSimpleType(atts[i].getType()) )
                        simple = false;
                    if(!simple){
                        while(area.indexOf("@IF SIMPLEDATATYPE")>-1) {
                            int sidx;
                            String x =area.substring(0,sidx=area.indexOf("@IF SIMPLEDATATYPE"));
                            String y =area.substring(area.indexOf("@ENDIF SIMPLEDATATYPE",sidx)+6);
                            area = x+y;

                        }

                    }

                    Enumeration oEnum = ht.keys();
                    while(oEnum.hasMoreElements()) {
                        String key = (String)oEnum.nextElement();
                        String value = (String)ht.get(key);
                        if(value.indexOf("$")>-1) {
                            value = StringUtils.replacestr(value,"$","\\$");
                        }
                        regexp.setPattern(key);
                        area = REUtil.substitute(regexp,value,true,area);
                    }
                    have=true;
                }
                if(have) {
                    sb = sb.delete(0, sb.length());
                    sb.append(this.buildClassMethods(atts[i].getMethods(),false)).append("\n");
                    //sb.append(getAttributeMethods(bodef,atts[i], atts, cjc, bridge, attName)).append("\n");
                    if(bridge)
                    {
                        sb.append(codeJavaHash.get(attName + "." + atts[i].getName())).append("\n");
                    }
                    else
                    {
                        sb.append(codeJavaHash.get(atts[i].getName())).append("\n");
                    }
                    area = StringUtils.replacestr(area,"#OBJECT.METHODS#",sb.toString());
                    area = StringUtils.replacestr(area,"#OBJECT.EVENTS#",this.getEvents(atts[i].getEvents()));
                    rarea += area;
                }
            }
            srccode = srccode.substring(0,srccode.indexOf(starttag)-1)+rarea+srccode.substring(srccode.indexOf(endtag)+endtag.length(),srccode.length());
        }
        return srccode;
    }

    public final void parseBridgesCodeJava(boDefHandler bodef,CodeJavaConstructor cjc) throws boRuntimeException
    {
            boDefAttribute[] atts = bodef.getBoAttributes();
            String rarea="";
            StringBuffer sb = new StringBuffer();
            for(int i=0;atts != null && i<atts.length;i++) {
                if(atts[i].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE && !atts[i].getDbIsTabled() && atts[i].getMaxOccurs()>1 && atts[i].getReferencedObjectDef()!=null)  {
                    if(atts[i].getReferencedObjectName().equals("boObject") || atts[i].getReferencedObjectDef()!=null) {
                        parseAttributesCodeJava(bodef, atts[i].getBridge().getBoAttributes(),true, atts[i].getName(), cjc);
                        boDefAttribute[] batts    = atts[i].getBridge().getBoAttributes();
                        boDefAttribute[] ballatts = new boDefAttribute[batts==null?1:batts.length+1];
                        if(batts !=null) {
                            System.arraycopy(batts,0,ballatts,0,batts.length);
                            ballatts[batts.length] = atts[i];
                        } else
                        {
                            ballatts[0] = atts[i];
                        }
                        parseObjectAttributesCodeJava(bodef, ballatts,true,true,atts[i].getName(),cjc);
                        sb = sb.delete(0, sb.length());
                        sb.append(getAttributeMethods(bodef,atts[i], atts, cjc, false, null)).append("\n");
                        codeJavaHash.put(atts[i].getName(), sb.toString());
                    }
                }
            }
    }

    public final String parseBridges(boDefHandler bodef,String srccode) throws boRuntimeException
    {
        RegularExpression regexp = new RegularExpression("");
        String starttag = "//@FOREACH MULTIOBJATTRIBUTE";
        String endtag = "//@ENDFOREACH MULTIOBJATTRIBUTE";
        StringBuffer sb = new StringBuffer();
        while(checktags(starttag,endtag,srccode)>-1) {
            boDefAttribute[] atts = bodef.getBoAttributes();
            String rarea="";
            for(int i=0;atts != null && i<atts.length;i++) {
                boolean have = false;
                String area = srccode.substring(srccode.indexOf(starttag)+starttag.length(),srccode.indexOf(endtag));
                if(atts[i].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE && !atts[i].getDbIsTabled() && atts[i].getMaxOccurs()>1 && atts[i].getReferencedObjectDef()!=null)  {
                    if(atts[i].getReferencedObjectName().equals("boObject") || atts[i].getReferencedObjectDef()!=null) {
                        //Hashtable ht = fillRptAttributes(atts[i]);
                        Hashtable htb = fillRptBridge(atts[i]);
                        /*Enumeration oEnum = ht.keys();
                        while(oEnum.hasMoreElements()) {
                            String key = (String)oEnum.nextElement();
                            String value = (String)ht.get(key);
                            if(value.indexOf("$")>-1) {
                                value = tools.replacestr(value,"$","\\$");
                            }
                            regexp.setPattern(key);
                            area = REUtil.substitute(regexp,value,true,area);
                        }*/
                        Enumeration oEnum = htb.keys();
                        while(oEnum.hasMoreElements()) {
                            String key = (String)oEnum.nextElement();
                            String value = (String)htb.get(key);
                            if(value.indexOf("$")>-1) {
                                value = StringUtils.replacestr(value,"$","\\$");
                            }
                            regexp.setPattern(key);
                            area = REUtil.substitute(regexp,value,true,area);
                        }
                        area = parseAttributes(bodef, atts[i].getBridge().getBoAttributes(),area,true, atts[i].getName());
                        boDefAttribute[] batts    = atts[i].getBridge().getBoAttributes();
                        boDefAttribute[] ballatts = new boDefAttribute[batts==null?1:batts.length+1];
                        if(batts !=null) {
                            System.arraycopy(batts,0,ballatts,0,batts.length);
                            ballatts[batts.length] = atts[i];
                        } else {
                            ballatts[0] = atts[i];
                        }
                        area = parseObjectAttributes(bodef, ballatts,area,true,true,atts[i].getName());
                        have=true;
                    }
                }
                if(have) {
                    sb = sb.delete(0, sb.length());
                    sb.append(this.buildClassMethods(atts[i].getBridge().getMethods(),false)).append("\n");
                    sb.append(codeJavaHash.get(atts[i].getName())).append("\n");
                    area = StringUtils.replacestr(area,"#OBJECT.METHODS#",sb.toString());
                    area = StringUtils.replacestr(area,"#OBJECT.EVENTS#",this.getEvents(atts[i].getEvents()));
                    rarea += area;
                }
            }
            srccode = srccode.substring(0,srccode.indexOf(starttag)-1)+rarea+srccode.substring(srccode.indexOf(endtag)+endtag.length(),srccode.length());
        }
        return srccode;
    }
    private static final int checktags(String starttag,String endtag,String srccode) {
        int x = srccode.indexOf(starttag);
//        if(x!= -1 &&  x > srccode.indexOf(endtag)) {
//            throw new RuntimeException(starttag+ " not closed ");
//        }
        return x;
    }

    private static final String getActivitiesCode(boDefHandler obj) {
        boDefActivity[] act = obj.getBoClsActivities();
        StringBuffer code = new StringBuffer();
        if(act!=null) {
            for (byte i = 0; i < act.length; i++)  {
                String modifier ="";
                String args="";
                if(act[i].getActivityType()==boDefActivity.TYPE_STATIC) {
                    modifier="static";
                    args = "EboContext ctx";
                }
                code.append("\t public "+modifier+" boolean Activity_"+act[i].getName()+"("+args+") throws boRuntimeException {\n\r");
                code.append("\t\t"+act[i].getBody()+"\n\r");
                code.append("}\n\r\n\r");
            }
            code.append("\t public boolean runActivity(String name) throws boRuntimeException {\n\r");
            code.append("\t\t boolean ret = true;");
            for (byte i = 0; i < act.length; i++)  {
                if(act[i].getActivityType()==boDefActivity.TYPE_INSTANCE) {
                    code.append("\t\tif(name.equals(\"").append(act[i].getName()).append("\")) ret = ret && Activity_").append(act[i].getName());
                    code.append("();\n\r");
                }
            }
            code.append("\t return ret;\n\r}");

        }
        return code.toString();
    }
}

