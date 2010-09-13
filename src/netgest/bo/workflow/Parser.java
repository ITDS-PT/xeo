/*Enconding=UTF-8*/
package netgest.bo.workflow;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.BridgeObjAttributeHandler;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.utils.StringUtils;




/**
 * <p>Title: Parser </p>
 * <p>Description: WorkFlow Parser </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public final class Parser
{
    private static String TEXT_TOKEN = "#";
    private static String BEGIN_HTML_TOKEN = "<SPAN id=";
//    private static String MIDDLE_HTML_TOKEN = " contentEditable=false unSelectable=\"on\">";    
//    private static String END_HTML_TOKEN = "</SPAN>";
    private static String MIDDLE_HTML_TOKEN = "/><SPAN contentEditable=false unSelectable=\"on\">";
    private static String END_HTML_TOKEN = "</SPAN><SPAN id=END_TOKEN/>";
    
    
    
    private static ArrayList getTokens(String text, String limit)
    {
        ArrayList tokens = new ArrayList();
        String token = null;
        int isToken = 0;
        while (text.lastIndexOf(limit) != -1)
        {
            isToken++;
            if (isToken == 2)
            {
                token = text.substring(0, text.indexOf(limit));
                if(isValidToken(token))
                {
                    tokens.add(token);
                    text = text.substring(text.indexOf(limit) +limit.length(), text.length());
                }
                else
                {
                    text = text.substring(text.indexOf(limit), text.length());
                }
                isToken = 0;                
            }
            else
            {
                text = text.substring(text.indexOf(limit) +limit.length(), text.length());
            }
        }

        return tokens;
    }
    private static boolean isValidToken(String token)
    {
        boolean result = false;
        if(!"".equals(token) && token.lastIndexOf(" ") == -1)
        {
            result = true;
        }
        return result;
        
    }
    private static ArrayList getTokens(String text, String limitMin, String limitMax, boolean deep)
    {
        ArrayList tokens = new ArrayList();
        int isToken = 0;
        int ee = text.indexOf(limitMin);
        int ee2 = text.indexOf(limitMax);
        String aux;
        while (text.indexOf(limitMin) != -1 && text.indexOf(limitMax) != -1) //&& (isToken == 1)))
        {
            isToken++;
            if (isToken == 2)
            {
                if (deep)
                {   
//                    tokens.add(text.substring(0, text.indexOf(limitMax)) + "$" + getTokens(text, text.substring(0, text.indexOf(limitMax)) + " contentEditable=false unSelectable=\"on\">", "</SPAN>", false).get(0));
                    ArrayList tokenAux = getTokens(text, text.substring(0, text.indexOf(limitMax)) + MIDDLE_HTML_TOKEN, END_HTML_TOKEN, false);
                    if(tokenAux.size() > 0)
                    {
                        tokens.add(text.substring(0, text.indexOf(limitMax)) + "$" + tokenAux.get(0));
                    }
                }
                else
                {
                    tokens.add(text.substring(0, text.indexOf(limitMax)));
                }

                isToken = 0;
            }
            else
            {                
                text = text.substring(text.indexOf(limitMin) + limitMin.length(), text.length());
            }
        }

        return tokens;
    }

    private static String buildToken(String[] token, String limit)
    {
        String[] values = token[token.length - 1].split("\\$");
        if (values.length > 1)
        {
            token[token.length - 1] = values[0];
        }

        String buildToken = limit;
        for (int i = 0; i < token.length; i++)
        {
            buildToken += token[i];
            if (i != (token.length - 1))
            {
                buildToken += ".";
            }
        }

        buildToken += limit;
        return buildToken;
    }

    private static String replaceToken(String text, String ntext, String buildToken, boolean ch_obj)
        throws boRuntimeException
    {
		//PSENOS
		String buildNewText = null;
		if(ch_obj)
		{
			buildNewText = BEGIN_HTML_TOKEN;
			buildNewText += (buildToken.substring(1, buildToken.length() - 1) + MIDDLE_HTML_TOKEN + ntext + END_HTML_TOKEN);
		}
		else buildNewText = ntext;
        return StringUtils.replacestr(text,buildToken, buildNewText);
    }

    private static ArrayList getTokenUsed(ArrayList tokens, String token)
    {
        ArrayList listUseToken = new ArrayList();
        String useToken = null;
        boolean exists = false;
        for (Iterator objects = tokens.iterator(); objects.hasNext();)
        {
            useToken = ( String ) objects.next();
            
            // PSenos - publication = publicationContents
            exists = useToken.equals(token);
            if (exists)
            {
                listUseToken.add(useToken);
            }
            /*
            i = useToken.indexOf(token);
            if (i != -1)
            {
                //acrescentei o if de modo a incluir apenas ser fôr primeiro da cadeia a.b.c
                if(useToken.indexOf(".") == -1 || useToken.indexOf(".") > i)
                {
                    listUseToken.add(useToken);
                }
            }
            */
        }

        return listUseToken;
    }

    private static String replaceObject(boObject obj, boObject extAttr, String text, String[] tokenUse, boolean ch_obj)
        throws boRuntimeException
    {        
      return replaceObject( obj,  extAttr,  text,  tokenUse,  ch_obj,true);
    }

    private static String replaceObject(boObject obj, boObject extAttr, String text, String[] tokenUse, boolean ch_obj, boolean leaveTokens)
        throws boRuntimeException
    {        
        String ntext;
        String[] values = tokenUse[tokenUse.length - 1].split("\\$");
		if(values[0].startsWith("PORTLET_FIELD")) return text;
        if ((obj != null) && (tokenUse.length == 1))
        {
            ntext = obj.getTextCARDID().toString();
            if (!"".equals(ntext))
            {
                values = tokenUse[tokenUse.length - 1].split("\\$");
                if (values.length == 1)
                {
                    text = replaceToken(text, ntext, buildToken(tokenUse, TEXT_TOKEN), ch_obj);
                }
                else
                {
//                    text = text.replaceAll(values[values.length - 1], ntext);
                    text = StringUtils.replacestr(text,values[values.length - 1],ntext);
                }
            }
        }
        else if ((obj != null) && (tokenUse.length > 1))
        {
            if(!"parent".equalsIgnoreCase(tokenUse[0]))
            {
                ntext = obj.getAttribute(values[0]).getValueString();
            }
            else
            {                 
                ntext = obj.getTextCARDID().toString();                   
            }            
            if (!"".equals(ntext))
            {
                if (values.length == 1)
                {
                    text = replaceToken(text, ntext, buildToken(tokenUse, TEXT_TOKEN), ch_obj);
                }
                else
                {
//                    text = text.replaceAll(values[values.length - 1], ntext);
                    text = StringUtils.replacestr(text,values[values.length - 1],ntext);
                }
            }
        }
        else if ((obj == null) && (values.length > 1))
        {
//            text = text.replaceAll(
//                    "<SPAN id=" + buildToken(tokenUse, "") + " contentEditable=false unSelectable=\"on\">" + values[values.length - 1] + "</SPAN>",
//                    buildToken(tokenUse, "#"));
//            text = text.replaceAll(
//                    BEGIN_HTML_TOKEN + buildToken(tokenUse, "") + MIDDLE_HTML_TOKEN + values[values.length - 1] + END_HTML_TOKEN,
//                    buildToken(tokenUse, TEXT_TOKEN));
            text = StringUtils.replacestr(text,
                                    BEGIN_HTML_TOKEN + buildToken(tokenUse, "") + MIDDLE_HTML_TOKEN + values[values.length - 1] + END_HTML_TOKEN,
                                    buildToken(tokenUse, TEXT_TOKEN));                    
        }
        if (!leaveTokens)
        {
            text=StringUtils.replacestr(text,TEXT_TOKEN+buildToken(tokenUse, "")+TEXT_TOKEN,"");
        }        

        return text;
    }
    private static String replaceText(String newTxt, boObject extAttr, String text, String[] tokenUse, boolean ch_obj)
        throws boRuntimeException
    {

        String ntext;
        String[] values = tokenUse[tokenUse.length - 1].split("\\$");
        if ((!"".equals(newTxt)) && (tokenUse.length == 1))
        {
            values = tokenUse[tokenUse.length - 1].split("\\$");
            if (values.length == 1)
            {
                text = replaceToken(text, newTxt, buildToken(tokenUse, TEXT_TOKEN), ch_obj);
            }
            else
            {
//                text = text.replaceAll(values[values.length - 1], newTxt);
                text = StringUtils.replacestr(text,values[values.length - 1],newTxt);
            }
        }
        else if ((!"".equals(newTxt)) && (tokenUse.length > 1))
        {
            if (values.length == 1)
            {
                text = replaceToken(text, newTxt, buildToken(tokenUse, TEXT_TOKEN), ch_obj);
            }
            else
            {
//                text = text.replaceAll(values[values.length - 1], newTxt);
                text = StringUtils.replacestr(text,values[values.length - 1],newTxt);                
            }
        }
        else if (("".equals(newTxt)) && (values.length > 1))
        {
//            text = text.replaceAll(
//                    "<SPAN id=" + buildToken(tokenUse, "") + " contentEditable=false unSelectable=\"on\">" + values[values.length - 1] + "</SPAN>",
//                    buildToken(tokenUse, "#"));
//            text = text.replaceAll(
//                    BEGIN_HTML_TOKEN + buildToken(tokenUse, "") + MIDDLE_HTML_TOKEN + values[values.length - 1] + END_HTML_TOKEN,
//                    buildToken(tokenUse, TEXT_TOKEN));
                    
            text = StringUtils.replacestr(text,
                                    BEGIN_HTML_TOKEN + buildToken(tokenUse, "") + MIDDLE_HTML_TOKEN + values[values.length - 1] + END_HTML_TOKEN,
                                    buildToken(tokenUse, TEXT_TOKEN));                    
                    
        }

        return text;
    }
    private static String replaceText(String textToReplace, String text, String[] tokenUse, boolean ch_obj, boolean leaveTokens)
        throws boRuntimeException
    {
        if (textToReplace == null)
        {
            textToReplace = "";
        }
        
        String ntext;
        String[] values = tokenUse[tokenUse.length - 1].split("\\$");
        if (tokenUse.length == 1)
        {
            ntext = textToReplace;
			if(!leaveTokens)
			{
				values = tokenUse[tokenUse.length - 1].split("\\$");
				if (values.length == 1)
				{
					text = replaceToken(text, ntext, buildToken(tokenUse, TEXT_TOKEN), ch_obj);
				}
				else
				{
					text = text.replaceAll(values[values.length - 1], ntext);
				}
			}
			else
			{
				if (!"".equals(ntext))
				{
					values = tokenUse[tokenUse.length - 1].split("\\$");
					if (values.length == 1)
					{
						text = replaceToken(text, ntext, buildToken(tokenUse, TEXT_TOKEN), ch_obj);
					}
					else
					{
//                    	text = text.replaceAll(values[values.length - 1], ntext);
						text = StringUtils.replacestr(text,values[values.length - 1],ntext);
					}
				}
			}
        }
        else if (tokenUse.length > 1)
        {
            ntext = textToReplace;
            if (!"".equals(ntext))
            {                
                if (values.length == 1)
                {
                    text = replaceToken(text, ntext, buildToken(tokenUse, TEXT_TOKEN), ch_obj);
                }
                else
                {
//                    ntext = ntext.replaceAll("\\?","\\\\\\?").replaceAll("\\$","\\\\\\$");
//                    text = text.replaceAll(values[values.length - 1].replaceAll("\\?","\\\\\\?").replaceAll("\\$","\\\\\\$"), ntext);
                    text = StringUtils.replacestr(text,values[values.length - 1],ntext);
                }
            }
        }
        return text;
    }
    private static String replaceList(
        bridgeHandler bHandler, boObject extAttr, String text, String[] tokenUse, boolean ch_obj)
        throws boRuntimeException
    {
      return replaceList(
         bHandler,  extAttr,  text,  tokenUse,  ch_obj,true);
    }
    
    private static String replaceList(
        bridgeHandler bHandler, boObject extAttr, String text, String[] tokenUse, boolean ch_obj, boolean leaveTokens)
        throws boRuntimeException
    {
        String ntext;
        String[] values = tokenUse[tokenUse.length - 1].split("\\$");
        StringBuffer allList = new StringBuffer();

        //bridgeHandler bHandler = extAttr.getBridge("valueList");
        long size = bHandler.getRecordCount();
        int count = 0;
        bHandler.beforeFirst();
        while (bHandler.next())
        {
            boObject obj = bHandler.getObject();
            if ((obj != null) && (tokenUse.length == 1))
            {
                ntext = obj.getTextCARDID().toString();
                if (!"".equals(ntext))
                {
                    allList.append(ntext);
                }
            }
            else if ((obj != null) && (tokenUse.length > 1))
            {
                ntext = obj.getAttribute(values[0]).getValueString();
                if (!"".equals(ntext))
                {
                    allList.append(ntext);
                }
            }

            count++;
            if (count < size)
            {
                allList.append(";");
            }
        }

        if (!bHandler.isEmpty())
        {
            if (values.length == 1)
            {
                return text = replaceToken(text, allList.toString(), buildToken(tokenUse, TEXT_TOKEN), ch_obj);
            }
            else
            {
//                return text = text.replaceAll(
//                        "<SPAN id=" + buildToken(tokenUse, "") + " contentEditable=false unSelectable=\"on\">" + values[values.length - 1] + "</SPAN>",
//                        buildToken(tokenUse, "#"));
//                return text = text.replaceAll(
//                        BEGIN_HTML_TOKEN + buildToken(tokenUse, "") + MIDDLE_HTML_TOKEN + values[values.length - 1] + END_HTML_TOKEN,
//                        buildToken(tokenUse, TEXT_TOKEN));

                return text = StringUtils.replacestr(text,
                            BEGIN_HTML_TOKEN + buildToken(tokenUse, "") + MIDDLE_HTML_TOKEN + values[values.length - 1] + END_HTML_TOKEN,
                            buildToken(tokenUse, TEXT_TOKEN));                          
                //return text = text.replaceAll(values[values.length -1],allList.toString());
            }
        }
        else if (bHandler.isEmpty() && (values.length > 1))
        {
//            return text = text.replaceAll(
//                    "<SPAN id=" + buildToken(tokenUse, "") + " contentEditable=false unSelectable=\"on\">" + values[values.length - 1] + "</SPAN>",
//                    buildToken(tokenUse, "#"));
//            return text = text.replaceAll(
//                    BEGIN_HTML_TOKEN + buildToken(tokenUse, "") + MIDDLE_HTML_TOKEN + values[values.length - 1] + END_HTML_TOKEN,
//                    buildToken(tokenUse, TEXT_TOKEN));
            text = StringUtils.replacestr(text,
                    BEGIN_HTML_TOKEN + buildToken(tokenUse, "") + MIDDLE_HTML_TOKEN + values[values.length - 1] + END_HTML_TOKEN,
                    buildToken(tokenUse, TEXT_TOKEN));
        }
        if (!leaveTokens && bHandler.isEmpty())
        {
             text=StringUtils.replacestr(text,TEXT_TOKEN+buildToken(tokenUse, "")+TEXT_TOKEN,"");
        }
        
         return text;
    }

    private static String doParse(boObject object, String attName, String text, ArrayList tokens, boolean ch_obj, boolean leaveTokens)
        throws boRuntimeException
    {
        boolean changes = false;
        String aux;
        String shortAlias;
        String[] tokenUse;
        ArrayList toSplit;
        bridgeHandler bHandler = object.getBridge("extendAttribute");
		if(bHandler != null)
		{
			bHandler.beforeFirst();
			while (bHandler.next())
			{
				tokenUse = null;
	
				boObject extAttr = bHandler.getObject();
				shortAlias = extAttr.getAttribute("shortAlias").getValueString();
				toSplit = getTokenUsed(tokens, shortAlias);
	
				if (toSplit != null)
				{
					for (int i = 0; i < toSplit.size(); i++)
					{
						tokenUse = (( String ) toSplit.get(i)).split("\\.");
	
						String cardinal = extAttr.getAttribute("attributeCardinal").getValueString();
						if ((tokenUse != null) && "2".equals(cardinal))
						{
							text = replaceList(extAttr.getBridge("valueList"), extAttr, text, tokenUse, ch_obj);
							changes = true;
						}
						else if (tokenUse != null)
						{
							
							String attributeType = extAttr.getAttribute("attributeType").getValueString();
							if("0".equals(attributeType))
							{
								text = replaceObject(extAttr.getAttribute("valueObject").getObject(), extAttr, text, tokenUse, ch_obj);
							}
							else if("9".equals(attributeType))
							{
								text = replaceText(extAttr.getAttribute("valueText").getValueString(), extAttr, text, tokenUse, ch_obj);
							}
							else if("4".equals(attributeType))
							{
								text = replaceText(extAttr.getAttribute("valueNumber").getValueString(), extAttr, text, tokenUse, ch_obj);
							}
							else if("6".equals(attributeType))
							{
								text = replaceText(extAttr.getAttribute("valueDate").getValueString(), extAttr, text, tokenUse, ch_obj);
							}
							else if("5".equals(attributeType))
							{
								text = replaceText(extAttr.getAttribute("valueDateTime").getValueString(), extAttr, text, tokenUse, ch_obj);
							}
							else if("1".equals(attributeType))
							{
								aux = extAttr.getAttribute("valueBoolean").getValueString();
								if("0".equals(aux))
								{
									aux = "Não"; 
								}
								else if("1".equals(aux))
								{
									aux = "Sim";
								}
								text = replaceText(aux, extAttr, text, tokenUse, ch_obj);
							}  
							else if("12".equals(attributeType))
							{
								aux = extAttr.getAttribute("valueLov").getValueString();
								boObject lov = extAttr.getAttribute("lov").getObject(); 
								if(aux != null && !"".equals(aux) && lov != null)
								{
									if(lov.exists())
									{
										bridgeHandler lovdetails= lov.getBridge("details");
										lovdetails.beforeFirst();
										boObject det;
										while(lovdetails.next())
										{
											det = lovdetails.getObject();
											if(aux.equalsIgnoreCase(det.getAttribute("value").getValueString()))
											{
												aux = det.getAttribute("description").getValueString();
											}
										}
									}
								}
								text = replaceText(aux, extAttr, text, tokenUse, ch_obj);
							}                           
							changes = true;
						}
					}
				}
			}
		}

        boAttributesArray attrs = object.getAttributes();

        Enumeration oEnum = attrs.elements();
        while (oEnum.hasMoreElements())
        {
            AttributeHandler atr = ( AttributeHandler ) oEnum.nextElement();
            String name = atr.getName();
            
            toSplit = getTokenUsed(tokens, name);

            if (toSplit != null)
            {
                for (int i = 0; i < toSplit.size(); i++)
                {
                    tokenUse = (( String ) toSplit.get(i)).split("\\.");
                    //tokenUse = (( String ) toSplit.get(i)).split("\\.",2);
                    //bridgeHandler bridge = object.getBridge( name );
                    //boObject objattr = atr.getObject();
                    int type = 0; // texto
                    String lovname=atr.getDefAttribute().getLOVName();
                    if(lovname != null && !lovname.equals("")) 
                    {
                      type = 3;                              
                    }
                    else if (atr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                    {
                        if (
                            (atr.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1) ||
                                atr instanceof BridgeObjAttributeHandler)
                        {
                            type = 2;
                        }
                        else
                        {
                            type = 1;
                        }
                    }

                    if (tokenUse != null)
                    {
                        if (type == 1)
                        {
                            text = replaceList(object.getBridge(name), null, text, tokenUse, ch_obj);
                            changes = true;
                        }
                        else if (type == 2)
                        {
                            text = replaceObject(atr.getObject(), null, text, tokenUse, ch_obj);
                            changes = true;
                        }
                        else if (type == 3)
                        {
                          String lname = atr.getDefAttribute().getLOVName();
                          
                          String xtext = atr.getValueString();
                          
                          boObject lov = boObject.getBoManager().loadObject(object.getEboContext(),"select Ebo_LOV where name='" + lname + "'");
                          
                          if(lov.exists())
                          {
                            bridgeHandler lovdetails= lov.getBridge("details");
                            lovdetails.beforeFirst();
                            boObject det;
                            while(lovdetails.next())
                            {
                              det = lovdetails.getObject();
                              if(xtext.equalsIgnoreCase(det.getAttribute("value").getValueString()))
                              {
                                xtext = det.getAttribute("description").getValueString();
                              }
                            }
                          }
                          text = replaceText(xtext, text, tokenUse, ch_obj, leaveTokens);
                          changes = true;
                        }                        
                        else
                        {
//                            text = replaceText(atr.getValueString(), text, tokenUse);
                            String xtext = atr.getValueString();
                            if ( atr.getDefAttribute().getValueType() == boDefAttribute.VALUE_DATE || atr.getDefAttribute().getValueType()==boDefAttribute.VALUE_DATETIME )
                            {
//                                if(atr.getValueDate() != null)
//                                {
//                                    SimpleDateFormat x= new SimpleDateFormat( "EEEEE, d MMMMM yyyy");
//                                    xtext = x.format( atr.getValueDate() );
//                                }
                            }
							text = replaceText(xtext, text, tokenUse, ch_obj, leaveTokens);
                            changes = true;
                        }
                    }
                }
            }
        }

        if (object.getParents().length > 0)
        {
            // igual para o pai
            boObject parent = object.getParents()[0];
            attrs = parent.getAttributes();

            oEnum = attrs.elements();
            while (oEnum.hasMoreElements())
            {
                AttributeHandler atr = ( AttributeHandler ) oEnum.nextElement();
                String name = atr.getName();

                toSplit = getTokenUsed(tokens, "parent."+name);

                if (toSplit != null)
                {
                    for (int i = 0; i < toSplit.size(); i++)
                    {                        

                        tokenUse = (( String ) toSplit.get(i)).split("\\.",2);
                        //tokenUse = (( String ) toSplit.get(i)).split("\\.");                        
                                            

                        //bridgeHandler bridge = object.getBridge( name );
                        //boObject objattr = atr.getObject();
                        int type = 0; // texto
                        if (atr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                        {
                            if (
                                (atr.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1) ||
                                    atr instanceof BridgeObjAttributeHandler)
                            {
                                type = 2;
                            }
                            else
                            {
                                type = 1;
                            }
                        }

                        if (tokenUse != null)
                        {
                            if (type == 1)
                            {
                                text = replaceList(parent.getBridge(name), null, text, tokenUse, ch_obj,leaveTokens);
                                changes = true;
                            }
                            else if (type == 2)
                            {
                                text = replaceObject(atr.getObject(), null, text, tokenUse, ch_obj,leaveTokens);
                                changes = true;
                            }
                            else
                            {
                                String xtext = atr.getValueString();
                                if ( atr.getDefAttribute().getValueType() == boDefAttribute.VALUE_DATE || atr.getDefAttribute().getValueType()==boDefAttribute.VALUE_DATETIME )
                                {
                                    if(atr.getValueDate() != null)
                                    {
                                        SimpleDateFormat x= new SimpleDateFormat( "EEEEE, d MMMMM yyyy");
                                        xtext = x.format( atr.getValueDate() );
                                    }
                                }
								text = replaceText(xtext, text, tokenUse, ch_obj, leaveTokens);
                                changes = true;
                            }
                        }
                    }
                }
            }
        }

        if (changes && ch_obj)
        {
            object.getAttribute(attName).setValueString(text);
        }

        return text;
    }

    public static boolean doParse(boObject object, ArrayList attList)
        throws boRuntimeException
    {
        for (int i = 0; i < attList.size(); i++)
        {
            String text = object.getAttribute(( String ) attList.get(i)).getValueString();
//            ArrayList tokens = getTokens(text, "<SPAN id=", " contentEditable=false unSelectable=\"on\">", true);
            ArrayList tokens = getTokens(text, BEGIN_HTML_TOKEN, MIDDLE_HTML_TOKEN, true);
            if (tokens.size() > 0)
            {
				text = doParse(object, ( String ) attList.get(i), text, tokens, true, true);
            }

//            tokens = getTokens(text, "#");
            tokens = getTokens(text, TEXT_TOKEN);
            if (tokens.size() > 0)
            {
				text = doParse(object, ( String ) attList.get(i), text, tokens, true, true);
            }
        }

        return true;
	}

    public static String doParse(boObject object, String text, boolean leaveTokens) throws boRuntimeException
    {
      TEXT_TOKEN="#";
      ArrayList tokens = getTokens(text, "<SPAN id=", " contentEditable=false unSelectable=\"on\">", true);
      if (tokens.size() > 0)
      {
        text = doParse(object, text, text, tokens, false, leaveTokens);
      }
      tokens = getTokens(text, "#");
      if (tokens.size() > 0)
      {
        text = doParse(object, text, text, tokens, false, leaveTokens);
      }
          return text;
    }
    public static boolean doParse(boObject object) throws boRuntimeException
    {
        TEXT_TOKEN="#";
        ArrayList list = new ArrayList();
        list.add("description");        
        return doParse(object, list); 
    }

    public static String doParseC$(boObject object, String text, boolean leaveTokens) throws boRuntimeException
    {
        TEXT_TOKEN = "#$";
      ArrayList tokens = getTokens(text, "<SPAN id=", " contentEditable=false unSelectable=\"on\">", true);
      if (tokens.size() > 0)
      {
        text = doParse(object, text, text, tokens, false, leaveTokens);
      }
      tokens = getTokens(text, TEXT_TOKEN);
      if (tokens.size() > 0)
      {
        text = doParse(object, text, text, tokens, false, leaveTokens);
      }
          return text;
    } 	
}
