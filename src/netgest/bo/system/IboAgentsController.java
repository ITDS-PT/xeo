package netgest.bo.system;
import java.lang.InterruptedException;

public interface IboAgentsController 
{
   public Object[] getThreadByName( String name );
   public void chekAndStartThread( int id_idx );
   public void suspendAgents();
   public void start();
   public void interrupt();
   public void join() throws InterruptedException;
   public boolean isAlive();
}