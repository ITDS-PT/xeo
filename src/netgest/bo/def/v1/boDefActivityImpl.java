/*Enconding=UTF-8*/
package netgest.bo.def.v1;

import netgest.bo.def.boDefClsState;
import netgest.bo.def.v1.boDefClsStateImpl;
import netgest.bo.def.v1.boDefHandlerImpl;

import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Node;

public class boDefActivityImpl extends ngtXMLHandler implements netgest.bo.def.boDefActivity 
{

    public static final byte TYPE_INSTANCE=0;
    public static final byte TYPE_STATIC=1;
    
    private byte p_type;
    private String p_schedtype;
    private String p_schedstartdate;
    private String p_schedenddate;
    private String p_schedevery;
    private String p_schedat;
    private String p_performer;
    private String p_class;
    private String p_onerror;
    private String p_rpath;

    private String p_priority;
    private String p_body;
    
    private String p_id;
    private String p_description;

    private boDefHandlerImpl  p_bodef;
    private boDefClsStateImpl p_state;
    private boDefClsStateImpl p_stateatt;

    public boDefActivityImpl(boDefHandlerImpl bodef,Node x,boDefClsStateImpl instate,boDefClsStateImpl stateatt,String attname) {
        super(x);
        p_bodef = bodef;
        p_state = instate;
        p_stateatt = stateatt;
        p_type = TYPE_INSTANCE;
        if(attname != null && attname.equalsIgnoreCase("static")) {
            p_type = TYPE_STATIC;
        }
        parse();
    }
    public void parse() {
        if(p_state != null) {
            p_id = p_bodef.getBoName()+"_"+p_state.getName()+"_"+super.getNodeName();
        } else {
            p_id = p_bodef.getBoName()+"_"+super.getNodeName();
        }
        p_description    = super.getChildNode("name").getText();
        p_performer      = super.getChildNodeText("performer","");
        p_priority       = super.getChildNodeText("priority","");
        p_rpath          = super.getChildNodeText("rpath","");
        p_class          = p_bodef.getBoName();
        
        ngtXMLHandler x  = super.getChildNode("Schedule");
        if(x==null) {
            x= super.getChildNode("ScheduleId");
            p_schedtype = x.getText();
        } else {
            p_schedtype      = x.getChildNodeText("Schedule_Type","");
        }
        p_schedstartdate = x.getChildNodeText("Start_Date","");
        p_schedenddate   = x.getChildNodeText("End_Date","");
        p_schedevery     = x.getChildNodeText("Every","");
        p_schedat        = x.getChildNodeText("At","");
        p_onerror        = super.getChildNodeText("OnError","");
        p_body           = super.getChildNodeText("body","");
        
    }
    public String getId() {
        return p_id;
    }
    public String getDescription() {
        return p_description;
    }
    public String getScheduleType() {
        return p_schedtype;
    }
    public String getStartDate() {
        return p_schedstartdate;
    }
    public String getEndDate() {
        return p_schedenddate;
   }
    public String getEvery() {
        return p_schedevery;
    }
    public String getAt() {
        return p_schedat;
    }
    public String getPerformer() {
        return p_performer;
    }
    public String getClassName() {
        return p_class;
    }
    public String getPriority() {
        return p_priority;
    }
    public byte getActivityType() {
        return p_type;
    }
    public boDefClsState getClsState() {
        return p_state;
    }
    public boDefClsState getClsStateAttribute() {
        return p_stateatt;
    }
    public String getBody() {
        return p_body;
    }
    public String getName() {
        return super.getNodeName();
    }
}