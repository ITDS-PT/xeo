package netgest.bo.dochtml;
import java.io.CharArrayWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import netgest.bo.def.*;
import java.util.*;
import netgest.bo.ejb.*;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.*;
import java.lang.reflect.*;

public final class docHTML_gridCol  {
    public int              p_width;
    public boDefAttribute   p_atr;
    public String           p_method;
    public String           p_viewmode;
    public boolean          p_isBridgeAttr;
    public Hashtable        p_attributes;
    public boolean          p_isAbstractAttr;
    public String           p_atrName;
    public String           p_specialName=null;
    public StringBuffer     p_label = null;
    public Hashtable        p_values = null;
    
    public docHTML_gridCol(boDefAttribute atr , boolean isBridgeAttr , int width,Hashtable attributes, Hashtable p_values){
        p_width         = width;
        p_atr           = atr;
        p_attributes    = attributes;
        p_isBridgeAttr  = isBridgeAttr;
        p_isAbstractAttr= false;
        p_atrName       = atr.getName();
        this.p_values   = p_values;
    }
    public docHTML_gridCol(boDefAttribute atr , boolean isBridgeAttr, int width, String method, String viewmode,Hashtable attributes, Hashtable p_values){
        p_width         = width;
        p_atr           = atr;
        p_method        = method;
        p_viewmode      = viewmode;
        p_attributes    = attributes;
        p_isBridgeAttr  = isBridgeAttr;
        p_isAbstractAttr= false;
        p_atrName       = atr.getName();
        this.p_values   = p_values;
    }
    
    public docHTML_gridCol(String atrName , boolean isBridgeAttr , int width,Hashtable attributes, Hashtable p_values){
        p_width         = width;
        p_atr           = null;
        p_attributes    = attributes;
        p_isBridgeAttr  = isBridgeAttr;
        p_isAbstractAttr= true;
        p_atrName       = atrName;
        this.p_values   = p_values;
    }
    public docHTML_gridCol(String atrName , boolean isBridgeAttr, int width, String method, String viewmode,Hashtable attributes, Hashtable p_values){
        p_width         = width;
        p_atr           = null;
        p_method        = method;
        p_viewmode      = viewmode;
        p_attributes    = attributes;
        p_isBridgeAttr  = isBridgeAttr;
        p_isAbstractAttr= true;
        p_atrName       = atrName;
        this.p_values   = p_values;
    }

    public docHTML_gridCol(String specialName , int width, Hashtable attributes, Hashtable p_values){
        p_width         = width;
        p_atr           = null;
        p_attributes    = attributes;
        p_specialName   = specialName;
        p_isAbstractAttr=false;
        this.p_values   = p_values;
    }

