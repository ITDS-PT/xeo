package netgest.bo.runtime.robots.ejbtimers;

import javax.ejb.EJBLocalObject;
import netgest.bo.runtime.boObject;

public interface boScheduleThreadLocalEJB
    extends EJBLocalObject
{

    public void addSchedule(boObject boobject);

    public void start(String name);	

    public void suspend();
}
