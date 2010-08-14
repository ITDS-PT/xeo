/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge.gestemp.presentation;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import netgest.bo.def.*;

import netgest.bo.dochtml.*;

import netgest.bo.ql.*;

import netgest.bo.runtime.*;

import netgest.utils.*;

import java.util.*;
import netgest.bo.system.Logger;


public class MapTempAtribTreeHelper
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.userquery.queryBuilderHelper");
    private final static String[] NOT_INCLUDE = {"TEMPLATE", "PARENTCTX", "SYS_ORIGIN"
                                                          };
    
    public MapTempAtribTreeHelper()
    {
    }

    public static StringBuffer getXMLAttributes( EboContext ctx, long templateBoui ) throws boRuntimeException
    {

        boObject templateObject = boObject.getBoManager().loadObject( ctx, templateBoui );
        StringBuffer attributesStr = new StringBuffer("");
        attributesStr.append( getQueries( templateObject ) ); 

        return attributesStr;
    }
    
    public static char[] getQueries( boObject templateObj ) throws boRuntimeException
    {
        CharArrayWriter sbQueryFields = new CharArrayWriter();
        PrintWriter pw = new PrintWriter( sbQueryFields );
        
        pw.print("<campos>");
        
        int ix = 0;
        boBridgeIterator it = templateObj.getBridge("queries").iterator();
        it.beforeFirst();
        while( it.next() )
        {
            boObject objQuery = it.currentRow().getObject();
            String idQuery    = objQuery.getAttribute("nome").getValueString();
            String idParam    = idQuery;
            if(objQuery.getAttribute("parametro").getObject() != null)
            {
                idParam = objQuery.getAttribute("parametro").getObject().getAttribute("nome").getValueString();
            }
            String label = objQuery.getAttribute("pergunta").getValueString();
            if( label.trim().length() == 0 )
            {
                label = objQuery.getAttribute("nome").getValueString();
            }
            pw.print( "<"+ idQuery + " type='1' relation='1'>");
            pw.print( "<value>"+objQuery.getBoui()+"</value>");
            pw.print( "<label>"+label+"</label>");
            pw.print( "<level>0</level>");
            pw.print( "<prefixlabel/>" );
            pw.print( "<objectLabel>"+label+"</objectLabel>");
            pw.print( "</"+ idQuery + ">");
            getQueryFields( objQuery.getBridge("campos").iterator(), pw, idQuery, idQuery, 1 );            
        }

        // Manual fieds
        getQueryFields( templateObj.getBridge("camposManuais").iterator(), pw, null, null, 0 );
        pw.print("</campos>");
        pw.close();
        sbQueryFields.close();
        
        return sbQueryFields.toCharArray();
    }
    
    public static void getBridgeField( boObject bridgeField, PrintWriter pw, String parentQuery, String parentTree, int level ) throws boRuntimeException
    {
        int ix = 0;
        String idBridgeField = bridgeField.getAttribute("nome").getValueString();
        String label = bridgeField.getAttribute("pergunta").getValueString();
        if( label.trim().length() == 0 )
        {
            label = bridgeField.getAttribute("nome").getValueString();
        }
        pw.print( "<"+ parentQuery + "." + idBridgeField + " type='1' relation='1'>");
        pw.print( "<value>"+bridgeField.getBoui()+"</value>");
        pw.print( "<label>"+label+"</label>");
        pw.print( "<level>"+level+"</level>");
        pw.print( "<prefixlabel/>" );
        pw.print( "<objectLabel>"+label+"</objectLabel>");
        pw.print( "</"+ parentQuery + "." + idBridgeField + ">");
        getQueryFields( bridgeField.getBridge("campos").iterator(), pw, parentQuery + "." + idBridgeField, parentQuery + "." + idBridgeField, level+1 );            

    }
    
    
    private static boolean isBridgeField(String objName)
    {
        if("GESTEMP_CampoNFormula".equals(objName) || "GESTEMP_CampoNJava".equals(objName) || "GESTEMP_CampoNObjecto".equals(objName))
        {
            return true;
        }
        return false;
    }
    
    public static void getQueryFields( boBridgeIterator it, PrintWriter pw, String parentQuery, String parentTree, int level) throws boRuntimeException
    {
        int ix = 0;
        //boBridgeIterator it = objQuery.getBridge("campos").iterator();
        it.beforeFirst();
        while( it.next() )
        {
            boObject fieldObj = it.currentRow().getObject();
            if(isBridgeField(fieldObj.getName()))
            {
                getBridgeField(fieldObj,  pw, parentQuery, parentTree,level);
            }
            else
            {
                String id = fieldObj.getAttribute("nome").getValueString();
                String label = fieldObj.getAttribute("pergunta").getValueString();
                
                if( label.trim().length() == 0 )
                {
                    label = fieldObj.getAttribute("nome").getValueString();
                }
                if(parentQuery == null)
                    pw.print( "<"+ id + " type='1' relation='1'>");
                else            
                    pw.print( "<"+ parentQuery + "." + id + " type='1' relation='1'>");
                pw.print( "<value>"+fieldObj.getBoui()+"</value>");
                pw.print( "<label>"+label+"</label>");
                pw.print( "<level>"+level+"</level>");
                pw.print( "<prefixlabel/>" );
                pw.print( "<objectLabel>"+label+"</objectLabel>");
                if(parentQuery == null)
                    pw.print( "</"+ id + ">");
                else
                    pw.print( "</"+ parentQuery + "." + id + ">");
            }
        }
    }
    
    //--------------------------JAVASCRIPT TREE
    public static StringBuffer getJavaScriptTree(EboContext ctx, long templateBoui) throws boRuntimeException
    {
        boObject templateObject = null;
        
        try
        {
            templateObject = boObject.getBoManager().loadObject( ctx, templateBoui );
        }
        catch (Exception e)
        {
            //ignore
        }
        
        StringBuffer attributesStr = new StringBuffer("");

        attributesStr.append("attributes=[];\n");
        attributesStr.append(Messages.getString("MapTempAtribTreeHelper.154"));
        attributesStr.append("foldersTree.xID='rootNode';\n");
        if(templateObject != null)
            attributesStr.append( getJavaScriptQueries( templateObject ) ); 

        return attributesStr;
    }
    
    public static char[] getJavaScriptQueries( boObject templateObj ) throws boRuntimeException
    {
        CharArrayWriter sbQueryFields = new CharArrayWriter();
        PrintWriter pw = new PrintWriter( sbQueryFields );
        
        StringBuffer toAdd = new StringBuffer("[");
        
        int ix = 0;
        boBridgeIterator it = templateObj.getBridge("queries").iterator();
        it.beforeFirst();
        while( it.next() )
        {
            boObject objQuery = it.currentRow().getObject();
            String idQuery    = objQuery.getAttribute("nome").getValueString();
            String idParam    = idQuery;
            String label = objQuery.getAttribute("pergunta").getValueString();
            if( label.trim().length() == 0 )
            {
                label = objQuery.getAttribute("nome").getValueString();
            }
            pw.println("attributes[" +(ix)+"]=\""+idParam+"\";");
            pw.println("foldersTree_"+idQuery+"=gFld(\""+label+"\", \"javascript:selAttribute("+ix+")\");");
            pw.println("foldersTree_"+idQuery+".xID='" +ix+"';");
            ix = getJavaScriptQueryFields(objQuery.getBridge("campos").iterator(), pw, idQuery,idQuery, "foldersTree_"+idQuery, ix+1 );
            
            if( toAdd.length() > 1 ) 
            {
                toAdd.append(',');
            }
            toAdd.append( "foldersTree_"+idQuery );
        }

        // Manual fieds
        {
            String id = "manualFields";
            String label = "Campos Manuais";
            pw.println("attributes[" +(ix)+"]=\""+id+"\";");
            pw.println("foldersTree_"+id+"=gFld(\""+label+"\", \"javascript:void(0)\");");
            pw.println("foldersTree_"+id+".xID='" +ix+"';");
            ix = getJavaScriptQueryFields(templateObj.getBridge("camposManuais").iterator(), pw, null,null, "foldersTree_"+id, ix );
            if( toAdd.length() > 1 ) 
            {
                toAdd.append(',');
            }
            toAdd.append( "foldersTree_"+id );
        }

        toAdd.append(']');
        pw.print( "foldersTree.addChildren(");
        pw.print( toAdd );
        pw.println( ");");


        
        pw.close();
        sbQueryFields.close();
        
        return sbQueryFields.toCharArray();
    }
    
    public static int getJavaScriptBridgeField( boObject bridgeField, PrintWriter pw, String parentQuery, String parentTree, int index) throws boRuntimeException
    {
        String id    = bridgeField.getAttribute("nome").getValueString();
        String label = bridgeField.getAttribute("pergunta").getValueString();
        if( label.trim().length() == 0 )
        {
            label = bridgeField.getAttribute("nome").getValueString();
        }
        
        pw.println("attributes[" +(index)+"]=\""+parentQuery+"."+id+"\";"); 
        pw.println("foldersTree_"+parentQuery+"_"+id+"=gFld(\""+label+"\", \"javascript:selAttribute("+index+")\");");
        pw.println("foldersTree_"+parentQuery+"_"+id+".xID='" +index+"';");
        index = getJavaScriptQueryFields(bridgeField.getBridge("campos").iterator(), pw, parentQuery+"."+id,parentQuery+"_"+id, "foldersTree_"+parentQuery+"_"+id, index+1 );
        return index;
    }
    public static int getJavaScriptQueryFields( boBridgeIterator it, PrintWriter pw, String parentQuery, String parentVar, String parentTree, int index) throws boRuntimeException
    {
        StringBuffer toAdd = new StringBuffer("[");
        while( it.next() )
        {
            boObject fieldObj = it.currentRow().getObject();
            if(isBridgeField(fieldObj.getName()))
            {
                index = getJavaScriptBridgeField(fieldObj, pw, parentQuery, parentTree, index);
                if( toAdd.length() > 1 ) 
                {
                    toAdd.append(',');
                }
                toAdd.append( "foldersTree_"+parentQuery+"_"+fieldObj.getAttribute("nome").getValueString() );
            }
            else
            {
                String id = fieldObj.getAttribute("nome").getValueString();
                String label = fieldObj.getAttribute("pergunta").getValueString();
                
                if( label.trim().length() == 0 )
                {
                    label = fieldObj.getAttribute("nome").getValueString();
                }
                if(parentQuery == null)
                {
                    pw.println("attributes[" +(index)+"]=\""+id+"\";");
                    pw.println(id+"=[\"" +label+"\", \"javascript:selAttribute("+index+")\"];");
                    pw.println(id+".xID='" +index+"';");
                }
                else
                {
                    pw.println("attributes[" +(index)+"]=\""+parentQuery+"."+id+"\";");
                    pw.println(parentVar+"_"+id+"=[\"" +label+"\", \"javascript:selAttribute("+index+")\"];");
                    pw.println(parentVar+"_"+id+".xID='" +index+"';");
                }
                index++;
                if( toAdd.length() > 1 ) 
                {
                    toAdd.append(',');
                }
                if(parentQuery == null)
                {
                    toAdd.append( id );
                }
                else
                {
                    toAdd.append( parentVar+"_"+id );
                }
            }
        }
        toAdd.append("]");
        pw.print( parentTree + ".addChildren(");
        pw.print( toAdd.toString() );
        pw.println( ");");
        return index;
    }

    public static class MergeFieldFolder
    {
        
    }
    
    public static class MergeFieldItem
    {
        
    }
}
