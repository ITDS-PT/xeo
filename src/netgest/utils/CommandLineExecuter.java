package netgest.utils;
import java.io.IOException;
import netgest.bo.system.Logger;

public class CommandLineExecuter 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.utils.CommandLineExecuter");
    
    private ProcessMonitor  pm;
    private String createProcessError = null;
    
    public CommandLineExecuter()
    {
    }
    
    public boolean execute(String programExec, String[] args, int timeOut )
    {
        StringBuffer sb = new StringBuffer();
        sb.append(programExec).append("  ");
        String aux;
        sb.append("//NoLogo ");
        for (int i = 0; i < args.length; i++) 
        {
            aux = args[i].replaceAll("\\\\", "\\\\\\\\");
            sb.append("\"").append(aux).append("\"").append(" ");
        }
        try
        {
            Process process = Runtime.getRuntime().exec(sb.toString());
            pm = new ProcessMonitor( process, timeOut );
            pm.waitFor();
            return pm.getExitCode() == 0;
        }
        catch (Exception e)
        {
            logger.severe(e);   
            createProcessError = e.getClass().getName() + " - " + e.getMessage();
        }
        return false;
    }


    public ProcessMonitor getProcessMonitor()
    {
        return pm;
    }


    public String getCreateProcessErrorMessage()
    {
        return createProcessError;
    }
}