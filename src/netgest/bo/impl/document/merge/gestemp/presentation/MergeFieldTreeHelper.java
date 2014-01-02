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
import netgest.bo.system.Logger;

public class MergeFieldTreeHelper 
{

    public static StringBuffer getJavaScriptTree( EboContext ctx, long templateBoui ) throws boRuntimeException
    {

        boObject templateObject = boObject.getBoManager().loadObject( ctx, templateBoui );
        StringBuffer attributesStr = new StringBuffer("");

        attributesStr.append("attributes=[];\n");
        attributesStr.append(Messages.getString("MergeFieldTreeHelper.12"));
        attributesStr.append("foldersTree.xID='rootNode';\n");
        
        Vector toAdd = new Vector();
        

        attributesStr.append( getSystemFields( toAdd ) ); 

        attributesStr.append( getStaticFields( ctx, toAdd ) ); 
        
        attributesStr.append( getQueries( toAdd,templateObject ) ); 
        
        attributesStr.append('\t');
        
        attributesStr.append( "foldersTree.addChildren([");
        for (int i = 0; i < toAdd.size(); i++) 
        {
            if( i > 0 ) attributesStr.append( ',' );
            attributesStr.append( toAdd.get( i ) );
            
        }
        attributesStr.append( "]);");

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
        boBridgeIterator it = templateObj.getBridge("queries").iterator();
        it.beforeFirst();
        while( it.next() )
        {
            boObject objQuery = it.currentRow().getObject();
            String id    = objQuery.getAttribute("nome").getValueString();
            String label = objQuery.getAttribute("pergunta").getValueString();
            if( label.trim().length() == 0 )
            {
                label = objQuery.getAttribute("nome").getValueString();
            }
            pw.println( "var "+ id + " = gFld('"+ label +"','javascript:void(0)');");
            pw.println( id + ".xID='xq"+(++ix)+"'");
            getQueryFields( objQuery.getBridge("campos").iterator(), pw, id, id );
            toAdd.add( id );
        }

        // Manual fieds
        {
            String id = "manualFields";
            pw.println( "var manualFields = gFld('Campos Manuais','javascript:void(0)');");
            pw.println( id + ".xID='xm"+(++ix)+"'");
            getQueryFields( templateObj.getBridge("camposManuais").iterator(), pw, id, null );
    
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
        StringBuffer toAdd = new StringBuffer("[");
        while( it.next() )
        {
            boObject fieldObj = it.currentRow().getObject();
            
            String id = fieldObj.getAttribute("nome").getValueString();
            String label = fieldObj.getAttribute("pergunta").getValueString();
            String jsid = parentTree + "_" + id;

            if( label.trim().length() == 0 )
            {
                label = fieldObj.getAttribute("nome").getValueString();
            }
            String chooseId = parentQuery!=null?(parentQuery+ "__" + id):id;
            String tipo = fieldObj.getAttribute("tipo").getValueString();
            if( "8".equals( tipo ) ) // Campo de imagem
            {
                chooseId += "(image)";
            }

            if( "GESTEMP_CampoNObjecto".equals( fieldObj.getName() ) )
            {
                pw.println( "var "+jsid+" = gFld('"+label+" (Bookmark)','javascript:_selBookmark(\"" + chooseId + "\")');");
                pw.println( jsid + ".xID='xb"+(++ix)+"'");
                getBookmarkFields( pw, fieldObj, chooseId ,jsid, "", ix );
            } 
            else if ( "GESTEMP_CampoNManual".equals( fieldObj.getName() ) )
            { 
                pw.println( "var "+jsid+" = gFld('"+label + " (Bookmark)" +"','javascript:_selBookmark(\"" + chooseId + "\")');");
                pw.println( jsid + ".xID='xb"+(++ix)+"'");

                pw.println( "var " + jsid + "_" + jsid + " = ['"+label+"',\"javascript:_selBookmarkField(\\\\'" + chooseId + "\\\\',\\\\'" + chooseId + "\\\\')\"];");
                pw.println( jsid + "_" + jsid + ".xID='xf"+ix+"_"+(0)+"'");

                pw.println( jsid + ".addChildren(["+jsid + "_" + jsid+"]);");
            }
            else
            {
                pw.println( "var " + jsid + " = ['"+label+"',\"javascript:_selAttribute(\\\\'" + chooseId + "\\\\')\"];");
                pw.println( jsid + ".xID='xf"+(++ix)+"'");
            }
            

            if( toAdd.length() > 1 ) 
            {
                toAdd.append(',');
            }
            toAdd.append( jsid );
        }
        toAdd.append("]");
        pw.print( parentTree + ".addChildren(");
        pw.print( toAdd );
        pw.println( ");");
    }
    
    public static void getBookmarkFields( PrintWriter pw, boObject field, String bookmarkId, String parentTree, String parentQuery, int parentIx ) throws boRuntimeException
    {
        int ix = 0;
        StringBuffer toAdd  = new StringBuffer("[");
        boBridgeIterator it = field.getBridge("campos").iterator(); 
        while( it.next() )
        {
            boObject fieldObj = it.currentRow().getObject();
            
            String id = fieldObj.getAttribute("nome").getValueString();
            String label = fieldObj.getAttribute("pergunta").getValueString();
            String jsid = parentTree + "_" + id;

            if( label.trim().length() == 0 )
            {
                label = fieldObj.getAttribute("nome").getValueString();
            }
            String chooseId = parentQuery!=null?(id):id;
            String tipo = fieldObj.getAttribute("tipo").getValueString();
            if( "8".equals( tipo ) ) // Campo de imagem
            {
                chooseId += "(image)";
            }
            
            if( "GESTEMP_CampoNObjecto".equals( fieldObj.getName() ) )
            {
                label += Messages.getString("MergeFieldTreeHelper.11");                
            }
            
            pw.println( "var " + jsid + " = ['"+label+"',\"javascript:_selBookmarkField(\\\\'" + bookmarkId + "\\\\',\\\\'" + chooseId + "\\\\')\"];");
            pw.println( jsid + ".xID='xf"+parentIx+"_"+(++ix)+"'");
            
            if( toAdd.length() > 1 ) 
            {
                toAdd.append(',');
            }
            toAdd.append( jsid );
        }
        toAdd.append("]");
        pw.print( parentTree + ".addChildren(");
        pw.print( toAdd );
        pw.println( ");");
        
    }
    
    public static char[] getSystemFields( Vector toAdd )
    {
        CharArrayWriter sbSystemFields = new CharArrayWriter();
        PrintWriter pw = new PrintWriter( sbSystemFields );
        
        composeFolder( pw, "sys", "sys1", Messages.getString("MergeFieldTreeHelper.10") );

        composeFolder( pw, "sys_message"            ,"sys11", Messages.getString("MergeFieldTreeHelper.95") );
        composeField(  pw, "sys_message_id"         ,"sys111",Messages.getString("MergeFieldTreeHelper.98"),"mensagemID");
        composeField(  pw, "sys_message_idext"         ,"sys111",Messages.getString("MergeFieldTreeHelper.102"),"mensagemIDExt");
        composeField(  pw, "sys_message_Date"       ,"sys112",Messages.getString("MergeFieldTreeHelper.106"),"mensagemData");
        composeField(  pw, "sys_message_anexos"       ,"sys113",Messages.getString("MergeFieldTreeHelper.110"), SpecialField.MESSAGE_ANEXOS );
        pw.print("sys_message.addChildren([sys_message_id,sys_message_idext,sys_message_Date,sys_message_anexos]);");
        
/*
        composeFolder( pw, "sys_creator"            ,"sys12", "Criador" );
        composeField(  pw, "sys_creator_id"         ,"sys121","Id","mensagemCriadorId");
        composeField(  pw, "sys_creator_username"   ,"sys122","Username","mensagemCriadorUsername");
        composeField(  pw, "sys_creator_name"       ,"sys123","Nome","mensagemCriadorNome");
        composeField(  pw, "sys_creator_lastname"   ,"sys124","Apelido","mensagemCriadorApelido");
        composeField(  pw, "sys_creator_email"      ,"sys125","Email","mensagemCriadorEmail");
        pw.println("sys_creator.addChildren([sys_creator_id,sys_creator_username,sys_creator_name,sys_creator_lastname,sys_creator_email]);");
*/
        composeFolder( pw, "sys_ctt"                ,"sys13", Messages.getString("MergeFieldTreeHelper.114") );
        composeField(  pw, "sys_ctt_sendtype"       ,"sys131",Messages.getString("MergeFieldTreeHelper.117"),"cartaTipoEnvio");
        composeField(  pw, "sys_ctt_remetente1"       ,"sys1311",Messages.getString("MergeFieldTreeHelper.121"),"cartaRemetenteCTT1");
        composeField(  pw, "sys_ctt_remetente2"       ,"sys1311",Messages.getString("MergeFieldTreeHelper.125"),"cartaRemetenteCTT2");
        composeField(  pw, "sys_ctt_mailregist"     ,"sys132",Messages.getString("MergeFieldTreeHelper.129"),"cartaRegistoCTT");
        composeField(  pw, "sys_ctt_mailregistext"     ,"sys1321",Messages.getString("MergeFieldTreeHelper.133"),"cartaRegistoCTTExt");
        composeField(  pw, "sys_ctt_srp"            ,"sys133",Messages.getString("MergeFieldTreeHelper.8"),"cartaSrpCTT");
        composeField(  pw, "sys_ctt_srpcustomer"    ,"sys1331",Messages.getString("MergeFieldTreeHelper.9"),"cartaSrpClienteCTT");
        composeField(  pw, "sys_ctt_customer"       ,"sys134",Messages.getString("MergeFieldTreeHelper.145"),"cartaClienteCTT");
        composeField(  pw, "sys_ctt_4estados"       ,"sys135",Messages.getString("MergeFieldTreeHelper.149"),"cartaEstadosCTT");
        composeField(  pw, "sys_ctt_4estadosext"    ,"sys136",Messages.getString("MergeFieldTreeHelper.153"),"cartaEstadosCTTExt");
        pw.println("sys_ctt.addChildren([sys_ctt_sendtype,sys_ctt_remetente1,sys_ctt_remetente2,sys_ctt_mailregist,sys_ctt_mailregistext,sys_ctt_srp,sys_ctt_customer,sys_ctt_srpcustomer,sys_ctt_4estados,sys_ctt_4estadosext]);");

        composeFolder( pw, "sys_approver"           ,"sys14",Messages.getString("MergeFieldTreeHelper.158") );
        composeField(  pw, "sys_approver_signature" ,"sys141",Messages.getString("MergeFieldTreeHelper.161"),"apovadorAssinatura(image)");
        composeField(  pw, "sys_approver_name"      ,"sys142",Messages.getString("MergeFieldTreeHelper.165"),"aprovadorNome");
        composeField(  pw, "sys_approver_role"      ,"sys143",Messages.getString("MergeFieldTreeHelper.169"),"aprovadorFuncao");
        composeField(  pw, "sys_approver_date"      ,"sys144",Messages.getString("MergeFieldTreeHelper.173"),"aprovadorData");
        pw.println("sys_approver.addChildren([sys_approver_signature,sys_approver_name,sys_approver_role,sys_approver_date]);");

        pw.println("sys.addChildren([sys_message,sys_ctt,sys_approver]);");

        toAdd.add( "sys" );        

        return sbSystemFields.toCharArray();
    }
    
    private static void composeField( PrintWriter pw, String jsvar, String jsId, String label, String id )
    {
        pw.print("var ");
        pw.print( jsvar );
        pw.print(" = ['");
        pw.print( label );
        pw.println("',\"javascript:_selAttribute(\\\\'"+id+"\\\\')\"];");
//        pw.print( id );
//        pw.println("\\')\"];");
        pw.print( jsvar );
        pw.println(".xID='"+jsId+"'");
    }

    private static void composeFolder( PrintWriter pw, String jsvar, String jsId, String label )
    {
        pw.println("var "+jsvar+" = gFld('"+label+"','javascript:void(0)');");
        pw.println(jsvar + ".xID='"+jsId+"'");
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

            if (!relBo.getName().equalsIgnoreCase("boObject"))
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

        String atrVar = parentItem + "_" + attr.getName();

        if (!haveChildren)
        {
            String atrName = "";

            if (parentAttribute.length() > 0)
            {
                atrName = parentAttribute + "." + attr.getName();
            }
            else
            {
                atrName = attr.getName();
            }

            attributesStr.append("attributes[").append(index.getNumber()).append("]=\"").append(atrName).append("\";\n");

            attributesStr.append(atrVar);
            attributesStr.append("=[\"");
            attributesStr.append(attr.getLabel());
            attributesStr.append("\",\"");
            attributesStr.append("javascript:_selAttribute(").append(index.getNumber()).append(" )");
            attributesStr.append("\"] ;\n");

            attributesStr.append(atrVar).append(".xID='x").append(index.getNumber()).append("';\n");

            toRet.append(atrVar);
            index.increment();
        }
        else
        {
            String atrName = "";

            if (parentAttribute.length() > 0)
            {
                atrName = parentAttribute + "." + attr.getName();
            }
            else
            {
                atrName = attr.getName();
            }

            attributesStr.append("attributes[").append(index.getNumber()).append("]=\"").append(atrName).append("\";\n");

            StringBuffer[] strScript = new StringBuffer[attrs2.length];

            attributesStr.append(atrVar).append(" = gFld(\"").append(attr.getLabel()).append("\",\"javascript:_selAttribute(");
            attributesStr.append(index.getNumber());
            attributesStr.append(")\");\n");
            attributesStr.append(atrVar).append(".xID='x").append(index.getNumber()).append("';\n");
            index.increment();

            //       attributesStr.append("rootTree.xID=rootNode;\n");
            int z = 0;

            for (int i = 0; i < attrs2.length; i++)
            {
                strScript[z++] = getJAVASCRIPTAttribute(level + 1, attributesStr, attrs2[i], null, includeAttributeObjects, parentItem + "_" + attr.getName(),
                        atrName, index);
            }

            toRet.append(atrVar);

            attributesStr.append(atrVar).append(".addChildren([");

            for (int i = 0; i < strScript.length; i++)
            {
                attributesStr.append(strScript[i]);

                if ((i + 1) < strScript.length)
                {
                    attributesStr.append(',');
                }
            }

            attributesStr.append("]);");
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