/*Enconding=UTF-8*/
package netgest.bo.impl;

import netgest.bo.impl.document.print.PrintHelper;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.message.MessageServer;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.impl.document.merge.MergeHelper;
import netgest.utils.ngtXMLHandler;
import netgest.bo.userquery.userquery;
import netgest.bo.impl.templates.boTemplateManager;

public class XEO_MailMergeImpl 
{
	public static void process(boObject obj)
	{
		try
		{
			if(obj == null)
			{
				obj.addErrorMessage(MessageLocalizer.getMessage("ERROR_THE_OBJECT_WAS_NO_PASSED"));
				return;
			}
			
			// Obter os dados do objecto
			boObjectList list = null;
			
			boObject user = obj.getAttribute("from").getObject();
			String conf = obj.getAttribute("objects").getValueString();
			/*String fulltext = null;
			String filter = null;
			
			if(conf.length()>0)
			{
				ngtXMLHandler defTree = new ngtXMLHandler(conf);
				if(defTree.getFirstChild().getChildNode("fulltext") != null)
					fulltext = defTree.getFirstChild().getChildNode("fulltext").getText();
				if(defTree.getFirstChild().getChildNode("filter") != null)
					filter = defTree.getFirstChild().getChildNode("filter").getText();
			}
			
			StringBuffer qry = new StringBuffer("select iContact where ");
			
			if(fulltext == null && filter == null)
				qry.append("1=1");
			else
			{
				if(fulltext != null)
				{
					qry.append(" contains '").append(boObjectList.arrangeFulltext(fulltext)).append("'");
					if(filter != null) qry.append(" AND ");	
				}
				if(filter != null)
				{
					qry.append(userquery.userQueryToBoql_ClauseWhere(obj.getEboContext(),filter));
				}
			}
			
			list = boObjectList.list(obj.getEboContext(),qry.toString());*/
			
			list = obj.getBridge("selected_objects");
			
			String media = obj.getAttribute("media").getValueString();
			boObject template =  obj.getAttribute("merge_template").getObject();
			bridgeHandler results = obj.getBridge("results");
			
			// Inicio do processamento
			
			results.truncate();
			obj.update();
			
			long cnt = 1; // temp
			
			list.beforeFirst();
			while(list.next())
			{
				boObject temp = list.getObject();
				
				// Tipos do message lov - Letter, email, fax, Sgis
				boObject msg = null;
				if(media.equalsIgnoreCase("Letter"))
					msg = boObject.getBoManager().createObject(obj.getEboContext(), "messageLetter");
				else if(media.equalsIgnoreCase("E-mail"))
					msg = boObject.getBoManager().createObject(obj.getEboContext(), "messageMail");
				else if(media.equalsIgnoreCase("Fax"))
					msg = boObject.getBoManager().createObject(obj.getEboContext(), "messageFax");
				else if(media.equalsIgnoreCase("Sgis"))
					msg = boObject.getBoManager().createObject(obj.getEboContext(), "messageSgis");

				msg.getBridge("to").add(temp.getBoui());
				msg.getBridge("to").beforeFirst();
				msg.getBridge("to").next();
				boObject auxDeliver = msg.getBridge("to").getObject();
				
				auxDeliver.getAttribute("media").setValueString(media, AttributeHandler.INPUT_FROM_INTERNAL);
				
				msg.applyTemplate(null,template.getBoui());
				
				msg.getAttribute("name").setValueString(MessageLocalizer.getMessage("OBJECT_MAIL_MERGE")+" - " + temp.getBoui());
				
				msg.getAttribute("preferedMedia").setValueString(media);
				
				msg.getAttribute("from").setObject(user);
				
				msg.update();
				
				MessageServer.mergeMessage(msg);
				
				// Fazer o mail merge
				// long boui = MergeHelper.merge(temp,template);
				
				// Gravar no resultado
				
				boObject res = boObject.getBoManager().createObject(obj.getEboContext(), "XEO_MailMerge_res");
				
				res.getAttribute("result").setObject(msg);
				
				// Inserir aqui o número do REG
				res.getAttribute("number").setValueDouble(cnt++);
				
				res.getAttribute("object").setObject(temp);
				
				res.update();
				
				results.add(res.getBoui());
				
				obj.update();
			}
			if(cnt == 1) obj.addErrorMessage(MessageLocalizer.getMessage("THERE_ARE_NO_OBJECTS_TO_BE_PROCESSED"));
		}
		catch(Exception e)
		{
			obj.addErrorMessage(MessageLocalizer.getMessage("ERROR")+": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void print(boObject obj)
	{
		try
		{
			if(obj == null)
			{
				obj.addErrorMessage(MessageLocalizer.getMessage("ERROR_THE_OBJECT_WAS_NO_PASSED"));
				return;
			}
			
			// Obter os dados do objecto
			boObjectList list = obj.getBridge("results");
			
			list.beforeFirst();
			
			while(list.next())
			{
				boObject temp = list.getObject().getAttribute("result").getObject();
				
				String media = temp.getAttribute("preferedMedia").getValueString();
				
				if(media.equalsIgnoreCase("Letter") || media.equalsIgnoreCase("Fax"))
				{
					boObjectList para = temp.getBridge("to");
					para.beforeFirst();
					if(para.next())
					{
						boObject user = para.getObject();
						boObject doc = user.getAttribute("document").getObject();
						if(doc != null)
						{
							PrintHelper.printDocument(doc);
						}
					}
				}
			}
			
		}
		catch(Exception e)
		{
			obj.addErrorMessage(MessageLocalizer.getMessage("ERROR")+": " + e.getMessage());
			e.printStackTrace();
		}
	}

}