/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import java.util.*;
import netgest.bo.def.*;

public final class docHTML_sectionRow  {
    public Vector cells;

    
    public docHTML_sectionRow() {
       cells=new Vector();

    }
    
    public void addCell(boDefAttribute atr,Hashtable attributes){
          cells.add(new docHTML_sectionCell(atr,attributes));
    }
    public void addCell(boDefAttribute atr,Hashtable attributes,String method,String viewmode){
        cells.add(new docHTML_sectionCell(atr,attributes,method,viewmode));
    }
    
   public void addCell(String text, boDefAttribute atr,Hashtable attributes)
   {
        cells.add(new docHTML_sectionCell(text,atr,attributes));
   }
   public void addCell( ICustomField fieldRender, boDefAttribute atr, Hashtable attributes )
   {
        cells.add( new docHTML_sectionCell( fieldRender, atr, attributes ) );       
   }


    public void addCell(docHTML_sectionField field,Hashtable attributes){
          cells.add(new docHTML_sectionCell(field,attributes));
    }
    public docHTML_sectionCell addCellMultiField(){
          docHTML_sectionCell xcell=new docHTML_sectionCell( true );
          cells.add( xcell);
          return xcell;
    }

    public void addCell(){
        cells.add(new docHTML_sectionCell());
    }

  
}