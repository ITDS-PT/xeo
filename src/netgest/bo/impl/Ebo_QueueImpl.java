/*Enconding=UTF-8*/
package netgest.bo.impl;

import bsh.BshClassManager;
import bsh.NameSpace;
import bsh.UtilEvalError;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boEvent;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.robots.boQueueAgent;

import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;

import netgest.utils.ngtXMLHandler;

import netgest.xwf.common.xwfBoManager;

import netgest.xwf.core.xwfECMAevaluator;

import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XSLException;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Title: Ebo_QueueImpl </p>
 * <p>Description: Classe que ajuda a composição do código a ser executado.</p>
 * <p> O formato do xml: </p>
 * <request>
 *            <imports>
 *                    <class><![CDATA[]]></class>
 *                    <package><![CDATA[]]></package>
 *          </imports>
 *            <code ctxName=''><![CDATA[]]></code>
 * </request>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * @Company: Enlace3 </p>
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 1.0
 */
public abstract class Ebo_QueueImpl extends boObject
{
    public static final String XEP_CODE  = "XEP";
    public static final String JAVA_CODE = "JAVA";
    private ngtXMLHandler internalCode   = null;

    /**
    * Instancia os atributos necessários para ajudar a compor e a executar o código.
    */
    public void init()
        throws boRuntimeException
    {
        super.init();

        String code = this.getAttribute("code").getValueString();

        if ((code != null) && (code.length() > 0))
        {
            internalCode = new ngtXMLHandler(code);
        }
        else
        {
            internalCode = new ngtXMLHandler(
                    "<request><imports></imports><code><![CDATA[]]></code></request>"
                );
        }
    }

    /**
    * Antes de realizar a actualização faz o set dos atributos que foram alterados para a execução do código.
    * @return result, TRUE.
    */
    public boolean onBeforeSave(boEvent event)
        throws boRuntimeException
    {
        if (!isExecuted())
        {
            try
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                internalCode.getDocument().print(out, "UTF-8");
                this.getAttribute("code").setValueString(out.toString());
            }
            catch (IOException e)
            {
                //ignorar
            }
        }

