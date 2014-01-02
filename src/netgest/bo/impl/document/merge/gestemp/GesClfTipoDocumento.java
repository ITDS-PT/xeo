package netgest.bo.impl.document.merge.gestemp;
import java.util.Hashtable;
import netgest.bo.lovmanager.*;
import netgest.bo.runtime.*;
import netgest.utils.StringUtils;
import netgest.utils.tools;


public class GesClfTipoDocumento extends Thread 
{
    public GesClfTipoDocumento()
    {
    }
    
    public static String getTipoDocumento( EboContext ctx, String fieldName, String filterSegmento, String filterRamo, String value, String style ) throws boRuntimeException
    {
        return getTipoDocumento( ctx, fieldName, filterSegmento, filterRamo, value, style, null);
    }
    public static String getTipoDocumento( EboContext ctx, String fieldName, String filterSegmento, String filterRamo, String value, String style, Hashtable filterDocs ) throws boRuntimeException
    {
        StringBuffer toPrint = new StringBuffer();
        String boql = "select GesDocClf_TDoc ";
        Object[] args = new Object[0];
        if( filterSegmento != null )
        {
            boql += " where segmento in ("+filterSegmento+") ";
            //args = new Object[] { filterSegmento };
        }
        if( filterRamo != null )
        {
            boql += " and ramo = ? ";
            args = new Object[] { filterRamo };
        }
        boql += "AND activo = '1' order by segmento,ramo,description";
        boObjectList list = boObjectList.list( ctx, boql,args,1,1000,"");
        list.beforeFirst();
        boObject oTipoDoc;
        
        String lastRamo = null;
        String lastSegmento = null;
        
        boolean closeRamo = false; 
        boolean closeSegmento = false;
        
        lovObject oLovRamos = LovManager.getLovObject( ctx, "Ramos" );
        Hashtable oHtLovRamos = new Hashtable();
        oLovRamos.beforeFirst();
        while( oLovRamos.next() )
        {
            oHtLovRamos.put( oLovRamos.getCode(), oLovRamos.getDescription() );
        }
        lovObject oLovSegmentos = LovManager.getLovObject( ctx, "appSegmentType" );
        Hashtable oHtLovSegmentos = new Hashtable();
        oLovSegmentos.beforeFirst();
        while( oLovSegmentos.next() )
        {
            oHtLovSegmentos.put( oLovSegmentos.getCode(), oLovSegmentos.getDescription() );
        }
        /*
                <span class="selectBox" value='1'>
                    <TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2>
                    <TBODY>                
                        <TR group='1'>
                            <TD><b>Sinistros</b></TD>
                        </TR>
                        <TR>
                            <TD val="1" title="desc">   Ola 1</TD>
                        </TR>
                    </TBODY>
                    </TABLE>
                </span>
        */
        
        toPrint.append("<span OnReturnValueChange='document.getElementById(\""+fieldName+"\").value=this.returnValue' class='selectGroupBox' value='"+(value!=null?value.trim():"")+"'>");
        toPrint.append("<TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2>");
        toPrint.append("<TBODY>");
        toPrint.append("<TR><TD val=''>&nbsp;</TD></TR>");
        
        //toPrint.append( "<option value=''/>" );
        String sValue = null;
        
        int iDeep = 0;
        
        while( list.next() )
        {
            oTipoDoc = list.getObject();
            String ramo =  oTipoDoc.getAttribute("ramo").getValueString();
            String tdocDesc =  oTipoDoc.getAttribute("description").getValueString();
            
            if( filterDocs == null || filterDocs.containsKey( tdocDesc ) )
            {
                String segmento =  oTipoDoc.getAttribute("segmento").getValueString();
    
                if( !segmento.equals( lastSegmento ) )
                {
                    if( closeSegmento )
                    {
                        iDeep --;
                        //toPrint.append("</optgroup>");
                    }
                    iDeep ++;
                    closeSegmento = true;
                    toPrint.append( "<tr group='true'><td><b>" );
                    toPrint.append( oHtLovSegmentos.get( segmento )+"</b></td></tr>" );
                    lastSegmento = segmento;
                }
    
                if( !ramo.equals( lastRamo ) )
                {
                    if( closeRamo )
                    {
                        //toPrint.append("</optgroup>");
                        iDeep--;
                    }
                    iDeep++;
                    closeRamo = true;
                    toPrint.append( "<tr group='true'><td><b>" );
                    toPrint.append( oHtLovRamos.get( ramo )+"</b></td></tr>" );
                    //toPrint.append("<optgroup label='"+oHtLovRamos.get( ramo )+"'>");
                    lastRamo = ramo;
                }
    
                oTipoDoc = list.getObject();
                sValue   = oTipoDoc.getAttribute("value").getValueString();
                
                
                //toPrint.append("<option value='");
                toPrint.append("<TR><TD val='");
                if( filterDocs == null )
                {
                    toPrint.append( sValue );
                }
                else
                {
                    toPrint.append( tdocDesc );
                }
                toPrint.append("' title='");
                toPrint.append(  oTipoDoc.getAttribute("description").getValueString() );
                toPrint.append("'>");
                
                for (int y = 0; y < (iDeep * 2); y++) 
                {
                    toPrint.append("&nbsp;");
                }
    
                
                /*
                if( sValue.equals( value ) )
                {
                    toPrint.append( " selected='on' >" );
                }
                else
                {
                    toPrint.append( ">" );
                }
                */
                toPrint.append( oTipoDoc.getAttribute("description").getValueString() );
                toPrint.append("</td></tr>");
                
                //toPrint.append("</option>");
            }
        }
        /*
        if( closeRamo )
        {
            toPrint.append("</optgroup>");
        }
        if( closeSegmento )
        {
            toPrint.append("</optgroup>");
        }
        */
        //toPrint.append("</select>");
        toPrint.append("</tbody></table></span>");
        toPrint.append("<input type='hidden' id='"+fieldName+"' name='"+fieldName+"' value='"+(value != null ? value:"")+"'/>");
        return toPrint.toString();
    }
}