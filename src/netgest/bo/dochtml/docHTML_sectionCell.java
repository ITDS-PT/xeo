/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import java.util.*;
import netgest.bo.def.*;

public final class docHTML_sectionCell  {
    public boDefAttribute p_atr;
    public String p_text;
    public Hashtable p_attributes;
    public String p_viewmode;
    public String p_method;
    public docHTML_sectionField p_field;
    public Vector p_fields;
    public Vector p_fieldsSize;
    public Vector p_fieldsAttr;
    public boolean p_multiFields=false;

    public ICustomField p_customRender;
    
    public docHTML_sectionCell(boDefAttribute atr,Hashtable attributes,String method,String viewmode){
        p_atr=atr;
        p_method=method;
        p_viewmode=viewmode;
        p_attributes=attributes;
    }
    public docHTML_sectionCell(boDefAttribute atr,Hashtable attributes){
        p_atr=atr;
        p_attributes=attributes;
        
    }
    
    public docHTML_sectionCell( ICustomField customRender , boDefAttribute atr, Hashtable attributes )
    {   
        p_customRender = customRender;
        p_atr          = atr;
        p_attributes   = attributes;
    }
    
     public docHTML_sectionCell(String text , boDefAttribute atr,Hashtable attributes)
     {
        p_atr=atr;
        p_attributes=attributes;
        p_text=text;
        
    }


    public docHTML_sectionCell( docHTML_sectionField field, Hashtable attributes){
        p_field=field;
        p_attributes=attributes;
    }

    public docHTML_sectionCell(){
        
    }
    
    public docHTML_sectionCell( boolean multiFields )
    {
        p_multiFields=true;
        p_fields=new Vector();
        p_fieldsSize=new Vector();
        p_fieldsAttr=new Vector();
    }
    
    public void addField( docHTML_sectionField field, String size , Hashtable attributes  )
    {
        p_fields.add( field);
        p_fieldsSize.add( size );
        p_fieldsAttr.add( attributes );
        p_attributes=attributes;
    }
}