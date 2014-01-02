/*Enconding=UTF-8*/
package netgest.bo.presentation.render.elements;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.SQLException;

import java.util.ArrayList;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.presentation.render.HTMLBuilder;
import netgest.bo.presentation.render.PageController;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.Logger;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class Tree implements Element {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.presentation.render.elements.Tree");
    private String code;
    private Explorer tree;
    
    private long showingBouis[] = null;

    /**
     *
     * @Company Enlace3
     * @since
     */
    public Tree(String code) {
        this.code = code;
    }

    public Tree(Explorer tree) {
        this.tree = tree;
    }

    public Explorer getExplorer() {
        return this.tree;
    }

    public String getCode() {
        return this.code;
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList,  PageController control)
        throws IOException, boRuntimeException {
        try {
            HTMLBuilder.writeTree(out, this,docHTML, docList, control);
        } catch (SQLException e) {
            throw new boRuntimeException("Tree.java", null, e);
        }
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        try {
            return HTMLBuilder.getTreeHTML(this,docHTML, docList, control);
        } catch (SQLException e) {
            throw new boRuntimeException("Tree.java", null, e);
        }
    }
    
    public void clearShowingBouis()
    {
        showingBouis = null;
    }
    
    public long[] getShowingBouis()
    {
        return showingBouis;
    }
    
    public void addShowingBouis(long[] bouis)
    {
        showingBouis = bouis ;
    }
    
    public long getBouiAtIndex(int pos)
    {
        if(showingBouis.length > pos)
        {
            return showingBouis[pos];
        }
        return -1;
    }
    
    public int getFirstIndexOfBoui(long boui)
    {
        for (int i = 0; i < showingBouis.length; i++) 
        {
            if(showingBouis[i] == boui)
            {
                return i;
            }
        }
        return -1;
    }
    
    public int getLastIndexOfBoui(long boui)
    {
        for (int i = showingBouis.length-1; i > -1 ; i--) 
        {
            if(showingBouis[i] == boui)
            {
                return i;
            }
        }
        return -1;
    }
    
    public String getAllIndexOfBoui(long boui)
    {
        String toRet;
        for (int i = showingBouis.length-1; i > -1 ; i--) 
        {
            if(showingBouis[i] == boui)
            {
                toRet = i + ";";
            }
        }
        return null;
    }
    
    public long getNext(int i)
    {
        if((i+1) < showingBouis.length)
        {
            return showingBouis[i+1];
        }
        return -1;
    }
    
    public long getBefore(int i)
    {
        if((i-1) > -1)
        {
            return showingBouis[i-1];
        }
        return -1;
    }
    
    public boolean hasNext(int i)
    {
        if((i+1) < showingBouis.length)
        {
            return true;
        }
        return false;
    }
    
    public boolean hasBefore(int i)
    {
        if((i-1) > -1)
        {
            return true;
        }
        return false;
    }
}
