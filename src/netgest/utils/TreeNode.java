/*Enconding=UTF-8*/
package netgest.utils;
import java.util.*;

/**
 * 
 * @author JMF
 */
public final class TreeNode 
{
    private ArrayList p_childs;
    private TreeNode  p_parent; 
    private Object    p_content;
    public TreeNode( Object content  )
    {
        p_parent  =null;
        p_content = content;
    }
    
    private TreeNode( TreeNode parent , Object child  )
    {
        p_parent=parent;
        p_content = child;
    }

    public ArrayList getChilds()
    {
        return p_childs;
    }
    
    public TreeNode find(Object toFind )
    {
        if ( p_content.equals( toFind ) )
        {
            return this;
        }
        else
        {
            
            if (p_childs!=null)
            {
                for (int i = 0; i < p_childs.size() ; i++) 
                {
                   TreeNode x = ( ( TreeNode )p_childs.get(i)).find( toFind );
                   if ( x!=null ) return x;
                }
            }
            return null;
        }
        
    }
    public Object getContent()
    {
        return p_content;
    }
    public TreeNode getParent()
    { 
      return p_parent;  
    }
    public void addChild( Object child )
    {
        boolean find = false;
        if ( p_childs!=null )
        {
            for (int i = 0; i < p_childs.size() && !find ; i++) 
            {
                if ( ((TreeNode) p_childs.get(i)).getContent().equals( child ) )
                {
                    find=true;
                }
            }
            
                
        }
        
        if ( p_childs == null || !find )
        {
            addChild( new TreeNode( this, child) );
        }
    }
    
    private void addChild( TreeNode child )
    {
        if ( p_childs == null ) p_childs = new ArrayList();
        p_childs.add( child );
    }

   
    
}