/*Enconding=UTF-8*/
package netgest.bo.userquery;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import netgest.bo.def.*;
import netgest.bo.dochtml.*;
import netgest.bo.ql.*;
import netgest.bo.runtime.*;
import netgest.utils.*;


public final class userquery 
{
/*
KA_IC_CHAR[i++]	="EQUAL";
KA_IC_CHAR[i++]	="NOTEQUAL";
KA_IC_CHAR[i++]	="START";
KA_IC_CHAR[i++]	="FINISH";
KA_IC_CHAR[i++]	="IN";
KA_IC_CHAR[i++]	="NOTIN";
KA_IC_CHAR[i++]	="GREATER";
KA_IC_CHAR[i++]	="GREATER_EQUAL";
KA_IC_CHAR[i++]	="LESSER";
KA_IC_CHAR[i++]	="LESSER_EQUAL";
KA_IC_CHAR[i++]	="ISNOTNULL";
KA_IC_CHAR[i++]	="ISNULL";
*/

 
 
    private userquery()
    {
    }
    
    public static boObjectList getUserQueries( EboContext ctx , String masterObjectName ) 
    {    
        
     String boql="select Ebo_Filter where masterObjectClass.name='"+masterObjectName+"' and ( share in ( select iXEOUser.queues where boui=CTX_PERFORMER_BOUI) or  share in ( select iXEOUser.groups where boui=CTX_PERFORMER_BOUI) or share in ( select iXEOUser.roles where boui=CTX_PERFORMER_BOUI) or share =CTX_PERFORMER_BOUI )";
     return  boObjectList.list( ctx , boql , 1 ,100 );
      
    
    
    }

    public static String parCode(String join, int pos)
    {
        if("LPAR".equals(join))
        {
            return "(";
        }
        if("RPAR".equals(join))
        {
         return ")";
        }
        if("ELPAR".equals(join))
        {
            return "and(";
        }
        if("ERPAR".equals(join))
        {
            return ")and";
        }
        if("OLPAR".equals(join))
        {
            return "or(";
        }
        if("ORPAR".equals(join))
        {
            return ")or";
        }
        if("EBPAR".equals(join))
        {
            return ")and(";
        }
        if("OBPAR".equals(join))
        {
            return ")or(";
        }
        if("EMPTY".equals(join))
        {
            return "";
        }
        if ( pos==0 ) return "";
        return join;
    }


    public static boolean paranteses(String join, boolean left)
    {
        if("LPAR".equals(join) && left)
        {
            return true;
        }
        if("RPAR".equals(join) && !left)
        {
         return true;
        }
        if("ELPAR".equals(join) && left)
        {
            return true;
        }
        if("ERPAR".equals(join) && !left)
        {
            return true;
        }
        if("OLPAR".equals(join) && left)
        {
            return true;
        }
        if("ORPAR".equals(join) && !left)
        {
            return true;
        }
        if("EBPAR".equals(join) && left)
        {
            return true;
        }
        if("OBPAR".equals(join) && left)
        {
            return true;
        }
        return false;
    }

