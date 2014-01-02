/*Enconding=UTF-8*/
package netgest.bo.presentation.manager;

import netgest.bo.*;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;

import netgest.bo.utils.*;

import java.util.*;


/**
 *
 * @author JMF
 * @version
 * @see
 */
public class uiObjectManager
{
    /**
     *
     * @see
     */
    public static final String TYPE_WORKPLACE  = "workPlace";
    public static final String TYPE_LISTBAR    = "listBar";
    public static final String TYPE_TREELB     = "treeLB";
    public static final String TYPE_OBJECTHTML = "objectHTML";
    public static Hashtable uiObjects = new Hashtable();

    public static boObject getUiObject(EboContext ctx, String type)
        throws boRuntimeException
    {
        return getUiObjectByProfile(ctx, type, ctx.getBoSession().getPerformerIProfileBoui());
    }

    public static boObject getUiObjectByProfile(EboContext ctx, String type, long iprofile)
        throws boRuntimeException
    {
        boObject toRet       = null;
        String uiUserProfile = "";
        Long performer       = ctx.getBoSession().getPerformerBouiLong();

        String keyProfile = type + iprofile + uiUserProfile + "" + performer.toString();
        String keyUser    = type + iprofile + uiUserProfile;

        //iprofile
        boObject iprofileboObj = null;

        if (iprofile > 0)
        {
            iprofileboObj = boObject.getBoManager().loadObject(
                    ctx, "select uiProfile where boui=" + iprofile
                );
        }

        //        else
        //        {
        //            iprofileboObj = boObject.getBoManager().loadObject(ctx,"select uiProfile where name="+IProfileUtils.DEFAULT);
        //        }
        if (type.equals(TYPE_WORKPLACE))
        {
            //workPlace
            boObjectList workPlaceList = null;

            if (iprofileboObj != null)
            {
                workPlaceList = boObjectList.list(
                        ctx,
                        "select uiWorkPlace where profile=" + iprofileboObj.getBoui() + " and user=" +
                        performer.longValue()
                    );
            }
            else
            {
                workPlaceList = boObjectList.list(
                        ctx,
                        "select uiWorkPlace where upper(name) = '" + IProfileUtils.DEFAULT.toUpperCase() +
                        "' and user=" + performer.longValue()
                    );
            }

            workPlaceList.beforeFirst();

            if (workPlaceList.next())
            {
                toRet = workPlaceList.getObject();
            }
            else
            {
                if (iprofileboObj != null)
                {
                    workPlaceList = boObjectList.list(
                            ctx,
                            "select uiWorkPlace where profile=" + iprofileboObj.getBoui() +
                            " and user is null "
                        );
                }
                else
                {
                    workPlaceList = boObjectList.list(
                            ctx,
                            "select uiWorkPlace where upper(name) = '" + IProfileUtils.DEFAULT.toUpperCase() +
                            "' and user is null"
                        );
                }

                workPlaceList.beforeFirst();

                if (workPlaceList.next())
                {
                    toRet = workPlaceList.getObject();
                }
                else
                {
                    //                    BpmDefaultWorkPlace workPlaces = boConfig.getWorkPlaces();
                    //                    WorkPlace wPlace = null;
                    //                    if(iprofileboObj != null)
                    //                    {
                    //                        wPlace = workPlaces.getWorkPlaceForIProfile(iprofileboObj.getAttribute("name").getValueString());
                    //                    }
                    //                    else
                    //                    {
                    //                        wPlace = workPlaces.getWorkPlaceForIProfile(IProfileUtils.DEFAULT);
                    //                    }
                    //                    boObject uiWorkPlace = boObject.getBoManager().createObject(ctx, "uiWorkPlace");
                    //                    toRet = uiWorkPlace;
                    //                    uiWorkPlace.getAttribute("name").setValueString(wPlace.getName());
                    //                    uiWorkPlace.getAttribute("description").setValueString(wPlace.getDescription());
                    //                    uiWorkPlace.getAttribute("img").setValueString(wPlace.getImage());
                    //                    if(iprofileboObj != null)
                    //                    {
                    //                        uiWorkPlace.getAttribute("profile").setValueLong(iprofileboObj.getBoui());
                    //                    }
                    //                    
                    //                    //listBar
                    //                    boObject uiObject;
                    //                    boObject uiListBar = boObject.getBoManager().createObject(ctx, "uiListBar");
                    //                    uiWorkPlace.getAttribute("listbar").setValueLong(uiListBar.getBoui());
                    //                    Bar bar = wPlace.getBar();
                    //                    uiListBar.getAttribute("name").setValueString(bar.getName());
                    //                    uiListBar.getAttribute("description").setValueString(bar.getDescription());
                    //                    uiListBar.getAttribute("img").setValueString(bar.getImage());
                    //                    if(iprofileboObj != null)
                    //                    {
                    //                        uiListBar.getAttribute("profile").setValueLong(iprofileboObj.getBoui());
                    //                    }
                    //                    bridgeHandler bh = uiListBar.getBridge("content");
                    //                    ArrayList bars = bar.getBars();
                    //                    TreeLB auxTree;
                    //                    HTMLObject auxHTML;
                    //                    
                    //                    
                    //                    for (int i = 0; i < bars.size(); i++) 
                    //                    {
                    //                        if(bars.get(i) instanceof TreeLB)
                    //                        {
                    //                            auxTree = ((TreeLB)bars.get(i));
                    //                            uiObject = boObject.getBoManager().loadObject(ctx,"select uiTreeLB where name='"+auxTree.getName()+"' and profile is null and user is null");
                    //                            if(uiObject != null && uiObject.exists())
                    //                            {
                    //                                bh.add(uiObject.getBoui());
                    //                            }
                    //                            else
                    //                            {
                    //                                uiObject = bh.addNewObject("uiTreeLB");
                    //                                uiObject.getAttribute("name").setValueString(auxTree.getName());
                    //                                uiObject.getAttribute("description").setValueString(auxTree.getDescription());
                    //                                uiObject.getAttribute("img").setValueString(auxTree.getImage());
                    //                                uiObject.getAttribute("xmlTree").setValueString(auxTree.getXML());
                    ////                                if(iprofileboObj != null)
                    ////                                {
                    ////                                    uiObject.getAttribute("profile").setValueLong(iprofileboObj.getBoui());
                    ////                                }
                    //                            }
                    //                        }
                    //                        else
                    //                        {
                    //                            auxHTML = ((HTMLObject)bars.get(i));
                    //                            uiObject = boObject.getBoManager().loadObject(ctx,"select uiObjectHTML where name='"+auxHTML.getName()+"' and profile is null and user is null");
                    //                            if(uiObject != null && uiObject.exists())
                    //                            {
                    //                                bh.add(uiObject.getBoui());
                    //                            }
                    //                            else
                    //                            {
                    //                                uiObject = bh.addNewObject("uiObjectHTML");
                    //                                uiObject.getAttribute("name").setValueString(auxHTML.getName());
                    //                                uiObject.getAttribute("description").setValueString(auxHTML.getDescription());
                    //                                uiObject.getAttribute("img").setValueString(auxHTML.getImage());
                    //                                uiObject.getAttribute("htmlcode").setValueString(auxHTML.getHTML());
                    //                                if(iprofileboObj != null)
                    //                                {
                    //                                    uiObject.getAttribute("profile").setValueLong(iprofileboObj.getBoui());
                    //                                }
                    //                            }
                    //                        }
                    //                    }
                    //                    uiWorkPlace.update();
                }
            }
        }
        else if (type.equals(TYPE_LISTBAR))
        {
            //listBar
            boObjectList listBar = null;

            if (iprofileboObj != null)
            {
                listBar = boObjectList.list(
                        ctx,
                        "select uiListBar where profile=" + iprofileboObj.getBoui() + " and user=" +
                        performer.longValue()
                    );
            }
            else
            {
                listBar = boObjectList.list(
                        ctx,
                        "select uiListBar where upper(name) = '" + getNameFromDefault(TYPE_LISTBAR) +
                        "' and user=" + performer.longValue()
                    );
            }

            listBar.beforeFirst();

            if (listBar.next())
            {
                toRet = listBar.getObject();
            }
            else
            {
                if (iprofileboObj != null)
                {
                    listBar = boObjectList.list(
                            ctx,
                            "select uiListBar where profile=" + iprofileboObj.getBoui() +
                            " and user is null"
                        );
                }
                else
                {
                    listBar = boObjectList.list(
                            ctx,
                            "select uiListBar where upper(name) = '" + getNameFromDefault(TYPE_LISTBAR) +
                            "'  and user is null"
                        );
                }

                listBar.beforeFirst();

                if (listBar.next())
                {
                    toRet = listBar.getObject();
                }
                else
                {
                    //quando chega aquí têm que ter criado o uiListBar para o profile;
                }
            }
        }
        else
        {
            throw new boRuntimeException("uiObjectManager", MessageLocalizer.getMessage("UNEXPECTED_TYPE")+"<" + type + ".", null);
        }

        return toRet;
    }

