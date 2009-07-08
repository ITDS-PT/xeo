/*Enconding=UTF-8*/
package netgest.bo.report;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import java.util.Vector;
import netgest.bo.def.*;
import netgest.bo.def.boDefHandler;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.EboContext;
/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class XMLObject 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
        private String tableName;
        private String objName;
        private String description;
        private ArrayList relationOneToOne;
        private ArrayList relationSimple;
        private ArrayList relationZeroToMany;
        private ArrayList views;
        private ArrayList attributes;
        private ArrayList fks;
        private String cap;
        private String anchor;
        private ArrayList bridgesTables;
        private Connection conn;
        
        public XMLObject(Connection conn)
        {
            relationOneToOne = new ArrayList();
            relationZeroToMany = new ArrayList();
            relationSimple = new ArrayList();
            attributes = new ArrayList();
            views = new ArrayList();
            bridgesTables = new ArrayList();
            fks = new ArrayList();
            this.conn = conn;
        }
        public void setCap(String cap)
        {
            this.cap = cap;            
        }
        
        public String getCap()
        {
            return cap;            
        }
        public void setAnchor(String anchor)
        {
            this.anchor = anchor;            
        }
        
        public String getAnchor()
        {
            return anchor;            
        }        
        public void setTableName(String attName)
        {
            this.tableName = tableName;            
        }
        
        public String getTableName()
        {
            return tableName;            
        }
        
        public void setObjName(String attBDName)
        {
            this.objName = objName;            
        }
        
        public String getObjName()
        {
            return objName;
        }

        public void setDescription(String desc)
        {
            this.description = desc;            
        }
        
        public String getDescription()
        {
            return description;
        }        

        public void addRelationOneToOne(XMLObject obj)
        {
            relationOneToOne.add(obj);            
        }
        
        public ArrayList getRelationOneToOne()
        {
            return relationOneToOne;
        }

        public void addRelationZeroToMany(XMLObject obj)
        {
            relationZeroToMany.add(obj);            
        }
        
        public ArrayList getRelationZeroToMany()
        {
            return relationZeroToMany;
        }
        
        public ArrayList getViews()
        {
            return views;
        }

        public ArrayList getFks()
        {
            return fks;
        }
        
        public ArrayList getSimpleRel()
        {
            return relationSimple;
        }
        
        public ArrayList getAttributes()
        {
            return attributes;
        }
        
        public ArrayList getTableBridges()
        {
            return bridgesTables;
        }
        
        public void setXMLObject(EboContext ctx, boDefHandler obj) throws Exception
        {
            objName = obj.getName();
            tableName = obj.getBoPhisicalMasterTable();
            description = obj.getDescription();
            boDefAttribute[] attDef = obj.getAttributesDef();
            XMLAttribute xmlAtt;
            PreparedStatement pst;
            ResultSet rs;
            for(int i = 0; i < attDef.length; i++)
            {
//                if(attDef[i].getDbIsTabled())
//                {
                     xmlAtt = new XMLAttribute(attDef[i]);
                     xmlAtt.setAttributeBDName(attDef[i].getDbName());
                     xmlAtt.setAttributeName(attDef[i].getName());
					 
					 //----------- get dos valores das LOV's ------------
                    String desc= attDef[i].getLabel();
                    if(desc==null)
                        desc=attDef[i].getDescription();
					 
					 if(attDef[i].getLOVName()!=null && !attDef[i].getLOVName().equalsIgnoreCase("")
                        && LovManager.getLovObject( ctx, attDef[i].getLOVName() ) != null
                     )
					 {
//						 PreparedStatement prep;
//						 ResultSet rset;
//						 
//						 desc+="<br><br><b>Lista de Valores:<b><br><br>";
//						 
//						 String sSql = "SELECT z.VALUE, z.description FROM ebo_lov x, ebo_lov$details y, ebo_lovdetails z "; 
//						 sSql += "WHERE ( (x.boui = y.parent$) AND (y.child$ = z.boui) ) ";
//						 sSql += "AND x.name LIKE ? ";
//						 
//						 prep = conn.prepareStatement(sSql);
//                         prep.setString(1, attDef[i].getLOVName());
//						 rset = prep.executeQuery();
						 
                         lovObject lov = LovManager.getLovObject( ctx, attDef[i].getLOVName() );
                         
//						 while(rset.next())
//						 {
//							 desc += (rset.getString("value")==null?"":rset.getString("value"));
//							 desc += " - ";
//							 desc += (rset.getString("description")==null?"":rset.getString("description"));
//							 desc += "<br>";
//						 }
//                         
                        lov.beforeFirst();
                         while(lov.next())
						 {
							 desc += lov.getCode();
							 desc += " - ";
							 desc += lov.getDescription();
							 desc += "<br>";
						 }
						 
//						 if(rset!=null) rset.close();
//						 if(prep!=null) prep.close();
					 }
					 
                     xmlAtt.setDescription(desc);
                     xmlAtt.setMax(attDef[i].getMaxOccurs());
                     xmlAtt.setMin(attDef[i].getMinOccurs());
                     xmlAtt.setUnique(attDef[i].getDbIsUnique());
                     xmlAtt.setIndexed(attDef[i].getDbIsIndexed());
					 
//                     xmlAtt.setAttributeName(attDef[i].getDescription()); 
                     
                      pst = conn.prepareStatement("select 1 from ngtdic where tablename = ? and expression like ? and objecttype = ?");
                      pst.setString(1, tableName.toUpperCase());
                      pst.setString(2, "%"+attDef[i].getDbName().toUpperCase()+"%");
                      pst.setString(3, "PK");
                      rs = pst.executeQuery();
                      if(rs.next())
                      {
                        xmlAtt.setPrimaryKey(true);                       
                      }
                      else
                      {
                        xmlAtt.setPrimaryKey(false);
                      }
                      pst.close();
                      rs.close();
                     
                     if(attDef[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                     {
                         if(attDef[i].getRelationType() == boDefAttribute.RELATION_1_TO_1 ||
                            attDef[i].getRelationType() == boDefAttribute.RELATION_MULTI_VALUES ||
                            attDef[i].getMaxOccurs() == 1
                         )
                         {
                            
                            boDefHandler def = attDef[i].getReferencedObjectDef();
                            if( def != null )
                            {
                                if( def.getClassType() == boDefHandler.TYPE_INTERFACE )
                                {
                                    StringBuffer sb = new StringBuffer();
                                    String[] implObjs = ((boDefInterface)def).getImplObjects();
                                    Vector tables = new Vector( );
                                    for (int k = 0; k < implObjs.length; k++) 
                                    {
                                        String objTables[] = getTablesFromObjectHier( implObjs[k] );
                                        for (int z=0 ;z < objTables.length; z++ ) 
                                        {
                                            if( !tables.contains( objTables[z] ) )
                                            {
                                                tables.add( objTables[z] );
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    String objTables[] = getTablesFromObjectHier( def.getName() );
                                }
                            }
                            
                         
                         
                            relationOneToOne.add(xmlAtt);
                            attributes.add(xmlAtt);
                            String childFieldName = attDef[i].getDbTableChildFieldName();                        
                            String sss = attDef[i].getDbTableFatherFieldName();
                            String ss = attDef[i].getDbTableName();
                            String ass = attDef[i].getDbName();
                            String aass = attDef[i].getTableName();
                            pst = conn.prepareStatement("select tablereference from ngtdic where objecttype = 'FK' and UPPER(tablename) = ? and UPPER(expression) like ?");
                            pst.setString(1, tableName.toUpperCase());
                            pst.setString(2, childFieldName);
                            rs = pst.executeQuery();
                            String aux;
                            if(rs.next())
                            {
                                xmlAtt.setTableReferenced( rs.getString(1) );
                            }
                            else
                            {
                                xmlAtt.setTableReferenced("&nbsp;");
                            }
                            pst.close();
                            rs.close();
                            
                         }
                         else
                         {
                            relationZeroToMany.add(xmlAtt);
                            attributes.add(xmlAtt);                            
                            pst = conn.prepareStatement("select FRIENDLYNAME from ngtdic where objecttype = 'T' and UPPER(tablename) = ? and UPPER(objectName) = ?");
                            pst.setString(1, attDef[i].getBridge().getBoMasterTable().toUpperCase());
                            pst.setString(2, attDef[i].getBridge().getBoMasterTable().toUpperCase());
                            rs = pst.executeQuery();
                            String aux;
                            if(rs.next())
                            {
                                xmlAtt.setTableReferenced(rs.getString(1));
                                bridgesTables.add(attDef[i].getBridge().getBoMasterTable() + "|" + rs.getString(1));
                            }
                            else
                            {
                                xmlAtt.setTableReferenced("&nbsp;");
                                bridgesTables.add(attDef[i].getBridge().getBoMasterTable() + "|" + "&nbsp;");
                            }
                            pst.close();
                            rs.close();
                        }
                     }
                     else
                     {
                        pst = conn.prepareStatement("Select fieldtype, fieldsize from ngtdic where tablename = ? and objectname = ?");
                        pst.setString(1, tableName.toUpperCase());
                        pst.setString(2, attDef[i].getDbName().toUpperCase());
                        rs = pst.executeQuery();
                        String typeAux="";
						String typeLen="";
                        if(rs.next())
                        {
                            typeAux = rs.getString(1);
							typeLen = rs.getString(2);
                        }
                        rs.close();
                        pst.close();
                        if(typeAux == null)
                        {
                            if(attDef[i].getType().toUpperCase().equals("DURATION"))
                            {
                                xmlAtt.setAttributeType("NUMBER");
                            }
                            else if(attDef[i].getType().toUpperCase().equals("IFILE"))
                            {
                                xmlAtt.setAttributeType("VARCHAR");
                            }
                            else
                            {
                                xmlAtt.setAttributeType("VARCHAR");
                            }
                        }
                        else if(typeAux.toUpperCase().equals("BL"))
                        {
                            xmlAtt.setAttributeType("BLOB");
                        }
                        else if(typeAux.toUpperCase().equals("CL"))
                        {
                            xmlAtt.setAttributeType("CLOB");
                        }
                        else if(typeAux.toUpperCase().equals("C"))
                        {
                            xmlAtt.setAttributeType("VARCHAR (" + typeLen + ")");
                        }
                        else if(typeAux.toUpperCase().equals("N"))
                        {
                            xmlAtt.setAttributeType("NUMBER");
                        }
                        else if(typeAux.toUpperCase().equals("D"))
                        {
                            xmlAtt.setAttributeType("DATE");
                        }
                        relationSimple.add(xmlAtt);
                        attributes.add(xmlAtt);
                     }
//                }
            }
            
            //
            pst = conn.prepareStatement("select tablename from ngtdic where objecttype = 'V' and UPPER(friendlyname) like ?");
            pst.setString(1, "%[" + tableName.toUpperCase() + "]%");
            rs = pst.executeQuery();
            String aux;
            while(rs.next())
            {
                views.add(rs.getString(1));
            }
            pst.close();
            rs.close();
            
            pst = conn.prepareStatement("select objectname, expression, tableReference, fieldReference from ngtdic where objecttype = 'FK' and UPPER(tablename) = ?");
            pst.setString(1, tableName.toUpperCase());
            rs = pst.executeQuery();
            String field;
            String fkName;
            String refTab;
            String refField;
            while(rs.next())
            {
                field = rs.getString(2);
                fkName = rs.getString(1);
                refTab = rs.getString(3);
                refField = rs.getString(4);
                String s = (field== null||"".equals(field)) ? "&nbsp;":field;
                s+="|";
                s+= (fkName== null||"".equals(fkName)) ? "&nbsp;":fkName;
                s+="|";
                s+= (refField== null||"".equals(refField)) ? "&nbsp;":refField;
                fks.add(s);
            }
            pst.close();
            rs.close();
        }
        
        private String[] getTablesFromObjectHier( String objName )
        {
            boDefHandler objDef = boDefHandler.getBoDefinition( objName );
            Vector tables = new Vector();
            if( objDef != null  )
            {
                tables.add( objDef.getName() );
                while( objDef.getBoSuperBo() != null )
                {
                    objDef = boDefHandler.getBoDefinition( objDef.getBoSuperBo() );
                    if( objDef != null )
                    {
                        tables.add( objDef.getBoPhisicalMasterTable() );
                    }
                }
            }
            return (String[])tables.toArray( new String[ tables.size() ] );
        }
}