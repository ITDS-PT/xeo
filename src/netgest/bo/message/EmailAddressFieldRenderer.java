package netgest.bo.message;
import netgest.bo.runtime.EboContext;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.dochtml.docHTML;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import java.io.PrintWriter;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.dochtml.ICustomField;
import netgest.bo.dochtml.ICustomFieldDataBinding;

public class EmailAddressFieldRenderer implements ICustomField, ICustomFieldDataBinding 
{
    public EmailAddressFieldRenderer()
    {
    }

    public int render(EboContext ctx, docHTML_controler doccont, docHTML doc, boObject object, PrintWriter out, AttributeHandler relatedAtt) throws boRuntimeException
    {
        boolean bIsEnabled = !relatedAtt.disableWhen();
        String sHtmlName = getHtmlInputName( relatedAtt );
        if ( ctx.getRequest().getAttribute( "EMAILFIELDRENDER_SCRIPTS" ) == null )
        {
            out.println("<script type=\"text/javascript\" language=\"javascript\" src=\"emailAddressEditor/EmailAddressEditor.js\"></script>");
            out.println("<link rel=\"Stylesheet\" href=\"emailAddressEditor/EmailAddressEditor.css\" type=\"text/css\" />");
            ctx.getRequest().setAttribute( "EMAILFIELDRENDER_SCRIPTS", String.class );
        }
        
        out.print( "     <input type=\"hidden\" name=\""+sHtmlName+"\" id=\""+sHtmlName+"\" /> " );
        out.print( " <table width='100%' cellspacing=0 cellpadding=0 ><tr> " );
        out.print( "     <td width='100%'><span  " );
        out.print( "         id=\""+sHtmlName+"H\"  " );
        out.print( "         style=\"border:solid 1px gray;width:100%;font-family:Tahoma;font-size:x-small\"  " );
        out.print( "         contentEditable=\""+(bIsEnabled?"true":"false")+"\"  " );
        out.print( "         onkeypress=\"onEmailKeyPress(this)\" " );
        out.print( "         onkeydown=\"onEmailKeyDown(this)\" " );
        out.print( "         onkeyup=\"onEmailKeyUp(this)\" " );
        out.print( "         _hiddenInputId=\""+sHtmlName+"\" " );
        out.print( "     > " );
        
        renderEmails( out, object, relatedAtt );

        out.print( "     </td>" );
//        out.print( "     <td>&nbsp;<img src=\"emailAddressEditor/checkname.gif\" title=\"Verificar Nomes\" onclick=\"checkNames( "+sHtmlName+" );\" /></td>" );
        out.print( "     </tr></table>" );
        
        return ICustomField.RENDER_CONTINUE;
    }
    
    public void renderEmails( PrintWriter out, boObject oObject, AttributeHandler attr ) 
        throws boRuntimeException
    {
        boolean bFirst = true;
        if( oObject.getBridge( attr.getName() ) != null )
        {
            boBridgeIterator oBridgeIt = oObject.getBridge( attr.getName() ).iterator();
            
            while( oBridgeIt.next() )
            {
                boObject oAddressObject = oBridgeIt.currentRow().getObject();
                if( !bFirst )
                {
                    out.print( "; " );
                }
                renderEmailAddress( out, oAddressObject );
                
                bFirst = false;
            }
        }
        else
        {
            boObject oAddressObject = attr.getObject();
            if( oAddressObject != null )
            {
                renderEmailAddress( out, oAddressObject );
            }
        }
    }
    
    private void renderEmailAddress( PrintWriter out, boObject oAddressObject ) throws boRuntimeException
    {
    
        String sEmail = oAddressObject.getAttribute("email").getValueString();
        String sName  = oAddressObject.getAttribute("name").getValueString();
        
        if( sName.trim().length() == 0 )
        {
            sName = sEmail;
        }
        
        out.println( "     <span  " );
        out.println( "         class=\"ed_email\"  " );
        out.println( "         _valid=\"1\" " );
        out.println( "         _email=\""+ sEmail +"\" " 
                    );
        out.println( "         title=\"" + sEmail + "\" " 
                    );
        out.println( "         contentEditable=\"false\" " );
        out.println( "         UNSELECTABLE='on' " );
        out.println( "         _boui=" + oAddressObject.getBoui() + " " );
        out.println( "     >" + sName + "</span>" );
        
    }

    public String getRelatedAttribute() throws boRuntimeException
    {
        return null;
    }

    public void processRequestData(EboContext eboctx, String id, String value)
    {
        
        try
        {
            String[] aFiedName = id.split("_");
            
            long    lBoui       = Long.parseLong( aFiedName[1] );
            String  sAttName    = aFiedName[2];
            boObject oBindObject = boObject.getBoManager().loadObject( eboctx, lBoui );
            
            AttributeHandler oAttrHandle = oBindObject.getAttribute( sAttName );
            
            if( !"[empty]".equals( value ) )
            {
                 
                if( oAttrHandle.isBridge() )
                {
                    oBindObject.getBridge( oAttrHandle.getName() ).truncate();
                }
                else
                {
                    oAttrHandle.setValueObject( null );
                }
                
                String sValues[] = value.split(";");            
                for (int i = 0; i < sValues.length; i++) 
                {
                
                    boObject oAdrressObject = null;
                    if( sValues[i].matches( "[0-9]{1,}" ) ) // Boui
                    {
                        oAdrressObject = boObject.getBoManager().loadObject( 
                            eboctx,
                            Long.parseLong( sValues[i] )
                        );
                    }
                    else // endereço de email
                    {
                        String sNewEmail[] = sValues[i].split("\\|");
                        
                        String sEmail = sNewEmail[0];
                        String sNome  = sNewEmail[1];
                        
                        oAdrressObject = boObject.getBoManager().createObject(eboctx, "deliveryMessage");
                        oAdrressObject.getAttribute("name").setValueString(
                                sNome
                        );
                        oAdrressObject.getAttribute("email").setValueString(
                                sEmail
                        );
                    }
                    
                    if( oAttrHandle.isBridge() )
                    {
                        oBindObject.getBridge( oAttrHandle.getName() ).add( oAdrressObject.getBoui() );
                    }
                    else
                    {
                        oAttrHandle.setObject( oAdrressObject );
                    }
                    
                }
            }
            else
            {
                oAttrHandle.setValueString("");
            }
        }
        catch (boRuntimeException e)
        {
            throw new RuntimeException( e );
        }
        
        
    }
    
    public String getHtmlInputName( AttributeHandler relatedAtt )
    {
        return "icustom_" + relatedAtt.getParent().getBoui() + "_" + relatedAtt.getName();
    }
}