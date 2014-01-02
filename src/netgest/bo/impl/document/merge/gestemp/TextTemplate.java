package netgest.bo.impl.document.merge.gestemp;
import com.ibm.regex.Match;
import com.ibm.regex.RegularExpression;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import netgest.utils.StringUtils;

public class TextTemplate 
{
    private InputStream p_in = null;
    private StringBuffer source = null;
    private Hashtable p_repeatBlock = new Hashtable();
    private ArrayList bookmarks = new ArrayList(0); 
    private ResultSet rsData = null;
    
    private static final String TOKEN_DATA = "(#([A-Za-z0-9_]+)#)";
    private static final String TOKEN_REPEAT_BLOCK = "(\\[(.*?)\\]).*(\\[\\2\\])";
    private String[] dataHeader = null;
//    private Hashtable repeatBlockHeader = new Hashtable();
    ArrayList repeatBlockIntervals = new ArrayList();
    private static final String TIMESTAMP_PAT = "yyyy/MM/dd HH:mm:ss";
    private static final String DATA_PAT = "yyyy/MM/dd";
    
    private String result=null; 
    
    public TextTemplate()
    {
    }
    
    public void open(InputStream in) throws IOException
    {
        p_in = in;
        if(p_in != null)
        {
            InputStreamReader inRead = new InputStreamReader(p_in);
            source = new StringBuffer();
            
            char[] cbuff = new char[4096];
            int br;
            while((br=inRead.read(cbuff))>0) {
                source.append(cbuff,0,br);
            }
            inRead.close();
            findRepeatBlocks(source.toString(), bookmarks, repeatBlockIntervals);
            dataHeader = findData(source.toString(), repeatBlockIntervals);
        }
    }
    
    public void open(String in)
    {
        source = new StringBuffer(in);
        findRepeatBlocks(source.toString(), bookmarks, repeatBlockIntervals);
        dataHeader = findData(source.toString(), repeatBlockIntervals);
    }
    
    public String[] getFieldMarkers()
    {
        return dataHeader;
    }
    
    public ArrayList getBookmarks()
    {
        return bookmarks;
    }
    