    public static String userQueryToSql( EboContext ctx , String text )
    {
        return userquery.userQueryToSql( ctx , text , false );
    }
    public static String userQueryToBoql_ClauseWhere( EboContext ctx , String text ) 
    {
        int x=1;
        
        if( text.indexOf("cleanFilter") > -1) return ""; //a mudar
        ngtXMLHandler xml= new ngtXMLHandler( text ).getFirstChild();
        String object=xml.getAttribute("object");
        String onlyObjects=xml.getAttribute("onlyObjects","");
        String restrictQuery=xml.getChildNode("boql") == null ? "": 
                    (xml.getChildNode("boql").getText() == null ?"":xml.getChildNode("boql").getText());
        
        ngtXMLHandler[] xmlAllQueries=xml.getChildNodes();
        ngtXMLHandler[] xmlQueries;
        
        boDefHandler bodef= boDefHandler.getBoDefinition( object );
        if(xml.getChildNode("boql") != null)
        {
            xmlQueries = new ngtXMLHandler[xmlAllQueries.length - 1];
            int pos = 0;
            for (int i = 0; i < xmlAllQueries.length; i++) 
            {
                if(!"boql".equalsIgnoreCase(xmlAllQueries[i].getNodeName()))
                {
                    xmlQueries[pos] = xmlAllQueries[i];
                    pos++;
                }
            }
        }
        else
        {
            xmlQueries = xmlAllQueries;
        }
        
        //String boql="select "+object+" where ";
        String totalBoql="(";
        boolean leftPar = false, rightPar = false;
        
        for (int i = 0; i < xmlQueries.length ; i++) 
        {
            String boql="";
            String join         = xmlQueries[i].getChildNode("join").getText();
            String attributeName= xmlQueries[i].getChildNode("attribute").getText();
            String condition    = xmlQueries[i].getChildNode("condition").getText();
            String value        = xmlQueries[i].getChildNode("value").getText();
            String subquery     = xmlQueries[i].getChildNode("subquery").getText();
            boolean nullIgnore     = "1".equals(xmlQueries[i].getChildNode("nullIgnore").getText());
            String question = null;
            if(xmlQueries[i].getChildNode("question") != null)
            {
                question = xmlQueries[i].getChildNode("question").getText();
            }
            if(!(nullIgnore && (question != null && question.trim().length() > 0) && (value == null || value.trim().length() == 0)))
            {
                
                if(!"LPAR".equals(join) && leftPar)
                {
                    join="";
                    leftPar = false;
                }
                leftPar = paranteses(join, true);
                rightPar = paranteses(join, false);
                if ( totalBoql.length() > 1 ) join = parCode(join, i);
                else join="";
    
                if(!leftPar && !rightPar) 
                {
                //{
                    boDefAttribute defAttribute=bodef.getAttributeRef( attributeName,true );
                
                
                
                    if ( defAttribute != null )
                    {
                        
                        
                        byte type       = defAttribute.getAtributeType();
                        String lovName  = defAttribute.getLOVName();
                        if ( lovName== null || lovName.equals("") ) lovName=null;
                        if ( type== boDefAttribute.TYPE_ATTRIBUTE  || type == boDefAttribute.TYPE_STATEATTRIBUTE )
                        {
                            
                                byte typeValue= defAttribute.getValueType();
                                
                                String decimals="";
                                
                                if ( typeValue == boDefAttribute.VALUE_BOOLEAN || lovName!=null || type == boDefAttribute.TYPE_STATEATTRIBUTE )
                                {
                                    
                                    String[] values=new String[1];;
                                    if ( value != null && value.indexOf(";") > -1 )
                                    {
                                        values=value.split(";");
                                        if ( values.length == 1 )
                                        {
                                            value=values[0];
                                        }
                                        
                                    }
                                    else
                                    {
                                        values[0]=value;
                                    }
                                    if ( condition.equals("EQUAL") || condition.equals("IN") )
                                    {
                                        if ( values.length > 1 )
                                        {
                                            boql+="( ";
                                        }
                                        for (int j = 0; j < values.length ; j++) 
                                        {
//                                              if ( typeValue == boDefAttribute.VALUE_BOOLEAN || typeValue == boDefAttribute.VALUE_CHAR || typeValue == boDefAttribute.VALUE_DATE || typeValue == boDefAttribute.VALUE_DATETIME || typeValue == boDefAttribute.VALUE_CLOB )
//                                              {
//                                                 boql+=attributeName+" = '"+values[j]+"'";
//                                              }
                                              /*else*/ if ( typeValue == boDefAttribute.VALUE_NUMBER )
                                              {
                                                 boql+=attributeName+" = "+values[j];   
                                              }
                                              else
                                              {
                                                 boql+=attributeName+" = '"+values[j]+"'";
                                              }
                                             if ( j+1 < values.length )
                                             {
                                                 boql+=" or ";
                                             }
                                            
                                        }
                                        
                                        if ( values.length > 1 )
                                        {
                                            boql+=" ) ";
                                        }
                                        
                                    }
                                    else if ( condition.equals("NOTEQUAL") )
                                    {
                                         
                                         if ( values.length > 1 )
                                        {
                                            boql+="( ";
                                        }
                                        for (int j = 0; j < values.length ; j++) 
                                        {
                                              if ( typeValue == boDefAttribute.VALUE_CHAR || typeValue == boDefAttribute.VALUE_DATE || typeValue == boDefAttribute.VALUE_DATETIME || typeValue == boDefAttribute.VALUE_CLOB )
                                              {
                                                 boql+=attributeName+" <> '"+values[j]+"'";
                                              }
                                              else if ( typeValue == boDefAttribute.VALUE_NUMBER )
                                              {
                                                 boql+=attributeName+" <> "+values[j];   
                                              }
                                             if ( j+1 < values.length )
                                             {
                                                 boql+=" and ";
                                             }
                                            
                                        }
                                        
                                        if ( values.length > 1 )
                                        {
                                            boql+=") ";
                                        }
                                    }
                                   
                                    
                                    else if ( condition.equals("NOTIN") )
                                    {
                                        
                                        if ( values.length > 1 )
                                        {
                                            boql+="( ";
                                        }
                                        for (int j = 0; j < values.length ; j++) 
                                        {
                                              if ( typeValue == boDefAttribute.VALUE_CHAR || typeValue == boDefAttribute.VALUE_DATE || typeValue == boDefAttribute.VALUE_DATETIME || typeValue == boDefAttribute.VALUE_CLOB )
                                              {
                                                 boql+=attributeName+" <> '"+values[j]+"'";
                                              }
                                              else if ( typeValue == boDefAttribute.VALUE_NUMBER )
                                              {
                                                 boql+=attributeName+" <> "+values[j];   
                                              }
                                             if ( j+1 < values.length )
                                             {
                                                 boql+=" and ";
                                             }
                                            
                                        }
                                        if ( values.length > 1 )
                                        {
                                            boql+=") ";
                                        }
                                        
                                    }
                                    else if ( condition.equals("ISNULL") )
                                    {
                                        boql=attributeName+" is null ";
                                    }
                                    else if ( condition.equals("ISNOTNULL") )
                                    {
                                        boql=attributeName+" is not null";
                                    }
                                    else if ( condition.equals("START") )
                                    {
                                        boql=attributeName+" like '"+value+"%'";
                                    }
                                    else if ( condition.equals("FINISH") )
                                    {
                                        boql=attributeName+" like '%"+value+"'";
                                    }
                                    else if ( condition.equals("GREATER") )
                                    {
                                        boql=attributeName+" > '"+value+"'";
                                    }
                                    else if ( condition.equals("LESSER") )
                                    {
                                        boql=attributeName+" < '"+value+"'";
                                    }
                                    else if ( condition.equals("GREATER_EQUAL") )
                                    {
                                        boql=attributeName+" >= '"+value+"'";
                                    }
                                    else if ( condition.equals("LESSER_EQUAL") )
                                    {
                                        boql=attributeName+" <= '"+value+"'";
                                    }
                                    
                                    
                                    
                                }
                                else if ( typeValue == boDefAttribute.VALUE_CHAR || typeValue == boDefAttribute.VALUE_CLOB )
                                {
                                
                                    if ( value != null )value=value.replaceAll("'","''");
                                    if ( condition.equals("EQUAL") )
                                    {
                                        if ( typeValue == boDefAttribute.VALUE_CLOB || value.indexOf("%") > -1 )
                                        {
                                            boql=attributeName+" like '"+value+"'";
                                        }
                                        else boql=attributeName+" = '"+value+"'";
                                        
                                    }
                                    else if ( condition.equals("NOTEQUAL") )
                                    {
                                        if ( typeValue == boDefAttribute.VALUE_CLOB || value.indexOf("%") > -1 )
                                        {
                                            boql=attributeName+" not like '"+value+"'";
                                        }
                                        else boql=attributeName+" <> '"+value+"'";
                                        
                                    }
                                    else if ( condition.equals("START") )
                                    {
                                        boql=attributeName+" like '"+value+"%'";
                                    }
                                    else if ( condition.equals("FINISH") )
                                    {
                                        boql=attributeName+" like '%"+value+"'";
                                    }
                                    else if ( condition.equals("IN") )
                                    {
                                        boql=attributeName+" like '%"+value+"%'";
                                    }
                                    else if ( condition.equals("NOTIN") )
                                    {
                                        boql=attributeName+" not like '%"+value+"%'";
                                    }
                                    else if ( condition.equals("GREATER") )
                                    {
                                        boql=attributeName+" > '"+value+"'";
                                    }
                                    else if ( condition.equals("LESSER") )
                                    {
                                        boql=attributeName+" < '"+value+"'";
                                    }
                                    else if ( condition.equals("GREATER_EQUAL") )
                                    {
                                        boql=attributeName+" >= '"+value+"'";
                                    }
                                    else if ( condition.equals("LESSER_EQUAL") )
                                    {
                                        boql=attributeName+" <= '"+value+"'";
                                    }
                                    else if ( condition.equals("ISNULL") )
                                    {
                                        boql=attributeName+" is null ";
                                    }
                                    else if ( condition.equals("ISNOTNULL") )
                                    {
                                        boql=attributeName+" is not null";
                                    }
                                    
                                }
                                
                                 else if (  typeValue == boDefAttribute.VALUE_DATE || typeValue == boDefAttribute.VALUE_DATETIME  )
                                {
                                
                                    String format="";
                                    if ( typeValue == boDefAttribute.VALUE_DATE  ) format = "'YYYY-MM-DD'";
                                    else format = "'YYYY-MM-DD HH24:MI:SS'";
                                  
                                    if (value != null )
                                        value=value.replaceAll("\\/","-");
                                    if (value!=null && value.substring(0,3 ).indexOf("-") > -1 )
                                    {
                                        if ( typeValue == boDefAttribute.VALUE_DATE  ) format = "'DD-MM-YYYY'";
                                        else format = "'DD-MM-YYYY HH24:MI:SS'";    
                                    }
                                    if ( value != null )value=value.replaceAll("'","''");
                                    if ( condition.equals("EQUAL") )
                                    {
                                        
                                        boql=attributeName+" = to_Date('"+ value.replace('T',' ') +"',"+format+" ) ";
                                        
                                    }
                                    else if ( condition.equals("NOTEQUAL") )
                                    {
                                        
                                        boql=attributeName+" <> to_Date('"+ value.replace('T',' ') +"',"+format+" ) ";
                                        
                                    }
                                    else if ( condition.equals("GREATER") )
                                    {
                                        boql=attributeName+" > to_Date('"+ value.replace('T',' ') +"',"+format+" ) ";
                                    }
                                    else if ( condition.equals("LESSER") )
                                    {
                                        boql=attributeName+" < to_Date('"+ value.replace('T',' ') +"',"+format+" ) ";
                                    }
                                    else if ( condition.equals("GREATER_EQUAL") )
                                    {
                                        boql=attributeName+" >= to_Date('"+ value.replace('T',' ') +"',"+format+" ) ";
                                    }
                                    else if ( condition.equals("LESSER_EQUAL") )
                                    {
                                        boql=attributeName+" <= to_Date('"+ value.replace('T',' ') +"',"+format+" ) ";
                                    }
                                    else if ( condition.equals("ISNULL") )
                                    {
                                        boql=attributeName+" is null ";
                                    }
                                    else if ( condition.equals("ISNOTNULL") )
                                    {
                                        boql=attributeName+" is not null";
                                    }
                                    
                                }
        
                               
                                else if ( typeValue == boDefAttribute.VALUE_NUMBER )
                                {
                                    if ( condition.equals("EQUAL") )
                                    {
                                       boql=attributeName+" = "+value;
                                        
                                    }
                                    else if ( condition.equals("NOTEQUAL") )
                                    {
                                        boql=attributeName+" <> "+value;
                                        
                                    }
                                    else if ( condition.equals("GREATER") )
                                    {
                                        boql=attributeName+" > "+value;
                                    }
                                    else if ( condition.equals("LESSER") )
                                    {
                                        boql=attributeName+" < "+value;
                                    }
                                    else if ( condition.equals("GREATER_EQUAL") )
                                    {
                                        boql=attributeName+" >= "+value;
                                    }
                                    else if ( condition.equals("LESSER_EQUAL") )
                                    {
                                        boql=attributeName+" <= "+value;
                                    }
                                    else if ( condition.equals("ISNULL") )
                                    {
                                        boql=attributeName+" is null ";
                                    }
                                    else if ( condition.equals("ISNOTNULL") )
                                    {
                                        boql=attributeName+" is not null";
                                    }
                                
                                
                                }
                                            
                            
                                
                        }
                        else if ( type == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                        {
                            byte typeRelation =  defAttribute.getRelationType();
                            char charTypeRel=' ';
                            if ( typeRelation == boDefAttribute.RELATION_1_TO_1 )
                            {
                                charTypeRel='1';
                            }
                            else if (typeRelation == boDefAttribute.RELATION_1_TO_N )
                            {
                                charTypeRel='N';
                            }
                            else if (typeRelation == boDefAttribute.RELATION_1_TO_N_WBRIDGE)
                            {
                                charTypeRel='N';
                            }
                            
                            String[] values=new String[1];;
                                    
                                    if ( value!=null)
                                    {
                                        if ( value.indexOf(";") > -1 )
                                        {
                                            values=value.split(";");
                                            if ( values.length == 1 )
                                            {
                                                value=values[0];
                                            }
                                            
                                        }
                                        else
                                        {
                                            values[0]=value;
                                        }
                                    }
                                    
                                    if ( condition.equals("EQUAL") || condition.equals("IN") )
                                    {
                                        boql=""; 
                                        if ( values.length > 1 )
                                        {
                                            boql="(";
                                        }
                                        for (int j = 0; j < values.length ; j++) 
                                        {
                                              
                                             boql+=attributeName+" = "+values[j];   
                                             if ( j+1 < values.length )
                                             {
                                                 boql+=" or ";
                                             }
                                            
                                        }
                                        if ( values.length > 1 )
                                        {
                                            boql+=")";
                                        }
                                        
                                        
                                    }
                                    else if ( condition.equals("NOTEQUAL") )
                                    {
                                        boql=""; 
                                        if ( values.length > 1 )
                                        {
                                            boql+="( ";
                                        }
                                        for (int j = 0; j < values.length ; j++) 
                                        {
                                             boql+=attributeName+" <> "+values[j];   
                                             if ( j+1 < values.length )
                                             {
                                                 boql+=" or ";
                                             }
                                            
                                        }
                                        if ( values.length > 1 )
                                        {
                                            boql+=") ";
                                        }
                                        
                                    }
                                    else if ( condition.equals("NOTIN") )
                                    {
                                        boql="";
                                        if ( values.length > 1 )
                                        {
                                            boql+="( ";
                                        }
                                        for (int j = 0; j < values.length ; j++) 
                                        {
                                             
                                             boql+=attributeName+" <> "+values[j];   
                                             if ( j+1 < values.length )
                                             {
                                                 boql+=" and ";
                                             }
                                            
                                        }
                                        if ( values.length > 1 )
                                        {
                                            boql+=")";
                                        }
                                    }
                                    else if ( condition.equals("ISNULL") )
                                    {
                                        boql=attributeName+" is null ";
                                    }
                                    else if ( condition.equals("ISNOTNULL") )
                                    {
                                        boql=attributeName+" is not null";
                                    }
                            
                            
                            
                            
                        }
                        
                        if ( !boql.equals("") )
                        {
                            totalBoql+=join+" "+ boql +" ";
                            
                        }
                        
                    }
                }
                else
                {
                    totalBoql+=join;
                }
            }
        }
        if("(".equals(totalBoql))
        {
            totalBoql = "";
        }
        else
        {
            totalBoql = totalBoql + ")";
        }
        if ( onlyObjects!=null && onlyObjects.length()>=1 )
            {
                if( totalBoql.length() > 0 ) totalBoql+=" and ( ";
                else totalBoql+=" ( ";
                
                String[] ex=onlyObjects.split(";");
                for (int j = 0; j < ex.length ; j++) 
                {
                    totalBoql+=" className = '"+ex[j]+"'";
                    if( j+1 < ex.length )
                    {
                        totalBoql+=" or ";
                    }
                }
                totalBoql+=" ) ";
                
            }

        if(restrictQuery != null && !"".equals(restrictQuery) && 
            restrictQuery.toUpperCase().indexOf("WHERE") != -1)
        {
            if(totalBoql == null || "".equals(totalBoql))
            {
                totalBoql = "(" + restrictQuery.substring(restrictQuery.toUpperCase().indexOf("WHERE") + 5) + ")";
            }
            else
            {
                totalBoql = "(" + totalBoql + " and (" + restrictQuery.substring(restrictQuery.toUpperCase().indexOf("WHERE") + 5) + "))";
            }
        }

        return totalBoql;
        
    }
    
    
    
    public static String userQueryToBoql_ClauseWhere( EboContext ctx,long boui ,ArrayList userParameters ) throws boRuntimeException
    {
        String toRet=null;
        String _filterXML=userquery.userQueryToXML(ctx,boui,true,userParameters);
        if ( _filterXML != null )  toRet=userquery.userQueryToBoql_ClauseWhere(ctx , _filterXML );
        return toRet;
        
    }

    public static boolean hasHintNoSecurity( EboContext ctx,long boui ,ArrayList userParameters ) throws boRuntimeException
    {
        String _filterXML=userquery.userQueryToXML(ctx,boui,true,userParameters);
        return (_filterXML!= null && _filterXML.indexOf("NO_SECURITY") > -1) ? true:false;
    }
    
    public static String userQueryToSql( EboContext ctx , String text, boolean extend )
    {
        
        String toRet=null;
        String totalBoql = userquery.userQueryToBoql_ClauseWhere( ctx , text );
        ngtXMLHandler xml= new ngtXMLHandler( text ).getFirstChild();
        String object=xml.getAttribute("object");
        if ( !totalBoql.equals("") )
        {
            
            
            QLParser xparser= new QLParser();
            toRet = xparser.toSql( " select "+object+( extend? " ext":"")+" where "+totalBoql, ctx );
            
            
        }
        else if(object != null)
        {
           
                QLParser xparser= new QLParser();
                toRet = xparser.toSql( " select "+object+( extend? " ext":"")+"", ctx );
           
        }
        else
        {
            toRet = "";
        }
        
        return toRet;
    }
    
    public static String userQueryToSql( EboContext ctx , long boui , ArrayList userParameters ) throws boRuntimeException
    {
        return userquery.userQueryToSql( ctx , boui , false , userParameters);
    }
    
    public static String userQueryToXML( EboContext ctx,long boui , boolean extend , ArrayList userParameters  ) throws boRuntimeException
    {
        int x=1;
        String toRet=null;
        boObject o=boObject.getBoManager().loadObject(ctx ,"Ebo_Filter", boui );
        if ( o != null )
        {
            boObject cls = o.getAttribute("masterObjectClass").getObject();
            if ( cls != null )
            {
                
                String objectName= cls.getAttribute("name").getValueString();
                String onlyObjects= o.getAttribute("onlyObjects").getValueString();
                
                
                bridgeHandler qrys=o.getBridge("details");
                String _filterXML ="<filter object=\""+objectName+"\" onlyObjects=\""+onlyObjects+"\">";
                if(o.getAttribute("boql").getValueString() != null && 
                    !"".equals(o.getAttribute("boql").getValueString()) )
                {
                    _filterXML = _filterXML + "<boql><![CDATA[" + o.getAttribute("boql").getValueString() + "]]></boql>"; 
                }
                
                int pointerParameter=0;
                qrys.beforeFirst();
                String question = null;
                boolean nullIgnore =false;
                  while ( qrys.next() )
                  {
                  
                    _filterXML+="<query><join>"+qrys.getObject().getAttribute("joinQuery").getValueString();
                    _filterXML+="</join><attribute>"+qrys.getObject().getAttribute("attributeName").getValueString();
                    _filterXML+="</attribute><condition>"+qrys.getObject().getAttribute("operator").getValueString();
                    question = qrys.getObject().getAttribute("question").getValueString();
                    nullIgnore = "1".equals(qrys.getObject().getAttribute("nullIgnore").getValueString());
                     if (  question != null && question.trim().length() > 0 )
                     {
                        String valueParameter = null;
                        if(pointerParameter < userParameters.size())
                        {
                            valueParameter = (String) userParameters.get( pointerParameter++ );
                        }
                        if( valueParameter == null ) valueParameter ="";
                        _filterXML+="</condition><value><![CDATA["+ valueParameter ;
                        _filterXML+="]]></value><question>";
                        _filterXML+=qrys.getObject().getAttribute("question").getValueString()+"</question>";
                        if(nullIgnore)
                        {
                            _filterXML+="<nullIgnore>1</nullIgnore>";
                        }
                        else
                        {
                            _filterXML+="<nullIgnore>0</nullIgnore>";
                        }
                     }
                     else
                     {
                        _filterXML+="</condition><value><![CDATA["+qrys.getObject().getAttribute("value").getValueString();
                        _filterXML+="]]></value><question></question><nullIgnore>0</nullIgnore>";
                     }
                    _filterXML+="<subquery>"+qrys.getObject().getAttribute("subFilter").getValueString();
                    _filterXML+="</subquery></query>";
               
                  }
                  _filterXML+="</filter>";
                  toRet=_filterXML;
            }
            
        }
        
        return toRet;
        
    }

    
    public static String userQueryToSql( EboContext ctx,long boui , boolean extend , ArrayList userParameters ) throws boRuntimeException
    {
        int x=1;
        String toRet=null;
        String _filterXML = userQueryToXML( ctx , boui , extend , userParameters );
        if ( _filterXML != null )
            toRet=userquery.userQueryToSql(ctx , _filterXML, extend );
             
        return toRet;
        
    }

   
    
    public static void readParameters( EboContext ctx,long boui, ArrayList paramValue, ArrayList nullIgnore ) throws boRuntimeException
    {
        int x=1;
        
        boObject o=null;
        if(boui != -1)
            o=boObject.getBoManager().loadObject(ctx ,"Ebo_Filter", boui );

        if ( o != null )
        {
            boObject cls = o.getAttribute("masterObjectClass").getObject();
            if ( cls != null )
            {
                
                
                  bridgeHandler qrys=o.getBridge("details");
                  qrys.beforeFirst();String auxSS;
                  while ( qrys.next() )
                  {
                  
                    auxSS = qrys.getObject().getAttribute("question").getValueString();
                     if (auxSS != null && auxSS.trim().length() > 0)
                     {
                          String value = qrys.getObject().getAttribute("value").getValueString();
                          if ( value.length() > 0 )
                          {
                            paramValue.add( value );  
                          }
                          else
                          {
                            paramValue.add( null );
                          }
                          
                          value = qrys.getObject().getAttribute("nullIgnore").getValueString();
                          if ( "1".equals(value) )
                          {
                            nullIgnore.add( "1" );  
                          }
                          else
                          {
                            nullIgnore.add( "0" );
                          }
                     }
                    
               
                  }
                
                  
            }
            
        }
    }

    
}