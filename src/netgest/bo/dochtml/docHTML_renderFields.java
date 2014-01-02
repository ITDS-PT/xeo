/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import java.util.Date;
import java.util.Hashtable;

import netgest.bo.controller.xwf.XwfController;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.localized.JSMessages;
import netgest.bo.localized.JSPMessages;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.presentation.manager.favoritesLookupManager;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.io.iFile;
import netgest.xwf.common.xwfHelper;
import netgest.utils.ClassUtils;

import xeo.client.business.helper.RegistryHelper;

public final class docHTML_renderFields  {
    public docHTML_renderFields() {
    }


    public static void writeHTML_forDuration(
        StringBuffer toReturn ,
        StringBuffer Value ,
        StringBuffer Name ,
        StringBuffer id ,
        int tabIndex ,
        StringBuffer[] comboValues ,
        boolean allowValueEdit ,  
        boolean isDisabled ,
        boolean isVisible ,
        boolean inEditTemplate ,
        StringBuffer onChange ,
        boolean isRequired,
        boolean isRecommend,
        boolean clock,
        Hashtable xattributes
        )
        
        {
        
        if ( clock && isVisible) 
        {
            toReturn.append("<table style='width:100%' cellpadding='0' cellspacing='0'><tr><td>");
        }
        toReturn.append("<INPUT class=duration type=hidden name='");
        toReturn.append(Name);
        toReturn.append("' id ='");
        toReturn.append(id);
        toReturn.append("' returnValue='");
        toReturn.append(Value);
        toReturn.append('\'');
        toReturn.append(" tabIndex='");
        toReturn.append(tabIndex);
        toReturn.append("'>");
        toReturn.append("<SPAN class='selectBox' name='");
        toReturn.append("_ignore_sControl");
        toReturn.append(Name);
        toReturn.append('\'');
        if ( isDisabled ){
            toReturn.append(" disabled  ");
        }
        //focus
        //if(isVisible && !isDisabled)
            //toReturn.append(getonfocus(Name.toString()));
            
        if ( onChange.length() >0 ) {
            toReturn.append(" changeHandler='");
            toReturn.append( onChange );
            toReturn.append('\'');
        }
        if ( !isVisible ) {
            toReturn.append(" style='display:none' ");
        }
        
        toReturn.append(" tabbingIndex='");
        toReturn.append(tabIndex);
        toReturn.append("' value='");
        toReturn.append( formatDuration(Value.toString()) );
        
        if(allowValueEdit){
            toReturn.append("' allowValueEdit='true' >");
        }
        
        toReturn.append("<TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2><TBODY>");
    
        for (int i = 0; i < comboValues.length ; i++)  {
            toReturn.append("<TR><TD val='");
            toReturn.append( comboValues[i] );
            toReturn.append("'>");
            toReturn.append( comboValues[i] );
            toReturn.append("</TD></TR>");
        }
        toReturn.append("</TBODY></TABLE></SPAN>");
        
        if ( clock && isVisible)
        {
        
            toReturn.append("</td><td width=25 align=right><IMG class='clock' ");
            toReturn.append(" id='clock_");
            toReturn.append(id);
            if ( xattributes.get("onactiveexecute")!=null )
            {
                toReturn.append("' onactiveexecute='");
                toReturn.append( xattributes.get("onactiveexecute") );
            }
            toReturn.append("' relatedID='");
            toReturn.append(id);
            if ( isDisabled  || "disabled".equalsIgnoreCase( (String)xattributes.get("defaultState") )  )   toReturn.append("' disable=true src='resources/clockrun.gif' width=21 height=19 /></td></td></tr></table>");
            else toReturn.append("' src='resources/clockrun.gif' width=21 height=19 /></td></td></tr></table>");
         
        }
      
        
    }
    
    public static void writeHTML_forDuration(
        StringBuffer toRet,
        StringBuffer Value,
        StringBuffer Name,
        StringBuffer id,
        int tabIndex,
        boolean allowValueEdit,
        boolean isDisabled ,
        boolean isVisible ,
        boolean inEditTemplate ,
        StringBuffer onChange ,
        boolean isRequired,
        boolean isRecommend,
        boolean clock,
        Hashtable xattributes
        )
        {
        StringBuffer[] combo={
        new StringBuffer("1 minuto"),
        new StringBuffer("5 minutos"),
        new StringBuffer("15 minutos"),
        new StringBuffer("30 minutos"),
        new StringBuffer("45 minutos"),
        new StringBuffer("1 hora"),
        new StringBuffer("1.5 horas"),
        new StringBuffer("2 horas"),
        new StringBuffer("3 horas"),
        new StringBuffer("3.5 horas"),
        new StringBuffer("4 horas"),
        new StringBuffer("4.5 horas"),
        new StringBuffer("5 horas"),
        new StringBuffer("5.5 horas"),
        new StringBuffer("6 horas"),
        new StringBuffer("6.5 horas"),
        new StringBuffer("7 horas"),
        new StringBuffer("7.5 horas"),
        new StringBuffer("8 horas"),
        new StringBuffer("1 dia"),
        new StringBuffer("2 dias"),
        new StringBuffer("3 dias")        
        }; 
        
        writeHTML_forDuration(toRet,Value,Name,id,tabIndex,combo,allowValueEdit,isDisabled,isVisible,inEditTemplate,onChange,isRequired,isRecommend,clock,xattributes);
    }
    

    public static StringBuffer formatDuration(String valueMinutes){
            StringBuffer toRet=new StringBuffer();
            int iMinutes;
            iMinutes=ClassUtils.convertToInt(valueMinutes,0);
            if( iMinutes < 0){iMinutes = 0;}

            if(iMinutes < 60)
            {
                if(iMinutes == 1)
                {
                    toRet.append("1 minuto");
                }
                else
                {
                    toRet.append(iMinutes);
                    toRet.append(" minutos");
                }
            } else if( iMinutes >= 60 && iMinutes < 1440 )
            {
                int iHours =  iMinutes / 60;

                if(iHours == 1)
                {
                    toRet.append("1 hora");
                }
                else
                {
                    toRet.append(formatFloat( ""+iHours ));
                    toRet.append(" horas");
            
                }
            } else if( iMinutes >= 1440 )
            {
                int iHours = iMinutes / 60;
                int iDays = iHours / 24;

                if(iDays == 1)
                {
                    toRet.append(formatFloat( ""+iDays ));
                    toRet.append(" dia");
                }
                else
                {
                    toRet.append(formatFloat( ""+iDays ));
                    toRet.append(" dias");
                }
            }

	

            

            return toRet;
    }

    private static String formatFloat(String sNum) {
        StringBuffer  sBase= new StringBuffer();
        StringBuffer  sRem =new StringBuffer();
        boolean bRem = false;
        char curChar;

        for(int i = 0; i < sNum.length(); i++)
        {

            curChar = sNum.charAt(i);

            if(bRem)
            {
                sRem.append(curChar);
                if(sRem.length() == 2) break;
            }
            else
            {
                sBase.append(curChar);
            }

            if(curChar == '.')
            {
                bRem = true;
                continue;
            }
        }
    	return sBase.toString() + sRem.toString();
    }   

    public static void writeHTML_forDate(
            StringBuffer toPrint ,
            StringBuffer Value,
            StringBuffer Name,
            StringBuffer id,
            int tabIndex,
            boolean isDisabled ,
            boolean isVisible ,
            boolean inEditTemplate ,
            StringBuffer onChange ,
            boolean isRequired,
            boolean isRecommend,
            Hashtable xattributes
            )
            
            {
    
          toPrint.append("<TABLE style='TABLE-LAYOUT: fixed' cellSpacing=0 cellPadding=0 " );
          toPrint.append("width='100%'>");
          toPrint.append("<COLGROUP>");

          toPrint.append("<COL>");
          toPrint.append("<COL width=40>");
          
          
          toPrint.append("<TBODY>");
          toPrint.append("<TR>");

          toPrint.append("<TD><INPUT class=dtm maxLength=10");
          
          //focus
          if(isVisible && !isDisabled)
            toPrint.append(getonfocus(Name.toString()));
        
          if ( onChange.length() >0 ) {
             toPrint.append(" onreturnvaluechange='");
             toPrint.append( onChange );
             toPrint.append('\'');
          }  
          
          if ( isDisabled ) {           
             toPrint.append(" disabled  ");
          }
          
          if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
          }

           
          toPrint.append(" tabindex = '");
          toPrint.append(tabIndex);
          toPrint.append("' name = '");
          toPrint.append(Name);
          toPrint.append("' id = '");
          toPrint.append(id);
          toPrint.append("' boType='dtm' returnValue='");
          toPrint.append(Value);
           toPrint.append("' value='");
          toPrint.append(Value);
          toPrint.append("'/></TD>");
          toPrint.append("<TD style='PADDING-LEFT: 4px'><IMG class=dtm ");
          if ( isDisabled ) {
             toPrint.append(" dtmdisabled=true");
             toPrint.append(" src='templates/form/std/button_dis_Cal.gif' />");
          }
          else
          {
            toPrint.append(" dtmdisabled=false");
            toPrint.append(" src='templates/form/std/btn_off_Cal.gif' />");
          }
          if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
          }
          
          
          toPrint.append("</TD></TR></TBODY></TABLE>");
    }
    
/*
<TABLE cellSpacing=0 cellPadding=0>
                    <TBODY>
                    <TR>
                      <TD><INPUT class=rad id=crmFormdonotemail0 tabIndex=170 
                        type=radio value=0 name=crmFormdonotemail></TD>
                      <TD class=radioLabel><LABEL 
                        for=crmFormdonotemail0>Allow</LABEL></TD>
                      <TD><INPUT class=rad id=crmFormdonotemail1 tabIndex=171 
                        type=radio value=1 name=crmFormdonotemail></TD>
                      <TD class=radioLabel><LABEL for=crmFormdonotemail1>Do 
                        Not Allow</LABEL></TD></TR></TBODY></TABLE><INPUT type=hidden 
                  value=0 name=donotemail>
                  */
    
    public static void writeHTML_forBoolean(
            StringBuffer toPrint ,
            StringBuffer Value,
            StringBuffer Name,
            StringBuffer id,
            int tabIndex,
            boolean isDisabled ,
            boolean isVisible ,
            boolean inEditTemplate ,
            StringBuffer onChange ,
            boolean isRequired,
            boolean isRecommend,
            Hashtable xattributes
            )
            
            {
    
           toPrint.append("<TABLE cellSpacing=0 cellPadding=0 ");
           if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
           }

           toPrint.append("><TBODY>");
           toPrint.append("<TR>");
           toPrint.append("<TD><INPUT class=rad id=");
           toPrint.append(id+"0");
           toPrint.append(" tabindex=");
           toPrint.append( tabIndex ) ;
           toPrint.append(" type=radio value='1' name=_ignore_");
           toPrint.append(Name);
           if ( isDisabled ) {
             toPrint.append(" disabled ");
           }
           toPrint.append("></TD>");
           toPrint.append("<TD class=radioLabel><LABEL for="); 
           toPrint.append(id+"0");
           toPrint.append(">")
           .append( JSPMessages.getString( "docHTML_renderFields.1" ))
           .append("</LABEL></TD>");
           
           toPrint.append("<TD><INPUT class=rad id=");
           toPrint.append(id+"1");
           toPrint.append(" tabindex=");
           toPrint.append( tabIndex ) ;
           
           //focus
            if(isVisible && !isDisabled)
                toPrint.append(getonfocus(Name.toString()));
           
           toPrint.append(" type=radio value='0' name=_ignore_");
           toPrint.append(Name);
           if ( isDisabled ) {
             toPrint.append(" disabled ");
           }
           if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
           }

           toPrint.append("></TD>");
           toPrint.append("<TD class=radioLabel><LABEL for="); 
           toPrint.append(id+"1");
           
           toPrint.append(">")
           .append( JSPMessages.getString( "docHTML_renderFields.2" ) )
           .append("</LABEL></TD>");
           
           toPrint.append("</TR></TBODY></TABLE><INPUT type=hidden "); 
           toPrint.append("value='");
           
           toPrint.append(Value);
           toPrint.append("' name=");
           toPrint.append(Name);
           if ( isDisabled ) {
             toPrint.append(" disabled ");
           }
           if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
           }

           toPrint.append(">");

          

           
          
    }
     public static void writeHTML_forBooleanAsCheck(
            String label ,
            StringBuffer toPrint ,
            StringBuffer Value,
            StringBuffer Name,
            StringBuffer id,
            int tabIndex,
            boolean isDisabled ,
            boolean isVisible ,
            boolean inEditTemplate ,
            StringBuffer onChange ,
            boolean isRequired,
            boolean isRecommend,
            Hashtable xattributes
            )
            
            {


           toPrint.append("<INPUT style='border:0' onclick='if(this.checked){this.value=1;this.original=0;}else{this.value=0;this.original=1;};");
           toPrint.append(onChange).append("'"); 
           toPrint.append(" id=");
           toPrint.append(id);
           toPrint.append(" tabindex=deb");
           toPrint.append( tabIndex ) ;
           toPrint.append(" type=checkbox ");
           
           if( Value.length()==0 ) Value=new StringBuffer("0");
           
           toPrint.append(" value=");
           toPrint.append(Value);
           if( Value.toString().equals("1") )
           {
            toPrint.append(" checked");    
           }

           
           toPrint.append(" original=");
           toPrint.append(Value);

           if ( onChange.length()>0 ){ 
               toPrint.append(" onchange='");
               toPrint.append(onChange);
               toPrint.append('\'');
           }
           
           toPrint.append("  name=");
           toPrint.append(Name);
           if ( isDisabled ) {
             toPrint.append(" disabled ");
           }
           if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
           }
           toPrint.append(" />");
           toPrint.append("<LABEL ");
           if(isRequired)
           {
               toPrint.append(" class=req ");    
           }
           toPrint.append(" for="); 
           toPrint.append(id);
           if ( !isVisible )
           {
             toPrint.append(" style='display:none' ");
           }
           toPrint.append(">").append( label ).append("</LABEL>");

           

          
    }

    public static void writeHTML_forDateTime(
            StringBuffer toPrint,
            StringBuffer Value,
            StringBuffer Name,
            StringBuffer id,
            int tabIndex,
            boolean allowValueEdit,
            boolean isDisabled ,
            boolean isVisible ,
            boolean inEditTemplate ,
            StringBuffer onChange,
            boolean isRequired,
            boolean isRecommend,
            Hashtable xattributes
            )
            {

          toPrint.append("<TABLE style='TABLE-LAYOUT: fixed' cellSpacing=0 cellPadding=0 " );
          toPrint.append("width='100%'>");
          toPrint.append("<COLGROUP/>");
        
          toPrint.append("<COL ='100%'/>");
          toPrint.append("<COL width=40/>");
          toPrint.append("<COL width=70/>");
          toPrint.append("<TBODY>");
          toPrint.append("<TR>");

          toPrint.append("<TD><INPUT class=dtm maxLength=10 ");    
          toPrint.append(" tabindex='");
          toPrint.append(tabIndex);
          toPrint.append("' ");
          
          //focus
          if(isVisible && !isDisabled)
            toPrint.append(getonfocus(Name.toString()));
          
          if ( onChange.length() >0 ) {
             toPrint.append(" onreturnvaluechange='");
             toPrint.append( onChange );
             toPrint.append('\'');
          }  
          
          if ( isDisabled ) {
             toPrint.append(" disabled  ");
          }
          
          if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
          }
          
          toPrint.append(" name = '");
          toPrint.append(Name);
          toPrint.append("' id = '");
          toPrint.append(id);
          toPrint.append("' value='");
          if(Value.length()>10)   toPrint.append(Value.substring(0,10));
          else toPrint.append(Value);
          toPrint.append("' returnValue='");
          toPrint.append(Value);
          toPrint.append("' /></TD>");
          toPrint.append("<TD style='PADDING-LEFT: 4px'><IMG class=dtm id='id");
          toPrint.append(Name);
          toPrint.append('\'');
          if ( isDisabled ) {
             toPrint.append(" dtmdisabled=true ");
             toPrint.append(" src='templates/form/std/button_dis_Cal.gif' />");
          }
          else{
             toPrint.append(" dtmdisabled=false ");
             toPrint.append(" src='templates/form/std/btn_off_Cal.gif'></TD>");
          }
          if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
          }
          
          
          toPrint.append("<TD style='PADDING-LEFT: 4px'><DIV class=timeedit timeVisible='True' TimeFormat='2'><SPAN class=selectBox "); 
          toPrint.append(" value='");
          String arr[]=ClassUtils.splitToArray(Value.toString()," ");
          if( arr.length >1)toPrint.append(arr[1]);
          else toPrint.append(" ");
          toPrint.append("' name = '_ignore_");
          toPrint.append(Name);
          toPrint.append('\'');
          
          if ( isDisabled ) {
             toPrint.append(" disabled  ");
          }
          
          if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
          }
          
          toPrint.append(" id = 'idtime");
          toPrint.append(Name);
          toPrint.append("' tabbingIndex=");
          toPrint.append(tabIndex);
          if( allowValueEdit) toPrint.append(" allowValueEdit='true' ");
          //else toPrint.append(" allowValueEdit='false' ");

          toPrint.append(" setdisabled='1' >");
          toPrint.append("<TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2>");
          toPrint.append("<TBODY>");
          
          /*
          toPrint.append("<TR><TD val='12:00'>12:00</TD></TR>");
          
          toPrint.append("<TR><TD val='1:30 '>1:30 ,1:30 </TD></TR><TR><TD val='2:00 '>2:00 ,2:00 </TD></TR>");
          toPrint.append("<TR><TD val='2:30 '>2:30 ,2:30 </TD></TR><TR><TD val='3:00 '>3:00 ,3:00 </TD></TR>");
          toPrint.append("<TR><TD val='3:30 '>3:30 ,2:30 </TD></TR><TR><TD val='4:00 '>4:00 ,4:00 </TD></TR>");
          toPrint.append("<TR><TD val='4:30 '>4:30 ,2:30 </TD></TR><TR><TD val='5:00 '>5:00 ,5:00 </TD></TR>");
          toPrint.append("<TR><TD val='5:30 '>5:30 ,2:30 </TD></TR><TR><TD val='6:00 '>6:00 ,6:00 </TD></TR>");
          toPrint.append("<TR><TD val='6:30 '>6:30 ,2:30 </TD></TR><TR><TD val='7:00 '>7:00 ,7:00 </TD></TR>");
          toPrint.append("<TR><TD val='7:30 '>7:30 ,2:30 </TD></TR><TR><TD val='8:00 '>8:00 ,8:00 </TD></TR>");
          toPrint.append("<TR><TD val='8:30 '>8:30 ,2:30 </TD></TR><TR><TD val='9:00 '>9:00 ,9:00 </TD></TR>");
          toPrint.append("<TR><TD val='9:30 '>9:30 ,2:30 </TD></TR><TR><TD val='10:00 '>10:00 ,10:00 </TD></TR>");
          toPrint.append("<TR><TD val='10:30 '>10:30 ,10:30 </TD></TR><TR><TD val='11:00 '>11:00 ,11:00 </TD></TR>");
          toPrint.append("<TR><TD val='11:30 '>11:30 ,11:30 </TD></TR>");
          */
          //toRet[i++]="<TR><TD val='"+h+":"+m+"'>"+h+":"+m+"</TD></TR>";
          for (int j=0;j<24;j++)
            {
				toPrint.append("<TR><TD val='"+j+":00'>"+j+":00</TD></TR>");
				toPrint.append("<TR><TD val='"+j+":30'>"+j+":30</TD></TR>");
            }
          toPrint.append("</TBODY></TABLE></SPAN></DIV></TD></TR></TBODY></TABLE>");
          
          if( inEditTemplate ){
            //toPrint.append("<img src='templates/form/std/iconformula_on.gif' class='imgonoff' />");
          }
                             
    }
    
