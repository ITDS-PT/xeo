/*Enconding=UTF-8*/
package netgest.bo.impl;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boBridgeMasterAttribute;
import netgest.bo.runtime.boEvent;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectUtils;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.xwf.common.xwfHelper;

public abstract class Ebo_TextIndexImpl extends boObject {
//    private static boolean isrunning;

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//logger
    private static Logger logger = Logger.getLogger("netgest.bo.impl.Ebo_TextIndexImpl");
    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    private static String []NOT_INDEX = {"SYS_ORIGIN", "SYS_DTSAVE", "SYS_FLDINPUTTYPE", "SYS_USER", "SYS_ICN"};

    long initime;
	Hashtable tokens = null;
	ArrayList tokensOrdered = null;


    public void addToken(Object token)
    {
      addToken(token,true);
    }
    
    public void addToken( Object token, boolean removeDuplicates)
    {
        if( token != null )
        {
            String tokenValue = String.valueOf( token ).replaceAll( "\\&amp;","" );            
            if (removeDuplicates)
            {
              tokenValue=tokenValue.toUpperCase();
              String[] subTokens = tokenValue.split( "\\s" );
              for (int i = 0; i < subTokens.length; i++)
              {
                  if( !tokens.containsKey( subTokens[i] ) ) {
                	  tokensOrdered.add( subTokens[i] );
                      tokens.put( subTokens[i], Object.class );
                  }
              }
            }
            else {
          	  	tokensOrdered.add( tokenValue );
            	tokens.put(tokenValue,Object.class);
            }
        }
    }


    public boolean onBeforeSave(boEvent event) throws boRuntimeException
    {
        String xstrui = super.getAttribute("ui").getValueString();
        String name = super.getAttribute("uiClass").getValueString();
        if(xstrui.length()>0)
        {
			tokens = new Hashtable();
			tokensOrdered = new ArrayList();
			
			initime = System.currentTimeMillis();
			AttributeHandler text  = this.getAttribute("text");
            long objectui = Long.parseLong(xstrui);
            boObject bobj = super.getBoManager().loadObject(super.getEboContext(),name,objectui);
            if(bobj.exists())
			{
				this.processObject(bobj,bobj.getBoDefinition().getIfIndexChilds(),1);
			}


            Iterator     	tokensEnum = tokensOrdered.iterator();
            StringBuffer    sbText     = new StringBuffer();

            while( tokensEnum.hasNext() )
            {
                sbText.append( tokensEnum.next() );
                sbText.append( ' ' );
            }
			text.setValueString( sbText.toString() );

			getAttribute("TEMPLATE").setValueObject( null );
        }
        return true;
        // Rebuild the fulltextcol index ....
    }

    private void processXwfActivity(boObject bobj) throws boRuntimeException
	{
        if("xwfActivity".equals(bobj.getName()) ||
            "xwfActivity".equals(bobj.getBoDefinition().getBoSuperBo()))
        {
            Object[] relatedObj = xwfHelper.getRelatedInformation(bobj);
            for (int i = 0; relatedObj != null && i < relatedObj.length; i++)
            {
                if(relatedObj[i] != null)
                {
                    if(relatedObj[i] instanceof String)
                    {
                        addToken( relatedObj[i] );
                    }
                    else
                    {
                        try
                        {
                            this.appendOrphanObject( ((boObject)relatedObj[i]).getBoui() );
                        }
                        catch( Exception e )
                        {
                            //ESTA A DAR UM ERRO AQUI QUANDO INDEXA UM XWFACTIVITYfill boui= 14964870
                            logger.severe("ERRO A INDEXAR ACTIVIDADE "+bobj.getBoui() );
                        }
                    }
                }
            }
        }
    }

