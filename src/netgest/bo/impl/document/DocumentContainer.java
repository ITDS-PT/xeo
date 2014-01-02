/*Enconding=UTF-8*/
package netgest.bo.impl.document;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

public class DocumentContainer 
{    
    private String filename = "noNameDefine.xeo";
    private String filetmp = String.valueOf(System.currentTimeMillis()) + "_xeo.tmp";
    private InputStream input = null;
    private long size = -1;    
    private boolean display = false;
    

    public void setFilename(String filename)
    {
        this.filename = filename;
    }


    public String getFilename()
    {
        return filename;
    }

    public InputStream getInputStream()
    {
        return input;      
    }
    
    public void setInputStream(InputStream input)
    {
        this.input = input;
    }

    public void setSize(long size)
    {
        this.size = size;
    }


    public long getSize()
    {
        return size;
    }
    public void setSizeFromTmpFile()
    {
        File deltmp = new File(filetmp);
        setSize(deltmp.length());
    }

    public void setFileTmp(String filetmp)
    {
        this.filetmp = filetmp;
    }


    public String getFileTmp()
    {
        return filetmp;
    }

    public void release()
    {
        File file = new File(filetmp);
        file.delete();                   
    }

    public void setDisplay(boolean display)
    {
        this.display = display;
    }

    public boolean display()
    {
        return display;
    }
    public GridDefinition getGridDefinition()
    {
        return new GridDefinition();
    }
    public class GridDefinition implements Serializable 
    {
        public String attrName;
        public String attrLabel;
        public String size;
        
        public void setAttrName(String attrName)
        {
            this.attrName = attrName;
        }
        
        public String getAttrName()
        {
            return attrName;
        }        

        public void setAttrLabel(String attrLabel)
        {
            this.attrLabel = attrLabel;
        }
        
        public String getAttrLabel()
        {
            return attrLabel;
        }  
        
        public void setSize(String size)
        {
            this.size = size;
        }
        
        public String getSize()
        {
            return size;
        }                
    }        
}