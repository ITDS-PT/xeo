package netgest.bo.impl.document.merge.gestemp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.dochtml.docHTML;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.impl.document.merge.Tabela;
import netgest.bo.impl.document.merge.gestemp.presentation.GesDocViewer;
import netgest.bo.impl.document.merge.resultSet.MergeResultSetBoObject;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.message.MessageServer;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boEvent;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.io.FSiFile;
import netgest.io.iFile;
import netgest.io.iFilePermissionDenied;
import netgest.xwf.common.xwfFunctions;
import netgest.xwf.common.xwfHelper;
import netgest.xwf.core.xwfManager;
import netgest.xwf.core.xwfMessage;
import netgest.bo.system.Logger;
//TODO:Implement Interface LUSITANIA
//import pt.lusitania.gd.AutomaticClassifications;
public class GtAutoClassif 
{
    private ArrayList repeatBlocks = new ArrayList();
    private ArrayList repeatBlocksNames = new ArrayList();
    private Tabela parametros = null;
    
    public GtAutoClassif()
    {
    }
    
    public boObject setAutoClassif(boObject obj) throws boRuntimeException
    {
        boObject ret = null;
        EboContext boctx = obj.getEboContext();
        boObject msg = null, actvSend = null;
        //obtenção da mensagem e da actividade
        if("message".equals(obj.getName()) || "message".equals(obj.getBoDefinition().getBoSuperBo()))
        {
            msg = obj;
            String progActv = xwfHelper.getProgActvFromMessage(boctx, msg.getBoui());
            if(progActv != null && progActv.length() > 0)
            {
                String[] codes = progActv.split(":");
                if(codes.length == 2)
                {
                    actvSend = boObject.getBoManager().loadObject(boctx, Long.parseLong(codes[1]));
                }
            }
        }
        else if("xwfActivitySend".equals(obj.getName()))
        {
            actvSend = obj;
            boObject xwfVar = actvSend.getAttribute("message").getObject();
            if(xwfVar != null)
            {
                boObject varValue = xwfVar.getAttribute("value").getObject();
                if(varValue != null)
                {
                    msg = varValue.getAttribute("valueObject").getObject();
                }
            }
        }
        if(actvSend == null || msg == null || msg.getAttribute("usedTemplate").getValueLong() <= 0)
        {
            throw new boRuntimeException("", MessageLocalizer.getMessage("INVALID_MESSAGE_FOR_AUTOMATIC_CLASSIFICATION"), null);
        }
        
        boObject modelo = boObject.getBoManager().loadObject( msg.getEboContext(), msg.getAttribute("usedTemplate").getValueLong() );
        GtTemplate gtTemp = GtTemplate.getTemplate(boctx, modelo.getBoui() );
        gtTemp.setAnswer(msg);
        String segmento = modelo.getAttribute("segmento").getValueString();

        //ArrayList docs = getDocument(boctx, msg);
        
        boBridgeIterator bgBinnaryDocument = msg.getBridge("binaryDocuments").iterator();

//        for (int i = 0; i < docs.size(); i++) 
//        {

        if ( bgBinnaryDocument.first() )
        {
            
            boObject doc = bgBinnaryDocument.currentRow().getObject();

            // Nos emails a ultima página fica em primeiro            
            if( "messageMail".equals( msg.getName() ) && bgBinnaryDocument.last() )
        {
                doc = bgBinnaryDocument.currentRow().getObject();    
            }
            
            //doc = (boObject)docs.get(i);
            
            doc.getBridge("classification").truncate();
            
            doc.getAttribute("codeBarClienteCTT").setValueString( msg.getAttribute("docSeq").getValueString() );
            doc.getAttribute("registoCTT").setValueString( msg.getAttribute("registoCTT").getValueString() );
            
            //vou passar a classificar pelos mapeamentos
            setMapsClassif(actvSend, msg, msg.getAttribute("usedTemplate").getObject(), doc, gtTemp);
            //classificações automaticas
            //TODO:Implement Interface LUSITANIA
            //AutomaticClassifications.setAutoClassif(actvSend, msg, doc);
            
            boObjectList listClf  = boObjectList.list(doc.getEboContext(),"select GESDocClf where internalname =?", new Object[] {"importacao_sinistros"});
            listClf.beforeFirst();
            boObjectList listPessoa = boObjectList.list(doc.getEboContext(),"select GESDocClfObject where internalname =?", new Object[] {"pessoa"});
            listPessoa.beforeFirst();

            if ( listPessoa.next() &&  listClf.next() )
            {

                long destinatarioBoui = Helper.getDestinatario( boctx, gtTemp ).getBoui();
                
                long clfBoui   = listClf.getCurrentBoui();
                long clfPessoaBoui = listPessoa.getCurrentBoui();
                
                boolean found = false;
                boBridgeIterator it = doc.getBridge("classification").iterator();
                while( it.next() )
                {
                    if( it.currentRow().getAttribute("valueObject").getValueLong() == destinatarioBoui
                        &&
                        it.currentRow().getValueLong() == clfPessoaBoui
                    )
                    {
                        found = true;
                        break;
                    }
                }
                
                if( !found )
                {
                    String groupSeq = GesDocViewer.newGroupSequence(boctx);
                    bridgeHandler bh = doc.getBridge("classification");

                    bh.add( clfPessoaBoui );
                    bh.getAttribute("segmento").setValueString( segmento );
                    bh.getAttribute("groupSeq").setValueString( groupSeq );
                    bh.getAttribute("valueClassification").setValueLong( clfBoui );   
                    bh.getAttribute("valueObject").setValueLong( destinatarioBoui );
                }
            }
            
            //doc.update();
            if( ret != null )
                doc.update();
            else
                ret = doc;
        }
        return ret;
    }
    
