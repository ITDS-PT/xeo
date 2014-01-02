/*Enconding=UTF-8*/
package netgest.bo.dochtml;

public final class docHTML_sectionField  {

    public static final byte HTML_TEXT      = 0 ;
    public static final byte HTML_DATE      = 1 ;
    public static final byte HTML_DATETIME  = 2 ;
    public static final byte HTML_SELECT    = 3 ;
    public static final byte HTML_DURATION  = 4 ;
    public static final byte HTML_BUTTON    = 5 ; 
    public static final byte HTML_RADIO     = 6 ;
    public static final byte HTML_NUMBER    = 7 ;
    public static final byte HTML_EDITOR    = 8 ;
    public static final byte HTML_BOOLEAN   = 9 ;
    public static final byte HTML_FILE      = 10 ;
    
    public  StringBuffer    p_label;
    public  StringBuffer    p_value;
    public  byte            p_htmlRenderType;
    public  StringBuffer    p_JSonChange;
    public  StringBuffer    p_JSonClick;
    public  StringBuffer    p_JSonBlur;
    public  StringBuffer[]  p_selectDisplayValues;
    public  StringBuffer[]  p_selectInternalValues;
    public  boolean         p_selectAllowValueEdit;
    public  boolean         p_disabled;
    public  boolean         p_visible=true;
    public  boolean         p_required=false;
    public  boolean         p_recommend=false;
    public  String             p_max ;
    public  String          p_min ;
    public  int             p_decimals;
    public  int             p_minDecimals;
    public  boolean         p_grouping=false;     
    public  StringBuffer    p_mask ;
    public  StringBuffer    p_id;
    public  StringBuffer    p_name;
    
    
    public  docHTML_sectionField(
            StringBuffer    label ,
            StringBuffer    value ,
            byte            htmlRenderType , 
            StringBuffer[]  selectDisplayValues ,
            StringBuffer[]  selectInternalValues ,
            boolean         selectAllowValueEdit , 
            boolean         disabled ,
            StringBuffer    JSonChange ,
            StringBuffer    JSonClick , 
            StringBuffer    JSonBlur ,
            StringBuffer    id,
            StringBuffer    name
        
            ) 
    {
        p_label                 =   label ;
        p_value                 =   value ;
        p_htmlRenderType        =   htmlRenderType ; 
        p_JSonChange            =   JSonChange ;
        p_JSonClick             =   JSonClick ;
        p_JSonBlur              =   JSonBlur ;
        p_selectDisplayValues   =   selectDisplayValues ;
        p_selectInternalValues  =   selectInternalValues ;
        p_selectAllowValueEdit  =   selectAllowValueEdit ; 
        p_disabled              =   disabled ;
        p_id                    =   id ;
        p_name                  =   name;
        if( p_htmlRenderType == docHTML_sectionField.HTML_SELECT )
        {
            p_required=true;
        }
        
    
    }
    
    public  docHTML_sectionField(
            StringBuffer    label ,
            StringBuffer    value ,
            byte            htmlRenderType , 
            StringBuffer    mask ,
            int             decimals,
            int             minDecimals,
            boolean         grouping,
            String             min ,
            String             max , 
            boolean         disabled ,
            StringBuffer    JSonChange ,
            StringBuffer    JSonClick , 
            StringBuffer    JSonBlur ,
            StringBuffer    id ,
            StringBuffer    name 
        
            ) 
    {
        p_label                 =   label ;
        p_value                 =   value ;
        p_htmlRenderType        =   htmlRenderType ; 
        p_JSonChange            =   JSonChange ;
        p_JSonClick             =   JSonClick ;
        p_JSonBlur              =   JSonClick ;
        p_min                   =   min ;
        p_max                   =   max ;
        p_mask                  =   mask ; 
        p_disabled              =   disabled ; 
        p_id                    =   id ;
        p_name                  =   name;
        p_decimals              =   decimals;
        p_grouping              =   grouping;
    }
    

