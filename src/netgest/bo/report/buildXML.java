/*Enconding=UTF-8*/
package netgest.bo.report;
import java.net.*;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import netgest.bo.def.*;
import netgest.bo.dochtml.*;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.*;
import netgest.bo.workflow.*;
import netgest.utils.*;
import oracle.xml.parser.v2.*;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import netgest.bo.utils.DifferenceContainer;
import netgest.bo.utils.DifferenceHelper;
import netgest.bo.utils.DifferenceElement;


public final class buildXML
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.report.buildXML");
    private static final String CARD_NAO = "Não";
    private static final String CARD_SIM = "Sim";
    private static final String CARD_Y = "Y";

    public buildXML()
    {
    }

    public static void  buildCard(  docHTML doc,
                                    boObject bo,
                                    boObjectList bolist ,
                                    String viewerName ,
                                    String formName ,
                                    XMLDocument domToBuild  ,
                                    Node nodeToBuild ,
                                    ngtXMLHandler xmlForm ,
                                    String fromLabel
                                    ) throws SQLException , boRuntimeException
    {

        ngtXMLHandler[] childs=xmlForm.getChildNodes();
        Node xx=xmlForm.getNode();
        int nodeType= xx.getNodeType();
        String lastlabel="";
        for (int i = 0; i < childs.length ; i++)
        {
            String nodeName=childs[i].getNodeName();
            if ( nodeName.equalsIgnoreCase("Areas") || nodeName.equalsIgnoreCase("Panel") )
            {
               ngtXMLHandler[] blockChilds = childs[i].getChildNodes();
               for (int j = 0; j < blockChilds.length ; j++)
               {

                    ngtXMLHandler formBlock = blockChilds[ j ];
                    String blockName = formBlock.getNodeName();
                    String category = formBlock.getAttribute("bo_node");
                    boolean hasRights=true;
                    boolean toContinue = true;
                    if ( category != null )
                    {
                        hasRights = doc.hasCategoryRights( category , bo );
                    }
                    toContinue = hasRights ;

                    String formConstraint = formBlock.getAttribute("constraint");
                    if ( formConstraint != null )
                    {
                        if ( formConstraint.equalsIgnoreCase("when_object_is_extend"))
                        {
                            if ( bo != null )
                            {
                                toContinue =toContinue &&  !bo.getBridge("extendAttribute").isEmpty();
                            }
                        }
                        else if ( formConstraint.equalsIgnoreCase("when_object_is_notextend"))
                        {
                            if ( bo!= null )
                            {
                                toContinue =toContinue && bo.getBridge("extendAttribute").isEmpty();
                            }
                        }
                        else if ( formConstraint.equalsIgnoreCase("when_extend_attribute_in_template_mode"))
                        {
                             if ( bo!= null )
                            {
                                toContinue =toContinue && bo.getMode()==boObject.MODE_EDIT_TEMPLATE;
                            }
                        }
                         else if ( formConstraint.equalsIgnoreCase("when_object_in_template_mode"))
                        {
                            toContinue =toContinue && bo.getMode()==boObject.MODE_EDIT_TEMPLATE;
                        }
                        else if (formConstraint.startsWith("INTERFACE:"))
                        {
                          toContinue =toContinue && bo.getAttribute("implements_"+formConstraint.substring(10)).getValueString().equalsIgnoreCase("S");
                        }

                    }


                    if ( toContinue ) //&& formBlock.getAttribute("forTemplate")==null )
                    {



                          //when_object_is_extend
//                        when_object_is_notextend
//                        when_object_in_template_mode
//                        BOI.getBridge("extendAttribute").isEmpty()

                        String label = formBlock.getAttribute("label","");
                        if ( blockName.equals("tab") && label.length() == 0)
                        {
                            label=doc.getCategoryLabel_for_TAB_Header( category, bo );
                        }
                        else if ( label.length() > 0 && category!= null)
                        {
                            label=doc.getCategoryLabel(bo , viewerName , category );
                        }
                        String xlabel=label;
                        if ( label.equals( fromLabel ) ) xlabel ="";
                        Node xnode=buildBlock( blockName, xlabel , doc , bo ,domToBuild , nodeToBuild , formBlock );

                        String constraint = formBlock.getAttribute("constraint","");
                        if(constraint!=null)
                        {
                          if(constraint.startsWith("INTERFACE:"))
                          {
                            boDefHandler defInt = boDefHandler.getBoDefinition(constraint.substring(10));
                            formBlock = defInt.getViewer("general").getForm("edit");
                          }
                        }

                        buildCard( doc , bo , bolist , viewerName , formName , domToBuild , xnode ,  formBlock , label );

                    }
               }
            }
            else if ( nodeName.equalsIgnoreCase("section") )
            {

                    ngtXMLHandler formBlock = childs[ i ] ;
                    String blockName = formBlock.getNodeName();
                    String category = formBlock.getAttribute("bo_node");
                    boolean hasRights=true;
                     boolean toContinue = true;
                    if ( category != null )
                    {
                        hasRights = doc.hasCategoryRights( category , bo );
                    }
                    toContinue = hasRights ;

                    String formConstraint = formBlock.getAttribute("constraint");
                    if ( formConstraint != null )
                    {
                        if ( formConstraint.equalsIgnoreCase("when_object_is_extend"))
                        {
                            if ( bo != null )
                            {
                                toContinue =toContinue &&  !bo.getBridge("extendAttribute").isEmpty();
                            }
                        }
                        else if ( formConstraint.equalsIgnoreCase("when_object_is_notextend"))
                        {
                            if ( bo!= null )
                            {
                                toContinue =toContinue && bo.getBridge("extendAttribute").isEmpty();
                            }
                        }
                        else if ( formConstraint.equalsIgnoreCase("when_extend_attribute_in_template_mode"))
                        {
                             if ( bo!= null )
                            {
                                toContinue =toContinue && bo.getMode()==boObject.MODE_EDIT_TEMPLATE;
                            }
                        }
                         else if ( formConstraint.equalsIgnoreCase("when_object_in_template_mode"))
                        {
                            toContinue =toContinue && bo.getMode()==boObject.MODE_EDIT_TEMPLATE;
                        }

                    }


                    if ( toContinue ) //&& formBlock.getAttribute("forTemplate")==null )
                    {
                        String label = formBlock.getAttribute("label","");

                        if ( label.length() > 0 && category!= null)
                        {
                            label=doc.getCategoryLabel(bo , viewerName , category );
                        }

                       buildSection( label , doc , bo ,domToBuild , nodeToBuild , formBlock );

                    }
            }
            else if ( nodeName.equalsIgnoreCase("include-frame")  )
            {

                ngtXMLHandler formBlock = childs[ i ] ;
                String jspName = formBlock.getText();
                if ( jspName.equals("__extendAttribute.jsp") )
                {
                   if ( !bo.getBridge("extendAttribute").isEmpty() && bo.getMode() != boObject.MODE_EDIT_TEMPLATE  )
                   {
                       renderExtendAttributesForPrint("",doc,bo,domToBuild,nodeToBuild,formBlock );

                   }

                }


            }
            else if ( nodeName.equalsIgnoreCase("grid") && bolist !=null )
            {
                    ngtXMLHandler formBlock = childs[ i ] ;
                    String blockName = formBlock.getNodeName();
                    String category = formBlock.getAttribute("bo_node");
                    boolean hasRights=true;
                     boolean toContinue = true;
                    if ( category != null )
                    {
                        hasRights = doc.hasCategoryRights( category , bo );
                    }
                    toContinue = hasRights ;

                    String formConstraint = formBlock.getAttribute("constraint");
                    if ( formConstraint != null )
                    {
                        if ( formConstraint.equalsIgnoreCase("when_object_is_extend"))
                        {
                            if ( bo != null )
                            {
                                toContinue =toContinue &&  !bo.getBridge("extendAttribute").isEmpty();
                            }
                        }
                        else if ( formConstraint.equalsIgnoreCase("when_object_is_notextend"))
                        {
                            if ( bo!= null )
                            {
                                toContinue =toContinue && bo.getBridge("extendAttribute").isEmpty();
                            }
                        }
                        else if ( formConstraint.equalsIgnoreCase("when_extend_attribute_in_template_mode"))
                        {
                             if ( bo!= null )
                            {
                                toContinue =toContinue && bo.getMode()==boObject.MODE_EDIT_TEMPLATE;
                            }
                        }
                         else if ( formConstraint.equalsIgnoreCase("when_object_in_template_mode"))
                        {
                            toContinue =toContinue && bo.getMode()==boObject.MODE_EDIT_TEMPLATE;
                        }

                    }


                    if ( toContinue ) //&& formBlock.getAttribute("forTemplate")==null )
                    {
                        String label = formBlock.getAttribute("label","");

                        if ( label.length() > 0 && category!= null)
                        {
                            label=doc.getCategoryLabel(bo , viewerName , category );
                        }

                       buildGrid( label , doc , bolist ,domToBuild , nodeToBuild , formBlock );

                    }

            }
            else if ( nodeName.equalsIgnoreCase("div") )
            {
                String xdiv=childs[i].getText();
                String objectDef = childs[i].getAttribute("object");
                String viewerDef = childs[i].getAttribute("viewer");
                if (xdiv!=null && xdiv.indexOf('.') >-1)
                {
                    String[] xd=xdiv.split("\\.");
                    String xd1=xd[0];
                    String childFormName=xd[1];
                    AttributeHandler attr= bo.getAttribute(xd[0]);
                    if ( attr!= null && attr.hasRights() )
                    {
                        boDefAttribute attrdef=attr.getDefAttribute();
                        if ( attrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                        {
                            if ( attrdef.getRelationType() == boDefAttribute.RELATION_1_TO_1 )
                            {
                                boObject o = attr.getObject();
                                if ( o != null )
                                {

                                   buildCard( doc , o , null , viewerName , childFormName , domToBuild , nodeToBuild , o.getBoDefinition().getViewer(viewerName).getForm( childFormName ),"");
                                }
                            }
                            else
                            {
                                bridgeHandler bridge= bo.getBridge(  xd[0] );
                                String fname= attr.getName()+"_"+childFormName;
                                if ( bo.getBoDefinition().hasForm( viewerName , fname ) && !fname.equals("documents_list") )
                                {
                                   buildCard( doc , bo , bridge , viewerName , childFormName , domToBuild , nodeToBuild , bo.getBoDefinition().getViewer(viewerName).getForm( fname ),"");
                                }
                                else
                                {
                                    boObject o=bridge.getObject();
                                    if ( o!= null )
                                    {
                                    buildCard( doc , bo , bridge , viewerName , childFormName , domToBuild , nodeToBuild , o.getBoDefinition().getViewer(viewerName).getForm( childFormName ),"");
                                    }
                                    else
                                    {
                                     if ( bridge.getBoDef().hasForm(viewerName,childFormName ) )
                                     {
                                        buildCard( doc , bo , bridge , viewerName , childFormName , domToBuild , nodeToBuild , bridge.getBoDef().getViewer(viewerName).getForm( childFormName ),"");
                                     }
                                    }

                                }

                            }

                        }

                    }

                }
                else if(viewerDef!=null && objectDef!=null)
                {
                  boDefHandler obj_def = boDefHandler.getBoDefinition(objectDef);
                  if(obj_def!=null)
                  {
                    buildCard( doc , bo , null , viewerName , viewerDef , domToBuild , nodeToBuild , obj_def.getViewer(viewerName).getForm(viewerDef),"");

                  }
                }
            }
            else
            {
                buildCard( doc , bo , bolist , viewerName , formName , domToBuild , nodeToBuild ,  childs[ i ] ,"");
            }

        }
        String xxx=ngtXMLUtils.getXML( domToBuild );
        int dd=1;

    }

    public static Node buildBlock( String blockName , String label , docHTML doc, boObject o , XMLDocument dom, Node node ,  ngtXMLHandler xmlForm )
    {
         Element area=dom.createElement("div");
         area.setAttribute("class",blockName+"_title");

         Node node1=node.appendChild(area);
         Element tit=dom.createElement("p");

         tit.appendChild( dom.createTextNode( ""+label ));
         node1.appendChild( tit );

         return node;

    }

    private static Text getValueAttribute(  XMLDocument dom , AttributeHandler attr ) throws SQLException , boRuntimeException
    {
        String value=null;
        Text toRet=null;
        boolean iscdata=false;

         if ( attr!=null )
         {

             if ( attr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
             {
                 if ( attr.getObject() != null )
                 {
                     value=attr.getObject().getCARDID(false).toString();
                     iscdata=true;
                 }

             }
             else
             {
                value= getValue(attr);
                if ( attr.getDefAttribute().getValueType() == boDefAttribute.VALUE_CLOB )
                {
                    iscdata=true;
                }
             }


             if ( value== null || value.length() == 0)
             {
                 value="NBSP";
             }
             if ( attr.getParent().getName().equals("Ebo_Map") )
             {
                 if ( attr.getName().equals("objectAttributeName") || attr.getName().equals("value") )
                 {
                    boObject o2= attr.getParent();
                    String objectName=o2.getParentBridgeRow().getParent().getAttribute("masterObjectClass").getObject().getAttribute("name").getValueString();

                    boDefHandler defobject = boDefHandler.getBoDefinition( objectName );
                    boDefAttribute atrdef=null;
                    String nameAttribute = attr.getParent().getAttribute("objectAttributeName").getValueString();
                    if ( defobject != null )
                    {
                        if  ( value.indexOf(".") >-1 && attr.getName().equals("objectAttributeName") )
                        {
                            value = value.split("\\.")[1];
                        }
                        if ( nameAttribute.indexOf(".") > -1 )
                        {
                            nameAttribute = nameAttribute.split("\\.")[1];
                        }
                        atrdef= defobject.getAttributeRef( nameAttribute );
                    }

                    if ( atrdef != null )
                    {
                       if ( attr.getName().equals("objectAttributeName") )
                       {
                        value=atrdef.getLabel();
                       }
                       else
                       {
                           if ( atrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                           {
                                if  ( value.indexOf(".") >-1 )
                                {
                                    String[] values = value.split("\\.");
                                    value="";
                                    for (int i = 0; i < values.length ; i++)
                                    {

                                        try
                                        {
                                            boObject o3 = o2.getObject( ClassUtils.convertToLong(values[i]) );
                                            value += o3.getCARDID(false).toString();
                                            iscdata=true;
                                        }
                                        catch (Exception e)
                                        {
                                            logger.warn(" Objecto com boui " + values[i] +"nao encontrado - Referencia no Template");
                                        }
                                    }


                                }
                                else
                                {

                                    try
                                    {
                                        boObject o3 = o2.getObject( ClassUtils.convertToLong(value) );
                                        value = o3.getCARDID(false).toString();
                                        iscdata=true;
                                    }
                                    catch (Exception e)
                                    {
                                        logger.warn(" Objecto com boui " + value +"nao encontrado - Referencia no Template");
                                    }
                                }
                           }
                       }
                    }



                 }

             }

         }
         else
         {
             value="NBSP";
         }
         if ( iscdata )
         {
             toRet = dom.createCDATASection( value );

         }
         else
         {
             toRet = dom.createTextNode( value );
         }
         return toRet;

    }

    private static void renderExtendAttributesForPrint( String label , docHTML doc, boObject o , XMLDocument dom, Node node ,  ngtXMLHandler xmlForm ) throws SQLException , boRuntimeException
    {
        bridgeHandler bridge = o.getBridge("extendAttribute");
        boBridgeIterator it = bridge.iterator();



        Element table=dom.createElement("table");
        node.appendChild(table);
        table.setAttribute("align","top");
        table.setAttribute("class","section" );
        table.setAttribute("cellSpacing","0" );
        table.setAttribute("cellPadding","3" );
        table.setAttribute("width","100%" );

        Element colgroup=dom.createElement("COLGROUP");
        table.appendChild( colgroup );
        Element col = dom.createElement("COL");
        col.setAttribute("width","120");
        table.appendChild( col );
        col = dom.createElement("COL");
        table.appendChild(col);
        col = dom.createElement("COL");
        col.setAttribute("style","PADDING-LEFT:5px");
        col.setAttribute("width","70px");
        table.appendChild( col );
        col = dom.createElement("COL");
        table.appendChild(col);

        Element tbody = dom.createElement("TBODY");
        table.appendChild( tbody );

        while ( it.next() )
        {
                boObject extAttr = ( boObject) it.currentRow().getObject();

                long attributeType = extAttr.getAttribute("attributeType").getValueLong();
                long cadinalidade =  extAttr.getAttribute("attributeCardinal").getValueLong();


                    if(cadinalidade == 1)
                    {
                        Element TR = dom.createElement("TR");
                        tbody.appendChild( TR );

                        Element TD1= dom.createElement("td");
                        TD1.appendChild(dom.createTextNode( extAttr.getAttribute("alias").getValueString() ));
                        TD1.setAttribute("class","label");
                        TD1.setAttribute("width","10%");
                        Element TD2= dom.createElement("td");

                        TD2.appendChild( buildXML.getValueExtendedAttribute( dom , extAttr ,doc ) );

                        TD2.setAttribute("width","90%" );
                        TD2.setAttribute("colspan","3");
                        TD2.setAttribute("class","input");
                        TR.appendChild( TD1 );
                        TR.appendChild( TD2 );

                    }
                    else
                    {
                        Element TR = dom.createElement("TR");
                        tbody.appendChild( TR );
                        Element TD1= dom.createElement("td");
                        TD1.setAttribute("colspan","4");
                        TD1.appendChild( buildXML.buildExtendedAttributeList( dom , extAttr ,doc ) );
                        TR.appendChild( TD1 );
//                        out.print("<tr><td colspan=4 height=190px >");

//                        String iframe = "<div class=extendList><IFRAME id='inc_" + extAttr.getName() + "__" +extAttr.getBoui() + "__valueList' src='__extendAttributeList.jsp?docid="+IDX+"&method=list&parent_attribute=valueList&parent_boui="+extAttr.getBoui()+"' frameBorder='0' width='100%'  scrolling=no height='180px'  ></IFRAME></div>";
//                        out.print(iframe);
//                        out.print("\n");
//                        out.print("</td></tr>");
                    }

            }

//           out.print("</tbdoy></table>");

    }

     private static Element buildExtendedAttributeList( XMLDocument dom ,  boObject extAttr , docHTML doc ) throws SQLException , boRuntimeException
    {

         //header
         Element toRet = dom.createElement("div");
         toRet.setAttribute("class","extendList" );


         boolean renderCols[] = new boolean[ 1 ] ;
         String renderColsNames[] = new String[ 1 ] ;
         renderCols[0] = true;
         renderColsNames[0] = "cardid";
//         for (int i = 0; i < cols.length ; i++)
//         {
//             String AttributeName = cols[i].getFirstChild().getText();
//             renderCols[i] = true;
//             if ( bolist.getName().equals("DAO") && AttributeName.equals("name") )
//             {
//                 renderCols[i]=false;
//             }
//         }

         Element table = dom.createElement("table");
         toRet.appendChild( table );
         table.setAttribute("class","grid");


         Element tableH= dom.createElement("thead" );
         table.appendChild( tableH );

         Element tableB= dom.createElement("tbody" );
         table.appendChild( tableB );


         Element TR = dom.createElement("TR");
         tableH.appendChild( TR );

         Element TH1 = dom.createElement("TH");
         TH1.setAttribute("align","left");
         TR.appendChild( TH1 );
//         TH1.appendChild(dom.createTextNode("NBSP"));
         String title= extAttr.getAttribute("alias").getValueString();
         TH1.appendChild(dom.createTextNode(title));
         TH1.setAttribute("class","gridCHeader");



//         for (int i = 0; i < renderCols.length ; i++)
//         {
//             if ( renderCols[i] )
//             {
//                 Element TH = dom.createElement("TH");
//                 TR.appendChild( TH );
//                 String AttributeName = renderColsNames[0];
//
//                 String title="NBSP";
//
//                 title= extAttr.getAttribute("alias").getValueString();
//
//                 TH.setAttribute("class","gridCHeader");
//                 TH.appendChild( dom.createTextNode( title )  );
//             }
//
//         }


        bridgeHandler bolist = extAttr.getBridge("valueList");

         bolist.beforeFirst();
         while ( bolist.next() )
         {


                 Element TRB = dom.createElement("TR");
                 boObject o=bolist.getObject();
                 tableB.appendChild( TRB );

                 Element TD1 = dom.createElement("TD");
                // if ( !bolist.haveVL() )
                 TD1.appendChild( dom.createCDATASection( o.getCARDIDwState().toString() ) );
                // else
                // TD1.appendChild( dom.createTextNode( "NBSP" ) );
                 TRB.appendChild( TD1 );
                 TD1.setAttribute("class","gridCBody");
//                 for (int i = 0; i < cols.length ; i++)
//                 {
//                     if ( renderCols[i])
//                     {
//                         boolean iscdata=false;
//                         Element TD = dom.createElement("TD");
//                         TRB.appendChild( TD );
//                         String AttributeName = cols[i].getFirstChild().getText();
//                         AttributeHandler attr= o.getAttribute( AttributeName );
//                         Text value=null;
//
//                         if ( attr != null )
//                         {
//
//                             value=buildXML.getValueAttribute( dom , attr );
//
//                         }
//                         else
//                         {
//                             attr=((bridgeHandler) bolist).getAttribute( AttributeName );
//                             value=buildXML.getValueAttribute( dom , attr );
//
//                         }
//                         TD.setAttribute("class","gridCBody");
//                         TD.appendChild( value );
//
//
//                     }
//
//                }


         }

           return toRet;
    }

    private static Text getValueExtendedAttribute(  XMLDocument dom , boObject extAttr , docHTML doc ) throws SQLException , boRuntimeException
    {

        String value=null;
        Text toRet=null;
        boolean iscdata=false;

         if ( extAttr!=null )
         {

             long attributeType = extAttr.getAttribute("attributeType").getValueLong();
             String attrName = DocWfHTML.getExtendAttributeName(extAttr);
             AttributeHandler attrHandler = extAttr.getAttribute(attrName);
             String valueObject = "";
             if(extAttr.getAttribute(attrName).getValueString() != null)
             {
                valueObject = extAttr.getAttribute(attrName).getValueString();
             }
             if ( attributeType == 0 )
             {
                 if ( valueObject.length() > 0)
                 {
                     boObject o=doc.getObject( ClassUtils.convertToLong( valueObject ) );
                     if ( o != null )  value = o.getCARDID(false).toString();
                     iscdata=true;
                 }

             }
             else
             {
                value= valueObject; //  attr.getValueString();
                if ( attributeType == 1 ) // boolean
                {
                    int i0=1;
                }
                else if ( attributeType == 4 ) {}//number
                else if ( attributeType == 5 ) {}//datetime
                else if ( attributeType == 6 ) {}//date
                else if ( attributeType == 9 ) //text
                {
                    iscdata=true;
                }
                else if ( attributeType == 12 )
                {
                    String[] values = null;
                    long lovBoui = extAttr.getAttribute("lov").getValueLong();
                    boObject lov = null;
                    if( lovBoui != 0 )
                    {
                        lov = boObject.getBoManager().loadObject(doc.getEboContext(),lovBoui);
                    }

                    bridgeHandler lovHandler = null;
                    if(lov != null)
                    {
                        lovHandler = lov.getBridge("details");
                        lovHandler.beforeFirst();
                        while ( lovHandler.next() )
                        {
                           if ( lovHandler.getObject().getAttribute("value").getValueString().equals( value ) )
                           {
                               value = lovHandler.getObject().getAttribute("value").getValueString();
                           }
                        }
                    }
                }


             }


             if ( value== null || value.length() == 0)
             {
                 value="NBSP";
             }


         }
         else
         {
             value="NBSP";
         }
         if ( iscdata )
         {
             toRet = dom.createCDATASection( value );

         }
         else
         {
             toRet = dom.createTextNode( value );
         }
         return toRet;

    }

    private static void buildSection( String label , docHTML doc, boObject o , XMLDocument dom, Node node ,  ngtXMLHandler xmlForm ) throws SQLException , boRuntimeException
    {


         ngtXMLHandler[] rows = xmlForm.getChildNode("rows").getChildNodes() ;
         boolean renderSection =false;

         boolean renderRows[] = new boolean[ rows.length ] ;
         /*verificar seguranças */
         for (int i = 0; i < rows.length ; i++)
         {
            ngtXMLHandler[] cells=rows[i].getChildNodes();
            String attr1Name =  null;
            String attr2Name =  null;

            attr1Name = cells[0].getFirstChild() != null ? cells[0].getFirstChild().getText():null;

            AttributeHandler attr1=null;
            AttributeHandler attr2=null;

            if( attr1Name != null ) attr1=o.getAttribute( attr1Name );

            if ( cells.length ==2 )
            {
                attr2Name = cells[1].getFirstChild() != null ? cells[1].getFirstChild().getText():null;
                if ( attr2Name != null ) attr2=o.getAttribute( attr2Name );
            }

            renderRows[ i ] = false;
            if ( attr1 != null && attr1.hasRights() )
            {
                renderSection=true;
                renderRows[ i ] =true;
            }
            if ( attr2!= null && attr2.hasRights() )
            {
                renderSection=true;
                renderRows[ i ] =true;
            }


         }
         /*fim de verifcar segurity */

         if ( renderSection )
         {
               if ( !xmlForm.getAttribute("showlabel","no" ).equalsIgnoreCase("no") )
                {
                    Element area=dom.createElement("div");
                    area.setAttribute("class","section_title");

                    Node node1=node.appendChild(area);
                    Element tit=dom.createElement("p");

                    tit.appendChild( dom.createTextNode( ""+label ));
                    node1.appendChild( tit );
                }
                Element table = dom.createElement("table");
                table.setAttribute("class","section");

                node.appendChild( table );
                Element TRx = dom.createElement("tr");
                table.appendChild(TRx);
                Element TDx = dom.createElement("td");
                TDx.setAttribute("width","10%");
                TRx.appendChild( TDx );
                TDx = dom.createElement("td");
                TDx.setAttribute("width","40%");
                TRx.appendChild( TDx );
                TDx = dom.createElement("td");
                TDx.setAttribute("width","10%");
                TRx.appendChild( TDx );

                TDx = dom.createElement("td");
                TDx.setAttribute("width","40%");
                TRx.appendChild( TDx );

                for (int i = 0; i < rows.length  ; i++)
                 {

                    if ( renderRows[ i ] )
                    {
                        Element TR = dom.createElement("tr");
                        table.appendChild( TR );

                        ngtXMLHandler[] cells=rows[i].getChildNodes();
                        String attr1Name =  null;
                        String attr2Name =  null;

                        attr1Name = cells[0].getFirstChild() != null ? cells[0].getFirstChild().getText():null;

                        AttributeHandler attr1=null;
                        AttributeHandler attr2=null;

                        if ( attr1Name != null ) attr1=o.getAttribute( attr1Name );

                        if ( cells.length ==2 )
                        {
                            attr2Name = cells[1].getFirstChild() != null ? cells[1].getFirstChild().getText():null;
                            if ( attr2Name != null ) attr2=o.getAttribute( attr2Name );
                        }

                        if ( attr1!= null && !attr1.canAccess()) attr1=null;
                        if ( attr2!= null && !attr2.canAccess()) attr2=null;

                        if ( cells.length == 1)
                        {
                            if ( attr1!= null )
                            {
                                String xx=cells[0].getFirstChild().getNodeName();
                                boolean showLabel = cells[0].getFirstChild().getAttribute("showlabel","no").equalsIgnoreCase("yes");

                                if ( showLabel )
                                {
                                    Element TD1= dom.createElement("td");
                                    TD1.appendChild(dom.createTextNode( attr1.getDefAttribute().getLabel() ) );
                                    TD1.setAttribute("class","label");
                                    TD1.setAttribute("width","10%");
                                    Element TD2= dom.createElement("td");
                                    TD2.appendChild( buildXML.getValueAttribute( dom , attr1 ) );
                                    TD2.setAttribute("width","90%" );
                                    TD2.setAttribute("colspan","3");
                                    TD2.setAttribute("class","input");
                                    TR.appendChild( TD1 );
                                    TR.appendChild( TD2 );
                                }
                                else
                                {
                                    Element TD2= dom.createElement("td");
                                    TD2.appendChild( buildXML.getValueAttribute( dom , attr1 ) );
                                    TD2.setAttribute("width","100%" );
                                    TD2.setAttribute("colspan","4");
                                    TD2.setAttribute("class","input");
                                    TR.appendChild( TD2 );
                                }
                            }
                            else
                            {
                                Element TD = dom.createElement("td");
                                TD.setAttribute("class","label");
                                TD.setAttribute("width","100%");
                                TD.setAttribute("colspan","4");
                                TR.appendChild(TD);

                            }

                        }
                        else // cells.length=2
                        {
                            if ( attr1!= null )
                            {
                                boolean showLabel = cells[0].getFirstChild().getAttribute("showlabel","no").equalsIgnoreCase("yes");
                                if ( showLabel )
                                {
                                    Element TD1= dom.createElement("td");
                                    TD1.appendChild(dom.createTextNode( attr1.getDefAttribute().getLabel() ) );
                                    TD1.setAttribute("class","label");
                                    TD1.setAttribute("width","10%");
                                    Element TD2= dom.createElement("td");
                                    TD2.appendChild( buildXML.getValueAttribute( dom , attr1 ) );


                                    TD2.setAttribute("width","40%" );
                                    TD2.setAttribute("class","input");
                                    TR.appendChild( TD1 );
                                    TR.appendChild( TD2 );
                                }
                                else
                                {
                                    Element TD2= dom.createElement("td");
                                    TD2.appendChild( buildXML.getValueAttribute( dom , attr1 ) );

                                    TD2.setAttribute("width","50%" );
                                    TD2.setAttribute("colspan","2");
                                    TD2.setAttribute("class","input");
                                    TR.appendChild( TD2 );
                                }
                            }
                            else
                            {
                                Element TD = dom.createElement("td");
                                TD.setAttribute("class","label");
                                TD.setAttribute("width","50%");
                                TD.setAttribute("colspan","2");
                                TR.appendChild(TD);
                            }

                            if ( attr2!= null )
                            {
                                boolean showLabel = cells[1].getFirstChild().getAttribute("showlabel","no").equalsIgnoreCase("yes");
                                if ( showLabel )
                                {
                                    Element TD1= dom.createElement("td");
                                    TD1.appendChild(dom.createTextNode( attr2.getDefAttribute().getLabel() ) );
                                    TD1.setAttribute("class","label");
                                    TD1.setAttribute("width","10%");
                                    Element TD2= dom.createElement("td");
                                    TD2.appendChild( buildXML.getValueAttribute( dom , attr2 ) );
                                    TD2.setAttribute("width","40%" );
                                    TD2.setAttribute("class","input");
                                    TR.appendChild( TD1 );
                                    TR.appendChild( TD2 );
                                }
                                else
                                {
                                    Element TD2= dom.createElement("td");
                                    TD2.appendChild( buildXML.getValueAttribute( dom , attr2 ) );

                                    TD2.setAttribute("width","50%" );
                                    TD2.setAttribute("colspan","2");
                                    TD2.setAttribute("class","input");
                                    TR.appendChild( TD2 );
                                }
                            }
                            else
                            {
                                Element TD = dom.createElement("td");
                                TD.setAttribute("class","label");
                                TD.setAttribute("width","50%");
                                TD.setAttribute("colspan","2");
                                TR.appendChild(TD);
                            }

                        }
                    }
                 }
         }





    }




     private static void buildGrid( String label , docHTML doc, boObjectList bolist , XMLDocument dom, Node node ,  ngtXMLHandler xmlForm ) throws SQLException , boRuntimeException
    {


         ngtXMLHandler[] cols = xmlForm.getChildNode("cols").getChildNodes() ;

         boolean renderCols[] = new boolean[ cols.length ] ;
         for (int i = 0; i < cols.length ; i++)
         {
             String AttributeName = cols[i].getFirstChild().getText();
             renderCols[i] = true;
             if ( bolist.getName().equals("DAO") && AttributeName.equals("name") )
             {
                 renderCols[i]=false;
             }
         }

         //header
         Element table = dom.createElement("table");
         table.setAttribute("class","grid");
         node.appendChild( table );

         Element tableH= dom.createElement("thead" );
         table.appendChild( tableH );

         Element tableB= dom.createElement("tbody" );
         table.appendChild( tableB );

         Element TR = dom.createElement("TR");
         tableH.appendChild( TR );
         bolist.beforeFirst();
         bolist.next();
         boObject o = bolist.getObject();
         Element TH1 = dom.createElement("TH");
         TR.appendChild( TH1 );
         TH1.appendChild(dom.createTextNode("NBSP"));
         TH1.setAttribute("class","gridCHeader");
         boDefHandler def = bolist.getBoDef();

         for (int i = 0; i < cols.length ; i++)
         {
             if ( renderCols[i] )
             {
                 Element TH = dom.createElement("TH");
                 TR.appendChild( TH );
                 String AttributeName = cols[i].getFirstChild().getText();

                boDefAttribute attrdef= def.getAttributeRef( AttributeName );

                 String title="NBSP";
                 if ( attrdef != null )
                 {
                     title= attrdef.getLabel();
                 }
                 else
                 {
                     if( !bolist.isEmpty() )
                     {
                     AttributeHandler attr=((bridgeHandler) bolist).getAttribute( AttributeName );
                     if ( attr != null)
                     {
                         title=attr.getDefAttribute().getLabel();
                     }
                     }
                 }
                 TH.setAttribute("class","gridCHeader");
                 TH.appendChild( dom.createTextNode( title )  );
             }

         }

         if ( bolist.first() )
         {
             bolist.beforeFirst();
             while ( bolist.next() )
             {


                     Element TRB = dom.createElement("TR");
                     o=bolist.getObject();
                     tableB.appendChild( TRB );

                     Element TD1 = dom.createElement("TD");
                    // if ( !bolist.haveVL() )
                     TD1.appendChild( dom.createCDATASection( o.getCARDIDwState().toString() ) );
                    // else
                    // TD1.appendChild( dom.createTextNode( "NBSP" ) );
                     TRB.appendChild( TD1 );
                     TD1.setAttribute("class","gridCBody");
                     for (int i = 0; i < cols.length ; i++)
                     {
                         if ( renderCols[i])
                         {
                             boolean iscdata=false;
                             Element TD = dom.createElement("TD");
                             TRB.appendChild( TD );
                             String AttributeName = cols[i].getFirstChild().getText();
                             AttributeHandler attr= o.getAttribute( AttributeName );
                             Text value=null;

                             if ( attr != null )
                             {

                                 value=buildXML.getValueAttribute( dom , attr );

                             }
                             else
                             {
                                 attr=((bridgeHandler) bolist).getAttribute( AttributeName );
                                 value=buildXML.getValueAttribute( dom , attr );

                             }
                             TD.setAttribute("class","gridCBody");
                             TD.appendChild( value );


                         }

                    }


             }
         }
         else
         {
                     Element TRB = dom.createElement("TR");

                     tableB.appendChild( TRB );

                     Element TD1 = dom.createElement("TD");
                     TD1.setAttribute("colspan",""+cols.length+1);
                     TD1.appendChild( dom.createCDATASection( "Sem registos" ) );

                     TRB.appendChild( TD1 );
                     TD1.setAttribute("class","gridCBody");
         }


    }


    // show difference

    public static void  buildCard(  DifferenceContainer dc,
                                    docHTML doc,
                                    boObject srcBo,
                                    boObject dstBo,
                                    boObjectList bolist ,
                                    boObjectList bolistDst ,
                                    String viewerName ,
                                    String formName ,
                                    XMLDocument domToBuild  ,
                                    Node nodeToBuild ,
                                    ngtXMLHandler xmlForm
                                    ) throws SQLException , boRuntimeException
    {

        ngtXMLHandler[] childs=xmlForm.getChildNodes();
        Node xx=xmlForm.getNode();
        int nodeType= xx.getNodeType();
        for (int i = 0; i < childs.length ; i++)
        {
            String nodeName=childs[i].getNodeName();
            if ( nodeName.equalsIgnoreCase("Areas") || nodeName.equalsIgnoreCase("Panel") )
            {
               ngtXMLHandler[] blockChilds = childs[i].getChildNodes();
               for (int j = 0; j < blockChilds.length ; j++)
               {

                    ngtXMLHandler formBlock = blockChilds[ j ];
                    String blockName = formBlock.getNodeName();
                    String category = formBlock.getAttribute("bo_node");
                    boolean hasRights=true;
                    /*if ( category != null )
                    {
                        hasRights = doc.hasCategoryRights( category , srcBo );
                    }*/

                    if ( hasRights && formBlock.getAttribute("forTemplate")==null )
                    {
                        String label = formBlock.getAttribute("label","");
                        if ( blockName.equals("tab") && label.length() == 0)
                        {
                            label=doc.getCategoryLabel_for_TAB_Header( category, srcBo );
                        }
                        else if ( label.length() > 0 && category!= null)
                        {
                            label=doc.getCategoryLabel(srcBo , viewerName , category );
                        }

                        Node xnode=buildBlock( blockName, label , doc , srcBo ,domToBuild , nodeToBuild , formBlock );

                        String constraint = formBlock.getAttribute("constraint","");
                        if(constraint!=null)
                        {
                          if(constraint.startsWith("INTERFACE:"))
                          {
                            boDefHandler defInt = boDefHandler.getBoDefinition(constraint.substring(10));
                            formBlock = defInt.getViewer("general").getForm("edit");
                          }
                        }

                        buildCard( dc,doc , srcBo , dstBo,bolist , bolistDst, viewerName , formName , domToBuild , xnode ,  formBlock );
                    }
               }
            }
            else if ( nodeName.equalsIgnoreCase("section") )
            {

                    ngtXMLHandler formBlock = childs[ i ] ;
                    String blockName = formBlock.getNodeName();
                    String category = formBlock.getAttribute("bo_node");
                    boolean hasRights=true;
                    // TODO pq?
 /*                   if ( category != null )
                    {
                        hasRights = doc.hasCategoryRights( category , srcBo );
                    }
   */
                    if ( hasRights )
                    {
                        String label = formBlock.getAttribute("label","");

                        if ( label.length() > 0 && category!= null)
                        {
                            label=doc.getCategoryLabel(srcBo , viewerName , category );
                        }

                       buildSection( dc,label , doc , srcBo, dstBo ,domToBuild , nodeToBuild , formBlock );


                    }
            }
            else if ( nodeName.equalsIgnoreCase("grid") && bolist !=null )
            {
                    ngtXMLHandler formBlock = childs[ i ] ;
                    String blockName = formBlock.getNodeName();
                    String category = formBlock.getAttribute("bo_node");
                    boolean hasRights=true;
                   /* if ( category != null )
                    {
                        hasRights = doc.hasCategoryRights( category , srcBo );
                    }*/

                    if ( hasRights )
                    {
                        String label = formBlock.getAttribute("label","");

                        if ( label.length() > 0 && category!= null)
                        {
                            label=doc.getCategoryLabel(srcBo , viewerName , category );
                        }

                       buildGrid(  dc, label , doc , bolist , bolistDst, domToBuild , nodeToBuild , formBlock );

                    }

            }
            else if ( nodeName.equalsIgnoreCase("div") )
            {
                String xdiv=childs[i].getText();
                String objectDef = childs[i].getAttribute("object");
                String viewerDef = childs[i].getAttribute("viewer");
                if ( xdiv!=null && xdiv.indexOf('.') >-1)
                {
                    String[] xd=xdiv.split("\\.");
                    String xd1=xd[0];
                    String childFormName=xd[1];
                    AttributeHandler attr = srcBo.getAttribute(xd[0]);
                    AttributeHandler attrDst = null;
                    if(dstBo != null)
                    {
                        attrDst = dstBo.getAttribute(xd[0]); //novo
                    }
                    if ( attr!= null &&  attr.hasRights() )
                    {
                        boDefAttribute attrdef=attr.getDefAttribute();
                        if ( attrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                        {
                            if ( attrdef.getRelationType() == boDefAttribute.RELATION_1_TO_1 )
                            {
                                boObject o = attr.getObject();
                                boObject oDst = null;
                                if(attrDst != null){
                                    oDst = attrDst.getObject();
                                }
                                if ( o != null )
                                {
                                   buildCard( dc,doc , o ,oDst, null , null, viewerName , childFormName , domToBuild , nodeToBuild , o.getBoDefinition().getViewer(viewerName).getForm( childFormName ));
                                }
                            }
                            else
                            {
                                bridgeHandler bridge= srcBo.getBridge(  xd[0] );
                                bridgeHandler bridgeDst = null;
                                if(dstBo != null)
                                {
                                 bridgeDst = dstBo.getBridge(  xd[0] );
                                }
                                String fname= attr.getName()+"_"+childFormName;
                                if ( srcBo.getBoDefinition().hasForm( viewerName , fname ) )
                                {
                                   buildCard( dc,doc , srcBo ,dstBo, bridge , bridgeDst, viewerName , childFormName , domToBuild , nodeToBuild , srcBo.getBoDefinition().getViewer(viewerName).getForm( fname ));
                                }
                                else
                                {
                                    boObject o=bridge.getObject();
                                    if(o!= null)
                                    {
                                        buildCard(dc, doc , srcBo ,dstBo, bridge , bridgeDst, viewerName , childFormName , domToBuild , nodeToBuild , o.getBoDefinition().getViewer(viewerName).getForm( childFormName ));
                                    }
                                }



                            }

                        }

                    }

                }
                else if(viewerDef!=null && objectDef!=null)
                {
                  boDefHandler obj_def = boDefHandler.getBoDefinition(objectDef);
                  if(obj_def!=null)
                  {
                    buildCard( dc, doc , srcBo, dstBo ,null, null , viewerName , viewerDef , domToBuild , nodeToBuild , obj_def.getViewer(viewerName).getForm(viewerDef));

                  }
                }

            }
            else
            {
                buildCard(dc, doc , srcBo ,dstBo, bolist , bolistDst,  viewerName , formName , domToBuild , nodeToBuild ,  childs[ i ] );
            }

        }
        String xxx=ngtXMLUtils.getXML( domToBuild );
        int dd=1;

    }

     private static void buildGrid( DifferenceContainer diffContainer ,String label , docHTML doc, boObjectList bolist ,boObjectList bolistDst , XMLDocument dom, Node node ,  ngtXMLHandler xmlForm ) throws SQLException , boRuntimeException
    {

         ngtXMLHandler[] cols = xmlForm.getChildNode("cols").getChildNodes() ;

         boolean renderCols[] = new boolean[ cols.length ] ;
         for (int i = 0; i < cols.length ; i++)
         {
             String AttributeName = cols[i].getFirstChild().getText();
             renderCols[i] = true;
             if ( bolist.getName().equals("DAO") && AttributeName.equals("name") )
             {
                 renderCols[i]=false;
             }
         }

         //header
         Element table = dom.createElement("table");
         table.setAttribute("class","grid");
         node.appendChild( table );

         Element tableH= dom.createElement("thead" );
         table.appendChild( tableH );

         Element tableB= dom.createElement("tbody" );
         table.appendChild( tableB );

         Element TR = dom.createElement("TR");
         tableH.appendChild( TR );
         bolist.beforeFirst();
         bolist.next();
         boObject o = bolist.getObject();
         Element TH1 = dom.createElement("TH");
         TR.appendChild( TH1 );
         TH1.appendChild(dom.createTextNode("NBSP"));
         TH1.setAttribute("class","gridCHeader");

         for (int i = 0; i < cols.length ; i++)
         {
             if ( renderCols[i] )
             {
                 Element TH = dom.createElement("TH");
                 TR.appendChild( TH );
                 String AttributeName = cols[i].getFirstChild().getText();
                 boDefAttribute attrdef = bolist.getBoDef().getAttributeRef(  AttributeName);
                 //AttributeHandler attr= o.getAttribute( AttributeName );
                 String title="NBSP";
                 if ( attrdef != null )
                 {
                     title= attrdef.getLabel();
                 }
                 else
                 {
                     if ( !bolist.isEmpty() )
                     {

                         AttributeHandler attr=((bridgeHandler) bolist).getAttribute( AttributeName );
                         if ( attr != null)
                         {
                             title=attr.getDefAttribute().getLabel();
                         }
                     }

                 }
                 TH.setAttribute("class","gridCHeader");
                 TH.appendChild( dom.createTextNode( title )  );
             }

         }
         DifferenceElement diffElem = null;
         bridgeHandler bridgeDst = null;
//         if(dstBo != null)
//         {
//           bridgeDst = dstBo.getBridge( bolist.getName() );
//         }
//         if(bridgeDst == null)
//         {
//             bridgeDst = (bridgeHandler)bolistDst;
//         }
         bridgeDst = (bridgeHandler)bolistDst;
         if(bridgeDst != null) bridgeDst.beforeFirst();

         bolist.beforeFirst();
         while ( bolist.next() )
         {
                 long bouiNew = 0;
                 o=bolist.getObject();
                 boolean exists = false;
                 if(bridgeDst != null) exists = bridgeDst.haveBoui(o.getBoui());
                 Element TRB = dom.createElement("TR");
                 //if(!bolist.haveVL())
                 tableB.appendChild( TRB );

                 Element TD1 = dom.createElement("TD");
//                 if ( !bolist.haveVL() )
                     TD1.appendChild( dom.createCDATASection( o.getCARDIDwState().toString() ) );
               //  else
               //     TD1.appendChild( dom.createTextNode( "NBSP" ) );
                 TRB.appendChild( TD1 );


//                 TD1.setAttribute("class", (exists || bolist.haveVL()) ? "gridCBody" : "gridCBodySrc");
                 TD1.setAttribute("class", (exists) ? "gridCBody" : "gridCBodySrc");


                 for (int i = 0; i < cols.length ; i++)
                 {
                     if ( renderCols[i])
                     {
                         boolean iscdata=false;
                         Element TD = dom.createElement("TD");
                         TRB.appendChild( TD );
                         String AttributeName = cols[i].getFirstChild().getText();
                         diffElem = diffContainer.getBridgeDiffElement(AttributeName,bolist.getName(),o.getBoui());
                         AttributeHandler attr= o.getAttribute( AttributeName );

                         Text value=null;
                         if ( attr != null )
                         {

                             value=buildXML.getValueAttribute( dom , attr );

                         }
                         else
                         {

                             attr=((bridgeHandler) bolist).getAttribute( AttributeName );
                             value=buildXML.getValueAttribute( dom , attr );

                         }
                         if(diffElem != null)
                         {
                            TD.setAttribute("class","gridCBodySrc");
                            bouiNew = diffElem.getBoui();
                         }
                         else
                         {
//                            TD.setAttribute("class", (exists || bolist.haveVL()) ? "gridCBody" : "gridCBodySrc");
                            TD.setAttribute("class", (exists ) ? "gridCBody" : "gridCBodySrc");
                         }
                         TD.appendChild( value );

                     }
                }
                if(bouiNew != 0){
                    // linha só com os atributos alterados
                    bridgeDst.beforeFirst();
                    boObject objNew = bridgeDst.getObject(bouiNew);

                     TRB = dom.createElement("TR");
                     tableB.appendChild( TRB );

                     TD1 = dom.createElement("TD");
//                     if ( !bolist.haveVL() )
                         TD1.appendChild( dom.createCDATASection( objNew.getCARDIDwState().toString() ) );
//                     else
//                        TD1.appendChild( dom.createTextNode( "NBSP" ) );
                     TRB.appendChild( TD1 );
                     TD1.setAttribute("class","gridCBody");
                     for (int i = 0; i < cols.length ; i++)
                     {
                         if ( renderCols[i])
                         {
                             boolean iscdata=false;
                             Element TD = dom.createElement("TD");
                             TRB.appendChild( TD );
                             String AttributeName = cols[i].getFirstChild().getText();
                             diffElem = diffContainer.getBridgeDiffElement(AttributeName,bolist.getName(),objNew.getBoui());
                             AttributeHandler attr= objNew.getAttribute( AttributeName );

                             Text value=null;
                             if ( attr != null )
                             {

                                 value=buildXML.getValueAttribute( dom , attr );

                             }
                             else
                             {

                                 attr=((bridgeHandler) bolist).getAttribute( AttributeName );
                                 value=buildXML.getValueAttribute( dom , attr );

                             }
                             if(diffElem != null)
                             {
                                TD.setAttribute("class","gridCBodyDst");
                             }
                             else
                             {
                                TD.setAttribute("class","gridCBody");
                             }
                             TD.appendChild( value );

                         }
                    }
                }
         }
         if(bridgeDst != null && diffContainer.getBridgeDstDiffSize() > 0){
             bridgeDst.beforeFirst();
             while ( bridgeDst.next() )
             {
                     o = bridgeDst.getObject();
                     diffElem = diffContainer.getBridgeDstDiffElem(bridgeDst.getName(),o.getBoui());
                     if(diffElem != null){
                         Element TRB = dom.createElement("TR");
                         tableB.appendChild( TRB );

                         Element TD1 = dom.createElement("TD");
//                         if ( !bridgeDst.haveVL() )
                             TD1.appendChild( dom.createCDATASection( o.getCARDIDwState().toString() ) );
//                         else
//                            TD1.appendChild( dom.createTextNode( "NBSP" ) );
                         TRB.appendChild( TD1 );
                         TD1.setAttribute("class","gridCBodyDst");
                         for (int i = 0; i < cols.length ; i++)
                         {
                             if ( renderCols[i])
                             {
                                 boolean iscdata=false;
                                 Element TD = dom.createElement("TD");
                                 TRB.appendChild( TD );
                                 String AttributeName = cols[i].getFirstChild().getText();
                                 AttributeHandler attr= o.getAttribute( AttributeName );

                                 Text value=null;
                                 if ( attr != null )
                                 {

                                     value=buildXML.getValueAttribute( dom , attr );

                                 }
                                 else
                                 {

                                     attr=((bridgeHandler) bridgeDst).getAttribute( AttributeName );
                                     value=buildXML.getValueAttribute( dom , attr );

                                 }
                                 TD.setAttribute("class","gridCBodyDst");
                                 TD.appendChild( value );

                             }
                        }
                     }
             }
         }
    }
    private static void buildSection(DifferenceContainer diffContainer, String label , docHTML doc, boObject srcBo ,boObject dstBo, XMLDocument dom, Node node ,  ngtXMLHandler xmlForm ) throws SQLException , boRuntimeException
    {
         ngtXMLHandler[] rows = xmlForm.getChildNode("rows").getChildNodes() ;
         boolean renderSection =false;
         AttributeHandler attrDst=null;

         boolean renderRows[] = new boolean[ rows.length ] ;
         /*verificar seguranças */
         for (int i = 0; i < rows.length ; i++)
         {
            ngtXMLHandler[] cells=rows[i].getChildNodes();
            String attr1Name =  null;
            String attr2Name =  null;

            attr1Name = cells[0].getFirstChild() != null ? cells[0].getFirstChild().getText():null;

            AttributeHandler attr1=null;
            AttributeHandler attr2=null;


            if( attr1Name != null ) attr1=srcBo.getAttribute( attr1Name );

            if ( cells.length ==2 )
            {
                attr2Name = cells[1].getFirstChild() != null ? cells[1].getFirstChild().getText():null;
                if ( attr2Name != null ) attr2=srcBo.getAttribute( attr2Name );
            }

            renderRows[ i ] = false;
            if ( attr1 != null && attr1.hasRights() )
            {
                renderSection=true;
                renderRows[ i ] =true;
            }
            if ( attr2!= null && attr2.hasRights() )
            {
                renderSection=true;
                renderRows[ i ] =true;
            }


         }
         /*fim de verifcar segurity */

         if ( renderSection )
         {
               if ( !xmlForm.getAttribute("showlabel","no" ).equalsIgnoreCase("no") )
                {
                    Element area=dom.createElement("div");
                    area.setAttribute("class","section_title");

                    Node node1=node.appendChild(area);
                    Element tit=dom.createElement("p");

                    tit.appendChild( dom.createTextNode( ""+label ));
                    node1.appendChild( tit );
                }
                Element table = dom.createElement("table");
                table.setAttribute("class","section");

                node.appendChild( table );
                Element TRx = dom.createElement("tr");
                table.appendChild(TRx);
                Element TDx = dom.createElement("td");
                TDx.setAttribute("width","10%");
                TRx.appendChild( TDx );
                TDx = dom.createElement("td");
                TDx.setAttribute("width","40%");
                TRx.appendChild( TDx );
                TDx = dom.createElement("td");
                TDx.setAttribute("width","10%");
                TRx.appendChild( TDx );

                TDx = dom.createElement("td");
                TDx.setAttribute("width","40%");
                TRx.appendChild( TDx );

                for (int i = 0; i < rows.length  ; i++)
                 {

                    if ( renderRows[ i ] )
                    {
                        Element TR = dom.createElement("tr");
                        table.appendChild( TR );

                        ngtXMLHandler[] cells=rows[i].getChildNodes();
                        String attr1Name =  null;
                        String attr2Name =  null;

                        attr1Name = cells[0].getFirstChild() != null ? cells[0].getFirstChild().getText():null;

                        AttributeHandler attr1=null;
                        AttributeHandler attr2=null;

                        if ( attr1Name != null ) attr1=srcBo.getAttribute( attr1Name );

                        if ( cells.length ==2 )
                        {
                            attr2Name = cells[1].getFirstChild() != null ? cells[1].getFirstChild().getText():null;
                            if ( attr2Name != null ) attr2=srcBo.getAttribute( attr2Name );
                        }

                        if ( attr1!= null && !attr1.canAccess()) attr1=null;
                        if ( attr2!= null && !attr2.canAccess()) attr2=null;

                        DifferenceElement diffElem = null;
                        if ( cells.length == 1)
                        {
                            if ( attr1!= null )
                            {
                                String xx=cells[0].getFirstChild().getNodeName();
                                diffElem = diffContainer.getDifferenceElement(attr1.getName(),srcBo.getBoui());

                                boolean showLabel = cells[0].getFirstChild().getAttribute("showlabel","no").equalsIgnoreCase("yes");
                                if ( showLabel )
                                {
                                    Element TD1= dom.createElement("td");
                                    TD1.appendChild(dom.createTextNode( attr1.getDefAttribute().getLabel() ) );
                                   // TD1.setAttribute("class",(de == null) ? "label" : "labelSrc");
                                    TD1.setAttribute("width","10%");

                                    Element TD2= dom.createElement("td");
                                    TD2.setAttribute("width","90%" );
                                    TD2.setAttribute("colspan","3");
                                    boolean atObj = setValueAttribute(TD1, TD2, false, dom ,srcBo,dstBo,attr1, diffElem, diffContainer, "inputSrc" );
                                    //TD2.setAttribute("class",(de == null) ? "input" : "inputSrc");
                                    TR.appendChild( TD1 );
                                    TR.appendChild( TD2 );

                                    if(diffElem != null || atObj){
                                        Element TD = dom.createElement("td");
                                        TD.setAttribute("class","label");
                                        TD.setAttribute("width","100%");
                                        TD.setAttribute("colspan","4");
                                        TR.appendChild(TD);


                                        TR = dom.createElement("tr");
                                        table.appendChild( TR );
                                        if ( attr1Name != null ) attrDst=dstBo.getAttribute( attr1Name );
                                        Element TD1Dst= dom.createElement("td");
                                        //TD1Dst.appendChild(dom.createTextNode( attrDst.getDefAttribute().getLabel() ) );
                                        //TD1Dst.setAttribute("class","labelDst");
                                        TD1Dst.setAttribute("width","10%");
                                        Element TD2Dst = dom.createElement("td");
                                        setValueAttribute( null,TD2Dst, true, dom ,dstBo,srcBo, attrDst, diffElem, diffContainer,"inputDst" );
                                        //TD2Dst.appendChild( getValueAttribute( dom , dstBo, attrDst ) );
                                        TD2Dst.setAttribute("width","90%" );
                                        TD2Dst.setAttribute("colspan","3");
                                        //TD2Dst.setAttribute("class","inputDst");
                                        TR.appendChild( TD1Dst );
                                        TR.appendChild( TD2Dst );
                                    }
                                }
                                else
                                {
                                    Element TD2= dom.createElement("td");
                                    boolean atObj = setValueAttribute( null,TD2 ,false,  dom ,srcBo,dstBo, attr1, diffElem, diffContainer,"inputSrc" );
                                    //TD2.appendChild( getValueAttribute( dom , srcBo, attr1 ) );
                                    TD2.setAttribute("width","100%" );
                                    TD2.setAttribute("colspan","4");
                                    //TD2.setAttribute("class",(de == null) ? "input" : "inputSrc");
                                    TR.appendChild( TD2 );
                                    if(diffElem != null || atObj)
                                    {
                                        Element TD = dom.createElement("td");
                                        TD.setAttribute("class","label");
                                        TD.setAttribute("width","100%");
                                        TD.setAttribute("colspan","4");
                                        TR.appendChild(TD);

                                        TR = dom.createElement("tr");
                                        table.appendChild( TR );
                                        if ( attr1Name != null ) attrDst=dstBo.getAttribute( attr1Name );
                                        Element TD2Dst= dom.createElement("td");
                                       // TD2Dst.appendChild( getValueAttribute( dom , dstBo, attrDst ) );
                                        setValueAttribute(null, TD2Dst , true, dom ,srcBo,dstBo, attrDst, diffElem, diffContainer,"inputDst" );
                                        TD2Dst.setAttribute("width","100%" );
                                        TD2Dst.setAttribute("colspan","4");
                                        //TD2Dst.setAttribute("class","inputDst");
                                        TR.appendChild( TD2Dst );
                                    }
                                }
                            }
                            else if(diffElem == null)
                            {
                                Element TD = dom.createElement("td");
                                TD.setAttribute("class","label");
                                TD.setAttribute("width","100%");
                                TD.setAttribute("colspan","4");
                                TR.appendChild(TD);

                            }

                        }
                        else // cells.length=2
                        {
                            boolean atObjCol1 = false;
                            boolean atObjCol2 = false;
                            if ( attr1!= null )
                            {
                                boolean showLabel = cells[0].getFirstChild().getAttribute("showlabel","no").equalsIgnoreCase("yes");
                                diffElem = diffContainer.getDifferenceElement(attr1.getName(),srcBo.getBoui());
                                if ( showLabel )
                                {
                                    Element TD1= dom.createElement("td");
                                    TD1.appendChild(dom.createTextNode( attr1.getDefAttribute().getLabel() ) );
                                    TD1.setAttribute("class",(diffElem == null) ? "label" : "labelSrc");
                                    TD1.setAttribute("width","10%");
                                    Element TD2= dom.createElement("td");
                                    atObjCol1 = setValueAttribute( TD1, TD2 ,false, dom ,srcBo,dstBo, attr1, diffElem, diffContainer,"inputSrc" );
                                    //TD2.appendChild( getValueAttribute( dom , srcBo, attr1 ) );
                                    TD2.setAttribute("width","40%" );
                                    //TD2.setAttribute("class",(de == null) ? "input" : "inputSrc");
                                    TR.appendChild( TD1 );
                                    TR.appendChild( TD2 );

                                }
                                else
                                {
                                    Element TD2= dom.createElement("td");
                                    //TD2.appendChild( getValueAttribute( dom ,srcBo, attr1 ) );
                                    atObjCol1 = setValueAttribute(null, TD2 ,false, dom ,srcBo,dstBo, attr1, diffElem, diffContainer,"inputSrc" );
                                    TD2.setAttribute("width","50%" );
                                    TD2.setAttribute("colspan","2");
                                    TD2.setAttribute("class",(diffElem == null) ? "input" : "inputSrc");
                                    TR.appendChild( TD2 );

                                }

                            }
                            else
                            {
                                Element TD = dom.createElement("td");
                                TD.setAttribute("class","label");
                                TD.setAttribute("width","50%");
                                TD.setAttribute("colspan","2");
                                TR.appendChild(TD);
                            }
                            DifferenceElement diffElem2 = null;
                            if ( attr2!= null )
                            {
                                diffElem2 = diffContainer.getDifferenceElement(attr2.getName(),srcBo.getBoui());
                                boolean showLabel = cells[1].getFirstChild().getAttribute("showlabel","no").equalsIgnoreCase("yes");
                                if ( showLabel )
                                {
                                    Element TD1= dom.createElement("td");
                                    TD1.appendChild(dom.createTextNode( attr2.getDefAttribute().getLabel() ) );
                                    TD1.setAttribute("class",(diffElem2 == null) ? "label" : "labelSrc");
                                    TD1.setAttribute("width","10%");
                                    Element TD2= dom.createElement("td");
                                    //TD2.appendChild( getValueAttribute( dom , dstBo, attr2 ) );
                                    atObjCol2 = setValueAttribute(TD1, TD2 , true, dom ,dstBo,srcBo, attr2, diffElem2, diffContainer,"inputSrc" );
                                    TD2.setAttribute("width","40%" );
                                    //TD2.setAttribute("class",(de2 == null) ? "input" : "inputSrc");
                                    TR.appendChild( TD1 );
                                    TR.appendChild( TD2 );

                                }
                                else
                                {
                                    Element TD2= dom.createElement("td");
                                    //TD2.appendChild( getValueAttribute( dom , dstBo, attr2 ) );
                                    atObjCol2 = setValueAttribute(null, TD2 , true, dom ,dstBo,srcBo, attr2, diffElem2, diffContainer,"inputSrc" );
                                    TD2.setAttribute("width","50%" );
                                    TD2.setAttribute("colspan","2");
                                    //TD2.setAttribute("class",(de2 == null) ? "input" : "inputSrc");
                                    TR.appendChild( TD2 );
                                }
                            }
                            else
                            {
                                Element TD = dom.createElement("td");
                                TD.setAttribute("class","label");
                                TD.setAttribute("width","50%");
                                TD.setAttribute("colspan","2");
                                TR.appendChild(TD);
                            }
                            boolean noCell = false;
                        // Differencas
                            if ( attr1!= null )
                            {
                                if(diffElem != null || atObjCol1){
                                    TR = dom.createElement("tr");
                                    table.appendChild( TR );

                                    if ( attr1Name != null ) attrDst=dstBo.getAttribute( attr1Name );
                                    Element TD1Dst= dom.createElement("td");
                                   // TD1Dst.appendChild(dom.createTextNode( attrDst.getDefAttribute().getLabel() ) );
                                    //TD1Dst.setAttribute("class","labelDst");
                                    TD1Dst.setAttribute("width","10%");
                                    Element TD2Dst = dom.createElement("td");
                                    //TD2Dst.appendChild( getValueAttribute( dom ,dstBo, attrDst ) );
                                    setValueAttribute(null,TD2Dst , true, dom ,dstBo, srcBo, attrDst, diffElem, diffContainer,"inputDst" );
                                    TD2Dst.setAttribute("width","40%" );
                                    //TD2Dst.setAttribute("class","inputDst");
                                    TR.appendChild( TD1Dst );
                                    TR.appendChild( TD2Dst );
                                }
                                noCell = true;
                            }
                            else
                            {
                                TR = dom.createElement("tr");
                                table.appendChild( TR );

                                Element TD = dom.createElement("td");
                                TD.setAttribute("class","label");
                                TD.setAttribute("width","50%");
                                TD.setAttribute("colspan","2");
                                TR.appendChild(TD);
                            }

                            if ( attr2!= null )
                            {

                                if(diffElem2 != null){
                                // tem de haver uma condiçao se attr1 == null para escrecer isto aqui!!!!
                                    if(noCell)
                                    {
                                        TR = dom.createElement("tr");
                                        table.appendChild( TR );

                                        Element TD = dom.createElement("td");
                                        TD.setAttribute("class","label");
                                        TD.setAttribute("width","50%");
                                        TD.setAttribute("colspan","2");
                                        TR.appendChild(TD);
                                    }
                                    if ( attr2Name != null ) attrDst=dstBo.getAttribute( attr2Name );
                                    Element TD1Dst= dom.createElement("td");
                                    //TD1Dst.appendChild(dom.createTextNode( attrDst.getDefAttribute().getLabel() ) );
                                    //TD1Dst.setAttribute("class","labelDst");
                                    TD1Dst.setAttribute("width","10%");
                                    Element TD2Dst = dom.createElement("td");
                                    //TD2Dst.appendChild( getValueAttribute( dom , dstBo, attrDst ) );
                                    setValueAttribute( null,TD2Dst , true,dom ,dstBo,srcBo, attrDst, diffElem2, diffContainer,"inputDst" );
                                    TD2Dst.setAttribute("width","40%" );
                                    //TD2Dst.setAttribute("class","inputDst");
                                    TR.appendChild( TD1Dst );
                                    TR.appendChild( TD2Dst );
                                }
                            }
                            else
                            {
                                Element TD = dom.createElement("td");
                                TD.setAttribute("class","label");
                                TD.setAttribute("width","50%");
                                TD.setAttribute("colspan","2");
                                TR.appendChild(TD);
                            }


                        }



                    }
                 }
         }
    }
    private static boolean setValueAttribute(Element td1,Element td2, boolean difference ,XMLDocument dom , boObject srcBo,boObject dstBo, AttributeHandler attHandler,DifferenceElement de,DifferenceContainer diffContainer,String labelClass)  throws SQLException , boRuntimeException
    {
        if(attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_OBJECTATTRIBUTE)
        {
            if(attHandler.getDefAttribute().getRelationType() == attHandler.getDefAttribute().RELATION_1_TO_1)
            {
                if(td1 != null)td1.setAttribute("class",(de == null && dstBo!=null) ? "label" : "labelSrc");
                td2.setAttribute("class",(de == null && dstBo!=null) ? "input" : labelClass);
                if(labelClass!=null && de!=null && (labelClass.equals("inputSrc") && de.getSrcValue()==null) || (labelClass.equals("inputDst") && de.getDstValue()==null))
                  td2.appendChild(getValueAttribute( dom , null ) );
                else
                  td2.appendChild(getValueAttribute( dom , attHandler ) );
                return false;
            }
            else if(attHandler.getDefAttribute().getRelationType() == attHandler.getDefAttribute().RELATION_MULTI_VALUES)
            {
                setMultiValues(td1,td2,dom,difference,de,attHandler,dstBo.getAttribute(attHandler.getName()),labelClass);
                return true;
            }
            else
            {
                Element TDaux;
                Element TABLE = dom.createElement("TABLE");
                TABLE.setAttribute("cellpadding","0pt");
                TABLE.setAttribute("cellspacing","0pt");
                TABLE.setAttribute("class","gridTD");
                Element TBODY = dom.createElement("TBODY");
                Element TR = dom.createElement("TR");
                DifferenceElement diffElem = null;
                String value=null;
                bridgeHandler bHandler = srcBo.getBridge(attHandler.getName());
                bHandler.beforeFirst();
                bridgeHandler bridgeDst = null;
                bridgeDst = dstBo.getBridge( attHandler.getName() );
                bridgeDst.beforeFirst();
                long size = bHandler.getRecordCount();
                boolean markNoExistence = false;
                boolean exists = false;
                while ( bHandler.next() )
                {
                    boObject objHandler = bHandler.getObject();
                    if(!difference)
                    {
                        diffElem = diffContainer.getBridgeDiffElement(objHandler.getName(),attHandler.getName(),objHandler.getBoui(),false);
                    }
                    exists = bridgeDst.haveBoui(objHandler.getBoui());
                    if(!exists) markNoExistence = true;
                    if(!difference || (!exists && difference))
                    {
                        value  = objHandler.getCARDID(false).toString();
                        TDaux = dom.createElement("TD");
                        TDaux.setAttribute("nowrap","");
                        TDaux.setAttribute("class",(exists) ? "input" : labelClass );
                        TDaux.appendChild(dom.createCDATASection(value != null ? value : "NBSP"));
                        TR.appendChild(TDaux);
                        size --;
                        if(size == 0) TDaux.setAttribute("width","100%");
                    }
                }
                TBODY.appendChild(TR);
                TABLE.appendChild(TBODY);

                if(bHandler.getRecordCount() > 0 )
                {
                    if(td1 != null)td1.setAttribute("class",(!markNoExistence) ? "label" : "labelSrc");
                    td2.appendChild(TABLE);
                }
                else if((!difference && bHandler.getRecordCount() == 0)|| (bridgeDst.getRecordCount() > 0 && difference))
                {
                    if(td1 != null)td1.setAttribute("class",bridgeDst.getRecordCount() == 0 ? "label" : "labelSrc");
                    td2.setAttribute("class",(de == null)  ? "input" : labelClass);
                    td2.appendChild(dom.createTextNode( "NBSP"));
                }
                return true;
            }
        }
        else if(attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_ATTRIBUTE)
        {
                if(td1 != null)td1.setAttribute("class",(de == null && dstBo != null) ? "label" : "labelSrc");
                td2.setAttribute("class",(de == null && dstBo != null) ? "input" : labelClass);
                if(labelClass!=null && de!=null && (labelClass.equals("inputSrc") && de.getSrcValue()==null) || (labelClass.equals("inputDst") && de.getDstValue()==null))
                  td2.appendChild(getValueAttribute( dom , null ) );
                else
                  td2.appendChild(getValueAttribute( dom , attHandler ) );
                return false;

        }
        if(td1 != null)td1.setAttribute("class",(de == null) ? "label" : "labelSrc");
        td2.setAttribute("class",(de == null) ? "input" : labelClass);
        td2.appendChild(dom.createTextNode( "NBSP"));
        return false;
    }

    private static void setMultiValues(Element td1,Element td2,XMLDocument dom,boolean difference,DifferenceElement diffElem,AttributeHandler srcAttHandler , AttributeHandler dstAttHandler,String labelClass) throws boRuntimeException
    {
        boObject srcBo = null;
        boObject dstBo = null;
        boObject[] srcAttBoObjects = srcAttHandler.getObjects();
        boObject[] dstAttBoObjects = dstAttHandler.getObjects();
        long boui;
        if(srcAttBoObjects != null && dstAttBoObjects != null)
        {
            String value=null;
            boolean markNoExistence = false;
            Element TDaux;
            Element TABLE = dom.createElement("TABLE");
            TABLE.setAttribute("cellpadding","0pt");
            TABLE.setAttribute("cellspacing","0pt");
            TABLE.setAttribute("class","gridTD");
            Element TBODY = dom.createElement("TBODY");
            Element TR = dom.createElement("TR");
            for(int i = 0 ; i < srcAttBoObjects.length ; i++)
            {
                TDaux = dom.createElement("TD");
                TDaux.setAttribute("nowrap","");
                srcBo = srcAttBoObjects[i];
                value  = srcBo.getCARDID(false).toString();
                boui = srcBo.getBoui();
                if(DifferenceHelper.existMultiValue(dstAttBoObjects , boui))
                {
                    //if(!difference)TDaux.setAttribute("class","input");
                    TDaux.setAttribute("class","input");
                }
                else
                {
                    markNoExistence = true;
                    TDaux.setAttribute("class", labelClass );
                }
                TDaux.appendChild(dom.createCDATASection(value != null ? value : "NBSP"));
                TR.appendChild(TDaux);
                if(i == srcAttBoObjects.length - 1) TDaux.setAttribute("width","100%");
            }
            TBODY.appendChild(TR);
            TABLE.appendChild(TBODY);
            if(srcAttBoObjects.length > 0 )
            {
                if(td1 != null){
                    boolean moreMultiValues = false;
                    for(int i = 0 ; i < dstAttBoObjects.length ; i++)
                    {
                        dstBo = dstAttBoObjects[i];
                        boui = dstBo.getBoui();
                        if(!DifferenceHelper.existMultiValue(srcAttBoObjects , boui)) moreMultiValues = true;
                    }
                    td1.setAttribute("class",(markNoExistence || moreMultiValues) ? "labelSrc" : "label");
                }
                td2.appendChild(TABLE);
            }
            else if((!difference && srcAttBoObjects.length == 0)|| (dstAttBoObjects.length > 0 && difference))
            {
                td2.setAttribute("class",(diffElem == null) ? "input" : labelClass);
                td2.appendChild(dom.createTextNode( "NBSP"));
            }
        }
        else if(srcAttBoObjects == null && dstAttBoObjects == null && !difference)
        {
            td2.setAttribute("class", "input" );
            td2.appendChild(dom.createTextNode( "NBSP"));
        }
        else if(srcAttBoObjects != null && dstAttBoObjects == null && !difference)
        {
            String value=null;
            Element TDaux;
            Element TABLE = dom.createElement("TABLE");
            TABLE.setAttribute("cellpadding","0pt");
            TABLE.setAttribute("cellspacing","0pt");
            TABLE.setAttribute("class","gridTD");
            Element TBODY = dom.createElement("TBODY");
            Element TR = dom.createElement("TR");
            for(int i = 0 ; i < srcAttBoObjects.length ; i++)
            {
                TDaux = dom.createElement("TD");
                TDaux.setAttribute("nowrap","");
                srcBo = srcAttBoObjects[i];
                value  = srcBo.getCARDID(false).toString();
                TDaux.setAttribute("class", labelClass );
                TDaux.appendChild(dom.createCDATASection(value != null ? value : "NBSP"));
                TR.appendChild(TDaux);
                if(i == srcAttBoObjects.length - 1) TDaux.setAttribute("width","100%");
            }
            TBODY.appendChild(TR);
            TABLE.appendChild(TBODY);
            if(srcAttBoObjects.length > 0 )
            {
                if(td1 != null){
                    td1.setAttribute("class", "label");
                }
                td2.appendChild(TABLE);
            }
        }
        else if(srcAttBoObjects == null && dstAttBoObjects != null && !difference)
        {
            if(td1 != null){
                td1.setAttribute("class", "labelSrc");
            }
            td2.setAttribute("class", "inputSrc" );
            td2.appendChild(dom.createTextNode( "NBSP"));
        }
        else if(srcAttBoObjects != null && dstAttBoObjects == null && difference)
        {
            String value=null;
            Element TDaux;
            Element TABLE = dom.createElement("TABLE");
            TABLE.setAttribute("cellpadding","0pt");
            TABLE.setAttribute("cellspacing","0pt");
            TABLE.setAttribute("class","gridTD");
            Element TBODY = dom.createElement("TBODY");
            Element TR = dom.createElement("TR");
            for(int i = 0 ; i < srcAttBoObjects.length ; i++)
            {
                TDaux = dom.createElement("TD");
                TDaux.setAttribute("nowrap","");
                srcBo = srcAttBoObjects[i];
                value  = srcBo.getCARDID(false).toString();
                TDaux.setAttribute("class", labelClass );
                TDaux.appendChild(dom.createCDATASection(value != null ? value : "NBSP"));
                TR.appendChild(TDaux);
                if(i == srcAttBoObjects.length - 1) TDaux.setAttribute("width","100%");
            }
            TBODY.appendChild(TR);
            TABLE.appendChild(TBODY);
            td2.appendChild(TABLE);

        }
    }
    public static void buildDifferenceResume(docHTML doc,DifferenceContainer diffContainer, boObject srcBo,boObject dstBo, XMLDocument dom, Node node ,EboContext boctxDst ) throws SQLException , boRuntimeException
    {
          // Attribute Resume
          buildAttributeResume(diffContainer,srcBo,dstBo,dom,node);
          // Bridge Resume
          buildBridgeResume(diffContainer,srcBo,dstBo,dom,node);
          // Childs
          buildChildsResume(doc,diffContainer,dom,node,boctxDst);
    }
    private static void buildChildsResume(docHTML doc, DifferenceContainer diffContainer,XMLDocument dom, Node node ,EboContext boctxDst) throws SQLException , boRuntimeException
    {
        boObject srcBo = null;
        boObject dstBo = null;
        DifferenceContainer dContChild = null;
        for (Iterator objects = diffContainer.getDiffChildsIterator(); objects.hasNext() ;) {
            dContChild = (DifferenceContainer)objects.next();
            dstBo = doc.getObject(dContChild.getBouiSrc());
            srcBo = boObject.getBoManager().loadObject(boctxDst,dContChild.getBouiDst());
            buildDifferenceResume(doc,dContChild,srcBo,dstBo,dom,node,boctxDst);
        }
    }
    private static void buildAttributeResume( DifferenceContainer diffContainer, boObject srcBo,boObject dstBo, XMLDocument dom, Node node ) throws SQLException , boRuntimeException
    {

        Element table = dom.createElement("table");
        table.setAttribute("class","section");
        node.appendChild( table );
        Element TRx = dom.createElement("tr");
        table.appendChild(TRx);
        Element TDx = dom.createElement("td");
        TDx.setAttribute("width","10%");
        TRx.appendChild( TDx );
        TDx = dom.createElement("td");
        TDx.setAttribute("width","40%");
        TRx.appendChild( TDx );
        TDx = dom.createElement("td");
        TDx.setAttribute("width","10%");
        TRx.appendChild( TDx );
        TDx = dom.createElement("td");
        TDx.setAttribute("width","40%");
        TRx.appendChild( TDx );


        DifferenceElement diffElem = null;
        Iterator att = diffContainer.getAttributeDiffIterator();
        while(att.hasNext()) {
            diffElem = (DifferenceElement)att.next();
            if("".equals(diffElem.getBridgeName()) || diffElem.getBridgeName() == null){
                Element TR = dom.createElement("tr");
                table.appendChild( TR );

                Element TD1= dom.createElement("td");
                TD1.appendChild(dom.createTextNode(srcBo.getAttribute(diffElem.getAttributeName()).getDefAttribute().getLabel()));
                TD1.setAttribute("class","label");
                TD1.setAttribute("width","10%");

                Element TD2= dom.createElement("td");
                TD2.setAttribute("width","90%" );
                TD2.setAttribute("colspan","3");
                //martelada, devido a quando um atributo de um objecto filho está vazio, ele baralha-se e vai buscar o novo valor
                if(diffElem.getSrcValue()==null)
                {
                  TD2.appendChild(getValueAttribute(dom,null));
                }
                else
                {
                  TD2.appendChild(getValueAttribute(dom,srcBo.getAttribute(diffElem.getAttributeName())));
                }
                TD2.setAttribute("class","inputSrc");
                TR.appendChild( TD1 );
                TR.appendChild( TD2 );

                Element TRDst = dom.createElement("tr");
                table.appendChild( TRDst );

                Element TD1Dst= dom.createElement("td");
                TD1Dst.setAttribute("width","10%");

                Element TD2Dst= dom.createElement("td");
                TD2Dst.setAttribute("width","90%" );
                TD2Dst.setAttribute("colspan","3");
                //martelada, quanod o atributo de origem esta vazio, ele não consegue ir buscar o novo
                String valDst = getValueAttribute(dom,dstBo.getAttribute(diffElem.getAttributeName())).getData();
                if(valDst.equals("NBSP") && diffElem.getDstValue()!=null && !diffElem.isRelation())
                {
                    TD2Dst.appendChild(dom.createTextNode( diffElem.getDstValue().toString() ));
                }
                else
                {
                    TD2Dst.appendChild(getValueAttribute(dom,dstBo.getAttribute(diffElem.getAttributeName())));
                }
                //TD2Dst.appendChild(getValueAttribute(dom,dstBo.getAttribute(diffElem.getAttributeName())));
                TD2Dst.setAttribute("class","inputDst");
                TRDst.appendChild( TD1Dst );
                TRDst.appendChild( TD2Dst );
            }
        }

        att = diffContainer.getBridgeDiffIterator();
        String mvName = null;
        boolean haveLabel = false;
        while(att.hasNext()) {
            diffElem = (DifferenceElement)att.next();
            if(diffElem.isMultiValue()){
                Element TR = dom.createElement("tr");
                if(mvName == null) mvName = diffElem.getAttributeName();
                if(!mvName.equals(diffElem.getAttributeName())){
                    mvName = diffElem.getAttributeName();
                    haveLabel = false;
                }
                else if(!haveLabel)
                {
                    table.appendChild( TR );
                    Element TD1= dom.createElement("td");
                    TD1.appendChild(dom.createTextNode(srcBo.getAttribute(diffElem.getAttributeName()).getDefAttribute().getLabel()));
                    TD1.setAttribute("class","label");
                    TD1.setAttribute("width","10%");
                    TR.appendChild( TD1 );
                    haveLabel = true;
                }
                else
                {
                    table.appendChild( TR );
                    Element TD1= dom.createElement("td");
                    TD1.setAttribute("class","label");
                    TD1.setAttribute("width","10%");
                    TR.appendChild( TD1 );
                }
                Element TD2= dom.createElement("td");
                if(diffElem.getSrcValue() != null)
                {
                    TD2.setAttribute("width","90%" );
                    TD2.setAttribute("colspan","3");
                    //attr.getObject().getCARDID(false).toString();
                    TD2.appendChild(dom.createCDATASection(((boObject)diffElem.getSrcValue()).getCARDID(false).toString() ) );
                    //TD2.appendChild(getValueAttribute(dom,srcBo.getAttribute(diffElem.getAttributeName())));
                    TD2.setAttribute("class","inputSrc");
                    TR.appendChild( TD2 );
                }
                if(diffElem.getDstValue() != null)
                {
                    TD2.setAttribute("width","90%" );
                    TD2.setAttribute("colspan","3");
                    TD2.appendChild(dom.createCDATASection(((boObject)diffElem.getDstValue()).getCARDID(false).toString() ) );
                    //TD2Dst.appendChild(dom.createTextNode(diffElem.getDstValue().toString() ) );
                    //TD2Dst.appendChild(getValueAttribute(dom,dstBo.getAttribute(diffElem.getAttributeName())));
                    TD2.setAttribute("class","inputDst");
                    TR.appendChild( TD2 );
                }

            }
        }
    }

    private static void buildBridgeResume(DifferenceContainer diffContainer ,boObject srcBo,boObject dstBo,XMLDocument dom, Node node ) throws SQLException , boRuntimeException
    {
        bridgeHandler bHandler = null;
        Iterator att = null;
        DifferenceElement diffElem = null;
        boBridgesArray  bridgesArray = srcBo.getBridges();
        Enumeration oEnum =  bridgesArray.elements();
        while( oEnum.hasMoreElements() )
        {
            boolean labelOn = false;
            bHandler = (bridgeHandler)oEnum.nextElement();

            Element table = dom.createElement("table");

            // Atributos
            att = diffContainer.getAttributeDiffIterator();
            while(att.hasNext()) {
                diffElem = (DifferenceElement)att.next();
                if((!"".equals(diffElem.getBridgeName()) || diffElem.getBridgeName() != null) && bHandler.getName().equals(diffElem.getBridgeName()))
                {
                    if(!labelOn)
                    {
                        labelOn = true;

                        Element area=dom.createElement("div");
                        area.setAttribute("class","area_title");
                        Node node1 = node.appendChild(area);
                        Element tit = dom.createElement("p");
                        node1.appendChild( tit );
                        tit.appendChild( dom.createTextNode( bHandler.getDefAttribute().getLabel() ));

                        table = dom.createElement("table");
                        table.setAttribute("class","section");
                        node.appendChild( table );
                        Element TRx = dom.createElement("tr");
                        table.appendChild(TRx);
                        Element TDx = dom.createElement("td");
                        TDx.setAttribute("width","10%");
                        TRx.appendChild( TDx );
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width","40%");
                        TRx.appendChild( TDx );
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width","10%");
                        TRx.appendChild( TDx );
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width","40%");
                        TRx.appendChild( TDx );
                    }
                    Element TR = dom.createElement("tr");
                    table.appendChild( TR );

                    Element TD1= dom.createElement("td");
                    TD1.appendChild(dom.createTextNode(srcBo.getAttribute(diffElem.getAttributeName()).getDefAttribute().getLabel()));
                    TD1.setAttribute("class","label");
                    TD1.setAttribute("width","10%");

                    Element TD2= dom.createElement("td");
                    TD2.setAttribute("width","90%" );
                    TD2.setAttribute("colspan","2");
                    //TD2.appendChild(dom.createTextNode(diffElem.getSrcValue().toString() ) );
                    TD2.appendChild(getValueAttribute(dom,srcBo.getAttribute(diffElem.getAttributeName())));
                    TD2.setAttribute("class","inputSrc");
                    TR.appendChild( TD1 );
                    TR.appendChild( TD2 );

                    Element TRDst = dom.createElement("tr");
                    table.appendChild( TRDst );

                    Element TD1Dst= dom.createElement("td");
                    TD1Dst.setAttribute("width","10%");

                    Element TD2Dst= dom.createElement("td");
                    TD2Dst.setAttribute("width","90%" );
                    TD2Dst.setAttribute("colspan","3");
                    //TD2Dst.appendChild(dom.createTextNode(diffElem.getDstValue().toString() ) );
                    TD2Dst.appendChild(getValueAttribute(dom,dstBo.getAttribute(diffElem.getAttributeName())));
                    TD2Dst.setAttribute("class","inputDst");
                    TRDst.appendChild( TD1Dst );
                    TRDst.appendChild( TD2Dst );
                }
            }
           // Bridges Antigas
            Iterator bridge = diffContainer.getBridgeSrcDiffIterator();
            while(bridge.hasNext()) {
                diffElem = (DifferenceElement)bridge.next();
                if((!"".equals(diffElem.getBridgeName()) || diffElem.getBridgeName() != null) && bHandler.getName().equals(diffElem.getBridgeName()))
                {
                    if(!labelOn)
                    {
                        labelOn = true;

                        Element area=dom.createElement("div");
                        area.setAttribute("class","area_title");
                        Node node1 = node.appendChild(area);
                        Element tit = dom.createElement("p");
                        node1.appendChild( tit );
                        tit.appendChild( dom.createTextNode( bHandler.getDefAttribute().getLabel() ));

                        table = dom.createElement("table");
                        table.setAttribute("class","section");
                        node.appendChild( table );
                        Element TRx = dom.createElement("tr");
                        table.appendChild(TRx);
                        Element TDx = dom.createElement("td");
                        TDx.setAttribute("width","10%");
                        TRx.appendChild( TDx );
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width","40%");
                        TRx.appendChild( TDx );
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width","10%");
                        TRx.appendChild( TDx );
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width","40%");
                        TRx.appendChild( TDx );
                    }
                    Element TR = dom.createElement("tr");
                    table.appendChild( TR );

                    Element TD2= dom.createElement("td");
                    TD2.setAttribute("width","100%" );
                    TD2.setAttribute("colspan","4");
                    TD2.appendChild(dom.createCDATASection(srcBo.getObject(diffElem.getBoui()).getCARDID(false).toString() ) );
                    TD2.setAttribute("class","inputSrc");
                    TR.appendChild( TD2 );
                }
            }
            // Bridges Novas
            bridge = diffContainer.getBridgeDstDiffIterator();
            while(bridge.hasNext()) {
                diffElem = (DifferenceElement)bridge.next();
                if((!"".equals(diffElem.getBridgeName()) || diffElem.getBridgeName() != null) && bHandler.getName().equals(diffElem.getBridgeName()))
                {
                    if(!labelOn)
                    {
                        labelOn = true;

                        Element area=dom.createElement("div");
                        area.setAttribute("class","area_title");
                        Node node1 = node.appendChild(area);
                        Element tit = dom.createElement("p");
                        node1.appendChild( tit );
                        tit.appendChild( dom.createTextNode( bHandler.getDefAttribute().getLabel() ));

                        table = dom.createElement("table");
                        table.setAttribute("class","section");
                        node.appendChild( table );
                        Element TRx = dom.createElement("tr");
                        table.appendChild(TRx);
                        Element TDx = dom.createElement("td");
                        TDx.setAttribute("width","10%");
                        TRx.appendChild( TDx );
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width","40%");
                        TRx.appendChild( TDx );
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width","10%");
                        TRx.appendChild( TDx );
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width","40%");
                        TRx.appendChild( TDx );
                    }
                    Element TR = dom.createElement("tr");
                    table.appendChild( TR );

                    Element TD2= dom.createElement("td");
                    TD2.setAttribute("width","100%" );
                    TD2.setAttribute("colspan","4");
                    TD2.appendChild(dom.createCDATASection(srcBo.getObject(diffElem.getBoui()).getCARDID(false).toString() ) );
                    TD2.setAttribute("class","inputDst");
                    TR.appendChild( TD2 );
                }
            }
        }
    }

    private static String getValue(AttributeHandler attr) throws boRuntimeException
    {
        if("boolean".equalsIgnoreCase(attr.getDefAttribute().getType()))
        {
            String value = attr.getValueString();
            if("0".equals(value))
            {
                //falta verificar a lingua
                return CARD_NAO;
            }
            else if("1".equals(value))
            {
                return CARD_SIM;
            }
            return value;
       }
       else if(attr.getDefAttribute().getLOVName() != null &&
                !"".equals(attr.getDefAttribute().getLOVName()))
       {
            String xlov = attr.getDefAttribute().getLOVName();
            String value = attr.getValueString();
            if(value != null && !"".equals(value))
            {
                lovObject lovObj = LovManager.getLovObject(attr.getParent().getEboContext(), xlov);
                if(lovObj != null)
                {
                    lovObj.beforeFirst();
                    while(lovObj.next())
                    {
                        if(value.equalsIgnoreCase(lovObj.getCode()))
                        {
                            return lovObj.getDescription();
                        }
                    }
                }
            }
            return attr.getValueString();
         }
         else if("dateTime".equalsIgnoreCase(attr.getDefAttribute().getType()))
         {
            java.util.Date d = null;
            if((d = attr.getValueDate()) != null)
            {
                 SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
                 return formatter.format(d);
            }
            return "";
         }
         else if("date".equalsIgnoreCase(attr.getDefAttribute().getType()))
         {
            java.util.Date d = null;
            if((d = attr.getValueDate()) != null)
            {
                 SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy");
                 return formatter.format(d);
            }
            return "";
         }
         else
         {
            NumberFormat currencyFormatter = NumberFormat.getInstance();
            if(attr.getDefAttribute().getDecimals() != 0)
            {
                //currency
                currencyFormatter.setParseIntegerOnly(false);
                if(CARD_Y.equalsIgnoreCase(attr.getDefAttribute().getGrouping()))
                {
                    currencyFormatter.setGroupingUsed(true);
                }
                currencyFormatter.setMaximumFractionDigits(attr.getDefAttribute().getDecimals());
                currencyFormatter.setMinimumFractionDigits(attr.getDefAttribute().getMinDecimals());
                currencyFormatter.setMinimumIntegerDigits(1);
                return currencyFormatter.format(attr.getValueDouble());
            }
            else if(CARD_Y.equalsIgnoreCase(attr.getDefAttribute().getGrouping()))
            {
                currencyFormatter.setParseIntegerOnly(false);
                currencyFormatter.setMinimumIntegerDigits(1);
                currencyFormatter.setGroupingUsed(true);
                return currencyFormatter.format(attr.getValueDouble());
            }
            return attr.getValueString();
         }
    }
}