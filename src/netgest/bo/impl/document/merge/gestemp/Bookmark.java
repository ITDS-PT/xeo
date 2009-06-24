package netgest.bo.impl.document.merge.gestemp;
import java.util.ArrayList;

public class Bookmark 
{
    private String bookmark = null;
    private String[] fields = null;
    private ArrayList insideBookMarks = null;
    private int     beginIndex;
    private int     endIndex;

    public Bookmark(String bookmark, String [] fields, int beginIndex, int endIndex)
    {
        this( bookmark, fields );        
        this.beginIndex = beginIndex;
        this.endIndex   = endIndex;
    }

    public Bookmark(String bookmark, String [] fields)
    {
        this.bookmark = bookmark;
        this.fields = fields;
    }
    
    public String[] getFields()
    {
        return fields;
    }
    
    public String getBookmarkName()
    {
        return bookmark;
    }
    
    public boolean hasField(String nameToSearch)
    {
        if(fields != null)
        {
            String aux;
            for (int i = 0; i < fields.length; i++) 
            {
                aux = (String)fields[i];
                if(nameToSearch.endsWith("__"))
                {
                    if(aux.startsWith(nameToSearch)) return true;
                    if(aux.equals(nameToSearch.substring(0, nameToSearch.length() - 2))) return true;
                }
                else if(nameToSearch.equals(aux))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void addBookmark(Bookmark insideBookMark)
    {
        if(insideBookMarks == null) insideBookMarks = new ArrayList();
        insideBookMarks.add(insideBookMark);
    }
    
    public ArrayList getInsideBookmarks()
    {
        return insideBookMarks;
    }
    
    public int getBeginIndex()
    {
        return this.beginIndex;
    }
    public int getEndIndex()
    {
        return this.endIndex;
    }
}