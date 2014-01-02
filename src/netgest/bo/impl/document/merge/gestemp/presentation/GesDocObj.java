package netgest.bo.impl.document.merge.gestemp.presentation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import netgest.bo.dochtml.docHTML;
import netgest.bo.impl.document.merge.gestemp.Segmento;
import netgest.bo.impl.document.merge.gestemp.validation.Classificacao;
import netgest.bo.impl.document.merge.gestemp.validation.Contexto;
import netgest.bo.impl.document.merge.gestemp.validation.JavaExecuter;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.bridgeHandler;

public abstract class GesDocObj
{
    protected String internalName = null;
    protected String name = null;
    protected String htmlID = null;
    protected String value = null;
    protected boolean required = true;
    protected String validation = null;
    protected long gesDocBoui;
    protected GesDocViewer clfViewer;
    
    public GesDocObj()
    {
    
    }
    
    public void setObrigatorio(boolean required)
    {
        this.required = required;
    }
    
    public boolean getObrigatorio( EboContext ctx )
    {
        /*
        if( internalName.equals("tipo_documento") )
        {
            PreparedStatement pstm = null;
            ResultSet rslt = null;
            try
            {
                pstm = ctx.getConnectionData().prepareStatement("select 1 from ebo_document$classification where parent$=? and child$=?");
                pstm.setLong( 1, getClfViewer().getDocument() );            
                pstm.setLong( 2, gesDocBoui );            
                rslt = pstm.executeQuery();
                return !rslt.next();
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
            finally
            {
                try
                {
                    if( pstm != null ) pstm.close();
                    if( rslt != null ) rslt.close();
                }
                catch (Exception e)
                {
                    
                }
            }
        }
        else
        {
        */
            return this.required;
        /*            
        }
        */
    }
    
    public void setValidation(String validation)
    {
        this.validation = validation;
    }
    
