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
    private static Logger logger = Logger.getLogger("netgest.bo.userquery.queryBuilderHelper"); //$NON-NLS-1$
    private final static String[] NOT_INCLUDE = {"TEMPLATE", "PARENTCTX", "SYS_ORIGIN" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                          };
    
    public MapTempAtribTreeHelper()
    {
    }

    public static StringBuffer getXMLAttributes( EboContext ctx, long templateBoui ) throws boRuntimeException
    {

        boObject templateObject = boObject.getBoManager().loadObject( ctx, templateBoui );
        StringBuffer attributesStr = new StringBuffer(""); //$NON-NLS-1$
        attributesStr.append( getQueries( templateObject ) ); 

        return attributesStr;
    }
    
    public static char[] getQueries( boObject templateObj ) throws boRuntimeException
    {
        CharArrayWriter sbQueryFields = new CharArrayWriter();
        PrintWriter pw = new PrintWriter( sbQueryFields );
        
        pw.print("<campos>"); //$NON-NLS-1$
        
        int ix = 0;
        boBridgeIterator it = templateObj.getBridge("queries").iterator(); //$NON-NLS-1$
        it.beforeFirst();
        while( it.next() )
        {
            boObject objQuery = it.currentRow().getObject();
            String idQuery    = objQuery.getAttribute("nome").getValueString(); //$NON-NLS-1$
            String idParam    = idQuery;
            if(objQuery.getAttribute("parametro").getObject() != null) //$NON-NLS-1$
            {
                idParam = objQuery.getAttribute("parametro").getObject().getAttribute("nome").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
            }
            String label = objQuery.getAttribute("pergunta").getValueString(); //$NON-NLS-1$
            if( label.trim().length() == 0 )
            {
                label = objQuery.getAttribute("nome").getValueString(); //$NON-NLS-1$
            }
            pw.print( "<"+ idQuery + " type='1' relation='1'>"); //$NON-NLS-1$ //$NON-NLS-2$
            pw.print( "<value>"+objQuery.getBoui()+"</value>"); //$NON-NLS-1$ //$NON-NLS-2$
            pw.print( "<label>"+label+"</label>"); //$NON-NLS-1$ //$NON-NLS-2$
            pw.print( "<level>0</level>"); //$NON-NLS-1$
            pw.print( "<prefixlabel/>" ); //$NON-NLS-1$
            pw.print( "<objectLabel>"+label+"</objectLabel>"); //$NON-NLS-1$ //$NON-NLS-2$
            pw.print( "</"+ idQuery + ">"); //$NON-NLS-1$ //$NON-NLS-2$
            getQueryFields( objQuery.getBridge("campos").iterator(), pw, idQuery, idQuery, 1 );             //$NON-NLS-1$
        }

        // Manual fieds
        getQueryFields( templateObj.getBridge("camposManuais").iterator(), pw, null, null, 0 ); //$NON-NLS-1$
        pw.print("</campos>"); //$NON-NLS-1$
        pw.close();
        sbQueryFields.close();
        
        return sbQueryFields.toCharArray();
    }
    
    public static void getBridgeField( boObject bridgeField, PrintWriter pw, String parentQuery, String parentTree, int level ) throws boRuntimeException
    {
        int ix = 0;
        String idBridgeField = bridgeField.getAttribute("nome").getValueString(); //$NON-NLS-1$
        String label = bridgeField.getAttribute("pergunta").getValueString(); //$NON-NLS-1$
        if( label.trim().length() == 0 )
        {
            label = bridgeField.getAttribute("nome").getValueString(); //$NON-NLS-1$
        }
        pw.print( "<"+ parentQuery + "." + idBridgeField + " type='1' relation='1'>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        pw.print( "<value>"+bridgeField.getBoui()+"</value>"); //$NON-NLS-1$ //$NON-NLS-2$
        pw.print( "<label>"+label+"</label>"); //$NON-NLS-1$ //$NON-NLS-2$
        pw.print( "<level>"+level+"</level>"); //$NON-NLS-1$ //$NON-NLS-2$
        pw.print( "<prefixlabel/>" ); //$NON-NLS-1$
        pw.print( "<objectLabel>"+label+"</objectLabel>"); //$NON-NLS-1$ //$NON-NLS-2$
        pw.print( "</"+ parentQuery + "." + idBridgeField + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        getQueryFields( bridgeField.getBridge("campos").iterator(), pw, parentQuery + "." + idBridgeField, parentQuery + "." + idBridgeField, level+1 );             //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    }
    
    
    private static boolean isBridgeField(String objName)
    {
        if("GESTEMP_CampoNFormula".equals(objName) || "GESTEMP_CampoNJava".equals(objName) || "GESTEMP_CampoNObjecto".equals(objName)) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
                String id = fieldObj.getAttribute("nome").getValueString(); //$NON-NLS-1$
                String label = fieldObj.getAttribute("pergunta").getValueString(); //$NON-NLS-1$
                
                if( label.trim().length() == 0 )
                {
                    label = fieldObj.getAttribute("nome").getValueString(); //$NON-NLS-1$
                }
                if(parentQuery == null)
                    pw.print( "<"+ id + " type='1' relation='1'>"); //$NON-NLS-1$ //$NON-NLS-2$
                else            
                    pw.print( "<"+ parentQuery + "." + id + " type='1' relation='1'>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                pw.print( "<value>"+fieldObj.getBoui()+"</value>"); //$NON-NLS-1$ //$NON-NLS-2$
                pw.print( "<label>"+label+"</label>"); //$NON-NLS-1$ //$NON-NLS-2$
                pw.print( "<level>"+level+"</level>"); //$NON-NLS-1$ //$NON-NLS-2$
                pw.print( "<prefixlabel/>" ); //$NON-NLS-1$
                pw.print( "<objectLabel>"+label+"</objectLabel>"); //$NON-NLS-1$ //$NON-NLS-2$
                if(parentQuery == null)
                    pw.print( "</"+ id + ">"); //$NON-NLS-1$ //$NON-NLS-2$
                else
                    pw.print( "</"+ parentQuery + "." + id + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
        
        StringBuffer attributesStr = new StringBuffer(""); //$NON-NLS-1$

        attributesStr.append("attributes=[];\n"); //$NON-NLS-1$
        attributesStr.append(Messages.getString("MapTempAtribTreeHelper.154")); //$NON-NLS-1$
        attributesStr.append("foldersTree.xID='rootNode';\n"); //$NON-NLS-1$
        if(templateObject != null)
            attributesStr.append( getJavaScriptQueries( templateObject ) ); 

        return attributesStr;
    }
    
    public static char[] getJavaScriptQueries( boObject templateObj ) throws boRuntimeException
    {
        CharArrayWriter sbQueryFields = new CharArrayWriter();
        PrintWriter pw = new PrintWriter( sbQueryFields );
        
        StringBuffer toAdd = new StringBuffer("["); //$NON-NLS-1$
        
        int ix = 0;
        boBridgeIterator it = templateObj.getBridge("queries").iterator(); //$NON-NLS-1$
        it.beforeFirst();
        while( it.next() )
        {
            boObject objQuery = it.currentRow().getObject();
            String idQuery    = objQuery.getAttribute("nome").getValueString(); //$NON-NLS-1$
            String idParam    = idQuery;
            String label = objQuery.getAttribute("pergunta").getValueString(); //$NON-NLS-1$
            if( label.trim().length() == 0 )
            {
                label = objQuery.getAttribute("nome").getValueString(); //$NON-NLS-1$
            }
            pw.println("attributes[" +(ix)+"]=\""+idParam+"\";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            pw.println("foldersTree_"+idQuery+"=gFld(\""+label+"\", \"javascript:selAttribute("+ix+")\");"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            pw.println("foldersTree_"+idQuery+".xID='" +ix+"';"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            ix = getJavaScriptQueryFields(objQuery.getBridge("campos").iterator(), pw, idQuery,idQuery, "foldersTree_"+idQuery, ix+1 ); //$NON-NLS-1$ //$NON-NLS-2$
            
            if( toAdd.length() > 1 ) 
            {
                toAdd.append(',');
            }
            toAdd.append( "foldersTree_"+idQuery ); //$NON-NLS-1$
        }

        // Manual fieds
        {
            String id = "manualFields"; //$NON-NLS-1$
            String label = "Campos Manuais"; //$NON-NLS-1$
            pw.println("attributes[" +(ix)+"]=\""+id+"\";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            pw.println("foldersTree_"+id+"=gFld(\""+label+"\", \"javascript:void(0)\");"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            pw.println("foldersTree_"+id+".xID='" +ix+"';"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            ix = getJavaScriptQueryFields(templateObj.getBridge("camposManuais").iterator(), pw, null,null, "foldersTree_"+id, ix ); //$NON-NLS-1$ //$NON-NLS-2$
            if( toAdd.length() > 1 ) 
            {
                toAdd.append(',');
            }
            toAdd.append( "foldersTree_"+id ); //$NON-NLS-1$
        }

        toAdd.append(']');
        pw.print( "foldersTree.addChildren("); //$NON-NLS-1$
        pw.print( toAdd );
        pw.println( ");"); //$NON-NLS-1$


        
        pw.close();
        sbQueryFields.close();
        
        return sbQueryFields.toCharArray();
    }
    
    public static int getJavaScriptBridgeField( boObject bridgeField, PrintWriter pw, String parentQuery, String parentTree, int index) throws boRuntimeException
    {
        String id    = bridgeField.getAttribute("nome").getValueString(); //$NON-NLS-1$
        String label = bridgeField.getAttribute("pergunta").getValueString(); //$NON-NLS-1$
        if( label.trim().length() == 0 )
        {
            label = bridgeField.getAttribute("nome").getValueString(); //$NON-NLS-1$
        }
        
        pw.println("attributes[" +(index)+"]=\""+parentQuery+"."+id+"\";");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        pw.println("foldersTree_"+parentQuery+"_"+id+"=gFld(\""+label+"\", \"javascript:selAttribute("+index+")\");"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        pw.println("foldersTree_"+parentQuery+"_"+id+".xID='" +index+"';"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        index = getJavaScriptQueryFields(bridgeField.getBridge("campos").iterator(), pw, parentQuery+"."+id,parentQuery+"_"+id, "foldersTree_"+parentQuery+"_"+id, index+1 ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        return index;
    }
    public static int getJavaScriptQueryFields( boBridgeIterator it, PrintWriter pw, String parentQuery, String parentVar, String parentTree, int index) throws boRuntimeException
    {
        StringBuffer toAdd = new StringBuffer("["); //$NON-NLS-1$
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
                toAdd.append( "foldersTree_"+parentQuery+"_"+fieldObj.getAttribute("nome").getValueString() ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            else
            {
                String id = fieldObj.getAttribute("nome").getValueString(); //$NON-NLS-1$
                String label = fieldObj.getAttribute("pergunta").getValueString(); //$NON-NLS-1$
                
                if( label.trim().length() == 0 )
                {
                    label = fieldObj.getAttribute("nome").getValueString(); //$NON-NLS-1$
                }
                if(parentQuery == null)
                {
                    pw.println("attributes[" +(index)+"]=\""+id+"\";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    pw.println(id+"=[\"" +label+"\", \"javascript:selAttribute("+index+")\"];"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    pw.println(id+".xID='" +index+"';"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                    pw.println("attributes[" +(index)+"]=\""+parentQuery+"."+id+"\";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    pw.println(parentVar+"_"+id+"=[\"" +label+"\", \"javascript:selAttribute("+index+")\"];"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    pw.println(parentVar+"_"+id+".xID='" +index+"';"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
                    toAdd.append( parentVar+"_"+id ); //$NON-NLS-1$
                }
            }
        }
        toAdd.append("]"); //$NON-NLS-1$
        pw.print( parentTree + ".addChildren("); //$NON-NLS-1$
        pw.print( toAdd.toString() );
        pw.println( ");"); //$NON-NLS-1$
        return index;
    }

    public static class MergeFieldFolder
    {
        
    }
    
    public static class MergeFieldItem
    {
        
    }
}