//    public static void writeHTML_forCombo(
//          StringBuffer toPrint , 
//          StringBuffer Value ,
//          StringBuffer Name,
//          StringBuffer id, 
//          int tabIndex, 
//          bridgeHandler lovdetails ,
//          boolean allowValueEdit,
//          boolean isDisabled ,
//          boolean isVisible ,
//          boolean inEditTemplate ,
//          StringBuffer onChange,
//          boolean isRequired,
//          boolean isRecommend,
//          Hashtable xattributes,
//          String[] values
//          ) 
//          throws boRuntimeException{
//          
//          toPrint.append("<SPAN class=selectBox "); 
//          toPrint.append(" name = '");
//          toPrint.append(Name);
//          toPrint.append("' id = '");
//          toPrint.append(id);
//          toPrint.append('\'');
//          
//          StringBuffer onchangeHandler=new StringBuffer();
//          
//          if ( isDisabled ){
//                toPrint.append(" disabled  ");
//          }
//          
//          //focus
//          if(isVisible && !isDisabled)
//            toPrint.append(getonfocus(Name.toString()));
//          
//          if ( onChange.length() >0 ) {
//                toPrint.append(" changeHandler='");
//                if ( onChange.toString().indexOf("(") > -1 )
//                {
//                    onchangeHandler.append( "<script> function hlv" );
//                    onchangeHandler.append( Name );
//                    onchangeHandler.append( "(){" );
//                    onchangeHandler.append( onChange );
//                    onchangeHandler.append( "}</script>");
//                    toPrint.append( "hlv" );
//                    toPrint.append( Name );
//                }
//                else
//                {
//                    toPrint.append( onChange );
//                }
//                toPrint.append('\'');
//          }
//          
//          if ( !isVisible ) {
//             toPrint.append(" style='display:none' ");
//          }
//        
//          toPrint.append(" tabbingIndex='");
//          toPrint.append(tabIndex);
//          if( allowValueEdit) toPrint.append("' allowValueEdit='true' ");
//          else toPrint.append("'");
//          toPrint.append(" value='");
//          toPrint.append(Value);
//          toPrint.append("'>");
//          
// 
//          toPrint.append("<TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2><TBODY>");
//
//          
//          lovdetails.beforeFirst();
//          // Ebo_LOVDetails det;
//          boObject det;
//          if(!isRequired)
//          {
//            printLovValue(toPrint, "", 
//                "&nbsp;");
//          }
//          boolean printed = false;
//          if( lovdetails.getRowCount() > 0){
//                String v = null;
//                while (lovdetails.next()){
//                   det = lovdetails.getObject();
//                    v = det.getAttribute("value").getValueString();
//                    if(values != null && values.length > 0)
//                    {
//                        if(toShowLovValue(values, v))
//                        {        
//                            printed = true;
//                            printLovValue(toPrint, det.getAttribute("value").getValueString(), 
//                                det.getAttribute("description").getValueString());  
//                        }
//                    }
//                    else
//                    {
//                        printed = true;
//                        printLovValue(toPrint, det.getAttribute("value").getValueString(), 
//                            det.getAttribute("description").getValueString());                        
//                    }                
//                }
//          }
//          if(!printed && isRequired)
//          {
//             printLovValue(toPrint, "", "&nbsp;");
//          }
//    
//          toPrint.append("</TBODY></TABLE>");
//          toPrint.append("</SPAN>");
//          if ( onchangeHandler.length() > 0 )
//          {
//            toPrint.append(onchangeHandler );    
//          }
//          
//          if(inEditTemplate){
//            //toPrint.append("<img src='templates/form/std/iconformula_on.gif' class='imgonoff' />");
//          }
//
//
//    }
public static void writeHTML_forCombo(
          StringBuffer toPrint , 
          StringBuffer Value ,
          StringBuffer Name,
          StringBuffer id, 
          int tabIndex, 
          lovObject lov_obj  ,
          boolean allowValueEdit,
          boolean isDisabled ,
          boolean isVisible ,
          boolean inEditTemplate ,
          StringBuffer onChange,
          boolean isRequired,
          boolean isRecommend,
          Hashtable xattributes
          ) 
          throws boRuntimeException{
          
          toPrint.append("<SPAN class=selectBox "); 
          toPrint.append(" name = '");
          toPrint.append(Name);
          toPrint.append("' id = '");
          toPrint.append(id);
          toPrint.append('\'');
          
          StringBuffer onchangeHandler=new StringBuffer();
          
          if ( isDisabled ){
                toPrint.append(" disabled  ");
          }
          
          //focus
          if(isVisible && !isDisabled)
            toPrint.append(getonfocus(Name.toString()));
          
          if ( onChange.length() >0 ) {
                toPrint.append(" changeHandler='");
                if ( onChange.toString().indexOf("(") > -1 )
                {
                    onchangeHandler.append( "<script> function hlv" );
                    onchangeHandler.append( Name );
                    onchangeHandler.append( "(){" );
                    onchangeHandler.append( onChange );
                    onchangeHandler.append( "}</script>");
                    toPrint.append( "hlv" );
                    toPrint.append( Name );
                }
                else
                {
                    toPrint.append( onChange );
                }
                toPrint.append('\'');
          }
          
          if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
          }
        
          toPrint.append(" tabbingIndex='");
          toPrint.append(tabIndex);
          if( allowValueEdit) toPrint.append("' allowValueEdit='true' ");
          else toPrint.append("'");
          toPrint.append(" value='");
          toPrint.append( ClassUtils.html2TextLov( Value.toString() ) );
          toPrint.append("'>");
          
 
          toPrint.append("<TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2><TBODY>");

          
          
          boObject det;
          if(!isRequired)
          {
            printLovValue(toPrint, "", 
                "&nbsp;");
          }
          boolean printed = false;
          if( lov_obj.getSize() > 0){
                String v = null;
                lov_obj.beforeFirst();
                while (lov_obj.next())
                {
                    printed = true;
                    printLovValue(toPrint,lov_obj.getCode() , lov_obj.getDescription() );                        
                                   
                }
          }
          if(!printed && isRequired)
          {
             printLovValue(toPrint, "", "&nbsp;");
          }
    
          toPrint.append("</TBODY></TABLE>");
          toPrint.append("</SPAN>");
          if ( onchangeHandler.length() > 0 )
          {
            toPrint.append(onchangeHandler );    
          }
          
          if(inEditTemplate){
            //toPrint.append("<img src='templates/form/std/iconformula_on.gif' class='imgonoff' />");
          }


    }

    private static void printLovValue(StringBuffer toPrint, String value, String desc)
    {
        toPrint.append("<TR><TD val='");
        toPrint.append(ClassUtils.html2TextLov(value));
        if(desc!= null && desc.length() > 0 && !"&nbsp;".equals(desc))
        {
            desc = ClassUtils.html2TextLov(desc);
            toPrint.append("' title = '"+ desc +"'>");
        }
        else
        {
            toPrint.append("'>");
        }
        
        toPrint.append(desc);
        toPrint.append("</TD></TR>");
    }