    public String getValidation()
    {
        return this.validation;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public String getInternalName()
    {
        return this.internalName;
    }
    
    public long getGesDocBoui()
    {
        return this.gesDocBoui;
    }
    
    public String getHTMLFieldName()
    {
        if(htmlID == null)
        {
            
            htmlID = String.valueOf(name.hashCode() < 0 ? (-1*name.hashCode()):name.hashCode());
        }
        return this.htmlID;
    }
    
    public String bindValues(docHTML doc)
    {
        return "";
    }

    public void setValue(EboContext boctx)  throws boRuntimeException
    {
        HttpServletRequest request = boctx.getRequest();
        value=request.getParameter(getHTMLFieldName());
    }
    
    public void setValue(String value)  throws boRuntimeException
    {
        this.value = value;
    }

//    public void setValue(EboContext boctx, boBridgeIterator bit, long classif)  throws boRuntimeException
//    {
//        bit.beforeFirst();
//        while(bit.next())
//        {
//            if(bit.currentRow().getAttribute("valueClassification").getValueLong() == classif)
//            {
//                if(bit.currentRow().getValueLong() == gesDocBoui)
//                {
//                    value = bit.currentRow().getAttribute("valueText").getValueString();
//                }
//            }
//        }
//    }
    
    public void setValue(EboContext boctx, boBridgeIterator bit, String groupSeq)  throws boRuntimeException
    {
        bit.beforeFirst();
        while(bit.next())
        {
            if(groupSeq.equals(bit.currentRow().getAttribute("groupSeq").getValueString()))
            {
                if(bit.currentRow().getValueLong() == gesDocBoui)
                {
                    value = bit.currentRow().getAttribute("valueText").getValueString();
                }
            }
        }
    }
    
//   public void setClassification(GesDocViewer viewer, boObject document)  throws boRuntimeException
//    {
//        if(document != null && value != null && value.length() > 0)
//        {
//            boBridgeIterator bit = document.getBridge("classification").iterator();
//            bit.beforeFirst();
//            long auxL;
//            String auxS;
//            boolean found = false;
//            while(bit.next() && !found)
//            {
//                auxL = bit.currentRow().getAttribute("valueClassification").getValueLong();
//                if(auxL == viewer.getClassBoui())
//                {
//                    auxL = bit.currentRow().getObject() != null ? bit.currentRow().getObject().getBoui():-1;
//                    if(auxL == getGesDocBoui())
//                    {
//                        found = true;
//                        auxS = bit.currentRow().getAttribute("valueText").getValueString();
//                        if(!value.equalsIgnoreCase(auxS))
//                        {
//                            bit.currentRow().getAttribute("valueText").setValueString(value);
//                        }
//                    }
//                }
//            }
//            if(!found)
//            {
//                bridgeHandler bh = document.getBridge("classification");
//                bh.add(getGesDocBoui());
//                bh.getAttribute("valueText").setValueString(value);
//                bh.getAttribute("valueClassification").setValueLong(viewer.getClassBoui());
//            }
//        }
//    }

    public void setClassification(GesDocViewer viewer, boObject document)  throws boRuntimeException
    {
        if(document != null && value != null && value.length() > 0)
        {
            boBridgeIterator bit = document.getBridge("classification").iterator();
            bit.beforeFirst();
            String auxGS;
            long auxL;
            String auxS;
            boolean found = false;
            if(viewer.isEditing())
            {
                while(bit.next() && !found)
                {
                    auxGS = bit.currentRow().getAttribute("groupSeq").getValueString();
                    if(auxGS.equals(viewer.getGroupSequence()))
                    {
                        auxL = bit.currentRow().getObject() != null ? bit.currentRow().getObject().getBoui():-1;
                        if(auxL == getGesDocBoui())
                        {
                            found = true;
                            auxS = bit.currentRow().getAttribute("valueText").getValueString();
                            if(!value.equalsIgnoreCase(auxS))
                            {
                                bit.currentRow().getAttribute("valueText").setValueString(value);
                            }
                        }
                    }
                }
            }
            if(!found && !hasThisClassifs(viewer, document))
            {
                bridgeHandler bh = document.getBridge("classification");
                bh.add(getGesDocBoui());
                bh.getAttribute("valueText").setValueString(value);
                bh.getAttribute("valueClassification").setValueLong(viewer.getClassBoui());
                bh.getAttribute("groupSeq").setValueString(viewer.getGroupSequence());
                bh.getAttribute("segmento").setValueString(Segmento.getSegmento(document.getEboContext(), viewer.getClassBoui()));
            }
        }
    }
    public boolean hasThisClassifs(GesDocViewer viewer, boObject document) throws boRuntimeException
    {
        if(document != null && value != null && value.length() > 0)
        {
            boBridgeIterator bit = document.getBridge("classification").iterator();
            bit.beforeFirst();
            long auxL;
            String auxS;
            while(bit.next())
            {
                auxL = bit.currentRow().getObject() != null ? bit.currentRow().getObject().getBoui():-1;
                if(auxL == getGesDocBoui())
                {
                    auxS = bit.currentRow().getAttribute("valueText").getValueString();
                    if(value.equalsIgnoreCase(auxS))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public String getValue()  throws boRuntimeException
    {
        return value;
    }

    public void setRuntime(boObject runtime)  throws boRuntimeException
    {
        if(runtime != null && value != null && value.length() > 0)
        {
            boObject o = runtime.getBridge("answers").addNewObject();
            o.getAttribute("classificacao").setValueLong(getGesDocBoui());
            o.getAttribute("resposta").setValueString(value);
        }
    }
    
    public void validate(EboContext boctx, ArrayList erros) throws boRuntimeException
    {
        if( getObrigatorio( boctx ) )
        {
            if(value == null || value.length() == 0)
            {
                if(erros.indexOf("Preencha os campos obrigatórios") == -1)
                {
                    erros.add("Preencha os campos obrigatórios");
                }
            }
        }
        if(erros.size() == 0 && validation != null && validation.length() > 0)
        {
            javaValidation(boctx, erros);
        }
    
    }
    
    private boolean javaValidation(EboContext boctx, ArrayList erros) throws boRuntimeException
    {
        JavaExecuter javaExec = new JavaExecuter(name);
        //imports
        javaExec.addImport("netgest.bo");
        javaExec.addImport("netgest.bo.def");
        javaExec.addImport("netgest.utils");
        javaExec.addImport("netgest.bo.runtime");
        javaExec.addImport("netgest.bo.utils");
        javaExec.addImport("netgest.bo.impl.document.merge.gestemp");
    
        //variaveis
        Contexto contexto = new Contexto(boctx);
        javaExec.addTypedVariable( "contexto", Contexto.class, contexto, null);
        javaExec.addTypedVariable( internalName, Classificacao.class, new Classificacao(boctx, clfViewer), null);
        javaExec.addTypedVariable( "classificacao", Classificacao.class, new Classificacao(boctx, clfViewer), null);
        

        //javaCode
        javaExec.setJavaCode(validation);

        Object result = javaExec.execute();
        if( javaExec.sucess() )
        {
        if(result != null && result instanceof Boolean)
        {
            if(!((Boolean)result).booleanValue())
            {
                for (int i = 0; i < contexto.getErros().size(); i++) 
                {
                    erros.add(contexto.getErros().get(i));
                }
                return false;
            }
            }
        }
        else
        {
            String sErrorMessage = "Erro a validar atributo [" + this.getName() + "].\n" + 
                    javaExec.getErrorMessage();
            contexto.addErro( sErrorMessage );
            return false;
        }
        return true;
    }


    public void setClfViewer(GesDocViewer clfViewer)
    {
        this.clfViewer = clfViewer;
    }


    public GesDocViewer getClfViewer()
    {
        return clfViewer;
    }
}