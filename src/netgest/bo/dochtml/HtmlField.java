/*Enconding=UTF-8*/
package netgest.bo.dochtml;


/**
 *
 * @Company Enlace3
 * @author Francisco Câmara
 * @version 1.0
 * @since
 */
public class HtmlField
{
    /**
     *
     * @Company Enlace3
     * @since
     */
    private String value;
    private String htmlId;
    private String bdId;
    private String labelName = null;
    private boolean disable;
    private boolean required;
    private boolean hidden;
    private boolean bool = false;
    private boolean toRefresh = false;
    private int tab;
    private int fieldId;
    private boolean isSection = false;
    private boolean buttonEndDisabled = false;
    private boolean buttonNextDisabled = false;
    private boolean buttonPreviousDisabled = false;

    public HtmlField(String value, String htmlId, String bdId, boolean disable, boolean required, 
        boolean hidden)
    {
        this.value = value;
        this.htmlId = htmlId;
        this.bdId = bdId;
        this.hidden = hidden;
        this.disable = disable;
        this.required = required;
        toRefresh = false;
    }

    public HtmlField(String value, String htmlId, String bdId, boolean disable, boolean required, 
        boolean hidden, int fieldNumber, int tab)
    {
        this.value = value;
        this.htmlId = htmlId;
        this.bdId = bdId;
        this.hidden = hidden;
        this.disable = disable;
        this.required = required;
        if(htmlId.indexOf(String.valueOf(fieldNumber)) == -1)
        {
            labelName = htmlId + String.valueOf(fieldNumber); 
        }
        this.fieldId = fieldNumber;
        this.tab = tab;
        toRefresh = false;
    }

    public String getValue()
    {
        return value;
    }

    public String getTreatValue()
    {
        if(value == null) return "";
        String s = escapeCode(value);  
        return s;
    }

    public String getHtmlId()
    {
        return htmlId;
    }

    public String getLabelName()
    {
        return labelName;
    }

    public String getBdId()
    {
        return bdId;
    }
    
    public boolean getDisable()
    {
        return disable;
    }
    
    public boolean getRequired()
    {
        return required;
    } 

    public boolean getHidden()
    {
        return hidden;
    }

    public void setValue(String nValue)
    {
        this.value = nValue;
    }
    
    public void setDisable(boolean b)
    {
        this.disable = b;
    }
    
    public void setRequired(boolean b)
    {
        this.required = b;
    } 

    public void setHidden(boolean b)
    {
        this.hidden = b;
    }
    
    public void setBool()
    {                
        StringBuffer auxSb = new StringBuffer("_ignore_");
        auxSb.append(htmlId);
        htmlId = auxSb.toString();
        bool = true;
    }
    
    public void setFieldId(int id)
    {
        fieldId = id;
    }

    public int getFieldId()
    {
        return fieldId;
    }

    public void setTabIndex(int tab)
    {
        this.tab = tab;
    }

    public int getTab()
    {
        return tab;
    }
    
    public boolean getBool()
    {
        return bool;
    }
    public static String escapeCode(String s)
    {
        String aux = new String(s);
        try
        {
            if(aux == null) return "";
            aux=aux.replaceAll("\\\\", "\\\\\\\\");
            aux = aux.replaceAll("'", "\\\\'");
            aux = aux.replaceAll("\n", "\\\\\\n");
            aux = aux.replaceAll("\r", "\\\\\\r");
            return aux;
        }catch(Exception e)
        {
            return s;
        }
        
    }
    public boolean setToRefresh(boolean b)
    {
        return toRefresh = b;
    }
    
    public boolean isToRefresh()
    {
        return toRefresh;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer("HtmlField [");

        sb.append("value: ");
        sb.append(value);
        sb.append(", htmlId: ");
        sb.append(htmlId);
        sb.append(", bdId: ");
        sb.append(bdId);
        sb.append(", labelName: ");
        sb.append(labelName);
        sb.append(", disable: ");
        sb.append(disable);
        sb.append(", required: ");
        sb.append(required);
        sb.append(", hidden: ");
        sb.append(hidden);
        sb.append(", bool: ");
        sb.append(bool);
        sb.append(", tab: ");
        sb.append(tab);
        sb.append(", fieldId: ");
        sb.append(fieldId);
        sb.append("]");
        return sb.toString();  
    }
    
    public int getHashcode()
    {
        return toString().hashCode();
    }
    
    public void setSection(boolean b)
    {
        isSection = b;
    }
    
    public boolean isSection()
    {
        return isSection;
    }
    
    public void setButtonEnd(boolean disabled)
    {
        this.buttonEndDisabled = disabled;
    }
    
    public boolean isButtonEndDisabled()
    {
        return this.buttonEndDisabled; 
    }
    
    public void setButtonPrevious(boolean disabled)
    {
        this.buttonPreviousDisabled = disabled;
    }
    
    public boolean isButtonPreviousDisabled()
    {
        return this.buttonPreviousDisabled; 
    }
    
    public void setButtonNext(boolean disabled)
    {
        this.buttonNextDisabled = disabled;
    }
    
    public boolean isButtonNextDisabled()
    {
        return this.buttonNextDisabled; 
    }
}
