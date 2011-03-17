/*Encoding=UTF-8*/
package netgest.bo.def.v2;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefMethod;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSessionUser;
import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Node;


public class boDefMethodImpl extends ngtXMLHandler implements boDefMethod
{

	private String[] 		 p_argsnames;
    private String[] 		 p_argclassnames;
    private Class[] 		 p_argsclasses;
    private boDefHandlerImpl p_defhandler;
    private String 			 p_name;
    private boolean 		 p_requiredtrans = false;
    private boolean 		 p_isnative = false;
    private boolean 		 p_overwrite = false;
    private boolean 		 p_menu = false;
    private boolean 		 p_toolbar = false;
    
    private String  		 p_label;
    
    private String 			 p_body;
    private String 			 p_returntype;
    private boDefXeoCodeImpl p_hiddenWhen;
    private boolean 		 p_openDoc;
    private String 			 p_toObject;
    private boDefAttribute 	 p_parentAtt;
    
    private ngtXMLHandler[]  p_javascriptToRun;
    
    //Name used on file languages properties  as prefix of a method     
    public static final String METHOD_PROPERTY = "method";
    
    private boDefHandler defHandler;
	private String language;
	private String nome;
	private String AttributeName;
    

    public boDefMethodImpl(boDefHandlerImpl defh, Node xml)
    {
        this( defh, xml, null );
    }
    
    public boDefMethodImpl(boDefHandlerImpl defh, Node xml, boDefAttribute parentAtt )
    {
        super(xml);
        this.p_defhandler = defh;
        this.p_parentAtt = parentAtt;
        parse();
    }
    public boDefMethodImpl(String name, String rettype, String[] argnames,String[] argclassnames, Class[] argclasses, boDefHandlerImpl defh, Node xml )
    {
        this(name, rettype, argnames,argclassnames, argclasses, defh, xml, null);
    }

    public boDefMethodImpl(String name, String rettype, String[] argnames, String[] argclassnames, Class[] argclasses, boDefHandlerImpl defh, Node xml, boDefAttribute parentAtt )
    {
        super(xml);
        p_name = name;
        p_argsnames = argnames;
        p_argclassnames = argclassnames;
        p_argsclasses = argclasses;
        p_isnative = true;
        p_returntype = rettype;

        if (xml != null)
        {
            ngtXMLHandler xnode = super.getChildNode("body");

            if (xnode != null)
            {
                p_body = xnode.getText();
            }
        }
    }

