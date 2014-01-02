/*Enconding=UTF-8*/
package netgest.bo.report;
import java.util.ArrayList;

import netgest.bo.def.*;
/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class XMLAttribute 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
 private String attBDName;
        private String attName;
        private String bridgeTable = null;
        private String type;
        private String description;
        private ArrayList relationObj;
        private boolean unique;
        private int min;
        private int max;
        private boolean indexed;
        private boDefAttribute attHandler;
        private boolean pk;
        private boolean fk;
        private String tableReferenced;
        public XMLAttribute(boDefAttribute h)
        {
            this.attHandler = h;
            relationObj = new ArrayList();
        }
        
        public boDefAttribute getDef()
        {
            return attHandler;
        }
        public void setAttributeName(String attName)
        {
            this.attName = attName;            
        }
        
        public String getAttributeName()
        {
            return attName;            
        }

        public void setTableReferenced(String tableReferenced)
        {
            this.tableReferenced = tableReferenced;            
        }
        
        public String getTableReferenced()
        {
            return tableReferenced;            
        }

        public void setBridgeTable(String bridgeTable)
        {
            this.bridgeTable = bridgeTable;            
        }
        
        public String getBridgeTable()
        {
            return bridgeTable;            
        }
 
        public void setAttributeType(String type)
        {
            this.type = type;            
        }
        
        public String getAttributeType()
        {
            return type;            
        } 
        
        public void setAttributeBDName(String attBDName)
        {
            this.attBDName = attBDName;            
        }
        
        public String getAttributeBDName()
        {
            return attBDName;
        }

        public void setDescription(String desc)
        {
            this.description = desc;            
        }
        
        public String getDescription()
        {
            return description;
        }        

        public void setIndexed(boolean b)
        {
            this.indexed = b;            
        }
        
        public boolean getIndexed()
        {
            return indexed;            
        }        

        public void setPrimaryKey(boolean b)
        {
            this.pk = b;            
        }
        
        public boolean getPrimaryKey()
        {
            return pk;            
        }  

        public void setForeignKey(boolean b)
        {
            this.fk = b;            
        }
        
        public boolean getForeignKey()
        {
            return fk;            
        }

        public void setUnique(boolean b)
        {
            this.unique = b;            
        }
        
        public boolean getUnique()
        {
            return unique;            
        }  

        public void setMin(int b)
        {
            this.min = b;            
        }
        
        public int getMin()
        {
            return min;            
        }  

        public void setMax(int b)
        {
            this.max = b;            
        }
        
        public int getMax()
        {
            return max;            
        }  
        

        public void addRelationObject(XMLObject obj)
        {
            relationObj.add(obj);            
        }
        
        public ArrayList getRelationObject()
        {
            return relationObj;
        } 
}