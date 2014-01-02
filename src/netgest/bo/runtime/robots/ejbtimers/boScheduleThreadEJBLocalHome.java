package netgest.bo.runtime.robots.ejbtimers;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

// Referenced classes of package netgest.bo.runtime.robots.ejbtimers:
//            xeoEJBTimerLocal

public interface boScheduleThreadEJBLocalHome
    extends EJBLocalHome
{

    public boScheduleThreadLocalEJB create()
        throws CreateException;    
}