//
//    private static boolean toShowLovValue(String[] values, String value)
//    {        
//        for(int i = 0; i < values.length; i++)
//        {
//            if(value.equalsIgnoreCase(values[i]))
//                return true;
//        }
//        return false;
//    }    
/*
 * for (int i=0 ; i < displayValues.length ; i++ ){
               toPrint.append("<TR><TD val='");
               toPrint.append( internalValues[i] );
               toPrint.append("'>");
               toPrint.append( displayValues[i] );
               toPrint.append("<TD></TR>");                  
            }
 * */
    public static void writeHTML_forCombo(
        StringBuffer toPrint,
        StringBuffer Value,
        StringBuffer Name,
        StringBuffer id,
        int tabIndex,
        StringBuffer[] displayValues ,
        StringBuffer[] internalValues ,
        boolean allowValueEdit,
        boolean isDisabled ,
        boolean isVisible ,
        boolean inEditTemplate ,
        StringBuffer onChange,
        boolean isRequired,
        boolean isRecommend,
        Hashtable xattributes
        )
          throws boRuntimeException
        {
          
          toPrint.append("<SPAN class=selectBox "); 
          toPrint.append(" name = '");
          toPrint.append(Name);
          toPrint.append("' id = '");
          toPrint.append(id);
          toPrint.append('\'');
          
          StringBuffer onchangeHandler=new StringBuffer();
          
          if ( isDisabled ){
                toPrint.append(" disabled  ");
          }
          
          //focus
          if(isVisible && !isDisabled)
            toPrint.append(getonfocus(Name.toString()));
          
          if ( onChange.length() >0 ) {
                toPrint.append(" changeHandler='");
                if ( onChange.toString().indexOf("(") > -1 )
                {
                    onchangeHandler.append( "<script> function hlv" );
                    onchangeHandler.append( Name );
                    onchangeHandler.append( "(){" );
                    onchangeHandler.append( onChange );
                    onchangeHandler.append( "}</script>");
                    toPrint.append( "hlv" );
                    toPrint.append( Name );
                }
                else
                {
                    toPrint.append( onChange );
                }
                toPrint.append('\'');
          }
          
          if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
          }
        
          toPrint.append(" tabbingIndex='");
          toPrint.append(tabIndex);
          if( allowValueEdit) toPrint.append("' allowValueEdit='true' ");
          else toPrint.append("'");
          toPrint.append(" value='");
          toPrint.append(Value);
          toPrint.append("'>");
          
 
          toPrint.append("<TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2><TBODY>");

          
          if(!isRequired)
          {
            printLovValue(toPrint, "", 
                "&nbsp;");
          }
          
          for (int i=0 ; i < displayValues.length ; i++ ){
            printLovValue(toPrint, internalValues[i].toString(),
                displayValues[i].toString());                  
          }
    
          toPrint.append("</TBODY></TABLE>");
          toPrint.append("</SPAN>");
          if ( onchangeHandler.length() > 0 )
          {
            toPrint.append(onchangeHandler );    
          }
          
          if(inEditTemplate){
            //toPrint.append("<img src='templates/form/std/iconformula_on.gif' class='imgonoff' />");
          }
    }
    
    public static void writeHTML_forNumber(
            StringBuffer toPrint,
            StringBuffer Value,
            StringBuffer Name,
            StringBuffer id,
            int tabIndex,
            StringBuffer type,
            StringBuffer decimals,
            StringBuffer minDecimals,
            boolean grouping,
            StringBuffer max,
            StringBuffer min,
            boolean isDisabled ,
            boolean isVisible ,
            boolean inEditTemplate ,
            StringBuffer onChange  ,
            boolean isRequired,
            boolean isRecommend,
            Hashtable xattributes
            ){
            
          toPrint.append("<input class='num'  "); 
          toPrint.append(" name = '");
          toPrint.append(Name);
          toPrint.append("' id = '");
          toPrint.append(id);
          toPrint.append("' tabIndex='");
          toPrint.append(tabIndex);
          toPrint.append('\'');
          
           if ( isDisabled ){
            toPrint.append(" disabled  ");
          }
          
          //focus
          if(isVisible && !isDisabled)
            toPrint.append(getonfocus(Name.toString()));
          
          if ( onChange.length() >0 ) {
                toPrint.append(" onChange='this.Parse();");
                toPrint.append( onChange );
                toPrint.append('\'');
          }
          
          if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
          }
          
          String sValue = validateNumber(Value.toString());
          toPrint.append(" value='");
          toPrint.append(sValue);
          toPrint.append("' returnValue='");
          toPrint.append(sValue);
          toPrint.append("' ");
          toPrint.append(" dt='");
          toPrint.append(type);
          toPrint.append("' acc='");
          toPrint.append(decimals);
          toPrint.append("' minAcc='");
          toPrint.append(minDecimals);
          if (grouping) toPrint.append("' grp='true' ");
          else toPrint.append("' grp='false' ");
          toPrint.append(" max='");
          toPrint.append(max);
          toPrint.append("' min='");
          toPrint.append(min);
          toPrint.append("' >");
          
          if(inEditTemplate){
             //toPrint.append("<img src='templates/form/std/iconformula_on.gif' class='imgonoff' />");
          }

          /*
                <INPUT class=num tabIndex=135 maxLength=50 
                  name=numberofemployees returnValue="" dt="" acc="0" grp="true" 
                  max="1000000000" min="0">
                  */

    }

    public static String mergeAttributes(String text,boObject obj) throws boRuntimeException{
        StringBuffer toRet;
        if(text!=null &&obj.exists()){
            toRet=new StringBuffer();
            StringBuffer wordAttribute=new StringBuffer();
            
            //String[] arrstr=ClassUtils.splitToArray(text,"+");
            AttributeHandler xatr;
            final char tokenBegin='[';
            final char tokenEnd=']';
            final char escapeChar='\\';
            char ch[]=text.toCharArray();
            byte state=0;
            boolean toAdd=false;
            boolean inBuildExpr=false;
            boolean toProcess=false;
            char lastChar=' ';
            for (int i = 0; i < ch.length; i++)  {
                switch ( ch[i] ){
                    case tokenBegin:
                        if (lastChar==escapeChar){
                            toAdd=true;
                        }
                        else{
                            if ( !inBuildExpr ){
                                toAdd=false;
                                inBuildExpr=true;
                            }
                        }
                        break;
                    case tokenEnd:
                        if (lastChar==escapeChar){
                            toAdd=true;
                        }
                        else{
                            if ( inBuildExpr ){
                                toAdd=false;
                                inBuildExpr=false;
                                toProcess=true;
                            }
                            else toAdd=true;
                        }
                        break;
                    case escapeChar:
                        if(lastChar==escapeChar){
                            toAdd=true;
                        }
                        else{
                            toAdd=false;
                        }
                    break;
                    default:
                     toAdd=true;
                }
                
                if (inBuildExpr){
                    if(toAdd) wordAttribute.append(ch[i]);
                }
                else{
                    if( toProcess ){
                        
                        xatr=obj.getAttribute(wordAttribute.toString());
                        if(xatr!=null) toRet.append( xatr.getValueString() );
                        wordAttribute.setLength(0);
                    }
                    if(toAdd){
                        toRet.append(ch[i]);
                    }
                }
                lastChar=ch[i];

                
            }
        }
        else toRet=new StringBuffer("");
        return toRet.toString();
    

    }
    
    public static  String buildCARDID( String text,boObject obj)throws boRuntimeException {
        String toRet;
        if(text!=null){
            //toRet= mergeAttributes(text,obj);
            toRet= obj.getTextCARDID().toString();
        }
        else toRet=""+obj.bo_boui;
        return toRet;
    }
    private static StringBuffer getLookUpValidObjects(boDefAttribute boDefAttr)
    {
        StringBuffer result = new StringBuffer();
        boDefHandler[] xdfs = null;
        boDefHandler bodef = boDefAttr.getReferencedObjectDef();             
        if ( bodef.getName().equalsIgnoreCase("boobject") )
        {
            xdfs = boDefAttr.getObjects();
            if ( xdfs!=null )
            {
                for (int i = 0; i < xdfs.length ; i++) 
                {
                    result.append( xdfs[i].getName() );
                    if ( i+1 < xdfs.length ) result.append(';');
                }
            
            }
            xdfs = boDefAttr.getTransformObjects();
            if ( xdfs!=null )
            {
                for (int i = 0; i < xdfs.length ; i++) 
                {
                    result.append( xdfs[i].getName() );
                    if ( i+1 < xdfs.length ) result.append(';');
                }
            }                        
         }
         else
         {
            xdfs = boDefAttr.getTransformObjects();
            result.append( bodef.getName() );
            if ( xdfs != null )
            {
                if (  xdfs.length > 0 ) result.append(';');
             
                for (int i = 0; i < xdfs.length ; i++) 
                {
                    result.append( xdfs[i].getName() );
                    if ( i+1 < xdfs.length ) result.append(';');
                }
            }             
        }      
        return result;
    }
        private static boolean isDocumentValidForClientRender(EboContext ctx, String objClassName ,long documentBoui)throws boRuntimeException
        {
            boolean result = false;
            String  XeoWin32Client_address = ctx.getXeoWin32Client_adress();
            if(DocumentHelper.isDocument(objClassName) &&                
               XeoWin32Client_address != null &&
               RegistryHelper.isClientConnected(XeoWin32Client_address) &&
               DocumentHelper.canClientOpenExtension(ctx,documentBoui))   
            {
                result = true;
            }
            else if ( DocumentHelper.isDocument(objClassName) && isXeoControlActive( ctx ) ) 
            {
                if( documentBoui != -1 )
                {
                    boObject doc = boObject.getBoManager().loadObject( ctx, documentBoui );
                    iFile x = doc.getAttribute("file").getValueiFile();
                    if( x != null && x.getName().toLowerCase().endsWith(".doc") )
                    {
                        result= true;
                    }
                }
            }   
            return result;
      }
      
      private static final boolean isXeoControlActive( EboContext ctx )
      {
          boolean result = false;
          if( ctx.getRequest() != null )
          {
            if( ctx.getRequest().getCookies() != null )
            {
                for (int i = 0; i < ctx.getRequest().getCookies().length; i++) 
                {
                    if( "XeoControl".equals(ctx.getRequest().getCookies()[i].getName() ) )
                    {
                        result = true;
                        break;
                    }
                }
            }
          }
          return result;
      }
      
      
      public static void writeHTML_lookup(
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
        throws boRuntimeException
        {
            docHTML_renderFields.writeHTML_lookup(toPrint,
                                                  objParent,
                                                  atrParent,
                                                  Value,
                                                  Name,
                                                  id,
                                                  tabIndex,
                                                  doc,
                                                  isDisabled,
                                                  isVisible,
                                                  inEditTemplate,
                                                  isRequired,
                                                  isRecommend,
                                                  true,
                                                  xattributes);
        }
      
      public static void writeHTML_lookup(
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
        boolean showLink,
        Hashtable xattributes
        )        
        throws boRuntimeException
        {
            docHTML_renderFields.writeHTML_lookup(toPrint,
                                                  objParent,
                                                  atrParent,
                                                  Value,
                                                  Name,
                                                  id,
                                                  tabIndex,
                                                  doc,
                                                  isDisabled,
                                                  isVisible,
                                                  inEditTemplate,
                                                  isRequired,
                                                  isRecommend,
                                                  showLink,
                                                  xattributes, 
                                                  null,
                                                  null
                                                  );
        }
        
      public static void writeHTML_lookup(
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
        Hashtable xattributes,
        boDefAttribute boDefAttr,
        String lookupDetachField
        )
        
        throws boRuntimeException{
            writeHTML_lookup(
                toPrint,
                objParent,
                atrParent,
                Value,
                Name,
                id,
                tabIndex,
                doc,
                isDisabled ,
                inEditTemplate ,
                isRequired,
                isRecommend,
                true,
                xattributes,
                boDefAttr,
                lookupDetachField
                );
        }
      public static void writeHTML_lookup(
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
        boolean showLink,
        Hashtable xattributes,
        boDefAttribute boDefAttr,
        String lookupDetachField
        )
        
        throws boRuntimeException{

        boDefAttribute defAttribute = null;
        if(atrParent != null)
        {
            defAttribute = atrParent.getDefAttribute();
        }
        else
        {
            defAttribute = boDefAttr;
        }
        
        toPrint.append("<table id='");
        toPrint.append(id);
        toPrint.append("' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>");
        long xRefBoui=ClassUtils.convertToLong(Value,-1);
        if(isVisible)
        {            
            if(xRefBoui!=-1 && xRefBoui!=0)
            {
                toPrint.append("<div class='lu ro lui' ");
            }
            else
            {
                toPrint.append("<div style='overflow:hidden' class='lu ro lui' ");
            }
            toPrint.append("valido='");
            toPrint.append(getLookUpValidObjects(defAttribute));
        } 
        
        toPrint.append("' >");
        
        String objClassName = defAttribute.getReferencedObjectName();        
        
//        String  XeoWin32Client_address = doc.getEboContext().getXeoWin32Client_adress();        
//        if(!DocumentHelper.isDocument(objClassName) || 
//               !DocumentHelper.isMSWordFile( xRefBoui==-1||xRefBoui==0?null:doc.getObject( xRefBoui ) ) ||
//               XeoWin32Client_address == null ||
//               !RegistryHelper.isClientConnected(XeoWin32Client_address)
//            )  
        if(!isDocumentValidForClientRender(doc.getEboContext(),objClassName,xRefBoui))
        {
            if(boDefAttr == null)
            {
                writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible,showLink,xRefBoui,false);
            }
            else
            {
                writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible,showLink,xRefBoui,false,defAttribute);
            }                               
        }
        else
        {
            writeHTML_lookupDocument(toPrint,objParent,atrParent,Value,Name,tabIndex,doc,isDisabled,isVisible,showLink,xRefBoui,objClassName,false);
        }
        
        /*
        if(!DocumentHelper.isDocument(objClassName))           
        {
            if(boDefAttr == null)
            {
                writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible,xRefBoui,false);
            }
            else
            {
                writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible,xRefBoui,false,defAttribute);
            }
        }
        else // Documents
        {
            String  XeoWin32Client_address = objParent.getEboContext().getXeoWin32Client_adress();            
            
            if(DocumentHelper.isMSWordFile( xRefBoui==-1||xRefBoui==0?null:doc.getObject( xRefBoui ) ) && XeoWin32Client_address!=null && RegistryHelper.isClientConnected(XeoWin32Client_address))
            {
                writeHTML_lookupDocument(toPrint,objParent,atrParent,Value,Name,tabIndex,doc,isDisabled,isVisible,xRefBoui,objClassName,false);   
            }   
            else
            {
                writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible,xRefBoui,false);    
            }
        }
        */
		toPrint.append("</div>");
        toPrint.append("</td>");
        
        //botão de limpar campo
        String auxLookupStyle="single"; 
        if(!(defAttribute.getRelationType()==boDefAttribute.RELATION_1_TO_1 || defAttribute.getRuntimeMaxOccurs() <= 1))
        {
            auxLookupStyle="multi";
        }
        toPrint.append("<td style='");
        if(xRefBoui<=0 || isDisabled)
        {
            toPrint.append("display=none;");
        }
        toPrint.append("width=16px'>")
        .append("<img  title='Clique para limpar o campo' onclick='")
        .append(
            onclickRemoveButton(objParent != null ? objParent.getName():"", 
                objParent != null ? String.valueOf(objParent.getBoui()):"", 
                atrParent != null ? atrParent.getName():"",
                objClassName, auxLookupStyle, 
                String.valueOf(doc.getDocIdx())))
        .append("' border='0' src='");
        toPrint.append("templates/form/std/remove.gif' width='16px' height='16px'/></td>");
        
        
        // psantos ini 20061030
        // drag and drop de documentos
        // descomentar para activar o drag and drop
        /*
        boolean isBridge=atrParent.isBridge();
        if (!isDisabled) 
        {
          if (!isBridge)
          {
            boDefHandler bodef = defAttribute.getReferencedObjectDef();
            if(DocumentHelper.isDocument(bodef.getName()))
            {
                if (atrParent.getObject() != null)
                {
                    toPrint.append("<td id='tdDragDrop' width='25'>");
                    toPrint.append(" <iframe src = \"__gdSubmitFile.jsp?"+
                                   "method=edit&docid="+doc.getDocIdx()+"&"+
                                   "parent_boui="+objParent.getBoui() + "&"+
                                   "atrParent="+atrParent.getName() + "&"+
                                   "objParent="+objParent.getName() + "&"+
                                   "isNew= 0\"" +
                                   " height=25 width=25 SCROLLING=no MARGINWIDTH=0 MARGINHEIGHT=0>"); 
                    toPrint.append("</iframe>");
                    toPrint.append("</td>");
                }
                else
                {
                    toPrint.append("<td id='tdDragDrop' width='25'>");
                    toPrint.append(" <iframe src = \"__gdSubmitFile.jsp?"+
                                   "method=edit&docid="+doc.getDocIdx()+"&"+
                                   "parent_boui="+objParent.getBoui() + "&"+
                                   "atrParent="+atrParent.getName() + "&"+
                                   "objParent="+objParent.getName() + "&"+
                                   "isNew= 1\"" +
                                   " height=25 width=25 SCROLLING=no MARGINWIDTH=0 MARGINHEIGHT=0>"); 
                    toPrint.append("</iframe>");
                    toPrint.append("</td>");
                }
            }
          }
          else // isbridge
          {
              // nunca passa pro aqui o codigo é escrito pelo writeHTML_lookupN
          }
        }
        */
        //psantos fim 20061030
        
        //------------------------------
        
        boDefHandler bodef = defAttribute.getReferencedObjectDef();
       