    private void setMapsClassif(boObject actv, boObject msg, boObject template, boObject doc, GtTemplate gtTemp ) throws boRuntimeException
    {

        String segmento = template.getAttribute("segmento").getValueString();

        EboContext boctx = msg.getEboContext();
        GtMap maps[] = new GtMap[(int)template.getBridge("mapeamentos").getRecordCount()];
        
        if(maps != null && maps.length > 0)
        {
            GtQuery queries[] = gtTemp.getQueries();
            for (int i = 0; i < queries.length; i++) 
            {
                ((GtQuery)queries[i]).calculateAutomicFields(boctx, gtTemp);        
            }
            
            gtTemp.calculate(boctx, this);
            boBridgeIterator bit = template.getBridge("mapeamentos").iterator();
            maps = new GtMap[(int)template.getBridge("mapeamentos").getRecordCount()];
            bit.beforeFirst();
            int j = 0;
            while(bit.next())
            {
                maps[j] = new GtMap(bit.currentRow().getObject());
                j++;
            }
            if(maps.length > 1)
            {
                Arrays.sort(maps, new GtMap.GtMapComparator());
            }
    
            long lastClf = -1;
            String aux=null;
            String groupSeq = null;
            for (int i = 0; i < maps.length; i++) 
            {
                if(lastClf == -1)
                {
                    lastClf = maps[i].getClassif();
                    groupSeq = GesDocViewer.newGroupSequence(boctx);
                }
                else if(lastClf != maps[i].getClassif())
                {
                    groupSeq = GesDocViewer.newGroupSequence(boctx);
                    lastClf = maps[i].getClassif();
                }
                aux = maps[i].getHelper();
                aux = aux.replaceAll("\\.", "__");
                Object valor = parametros.getValue(aux);
                if(valor != null)
                {
                    maps[i].setClassification(doc, valor, groupSeq, segmento );
                }
                else
                {
                    // Verifica se faz parte de um bookmark 
                    for (int z = 0; z < repeatBlocksNames.size(); z++) 
                    {
                        String key = repeatBlocksNames.get( z ).toString();
                        if ( aux.startsWith( key )  )
                        {
                            // Encontrou nos bookMarks
                            Tabela tabela = (Tabela)repeatBlocks.get( z );
                            tabela.beforeFirst();
                            while( tabela.next() )
                            {
                                String fieldName;
                                
                                // Referencia directamente o campo..
                                if ( aux.equals( key ) )
                                {
                                    fieldName = key.split( "__" )[1];
                                }
                                else
                                {
                                    fieldName = aux.substring( ( key + "__").length() );
                                }
                                
                                valor = tabela.getValue( fieldName );
                                if(valor != null)
                                {
                                    maps[i].setClassification(doc, valor, groupSeq, segmento );
                                }
                            }
                            key = key;
                            break;
                        }
                    }
                    
                    
                }
            }
            //classificacoes repetidas
            for (int i = 0; i < maps.length; i++) 
            {
                int pos = 0;
                String bookmark = null;
                String field = null;
                String[] nameParts = maps[i].getHelper().split("\\.");
                if(nameParts.length > 1)
                {
                    if(nameParts.length == 2)
                    {
                        bookmark = nameParts[0] + "__" +nameParts[1];
                        field = nameParts[1];
                    }
                    else if(nameParts.length == 3)
                    {
                        bookmark = nameParts[0] + "__" +nameParts[1];
                        field = nameParts[2];
                    }
                    if(bookmark != null && (pos = repeatBlocksNames.indexOf(bookmark)) > -1)
                    {
                        Tabela auxTab = (Tabela)repeatBlocks.get(pos);
                        Object valor;
                        while(auxTab.next())
                        {
                            valor = auxTab.getValue(field);
                            if(valor != null)
                            {
                                maps[i].setClassification(doc, valor, GesDocViewer.newGroupSequence(boctx),segmento );
                            }
                        }
                        auxTab.beforeFirst();
                    }
                }
            }
        }
    }
    
    private static ArrayList getDocument(EboContext boctx, boObject msg) throws boRuntimeException
    {
        ArrayList toRet = new ArrayList();
        boObjectList list = boObjectList.list(boctx, "select Ebo_Document where msg = ? ", new Object[]{new Long(msg.getBoui())}, 1, true);
        list.beforeFirst();
        while(list.next())
        {
            toRet.add(list.getObject());
        }
        return toRet;
    }
    
    public void setRepeatBlock(Tabela tab, String nome)
    {
        repeatBlocksNames.add(nome);
        repeatBlocks.add(tab);
    }
    
    public void setDataSource(Tabela tab)
    {
        parametros = tab;
        if(parametros != null) 
            parametros.next();
    }
    
}