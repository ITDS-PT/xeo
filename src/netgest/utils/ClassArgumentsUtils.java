/*Enconding=UTF-8*/
package netgest.utils;

public class ClassArgumentsUtils  {
    public static final Object toObject(int value) {
        return new Integer(value);
    }
    public static final Object toObject(long value) {
        return new Long(value);
    }
    public static final Object toObject(short value) {
        return new Short(value);
    }
    public static final Object toObject(double value) {
        return new Double(value);
    }
    public static final Object toObject(float value) {
        return new Float(value);
    }
    public static final Object toObject(Object value) {
        return value;
    }
    public static final Object toObject(boolean value) {
        return new Boolean(value);
    }
    
}