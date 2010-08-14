package netgest.bo.presentation.render.elements;

import javax.servlet.http.HttpServletRequest;

public interface IExplorerExtension {
    
    public void setExplorer( Explorer exp );
    public Explorer getExplorer();
    public void     readParameters( Explorer exp, HttpServletRequest request );
    public Menu     getMenu( Explorer exp );
    public String   getExtensionSql();

}
