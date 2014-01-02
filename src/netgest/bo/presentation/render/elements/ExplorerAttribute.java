/*Enconding=UTF-8*/
package netgest.bo.presentation.render.elements;
import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import netgest.bo.def.*;
import javax.servlet.jsp.*;
import netgest.bo.presentation.render.ie.components.TreeBuilder;
import netgest.bo.ql.*;
import netgest.bo.runtime.*;
import netgest.utils.*;

public class ExplorerAttribute
    {   
        public String       p_sql;
        public String       p_sqlToGroup;
        public String       p_name;
        public String       p_format=null;
        
        private String      p_label="";
        private String      p_color="#000000";
        public Hashtable    p_valuesResult;
        public Hashtable    p_colorResult;
        public boolean      p_isAttribute;
        public boolean      p_isAttributeExternal;
        public boDefAttribute p_defatr;
        private int         p_width;   
        public byte         p_returnType;
        private String      p_showLink = null;
        private String      p_openDocument = null;
        
        //indicador de atributo dependete de uma bridge, indica qual o index (no p_attributes) do atributo do qual este depende
        public int p_bridgeInd = -1;
        //indica quantas bridges já se imprimiu
        public int p_countBr=0;
        
        
        
        public ExplorerAttribute( ngtXMLHandler attr , boDefHandler bodef, EboContext boctx )
        {
            String language=bodef.getBoDefaultLanguage();
            String attName = attr.getText();
            if( attName == null ||  attName.trim().length()==0 )
            {
                attName = attr.getNodeName();
            }

            p_name=attName;
            p_isAttribute=false;
            p_isAttributeExternal=false;
            boDefAttribute attrdef=null;;
            StringBuffer labelPrefix=new StringBuffer();
            if ( p_name.indexOf('.') > -1 )
            {
                p_isAttributeExternal=true;    
                String[] names=p_name.split("\\.");
                boDefHandler bod=bodef;
                for (int i = 0; i < names.length; i++) 
                {
                   attrdef=bod.getAttributeRef( names[i] , true );
                   
                   
                   if( i+1 < names.length )
                   {
                      labelPrefix.append( attrdef.getLabel() );
                      bod=attrdef.getReferencedObjectDef();
                      labelPrefix.append(">");
                   }
                   
                   
                }
                
            }
            else
            {
                attrdef=bodef.getAttributeRef( p_name , true );
            }
            
            // Label
            p_defatr=attrdef;
            ngtXMLHandler lblnode=attr.getChildNode("label");
            int width = ClassUtils.convertToInt( attr.getAttribute("width","0") );
            
            if ( attrdef!=null )
            {
                p_label=labelPrefix.toString()+attrdef.getLabel();
                p_isAttribute=true;
            }

            if ( lblnode !=null )
            {
                p_label=lblnode.getChildNode(language).getText();
            }

            // Format
            p_format =attr.getAttribute("format","");


            //sql
            ngtXMLHandler sqlnode=attr.getChildNode("sql");
            if ( sqlnode != null)
            {
                p_sql=sqlnode.getText().trim();
                p_sqlToGroup=p_sql;
                if ( p_sql.indexOf("CTX_PERFORMER_BOUI") > -1)
                {
                    p_sql=p_sql.replaceAll("CTX_PERFORMER_BOUI",""+ boctx.getBoSession().getPerformerBoui()  );
                    p_sqlToGroup=p_sql;
                }
                p_sql="["+p_sql+"]"; //required for use in QLparser
                String type=sqlnode.getAttribute("returnType","number");
                p_returnType =  boDefDataTypeMapping.getValueType( type );
                
                
            }
            else
            {
                if ( attrdef!=null )
                {
					p_sql=bodef.getBoExtendedTable()+".\""+ attrdef.getDbName()+"\"";
                    p_sqlToGroup="\""+attrdef.getDbName()+"\"";
                    if ( attrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                    {
                       if (  attrdef.getRelationType() == boDefAttribute.RELATION_MULTI_VALUES )
                       {
                              p_sql = attrdef.getDbTableName()+"."+attrdef.getDbTableChildFieldName();
                              p_sqlToGroup=p_sql;
                       }
                        else if ( attrdef.getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE )
                       {
                            
                             p_sql = attrdef.getBridge().getBoMasterTable()+"."+attrdef.getBridge().getChildFieldName();
                             p_sqlToGroup=p_sql;
                       }
                    }
                    
                    p_returnType=attrdef.getValueType();
                    if ( width == 0 )
                    {
                        if ( attrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                        {
                            width=150;
                        }
                        else 
                        {
                            
                            
          
                            byte type= attrdef.getValueType();
                            
                           
                            if (type == boDefAttribute.VALUE_NUMBER ) {
                                width=100;
                            } else if(type == boDefAttribute.VALUE_CHAR) {
                                width=attrdef.getLen();
                                if ( width > 200 ) width=200;
                                 if( attrdef.getLOVName().length()>0)
                                 {
                                     width=150;
                                 }
                            } else if (type == boDefAttribute.VALUE_BOOLEAN) {
                                width=50;
                            } else if (type == boDefAttribute.VALUE_DURATION) {
                                width=90;
                            } else if (type == boDefAttribute.VALUE_CURRENCY) {
                                width=90;
                            } else if (type == boDefAttribute.VALUE_DATETIME) {
                                width=110;
                            } else if (type == boDefAttribute.VALUE_DATE) {
                                width=90;
                            } else if (type == boDefAttribute.VALUE_CLOB) {
                                width=150;
                            } else if ( type == boDefAttribute.VALUE_BLOB ) {
                                width=150;
                            } else if (type == boDefAttribute.VALUE_IFILELINK) {
                                width=90;
                            }
                            else
                            {
                              width=40;    
                            }

                
                  
                            
                            
                        }
                    }
                    
                }
            }
            
            //showLink
            p_showLink = attr.getAttribute("showLink","");

            //openDocument
            p_openDocument = attr.getAttribute("openDocument","");
            
            
            
            //translated values and colors
            ngtXMLHandler valuesnode=attr.getChildNode("values");
            if (width==0) width=50;
            p_width=width;
            
            if( valuesnode != null )
            {
                ngtXMLHandler[] values=valuesnode.getChildNodes();
                for (int i = 0; i < values.length ; i++) 
                {
                    String result=values[i].getNodeName().substring(1);
                    String lbl=values[i].getChildNode("label").getChildNode(language).getText();
                    String color=null;
                    ngtXMLHandler clrnode=values[i].getChildNode("color");
                       
                    if ( clrnode!= null ) color=clrnode.getText();
                    
                    if ( p_valuesResult == null ) p_valuesResult=new Hashtable();
                    p_valuesResult.put( result ,lbl );
                    
                    if ( color!=null)
                    {
                        if ( p_colorResult == null ) p_colorResult=new Hashtable();
                        p_colorResult.put( result ,color);    
                    }
                }
            }
        }
        
        
        public ExplorerAttribute( String name , String label , boDefAttribute attrdef, boDefHandler bodef, EboContext boctx )
        {
            String language=bodef.getBoDefaultLanguage();
            p_name=name;
            p_isAttribute=true;
            
            if ( p_name.indexOf('.') > -1 )
            {
                p_isAttributeExternal=true;    
            }
          
            
            p_defatr=attrdef;
            p_label= label ;
            p_returnType=attrdef.getValueType();           
            p_sql=name;
            p_sqlToGroup=name;
            if ( attrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
            {
               if (  attrdef.getRelationType() == boDefAttribute.RELATION_MULTI_VALUES )
               {
//                      p_sql = attrdef.getDbTableName()+"."+attrdef.getDbTableChildFieldName();
//                      p_sqlToGroup=p_sql;
               }
                else if ( attrdef.getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE )
               {
                    
//                     p_sql = attrdef.getBridge().getBoMasterTable()+"."+attrdef.getBridge().getChildFieldName();
//                     p_sqlToGroup=p_sql;

               }
            }
            
           
            if ( attrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
            {
                p_width=150;
            }
            else 
            {
                byte type= attrdef.getValueType();
                
               
                if (type == boDefAttribute.VALUE_NUMBER ) {
                    p_width=100;
                } else if(type == boDefAttribute.VALUE_CHAR) {
                    p_width=100;
                } else if (type == boDefAttribute.VALUE_BOOLEAN) {
                    p_width=50;
                } else if (type == boDefAttribute.VALUE_DURATION) {
                    p_width=90;
                } else if (type == boDefAttribute.VALUE_CURRENCY) {
                    p_width=90;
                } else if (type == boDefAttribute.VALUE_DATETIME) {
                    p_width=110;
                } else if (type == boDefAttribute.VALUE_DATE) {
                    p_width=90;
                } else if (type == boDefAttribute.VALUE_CLOB) {
                    p_width=150;
                } else if ( type == boDefAttribute.VALUE_BLOB ) {
                    p_width=150;
                } else if (type == boDefAttribute.VALUE_IFILELINK) {
                    p_width=90;
                }
                else
                {
                  p_width=40;    
                }
                if ( p_width > 200 ) p_width=200;
                
                
            }
           
            
        }

        public int getWidth()
        {
            return p_width;
        }
        
        public String showLink()
        {
            return p_showLink;
        }
        
        public String openDocument()
        {
            return p_openDocument;
        }

        public void setWidth(int width)
        {
            p_width = width;
        }

        public boolean hasResults()
        {
            return p_valuesResult!=null;
        }
        public byte getType()
        {
            return p_returnType;
        }
        
        public String getLabel()
        {
            return p_label;
        }
       
        public String getName()
        {
            return p_name;
        }
        
        public String getValueResult(String value , EboContext boctx) throws boRuntimeException
        {
            return getValueResult(value, boctx, true);
        }
        
        public String getValueResult(String value , EboContext boctx, boolean showLink) throws boRuntimeException
        {
           String toRet="";
           if ( p_valuesResult != null ) 
           {
                if(value!=null)
                {
                  toRet = ( String) p_valuesResult.get( value );  
                  if ( toRet == null ) toRet=value;
                }
           }
           else
           {
               if (p_defatr != null &&  p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
               {
                   if ( value != null )
                   {
                       
                    long v = ClassUtils.convertToLong( value );
                    
                    if ( v > 0)
                    {
                        try
                        {
                            StringBuffer x=new StringBuffer();
                            x.append("<span style='white-space: nowrap;overflow-y:hidden;background-color:transparent;border:0' class='lu ro'>");
                            boObject o = boObject.getBoManager().loadObject(boctx, v );
                            if(showLink)
                            {
                                x.append("<span class='lui' onclick=\"");
                                x.append("winmain().openDoc('medium','");
                                x.append( o.getName().toLowerCase() );
                                x.append("','edit','method=edit&boui=");
                                x.append( v );
                                x.append("')");
                                x.append(";event.cancelBubble=true\"");
                                x.append(" boui='");
                                x.append(v);
                                x.append("' object='");
                                x.append(o.getName() );
                                x.append("'>");
                                x.append(o.getCARDID(false).toString());
                                x.append("</span></span>" );
                                toRet=x.toString();
                            }
                            else
                            {
                                x.append(o.getCARDID(false).toString());
                                x.append("</span>");
                                toRet=x.toString();
                            }
                        }
                        catch (Exception e)
                        {
                            StringBuffer x=new StringBuffer();
                            x.append("<span>");
                            x.append(e.getMessage() );
                            x.append("</span>");
                            toRet=x.toString();
                        }
                    }
                           
                
                   }
                   
               }
               else
               {
                 toRet=value;    
               }
               
               
           }
           return toRet;
        }
        
        public String getColorResult( String value )
        {
           if ( p_colorResult == null ) return null;
           return ( String) p_colorResult.get( value );
            
        }

    public String getFormat() {
        return p_format;
    }
} //END CLASS ATTRIBUTE
