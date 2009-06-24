/*Enconding=UTF-8*/
package netgest.bo.events;

import java.util.Vector;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectStateHandler;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.utils.boRequest;

/**
 *
 * @author Luís Eduardo Moscoso Barreira
 * @Company Enlace 3
 */
public final class requestEvents {
    public requestEvents() {
    }

    public static void beforeSave(boObject object) throws boRuntimeException {
        boolean accept = true;
        Vector requests = new Vector();
        Vector protocol = new Vector();

        if(object.getDataRow().getFlashBackRow()!=null && object.getDataRow().getFlashBackRow().getString("requestState")!=null)
        {
          ((netgest.bo.impl.states.actionObjectImpl)object.getStateManager()).activity_CompleteSoft();
        }

        if (object.getParents().length > 0) {
            return;
        }

        boObjectStateHandler state = object.getStateAttribute("requestState");

        if ((state == null) || (state.getValue() == null) ||
                state.getValue().equals("")) {
            return;
        }

        if (state.getValue().equalsIgnoreCase("deniedAproval")) {
            accept = false;
        }

        if (state.getValue().equalsIgnoreCase("needAproval")) {
            return;
        }

        if (state.getValue().equalsIgnoreCase("comited")) {
            return;
        }

        requests.add(object);
        protocol.add(object);

        bridgeHandler dao = object.getBridge("DAO");

        dao.beforeFirst();

        while (dao.next()) {
            boObject daoObj = dao.getObject();

            state = daoObj.getStateAttribute("requestState");

            if (state == null) {
                continue;
            }

            if (state.getValue().equalsIgnoreCase("deniedAproval")) {
                accept = false;
            }

            if (state.getValue().equalsIgnoreCase("needAproval")) {
                return;
            }

            if (((daoObj.getParents()[0]).getAttribute("changedObject") != null) &&
                    ((daoObj.getParents()[0]).getAttribute("changedObject")
                          .getValueLong() != daoObj.getAttribute(
                        "changedObject").getValueLong())) {
                requests.add(daoObj);
            }

            protocol.add(daoObj);
        }

        for (int i = requests.size() - 1; i >= 0; i--) {
            if (accept) {
                boRequest.acceptRequest((boObject) requests.elementAt(i));
            } else {
                boRequest.refuseRequest((boObject) requests.elementAt(i));
            }
        }

        for (int i = 0; i < protocol.size(); i++) {
            if (accept) {
                ((boObject) protocol.elementAt(i)).getStateAttribute(
                    "requestState").setValue("comited");
            } else {
                ((boObject) protocol.elementAt(i)).getStateAttribute(
                    "requestState").setValue("deniedAproval");
            }
            
            ((boObject) protocol.elementAt(i)).update();
        }
    }
}
