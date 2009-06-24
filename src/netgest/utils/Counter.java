/*Enconding=UTF-8*/
package netgest.utils;


/**
 *
 * @author JMF
 */
public class Counter
{
    private int count;

    public Counter()
    {
        count=0;
    }

    public void increment()
    {
        count++;
    }
    
    public void decrement()
    {
        count--;
    }

    public int getNumber()
    {
        return count;
    }
}