//        if("Ebo_Document".equals(bodef.getName()) || "Ebo_WordTemplate".equals(bodef.getName()))
        if(DocumentHelper.isDocument(bodef.getName()))
        {
            writeHTML_menuOptions(toPrint,objParent,atrParent,Value,Name,tabIndex,doc,isDisabled,isVisible,inEditTemplate);
        }
        else
        {
            if((defAttribute.getType().indexOf("boObject") >= 0 || 
                defAttribute.getReferencedObjectDef().getBoCanBeOrphan() ||
                defAttribute.getReferencedObjectDef().getBoHaveMultiParent() ||
                defAttribute.hasTransformer() || defAttribute.getShowLookup())
                
                && !(!defAttribute.getReferencedObjectDef().getBoCanBeOrphan() &&
                    defAttribute.getReferencedObjectDef().getBoHaveMultiParent() &&
                    !defAttribute.getShowLookup())
                    
            )
            {
            toPrint.append("<td style='TEXT-ALIGN: right' width='25'><img class='lu' style='CURSOR: default' "); 
            //if(xRefBoui!=-1 && xRefBoui!=0 && !isDisabled)
            //{
                toPrint.append(" tabindex='");
                toPrint.append(tabIndex);
            //}
            
            if ( isDisabled ) {
                    toPrint.append("' disabled src='templates/form/std/btn_dis_lookup.gif' lookupstyle='single' ");
            }
            else {
                    if(defAttribute.getRelationType()==boDefAttribute.RELATION_1_TO_1 || defAttribute.getRuntimeMaxOccurs() <= 1){
                    toPrint.append("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='single' ");
                }
                else{
                    toPrint.append("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='multi' ");
                }
                
                
                if ( !isVisible ) {
                        toPrint.append(" style='display:none' ");
                }
            }
            }
            else
            {
                toPrint.append("<td style='TEXT-ALIGN: right' width='25'"); 
                if(xRefBoui == -1 || xRefBoui == 0)
                {
                    toPrint.append(" onclick=\"winmain().newPage(getIDX(),'")
                    .append(defAttribute.getReferencedObjectName().toLowerCase())
                    .append("','edit','method=new&object=")
                    .append(defAttribute.getReferencedObjectName())
                    .append("&parentBoui=").append(objParent.getBoui())
                    .append("&parentAttribute=").append(defAttribute.getName())
                    .append("&relatedClientId=").append(doc.getDocIdx() )
                    .append("&ctxParent=").append( objParent.getBoui() )
                    .append("&ctxParentIdx=").append( doc.getDocIdx() )
                    .append("&addToCtxParentBridge=").append( defAttribute.getName() )
                    .append("&docid=").append( doc.getDocIdx() )
                    .append("&searchClientIdx='+getIDX()+'');\">");
                    
                    toPrint.append("<img style='CURSOR: default' "); 
                    toPrint.append(" tabindex='");
                    toPrint.append(tabIndex);
                    if ( isDisabled ) {
                        toPrint.append("' disabled src='templates/form/std/btn_off_create.gif'");
                    }
                    else 
                    {
                        if(defAttribute.getRelationType()==boDefAttribute.RELATION_1_TO_1 || defAttribute.getRuntimeMaxOccurs() <= 1){
                            toPrint.append("' src='templates/form/std/btn_on_create.gif' lookupstyle='single'");
                        }
                        else{
                            toPrint.append("' src='templates/form/std/btn_on_create.gif' lookupstyle='multi' ");
                        }
                        if ( !isVisible ) {
                                toPrint.append(" style='display:none' ");
                        }
                    }
                }
                else
                {
                    if(!isDisabled)
                    {
                        toPrint.append(" onclick= document.getElementById(\"refreshframe\").contentWindow.BindToClean(\"" + Name +"\");>");
                    }
                    else
                    {
                        toPrint.append(">");
                    }
                    toPrint.append("<img style='CURSOR: default' "); 
                    toPrint.append(" tabindex='");
                    toPrint.append(tabIndex);
                    if ( isDisabled ) {
                        toPrint.append("' disabled src='templates/form/std/btn_off_remove.gif'");
                    }
                    else 
                    {
                        if(defAttribute.getRelationType()==boDefAttribute.RELATION_1_TO_1 || defAttribute.getRuntimeMaxOccurs() <= 1){
                            toPrint.append("' src='templates/form/std/btn_on_remove.gif' lookupstyle='single'");
                        }
                        else{
                            toPrint.append("' src='templates/form/std/btn_on_remove.gif' lookupstyle='multi' ");
                        }
                        if ( !isVisible ) {
                                toPrint.append(" style='display:none' ");
                        }
                    }
                }


            }
                 
            toPrint.append(" shownew='");
            toPrint.append("1'");
            if(lookupDetachField != null)
            {
                toPrint.append(" lookupDetachField='");
                toPrint.append(lookupDetachField);
                toPrint.append("'");
            }
            if(objParent != null)
            {
                toPrint.append(" parentBoui='").append(objParent.bo_boui).append("' ");
            }
            toPrint.append("  parentObj='");
            toPrint.append(objParent.getName());
            toPrint.append("' parentAttribute='");
            toPrint.append(defAttribute.getName() );
            toPrint.append("' object='");
            toPrint.append(defAttribute.getReferencedObjectName());
            toPrint.append("'  docid='");
            toPrint.append(doc.getDocIdx());
            toPrint.append("' width='21' height='19'><input original='" + Value + "' type='hidden' value='");
            toPrint.append(Value);
            toPrint.append("' name='");
            toPrint.append(Name);
            toPrint.append("' object='");
            toPrint.append(defAttribute.getReferencedObjectName());
            
            toPrint.append("' req='");
            
            boolean req = defAttribute.getRequired()!=null&&defAttribute.getRequired().getBooleanValue();
            if ( req ) toPrint.append(1);
            else  toPrint.append(0);
            
            toPrint.append("' boType='lu'>");
            if(atrParent != null)
            {
                toPrint.append( favoritesLookupManager.getHTMLFavorites( atrParent , Name ));
            }
            toPrint.append("</td>");
        }
        
        


        toPrint.append("</tr></tbody></table>");
        
        
        
        if(inEditTemplate){
            //toPrint.append("<img src='templates/form/std/iconformula_on.gif' class='imgonoff' />");
        }
        
    }
    
    private static String onclickRemoveButton(String parentObjName, String parentBoui, String parentAtt, String objName, 
        String lookupStyle, String docID)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("winmain().openDoc(\"tall\", \"")
        .append(objName)
        .append("\", \"\", \"\", \"lookup\", \"lookup")
        .append(lookupStyle)
        .append(".jsp?look_object=")
        .append(objName)
        .append("&showNew=false&docid=")
        .append(docID)
        .append("&fromSection=y&clientIDX=\"+getIDX()+\"")
        .append("&look_parentObj=")
        .append(parentObjName)
        .append("&look_parentBoui=")
        .append(parentBoui)
        .append("&searchString=b:&look_parentAttribute=")
        .append(parentAtt)
        .append("&look_action=lookupsingleupdateparam.jsp")
        .append("\");");
        return sb.toString();
    }
    
    public static void writeHTML_menuOptions(
            StringBuffer toPrint,
            boObject objParent,
            AttributeHandler atrParent,
            StringBuffer value,
            StringBuffer name,
            int tabIndex,
            docHTML doc,
            boolean isDisabled ,
            boolean isVisible ,
            boolean inEditTemplate            
            )throws boRuntimeException
    {
        if (!inEditTemplate)
        {
            AttributeHandler parentAttr = objParent.getAttribute("TEMPLATE");
            if(parentAttr != null)
            {
                long templateBoui = parentAttr.getValueLong();
                String objName = atrParent.getDefAttribute().getReferencedObjectName();
                if(value.length() == 0 || "".equals(value)) //N Preenchido
                {
                    if (templateBoui == 0) //No Template
                    {
                        if(!"Ebo_WordTemplate".equals(objName))
                        {
                            //Adicionar Ebo_Document
                            writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,false, "add","document","Adicionar novo Documento",java.net.URLEncoder.encode("SELECT "+objName+" WHERE 1=1"));
                            //Criar Ebo_Document
                            writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,false, "create","document","Criar novo Documento","");
                        }                        
                        //Adicionar Ebo_DocumentTemplate
//                        if(objParent.getBoDefinition().getBoSuperBo() == null)
//                        {
//                            writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,"add","template","Adicionar novo Template",java.net.URLEncoder.encode("SELECT Ebo_WordTemplate WHERE object IN (SELECT Ebo_ClsReg WHERE Ebo_ClsReg.name = '" + objParent.getName() + "')"));
//                        }
//                        else
//                        {
//                            writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,"add","template","Adicionar novo Template",java.net.URLEncoder.encode("SELECT Ebo_WordTemplate WHERE object IN (SELECT Ebo_ClsReg WHERE Ebo_ClsReg.name = '" + objParent.getBoDefinition().getBoSuperBo()+ "' or Ebo_ClsReg.name = '" + objParent.getName() + "')"));
//                        }
                    }
                    else //Com Template
                    {
                        //Adicionar Ebo_Document
                        writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,false, "add","document","Adicionar novo Documento",java.net.URLEncoder.encode("SELECT "+objName+" WHERE 1=1"));
                        //Criar Ebo_Document
                        writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,false, "create","document","Criar novo Documento","");                                                
                        //Adicionar Ebo_DocumentTemplate
//                        writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,"add","template","Adicionar novo Template","SELECT Ebo_Template.documentTemplates WHERE Ebo_Template.BOUI = " + templateBoui);                        
                    }
                }
                else // Preenchido
                {
                    boObject obj = boObject.getBoManager().loadObject(objParent.getEboContext(),Long.parseLong(value.toString()));
                    if (templateBoui == 0) //No Template
                    {
                        //Adicionar Ebo_Document
                        writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,false, "add","document","Adicionar novo Documento",java.net.URLEncoder.encode("SELECT "+objName+" WHERE 1=1"));                                                                
                        //Criar Ebo_Document
//                        writeHTML_lookupAction(toPrint,objParent,atrParent,value,name,tabIndex,doc,isDisabled,isVisible,"create","Criar novo Documento","");                        
                        //Adicionar Outro Ebo_DocumentTemplate
//                        if(objParent.getBoDefinition().getBoSuperBo() == null)
//                        {
//                            writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,"add","template","Adicionar novo Template",java.net.URLEncoder.encode("SELECT Ebo_WordTemplate WHERE object IN (SELECT Ebo_ClsReg WHERE Ebo_ClsReg.name = '" + objParent.getName() + "')"));
//                        }
//                        else
//                        {
//                            writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,"add","template","Adicionar novo Template",java.net.URLEncoder.encode("SELECT Ebo_WordTemplate WHERE object IN (SELECT Ebo_ClsReg WHERE Ebo_ClsReg.name = '" + objParent.getBoDefinition().getBoSuperBo()+ "' or Ebo_ClsReg.name = '" + objParent.getName() + "')"));
//                        }                                       
                        
//                        if("Ebo_Document".equals(obj.getName())) //Ebo_Document
//                        {
//                            if(objParent.exists())
//                            {
//                                //Editar Ebo_Document                            
//                                if(!"".equals( obj.getAttribute("docTemplate").getValueString()))
//                                {                            
//                                    //reProcessar Ebo_DocumentTemplate
//                                    writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,"remerge","","Reprocessar Template","");                                                                                
//                                }
//                            }
//                        }                      
                    }
                    else //Com Template
                    {
                        //Adicionar Ebo_Document
                        writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,false, "add","document","Adicionar novo Documento",java.net.URLEncoder.encode("SELECT "+objName+" WHERE 1=1"));                        
                        //Criar Ebo_Document
//                        writeHTML_lookupAction(toPrint,objParent,atrParent,value,name,tabIndex,doc,isDisabled,isVisible,"create","");                        
                        //Adicionar Outro Ebo_DocumentTemplate