        return true;
    }

    /**
     * Adiciona o código a ser exexutado.
     * @param code , código a ser executado.
     * @param codeType , se é XEP ou JAVA.
     */
    public void addCode(String code, String codeType)
        throws boRuntimeException
    {
        if (!isExecuted())
        {
            try
            {
                this.getAttribute("type").setValueString(codeType);

                CDATASection cdataNode = internalCode.getDocument().createCDATASection(code);

                //                CharacterData cdata = cdataNode;
                //                cdata.setData(code);
                Node node = internalCode.getDocument().selectSingleNode("//code");
                node.appendChild(cdataNode);

                //node.appendChild(node.getOwnerDocument().appendChild(cdataNode));                                 
            }
            catch (XSLException e)
            {
                //ignorar
            }
        }
        else
        {
            throw new RuntimeException(MessageLocalizer.getMessage("ALREADY_EXECUTED"));
        }
    }

    /**
     * Definir o nome do contexto a ser usado na execução.
     * @param name , código a ser executado.
     */
    public void setContextVarName(String name)
    {
        Attr att = internalCode.getDocument().createAttribute("ctxName");
        att.setValue(name);

        try
        {
            Node node = internalCode.getDocument().selectSingleNode("//code");
            (( XMLElement ) node).setAttributeNode(att);
        }
        catch (XSLException e)
        {
            //ignorar
        }
    }

    /**
     * Adiciona um import de uma classe a ser utilizado na execução.
     * @param importCode , import a ser adicionado.
     */
    public void addImportClass(String importCode)
        throws boRuntimeException
    {
        if (!isExecuted())
        {
            addImport(importCode, "class");
        }
        else
        {
            throw new RuntimeException(MessageLocalizer.getMessage("ALREADY_EXECUTED"));
        }
    }

    /**
     * Adiciona um import de um package a ser utilizado na execução.
     * @param importCode , import a ser adicionado.
     */
    public void addImportPackage(String importCode)
        throws boRuntimeException
    {
        if (!isExecuted())
        {
            addImport(importCode, "package");
        }
        else
        {
            throw new RuntimeException(MessageLocalizer.getMessage("ALREADY_EXECUTED"));
        }
    }

    /**
     * Adiciona um import a ser utilizado na execução.
     * @param importCode , import a ser adicionado.
     * @param importType , tipo do import (class e package).
     */
    private void addImport(String importCode, String importType)
        throws boRuntimeException
    {
        try
        {
            //            ngtXMLHandler importNode = new ngtXMLHandler("<"+importType+"><![CDATA[]]></"+importType+">");
            //            CDATASection cdataNode = importNode.getDocument().createCDATASection("");
            //            CharacterData cdata = cdataNode;
            //            cdata.setData(importCode);
            //            importNode.getFirstChild().getNode().appendChild(cdataNode);
            //                           
            //            Node root = importNode.getDocument().getDocumentElement();              
            Node imps = internalCode.getDocument().selectSingleNode("//imports");

            //            internalCode.getDocument().adoptNode(root);
            //            imps.appendChild(imps.getOwnerDocument().importNode(root,true));
            Node impType = internalCode.getDocument().createElement(importType);
            impType.appendChild(internalCode.getDocument().createCDATASection(importCode));
            imps.appendChild(impType);
        }
        catch (XSLException e)
        {
            //ignorar
        }
    }

    /**
     * Devolve se o código já foi executado.
     * @return result , true caso já tenha sido executado e false caso contrário.
     */
    private boolean isExecuted()
        throws boRuntimeException
    {
        boolean result = false;
        long executed  = this.getAttribute("executed").getValueLong();

        if (executed == 1)
        {
            result = true;
        }

        return result;
    }

    /**
     * Devolve lista de imports de classes.
     * @return List, lista de imports de classes.
     */
    private List getClassImports()
    {
        return getImports("class");
    }

    /**
     * Devolve lista de imports de packages.
     * @return List, lista de imports de packages.
     */
    private List getPackageImports()
    {
        return getImports("package");
    }

    /**
     * Devolve lista de imports conforme o tipo.
     * @param importType , tipo de import a devolver (class ou package).
     * @return imports, lista de imports.
     */
    private List getImports(String importType)
    {
        List imports         = new ArrayList();
        ngtXMLHandler[] list = internalCode.getFirstChild().getChildNode("imports").getChildNodes();

        for (int i = 0; i < list.length; i++)
        {
            if (importType.equals(list[i].getNodeName()))
            {
                imports.add(list[i].getCDataText());
            }
        }

        return imports;
    }

    /**
     * Devolve o código a ser executado.
     * @return code, código a ser executado.
     */
    private String getCode()
    {
        return internalCode.getFirstChild().getChildNodeText("code", null);
    }

    /**
     * Devolve o nome da variavel de contexto.
     * @return name, nome da variavel do contexto.
     */
    private String getContextVarName()
    {
        String name         = null;
        ngtXMLHandler ncode = internalCode.getFirstChild().getChildNode("code");

        if (ncode != null)
        {
            name = ncode.getAttribute("ctxName", null);
        }

        return name;
    }

    /**
     * Chamada para executar o código, avalia e decide se é XEP ou JAVA.
     * @param ctx , EboContext, contexto para a execução.
     * @return result, true caso tenha sucesso, false cc.
     */
    public boolean execute(EboContext ctx)
        throws boRuntimeException, boLoginException
    {
        boolean result     = false;
        EboContext userCtx = null;
        boSession userSession = null;
        try
        {            
            long toPerfBoui       = this.getAttribute("toperf").getValueLong();
            boObject toPerf       = null;

            if (toPerfBoui != 0)
            {
                toPerf          = this.getBoManager().loadObject(ctx, toPerfBoui);
                userSession     = ctx.getApplication().boLogin(
                        toPerf.getAttribute("username").getValueString(), boLoginBean.getSystemKey()
                    );
                userCtx = userSession.createRequestContext(null, null, null);
            }

            String type = this.getAttribute("type").getValueString();

            if (type.equals(XEP_CODE))
            {
                result = executeXepCode((userCtx == null) ? ctx : userCtx);
            }
            else if (type.equals(JAVA_CODE))
            {
                result = executeJavaCode((userCtx == null) ? ctx : userCtx);
            }
        }
        finally
        {
            try
            {
                if(userSession != null)
                {
                    userSession.closeSession();
                }
            }
            catch (Exception e)
            {
                
            }
            if (userCtx != null)
            {
                userCtx.close();
            }
        }

        return result;
    }

    /**
    * Chamada para executar o código em XEP.
    * @param ctx , EboContext, contexto para a execução.
    * @return result, true caso tenha sucesso, false cc.
    */
    private boolean executeXepCode(EboContext ctx)
        throws boRuntimeException
    {
        boolean result = false;

        xwfBoManager xm       = new xwfBoManager(ctx, null);
        xwfECMAevaluator ecma = new xwfECMAevaluator();
        List imp              = this.getPackageImports();

        for (int i = 0; i < imp.size(); i++)
        {
            ecma.addImportPackage(( String ) imp.get(i));
        }

        imp = this.getClassImports();

        for (int i = 0; i < imp.size(); i++)
        {
            ecma.addImportClass(( String ) imp.get(i));
        }

        String ctxVarName = this.getContextVarName();

        if (ctxVarName != null)
        {
            ecma.varChange(ctxVarName, EboContext.class, ctx);
        }

        Object xo = ecma.eval(xm, this.getCode());

        if (xo instanceof Boolean)
        {
            result = (( Boolean ) xo).booleanValue();
        }
        else
        {
            result = true;
        }

        return result;
    }

    /**
    * Chamada para executar o código em JAVA.
    * @param ctx , EboContext, contexto para a execução.
    * @return result, true caso tenha sucesso, false cc.
    */
    private boolean executeJavaCode(EboContext ctx)
        throws boRuntimeException
    {
        boolean result    = false;
        NameSpace nsp = new NameSpace(new BshClassManager(),"eboQueueJAVA");
//        bsh.NameSpace nsp = bsh.NameSpace.JAVACODE;

        nsp.importPackage("netgest.bo");
        nsp.importPackage("netgest.bo.def");
        nsp.importPackage("netgest.utils");
        nsp.importPackage("netgest.bo.runtime");
        nsp.importPackage("netgest.bo.utils");

        List imp = this.getPackageImports();

        for (int i = 0; i < imp.size(); i++)
        {
            nsp.importPackage(( String ) imp.get(i));
        }

        imp = this.getClassImports();

        for (int i = 0; i < imp.size(); i++)
        {
            nsp.importClass(( String ) imp.get(i));
        }

        String ctxVarName = this.getContextVarName();

        if (ctxVarName != null)
        {
            try
            {
                nsp.setTypedVariable(ctxVarName, EboContext.class, ctx, null);
            }
            catch (UtilEvalError e)
            {
            }
        }

        bsh.Interpreter bshi = new bsh.Interpreter();
        bshi.setNameSpace(nsp);

        try
        {
            Object xo = bshi.eval(this.getCode());

            if (xo instanceof Boolean)
            {
                result = (( Boolean ) xo).booleanValue();
            }
            else
            {
                result = true;
            }
        }
        catch (Exception e)
        {
            throw new boRuntimeException("Ebo_QueueImpl", "executeJavaCode", e);
        }

        return result;
    }

    public void onCommit()
        throws boRuntimeException
    {
        super.onCommit();

        Object[] agent = getEboContext().getBoSession().getApplication()
                                                      .getBoAgentsController().getThreadByName(
                "boQueue Agent"
            );

        if ((agent != null) && (agent.length > 0))
        {
            ((boQueueAgent)agent[0]).runNow();
        }
    }
}
