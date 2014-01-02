/*Enconding=UTF-8*/
package netgest.bo.builder;
import com.ibm.regex.REUtil;
import com.ibm.regex.RegularExpression;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import netgest.bo.def.*;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;

import netgest.bo.*;
import netgest.utils.*;

/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class boBuildStates extends boAttributesArray
{

    public void build(boDefHandler bodef) throws boRuntimeException {
        try {
            Vector srcfiles = new Vector();
            boConfig bocfg = new boConfig();
            
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( "netgest/bo/builder/templates/StateTemplate.xml" );
            XMLDocument doc = ngtXMLUtils.loadXML( is );
            String textContent = ((XMLElement)doc.getDocumentElement()).getText();
            
            String srctemp = generateSrcFile( textContent, bodef );

            String version="v"+bodef.getBoVersion();
            version = version.replace('.','_');

            srctemp = "package "+version+";\n\r"+srctemp;

            File srcdir = new File(bocfg.getDeploymentsrcdir()+version);
            srcdir.mkdirs();
            File srcfile = new File(bocfg.getDeploymentsrcdir()+version+File.separator+bodef.getBoName()+"StateManager.java");
            srcfile.createNewFile();
            FileWriter fw = new FileWriter(srcfile);
            fw.write(srctemp);
            fw.close();
            srcfiles.add(srcfile);

            File[] a_srcfiles= new File[srcfiles.size()];
            srcfiles.toArray(a_srcfiles);

            boClassCompiler comp = new boClassCompiler();
            comp.compile(bocfg.getDeploymentsrcdir(),a_srcfiles,bocfg.getDeploymentclassdir());

        } catch (IOException e) {
            throw new boException(this.getClass().getName()+"build(String)","BO-1601",e,bodef.getBoName());
        } catch (RuntimeException e) {
//            e.printStackTrace();
            throw new boException(this.getClass().getName()+"build(String)","BO-1601",e,bodef.getBoName());
        }
    }

    private String generateSrcFile(String srccode,boDefHandler bodef) {

        RegularExpression regexp = new RegularExpression("");

        if(bodef.getBoExtendsClass()!=null)
        {
            srccode = StringUtils.replacestr(srccode,"#BO.SUPERCLASS#",bodef.getBoExtendsClass());
        }
        else
        {
            if(bodef.getBoSuperBo() !=null)
            {
                srccode = StringUtils.replacestr(srccode,"#BO.SUPERCLASS#",bodef.getBoSuperBo());
            }
            else
            {
                srccode = StringUtils.replacestr(srccode,"#BO.SUPERCLASS#","boObjectState");
            }
        }


        // Remove any extra data in template
        String starttag = "//@REMOVE";
        String endtag = "//@ENDREMOVE";
        while(srccode.indexOf(starttag)>-1) {
            srccode = srccode.substring(0,srccode.indexOf(starttag)-1)+srccode.substring(srccode.indexOf(endtag)+endtag.length(),srccode.length());
        }

        // Replace business object properties
         Hashtable htbo = fillRptObject(bodef);
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
        srccode = srccode.replaceAll("_boTemplate_",bodef.getName()+"StateManager");

        srccode = parseStateAttributes(bodef.getBoClsState(),srccode);

        StringBuffer gmethods = new StringBuffer();
        gmethods.append(getEvents(bodef.getBoClsEvents()));


        srccode = srccode.replaceAll("#BO.EVENTS#",gmethods.toString());

        // Build Methods activities and events
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

    private static final int checktags(String starttag,String endtag,String srccode) {
        int x = srccode.indexOf(starttag);
        if(x!= -1 &&  x > srccode.indexOf(endtag)) {
            throw new RuntimeException(starttag+ " "+MessageLocalizer.getMessage("NOT_CLOSED"));
        }
        return x;
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

                ht.put("#ATT.DBNAME#",state.getDbName());
                ht.put("#ATT.PARENT#",state.getName());

                Enumeration oEnum = ht.keys();
                while(oEnum.hasMoreElements()) {
                    String key = (String)oEnum.nextElement();
                    String value = (String)ht.get(key);
                    if( value.indexOf("$")>-1 ) {
                        value = StringUtils.replacestr(value,"$","\\$");
                    }
                    regexp.setPattern(key);
                    area = REUtil.substitute(regexp,value,true,area);
                }
                area = StringUtils.replacestr(area,"#OBJECT.EVENTS#",getEventsCalls(state.getBoDefHandler().getBoClsEvents(),atts[i].getName(), state.getName() ));
                have=true;
                if(have) rarea += area;
            }
            srccode = srccode.substring(0,srccode.indexOf(starttag)-1)+rarea+srccode.substring(srccode.indexOf(endtag)+endtag.length(),srccode.length());
        }
        return srccode;
    }

    private static final Hashtable fillRptStateAttributes(boDefClsState att) {
        Hashtable ht = new Hashtable();
        ht.put("#ATT.NAME#",att.getName());
        ht.put("#ATT.DBNAME#",att.getDbName());
        ht.put("#ATT.DBBINDING#",att.getDbIsBinding()?"true":"false");
        ht.put("#ATT.JAVADATATYPEOBJ#",boClassBuilder.parseJavaDataType(att.getType(),boClassBuilder.TYPE_OBJECT));
        ht.put("#ATT.JAVADATATYPE#",boClassBuilder.parseJavaDataType(att.getType(),boClassBuilder.TYPE_SIMPLE));
        ht.put("#ATT.PRIMITIVEDATATYPE#",boClassBuilder.parseJavaDataType(att.getType(),boClassBuilder.TYPE_DATA));
        ht.put("#ATT.NUMERICFORM#",""+att.getNumericForm());
        ht.put("#ATT.TYPE#",att.getType());

//        if(att.getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE)
//            ht.put("#ATT.REFOBJECTNAME#",att.getReferencedObjectName());
        return ht;
    }

    private static String getEventsCalls(boDefClsEvents[] events,String attname,String parent) {

        StringBuffer smeth = new StringBuffer();
        for (byte i = 0;events != null && i < events.length; i++)
        {
            String name = events[i].getEventName();
            if( (attname == null && name.indexOf('.')==-1) ||
                (attname != null && name.substring(name.lastIndexOf('.')+1).equals(attname) )

              )
            {
                if( name.indexOf('.') >-1 )
                {
                    name = name.substring( 0 , name.indexOf('.') );
                }

                smeth
                .append( "public static " );
                if( name.equalsIgnoreCase("onValid") )
                {
                    smeth.append( " boolean " );
                }
                else
                {
                    smeth.append( " void " );
                }

                smeth
                .append( name )
                .append( " ( ) throws boRuntimeException " )
                .append( " \n{ \n" );
                if( name.equalsIgnoreCase("onValid") )
                {
                    smeth.append(" return ");
                }
                smeth
                .append( name )
                .append( "_" )
                .append( parent )
                .append( "_" )
                .append( attname )
                .append( "( object );" )
                .append( '\n' )
                .append( " } \n" );

            }
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
                    smeth.append(name).append("( boObject object ) throws boRuntimeException {\n\r\t");
                    smeth.append(body);
                    smeth.append("\n\r}\n\r");
                }
            }
        }
        return smeth.toString();
    }
    private static final Hashtable fillRptObject(boDefHandler bodef) {
        Hashtable ht = new Hashtable();
        ht.put("#BO.VERSION#",bodef.getBoMajorVersion()+"."+bodef.getBoMinorVersion());
        ht.put("#BO.NAME#",bodef.getBoName());
        ht.put("#BO.MODIFIERS#",bodef.getClassType()==boDefHandler.TYPE_ABSTRACT_CLASS?"abstract":"");
        //ht.put("#BO.MODIFIERS#",bodef.getBoIsAbstract()?"":"");
        return ht;
    }

    public static String getMissedEvents( boDefClsEvents[] evs , String attname , String parent )
    {
        StringBuffer sb = new StringBuffer();

        if( !haveEvent( evs , "onLoad_" + parent + "_" + attname ) )
            sb.append(" public void OnLoad_").append(parent).append("_").append(attname).append(" () {}");

        return sb.toString();
    }
    private static boolean haveEvent(boDefClsEvents[] evs,String name)
    {
        boolean ret= false;
        for (short i = 0; i < evs.length; i++)
        {
            if( evs[i].getEventName().equals(name) )
            {
                ret = true;
                break;
            }
        }
        return ret;
    }

}