//                        writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,"add","template","Adicionar novo Template","SELECT Ebo_Template.documentTemplates WHERE Ebo_Template.BOUI = " + templateBoui);                        
//                        if("Ebo_Document".equals(obj.getName())) //Ebo_Document
//                        {
//                            //Editar Ebo_Document   
//                            if(objParent.exists())
//                            {
//                                if(!"".equals( obj.getAttribute("docTemplate").getValueString()))
//                                {
//                                    //reProcessar Ebo_DocumentTemplate
//                                    writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,"remerge","","Reprocessar Template","");                                                                                
//                                }
//                            }
//                        }                                           
                    }
                    if( isXeoControlActive( atrParent.getEboContext() ) || (!atrParent.isBridge() && DocumentHelper.isIFile(atrParent.getObject()) && RegistryHelper.isClientConnected(objParent.getEboContext().getXeoWin32Client_adress())) )
                    {
                        //Imprimir Documento
                        if((!"messageLetter".equals(objParent.getName()) && !"messageFax".equals(objParent.getName())) || 
                        objParent.getAttribute("impCentral") == null ||
                        !"1".equals(objParent.getAttribute("impCentral").getValueString())
                        )
                        {
                            writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,false, "print","","Imprimir","");
                        }
                    }
                    
                }
            }
        }
        else // Template Mode
        {
            String objName = atrParent.getDefAttribute().getReferencedObjectName();
            //Adicionar Ebo_Document
            writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,false,"add","template","Adicionar novo Documento",java.net.URLEncoder.encode("SELECT "+objName+" WHERE 1=1"));                        
            //Adicionar Ebo_DocumentTemplate
            writeHTML_lookupAction(toPrint,objParent,atrParent,null,value,name,tabIndex,doc,isDisabled,isVisible,false,"add","template","Adicionar novo Template",java.net.URLEncoder.encode("SELECT Ebo_WordTemplate WHERE Ebo_WordTemplate.object IN (SELECT Ebo_ClsReg WHERE Ebo_ClsReg.name = '" + objParent.getName() + "')"));
        }

    }
    public static void writeHTML_lookupAction(
            StringBuffer toPrint,
            boObject objParent,
            AttributeHandler atrParent,
            String docBoui,
            StringBuffer value,
            StringBuffer name,
//            StringBuffer id,
            int tabIndex,
            docHTML doc,
            boolean isDisabled ,
            boolean isVisible ,
            boolean showLink ,
//            boolean inEditTemplate ,
//            boolean isRequired,
//            boolean isRecommend,
//            Hashtable xattributes            
            String options,
            String options2,
            String title,
            String query
            )throws boRuntimeException
    {
        
        String imgName = "";
        if(!"edit".equals(options))
        { 
            toPrint.append("<td style='TEXT-ALIGN: right' width='25'");
        }
        else
        {
            if(showLink)
            {
                toPrint.append("<span style='cursor:hand;text-decoration:underline' ");
            }
            else
                toPrint.append("<span ");
        }
        toPrint.append(" title= '").append(title).append("' ");
        if("create".equals(options))
        {
            if(!isDisabled)
            {
                toPrint.append(" onclick=\"winmain().newPage(getIDX(),'")
                .append(atrParent.getDefAttribute().getReferencedObjectName().toLowerCase())
                .append("','edit','method=new&object=")
                .append(atrParent.getDefAttribute().getReferencedObjectName())
                .append("&parentAttribute=").append(atrParent.getName())
                .append("&relatedClientId=").append(doc.getDocIdx() )
                .append("&ctxParent=").append( objParent.getBoui() )
                .append("&ctxParentIdx=").append( doc.getDocIdx() )
                .append("&addToCtxParentBridge=").append( atrParent.getName() )
                .append("&docid=").append( doc.getDocIdx() )
                .append("&searchClientIdx='+getIDX()+'');\">");
            }
            else
            {
                toPrint.append(">");
            }
            
            
            if( options2.equals("document") )
            {
                imgName = "create";    
            }
            else
            {
                imgName = "createWord";    
            }
            
            /* toPrint.append(" onclick=\"winmain().openDoc('medium','");
            toPrint.append(atrParent.getDefAttribute().getReferencedObjectName().toLowerCase()).append("','edit','method=new&relatedClientIDX='+getIDX()+'&ctxParentIdx='+getDocId()+'&docid='+getDocId()+'");
            toPrint.append("&ctxParent=").append(objParent.getBoui());
            toPrint.append("&object=").append(atrParent.getDefAttribute().getReferencedObjectName());
            toPrint.append("&parentAttribute=").append(atrParent.getName()).append("');\">");   */
            
            toPrint.append("<img  class='lua' style='CURSOR: default' "); 
        }
//        else if("remerge".equals(options))
//        {
//            toPrint.append(" onclick=\" boForm.executeStaticMeth('netgest.bo.impl.document.template.TemplateHelper.reMerge',['this','"+atrParent.getName()+"']); \">");
//            imgName="reprocessWord";
//            toPrint.append("<img  class='lua' style='CURSOR: default' ");             
//        }
//        else if("merge".equals(options) && options2.equals(""))
//        {
//            toPrint.append(" onclick=\" boForm.executeStaticMeth('netgest.bo.impl.document.template.TemplateHelper.merge',['this','"+atrParent.getName()+"']); \">");
//            imgName="processWord";
//            toPrint.append("<img  class='lua' style='CURSOR: default' ");             
//        }
        else if("edit".equals(options) && "document".equals(options2))
        {
//            toPrint.append(" onclick=\" boForm.executeStaticMeth('netgest.bo.impl.document.template.TemplateHelper.editWordDocumentInClient',['this','"+ docBoui +"']); \">");
            if(showLink)
            {
                if( isXeoControlActive( doc.getEboContext() ) )
                {
                    toPrint.append(" onclick=\"javascript:window.top.XEOControl.documentManager.OpenWordDocument('"+objParent.getEboContext().getBoSession().getId()+"','"+doc.getDochtmlController().poolUniqueId()+"|"+doc.getDocIdx()+"',"+docBoui+");\">");
                }
                else
                {
                    toPrint.append(" onclick=\" boForm.executeStaticMeth('netgest.bo.impl.document.DocumentHelper.openDocumentInClient',['this','"+ docBoui +"']); \">");
                }
            }
            else
            {
                toPrint.append(" >");
            }
//            toPrint.append(" onclick=\" boForm.executeStaticMeth('netgest.bo.impl.document.DocumentHelper.open',['this','"+ docBoui +"']); \">");
        }
//        else if("edit".equals(options) && "template".equals(options2))
//        {
//            toPrint.append(" onclick=\" boForm.executeStaticMeth('netgest.bo.impl.document.template.TemplateHelper.editWordTemplateInClient',['this','"+ docBoui +"']); \">");             
//        }
        else if("print".equals(options))
        {
//            toPrint.append(" onclick=\" boForm.executeStaticMeth('netgest.bo.impl.document.print.PrintHelper.printDocument',['this','"+atrParent.getName()+"']); \">");
            if( isXeoControlActive( doc.getEboContext() ) )
            {
                //if(atrParent.isBridge()) 
                //{  
                    String strBouis = atrParent.getValueString();
                    StringBuffer sb = new StringBuffer();
                    sb.append(" onclick=\"javascript:"); 
                    sb.append(
                    "var bSelPrint = false;\n" +
                    "var sPrinters = null;\n" +
                    "var sPrinter=null;\n" +
                    "try {sPrinters = window.top.XEOControl.documentManager.getPrinterNames();bSelPrint=true}catch(e){}" +
                    "if( bSelPrint )sPrinter=window.showModalDialog('selPrinter.html',window.top.XEOControl.documentManager.getPrinterNames(),'center:yes;dialogHeight:100px;dialogWidth:400px;resizable:no')\n" +
                    "if(sPrinter != null || !bSelPrint ) {"
                    );
                    
                    sb.append("try{ ");
                    
                    String[] aStrBouis = strBouis.split(";");
                    for (int i = 0; i < aStrBouis.length; i++) 
                    {
                        sb.append( "if(bSelPrint)" );
                        sb.append("window.top.XEOControl.documentManager.PrintWordDocumentsPrinter(");
                        sb.append("'"+objParent.getEboContext().getBoSession().getId()+"','"+doc.getDochtmlController().poolUniqueId()+"|"+doc.getDocIdx()+"',sPrinter,"+objParent.getBoui()+",'" );
                        sb.append(  aStrBouis[i] );
                        sb.append("')\n"); 
                        sb.append( "else " );
                        sb.append("window.top.XEOControl.documentManager.PrintWordDocumentsSelPrinter(");
                    sb.append("'"+objParent.getEboContext().getBoSession().getId()+"','"+doc.getDochtmlController().poolUniqueId()+"|"+doc.getDocIdx()+"',"+objParent.getBoui()+",'" );
                        sb.append(  aStrBouis[i] );
                        sb.append("')\n"); 
                    }
                    sb.append("window.top.XEOControl.documentManager.setAsPrinted(");
                    sb.append("'"+objParent.getEboContext().getBoSession().getId()+"','"+doc.getDochtmlController().poolUniqueId()+"|"+doc.getDocIdx()+"',"+objParent.getBoui()+"," );
                    sb.append( "0"  ).append( ");\n" );
 
                    sb.append("boForm.Save();");
                    sb.append("}catch(exp){alert('Erro a imprimir.\\n'+exp.message);}");
                    
                    sb.append("}\">"); 
                    
                    toPrint.append( sb );
                /*} 
                else
                {
                    toPrint.append(" onclick=\"javascript:try{window.top.XEOControl.documentManager.PrintWordDocument('"+objParent.getEboContext().getBoSession().getId()+"','"+doc.getDochtmlController().poolUniqueId()+"|"+doc.getDocIdx()+"',"+objParent.getBoui()+","+docBoui+");boForm.Save();}catch(exp){alert('Erro a imprimir.\\n'+exp.message);};\">");
                }
                */
            }
            else
            {
                toPrint.append(" onclick=\" boForm.executeStaticMeth('netgest.bo.impl.document.DocumentHelper.print',['this','"+atrParent.getName()+"']); \">");
            }
            imgName="print";
            toPrint.append("<img  class='lua' style='CURSOR: default' ");             
        }     
        else
        {        
            imgName="lookup";
            if ( options2.equals("document" ) )
            {
                
            }
            else if ( options2.equals("template") || options2.equals("mergeNewTemplate") )
            {
                imgName="lookupWord";    
            }
            toPrint.append(" >"); 
        }
        //if(xRefBoui!=-1 && xRefBoui!=0 && !isDisabled)
        //{

        //}
        if(!"edit".equals(options))
        {
            toPrint.append("<img  class='lu' style='CURSOR: default' ");
            toPrint.append(" tabindex='");
            toPrint.append(tabIndex);
            if ( isDisabled && !"print".equals(options)) 
            {
                    toPrint.append("' disabled src='templates/form/std/btn_dis_"+imgName+".gif' lookupstyle='single' ");
            }
            else 
            {
                if(atrParent.getDefAttribute().getRelationType()==boDefAttribute.RELATION_1_TO_1)
                {
                    toPrint.append("' src='templates/form/std/btn_off_"+imgName+".gif' lookupstyle='single' ");
                }
                else
                {
                    toPrint.append("' src='templates/form/std/btn_off_"+imgName+".gif' lookupstyle='multi' ");
                }
                
                
                if ( !isVisible ) {
                        toPrint.append(" style='display:none' ");
                }
            }

            toPrint.append(" options='");
            toPrint.append(options);
            toPrint.append("'");
            toPrint.append(" lookupQuery='");
            toPrint.append(query);
            toPrint.append("'");
            toPrint.append(" shownew='");
            toPrint.append("1'");
            toPrint.append(" parentBoui='");
            toPrint.append(objParent.bo_boui);
            toPrint.append("' parentObj='");
            toPrint.append(objParent.getName());
            toPrint.append("' parentAttribute='");
            toPrint.append(atrParent.getName() );
            toPrint.append("' object='");
            toPrint.append(atrParent.getDefAttribute().getReferencedObjectName());
            toPrint.append("'  docid='");
            toPrint.append(doc.getDocIdx());
            toPrint.append("' width='21' height='19'>");
        } 
        else
        {
            toPrint.append(value);            
        }
        toPrint.append("<input original='" + value + "' type='hidden' value='");
        toPrint.append(value);
        toPrint.append("' name='");
        toPrint.append(name);
        toPrint.append("' object='");
        toPrint.append(atrParent.getDefAttribute().getReferencedObjectName());
        toPrint.append("' req='");
        
        boolean req = atrParent.getDefAttribute().getRequired()!=null&&atrParent.getDefAttribute().getRequired().getBooleanValue();
        if ( req ) toPrint.append(1);
        else  toPrint.append(0);
        
        toPrint.append("' boType='lu'>");
        if(!"edit".equals(options))
        {
            toPrint.append("</td>");
        }
        else
        {
            toPrint.append("</span>");
        }


    }
    
     public static void writeHTML_lookupObject(StringBuffer toPrint,
                                              boObject objParent,
                                              AttributeHandler atrParent,
                                              StringBuffer Value,
                                              int tabIndex,
                                              docHTML doc,
                                              boolean isDisabled ,
                                              boolean isVisible ,
                                              long xRefBoui , boolean isMulti ) throws boRuntimeException
    {
        writeHTML_lookupObject(toPrint,
                               objParent,
                               atrParent,
                               Value,
                               tabIndex,
                               doc,
                               isDisabled ,
                               isVisible ,
                               true ,
                               xRefBoui , isMulti );
    }

    public static void writeHTML_lookupObject(StringBuffer toPrint,
                                              boObject objParent,
                                              AttributeHandler atrParent,
                                              StringBuffer Value,
                                              int tabIndex,
                                              docHTML doc,
                                              boolean isDisabled ,
                                              boolean isVisible ,
                                              boolean showLink,
                                              long xRefBoui , boolean isMulti ) throws boRuntimeException
    {
        writeHTML_lookupObject(toPrint,
                              objParent,
                              atrParent,
                              Value,
                              tabIndex,
                              doc,
                              isDisabled ,
                              isVisible ,
                              showLink,
                              xRefBoui , 
                              isMulti,
                              null);
    }
    public static void writeHTML_lookupObject(StringBuffer toPrint,
                                              boObject objParent,
                                              AttributeHandler atrParent,
                                              StringBuffer Value,
                                              int tabIndex,
                                              docHTML doc,
                                              boolean isDisabled ,
                                              boolean isVisible ,
                                              long xRefBoui , 
                                              boolean isMulti,
                                              boDefAttribute boDefAttr
                                              ) throws boRuntimeException
    {
        writeHTML_lookupObject(toPrint,
                               objParent,
                               atrParent,
                               Value,
                               tabIndex,
                               doc,
                               isDisabled ,
                               isVisible ,
                               true,
                               xRefBoui , 
                               isMulti,
                               boDefAttr
                               );
    }
    
    public static void writeHTML_lookupObject(StringBuffer toPrint,
                                              boObject objParent,
                                              AttributeHandler atrParent,
                                              StringBuffer Value,
                                              int tabIndex,
                                              docHTML doc,
                                              boolean isDisabled ,
                                              boolean isVisible ,
                                              boolean showLink,
                                              long xRefBoui , 
                                              boolean isMulti,
                                              boDefAttribute boDefAttr
                                              ) throws boRuntimeException
    {
        boDefAttribute defAttribute = null;
        if(atrParent != null)
        {
            defAttribute = atrParent.getDefAttribute();
        }
        else
        {
            defAttribute = boDefAttr;
        }
        
        StringBuffer toPass = new StringBuffer();
        boObject xref=null;
        if(xRefBoui!=-1 && xRefBoui!=0)
        {
//             xref=objParent.getBoManager().loadObject(objParent.getEboContext(),xRefBoui );
             xref = boObject.getBoManager().loadObject(doc.getEboContext(),xRefBoui );
        }
        if(showLink)
        {
            toPrint.append("<span class='lui' onclick=\"");        
            
            //openDoc("medium","activity","edit","method=edit&boui=1264",null,null,null);            
            boolean sameWindow = false;
            
            if ( xref != null )
            {
                if ( xref.getBoDefinition().getBoCanBeOrphan()  && 
                    (xref.exists() || ("XwfController".equalsIgnoreCase(doc.getController().getName()) && ((XwfController)doc.getController()).getEngine().getExecutionMode() == xwfHelper.PROGRAM_EXEC_TEST_MODE)) 
                )
                {
                    toPrint.append("winmain().openDoc('medium','");
                }
                else
                {
                    toPrint.append("winmain().newPage(getIDX(),'"); 
                    sameWindow = true;
                }
            }
            else
            {
                toPrint.append("winmain().openDoc('medium','");    
            }
            
            
            if ( defAttribute.getObjects() == null || Value.length()==0 )
            {
                if ( xref!= null )
                {
                 toPrint.append(xref.getName().toLowerCase() );   
                }
                else
                {
                 toPrint.append(defAttribute.getReferencedObjectName().toLowerCase() );
                }
            }
            else
            {   
    //            long v=ClassUtils.convertToLong( Value );
    //            boObject o = null; 
    //            if(v!=0) o = doc.getObject( v );
    //            if (o != null) toPrint.append( o.getName().toLowerCase() );
                if (xref != null) toPrint.append( xref.getName().toLowerCase() );
            }
            
            toPrint.append("','edit','method=edit&boui=");
            if ( xref!= null )
            {
                toPrint.append( xref.getBoui() );
                toPass.append("boui=").append( xref.getBoui() );
            }
            else
            {
                toPrint.append( Value );
                toPass.append("boui=").append( Value );
            }
            if(objParent != null)
            {
                toPrint.append("&actRenderObj=");
                toPrint.append(objParent.bo_boui);
                toPass.append("&actRenderObj=").append( objParent.bo_boui );
            }
            
             if ( !(sameWindow  ||  ( xref!= null && !xref.exists() ) )  )
            {
                    toPrint.append("&actRenderDocid=");
                    toPrint.append( doc.getDocIdx() );
                    toPass.append("&actRenderDocid=").append( doc.getDocIdx() );
                    
    //                toPrint.append("&docid=").append( doc.getDocIdx() );
                    
                    toPrint.append("&actRenderAttribute=");
                    toPrint.append( defAttribute.getName());
                    toPrint.append("&actIdxClient='+getIDX()+'");
                    toPass.append("&actRenderAttribute=").append( defAttribute.getName() )
                        .append("&actIdxClient='+getIDX()+'");
            }
    
                  
             if ( sameWindow  ||  ( xref!= null && !xref.exists() ) )
            {
                
                 toPrint.append("&parentAttribute=").append(defAttribute.getName())
                    .append("&relatedClientId=").append(doc.getDocIdx() )
                    .append("&ctxParent=").append( objParent.getBoui() )
                    .append("&ctxParentIdx=").append( doc.getDocIdx() )
                    .append("&addToCtxParentBridge=").append( defAttribute.getName() )
                    .append("&docid=").append( doc.getDocIdx() );
                 toPass.append("&parentAttribute=").append(defAttribute.getName())
                    .append("&relatedClientId=").append(doc.getDocIdx() )
                    .append("&ctxParent=").append( objParent.getBoui() )
                    .append("&ctxParentIdx=").append( doc.getDocIdx() )
                    .append("&addToCtxParentBridge=").append( defAttribute.getName() )
                    .append("&docid=").append( doc.getDocIdx() );
                
            }
            
            //PODE ESTAR NO 2?
            toPrint.append("&actRenderAttribute=");
            toPrint.append( defAttribute.getName());
            toPrint.append("&actRenderDocid=");
            toPrint.append( doc.getDocIdx() );
            toPrint.append("&actIdxClient='+getIDX()+'");
            
            toPass.append("&actRenderAttribute=");
            toPass.append( defAttribute.getName());
            toPass.append("&actRenderDocid=");
            toPass.append( doc.getDocIdx() );
            toPass.append("&actIdxClient='+getIDX()+'");
            
    
          
            toPrint.append("')\"");
    
            toPrint.append(" boui='");
            toPrint.append(Value);
            toPrint.append("' object='");
            if ( xref == null )
            {
                toPrint.append(defAttribute.getReferencedObjectName());
            }
            else
            {
                toPrint.append( xref.getName() );
            }
            toPrint.append("'>");
        }

     //   long xRefBoui=ClassUtils.convertToLong(Value,-1);
        if(xRefBoui!=-1 && xRefBoui!=0){
//             boObject xref=objParent.getBoManager().loadObject(objParent.getEboContext(),xRefBoui );
             if(showLink)
             {
                toPrint.append(xref.getCARDIDwLink(true, false, toPass.toString()));
             }
             else
             {
                toPrint.append(xref.getCARDID(false).toString());
             }
//             toPrint.append("<img  class='lui' title='");
//             toPrint.append("Imagem representativa do objecto " );
//             toPrint.append( xref.getBoDefinition().getLabel() );
//             toPrint.append("' src='");
//             //resources/activity/ico16.gif
//             toPrint.append(xref.getSrcForIcon16());
//             toPrint.append('\'');
//             
//             if ( !isVisible ) {
//                toPrint.append(" style='display:none' ");
//             }
//             toPrint.append(" object='" );
//             toPrint.append( xref.getName() );
//             toPrint.append('\'');
//             
//             toPrint.append(" boui='" );
//             toPrint.append( xref.getBoui() );
//             toPrint.append('\'');
//             
//             toPrint.append(" width='16' height='16'/>");
//
//             toPrint.append( docHTML_renderFields.buildCARDID(xref.getBoDefinition().getCARDID(),xref));  
            // Administrator,  Administrator
             
             toPrint.append("</span>");
             
        }
        else
        {
            toPrint.append("</span>");
            if ( !isDisabled )
            {
               if(
                defAttribute.getType().indexOf("boObject") >= 0 ||
                defAttribute.getReferencedObjectDef().getBoCanBeOrphan() ||
                defAttribute.getReferencedObjectDef().getBoHaveMultiParent() ||
                defAttribute.hasTransformer())
                {
                    toPrint.append("<input tabindex="+tabIndex+" original='" + Value + "' style='width:100%;border:0' onblur='this.parentElement.parentElement.parentElement.children[2].firstChild.fromInput(this);' />");
                }
            }
        }    
    }
    public static void writeHTML_lookupDocument(StringBuffer toPrint,
                                              boObject objParent,
                                              AttributeHandler atrParent,
                                              StringBuffer Value,
                                              StringBuffer Name,
                                              int tabIndex,
                                              docHTML doc,
                                              boolean isDisabled ,
                                              boolean isVisible ,
                                              long xRefBoui ,
                                              String objClassName,boolean isMulti ) throws boRuntimeException                    
    {
        writeHTML_lookupDocument(toPrint,
                                 objParent,
                                 atrParent,
                                 Value,
                                 Name,
                                 tabIndex,
                                 doc,
                                 isDisabled ,
                                 isVisible ,
                                 true,
                                 xRefBoui ,
                                 objClassName,isMulti );
    }
    public static void writeHTML_lookupDocument(StringBuffer toPrint,
                                              boObject objParent,
                                              AttributeHandler atrParent,
                                              StringBuffer Value,
                                              StringBuffer Name,
                                              int tabIndex,
                                              docHTML doc,
                                              boolean isDisabled ,
                                              boolean isVisible ,
                                              boolean showLink,
                                              long xRefBoui ,
                                              String objClassName,boolean isMulti ) throws boRuntimeException                    
    {
        boObject xref=null;
        boolean sameWindow = false;
        if(xRefBoui!=-1 && xRefBoui!=0)
        {
             xref=objParent.getBoManager().loadObject(objParent.getEboContext(),xRefBoui );
        }
        if(showLink)
        {
            toPrint.append("<span class='lui' onclick=\"");
            if ( xref != null )
            {
                if ( xref.getBoDefinition().getBoCanBeOrphan()  && xref.exists() )
                {
                    toPrint.append("winmain().openDoc('medium','");
                }
                else
                {
                    toPrint.append("winmain().newPage(getIDX(),'");   
                    sameWindow = true;
                }
            }
            else
            {
                toPrint.append("winmain().openDoc('medium','");    
            }
            
            
            if ( atrParent.getDefAttribute().getObjects() == null || Value.length()==0 )
            {
                if ( xref!= null )
                {
                 toPrint.append(xref.getName().toLowerCase() );   
                }
                else
                {
                 toPrint.append(atrParent.getDefAttribute().getReferencedObjectName().toLowerCase() );
                }
            }
            else
            {   
                if (xref != null)
                {
                    toPrint.append( xref.getName().toLowerCase() );
                }
            }
            toPrint.append("','edit','method=edit&boui=");
           
             if ( xref!= null )
            {
                toPrint.append( xref.getBoui() );
            }
            else
            {
                toPrint.append( Value );    
            }
            toPrint.append("&actRenderObj=");
            toPrint.append(objParent.bo_boui);
            if ( !(sameWindow  ||  ( xref!= null && !xref.exists() ) )  )
            {
                    toPrint.append("&actRenderDocid=");
                    toPrint.append( doc.getDocIdx() );                
                    
                    toPrint.append("&actRenderAttribute=");
                    toPrint.append( atrParent.getName());
                    toPrint.append("&actIdxClient='+getIDX()+'");
            }
            
            if ( sameWindow  ||  ( xref!= null && !xref.exists() ) )
            {
                
                 toPrint.append("&parentAttribute=").append(atrParent.getName())
                    .append("&relatedClientId=").append(doc.getDocIdx() )
                    .append("&ctxParent=").append( objParent.getBoui() )
                    .append("&ctxParentIdx=").append( doc.getDocIdx() )
                    .append("&addToCtxParentBridge=").append( atrParent.getName() )
                    .append("&docid=").append( doc.getDocIdx() );
                
            }
            toPrint.append("&actRenderAttribute=");
            toPrint.append( atrParent.getName());
            toPrint.append("&actRenderDocid=");
            toPrint.append( doc.getDocIdx() );        
            toPrint.append("&actIdxClient='+getIDX()+'");
            
            
            toPrint.append("')\"");
            
            toPrint.append(" boui='");
            toPrint.append(Value);
            toPrint.append("' object='");
            toPrint.append(atrParent.getDefAttribute().getReferencedObjectName());
            toPrint.append("'>");
        }
        
        
        if(xRefBoui!=-1 && xRefBoui!=0)
        {
             
             toPrint.append("<img  class='lui' title='");
             toPrint.append("Imagem representativa do objecto " );
             toPrint.append( xref.getBoDefinition().getLabel() );
             toPrint.append("' src='");             
             toPrint.append(xref.getSrcForIcon16());
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
             if(showLink)
             {
                toPrint.append("</span>");
             }
        
              writeHTML_lookupAction(toPrint,
                    objParent,
                    atrParent,
                    String.valueOf(xRefBoui),
                    new StringBuffer(docHTML_renderFields.buildCARDID(xref.getBoDefinition().getCARDID(),xref)),
                    Name,
                    tabIndex,
                    doc,
                    isDisabled,
                    isVisible,showLink,"edit","document","Abrir Documento",null);                    
        }
        else
        {
            toPrint.append("</span>");
            if ( !isDisabled )
            {
            toPrint.append("<input tabindex="+tabIndex+" original='" + Value + "' style='width:100%;border:0' onblur='this.parentElement.parentElement.parentElement.children[2].firstChild.fromInput(this);' />");
            }
        }         
    }
    public static void writeHTML_lookupN(
        StringBuffer toPrint,
        boObject objParent,
        bridgeHandler bridge,
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
        
        throws boRuntimeException
        {
            writeHTML_lookupN(
                toPrint,
                objParent,
                bridge,
                atrParent,
                Value,
                Name,
                id,
                tabIndex,
                doc,
                isDisabled ,
                isVisible ,
                inEditTemplate ,
                isRequired,
                isRecommend,
                true,
                xattributes
                );
        }
    
    public static void writeHTML_lookupN(
        StringBuffer toPrint,
        boObject objParent,
        bridgeHandler bridge,
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
        boolean showLink,
        Hashtable xattributes
        )
        
        throws boRuntimeException
        {
            writeHTML_lookupN(toPrint,
                            objParent,
                            bridge,
                            atrParent,
                            Value,
                            Name,
                            id,
                            tabIndex,
                            doc,
                            isDisabled ,
                            isVisible ,
                            inEditTemplate ,
                            isRequired,
                            isRecommend,
                            showLink,
                            xattributes,
                            null,
                            null);
        }
    
     public static void writeHTML_lookupN(
        StringBuffer toPrint,
        boObject objParent,
        bridgeHandler bridge,
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
        Hashtable xattributes,
        boDefAttribute boDefAttr,
        String lookupDetachField            
        )throws boRuntimeException
        
    {
        writeHTML_lookupN(
        toPrint,
        objParent,
        bridge,
        atrParent,
        Value,
        Name,
        id,
        tabIndex,
        doc,
        isDisabled ,
        isVisible ,
        inEditTemplate ,
        isRequired,
        isRecommend,
        true, 
        xattributes,
        boDefAttr,
        lookupDetachField            
        );
    }
    
    public static void writeHTML_lookupN(
        StringBuffer toPrint,
        boObject objParent,
        bridgeHandler bridge,
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
        boolean showLink, 
        Hashtable xattributes,
        boDefAttribute boDefAttr,
        String lookupDetachField            
        )
        
        throws boRuntimeException{
        boolean docs = false;
        boDefAttribute defAttribute = null;
        if(atrParent != null)
        {
            defAttribute = atrParent.getDefAttribute();
        }
        else
        {
            defAttribute = boDefAttr;
        }
        
        toPrint.append("<table id='");
        toPrint.append(id);
        toPrint.append("' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>");
        
        String[] bouis = null;
        int rc = 0;
        if(bridge != null)
        {
            bridge.beforeFirst();                 
            rc=bridge.getRowCount();            
        }
        else
        {             
            if(Value != null && Value.length() > 0)
            {
                bouis = Value.toString().split(";");
                rc = bouis.length;
            }
            
        }

        
        if ( rc>0 )
        {
            boObject o = null;
            String objClassName = null;
            toPrint.append("<div class='lu ro'>");
            String  XeoWin32Client_address = doc.getEboContext().getXeoWin32Client_adress();
            if(bridge != null)
            {
                while ( bridge.next() )
                {                    
                    o = bridge.getObject();
                    objClassName = o.getName();    
                    
//                    if(!DocumentHelper.isDocument(objClassName) || 
//                       !DocumentHelper.isMSWordFile( o ) ||
//                       XeoWin32Client_address==null ||
//                       !RegistryHelper.isClientConnected(XeoWin32Client_address)
//                    )
                    if(!isDocumentValidForClientRender(doc.getEboContext(),objClassName,o.getBoui()))                    
                    {   
                        writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible,showLink,o.getBoui(), true);
                    }
                    else
                    {
                        docs = true;
                        writeHTML_lookupDocument(toPrint,objParent,atrParent,Value,Name,tabIndex,doc,isDisabled,isVisible,showLink, o.getBoui(),objClassName, true);
                    }

/*                    if(!DocumentHelper.isDocument(objClassName))   
                    {
                        writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible,o.getBoui(),true);
                    }
                    else // Documents
                    {
                        if(DocumentHelper.isMSWordFile( o ) && 
                           XeoWin32Client_address!=null && 
                           RegistryHelper.isClientConnected(XeoWin32Client_address))
                        {
                            writeHTML_lookupDocument(toPrint,objParent,atrParent,Value,Name,tabIndex,doc,isDisabled,isVisible, o.getBoui(),objClassName,true);   
                        }   
                        else
                        {
                            writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible, o.getBoui(),true);    
                        }
                    }
                    */
                }
            }
            else if( bouis != null)
            {
                long xboui = -1;
                for (int i = 0; i < bouis.length; i++) 
                {
                    xboui = ClassUtils.convertToLong(bouis[i],-1);
                    if(xboui != -1)
                    {
                        o = boObject.getBoManager().loadObject(doc.getEboContext(),xboui);
                        objClassName = o.getName();
                        if(!DocumentHelper.isDocument(objClassName))   
                        {
                            writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible,showLink,o.getBoui(),true,defAttribute);
                        }
                        else // Documents
                        {
                            docs = true;
                            if(DocumentHelper.isMSWordFile( o ) && XeoWin32Client_address!=null && RegistryHelper.isClientConnected(XeoWin32Client_address))
                            {
                                writeHTML_lookupDocument(toPrint,objParent,atrParent,Value,Name,tabIndex,doc,isDisabled,isVisible,showLink, o.getBoui(),objClassName,true);   
                            }   
                            else
                            {
                                writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible,showLink, o.getBoui(),true);    
                            }
                        }                        
                    }                    
                }            
            }
        }
        else
        {
               toPrint.append("<div style='overflow:hidden' class='lu ro'>");
                toPrint.append("<span class='lui' >");
                
                toPrint.append("</span>");
                toPrint.append("<input "+(isDisabled ? "disabled":"")+" tabindex="+tabIndex+" original='" + Value + "' style='width:100%;border:0' onblur='this.parentElement.parentElement.parentElement.children[2].firstChild.fromInput(this);' />");

                
        }
		toPrint.append("</div>");
        toPrint.append("</td>");
        //botão de limpar campo
        String auxLookupStyle="single"; 
        if(!(defAttribute.getRelationType()==boDefAttribute.RELATION_1_TO_1 || defAttribute.getRuntimeMaxOccurs() <= 1))
        {
            auxLookupStyle="multi";
        }
        toPrint.append("<td style='");
        if(rc<=0 || isDisabled)
        {
            toPrint.append("display=none;");
        }
        toPrint.append(" width=16px'>")
        .append("<img  title='Clique para limpar o campo' onclick='")
        .append(
            onclickRemoveButton(objParent != null ? objParent.getName():"", 
                objParent != null ? String.valueOf(objParent.getBoui()):"", 
                atrParent != null ? atrParent.getName():"",
                defAttribute.getReferencedObjectName(), auxLookupStyle, 
                String.valueOf(doc.getDocIdx())))
        .append("' border='0' src='");
        toPrint.append("templates/form/std/remove.gif' width='16px' height='16px'/></td>");
        //------------------------------
        
        //psantos ini 20061114
        // descomentar para activar o drag and drop
        /*
        boolean isBridge=atrParent.isBridge();
        if (!isDisabled) 
        {
            boDefHandler bodef = defAttribute.getReferencedObjectDef();
            if(DocumentHelper.isDocument(bodef.getName()))
            {
                if (!isBridge)
                {
                    // nunca pasa por aqui o codigo é escrito pelo writeHTML_lookup
                } 
                else // isbridge
                {
                  if (atrParent.getObject() != null)
                  {
                      toPrint.append("<td id='tdDragDrop' width='25'>");
                      toPrint.append(" <iframe src = \"__gdSubmitFile.jsp?"+
                                     "method=edit&docid="+doc.getDocIdx()+"&"+
                                     "parent_boui="+objParent.getBoui() + "&"+
                                     "atrParent="+atrParent.getName() + "&"+
                                     "objParent="+objParent.getName() + "&"+
                                     "isNew= 0\"" +
                                     " height=25 width=25 SCROLLING=no MARGINWIDTH=0 MARGINHEIGHT=0>"); 
                      toPrint.append("</iframe>");
                      toPrint.append("</td>");
                  }
                  else
                  {
                      toPrint.append("<td id='tdDragDrop' width='25'>");
                      toPrint.append(" <iframe src = \"__gdSubmitFile.jsp?"+
                                     "method=edit&docid="+doc.getDocIdx()+"&"+
                                     "parent_boui="+objParent.getBoui() + "&"+
                                     "atrParent="+atrParent.getName() + "&"+
                                     "objParent="+objParent.getName() + "&"+
                                     "isNew= 1\"" +
                                     " height=25 width=25 SCROLLING=no MARGINWIDTH=0 MARGINHEIGHT=0>"); 
                      toPrint.append("</iframe>");
                      toPrint.append("</td>");
                  }
                }
            }
        }
        */
        //psantos fim 20061114

        toPrint.append("<td style='TEXT-ALIGN: right' width='25'><img class='lu' id style='CURSOR: default' tabIndex='");
        toPrint.append(tabIndex);
        
        if ( isDisabled ) {                
                if(defAttribute.getRelationType()==boDefAttribute.RELATION_1_TO_1)
                {                
                    toPrint.append("' disabled src='templates/form/std/btn_dis_lookup.gif' lookupstyle='single' ");
                }
                else
                {
                    toPrint.append("' disabled src='templates/form/std/btn_dis_lookup.gif' lookupstyle='multi' ");                
                }                 
        }
        else {
            if(defAttribute.getRelationType()==boDefAttribute.RELATION_1_TO_1){
                toPrint.append("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='single' ");
            }
            else{
                toPrint.append("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='multi' ");
            }
            
            
            if ( !isVisible ) {
                    toPrint.append(" style='display:none' ");
            }
        }
             
        toPrint.append(" shownew='");
        toPrint.append("1'");
        if(lookupDetachField != null)
        {
            toPrint.append(" lookupDetachField='");
            toPrint.append(lookupDetachField);
            toPrint.append("'");
        }        
        if(objParent != null)
        {
            toPrint.append(" parentBoui='").append(objParent.bo_boui).append("' ");
            toPrint.append(" parentObj='");
            toPrint.append(objParent.getName());
        }
        else
        {
            toPrint.append(" parentObj='");
            toPrint.append(defAttribute.getName());
        }
        toPrint.append("' parentAttribute='");
        toPrint.append(defAttribute.getName() );
        toPrint.append("' object='");
        toPrint.append(defAttribute.getReferencedObjectName());
        toPrint.append("'  docid='");
        toPrint.append(doc.getDocIdx());
        toPrint.append("' width='21' height='19'/><input original='" +Value + "' type='hidden' value='");
        toPrint.append(Value);
        toPrint.append("' name='");
        toPrint.append(Name);
        toPrint.append("' object='");
        toPrint.append(defAttribute.getReferencedObjectName());
        toPrint.append("' req='");
        
        boolean req = defAttribute.getRequired()!=null&&defAttribute.getRequired().getBooleanValue();
        if ( req )  toPrint.append(1);
        else  toPrint.append(0);
        
        toPrint.append("' boType='lu'/>");
        if(atrParent != null)
        {
            toPrint.append( favoritesLookupManager.getHTMLFavorites( atrParent , Name ));
        }
        toPrint.append("</td>");
        if(docs  && (isXeoControlActive( atrParent.getEboContext() ) ||  RegistryHelper.isClientConnected(objParent.getEboContext().getXeoWin32Client_adress())))
        {
            if((!"messageLetter".equals(objParent.getName()) && !"messageFax".equals(objParent.getName())) || 
            objParent.getAttribute("impCentral") == null ||
            !"1".equals(objParent.getAttribute("impCentral").getValueString())
            )
            {
                writeHTML_lookupAction(toPrint,objParent,atrParent,null,Value,Name,tabIndex,doc,isDisabled,isVisible,false, "print","","Imprimir","");
            }
        }
        toPrint.append("</tr></tbody></table>");

        if(inEditTemplate){
            //toPrint.append("<img src='templates/form/std/iconformula_on.gif' class='imgonoff' />");
        }
        
    }
  
    
    
    
   public static void writeHTML_forHTMLEDITOR(
        StringBuffer toPrint,
        boObject objParent,
        AttributeHandler atrParent,
        docHTML doc, 
        StringBuffer Value,
        StringBuffer Name,
        StringBuffer id,
        int tabIndex,
        boolean isDisabled ,
        boolean isVisible ,
        boolean inEditTemplate,
        StringBuffer onChange,
        boolean isRequired,
        boolean isRecommend,
        Hashtable xattributes,
        String editor
        ){
    
        toPrint.append("\n<!--BEGIN -->\n");
		
		if (isDisabled)
		{
			toPrint.append("<xml id='valueFor");
			toPrint.append(id);
			toPrint.append("' name='");
			toPrint.append(Name);
			toPrint.append("' >");
			toPrint.append(ClassUtils.removeHtmlGarbage(Value.toString()));
	
			toPrint.append("</xml>");
			toPrint.append("     <table ");
			toPrint.append("id='");
			toPrint.append(id);
			toPrint.append('C');
			toPrint.append('\'');
	
			if (isVisible)
			{
				toPrint.append(" style=\"table-layout:fixed;");
			}
			else
			{
				toPrint.append(" style=\"table-layout:fixed;display:none;");
			}
	
			String xh = (String) xattributes.get("height");
			String xw = (String) xattributes.get("width");
	
			if (xh != null)
			{
				toPrint.append("height:");
				toPrint.append(xh);
				toPrint.append(';');
			}
	
			if (xw != null)
			{
				toPrint.append("width:");
				toPrint.append(xw);
				toPrint.append(';');
			}
	
			toPrint.append("\"");
			toPrint.append(" disabled ");
			toPrint.append(" cellSpacing=\"0\" cellPadding=\"0\" height=\"100%\" width=\"100%\">\n");
			toPrint.append("           <colgroup>\n");
			toPrint.append("           <col width=\"290\">\n");
			toPrint.append("           <col>\n");
			toPrint.append("           <col width=\"10\">\n");
			toPrint.append("           <col width=\"190\">\n");
			toPrint.append("           <col>\n");
			toPrint.append("  <tbody>\n");
			toPrint.append("        <tr>\n");
			toPrint.append(	"    	   <td colspan=\"5\" height=\"100%\" style=\"background-color:#EAEAEA\">\n");
			toPrint.append(	"    	     <table height=\"100%\"  cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\"><tbody>\n");
			toPrint.append("            <tr>\n");
			
			String fieldName = id + "MessageBody";
			String htmleditoronoff = "on";
			
			htmleditoronoff = "off";
			
			toPrint.append(
				"              <td><iframe class=\"editPage\" onfocus='setFieldWFocus(\"" +
				fieldName + "\")' id=\"");
			toPrint.append(id);
			toPrint.append("MessageBody\"  onload=\"");
			toPrint.append(id);
			toPrint.append("MessageBody.document.body.contentEditable = 'False';");
			toPrint.append(id);
			toPrint.append("MessageBody.document.designMode = '" + htmleditoronoff +
				"';UpdateMessageBody(");
			toPrint.append(id);
			toPrint.append("MessageBody,valueFor");
			toPrint.append(id);
			toPrint.append(");window.event.cancelBubble=true;\""); 
			toPrint.append("  tabIndex=\"6\" src=\"templates/form/std/msgBody.htm\" frameBorder=\"0\" sxecurity=\"restricted\"></iframe>\n");
			toPrint.append("</td></tr>");
			toPrint.append("</tbody></table>\n");
			toPrint.append("</td> </tr>");
			toPrint.append("</tbody></table>");
		}
		else
		{
			//if(Value.length()==0) Value.append("&nbsp;");
			
			String xw = (String) xattributes.get("width");
			String xh = (String) xattributes.get("height");

			toPrint.append("<textarea id='");
			toPrint.append(id);
			toPrint.append("' name='");
			toPrint.append(Name);
			toPrint.append("' >");
			toPrint.append(ClassUtils.removeHtmlGarbage(Value.toString()));
			toPrint.append("</textarea>");
//			toPrint.append("\n<script type=\"text/javascript\" src=\"FCKeditor/fckeditor.js\"></script>");
			toPrint.append("\n<script>");
			toPrint.append("\nvar " + id + " = new FCKeditor( '" + id + "' );");
			
			toPrint.append("\n" + id + ".attrParent='" + atrParent.getName() + "';");
			toPrint.append("\n" + id + ".bouiParent='" + objParent.getBoui() + "';");
			toPrint.append("\n" + id + ".objXeoName='" + objParent.getName() + "';");
			toPrint.append("\n" + id + ".docID='" + doc.getDocIdx() + "';");
			
			if(editor!=null && editor.trim().length()>0)
			{
				// Vai instanciar a barra de opções do editor
				// As mesmas podem ser definidas no ficheiro fckconfig.js
				// ATENÇÃO: Não modificar os nomes das Toolbars 'Default' e 'None'
				toPrint.append("\n" + id + ".ToolbarSet='" + editor + "';");
			}
			else
			{
				toPrint.append("\n" + id + ".ToolbarSet='Default';");
			}
			
			toPrint.append("\n" + id + ".visible='" + isVisible + "';");
			toPrint.append("\n" + id + ".BasePath='FCKeditor/';");
			toPrint.append("\n" + id + ".Config['SkinPath']='skins/office2003/';");
			if(xh != null) toPrint.append("\n" + id + ".Height='" + xh + "';");
			if(xw != null) toPrint.append("\n" + id + ".Width='" + xw + "';");
			toPrint.append("\n" + id + ".ReplaceTextarea();");
		
			//toPrint.append("\n" + id + ".Create();");
			toPrint.append("\n</script>");
		}
		
        toPrint.append("\n<!--END -->\n");
    }

    private static void getButton(StringBuffer toPrint, StringBuffer id, boolean readOnly, String editor, int tabIndex)
    {
        if(!readOnly)
        {
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Cut\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('cut')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-cut.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Copy\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('copy')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-copy.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Paste\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('paste')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-paste.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            if ("htmladvanced".equalsIgnoreCase(editor))
            {
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Undo\" style=\"WIDTH: 26px\" onclick=\"");
              toPrint.append(id);
              toPrint.append(
                  ".htmlExec('Undo')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-undo.gif\" ></td>\n");        
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Redo\" style=\"WIDTH: 26px\" onclick=\"");
              toPrint.append(id);
              toPrint.append(
                  ".htmlExec('Redo')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-redo.gif\" ></td>\n");
            }
            toPrint.append("                    <td style=\"PADDING-LEFT: 4px; WIDTH: 10px\">\n");
            toPrint.append("                      <div style=\"BORDER-LEFT: #c5c2b8 1px solid\">&nbsp;</div></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Bold\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('bold')\" noWrap tabindex='" + tabIndex + "'><b>B</b></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Italic\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('italic')\" noWrap tabindex='" + tabIndex + "'><b><i>I</i></b></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Underline\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('underline')\" noWrap tabindex='" + tabIndex + "'><b><u>U</u></b></td>\n");
            if ("htmladvanced".equalsIgnoreCase(editor))
            {
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"SuperScript\" style=\"WIDTH: 26px\" onclick=\"");
              toPrint.append(id);
              toPrint.append(
                  ".htmlExec('superscript')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-superscript.gif\" ></td>\n");
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Subscript\" style=\"WIDTH: 26px\" onclick=\"");
              toPrint.append(id);
              toPrint.append(
                  ".htmlExec('subscript')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-subscript.gif\" ></td>\n");
            }
            toPrint.append("                    <td style=\"PADDING-LEFT: 4px; WIDTH: 10px\">\n");
            toPrint.append("                      <div style=\"BORDER-LEFT: #c5c2b8 1px solid\">&nbsp;</div></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Align Left\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('justifyleft')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-justifyleft.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Center\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('justifycenter')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-justifycenter.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Align Right\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('justifyright')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-justifyright.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td style=\"PADDING-LEFT: 4px; WIDTH: 10px\">\n");
            toPrint.append("                      <div style=\"BORDER-LEFT: #c5c2b8 1px solid\">&nbsp;</div></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Numbering\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('insertOrderedList')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-insertOrderedList.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Bullets\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('insertUnorderedList')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-insertUnorderedList.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td style=\"PADDING-LEFT: 4px; WIDTH: 10px\">\n");
            toPrint.append("                      <div style=\"BORDER-LEFT: #c5c2b8 1px solid\">&nbsp;</div></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Increase Indent\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('indent')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-indent.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Decrease Indent\" style=\"WIDTH: 26px\" onclick=\"");toPrint.append(id);toPrint.append(".htmlExec('outdent')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-outdent.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            if ("htmladvanced".equalsIgnoreCase(editor)) 
            {
              toPrint.append("</tr><tr>");
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Insert Horizontal Rule\" style=\"WIDTH: 26px\" onclick=\"");
              toPrint.append(id);
              toPrint.append(
                  ".htmlExec('InsertHorizontalRule')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-hr.gif\" ></td>\n");
            }
            else
            {
                toPrint.append("                    <td style=\"PADDING-LEFT: 4px; WIDTH: 10px\">\n");
                toPrint.append("                      <div style=\"BORDER-LEFT: #c5c2b8 1px solid\">&nbsp;</div></td>\n");
            }            
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Font Name\" style=\"WIDTH: 30px\" noWrap command=\"fontname\" dropdown=\"true\"><img src=\"templates/form/std/imgEditor/cmd-fontname.gif\" WIDTH=\"24\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Font Size\" style=\"WIDTH: 30px\" noWrap command=\"fontsize\" dropdown=\"true\"><img src=\"templates/form/std/imgEditor/cmd-fontsize.gif\" WIDTH=\"24\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Font Color\" style=\"WIDTH: 30px\" noWrap command=\"fgcolor\" dropdown=\"true\"><img src=\"templates/form/std/imgEditor/cmd-fgcolor.gif\" WIDTH=\"24\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            if ("htmladvanced".equalsIgnoreCase(editor)) 
            {
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Insert Table\" style=\"WIDTH: 26px\" onclick=\"");
              toPrint.append(id);
              toPrint.append(".insertTable(");
              toPrint.append(id + "MessageBody");
              toPrint.append(
                  ");\" noWrap><img src=\"templates/form/std/imgEditor/cmd-table.gif\" ></td>\n");
              toPrint.append(
                  "                    <td style=\"PADDING-LEFT: 4px; WIDTH: 10px\">\n");
      
              toPrint.append(
                  "                      <div style=\"BORDER-LEFT: #c5c2b8 1px solid\">&nbsp;</div></td>\n");
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Insert Link\" style=\"WIDTH: 26px\" onclick=\"");
              toPrint.append(id);
              toPrint.append(
                  ".htmlExec('createlink',null,true)\" noWrap><img src=\"templates/form/std/imgEditor/cmd-link.gif\" ></td>\n");
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Insert Link to Content\" style=\"WIDTH: 26px\" onclick=\"");
              toPrint.append(id);
              toPrint.append(
                  ".insertLinkToContent(");
              toPrint.append(id + "MessageBody,'"+id+"'");    
              toPrint.append(");\" noWrap><img src=\"templates/form/std/imgEditor/cmd-linkcontent.gif\" ></td>\n");               
                  
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Insert Link to Local Image\" style=\"WIDTH: 26px\" onclick=\"");
              toPrint.append(id);
              toPrint.append(
                  ".insertLinkToImage(");
              toPrint.append(id + "MessageBody,'"+id+"'");    
              toPrint.append(");\" noWrap><img src=\"templates/form/std/imgEditor/cmd-imagelocal.gif\" ></td>\n");              
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Insert Link to External Image\" style=\"WIDTH: 26px\" onclick=\"");
              toPrint.append(id);
              toPrint.append(
                  ".htmlExec('insertimage')\" noWrap><img src=\"templates/form/std/imgEditor/cmd-imagelink.gif\" ></td>\n");
            }
        }
        else
        {
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Cut\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-cut.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Copy\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-copy.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Paste\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-paste.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            if ("htmladvanced".equalsIgnoreCase(editor))
            {
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Undo\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-undo.gif\" ></td>\n");        
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Redo\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-redo.gif\" ></td>\n");
            }
            toPrint.append("                    <td style=\"PADDING-LEFT: 4px; WIDTH: 10px\">\n");
            toPrint.append("                      <div style=\"BORDER-LEFT: #c5c2b8 1px solid\">&nbsp;</div></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Bold\" style=\"WIDTH: 26px\" noWrap tabindex='" + tabIndex + "'><b>B</b></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Italic\" style=\"WIDTH: 26px\" noWrap tabindex='" + tabIndex + "'><b><i>I</i></b></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Underline\" style=\"WIDTH: 26px\" noWrap tabindex='" + tabIndex + "'><b><u>U</u></b></td>\n");
            if ("htmladvanced".equalsIgnoreCase(editor))
            {
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"SuperScript\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-superscript.gif\" ></td>\n");
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Subscript\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-subscript.gif\" ></td>\n");
            }
            toPrint.append("                    <td style=\"PADDING-LEFT: 4px; WIDTH: 10px\">\n");
            toPrint.append("                      <div style=\"BORDER-LEFT: #c5c2b8 1px solid\">&nbsp;</div></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Align Left\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-justifyleft.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Center\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-justifycenter.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Align Right\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-justifyright.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td style=\"PADDING-LEFT: 4px; WIDTH: 10px\">\n");
            toPrint.append("                      <div style=\"BORDER-LEFT: #c5c2b8 1px solid\">&nbsp;</div></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Numbering\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-insertOrderedList.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Bullets\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-insertUnorderedList.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td style=\"PADDING-LEFT: 4px; WIDTH: 10px\">\n");
            toPrint.append("                      <div style=\"BORDER-LEFT: #c5c2b8 1px solid\">&nbsp;</div></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Increase Indent\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-indent.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Decrease Indent\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-outdent.gif\" WIDTH=\"16\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            if ("htmladvanced".equalsIgnoreCase(editor)) 
            {
              toPrint.append("</tr><tr>");
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Insert Horizontal Rule\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-hr.gif\" ></td>\n");
            }
            else
            {
                toPrint.append("                    <td style=\"PADDING-LEFT: 4px; WIDTH: 10px\">\n");
                toPrint.append("                      <div style=\"BORDER-LEFT: #c5c2b8 1px solid\">&nbsp;</div></td>\n");
            }
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Font Name\" style=\"WIDTH: 30px\" noWrap command=\"fontname\" dropdown=\"false\"><img src=\"templates/form/std/imgEditor/cmd-fontname.gif\" WIDTH=\"24\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Font Size\" style=\"WIDTH: 30px\" noWrap command=\"fontsize\" dropdown=\"false\"><img src=\"templates/form/std/imgEditor/cmd-fontsize.gif\" WIDTH=\"24\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            toPrint.append("                    <td class=\"htmlBtn\" title=\"Font Color\" style=\"WIDTH: 30px\" noWrap command=\"fgcolor\" dropdown=\"false\"><img src=\"templates/form/std/imgEditor/cmd-fgcolor.gif\" WIDTH=\"24\" HEIGHT=\"16\" tabindex='" + tabIndex + "'></td>\n");
            if ("htmladvanced".equalsIgnoreCase(editor)) 
            {
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Insert Table\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-table.gif\" ></td>\n");
              toPrint.append(
                  "                    <td style=\"PADDING-LEFT: 4px; WIDTH: 10px\">\n");
      
              toPrint.append(
                  "                      <div style=\"BORDER-LEFT: #c5c2b8 1px solid\">&nbsp;</div></td>\n");
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Insert Link\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-link.gif\" ></td>\n");
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Insert Link to Content\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-linkcontent.gif\" ></td>\n");               
                  
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Insert Link to Local Image\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-imagelocal.gif\" ></td>\n");              
              toPrint.append(
                  "                    <td class=\"htmlBtn\" title=\"Insert Link to External Image\" style=\"WIDTH: 26px\" noWrap><img src=\"templates/form/std/imgEditor/cmd-imagelink.gif\" ></td>\n");
            }
        }
    }
    private static void appendMaximizeCode(StringBuffer toPrint , AttributeHandler atrParent, boObject parentObj, docHTML doc, boolean disabled, int tabIndex, String frameName, String editor) 
    {
        toPrint.append("   <td class=\"htmlBtn\" title=\"Maximize\" style=\"WIDTH: 30px\" ")
            //.append("onclick = \"window.showModalDialog('__htmlArea.jsp?")
            .append("onclick = \"winmain().openModeDocUrl(getIDX(), 'bind','full', '__htmlArea.jsp', ")
            .append("'?parentBoui=").append(parentObj.getBoui())
            .append("&docid=").append(doc.getDocIdx())
            .append("&parentObj=").append(parentObj.getName())
            .append("&parentAttribute=").append(atrParent.getName());
        if(disabled)
        {
            toPrint.append("&disabled=on");
        }
        else
        {
            toPrint.append("&disabled=off");
        }
        toPrint.append("&editor=" + editor);
        toPrint.append("&attName=").append(frameName)
            .append("&actIdxClient='+getIDX()")
            .append(",'lookup')")
            //.append(",'0.5','dialogHeight: 600px; dialogWidth: 800px;edge: Raised; center: Yes; help: No; resizable: no; status: no;')")
            .append(";\" noWrap><img src=\"templates/form/std/imgEditor/resize.gif\" WIDTH=\"24\" HEIGHT=\"16\" ")
            .append("tabindex='" + tabIndex + "'></td></tr>\n");
    }


    public static void writeHTML_text(
        StringBuffer toPrint,
        StringBuffer Value,
        StringBuffer Name,
        StringBuffer id,
        int tabIndex,
        boolean isDisabled ,
        boolean isVisible ,
        boolean inEditTemplate,
        StringBuffer onChange,
        boolean isRequired,
        boolean isRecommend,
        int charLen,
        Hashtable xattributes
        ){
        if ( charLen > 0 && charLen < 256 ) 
        {
               toPrint.append("<input class='text' value=\"");
               String x=Value.toString().replaceAll("\"","&#34;");
               toPrint.append(x);
               toPrint.append("\" id='");
               toPrint.append( id );
               toPrint.append('\'');
               if ( isRequired )
               {
                   toPrint.append(" req=1 ");
               }
               if ( isRecommend )
               {
                   toPrint.append(" rcm=1 ");
               }
               if ( isDisabled ) {
                    toPrint.append(" disabled  ");
               }
               if ( !isVisible ) {
                   toPrint.append(" style='display:none' ");
               }
               
               //focus
                if(isVisible && !isDisabled)
                    toPrint.append(getonfocus(Name.toString()));
               
               if ( onChange.length()>0 ){
                   toPrint.append(" onchange='");
                   toPrint.append(onChange);
                   toPrint.append('\'');
               }
               toPrint.append(" maxlength=\"");
               toPrint.append(charLen).append("\"");
               toPrint.append(" name = '");
               toPrint.append( Name );
               toPrint.append("' tabindex='"+tabIndex+"'>");     
        }
        else
        {
          if(charLen == -1)
          {
            toPrint.append("<textarea style='height=100%' class='text' ");
          }
          else if(charLen >= 4000)
          {
            toPrint.append("<textarea style='height=100%' maxlength='" + charLen + "' class='text' ");
          }
          else
          {
            String h = (String)xattributes.get("height");
            String w = (String)xattributes.get("width");
            
            if(h == null || "".equals(h))
            {
              toPrint.append("<textarea style='height=20px' maxlength='" + charLen + "' class='text' ");
            }
            else
            {
              if(w == null || "".equals(w))
              {
                toPrint.append("<textarea style='height=100%' maxlength='" + charLen + "' class='text' ");
              }
              else
              {
                toPrint.append("<textarea style='height=100%;width="+w+"'  maxlength='" + charLen + "' class='text' ");
              }
            }
          }
			
          toPrint.append("' id='");
          toPrint.append( id );
          toPrint.append('\'');
          
          if ( isRequired )
          {
            toPrint.append(" req=1 ");
          }
          
          if ( isRecommend )
          {
            toPrint.append(" rcm=1 ");
          }
          
          if ( isDisabled )
          {
            toPrint.append(" disabled  ");
          }
          
          if ( !isVisible )
          {
            toPrint.append(" style='display:none' ");
          }
                   
          //focus
          if(isVisible && !isDisabled)
            toPrint.append(getonfocus(Name.toString()));
                   
          if ( onChange.length()>0 )
          {
            toPrint.append(" onchange='");
            toPrint.append(onChange);
            toPrint.append('\'');
          }
          
          if (charLen!=-1)
          {
            toPrint.append(" ONKEYPRESS=\" return verifySize(this, ");
            toPrint.append(charLen).append("); \"");
            toPrint.append(" onbeforepaste=\" return doBeforePaste(this, ");
            toPrint.append(charLen).append("); \"");
            toPrint.append(" onpaste=\" return doPaste(this, ");
            toPrint.append(charLen).append("); \"");
          }          
          toPrint.append(" name = '");
          toPrint.append( Name );
          toPrint.append("' tabindex='"+tabIndex+"'/>");
          toPrint.append(Value);
          toPrint.append("</textarea>");
        }
	}
    
   /* public static void writeHTML_iFile(
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

        StringBuffer value=new StringBuffer();
        if(atrParent.getValueString().length()>0) {
            value.append("<a target='_blank' href='attachfile.jsp?look_parentBoui=")
            .append(objParent.getBoui())
            .append("&att_display=n&att_download=y&docid=")
            .append(doc.getDocIdx())
            .append("' >")
            .append(atrParent.getValueString())
            .append("</a>");
        } 
        else 
        {
            value.append(atrParent.getValueString());
        }

        toPrint.append("<table id='")
        .append(id)
        .append("' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>")
        .append("<div class='lu ro'><span class='lui' ")
        .append(">")
        .append(value)
		.append("</div>")
        .append("</td>")
        .append("<td style='TEXT-ALIGN: right' width='25'><img style='CURSOR: default' tabIndex='")
        .append(tabIndex);
        
        if ( isDisabled ) {
                toPrint.append("' disabled src='templates/form/std/btn_dis_ifile.gif' ");
        }
        else {
            toPrint.append("' onmouseover='this.src=\"templates/form/std/btn_on_ifile.gif\"'  onmouseout='this.src=\"templates/form/std/btn_off_ifile.gif\"'  src='templates/form/std/btn_off_ifile.gif' ");
            if ( !isVisible ) {
                toPrint.append(" style='display:none' ");
            }
        }
        toPrint.append(Value);
        toPrint.append("' name='");
        toPrint.append(Name);
        toPrint.append("' req='");
        
        boolean req = (atrParent.getDefAttribute().getRequired().toUpperCase().equals("Y")||atrParent.getDefAttribute().getRequired().toUpperCase().equals("YES")) ? true:false;
        if ( req )  toPrint.append(1);
        else  toPrint.append(0);
        

        StringBuffer url = new StringBuffer();
        url.append("attachfile.jsp?")
        .append("docid=").append(doc.getDocIdx())
        .append("&look_parentObj=").append(objParent.getName())
        .append("&look_parentAttribute=").append(atrParent.getName())
        .append("&look_parentBoui=").append(objParent.getBoui())
        .append("&clientIDX='+getIDX()+'");
                

        toPrint.append("' onclick=\"winmain().openDoc('fixed,450px,210px,noresize',null,'','',")
        .append("'lookup','").append(url)
        .append("')\"");        
        toPrint.append(">"); 
        toPrint.append("</td></tr></tbody></table>");
    }*/
    public static void writeHTML_iFile(
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

        StringBuffer value=new StringBuffer();
        
//        String  XeoWin32Client_address = objParent.getEboContext().getXeoWin32Client_adress();
//        if ( XeoWin32Client_address != null && 
//             DocumentHelper.isMSWordFile( objParent ) && 
//             RegistryHelper.isClientConnected(XeoWin32Client_address)
//            )
//        {        
                                    
        if(isDocumentValidForClientRender(doc.getEboContext(),objParent.getBoDefinition().getName(),objParent.getBoui()))
        {
                if( isXeoControlActive( doc.getEboContext() ) )
                {
                    value.append("<span class=lui onclick=\"javascript:");
                    value.append("window.top.XEOControl.documentManager.OpenWordDocument('"+
                        objParent.getEboContext().getBoSession().getId()+"','"+
                        doc.getDochtmlController().poolUniqueId()+"|"+
                        doc.getDocIdx()+"',"+
                        objParent.getBoui()+");\">"
                    );
                    value.append(atrParent.getValueString())
                    .append("</span>");              
                }
                else
                {
                    value.append("<span class=lui onclick='javascript:")
    //                .append( "boForm.executeStaticMeth(\"netgest.bo.impl.document.DocumentHelper.openDocumentInClient\",[\"this\"]);")
                    .append( "boForm.executeStaticMeth(\"netgest.bo.impl.document.DocumentHelper.open\",[\"this\"]);")
                    .append("' >")
                    .append(atrParent.getValueString())
                    .append("</span>");              
                }
            
        }
        else
        {
            
            value.append("<iframe id='downloadframe_"+atrParent.getName()+"' name='downloadframe_"+atrParent.getName()+"' style='height:0px;width:0px;visibile:hidden'></iframe>");
            value.append("<script type='text/javascript'>");
            value.append("document.getElementById('downloadframe_"+atrParent.getName()+"').onreadystatechange=changeHiddenIFrame;");
            value.append("function changeHiddenIFrame(){");
            value.append("try{winmain().parent.setLogoutFlag(false);}catch(e){}");
            value.append("}</script>");
            
//            value.append("<a target='downloadframe_"+atrParent.getName()+"' href='attachfile.jsp?look_parentBoui=")
//            .append(objParent.getBoui());
//            if(atrParent != null)
//            {
//                value.append("&look_parentAttribute=").append(atrParent.getName());
//            }
//            value.append("&att_display=n&att_download=y&docid=")
//            .append(doc.getDocIdx())
//            .append("&curTime="+(new Date()).getTime()+"' >")
//            .append(atrParent.getValueString())
//            .append("</a>");
            value.append("<a href=\"javascript:void(0);\" onclick=\"try{winmain().parent.setLogoutFlag(true);}catch(ex){}");
            
            value.append("document.getElementById('downloadframe_" + atrParent.getName() + "').src='attachfile.jsp?look_parentBoui=");
            value.append(objParent.getBoui());
            if(atrParent != null)
            {
                value.append("&look_parentAttribute=");
                value.append(atrParent.getName());
            }
            value.append("&att_display=n&att_download=y&docid=");
            value.append(doc.getDocIdx());
            value.append("&curTime=" + (new Date()).getTime() + "';try{winmain().parent.setLogoutFlag(true);}catch(ex){}\">");
            
            value.append(atrParent.getValueString());
            value.append("</a>");
        }   
        
        toPrint.append("<table id='")
        .append(id)
        .append("' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>")
        .append("<div class='lu ro'><span class='lui' ")
        .append(">")
        .append(value)
		.append("</div>")
        .append("</td>")
        .append("<td style='TEXT-ALIGN: right' width='25'><img style='CURSOR: default' tabIndex='")
        .append(tabIndex);
        
        if ( isDisabled ) {
                toPrint.append("' disabled src='templates/form/std/btn_dis_ifile.gif' ");
        }
        else {
            toPrint.append("' onmouseover='this.src=\"templates/form/std/btn_on_ifile.gif\"'  onmouseout='this.src=\"templates/form/std/btn_off_ifile.gif\"'  src='templates/form/std/btn_off_ifile.gif' ");
            if ( !isVisible ) {
                toPrint.append(" style='display:none' ");
            }
        }
        toPrint.append(Value);
        toPrint.append("' name='");
        toPrint.append(Name);
        toPrint.append("' req='");
        
        boolean req = atrParent.getDefAttribute().getRequired()!=null&&atrParent.getDefAttribute().getRequired().getBooleanValue();
        if ( req )  toPrint.append(1);
        else  toPrint.append(0);
        

        StringBuffer url = new StringBuffer();
        url.append("attachfile.jsp?")
        .append("docid=").append(doc.getDocIdx())
        .append("&look_parentObj=").append(objParent.getName())
        .append("&look_parentAttribute=").append(atrParent.getName())
        .append("&look_parentBoui=").append(objParent.getBoui())
        .append("&curTime=").append((new Date()).getTime())
        .append("&clientIDX='+getIDX()+'");
                
                     
        toPrint.append("' onclick=\"winmain().openDoc('fixed,450px,210px,noresize',null,'','',")
        .append("'lookup','").append(url)
        .append("')\"");    
        toPrint.append(">"); 
        
        
        toPrint.append("</td>");
      

        toPrint.append("</tr></tbody></table>");
        
    }    
    private static String getonfocus(String fieldName)
    {
        return " onfocus='setFieldWFocus(\"" + fieldName + "\")' ";
    }
    
    private static String validateNumber(String s)
    {
        if(s.indexOf(",") == -1 && s.indexOf(".") != -1)
        {
            s = s.replace('.', ',');
        }
        return s;
    }
}
