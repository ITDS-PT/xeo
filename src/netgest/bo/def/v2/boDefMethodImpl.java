/*Enconding=UTF-8*/
package netgest.bo.def.v2;

import netgest.bo.data.DataSet;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefMethod;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.def.v2.boDefHandlerImpl;
import netgest.bo.runtime.boRuntimeException;

import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Node;


public class boDefMethodImpl extends ngtXMLHandler implements boDefMethod
{
    private static final String[] nnames = 
    {
        "load", "load", "create", "create", "update", "edit", "revertToSaved",
        "saveAsTemplate"
    };
    private static final String[][] nargtypes = 
    {
        { "long" },
        { "String" },
        { "long" },
        { "long", "DataSet" },
        {  },
        {  },
        {  },
        {  }
    };
    private static final String[][] nargnames = 
    {
        { "xboui" },
        { "xboql" },
        { "xboui" },
        { "xboui", "xdata" },
        {  },
        {  },
        {  },
        {  }
    };
    private static final Class[][] nargclass = 
    {
        { Long.TYPE },
        { String.class },
        { Long.TYPE },
        { Long.TYPE, DataSet.class },
        {  },
        {  },
        {  },
        {  }
    };
    private static final String[] nrettypes = 
    {
        "void", "void", "void", "void", "void", "void", "void", "void"
    };
    private static final String[] caption = 
    {
        "Load", "Load", "New   ", "CreateWith", "Save", "Edit", "Cancel       ",
        "Save As Template"
    };
    private String[] p_argsnames;
    private String[] p_argclassnames;
    private Class[] p_argsclasses;
    private boDefHandlerImpl p_defhandler;
    private String p_name;
    private boolean p_requiredtrans = false;
    private boolean p_isnative = false;
    private boolean p_overwrite = false;
    private boolean p_menu = false;
    private boolean p_toolbar = false;
    
    private boolean p_public = false;
    private boolean p_serverOnly = false;
    private boolean p_modeTemplate = false;
    
    private String  p_label;
    
    private String p_body;
    private String p_returntype;
    private boDefXeoCodeImpl p_hiddenWhen;
    private boolean p_openDoc;
    private String p_toObject;
    private boDefAttribute p_parentAtt;
    
    private ngtXMLHandler[] p_javascriptToRun;

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
            p_public        = GenericParseUtils.parseBoolean( getAttribute("public") );
            p_menu          = GenericParseUtils.parseBoolean( getAttribute("menu") );
            p_toolbar       = GenericParseUtils.parseBoolean( getAttribute("toolbar") );
            p_requiredtrans = GenericParseUtils.parseBoolean( getAttribute("requiredTransaction") );
            p_serverOnly    = GenericParseUtils.parseBoolean( getAttribute("serverOnly") );
            p_openDoc       = GenericParseUtils.parseBoolean( getAttribute("openDoc") );
            p_modeTemplate  = GenericParseUtils.parseBoolean( getAttribute("modeTemplate") );
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

    public String getLabel()
    {
        return p_label;
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
        String ret = "";
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

    protected static final boDefMethodImpl[] checkNativeMethods(
        boDefMethodImpl[] methods, boDefHandlerImpl bodef)
    {
        boolean[] mexists = new boolean[nnames.length];

        for (byte i = 0; i < methods.length; i++)
        {
            byte npos;

            if ((npos = indexInNative(methods[i])) > -1)
            {
                mexists[npos] = true;
                methods[i] = new boDefMethodImpl(nnames[npos], nrettypes[npos],
                        nargnames[npos], nargtypes[npos], nargclass[npos],
                        bodef, methods[i].getNode());
                ((boDefMethodImpl)methods[i]).p_isnative = false;
                ((boDefMethodImpl)methods[i]).p_overwrite = true;
            }
        }

        boDefMethod[] bmethods = new boDefMethod[mexists.length];
        byte missmethods = 0;

        for (byte i = 0; i < mexists.length; i++)
        {
            if (!mexists[i])
            {
                bmethods[missmethods] = new boDefMethodImpl(nnames[i],
                        nrettypes[i], nargnames[i], nargtypes[i], nargclass[i],
                        bodef, null);
                missmethods++;
            }
        }

        if (missmethods > 0)
        {
            boDefMethodImpl[] nmethods = new boDefMethodImpl[methods.length +
                missmethods];
            System.arraycopy(methods, 0, nmethods, 0, methods.length);
            System.arraycopy(bmethods, 0, nmethods, methods.length, missmethods);
            methods = nmethods;
        }

        return methods;
    }

    private static final byte indexInNative(boDefMethod method)
    {
        byte i;
        boolean exists = false;
        String name = method.getName();

        for (i = 0; i < nnames.length; i++)
        {
            if (nnames[i].equals(name))
            {
                if (compareMethodAssinature(nargclass[i],
                            method.getAssinatureClasses()))
                {
                    exists = true;

                    break;
                }
            }
        }

        return exists ? i : (-1);
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
