package netgest.bo.builder;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefUtils;
import netgest.utils.ngtXMLHandler;

public class boBuilderOPL {

	private boDefHandler bodef = null;
	private ngtXMLHandler atts = null;
	
	public boBuilderOPL(boDefHandler bodef,ngtXMLHandler atts)
	{
		this.bodef = bodef;
		this.atts = atts;
	}
	
	
	public void createOPLAttributes()
	{
		if (!bodef.hasAttribute("KEYS")) {
			createKeys();
		}
		else
		{
			boDefAttribute keys=bodef.getAttributeRef("KEYS");
			if (!keys.getBridge().hasAttribute("securityType"))
			{
				Node tagatts=keys.getChildNode("bridge").getChildNode("attributes").getNode();
				addAttributeSecurityType(tagatts);
			}
			
		}
		
		if (!bodef.hasAttribute("KEYS_PERMISSIONS")) {
			createKeysPermissions();									
		}
	}
	
	private void createKeys()
	{
		Node tagatts=createSecurityAndReturnAtts("KEYS", "Security");
		addAttributeSecurityCode(tagatts);
		addAttributeSecurityType(tagatts);
	}
	
	private void createKeysPermissions()
	{
		Node tagatts=createSecurityAndReturnAtts("KEYS_PERMISSIONS", "User Security");		
		addAttributeSecurityCode(tagatts);
	}
	
	private Node createSecurityAndReturnAtts(String attname,String attlabel)
	{
		Node keys=atts.getNode().appendChild(
				boDefUtils.createAttribute(attname,
						attname, attlabel,
						"attributeObjectCollection", "object.boObject", 0, false,
						atts.getNode().getOwnerDocument()));
		keys.appendChild(atts.getNode().getOwnerDocument().createElement("objectFilter"));
		Node objects=keys.appendChild(atts.getNode().getOwnerDocument().createElement("objects"));
		objects.appendChild(boDefUtils.createTextNode((Element)keys, "object", "iXEOUser"));
		objects.appendChild(boDefUtils.createTextNode((Element)keys, "object", "workQueue"));
		objects.appendChild(boDefUtils.createTextNode((Element)keys, "object", "Ebo_Role"));
		objects.appendChild(boDefUtils.createTextNode((Element)keys, "object", "Ebo_Group"));
		keys.appendChild(boDefUtils.createTextNode((Element)keys, "minOccurs", "0"));
		keys.appendChild(boDefUtils.createTextNode((Element)keys, "maxOccurs", "N"));
		Node bridge=keys.appendChild(atts.getNode().getOwnerDocument().createElement("bridge"));
		Node tagatts=bridge.appendChild(atts.getNode().getOwnerDocument().createElement("attributes"));
		
		return tagatts;
	
	}
	private void addAttributeSecurityCode(Node tagatts)
	{
		tagatts.appendChild(
				boDefUtils.createAttribute("securityCode",
						"securityCode", "Security Code",
						"attributeNumber", "", 1, false,
						atts.getNode().getOwnerDocument()));	
	}
	
	private void addAttributeSecurityType(Node tagatts)
	{
		Node securityType=tagatts.appendChild(
				boDefUtils.createAttribute("securityType",
						"securityType", "Security Type",
						"attributeNumber", "", 1, false,
						atts.getNode().getOwnerDocument()));					
		String [][]attributes=new String[1][2];
		attributes[0][0]="language";
		attributes[0][1]="BOL";
		boDefUtils.createTextNode((Element)securityType, "defaultValue", "0",
				attributes);		
	}
	
	
}
