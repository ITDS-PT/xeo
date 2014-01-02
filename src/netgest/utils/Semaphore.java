/*Enconding=UTF-8*/
package netgest.utils;

//imports
import java.io.Serializable;

/**
 * Objecto utilizado para a sincronização das threads.
 * <P>
 * @author Francisco Câmara
 */
public class Semaphore implements Serializable
{
	//serialVersionUID
	static final long serialVersionUID = -4473207537736282239L;
    
    private static Semaphore instance;
	private boolean doWork = true;
	private boolean workDone = false;
	private boolean done = false;

    static
    {
        instance = new Semaphore();
    }
    public static Semaphore getInstance()
    {
        return instance;
    }
	/**
     * Refere se uma thread deve ou não efectuar o trabalho
	 *
	 * @return true se for para efectuar o trabalho
	 * @return false caso contrário
	 */
	public synchronized boolean isToWork()
	{
		if (doWork) {
			doWork = false;
			workDone = false;
			return true;
		}

		return false;
	}

	/**
	 * Caso o trabalho ainda não tenha sido efectuado faz com as thread
	 * restantes esperem pelo término de trabalho.
	 *
	 */

	public synchronized void waitForFinishWork()
	{
		if (!workDone) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				// Ignorar
			}
		}
		notify();
	}

	/**
	 * Invocado sempre que uma thread termina o seu trabalho.
	 *
	 */
	public synchronized void finishedWork()
	{
		doWork = true;
		workDone = true;
		done = true;

		notify();
	}

	/**
	 * Verifica se o trabalho já foi ou não efectuado.
	 */
	public boolean isDone()
	{
		return done;
	}
}

