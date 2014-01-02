/*Enconding=UTF-8*/
package netgest.bo.workflow;

import java.io.IOException;
import java.util.Hashtable;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.dochtml.docHTML_grid;
import netgest.bo.dochtml.docHTML_renderFields;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.utils.ClassUtils;
import netgest.bo.runtime.boObjectStateHandler;
/**
 * <p>Title: DocWfHTML </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
    public final class DocWfHTML 
{

    public static void renderExtendAttributes(boObject actionObject,bridgeHandler bridgeHandler, docHTML DOC, docHTML_controler DOCLIST,PageContext pageContext,int IDX)  throws boRuntimeException,IOException    
    {
        JspWriter out= pageContext.getOut();        
        if(!bridgeHandler.isEmpty()){             
            bridgeHandler.beforeFirst();    
            boolean alreadyPrintHeader=false;
            while ( bridgeHandler.next() ) {             
//                boObject extAttr = bridgeHandler.edit().getObject();      
                boObject extAttr = bridgeHandler.getObject();
                long attributeType = extAttr.getAttribute("attributeType").getValueLong();                
                long cadinalidade =  extAttr.getAttribute("attributeCardinal").getValueLong();
                
                if ( !alreadyPrintHeader )
                {
                    out.print("<table align=top class='section' ");
                    out.print(" cellSpacing='0' cellPadding='3' width='100%'><COLGROUP/><COL width='120' /><COL /><COL style=\"PADDING-LEFT: 5px\" width='70' /><COL /><tbody>");
                    alreadyPrintHeader=true;
                }
                if(attributeType == 0){
                    if(cadinalidade == 1)
                    {
                        out.print("<tr>");        
                        StringBuffer toPrint = new StringBuffer();     
                        renderObject(toPrint,extAttr,actionObject,DOC,true);                    
                        out.print(toPrint.toString());
                        out.print("</tr>");
                    }
                    else
                    {   
                        out.print("<tr><td colspan=4 height=190px >");
                        String iframe = "<div class=extendList><IFRAME id='inc_" + extAttr.getName() + "__" +extAttr.getBoui() + "__valueList' src='__extendAttributeList.jsp?docid="+IDX+"&method=list&parent_attribute=valueList&parent_boui="+extAttr.getBoui()+"' frameBorder='0' width='100%'  scrolling=no height='180px'  ></IFRAME></div>";                                        
                        out.print(iframe);                                        
                        out.print("\n");
                        out.print("</td></tr>");
                    }                
                }
                else 
                {                    
                    if(attributeType == 9)
                    {
                        out.print("<tr height=60px>");
                    }
                    else
                    {
                        out.print("<tr>");   
                    }                    
                    StringBuffer toPrint = new StringBuffer();     
                    renderObject(toPrint,extAttr,actionObject,DOC,true);                    
                    out.print(toPrint.toString());
                    out.print("</tr>");
                }                
            }
            if ( alreadyPrintHeader )
            {
                out.print("</tbdoy></table>");
            }
            
        }         
    }
    public final static String getExtendAttributeName(boObject extAttr) throws boRuntimeException 
    {
        String result = null;
        long cadinalidade =  extAttr.getAttribute("attributeCardinal").getValueLong();
        int attributeType = Integer.parseInt(extAttr.getAttribute("attributeType").getValueString());
        
        switch(attributeType) {
            case 0: if(cadinalidade == 1) return "valueObject";
                    else return "valueList";

            case 1: return "valueBoolean";

            case 4: return "valueNumber";

            case 5: return "valueDateTime";

            case 6: return "valueDate";

            case 9: return "valueText";

            case 12: return "valueLov";

            
        }
        return result;
    }
    public static void renderObject(StringBuffer toPrint,boObject extAttr,boObject actionObject,docHTML doc, boolean renderLabel)throws boRuntimeException 
    {
        long attributeType = extAttr.getAttribute("attributeType").getValueLong();
        String attrName = getExtendAttributeName(extAttr);
        AttributeHandler attrHandler = extAttr.getAttribute(attrName);
        
        Hashtable attributes = new Hashtable();            
        boolean changed = "0".equals(extAttr.getAttribute("changed").getValueString()) ? false : true;        
        boObject boDef = extAttr.getAttribute("object").getObject();                
        String valueObject = "";
        if(extAttr.getAttribute(attrName).getValueString() != null)
        {
            valueObject = extAttr.getAttribute(attrName).getValueString();
        }
        int definition =  Integer.parseInt(extAttr.getAttribute("attributeConstraints").getValueString());        
        int require =  Integer.parseInt(extAttr.getAttribute("attributeRequire").getValueString());                
        StringBuffer nameH = new StringBuffer();
        StringBuffer id = new StringBuffer();
        nameH.append( extAttr.getName() ).append( "__" ).append( extAttr.bo_boui ).append("__").append( attrName );
        id.append("tblLook").append( extAttr.getName() ).append( "__" ).append( extAttr.bo_boui ).append("__").append( attrName );        
        boolean stateClose = false;
        if(actionObject != null)
        {
            boObjectStateHandler pstate=actionObject.getStateAttribute( "primaryState" );
            if (pstate!=null && pstate.getValueString().equals("close")){        
                stateClose = true;
            }
        }        
        
        
        boolean isDisabled = false;
        boolean isVisible = true;
        boolean inEditTemplate=false;
        boolean isRequired = false;
        boolean isRecommend = false;       

        
        switch(definition) {
            case 1:         
                
                //writeHTML_lookup(new StringBuffer( boDef.getAttribute("name").getValueString()),toPrint,extAttr,extAttr.getAttribute("valueObject"),new StringBuffer( valueObject ),nameH,id,1,doc,!changed,true,false,false,false,stateClose,require,attributes);
                isDisabled=!changed;
                if(stateClose) isDisabled = true;
                isVisible=true;
                isRequired=false;
                isRecommend=false;
                break;
            case 2:                
//                writeHTML_lookup(new StringBuffer( boDef.getAttribute("name").getValueString()),toPrint,extAttr,extAttr.getAttribute("valueObject"),new StringBuffer(valueObject),nameH,id,1,doc,!changed,true,false,false,false,stateClose,require,attributes);
                    isDisabled=!changed;
                    if(stateClose) isDisabled = true;
                    isVisible=true;
                    isRequired=false;
                    isRecommend=false;

                break;
            case 3:                
                if(!"".equals(valueObject) && !changed)
                {
                    //writeHTML_lookup(new StringBuffer( boDef.getAttribute("name").getValueString()),toPrint,extAttr,extAttr.getAttribute("valueObject"),new StringBuffer(valueObject),nameH,id,1,doc,true,true,false,true,true,stateClose,require,attributes);
                    isDisabled=true;
                    isVisible=true;
                    isRequired=true;
                    isRecommend=true;

                    break;
                }
                else
                {
//                   writeHTML_lookup(new StringBuffer( boDef.getAttribute("name").getValueString()),toPrint,extAttr,extAttr.getAttribute("valueObject"),new StringBuffer(valueObject),nameH,id,1,doc,false,true,false,true,true,stateClose,require,attributes);                        
                    isDisabled=false;
                    if(stateClose) isDisabled = true;
                    isVisible=true;
                    isRequired=true;
                    isRecommend=true;
                    break;
                }        
            case 4:     
//                 writeHTML_lookup(new StringBuffer( boDef.getAttribute("name").getValueString()),toPrint,extAttr,extAttr.getAttribute("valueObject"),new StringBuffer(valueObject),nameH,id,1,doc,!changed,true,false,false,false,stateClose,require,attributes);                
                isDisabled=!changed;
                if(stateClose) isDisabled = true;
                isVisible=true;
                isRequired=false;
                isRecommend=false;                
                break;
        }
        
        if ( renderLabel)
        {
          toPrint.append("<TD>");
          toPrint.append("<label ");                
          if(stateClose)
          {
              toPrint.append(" disabled ");
          }
          if(isRequired || require ==1)
          {
              toPrint.append(" class=req ");
          }
          else if( require == 2)
          {
              toPrint.append(" class=req STYLE ='color:green' ");    
          }
          toPrint.append(" for='"+ nameH +"'>");       
          toPrint.append(extAttr.getAttribute("alias").getValueString());
          toPrint.append("</label></TD>");                
          toPrint.append("<td colspan='3'>");
          
        }
        if(attributeType == 0)
        {
                    
          writeHTML_lookup(
            new StringBuffer( boDef.getAttribute("name").getValueString()),
            toPrint,
            extAttr,
            extAttr.getAttribute(attrName),
            new StringBuffer(valueObject),
            nameH,
            id,
            1,
            doc,
            isDisabled,
            isVisible,
            false,
            isRequired,
            isRecommend,
            stateClose,
            require,
            attributes
          );
        }
        else if(attributeType == 1)
        {
            docHTML_renderFields.writeHTML_forBoolean(toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""),
                                            isRequired,
                                            isRecommend,
                                            attributes
                                            );                                                                 
        }
        else if(attributeType == 4)
        {
         /*docHTML_renderFields.writeHTML_forNumber(toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            new StringBuffer(attrHandler.getDefAttribute().getType().toString()),
                                            new StringBuffer(String.valueOf(attrHandler.getDefAttribute().getDecimals())),
                                            new StringBuffer("0"),
                                            //new StringBuffer(String.valueOf(attrHandler.getDefAttribute().getGrouping())),
                                            false,    
                                            new StringBuffer("99999999"),
                                            new StringBuffer("-99999999"),
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""),
                                            isRequired,
                                            isRecommend,
                                            attributes
                                            );    */   
         String decimals = extAttr.getAttribute("decimals").getValueString();         
         if(decimals == null || "".equals(decimals))
         {
             decimals = "0";
         }
         String minDecimals =  extAttr.getAttribute("minDecimals").getValueString();
         if(minDecimals == null || "".equals(minDecimals))
         {
             minDecimals = "-99999999";
         }
         String  maxNumber = extAttr.getAttribute("maxNumber").getValueString();
         if(maxNumber == null || "".equals(maxNumber))
         {
             maxNumber = "99999999";
         }         
         String  minNumber = extAttr.getAttribute("minNumber").getValueString();   
         if(minNumber == null || "".equals(minNumber))
         {
             minNumber = "-99999999";
         }           
         String grouping = extAttr.getAttribute("grouping").getValueString();
         if(grouping == null || "".equals(grouping))
         {
             grouping = "0";
         }         
         docHTML_renderFields.writeHTML_forNumber(toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            new StringBuffer(attrHandler.getDefAttribute().getType().toString()),
                                            new StringBuffer(decimals),
                                            new StringBuffer(minDecimals),
                                            ("0".equals(grouping)) ?  false :  true,    
                                            new StringBuffer(maxNumber),
                                            new StringBuffer(minNumber),
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""),
                                            isRequired,
                                            isRecommend,
                                            attributes
                                            );                                                                                                     
                                            
        }
        else if(attributeType == 5)
        {
            docHTML_renderFields.writeHTML_forDateTime(toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            isDisabled,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""),
                                            isRequired,
                                            isRecommend,
                                            attributes
                                            );                                                                 
        }       
        else if(attributeType == 6)
        {
            docHTML_renderFields.writeHTML_forDate(toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""),
                                            isRequired,
                                            isRecommend,
                                            attributes
                                            );                                                                 
        }           
        else if(attributeType == 9)
        {                
        docHTML_renderFields.writeHTML_text(toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""),
                                            isRequired,
                                            isRecommend,
                                            attrHandler.getDefAttribute().getLen(),
                                            attributes
                                            );
        }        
        else if(attributeType == 12)
        {
            String[] values = null;  
            long lovBoui = extAttr.getAttribute("lov").getValueLong();
            boObject lov = null;
            if(lovBoui != 0)
            {
                lov = boObject.getBoManager().loadObject(actionObject.getEboContext(),lovBoui);
            }
            
            lovObject lovHandler=null;
            if(lov != null)
            {
                 lovHandler = LovManager.getLovObject( actionObject.getEboContext(), lov.getAttribute("name").getValueString() );
            }
            if ( lovHandler!=null)
            {
            
            
            docHTML_renderFields.writeHTML_forCombo(
                                            toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            lovHandler,
                                            false,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""),
                                            isRequired,
                                            isRecommend,
                                            attributes
                                            
                                            );
            }
        }   
        
          if(renderLabel)
          toPrint.append("</td>\n");
    }  
    public static void writeHTML_lookup(
        StringBuffer clss,
        StringBuffer toPrint,
        boObject objParent,
        AttributeHandler atrParent,
        StringBuffer Value,
        StringBuffer Name,
        StringBuffer id,
        int tabIndex,
        docHTML doc,
        boolean isDisabled ,
        boolean isVisible ,
        boolean inEditTemplate ,
        boolean isRequired,
        boolean isRecommend,        
        Hashtable xattributes
        )
        
        throws boRuntimeException{
        
            boObject extAttr = atrParent.getParent();
            boObject actionObject = extAttr.getParent();
            renderObject(toPrint,extAttr,actionObject,doc,false);
        }
    
    private static void writeHTML_lookup(
        StringBuffer clss,
        StringBuffer toPrint,
        boObject objParent,
        AttributeHandler atrParent,
        StringBuffer Value,
        StringBuffer Name,
        StringBuffer id,
        int tabIndex,
        docHTML doc,
        boolean isDisabled ,
        boolean isVisible ,
        boolean inEditTemplate ,
        boolean isRequired,
        boolean isRecommend,        
        boolean stateClose,
        int stateRequire,
        Hashtable xattributes
        )
        
        throws boRuntimeException{

        long xRefBoui=ClassUtils.convertToLong(Value,-1);
                
        toPrint.append("<table id='");
        toPrint.append("ext").append(id);
        toPrint.append("' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>");
        
        
        
            if(xRefBoui!=-1 && xRefBoui!=0)
            {
                toPrint.append("<div class='lu ro lui' ");
            }
            else
            {
                toPrint.append("<div style='overflow:hidden' class='lu ro lui' ");
            }
        
        toPrint.append("valido='");
         
        
         toPrint.append( clss );
        
         
        toPrint.append("' ><span class='lui' onclick=\"");

        toPrint.append("winmain().openDoc('medium','");
        if ( atrParent.getDefAttribute().getObjects() == null || Value.length()==0 )
        {            
            toPrint.append( clss.toString().toLowerCase() );
        }
        else
        {   
            long v=ClassUtils.convertToLong( Value );
            boObject o = null; 
            if(v!=0) o = doc.getObject( v );
            if (o != null) toPrint.append( o.getName().toLowerCase() );
        }
        toPrint.append("','edit','method=edit&boui=");
        toPrint.append(Value);
        toPrint.append("&actRenderObj=");
        toPrint.append(objParent.bo_boui);
        toPrint.append("&actRenderAttribute=");
        toPrint.append( atrParent.getName());
        toPrint.append("&actIdxClient='+getIDX()+'");
        toPrint.append("')\"");

        toPrint.append(" boui='");
        toPrint.append(Value);
        toPrint.append("' object='");
        toPrint.append(atrParent.getDefAttribute().getReferencedObjectName());
        toPrint.append("'>");

        
        if(xRefBoui!=-1 && xRefBoui!=0){
             boObject xref=objParent.getBoManager().loadObject(objParent.getEboContext(),xRefBoui );
             
             toPrint.append("<img  class='lui' title='");
             toPrint.append("Imagem representativa do objecto " );
             toPrint.append( xref.getBoDefinition().getLabel() );
             toPrint.append("' src='");             
             toPrint.append(xref.getBoDefinition().getSrcForIcon16());
             toPrint.append('\'');
             
             if ( !isVisible ) {
                toPrint.append(" style='display:none' ");
             }
             toPrint.append(" object='" );
             toPrint.append( xref.getName() );
             toPrint.append('\'');             
             toPrint.append(" boui='" );
             toPrint.append( xref.getBoui() );
             toPrint.append('\'');             
             toPrint.append(" width='16' height='16'/>");
             toPrint.append( docHTML_renderFields.buildCARDID(xref.getBoDefinition().getCARDID(),xref));              
             toPrint.append("</span>");
             
        }
         else
        {
            toPrint.append("</span>");
            if ( !isDisabled )
            {
            toPrint.append("<input style='width:100%;border:0'  onblur='this.parentElement.parentElement.parentElement.children[1].firstChild.fromInput(this);' />");
            }
        }
        
		toPrint.append("</div>");
        toPrint.append("</td>");
        toPrint.append("<td style='TEXT-ALIGN: right' width='25'><img class='lu' id style='CURSOR: default' tabIndex='");
        toPrint.append(tabIndex);
        
        if ( isDisabled || stateClose) {
                toPrint.append("' disabled src='templates/form/std/btn_dis_lookup.gif' lookupstyle='single' ");
        }
        else {
            if(atrParent.getDefAttribute().getRelationType()==boDefAttribute.RELATION_1_TO_1){
                toPrint.append("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='single' ");
            }
            else{
                toPrint.append("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='multi' ");
            }
            
            
            if ( !isVisible ) {
                    toPrint.append(" style='display:none' ");
            }
        }
        toPrint.append(" options='forWorkFlow'");
        toPrint.append(" shownew='");
        toPrint.append("1'");
        toPrint.append(" parentBoui='");
        toPrint.append(objParent.bo_boui);
        toPrint.append("' parentObj='");
        toPrint.append(objParent.getName());
        toPrint.append("' parentAttribute='");
        toPrint.append(atrParent.getName() );
        toPrint.append("' object='");
        toPrint.append(clss);
        //toPrint.append(atrParent.getDefAttribute().getReferencedObjectName());
        toPrint.append("'  docid='");
        toPrint.append(doc.getDocIdx());
        toPrint.append("' width='21' height='19'><input type='hidden' value='");
        toPrint.append(Value);
        toPrint.append("' name='");
        toPrint.append(Name);
        toPrint.append("' object='");
        toPrint.append(atrParent.getDefAttribute().getReferencedObjectName());
        toPrint.append("' req='");
        //boolean req = (atrParent.getDefAttribute().getRequired().toUpperCase().equals("Y")||atrParent.getDefAttribute().getRequired().toUpperCase().equals("YES")) ? true:false;
        boolean req = atrParent.required();
        
        if ( req ) toPrint.append(1);
        else  toPrint.append(0);
        toPrint.append("' boType='lu'>");
        
        
        toPrint.append("</td></tr></tbody></table>");
      
        
    }
    public static void renderList(PageContext pageContext,docHTML_controler DOCLIST,boObject extAttr,String parent_attribute,boolean stateClose)  throws boRuntimeException,IOException 
    {        
        int definition =  Integer.parseInt(extAttr.getAttribute("attributeConstraints").getValueString());        
        int stateRequire =  Integer.parseInt(extAttr.getAttribute("attributeRequire").getValueString());                   
        boolean changed = "0".equals(extAttr.getAttribute("changed").getValueString()) ? false : true;
        bridgeHandler bridge = extAttr.getBridge("valueList");        
        boObject boDef = extAttr.getAttribute("object").getObject();
        JspWriter out = pageContext.getOut();  
        
        
         
        
        
        
        out.print("<TABLE cellSpacing='0' cellPadding='0' style='height:100%;width:100%;table-layout:fixed'>");
        out.print("<!-- BEGIN MENU -->");
        out.print("	<TR height='24'><TD class=headerExtendList>");
        
        
        out.print("<label ");                
        if(stateClose)
        {
            out.print(" disabled ");
        }
        if(definition == 3 || stateRequire ==1)
        {
            out.print(" class=req ");
        }
        else if( stateRequire == 2)
        {
            out.print(" class=reqToComplete ");    
        }
        out.print(">");
        
        
        out.print(extAttr.getAttribute("alias").getValueString());
        
        
        
        
        
        out.print("</label></TD>");   
        
        out.print("	</TD></TR>");
        out.print("	<TR height='24'><TD>");
        out.print("	<table style='z-Index:1000' class='layout' cellSpacing='0' cellPadding='0'>");
        out.print("	<tbody>");
        out.print("	  <tr height='24'>");
        out.print("	  <td>");
        out.print("	    <table class='mnubarFlat' id='mnuBar1' cellSpacing='0' cellPadding='0'>");
        out.print("	    <tbody>");
        out.print("	       <tr>");
        out.print("          <td width='9'><img hspace='3' src='templates/menu/std/mnu_vSpacer.gif' WIDTH='5' HEIGHT='18'/></td>");
        out.print("          <td class='icMenu' noWrap='1'>");
        if(changed && !stateClose)
        {        
            out.print("          <span class='menu' tabIndex='0' accessKey='A'  menu='SUBMENU_ACCOES'  ><u>A</u>cções<table class='mnuList' id='SUBMENU_ACCOES' cellSpacing='0' cellPadding='3'>");
            out.print("          <colgroup/>");
            out.print("          <col class='mnuLeft'/>");
            out.print("          <col/><tbody>");
            out.print("          <tr tabIndex='0'  onclick=\"deleteSelected(16796536)\"  >");
            out.print("            <td>&nbsp;</td>");
            out.print("            <td class='mnuItm'>Apagar Selecção</td>");
            out.print("          </tr>          ");
            out.print("          </tbody></table>");
            out.print("          </span>");
        }
        out.print("          </td>");
        out.print("          <TD class='icMenu mnuRight' noWrap>");
        if(changed && !stateClose)
        {
            pageContext.setAttribute("otherJsp","__extendAttribute.jsp");
            out.print("             <SPAN class=menu title='LookupObjects(  , multi , "+boDef.getAttribute("name").getValueString()+", "+ extAttr.getName()+" , "+extAttr.getBoui()+" , "+parent_attribute+" , 1 )' onclick=\" LookupObjects('','multi','"+boDef.getAttribute("name").getValueString()+"','"+extAttr.getName()+"','"+extAttr.getBoui()+"','valueList','1')\" tabindex='0' ><IMG class=mnuBtn src='resources/boObject/ico16.gif'>Adicionar </SPAN>");
        }
        out.print("          </TD>");
        out.print("          <td class='mnuTitle mnuRight' id='tdTitle' noWrap='1'></td></tr>");
        out.print("      </tbody></table><div class='barInterval'></div></td></tr></tbody>");
        out.print("      </table></TD></TR> ");
        out.print("<!--END MENU -->");
        
        out.print("	<TR>");
        out.print("	<TD style='height:100%;width:100%' >");
        out.print("	<DIV style='width:100%;height:100%;overflow-x:auto'>");
        out.print("	<TABLE style='height:100%;width:100%;' class='g_std' cellSpacing='0' cellPadding='0' width='100%'>");
        out.print("	<TBODY>");
        out.print("	<TR height='20'>");
        out.print("	<TD >");
        out.print("	<TABLE id='g5953458_body' onmouseover=\"so('g5953458');onOver_GridHeader_std(event);\" onmouseout=\"so('g5953458');onOut_GridHeader_std(event);\" onclick=\"so('g5953458');onClick_GridHeader_std(event);\" cellpadding='2' cellspacing='0' style=\"height:25px\" class='gh_std'>");
        out.print(" <COLGROUP/>");
        out.print(" <COL width='25'/>");
        out.print(" <COL width='20'/>");
        out.print(" <COL width='20'/>");
        out.print(" <COL width='20'/>");
        out.print(" <COL width='18'/>");
        out.print(" <COL width='2'/>");
        out.print(" <COL width='98' />");
        out.print(" <COL />");
        out.print(" <COL width='2'/>");
        out.print(" <COL width='15'/>");
        out.print(" <TBODY>");
        out.print("    <TR> ");
        out.print("        <TD class='gh_std' >&nbsp;</TD>");
        out.print("        <TD class='gh_std'><INPUT id='g16796536_check' class='rad' type='checkBox'/></TD> ");
        out.print("        <TD class='gh_std' >&nbsp;</TD> <TD class='gh_std' >&nbsp;</TD> ");
        out.print("        <TD class='gh_std' >&nbsp;</TD> <TD class='ghSep_std' >&nbsp;</TD>");
        out.print("        <TD id='g16796536_ExpanderParent' colspan=2 class='ghSort_std'>");
        /*
        out.print(extAttr.getAttribute("alias").getValueString());
        
        
        if(definition == 3 || stateRequire ==1)
        {
          //  out.print(" (Lista Obrigatória para gravar)");
        }
        else if( stateRequire == 2)
        {
            out.print(" (Lista Obrigatória para completar)");
        }        
        out.print("");*/
        out.print("        </TD> ");
        out.print("        <TD class='ghSep_std' >&nbsp;</TD>");
        out.print("        <TD class='gh_std' width='14'><img onclick='submitGrid();' title='Clique aqui para actualizar a lista' src='templates/grid/std/ghRefresh.gif' width='13' height='13' /></TD>");
        out.print("    </TR>");
        out.print(" </TBODY>");
        out.print(" </TABLE>");
        out.print(" </TD>");
        out.print(" </TR>");
        out.print("  <TR><TD><DIV id='g16796536_divc' class='gContainerLines_std'>  ");
        if(!bridge.isEmpty()){ 
            out.print("  <TABLE id='g16796536_body' container='1' onmouseover=\"so('g16796536');onOver_GridBody_std(event)\" onmouseout=\"so('g16796536');onOut_GridBody_std(event)\" onclick=\"so('g16796536');onClick_GridBody_std(event)\" cellpadding='2' cellspacing='0'  options=''  mode='normal'  letter_field='null'  class='gBodyLines_std'> ");
        }else{
            out.print("  <TABLE id='g16796536_body' style='height:100%' container='1' onmouseover=\"so('g16796536');onOver_GridBody_std(event)\" onmouseout=\"so('g16796536');onOut_GridBody_std(event)\" onclick=\"so('g16796536');onClick_GridBody_std(event)\" cellpadding='2' cellspacing='0'  options=''  mode='normal'  letter_field='null'  class='gBodyLines_std'> ");
        }
        out.print("  <COLGROUP/><COL width='25'/><COL width='20'/><COL width='20'/><COL width='20'/><COL width='20'/><COL />");

        boObject object = null;
        if(!bridge.isEmpty()){ 
            int i = 1;
            bridge.beforeFirst();
            while ( bridge.next() ) { 
                object = bridge.getObject();                   
                out.print("<TR isextendlist=true ondragleave='grid_DragLeave()' ondrop='grid_Drop()' ondragover='grid_DragOver()' ondragenter='grid_DragEnter()' id='16796536__"+object.getName()+"__"+object.getBoui()+"'>");
                
                out.print("    <TD style='' class='gCellNumber_std'><img width=18 title='Seleccione a linha e depois clique arraste para mover a linha'  class=\"numberLine\" ondragstart=\"grid_StartMoveLine()\"  height=16 lin="+ i +" src=resources/numbers/" + i++ +".gif></TD>");                               
                out.print("    <TD class='gCell_std'><INPUT class='rad' type='checkBox' name='"+object.getName()+"__"+ object.getBoui() +"'/></TD>");                                
                out.print("    <TD class='gCell_std'><IMG src='templates/grid/std/quickview.gif' width='13' height='13'/></TD>\n");                
                //out.print("    <TD class='gCell_std'><IMG title='Imagem representativa do objecto Ficheiro' src='resources/"+object.getName()+"/ico16.gif' ondragstart=\"startDragObject( '"+object.getName()+"',"+object.getBoui()+","+object.exists()+",16796536 )\"  height='16' width='16'/></TD>");                                
                out.print("    <TD class='gCell_std'>&nbsp;</TD>");
                out.print("    <TD class='gCell_std'><IMG src='resources/none.gif' height=16 width=16 /></TD>");
                out.print("    <TD class='gCell_std'>");
                out.print(object.getCARDID().toString());
                out.print("    </TD>");        
                out.print("</TR> ");
            }     
        } else {
            out.print("<TR ondragleave='grid_DragLeave()' ondrop='grid_Drop()' ondragover='grid_DragOver()' ondragenter='grid_DragEnter()' id='16796536__"+ extAttr.getName()+"__"+ extAttr.getBoui() +"' exists=no>");            
            out.print("<TD select='none' COLSPAN='6'>");
            out.print("<TABLE id='g16796536' select='none' style='height:100%;width:100%;border:0px' morerecords='0'>");
            out.print("<TBODY>");
            out.print("<TR>");
            out.print("     <TD select='none' style='COLOR: #999999; BORDER:0px' align=center width='100%' >Não existem objectos do tipo "+ boDef.getAttribute("description").getValueString() +" com os parametros actuais</TD>");
            out.print("</TR>");
            out.print("</TBODY>");
            out.print("</TABLE>");
            out.print("</TD>");
            out.print("</TR> ");
        }         
        out.print("  </TABLE> ");
        out.print("  </DIV></TD></TR>");
        out.print("  </TBODY>");
        out.print("  </TABLE>");        
        out.print("</DIV></TD></TR>");  
        out.print("<script>window.recs=1;window.onload=actNumberOfArea</script>   ");
        out.print("</TABLE>");
    }
}