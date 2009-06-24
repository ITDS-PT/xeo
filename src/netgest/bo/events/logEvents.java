/*Enconding=UTF-8*/
package netgest.bo.events;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;


public final class logEvents
{
    public static void afterLoadLog(boObject object) throws boRuntimeException
    {
        String value = "";
        String type = object.getAttribute("type").getValueString();

        if (type.equalsIgnoreCase("BOOLEAN") || type.equalsIgnoreCase("CHAR"))
        {
            value += object.getAttribute("value_String").getValueString();
        }
        else if (type.equalsIgnoreCase("CURRENCY") || type.equalsIgnoreCase("NUMBER"))
        {
            value += object.getAttribute("value_Long").getValueString();
        }
        else if (type.equalsIgnoreCase("DATE") || type.equalsIgnoreCase("DATETIME") ||
                type.equalsIgnoreCase("DURATION"))
        {
            value += object.getAttribute("value_Date").getValueString();
        }
        else if (type.equalsIgnoreCase("CLOB"))
        {
            value += object.getAttribute("value_CLOB").getValueString();
        }

        object.getAttribute("value").setValueString(value);
    }
}