    public static boObject getUiObject(EboContext ctx, String type, long uiObjectBoui)
        throws boRuntimeException
    {
        boObject toRet       = null;
        String uiUserProfile = "";
        Long performer       = ctx.getBoSession().getPerformerBouiLong();
        long iprofile        = ctx.getBoSession().getPerformerIProfileBoui();
        String keyProfile    = type + iprofile + uiUserProfile + "" + performer.toString();
        String keyUser       = type + iprofile + uiUserProfile;

        //iprofile
        boObject iprofileboObj = null;

        if (iprofile > 0)
        {
            iprofileboObj = boObject.getBoManager().loadObject(
                    ctx, "select uiProfile where boui=" + iprofile
                );
        }

        if (type.equals(TYPE_WORKPLACE))
        {
            //workPlace
            boObjectList workPlaceList = null;

            if (iprofileboObj != null)
            {
                workPlaceList = boObjectList.list(
                        ctx,
                        "select uiWorkPlace where boui = " + uiObjectBoui + " and profile=" +
                        iprofileboObj.getBoui() + " and user=" + performer.longValue()
                    );
            }
            else
            {
                workPlaceList = boObjectList.list(
                        ctx,
                        "select uiWorkPlace where boui = " + uiObjectBoui + " and upper(name)='" +
                        IProfileUtils.DEFAULT.toUpperCase() + "' and user=" + performer.longValue()
                    );
            }

            workPlaceList.beforeFirst();

            if (workPlaceList.next())
            {
                toRet = workPlaceList.getObject();
            }
            else
            {
                if (iprofileboObj != null)
                {
                    workPlaceList = boObjectList.list(
                            ctx,
                            "select uiWorkPlace where boui = " + uiObjectBoui + " and profile=" +
                            iprofileboObj.getBoui() + " and user is null"
                        );
                }
                else
                {
                    workPlaceList = boObjectList.list(
                            ctx,
                            "select uiWorkPlace where boui = " + uiObjectBoui + " and upper(name)='" +
                            getNameFromDefault(TYPE_WORKPLACE) + "' and user is null"
                        );
                }

                workPlaceList.beforeFirst();

                if (workPlaceList.next())
                {
                    toRet = workPlaceList.getObject();
                }
                else
                {
                    //quando chega aquí têm que ter criado o uiListBar para o profile;
                }
            }
        }
        else if (type.equals(TYPE_LISTBAR))
        {
            //listBar
            //            boObjectList listBar  = null;
            //            if(iprofileboObj != null)
            //            {
            //                listBar  = boObjectList.list( ctx,"select uiListBar where boui = " +uiObjectBoui + " and profile="+iprofileboObj.getBoui()+" and user="+performer.longValue());
            //            }
            //            else
            //            {
            //                listBar  = boObjectList.list( ctx,"select uiListBar where boui = " +uiObjectBoui + " and user="+performer.longValue());
            //            }
            //            listBar.beforeFirst();
            //            if(listBar.next())
            //            {
            //                toRet = listBar.getObject();
            //            }
            //            else
            //            {
            //                if(iprofileboObj != null)
            //                {
            //                    listBar  = boObjectList.list( ctx,"select uiListBar where boui = " +uiObjectBoui + " and profile="+iprofileboObj.getBoui()+" and user is null");
            //                }
            //                else
            //                {
            //                    listBar  = boObjectList.list( ctx,"select uiListBar where boui = " +uiObjectBoui+" and user is null");
            //                }
            //                listBar.beforeFirst();
            //                if(listBar.next())
            //                {
            //                    toRet = listBar.getObject();
            //                }
            //                else
            //                {
            //                    //quando chega aquí têm que ter criado o uiListBar para o profile;
            //                }
            //            }
            boObjectList listBar = null;
            listBar = boObjectList.list(ctx, "select uiListBar where boui = " + uiObjectBoui);
            listBar.beforeFirst();

            if (listBar.next())
            {
                toRet = listBar.getObject();
            }
            else
            {
                //não deverá acontecer
            }
        }
        else if (type.equals(TYPE_TREELB))
        {
            //tree
            boObjectList tree = null;
            tree = boObjectList.list(ctx, "select uiTreeLB where boui = " + uiObjectBoui);
            tree.beforeFirst();

            if (tree.next())
            {
                toRet = tree.getObject();
            }
            else
            {
                //não deverá acontecer
            }
        }
        else if (type.equals(TYPE_OBJECTHTML))
        {
            //htmlObj
            boObjectList htmlObj = null;
            htmlObj = boObjectList.list(ctx, "select uiObjectHTML where boui = " + uiObjectBoui);
            htmlObj.beforeFirst();

            if (htmlObj.next())
            {
                toRet = htmlObj.getObject();
            }
            else
            {
                //não deverá acontecer
            }
        }

        return toRet;
    }

    private static String getNameFromDefault(String type)
        throws boRuntimeException
    {
        return "DEFAULT";

        //       return IProfileUtils.DEFAULT.toUpperCase();
        //        if ( type.equals( TYPE_WORKPLACE ) )
        //        {
        //            return IProfileUtils.DEFAULT.toUpperCase();
        //        }
        //        else if ( type.equals( TYPE_LISTBAR ) )
        //        {
        ////            BpmDefaultWorkPlace bpWrk = boConfig.getWorkPlaces();
        ////            return bpWrk.getWorkPlace(IProfileUtils.DEFAULT).getBar().getName().toUpperCase();
        //        }
        //        else
        //        {
        //            throw new boRuntimeException("uiObjectManager", "Tipo inesperado<" + type+ ".",null); 
        //        }
    }
}
