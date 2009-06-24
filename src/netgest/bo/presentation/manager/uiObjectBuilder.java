/*Enconding=UTF-8*/
package netgest.bo.presentation.manager;

import netgest.bo.boConfig;

import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectUpdateQueue;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;

import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class uiObjectBuilder
{
    public static void buildWorkPlaceDefault(EboContext ctx)
        throws boRuntimeException
    {
        boConfig.getApplicationConfig().refresh();

        ngtXMLHandler[] workPlaces = boConfig.getWorkPlaces();

        for (int i = 0; i < workPlaces.length; i++)
        {
            buildUIObjects(workPlaces[i], ctx);
        }
    }

    public static void buildUIObjects(ngtXMLHandler xmlWorkPlace, EboContext ctx)
        throws boRuntimeException
    {
        ngtXMLHandler[] uiObjects = xmlWorkPlace.getFirstChild().getChildNodes();
        ArrayList uiBoObjects     = new ArrayList();

        for (int i = 0; i < uiObjects.length; i++)
        {
            if (uiObjects[i].getNodeName().toUpperCase().equals("WORKPLACE"))
            {
                ngtXMLHandler profile    = uiObjects[i].getChildNode("profiles");
                ngtXMLHandler[] profiles = profile.getChildNodes();

                for (int j = 0; j < profiles.length; j++)
                {
                    String profileName = profiles[j].getText();
                    boObject oprofile  = getProfile(ctx, profileName);

                    buildWorkPlace(ctx, uiObjects, uiObjects[i], oprofile.getBoui());
                }
            }
        }
    }

    public static boObject getProfile(EboContext ctx, String profileName)
        throws boRuntimeException
    {
        boObjectList listProfiles = boObjectList.list(
                ctx, "select uiProfile where name='" + profileName + "'"
            );
        listProfiles.beforeFirst();

        boObject profileToret = null;

        if (!listProfiles.first())
        {
            profileToret = boObject.getBoManager().createObject(ctx, "uiProfile"); // A descrição tem de entrar
            profileToret.getAttribute("name").setValueString(profileName);
            profileToret.getAttribute("description").setValueString(profileName);
            profileToret.update();
        }
        else
        {
            profileToret = listProfiles.getObject();
        }

        return profileToret;
    }

    public static void buildWorkPlace(
        EboContext ctx, ngtXMLHandler[] uiObjects, ngtXMLHandler xmlWorkPlace, long profileBoui
    )
        throws boRuntimeException
    {
        String workPlaceName        = xmlWorkPlace.getAttribute("name");
        boObjectList listWorkPlaces = boObjectList.list(
                ctx, "select uiWorkPlace where user is null and name='" + workPlaceName + "'"
            );
        boObject objectWorkPlace = null;

        if (!listWorkPlaces.first())
        {
            objectWorkPlace = boObject.getBoManager().createObject(ctx, "uiWorkPlace");
        }
        else
        {
            objectWorkPlace = listWorkPlaces.getObject();
        }

        //actualizar boObject
        objectWorkPlace.getAttribute("name").setValueString(workPlaceName);
        objectWorkPlace.getAttribute("description").setValueString(xmlWorkPlace.getAttribute("description"));
        objectWorkPlace.getAttribute("img").setValueString(xmlWorkPlace.getAttribute("image"));
        objectWorkPlace.getAttribute("profile").setValueLong(profileBoui);
		objectWorkPlace.getAttribute("largura").setValueString(xmlWorkPlace.getAttribute("largura"));
		
        String listBarName = xmlWorkPlace.getAttribute("listbar");

        for (int i = 0; i < uiObjects.length; i++)
        {
            if (
                uiObjects[i].getNodeName().toUpperCase().equals("BAR") &&
                uiObjects[i].getAttribute("name").equals(listBarName)
            )
            {
                boObject listBar = getListBar(ctx, uiObjects, uiObjects[i]);
                objectWorkPlace.getAttribute("listbar").setValueLong(listBar.getBoui());

                break;
            }
        }

        //objectWorkPlace.getUpdateQueue().add(  );
        objectWorkPlace.update();
    }

    public static boObject getListBar(EboContext ctx, ngtXMLHandler[] uiObjects, ngtXMLHandler xmlListBar)
        throws boRuntimeException
    {
        String listBarName    = xmlListBar.getAttribute("name");
        boObjectList listBars = boObjectList.list(
                ctx, "select uiListBar where user is null and name='" + listBarName + "'"
            );
        boObject objectListBar = null;

        if (!listBars.first())
        {
            objectListBar = boObject.getBoManager().createObject(ctx, "uiListBar");
        }
        else
        {
            objectListBar = listBars.getObject();
        }

        //actualizar boObject
        objectListBar.getAttribute("name").setValueString(listBarName);
        objectListBar.getAttribute("description").setValueString(xmlListBar.getAttribute("description"));
        objectListBar.getAttribute("img").setValueString(xmlListBar.getAttribute("image"));

        ngtXMLHandler content  = xmlListBar.getChildNode("content");
        ngtXMLHandler[] childs = content.getChildNodes();
        objectListBar.getBridge("content").truncate();

        for (int i = 0; i < childs.length; i++)
        {
            String type = childs[i].getNodeName().toUpperCase();

            if (type.equalsIgnoreCase("TREE"))
            {
                type = "UITREE";
            }

            String objName = childs[i].getAttribute("name");

            for (int j = 0; j < uiObjects.length; j++)
            {
                String xx    = uiObjects[j].getNodeName();
                String xname = uiObjects[j].getAttribute("name");

                if (
                    uiObjects[j].getNodeName().toUpperCase().equals(type) &&
                    uiObjects[j].getAttribute("name").equals(objName)
                )
                {
                    boObject obj = null;

                    if (type.equals("UITREE"))
                    {
                        obj = getTree(ctx, uiObjects[j]);
                    }
                    else if (type.equals("HTML"))
                    {
                        obj = getHTMLobject(ctx, uiObjects[j]);
                    }

                    if (obj != null)
                    {
                        objectListBar.getBridge("content").add(obj.getBoui());
                    }

                    break;
                }
            }
        }

        return objectListBar;
    }

    public static boObject getTree(EboContext ctx, ngtXMLHandler xmlTree)
        throws boRuntimeException
    {
        String treeName        = xmlTree.getAttribute("name");
        boObjectList listTrees = boObjectList.list(
                ctx, "select uiTreeLB where user is null and name='" + treeName + "'"
            );
        boObject objectTree = null;

        if (!listTrees.first())
        {
            objectTree = boObject.getBoManager().createObject(ctx, "uiTreeLB");
        }
        else
        {
            objectTree = listTrees.getObject();
        }

        //actualizar boObject
        objectTree.getAttribute("name").setValueString(treeName);
        objectTree.getAttribute("description").setValueString(xmlTree.getAttribute("description"));

        XMLDocument xmldoc = new XMLDocument();
        xmldoc.setEncoding("UTF-8");
        xmldoc.appendChild(xmldoc.importNode(xmlTree.getChildNode("tree").getNode(), true));

        String xx = ngtXMLUtils.getXML(xmldoc);

        ngtXMLHandler x = new ngtXMLHandler(xmldoc);
        findFilterQuery(ctx, x, treeName);

        String xx1 = ngtXMLUtils.getXML(xmldoc);
        objectTree.getAttribute("xmlTree").setValueString(xx1);

        return objectTree;
    }

    public static boObject getHTMLobject(EboContext ctx, ngtXMLHandler xmlhtmlcode)
        throws boRuntimeException
    {
        String htmlcodeName        = xmlhtmlcode.getAttribute("name");
        boObjectList listhtmlcodes = boObjectList.list(
                ctx, "select uiObjectHTML where user is null and name='" + htmlcodeName + "'"
            );
        boObject objecthtmlcode = null;

        if (!listhtmlcodes.first())
        {
            objecthtmlcode = boObject.getBoManager().createObject(ctx, "uiObjectHTML");
        }
        else
        {
            objecthtmlcode = listhtmlcodes.getObject();
        }

        //actualizar boObject
        objecthtmlcode.getAttribute("name").setValueString(htmlcodeName);
        objecthtmlcode.getAttribute("description").setValueString(xmlhtmlcode.getAttribute("description"));
        objecthtmlcode.getAttribute("htmlcode").setValueString(xmlhtmlcode.getText());

        return objecthtmlcode;
    }

    private static void findFilterQuery(EboContext ctx, ngtXMLHandler treeHandler, String treeName)
        throws boRuntimeException
    {
        String filterBoui  = null;
        NodeList list      = null;
        ngtXMLHandler node = null;

        try
        {
            list = treeHandler.getDocument().selectNodes("//optionObject");

            for (int i = 0; i < list.getLength(); i++)
            {
                node           = new ngtXMLHandler(list.item(i));
                filterBoui     = node.getAttribute("filterBoui");

                if ((filterBoui == null) || "".equals(filterBoui))
                {
                    String boql = (node.getChildNode("boql") == null) ? null
                                                                      : node.getChildNode("boql").getText();

                    if (boql != null)
                    {
                        filterBoui = getFilterQuery(ctx, boql, node.getAttribute("object"), treeName);

                        Attr attr = treeHandler.getDocument().createAttribute("filterBoui");
                        attr.setValue(filterBoui);
                        (( XMLElement ) node.getNode()).setAttributeNode(attr);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        //return treeLb.getXML();
    }

    private static String getFilterQuery(EboContext ctx, String boql, String object, String name)
        throws boRuntimeException
    {
        String result = null;

        if ((boql != null) && !"".equals(boql))
        {
            boObject filter      = null;
            boObjectList filters = boObjectList.list(ctx,  "SELECT Ebo_Filter WHERE boql = '" + QLParser.stupidEncode(boql) + "'");
            filters.beforeFirst();

            if (filters.next())
            {
                filter = filters.getObject();
            }
            else
            {
                boObject eboClsReg = null;
                boObjectList list  = boObjectList.list(
                        ctx, "SELECT Ebo_ClsReg WHERE name = '" + object + "'"
                    );
                list.beforeFirst();

                if (list.next())
                {
                    eboClsReg     = list.getObject();
                    filter        = boObject.getBoManager().createObject(ctx, "Ebo_Filter");
                    filter.getAttribute("boql").setValueString(boql);
                    filter.getAttribute("masterObjectClass").setValueLong(eboClsReg.getBoui());
                    filter.getAttribute("name").setValueString(object + ", Filter for Tree " + name);
                    filter.update();
                }
            }

            if (filter != null)
            {
                result = String.valueOf(filter.getBoui());
            }
        }

        return result;
    }

    public static void removeUserWorkPlaces(EboContext ctx)
        throws boRuntimeException
    {
        long boui               = 0;
        boObject wp             = null;
        boObject object         = null;
        boObject objectAux      = null;
        bridgeHandler bridgeAux = null;
        boObjectList bridge     = null;
        List toDestroy          = new ArrayList();
        bridge                  = boObjectList.list(ctx, "SELECT uiWorkPlace WHERE user IS NOT NULL");
        bridge.beforeFirst();

        while (bridge.next())
        {
            wp         = bridge.getObject();
//            object     = wp.getAttribute("listbar").getObject();

            if (object != null)
            {
             //  object.getBridge("content").truncate();
               // object.update();

           //     wp.getUpdateQueue().add( object , boObjectUpdateQueue.MODE_DESTROY_FORCED );
               // object.destroy();
                //                wp.getAttribute("listbar").setValueString(null);
                //                bridgeAux = object.getBridge("content");
                //                bridgeAux.beforeFirst();
                //                while(bridgeAux.next())
                //                {
                //                    objectAux = bridgeAux.getObject();                
                //                    boui = objectAux.getAttribute("user").getValueLong();
                //                    if(boui > 0)
                //                    {
                //                        toDestroy.add(objectAux);
                //                    }                
                //                }    
                //                                
                //                wp.destroy();
                //                for (int i = 0; i < toDestroy.size(); i++) 
                //                {                    
                //                    ((boObject)toDestroy.get(i)).destroy();                                
                //                }                             
            }

           
            wp.destroyForce();
        }
        
         bridge     = boObjectList.list(ctx, "SELECT uiTreeLB WHERE user IS NOT NULL");
         bridge.beforeFirst();
         while ( bridge.next() )
         {
             bridge.getObject().destroyForce();
         }
         bridge     = boObjectList.list(ctx, "SELECT uiListBar WHERE user IS NOT NULL");
         bridge.beforeFirst();
         while ( bridge.next() )
         {
             bridge.getObject().destroyForce();
         }
    
        bridge     = boObjectList.list(ctx, "SELECT uiObjectHTML WHERE user IS NOT NULL");
         bridge.beforeFirst();
         while ( bridge.next() )
         {
             bridge.getObject().destroyForce();
         }

//        bridge.beforeFirst();
//        wp.isInOnSave = boObject.READYSTATE_COMPLETE;
//        while (bridge.next())
//        {
//            wp = bridge.getObject();
//            wp.destroy();
//        }
    }
}
