package netgest.bo.ql;

import java.util.ArrayList;
import java.util.Vector;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.utils.XEOQLModifier;
import netgest.utils.tools;

public class QLOrderAndGroupByCardID {
    
    public static String parseOrderByCardID(String strQuery)
    {
		if (strQuery.toUpperCase().indexOf("ORDER BY")>-1)
		{
	        	boolean asObjAtt=false;
	        	ArrayList<String> dummy=new ArrayList<String>(0);
	        	//dummy.add("");
	        	
	        	XEOQLModifier qlmod=new XEOQLModifier(strQuery, dummy);	
	        	String orderby=qlmod.getOrderByPart();
	        	
	        	//Inspect order by Fields
	        	Vector<String> obyclauses=tools.Split(orderby, ",");
	        	
	        	for (String currclause:obyclauses)
	        	{
	        	    String att=currclause.trim();
	        	    if (att.indexOf(" ")>-1)
	        	    	att=att.substring(0,att.indexOf(" "));
	        	    
	        	    String [] cardids=getCardIdAtts(qlmod.getObjectPart(), att);
	        	    
	        	    if (cardids!=null && cardids.length > 0)        	    
		        	{
		        		String direction=getDirection(currclause);
		        		String neworderby="";
		        		asObjAtt = true;
		        		for (String attcardid:cardids)
		        		{
		        		    neworderby+=att+"."+attcardid+" "+direction+",";
		        		}
		        		neworderby = neworderby.substring(0,neworderby.lastIndexOf(","));
		        		orderby=tools.replacestr(orderby, currclause, neworderby);
	        	    }
	        	}
	        	
	        	if (!asObjAtt)
	        	    return strQuery;
	        	else
	        	{
	        	    qlmod.setOrderByPart(orderby);
	        	    return qlmod.toBOQL(dummy);
	        	}
		}
		else return strQuery;
    }
    
    public static String parseGroupByCardID(String strQuery)
    {	
		if (strQuery.toUpperCase().indexOf("GROUP BY")>-1 && strQuery.toUpperCase().indexOf("ORDER BY")>-1)
		{
        	boolean asObjAtt=false;
        	ArrayList<String> dummy=new ArrayList<String>(0);
        	//dummy.add("");
        	
        	XEOQLModifier qlmod=new XEOQLModifier(strQuery, dummy);	
        	String groupby=qlmod.getGroupByPart();
        	
        	//Inspect Group by Fields
        	Vector<String> gbyclauses=tools.Split(groupby, ",");
        	
        	for (String currclause:gbyclauses)
        	{
        	    String att=currclause.trim();
        	    if (att.indexOf(" ")>-1)
        	    	att=att.substring(0,att.indexOf(" "));
        	    
        	    String [] cardids=getCardIdAtts(qlmod.getObjectPart(), att);
        	    
        	    if (cardids!=null && cardids.length > 0)        	    
        	    {
	        		String direction=getDirection(qlmod.getOrderByPart());
	        		String neworderby="";
	        		asObjAtt = true;
	        		for (String attcardid:cardids)
	        		{
	        		    neworderby+=att+"."+attcardid+" "+direction+",";
	        		}
	        		neworderby = neworderby.substring(0,neworderby.lastIndexOf(","));     		        		        		
	        		qlmod.setOrderByPart(neworderby);
        	    }
        	}
        	
        	if (!asObjAtt)
        	    return strQuery;
        	else
        	{        	    
        	    return qlmod.toBOQL(dummy);
        	}
		}
		else return strQuery;
    }
    
    private static String[] getCardIdAtts(String objName,String att)
    {
		String [] toRet=null;	
		boDefHandler objdef=getObjectDefinitionForAtt(objName,att);
		if (objdef!=null)
		{
		    String cardid=objdef.getCARDID(); 		
		    byte[] cardidbytes=cardid.getBytes();
		    String cardidatt="";
		    Vector<String> cardids= new Vector<String>();
		    boolean append=false;
		    for (int j=0;j<cardidbytes.length;j++)
		    { 				        				
				byte currb=cardidbytes[j];
				if (currb=='[')
				    append=true;
				if (currb==']')
				{	
				    if (cardidatt.indexOf(".")==-1)
				    	cardids.add(cardidatt);
				    
				    append=false;
				    cardidatt="";
				}
				if (append && !(currb=='['))
				    cardidatt+=(char)currb;       					
		    }
		    toRet = new String[cardids.size()];				
		    cardids.toArray(toRet);
		}
		return toRet;
    }

    private static boDefHandler getObjectDefinitionForAtt(String firstObjName,String att)
    {
		boDefHandler bodef=null;
		
		//If the object name contains . does not support this
		if (firstObjName.indexOf(".")>-1) return bodef;
		
		//Treates ext
		if (firstObjName.trim().toUpperCase().endsWith(" EXT"))
			firstObjName=firstObjName.substring(0,firstObjName.toUpperCase().lastIndexOf(" EXT"));
		
		boDefHandler objdef=boDefHandler.getBoDefinition(firstObjName);
		Vector<String> atts=tools.Split(att, ".");
		if (objdef!=null)
		{
			for (int i=0;i<atts.size();i++)
			{
			    String currAtt=atts.get(i);
			    boDefAttribute attdef=objdef.getAttributeRef(currAtt);
		
			    //Only for ObjectAttribute and excluding attributes with more than one type
			    if (attdef!=null && attdef.getAtributeDeclaredType()==boDefAttribute.ATTRIBUTE_OBJECT
			    	&& (attdef.getObjectsName()==null || attdef.getObjectsName().length==0))
			    {
					String type=attdef.getType();			        			
					objdef=boDefHandler.getBoDefinition(tools.replacestr(type, "object.", ""));
					if (i==(atts.size()-1))bodef=objdef;
			    }
			}
		}
		return bodef;
    }
    
    private static String getDirection(String clause)
    {
		String direction="";
	 	if (clause.trim().toUpperCase().endsWith("DESC") || 
	 		clause.trim().toUpperCase().endsWith("DESC]"))
	 	{
	 		direction="DESC";
	 	}
	 	else if (clause.trim().toUpperCase().endsWith("ASC") ||
	 		clause.trim().toUpperCase().endsWith("ASC]"))
	 	{
	 		direction="ASC";
	 	}
	 	return direction;
    }
}