    public void process()
    {
        try
        {
            if(source != null && source.length() > 0)
            {
                //ainda não estou a tratar os repetedblock
                //mas este tem de ser tratados 1º que os outros
                
                result = "";
                
                Pattern pat = Pattern.compile( TOKEN_DATA );
                
                rsData.next();

                int         nextPos     = 0; 

                boolean     wasbookmark = false;
                
                Matcher m = pat.matcher( source );

                String header, valor = null;
                if( m.find(  ) )
                {
                    m = pat.matcher( source );
                    while( m.find( nextPos ) )
                    {
                        wasbookmark = false;
                        
                        header = m.group( 2 );
                        
                        ResultSet currentData = rsData;
                        
                        // Check if the field is inside of bookmark
                        for (int i = 0;this.bookmarks!=null && i < this.bookmarks.size(); i++) 
                        {
                            Bookmark bm = (Bookmark)this.bookmarks.get( i );
                            if( m.start() > bm.getBeginIndex() && m.end() < bm.getEndIndex() )
                            {
                                wasbookmark = true;
                                
                                currentData = (ResultSet)this.p_repeatBlock.get( bm.getBookmarkName() );
                                String toProcess = source.substring( bm.getBeginIndex() + 1 , bm.getEndIndex() -1 );
                                String resultBm  = "";
                                //currentData.beforeFirst();
                                while( currentData.next() ) 
                                {
                                    Matcher m2 = pat.matcher( toProcess );
                                    int bookMarkNextPos = 0;
                                    int pos2 = toProcess.indexOf( "]" ) + 1;
                                    while( m2.find( bookMarkNextPos ) )
                                    {
                                        header = m2.group( 2 );
                                        valor     = getValue( currentData , header);
                                        if( valor == null )
                                        {
                                            valor = "";
                                        }
                                        resultBm += toProcess.substring( pos2, m2.start() ) + valor;
                                        bookMarkNextPos = m2.end();
                                        pos2 = bookMarkNextPos;
                                    }
                                    resultBm += toProcess.substring( bookMarkNextPos, toProcess.length() - toProcess.indexOf( "]" ) - 1 );
                                }
                                valor        = resultBm;
                                result      += source.substring( nextPos, bm.getBeginIndex() ) + valor;
                                nextPos      = bm.getEndIndex();
                           }
                        }
                        
                        if( !wasbookmark )
                        {
                            header  = m.group(2);
                            valor   = getValue( currentData , header);
                            if( valor == null )
                                valor = "";
                            
                            int iPos = 0;
                            
                            for(int z=0; z < valor.length(); z++)
                            {
                                iPos = valor.indexOf("\n", z);
                                if(iPos < 0)
                                    break;
                                
                                valor = valor.substring(0, iPos-1) + "<br />" + valor.substring(iPos+1);
                                z = iPos;
                            }
                            
                            result += source.substring( nextPos, m.start() ) + valor;
                            nextPos = m.end();
                        }
                    }
                    
                    if( nextPos < source.length() )
                    {
                        result += source.substring( nextPos );
                    }
                }
                else
                {
                    result = source.toString();
                }
                
/*                String header, valor;
                if(rsData != null)
                {
                    rsData.next();
                    for (int i = 0; i < dataHeader.length; i++) 
                    {
                        header = dataHeader[i];
                        valor = getValue(rsData, header);
                        str = StringUtils.replacestr(str, "#" + header + "#", (valor == null ? "":valor));
                    }
                }
                result = str;
*/                
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    private static String getValue(ResultSet rs, String header) throws SQLException
    {
        int pos = rs.findColumn(header);
        String toRet = "";
        SimpleDateFormat sdf = null;
        if(pos > 0)
        {
            int sqlType = rs.getMetaData().getColumnType(pos);
            switch(sqlType)
            {
                case Types.NUMERIC:
                    toRet = String.valueOf(rs.getDouble(pos));
                    break;
                case Types.TIMESTAMP:
                    java.util.Date t= rs.getTimestamp(pos);
                    sdf = new SimpleDateFormat(TIMESTAMP_PAT);
                    toRet = sdf.format(t);
                    break;
                case Types.DATE:
                    Date d= rs.getDate(pos);
                    sdf = new SimpleDateFormat(DATA_PAT);
                    toRet = sdf.format(d);
                    break;    
                default:
                    toRet = rs.getString(pos);
            }
            
        }
        return toRet;
    }
    
    private static String[] findData(String toCompile, ArrayList intervals)
    {
        ArrayList toRet = new ArrayList();
        Pattern p=Pattern.compile(TOKEN_DATA);
        Matcher m=p.matcher(toCompile);
        String aux, finded;
        int start, end;
        while (m.find())
        {
            finded=m.group();
            start = m.start()+1;
            end = m.end();
            if(intervals == null || !contained(start, end, intervals))
            {
                aux = finded.substring(finded.indexOf("#")+1,finded.lastIndexOf("#"));
                toRet.add(aux);
            }
        }
        
        return (String[])toRet.toArray(new String[toRet.size()]);
    }
    
    private static boolean contained(int start, int end, ArrayList contained)
    {
        String str = null;
        String [] interval = null;
        int ti, tf;
        for (int i = 0; i < contained.size(); i++) 
        {
            String v = (String)contained.get(i);
            interval = v.split("_");
            ti = Integer.parseInt(interval[0]);
            tf = Integer.parseInt(interval[1]);
            if(ti <= start && tf >= end)
            {
                return true;
            }
        }
        return false;
    }
    
    private static void findRepeatBlocks(String tocompile, ArrayList bookmarks, ArrayList intervals)
    {
        //RegularExpression xx = new RegularExpression(TOKEN_REPEAT_BLOCK);
        //Match m = new Match();
        Pattern p=Pattern.compile(TOKEN_REPEAT_BLOCK);
        Matcher m=p.matcher(tocompile);
        String aux, finded, all;
        String[] rpData;
        int start, end;
        int bIndex = 0;
        //while( xx.matches( tocompile , bIndex, tocompile.length(), m ) )
        while( m.find() )
        {
            start = m.start()+1;
            if(tocompile.indexOf("]", start) < tocompile.indexOf(" ", start))
            {
                end = tocompile.indexOf("]", start);
            }
            else
            {
                end = tocompile.indexOf(" ", start);
            }
            all = tocompile.substring(start, m.end() );
            intervals.add(m.start() + "_" + m.end());
            aux = tocompile.substring(start, end);
            rpData = findData(all, null);
            if(rpData.length > 0)
            {
                bookmarks.add(new Bookmark(aux, rpData,m.start(),m.end()));
            }
        }
    }
    
    public void save(OutputStream out) throws IOException
    {
        if(result != null)
        {
            byte [] b = result.getBytes();
            out.write(b);
        }
    }
    
    public String getResult()
    {
        return result;
    }
    
    public void setDataSource(ResultSet rs)
    {
        rsData = rs;
    }
    
    public void setRepeatBlock(ResultSet rs, String name)
    {
        p_repeatBlock.put(name, rs);
    }
    
    public static void main(String[] args)
    {
        StringBuffer sb = 
            new StringBuffer("A peritagem do veículo com a matrícula [assim #veiculo#] que este [aaa] no acidente do dia #sinistro.sinistroData# da [aaa]apólice #sinistro.apoliceNr# Agradecemos contacto telefónico para a marcação de peritagem ao veículo #matricula#, na sequência do acidente em #sinistro__sinistroData#.");
        sb.append("\n\n")
        .append("Proc. #sinistro__sinistroNr#/#sinistro__sinistroAno#")
        .append("#remetente__nome#, LUSITANIA SEGUROS + #telefone#");
    
        TextTemplate t = new TextTemplate();
//        t.open("A peritagem do veículo com a matrícula [assim #veiculo#] que este no acidente do dia #sinistro.sinistroData# da apólice #sinistro.apoliceNr#");
        t.open(sb.toString());

    }
}