	private void processObject(boObject bobj, int maxDeep, int deep) throws boRuntimeException
	{
		Enumeration oEnum = bobj.getAllAttributes().elements();
		while(oEnum.hasMoreElements()) {
			AttributeHandler att = (AttributeHandler)oEnum.nextElement();
            if(att.getDefAttribute().textIndex() && canIndexAtt(att.getName()))
            {
                if(att.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE )
                {
                    if( att.getDefAttribute().getDbIsTabled() )
                    {

                        /*try
                        {
                            Object[] values = (Object[])att.getValueObject();
                            for ( short x = 0 ; x < values.length ; x ++ )
                            {
                  sb.append( values[x] ).append(' ').append(',').append(' ');
                            }

                        }
                        catch (ClassCastException e)
                        {
                            e.printStackTrace();
                        }*/
                    }
                    else if(att.getDefAttribute().getLOVName()!=null && !att.getDefAttribute().getLOVName().equals(""))
                    {
                        String x=boObjectUtils.getLovDescription(this.getEboContext(),att.getDefAttribute().getLOVName(),att.getValueString());
                        if(x!=null && x.length()>0)
                        {
                            addToken( x );
                        }
                    }
                    else if(att.getDefAttribute().getValueType() == boDefAttribute.VALUE_DATETIME || att.getDefAttribute().getValueType() == boDefAttribute.VALUE_DATE)
                    {
                        java.util.Date d = att.getValueDate();
                        if(d != null)
                        {
                            String x = SDF.format(d);
                            addToken( x );
                        }
                    }
                    else if("CLASSNAME".equalsIgnoreCase(att.getName()) )
                    {
                        //vou juntar a label do objecto
                        String x =att.getValueString();
                        if(x!=null && x.length()>0)
                        {
                            addToken( x );
                        }
                        if(bobj != null && bobj.getLabel() != null && !"".equals(bobj.getLabel()))
                        {
                            x = bobj.getLabel();
                            addToken( x );
                        }
                    }
                    else
                    {

                        String x =att.getValueString();
                        if(x!=null && x.length()>0)
                        {
                            addToken( x ,false);
                        }

                    }
                }
                else if( att.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                {
                    if( !(att instanceof boBridgeMasterAttribute) )
                    {

                        if(
                            (
                                !att.getDefAttribute().getChildIsOrphan() ||
                                bobj.getBoDefinition().getIndexProcessChild(att.getName())
                            )
                            &&
                            att.getValueObject() != null
                        )
                        {
                            try
                            {
                                if( att.getDefAttribute().getDbIsTabled() )
                                {
                                    if(bobj.getBoDefinition().getIndexProcessChild(att.getName()) &&
                                       bobj.getBoDefinition().indexOnlyCardID(att.getName()))
                                    {
                                        this.appendCardId( att.getValuesLong());
                                    }
                                    else if(att.getDefAttribute().indexOnlyCardId())
                                    {
                                        this.appendCardId( att.getValuesLong());
                                    }
                                    else
                                    {
                                    	if( maxDeep > 1 && deep < maxDeep ) {
	                                    	boObject objects[] = att.getObjects();
	                                    	for (int i = 0; i < objects.length; i++) {
												processObject( objects[i], maxDeep, deep + 1 );
											}
                                    	}
                                    	else {
	                                        this.appendOrphanObject( att.getValuesLong() );
                                    	}
                                    }
                                }
                                else
                                {
                                    if(bobj.getBoDefinition().getIndexProcessChild(att.getName()) &&
                                       bobj.getBoDefinition().indexOnlyCardID(att.getName()))
                                    {
                                        this.appendCardId( att.getValueLong());
                                    }
                                    else if(att.getDefAttribute().indexOnlyCardId())
                                    {
                                        this.appendCardId( att.getValueLong());
                                    }
                                    else
                                    {
                                    	if( maxDeep > 1 && deep < maxDeep ) {
                                    		processObject( att.getObject(), maxDeep, deep + 1 );
                                    	}
                                    	else {
                                    		this.appendOrphanObject( att.getValueLong() );
                                    	}
                                    }
                                }
                            }
                            catch ( Exception e )
                            {
                                logger.severe("",e);
                            }
                        }
                        else
                          {
                                            /*TODO A VER JMF
                                            if( att.getDefAttribute().getDbIsTabled() )
                                            {
                                                this.appendCardId( sb, att.getValuesLong() );
                                            }
                                            else
                                            {
                                                this.appendCardId( sb, att.getValueLong() );
                                            }*/
                          }
                    }
                    else if ( !"process_sinistro".equals( bobj.getName() ) )
                    {
                        String bridgeValues = ((boBridgeMasterAttribute)att).getValueString();
                        if(bridgeValues != null && !"".equals(bridgeValues))
                        {
                            boBridgeIterator bit = bobj.getBridge(att.getName()).iterator();
                            boObject obj = null;
                            while(bit.next())
                            {
                                boDefBridge defBridge = att.getDefAttribute().getBridge();
                                boDefAttribute brAtt[] =  defBridge.getBridgeAttributes();
                                for (int i = 0; i < brAtt.length; i++)
                                {
                                    if(!"LIN".equals(brAtt[i].getName()) && brAtt[i].textIndex())
                                    {
//                                        System.out.println(brAtt[i].getName());
                                        if(brAtt[i].getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE )
                                        {
                                            if(brAtt[i].getValueType() != boDefAttribute.VALUE_BOOLEAN)
                                            {
                                                String valor = bit.currentRow().getAttribute(brAtt[i].getName()).getValueString();
                                                if(brAtt[i].getLOVName()!=null && !brAtt[i].getLOVName().equals(""))
                                                {
                                                    String x=boObjectUtils.getLovDescription(this.getEboContext(),brAtt[i].getLOVName(),valor);
                                                    if(x!=null && x.length()>0)
                                                    {
                                                        addToken( x );
                                                    }
                                                }
                                                else if(brAtt[i].getValueType() == boDefAttribute.VALUE_DATETIME || brAtt[i].getValueType() == boDefAttribute.VALUE_DATE)
                                                {
                                                    java.util.Date d = bit.currentRow().getAttribute(brAtt[i].getName()).getValueDate();
                                                    if(d != null)
                                                    {
                                                        String x = SDF.format(d);
                                                        addToken( x );
                                                    }
                                                }
                                                else if(valor!=null && valor.length()>0)
                                                {
                                                  addToken(valor,false);
                                                }
                                            }
                                        }
                                        else if( brAtt[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                                        {
                                            try
                                            {
                                                if( att.getDefAttribute().getDbIsTabled() )
                                                {
                                                	if( maxDeep > 1 && deep < maxDeep ) {
            	                                    	boObject objects[] = att.getObjects();
            	                                    	for (int j = 0; j < objects.length; j++) {
            												processObject( objects[j], maxDeep, deep + 1 );
            											}
                                                	}
                                                	else {
                                                		this.appendOrphanObject( att.getValuesLong() );
                                                	}
                                                }
                                                else
                                                {
                                                	if( maxDeep > 1 && deep < maxDeep ) {
        												processObject( att.getObject(), maxDeep, deep + 1 );
                                                	}
        	                                    	else {
        	                                    		this.appendOrphanObject( bit.currentRow().getAttribute(brAtt[i].getName()).getValueLong() );
        	                                    	}
                                                }
                                            }
                                            catch ( Exception e )
                                            {
                                                logger.severe("",e);
                                            }
                                        }
                                    }
                                }
                                //CHILD$
                                obj = bit.currentRow().getObject();
                                if((!obj.getBoDefinition().getBoCanBeOrphan() || bobj.getBoDefinition().getIndexProcessChild(att.getName())))
                                {
                                    try
                                    {
                                        if( !att.getDefAttribute().getDbIsTabled() )
                                        {
                                            if(bobj.getBoDefinition().getIndexProcessChild(att.getName()) &&
                                               bobj.getBoDefinition().indexOnlyCardID(att.getName()))
                                            {
                                                this.appendCardId( obj.getBoui());
                                            }
                                            else if(att.getDefAttribute().indexOnlyCardId())
                                            {
                                                this.appendCardId( obj.getBoui());
                                            }
                                            else
                                            {
                                                this.appendOrphanObject( obj.getBoui() );
                                            }
                                        }
                                    }
                                    catch ( Exception e )
                                    {
                                        logger.severe("",e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
		}
        processXwfActivity(bobj);
		/*TODO A VER JMF
        boDefAttribute[] allatts = bobj.getBoDefinition().getAttributesDef();
		for (int i = 0; i < allatts.length; i++)
		{
			if( allatts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && allatts[i].getMaxOccurs() > 1 && !allatts[i].getDbIsTabled() )
			{
				bridgeHandler bridge = bobj.getBridge( allatts[i].getName() );
				if( bridge != null && deep > 0)
				{
					bridge.beforeFirst();
					while(bridge.next())
					{
						this.processObject(bridge.getObject(),deep-1);
					}
				}
			}
		}
        */
	}

    public final void appendCardId( long[] bouis ) throws boRuntimeException
    {
        if(bouis != null)
          for (int i = 0; i < bouis.length; i++)
          {
              appendCardId( bouis[i] );
          }
    }
    private final void appendCardId( long boui ) throws boRuntimeException
    {
        if(boui==0)
          return;
        boObject object = boObject.getBoManager().loadObject( this.getEboContext(), boui );
        if(!object.exists())
          return;
        if(object.getCARDIDwNoIMG() != null && object.getCARDIDwNoIMG().length() > 0)
        {
            String x = object.getCARDIDwNoIMG().toString();
            addToken( x );
        }
    }

    public final void appendOrphanObject( long[] bouis ) throws boRuntimeException
    {
        if(bouis != null)
        {
            for (int i = 0; i < bouis.length; i++)
            {
                appendOrphanObject( bouis[i] );
            }
        }

    }
    public final void appendOrphanObject( long boui ) throws boRuntimeException
    {
        if( boui > 0 )
        {
            boObject object = boObject.getBoManager().loadObject( this.getEboContext(), boui );
            boDefHandler def = object.getBoDefinition();
            boAttributesArray atts = object.getAllAttributes();
            Enumeration oEnum = atts.elements();
            while(oEnum.hasMoreElements()) {
                AttributeHandler att = (AttributeHandler)oEnum.nextElement();
                if( att.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE )
                {
                    if(att.getDefAttribute().getValueType() != boDefAttribute.VALUE_BOOLEAN)
                    {
                        if(att.getDefAttribute().getValueType() == boDefAttribute.VALUE_DATETIME || att.getDefAttribute().getValueType() == boDefAttribute.VALUE_DATE)
                        {
                            java.util.Date d = att.getValueDate();
                            if(d != null)
                            {
                                String x = SDF.format(d);
                                addToken( x );
                            }
                        }
                        else if("CLASSNAME".equalsIgnoreCase(att.getName()) )
                        {
                            //vou juntar a label do objecto
                            String x =att.getValueString();
                            if(x!=null && x.length()>0)
                            {
                                addToken( x );
                            }
                            if(object != null && object.getLabel() != null && !"".equals(object.getLabel()))
                            {
                                x = object.getLabel();
                                addToken( x );
                            }
                        }
                        else
                        {
                            String x=att.getValueString();
                            if(x!=null && x.length()>0)
                            {
                                addToken( x ,false);
                            }
                        }
                    }
                }
            }
        }
    }

    public void onAfterSave( boEvent event ) throws boRuntimeException
    {
//        if(!isrunning) {
//            isrunning = true;
//            ((v1_0.Ebo_TextIndex)this).Async_rebuildIndex();
//            ((v1_0.Ebo_TextIndex)this).executeAsynch();
//        }
//        logger.finest("TextIndex update time:"+(System.currentTimeMillis()-initime));
    }


    public synchronized static boolean rebuildIndex( EboContext ctx, boolean alreadyLocked ) throws netgest.bo.runtime.boRuntimeException {
//        boolean ret = true;
//        Connection cn=null;
//        CallableStatement cstm=null;
//        boolean locked = false;
//        try {
////            if(alreadyLocked || DBLock.lock(ctx, "EBO_TextIndex", 300, 0, false))
////            {
//                locked = true;
//                cn = ctx.getDedicatedConnectionData();
//                boDefHandler defh = boDefHandler.getBoDefinition("Ebo_TextIndex");
////                cstm  = cn.prepareCall("ALTER INDEX SYS_IM_"+defh.getBoPhisicalMasterTable()+" REBUILD ONLINE PARAMETERS('sync memory 45M')");
//
//                cstm  = cn.prepareCall("{ CALL CTX_DDL.SYNC_INDEX( ?, '45M' ) }");
//                cstm.setString(1, "SYS_IM_"+defh.getBoPhisicalMasterTable()  );
//                cstm.execute();
//                cn.commit();
////            }
//        }
//        catch (SQLException e) {
//            logger.warn("Error rebuilding FullText index.\n"+e.getMessage());
//        }
//        finally {
////            if(!alreadyLocked && locked)
////            {
////                DBLock.releaseLock(ctx, "EBO_TextIndex");
////            }
//            try {
//                if(cstm!=null) cstm.close();
//            }
//            catch (Exception e) {
//
//            }
//            try {
//                if(cn!=null) cn.close();
//            }
//            catch (Exception e) {
//
//            }
//        }
////        isrunning= false;
//        return ret;
        return true;
    }
    public static boolean rebuildAllObjectsText(EboContext ctx) {
        boDefHandler[] defs= boDefHandler.listBoDefinitions();
        for(short i=0;i<defs.length;i++) {
            rebuildObjectText(ctx,defs[i].getName());
        }
        return true;
    }
    public static boolean rebuildObjectText(EboContext ctx,String objectname) {
        if(!objectname.equals("Ebo_TextIndex") && !objectname.equals("Ebo_Registry") && !objectname.equals("boObject")) {
            int page = 1;
            int pages = 1;
            boObjectList list=null;
            while(page <= pages ) {
                list = boObjectList.list(ctx,"SELECT "+objectname+" WHERE 1=1",page,100);
                list.beforeFirst();
                while(list.next()) {
                    long xx = System.currentTimeMillis();
                    long boui = list.getCurrentBoui();
                    try {
                        boObject cobj = boObject.getBoManager().loadObject(ctx,"SELECT Ebo_TextIndex WHERE UI = '"+boui+"'");
                        if(!cobj.exists()) {
                            cobj = boObject.getBoManager().createObject(ctx,"Ebo_TextIndex");
                            cobj.getAttribute("ui").setValueString(""+boui);
                            cobj.getAttribute("uiClass").setValueString(objectname);
                        }
                        cobj.update();
                    } catch (Exception e) {
                        logger.severe(e);
                    }
                    logger.finest("Tempo:" + (System.currentTimeMillis()-xx));
                }

                if(pages==1) {
                    pages = list.getPages();
                }
                page++;

            }
        }
        return true;
    }

    private static boolean canIndexAtt(String attName)
    {
        for (int i = 0; i < NOT_INDEX.length; i++)
        {
            if(NOT_INDEX[i].equalsIgnoreCase(attName))
            {
                return false;
            }
        }
        return true;
    }

/*
    private static boolean alreadySet(StringBuffer sb, String valueToSearch)
    {
        try
        {
            if(sb != null && sb.length() > 0)
            {
                String total = sb.toString().toUpperCase();
                if(valueToSearch != null && valueToSearch.length() > 0)
                {
                    String lookStr = valueToSearch.toUpperCase();
                    int pos = 0;
                    if((pos = total.indexOf(lookStr)) >= 0)
                    {
                        if(pos == 0 || total.charAt(pos-1) == ' ')
                        {
                            String word = getWord(total, pos);
                            if(lookStr.equalsIgnoreCase(lookStr))
                            {
                                return true;
                            }
                        }
                    }
                    return false;
                }
                return true;
            }
        }
        catch (Exception e)
        {
            logger.severe("alreadySet method error", e);
            //ignore
        }
        return false;
    }
*/
/*
    private static String getWord(String phrase, int from)
    {
        String toRet="";
        for(int i = from; i < phrase.length(); i++)
        {
            if(phrase.charAt(i) == ' ')
            {
                break;
            }
            else
            {
                toRet = toRet + phrase.charAt(i);
            }
        }
        return toRet;
    }
*/
}