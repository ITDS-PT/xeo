package netgest.bo.impl.document.merge.gestemp.presentation;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import netgest.bo.def.*;

import netgest.bo.dochtml.*;

import netgest.bo.impl.document.merge.gestemp.SpecialField;
import netgest.bo.ql.*;

import netgest.bo.runtime.*;

import netgest.utils.*;

import java.util.*;
import org.apache.log4j.Logger;

public class MergeFieldTreeHelper 
{

    public static StringBuffer getJavaScriptTree( EboContext ctx, long templateBoui ) throws boRuntimeException
    {

        boObject templateObject = boObject.getBoManager().loadObject( ctx, templateBoui );
        StringBuffer attributesStr = new StringBuffer(""); //$NON-NLS-1$

        attributesStr.append("attributes=[];\n"); //$NON-NLS-1$
        attributesStr.append(Messages.getString("MergeFieldTreeHelper.12")); //$NON-NLS-1$
        attributesStr.append("foldersTree.xID='rootNode';\n"); //$NON-NLS-1$
        
        Vector toAdd = new Vector();
        

        attributesStr.append( getSystemFields( toAdd ) ); 

        attributesStr.append( getStaticFields( ctx, toAdd ) ); 
        
        attributesStr.append( getQueries( toAdd,templateObject ) ); 
        
        attributesStr.append('\t');
        
        attributesStr.append( "foldersTree.addChildren(["); //$NON-NLS-1$
        for (int i = 0; i < toAdd.size(); i++) 
        {
            if( i > 0 ) attributesStr.append( ',' );
            attributesStr.append( toAdd.get( i ) );
            
        }
        attributesStr.append( "]);"); //$NON-NLS-1$

        return attributesStr;
    }
    
    public static char[] getStaticFields( EboContext ctx, Vector toAdd ) throws boRuntimeException
    {
        boObjectList list = boObjectList.list( ctx, "select GESTEMP_CampoFixo",1,1000 );
        if( list.next() )
        {
            CharArrayWriter sbQueryFields = new CharArrayWriter();
            PrintWriter pw = new PrintWriter( sbQueryFields );

            composeFolder( pw, "cfixo", "cfixo1", "Campos Fixos" );
            StringBuffer sb = new StringBuffer();
            list.beforeFirst();
            int i = 0;
            while( list.next() )
            {
                i++;
                
                boObject boCampoFixo = list.getObject();

                String nome = boCampoFixo.getAttribute("nome").getValueString();
                String descricao = boCampoFixo.getAttribute("descricao").getValueString();
                String tipo = boCampoFixo.getAttribute("tipo").getValueString();
                if( "2".equals( tipo ) )
                {
                    // Tipo Imagem    
                    composeField(  pw, "cfixo_"+nome ,"cfixo1"+i,descricao, "cfixo_"+nome+"(image)");
                }
                else
                {
                    // Tipo Texto
                    composeField(  pw, "cfixo_"+nome ,"cfixo1"+i,descricao, "cfixo_"+nome);
                }
                if( sb.length() > 0 )
                {
                    sb.append( ',' );
                }
                sb.append( "cfixo_"+nome );
                
            }
            pw.print("cfixo.addChildren(["+sb+"]);");
            toAdd.add( "cfixo" );
            pw.close();
            sbQueryFields.close();
            return sbQueryFields.toCharArray();
        }
        return new char[0];
    }
    
