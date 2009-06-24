/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import netgest.bo.runtime.*;
import netgest.utils.*;
public class ScheduleAgentThread extends Thread 
{
    private static boolean p_isrunning;
    private static boolean p_suspend;
    private static boolean p_working;
    private EboContext p_eboctx;

    public ScheduleAgentThread(EboContext ctx)
    {
        p_eboctx = ctx;
        synchronized(this) {
            if(!p_isrunning)
                p_isrunning = true;
            else 
                throw new RuntimeException("Canoot load to ScheduleAgent Threads");
        }
    }
    public void run() {
        p_isrunning = true;
        //ScheduleAgent sa = new ScheduleAgent(p_eboctx);
       // ScheduleAgent sa =null;
        while(true) {
            try {
                if(!p_suspend) {
                    p_working = true;
                    //sa.run();
                   // p_eboctx.close();
                    p_working = false;
                }
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void suspendAgent() {
        p_suspend = true;
        while(p_working) {
            try {
            Thread.sleep(5);
            } catch (InterruptedException e) {};
        }
    }
    public static void resumeAgent() {
        p_suspend = false;
    }
    public static boolean isSuspended() {
        return p_suspend;
    }

}