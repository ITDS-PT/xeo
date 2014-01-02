package netgest.utils;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProcessMonitor extends Thread
{
    Process         proc;
    boolean         running     = true;
    int             maxwait;
    boolean         timedout    = false; 
    int             exitCode    = Integer.MAX_VALUE;
    ByteArrayOutputStream stdout;
    ByteArrayOutputStream stderr;
    public ProcessMonitor( Process proc, int maxwait )
    {
        this.proc = proc;
        this.maxwait = maxwait;
        start();
    }
    
    public void run()
    {
        long init = System.currentTimeMillis();
        stdout = new ByteArrayOutputStream();
        stderr = new ByteArrayOutputStream();
        
        ReadStream errReader = new ReadStream( proc.getErrorStream(),stderr );
        ReadStream outReader = new ReadStream( proc.getInputStream(),stdout );
        
        WaitForProcess pm = new WaitForProcess( proc );
        
        try
        {
            while( pm.running && !interrupted() )
            {
                Thread.sleep(100);
                if( (System.currentTimeMillis() - init) >= maxwait )
                {
                    timedout = true;
                    try
                    {
                        proc.destroy();
                    }
                    catch (Exception e)
                    {
                        
                    }
                    break;
                }
            }
            if( !pm.running )
            {
                exitCode = pm.proc.exitValue();
            }
        }
        catch (InterruptedException ex)
        {
            try
            {
                proc.destroy();
            }
            catch (Exception e)
            {
                
            }
        }
        running = false;
    }
    public byte[] getOutBytes()
    {
        return this.stdout.toByteArray();
    }
    public byte[] getErrBytes()
    {
        return this.stderr.toByteArray();
    }
    
    public void waitFor()
    {
        try
        {
                this.join();
        }
        catch (InterruptedException e)
        {
            
        }
    }
    
    
    private class ReadStream extends Thread
    {
        InputStream     in;
        OutputStream    out;
        public ReadStream( InputStream in, OutputStream out )
        {
            this.in     = in;
            this.out    = out;
            start();
        }
        public void run()
        {
            try
            {
                byte[] buffer = new byte[2048];
                while( running && !interrupted() )
                {
                    int br = in.read( buffer );
                    if( br > 0 )
                    {
                        out.write( buffer );
                    }
                    else
                    {
                        Thread.sleep(50);
                    }
                }
            }
            catch (Exception e)
            {
                
            }
        }
    }
    
    private class WaitForProcess extends Thread
    {
        boolean running = true;
        Process proc;
        public WaitForProcess(Process proc)
        {
            this.proc = proc;
            start();
        }
        
        public void run()
        {
            try
            {
                this.proc.waitFor();
            }
            catch (InterruptedException e)
            {
            }
            running = false;
        }
    }

    public Process getProc()
    {
        return proc;
    }

    public boolean isRunning()
    {
        return running;
    }


    public int getMaxwait()
    {
        return maxwait;
    }


    public boolean isTimedout()
    {
        return timedout;
    }


    public int getExitCode()
    {
        return exitCode;
    }


    public ByteArrayOutputStream getStdout()
    {
        return stdout;
    }


    public ByteArrayOutputStream getStderr()
    {
        return stderr;
    }
}