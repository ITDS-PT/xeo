/*Enconding=UTF-8*/
package netgest.bo.runtime.sorter;
import java.util.Comparator;
import netgest.bo.runtime.*;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public interface ClassSorter extends Comparator
{
    public void sort(SorterNode[] objects);
    public int compareboObject(SorterNode firstAct, SorterNode secAct) throws netgest.bo.runtime.boRuntimeException;
    public SorterNode[] getValues(boObjectList list) throws netgest.bo.runtime.boRuntimeException;
    public int getAlgorithm();
    public boolean boObjectListOrder();
}