    public static char[] getQueries( Vector toAdd, boObject templateObj ) throws boRuntimeException
    {
        CharArrayWriter sbQueryFields = new CharArrayWriter();
        PrintWriter pw = new PrintWriter( sbQueryFields );
        
        int ix = 0;
        boBridgeIterator it = templateObj.getBridge("queries").iterator(); //$NON-NLS-1$
        it.beforeFirst();
        while( it.next() )
        {
            boObject objQuery = it.currentRow().getObject();
            String id    = objQuery.getAttribute("nome").getValueString(); //$NON-NLS-1$
            String label = objQuery.getAttribute("pergunta").getValueString(); //$NON-NLS-1$
            if( label.trim().length() == 0 )
            {
                label = objQuery.getAttribute("nome").getValueString(); //$NON-NLS-1$
            }
            pw.println( "var "+ id + " = gFld('"+ label +"','javascript:void(0)');"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            pw.println( id + ".xID='xq"+(++ix)+"'"); //$NON-NLS-1$ //$NON-NLS-2$
            getQueryFields( objQuery.getBridge("campos").iterator(), pw, id, id ); //$NON-NLS-1$
            toAdd.add( id );
        }

        // Manual fieds
        {
            String id = "manualFields"; //$NON-NLS-1$
            pw.println( "var manualFields = gFld('Campos Manuais','javascript:void(0)');"); //$NON-NLS-1$
            pw.println( id + ".xID='xm"+(++ix)+"'"); //$NON-NLS-1$ //$NON-NLS-2$
            getQueryFields( templateObj.getBridge("camposManuais").iterator(), pw, id, null ); //$NON-NLS-1$
    
            toAdd.add( id );
        }
        pw.close();
        sbQueryFields.close();
        
        return sbQueryFields.toCharArray();
    }
    
    public static void getQueryFields( boBridgeIterator it, PrintWriter pw, String parentTree, String parentQuery ) throws boRuntimeException
    {
        int ix = 0;
        //boBridgeIterator it = objQuery.getBridge("campos").iterator();
        StringBuffer toAdd = new StringBuffer("["); //$NON-NLS-1$
        while( it.next() )
        {
            boObject fieldObj = it.currentRow().getObject();
            
            String id = fieldObj.getAttribute("nome").getValueString(); //$NON-NLS-1$
            String label = fieldObj.getAttribute("pergunta").getValueString(); //$NON-NLS-1$
            String jsid = parentTree + "_" + id; //$NON-NLS-1$

            if( label.trim().length() == 0 )
            {
                label = fieldObj.getAttribute("nome").getValueString(); //$NON-NLS-1$
            }
            String chooseId = parentQuery!=null?(parentQuery+ "__" + id):id; //$NON-NLS-1$
            String tipo = fieldObj.getAttribute("tipo").getValueString(); //$NON-NLS-1$
            if( "8".equals( tipo ) ) // Campo de imagem //$NON-NLS-1$
            {
                chooseId += "(image)"; //$NON-NLS-1$
            }

            if( "GESTEMP_CampoNObjecto".equals( fieldObj.getName() ) ) //$NON-NLS-1$
            {
                pw.println( "var "+jsid+" = gFld('"+label+" (Bookmark)','javascript:_selBookmark(\"" + chooseId + "\")');"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                pw.println( jsid + ".xID='xb"+(++ix)+"'"); //$NON-NLS-1$ //$NON-NLS-2$
                getBookmarkFields( pw, fieldObj, chooseId ,jsid, "", ix ); //$NON-NLS-1$
            } 
            else if ( "GESTEMP_CampoNManual".equals( fieldObj.getName() ) ) //$NON-NLS-1$
            { 
                pw.println( "var "+jsid+" = gFld('"+label + " (Bookmark)" +"','javascript:_selBookmark(\"" + chooseId + "\")');"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                pw.println( jsid + ".xID='xb"+(++ix)+"'"); //$NON-NLS-1$ //$NON-NLS-2$

                pw.println( "var " + jsid + "_" + jsid + " = ['"+label+"',\"javascript:_selBookmarkField(\\\\'" + chooseId + "\\\\',\\\\'" + chooseId + "\\\\')\"];"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                pw.println( jsid + "_" + jsid + ".xID='xf"+ix+"_"+(0)+"'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

                pw.println( jsid + ".addChildren(["+jsid + "_" + jsid+"]);"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            else
            {
                pw.println( "var " + jsid + " = ['"+label+"',\"javascript:_selAttribute(\\\\'" + chooseId + "\\\\')\"];"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                pw.println( jsid + ".xID='xf"+(++ix)+"'"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            

            if( toAdd.length() > 1 ) 
            {
                toAdd.append(',');
            }
            toAdd.append( jsid );
        }
        toAdd.append("]"); //$NON-NLS-1$
        pw.print( parentTree + ".addChildren("); //$NON-NLS-1$
        pw.print( toAdd );
        pw.println( ");"); //$NON-NLS-1$
    }
    
    public static void getBookmarkFields( PrintWriter pw, boObject field, String bookmarkId, String parentTree, String parentQuery, int parentIx ) throws boRuntimeException
    {
        int ix = 0;
        StringBuffer toAdd  = new StringBuffer("["); //$NON-NLS-1$
        boBridgeIterator it = field.getBridge("campos").iterator();  //$NON-NLS-1$
        while( it.next() )
        {
            boObject fieldObj = it.currentRow().getObject();
            
            String id = fieldObj.getAttribute("nome").getValueString(); //$NON-NLS-1$
            String label = fieldObj.getAttribute("pergunta").getValueString(); //$NON-NLS-1$
            String jsid = parentTree + "_" + id; //$NON-NLS-1$

            if( label.trim().length() == 0 )
            {
                label = fieldObj.getAttribute("nome").getValueString(); //$NON-NLS-1$
            }
            String chooseId = parentQuery!=null?(id):id;
            String tipo = fieldObj.getAttribute("tipo").getValueString(); //$NON-NLS-1$
            if( "8".equals( tipo ) ) // Campo de imagem //$NON-NLS-1$
            {
                chooseId += "(image)"; //$NON-NLS-1$
            }
            
            if( "GESTEMP_CampoNObjecto".equals( fieldObj.getName() ) ) //$NON-NLS-1$
            {
                label += Messages.getString("MergeFieldTreeHelper.11");                 //$NON-NLS-1$
            }
            
            pw.println( "var " + jsid + " = ['"+label+"',\"javascript:_selBookmarkField(\\\\'" + bookmarkId + "\\\\',\\\\'" + chooseId + "\\\\')\"];"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            pw.println( jsid + ".xID='xf"+parentIx+"_"+(++ix)+"'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            
            if( toAdd.length() > 1 ) 
            {
                toAdd.append(',');
            }
            toAdd.append( jsid );
        }
        toAdd.append("]"); //$NON-NLS-1$
        pw.print( parentTree + ".addChildren("); //$NON-NLS-1$
        pw.print( toAdd );
        pw.println( ");"); //$NON-NLS-1$
        
    }
    
    public static char[] getSystemFields( Vector toAdd )
    {
        CharArrayWriter sbSystemFields = new CharArrayWriter();
        PrintWriter pw = new PrintWriter( sbSystemFields );
        
        composeFolder( pw, "sys", "sys1", Messages.getString("MergeFieldTreeHelper.10") ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        composeFolder( pw, "sys_message"            ,"sys11", Messages.getString("MergeFieldTreeHelper.95") ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        composeField(  pw, "sys_message_id"         ,"sys111",Messages.getString("MergeFieldTreeHelper.98"),"mensagemID"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_message_idext"         ,"sys111",Messages.getString("MergeFieldTreeHelper.102"),"mensagemIDExt"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_message_Date"       ,"sys112",Messages.getString("MergeFieldTreeHelper.106"),"mensagemData"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_message_anexos"       ,"sys113",Messages.getString("MergeFieldTreeHelper.110"), SpecialField.MESSAGE_ANEXOS ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        pw.print("sys_message.addChildren([sys_message_id,sys_message_idext,sys_message_Date,sys_message_anexos]);"); //$NON-NLS-1$
        
/*
        composeFolder( pw, "sys_creator"            ,"sys12", "Criador" );
        composeField(  pw, "sys_creator_id"         ,"sys121","Id","mensagemCriadorId");
        composeField(  pw, "sys_creator_username"   ,"sys122","Username","mensagemCriadorUsername");
        composeField(  pw, "sys_creator_name"       ,"sys123","Nome","mensagemCriadorNome");
        composeField(  pw, "sys_creator_lastname"   ,"sys124","Apelido","mensagemCriadorApelido");
        composeField(  pw, "sys_creator_email"      ,"sys125","Email","mensagemCriadorEmail");
        pw.println("sys_creator.addChildren([sys_creator_id,sys_creator_username,sys_creator_name,sys_creator_lastname,sys_creator_email]);");
*/
        composeFolder( pw, "sys_ctt"                ,"sys13", Messages.getString("MergeFieldTreeHelper.114") ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        composeField(  pw, "sys_ctt_sendtype"       ,"sys131",Messages.getString("MergeFieldTreeHelper.117"),"cartaTipoEnvio"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_ctt_remetente1"       ,"sys1311",Messages.getString("MergeFieldTreeHelper.121"),"cartaRemetenteCTT1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_ctt_remetente2"       ,"sys1311",Messages.getString("MergeFieldTreeHelper.125"),"cartaRemetenteCTT2"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_ctt_mailregist"     ,"sys132",Messages.getString("MergeFieldTreeHelper.129"),"cartaRegistoCTT"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_ctt_mailregistext"     ,"sys1321",Messages.getString("MergeFieldTreeHelper.133"),"cartaRegistoCTTExt"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_ctt_srp"            ,"sys133",Messages.getString("MergeFieldTreeHelper.8"),"cartaSrpCTT"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_ctt_srpcustomer"    ,"sys1331",Messages.getString("MergeFieldTreeHelper.9"),"cartaSrpClienteCTT"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_ctt_customer"       ,"sys134",Messages.getString("MergeFieldTreeHelper.145"),"cartaClienteCTT"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_ctt_4estados"       ,"sys135",Messages.getString("MergeFieldTreeHelper.149"),"cartaEstadosCTT"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_ctt_4estadosext"    ,"sys136",Messages.getString("MergeFieldTreeHelper.153"),"cartaEstadosCTTExt"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        pw.println("sys_ctt.addChildren([sys_ctt_sendtype,sys_ctt_remetente1,sys_ctt_remetente2,sys_ctt_mailregist,sys_ctt_mailregistext,sys_ctt_srp,sys_ctt_customer,sys_ctt_srpcustomer,sys_ctt_4estados,sys_ctt_4estadosext]);"); //$NON-NLS-1$

        composeFolder( pw, "sys_approver"           ,"sys14",Messages.getString("MergeFieldTreeHelper.158") ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        composeField(  pw, "sys_approver_signature" ,"sys141",Messages.getString("MergeFieldTreeHelper.161"),"apovadorAssinatura(image)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_approver_name"      ,"sys142",Messages.getString("MergeFieldTreeHelper.165"),"aprovadorNome"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_approver_role"      ,"sys143",Messages.getString("MergeFieldTreeHelper.169"),"aprovadorFuncao"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        composeField(  pw, "sys_approver_date"      ,"sys144",Messages.getString("MergeFieldTreeHelper.173"),"aprovadorData"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        pw.println("sys_approver.addChildren([sys_approver_signature,sys_approver_name,sys_approver_role,sys_approver_date]);"); //$NON-NLS-1$

        pw.println("sys.addChildren([sys_message,sys_ctt,sys_approver]);"); //$NON-NLS-1$

        toAdd.add( "sys" );         //$NON-NLS-1$

        return sbSystemFields.toCharArray();
    }
    
    private static void composeField( PrintWriter pw, String jsvar, String jsId, String label, String id )
    {
        pw.print("var "); //$NON-NLS-1$
        pw.print( jsvar );
        pw.print(" = ['"); //$NON-NLS-1$
        pw.print( label );
        pw.println("',\"javascript:_selAttribute(\\\\'"+id+"\\\\')\"];"); //$NON-NLS-1$ //$NON-NLS-2$
//        pw.print( id );
//        pw.println("\\')\"];");
        pw.print( jsvar );
        pw.println(".xID='"+jsId+"'"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static void composeFolder( PrintWriter pw, String jsvar, String jsId, String label )
    {
        pw.println("var "+jsvar+" = gFld('"+label+"','javascript:void(0)');"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        pw.println(jsvar + ".xID='"+jsId+"'"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    

    public static StringBuffer getJAVASCRIPTAttribute(int level, StringBuffer attributesStr, boDefAttribute attr, StringBuffer options,
        boolean includeAttributeObjects, String parentItem, String parentAttribute, Counter index)
    {
        boolean haveChildren = false;
        StringBuffer toRet = new StringBuffer();
        boDefAttribute[] attrs2 = null;

        if (includeAttributeObjects && (attr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                (attr.getRelationType() == boDefAttribute.RELATION_1_TO_1))
        {
            boDefHandler relBo = attr.getReferencedObjectDef();
            String xname = attr.getName();

            if (!relBo.getName().equalsIgnoreCase("boObject")) //$NON-NLS-1$
            {
                attrs2 = relBo.getAttributesDef();

                if ((level < 3) || !relBo.getBoCanBeOrphan())
                {
                    if (attrs2.length > 0)
                    {
                        haveChildren = true;
                    }
                }
            }
        }

        String atrVar = parentItem + "_" + attr.getName(); //$NON-NLS-1$

        if (!haveChildren)
        {
            String atrName = ""; //$NON-NLS-1$

            if (parentAttribute.length() > 0)
            {
                atrName = parentAttribute + "." + attr.getName(); //$NON-NLS-1$
            }
            else
            {
                atrName = attr.getName();
            }

            attributesStr.append("attributes[").append(index.getNumber()).append("]=\"").append(atrName).append("\";\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            attributesStr.append(atrVar);
            attributesStr.append("=[\""); //$NON-NLS-1$
            attributesStr.append(attr.getLabel());
            attributesStr.append("\",\""); //$NON-NLS-1$
            attributesStr.append("javascript:_selAttribute(").append(index.getNumber()).append(" )"); //$NON-NLS-1$ //$NON-NLS-2$
            attributesStr.append("\"] ;\n"); //$NON-NLS-1$

            attributesStr.append(atrVar).append(".xID='x").append(index.getNumber()).append("';\n"); //$NON-NLS-1$ //$NON-NLS-2$

            toRet.append(atrVar);
            index.increment();
        }
        else
        {
            String atrName = ""; //$NON-NLS-1$

            if (parentAttribute.length() > 0)
            {
                atrName = parentAttribute + "." + attr.getName(); //$NON-NLS-1$
            }
            else
            {
                atrName = attr.getName();
            }

            attributesStr.append("attributes[").append(index.getNumber()).append("]=\"").append(atrName).append("\";\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            StringBuffer[] strScript = new StringBuffer[attrs2.length];

            attributesStr.append(atrVar).append(" = gFld(\"").append(attr.getLabel()).append("\",\"javascript:_selAttribute("); //$NON-NLS-1$ //$NON-NLS-2$
            attributesStr.append(index.getNumber());
            attributesStr.append(")\");\n"); //$NON-NLS-1$
            attributesStr.append(atrVar).append(".xID='x").append(index.getNumber()).append("';\n"); //$NON-NLS-1$ //$NON-NLS-2$
            index.increment();

            //       attributesStr.append("rootTree.xID=rootNode;\n");
            int z = 0;

            for (int i = 0; i < attrs2.length; i++)
            {
                strScript[z++] = getJAVASCRIPTAttribute(level + 1, attributesStr, attrs2[i], null, includeAttributeObjects, parentItem + "_" + attr.getName(), //$NON-NLS-1$
                        atrName, index);
            }

            toRet.append(atrVar);

            attributesStr.append(atrVar).append(".addChildren(["); //$NON-NLS-1$

            for (int i = 0; i < strScript.length; i++)
            {
                attributesStr.append(strScript[i]);

                if ((i + 1) < strScript.length)
                {
                    attributesStr.append(',');
                }
            }

            attributesStr.append("]);"); //$NON-NLS-1$
        }

        return toRet;
    }


    public static class MergeFieldFolder
    {
        
    }
    
    public static class MergeFieldItem
    {
        
    }

}