    private void parse()
    {
        try
        {
            ngtXMLHandler xnode = super.getChildNode("assinature");
            
            p_name          = getAttribute("name","");
            p_menu          = GenericParseUtils.parseBoolean( getAttribute("menu") );
            p_toolbar       = GenericParseUtils.parseBoolean( getAttribute("toolbar") );
            p_requiredtrans = GenericParseUtils.parseBoolean( getAttribute("requiredTransaction") );
            p_openDoc       = GenericParseUtils.parseBoolean( getAttribute("openDoc") );
            p_toObject      = super.getAttribute("toObject");
            p_label         = super.getChildNodeText( "Label", p_name );

            if (xnode != null)
            {
                ngtXMLHandler[] x = xnode.getChildNodes();
                p_argclassnames = new String[x.length];
                p_argsclasses = new Class[x.length];
                p_argsnames = new String[x.length];

                for (int i = 0; i < x.length; i++)
                {
                    p_argsnames[i] = x[i].getNodeName();
                    p_argclassnames[i] = x[i].getAttribute("type", "String");
                    p_argsclasses[i] = ClassUtils.parseArgument(p_argclassnames[i]);
                }
            }

            xnode = super.getChildNode("hiddenWhen");
            if (xnode != null)
            {
                p_hiddenWhen = GeneralParseUtils.parseCode( xnode );
            }

            xnode = super.getChildNode("javascriptToRun");
            if (xnode != null)
            {
                p_javascriptToRun = xnode.getChildNodes();
            }
            else
            {
                p_javascriptToRun = null;
            }

            xnode = super.getChildNode("assinature");
            if (xnode != null)
            {
                p_returntype = xnode.getAttribute("return", "void");
                ngtXMLHandler[] argumNodes = xnode.getChildNodes();
                
                p_argclassnames = new String[ argumNodes.length ];
                p_argsnames     = new String[ argumNodes.length ];
                
                for (int i = 0; i < p_argclassnames.length; i++)
                {
                    p_argsnames[i] = argumNodes[i].getAttribute("name","");
                    p_argclassnames[i] = argumNodes[i].getAttribute("type","String");
                }
            }
            else
            {
                p_returntype = "void";
            }

            xnode = super.getChildNode("body");
            if (xnode != null)
            {
                p_body = xnode.getText();
            }
        }
        catch (boRuntimeException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getName()
    {
        return p_name;
    }

    public boolean getRequireTransaction()
    {
        return p_requiredtrans;
    }

    public String[] getAssinatureClassNames()
    {
        return p_argclassnames;
    }

    public Class[] getAssinatureClasses()
    {
        return p_argsclasses;
    }

    public String[] getAssinatureArgNames()
    {
        return p_argsnames;
    }

    public boolean getIsNative()
    {
        return p_isnative;
    }

    public boolean getIsNativeOverwrited()
    {
        return p_overwrite;
    }

    public boolean getIsMenu()
    {
        return p_menu;
    }

    public boolean getIsToolbar()
    {
        return p_toolbar;
    }

    public String getBody()
    {
        return p_body;
    }

    public String getReturnType()
    {
        return p_returntype;
    }
    
    
    /**
     * returns the label of the attribute in the current language
     */
    public String getLabel()
    {
    	defHandler = getBoDefHandler();
		language = defHandler.getBoLanguage();
		nome = defHandler.getName();
		AttributeName =this.getName();
		boSessionUser boUser = boApplication.currentContext().getEboContext().getBoSession().getUser();
		language=boUser.getLanguage();
		
	  String label=	boDefHandlerImpl.getTranslation(nome, p_label,METHOD_PROPERTY, language,AttributeName,"label");
		return label;
    }

    public boDefXeoCode getHiddenWhen()
    {
        return p_hiddenWhen;
    }

    public String getJavaScriptToRunBefore(String viewerName)
    {
        for (int i = 0;
                (p_javascriptToRun != null) && (i < p_javascriptToRun.length);
                i++)
        {
            if (p_javascriptToRun[i].getAttribute("name").equals(viewerName))
            {
                return (p_javascriptToRun[i].getChildNode("before") != null)
                ? p_javascriptToRun[i].getChildNode("before").getText() : "";
            }
        }

        return "";
    }

    public String getJavaScriptToRunAfter(String viewerName)
    {
        for (int i = 0;
                (p_javascriptToRun != null) && (i < p_javascriptToRun.length);
                i++)
        {
            if (p_javascriptToRun[i].getAttribute("name").equals(viewerName))
            {
                return (p_javascriptToRun[i].getChildNode("after") != null)
                ? p_javascriptToRun[i].getChildNode("after").getText() : "";
            }
        }

        return "";
    }

    public boolean templateMode()
    {
        ngtXMLHandler labnode = super.getChildNode("modeTemplate");

        if (labnode == null)
        {
            return true;
        }
        else
        {
            String mt = labnode.getText();

            if ((mt == null) || mt.equalsIgnoreCase("Y"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public static final boolean compareMethodAssinature(Class[] leftargs,
        Class[] rightargs)
    {
        boolean equals = false;

        if (leftargs.length == rightargs.length)
        {
            equals = true;

            for (byte i = 0; i < rightargs.length; i++)
            {
                if (!rightargs[i].getName().equals(leftargs[i].getName()))
                {
                    equals = false;

                    break;
                }
                else
                {
                    equals = true;
                }
            }
        }

        return equals;
    }
    
    public boolean openDoc()
    {
        return p_openDoc;
    }
    
    public String getObjectName()
    {
        return p_toObject;
    }
    
    public boDefHandler getBoDefHandler()
    {
        return p_defhandler;
    }
    
    public boDefAttribute getParentAttribute()
    {
        return p_parentAtt;
    }
}
