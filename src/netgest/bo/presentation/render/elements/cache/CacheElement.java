package netgest.bo.presentation.render.elements.cache;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CacheElement 
{
    private Object element;
    private String key;
    private long lastTimeAcess = -1;
    private long user = -1;
    
    public CacheElement(String key, Object element, long user)
    {
        this.element = element;
        this.key = key;
        this.user = user;
        setTime();
    }
    
    public void setElement(String key, Object element, long user)
    {
        this.key = key;
        this.element = element;
        this.key = key;
        setTime();
    }
    
    public void setTime()
    {
        this.lastTimeAcess = System.currentTimeMillis();
    }
    
    public Object getElement()
    {
        setTime();
        return element;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public boolean isValid(long time)
    {
        if(lastTimeAcess <= time)
        {
            return false;
        }
        return true;
    }
    
    public long getUser()
    {
        return user;
    }
}