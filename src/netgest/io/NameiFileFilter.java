/*Enconding=UTF-8*/
package netgest.io;

import com.ibm.regex.RegularExpression;

public class NameiFileFilter extends iFileFilter {

  private String m_filter;
  private RegularExpression m_regex;
  
  public NameiFileFilter(String filter) {
    m_filter=filter;
    filter=filter.replace('*',' ');
    filter=filter.trim();
    filter="\\"+filter+"$";
    m_regex=new RegularExpression(filter);
  }

  public boolean accept(iFile p0) {
    boolean retval=false;
    if (p0.isFile()) {
      retval=m_regex.matches(p0.getName());
    } else if (p0.isDirectory()) {
      retval=true;
    }
    return retval;
  }
}