    public static final docHTML_sectionField newCombo ( 
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        StringBuffer[]  selectDisplayValues ,
        StringBuffer[]  selectInternalValues ,
        boolean         allowValueEdit 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_SELECT ,
                        selectDisplayValues ,
                        selectInternalValues ,
                        allowValueEdit ,
                        false ,
                        null ,
                        null ,
                        null , 
                        id ,
                        name 
                        );
        }

    public static final docHTML_sectionField newDuration (
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        StringBuffer[]  selectDisplayValues ,
        StringBuffer[]  selectInternalValues ,
        boolean         allowValueEdit ,
        StringBuffer    JSonChange ,
        StringBuffer    JSonClick , 
        StringBuffer    JSonBlur 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_DURATION ,
                        selectDisplayValues ,
                        selectInternalValues ,
                        allowValueEdit ,
                        false ,
                        JSonChange ,
                        JSonClick ,
                        JSonBlur ,
                        id ,
                        name
                        );
        }

    public static final docHTML_sectionField newDuration (
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        StringBuffer[]  selectDisplayValues ,
        StringBuffer[]  selectInternalValues ,
        boolean         allowValueEdit 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_DURATION ,
                        selectDisplayValues ,
                        selectInternalValues ,
                        allowValueEdit ,
                        false ,
                        null ,
                        null ,
                        null ,
                        id ,
                        name
                        );
        }

    public static final docHTML_sectionField newDuration (
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        boolean         allowValueEdit 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_DURATION ,
                        null ,
                        null ,
                        allowValueEdit ,
                        false ,
                        null ,
                        null ,
                        null ,
                        id ,
                        name 
                        );
        }

    public static final docHTML_sectionField newCombo ( 
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        StringBuffer[]  selectDisplayValues ,
        StringBuffer[]  selectInternalValues ,
        boolean         allowValueEdit ,
        StringBuffer    JSonChange ,
        StringBuffer    JSonClick , 
        StringBuffer    JSonBlur 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_SELECT ,
                        selectDisplayValues ,
                        selectInternalValues ,
                        allowValueEdit ,
                        false ,
                        JSonChange ,
                        JSonClick ,
                        JSonBlur ,
                        id ,
                        name
                        );
        }
    public static final docHTML_sectionField newCombo ( 
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        StringBuffer[]  selectDisplayValues ,
        StringBuffer[]  selectInternalValues ,
        boolean         isDisabled ,
        boolean         allowValueEdit ,
        StringBuffer    JSonChange ,
        StringBuffer    JSonClick , 
        StringBuffer    JSonBlur 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                            value ,
                        docHTML_sectionField.HTML_SELECT ,
                        selectDisplayValues ,
                        selectInternalValues ,
                        allowValueEdit ,
                        isDisabled ,
                        JSonChange ,
                        JSonClick ,
                        JSonBlur ,
                        id ,
                        name
                        );
        }
    
    public static final docHTML_sectionField newText (
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_TEXT ,
                        null ,
                        null ,
                        false ,
                        false ,
                        null ,
                        null ,
                        null ,
                        id ,
                        name
                        );
        }

    public static final docHTML_sectionField newText ( 
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        StringBuffer    JSonChange ,
        StringBuffer    JSonClick , 
        StringBuffer    JSonBlur 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_TEXT ,
                        null ,
                        null ,
                        false ,
                        false ,
                        JSonChange ,
                        JSonClick ,
                        JSonBlur ,
                        id ,
                        name 
                        );
        }
    public static final docHTML_sectionField newNumber ( 
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        StringBuffer    mask  ,
        int             decimals,
        int             minDecimals,
        boolean         grouping,
        String             max ,
        String             min ,
        StringBuffer    JSonChange ,
        StringBuffer    JSonClick , 
        StringBuffer    JSonBlur 
        
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_NUMBER ,
                        mask ,
                        decimals,
                        minDecimals,
                        grouping,
                        min ,
                        max ,
                        false ,
                        JSonChange ,
                        JSonClick ,
                        JSonBlur ,
                        id ,
                        name
                        );
        }

    public static final docHTML_sectionField newNumber ( 
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        StringBuffer    mask  ,
        int             decimals,
        int             minDecimals,
        boolean         grouping,
        String             max ,
        String             min
        )
        {
               return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_NUMBER ,
                        mask ,
                        decimals,
                        minDecimals,
                        grouping,
                        min ,
                        max ,
                        false ,
                        null ,
                        null ,
                        null ,
                        id ,
                        name 
                        );
        }



    public static final docHTML_sectionField newDate (
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_DATE ,
                        null ,
                        null ,
                        false ,
                        false ,
                        null ,
                        null ,
                        null ,
                        id ,
                        name
                        );
        }

    public static final docHTML_sectionField newDate (
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        StringBuffer    JSonChange ,
        StringBuffer    JSonClick , 
        StringBuffer    JSonBlur 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_DATE ,
                        null ,
                        null ,
                        false ,
                        false ,
                        JSonChange ,
                        JSonClick ,
                        JSonBlur ,
                        id ,
                        name
                        );
        }
        
    public static final docHTML_sectionField newBoolean (
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        StringBuffer    JSonChange ,
        StringBuffer    JSonClick , 
        StringBuffer    JSonBlur 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_BOOLEAN ,
                        null ,
                        null ,
                        false ,
                        false ,
                        JSonChange ,
                        JSonClick ,
                        JSonBlur ,
                        id ,
                        name
                        );
        }
        

    public static final docHTML_sectionField newDateTime (
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_DATETIME ,
                        null ,
                        null ,
                        false ,
                        false ,
                        null ,
                        null ,
                        null ,
                        id ,
                        name
                        );
        }

    public static final docHTML_sectionField newDateTime (
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        StringBuffer    JSonChange ,
        StringBuffer    JSonClick , 
        StringBuffer    JSonBlur 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_DATETIME ,
                        null ,
                        null ,
                        false ,
                        false ,
                        JSonChange ,
                        JSonClick ,
                        JSonBlur ,
                        id ,
                        name
                        );
        }


    public static final docHTML_sectionField newButton (
        StringBuffer    id ,
        StringBuffer    value ,
        StringBuffer    JSonClick  
        )
        {
            return new docHTML_sectionField ( 
                        null ,
                        value ,
                        docHTML_sectionField.HTML_BUTTON ,
                        null ,
                        null ,
                        false ,
                        false ,
                        null ,
                        JSonClick ,
                        null ,
                        id ,
                        null
                        );
        }

    public static final docHTML_sectionField newFileBrowse ( 
        StringBuffer    id ,
        StringBuffer    name ,
        StringBuffer    label ,
        StringBuffer    value ,
        StringBuffer    JSonChange ,
        StringBuffer    JSonClick , 
        StringBuffer    JSonBlur 
        )
        {
            return new docHTML_sectionField ( 
                        label ,
                        value ,
                        docHTML_sectionField.HTML_FILE ,
                        null ,
                        null ,
                        false ,
                        false ,
                        JSonChange ,
                        JSonClick ,
                        JSonBlur ,
                        id ,
                        name 
                        );
        }
}