    public StringBuffer getLabel()
    {
        if ( p_label != null ) return p_label;
        
        p_label=new StringBuffer();
        if ( p_isAbstractAttr )
        {
            
            if ( p_attributes.get("referenceLabel")!=null )
            {
                String[] ref = (( String ) p_attributes.get("referenceLabel")).split("\\.");
                if ( ref.length > 1) 
                {
                    boDefHandler def = boDefHandler.getBoDefinition( ref[0] );
                    if ( def != null )
                    {
                        boDefAttribute atrdef = def.getAttributeRef( ref[1] );
                        if ( atrdef != null )
                        {
                            p_label.append( atrdef.getLabel() );    
                        }
                    }
                }
            }
        }
        else
        {
            if ( p_specialName == null )
            {
                p_label.append(p_atr.getLabel());    
            }
            else
            {
                p_label.append("");
            }
            
            
        }
        
        return p_label;
    }
    public String getHTML( boObject obj, bridgeHandler bridge ) throws boRuntimeException
    {
            if ( bridge.getName().equals("Ebo_ShortCut") && p_atrName.equals("action") )
              return "<span exec='yes' style='display:none'>"+bridge.getAttribute( p_atrName ).getValueString()+"</span>";
            else
            {
             AttributeHandler attr=bridge.getAttribute( p_atrName );
             if ( attr.getDefAttribute().getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE )
             {
                 long b=bridge.getAttribute( p_atrName ).getValueLong();
                 if ( b > 0 )
                 {
                     boObject o = bridge.getAttribute( p_atrName ).getObject();
                     return o.getCARDID().toString();
                 }
                 else return "";
             }
             else if("boolean".equalsIgnoreCase(attr.getDefAttribute().getType()))
             {
                String value = bridge.getAttribute( p_atrName ).getValueString();
                if("0".equals(value))
                {
                    //falta verificar a lingua
                    return "Não";
                }
                else if("1".equals(value))
                {
                    return "Sim";
                }
                return value;
             }             
             else if(bridge.getAttribute( p_atrName ).getDefAttribute().getLOVName() != null &&  
                !"".equals(bridge.getAttribute( p_atrName ).getDefAttribute().getLOVName()))
             {
                String xlov = bridge.getAttribute( p_atrName ).getDefAttribute().getLOVName(); 
                String value = bridge.getAttribute( p_atrName ).getValueString();
                if(value != null && !"".equals(value))
                {
                    lovObject lovObj = LovManager.getLovObject(obj.getEboContext(), xlov);
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
                return bridge.getAttribute( p_atrName ).getValueString();
             }
             else if("dateTime".equalsIgnoreCase(attr.getDefAttribute().getType()))
             {
                Date d = null;            
                if((d = attr.getValueDate()) != null)
                {
                     SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
                     return formatter.format(d);
                }
                return "";
             }
             else if("date".equalsIgnoreCase(attr.getDefAttribute().getType()))
             {
                Date d = null;            
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
                if(bridge.getAttribute( p_atrName ).getDefAttribute().getDecimals() != 0)
                {
                    //currency
                    currencyFormatter.setParseIntegerOnly(false);
                    if("Y".equalsIgnoreCase(bridge.getAttribute( p_atrName ).getDefAttribute().getGrouping()))
                    {
                        currencyFormatter.setGroupingUsed(true);
                    }
                    currencyFormatter.setMaximumFractionDigits(bridge.getAttribute( p_atrName ).getDefAttribute().getDecimals());
                    currencyFormatter.setMinimumFractionDigits(bridge.getAttribute( p_atrName ).getDefAttribute().getMinDecimals());
                    currencyFormatter.setMinimumIntegerDigits(1);
                    return currencyFormatter.format(bridge.getAttribute( p_atrName ).getValueDouble());
                }
                else if("Y".equalsIgnoreCase(bridge.getAttribute( p_atrName ).getDefAttribute().getGrouping()))
                {
                    currencyFormatter.setParseIntegerOnly(false);
                    currencyFormatter.setMinimumIntegerDigits(1);
                    currencyFormatter.setGroupingUsed(true);
                    return currencyFormatter.format(bridge.getAttribute( p_atrName ).getValueDouble());
                }
                return bridge.getAttribute( p_atrName ).getValueString();
             }
            }
    }
    
    public String getHTML( boObject obj ) throws boRuntimeException
    {
        if ( p_specialName == null )
        {
            if ( obj.getName().equals("Ebo_ShortCut") && p_atrName.equals("action") )
              return "<span exec='yes' style='display:none'>"+obj.getAttribute( p_atrName ).getValueString()+"</span>";
            else
            {
             AttributeHandler attr=obj.getAttribute( p_atrName );
             if ( attr.getDefAttribute().getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE )
             {
                 long b=obj.getAttribute( p_atrName ).getValueLong();
                 if ( b > 0 )
                 {
                     boObject o = obj.getBoManager().loadObject( obj.getEboContext(), b );
                     return o.getCARDID().toString();
                 }
                 else return "";
             }
             else if("boolean".equalsIgnoreCase(attr.getDefAttribute().getType()))
             {
                String value = obj.getAttribute( p_atrName ).getValueString();
                if("0".equals(value))
                {
                    //falta verificar a lingua
                    return "Não";
                }
                else if("1".equals(value))
                {
                    return "Sim";
                }
                return value;
             }             
             else if(obj.getAttribute( p_atrName ).getDefAttribute().getLOVName() != null &&  
                !"".equals(obj.getAttribute( p_atrName ).getDefAttribute().getLOVName()))
             {
                String xlov = obj.getAttribute( p_atrName ).getDefAttribute().getLOVName(); 
                String value = obj.getAttribute( p_atrName ).getValueString();
                if(value != null && !"".equals(value))
                {
                    boObject lov;
                    lovObject lovObj = LovManager.getLovObject( obj.getEboContext(), xlov );
                    if( lovObj != null )
                    {
                        lovObj.beforeFirst();
                        while(lovObj.next())
                        {
                            if(value.equalsIgnoreCase( lovObj.getCode() ))
                            {
                                return lovObj.getDescription();
                            }
                        }
                    }
                }
                return obj.getAttribute( p_atrName ).getValueString();
             }
             else if("dateTime".equalsIgnoreCase(attr.getDefAttribute().getType()))
             {
                Date d = null;            
                if((d = attr.getValueDate()) != null)
                {
                     SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
                     return formatter.format(d);
                }
                return "";
             }
             else if("date".equalsIgnoreCase(attr.getDefAttribute().getType()))
             {
                Date d = null;            
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
                if(obj.getAttribute( p_atrName ).getDefAttribute().getDecimals() != 0)
                {
                    //currency
                    currencyFormatter.setParseIntegerOnly(false);
                    if("Y".equalsIgnoreCase(obj.getAttribute( p_atrName ).getDefAttribute().getGrouping()))
                    {
                        currencyFormatter.setGroupingUsed(true);
                    }
                    currencyFormatter.setMaximumFractionDigits(obj.getAttribute( p_atrName ).getDefAttribute().getDecimals());
                    currencyFormatter.setMinimumFractionDigits(obj.getAttribute( p_atrName ).getDefAttribute().getMinDecimals());
                    currencyFormatter.setMinimumIntegerDigits(1);
                    return currencyFormatter.format(obj.getAttribute( p_atrName ).getValueDouble());
                }
                else if("Y".equalsIgnoreCase(obj.getAttribute( p_atrName ).getDefAttribute().getGrouping()))
                {
                    currencyFormatter.setParseIntegerOnly(false);
                    currencyFormatter.setMinimumIntegerDigits(1);
                    currencyFormatter.setGroupingUsed(true);
                    return currencyFormatter.format(obj.getAttribute( p_atrName ).getValueDouble());
                }
                return obj.getAttribute( p_atrName ).getValueString();
             }
            }
        }
        else
        {
           if ( p_specialName.equalsIgnoreCase("method") )
           {
              String methName=(String)p_attributes.get("method");
              try{
              Method ometh = obj.getClass().getMethod(methName,new Class[0]);
              if(ometh!=null) {
            
                  return (ometh.invoke(obj,new Object[0]).toString());
              }
              }
              catch(Exception e) {}
           }
           else if ( p_specialName.equalsIgnoreCase("CARDID") )
           {
               
               return obj.getCARDID().toString();
           }
           return "";
              
        }
        
        
    }
    
    public String getHTML( boObject obj , bridgeHandler bridge , docHTML doc , docHTML_controler DOCLIST  ) throws boRuntimeException 
    {
        String toRet="";
        if ( p_isBridgeAttr )
        {
          String linkto=( String ) p_attributes.get("linkto");
          if ( linkto != null )
          {
                if ( linkto.equalsIgnoreCase("configObject") )
                {
                  
                     BridgeObjAttributeHandler xobj=( BridgeObjAttributeHandler )bridge.getAttribute( p_atrName );
                     String xvalue=xobj.getValueString();
                     String url="?docid='+getDocId()+'";
                      url+="&DAOcode="+bridge.getAttribute("code").getValueString();
                      url+="&DAOBoui="+bridge.getParent().getBoui();
                      url+="&lineBridge="+xobj.getLine();
                      url+="&parentIDX='+getIDX()+'";
                      url+="&parentAttribute="+bridge.getName();
                      url+="&ownerBoui="+bridge.getObject().getBoui();
                     if ( xvalue.equals("")) 
                     {
                      
                      
                      toRet="<span class='link' onclick=\"winmain().openDocUrl('medium','__configobject.jsp','"+url+"','formula','"+doc.getDocIdx()+"',getIDX());window.event.cancelBubble=true;\">Configurar</span>";
                            
                     }
                     else
                     {
                         boObject DAO_config=xobj.getObject();
                         String xstr=DAO_config.getTextCARDID().toString();
                         if ( xstr.trim().length() < 4)
                         {
                             xstr="Configurar";
                         }
                         toRet="<span class='link' onclick=\"winmain().openDocUrl('medium','__configobject.jsp','"+url+"','formula','"+doc.getDocIdx()+"',getIDX());window.event.cancelBubble=true;\">"+xstr+"</span>";
                     }
                      
                }
          }
          else
          {
              toRet = bridge.getAttribute( p_atrName ).getValueString();
              StringBuffer toPrint = new StringBuffer();
              Hashtable xattributes = new Hashtable(); 
              xattributes.put("noRenderTemplate","y");
              xattributes.put("noClock","y");
              if( bridge.getAttribute( p_atrName ).isDisabled() )
              {
                  toRet=getHTML(obj, bridge);
              }
              else
              {
                  docHTML_section.renderHTMLObject(toPrint, DOCLIST.countFields++ ,bridge.getAttribute( p_atrName ) , bridge.getParent() , DOCLIST , doc, xattributes, p_values);
                  toRet=toPrint.toString();
              }
          }
        }
        else
        {
            if ( p_specialName == null )
            {
//                obj.getAttribute( p_atrName ).getValueString();
                toRet = getHTML(obj);
            }
            else
            {   
                if ( p_specialName.equalsIgnoreCase("childObject.cardid"))
                {
                    toRet=bridge.getObject().getTextCARDID().toString();
                }
                //psantos ini 20061206
                else if (p_specialName.toLowerCase().startsWith("childobject.attributes")) 
                {
                    // entra uma coisa do tipo childobject.attributes[attributo1, attributo2, attributo3]
                    // ou childobject.attributes[attributo1, attributo2, attributo3/classe] em que a classe é
                    // um ICustomField responsável pelo render
                    // cria um vector com os nomes dos atributos
                    String aux = p_specialName.trim().substring(p_specialName.indexOf("[")+1, p_specialName.lastIndexOf("]"));
                    aux = aux.trim();
                    Vector v = new Vector();
                    if (aux.length()!=0)
                    {
                        while (true)
                        {
                            int pos = aux.indexOf(",");
                            if (pos == -1) 
                            {
                                if (!"".equals(aux.trim()))
                                    v.add(aux.trim());
                                break;
                            }
                            else
                            {
                                String s = aux.substring(0,pos).trim();
                                if (!"".equals(s))
                                    v.add(s);
                                aux = aux.substring(pos+1,aux.length());
                            }
                        }
                    }
                    
                    // vai ao objecto ver se esses atributos existem
                    for (int i = 0; i < v.size(); i++)
                    {
                        String elemento = (String)v.get(i);
                        String s = "";
                        String classe = "";
                        int pos = elemento.indexOf("/"); 
                        if (pos == -1)
                            s = elemento;
                        else
                        {
                            s = elemento.substring(0,pos);
                            classe = elemento.substring(pos+1,elemento.length()); 
                        }
                        AttributeHandler atr = obj.getAttribute(s);
                        if (atr != null) 
                        {
                            StringBuffer toPrint = new StringBuffer();
                            Hashtable xattributes = new Hashtable(); 
                            xattributes.put("noRenderTemplate","y");
                            xattributes.put("noClock","y");
                            
                            if (!"".equals(classe))
                            {
                                try
                                {
                                    ICustomField field = (ICustomField)Class.forName(classe).newInstance();
                                    CharArrayWriter cr = new CharArrayWriter();
                                    PrintWriter out = new PrintWriter(cr);
                                    field.render(obj.getEboContext(), DOCLIST, doc, obj, out, atr);
                                    out.flush();
                                    toRet+=cr.toString();
                                } 
                                catch (Exception e)
                                {
                                    throw new boRuntimeException(obj,e.getMessage(),"",e.getCause());
                                }
                            }
                            else
                            {
                                docHTML_section.renderHTMLObject(toPrint, DOCLIST.countFields++ ,atr , obj , DOCLIST , doc, xattributes, p_values);
                                toRet+=toPrint.toString();
                            }

                          // toRet +="<br>";
                        }
                    }
                    //toRet=;
                }
                //psantos fim 20061206
                else if ( p_specialName.equalsIgnoreCase("CARDID") )
                {
                   return obj.getCARDID().toString();
                }
                else if ( p_specialName.equalsIgnoreCase("CHOOSE_I") )
                {
                   //return obj.getCARDID().toString();
                   if ( obj.getName().equalsIgnoreCase("Ebo_ClsReg") )
                   {
                      String toRet1="<span class='link' onclick=\"";
                      toRet1+="LookupObjects( '' , 'single','"+obj.getAttribute("name").getValueString()+"','"+
                      bridge.getParent().getName()+"','"+
                      bridge.getParent().bo_boui+"','"+
                      bridge.getParentAtributeName()+"',1,'"+doc.getDocIdx()+"',false,'bouiToReplace="+bridge.getObject().bo_boui+"' );event.cancelBubble=true;\"";
                      toRet1+=">Definir</span>";
                      return toRet1; 
                   }
                   else if ( obj.getName().equalsIgnoreCase("Ebo_Template") )
                   {
                      
                       
                      String toRet1="<span class='link' onclick=\"";
                      toRet1+="LookupObjects( 'TEMPLATE="+obj.getBoui()+"' , 'single','"+obj.getAttribute("masterObjectClass").getObject().getAttribute("name").getValueString()+"','"+
                      bridge.getParent().getName()+"','"+
                      bridge.getParent().bo_boui+"','"+
                      bridge.getParentAtributeName()+"',1,'"+doc.getDocIdx()+"',false,'bouiToReplace="+bridge.getObject().bo_boui+"' );event.cancelBubble=true;\"";
                      toRet1+=">Definir</span>";
                      return toRet1;
                       
                   }
                   else
                   {
                      // return "Redefinir";
                      return "";
                   }
                   
                }
                else toRet=p_specialName;
            }
        }
        return toRet;
    }
    
      public String getHTML( boObject obj ,  docHTML doc , docHTML_controler DOCLIST  ) throws boRuntimeException 
    {
        String toRet="";
         if ( p_specialName == null )  
         {
         
          toRet="";
          toRet = obj.getAttribute( p_atrName ).getValueString();
          StringBuffer toPrint = new StringBuffer();
          Hashtable xattributes = new Hashtable();     
       //   if ( obj.getParentBridge() == null )
        //  {
     
              xattributes.put("noRenderTemplate","y");
              xattributes.put("noClock","y");
               docHTML_section.renderHTMLObject(toPrint, DOCLIST.countFields++ ,obj.getAttribute( p_atrName ) , obj , DOCLIST , doc, xattributes, p_values);              
        //  }
//          else
 //         {
   //             bridgeHandler bridge = obj.getParentBridge(); 
     //           docHTML_section.renderHTMLObject(toPrint, DOCLIST.countFields++ ,bridge.getAttribute( p_atrName ) , bridge.getParent() , DOCLIST , doc, xattributes);              
      //    }
              
          toRet=toPrint.toString();
         }
         else
         {   
            toRet=p_specialName;
            if ( p_specialName.equalsIgnoreCase("method") )
           {
              String methName=(String)p_attributes.get("method");
              try{
              Method ometh = obj.getClass().getMethod(methName,new Class[0]);
              if(ometh!=null) {
            
                  return (ometh.invoke(obj,new Object[0]).toString());
              }
              }
              catch(Exception e) {}
           }
           
         }
        
    return toRet;
    }
    public char typeSorted(){
       // returns ' ' if not sorted
       // return 'A' se a coluna estiver ordenada ascedente
       // return 'D' se a coluna estiver ordenada descendente
        return ' ';
    }
}

    